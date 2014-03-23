package org.nhindirect.xd.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.activation.DataHandler;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.xd.transform.util.type.MimeType;

public class XdmPackage {

    private String messageId;
    private DirectDocuments documents;
    @Deprecated
    private static final String SUFFIX = ".xml";
    private static final int BUFFER = 2048;
    private static final String XDM_SUB_FOLDER = "SUBSET01/";
    private static final String XDM_METADATA_FILE = "METADATA.xml";
    private static final Log LOGGER = LogFactory.getFactory().getInstance(XdmPackage.class);

    public XdmPackage() {
        this(UUID.randomUUID().toString());
    }

    public XdmPackage(String messageId) {
        this.messageId = messageId;
        this.documents = new DirectDocuments();
    }

    public void setDocuments(DirectDocuments documents) {
        this.documents = documents;
    }

    public DirectDocuments getDocuments() {
        return this.documents;
    }

    public File toFile() {
        File xdmFile = null;

        try {
            xdmFile = new File(messageId + "-xdm.zip");

            FileOutputStream dest = new FileOutputStream(xdmFile);

            ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(dest));
            zipOutputStream.setMethod(ZipOutputStream.DEFLATED);

            for (DirectDocument2 document : documents.getDocuments()) {
                if (document.getData() != null) {
                    addEntry(zipOutputStream, document.getData(), XDM_SUB_FOLDER + document.getMetadata().getId() + getSuffix(document.getMetadata().getMimeType()));
                }
            }

            addEntry(zipOutputStream, documents.getSubmitObjectsRequestAsString().getBytes(), XDM_SUB_FOLDER + XDM_METADATA_FILE);

            addEntry(zipOutputStream, getIndex().getBytes(), "INDEX.htm");

            addEntry(zipOutputStream, getReadme().getBytes(), "README.txt");

            if (SUFFIX.equals(".xml")) {
                addEntry(zipOutputStream, getXsl().getBytes(), XDM_SUB_FOLDER + "CCD.xsl");
            }

            zipOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return xdmFile;
    }

    private void addEntry(ZipOutputStream zipOutputStream, byte[] data, String fileName) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(data);
        BufferedInputStream outputStream = new BufferedInputStream(inputStream);

        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOutputStream.putNextEntry(zipEntry);

        int count = 0;
        byte dataOut[] = new byte[BUFFER];
        while ((count = outputStream.read(dataOut, 0, BUFFER)) != -1) {
            zipOutputStream.write(dataOut, 0, count);
        }
    }

    /*
     * Get the index file.
     */
    public String getIndex() throws Exception {
        String data;
        byte[] bytes;

        try {
            bytes = readFile("INDEX_head.txt");

            data = new String(bytes);

            for (DirectDocument2 document : documents.getDocuments()) {
                if (document.getData() != null) {
                    String file = XDM_SUB_FOLDER + document.getMetadata().getId() + getSuffix(document.getMetadata().getMimeType());
                    data += "<li><a href=\"" + file + "\">" + file + "</a> - " + document.getMetadata().getDescription() + "</li>";
                }
            }

            bytes = readFile("INDEX_tail.txt");

            data += new String(bytes);
        } catch (Exception e) {
            LOGGER.error("Unable to access index file.", e);
            throw e;
        }

        return data;
    }

    /*
     * Get the readme file.
     */
    public String getReadme() throws Exception {
        byte[] bytes;

        try {
            bytes = readFile("README.txt");
        } catch (Exception e) {
            LOGGER.error("Unable to access readme file.", e);
            throw e;
        }

        return new String(bytes);
    }

    /*
     * Get the xsl file.
     */
    public String getXsl() throws Exception {
        byte[] bytes;

        try {
            bytes = readFile("CCD.xsl");
        } catch (Exception e) {
            LOGGER.error("Unable to access xsl file.", e);
            throw e;
        }

        return new String(bytes);

    }

    public static XdmPackage fromXdmZipDataHandler(DataHandler dataHandler) throws Exception {
        File file = null;

        try {
            // Create a temporary work file
            file = fileFromDataHandler(dataHandler);
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error creating temporary work file, unable to complete transformation.", e);
            }
            throw new Exception("Error creating temporary work file, unable to complete transformation.", e);
        }

        XdmPackage xdmPackage = fromXdmZipFile(file);

        boolean delete = file.delete();

        if (delete) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Deleted temporary work file " + file.getAbsolutePath());
            }
        } else {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Unable to delete temporary work file " + file.getAbsolutePath());
            }
        }

        return xdmPackage;
    }

    public static XdmPackage fromXdmZipFile(File file) throws Exception {
        DirectDocuments documents = new DirectDocuments();

        ZipFile zipFile = new ZipFile(file, ZipFile.OPEN_READ);

        Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
        ZipEntry zipEntry = null;

        // Load metadata
        while (zipEntries.hasMoreElements()) {


            zipEntry = zipEntries.nextElement();
            String zname = zipEntry.getName();
            LOGGER.info("Processing a ZipEntry named " + zname);
            if (!zipEntry.isDirectory()) {
                String subsetDirspec = getSubmissionSetDirspec(zipEntry.getName());

                // Read metadata
                if (matchName(zname, subsetDirspec, XDM_METADATA_FILE)) {
                    ByteArrayOutputStream byteArrayOutputStream = readData(zipFile, zipEntry);

                    documents.setValues(byteArrayOutputStream.toString());
                }
            }
        }

        zipEntries = zipFile.entries();

        // load data
        while (zipEntries.hasMoreElements()) {
            LOGGER.trace("Processing a ZipEntry");

            zipEntry = zipEntries.nextElement();
            String zname = zipEntry.getName();

            if (!zipEntry.isDirectory()) {
                String subsetDirspec = getSubmissionSetDirspec(zipEntry.getName());

                // Read data
                if (StringUtils.contains(subsetDirspec, StringUtils.remove(XDM_SUB_FOLDER, "/"))
                        && !StringUtils.contains(zname, ".xsl") && !StringUtils.contains(zname, XDM_METADATA_FILE)) {
                    ByteArrayOutputStream byteArrayOutputStream = readData(zipFile, zipEntry);

                    String digest = DirectDocument2.getSha1Hash(byteArrayOutputStream.toString());
                    System.out.println(digest);
                    DirectDocument2 document = documents.getDocumentByHash(digest);

                    if (document == null) {
                        LOGGER.warn("Unable to find metadata for document by hash. Creating document with no supporting metadata.");

                        document = new DirectDocument2();
                        documents.getDocuments().add(document);
                    }

                    document.setData(byteArrayOutputStream.toByteArray());
                }
            }
        }

        zipFile.close();

        XdmPackage xdmPackage = new XdmPackage();
        xdmPackage.setDocuments(documents);

        return xdmPackage;
    }

    /**
     * Given a full ZipEntry filespec, extracts the name of the folder (if
     * present) under the IHE_XDM root specified by IHE XDM.
     * 
     * @param zipEntryName
     *            The ZIP entry name.
     * @return the name of the folder.
     */
    private static String getSubmissionSetDirspec(String zipEntryName) {
        if (zipEntryName == null) {
            return null;
        }

        String[] components = StringUtils.split(zipEntryName, "\\/");
        return components[0];
    }

    /**
     * Determine whether a filename matches the subset directory and file name.
     * 
     * @param zname
     *            The name to compare.
     * @param subsetDirspec
     *            The subset directory name.
     * @param subsetFilespec
     *            The subset file name.
     * @return true if the names match, false otherwise.
     */
    private static boolean matchName(String zname, String subsetDirspec, String subsetFilespec) {
        String zipFilespec = subsetDirspec + "\\" + subsetFilespec.replace('/', '\\');
        boolean ret = StringUtils.equalsIgnoreCase(zname, zipFilespec);

        if (!ret) {
            zipFilespec = zipFilespec.replace('\\', '/');
            ret = StringUtils.equalsIgnoreCase(zname, zipFilespec);
        }

        return ret;
    }

    /**
     * Read data the data of a zipEntry within a zipFile.
     * 
     * @param zipFile
     *            The ZipFile object.
     * @param zipEntry
     *            The ZipEntry object.
     * @return a ByteArrayOutputStream representing the data.
     * @throws IOException
     */
    private static ByteArrayOutputStream readData(ZipFile zipFile, ZipEntry zipEntry) throws IOException {
        InputStream in = zipFile.getInputStream(zipEntry);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int bytesRead = 0;
        byte[] buffer = new byte[2048];

        while ((bytesRead = in.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }

        in.close();

        return baos;
    }

    /*
     * Read a file and return the bytes.
     */
    private byte[] readFile(String filename) throws IOException {
        byte[] bytes;

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(filename);
        bytes = new byte[is.available()];
        is.read(bytes);

        is.close();

        return bytes;
    }

    private static File fileFromDataHandler(DataHandler dh) throws Exception {
        File f = null;
        OutputStream out = null;
        InputStream inputStream = null;

        final String fileName = UUID.randomUUID().toString() + ".zip";

        try {
            f = new File(fileName);
            inputStream = dh.getInputStream();
            out = new FileOutputStream(f);
            byte buf[] = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (FileNotFoundException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("File not found - " + fileName, e);
            }
            throw e;
        } catch (IOException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Exception thrown while trying to read file from DataHandler object", e);
            }
            throw e;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (out != null) {
                out.close();
            }
        }

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Created temporary work file " + f.getAbsolutePath());
        }

        return f;
    }

    private String getSuffix(String mimeType) {
        return "." + MimeType.lookup(mimeType).getSuffix();
    }
}
