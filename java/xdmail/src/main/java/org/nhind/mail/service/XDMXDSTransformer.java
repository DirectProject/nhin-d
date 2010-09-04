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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExternalIdentifierType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;

import org.nhind.util.XMLUtils;

/**
 *
 * @author vlewis
 */
public class XDMXDSTransformer {

    private static final Logger LOGGER = Logger.getLogger(XDMXDSTransformer.class.getName());
    static private String XDM_FILENAME_METADATA = "METADATA.xml";
    static private String XDM_FILENAME_DATA = "DOCUMENT.xml";
    static private String XDM_DIRSPEC_SUBMISSIONROOT = "SUBSET01";

    /**
     * Reads an XDM ZIP archive and returns a set of XDS submissions.
     */
    public ProvideAndRegisterDocumentSetRequestType getXDMRequest(DataHandler dh) throws Exception {
        ProvideAndRegisterDocumentSetRequestType prsr = new ProvideAndRegisterDocumentSetRequestType();
        ZipFile zipFile = null;
        LOGGER.info("in getMDMRequest 2");

        File archiveFile = fileFromDataHandler(dh);
        return getXDMRequest(archiveFile);
    }

    public ProvideAndRegisterDocumentSetRequestType getXDMRequest(File archiveFile) throws Exception {
        ProvideAndRegisterDocumentSetRequestType prsr = new ProvideAndRegisterDocumentSetRequestType();
        ZipFile zipFile = null;
        LOGGER.info("in getMDMRequest 2");
        String docId = null;
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
                        QName qname = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0", "SubmitObjectsRequest");
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

    String getDocId(SubmitObjectsRequest sor) {
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
            Logger.getLogger(this.getClass().getPackage().getName()).log(Level.INFO, elem.getDeclaredType().getName() + elem.getValue().toString());
        }
        return ret;
    }

    String getDocId(ExtrinsicObjectType eot) {
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

    private boolean matchName(String zname, String subsetDirspec, String subsetFilespec) {

        boolean ret = false;
        String zipFilespec = subsetDirspec + "\\" + subsetFilespec.replace('/', '\\');
        ret = zname.equals(zipFilespec);
        if (!ret) {
            zipFilespec = zipFilespec.replace('\\', '/');
            ret = zname.equals(zipFilespec);
        }
        return ret;
    }

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
     * Given a full ZipEntry filespec, extracts the name of the folder (if present) under the IHE_XDM root
     * specified by IHE XDM.
     */
    private String getSubmissionSetDirspec(String zipEntryName) {
        String result = null;
        if (zipEntryName != null) {
            String[] components = zipEntryName.split("\\\\");

            result = components[0];

        }
        return result;
    }

    File fileFromDataHandler(DataHandler dh) throws Exception {

        File f = new File("outFile.java");
        InputStream inputStream = dh.getInputStream();
        OutputStream out = new FileOutputStream(f);
        byte buf[] = new byte[1024];
        int len;
        while ((len = inputStream.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        inputStream.close();
        return f;
    }
}
