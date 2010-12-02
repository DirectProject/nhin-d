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

import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
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
import org.nhindirect.xd.common.type.ClassCodeEnum;
import org.nhindirect.xd.common.type.DirectDocumentType;
import org.nhindirect.xd.common.type.FormatCodeEnum;
import org.nhindirect.xd.common.type.HealthcareFacilityTypeCodeEnum;
import org.nhindirect.xd.common.type.LoincEnum;
import org.nhindirect.xd.common.type.PracticeSettingCodeEnum;
import org.nhindirect.xd.transform.MimeXdsTransformer;
import org.nhindirect.xd.transform.exception.TransformationException;
import org.nhindirect.xd.transform.parse.ccd.CcdParser;
import org.nhindirect.xd.transform.pojo.SimplePerson;
import org.nhindirect.xd.transform.util.type.MimeType;

/**
 * Transform a MimeMessage into a XDS request.
 * 
 * @author vlewis
 */
public class DefaultMimeXdsTransformer implements MimeXdsTransformer
{
    private byte[] xdsDocument = null;
    private String xdsMimeType = null;
    private String xdsFormatCode = null;
    private DirectDocumentType documentType = null;

    private static final Log LOGGER = LogFactory.getFactory().getInstance(DefaultMimeXdsTransformer.class);

    /**
     * Construct a new DefaultMimeXdsTransformer object.
     */
    public DefaultMimeXdsTransformer()
    {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.transform.MimeXdsTransformer#transform(javax.mail.internet.MimeMessage)
     */
    @Override
    public ProvideAndRegisterDocumentSetRequestType transform(MimeMessage mimeMessage) throws TransformationException
    {
        DirectDocuments documents = new DirectDocuments();

        try
        {
            Date sentDate        = mimeMessage.getSentDate();
            String subject       = mimeMessage.getSubject();
            String from          = mimeMessage.getFrom()[0].toString();
            Address[] recipients = mimeMessage.getAllRecipients();
            
            // Plain mail (no attachments)
            if (MimeType.TEXT_PLAIN.matches(mimeMessage.getContentType()))
            {
                LOGGER.info("Handling plain mail (no attachments)");

                // Get the document type
                documentType = DirectDocumentType.lookup(mimeMessage);
                
                xdsFormatCode = documentType.getFormatCode().getValue();
                xdsMimeType = documentType.getMimeType().getType();
                
                xdsDocument = ((String) mimeMessage.getContent()).getBytes();

                documents.getDocuments().add(getDocument(sentDate, from));
                documents.setSubmissionSet(getSubmissionSet(subject, sentDate, from, recipients));
            }
            // Multipart/mixed (attachments)
            else if (MimeType.MULTIPART_MIXED.matches(mimeMessage.getContentType()))
            {
                LOGGER.info("Handling multipart/mixed");

                MimeMultipart mimeMultipart = (MimeMultipart) mimeMessage.getContent();

                // For each BodyPart
                for (int i = 0; i < mimeMultipart.getCount(); i++)
                {
                    BodyPart bodyPart = mimeMultipart.getBodyPart(i);

                    // Skip empty BodyParts
                    if (bodyPart.getSize() <= 0)
                    {
                        LOGGER.warn("Empty body, skipping");
                        continue;
                    }
                    
                    // Get the document type
                    documentType = DirectDocumentType.lookup(bodyPart);

                    if (LOGGER.isInfoEnabled()) LOGGER.info("File name: " + bodyPart.getFileName());
                    if (LOGGER.isInfoEnabled()) LOGGER.info("Content type: " + bodyPart.getContentType());
                    if (LOGGER.isInfoEnabled()) LOGGER.info("DocumentType: " + documentType.toString());
                    
                    /*
                     * Special handling for XDM attachments.
                     * 
                     * Spec says if XDM package is present, this will be the
                     * only attachment.
                     * 
                     * Overwrite all documents with XDM content and then break
                     */
                    if (DirectDocumentType.XDM.equals(documentType))
                    {
                        XdmPackage xdmPackage = XdmPackage.fromXdmZipDataHandler(bodyPart.getDataHandler());

                        // Spec says if XDM package is present, this will be the only attachment
                        // Overwrite all documents with XDM content and then break
                        documents = xdmPackage.getDocuments();

                        break;
                    }
                    
                    xdsFormatCode = documentType.getFormatCode().getValue();
                    xdsMimeType = documentType.getMimeType().getType();
                    
                    // Best guess for UNKNOWN
                    if (DirectDocumentType.UNKNOWN.equals(documentType))
                        xdsMimeType = bodyPart.getContentType();
                    
                    xdsDocument = read(bodyPart).getBytes();
                    
                    documents.getDocuments().add(getDocument(sentDate, from));
                    documents.setSubmissionSet(getSubmissionSet(subject, sentDate, from, recipients));
                }
            }
            else
            {
                if (LOGGER.isWarnEnabled())
                    LOGGER.warn("Message content type (" + mimeMessage.getContentType() + ") is not supported, skipping");
            }
        }
        catch (MessagingException e)
        {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Unexpected MessagingException occured while handling MimeMessage", e);
            throw new TransformationException("Unable to complete transformation.", e);
        }
        catch (IOException e)
        {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Unexpected IOException occured while handling MimeMessage", e);
            throw new TransformationException("Unable to complete transformation.", e);
        }
        catch (Exception e)
        {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Unexpected Exception occured while handling MimeMessage", e);
            throw new TransformationException("Unable to complete transformation", e);
        }
        
        try
        {
            return documents.toProvideAndRegisterDocumentSetRequestType();
        }
        catch (IOException e)
        {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Unexpected IOException occured while transforming to ProvideAndRegisterDocumentSetRequestType", e);
            throw new TransformationException("Unable to complete transformation", e);
        }
    }
  
    private DirectDocuments.SubmissionSet getSubmissionSet(String subject, Date sentDate, String auth,
            Address[] recipients) throws Exception
    {
        DirectDocuments.SubmissionSet submissionSet = new DirectDocuments.SubmissionSet();
        
        // author R2 R
        submissionSet.setAuthorPerson(auth);
        submissionSet.setAuthorRole(auth == null ? "System" : auth + "'s role");
        
        // contentTypeCode R R2
        submissionSet.setContentTypeCode(subject, true);
        
        // entryUUID R R
        
        // intendedRecipient O R
        for (Address address : recipients)
            submissionSet.getIntendedRecipient().add("||^^Internet^" + address.toString());
        
        // patientId R R2
        
        // sourceId R R
        
        // submissionTime R R
        submissionSet.setSubmissionTime(sentDate);

        // title O O
        
        // uniqueId R R
        submissionSet.setUniqueId(UUID.randomUUID().toString());
        
        if (DirectDocumentType.CCD.equals(documentType))
        {
            // Parse CCD for patient info
            String sdoc = new String(xdsDocument);
            CcdParser cp = new CcdParser();
            cp.parse(sdoc);

            // Create patient object
            SimplePerson sourcePatient = new SimplePerson();
            sourcePatient.setLocalId(cp.getPatientId());
            sourcePatient.setLocalOrg(cp.getOrgId());
            
            // author R2 R
            submissionSet.setAuthorInstitution(Arrays.asList(sourcePatient.getLocalOrg()));
            
            // patientId R R2
            submissionSet.setPatientId(sourcePatient.getLocalId() + "^^^&" + sourcePatient.getLocalOrg());
            
            // sourceId R R
            submissionSet.setSourceId(sourcePatient.getLocalOrg());
        }
        
        return submissionSet;
    }

    private DirectDocument2 getDocument(Date sentDate, String auth) throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        // author R2 R2
        metadata.setAuthorPerson(auth);
        metadata.setAuthorRole(auth == null ? "System" : auth + "'s role");
        
        // classCode R R2
        metadata.setClassCode(ClassCodeEnum.HISTORY_AND_PHYSICAL.getValue(), true);
        
        // confidentialityCode R R2
        // TODO
        
        // creationTime R R2
        metadata.setCreationTime(sentDate);
        
        // entryUUID R R
        
        // formatCode R R2
        metadata.setFormatCode(xdsFormatCode, true);
        
        // healthcareFacilityTypeCode R R2
        metadata.setHealthcareFacilityTypeCode(HealthcareFacilityTypeCodeEnum.OF.getValue(), true);
        
        // languageCode R R2
        // TODO
        
        // mimeType R R
        metadata.setMimeType(xdsMimeType);
        
        // patientId R R2
        
        // practiceSettingCode R R2
        metadata.setPracticeSettingCode(PracticeSettingCodeEnum.MULTIDISCIPLINARY.getValue(), true);
        
        // sourcePatientId R R2
        // sourcePatientInfo R2 R2
        
        // typeCode R R2
        // TODO
        
        // uniqueId R R
        metadata.setUniqueId(UUID.randomUUID().toString());
        
        // TODO: There are extra values being set not specified in the XD* spec, need to verify correctness
        metadata.setLoinc(LoincEnum.LOINC_34133_9.getValue(), true);

        if (DirectDocumentType.CCD.equals(documentType))
        {
            // Parse CCD for patient info
            String sdoc = new String(xdsDocument);
            CcdParser cp = new CcdParser();
            cp.parse(sdoc);

            // Create patient object
            SimplePerson sourcePatient = new SimplePerson();
            sourcePatient.setLocalId(cp.getPatientId());
            sourcePatient.setLocalOrg(cp.getOrgId());

            // author R2 R2
            metadata.setAuthorInstitution(Arrays.asList(sourcePatient.getLocalOrg()));

            // patientId R R2
            metadata.setPatientId(sourcePatient.getLocalId() + "^^^&" + sourcePatient.getLocalOrg());

            // sourcePatientInfo R2 R2
            metadata.setSourcePatient(sourcePatient);
        }
        
        document.setData(new String(xdsDocument));

        return document;
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

        return new String(outputStream.toByteArray());
    }

}
