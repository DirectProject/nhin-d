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

package org.nhindirect.xd.common;

import static org.nhindirect.xd.common.DirectDocumentUtils.addSlot;
import static org.nhindirect.xd.common.DirectDocumentUtils.makeInternationalStringType;
import static org.nhindirect.xd.common.DirectDocumentUtils.makeSlot;
import static org.nhindirect.xd.common.DirectDocumentUtils.slotNotEmpty;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.activation.MimetypesFileTypeMap;
import javax.xml.namespace.QName;

import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExternalIdentifierType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.xd.common.exception.MetadataException;
import org.nhindirect.xd.common.type.ClassificationTypeEnum;
import org.nhindirect.xd.common.type.ExternalIdentifierTypeEnum;
import org.nhindirect.xd.common.type.ExtrinsicObjectTypeEnum;
import org.nhindirect.xd.common.type.FormatCodeEnum;
import org.nhindirect.xd.common.type.SlotType1Enum;
import org.nhindirect.xd.common.type.SubmitObjectsRequestEnum;
import org.nhindirect.xd.transform.pojo.SimplePerson;
import org.nhindirect.xd.transform.util.XmlUtils;

/**
 * Abstract representation of a document with supporting metadata.
 * 
 * TODO: This replace DirectDocument with this class.. wanted to wait to avoid
 * any merge conflicts for ongoing XD* work.
 * 
 * @author beau
 */
public class DirectDocument2
{
    private byte[] data;
    private Metadata metadata;

    private static final Log LOGGER = LogFactory.getFactory().getInstance(DirectDocument2.class);

    /**
     * Default document constructor.
     */
    public DirectDocument2()
    {
        this.metadata = new Metadata();
    }

    /**
     * @param file
     * @throws IOException
     */
    public DirectDocument2(File file) throws IOException
    {
        this.data = FileUtils.readFileToByteArray(file);
        this.metadata = new Metadata(file);
    }

    /**
     * Get the value of metadata.
     * 
     * @return the metadata.
     */
    public Metadata getMetadata()
    {
        return metadata;
    }

    /**
     * Set the value of metadata.
     * 
     * @param metadata
     *            The metadata to set.
     */
    public void setMetadata(Metadata metadata)
    {
        this.metadata = metadata;
    }

    /**
     * Get the value of data.
     * 
     * @return the data The value of data.
     */
    public byte[] getData()
    {
        return data;
    }

    /**
     * Set the value of data.
     * 
     * @param data
     *            The data to set;
     */
    public void setData(byte[] data)
    {
        this.data = data;
        
        this.metadata.setHash(getSha1Hash(data));
        this.metadata.setSize(new Long(data.length));
    }

    /**
     * Abstract representation of document metadata.
     * 
     * @author beau
     */
    static public class Metadata
    {
        private String mimeType;
        private String id;
        private String description;

        private Date creationTime;
        private String languageCode;
        private Date serviceStartTime;
        private Date serviceStopTime;
        
        private SimplePerson sourcePatient = new SimplePerson();
        
        private String authorPerson;
        private List<String> authorInstitution = new ArrayList<String>();
        private String authorRole;
        private String authorSpecialty;
        
        private String classCode;
        private String classCode_localized;

        private String confidentialityCode;
        private String confidentialityCode_localized;

        private String formatCode;
        private String formatCode_localized;

        private String healthcareFacilityTypeCode;
        private String healthcareFacilityTypeCode_localized;

        private String practiceSettingCode;
        private String practiceSettingCode_localized;

        private String loinc;
        private String loinc_localized;

        private String patientId;
        private String uniqueId;
        
        private String hash;
        private Long size;

        private String submissionSetStatus;

        private String uri;

        /**
         * Default constructor.
         */
        public Metadata()
        {
            this.id = UUID.randomUUID().toString();
            this.submissionSetStatus = "Original";
        }

        /**
         * Construct a new DirectDocument2.Metadata with default values given a
         * File object.
         * 
         * @param file
         *            A File object from which to extract metadata.
         */
        public Metadata(File file) throws IOException
        {
            super();
            
            MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
            
            mimetypesFileTypeMap.addMimeTypes("application/msword doc dot wiz rtf");
            mimetypesFileTypeMap.addMimeTypes("application/pdf pdf");
            mimetypesFileTypeMap.addMimeTypes("application/postscript ai eps ps");
            mimetypesFileTypeMap.addMimeTypes("application/vnd.ms-excel xls xlw xla xlc xlm xlt");
            mimetypesFileTypeMap.addMimeTypes("application/vnd.ms-powerpoint ppt pps pot");
            mimetypesFileTypeMap.addMimeTypes("application/x-javascript js");
            mimetypesFileTypeMap.addMimeTypes("application/x-asap asp");
            mimetypesFileTypeMap.addMimeTypes("application/x-latex latex");
            mimetypesFileTypeMap.addMimeTypes("application/x-tar tar");
            mimetypesFileTypeMap.addMimeTypes("application/x-texinfo texinfo texi");
            mimetypesFileTypeMap.addMimeTypes("application/zip zip");
            mimetypesFileTypeMap.addMimeTypes("text/css css");
            mimetypesFileTypeMap.addMimeTypes("text/html htm html");
            mimetypesFileTypeMap.addMimeTypes("text/plain txt");
            mimetypesFileTypeMap.addMimeTypes("text/richtext rtx");
            mimetypesFileTypeMap.addMimeTypes("text/xml xml");
            
            // Best guess at MIME type from list above
            this.mimeType = mimetypesFileTypeMap.getContentType(file);
            
            // File size and hash
            this.size = file.length();
            this.hash = getSha1Hash(FileUtils.readFileToString(file));
        }

        /**
         * @return
         */
        protected ExtrinsicObjectType generateExtrinsicObjectType()
        {
            ExtrinsicObjectType eot = new ExtrinsicObjectType();

            eot.setId(id);
            eot.setMimeType(mimeType);

            eot.setObjectType(ExtrinsicObjectTypeEnum.DOC.getObjectType());
            
            List<SlotType1> slots = eot.getSlot();
            addSlot(slots, makeSlot(SlotType1Enum.CREATION_TIME, creationTime != null ? (new SimpleDateFormat("yyyyMMdd")).format(creationTime) : null));
            addSlot(slots, makeSlot(SlotType1Enum.LANGUAGE_CODE, languageCode));
            addSlot(slots, makeSlot(SlotType1Enum.SERVICE_START_TIME, serviceStartTime != null ? (new SimpleDateFormat("yyyyMMddHHmm")).format(serviceStartTime) : null));
            addSlot(slots, makeSlot(SlotType1Enum.SERVICE_STOP_TIME, serviceStopTime != null ? (new SimpleDateFormat("yyyyMMddHHmm")).format(serviceStopTime) : null));
            addSlot(slots, makeSlot(SlotType1Enum.SOURCE_PATIENT_ID, sourcePatient.getLocalId() + "^^^&" + sourcePatient.getLocalOrg() + "&ISO"));
            addSlot(slots, makeSlot(SlotType1Enum.SOURCE_PATIENT_INFO, sourcePatient));
            addSlot(slots, makeSlot(SlotType1Enum.HASH, hash));
            addSlot(slots, makeSlot(SlotType1Enum.SIZE, size == null ? null : String.valueOf(size)));
            addSlot(slots, makeSlot(SlotType1Enum.URI, uri == null ? null : uri));

            eot.setName(makeInternationalStringType(classCode_localized));
            eot.setDescription(makeInternationalStringType(description));

            // author
            ClassificationType authorClassification = new ClassificationType();
            authorClassification.setClassifiedObject(id);
            authorClassification.setNodeRepresentation(""); // required empty string
            authorClassification.setId(ClassificationTypeEnum.DOC_AUTHOR.getClassificationId());
            authorClassification.setClassificationScheme(ClassificationTypeEnum.DOC_AUTHOR.getClassificationScheme());

            List<SlotType1> authorClassificationSlots = authorClassification.getSlot();
            addSlot(authorClassificationSlots, makeSlot(SlotType1Enum.AUTHOR_PERSON, authorPerson));
            addSlot(authorClassificationSlots, makeSlot(SlotType1Enum.AUTHOR_INSTITUTION, authorInstitution));
            addSlot(authorClassificationSlots, makeSlot(SlotType1Enum.AUTHOR_ROLE, authorRole));
            addSlot(authorClassificationSlots, makeSlot(SlotType1Enum.AUTHOR_SPECIALTY, authorSpecialty));

            eot.getClassification().add(authorClassification);

            // classCode
            if (classCode != null)
            {
                ClassificationType classCodeClassification = new ClassificationType();
                classCodeClassification.setClassifiedObject(id);
                classCodeClassification.setNodeRepresentation(classCode);
                classCodeClassification.setName(makeInternationalStringType(classCode_localized));
                classCodeClassification.setId(ClassificationTypeEnum.DOC_CLASS_CODE.getClassificationId());
                classCodeClassification.setClassificationScheme(ClassificationTypeEnum.DOC_CLASS_CODE.getClassificationScheme());
    
                List<SlotType1> classCodeClassificationSlots = classCodeClassification.getSlot();
                addSlot(classCodeClassificationSlots, makeSlot(SlotType1Enum.CODING_SCHEME, ClassificationTypeEnum.DOC_CLASS_CODE.getCodingScheme()));
    
                eot.getClassification().add(classCodeClassification);
            }

            // confidentialityCode
            if (confidentialityCode != null)
            {
                ClassificationType confidentialityCodeClassification = new ClassificationType();
                confidentialityCodeClassification.setClassifiedObject(id);
                confidentialityCodeClassification.setNodeRepresentation(confidentialityCode);
                confidentialityCodeClassification.setName(makeInternationalStringType(confidentialityCode_localized));
                confidentialityCodeClassification.setId(ClassificationTypeEnum.DOC_CONFIDENTIALITY_CODE.getClassificationId());
                confidentialityCodeClassification.setClassificationScheme(ClassificationTypeEnum.DOC_CONFIDENTIALITY_CODE.getClassificationScheme());
    
                List<SlotType1> confidentialityCodeClassificationSlots = confidentialityCodeClassification.getSlot();
                addSlot(confidentialityCodeClassificationSlots, makeSlot(SlotType1Enum.CODING_SCHEME, ClassificationTypeEnum.DOC_CONFIDENTIALITY_CODE.getCodingScheme()));
    
                eot.getClassification().add(confidentialityCodeClassification);
            }

            // formatCode
            if (formatCode != null)
            {
                ClassificationType formatCodeClassification = new ClassificationType();
                formatCodeClassification.setClassifiedObject(id);
                formatCodeClassification.setNodeRepresentation(formatCode);
                formatCodeClassification.setName(makeInternationalStringType(formatCode_localized));
                formatCodeClassification.setId(ClassificationTypeEnum.DOC_FORMAT_CODE.getClassificationId());
                formatCodeClassification.setClassificationScheme(ClassificationTypeEnum.DOC_FORMAT_CODE.getClassificationScheme());
    
                List<SlotType1> formatCodeClassificationSlots = formatCodeClassification.getSlot();
                addSlot(formatCodeClassificationSlots, makeSlot(SlotType1Enum.CODING_SCHEME, ClassificationTypeEnum.DOC_FORMAT_CODE.getCodingScheme()));
    
                eot.getClassification().add(formatCodeClassification);
            }

            // healthcareFacilityTypeCode
            if (healthcareFacilityTypeCode != null)
            {
                ClassificationType healthcareFacilityTypeCodeClassification = new ClassificationType();
                healthcareFacilityTypeCodeClassification.setClassifiedObject(id);
                healthcareFacilityTypeCodeClassification.setNodeRepresentation(healthcareFacilityTypeCode);
                healthcareFacilityTypeCodeClassification.setName(makeInternationalStringType(healthcareFacilityTypeCode_localized));
                healthcareFacilityTypeCodeClassification.setId(ClassificationTypeEnum.DOC_HEALTHCARE_FACILITY_TYPE_CODE.getClassificationId());
                healthcareFacilityTypeCodeClassification.setClassificationScheme(ClassificationTypeEnum.DOC_HEALTHCARE_FACILITY_TYPE_CODE.getClassificationScheme());
    
                List<SlotType1> healthcareFacilityTypeCodeClassificationSlots = healthcareFacilityTypeCodeClassification.getSlot();
                addSlot(healthcareFacilityTypeCodeClassificationSlots, makeSlot(SlotType1Enum.CODING_SCHEME, ClassificationTypeEnum.DOC_HEALTHCARE_FACILITY_TYPE_CODE.getCodingScheme()));
    
                eot.getClassification().add(healthcareFacilityTypeCodeClassification);
            }

            // practiceSettingCode
            if (practiceSettingCode != null)
            {
                ClassificationType practiceSettingCodeClassification = new ClassificationType();
                practiceSettingCodeClassification.setClassifiedObject(id);
                practiceSettingCodeClassification.setNodeRepresentation(practiceSettingCode);
                practiceSettingCodeClassification.setName(makeInternationalStringType(practiceSettingCode_localized));
                practiceSettingCodeClassification.setId(ClassificationTypeEnum.DOC_PRACTICE_SETTING_CODE.getClassificationId());
                practiceSettingCodeClassification.setClassificationScheme(ClassificationTypeEnum.DOC_PRACTICE_SETTING_CODE.getClassificationScheme());
    
                List<SlotType1> practiceSettingCodeClassificationSlots = practiceSettingCodeClassification.getSlot();
                addSlot(practiceSettingCodeClassificationSlots, makeSlot(SlotType1Enum.CODING_SCHEME, ClassificationTypeEnum.DOC_PRACTICE_SETTING_CODE.getCodingScheme()));
    
                eot.getClassification().add(practiceSettingCodeClassification);
            }

            // loinc
            if (loinc != null)
            {
                ClassificationType loincClassification = new ClassificationType();
                loincClassification.setClassifiedObject(id);
                loincClassification.setNodeRepresentation(loinc);
                loincClassification.setName(makeInternationalStringType(loinc_localized));
                loincClassification.setId(ClassificationTypeEnum.DOC_LOINC.getClassificationId());
                loincClassification.setClassificationScheme(ClassificationTypeEnum.DOC_LOINC.getClassificationScheme());
    
                List<SlotType1> loincClassificationSlots = loincClassification.getSlot();
                addSlot(loincClassificationSlots, makeSlot(SlotType1Enum.CODING_SCHEME, ClassificationTypeEnum.DOC_LOINC.getCodingScheme()));
    
                eot.getClassification().add(loincClassification);
            }

            // patientId
            ExternalIdentifierType xdsDocumentEntry_patientId = new ExternalIdentifierType();
            xdsDocumentEntry_patientId.setValue(patientId);
            xdsDocumentEntry_patientId.setRegistryObject(id);
            xdsDocumentEntry_patientId.setId(ExternalIdentifierTypeEnum.DOC_PATIENT_ID.getIdentificationId());
            xdsDocumentEntry_patientId.setIdentificationScheme(ExternalIdentifierTypeEnum.DOC_PATIENT_ID.getIdentificationScheme());
            xdsDocumentEntry_patientId.setName(makeInternationalStringType(ExternalIdentifierTypeEnum.DOC_PATIENT_ID.getLocalizedString()));

            eot.getExternalIdentifier().add(xdsDocumentEntry_patientId);

            // uniqueId
            ExternalIdentifierType xdsDocumentEntry_uniqueId = new ExternalIdentifierType();
            xdsDocumentEntry_uniqueId.setValue(uniqueId);
            xdsDocumentEntry_uniqueId.setRegistryObject(id);
            xdsDocumentEntry_uniqueId.setId(ExternalIdentifierTypeEnum.DOC_UNIQUE_ID.getIdentificationId());
            xdsDocumentEntry_uniqueId.setIdentificationScheme(ExternalIdentifierTypeEnum.DOC_UNIQUE_ID.getIdentificationScheme());
            xdsDocumentEntry_uniqueId.setName(makeInternationalStringType(ExternalIdentifierTypeEnum.DOC_UNIQUE_ID.getLocalizedString()));

            eot.getExternalIdentifier().add(xdsDocumentEntry_uniqueId);

            return eot;
        }

        public void setValues(ExtrinsicObjectType eot) throws MetadataException
        {
            mimeType = eot.getMimeType();
            id = eot.getId();

            if (eot.getDescription() != null && eot.getDescription().getLocalizedString() != null && !eot.getDescription().getLocalizedString().isEmpty())
                description = eot.getDescription().getLocalizedString().get(0).getValue();

            for (SlotType1 slot : eot.getSlot())
            {
                if (SlotType1Enum.CREATION_TIME.matches(slot.getName()))
                {
                    if (slotNotEmpty(slot))
                    {
                        try
                        {
                            creationTime = DateUtils.parseDate(slot.getValueList().getValue().get(0),
                                    new String[]
                                    { "yyyyMMddHHmmss", "yyyyMMddHHmm", "yyyyMMdd" });
                        }
                        catch (ParseException e)
                        {
                            LOGGER.error("Unable to parse creationTime", e);
                            throw new MetadataException("Unable to parse creationTime", e);
                        }
                    }
                }
                else if (SlotType1Enum.LANGUAGE_CODE.matches(slot.getName()))
                {
                    if (slotNotEmpty(slot))
                        languageCode = slot.getValueList().getValue().get(0);
                }
                else if (SlotType1Enum.SERVICE_START_TIME.matches(slot.getName()))
                {
                    if (slotNotEmpty(slot))
                    {
                        try
                        {
                            serviceStartTime = DateUtils.parseDate(slot.getValueList().getValue().get(0),
                                    new String[]
                                    { "yyyyMMddHHmmss", "yyyyMMddHHmm", "yyyyMMdd" });
                        }
                        catch (ParseException e)
                        {
                            LOGGER.error("Unable to parse serviceStartTime", e);
                            throw new MetadataException("Unable to parse serviceStartTime", e);
                        }
                    }
                }
                else if (SlotType1Enum.SERVICE_STOP_TIME.matches(slot.getName()))
                {
                    if (slotNotEmpty(slot))
                    {
                        try
                        {
                            serviceStopTime = DateUtils.parseDate(slot.getValueList().getValue().get(0),
                                    new String[]
                                    { "yyyyMMddHHmmss", "yyyyMMddHHmm", "yyyyMMdd" });
                        }
                        catch (ParseException e)
                        {
                            LOGGER.error("Unable to parse serviceStopTime", e);
                            throw new MetadataException("Unable to parse serviceStopTime", e);
                        }
                    }
                }
                else if (SlotType1Enum.SOURCE_PATIENT_ID.matches(slot.getName()))
                {
                    if (slotNotEmpty(slot))
                    {
                        String[] tokens = StringUtils.splitPreserveAllTokens(slot.getValueList().getValue().get(0), "^");

                        if (tokens != null && tokens.length >= 1)
                            sourcePatient.setLocalId(tokens[0]);
                        else
                            sourcePatient.setLocalId(slot.getValueList().getValue().get(0));

                        if (tokens != null && tokens.length >= 4)
                        {
                            tokens = StringUtils.splitPreserveAllTokens(slot.getValueList().getValue().get(0), "&");

                            if (tokens.length >= 2)
                                sourcePatient.setLocalOrg(tokens[1]);
                        }
                    }
                }
                else if (SlotType1Enum.SOURCE_PATIENT_INFO.matches(slot.getName()))
                
                {
                    if (slotNotEmpty(slot))
                    {
                        for (String value : slot.getValueList().getValue())
                        {
                            if (StringUtils.startsWith(value, "PID-3|"))
                            {
                                // Already have this from sourcePatientId
                            }
                            else if (StringUtils.startsWith(value, "PID-5|"))
                            {
                                String[] split = StringUtils.splitPreserveAllTokens(value, "|");
                                String[] tokens = StringUtils.splitPreserveAllTokens(split[1], "^");

                                if (tokens != null && tokens.length >= 1)
                                    sourcePatient.setLastName(tokens[0]);

                                if (tokens != null && tokens.length >= 2)
                                    sourcePatient.setFirstName(tokens[1]);

                                if (tokens != null && tokens.length >= 3)
                                    sourcePatient.setMiddleName(tokens[2]);
                            }
                            else if (StringUtils.startsWith(value, "PID-7|"))
                            {
                                String[] split = StringUtils.splitPreserveAllTokens(value, "|");

                                if (split.length >= 2)
                                    sourcePatient.setBirthDateTime(split[1]);
                            }
                            else if (StringUtils.startsWith(value, "PID-8|"))
                            {
                                String[] split = StringUtils.splitPreserveAllTokens(value, "|");

                                if (split.length >= 2)
                                    sourcePatient.setGenderCode(split[1]);
                            }
                            else if (StringUtils.startsWith(value, "PID-11|"))
                            {
                                String[] split = StringUtils.splitPreserveAllTokens(value, "|");
                                String[] tokens = StringUtils.splitPreserveAllTokens(split[1], "^");

                                if (tokens != null && tokens.length >= 1)
                                    sourcePatient.setStreetAddress1(tokens[0]);

                                if (tokens != null && tokens.length >= 3)
                                    sourcePatient.setCity(tokens[2]);

                                if (tokens != null && tokens.length >= 4)
                                    sourcePatient.setState(tokens[3]);

                                if (tokens != null && tokens.length >= 5)
                                    sourcePatient.setZipCode(tokens[4]);

                                if (tokens != null && tokens.length >= 6)
                                    sourcePatient.setCountry(tokens[5]);
                            }
                        }
                    }
                }
                else if (SlotType1Enum.HASH.matches(slot.getName()))
                {
                    if (slotNotEmpty(slot))
                        hash = slot.getValueList().getValue().get(0);
                }
                else if (SlotType1Enum.URI.matches(slot.getName()))
                {
                    if (slotNotEmpty(slot))
                        uri = slot.getValueList().getValue().get(0);
                }
                else if (SlotType1Enum.SIZE.matches(slot.getName()))
                {
                    if (slotNotEmpty(slot))
                        size = Long.valueOf(slot.getValueList().getValue().get(0));
                }
            }

            for (ClassificationType ct : eot.getClassification())
            {
                if (ClassificationTypeEnum.DOC_AUTHOR.matchesScheme(ct.getClassificationScheme()))
                {
                    for (SlotType1 slot : ct.getSlot())
                    {
                        if (SlotType1Enum.AUTHOR_PERSON.matches(slot.getName()))
                        {
                            if (slotNotEmpty(slot))
                                authorPerson = slot.getValueList().getValue().get(0);
                        }
                        else if (SlotType1Enum.AUTHOR_INSTITUTION.matches(slot.getName()))
                        {
                            if (slotNotEmpty(slot))
                                for (String value : slot.getValueList().getValue())
                                    authorInstitution.add(value);
                        }
                        else if (SlotType1Enum.AUTHOR_ROLE.matches(slot.getName()))
                        {
                            if (slotNotEmpty(slot))
                                authorRole = slot.getValueList().getValue().get(0);
                        }
                        else if (SlotType1Enum.AUTHOR_SPECIALTY.matches(slot.getName()))
                        {
                            if (slotNotEmpty(slot))
                                authorSpecialty = slot.getValueList().getValue().get(0);
                        }
                    }
                }
                else if (ClassificationTypeEnum.DOC_CLASS_CODE.matchesScheme(ct.getClassificationScheme()))
                {
                    for (SlotType1 slot : ct.getSlot())
                    {
                        if (SlotType1Enum.CODING_SCHEME.matches(slot.getName()))
                        {
                            if (slotNotEmpty(slot))
                            {
                                @SuppressWarnings("unused") String codingScheme = slot.getValueList().getValue().get(0);
                            }
                        }
                    }

                    classCode = ct.getNodeRepresentation();

                    if (ct.getName() != null && ct.getName().getLocalizedString() != null && !ct.getName().getLocalizedString().isEmpty())
                        classCode_localized = ct.getName().getLocalizedString().get(0).getValue();
                }
                else if (ClassificationTypeEnum.DOC_CONFIDENTIALITY_CODE.matchesScheme(ct.getClassificationScheme()))
                {
                    for (SlotType1 slot : ct.getSlot())
                    {
                        if (SlotType1Enum.CODING_SCHEME.matches(slot.getName()))
                        {
                            if (slotNotEmpty(slot))
                            {
                                @SuppressWarnings("unused") String codingScheme = slot.getValueList().getValue().get(0);
                            }
                        }
                    }

                    confidentialityCode = ct.getNodeRepresentation();

                    if (ct.getName() != null && ct.getName().getLocalizedString() != null && !ct.getName().getLocalizedString().isEmpty())
                        confidentialityCode_localized = ct.getName().getLocalizedString().get(0).getValue();
                }
                else if (ClassificationTypeEnum.DOC_FORMAT_CODE.matchesScheme(ct.getClassificationScheme()))
                {
                    for (SlotType1 slot : ct.getSlot())
                    {
                        if (SlotType1Enum.CODING_SCHEME.matches(slot.getName()))
                        {
                            if (slotNotEmpty(slot))
                            {
                                @SuppressWarnings("unused") String codingScheme = slot.getValueList().getValue().get(0);
                            }
                        }
                    }

                    formatCode = ct.getNodeRepresentation();

                    if (ct.getName() != null && ct.getName().getLocalizedString() != null && !ct.getName().getLocalizedString().isEmpty())
                        formatCode_localized = ct.getName().getLocalizedString().get(0).getValue();
                }
                else if (ClassificationTypeEnum.DOC_HEALTHCARE_FACILITY_TYPE_CODE.matchesScheme(ct.getClassificationScheme()))
                {
                    for (SlotType1 slot : ct.getSlot())
                    {
                        if (SlotType1Enum.CODING_SCHEME.matches(slot.getName()))
                        {
                            if (slotNotEmpty(slot))
                            {
                                @SuppressWarnings("unused") String codingScheme = slot.getValueList().getValue().get(0);
                            }
                        }
                    }

                    healthcareFacilityTypeCode = ct.getNodeRepresentation();

                    if (ct.getName() != null && ct.getName().getLocalizedString() != null && !ct.getName().getLocalizedString().isEmpty())
                        healthcareFacilityTypeCode_localized = ct.getName().getLocalizedString().get(0).getValue();
                }
                else if (ClassificationTypeEnum.DOC_PRACTICE_SETTING_CODE.matchesScheme(ct.getClassificationScheme()))
                {
                    for (SlotType1 slot : ct.getSlot())
                    {
                        if (SlotType1Enum.CODING_SCHEME.matches(slot.getName()))
                        {
                            if (slotNotEmpty(slot))
                            {
                                @SuppressWarnings("unused") String codingScheme = slot.getValueList().getValue().get(0);
                            }
                        }
                    }

                    practiceSettingCode = ct.getNodeRepresentation();

                    if (ct.getName() != null && ct.getName().getLocalizedString() != null && !ct.getName().getLocalizedString().isEmpty())
                        practiceSettingCode_localized = ct.getName().getLocalizedString().get(0).getValue();
                }
                else if (ClassificationTypeEnum.DOC_LOINC.matchesScheme(ct.getClassificationScheme()))
                {
                    for (SlotType1 slot : ct.getSlot())
                    {
                        if (SlotType1Enum.CODING_SCHEME.matches(slot.getName()))
                        {
                            if (slotNotEmpty(slot))
                            {
                                @SuppressWarnings("unused") String codingScheme = slot.getValueList().getValue().get(0);
                            }
                        }
                    }

                    loinc = ct.getNodeRepresentation();

                    if (ct.getName() != null && ct.getName().getLocalizedString() != null && !ct.getName().getLocalizedString().isEmpty())
                        loinc_localized = ct.getName().getLocalizedString().get(0).getValue();
                }
            }

            for (ExternalIdentifierType eit : eot.getExternalIdentifier())
            {
                if (ExternalIdentifierTypeEnum.DOC_PATIENT_ID.matchesScheme(eit.getIdentificationScheme()))
                {
                    patientId = eit.getValue();
                }
                else if (ExternalIdentifierTypeEnum.DOC_UNIQUE_ID.matchesScheme(eit.getIdentificationScheme()))
                {
                    uniqueId = eit.getValue();
                }
            }
        }
        
        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            QName qname = new QName(SubmitObjectsRequestEnum.EXTRINSIC_OBJECT.getNamespaceUri(), SubmitObjectsRequestEnum.EXTRINSIC_OBJECT.getName());
            return XmlUtils.marshal(qname, generateExtrinsicObjectType(), ihe.iti.xds_b._2007.ObjectFactory.class);
        }

        /**
         * @return the mimeType
         */
        public String getMimeType()
        {
            return mimeType;
        }

        /**
         * @param mimeType
         *            the mimeType to set
         */
        public void setMimeType(String mimeType)
        {
            this.mimeType = mimeType;
        }

        /**
         * @return the id
         */
        public String getId()
        {
            return id;
        }
        
        /**
         * @param id
         *            The id to set.
         */
        public void setId(String id)
        {
            this.id = id;
        }

        /**
         * @return the description
         */
        public String getDescription()
        {
            return description;
        }

        /**
         * @param description
         *            the description to set
         */
        public void setDescription(String description)
        {
            this.description = description;
        }
        
        /**
         * @return the creationTime
         */
        public Date getCreationTime()
        {
            return creationTime;
        }

        /**
         * @param creationTime
         *            the creationTime to set
         */
        public void setCreationTime(Date creationTime)
        {
            this.creationTime = creationTime;
        }

        /**
         * @return the languageCode
         */
        public String getLanguageCode()
        {
            return languageCode;
        }

        /**
         * @param languageCode
         *            the languageCode to set
         */
        public void setLanguageCode(String languageCode)
        {
            this.languageCode = languageCode;
        }

        /**
         * @return the serviceStartTime
         */
        public Date getServiceStartTime()
        {
            return serviceStartTime;
        }

        /**
         * @param serviceStartTime
         *            the serviceStartTime to set
         */
        public void setServiceStartTime(Date serviceStartTime)
        {
            this.serviceStartTime = serviceStartTime;
        }

        /**
         * @return the serviceStopTime
         */
        public Date getServiceStopTime()
        {
            return serviceStopTime;
        }

        /**
         * @param serviceStopTime
         *            the serviceStopTime to set
         */
        public void setServiceStopTime(Date serviceStopTime)
        {
            this.serviceStopTime = serviceStopTime;
        }

        /**
         * @return the sourcePatient
         */
        public SimplePerson getSourcePatient()
        {
            return sourcePatient;
        }

        /**
         * @param sourcePatient
         *            the sourcePatient to set
         */
        public void setSourcePatient(SimplePerson sourcePatient)
        {
            this.sourcePatient = sourcePatient;
        }

        /**
         * @return the authorPerson
         */
        public String getAuthorPerson()
        {
            return authorPerson;
        }

        /**
         * @param authorPerson
         *            the authorPerson to set
         */
        public void setAuthorPerson(String authorPerson)
        {
            this.authorPerson = authorPerson;
        }

        /**
         * @return the authorInstitution
         */
        public List<String> getAuthorInstitution()
        {
            return authorInstitution;
        }

        /**
         * @param authorInstitution
         *            the authorInstitution to set
         */
        public void setAuthorInstitution(List<String> authorInstitution)
        {
            this.authorInstitution = authorInstitution;
        }

        /**
         * @return the authorRole
         */
        public String getAuthorRole()
        {
            return authorRole;
        }

        /**
         * @param authorRole
         *            the authorRole to set
         */
        public void setAuthorRole(String authorRole)
        {
            this.authorRole = authorRole;
        }

        /**
         * @return the authorSpecialty
         */
        public String getAuthorSpecialty()
        {
            return authorSpecialty;
        }

        /**
         * @param authorSpecialty
         *            the authorSpecialty to set
         */
        public void setAuthorSpecialty(String authorSpecialty)
        {
            this.authorSpecialty = authorSpecialty;
        }

        /**
         * @return the classCode
         */
        public String getClassCode()
        {
            return classCode;
        }

        /**
         * @param classCode
         *            the classCode to set
         */
        public void setClassCode(String classCode)
        {
            setClassCode(classCode, false);
        }

        /**
         * @param classCode
         *            the classCode to set
         * @param setLocalized
         *            whether or not to set the localized field with the same
         *            value
         */
        public void setClassCode(String classCode, boolean setLocalized)
        {
            this.classCode = classCode;
            
            if (setLocalized)
                this.classCode_localized = classCode;
        }
        
        /**
         * @return the classCode_localized
         */
        public String getClassCode_localized()
        {
            return classCode_localized;
        }

        /**
         * @param classCodeLocalized
         *            the classCode_localized to set
         */
        public void setClassCode_localized(String classCodeLocalized)
        {
            classCode_localized = classCodeLocalized;
        }

        /**
         * @return the confidentialityCode
         */
        public String getConfidentialityCode()
        {
            return confidentialityCode;
        }

        /**
         * @param confidentialityCode
         *            the confidentialityCode to set
         */
        public void setConfidentialityCode(String confidentialityCode)
        {
            setConfidentialityCode(confidentialityCode, false);
        }

        /**
         * @param confidentialityCode
         *            the confidentialityCode to set
         * @param setLocalized
         *            whether or not to set the localized field with the same
         *            value
         */
        public void setConfidentialityCode(String confidentialityCode, boolean setLocalized)
        {
            this.confidentialityCode = confidentialityCode;
            
            if (setLocalized)
                this.confidentialityCode_localized = confidentialityCode;
        }
        
        /**
         * @return the confidentialityCode_localized
         */
        public String getConfidentialityCode_localized()
        {
            return confidentialityCode_localized;
        }

        /**
         * @param confidentialityCodeLocalized
         *            the confidentialityCode_localized to set
         */
        public void setConfidentialityCode_localized(String confidentialityCodeLocalized)
        {
            confidentialityCode_localized = confidentialityCodeLocalized;
        }

        /**
         * @return the formatCode
         */
        public String getFormatCode()
        {
            return formatCode;
        }

        /**
         * @param formatCode
         *            the formatCode to set
         */
        public void setFormatCode(String formatCode)
        {
            setFormatCode(formatCode, false);
        }

        /**
         * @param formatCode
         *            the formatCode to set
         * @param setLocalized
         *            whether or not to set the localized field with the same
         *            value
         */
        public void setFormatCode(String formatCode, boolean setLocalized)
        {
            this.formatCode = formatCode;
            
            if (setLocalized)
                formatCode_localized = formatCode;
        }
        
        /**
         * @param formatCode
         *            the formatCode to set
         */
        public void setFormatCode(FormatCodeEnum formatCode)
        {
            if (formatCode != null) {
                setFormatCode(formatCode.getConceptCode());
                setFormatCode_localized(formatCode.getConceptName());
            }
        }
        
        /**
         * @return the formatCode_localized
         */
        public String getFormatCode_localized()
        {
            return formatCode_localized;
        }

        /**
         * @param formatCodeLocalized
         *            the formatCode_localized to set
         */
        public void setFormatCode_localized(String formatCodeLocalized)
        {
            formatCode_localized = formatCodeLocalized;
        }
        
        /**
         * @return the healthcareFacilityTypeCode
         */
        public String getHealthcareFacilityTypeCode()
        {
            return healthcareFacilityTypeCode;
        }

        /**
         * @param healthcareFacilityTypeCode
         *            the healthcareFacilityTypeCode to set
         */
        public void setHealthcareFacilityTypeCode(String healthcareFacilityTypeCode)
        {
            setHealthcareFacilityTypeCode(healthcareFacilityTypeCode, false);
        }
        
        /**
         * @param healthcareFacilityTypeCode
         *            the healthcareFacilityTypeCode to set
         * @param setLocalized
         *            whether or not to set the localized field with the same
         *            value
         */
        public void setHealthcareFacilityTypeCode(String healthcareFacilityTypeCode, boolean setLocalized)
        {
            this.healthcareFacilityTypeCode = healthcareFacilityTypeCode;
            
            if (setLocalized)
                this.healthcareFacilityTypeCode_localized = healthcareFacilityTypeCode;
        }

        /**
         * @return the healthcareFacilityTypeCode_localized
         */
        public String getHealthcareFacilityTypeCode_localized()
        {
            return healthcareFacilityTypeCode_localized;
        }

        /**
         * @param healthcareFacilityTypeCodeLocalized
         *            the healthcareFacilityTypeCode_localized to set
         */
        public void setHealthcareFacilityTypeCode_localized(String healthcareFacilityTypeCodeLocalized)
        {
            healthcareFacilityTypeCode_localized = healthcareFacilityTypeCodeLocalized;
        }

        /**
         * @return the practiceSettingCode
         */
        public String getPracticeSettingCode()
        {
            return practiceSettingCode;
        }

        /**
         * @param practiceSettingCode
         *            the practiceSettingCode to set
         */
        public void setPracticeSettingCode(String practiceSettingCode)
        {
            setPracticeSettingCode(practiceSettingCode, false);
        }
        
        /**
         * @param practiceSettingCode
         *            the practiceSettingCode to set
         * @param setLocalized
         *            whether or not to set the localized field with the same
         *            value  
         */
        public void setPracticeSettingCode(String practiceSettingCode, boolean setLocalized)
        {
            this.practiceSettingCode = practiceSettingCode;
            
            if (setLocalized)
                this.practiceSettingCode_localized = practiceSettingCode;
        }

        /**
         * @return the practiceSettingCode_localized
         */
        public String getPracticeSettingCode_localized()
        {
            return practiceSettingCode_localized;
        }

        /**
         * @param practiceSettingCodeLocalized
         *            the practiceSettingCode_localized to set
         */
        public void setPracticeSettingCode_localized(String practiceSettingCodeLocalized)
        {
            practiceSettingCode_localized = practiceSettingCodeLocalized;
        }

        /**
         * @return the loinc
         */
        public String getLoinc()
        {
            return loinc;
        }

        /**
         * @param loinc
         *            the loinc to set
         */
        public void setLoinc(String loinc)
        {
            setLoinc(loinc, false);
        }

        /**
         * @param loinc
         *            the loinc to set
         * @param setLocalized
         *            whether or not to set the localized field with the same
         *            value  
         */
        public void setLoinc(String loinc, boolean setLocalized)
        {
            this.loinc = loinc;
            
            if (setLocalized)
                this.loinc_localized = loinc;
        }
        
        /**
         * @return the loinc_localized
         */
        public String getLoinc_localized()
        {
            return loinc_localized;
        }

        /**
         * @param loincLocalized
         *            the loinc_localized to set
         */
        public void setLoinc_localized(String loincLocalized)
        {
            loinc_localized = loincLocalized;
        }

        /**
         * @return the patientId
         */
        public String getPatientId()
        {
            return patientId;
        }

        /**
         * @param patientId
         *            the patientId to set
         */
        public void setPatientId(String patientId)
        {
            this.patientId = patientId;
        }

        /**
         * @return the uniqueId
         */
        public String getUniqueId()
        {
            return uniqueId;
        }

        /**
         * @param uniqueId
         *            the uniqueId to set
         */
        public void setUniqueId(String uniqueId)
        {
            this.uniqueId = uniqueId;
        }

        /**
         * @return the submissionSetStatus
         */
        public String getSubmissionSetStatus()
        {
            return submissionSetStatus;
        }

        /**
         * @param submissionSetStatus
         *            the submissionSetStatus to set
         */
        public void setSubmissionSetStatus(String submissionSetStatus)
        {
            this.submissionSetStatus = submissionSetStatus;
        }

        /**
         * @return the hash
         */
        public String getHash()
        {
            return hash;
        }

        /**
         * @param hash
         *            the hash to set
         */
        public void setHash(String hash)
        {
            if (StringUtils.isNotEmpty(this.hash) && !StringUtils.equalsIgnoreCase(this.hash, hash))
                LOGGER.warn("Replacing existing value with new value");
            
            this.hash = hash;
        }

        /**
         * @return the size
         */
        public Long getSize()
        {
            return size;
        }

        /**
         * @param size
         *            the size to set
         */
        public void setSize(Long size)
        {
            if (this.size != null && !this.size.equals(size))
                LOGGER.warn("Replacing existing size with new value");
            
            this.size = size;
        }
         /**
         * @return the hash
         */
        public String getURI()
        {
            return uri;
        }

        /**
         * @param hash
         *            the hash to set
         */
        public void setURI(String uri)
        {
            if (StringUtils.isNotEmpty(this.uri) && !StringUtils.equalsIgnoreCase(this.uri, uri))
                LOGGER.warn("Replacing existing value with new value");

            this.uri = uri;
        }
    }
    
    /**
     * Calculate the SHA-1 hash for the provided array of bytes.
     * 
     * @param bytes
     *            Bytes from which to calculate the SHA-1 hash.
     * @return the SHA-1 hash or null if unable to calculate.
     */
    public static String getSha1Hash(byte[] bytes)
    {
        return getSha1Hash(new String(bytes));
    }

    /**
     * Calculate the SHA-1 hash for the provided string.
     * 
     * @param string
     *            The string from which to calculate the SHA-1 hash.
     * @return the SHA-1 hash or null if unable to calculate.
     */
    public static String getSha1Hash(String string)
    {
        MessageDigest messageDigest = null;

        try
        {
            messageDigest = MessageDigest.getInstance("SHA-1");
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.error("Unable to calculate hash, returning null.", e);
            return null;
        }
        
        messageDigest.update(string.getBytes(), 0, string.length());
        byte[] sha1hash = messageDigest.digest();
        char[]hex = Hex.encodeHex(sha1hash);
        String newret = new String(hex);
        //BigInteger bigInt = new BigInteger(sha1hash);
        //return bigInt.toString(16);
        return newret;
    }
}
