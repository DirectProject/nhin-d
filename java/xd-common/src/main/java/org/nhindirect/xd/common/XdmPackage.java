package org.nhindirect.xd.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XdmPackage
{
    private String messageId;
    private DirectDocuments documents;

    // TODO: Need to figure out how to get suffix from a document
    private static final String SUFFIX = ".xml";

    private static final int BUFFER = 2048;
    private static final String XDM_SUB_FOLDER = "SUBSET01/";
    private static final Log LOGGER = LogFactory.getFactory().getInstance(XdmPackage.class);

    public XdmPackage(String messageId)
    {
        this.messageId = messageId;
        this.documents = new DirectDocuments();
    }
    
    public void setDocuments(DirectDocuments documents)
    {
        this.documents = documents;
    }

    public File toFile()
    {
        File xdmFile = null;

        try
        {
            xdmFile = new File(messageId + "-xdm.zip");

            FileOutputStream dest = new FileOutputStream(xdmFile);

            ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(dest));
            zipOutputStream.setMethod(ZipOutputStream.DEFLATED);

            for (DirectDocument2 document : documents.getDocuments())
            {
                addEntry(zipOutputStream, document.getData(), XDM_SUB_FOLDER + document.getMetadata().getId() + SUFFIX);
            }

            addEntry(zipOutputStream, documents.getSubmitObjectsRequestAsString(), XDM_SUB_FOLDER + "METADATA.xml");

            addEntry(zipOutputStream, getIndex(), "INDEX.htm");

            addEntry(zipOutputStream, getReadme(), "README.txt");

            if (SUFFIX.equals("xml"))
            {
                addEntry(zipOutputStream, getXsl(), XDM_SUB_FOLDER + "CCD.xsl");
            }

            zipOutputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return xdmFile;
    }

    private void addEntry(ZipOutputStream zipOutputStream, String data, String fileName) throws IOException
    {
        InputStream inputStream = new ByteArrayInputStream(data.getBytes());
        BufferedInputStream outputStream = new BufferedInputStream(inputStream);

        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOutputStream.putNextEntry(zipEntry);

        int count = 0;
        byte dataOut[] = new byte[BUFFER];
        while ((count = outputStream.read(dataOut, 0, BUFFER)) != -1)
        {
            zipOutputStream.write(dataOut, 0, count);
        }
    }

    /*
     * Get the index file.
     */
    public String getIndex() throws Exception
    {
        String data;
        byte[] bytes;

        try
        {
            bytes = readFile("INDEX_head.txt");
            
            data = new String(bytes);

            for (DirectDocument2 document : documents.getDocuments())
            {
                String file = XDM_SUB_FOLDER + document.getMetadata().getId() + SUFFIX;
                data += "<li><a href=\"" + file + "\">" + file + "</a> - " + document.getMetadata().getDescription() + "</li>";
            }
            
            bytes = readFile("INDEX_tail.txt");
            
            data += new String(bytes);
        }
        catch (Exception e)
        {
            LOGGER.error("Unable to access index file.", e);
            throw e;
        }

        return data;
    }

    /*
     * Get the readme file.
     */
    public String getReadme() throws Exception
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

        return new String(bytes);
    }

    /*
     * Get the xsl file.
     */
    public String getXsl() throws Exception
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

        return new String(bytes);

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
