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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.xd.common.DirectDocument2;
import org.nhindirect.xd.common.DirectDocuments;
import org.nhindirect.xd.common.XdmPackage;
import org.nhindirect.xd.common.type.DirectDocumentType;
import org.nhindirect.xd.common.type.FormatCodeEnum;
import org.nhindirect.xd.transform.MimeXdsTransformer;
import org.nhindirect.xd.transform.exception.TransformationException;
import org.nhindirect.xd.transform.util.type.MimeType;

/**
 * Transform a MimeMessage into a XDS request.
 * 
 * @author vlewis
 */
public class DefaultMimeXdsTransformer implements MimeXdsTransformer {

    private byte[] xdsDocument = null;
    private String xdsMimeType = null;
    private FormatCodeEnum xdsFormatCode = null;
    private DirectDocumentType documentType = null;
    private static final Log LOGGER = LogFactory.getFactory().getInstance(DefaultMimeXdsTransformer.class);

    /**
     * Construct a new DefaultMimeXdsTransformer object.
     */
    public DefaultMimeXdsTransformer() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.transform.MimeXdsTransformer#transform(javax.mail.internet.MimeMessage)
     */
    @Override
    public ProvideAndRegisterDocumentSetRequestType transform(MimeMessage mimeMessage) throws TransformationException {
        ProvideAndRegisterDocumentSetRequestType request;
        DirectDocuments documents = new DirectDocuments();

        try {
            Date sentDate = mimeMessage.getSentDate();
            String subject = mimeMessage.getSubject();
            String from = mimeMessage.getFrom()[0].toString();
            Address[] recipients = mimeMessage.getAllRecipients();

            // Plain mail (no attachments)
            if (MimeType.TEXT_PLAIN.matches(mimeMessage.getContentType())) {
                LOGGER.info("Handling plain mail (no attachments) - " + mimeMessage.getContentType());

                // Get the document type
                documentType = DirectDocumentType.lookup(mimeMessage);

                // Get the format code and MIME type
                xdsFormatCode = documentType.getFormatCode();
                xdsMimeType = documentType.getMimeType().getType();

                // Get the contents
                xdsDocument = ((String) mimeMessage.getContent()).getBytes();

                // Add document to the collection of documents
                documents.getDocuments().add(getDocument(sentDate, from));
                documents.setSubmissionSet(getSubmissionSet(subject, sentDate, from, recipients));
            } // Multipart/mixed (attachments)
            else if (MimeType.MULTIPART.matches(mimeMessage.getContentType())) {
                LOGGER.info("Handling multipart/mixed - " + mimeMessage.getContentType());

                MimeMultipart mimeMultipart = (MimeMultipart) mimeMessage.getContent();
               BodyPart xdmBodyPart = null;
                
                for (int i = 0; i < mimeMultipart.getCount(); i++) {
                    //check for XDM
                     BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                     documentType = DirectDocumentType.lookup(bodyPart);
                    if (DirectDocumentType.XDM.equals(documentType)) {
                        xdmBodyPart =  bodyPart;
                    }
                }
                
                
                // For each BodyPart
                for (int i = 0; i < mimeMultipart.getCount(); i++) {
                    
  /*
                     * Special handling for XDM attachments.
                     * 
                     * Spec says if XDM package is present, this will be the
                     * only attachment.
                     * 
                     * Overwrite all documents with XDM content and then break
                     */
                    if (xdmBodyPart != null) {
                        XdmPackage xdmPackage = XdmPackage.fromXdmZipDataHandler(xdmBodyPart.getDataHandler());

                        // Spec says if XDM package is present, this will be the only attachment
                        // Overwrite all documents with XDM content and then break
                        System.out.println("XDM FILE FOUND");
                        documents = xdmPackage.getDocuments();

                        break;
                    }
                    BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                    // Skip empty BodyParts
                    if (bodyPart.getSize() <= 0) {
                        LOGGER.warn("Empty body, skipping");
                        continue;
                    }

                    // Get the document type
                    documentType = DirectDocumentType.lookup(bodyPart);

                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("File name: " + bodyPart.getFileName());
                    }
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("Content type: " + bodyPart.getContentType());
                    }
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("DocumentType: " + documentType.toString());
                    }

                  

                    // Get the format code and MIME type
                    xdsFormatCode = documentType.getFormatCode();
                    xdsMimeType = documentType.getMimeType().getType();

                    // Best guess for UNKNOWN MIME type
                    if (DirectDocumentType.UNKNOWN.equals(documentType)) {
                        xdsMimeType = bodyPart.getContentType();
                    }
                    
                    // Get the contents
                    xdsDocument = read(bodyPart);

                    // Add the document to the collection of documents
                    documents.getDocuments().add(getDocument(sentDate, from));
                    documents.setSubmissionSet(getSubmissionSet(subject, sentDate, from, recipients));
                }
            } else {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Message content type (" + mimeMessage.getContentType() + ") is not supported, skipping");
                }
            }
        } catch (MessagingException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Unexpected MessagingException occured while handling MimeMessage", e);
            }
            throw new TransformationException("Unable to complete transformation.", e);
        } catch (IOException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Unexpected IOException occured while handling MimeMessage", e);
            }
            throw new TransformationException("Unable to complete transformation.", e);
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Unexpected Exception occured while handling MimeMessage", e);
            }
            throw new TransformationException("Unable to complete transformation", e);
        }

        try {
            request = documents.toProvideAndRegisterDocumentSetRequestType();
        } catch (IOException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Unexpected IOException occured while transforming to ProvideAndRegisterDocumentSetRequestType", e);
            }
            throw new TransformationException("Unable to complete transformation", e);
        }

        return request;
    }

    /*
     * Metadata Attribute           XDS     Minimal Metadata
     * -----------------------------------------------------
     * author                       R2      R
     * contentTypeCode              R       R2
     * entryUUID                    R       R
     * intendedRecipient            O       R
     * patientId                    R       R2
     * sourceId                     R       R
     * submissionTime               R       R
     * title                        O       O
     * uniqueId                     R       R
     */
    private DirectDocuments.SubmissionSet getSubmissionSet(String subject, Date sentDate, String auth,
            Address[] recipients) throws Exception {
        DirectDocuments.SubmissionSet submissionSet = new DirectDocuments.SubmissionSet();

        // (R) Minimal Metadata Source
        submissionSet.setAuthorTelecommunication(auth); // TODO: format this correctly
        submissionSet.setSourceId("TODO"); // TODO: "UUID URN mapped by configuration to sending organization"
        submissionSet.setSubmissionTime(sentDate);
        submissionSet.setUniqueId(UUID.randomUUID().toString());
        for (Address address : recipients) {
            submissionSet.getIntendedRecipient().add("||^^Internet^" + address.toString());
        }

        // (R2) Minimal Metadata Source
        // --

        // (O) Minimal Metadata Source
        // TODO: title (subject)

        // Additional metadata from document parsing
        documentType.parse(new String(xdsDocument), submissionSet);

        return submissionSet;
    }

    /*
     * Metadata Attribute           XDS     Minimal Metadata
     * -----------------------------------------------------
     * author                       R2      R2
     * classCode                    R       R2
     * confidentialityCode          R       R2
     * creationTime                 R       R2
     * entriUUID                    R       R
     * formatCode                   R       R
     * healthcareFacilityTypeCode   R       R2
     * languageCode                 R       R2
     * mimeType                     R       R
     * patientId                    R       R2
     * practiceSettingCode          R       R2
     * sourcePatientId              R       R2
     * typeCode                     R       R2
     * uniqueId                     R       R
     */
    private DirectDocument2 getDocument(Date sentDate, String auth) throws Exception {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        // (R) Minimal Metadata Source
        metadata.setMimeType(xdsMimeType);
        metadata.setUniqueId(UUID.randomUUID().toString());

        // (R2) Minimal Metadata Source
        if (xdsFormatCode != null) {
            metadata.setFormatCode(xdsFormatCode);
        }

        // Additional metadata from document parsing
        documentType.parse(new String(xdsDocument), metadata);

        document.setData(xdsDocument);

        return document;
    }

    private static byte[] read(BodyPart bodyPart) throws MessagingException, IOException {
        InputStream inputStream = bodyPart.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        int data = 0;
        byte[] buffer = new byte[1024];
        while ((data = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, data);
        }

        return outputStream.toByteArray();
    }
}
