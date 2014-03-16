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

import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMessage;

import org.nhindirect.stagent.mail.MailStandard;
import org.nhindirect.stagent.mail.MimeEntity;
import org.nhindirect.stagent.mail.MimeStandard;

/**
 * Standard SMIM headers and utility methods
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class SMIMEStandard extends MailStandard
{
    //
    // MIME Types
    //
    public static final String MediaType_Multipart = "multipart";
    public static final String MultiPartType_Mixed = "multipart/mixed;";
    public static final String MultiPartType_Signed = "multipart/signed; protocol=\"application/x-pkcs7-signature\";";
    public static final String MICAlgorithmKey = "micalg"; // Message Integrity Check Protocol   
    
    //
    // Cryptography
    //
    public static final String CmsEnvelopeMediaType = "application/pkcs7-mime";
    public static final String CmsEnvelopeMediaTypeAlt = "application/x-pkcs7-mime";   // we are forgiving when we receive messages    
    
    public static final String EncryptedContentTypeHeaderValue = "application/pkcs7-mime; smime-type=enveloped-data; name=\"smime.p7m\"";
    public static final String EncryptedContentMediaType = "application/pkcs7-mime";
    public static final String EncryptedContentMediaTypeAlternative = "application/x-pkcs7-mime";   // we are forgiving when we receive messages
    public static final String SignatureContentTypeHeaderValue = "application/pkcs7-signature; name=\"smime.p7s\"";
    public static final String SignatureContentMediaType = "application/pkcs7-signature";
    public static final String SignatureContentMediaTypeAlternative = "application/x-pkcs7-signature"; // we are forgiving when we receive messages
    public static final String SignatureDisposition = "attachment; filename=\"smime.p7s\"";
    
    public static final String SmimeTypeParameterKey = "smime-type";
    public static final String EnvelopedDataSmimeType = "enveloped-data";
    public static final String  SignedDataSmimeType = "signed-data";
    public static final String DefaultFileName = "smime.p7m";    
    
    public static boolean isContentCms(ContentType contentType)
    {
        if (contentType == null)
            throw new IllegalArgumentException();

        return (contentType.match(SMIMEStandard.CmsEnvelopeMediaType) 
                || contentType.match(SMIMEStandard.CmsEnvelopeMediaTypeAlt));
    } 
    
    public static boolean isContentEncrypted(ContentType contentType)
    {
        if (contentType == null)
            throw new IllegalArgumentException();
       
        
        return (SMIMEStandard.isContentCms(contentType)
                &&  contentType.getParameter(SMIMEStandard.SmimeTypeParameterKey) != null && 
                contentType.getParameter(SMIMEStandard.SmimeTypeParameterKey).equals(SMIMEStandard.EnvelopedDataSmimeType));
    }   
    
    public static boolean isContentEnvelopedSignature(ContentType contentType)
    {
        if (contentType == null)
            throw new IllegalArgumentException();

        return (SMIMEStandard.isContentCms(contentType)
                &&  contentType.getParameter(SMIMEStandard.SmimeTypeParameterKey) != null && 
                contentType.getParameter(SMIMEStandard.SmimeTypeParameterKey).equals(SMIMEStandard.SignedDataSmimeType));
    } 
    
    public static boolean isContentMultipartSignature(ContentType contentType)
    {
        if (contentType == null)
            throw new IllegalArgumentException();

        return (contentType.match(SMIMEStandard.MultiPartType_Signed));
    }
    
    public static boolean isContentDetachedSignature(ContentType contentType)
    {
        if (contentType == null)
            throw new IllegalArgumentException();    	
    	
        return (contentType.match(SMIMEStandard.SignatureContentMediaType) 
                ||  contentType.match(SMIMEStandard.SignatureContentMediaTypeAlternative));
    }   
    
    public static boolean isEncrypted(MimeMessage entity)
    {
        return (SMIMEStandard.isContentEncrypted(getContentType(entity)) && SMIMEStandard.verifyEncoding(entity));
    }    
    
    public static boolean isSignedEnvelope(MimeMessage entity)
    {
        return (SMIMEStandard.isContentEnvelopedSignature(getContentType(entity)) && SMIMEStandard.verifyEncoding(entity));
    }    
    
    public static boolean isSignedEnvelope(MimeEntity entity)
    {
        return (SMIMEStandard.isContentEnvelopedSignature(getContentType(entity)) && SMIMEStandard.verifyEncoding(entity));
    }     
    
    public static boolean isDetachedSignature(MimeMessage entity)
    {
        return (SMIMEStandard.isContentDetachedSignature(getContentType(entity)) && SMIMEStandard.verifyEncoding(entity));
    } 
    
    private static ContentType getContentType(MimeEntity entity)
    {
    	try
    	{
    		return new ContentType(entity.getContentType());
    	}
    	catch (MessagingException e) {/* no-op */}
    	
    	return null;
    }    
    
    private static ContentType getContentType(MimeMessage entity)
    {
    	try
    	{
    		return new ContentType(entity.getContentType());
    	}
    	catch (MessagingException e) {/* no-op */}
    	
    	return null;
    }
   
    static boolean verifyEncoding(MimeEntity entity)
    {
    	try
    	{
    		String header = entity.getHeader(MimeStandard.ContentTransferEncodingHeader, null);
        
    		return (header != null && header.matches(MimeStandard.TransferEncodingBase64));
    	}
    	catch (MessagingException e) {/* no-op */}
    	
    	return false;
    }       
    
    static boolean verifyEncoding(MimeMessage entity)
    {
    	try
    	{
    		String header = entity.getHeader(MimeStandard.ContentTransferEncodingHeader, null);
        
    		return (header != null && header.matches(MimeStandard.TransferEncodingBase64));
    	}
    	catch (MessagingException e) {/* no-op */}
    	
    	return false;
    }     
    
    public static String toString(DigestAlgorithm algorithm)
    {
        
    	
    	switch(algorithm)
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
}
