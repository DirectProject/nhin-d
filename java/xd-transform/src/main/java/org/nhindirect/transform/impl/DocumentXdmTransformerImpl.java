package org.nhindirect.transform.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.transform.DocumentXdmTransformer;

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

    private byte[] readFile(String filename) throws Exception
    {
        byte[] bytes;

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(filename);
        bytes = new byte[is.available()];
        is.read(bytes);

        return bytes;
    }

}
