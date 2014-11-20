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

import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.mail.internet.MimeMultipart;

import org.bouncycastle.cms.CMSSignedData;
import org.nhindirect.stagent.SignatureValidationException;
import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.mail.Message;
import org.nhindirect.stagent.mail.MimeEntity;

import com.google.inject.ImplementedBy;

/**
 * Executes the cryptography operations.  This include encryption, decryption, and signature generation. 
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
@ImplementedBy(SMIMECryptographerImpl.class)
public interface Cryptographer {
	
	/**
     * 
     * Encrypts a mulit part MIME entity using the provided certificate.
     * @param entity The entity that will be encrypted.
     * @param encryptingCertificate The public certificates that will be used to encrypt the message.
     * @return A MimeEntity containing the encrypted part.
     */
    public MimeEntity encrypt(MimeMultipart entity, X509Certificate encryptingCertificate);
    
    /**
     * Encrypts a mulit part MIME entity using the provided certificates.
     * @param entity The entity that will be encrypted.
     * @param encryptingCertificates The public certificates that will be used to encrypt the message.
     * @return A MimeEntity containing the encrypted part.
     */
    public MimeEntity encrypt(MimeMultipart mmEntity, Collection<X509Certificate> encryptingCertificates);
    
    /**
     * Encrypts an entity using the provided certificate.
     * @param entity The entity that will be encrypted.
     * @param encryptingCertificate The public certificates that will be used to encrypt the message.
     * @return A MimeEntity containing the encrypted part.
     */
    public MimeEntity encrypt(MimeEntity entity, X509Certificate encryptingCertificate);
    
    /** 
     * Encrypts an entity using the provided certificates.
     * @param entity The entity that will be encrypted.
     * @param encryptingCertificate The public certificates that will be used to encrypt the message.
     * @return A MimeEntity containing the encrypted part.
     */
    public MimeEntity encrypt(MimeEntity entity,  Collection<X509Certificate> encryptingCertificates);
    
    /**
     * Decrypts a message with the provided certificates private key.
     * @param message The message that will be decrypted.
     * @param decryptingCertificate The certificate whose private key will be used to decrypt the message.
     * @return A MimeEntity containing the decrypted part.
     */    
    public MimeEntity decrypt(Message message, X509CertificateEx decryptingCertificate);
    
    /**
     * Decrypts an entity with the provided certificate's private key.
     * @param encryptedEntity The entity that will be decrypted.
     * @param decryptingCertificate The certificate whose private key will be used to decrypt the message.
     * @return A MimeEntity containing the decrypted part.
     */  
    public MimeEntity decrypt(MimeEntity encryptedEntity, X509CertificateEx decryptingCertificate);
    
    /**
     * Decrypts an entity with the provided certificates' private key.
     * @param encryptedEntity The entity that will be decrypted.
     * @param decryptingCertificate The certificates whose private keys will be used to decrypt the message.
     * @return A MimeEntity containing the decrypted part.
     */  
    public MimeEntity decrypt(MimeEntity encryptedEntity, Collection<X509CertificateEx> decryptingCertificates);
    
    /**
     * Signs a message with the provided certificate.
     * @param message The message that will be signed.
     * @param signingCertificate The certificate used to sign the message.
     * @return A signed entity that consists of a multipart/signed entity containing the original entity and a message signature. 
     */    
    public SignedEntity sign(Message message, X509Certificate signingCertificate);
    
    public SignedEntity sign(Message message, Collection<X509Certificate> signingCertificates);
    
    /**
     * Signs an entity with the provided certificate.
     * @param message The entity that will be signed.
     * @param signingCertificate The certificate used to sign the message.
     * @return A signed entity that consists of a multipart/signed entity containing the original entity and a message signature. 
     */  
    public SignedEntity sign(MimeEntity entity, X509Certificate signingCertificate);
    
    /**
     * Signs an entity with the provided certificates.
     * @param message The entity that will be signed.
     * @param signingCertificates The certificates used to sign the message.
     * @return A signed entity that consists of a multipart/signed entity containing the original entity and a message signature. 
     */ 
    public SignedEntity sign(MimeEntity entity, Collection<X509Certificate> signingCertificates);
    
    /**
     * Validates that a signed entity has a valid message and signature.  The signer's certificate is validated to ensure authenticity of the message.  Message
     * tampering is also checked with the message's digest and the signed digest in the message signature.
     * @param signedEntity The entity containing the original signed part and the message signature.
     * @param signerCertificate The certificate used to sign the message.
     * @param anchors A collection of certificate anchors used to determine if the certificates used in the signature can be validated as trusted certificates.
     */
    public void checkSignature(SignedEntity signedEntity, X509Certificate signerCertificate, Collection<X509Certificate> anchors) throws SignatureValidationException;
    
    /**
     * Extracts the ASN1 encoded signature data from the signed entity.
     * @param entity The entity containing the original signed part and the message signature.
     * @return A CMSSignedData object that contains the ASN1 encoded signature data of the message.
     */
    public CMSSignedData deserializeSignatureEnvelope(SignedEntity entity);
    
    public CMSSignedData deserializeEnvelopedSignature(MimeEntity envelopeEntity);
    
    public CMSSignedData deserializeEnvelopedSignature(byte[] messageBytes);

}
