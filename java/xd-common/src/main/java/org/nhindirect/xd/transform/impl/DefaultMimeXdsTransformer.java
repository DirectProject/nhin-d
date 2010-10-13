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
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.xd.common.DirectDocument;
import org.nhindirect.xd.common.type.ClassCodeEnum;
import org.nhindirect.xd.common.type.FormatCodeEnum;
import org.nhindirect.xd.common.type.HealthcareFacilityTypeCodeEnum;
import org.nhindirect.xd.common.type.LoincEnum;
import org.nhindirect.xd.common.type.PracticeSettingCodeEnum;
import org.nhindirect.xd.transform.MimeXdsTransformer;
import org.nhindirect.xd.transform.XdmXdsTransformer;
import org.nhindirect.xd.transform.exception.TransformationException;
import org.nhindirect.xd.transform.parse.ccd.CcdParser;
import org.nhindirect.xd.transform.pojo.SimplePerson;
import org.nhindirect.xd.transform.util.type.MimeType;

/*
 * FIXME
 * 
 * The system currently handles multiple documents and recipients. 
 * 
 * Each document is placed into its own ProvideAndRegisterDocumentSetRequestType 
 * object, and correspondingly its own SOAP message. 
 * 
 * ProvideAndRegisterDocumentSetRequestType allows for multiple documents in a 
 * single request, and this class should eventually be updated to support this.
 */

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

    private XdmXdsTransformer xdmXdsTransformer = new DefaultXdmXdsTransformer();

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
     * @see
     * org.nhindirect.transform.MimeXdsTransformer#transform(javax.mail.internet
     * .MimeMessage)
     */
    @Override
    public List<ProvideAndRegisterDocumentSetRequestType> transform(MimeMessage mimeMessage)
            throws TransformationException
    {
        List<ProvideAndRegisterDocumentSetRequestType> requests = new ArrayList<ProvideAndRegisterDocumentSetRequestType>();

        try
        {
            Date sentDate = mimeMessage.getSentDate();
            String subject = mimeMessage.getSubject();

            String from = mimeMessage.getFrom()[0].toString();
            Address[] recipients = mimeMessage.getAllRecipients();

            // Plain mail (no attachments)
            if (MimeType.TEXT_PLAIN.matches(mimeMessage.getContentType()))
            {
                LOGGER.info("Handling plain mail (no attachments)");

                xdsFormatCode = FormatCodeEnum.TEXT.getValue();
                xdsMimeType = MimeType.TEXT_PLAIN.getType();
                xdsDocument = ((String) mimeMessage.getContent()).getBytes();

                List<ProvideAndRegisterDocumentSetRequestType> items = getRequests(subject, sentDate, from, recipients);
                requests.addAll(items);
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

                    if (StringUtils.contains(bodyPart.getFileName(), ".zip"))
                    {
                        try
                        {
                            LOGGER.info("Bodypart is an XDM request");

                            ProvideAndRegisterDocumentSetRequestType request = getXDMRequest(bodyPart);
                            requests.add(request);
                        }
                        catch (Exception x)
                        {
                            LOGGER.warn("Handling of assumed XDM request failed, skipping");
                        }

                        continue;
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

                        List<ProvideAndRegisterDocumentSetRequestType> items = getRequests(subject, mimeMessage
                                .getSentDate(), from, recipients);
                        requests.addAll(items);
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

                        List<ProvideAndRegisterDocumentSetRequestType> items = getRequests(subject, mimeMessage
                                .getSentDate(), from, recipients);
                        requests.addAll(items);
                    }
                    else
                    {
                        LOGGER.info("Did not match a type");

                        // Otherwise make best effort passing MIME content type

                        xdsFormatCode = FormatCodeEnum.TEXT.getValue();
                        xdsMimeType = bodyPart.getContentType();
                        xdsDocument = outputStream.toByteArray();

                        List<ProvideAndRegisterDocumentSetRequestType> items = getRequests(subject, mimeMessage
                                .getSentDate(), from, recipients);
                        requests.addAll(items);
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

        return requests;
    }

    /**
     * Get an XDM Request from a BodyPart object.
     * 
     * @param bodyPart
     *            The BodyPart object containing the XDM request.
     * @return a ProvideAndRegisterDocumentSetRequestType object.
     * @throws Exception
     */
    protected ProvideAndRegisterDocumentSetRequestType getXDMRequest(BodyPart bodyPart) throws Exception
    {
        LOGGER.trace("Inside getMDMRequest");

        DataHandler dh = bodyPart.getDataHandler();

        return xdmXdsTransformer.transform(dh);
    }

    /**
     * Create a list of ProvideAndRegisterDocumentSetRequestType objects from
     * the provided data for each of the provided recipients.
     * 
     * @param subject
     *            The message subject.
     * @param sentDate
     *            The message sent date.
     * @param auth
     *            The author of the document.
     * @param recipients
     *            The list of recipients to receive the XDS request.
     * @return a list of ProvideAndRegisterDocumentSetRequestType objects.
     */
    protected List<ProvideAndRegisterDocumentSetRequestType> getRequests(String subject, Date sentDate, String auth,
            Address[] recipients)
    {
        List<ProvideAndRegisterDocumentSetRequestType> requests = new ArrayList<ProvideAndRegisterDocumentSetRequestType>();

        for (Address recipient : recipients)
        {
            try
            {
                ProvideAndRegisterDocumentSetRequestType request = getRequest(subject, sentDate, auth, recipient
                        .toString());
                requests.add(request);
            }
            catch (Exception e)
            {
                if (LOGGER.isWarnEnabled())
                    LOGGER.warn("Error creating ProvideAndRegisterDocumentSetRequestType object, skipping", e);
            }
        }

        return requests;
    }

    /**
     * Create a single ProvideAndRegisterDocumentSetRequestType objects from the
     * provided data.
     * 
     * @param subject
     *            The message subject.
     * @param sentDate
     *            The message sent date.
     * @param auth
     *            The author of the document.
     * @param recip
     *            The recipient of the document.
     * @return a single ProvideAndRegisterDocumentSetRequestType object.
     * @throws Exception
     */
    protected ProvideAndRegisterDocumentSetRequestType getRequest(String subject, Date sentDate, String auth,
            String recip) throws Exception
    {
        ProvideAndRegisterDocumentSetRequestType prsr = new ProvideAndRegisterDocumentSetRequestType();

        // Parse CCD for patient info
        String sdoc = new String(xdsDocument);
        CcdParser cp = new CcdParser();
        cp.parse(sdoc);

        // Create patient object
        SimplePerson sourcePatient = new SimplePerson();
        sourcePatient.setLocalId(cp.getPatientId());
        sourcePatient.setLocalOrg(cp.getOrgId());

        // Build metadata
        DirectDocument.Metadata metadata = new DirectDocument.Metadata();

        metadata.setMimeType(xdsMimeType);
        metadata.setCreationTime(sentDate);
        metadata.setSourcePatient(sourcePatient);
        metadata.setAuthorPerson(auth);
        metadata.setAuthorInstitution(Arrays.asList(sourcePatient.getLocalOrg()));
        metadata.setAuthorRole(auth == null ? "System" : auth + "'s role");
        metadata.setClassCode(ClassCodeEnum.HISTORY_AND_PHYSICAL.getValue(), true);
        metadata.setFormatCode(xdsFormatCode, true);
        metadata.setHealthcareFacilityTypeCode(HealthcareFacilityTypeCodeEnum.OF.getValue(), true);
        metadata.setPracticeSettingCode(PracticeSettingCodeEnum.MULTIDISCIPLINARY.getValue(), true);
        metadata.setLoinc(LoincEnum.LOINC_34133_9.getValue(), true);
        metadata.setPatientId(sourcePatient.getLocalId() + "^^^&" + sourcePatient.getLocalOrg());
        metadata.setUniqueId(UUID.randomUUID().toString());

        metadata.setSs_submissionTime(sentDate);
        metadata.setSs_intendedRecipient("|" + recip + "^last^first^^^prefix^^^&amp;1.3.6.1.4.1.21367.3100.1&amp;ISO");
        metadata.setSs_authorPerson(auth);
        metadata.setSs_authorInstitution(Arrays.asList(sourcePatient.getLocalOrg()));
        metadata.setSs_authorRole(auth == null ? "System" : auth + "'s role");
        metadata.setContentTypeCode(subject, true);
        metadata.setSs_uniqueId(UUID.randomUUID().toString());
        metadata.setSs_sourceId(sourcePatient.getLocalOrg());
        metadata.setSs_patientId(sourcePatient.getLocalId() + "^^^&" + sourcePatient.getLocalOrg());

        prsr.setSubmitObjectsRequest(metadata.getSubmitObjectsRequest());

        // Build document
        DataSource source = new ByteArrayDataSource(xdsDocument, MimeType.APPLICATION_XML + "; charset=UTF-8");
        DataHandler dhnew = new DataHandler(source);

        List<Document> docs = prsr.getDocument();
        Document pdoc = new Document();

        pdoc.setValue(dhnew);
        pdoc.setId(metadata.getUniqueId());
        docs.add(pdoc);

        return prsr;
    }

}
