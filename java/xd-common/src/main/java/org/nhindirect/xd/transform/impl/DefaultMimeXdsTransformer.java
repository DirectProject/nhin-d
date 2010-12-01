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

import static org.nhindirect.xd.transform.util.XdConstants.CCD_EXTENSION;
import static org.nhindirect.xd.transform.util.XdConstants.CCD_XMLNS;
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.xd.common.DirectDocument2;
import org.nhindirect.xd.common.DirectDocuments;
import org.nhindirect.xd.common.XdmPackage;
import org.nhindirect.xd.common.type.ClassCodeEnum;
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

                xdsFormatCode = FormatCodeEnum.TEXT.getValue();
                xdsMimeType = MimeType.TEXT_PLAIN.getType();
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

                    // Skip empty file names
                    if (StringUtils.isBlank(bodyPart.getFileName())
                            || StringUtils.equalsIgnoreCase(bodyPart.getFileName(), "null"))
                    {
                        LOGGER.warn("Filename is blank, skipping");
                        continue;
                    }

                    if (LOGGER.isInfoEnabled())
                        LOGGER.info("File name: " + bodyPart.getFileName());

                    // Assumed that any .zip is an XDM package (FIXME: this is a bad assumption)
                    if (StringUtils.contains(bodyPart.getFileName(), ".zip"))
                    {
                        try
                        {
                            LOGGER.info("Bodypart is an XDM request");

                            XdmPackage xdmPackage = XdmPackage.fromXdmZipDataHandler(bodyPart.getDataHandler());

                            // Spec says if XDM package is present, this will be the only attachment
                            // Overwrite all documents with XDM content
                            documents = xdmPackage.getDocuments();
                        }
                        catch (Exception x)
                        {
                            LOGGER.warn("Handling of assumed XDM request failed, skipping");
                        }

                        // Spec says if XDM package is present, this will be the only attachment
                        // No need to look at additional attachments
                        break;
                    }

                    InputStream inputStream = bodyPart.getInputStream();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                    int data = 0;
                    byte[] buffer = new byte[1024];
                    while ((data = inputStream.read(buffer)) != -1)
                    {
                        outputStream.write(buffer, 0, data);
                    }

                    String contentString = new String(outputStream.toByteArray());

                    LOGGER.info("Content type is " + bodyPart.getContentType());

                    // special handling for recognized content types
                    if (MimeType.TEXT_PLAIN.matches(bodyPart.getContentType()))
                    {
                        LOGGER.info("Matched type TEXT_PLAIN");

                        if (StringUtils.isBlank(contentString))
                        {
                            continue; // skip 'empty' parts
                        }

                        xdsFormatCode = FormatCodeEnum.TEXT.getValue();
                        xdsMimeType = MimeType.TEXT_PLAIN.getType();
                        xdsDocument = outputStream.toByteArray();

                        documents.getDocuments().add(getDocument(sentDate, from));
                        documents.setSubmissionSet(getSubmissionSet(subject, sentDate, from, recipients));
                    }
                    else if (MimeType.TEXT_XML.matches(bodyPart.getContentType()))
                    {
                        LOGGER.info("Matched type TEXT_XML");

                        if (StringUtils.contains(contentString, CCD_XMLNS)
                                && StringUtils.contains(contentString, CCD_EXTENSION))
                        {
                            LOGGER.info("Matched format CODE_FORMAT_CDAR2");

                            xdsFormatCode = FormatCodeEnum.CDAR2.getValue();
                            xdsMimeType = MimeType.TEXT_XML.getType();
                            xdsDocument = outputStream.toByteArray();
                        }
                        else
                        {
                            // Other XML (possible CCR or HL7)
                            LOGGER.info("Defaulted to format CODE_FORMAT_TEXT");

                            xdsFormatCode = FormatCodeEnum.TEXT.getValue();
                            xdsMimeType = MimeType.TEXT_XML.getType();
                            xdsDocument = outputStream.toByteArray();
                        }

                        // TODO: support more XML types

                        documents.getDocuments().add(getDocument(sentDate, from));
                        documents.setSubmissionSet(getSubmissionSet(subject, sentDate, from, recipients));
                    }
                    else
                    {
                        LOGGER.info("Did not match a type");

                        // Otherwise make best effort passing MIME content type

                        xdsFormatCode = FormatCodeEnum.TEXT.getValue();
                        xdsMimeType = bodyPart.getContentType();
                        xdsDocument = outputStream.toByteArray();

                        documents.getDocuments().add(getDocument(sentDate, from));
                        documents.setSubmissionSet(getSubmissionSet(subject, sentDate, from, recipients));
                    }
                }
            }
            else
            {
                if (LOGGER.isWarnEnabled())
                    LOGGER.warn("Message content type (" + mimeMessage.getContentType()
                            + ") is not supported, skipping");
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
        
        // Parse CCD for patient info
        String sdoc = new String(xdsDocument);
        CcdParser cp = new CcdParser();
        cp.parse(sdoc);

        // Create patient object
        SimplePerson sourcePatient = new SimplePerson();
        sourcePatient.setLocalId(cp.getPatientId());
        sourcePatient.setLocalOrg(cp.getOrgId());
        
        // author R2 R
        submissionSet.setAuthorPerson(auth);
        submissionSet.setAuthorInstitution(Arrays.asList(sourcePatient.getLocalOrg()));
        submissionSet.setAuthorRole(auth == null ? "System" : auth + "'s role");
        
        // contentTypeCode R R2
        submissionSet.setContentTypeCode(subject, true);
        
        // entryUUID R R
        
        // intendedRecipient O R
        for (Address address : recipients)
            submissionSet.getIntendedRecipient().add("||^^Internet^" + address.toString());
        
        // patientId R R2
        submissionSet.setPatientId(sourcePatient.getLocalId() + "^^^&" + sourcePatient.getLocalOrg());
        
        // sourceId R R
        submissionSet.setSourceId(sourcePatient.getLocalOrg());
        
        // submissionTime R R
        submissionSet.setSubmissionTime(sentDate);

        // title O O
        
        // uniqueId R R
        submissionSet.setUniqueId(UUID.randomUUID().toString());
        
        return submissionSet;
    }

    private DirectDocument2 getDocument(Date sentDate, String auth) throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        // Parse CCD for patient info
        String sdoc = new String(xdsDocument);
        CcdParser cp = new CcdParser();
        cp.parse(sdoc);

        // Create patient object
        SimplePerson sourcePatient = new SimplePerson();
        sourcePatient.setLocalId(cp.getPatientId());
        sourcePatient.setLocalOrg(cp.getOrgId());

        // author R2 R2
        metadata.setAuthorPerson(auth);
        metadata.setAuthorInstitution(Arrays.asList(sourcePatient.getLocalOrg()));
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
        metadata.setPatientId(sourcePatient.getLocalId() + "^^^&" + sourcePatient.getLocalOrg());
        
        // practiceSettingCode R R2
        metadata.setPracticeSettingCode(PracticeSettingCodeEnum.MULTIDISCIPLINARY.getValue(), true);
        
        // sourcePatientId R R2
        // sourcePatientInfo R2 R2
        metadata.setSourcePatient(sourcePatient);
        
        // typeCode R R2
        // TODO
        
        // uniqueId R R
        metadata.setUniqueId(UUID.randomUUID().toString());
        
        // TODO: There are extra values being set not specified in the XD* spec, need to verify correctness
        metadata.setLoinc(LoincEnum.LOINC_34133_9.getValue(), true);

        document.setData(new String(xdsDocument));

        return document;
    }

}
