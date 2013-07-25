/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Umesh Madan     umeshma@microsoft.com
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

package org.nhindirect.stagent.cryptography;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;

import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.ParseException;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.smime.SMIMECapabilitiesAttribute;
import org.bouncycastle.asn1.smime.SMIMECapability;
import org.bouncycastle.asn1.smime.SMIMECapabilityVector;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.mail.smime.CMSProcessableBodyPart;
import org.bouncycastle.mail.smime.SMIMEEnveloped;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.NHINDException;
import org.nhindirect.stagent.SignatureValidationException;
import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.cryptography.annotation.IncludeEpilogInSig;
import org.nhindirect.stagent.mail.Message;
import org.nhindirect.stagent.mail.MimeEntity;
import org.nhindirect.stagent.mail.MimeError;
import org.nhindirect.stagent.mail.MimeException;
import org.nhindirect.stagent.mail.MimeStandard;
import org.nhindirect.stagent.parser.EntitySerializer;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;

/**
 * Executes the cryptography operations.  This include encryption, decryption, and signature generation. 
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
@SuppressWarnings("unchecked")
public class SMIMECryptographerImpl implements Cryptographer
{

	private static final Log LOGGER = LogFactory.getFactory().getInstance(SMIMECryptographerImpl.class);
	
    public final static SMIMECryptographerImpl Default = new SMIMECryptographerImpl();
    
    private EncryptionAlgorithm m_encryptionAlgorithm;
    private DigestAlgorithm m_digestAlgorithm;
    private boolean m_includeEpilogue = true;

    /**
     * Constructs a Cryptographer with a default EncryptionAlgorithm and DigestAlgorithm.
     */
    public SMIMECryptographerImpl()
    {
        this.m_encryptionAlgorithm = EncryptionAlgorithm.AES128;
        this.m_digestAlgorithm = DigestAlgorithm.SHA1;
    }

    /**
     * Constructs a Cryptographer with an EncryptionAlgorithm and DigestAlgorithm.
     * @param encryptionAlgorithm The encryption algorithm used to encrypt the message.
     * @param digestAlgorithm The digest algorithm used to generate the message digest stored in the message signature.
     */    
    public SMIMECryptographerImpl(EncryptionAlgorithm encryptionAlgorithm, DigestAlgorithm digestAlgorithm)
    {
        this.m_encryptionAlgorithm = encryptionAlgorithm;
        this.m_digestAlgorithm = digestAlgorithm;
    }

    /**
     * Gets the EncryptionAlgorithm.
     * @return The EncryptionAlgorithm used to encrypt messages.
     */
    public EncryptionAlgorithm getEncryptionAlgorithm()
    {
        return this.m_encryptionAlgorithm;
    }
    
    /**
     * Sets the EncryptionAlgorithm
     * @param value The EncryptionAlgorithm used to encrypt messages.
     */
    @Inject(optional=true)
    public void setEncryptionAlgorithm(EncryptionAlgorithm value)
    {
        this.m_encryptionAlgorithm = value;
    }

    /**
     * Gets the DigestAlgorithm.
     * @return The DigestAlgorithm used generate the message digest stored in the message signature.
     */
    public DigestAlgorithm getDigestAlgorithm()
    {
    	return this.m_digestAlgorithm;
    }
    
    /**
     * Sets the DigestAlgorithm.
     * @param value The DigestAlgorithm used generate the message digest stored in the message signature.
     */   
    @Inject(optional=true)
    public void setDigestAlgorithm(DigestAlgorithm value)
    {
        this.m_digestAlgorithm = value;
    }
    
    /**
     * Indicates if the the Epilogue part of a multipart entity should be used to generate the message signature.
     * @return True if the the Epilogue part of a multipart entity should be used to generate the message signature.  False otherwise.
     */
    public boolean isIncludeMultipartEpilogueInSignature()
    {
        return this.m_includeEpilogue;
    }            
    
    /**
     * Sets if the the Epilogue part of a multipart entity should be used to generate the message signature.
     * @param value True if the the Epilogue part of a multipart entity should be used to generate the message signature.  False otherwise.
     */   
    @Inject(optional=true)
    public void setIncludeMultipartEpilogueInSignature(@IncludeEpilogInSig boolean value)
    {
        this.m_includeEpilogue = value;
    }
    
    /*
     * Encryption
     */
    
    /**
     * 
     * Encrypts a mulit part MIME entity using the provided certificate.
     * @param entity The entity that will be encrypted.
     * @param encryptingCertificate The public certificates that will be used to encrypt the message.
     * @return A MimeEntity containing the encrypted part.
     */
    public MimeEntity encrypt(MimeMultipart entity, X509Certificate encryptingCertificate)
    {
    	Collection<X509Certificate> certs = new ArrayList<X509Certificate>();
    	certs.add(encryptingCertificate);
    	
        return this.encrypt(entity, certs);
    }    
    
    /**
     * Encrypts a mulit part MIME entity using the provided certificates.
     * @param entity The entity that will be encrypted.
     * @param encryptingCertificates The public certificates that will be used to encrypt the message.
     * @return A MimeEntity containing the encrypted part.
     */
    public MimeEntity encrypt(MimeMultipart mmEntity, Collection<X509Certificate> encryptingCertificates)
    {
    	MimeEntity entToEncrypt = null;
    	
    	ByteArrayOutputStream oStream = new ByteArrayOutputStream();
    	try
    	{
	    	mmEntity.writeTo(oStream);
	    	oStream.flush();
	    	InternetHeaders headers = new InternetHeaders();
	    	headers.addHeader(MimeStandard.ContentTypeHeader, mmEntity.getContentType());
	    	
	    	
	    	entToEncrypt = new MimeEntity(headers, oStream.toByteArray());
	    	oStream.close();
    	}    	
    	catch (Exception e)
    	{
    		throw new MimeException(MimeError.InvalidMimeEntity, e);
    	}
        
    	return this.encrypt(entToEncrypt, encryptingCertificates);
    }       
    
    /**
     * Encrypts an entity using the provided certificate.
     * @param entity The entity that will be encrypted.
     * @param encryptingCertificate The public certificates that will be used to encrypt the message.
     * @return A MimeEntity containing the encrypted part.
     */
    public MimeEntity encrypt(MimeEntity entity, X509Certificate encryptingCertificate)
    {
    	Collection<X509Certificate> certs = new ArrayList<X509Certificate>();
    	certs.add(encryptingCertificate);
    	
        return this.encrypt(entity, certs);
    }

    /** 
     * Encrypts an entity using the provided certificates.
     * @param entity The entity that will be encrypted.
     * @param encryptingCertificate The public certificates that will be used to encrypt the message.
     * @return A MimeEntity containing the encrypted part.
     */
    public MimeEntity encrypt(MimeEntity entity,  Collection<X509Certificate> encryptingCertificates)
    {
        if (entity == null)
        {
            throw new IllegalArgumentException();
        }
        	
        MimeBodyPart partToEncrypt = entity;                
        MimeBodyPart encryptedPart =  this.encrypt(partToEncrypt, encryptingCertificates);
        MimeEntity encryptedEntity = null;
        
        try
        {
        	byte[] encBytes = EntitySerializer.Default.serializeToBytes(encryptedPart);
        	ByteArrayInputStream inStream = new ByteArrayInputStream(EntitySerializer.Default.serializeToBytes(encryptedPart));
        	encryptedEntity = new MimeEntity(inStream);
        	
            if (LOGGER.isDebugEnabled())
            {	
            	writePostEncypt(encBytes);
            }        

            encryptedEntity.setHeader(MimeStandard.ContentTypeHeader, SMIMEStandard.EncryptedContentTypeHeaderValue);
            
        }
        catch (Exception e)
        {
        	throw new MimeException(MimeError.Unexpected, e);
        }

        return encryptedEntity;
    }

    private MimeBodyPart encrypt(MimeBodyPart bodyPart, Collection<X509Certificate> encryptingCertificates)
    {
        return this.createEncryptedEnvelope(bodyPart, encryptingCertificates);
    }
            
    private MimeBodyPart createEncryptedEnvelope(MimeBodyPart bodyPart, Collection<X509Certificate> encryptingCertificates)
    {
        if (bodyPart == null || encryptingCertificates == null || encryptingCertificates.size() == 0)
        {
            throw new IllegalArgumentException();
        }
        
        if (LOGGER.isDebugEnabled())
        {	
        	writePreEncypt(EntitySerializer.Default.serializeToBytes(bodyPart));
        }          
        
        SMIMEEnvelopedGenerator gen = new SMIMEEnvelopedGenerator();

        for(X509Certificate cert : encryptingCertificates)
        	gen.addKeyTransRecipient(cert);
        
        MimeBodyPart retVal = null;
        
        try
        {
        	retVal =  gen.generate(bodyPart, toEncyAlgorithmOid(this.m_encryptionAlgorithm), CryptoExtensions.getJCEProviderName());
        }
        catch (Exception e)
        {
        	throw new MimeException(MimeError.Unexpected, e);
        }
        
        return retVal;
    }

    //-----------------------------------------------------
    //
    // Decryption
    //
    //-----------------------------------------------------

    /**
     * Decrypts a message with the provided certificates private key.
     * @param message The message that will be decrypted.
     * @param decryptingCertificate The certificate whose private key will be used to decrypt the message.
     * @return A MimeEntity containing the decrypted part.
     */    
    public MimeEntity decrypt(Message message, X509CertificateEx decryptingCertificate)
    {
        return this.decrypt(message.extractMimeEntity(), decryptingCertificate);
    }
    
    /**
     * Decrypts an entity with the provided certificate's private key.
     * @param encryptedEntity The entity that will be decrypted.
     * @param decryptingCertificate The certificate whose private key will be used to decrypt the message.
     * @return A MimeEntity containing the decrypted part.
     */  
    public MimeEntity decrypt(MimeEntity encryptedEntity, X509CertificateEx decryptingCertificate)
    {
        if (encryptedEntity == null || decryptingCertificate == null)
        {
            throw new IllegalArgumentException();
        }
        
        if (!decryptingCertificate.hasPrivateKey())
        {
            throw new IllegalArgumentException("Certificate has no private key");
        }
        											   
        encryptedEntity.verifyContentType(SMIMEStandard.EncryptedContentTypeHeaderValue);
        encryptedEntity.verifyTransferEncoding(MimeStandard.TransferEncodingBase64);
        
    	Collection<X509CertificateEx> certs = new ArrayList<X509CertificateEx>();
    		certs.add(decryptingCertificate);
    		
        MimeEntity retVal = this.decrypt(encryptedEntity, certs);
        
        //
        // And turn the decrypted bytes back into an entity
        //
        return retVal;
    }
    
    /**
     * Decrypts an entity with the provided certificates' private key.
     * @param encryptedEntity The entity that will be decrypted.
     * @param decryptingCertificate The certificates whose private keys will be used to decrypt the message.
     * @return A MimeEntity containing the decrypted part.
     */  
    public MimeEntity decrypt(MimeEntity encryptedEntity, Collection<X509CertificateEx> decryptingCertificates)
    {
        if (decryptingCertificates == null || decryptingCertificates.size() == 0)
        {
            throw new IllegalArgumentException();
        }

        MimeEntity retEntity = null;
        try
        {        	                	
            if (LOGGER.isDebugEnabled())
            {	
            	byte[] encryptedContent = encryptedEntity.getContentAsBytes();
            	writePreDecrypt(encryptedContent);
            }   
            
            SMIMEEnveloped m = new SMIMEEnveloped(encryptedEntity);            
            
            X509CertificateEx decryptCert = decryptingCertificates.iterator().next();
            
            RecipientId recId = new RecipientId();        	
	        recId.setSerialNumber(decryptCert.getSerialNumber());
	        recId.setIssuer(decryptCert.getIssuerX500Principal().getEncoded());
	
	        RecipientInformationStore recipients = m.getRecipientInfos();
	        RecipientInformation recipient = recipients.get(recId);	
	        	        	        	       

	        byte[] decryptedPayload = recipient.getContent(decryptCert.getPrivateKey(), CryptoExtensions.getJCEProviderName());
	        
            if (LOGGER.isDebugEnabled())
            {	
            	writePostDecrypt(decryptedPayload);
            }   
	        
            ByteArrayInputStream inStream = new ByteArrayInputStream(decryptedPayload);
            
	        retEntity = new MimeEntity(inStream);
	        
        }
        catch (MessagingException e)
        {
        	throw new MimeException(MimeError.InvalidMimeEntity, e);
        }
        catch (Exception e)
        {
        	throw new MimeException(MimeError.Unexpected, e);
        }

        return retEntity;
    }

    /**
     * Signs a message with the provided certificate.
     * @param message The message that will be signed.
     * @param signingCertificate The certificate used to sign the message.
     * @return A signed entity that consists of a multipart/signed entity containing the original entity and a message signature. 
     */    
    public SignedEntity sign(Message message, X509Certificate signingCertificate)
    {
        return this.sign(message.extractEntityForSignature(this.m_includeEpilogue), signingCertificate);
    }
    
      
    public SignedEntity sign(Message message, Collection<X509Certificate> signingCertificates)
    {
        return this.sign(message.extractEntityForSignature(this.m_includeEpilogue), signingCertificates);
    }    
    
    /**
     * Signs an entity with the provided certificate.
     * @param message The entity that will be signed.
     * @param signingCertificate The certificate used to sign the message.
     * @return A signed entity that consists of a multipart/signed entity containing the original entity and a message signature. 
     */  
    public SignedEntity sign(MimeEntity entity, X509Certificate signingCertificate)        
    {
    	Collection<X509Certificate> certs = new ArrayList<X509Certificate>();
    	certs.add(signingCertificate);
    	
        return this.sign(entity, certs);
    }
    
    /**
     * Signs an entity with the provided certificates.
     * @param message The entity that will be signed.
     * @param signingCertificates The certificates used to sign the message.
     * @return A signed entity that consists of a multipart/signed entity containing the original entity and a message signature. 
     */ 
    public SignedEntity sign(MimeEntity entity, Collection<X509Certificate> signingCertificates)
    {
        if (entity == null)
        {
            throw new IllegalArgumentException();
        }

        byte[] messageBytes = EntitySerializer.Default.serializeToBytes(entity);     // Serialize message out as ASCII encoded...
     
        MimeMultipart mm = this.createSignatureEntity(messageBytes, signingCertificates);
        SignedEntity retVal = null;
        
        try
        {
        
        	retVal = new SignedEntity(new ContentType(mm.getContentType()), mm);
        }
        catch (ParseException e)
        {
        	throw new MimeException(MimeError.InvalidHeader, e);
        }
        
        return retVal;
    }

    private MimeMultipart createSignatureEntity(byte[] entity, Collection<X509Certificate> signingCertificates)
    {    	
    	MimeMultipart retVal = null;
    	try
   	{
	        MimeBodyPart signedContent = new MimeBodyPart(new ByteArrayInputStream(entity));
	    		        
	    	ASN1EncodableVector signedAttrs = new ASN1EncodableVector();
	    	SMIMECapabilityVector caps = new SMIMECapabilityVector();
	
	    	caps.addCapability(SMIMECapability.dES_EDE3_CBC);
	    	caps.addCapability(SMIMECapability.rC2_CBC, 128);
	    	caps.addCapability(SMIMECapability.dES_CBC);
	    	caps.addCapability(new DERObjectIdentifier("1.2.840.113549.1.7.1"));
	    	caps.addCapability(PKCSObjectIdentifiers.x509Certificate);
	    	signedAttrs.add(new SMIMECapabilitiesAttribute(caps));  
	    	
	    	List  certList = new ArrayList();
	    	CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
	    	for (X509Certificate signer : signingCertificates)
	    	{
	    		if (signer instanceof X509CertificateEx)
	    		{	    			
	    			generator.addSigner(((X509CertificateEx)signer).getPrivateKey(), signer,
	    					toDigestAlgorithmOid(this.m_digestAlgorithm), new AttributeTable(signedAttrs), null);
	    			certList.add(signer);
	    		}
	    	}    	  	    		    	
	    	
	    	CertStore certsAndcrls = CertStore.getInstance("Collection", new CollectionCertStoreParameters(certList), CryptoExtensions.getJCEProviderName());   
	    	generator.addCertificatesAndCRLs(certsAndcrls);
	    	CMSProcessableBodyPart content = new CMSProcessableBodyPart(signedContent);
	    	
	    	CMSSignedData signedData = generator.generate(content, false, CryptoExtensions.getJCEProviderName());
	    	  	    	
	        String  header = "signed; protocol=\"application/pkcs7-signature\"; micalg=" + toDigestAlgorithmMicalg(this.m_digestAlgorithm);           
	        
	        String encodedSig = Base64.encodeBase64String(signedData.getEncoded());
	        
	        retVal = new MimeMultipart(header.toString());
	        
	        MimeBodyPart sig = new MimeBodyPart(new InternetHeaders(), encodedSig.getBytes("ASCII"));
            sig.addHeader("Content-Type", "application/pkcs7-signature; name=smime.p7s; smime-type=signed-data");
            sig.addHeader("Content-Disposition", "attachment; filename=\"smime.p7s\"");
            sig.addHeader("Content-Description", "S/MIME Cryptographic Signature");
            sig.addHeader("Content-Transfer-Encoding", "base64");
	                    
            retVal.addBodyPart(signedContent);
            retVal.addBodyPart(sig);

    	}   
    	catch (MessagingException e)
    	{
    		throw new MimeException(MimeError.InvalidMimeEntity, e);  		
    	}    	
    	catch (IOException e)
    	{
    		throw new SignatureException(SignatureError.InvalidMultipartSigned, e);  		
    	}   
    	catch (Exception e)
    	{
    		throw new NHINDException(MimeError.Unexpected, e);   		
    	} 	
    	return retVal;  
  	
    }
    


    //-----------------------------------------------------
    //
    // Signature Validation
    //
    //-----------------------------------------------------
    /**
     * Validates that a signed entity has a valid message and signature.  The signer's certificate is validated to ensure authenticity of the message.  Message
     * tampering is also checked with the message's digest and the signed digest in the message signature.
     * @param signedEntity The entity containing the original signed part and the message signature.
     * @param signerCertificate The certificate used to sign the message.
     * @param anchors A collection of certificate anchors used to determine if the certificates used in the signature can be validated as trusted certificates.
     */
    public void checkSignature(SignedEntity signedEntity, X509Certificate signerCertificate, Collection<X509Certificate> anchors) throws SignatureValidationException
    {
    	CMSSignedData signatureEnvelope = deserializeSignatureEnvelope(signedEntity);
    	    	
    	try
    	{
	    	for (SignerInformation sigInfo : (Collection<SignerInformation>)signatureEnvelope.getSignerInfos().getSigners())
	    	{	    		
	    		sigInfo.verify(signerCertificate, CryptoExtensions.getJCEProviderName());
	    	}
    	}
    	catch (Throwable e)
    	{
    		throw new SignatureValidationException("Signature validation failure.");
    	}
    }                        
 	
    /**
     * Extracts the ASN1 encoded signature data from the signed entity.
     * @param entity The entity containing the original signed part and the message signature.
     * @return A CMSSignedData object that contains the ASN1 encoded signature data of the message.
     */
    public CMSSignedData deserializeSignatureEnvelope(SignedEntity entity)
    {

    	
    	if (entity == null)
        {
            throw new NHINDException();
        }

    	CMSSignedData signed = null;
    	
    	try
    	{
    		//signed = new SMIMESigned(entity.getMimeMultipart());
    		byte[] messageBytes = EntitySerializer.Default.serializeToBytes(entity.getContent());
            MimeBodyPart signedContent = null;
            
           	signedContent = new MimeBodyPart(new ByteArrayInputStream(messageBytes));

            //signed = new CMSSignedData(new CMSProcessableBodyPartInbound(signedContent), entity.getMimeMultipart().getBodyPart(1).getInputStream());                        
           	signed = new CMSSignedData(new CMSProcessableBodyPart(signedContent), entity.getMimeMultipart().getBodyPart(1).getInputStream());
           	
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
        	throw new MimeException(MimeError.Unexpected, e);
    	}
    	
    	return signed;
    }

    
    public CMSSignedData deserializeEnvelopedSignature(MimeEntity envelopeEntity)
    {
        if (envelopeEntity == null)
        {
            throw new SignatureException(SignatureError.NullEntity);
        }

        if (!SMIMEStandard.isSignedEnvelope(envelopeEntity))
        {
            throw new SignatureException(SignatureError.NotSignatureEnvelope);
        }

        byte[] envelopeBytes = EntitySerializer.Default.serializeToBytes(envelopeEntity);

        return this.deserializeEnvelopedSignature(envelopeBytes);
    }

    public CMSSignedData deserializeEnvelopedSignature(byte[] messageBytes)
    {
    	CMSSignedData signed = null;
    	
    	try
    	{                     
           	signed = new CMSSignedData(messageBytes);           	
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
        	throw new MimeException(MimeError.Unexpected, e);
    	}
    	
    	return signed;
    }    
    

    private String toDigestAlgorithmOid(DigestAlgorithm type)
    {
        switch (type)
        {
            default:
                throw new IllegalArgumentException();

            case SHA1:
                return CMSSignedDataGenerator.DIGEST_SHA1;
                
            case SHA256:
            	return CMSSignedDataGenerator.DIGEST_SHA256;
            	
            case SHA384:
            	return CMSSignedDataGenerator.DIGEST_SHA384;   
            	
            case SHA512:
            	return CMSSignedDataGenerator.DIGEST_SHA512;   
        }
    }

    private String toDigestAlgorithmMicalg(DigestAlgorithm type)
    {
        switch (type)
        {
            default:
                throw new IllegalArgumentException();

            case SHA1:
                return "sha1";
                
            case SHA256:
            	return "sha256";
            	
            case SHA384:
            	return "sha384"; 
            	
            case SHA512:
            	return "sha512";           	
        }
    }    
    
    private String toEncyAlgorithmOid(EncryptionAlgorithm type)
    {
        switch (type)
        {
            default:
                throw new IllegalArgumentException();

            case RSA_3DES:
                return SMIMEEnvelopedGenerator.DES_EDE3_CBC;
                
            case AES128:
            	return SMIMEEnvelopedGenerator.AES128_CBC;
            	
            case AES192: 
            	return SMIMEEnvelopedGenerator.AES192_CBC;
            	
            case AES256: 
            	return SMIMEEnvelopedGenerator.AES256_CBC;            	
        }
    }
    
    
    private void writePreEncypt(byte message[])
    {
    	String path = System.getProperty("user.dir") + "/tmp";
    	File tmpDir = new File(path);
    	
    	if (!tmpDir.exists())
    	{
    		if (!tmpDir.mkdir())
    			return;
    	}
    	
    	System.currentTimeMillis();
    	
    	File outFile = new File(path + "/preEncypt_" + System.currentTimeMillis() + ".eml");
    	

    	try
    	{
        	if (!outFile.exists())
        	{
        		if (!outFile.createNewFile())
        			return;
        	}
        	BufferedOutputStream oStream = new BufferedOutputStream(new FileOutputStream(outFile));
    		
    		oStream.write(message, 0, message.length);
    		oStream.flush();
    		oStream.close();    		
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    private void writePostEncypt(byte message[])
    {
    	String path = System.getProperty("user.dir") + "/tmp";
    	File tmpDir = new File(path);
    	
    	if (!tmpDir.exists())
    	{
    		if (!tmpDir.mkdir())
    			return;
    	}
    	
    	System.currentTimeMillis();
    	
    	File outFile = new File(path + "/postEncypt_" + System.currentTimeMillis() + ".eml");
    	

    	try
    	{
        	if (!outFile.exists())
        	{
        		if (!outFile.createNewFile())
        			return;
        	}
        	BufferedOutputStream oStream = new BufferedOutputStream(new FileOutputStream(outFile));
    		
    		oStream.write(message, 0, message.length);
    		oStream.flush();
    		oStream.close();    		
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    
    private void writePreDecrypt(byte message[])
    {
    	String path = System.getProperty("user.dir") + "/tmp";
    	File tmpDir = new File(path);
    	
    	if (!tmpDir.exists())
    	{
    		if (!tmpDir.mkdir())
    			return;
    	}
    	
    	System.currentTimeMillis();
    	
    	File outFile = new File(path + "/preDecrypt_" + System.currentTimeMillis() + ".eml");
    	

    	try
    	{
        	if (!outFile.exists())
        	{
        		if (!outFile.createNewFile())
        			return;
        	}
        	BufferedOutputStream oStream = new BufferedOutputStream(new FileOutputStream(outFile));
    		
    		oStream.write(message, 0, message.length);
    		oStream.flush();
    		oStream.close();    		
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    private void writePostDecrypt(byte message[])
    {
    	String path = System.getProperty("user.dir") + "/tmp";
    	File tmpDir = new File(path);
    	
    	if (!tmpDir.exists())
    	{
    		if (!tmpDir.mkdir())
    			return;
    	}
    	
    	System.currentTimeMillis();
    	
    	File outFile = new File(path + "/postDecrypt_" + System.currentTimeMillis() + ".eml");
    	

    	try
    	{
        	if (!outFile.exists())
        	{
        		if (!outFile.createNewFile())
        			return;
        		
        	}
        	BufferedOutputStream oStream = new BufferedOutputStream(new FileOutputStream(outFile));
    		
    		oStream.write(message, 0, message.length);
    		oStream.flush();
    		oStream.close();
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    }
}
