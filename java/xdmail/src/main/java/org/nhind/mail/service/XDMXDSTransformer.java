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

package org.nhind.mail.service;

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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBElement;

import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExternalIdentifierType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;

import org.nhind.mail.util.XMLUtils;

/**
 *
 * @author vlewis
 */
public class XDMXDSTransformer {

    static private String XDM_FILENAME_METADATA = "METADATA.xml";
    static private String XDM_FILENAME_DATA = "DOCUMENT.xml";
    // static private String XDM_DIRSPEC_SUBMISSIONROOT = "SUBSET01";

    /**
     * Class logger.
     */
    private static final Logger LOGGER = Logger.getLogger(XDMXDSTransformer.class.getName());
    
    /**
     * Reads an XDM ZIP archive and returns a set of XDS submissions.
     * 
     * @param dh
     * @return
     * @throws Exception
     */
    public ProvideAndRegisterDocumentSetRequestType getXDMRequest(DataHandler dh) throws Exception {
        LOGGER.info("Inside getXDMRequest(DataHandler)");

        File archiveFile = fileFromDataHandler(dh);
        return getXDMRequest(archiveFile);
    }

    /**
     * @param archiveFile
     * @return
     * @throws Exception
     */
    public ProvideAndRegisterDocumentSetRequestType getXDMRequest(File archiveFile) throws Exception {
        LOGGER.info("Inside getXDMRequest(File)");
        
        String docId = null;
        ZipFile zipFile = null;
        ProvideAndRegisterDocumentSetRequestType prsr = new ProvideAndRegisterDocumentSetRequestType();
        
        try {

            zipFile = new ZipFile(archiveFile, ZipFile.OPEN_READ);


            Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
            ZipEntry zipEntry = null;

            // load the ZIP archive into memory
            while (zipEntries.hasMoreElements()) {
                LOGGER.info("in zipEntries");
                zipEntry = zipEntries.nextElement();
                String zname = zipEntry.getName();
                if (!zipEntry.isDirectory()) { //&& zipEntry.getName().startsWith(XDM_DIRSPEC_SUBMISSIONROOT)) {
                    String subsetDirspec = getSubmissionSetDirspec(zipEntry.getName());
                    if (matchName(zname, subsetDirspec, XDM_FILENAME_METADATA)) {
                        InputStream in = zipFile.getInputStream(zipEntry);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        int bytesRead = 0;
                        byte[] buffer = new byte[2048];
                        while ((bytesRead = in.read(buffer)) != -1) {
                            baos.write(buffer, 0, bytesRead);
                        }
                        in.close();
                        LOGGER.info("metadata " + baos.toString());

                        SubmitObjectsRequest sor = (SubmitObjectsRequest) XMLUtils.unmarshal(baos.toString(), oasis.names.tc.ebxml_regrep.xsd.lcm._3.ObjectFactory.class);
                        prsr.setSubmitObjectsRequest(sor);
                        docId = getDocId(sor);


                    } else if (matchName(zname, subsetDirspec, XDM_FILENAME_DATA)) {

                        InputStream in = zipFile.getInputStream(zipEntry);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        int bytesRead = 0;
                        byte[] buffer = new byte[2048];
                        while ((bytesRead = in.read(buffer)) != -1) {
                            baos.write(buffer, 0, bytesRead);
                        }
                        in.close();
                        LOGGER.info("xml data " + baos.toString());
                        List<Document> docs = prsr.getDocument();
                        Document pdoc = new Document();

                        DataSource source = new ByteArrayDataSource(baos.toByteArray(), "application/xml; charset=UTF-8");
                        DataHandler dhnew = new DataHandler(source);
                        pdoc.setValue(dhnew);
                        pdoc.setId(docId);
                        docs.add(pdoc);
                    }
                }
                ((Document)prsr.getDocument().get(0)).setId(zname);
            }
        } finally {
            if (zipFile != null) {
                zipFile.close();
            }
        }

        return prsr;
    }

    /**
     * @param sor
     * @return
     */
    protected String getDocId(SubmitObjectsRequest sor) {
        String ret = null;
        RegistryObjectListType rol = sor.getRegistryObjectList();
        List extensible = rol.getIdentifiable();
        Iterator iext = extensible.iterator();
        while (iext.hasNext()) {
            JAXBElement elem = (JAXBElement) iext.next();
            String type = elem.getDeclaredType().getName();
            Object value = elem.getValue();
            if (type.equals("oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType")) {
                ret = getDocId((ExtrinsicObjectType) value);
            }
            LOGGER.info(elem.getDeclaredType().getName() + elem.getValue().toString());
        }
        return ret;
    }

    /**
     * @param eot
     * @return
     */
    protected String getDocId(ExtrinsicObjectType eot) {
        String ret = null;
        List<ExternalIdentifierType> eits= eot.getExternalIdentifier();
        Iterator<ExternalIdentifierType> ieits = eits.iterator();
        while(ieits.hasNext()){
            ExternalIdentifierType eit = ieits.next();
            if(eit.getIdentificationScheme().equals("urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab")){
             ret = eit.getValue();
            }
        }
        return ret;
    }

    /**
     * @param zname
     * @param subsetDirspec
     * @param subsetFilespec
     * @return
     */
    static boolean matchName(String zname, String subsetDirspec, String subsetFilespec) {
        boolean ret = false;

        String zipFilespec = subsetDirspec + "\\" + subsetFilespec.replace('/', '\\');
        ret = zname.equals(zipFilespec);
        if (!ret) {
            zipFilespec = zipFilespec.replace('\\', '/');
            ret = zname.equals(zipFilespec);
        }
        return ret;
    }

    /**
     * @param zipFile
     * @param subsetDirspec
     * @param subsetFilespec
     * @return
     */
    private ZipEntry getXDMZipEntry(ZipFile zipFile, String subsetDirspec, String subsetFilespec) {
        ZipEntry result = null;
        // String zipFilespec = XDM_DIRSPEC_SUBMISSIONROOT + "\\" + subsetDirspec + "\\" + subsetFilespec.replace('/', '\\');
        String zipFilespec = subsetDirspec + "\\" + subsetFilespec.replace('/', '\\');
        result = zipFile.getEntry(zipFilespec);
        if (result == null) {
            zipFilespec = zipFilespec.replace('\\', '/');
            result = zipFile.getEntry(zipFilespec);
        }
        return result;
    }

    /**
     * Given a full ZipEntry filespec, extracts the name of the folder (if
     * present) under the IHE_XDM root specified by IHE XDM.
     * 
     * @param zipEntryName
     * @return
     */
    private String getSubmissionSetDirspec(String zipEntryName) {
        String result = null;
        if (zipEntryName != null) {
            String[] components = zipEntryName.split("\\\\");

            result = components[0];

        }
        return result;
    }

    /**
     * @param dh
     * @return
     * @throws Exception
     */
    protected File fileFromDataHandler(DataHandler dh) throws Exception {
        File f = null;
        OutputStream out = null;      
        InputStream inputStream = null;
        
        // TODO: outFile.java?
        final String fileName = "outFile.java";

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
            LOGGER.warning("File not found - " + fileName);
            throw e;
        } catch (IOException e) {
            LOGGER.warning("Exception thrown while trying to read file from DataHandler object");
            throw e;
        } finally {
            if (inputStream != null)
                inputStream.close();
            if (out != null)
                out.close();
        }
        
        return f;
    }
}
