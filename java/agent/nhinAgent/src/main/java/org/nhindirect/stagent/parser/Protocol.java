package org.nhindirect.stagent.parser;

import java.util.Locale;

import org.nhindirect.stagent.DigestAlgorithm;

/**
 * Message and MIME constants
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class Protocol 
{
    public static final char CR = '\r';
    public static final char LF = '\n';
    public static final String CRLF = "\r\n";
    public static final char Escape = '\\';
    public static final char NameValueSeparator = ':';
    public static final char BoundaryChar = '-';
    public static final String BoundarySeparator = "--";
    //
    // Common Headers
    //
    public static final String ToHeader = "To";
    public static final String FromHeader = "From";
    public static final String CCHeader = "CC";
    public static final String MessageIDHeader = "Message-ID";
    public static final String SubjectHeader = "Subject";
    public static final String DateHeader = "Date";
    
    public static final String MimeHeaderPrefix = "Content-";
    public static final String MimeVersionHeader = "MIME-Version";
    public static final String ContentTypeHeader = "Content-Type";
    public static final String ContentIDHeader = "Content-ID";
    public static final String ContentDispositionHeader = "Content-Disposition";
    public static final String ContentDescriptionHeader = "Content-Description";
    public static final String ContentTransferEncodingHeader = "Content-Transfer-Encoding";

    public static final String TransferEncodingBase64 = "base64";
    public static final String TransferEncoding7Bit = "7bit";
    public static final String TransferEncodingQuoted = "quoted-printable";
    //
    // MIME
    //
    public static final String MediaType_Multipart = "multipart";
    public static final String MultiPartType_Mixed = "multipart/mixed;";
    public static final String MultiPartType_Signed = "multipart/signed; protocol=\"application/x-pkcs7-signature\";";
    //
    // Cryptography
    //
    public static final String EncryptedContentTypeHeaderValue = "application/pkcs7-mime; smime-type=enveloped-data; name=\"smime.p7m\"";
    public static final String EncryptedContentMediaType = "application/pkcs7-mime";
    public static final String EncryptedContentMediaTypeAlternative = "application/x-pkcs7-mime";   // we are forgiving when we receive messages
    public static final String SignatureContentTypeHeaderValue = "application/pkcs7-signature; name=\"smime.p7s\"";
    public static final String SignatureContentMediaType = "application/pkcs7-signature";
    public static final String SignatureContentMediaTypeAlternative = "application/x-pkcs7-signature"; // we are forgiving when we receive messages
    public static final String SignatureDisposition = "attachment; filename=\"smime.p7s\"";
    
    public static final String SmimeTypeParameterKey = "smime-type";
    public static final String EnvelopedDataSmimeType = "enveloped-data";
    public static final String DefaultFileName = "smime.p7m";
    
    //
    // MAIL
    //
    public static final String MailAddressSeparator = ",";
    //
    // Other..
    //    
    
    public static boolean isWhitespace(char ch)
    {
        //
        // CR/LF are reserved characters
        //
        return (ch == ' ' || ch == '\t');
    }
    
    public static String combine(String name, String value)
    {
        if (name == null || name.length() == 0)
        {
            throw new IllegalArgumentException("name");
        }
        if (value == null || value.length() == 0)
        {
            throw new IllegalArgumentException("value");
        }
        
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append(Protocol.NameValueSeparator).append(" ");
        builder.append(value);
        return builder.toString();
    }
    
    //
    // RFC: 
    //  - All string comparisions are case-insensitive        
    //  - locale independant - i.e. ordinal
    //
    public static boolean equals(String x, String y)
    {
        return String.CASE_INSENSITIVE_ORDER.compare(x, y) == 0;
    }
    
    public static boolean startsWith(String x, String y)
    {
    	if (y.length() > x.length())
    		return false;
    	
    	return String.CASE_INSENSITIVE_ORDER.compare(x.substring(0, y.length()), y) == 0;
    }

    public static boolean contains(String x, String y)
    {
        return x.toLowerCase(Locale.getDefault()).indexOf(y.toLowerCase(Locale.getDefault())) >= 0;
    }    
    
    public static String toString(DigestAlgorithm algorithm)
    {
        switch(algorithm)
        {
            default:
                throw new IllegalArgumentException();                
            
            case SHA1:
                return "SHA1";
        }
    }



}
