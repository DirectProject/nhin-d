/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.stagent.cryptography.activekeyops;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BERConstructedOctetString;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.cms.SignerIdentifier;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.TBSCertificateStructure;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;
import org.bouncycastle.cms.SimpleAttributeTableGenerator;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.cryptography.DigestAlgorithm;
import org.nhindirect.stagent.cryptography.EncryptionAlgorithm;

/**
 * An implementation that of the DirectSignedDataGenerator interface that allows for an optimized set of signing operations.
 * Unlike the BouncyCastle library which performs both the calculation of the message digest and the signing of the message digiest
 * using only one JCE provider, this implementation allows the two different operations to be split across different JCE providers.  
 * This is optimal in situations where asymmetric private key information MUST be protected by a PKCS11 token.  If the sigProvider is set to
 * the JCE provider of a PKCS11 token and the digestProvider is set to an in process JCE provider, this allows for the signing operations to be performed on
 * the token but for the digest calculation to be performed in process.  This minimizes the amount of information that needs to be sent to the 
 * PKCS11 token. 
 * @author Greg Meyer
 * @since 2.1
 */
public class SplitProviderDirectSignedDataGenerator extends CMSSignedDataGenerator implements DirectSignedDataGenerator
{
	protected final String sigProvider;
	protected final String digestProvider;
	
	protected final List<DirectTargetedSignerInf> privateSigners = new ArrayList<DirectTargetedSignerInf>();
	
	/**
	 * Constructor
	 * @param sigProvider The name of the JCE provider used to perform the signing operation.  If this is null or empty,
	 * the CryptoExtensions.getJCESensitiveProviderName() value will be used.
	 * @param digestProvider The name of the JCE provider used to perform the digest operation.  If this is null or empty,
	 * the CryptoExtensions.getJCEProviderName() value will be used.
	 */
	public SplitProviderDirectSignedDataGenerator(String sigProvider, String digestProvider)
	{
		super();
		
		this.sigProvider =  (StringUtils.isEmpty(sigProvider)) ? CryptoExtensions.getJCESensitiveProviderName() : sigProvider;
		this.digestProvider =  (StringUtils.isEmpty(sigProvider)) ? CryptoExtensions.getJCEProviderName() : digestProvider;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CMSSignedData generate(CMSProcessable content)
			throws NoSuchAlgorithmException, NoSuchProviderException, CMSException 
	{
		return generate(content, false, sigProvider);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addSigner(PrivateKey key, X509Certificate cert,
		    String digestOID, AttributeTable  signedAttr,
		    AttributeTable unsignedAttr) throws IllegalArgumentException
    {
		final String encOID = getEncOID(key, digestOID);
		
		privateSigners.add(new DirectTargetedSignerInf(key, cert, digestOID, encOID, 
				new DefaultSignedAttributeTableGenerator(signedAttr), new SimpleAttributeTableGenerator(unsignedAttr), signedAttr));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CMSSignedData generate(
		        String                  signedContentType,
		        CMSProcessable          content,
		        boolean                 encapsulate,
		        String                  sigProvider,
		        boolean                 addDefaultAttributes)
		        throws NoSuchAlgorithmException, NoSuchProviderException, CMSException
    {
		        final ASN1EncodableVector  digestAlgs = new ASN1EncodableVector();
		        final ASN1EncodableVector  signerInfos = new ASN1EncodableVector();

		        _digests.clear();  // clear the current preserved digest state
		        
		        //
		        // add the SignerInfo objects
		        //
		        DERObjectIdentifier  contentTypeOID;
		        boolean              isCounterSignature;

		        if (signedContentType != null)
		        {
		            contentTypeOID = new DERObjectIdentifier(signedContentType);
		            isCounterSignature = false;
		        }
		        else
		        {
		            contentTypeOID = CMSObjectIdentifiers.data;
		            isCounterSignature = true;
		        }

		        for (DirectTargetedSignerInf signer : privateSigners)
		        {
		            AlgorithmIdentifier  digAlgId;

		            try
		            {
		                digAlgId = new AlgorithmIdentifier(new DERObjectIdentifier(signer.digestOID), new DERNull());

		                digestAlgs.add(digAlgId);

		                signerInfos.add(signer.toSignerInfo(contentTypeOID, content, rand, sigProvider, 
		                		digestProvider, addDefaultAttributes, isCounterSignature));
		            }
		            catch (IOException e)
		            {
		                throw new CMSException("encoding error.", e);
		            }
		            catch (InvalidKeyException e)
		            {
		                throw new CMSException("key inappropriate for signature.", e);
		            }
		            catch (SignatureException e)
		            {
		                throw new CMSException("error creating signature.", e);
		            }
		            catch (CertificateEncodingException e)
		            {
		                throw new CMSException("error creating sid.", e);
		            }
		        }

		        ASN1Set certificates = null;

		        if (_certs.size() != 0)
		        {
		            certificates = createBerSetFromList(_certs);
		        }

		        ASN1Set certrevlist = null;

		        if (_crls.size() != 0)
		        {
		            certrevlist = createBerSetFromList(_crls);
		        }

		        ContentInfo encInfo;
		        
		        if (encapsulate)
		        {
		            ByteArrayOutputStream   bOut = new ByteArrayOutputStream();

		            try
		            {
		                content.write(bOut);
		            }
		            catch (IOException e)
		            {
		                throw new CMSException("encapsulation error.", e);
		            }

		            ASN1OctetString  octs = new BERConstructedOctetString(
		                                                    bOut.toByteArray());

		            encInfo = new ContentInfo(contentTypeOID, octs);
		        }
		        else
		        {
		            encInfo = new ContentInfo(contentTypeOID, null);
		        }

		        SignedData  sd = new SignedData(
		                                 new DERSet(digestAlgs),
		                                 encInfo, 
		                                 certificates, 
		                                 certrevlist, 
		                                 new DERSet(signerInfos));

		        ContentInfo contentInfo = new ContentInfo(
		                PKCSObjectIdentifiers.signedData, sd);

		        return new CMSSignedData(content, contentInfo);
	}
	
	/**
	 * This is an override of the BouncyCastle SignerInf class.  The SignerInf class is private, so a re-implementation is necessary.
	 * The main differences between this class and the BouncyCastle library is that this class allows for separate JCE providers for 
	 * digital signature and digest operations.  Digests do not require private key material, so it may be beneficial for performance reasons to
	 * create a digest in a local provider then sign the digest in another provider such as one implemented by a hardware security module.
	 */
    protected class DirectTargetedSignerInf
    {
        protected final PrivateKey                  key;
        protected final X509Certificate             cert;
        protected final String                      digestOID;
        protected final String                      encOID;
        protected final CMSAttributeTableGenerator  sAttr;
        protected final CMSAttributeTableGenerator  unsAttr;
        protected final AttributeTable              baseSignedTable;

        DirectTargetedSignerInf(PrivateKey key, X509Certificate cert, String digestOID, String encOID,
            CMSAttributeTableGenerator sAttr, CMSAttributeTableGenerator unsAttr, AttributeTable baseSigneTable)
        {
            this.key = key;
            this.cert = cert;
            this.digestOID = digestOID;
            this.encOID = encOID;
            this.sAttr = sAttr;
            this.unsAttr = unsAttr;
            this.baseSignedTable = baseSigneTable;
        }

        @SuppressWarnings("unchecked")
		protected SignerInfo toSignerInfo(DERObjectIdentifier contentType, CMSProcessable content, SecureRandom random,
            String sigProvider, String  digestProvider, boolean addDefaultAttributes, boolean isCounterSignature)
            throws IOException, SignatureException, InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException, CertificateEncodingException, CMSException
        {      	
            final AlgorithmIdentifier digAlgId = new AlgorithmIdentifier(
                  new DERObjectIdentifier(this.digestOID), new DERNull());
            final AlgorithmIdentifier encAlgId = getEncAlgorithmIdentifier(this.encOID);
            final String              digestName = DigestAlgorithm.fromOID(digestOID, DigestAlgorithm.SHA256).getAlgName();
            final String              signatureName = digestName + "with" + EncryptionAlgorithm.fromOID(encOID, EncryptionAlgorithm.RSA).getAlgName();
            
            // sign with the signature provider... this may be an out of process
            // provider such as an HSM
            final Signature           sig = Signature.getInstance(signatureName, sigProvider);
            
            // sign with the digital signature provider... this will most likely be an in process
            // provider (for performance reasons) and be a separate provider than the signature provider (although
            // this is not mandated)
            final MessageDigest       dig = MessageDigest.getInstance(digestName, digestProvider);               

            // create the hash
            byte[]      hash = null;
            if (content != null)
            {
                content.write(new DigOutputStream(dig));

                hash = dig.digest();

                _digests.put(digestOID, hash.clone());
            }

            AttributeTable signed;

            if (addDefaultAttributes)
            {
                @SuppressWarnings("rawtypes")
				final Map parameters = getBaseParameters(contentType, digAlgId, hash);
                signed = (sAttr != null) ? sAttr.getAttributes(Collections.unmodifiableMap(parameters)) : null;
            }
            else
            {
                signed = baseSignedTable;
            }

            if (isCounterSignature)
            {
                @SuppressWarnings("rawtypes")
				final Hashtable ats = signed.toHashtable();

                ats.remove(CMSAttributes.contentType);

                signed = new AttributeTable(ats);
            }
            
            final ASN1Set signedAttr = getAttributeSet(signed);

            //
            // sig must be composed from the DER encoding.
            //
            byte[] tmp;
            if (signedAttr != null) 
            {
                tmp = signedAttr.getEncoded(ASN1Encodable.DER);
            } 
            else
            {
                final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                try
                {
	                content.write(bOut);
	                tmp = bOut.toByteArray();
                }
                finally
                {
                	IOUtils.closeQuietly(bOut);
                }
            }

            // create the digital signature with the hash of the content
            sig.initSign(key, random);

            sig.update(tmp);

            final ASN1OctetString         encDigest = new DEROctetString(sig.sign());

            @SuppressWarnings("rawtypes")
			final Map parameters = getBaseParameters(contentType, digAlgId, hash);
            parameters.put(CMSAttributeTableGenerator.SIGNATURE, encDigest.getOctets().clone());

            final AttributeTable unsigned = (unsAttr != null) ? unsAttr.getAttributes(Collections.unmodifiableMap(parameters)) : null;

            final ASN1Set unsignedAttr = getAttributeSet(unsigned);

            ASN1InputStream  aIn = null;
            ByteArrayInputStream  bIn = null;
            try
            {
	            
	            final X509Certificate         cert = this.cert;
	            bIn = new ByteArrayInputStream(cert.getTBSCertificate());
	            aIn = new ASN1InputStream(bIn);
	            final TBSCertificateStructure tbs = TBSCertificateStructure.getInstance(aIn.readObject());
	            final IssuerAndSerialNumber   encSid = new IssuerAndSerialNumber(tbs.getIssuer(), tbs.getSerialNumber().getValue());
	
	            return new SignerInfo(new SignerIdentifier(encSid), digAlgId,
	                        signedAttr, encAlgId, encDigest, unsignedAttr);
            }
            finally
            {
            	IOUtils.closeQuietly(bIn);
            	IOUtils.closeQuietly(aIn);
            }
        }
    }
    
    
    private static class DigOutputStream extends OutputStream
    {
	    final MessageDigest   dig;
	
	    public DigOutputStream(MessageDigest   dig)
	    {
	        this.dig = dig;
	    }
	
	    @Override
	    public void write(byte[] b, int off, int len) throws IOException
	    {
	        dig.update(b, off, len);
	    }
	
	    @Override
	    public void write(int b) throws IOException
	    {
	        dig.update((byte)b);
	    }
    }
    
    @SuppressWarnings("rawtypes")
    private static ASN1Set createBerSetFromList(List derObjects)
    {
        ASN1EncodableVector v = new ASN1EncodableVector();

        for (Iterator it = derObjects.iterator(); it.hasNext();)
        {
            v.add((DEREncodable)it.next());
        }

        return new BERSet(v);
    }
}
