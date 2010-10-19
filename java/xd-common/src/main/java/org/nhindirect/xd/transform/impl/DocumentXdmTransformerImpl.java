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

package org.nhindirect.xd.transform.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.xd.common.DirectDocument;
import org.nhindirect.xd.transform.DocumentXdmTransformer;

/**
 * Class for handling the transformation of a Document to an XDM zip file.
 * 
 * @author Vince
 */
public class DocumentXdmTransformerImpl implements DocumentXdmTransformer
{
    private static final int BUFFER = 2048;

    private static final Log LOGGER = LogFactory.getFactory().getInstance(DocumentXdmTransformerImpl.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nhindirect.transform.DocumentXdmTransformer#transform(java.util.List,
     * java.lang.String, byte[])
     */
    @Override
    public File transform(Collection<String> docs, String suffix, byte[] meta)
    {
        String messageId = UUID.randomUUID().toString();
        return transform(docs, suffix, meta, messageId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nhindirect.transform.DocumentXdmTransformer#transform(java.util.List,
     * java.lang.String, byte[], java.lang.String)
     */
    @Override
    public File transform(Collection<String> docs, String suffix, byte[] meta, String messageId)
    {
        File temp = null;

        if (StringUtils.isBlank(messageId))
        {
            messageId = UUID.randomUUID().toString();

            if (LOGGER.isTraceEnabled())
                LOGGER.trace("Message ID not provided, using random ID (" + messageId + ")");
        }

        try
        {
            BufferedInputStream origin = null;
            temp = new File(messageId + "-xdm.zip");
            FileOutputStream dest = new FileOutputStream(temp);

            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            out.setMethod(ZipOutputStream.DEFLATED);

            for (String doc : docs)
            {
                byte[] attachment = doc.getBytes();

                byte data[] = new byte[BUFFER];

                byte[] bytevals = attachment;
                InputStream byteis = new ByteArrayInputStream(bytevals);
                origin = new BufferedInputStream(byteis);

                ZipEntry entry = new ZipEntry("SUBSET01\\DOCUMENT." + suffix);
                out.putNextEntry(entry);
                int count = 0;
                while ((count = origin.read(data, 0, BUFFER)) != -1)
                {
                    out.write(data, 0, count);
                }

            }
            byte[] bytevals = meta;
            InputStream byteis = new ByteArrayInputStream(bytevals);
            origin = new BufferedInputStream(byteis);

            ZipEntry entry = new ZipEntry("SUBSET01\\METADATA.xml");
            out.putNextEntry(entry);
            int count = 0;
            byte data[] = new byte[BUFFER];
            while ((count = origin.read(data, 0, BUFFER)) != -1)
            {
                out.write(data, 0, count);
            }

            bytevals = getIndex(suffix);
            byteis = new ByteArrayInputStream(bytevals);
            origin = new BufferedInputStream(byteis);

            entry = new ZipEntry("INDEX.htm");
            out.putNextEntry(entry);
            count = 0;
            while ((count = origin.read(data, 0, BUFFER)) != -1)
            {
                out.write(data, 0, count);
            }

            bytevals = getReadme();
            byteis = new ByteArrayInputStream(bytevals);
            origin = new BufferedInputStream(byteis);

            entry = new ZipEntry("README.txt");
            out.putNextEntry(entry);
            count = 0;
            while ((count = origin.read(data, 0, BUFFER)) != -1)
            {
                out.write(data, 0, count);
            }

            if (suffix.equals("xml"))
            {
                bytevals = getXsl();
                byteis = new ByteArrayInputStream(bytevals);
                origin = new BufferedInputStream(byteis);

                entry = new ZipEntry("SUBSET01\\CCD.xsl");
                out.putNextEntry(entry);
                count = 0;
                while ((count = origin.read(data, 0, BUFFER)) != -1)
                {
                    out.write(data, 0, count);
                }
            }

            origin.close();

            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return temp;

    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.xd.transform.DocumentXdmTransformer#transform(org.nhindirect.xd.common.DirectDocument, java.lang.String)
     */
    @Override
    public File transform(DirectDocument document, String suffix, String messageId)
    {
        return transform(Arrays.asList(document.getData()), suffix, document.getMetadata().toString().getBytes(), messageId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.xd.transform.DocumentXdmTransformer#transform(org.nhindirect.xd.common.DirectDocument)
     */
    @Override
    public File transform(DirectDocument document)
    {
        throw new UnsupportedOperationException("Not implemented yet");
    }


    /*
     * Get the index file.
     */
    private byte[] getIndex(String type) throws Exception
    {
        String data;
        byte[] bytes;

        try
        {
            bytes = readFile("INDEX.htm");
        }
        catch (Exception e)
        {
            LOGGER.error("Unable to access index file.", e);
            throw e;
        }

        data = new String(bytes);
        data = data.replace("XXX", type);

        return data.getBytes();
    }

    /*
     * Get the readme file.
     */
    private byte[] getReadme() throws Exception
    {
        byte[] bytes;

        try
        {
            bytes = readFile("README.txt");
        }
        catch (Exception e)
        {
            LOGGER.error("Unable to access readme file.", e);
            throw e;
        }

        return bytes;
    }

    /*
     * Get the xsl file.
     */
    private byte[] getXsl() throws Exception
    {
        byte[] bytes;

        try
        {
            bytes = readFile("CCD.xsl");
        }
        catch (Exception e)
        {
            LOGGER.error("Unable to access xsl file.", e);
            throw e;
        }

        return bytes;

    }

    /*
     * Read a file and return the bytes.
     */
    private byte[] readFile(String filename) throws Exception
    {
        byte[] bytes;

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(filename);
        bytes = new byte[is.available()];
        is.read(bytes);

        return bytes;
    }
}
