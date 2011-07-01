package org.nhindirect.stagent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import junit.framework.TestCase;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.smime.SMIMECapabilitiesAttribute;
import org.bouncycastle.asn1.smime.SMIMECapability;
import org.bouncycastle.asn1.smime.SMIMECapabilityVector;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataStreamGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.mail.smime.CMSProcessableBodyPartInbound;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.bouncycastle.mail.smime.SMIMEStreamingProcessor;
import org.bouncycastle.mail.smime.util.CRLFOutputStream;
import org.bouncycastle.util.Strings;
import org.bouncycastle.x509.X509Store;
import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.mail.Message;
import org.nhindirect.stagent.mail.MimeEntity;
import org.nhindirect.stagent.parser.EntitySerializer;
import org.nhindirect.stagent.utils.TestUtils;

public class SigTest extends TestCase
{
	
	public void testCreateVerifySig() throws Exception
	{
		X509CertificateEx internalCert = TestUtils.getInternalCert("user1");
		X509Certificate caCert = TestUtils.getExternalCert("cacert");
		
		
		String testMessage = TestUtils.readResource("MultipartMimeMessage.txt");
		
        MimeMessage entity = EntitySerializer.Default.deserialize(testMessage);
        Message message = new Message(entity);
        
        
        MimeEntity entityToSig = message.extractEntityForSignature(true);
        
        
        byte[] messageBytes = EntitySerializer.Default.serializeToBytes(entityToSig);     // Serialize message out as ASCII encoded...
    	
        MimeBodyPart partToSign = null;
        
        try
        {
        	partToSign = new MimeBodyPart(new ByteArrayInputStream(messageBytes));
        }
        catch (Exception e){}
        
    	SMIMESignedGenerator gen = new SMIMESignedGenerator();

    	ASN1EncodableVector signedAttrs = new ASN1EncodableVector();
    	SMIMECapabilityVector caps = new SMIMECapabilityVector();

    	caps.addCapability(SMIMECapability.dES_EDE3_CBC);
    	caps.addCapability(SMIMECapability.rC2_CBC, 128);
    	caps.addCapability(SMIMECapability.dES_CBC);
    	caps.addCapability(new DERObjectIdentifier("1.2.840.113549.1.7.1"));
    	caps.addCapability(PKCSObjectIdentifiers.x509Certificate);
    	signedAttrs.add(new SMIMECapabilitiesAttribute(caps));    	
    	
        List  certList = new ArrayList();
    	

    		gen.addSigner(internalCert.getPrivateKey(), internalCert,
    			 SMIMESignedGenerator.DIGEST_SHA1, new AttributeTable(signedAttrs), null);
    		     //SMIMESignedGenerator.DIGEST_SHA1, null, null);
    		
    		 certList.add(internalCert);
    	
    	MimeMultipart retVal = null;

        CertStore certsAndcrls = CertStore.getInstance("Collection", new CollectionCertStoreParameters(certList), CryptoExtensions.getJCEProviderName());        	        	
    	gen.addCertificatesAndCRLs(certsAndcrls);    		
		
    	 _certStores.add(certsAndcrls);
        _signers.add(new Signer(internalCert.getPrivateKey(), internalCert, SMIMESignedGenerator.DIGEST_SHA1, new AttributeTable(signedAttrs), null));    	
    	retVal = generate(partToSign, CryptoExtensions.getJCEProviderName());
    	
    	for (int i = 0; i < 10; ++ i)
    	{
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			retVal.writeTo(oStream);
			oStream.flush();
			byte[] serialzedBytes = oStream.toByteArray();
			
			//System.out.println(new String(serialzedBytes, "ASCII") + "\r\n\r\n\r\n\r\n\r\n");
			
			
			ByteArrayDataSource dataSource = new ByteArrayDataSource(serialzedBytes, retVal.getContentType());
			
			MimeMultipart verifyMM = new MimeMultipart(dataSource);	    	
			
			CMSSignedData signed = null;
	    	
	    	//CMSSignedData signeddata = new CMSSignedData(new CMSProcessableBodyPartInbound(verifyMM.getBodyPart(0)), verifyMM.getBodyPart(1).getInputStream());			
			CMSSignedData signeddata = new CMSSignedData(new CMSProcessableBodyPartInbound(partToSign), verifyMM.getBodyPart(1).getInputStream());
	    	
	    	int verified = 0;
	    	CertStore certs = signeddata.getCertificatesAndCRLs("Collection", CryptoExtensions.getJCEProviderName());
	    	SignerInformationStore  signers = signeddata.getSignerInfos();
	    	Collection              c = signers.getSigners();
	    	Iterator                it = c.iterator();	    	
	    	while (it.hasNext())
	    	{
	    		 SignerInformation   signer = (SignerInformation)it.next();
	    		 Collection          certCollection = certs.getCertificates(signer.getSID());
	    		   
	                Attribute dig = signer.getSignedAttributes().get(
                            CMSAttributes.messageDigest);
		
	                DERObject hashObj = dig.getAttrValues().getObjectAt(0).getDERObject();
	                
                    byte[]  signedHash = ((ASN1OctetString)hashObj).getOctets();
	    
	            	System.out.print("value of signedHash: \r\n\tvalue: ");
	            	for (byte bt : signedHash)
	            	{
	            		System.out.print(bt + " ");
	            		
	            	}
	            	System.out.println();  		 
	    		 
	    		  Iterator        certIt = certCollection.iterator();
	    		  try
	    		  {
	    			  assertTrue(signer.verify(internalCert, CryptoExtensions.getJCEProviderName()));
	    		  }
	    		  catch (Exception e) {e.printStackTrace();}
	    		  
	    		  byte[] bytes = signer.getContentDigest();
	    		  /*
	    		  X509Certificate cert = (X509Certificate)certIt.next();
	    		  
    		      if (signer.verify(cert.getPublicKey()))
    		      {
    		          verified++;
    		      }
	    		  */
	    		  verified++;
	    	}
    	}    	
	}
	
	
	
	private MimeMultipart generate(MimeBodyPart content , String prov) throws Exception
	{
		return make(makeContentBodyPart(content), prov);
	}
	
    private MimeMultipart make(
            MimeBodyPart    content,
            String          sigProvider)
            throws NoSuchAlgorithmException, NoSuchProviderException, SMIMEException
        {
            try
            {
                MimeBodyPart sig = new MimeBodyPart();

                sig.setContent(new ContentSigner(content, false, sigProvider), DETACHED_SIGNATURE_TYPE);
                //sig.setContent(new ContentSigner(content, true, sigProvider), DETACHED_SIGNATURE_TYPE);
                sig.addHeader("Content-Type", DETACHED_SIGNATURE_TYPE);
                sig.addHeader("Content-Disposition", "attachment; filename=\"smime.p7s\"");
                sig.addHeader("Content-Description", "S/MIME Cryptographic Signature");
                sig.addHeader("Content-Transfer-Encoding", encoding);

                //
                // build the multipart header
                //
                StringBuffer        header = new StringBuffer(
                        "signed; protocol=\"application/pkcs7-signature\"");
                        
                addHashHeader(header, _signers);
                
                MimeMultipart   mm = new MimeMultipart(header.toString());

                mm.addBodyPart(content);
                mm.addBodyPart(sig);

                return mm;
            }
            catch (MessagingException e)
            {
                throw new SMIMEException("exception putting multi-part together.", e);
            }
        }
	
    private MimeBodyPart makeContentBodyPart(
            MimeBodyPart    content)
            throws SMIMEException
        {
            //
            // add the headers to the body part - if they are missing, in
            // the event they have already been set the content settings override
            // any defaults that might be set.
            //
            try
            {
                MimeMessage     msg = new MimeMessage((Session)null);

                Enumeration     e = content.getAllHeaders();

                msg.setDataHandler(content.getDataHandler());

                while (e.hasMoreElements())
                {
                    Header  hdr =(Header)e.nextElement();

                    msg.setHeader(hdr.getName(), hdr.getValue());
                }

                msg.saveChanges();

                //
                // we do this to make sure at least the default headers are
                // set in the body part.
                //
                e = msg.getAllHeaders();

                while (e.hasMoreElements())
                {
                    Header  hdr =(Header)e.nextElement();

                    if (Strings.toLowerCase(hdr.getName()).startsWith("content-"))
                    {
                        content.setHeader(hdr.getName(), hdr.getValue());
                    }
                }
            }
            catch (MessagingException e)
            {
                throw new SMIMEException("exception saving message state.", e);
            }

            return content;
        }	
    
    
    private class ContentSigner
    implements SMIMEStreamingProcessor
    {
	    private final MimeBodyPart _content;
	    private final boolean      _encapsulate;
	    private final String       _provider;
	
	    ContentSigner(
	        MimeBodyPart content,
	        boolean      encapsulate,
	        String       provider)
	    {
	        _content = content;
	        _encapsulate = encapsulate;
	        _provider = provider;
	    }
	    
	    protected CMSSignedDataStreamGenerator getGenerator()
	        throws CMSException, CertStoreException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException
	    {
	        CMSSignedDataStreamGenerator gen = new CMSSignedDataStreamGenerator();
	        
	        for (Iterator it = _certStores.iterator(); it.hasNext();)
	        {
	            gen.addCertificatesAndCRLs((CertStore)it.next());
	        }
	
	        for (Iterator it = _attributeCerts.iterator(); it.hasNext();)
	        {
	            gen.addAttributeCertificates((X509Store)it.next());
	        }
	
	        for (Iterator it = _signers.iterator(); it.hasNext();)
	        {
	            Signer signer = (Signer)it.next();
	            
	            gen.addSigner(signer.getKey(), signer.getCert(), signer.getDigestOID(), signer.getSignedAttr(), signer.getUnsignedAttr(), _provider);
	        }
	
	        gen.addSigners(new SignerInformationStore(_oldSigners));
	        
	        return gen;
	    }
	
	    private void writeBodyPart(
	        OutputStream out,
	        MimeBodyPart bodyPart)
	        throws IOException, MessagingException
	    {
	        if (bodyPart.getContent() instanceof Multipart)
	        {
	            Multipart mp = (Multipart)bodyPart.getContent();
	            ContentType contentType = new ContentType(mp.getContentType());
	            String boundary = "--" + contentType.getParameter("boundary");
	
	            LineOutputStream lOut = new LineOutputStream(out);
	
	            Enumeration headers = bodyPart.getAllHeaderLines();
	            while (headers.hasMoreElements())
	            {
	                lOut.writeln((String)headers.nextElement());
	            }
	
	            lOut.writeln();      // CRLF separator
	
	            outputPreamble(lOut, bodyPart, boundary);
	
	            for (int i = 0; i < mp.getCount(); i++)
	            {
	                lOut.writeln(boundary);
	                writeBodyPart(out, (MimeBodyPart)mp.getBodyPart(i));
	                lOut.writeln();       // CRLF terminator
	            }
	            
	            lOut.writeln(boundary + "--");
	        }
	        else
	        {
	            if (isCanonicalisationRequired(bodyPart, _defaultContentTransferEncoding))
	            {
	                out = new CRLFOutputStream(out);
	            }
	
	            bodyPart.writeTo(out);
	        }
	    }
	
	    public void write(OutputStream out)
	        throws IOException
	    {
	        try
	        {
	            CMSSignedDataStreamGenerator gen = getGenerator();
	            
	            OutputStream signingStream = gen.open(out, _encapsulate);
	            
	            if (_content != null)
	            {
	                if (!_encapsulate)
	                {
	                    writeBodyPart(signingStream, _content);
	                }
	                else
	                {
	                    _content.writeTo(signingStream);
	                }
	            }
	            
	            signingStream.close();
	
	            
	            _digests = gen.getGeneratedDigests();
	           
	            
	            for (Map.Entry entry : (Set<Entry>)_digests.entrySet())
	            {
	            	byte[] bytes = (byte[])entry.getValue();
	            	
	            	System.out.print("digest of content: \r\n\tOID:" + entry.getKey() + "\r\n\tValue: ");
	            	for (byte bt : bytes)
	            	{
	            		System.out.print(bt + " ");
	            		
	            	}
	            	System.out.println();
	            }
	            	            
	            
	        }
	        catch (MessagingException e)
	        {
	            throw new IOException(e.toString());
	        }
	        catch (NoSuchAlgorithmException e)
	        {
	            throw new IOException(e.toString());
	        }
	        catch (NoSuchProviderException e)
	        {
	            throw new IOException(e.toString());
	        }
	        catch (CMSException e)
	        {
	            throw new IOException(e.toString());
	        }
	        catch (InvalidKeyException e)
	        {
	            throw new IOException(e.toString());
	        }
	        catch (CertStoreException e)
	        {
	            throw new IOException(e.toString());
	        }
	    }
    }
    
    
    
    private void addHashHeader(
            StringBuffer header,
            List         signers)
        {
            int                 count = 0;
            
            //
            // build the hash header
            //
            Iterator   it = signers.iterator();
            Set        micAlgs = new HashSet();
            
            while (it.hasNext())
            {
                Signer       signer = (Signer)it.next();
                
                if (signer.getDigestOID().equals(DIGEST_SHA1))
                {
                    micAlgs.add("sha1");
                }
                else if (signer.getDigestOID().equals(DIGEST_MD5))
                {
                    micAlgs.add("md5");
                }
                else if (signer.getDigestOID().equals(DIGEST_SHA224))
                {
                    micAlgs.add("sha224");
                }
                else if (signer.getDigestOID().equals(DIGEST_SHA256))
                {
                    micAlgs.add("sha256");
                }
                else if (signer.getDigestOID().equals(DIGEST_SHA384))
                {
                    micAlgs.add("sha384");
                }
                else if (signer.getDigestOID().equals(DIGEST_SHA512))
                {
                    micAlgs.add("sha512");
                }
                else if (signer.getDigestOID().equals(DIGEST_GOST3411))
                {
                    micAlgs.add("gostr3411-94");
                }
                else
                {
                    micAlgs.add("unknown");
                }
            }
            
            it = micAlgs.iterator();
            
            while (it.hasNext())
            {
                String    alg = (String)it.next();

                if (count == 0)
                {
                    if (micAlgs.size() != 1)
                    {
                        header.append("; micalg=\"");
                    }
                    else
                    {
                        header.append("; micalg=");
                    }
                }
                else
                {
                    header.append(',');
                }

                header.append(alg);

                count++;
            }

            if (count != 0)
            {
                if (micAlgs.size() != 1)
                {
                    header.append('\"');
                }
            }
        }
    
    
    class Signer
    {
        final PrivateKey      key;
        final X509Certificate cert;
        final String          digestOID;
        final AttributeTable  signedAttr;
        final AttributeTable  unsignedAttr;
        
        Signer(
            PrivateKey      key,
            X509Certificate cert,
            String          digestOID,
            AttributeTable  signedAttr,
            AttributeTable  unsignedAttr)
        {
            this.key = key;
            this.cert = cert;
            this.digestOID = digestOID;
            this.signedAttr = signedAttr;
            this.unsignedAttr = unsignedAttr;
        }

        public X509Certificate getCert()
        {
            return cert;
        }

        public String getDigestOID()
        {
            return digestOID;
        }

        public PrivateKey getKey()
        {
            return key;
        }

        public AttributeTable getSignedAttr()
        {
            return signedAttr;
        }

        public AttributeTable getUnsignedAttr()
        {
            return unsignedAttr;
        }
    }    
    
    static class LineOutputStream extends FilterOutputStream
    {
        private static byte newline[];

        public LineOutputStream(OutputStream outputstream)
        {
            super(outputstream);
        }

        public void writeln(String s)
            throws MessagingException
        {
            try
            {
                byte abyte0[] = getBytes(s);
                super.out.write(abyte0);
                super.out.write(newline);
            }
            catch(Exception exception)
            {
                throw new MessagingException("IOException", exception);
            }
        }

        public void writeln()
            throws MessagingException
        {
            try
            {
                super.out.write(newline);
            }
            catch(Exception exception)
            {
                throw new MessagingException("IOException", exception);
            }
        }

        static 
        {
            newline = new byte[2];
            newline[0] = 13;
            newline[1] = 10;
        }
        
        private static byte[] getBytes(String s)
        {
            char ac[] = s.toCharArray();
            int i = ac.length;
            byte abyte0[] = new byte[i];
            int j = 0;

            while (j < i)
            {
                abyte0[j] = (byte)ac[j++];
            }

            return abyte0;
        }
    }    
    
    
    static void outputPreamble(LineOutputStream lOut, MimeBodyPart part, String boundary)
    throws MessagingException, IOException
	{
	    InputStream in;
	
	    try
	    {
	        in = part.getRawInputStream();
	    }
	    catch (MessagingException e)
	    {
	        return;   // no underlying content rely on default generation
	    }
	
	    String line;
	
	    while ((line = readLine(in)) != null)
	    {
	        if (line.equals(boundary))
	        {
	            break;
	        }
	
	        lOut.writeln(line);
	    }
	
	    in.close();
	
	    if (line == null)
	    {
	        throw new MessagingException("no boundary found");
	    }
	}
    
    
	    private static String readLine(InputStream in)
	    throws IOException
	{
	    StringBuffer b = new StringBuffer();
	
	    int ch;
	    while ((ch = in.read()) >= 0 && ch != '\n')
	    {
	        if (ch != '\r')
	        {
	            b.append((char)ch);
	        }
	    }
	
	    if (ch < 0)
	    {
	        return null;
	    }
	    
	    return b.toString();
	}    
    
    static boolean isCanonicalisationRequired(
            MimeBodyPart   bodyPart,
            String defaultContentTransferEncoding) 
            throws MessagingException
        {
            String[]        cte = bodyPart.getHeader("Content-Transfer-Encoding");
            String          contentTransferEncoding;

            if (cte == null)
            {
                contentTransferEncoding = defaultContentTransferEncoding;
            }
            else
            {
                contentTransferEncoding = cte[0];
            }

            return !contentTransferEncoding.equalsIgnoreCase("binary");
        }	    
	    
    public static final String  DIGEST_SHA1 = OIWObjectIdentifiers.idSHA1.getId();
    public static final String  DIGEST_MD5 = PKCSObjectIdentifiers.md5.getId();
    public static final String  DIGEST_SHA224 = NISTObjectIdentifiers.id_sha224.getId();
    public static final String  DIGEST_SHA256 = NISTObjectIdentifiers.id_sha256.getId();
    public static final String  DIGEST_SHA384 = NISTObjectIdentifiers.id_sha384.getId();
    public static final String  DIGEST_SHA512 = NISTObjectIdentifiers.id_sha512.getId();
    public static final String  DIGEST_GOST3411 = CryptoProObjectIdentifiers.gostR3411.getId();
    public static final String  DIGEST_RIPEMD128 = TeleTrusTObjectIdentifiers.ripemd128.getId();
    public static final String  DIGEST_RIPEMD160 = TeleTrusTObjectIdentifiers.ripemd160.getId();
    public static final String  DIGEST_RIPEMD256 = TeleTrusTObjectIdentifiers.ripemd256.getId();

    public static final String  ENCRYPTION_RSA = PKCSObjectIdentifiers.rsaEncryption.getId();
    public static final String  ENCRYPTION_DSA = X9ObjectIdentifiers.id_dsa_with_sha1.getId();
    public static final String  ENCRYPTION_ECDSA = X9ObjectIdentifiers.ecdsa_with_SHA1.getId();
    public static final String  ENCRYPTION_RSA_PSS = PKCSObjectIdentifiers.id_RSASSA_PSS.getId();
    public static final String  ENCRYPTION_GOST3410 = CryptoProObjectIdentifiers.gostR3410_94.getId();
    public static final String  ENCRYPTION_ECGOST3410 = CryptoProObjectIdentifiers.gostR3410_2001.getId();

    private static final String CERTIFICATE_MANAGEMENT_CONTENT = "application/pkcs7-mime; name=smime.p7c; smime-type=certs-only";
    private static final String DETACHED_SIGNATURE_TYPE = "application/pkcs7-signature; name=smime.p7s; smime-type=signed-data";
    private static final String ENCAPSULATED_SIGNED_CONTENT_TYPE = "application/pkcs7-mime; name=smime.p7m; smime-type=signed-data";    
    
    protected boolean                     useBase64 = true;
    protected String                      encoding = "base64";  // default sets base64
    
    private List                _certStores = new ArrayList();
    private List                _signers = new ArrayList();
    private List                _oldSigners = new ArrayList();
    private List                _attributeCerts = new ArrayList();
    private Map                 _digests = new HashMap();    
    
    private final String        _defaultContentTransferEncoding = "7bit";
}
