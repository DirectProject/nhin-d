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

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.AssociationType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExternalIdentifierType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.IdentifiableType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.InternationalStringType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.LocalizedStringType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryPackageType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ValueListType;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.xd.common.exception.MetadataException;
import org.nhindirect.xd.transform.pojo.SimplePerson;
import org.nhindirect.xd.transform.util.XmlUtils;
import org.nhindirect.xd.transform.util.type.ClassificationTypeEnum;
import org.nhindirect.xd.transform.util.type.ExternalIdentifierTypeEnum;
import org.nhindirect.xd.transform.util.type.SlotType1Enum;

/**
 * Abstract representation of a document with supporting metadata.
 * 
 * @author beau
 */
public class DirectDocument
{
    private String data;
    private Metadata metadata;

    private static final Log LOGGER = LogFactory.getFactory().getInstance(DirectDocument.class);

    /**
     * Default document constructor.
     */
    public DirectDocument()
    {
        this.metadata = new Metadata();
    }

    public DirectDocument(File file) throws IOException
    {
        this.data = FileUtils.readFileToString(file);
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
    public String getData()
    {
        return data;
    }

    /**
     * Set the value of data.
     * 
     * @param data
     *            The data to set;
     */
    public void setData(String data)
    {
        this.data = data;
    }

    /**
     * Abstract representation of document metadata.
     * 
     * @author beau
     */
    static public class Metadata
    {
        private String mimeType;
        private String _eot_id;
        private String _eot_description;

        private Date creationTime;
        private String languageCode;
        private Date serviceStartTime;
        private Date serviceStopTime;
        
        private SimplePerson sourcePatient = new SimplePerson();
        
        private String authorPerson;
        private String authorInstitution;
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

        private String _rpt_id;
        private String _rpt_name;
        private String _rpt_description;

        private Date ss_submissionTime;
        private String ss_intendedRecipient;

        private String ss_authorPerson;
        private String ss_authorInstitution;
        private String ss_authorRole;
        private String ss_authorSpecialty;

        private String contentTypeCode;
        private String contentTypeCode_localized;

        private String ss_uniqueId;
        private String ss_sourceId;
        private String ss_patientId;

        private String submissionSetStatus;

        /**
         * Default constructor.
         */
        public Metadata()
        {
            this._eot_id = "Document01";
            this._rpt_id = "SubmissionSet01";
        }

        /**
         * Construct a new DirectDocument.Metadata with default values given a
         * File object.
         * 
         * @param file
         *            A File object from which to extract metadata.
         */
        public Metadata(File file)
        {
            // TODO add a mime.types file
            this.mimeType = new MimetypesFileTypeMap().getContentType(file);
        }

        /**
         * Get the metadata represented as a SubmitObjectsRequest object.
         */
        public SubmitObjectsRequest getSubmitObjectsRequest()
        {
            ExtrinsicObjectType extrinsicObjectType = generateExtrinsicObjectType();
            RegistryPackageType registryPackageType = generateRegistryPackageType();
            ClassificationType classificationType = generateClassificationType();
            AssociationType1 associationType = generateAssociationType();

            QName qname = null;

            qname = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "ExtrinsicObject");
            JAXBElement<ExtrinsicObjectType> jaxb_extrinsicObjectType = new JAXBElement<ExtrinsicObjectType>(qname, ExtrinsicObjectType.class, extrinsicObjectType);

            qname = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "RegistryPackage");
            JAXBElement<RegistryPackageType> jaxb_registryPackageType = new JAXBElement<RegistryPackageType>(qname, RegistryPackageType.class, registryPackageType);

            qname = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "Classification");
            JAXBElement<ClassificationType> jaxb_classificationType = new JAXBElement<ClassificationType>(qname, ClassificationType.class, classificationType);

            qname = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "Association");
            JAXBElement<AssociationType1> jaxb_AssociationType = new JAXBElement<AssociationType1>(qname, AssociationType1.class, associationType);

            SubmitObjectsRequest submitObjectsRequest = new SubmitObjectsRequest();
            RegistryObjectListType registryObjectListType = new RegistryObjectListType();

            List<JAXBElement<? extends IdentifiableType>> elements = registryObjectListType.getIdentifiable();
            elements.add(jaxb_extrinsicObjectType);
            elements.add(jaxb_registryPackageType);
            elements.add(jaxb_classificationType);
            elements.add(jaxb_AssociationType);

            submitObjectsRequest.setRegistryObjectList(registryObjectListType);

            return submitObjectsRequest;
        }

        private ExtrinsicObjectType generateExtrinsicObjectType()
        {
            ExtrinsicObjectType eot = new ExtrinsicObjectType();

            eot.setId(_eot_id);
            eot.setObjectType("urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1");
            eot.setMimeType(mimeType);

            List<SlotType1> slots = eot.getSlot();
            slots.add(makeSlot(SlotType1Enum.CREATION_TIME, creationTime != null ? (new SimpleDateFormat("yyyyMMdd")).format(creationTime) : null));
            slots.add(makeSlot(SlotType1Enum.LANGUAGE_CODE, languageCode));
            slots.add(makeSlot(SlotType1Enum.SERVICE_START_TIME, serviceStartTime != null ? (new SimpleDateFormat("yyyyMMddHHmm")).format(serviceStartTime) : null));
            slots.add(makeSlot(SlotType1Enum.SERVICE_STOP_TIME, serviceStopTime != null ? (new SimpleDateFormat("yyyyMMddHHmm")).format(serviceStopTime) : null));
            slots.add(makeSlot(SlotType1Enum.SOURCE_PATIENT_ID, sourcePatient.getLocalId() + "^^^&" + sourcePatient.getLocalOrg() + "&ISO"));
            slots.add(makeSlot(SlotType1Enum.SOURCE_PATIENT_INFO, sourcePatient));

            eot.setName(makeInternationalStringType(classCode_localized));
            eot.setDescription(makeInternationalStringType(_eot_description));

            // author
            ClassificationType authorClassification = new ClassificationType();
            authorClassification.setClassifiedObject(_eot_id);
            authorClassification.setId(ClassificationTypeEnum.DOC_AUTHOR.getClassificationId());
            authorClassification.setClassificationScheme(ClassificationTypeEnum.DOC_AUTHOR.getClassificationScheme());

            List<SlotType1> authorClassificationSlots = authorClassification.getSlot();
            authorClassificationSlots.add(makeSlot(SlotType1Enum.AUTHOR_PERSON, authorPerson));
            authorClassificationSlots.add(makeSlot(SlotType1Enum.AUTHOR_INSTITUTION, authorInstitution));
            authorClassificationSlots.add(makeSlot(SlotType1Enum.AUTHOR_ROLE, authorRole));
            authorClassificationSlots.add(makeSlot(SlotType1Enum.AUTHOR_SPECIALTY, authorSpecialty));

            eot.getClassification().add(authorClassification);

            // classCode
            ClassificationType classCodeClassification = new ClassificationType();
            classCodeClassification.setClassifiedObject(_eot_id);
            classCodeClassification.setNodeRepresentation(classCode);
            classCodeClassification.setName(makeInternationalStringType(classCode_localized));
            classCodeClassification.setId(ClassificationTypeEnum.DOC_CLASS_CODE.getClassificationId());
            classCodeClassification.setClassificationScheme(ClassificationTypeEnum.DOC_CLASS_CODE.getClassificationScheme());

            List<SlotType1> classCodeClassificationSlots = classCodeClassification.getSlot();
            classCodeClassificationSlots.add(makeSlot(SlotType1Enum.CODING_SCHEME, ClassificationTypeEnum.DOC_CLASS_CODE.getCodingScheme()));

            eot.getClassification().add(classCodeClassification);

            // confidentialityCode
            ClassificationType confidentialityCodeClassification = new ClassificationType();
            confidentialityCodeClassification.setClassifiedObject(_eot_id);
            confidentialityCodeClassification.setNodeRepresentation(confidentialityCode);
            confidentialityCodeClassification.setName(makeInternationalStringType(confidentialityCode_localized));
            confidentialityCodeClassification.setId(ClassificationTypeEnum.DOC_CONFIDENTIALITY_CODE.getClassificationId());
            confidentialityCodeClassification.setClassificationScheme(ClassificationTypeEnum.DOC_CONFIDENTIALITY_CODE.getClassificationScheme());

            List<SlotType1> confidentialityCodeClassificationSlots = confidentialityCodeClassification.getSlot();
            confidentialityCodeClassificationSlots.add(makeSlot(SlotType1Enum.CODING_SCHEME, ClassificationTypeEnum.DOC_CONFIDENTIALITY_CODE.getCodingScheme()));

            eot.getClassification().add(confidentialityCodeClassification);

            // formatCode
            ClassificationType formatCodeClassification = new ClassificationType();
            formatCodeClassification.setClassifiedObject(_eot_id);
            formatCodeClassification.setNodeRepresentation(formatCode);
            formatCodeClassification.setName(makeInternationalStringType(formatCode_localized));
            formatCodeClassification.setId(ClassificationTypeEnum.DOC_FORMAT_CODE.getClassificationId());
            formatCodeClassification.setClassificationScheme(ClassificationTypeEnum.DOC_FORMAT_CODE.getClassificationScheme());

            List<SlotType1> formatCodeClassificationSlots = formatCodeClassification.getSlot();
            formatCodeClassificationSlots.add(makeSlot(SlotType1Enum.CODING_SCHEME, ClassificationTypeEnum.DOC_FORMAT_CODE.getCodingScheme()));

            eot.getClassification().add(formatCodeClassification);

            // healthcareFacilityTypeCode
            ClassificationType healthcareFacilityTypeCodeClassification = new ClassificationType();
            healthcareFacilityTypeCodeClassification.setClassifiedObject(_eot_id);
            healthcareFacilityTypeCodeClassification.setNodeRepresentation(healthcareFacilityTypeCode);
            healthcareFacilityTypeCodeClassification.setName(makeInternationalStringType(healthcareFacilityTypeCode_localized));
            healthcareFacilityTypeCodeClassification.setId(ClassificationTypeEnum.DOC_HEALTHCARE_FACILITY_TYPE_CODE.getClassificationId());
            healthcareFacilityTypeCodeClassification.setClassificationScheme(ClassificationTypeEnum.DOC_HEALTHCARE_FACILITY_TYPE_CODE.getClassificationScheme());

            List<SlotType1> healthcareFacilityTypeCodeClassificationSlots = healthcareFacilityTypeCodeClassification.getSlot();
            healthcareFacilityTypeCodeClassificationSlots.add(makeSlot(SlotType1Enum.CODING_SCHEME, ClassificationTypeEnum.DOC_HEALTHCARE_FACILITY_TYPE_CODE.getCodingScheme()));

            eot.getClassification().add(healthcareFacilityTypeCodeClassification);

            // practiceSettingCode
            ClassificationType practiceSettingCodeClassification = new ClassificationType();
            practiceSettingCodeClassification.setClassifiedObject(_eot_id);
            practiceSettingCodeClassification.setNodeRepresentation(practiceSettingCode);
            practiceSettingCodeClassification.setName(makeInternationalStringType(practiceSettingCode_localized));
            practiceSettingCodeClassification.setId(ClassificationTypeEnum.DOC_PRACTICE_SETTING_CODE.getClassificationId());
            practiceSettingCodeClassification.setClassificationScheme(ClassificationTypeEnum.DOC_PRACTICE_SETTING_CODE.getClassificationScheme());

            List<SlotType1> practiceSettingCodeClassificationSlots = practiceSettingCodeClassification.getSlot();
            practiceSettingCodeClassificationSlots.add(makeSlot(SlotType1Enum.CODING_SCHEME, ClassificationTypeEnum.DOC_PRACTICE_SETTING_CODE.getCodingScheme()));

            eot.getClassification().add(practiceSettingCodeClassification);

            // loinc
            ClassificationType loincClassification = new ClassificationType();
            loincClassification.setClassifiedObject(_eot_id);
            loincClassification.setNodeRepresentation(loinc);
            loincClassification.setName(makeInternationalStringType(loinc_localized));
            loincClassification.setId(ClassificationTypeEnum.DOC_LOINC.getClassificationId());
            loincClassification.setClassificationScheme(ClassificationTypeEnum.DOC_LOINC.getClassificationScheme());

            List<SlotType1> loincClassificationSlots = loincClassification.getSlot();
            loincClassificationSlots.add(makeSlot(SlotType1Enum.CODING_SCHEME, ClassificationTypeEnum.DOC_LOINC.getCodingScheme()));

            eot.getClassification().add(loincClassification);

            // patientId
            ExternalIdentifierType xdsDocumentEntry_patientId = new ExternalIdentifierType();
            xdsDocumentEntry_patientId.setValue(patientId);
            xdsDocumentEntry_patientId.setRegistryObject(_eot_id);
            xdsDocumentEntry_patientId.setId(ExternalIdentifierTypeEnum.DOC_PATIENT_ID.getIdentificationId());
            xdsDocumentEntry_patientId.setIdentificationScheme(ExternalIdentifierTypeEnum.DOC_PATIENT_ID.getIdentificationScheme());
            xdsDocumentEntry_patientId.setName(makeInternationalStringType(ExternalIdentifierTypeEnum.DOC_PATIENT_ID.getLocalizedString()));

            eot.getExternalIdentifier().add(xdsDocumentEntry_patientId);

            // uniqueId
            ExternalIdentifierType xdsDocumentEntry_uniqueId = new ExternalIdentifierType();
            xdsDocumentEntry_uniqueId.setValue(uniqueId);
            xdsDocumentEntry_uniqueId.setRegistryObject(_eot_id);
            xdsDocumentEntry_uniqueId.setId(ExternalIdentifierTypeEnum.DOC_UNIQUE_ID.getIdentificationId());
            xdsDocumentEntry_uniqueId.setIdentificationScheme(ExternalIdentifierTypeEnum.DOC_UNIQUE_ID.getIdentificationScheme());
            xdsDocumentEntry_uniqueId.setName(makeInternationalStringType(ExternalIdentifierTypeEnum.DOC_UNIQUE_ID.getLocalizedString()));

            eot.getExternalIdentifier().add(xdsDocumentEntry_uniqueId);

            return eot;
        }

        private RegistryPackageType generateRegistryPackageType()
        {
            RegistryPackageType rpt = new RegistryPackageType();

            rpt.setId(_rpt_id);

            List<SlotType1> slots = rpt.getSlot();
            slots.add(makeSlot(SlotType1Enum.SUBMISSION_TIME, ss_submissionTime != null ? (new SimpleDateFormat("yyyyMMddHHmmss")).format(ss_submissionTime) : null));
            slots.add(makeSlot(SlotType1Enum.INTENDED_RECIPIENT, ss_intendedRecipient));

            rpt.setName(makeInternationalStringType(_rpt_name));
            rpt.setDescription(makeInternationalStringType(_rpt_description));

            // author
            ClassificationType authorClassification = new ClassificationType();
            authorClassification.setClassifiedObject(_rpt_id);
            authorClassification.setId(ClassificationTypeEnum.SS_AUTHOR.getClassificationId());
            authorClassification.setClassificationScheme(ClassificationTypeEnum.SS_AUTHOR.getClassificationScheme());

            List<SlotType1> authorClassificationSlots = authorClassification.getSlot();
            authorClassificationSlots.add(makeSlot(SlotType1Enum.AUTHOR_PERSON, ss_authorPerson));
            authorClassificationSlots.add(makeSlot(SlotType1Enum.AUTHOR_INSTITUTION, ss_authorInstitution));
            authorClassificationSlots.add(makeSlot(SlotType1Enum.AUTHOR_ROLE, ss_authorRole));
            authorClassificationSlots.add(makeSlot(SlotType1Enum.AUTHOR_SPECIALTY, ss_authorSpecialty));

            rpt.getClassification().add(authorClassification);

            // contentTypeCode
            ClassificationType contentTypeCodeClassification = new ClassificationType();
            contentTypeCodeClassification.setClassifiedObject(_rpt_id);
            contentTypeCodeClassification.setNodeRepresentation(contentTypeCode);
            contentTypeCodeClassification.setName(makeInternationalStringType(contentTypeCode_localized));
            contentTypeCodeClassification.setId(ClassificationTypeEnum.SS_CONTENT_TYPE_CODE.getClassificationId());
            contentTypeCodeClassification.setClassificationScheme(ClassificationTypeEnum.SS_CONTENT_TYPE_CODE.getClassificationScheme());

            List<SlotType1> contentTypeCodeClassificationSlots = contentTypeCodeClassification.getSlot();
            contentTypeCodeClassificationSlots.add(makeSlot(SlotType1Enum.CODING_SCHEME, ClassificationTypeEnum.SS_CONTENT_TYPE_CODE.getCodingScheme()));

            rpt.getClassification().add(contentTypeCodeClassification);

            // uniqueId
            ExternalIdentifierType xdsSubmissionSet_uniqueId = new ExternalIdentifierType();
            xdsSubmissionSet_uniqueId.setValue(ss_uniqueId);
            xdsSubmissionSet_uniqueId.setRegistryObject(_rpt_id);
            xdsSubmissionSet_uniqueId.setId(ExternalIdentifierTypeEnum.SS_UNIQUE_ID.getIdentificationId());
            xdsSubmissionSet_uniqueId.setIdentificationScheme(ExternalIdentifierTypeEnum.SS_UNIQUE_ID.getIdentificationScheme());
            xdsSubmissionSet_uniqueId.setName(makeInternationalStringType(ExternalIdentifierTypeEnum.SS_UNIQUE_ID.getLocalizedString()));

            rpt.getExternalIdentifier().add(xdsSubmissionSet_uniqueId);

            // sourceId
            ExternalIdentifierType xdsSubmissionSet_sourceId = new ExternalIdentifierType();
            xdsSubmissionSet_sourceId.setValue(ss_sourceId);
            xdsSubmissionSet_sourceId.setRegistryObject(_rpt_id);
            xdsSubmissionSet_sourceId.setId(ExternalIdentifierTypeEnum.SS_SOURCE_ID.getIdentificationId());
            xdsSubmissionSet_sourceId.setIdentificationScheme(ExternalIdentifierTypeEnum.SS_SOURCE_ID.getIdentificationScheme());
            xdsSubmissionSet_sourceId.setName(makeInternationalStringType(ExternalIdentifierTypeEnum.SS_SOURCE_ID.getLocalizedString()));

            rpt.getExternalIdentifier().add(xdsSubmissionSet_sourceId);

            // patientId
            ExternalIdentifierType xdsSubmissionSet_patientId = new ExternalIdentifierType();
            xdsSubmissionSet_patientId.setValue(ss_patientId);
            xdsSubmissionSet_patientId.setRegistryObject(_rpt_id);
            xdsSubmissionSet_patientId.setId(ExternalIdentifierTypeEnum.SS_PATIENT_ID.getIdentificationId());
            xdsSubmissionSet_patientId.setIdentificationScheme(ExternalIdentifierTypeEnum.SS_PATIENT_ID.getIdentificationScheme());
            xdsSubmissionSet_patientId.setName(makeInternationalStringType(ExternalIdentifierTypeEnum.SS_PATIENT_ID.getLocalizedString()));

            rpt.getExternalIdentifier().add(xdsSubmissionSet_patientId);

            return rpt;
        }

        private ClassificationType generateClassificationType()
        {
            ClassificationType ct = new ClassificationType();

            ct.setId("cl10");
            ct.setClassifiedObject(_rpt_id);
            ct.setClassificationScheme("urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd");

            return ct;
        }

        private AssociationType1 generateAssociationType()
        {
            AssociationType1 at = new AssociationType1();

            at.setId("as01");
            at.setAssociationType("HasMember");
            at.setSourceObject(_rpt_id);
            at.setTargetObject(_eot_id);

            List<SlotType1> slots = at.getSlot();
            slots.add(makeSlot(SlotType1Enum.SUBMISSION_SET_STATUS, submissionSetStatus));

            return at;
        }

        /**
         * Extract metadata from a SubmitObjectsRequest object represented as an
         * XML string.
         * 
         * @param submitObjectsRequest
         *            A SubmitObjectsRequest object represented as an XML
         *            string.
         * @throws Exception
         */
        public void setValues(String submitObjectsRequestXml) throws Exception
        {
            SubmitObjectsRequest sor = (SubmitObjectsRequest) XmlUtils.unmarshal(new String(submitObjectsRequestXml),
                    oasis.names.tc.ebxml_regrep.xsd.lcm._3.ObjectFactory.class);

            setValues(sor);
        }

        /**
         * Extract metadata from a SubmitObjectsRequest object.
         * 
         * @param submitObjectsRequest
         *            A SubmitObjectsRequest object.
         */
        public void setValues(SubmitObjectsRequest submitObjectsRequest) throws MetadataException
        {
            RegistryObjectListType rol = submitObjectsRequest.getRegistryObjectList();

            List<JAXBElement<? extends IdentifiableType>> elements = rol.getIdentifiable();

            for (JAXBElement<? extends IdentifiableType> element : elements)
            {
                if (element.getValue() instanceof oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType)
                {
                    ExtrinsicObjectType eot = (ExtrinsicObjectType) element.getValue();

                    mimeType = eot.getMimeType();
                    _eot_id = eot.getId();

                    if (eot.getDescription() != null && eot.getDescription().getLocalizedString() != null
                            && !eot.getDescription().getLocalizedString().isEmpty())
                        _eot_description = eot.getDescription().getLocalizedString().get(0).getValue();

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
                                String[] tokens = StringUtils.splitPreserveAllTokens(slot.getValueList().getValue()
                                        .get(0), "^");

                                if (tokens != null && tokens.length >= 1)
                                    sourcePatient.setLocalId(tokens[0]);
                                else
                                    sourcePatient.setLocalId(slot.getValueList().getValue().get(0));

                                if (tokens != null && tokens.length >= 4)
                                {
                                    tokens = StringUtils.splitPreserveAllTokens(slot.getValueList().getValue().get(0),
                                            "&");

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
                                        
                                        // TODO country ?
                                    }
                                }
                            }
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
                                        authorInstitution = slot.getValueList().getValue().get(0);
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

                            if (ct.getName() != null && ct.getName().getLocalizedString() != null
                                    && !ct.getName().getLocalizedString().isEmpty())
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

                            if (ct.getName() != null && ct.getName().getLocalizedString() != null
                                    && !ct.getName().getLocalizedString().isEmpty())
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

                            if (ct.getName() != null && ct.getName().getLocalizedString() != null
                                    && !ct.getName().getLocalizedString().isEmpty())
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

                            if (ct.getName() != null && ct.getName().getLocalizedString() != null
                                    && !ct.getName().getLocalizedString().isEmpty())
                                healthcareFacilityTypeCode_localized = ct.getName().getLocalizedString().get(0)
                                        .getValue();
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

                            if (ct.getName() != null && ct.getName().getLocalizedString() != null
                                    && !ct.getName().getLocalizedString().isEmpty())
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

                            if (ct.getName() != null && ct.getName().getLocalizedString() != null
                                    && !ct.getName().getLocalizedString().isEmpty())
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
                else if (element.getValue() instanceof oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryPackageType)
                {
                    RegistryPackageType rpt = (RegistryPackageType) element.getValue();

                    _rpt_id = rpt.getId();
                    _rpt_name = rpt.getName().getLocalizedString().get(0).getValue();
                    _rpt_description = rpt.getDescription().getLocalizedString().get(0).getValue();

                    for (SlotType1 slot : rpt.getSlot())
                    {
                        if (SlotType1Enum.SUBMISSION_TIME.matches(slot.getName()))
                        {
                            if (slotNotEmpty(slot))
                            {
                                try
                                {
                                    ss_submissionTime = DateUtils.parseDate(slot.getValueList().getValue().get(0),
                                            new String[]
                                            { "yyyyMMddHHmmss", "yyyyMMddHHmm", "yyyyMMdd" });
                                }
                                catch (ParseException e)
                                {
                                    LOGGER.error("Unable to parse submissionTime", e);
                                    throw new MetadataException("Unable to parse submissionTime", e);
                                }
                            }
                        }
                        else if (SlotType1Enum.INTENDED_RECIPIENT.matches(slot.getName()))
                        {
                            if (slotNotEmpty(slot))
                                ss_intendedRecipient = slot.getValueList().getValue().get(0);
                        }
                    }

                    for (ClassificationType ct : rpt.getClassification())
                    {
                        if (ClassificationTypeEnum.SS_AUTHOR.matchesScheme(ct.getClassificationScheme()))
                        {
                            for (SlotType1 slot : ct.getSlot())
                            {
                                if (SlotType1Enum.AUTHOR_PERSON.matches(slot.getName()))
                                {
                                    if (slotNotEmpty(slot))
                                        ss_authorPerson = slot.getValueList().getValue().get(0);
                                }
                                else if (SlotType1Enum.AUTHOR_INSTITUTION.matches(slot.getName()))
                                {
                                    // FIXME: this had two values
                                    
                                    if (slotNotEmpty(slot))
                                        ss_authorInstitution = slot.getValueList().getValue().get(0);
                                }
                                else if (SlotType1Enum.AUTHOR_ROLE.matches(slot.getName()))
                                {
                                    if (slotNotEmpty(slot))
                                        ss_authorRole = slot.getValueList().getValue().get(0);
                                }
                                else if (SlotType1Enum.AUTHOR_SPECIALTY.matches(slot.getName()))
                                {
                                    if (slotNotEmpty(slot))
                                        ss_authorSpecialty = slot.getValueList().getValue().get(0);
                                }
                            }
                        }
                        if (ClassificationTypeEnum.SS_CONTENT_TYPE_CODE.matchesScheme(ct.getClassificationScheme()))
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

                            contentTypeCode = ct.getNodeRepresentation();

                            if (ct.getName() != null && ct.getName().getLocalizedString() != null
                                    && !ct.getName().getLocalizedString().isEmpty())
                                contentTypeCode_localized = ct.getName().getLocalizedString().get(0).getValue();
                        }
                    }

                    for (ExternalIdentifierType eit : rpt.getExternalIdentifier())
                    {
                        if (ExternalIdentifierTypeEnum.SS_UNIQUE_ID.matchesScheme(eit.getIdentificationScheme()))
                        {
                            ss_uniqueId = eit.getValue();
                        }
                        else if (ExternalIdentifierTypeEnum.SS_SOURCE_ID.matchesScheme(eit.getIdentificationScheme()))
                        {
                            ss_sourceId = eit.getValue();
                        }
                        else if (ExternalIdentifierTypeEnum.SS_PATIENT_ID.matchesScheme(eit.getIdentificationScheme()))
                        {
                            ss_patientId = eit.getValue();
                        }
                    }
                }
                else if (element.getValue() instanceof oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType)
                {
                    // Empty in example
                }
                else if (element.getValue() instanceof oasis.names.tc.ebxml_regrep.xsd.rim._3.AssociationType1)
                {
                    AssociationType1 at = (AssociationType1) element.getValue();

                    for (SlotType1 slot : at.getSlot())
                    {
                        if (SlotType1Enum.SUBMISSION_SET_STATUS.matches(slot.getName()))
                        {
                            if (slotNotEmpty(slot))
                                submissionSetStatus = slot.getName();
                        }
                    }
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
            QName qname = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0", "SubmitObjectsRequest");
            return XmlUtils.marshal(qname, getSubmitObjectsRequest(), ihe.iti.xds_b._2007.ObjectFactory.class);
        }

        private SlotType1 makeSlot(SlotType1Enum slotTypeEnum, SimplePerson person)
        {
            SlotType1 slot = new SlotType1();
            ValueListType values = new ValueListType();
            List<String> vals = values.getValue();

            slot.setName(slotTypeEnum.getName());
            slot.setValueList(values);

            // <rim:Value>PID-3|pid1^^^domain</rim:Value>
            vals.add("PID-3|" + person.getLocalId() + "^^^&" + person.getLocalOrg() + "&ISO");

            // <rim:Value>PID-5|Doe^John^Middle^^</rim:Value>
            vals.add("PID-5|" + person.getLastName() + "^" + person.getFirstName() + "^" + person.getMiddleName() + "^^");

            // <rim:Value>PID-7|19560527</rim:Value>
            vals.add("PID-7|" + person.getBirthDateTime()); // TODO check this format

            // <rim:Value>PID-8|M</rim:Value>
            vals.add("PID-8|" + person.getGenderCode());

            // <rim:Value>PID-11|100 Main St^^Metropolis^Il^44130^USA</rim:Value>
            vals.add("PID-11|" + person.getStreetAddress1() + "^^" + person.getCity() + "^" + person.getState() + "^" + person.getZipCode() + "^");

            return slot;
        }

        private SlotType1 makeSlot(SlotType1Enum slotTypeEnum, String value)
        {
            SlotType1 slot = new SlotType1();
            ValueListType values = new ValueListType();
            List<String> vals = values.getValue();

            slot.setName(slotTypeEnum.getName());
            slot.setValueList(values);
            vals.add(value);

            return slot;
        }

        private InternationalStringType makeInternationalStringType(String value)
        {
            InternationalStringType name = new InternationalStringType();
            List<LocalizedStringType> names = name.getLocalizedString();
            LocalizedStringType lname = new LocalizedStringType();
            lname.setValue(value);
            names.add(lname);

            return name;
        }

        private boolean slotNotEmpty(SlotType1 slot)
        {
            if (slot.getValueList() != null && slot.getValueList().getValue() != null
                    && !slot.getValueList().getValue().isEmpty())
                return true;

            return false;
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
         * @return the _eot_id
         */
        public String get_eot_id()
        {
            return _eot_id;
        }

        /**
         * @return the _eot_description
         */
        public String get_eot_description()
        {
            return _eot_description;
        }

        /**
         * @param eotDescription
         *            the _eot_description to set
         */
        public void set_eot_description(String eotDescription)
        {
            _eot_description = eotDescription;
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
        public String getAuthorInstitution()
        {
            return authorInstitution;
        }

        /**
         * @param authorInstitution
         *            the authorInstitution to set
         */
        public void setAuthorInstitution(String authorInstitution)
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
            this.classCode = classCode;
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
            this.confidentialityCode = confidentialityCode;
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
            this.formatCode = formatCode;
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
            this.healthcareFacilityTypeCode = healthcareFacilityTypeCode;
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
            this.practiceSettingCode = practiceSettingCode;
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
            this.loinc = loinc;
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
         * @return the _rpt_id
         */
        public String get_rpt_id()
        {
            return _rpt_id;
        }

        /**
         * @return the _rpt_name
         */
        public String get_rpt_name()
        {
            return _rpt_name;
        }

        /**
         * @param rptName
         *            the _rpt_name to set
         */
        public void set_rpt_name(String rptName)
        {
            _rpt_name = rptName;
        }

        /**
         * @return the _rpt_description
         */
        public String get_rpt_description()
        {
            return _rpt_description;
        }

        /**
         * @param rptDescription
         *            the _rpt_description to set
         */
        public void set_rpt_description(String rptDescription)
        {
            _rpt_description = rptDescription;
        }

        /**
         * @return the ss_submissionTime
         */
        public Date getSs_submissionTime()
        {
            return ss_submissionTime;
        }

        /**
         * @param ssSubmissionTime
         *            the ss_submissionTime to set
         */
        public void setSs_submissionTime(Date ssSubmissionTime)
        {
            ss_submissionTime = ssSubmissionTime;
        }

        /**
         * @return the ss_intendedRecipient
         */
        public String getSs_intendedRecipient()
        {
            return ss_intendedRecipient;
        }

        /**
         * @param ssIntendedRecipient
         *            the ss_intendedRecipient to set
         */
        public void setSs_intendedRecipient(String ssIntendedRecipient)
        {
            ss_intendedRecipient = ssIntendedRecipient;
        }

        /**
         * @return the ss_authorPerson
         */
        public String getSs_authorPerson()
        {
            return ss_authorPerson;
        }

        /**
         * @param ssAuthorPerson
         *            the ss_authorPerson to set
         */
        public void setSs_authorPerson(String ssAuthorPerson)
        {
            ss_authorPerson = ssAuthorPerson;
        }

        /**
         * @return the ss_authorInstitution
         */
        public String getSs_authorInstitution()
        {
            return ss_authorInstitution;
        }

        /**
         * @param ssAuthorInstitution
         *            the ss_authorInstitution to set
         */
        public void setSs_authorInstitution(String ssAuthorInstitution)
        {
            ss_authorInstitution = ssAuthorInstitution;
        }

        /**
         * @return the ss_authorRole
         */
        public String getSs_authorRole()
        {
            return ss_authorRole;
        }

        /**
         * @param ssAuthorRole
         *            the ss_authorRole to set
         */
        public void setSs_authorRole(String ssAuthorRole)
        {
            ss_authorRole = ssAuthorRole;
        }

        /**
         * @return the ss_authorSpecialty
         */
        public String getSs_authorSpecialty()
        {
            return ss_authorSpecialty;
        }

        /**
         * @param ssAuthorSpecialty
         *            the ss_authorSpecialty to set
         */
        public void setSs_authorSpecialty(String ssAuthorSpecialty)
        {
            ss_authorSpecialty = ssAuthorSpecialty;
        }

        /**
         * @return the contentTypeCode
         */
        public String getContentTypeCode()
        {
            return contentTypeCode;
        }

        /**
         * @param contentTypeCode
         *            the contentTypeCode to set
         */
        public void setContentTypeCode(String contentTypeCode)
        {
            this.contentTypeCode = contentTypeCode;
        }

        /**
         * @return the contentTypeCode_localized
         */
        public String getContentTypeCode_localized()
        {
            return contentTypeCode_localized;
        }

        /**
         * @param contentTypeCodeLocalized
         *            the contentTypeCode_localized to set
         */
        public void setContentTypeCode_localized(String contentTypeCodeLocalized)
        {
            contentTypeCode_localized = contentTypeCodeLocalized;
        }

        /**
         * @return the ss_uniqueId
         */
        public String getSs_uniqueId()
        {
            return ss_uniqueId;
        }

        /**
         * @param ssUniqueId
         *            the ss_uniqueId to set
         */
        public void setSs_uniqueId(String ssUniqueId)
        {
            ss_uniqueId = ssUniqueId;
        }

        /**
         * @return the ss_sourceId
         */
        public String getSs_sourceId()
        {
            return ss_sourceId;
        }

        /**
         * @param ssSourceId
         *            the ss_sourceId to set
         */
        public void setSs_sourceId(String ssSourceId)
        {
            ss_sourceId = ssSourceId;
        }

        /**
         * @return the ss_patientId
         */
        public String getSs_patientId()
        {
            return ss_patientId;
        }

        /**
         * @param ssPatientId
         *            the ss_patientId to set
         */
        public void setSs_patientId(String ssPatientId)
        {
            ss_patientId = ssPatientId;
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
    }
}
