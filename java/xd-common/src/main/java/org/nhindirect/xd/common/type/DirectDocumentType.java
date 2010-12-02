package org.nhindirect.xd.common.type;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.nhindirect.xd.transform.util.type.MimeType;

/**
 * Enumeration of document types. Order of enumerations is important, and should
 * go from most specific to least specific.
 * 
 * @author beau
 */
public enum DirectDocumentType
{
    CCD(FormatCodeEnum.CDAR2, MimeType.TEXT_XML)
    {
        /*
         * (non-Javadoc)
         * 
         * @see org.nhindirect.xd.common.type.DirectDocumentType#matches(java.lang.String, java.lang.String, java.lang.String)
         */
        @Override
        public boolean matches(String data, String contentType, String fileName)
        {
            // FIXME: This is quick proof-of-concept.
            return StringUtils.contains(data, "POCD_HD000040");
        }
    },
    XDM(null, null)
    {
        /*
         * (non-Javadoc)
         * 
         * @see org.nhindirect.xd.common.type.DirectDocumentType#matches(java.lang.String, java.lang.String, java.lang.String)
         */
        @Override
        public boolean matches(String data, String contentType, String fileName)
        {
            // FIXME: Bad assumption
            return StringUtils.contains(fileName, ".zip");
        }  
    },
    PDF(FormatCodeEnum.TEXT, MimeType.APPLICATION_PDF),
    XML(FormatCodeEnum.TEXT, MimeType.TEXT_XML)
    {
        /*
         * (non-Javadoc)
         * 
         * @see org.nhindirect.xd.common.type.DirectDocumentType#matches(java.lang.String, java.lang.String, java.lang.String)
         */
        @Override
        public boolean matches(String data, String contentType, String fileName)
        {
            if (MimeType.TEXT_XML.matches(contentType))
                return true;

            return false;
        }
    },
    TEXT(FormatCodeEnum.TEXT, MimeType.TEXT_PLAIN)
    {
        /*
         * (non-Javadoc)
         * 
         * @see org.nhindirect.xd.common.type.DirectDocumentType#matches(java.lang.String, java.lang.String, java.lang.String)
         */
        @Override
        public boolean matches(String data, String contentType, String fileName)
        {
            if (MimeType.TEXT_PLAIN.matches(contentType))
                return true;

            return false;
        }
    },
    UNKNOWN(FormatCodeEnum.TEXT, MimeType.TEXT_PLAIN)
    {
        /*
         * (non-Javadoc)
         * 
         * @see org.nhindirect.xd.common.type.DirectDocumentType#matches(java.lang.String, java.lang.String, java.lang.String)
         */
        @Override
        public boolean matches(String data, String contentType, String fileName)
        {
            return true;
        }
    };

    private FormatCodeEnum formatCode;
    private MimeType mimeType;
    
    private DirectDocumentType(FormatCodeEnum formatCode, MimeType mimeType)
    {
        this.formatCode = formatCode;
        this.mimeType = mimeType;
    }

    /**
     * @param m
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public boolean matches(MimeMessage m) throws MessagingException, IOException
    {
        return matches((String) m.getContent(), m.getContentType(), m.getFileName());
    }

    /**
     * @param b
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public boolean matches(BodyPart b) throws MessagingException, IOException
    {
        String s = read(b);

        return matches(s, b.getContentType(), b.getFileName());
    }

    /**
     * @param data
     * @param contentType
     * @param fileName
     * @return
     */
    public boolean matches(String data, String contentType, String fileName)
    {
        return false;
    }

    /**
     * @param mimeMessage
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public static DirectDocumentType lookup(MimeMessage mimeMessage) throws MessagingException, IOException
    {
        for (DirectDocumentType d : values())
        {
            if (d.matches(mimeMessage))
                return d;
        }

        return UNKNOWN;
    }

    /**
     * @param bodyPart
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public static DirectDocumentType lookup(BodyPart bodyPart) throws MessagingException, IOException
    {
        for (DirectDocumentType d : values())
        {
            if (d.matches(bodyPart))
                return d;
        }

        return UNKNOWN;
    }

    /**
     * Return the value of formatCode.
     * 
     * @return the formatCode.
     */
    public FormatCodeEnum getFormatCode()
    {
        return formatCode;
    }

    /**
     * Return the value of mimeType.
     * 
     * @return the mimeType.
     */
    public MimeType getMimeType()
    {
        return mimeType;
    }
    
    private static String read(BodyPart bodyPart) throws MessagingException, IOException
    {
        InputStream inputStream = bodyPart.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        int data = 0;
        byte[] buffer = new byte[1024];
        while ((data = inputStream.read(buffer)) != -1)
        {
            outputStream.write(buffer, 0, data);
        }

        inputStream.close();

        return new String(outputStream.toByteArray());
    }
}
