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
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType.Document;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.AssociationType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExternalIdentifierType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.IdentifiableType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryPackageType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.xd.common.exception.MetadataException;
import org.nhindirect.xd.common.type.AssociationType1Enum;
import org.nhindirect.xd.common.type.ClassificationTypeEnum;
import org.nhindirect.xd.common.type.ExternalIdentifierTypeEnum;
import org.nhindirect.xd.common.type.SlotType1Enum;
import org.nhindirect.xd.common.type.SubmitObjectsRequestEnum;
import org.nhindirect.xd.transform.util.XmlUtils;

/**
 * Abstract representation of a collection of documents with supporting
 * metadata.
 * 
 * @author beau
 */
public class DirectDocuments {

    private List<DirectDocument2> documents;
    private SubmissionSet submissionSet;
    private static final Log LOGGER = LogFactory.getFactory().getInstance(DirectDocuments.class);

    public DirectDocuments() {
        this.documents = new ArrayList<DirectDocument2>();
        this.submissionSet = new SubmissionSet();
    }

    /**
     * @return the submissionSet
     */
    public SubmissionSet getSubmissionSet() {
        return submissionSet;
    }

    /**
     * @param submissionSet
     *            the submissionSet to set
     */
    public void setSubmissionSet(SubmissionSet submissionSet) {
        if (this.submissionSet != null && !this.submissionSet.equals(submissionSet)) {
            LOGGER.warn("Overwriting existing SubmissionSet values");
        }

        this.submissionSet = submissionSet;
    }

    /**
     * @return the documents
     */
    public List<DirectDocument2> getDocuments() {
        return documents;
    }

    /**
     * @param documents
     *            the documents to set
     */
    public void setDocuments(List<DirectDocument2> documents) {
        this.documents = documents;
    }

    /**
     * Get the metadata represented as a SubmitObjectsRequest object.
     */
    public SubmitObjectsRequest getSubmitObjectsRequest() {
        RegistryPackageType registryPackageType = submissionSet.generateRegistryPackageType();
        ClassificationType classificationType = submissionSet.generateClassificationType();

        QName qname = null;

        // Generate ExtrinsicObjectType objects for each document
        qname = new QName(SubmitObjectsRequestEnum.EXTRINSIC_OBJECT.getNamespaceUri(), SubmitObjectsRequestEnum.EXTRINSIC_OBJECT.getName());
        List<JAXBElement<ExtrinsicObjectType>> jaxb_extrinsicObjectTypes = new ArrayList<JAXBElement<ExtrinsicObjectType>>();
        for (DirectDocument2 document : documents) {
            ExtrinsicObjectType extrinsicObjectType = document.getMetadata().generateExtrinsicObjectType();
            JAXBElement<ExtrinsicObjectType> jaxb_extrinsicObjectType = new JAXBElement<ExtrinsicObjectType>(qname, ExtrinsicObjectType.class, extrinsicObjectType);
            jaxb_extrinsicObjectTypes.add(jaxb_extrinsicObjectType);
        }

        // Generate the RegistryPakageType (SubmissionSet) for the group of
        // documents
        qname = new QName(SubmitObjectsRequestEnum.REGISTRY_PACKAGE.getNamespaceUri(), SubmitObjectsRequestEnum.REGISTRY_PACKAGE.getName());
        JAXBElement<RegistryPackageType> jaxb_registryPackageType = new JAXBElement<RegistryPackageType>(qname, RegistryPackageType.class, registryPackageType);

        // Generate the ClassificationType object
        qname = new QName(SubmitObjectsRequestEnum.CLASSIFICATION.getNamespaceUri(), SubmitObjectsRequestEnum.CLASSIFICATION.getName());
        JAXBElement<ClassificationType> jaxb_classificationType = new JAXBElement<ClassificationType>(qname, ClassificationType.class, classificationType);

        // Generate AssociationType1 objects for each document
        qname = new QName(SubmitObjectsRequestEnum.ASSOCIATION.getNamespaceUri(), SubmitObjectsRequestEnum.ASSOCIATION.getName());
        List<JAXBElement<AssociationType1>> jaxb_associationType1s = new ArrayList<JAXBElement<AssociationType1>>();
        for (DirectDocument2 document : documents) {
            AssociationType1 associationType = submissionSet.generateAssociationType(document.getMetadata().getId(), document.getMetadata().getSubmissionSetStatus());
            JAXBElement<AssociationType1> jaxb_AssociationType1 = new JAXBElement<AssociationType1>(qname, AssociationType1.class, associationType);
            jaxb_associationType1s.add(jaxb_AssociationType1);
        }

        SubmitObjectsRequest submitObjectsRequest = new SubmitObjectsRequest();
        RegistryObjectListType registryObjectListType = new RegistryObjectListType();

        List<JAXBElement<? extends IdentifiableType>> elements = registryObjectListType.getIdentifiable();
        elements.addAll(jaxb_extrinsicObjectTypes);
        elements.add(jaxb_registryPackageType);
        elements.add(jaxb_classificationType);
        elements.addAll(jaxb_associationType1s);

        submitObjectsRequest.setRegistryObjectList(registryObjectListType);

        return submitObjectsRequest;
    }

    public void setValues(String submitObjectsRequestXml) throws Exception {
        SubmitObjectsRequest sor = (SubmitObjectsRequest) XmlUtils.unmarshal(new String(submitObjectsRequestXml), oasis.names.tc.ebxml_regrep.xsd.lcm._3.ObjectFactory.class);

        setValues(sor);
    }

    public void setValues(SubmitObjectsRequest submitObjectsRequest) throws MetadataException {
        RegistryObjectListType rol = submitObjectsRequest.getRegistryObjectList();

        List<JAXBElement<? extends IdentifiableType>> elements = rol.getIdentifiable();

        for (JAXBElement<? extends IdentifiableType> element : elements) {
            if (element.getValue() instanceof oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType) {
                ExtrinsicObjectType eot = (ExtrinsicObjectType) element.getValue();

                DirectDocument2 document = new DirectDocument2();
                DirectDocument2.Metadata metadata = document.getMetadata();

                metadata.setValues(eot);

                documents.add(document);
            } else if (element.getValue() instanceof oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryPackageType) {
                RegistryPackageType rpt = (RegistryPackageType) element.getValue();

                SubmissionSet submissionSet = new SubmissionSet();

                submissionSet.setValues(rpt);

                this.submissionSet = submissionSet;
            } else if (element.getValue() instanceof oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType) {
                // Empty in example
            }
        }

        for (JAXBElement<? extends IdentifiableType> element : elements) {
            if (element.getValue() instanceof oasis.names.tc.ebxml_regrep.xsd.rim._3.AssociationType1) {
                AssociationType1 at = (AssociationType1) element.getValue();

                for (SlotType1 slot : at.getSlot()) {
                    if (SlotType1Enum.SUBMISSION_SET_STATUS.matches(slot.getName())) {
                        if (slotNotEmpty(slot)) {
                            getDocumentById(at.getTargetObject()).getMetadata().setSubmissionSetStatus(slot.getName());
                        }
                    }
                }
            }
        }
    }

    public DirectDocument2 getDocumentById(String targetObject) {
        for (DirectDocument2 document : documents) {
            if (StringUtils.equalsIgnoreCase(document.getMetadata().getId(), targetObject)) {
                return document;
            }
        }

        return null;
    }

    public DirectDocument2 getDocumentByUniqueId(String uniqueId) {
        for (DirectDocument2 document : documents) {
            if (StringUtils.equalsIgnoreCase(document.getMetadata().getUniqueId(), uniqueId)) {
                return document;
            }
        }

        return null;
    }

    public DirectDocument2 getDocumentByHash(String hash) {
        for (DirectDocument2 document : documents) {
            if (StringUtils.equalsIgnoreCase(document.getMetadata().getHash(), hash)) {
                return document;
            }
        }

        return null;
    }

    public DirectDocument2 getDocument(String identifier) {
        DirectDocument2 document = null;

        document = getDocumentById(identifier);
        if (document != null) {
            return document;
        }

        document = getDocumentByUniqueId(identifier);
        if (document != null) {
            return document;
        }

        document = getDocumentByHash(identifier);
        if (document != null) {
            return document;
        }

        return document;
    }

    public String getSubmitObjectsRequestAsString() {
        QName qname = new QName(SubmitObjectsRequestEnum.SUBMIT_OBJECTS_REQUEST.getNamespaceUri(), SubmitObjectsRequestEnum.SUBMIT_OBJECTS_REQUEST.getName());
        return XmlUtils.marshal(qname, getSubmitObjectsRequest(), ihe.iti.xds_b._2007.ObjectFactory.class);
    }

    public ProvideAndRegisterDocumentSetRequestType toProvideAndRegisterDocumentSetRequestType() throws IOException {
        ProvideAndRegisterDocumentSetRequestType request = new ProvideAndRegisterDocumentSetRequestType();

        request.setSubmitObjectsRequest(this.getSubmitObjectsRequest());

        for (DirectDocument2 document : documents) {
            if (document.getData() != null) {
                DataSource source = new ByteArrayDataSource(document.getData(), document.getMetadata().getMimeType());
                DataHandler dhnew = new DataHandler(source);

                Document pdoc = new Document();
                pdoc.setValue(dhnew);
                String id = document.getMetadata().getId();
                pdoc.setId(id);

                request.getDocument().add(pdoc);
            }
        }

        return request;
    }

    public XdmPackage toXdmPackage(String messageId) {
        XdmPackage xdmPackage = new XdmPackage(messageId);
        xdmPackage.setDocuments(this);

        return xdmPackage;
    }

    /**
     * Representation of a Submission Set element.
     * 
     * @author beau
     */
    static public class SubmissionSet {

        private String id;
        private String name;
        private String description;
        private Date submissionTime;
        private List<String> intendedRecipient = new ArrayList<String>();
        private String authorPerson;
        private List<String> authorInstitution = new ArrayList<String>();
        private String authorRole;
        private String authorSpecialty;
        private String authorTelecommunication;
        private String contentTypeCode;
        private String contentTypeCode_localized;
        private String uniqueId;
        private String sourceId;
        private String patientId;

        public SubmissionSet() {
            this.id = "SubmissionSet01";
        }

        protected RegistryPackageType generateRegistryPackageType() {
            RegistryPackageType rpt = new RegistryPackageType();

            rpt.setId(id);

            List<SlotType1> slots = rpt.getSlot();
            addSlot(slots, makeSlot(SlotType1Enum.SUBMISSION_TIME, submissionTime != null ? (new SimpleDateFormat("yyyyMMddHHmmss")).format(submissionTime) : null));
            addSlot(slots, makeSlot(SlotType1Enum.INTENDED_RECIPIENT, intendedRecipient));

            rpt.setName(makeInternationalStringType(name));
            rpt.setDescription(makeInternationalStringType(description));

            // author
            ClassificationType authorClassification = new ClassificationType();
            authorClassification.setClassifiedObject(id);
            authorClassification.setNodeRepresentation(""); // required empty string
            authorClassification.setId(ClassificationTypeEnum.SS_AUTHOR.getClassificationId());
            authorClassification.setClassificationScheme(ClassificationTypeEnum.SS_AUTHOR.getClassificationScheme());

            List<SlotType1> authorClassificationSlots = authorClassification.getSlot();
            addSlot(authorClassificationSlots, makeSlot(SlotType1Enum.AUTHOR_PERSON, authorPerson));
            addSlot(authorClassificationSlots, makeSlot(SlotType1Enum.AUTHOR_INSTITUTION, authorInstitution));
            addSlot(authorClassificationSlots, makeSlot(SlotType1Enum.AUTHOR_ROLE, authorRole));
            addSlot(authorClassificationSlots, makeSlot(SlotType1Enum.AUTHOR_SPECIALTY, authorSpecialty));
            addSlot(authorClassificationSlots, makeSlot(SlotType1Enum.AUTHOR_TELECOMMUNICATION, authorTelecommunication));

            rpt.getClassification().add(authorClassification);

            // contentTypeCode
            ClassificationType contentTypeCodeClassification = new ClassificationType();
            contentTypeCodeClassification.setClassifiedObject(id);
            contentTypeCodeClassification.setNodeRepresentation(contentTypeCode);
            contentTypeCodeClassification.setName(makeInternationalStringType(contentTypeCode_localized));
            contentTypeCodeClassification.setId(ClassificationTypeEnum.SS_CONTENT_TYPE_CODE.getClassificationId());
            contentTypeCodeClassification.setClassificationScheme(ClassificationTypeEnum.SS_CONTENT_TYPE_CODE.getClassificationScheme());

            List<SlotType1> contentTypeCodeClassificationSlots = contentTypeCodeClassification.getSlot();
            addSlot(contentTypeCodeClassificationSlots, makeSlot(SlotType1Enum.CODING_SCHEME, ClassificationTypeEnum.SS_CONTENT_TYPE_CODE.getCodingScheme()));

            rpt.getClassification().add(contentTypeCodeClassification);

            // uniqueId
            ExternalIdentifierType xdsSubmissionSet_uniqueId = new ExternalIdentifierType();
            xdsSubmissionSet_uniqueId.setValue(uniqueId);
            xdsSubmissionSet_uniqueId.setRegistryObject(id);
            xdsSubmissionSet_uniqueId.setId(ExternalIdentifierTypeEnum.SS_UNIQUE_ID.getIdentificationId());
            xdsSubmissionSet_uniqueId.setIdentificationScheme(ExternalIdentifierTypeEnum.SS_UNIQUE_ID.getIdentificationScheme());
            xdsSubmissionSet_uniqueId.setName(makeInternationalStringType(ExternalIdentifierTypeEnum.SS_UNIQUE_ID.getLocalizedString()));

            rpt.getExternalIdentifier().add(xdsSubmissionSet_uniqueId);

            // sourceId
            ExternalIdentifierType xdsSubmissionSet_sourceId = new ExternalIdentifierType();
            xdsSubmissionSet_sourceId.setValue(sourceId);
            xdsSubmissionSet_sourceId.setRegistryObject(id);
            xdsSubmissionSet_sourceId.setId(ExternalIdentifierTypeEnum.SS_SOURCE_ID.getIdentificationId());
            xdsSubmissionSet_sourceId.setIdentificationScheme(ExternalIdentifierTypeEnum.SS_SOURCE_ID.getIdentificationScheme());
            xdsSubmissionSet_sourceId.setName(makeInternationalStringType(ExternalIdentifierTypeEnum.SS_SOURCE_ID.getLocalizedString()));

            rpt.getExternalIdentifier().add(xdsSubmissionSet_sourceId);

            // patientId
            ExternalIdentifierType xdsSubmissionSet_patientId = new ExternalIdentifierType();
            xdsSubmissionSet_patientId.setValue(patientId);
            xdsSubmissionSet_patientId.setRegistryObject(id);
            xdsSubmissionSet_patientId.setId(ExternalIdentifierTypeEnum.SS_PATIENT_ID.getIdentificationId());
            xdsSubmissionSet_patientId.setIdentificationScheme(ExternalIdentifierTypeEnum.SS_PATIENT_ID.getIdentificationScheme());
            xdsSubmissionSet_patientId.setName(makeInternationalStringType(ExternalIdentifierTypeEnum.SS_PATIENT_ID.getLocalizedString()));

            rpt.getExternalIdentifier().add(xdsSubmissionSet_patientId);

            return rpt;
        }

        protected ClassificationType generateClassificationType() {
            ClassificationType ct = new ClassificationType();

            ct.setClassifiedObject(id);
            ct.setId(ClassificationTypeEnum.SS.getClassificationId());
            ct.setClassificationScheme(ClassificationTypeEnum.SS.getClassificationScheme());

            return ct;
        }

        protected AssociationType1 generateAssociationType(String documentId, String submissionSetStatus) {
            AssociationType1 at = new AssociationType1();

            at.setSourceObject(id);
            at.setTargetObject(documentId);
            at.setId(AssociationType1Enum.HAS_MEMBER.getAssociationId());
            at.setAssociationType(AssociationType1Enum.HAS_MEMBER.getAssociationType());

            List<SlotType1> slots = at.getSlot();
            addSlot(slots, makeSlot(SlotType1Enum.SUBMISSION_SET_STATUS, submissionSetStatus));

            return at;
        }

        protected void setValues(RegistryPackageType rpt) throws MetadataException {
            id = rpt.getId();
            name = rpt.getName().getLocalizedString().get(0).getValue();
            if (rpt.getDescription() != null) {
                description = rpt.getDescription().getLocalizedString().get(0).getValue();
            }

            for (SlotType1 slot : rpt.getSlot()) {
                if (SlotType1Enum.SUBMISSION_TIME.matches(slot.getName())) {
                    if (slotNotEmpty(slot)) {
                        try {
                            submissionTime = DateUtils.parseDate(slot.getValueList().getValue().get(
                                    0), new String[]{"yyyyMMddHHmmss", "yyyyMMddHHmm", "yyyyMMdd"});
                        } catch (ParseException e) {
                            LOGGER.error("Unable to parse submissionTime", e);
                            throw new MetadataException("Unable to parse submissionTime", e);
                        }
                    }
                } else if (SlotType1Enum.INTENDED_RECIPIENT.matches(slot.getName())) {
                    if (slotNotEmpty(slot)) {
                        for (String value : slot.getValueList().getValue()) {
                            intendedRecipient.add(value);
                        }
                    }
                }
            }

            for (ClassificationType ct : rpt.getClassification()) {
                if (ClassificationTypeEnum.SS_AUTHOR.matchesScheme(ct.getClassificationScheme())) {
                    for (SlotType1 slot : ct.getSlot()) {
                        if (SlotType1Enum.AUTHOR_PERSON.matches(slot.getName())) {
                            if (slotNotEmpty(slot)) {
                                authorPerson = slot.getValueList().getValue().get(0);
                            }
                        } else if (SlotType1Enum.AUTHOR_INSTITUTION.matches(slot.getName())) {
                            if (slotNotEmpty(slot)) {
                                for (String value : slot.getValueList().getValue()) {
                                    authorInstitution.add(value);
                                }
                            }
                        } else if (SlotType1Enum.AUTHOR_ROLE.matches(slot.getName())) {
                            if (slotNotEmpty(slot)) {
                                authorRole = slot.getValueList().getValue().get(0);
                            }
                        } else if (SlotType1Enum.AUTHOR_SPECIALTY.matches(slot.getName())) {
                            if (slotNotEmpty(slot)) {
                                authorSpecialty = slot.getValueList().getValue().get(0);
                            }
                        } else if (SlotType1Enum.AUTHOR_TELECOMMUNICATION.matches(slot.getName())) {
                            if (slotNotEmpty(slot)) {
                                authorTelecommunication = slot.getValueList().getValue().get(0);
                            }
                        }
                    }
                }
                if (ClassificationTypeEnum.SS_CONTENT_TYPE_CODE.matchesScheme(ct.getClassificationScheme())) {
                    for (SlotType1 slot : ct.getSlot()) {
                        if (SlotType1Enum.CODING_SCHEME.matches(slot.getName())) {
                            if (slotNotEmpty(slot)) {
                                @SuppressWarnings("unused")
                                String codingScheme = slot.getValueList().getValue().get(0);
                            }
                        }
                    }

                    contentTypeCode = ct.getNodeRepresentation();

                    if (ct.getName() != null && ct.getName().getLocalizedString() != null && !ct.getName().getLocalizedString().isEmpty()) {
                        contentTypeCode_localized = ct.getName().getLocalizedString().get(0).getValue();
                    }
                }
            }

            for (ExternalIdentifierType eit : rpt.getExternalIdentifier()) {
                if (ExternalIdentifierTypeEnum.SS_UNIQUE_ID.matchesScheme(eit.getIdentificationScheme())) {
                    uniqueId = eit.getValue();
                } else if (ExternalIdentifierTypeEnum.SS_SOURCE_ID.matchesScheme(eit.getIdentificationScheme())) {
                    sourceId = eit.getValue();
                } else if (ExternalIdentifierTypeEnum.SS_PATIENT_ID.matchesScheme(eit.getIdentificationScheme())) {
                    patientId = eit.getValue();
                }
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            QName qname = new QName(SubmitObjectsRequestEnum.REGISTRY_PACKAGE.getNamespaceUri(), SubmitObjectsRequestEnum.REGISTRY_PACKAGE.getName());
            return XmlUtils.marshal(qname, generateRegistryPackageType(), ihe.iti.xds_b._2007.ObjectFactory.class);
        }

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id
         *            the id to set
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name
         *            the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * @param description
         *            the description to set
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * @return the submissionTime
         */
        public Date getSubmissionTime() {
            return submissionTime;
        }

        /**
         * @param submissionTime
         *            the submissionTime to set
         */
        public void setSubmissionTime(Date submissionTime) {
            this.submissionTime = submissionTime;
        }

        /**
         * @return the intendedRecipient
         */
        public List<String> getIntendedRecipient() {
            return intendedRecipient;
        }

        /**
         * @param intendedRecipient
         *            the intendedRecipient to set
         */
        public void setIntendedRecipient(List<String> intendedRecipient) {
            this.intendedRecipient = intendedRecipient;
        }

        /**
         * @return the authorPerson
         */
        public String getAuthorPerson() {
            return authorPerson;
        }

        /**
         * @param authorPerson
         *            the authorPerson to set
         */
        public void setAuthorPerson(String authorPerson) {
            this.authorPerson = authorPerson;
        }

        /**
         * @return the authorInstitution
         */
        public List<String> getAuthorInstitution() {
            return authorInstitution;
        }

        /**
         * @param authorInstitution
         *            the authorInstitution to set
         */
        public void setAuthorInstitution(List<String> authorInstitution) {
            this.authorInstitution = authorInstitution;
        }

        /**
         * @return the authorRole
         */
        public String getAuthorRole() {
            return authorRole;
        }

        /**
         * @param authorRole
         *            the authorRole to set
         */
        public void setAuthorRole(String authorRole) {
            this.authorRole = authorRole;
        }

        /**
         * @return the authorSpecialty
         */
        public String getAuthorSpecialty() {
            return authorSpecialty;
        }

        /**
         * @param authorSpecialty
         *            the authorSpecialty to set
         */
        public void setAuthorSpecialty(String authorSpecialty) {
            this.authorSpecialty = authorSpecialty;
        }

        /**
         * @return the authorTelecommunication
         */
        public String getAuthorTelecommunication() {
            return authorTelecommunication;
        }

        /**
         * @param authorTelecommunication
         *            the authorTelecommunication to set
         */
        public void setAuthorTelecommunication(String authorTelecommunication) {
            this.authorTelecommunication = authorTelecommunication;
        }

        /**
         * @return the contentTypeCode
         */
        public String getContentTypeCode() {
            return contentTypeCode;
        }

        /**
         * @param contentTypeCode
         *            the contentTypeCode to set
         */
        public void setContentTypeCode(String contentTypeCode) {
            setContentTypeCode(contentTypeCode, false);
        }

        /**
         * @param contentTypeCode
         *            the contentTypeCode to set
         * @param setLocalized
         *            whether or not to set the localized field with the same
         *            value
         */
        public void setContentTypeCode(String contentTypeCode, boolean setLocalized) {
            this.contentTypeCode = contentTypeCode;

            if (setLocalized) {
                this.contentTypeCode_localized = contentTypeCode;
            }
        }

        /**
         * @return the contentTypeCode_localized
         */
        public String getContentTypeCode_localized() {
            return contentTypeCode_localized;
        }

        /**
         * @param contentTypeCodeLocalized
         *            the contentTypeCode_localized to set
         */
        public void setContentTypeCode_localized(String contentTypeCodeLocalized) {
            contentTypeCode_localized = contentTypeCodeLocalized;
        }

        /**
         * @return the uniqueId
         */
        public String getUniqueId() {
            return uniqueId;
        }

        /**
         * @param uniqueId
         *            the uniqueId to set
         */
        public void setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
        }

        /**
         * @return the sourceId
         */
        public String getSourceId() {
            return sourceId;
        }

        /**
         * @param sourceId
         *            the sourceId to set
         */
        public void setSourceId(String sourceId) {
            this.sourceId = sourceId;
        }

        /**
         * @return the patientId
         */
        public String getPatientId() {
            return patientId;
        }

        /**
         * @param patientId
         *            the patientId to set
         */
        public void setPatientId(String patientId) {
            this.patientId = patientId;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((authorInstitution == null) ? 0 : authorInstitution.hashCode());
            result = prime * result + ((authorPerson == null) ? 0 : authorPerson.hashCode());
            result = prime * result + ((authorRole == null) ? 0 : authorRole.hashCode());
            result = prime * result + ((authorSpecialty == null) ? 0 : authorSpecialty.hashCode());
            result = prime * result + ((authorTelecommunication == null) ? 0 : authorTelecommunication.hashCode());
            result = prime * result + ((contentTypeCode == null) ? 0 : contentTypeCode.hashCode());
            result = prime * result + ((contentTypeCode_localized == null) ? 0 : contentTypeCode_localized.hashCode());
            result = prime * result + ((description == null) ? 0 : description.hashCode());
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            result = prime * result + ((intendedRecipient == null) ? 0 : intendedRecipient.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + ((patientId == null) ? 0 : patientId.hashCode());
            result = prime * result + ((sourceId == null) ? 0 : sourceId.hashCode());
            result = prime * result + ((submissionTime == null) ? 0 : submissionTime.hashCode());
            result = prime * result + ((uniqueId == null) ? 0 : uniqueId.hashCode());
            return result;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof SubmissionSet)) {
                return false;
            }
            SubmissionSet other = (SubmissionSet) obj;
            if (authorInstitution == null) {
                if (other.authorInstitution != null) {
                    return false;
                }
            } else if (!authorInstitution.equals(other.authorInstitution)) {
                return false;
            }
            if (authorPerson == null) {
                if (other.authorPerson != null) {
                    return false;
                }
            } else if (!authorPerson.equals(other.authorPerson)) {
                return false;
            }
            if (authorRole == null) {
                if (other.authorRole != null) {
                    return false;
                }
            } else if (!authorRole.equals(other.authorRole)) {
                return false;
            }
            if (authorSpecialty == null) {
                if (other.authorSpecialty != null) {
                    return false;
                }
            } else if (!authorSpecialty.equals(other.authorSpecialty)) {
                return false;
            }
            if (authorTelecommunication == null) {
                if (other.authorTelecommunication != null) {
                    return false;
                }
            } else if (!authorTelecommunication.equals(other.authorTelecommunication)) {
                return false;
            }
            if (contentTypeCode == null) {
                if (other.contentTypeCode != null) {
                    return false;
                }
            } else if (!contentTypeCode.equals(other.contentTypeCode)) {
                return false;
            }
            if (contentTypeCode_localized == null) {
                if (other.contentTypeCode_localized != null) {
                    return false;
                }
            } else if (!contentTypeCode_localized.equals(other.contentTypeCode_localized)) {
                return false;
            }
            if (description == null) {
                if (other.description != null) {
                    return false;
                }
            } else if (!description.equals(other.description)) {
                return false;
            }
            if (id == null) {
                if (other.id != null) {
                    return false;
                }
            } else if (!id.equals(other.id)) {
                return false;
            }
            if (intendedRecipient == null) {
                if (other.intendedRecipient != null) {
                    return false;
                }
            } else if (!intendedRecipient.equals(other.intendedRecipient)) {
                return false;
            }
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }
            if (patientId == null) {
                if (other.patientId != null) {
                    return false;
                }
            } else if (!patientId.equals(other.patientId)) {
                return false;
            }
            if (sourceId == null) {
                if (other.sourceId != null) {
                    return false;
                }
            } else if (!sourceId.equals(other.sourceId)) {
                return false;
            }
            if (submissionTime == null) {
                if (other.submissionTime != null) {
                    return false;
                }
            } else if (!submissionTime.equals(other.submissionTime)) {
                return false;
            }
            if (uniqueId == null) {
                if (other.uniqueId != null) {
                    return false;
                }
            } else if (!uniqueId.equals(other.uniqueId)) {
                return false;
            }
            return true;
        }
    }
}
