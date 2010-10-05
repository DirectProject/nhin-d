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

/**
 * TODO: Implement this class (metadata, specifically)
 * 
 * @author beau
 */
public class DirectDocument
{
    private Metadata metadata;
    private String data;

    public DirectDocument()
    {
        this.metadata = new Metadata();
    }

    public DirectDocument(Metadata metadata)
    {
        this.metadata = metadata;
    }

    /**
     * @return the metadata
     */
    public Metadata getMetadata()
    {
        return metadata;
    }

    /**
     * @param metadata
     *            the metadata to set
     */
    public void setMetadata(Metadata metadata)
    {
        this.metadata = metadata;
    }

    /**
     * @return the data
     */
    public String getData()
    {
        return data;
    }

    /**
     * @param data
     *            the data to set
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
        private String document_entryUUID; /* R */
        private String document_mimeType; /* R */
        private String document_uniqueId; /* R */

        private String document_author;
        private String document_classCode;
        private String document_confidentialityCode;
        private String document_creationTime;
        private String document_formatCode;
        private String document_healthcareFacilityTypeCode;
        private String document_languageCode;
        private String document_patientId;
        private String document_practiceSettingCode;
        private String document_sourcePatientId;
        private String document_sourcePatientInfo;
        private String document_typeCode;

        private String submission_author; /* R */
        private String submision_entryUUID; /* R */
        private String submission_intendedRecipient; /* R */
        private String submission_sourceId; /* R */
        private String submission_submissionTime; /* R */
        private String submission_uniqueId; /* R */

        private String submission_contentTypeCode;
        private String submission_patientId;

        private String xml;

        /**
         * @return the document_entryUUID
         */
        public String getDocument_entryUUID()
        {
            return document_entryUUID;
        }

        /**
         * @param documentEntryUUID
         *            the document_entryUUID to set
         */
        public void setDocument_entryUUID(String documentEntryUUID)
        {
            document_entryUUID = documentEntryUUID;
        }

        /**
         * @return the document_mimeType
         */
        public String getDocument_mimeType()
        {
            return document_mimeType;
        }

        /**
         * @param documentMimeType
         *            the document_mimeType to set
         */
        public void setDocument_mimeType(String documentMimeType)
        {
            document_mimeType = documentMimeType;
        }

        /**
         * @return the document_uniqueId
         */
        public String getDocument_uniqueId()
        {
            return document_uniqueId;
        }

        /**
         * @param documentUniqueId
         *            the document_uniqueId to set
         */
        public void setDocument_uniqueId(String documentUniqueId)
        {
            document_uniqueId = documentUniqueId;
        }

        /**
         * @return the document_author
         */
        public String getDocument_author()
        {
            return document_author;
        }

        /**
         * @param documentAuthor
         *            the document_author to set
         */
        public void setDocument_author(String documentAuthor)
        {
            document_author = documentAuthor;
        }

        /**
         * @return the document_classCode
         */
        public String getDocument_classCode()
        {
            return document_classCode;
        }

        /**
         * @param documentClassCode
         *            the document_classCode to set
         */
        public void setDocument_classCode(String documentClassCode)
        {
            document_classCode = documentClassCode;
        }

        /**
         * @return the document_confidentialityCode
         */
        public String getDocument_confidentialityCode()
        {
            return document_confidentialityCode;
        }

        /**
         * @param documentConfidentialityCode
         *            the document_confidentialityCode to set
         */
        public void setDocument_confidentialityCode(String documentConfidentialityCode)
        {
            document_confidentialityCode = documentConfidentialityCode;
        }

        /**
         * @return the document_creationTime
         */
        public String getDocument_creationTime()
        {
            return document_creationTime;
        }

        /**
         * @param documentCreationTime
         *            the document_creationTime to set
         */
        public void setDocument_creationTime(String documentCreationTime)
        {
            document_creationTime = documentCreationTime;
        }

        /**
         * @return the document_formatCode
         */
        public String getDocument_formatCode()
        {
            return document_formatCode;
        }

        /**
         * @param documentFormatCode
         *            the document_formatCode to set
         */
        public void setDocument_formatCode(String documentFormatCode)
        {
            document_formatCode = documentFormatCode;
        }

        /**
         * @return the document_healthcareFacilityTypeCode
         */
        public String getDocument_healthcareFacilityTypeCode()
        {
            return document_healthcareFacilityTypeCode;
        }

        /**
         * @param documentHealthcareFacilityTypeCode
         *            the document_healthcareFacilityTypeCode to set
         */
        public void setDocument_healthcareFacilityTypeCode(String documentHealthcareFacilityTypeCode)
        {
            document_healthcareFacilityTypeCode = documentHealthcareFacilityTypeCode;
        }

        /**
         * @return the document_languageCode
         */
        public String getDocument_languageCode()
        {
            return document_languageCode;
        }

        /**
         * @param documentLanguageCode
         *            the document_languageCode to set
         */
        public void setDocument_languageCode(String documentLanguageCode)
        {
            document_languageCode = documentLanguageCode;
        }

        /**
         * @return the document_patientId
         */
        public String getDocument_patientId()
        {
            return document_patientId;
        }

        /**
         * @param documentPatientId
         *            the document_patientId to set
         */
        public void setDocument_patientId(String documentPatientId)
        {
            document_patientId = documentPatientId;
        }

        /**
         * @return the document_practiceSettingCode
         */
        public String getDocument_practiceSettingCode()
        {
            return document_practiceSettingCode;
        }

        /**
         * @param documentPracticeSettingCode
         *            the document_practiceSettingCode to set
         */
        public void setDocument_practiceSettingCode(String documentPracticeSettingCode)
        {
            document_practiceSettingCode = documentPracticeSettingCode;
        }

        /**
         * @return the document_sourcePatientId
         */
        public String getDocument_sourcePatientId()
        {
            return document_sourcePatientId;
        }

        /**
         * @param documentSourcePatientId
         *            the document_sourcePatientId to set
         */
        public void setDocument_sourcePatientId(String documentSourcePatientId)
        {
            document_sourcePatientId = documentSourcePatientId;
        }

        /**
         * @return the document_sourcePatientInfo
         */
        public String getDocument_sourcePatientInfo()
        {
            return document_sourcePatientInfo;
        }

        /**
         * @param documentSourcePatientInfo
         *            the document_sourcePatientInfo to set
         */
        public void setDocument_sourcePatientInfo(String documentSourcePatientInfo)
        {
            document_sourcePatientInfo = documentSourcePatientInfo;
        }

        /**
         * @return the document_typeCode
         */
        public String getDocument_typeCode()
        {
            return document_typeCode;
        }

        /**
         * @param documentTypeCode
         *            the document_typeCode to set
         */
        public void setDocument_typeCode(String documentTypeCode)
        {
            document_typeCode = documentTypeCode;
        }

        /**
         * @return the submission_author
         */
        public String getSubmission_author()
        {
            return submission_author;
        }

        /**
         * @param submissionAuthor
         *            the submission_author to set
         */
        public void setSubmission_author(String submissionAuthor)
        {
            submission_author = submissionAuthor;
        }

        /**
         * @return the submision_entryUUID
         */
        public String getSubmision_entryUUID()
        {
            return submision_entryUUID;
        }

        /**
         * @param submisionEntryUUID
         *            the submision_entryUUID to set
         */
        public void setSubmision_entryUUID(String submisionEntryUUID)
        {
            submision_entryUUID = submisionEntryUUID;
        }

        /**
         * @return the submission_intendedRecipient
         */
        public String getSubmission_intendedRecipient()
        {
            return submission_intendedRecipient;
        }

        /**
         * @param submissionIntendedRecipient
         *            the submission_intendedRecipient to set
         */
        public void setSubmission_intendedRecipient(String submissionIntendedRecipient)
        {
            submission_intendedRecipient = submissionIntendedRecipient;
        }

        /**
         * @return the submission_sourceId
         */
        public String getSubmission_sourceId()
        {
            return submission_sourceId;
        }

        /**
         * @param submissionSourceId
         *            the submission_sourceId to set
         */
        public void setSubmission_sourceId(String submissionSourceId)
        {
            submission_sourceId = submissionSourceId;
        }

        /**
         * @return the submission_submissionTime
         */
        public String getSubmission_submissionTime()
        {
            return submission_submissionTime;
        }

        /**
         * @param submissionSubmissionTime
         *            the submission_submissionTime to set
         */
        public void setSubmission_submissionTime(String submissionSubmissionTime)
        {
            submission_submissionTime = submissionSubmissionTime;
        }

        /**
         * @return the submission_uniqueId
         */
        public String getSubmission_uniqueId()
        {
            return submission_uniqueId;
        }

        /**
         * @param submissionUniqueId
         *            the submission_uniqueId to set
         */
        public void setSubmission_uniqueId(String submissionUniqueId)
        {
            submission_uniqueId = submissionUniqueId;
        }

        /**
         * @return the submission_contentTypeCode
         */
        public String getSubmission_contentTypeCode()
        {
            return submission_contentTypeCode;
        }

        /**
         * @param submissionContentTypeCode
         *            the submission_contentTypeCode to set
         */
        public void setSubmission_contentTypeCode(String submissionContentTypeCode)
        {
            submission_contentTypeCode = submissionContentTypeCode;
        }

        /**
         * @return the submission_patientId
         */
        public String getSubmission_patientId()
        {
            return submission_patientId;
        }

        /**
         * @param submissionPatientId
         *            the submission_patientId to set
         */
        public void setSubmission_patientId(String submissionPatientId)
        {
            submission_patientId = submissionPatientId;
        }

        /**
         * @return the xml
         */
        public String getXml()
        {
            return xml;
        }

        /**
         * @param xml
         *            the xml to set
         */
        public void setXml(String xml)
        {
            this.xml = xml;
        }

    }

    public boolean isValid()
    {
        if (this.getMetadata().getDocument_entryUUID() == null 
                || this.getMetadata().getDocument_mimeType() == null
                || this.getMetadata().getDocument_uniqueId() == null
                || this.getMetadata().getSubmission_author() == null
                || this.getMetadata().getSubmision_entryUUID() == null
                || this.getMetadata().getSubmission_intendedRecipient() == null
                || this.getMetadata().getSubmission_sourceId() == null
                || this.getMetadata().getSubmission_submissionTime() == null
                || this.getMetadata().getSubmission_uniqueId() == null)
        {
            return false;
        }

        return true;
    }
}
