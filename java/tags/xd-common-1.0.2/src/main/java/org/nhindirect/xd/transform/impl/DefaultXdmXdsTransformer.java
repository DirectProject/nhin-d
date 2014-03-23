/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Vincent Lewis     vincent.lewis@gsihealth.com
 
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
package org.nhindirect.xd.transform.impl;

import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBElement;

import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExternalIdentifierType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.IdentifiableType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.xd.transform.XdmXdsTransformer;
import org.nhindirect.xd.transform.exception.TransformationException;
import org.nhindirect.xd.transform.util.XmlUtils;
import org.nhindirect.xd.transform.util.type.MimeType;

/**
 * This class handles the transformation of XDM to XDS.
 * 
 * @author vlewis
 */
public class DefaultXdmXdsTransformer implements XdmXdsTransformer {

    private static String XDM_FILENAME_DATA = "DOCUMENT.xml";
    private static final String XDM_FILENAME_METADATA = "METADATA.xml";
    private static final Log LOGGER = LogFactory.getFactory().getInstance(DefaultXdmXdsTransformer.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.transform.XdmXdsTransformer#transform(java.io.File)
     */
    @Override
    public ProvideAndRegisterDocumentSetRequestType transform(File file) throws TransformationException {
        LOGGER.trace("Begin transformation of XDM to XDS (file)");

        String docId = null;
        ZipFile zipFile = null;
        String docName = getDocName(file);
        if (docName != null) {
            XDM_FILENAME_DATA = docName;
        }

        ProvideAndRegisterDocumentSetRequestType prsr = new ProvideAndRegisterDocumentSetRequestType();

        try {
            zipFile = new ZipFile(file, ZipFile.OPEN_READ);

            Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
            ZipEntry zipEntry = null;

            // load the ZIP archive into memory
            while (zipEntries.hasMoreElements()) {


                zipEntry = zipEntries.nextElement();
                String zname = zipEntry.getName();
                LOGGER.trace("Processing a ZipEntry " + zname);
                if (!zipEntry.isDirectory()) {
                    String subsetDirspec = getSubmissionSetDirspec(zipEntry.getName());

                    // Read metadata
                    if (matchName(zname, subsetDirspec, XDM_FILENAME_METADATA)) {
                        ByteArrayOutputStream byteArrayOutputStream = readData(zipFile, zipEntry);

                        SubmitObjectsRequest submitObjectRequest = (SubmitObjectsRequest) XmlUtils.unmarshal(
                                byteArrayOutputStream.toString(),
                                oasis.names.tc.ebxml_regrep.xsd.lcm._3.ObjectFactory.class);

                        prsr.setSubmitObjectsRequest(submitObjectRequest);

                        docId = getDocId(submitObjectRequest);
                    } // Read data
                    else if (matchName(zname, subsetDirspec, XDM_FILENAME_DATA)) {
                        ByteArrayOutputStream byteArrayOutputStream = readData(zipFile, zipEntry);

                        DataSource source = new ByteArrayDataSource(byteArrayOutputStream.toByteArray(),
                                MimeType.APPLICATION_XML + "; charset=UTF-8");
                        DataHandler dhnew = new DataHandler(source);

                        Document pdoc = new Document();
                        pdoc.setValue(dhnew);
                        pdoc.setId(docId);

                        List<Document> docs = prsr.getDocument();
                        docs.add(pdoc);
                    }
                }

                if (!prsr.getDocument().isEmpty()) {
                    ((Document) prsr.getDocument().get(0)).setId(zname);
                }
            }

            zipFile.close();
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Unable to complete transformation.", e);
            }
            throw new TransformationException("Unable to complete transformation.", e);
        }

        return prsr;
    }

    public String getDocName(File file) throws TransformationException {
        LOGGER.trace("Begin transformation of XDM to XDS (file)");


        ZipFile zipFile = null;
        String objectId = null;

        ProvideAndRegisterDocumentSetRequestType prsr = new ProvideAndRegisterDocumentSetRequestType();

        try {
            zipFile = new ZipFile(file, ZipFile.OPEN_READ);

            Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
            ZipEntry zipEntry = null;

            // load the ZIP archive into memory
            while (zipEntries.hasMoreElements()) {


                zipEntry = zipEntries.nextElement();
                String zname = zipEntry.getName();
                LOGGER.trace("Processing a ZipEntry " + zname);
                if (!zipEntry.isDirectory()) {
                    String subsetDirspec = getSubmissionSetDirspec(zipEntry.getName());

                    // Read metadata
                    if (matchName(zname, subsetDirspec, XDM_FILENAME_METADATA)) {
                        ByteArrayOutputStream byteArrayOutputStream = readData(zipFile, zipEntry);

                        SubmitObjectsRequest submitObjectRequest = (SubmitObjectsRequest) XmlUtils.unmarshal(
                                byteArrayOutputStream.toString(),
                                oasis.names.tc.ebxml_regrep.xsd.lcm._3.ObjectFactory.class);

                        prsr.setSubmitObjectsRequest(submitObjectRequest);
                        objectId = getDocName(submitObjectRequest);
                    } // Read data

                }

            }
            zipFile.close();
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Unable to complete getObjectId.", e);
            }
            throw new TransformationException("Unable to complete getObjectId.", e);
        }

        return objectId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nhindirect.transform.XdmXdsTransformer#transform(javax.activation
     * .DataHandler)
     */
    @Override
    public ProvideAndRegisterDocumentSetRequestType transform(DataHandler dataHandler) throws TransformationException {
        LOGGER.trace("Begin transformation of XDM to XDS (datahandler)");

        File file = null;

        try {
            // Create a temporary work file
            file = fileFromDataHandler(dataHandler);
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error creating temporary work file, unable to complete transformation.", e);
            }
            throw new TransformationException("Error creating temporary work file, unable to complete transformation.",
                    e);
        }

        ProvideAndRegisterDocumentSetRequestType request = transform(file);

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

        return request;
    }

    /**
     * Get the document ID from a SubmitObjectsRequest object.
     * 
     * @param sor
     *            The SubmitObjectsRequest object from which to retrieve the
     *            document ID.
     * @return a document ID.
     */
    protected String getDocId(SubmitObjectsRequest submitObjectRequest) {
        if (submitObjectRequest == null) {
            throw new IllegalArgumentException("SubmitObjectRequest must not be null.");
        }

        String ret = null;

        RegistryObjectListType rol = submitObjectRequest.getRegistryObjectList();
        List<JAXBElement<? extends IdentifiableType>> extensible = rol.getIdentifiable();

        for (JAXBElement<? extends IdentifiableType> elem : extensible) {
            String type = elem.getDeclaredType().getName();
            Object value = elem.getValue();

            if (StringUtils.equals(type, "oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType")) {
                ret = getDocId((ExtrinsicObjectType) value);
            }

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(type + " " + value.toString());
            }
        }

        return ret;
    }

    protected String getDocName(SubmitObjectsRequest submitObjectRequest) {
        if (submitObjectRequest == null) {
            throw new IllegalArgumentException("SubmitObjectRequest must not be null.");
        }

        String ret = null;

        RegistryObjectListType rol = submitObjectRequest.getRegistryObjectList();
        List<JAXBElement<? extends IdentifiableType>> extensible = rol.getIdentifiable();

        for (JAXBElement<? extends IdentifiableType> elem : extensible) {
            String type = elem.getDeclaredType().getName();
            Object value = elem.getValue();

            if (StringUtils.equals(type, "oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType")) {
                String obId = ((ExtrinsicObjectType) value).getId();
                String mimeType = ((ExtrinsicObjectType) value).getMimeType();
                String suffix = "xml";
                if (mimeType.indexOf("xml") >= 0) {
                    suffix = "xml";
                } else if (mimeType.indexOf("pdf") >= 0) {
                    suffix = "pdf";
                }
                ret = obId + "." + suffix;

                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace(type + " " + value.toString());
                }
            }
        }

        return ret;
    }

    /**
     * Get the document ID from an EntrinsicObjectType object.
     * 
     * @param eot
     *            The EntrinsicObjectType object from which to retrieve the
     *            document ID.
     * @return a document ID.
     */
    protected String getDocId(ExtrinsicObjectType extrinsicObjectType) {
        if (extrinsicObjectType == null) {
            throw new IllegalArgumentException("ExtrinsicObjectType must not be null");
        }

        String ret = null;

        for (ExternalIdentifierType eit : extrinsicObjectType.getExternalIdentifier()) {
            if (StringUtils.equals(eit.getIdentificationScheme(), "urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab")) {
                ret = eit.getValue();
            }
        }

        return ret;
    }

    /**
     * Given a full ZipEntry filespec, extracts the name of the folder (if
     * present) under the IHE_XDM root specified by IHE XDM.
     * 
     * @param zipEntryName
     *            The ZIP entry name.
     * @return the name of the folder.
     */
    protected String getSubmissionSetDirspec(String zipEntryName) {
        if (zipEntryName == null) {
            return null;
        }
        String ret = "";
        zipEntryName = zipEntryName.replaceAll("\\\\", "/");
        String[] components = zipEntryName.split("/");
        for (int i = 0; i < components.length - 1; i++) {
            ret += (components[i] + "/");
        }
        if (ret.length() == 0) {
            return "";
        }
        ret = ret.substring(0, ret.length() - 1);


        return ret;
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
    protected boolean matchName(String zname, String subsetDirspec, String subsetFilespec) {
        zname = zname.replaceAll("\\\\", "/");
        String zipFilespec = subsetDirspec + "/" + subsetFilespec;
        boolean ret = StringUtils.equals(zname, zipFilespec);


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
    protected ByteArrayOutputStream readData(ZipFile zipFile, ZipEntry zipEntry) throws IOException {
        InputStream in = zipFile.getInputStream(zipEntry);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int bytesRead = 0;
        byte[] buffer = new byte[2048];

        while ((bytesRead = in.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }

        in.close();

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Data read: " + baos.toString());
        }

        return baos;
    }

    /**
     * Create a File object from the given DataHandler object.
     * 
     * @param dh
     *            The DataHandler object.
     * @return a File object created from the DataHandler object.
     * @throws Exception
     */
    protected File fileFromDataHandler(DataHandler dh) throws Exception {
        File f = null;
        OutputStream out = null;
        InputStream inputStream = null;

        final String fileName = "xdmail-" + UUID.randomUUID().toString() + ".zip";

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
}
