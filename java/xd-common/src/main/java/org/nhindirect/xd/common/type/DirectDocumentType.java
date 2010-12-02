/* 
 * Copyright (c) 2010, NHIN Direct Project
 * All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in the 
 *    documentation and/or other materials provided with the distribution.  
 * 3. Neither the name of the the NHIN Direct Project (nhindirect.org)
 *    nor the names of its contributors may be used to endorse or promote products 
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY 
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
    XML(FormatCodeEnum.TEXT, MimeType.TEXT_XML),
    TEXT(FormatCodeEnum.TEXT, MimeType.TEXT_PLAIN),
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
     * Check to see if the MimeMessage matches the current DirectDocumentType.
     * 
     * @param mimeMessage
     *            The MimeMessage object to compare against the current
     *            DirectDocumentType.
     * @return true if the current DirectDocumentType matches, false otherwise.
     * @throws MessagingException
     * @throws IOException
     */
    public boolean matches(MimeMessage mimeMessage) throws MessagingException, IOException
    {
        return matches((String) mimeMessage.getContent(), mimeMessage.getContentType(), mimeMessage.getFileName());
    }

    /**
     * Check to see if the BodyPart matches the current DirectDocumentType.
     * 
     * @param bodyPart
     *            The BodyPart object to compare against the current
     *            DirectDocumentType.
     * @return true if the current DirectDocumentType matches, false otherwise.
     * @throws MessagingException
     * @throws IOException
     */
    public boolean matches(BodyPart bodyPart) throws MessagingException, IOException
    {
        String s = read(bodyPart);

        return matches(s, bodyPart.getContentType(), bodyPart.getFileName());
    }

    /**
     * Check to see if the BodyPart matches the provided params. The default
     * implementation checks only the contentType param. More specific
     * enumerations should override this method with custom matching logic.
     * 
     * @param data
     *            The document contents.
     * @param contentType
     *            The document content type.
     * @param fileName
     *            The document file name.
     * @return true it the DirectDocumentType matches, false otherwise.
     */
    public boolean matches(String data, String contentType, String fileName)
    {
        if (this.mimeType.matches(contentType))
            return true;

        return false;
    }

    /**
     * Lookup and return the DirectDocumentType which most closely matches the
     * provided MimeMessage.
     * 
     * @param mimeMessage
     *            The MimeMessage to match up with a DirectDocumentType.
     * @return the most closely matching DirectDocumentType.
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
     * Lookup and return the DirectDocumentType which most closely matches the
     * provided BodyPart.
     * 
     * @param bodyPart
     *            The BodyPart to match up with a directDocumentType.
     * @return the most closely matching DirectDocumentType.
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
