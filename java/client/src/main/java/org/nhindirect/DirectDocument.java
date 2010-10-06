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

package org.nhindirect;

import java.util.List;
import java.util.logging.Logger;

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

import org.nhindirect.transform.util.XmlUtils;

/**
 * Abstract representation of a document with supporting metadata.
 * 
 * @author beau
 */
public class DirectDocument
{
    private Metadata metadata;
    private String data;

    private static final Logger LOGGER = Logger.getLogger(DirectDocument.class.getPackage().getName());

    /**
     * Default document constructor.
     */
    public DirectDocument()
    {
        this.metadata = new Metadata();
    }

    /**
     * Document constructor with specific metadata.
     * 
     * @param metadata
     *            The document metadata.
     */
    public DirectDocument(Metadata metadata)
    {
        this.metadata = metadata;
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
    public class Metadata
    {
        // TEMP
        private SubmitObjectsRequest _sor;

        private String mimeType;
        private String _objectType;
        private String _eot_id;

        private String creationTime;
        private String languageCode;
        private String serviceStartTime;
        private String serviceStopTime;
        private String sourcePatientId;
        private String sourcePatientInfo;

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

        private String ss_submissionTime;
        private String ss_intendedRecipient;

        private String ss_authorPerson;
        private String ss_authorInstitution;
        private String ss_authorRole;
        private String ss_authorSpecialty;

        private String contentTypeCode;
        private String contentType_localized;

        private String ss_uniqueId;
        private String ss_sourceId;
        private String ss_patientId;

        private String submissionSetStatus;

        public void generate()
        {
            throw new UnsupportedOperationException();
        }

        public void consume(String submitObjectsRequest) throws Exception
        {
            SubmitObjectsRequest sor = (SubmitObjectsRequest) XmlUtils.unmarshal(new String(submitObjectsRequest),
                    oasis.names.tc.ebxml_regrep.xsd.lcm._3.ObjectFactory.class);

            consume(sor);
        }

        public void consume(SubmitObjectsRequest submitObjectsRequest)
        {
            // TEMP
            this._sor = submitObjectsRequest;

            RegistryObjectListType rol = submitObjectsRequest.getRegistryObjectList();

            List<JAXBElement<? extends IdentifiableType>> elements = rol.getIdentifiable();

            for (JAXBElement<? extends IdentifiableType> element : elements)
            {
                if (element.getValue() instanceof oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType)
                {
                    ExtrinsicObjectType eot = (ExtrinsicObjectType) element.getValue();

                    mimeType = eot.getMimeType();
                    _objectType = eot.getObjectType();
                    _eot_id = eot.getId();

                    for (SlotType1 slot : eot.getSlot())
                    {
                        if (slot.getName().equals("creationTime"))
                        {
                            if (slotNotEmpty(slot))
                                creationTime = slot.getValueList().getValue().get(0);
                        }
                        else if (slot.getName().equals("languageCode"))
                        {
                            if (slotNotEmpty(slot))
                                languageCode = slot.getValueList().getValue().get(0);
                        }
                        else if (slot.getName().equals("serviceStartTime"))
                        {
                            if (slotNotEmpty(slot))
                                serviceStartTime = slot.getValueList().getValue().get(0);
                        }
                        else if (slot.getName().equals("serviceStopTime"))
                        {
                            if (slotNotEmpty(slot))
                                serviceStopTime = slot.getValueList().getValue().get(0);
                        }
                        else if (slot.getName().equals("sourcePatientId"))
                        {
                            if (slotNotEmpty(slot))
                                sourcePatientId = slot.getValueList().getValue().get(0);
                        }
                        else if (slot.getName().equals("sourcePatientInfo"))
                        {
                            // FIXME
                            sourcePatientInfo = "";

                            if (slotNotEmpty(slot))
                                for (String value : slot.getValueList().getValue())
                                {
                                    sourcePatientInfo += value;
                                }
                        }
                    }

                    for (ClassificationType ct : eot.getClassification())
                    {
                        if (ct.getClassificationScheme().equals("urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d"))
                        {
                            for (SlotType1 slot : ct.getSlot())
                            {
                                if (slot.getName().equals("authorPerson"))
                                {
                                    if (slotNotEmpty(slot))
                                        authorPerson = slot.getValueList().getValue().get(0);
                                }
                                else if (slot.getName().equals("authorInstitution"))
                                {
                                    if (slotNotEmpty(slot))
                                        authorInstitution = slot.getValueList().getValue().get(0);
                                }
                                else if (slot.getName().equals("authorRole"))
                                {
                                    if (slotNotEmpty(slot))
                                        authorRole = slot.getValueList().getValue().get(0);
                                }
                                else if (slot.getName().equals("authorSpecialty"))
                                {
                                    if (slotNotEmpty(slot))
                                        authorSpecialty = slot.getValueList().getValue().get(0);
                                }
                            }
                        }
                        else if (ct.getClassificationScheme().equals("urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a"))
                        {
                            for (SlotType1 slot : ct.getSlot())
                            {
                                if (slot.getName().equals("codingScheme"))
                                {
                                    if (slotNotEmpty(slot))
                                    {
                                        @SuppressWarnings("unused") String codingScheme = slot.getValueList()
                                                .getValue().get(0);
                                    }
                                }
                            }

                            classCode = ct.getNodeRepresentation();
                            classCode_localized = ct.getNodeRepresentation();
                        }
                        else if (ct.getClassificationScheme().equals("urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f"))
                        {
                            for (SlotType1 slot : ct.getSlot())
                            {
                                if (slot.getName().equals("codingScheme"))
                                {
                                    if (slotNotEmpty(slot))
                                    {
                                        @SuppressWarnings("unused") String codingScheme = slot.getValueList()
                                                .getValue().get(0);
                                    }
                                }
                            }

                            confidentialityCode = ct.getNodeRepresentation();
                            confidentialityCode_localized = ct.getNodeRepresentation();
                        }
                        else if (ct.getClassificationScheme().equals("urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d"))
                        {
                            for (SlotType1 slot : ct.getSlot())
                            {
                                if (slot.getName().equals("codingScheme"))
                                {
                                    if (slotNotEmpty(slot))
                                    {
                                        @SuppressWarnings("unused") String codingScheme = slot.getValueList()
                                                .getValue().get(0);
                                    }
                                }
                            }

                            formatCode = ct.getNodeRepresentation();
                            formatCode_localized = ct.getNodeRepresentation();
                        }
                        else if (ct.getClassificationScheme().equals("urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1"))
                        {
                            for (SlotType1 slot : ct.getSlot())
                            {
                                if (slot.getName().equals("codingScheme"))
                                {
                                    if (slotNotEmpty(slot))
                                    {
                                        @SuppressWarnings("unused") String codingScheme = slot.getValueList()
                                                .getValue().get(0);
                                    }
                                }
                            }

                            healthcareFacilityTypeCode = ct.getNodeRepresentation();
                            healthcareFacilityTypeCode_localized = ct.getNodeRepresentation();
                        }
                        else if (ct.getClassificationScheme().equals("urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead"))
                        {
                            for (SlotType1 slot : ct.getSlot())
                            {
                                if (slot.getName().equals("codingScheme"))
                                {
                                    if (slotNotEmpty(slot))
                                    {
                                        @SuppressWarnings("unused") String codingScheme = slot.getValueList()
                                                .getValue().get(0);
                                    }
                                }
                            }

                            practiceSettingCode = ct.getNodeRepresentation();
                            practiceSettingCode_localized = ct.getNodeRepresentation();
                        }
                        else if (ct.getClassificationScheme().equals("urn:uuid:f0306f51-975f-434e-a61c-c59651d33983"))
                        {
                            for (SlotType1 slot : ct.getSlot())
                            {
                                if (slot.getName().equals("codingScheme"))
                                {
                                    if (slotNotEmpty(slot))
                                    {
                                        @SuppressWarnings("unused") String codingScheme = slot.getValueList()
                                                .getValue().get(0);
                                    }
                                }
                            }

                            loinc = ct.getNodeRepresentation();
                            loinc_localized = ct.getNodeRepresentation();
                        }
                    }

                    for (ExternalIdentifierType eit : eot.getExternalIdentifier())
                    {
                        if (eit.getIdentificationScheme().equals("urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427"))
                        {
                            patientId = eit.getValue();
                        }
                        else if (eit.getIdentificationScheme().equals("urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab"))
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
                        if (slot.getName().equals("submissionTime"))
                        {
                            if (slotNotEmpty(slot))
                                ss_submissionTime = slot.getValueList().getValue().get(0);
                        }
                        else if (slot.getName().equals("intendedRecipient"))
                        {
                            if (slotNotEmpty(slot))
                                ss_intendedRecipient = slot.getValueList().getValue().get(0);
                        }
                    }

                    for (ClassificationType ct : rpt.getClassification())
                    {
                        if (ct.getClassificationScheme().equals("urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d"))
                        {
                            for (SlotType1 slot : ct.getSlot())
                            {
                                if (slot.getName().equals("authorPerson"))
                                {
                                    if (slotNotEmpty(slot))
                                        ss_authorPerson = slot.getValueList().getValue().get(0);
                                }
                                else if (slot.getName().equals("authorInstitution"))
                                {
                                    if (slotNotEmpty(slot))
                                        ss_authorInstitution = slot.getValueList().getValue().get(0);
                                    // TODO: this had two values
                                }
                                else if (slot.getName().equals("authorRole"))
                                {
                                    if (slotNotEmpty(slot))
                                        ss_authorRole = slot.getValueList().getValue().get(0);
                                }
                                else if (slot.getName().equals("authorSpecialty"))
                                {
                                    if (slotNotEmpty(slot))
                                        ss_authorSpecialty = slot.getValueList().getValue().get(0);
                                }
                            }
                        }
                        if (ct.getClassificationScheme().equals("urn:uuid:aa543740-bdda-424e-8c96-df4873be8500"))
                        {
                            for (SlotType1 slot : ct.getSlot())
                            {
                                if (slot.getName().equals("codingScheme"))
                                {
                                    if (slotNotEmpty(slot))
                                    {
                                        @SuppressWarnings("unused") String codingScheme = slot.getValueList()
                                                .getValue().get(0);
                                    }
                                }
                            }

                            contentTypeCode = ct.getNodeRepresentation();
                            contentType_localized = ct.getName().getLocalizedString().get(0).getValue();
                        }
                    }

                    for (ExternalIdentifierType eit : rpt.getExternalIdentifier())
                    {
                        if (eit.getIdentificationScheme().equals("urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8"))
                        {
                            ss_uniqueId = eit.getValue();
                        }
                        else if (eit.getIdentificationScheme().equals("urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832"))
                        {
                            ss_sourceId = eit.getValue();
                        }
                        else if (eit.getIdentificationScheme().equals("urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446"))
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
                        if (slot.getName().equals("SubmissionSetStatus"))
                        {
                            if (slotNotEmpty(slot))
                                submissionSetStatus = slot.getName();
                        }
                    }
                }
            }
        }

        public SubmitObjectsRequest getAsSubmitObjectsRequest()
        {
            return _sor;
        }

        public String getAsString()
        {
            QName qname = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0", "SubmitObjectsRequest");
            return XmlUtils.marshal(qname, getAsSubmitObjectsRequest(), ihe.iti.xds_b._2007.ObjectFactory.class);
        }

        public void printValues()
        {
            LOGGER.info("  mimeType                             " + mimeType);
            LOGGER.info("  _objectType                          " + _objectType);
            LOGGER.info("  _eot_id                              " + _eot_id);
            LOGGER.info("  creationTime                         " + creationTime);
            LOGGER.info("  languageCode                         " + languageCode);
            LOGGER.info("  serviceStartTime                     " + serviceStartTime);
            LOGGER.info("  serviceStopTime                      " + serviceStopTime);
            LOGGER.info("  sourcePatientId                      " + sourcePatientId);
            LOGGER.info("  sourcePatientInfo                    " + sourcePatientInfo);
            LOGGER.info("  authorPerson                         " + authorPerson);
            LOGGER.info("  authorInstitution                    " + authorInstitution);
            LOGGER.info("  authorRole                           " + authorRole);
            LOGGER.info("  authorSpecialty                      " + authorSpecialty);
            LOGGER.info("  classCode                            " + classCode);
            LOGGER.info("  classCode_localized                  " + classCode_localized);
            LOGGER.info("  confidentialityCode                  " + confidentialityCode);
            LOGGER.info("  confidentialityCode_localized        " + confidentialityCode_localized);
            LOGGER.info("  formatCode                           " + formatCode);
            LOGGER.info("  formatCode_localized                 " + formatCode_localized);
            LOGGER.info("  healthcareFacilityTypeCode           " + healthcareFacilityTypeCode);
            LOGGER.info("  healthcareFacilityTypeCode_localized " + healthcareFacilityTypeCode_localized);
            LOGGER.info("  practiceSettingCode                  " + practiceSettingCode);
            LOGGER.info("  practiceSettingCode_localized        " + practiceSettingCode_localized);
            LOGGER.info("  loinc                                " + loinc);
            LOGGER.info("  loinc_localized                      " + loinc_localized);
            LOGGER.info("  patientId                            " + patientId);
            LOGGER.info("  uniqueId                             " + uniqueId);
            LOGGER.info("  _rpt_id                              " + _rpt_id);
            LOGGER.info("  _rpt_name                            " + _rpt_name);
            LOGGER.info("  _rpt_description                     " + _rpt_description);
            LOGGER.info("  ss_submissionTime                    " + ss_submissionTime);
            LOGGER.info("  ss_intendedRecipient                 " + ss_intendedRecipient);
            LOGGER.info("  ss_authorPerson                      " + ss_authorPerson);
            LOGGER.info("  ss_authorInstitution                 " + ss_authorInstitution);
            LOGGER.info("  ss_authorRole                        " + ss_authorRole);
            LOGGER.info("  ss_authorSpecialty                   " + ss_authorSpecialty);
            LOGGER.info("  contentTypeCode                      " + contentTypeCode);
            LOGGER.info("  contentType_localized                " + contentType_localized);
            LOGGER.info("  ss_uniqueId                          " + ss_uniqueId);
            LOGGER.info("  ss_sourceId                          " + ss_sourceId);
            LOGGER.info("  ss_patientId                         " + ss_patientId);
            LOGGER.info("  submissionSetStatus                  " + submissionSetStatus);
        }

        private boolean slotNotEmpty(SlotType1 slot)
        {
            if (slot.getValueList() != null && slot.getValueList().getValue() != null
                    && !slot.getValueList().getValue().isEmpty())
                return true;

            return false;
        }
    }
}
