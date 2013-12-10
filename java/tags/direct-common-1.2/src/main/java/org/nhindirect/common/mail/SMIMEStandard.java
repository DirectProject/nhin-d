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

package org.nhindirect.common.mail;

/**
 * Standard SMIM headers and utility methods
 * @author Greg Meyer
 * @author Umesh Madan
 * @since 1.1
 */
public class SMIMEStandard 
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
}
