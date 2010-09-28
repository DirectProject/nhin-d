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

package org.nhindirect.transform.impl;

import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.transform.XdmXdsTransformer;
import org.nhindirect.transform.exception.TransformationException;
import org.nhindirect.transform.util.XmlUtils;

/**
 * This class handles the transformation of XDM to XDS.
 * 
 * @author beau
 */
public class DefaultXdmXdsTransformer implements XdmXdsTransformer
{
    private static final String XDM_FILENAME_DATA = "DOCUMENT.xml";
    private static final String XDM_FILENAME_METADATA = "METADATA.xml";

    private static final Log LOGGER = LogFactory.getFactory().getInstance(DefaultXdmXdsTransformer.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.transform.XdmXdsTransformer#transform(java.io.File)
     */
    @Override
    public ProvideAndRegisterDocumentSetRequestType transform(File file) throws TransformationException
    {
        LOGGER.trace("Begin transformation of XDM to XDS");

        String docId = null;
        ZipFile zipFile = null;

        ProvideAndRegisterDocumentSetRequestType prsr = new ProvideAndRegisterDocumentSetRequestType();

        try
        {
            zipFile = new ZipFile(file, ZipFile.OPEN_READ);

            Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
            ZipEntry zipEntry = null;

            // load the ZIP archive into memory
            while (zipEntries.hasMoreElements())
            {
                LOGGER.trace("Processing a ZipEntry");

                zipEntry = zipEntries.nextElement();
                String zname = zipEntry.getName();

                if (!zipEntry.isDirectory())
                {
                    String subsetDirspec = getSubmissionSetDirspec(zipEntry.getName());

                    // Read metadata
                    if (matchName(zname, subsetDirspec, XDM_FILENAME_METADATA))
                    {
                        ByteArrayOutputStream byteArrayOutputStream = readData(zipFile, zipEntry);

                        SubmitObjectsRequest submitObjectRequest = (SubmitObjectsRequest) XmlUtils.unmarshal(
                                byteArrayOutputStream.toString(),
                                oasis.names.tc.ebxml_regrep.xsd.lcm._3.ObjectFactory.class);

                        prsr.setSubmitObjectsRequest(submitObjectRequest);
                        docId = getDocId(submitObjectRequest);
                    }
                    // Read data
                    else if (matchName(zname, subsetDirspec, XDM_FILENAME_DATA))
                    {
                        ByteArrayOutputStream byteArrayOutputStream = readData(zipFile, zipEntry);

                        DataSource source = new ByteArrayDataSource(byteArrayOutputStream.toByteArray(),
                                "application/xml; charset=UTF-8");
                        DataHandler dhnew = new DataHandler(source);

                        Document pdoc = new Document();
                        pdoc.setValue(dhnew);
                        pdoc.setId(docId);

                        List<Document> docs = prsr.getDocument();
                        docs.add(pdoc);
                    }
                }

                zipFile.close();

                ((Document) prsr.getDocument().get(0)).setId(zname);
            }
        }
        catch (Exception e)
        {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Unable to complete transformation.", e);
            throw new TransformationException("Unable to complete transformation.", e);
        }

        return prsr;
    }

    /**
     * Get the document ID from a SubmitObjectsRequest object.
     * 
     * @param sor
     *            The SubmitObjectsRequest object from which to retrieve the
     *            document ID.
     * @return a document ID.
     */
    protected String getDocId(SubmitObjectsRequest submitObjectRequest)
    {
        if (submitObjectRequest == null)
            throw new IllegalArgumentException("SubmitObjectRequest must not be null.");

        String ret = null;

        RegistryObjectListType rol = submitObjectRequest.getRegistryObjectList();
        List<JAXBElement<? extends IdentifiableType>> extensible = rol.getIdentifiable();

        for (JAXBElement<? extends IdentifiableType> elem : extensible)
        {
            String type = elem.getDeclaredType().getName();
            Object value = elem.getValue();

            if (type.equalsIgnoreCase("oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType"))
            {
                ret = getDocId((ExtrinsicObjectType) value);
            }

            if (LOGGER.isTraceEnabled())
                LOGGER.trace(type + " " + value.toString());
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
    protected String getDocId(ExtrinsicObjectType extrinsicObjectType)
    {
        if (extrinsicObjectType == null)
            throw new IllegalArgumentException("ExtrinsicObjectType must not be null");

        String ret = null;

        for (ExternalIdentifierType eit : extrinsicObjectType.getExternalIdentifier())
        {
            if (eit.getIdentificationScheme().equals("urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab"))
            {
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
    protected String getSubmissionSetDirspec(String zipEntryName)
    {
        if (zipEntryName == null)
            return null;

        String[] components = zipEntryName.split("\\\\");
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
    protected boolean matchName(String zname, String subsetDirspec, String subsetFilespec)
    {
        String zipFilespec = subsetDirspec + "\\" + subsetFilespec.replace('/', '\\');
        boolean ret = zname.equals(zipFilespec);

        if (!ret)
        {
            zipFilespec = zipFilespec.replace('\\', '/');
            ret = zname.equals(zipFilespec);
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
    protected ByteArrayOutputStream readData(ZipFile zipFile, ZipEntry zipEntry) throws IOException
    {
        InputStream in = zipFile.getInputStream(zipEntry);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int bytesRead = 0;
        byte[] buffer = new byte[2048];

        while ((bytesRead = in.read(buffer)) != -1)
        {
            baos.write(buffer, 0, bytesRead);
        }

        in.close();

        if (LOGGER.isTraceEnabled())
            LOGGER.trace("Data read: " + baos.toString());

        return baos;
    }

}
