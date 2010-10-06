/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Arien Malec     arien.malec@nhindirect.org
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using NHINDirect.Metadata;

namespace NHINDirect.Xd
{
    /// <summary>
    /// The valid types of XD* Metadata attributes
    /// </summary>
    public enum XDAttribute
    {
#pragma warning disable 1591
        Author,
        ClassCode,
        Comments,
        ConfidentialityCode,
        CreationTime,
        EventCodeList,
        FormatCode,
        Hash,
        FacilityType,
        LanguageCode,
        LegalAuthenticator,
        MimeType,
        PatientId,
        PracticeSettingCode,
        ServiceStart,
        ServiceStop,
        Size,
        SourcePatientId,
        SourcePatientInfo,
        Title,
        TypeCode,
        UniqueId,
        Uri,
        ContentTypeCode
#pragma warning restore 1591
    }

    /// <summary>
    /// Constants and utility functions relating to XD* Metadata
    /// </summary>
    public class XDMetadataStandard
    {
        private static Dictionary<XDAttribute,string> m_classifications = new Dictionary<XDAttribute, string>
        {
            {XDAttribute.Author,DocumentAuthorUUID},
            {XDAttribute.EventCodeList, EventCodeUUID },
            {XDAttribute.ClassCode,DocumentClassUUID },
            {XDAttribute.ConfidentialityCode, DocumentConfidentialityUUID },
            {XDAttribute.FormatCode, FormatCodeUUID },
            {XDAttribute.FacilityType, FacilityCodeUUID },
            {XDAttribute.PracticeSettingCode, PracticeSettingUUID },
            {XDAttribute.TypeCode, DocumentTypeUUID },
            {XDAttribute.ContentTypeCode, ContentTypeCodeUUID},
        };

        /// <summary>
        /// A dictionary mapping attributes UUID
        /// </summary>
        public static Dictionary<XDAttribute, string> ClassificationUUIDs { get { return m_classifications; } }

        /// <summary>
        /// Tests if the XD metadata attribute is represented by an ebXML classification
        /// </summary>
        public static bool IsClassification(XDAttribute attr)
        {
            return ClassificationUUIDs.ContainsKey(attr);
        }

        /// <summary>
        /// Element name for a DocumentEntry object (ExtrinsicObject)
        /// </summary>
        public const string DocumentEntryElement = "ExtrinsicObject";


        // UUIDs

        /// <summary>
        /// UUID for a Submission Set classification
        /// </summary>
        public const string SubmissionSetClassificationUUID = "urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd";

        /// <summary>
        /// UUID for a document entry RegistryPackageType
        /// </summary>
        public const string DocumentEntryUUID = "urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1";

        /// <summary>
        /// UUID for an author classification at the document level
        /// </summary>
        public const string DocumentAuthorUUID = "urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d";


        /// <summary>
        /// UUID for an author classification at the submission set level
        /// </summary>
        public const string SubmissionSetAuthorUUID = "urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d";

        /// <summary>
        /// UUID for a document class code classification
        /// </summary>
        public const string DocumentClassUUID = "urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a";

        /// <summary>
        /// UUID for a document confidentiality code
        /// </summary>
        public const string DocumentConfidentialityUUID = "urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f";

        /// <summary>
        /// UUID for an event code
        /// </summary>
        public const string EventCodeUUID = "urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4";
        /// <summary>
        /// UUID for a format code
        /// </summary>
        public const string FormatCodeUUID = "urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d";

        /// <summary>
        /// UUID for a facility code
        /// </summary>
        public const string FacilityCodeUUID = "urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1";

        /// <summary>
        /// UUID for a patient identifier extenral identity scheme.
        /// </summary>
        public const string DocumentEntryPatientIdentitySchemeUUID = "urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427";


        /// <summary>
        /// UUID for a unique ID identity scheme for a document
        /// </summary>
        public const string DocumentUniqueIdIdentitySchemeUUID = "urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab";


        /// <summary>
        /// UUID for a unique ID identity scheme for a submission set
        /// </summary>
        public const string SubmissionSetPatientIdUUID = "urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446";

        /// <summary>
        /// UUID for a source ID for a submission set
        /// </summary>
        public const string SubmissionSetSourceIdUUID = "urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832";

        /// <summary>
        /// UUID for a submission set unique ID external identifier
        /// </summary>
        public const string SubmissionSetUniqueIdUUID = "urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8";

        /// <summary>
        /// UUID for a practice setting classification scheme
        /// </summary>
        public const string PracticeSettingUUID = "urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead";

        /// <summary>
        /// UUID for a document typeCode
        /// </summary>
        public const string DocumentTypeUUID = "urn:uuid:f0306f51-975f-434e-a61c-c59651d33983";

        /// <summary>
        /// UUID for a submission set content type code
        /// </summary>
        public const string ContentTypeCodeUUID = "urn:uuid:aa543740-bdda-424e-8c96-df4873be8500";


        // Element Names

        /// <summary>
        /// The element name for a classification
        /// </summary>
        public const string ClassificationElt = "Classification";

        /// <summary>
        /// The element name for a slot.
        /// </summary>
        public const string SlotElt = "Slot";

        /// <summary>
        /// Element name for a Name element
        /// </summary>
        public const string NameElt = "Name"; 

        // Attribute names

        /// <summary>
        /// The attribute name for a slot name.
        /// </summary>
        public const string SlotNameAttr = "name";

        /// <summary>
        /// The attribute name for a classification scheme
        /// </summary>
        public const string ClassificationSchemeAttr = "classificationScheme";

        /// <summary>
        /// The attribute name for a node representation
        /// </summary>
        public const string NodeRepresentationAttr = "nodeRepresentation";


        /// <summary>
        /// The attribute name for object type
        /// </summary>
        public const string ObjectTypeAttr = "objectType";


        // Slot names

        /// <summary>
        /// XD* Metadata standard slot names
        /// </summary>
        public struct Slots
        {

            /// <summary>
            /// The slot name for an authorPerson
            /// </summary>
            public const string AuthorPerson = "authorPerson";

            /// <summary>
            /// The slot name for an authorInstition
            /// </summary>
            public const string AuthorInstitutions = "authorInstitution";

            /// <summary>
            /// The slot name for an authorRole
            /// </summary>
            public const string AuthorRoles = "authorRole";

            /// <summary>
            /// The slot name for authorSpecialty
            /// </summary>
            public const string AuthorSpecialities = "authorSpecialty";

            /// <summary>
            /// The slot name for hash
            /// </summary>
            public const string Hash = "hash";

            /// <summary>
            /// The slot name for a legal authenticator
            /// </summary>
            public const string LegalAuthenticator = "legalAuthenticator";

            /// <summary>
            /// The language code slot name
            /// </summary>
            public const string LanguageCode = "languageCode";

            /// <summary>
            /// The service start slot name
            /// </summary>
            public const string ServiceStart = "serviceStartTime";

            /// <summary>
            /// The service end slot name
            /// </summary>
            public const string ServiceStop = "serviceStopTime";

            /// <summary>
            /// The size slot name
            /// </summary>
            public const string Size = "size";

            /// <summary>
            /// The source patient ID slot name.
            /// </summary>
            public const string SourcePatientID = "sourcePatientId";

            /// <summary>
            /// The source patient Info slot name
            /// </summary>
            public const string SourcePatientInfo = "sourcePatientInfo";

            /// <summary>
            /// The creation time slot name
            /// </summary>
            public const string CreationTime = "creationTime";

            /// <summary>
            /// The coding scheme slot name
            /// </summary>
            public const string CodingScheme = "codingScheme";

            /// <summary>
            /// The slot name for a URI
            /// </summary>
            public const string Uri = "URI";

            /// <summary>
            /// The slot name for a submission time
            /// </summary>
            public const string SubmissionTime = "submissionTime";

            /// <summary>
            /// The slot name for intended recipient
            /// </summary>
            public const string IntendedRecipient = "intendedRecipient";
        }
        
        // Object types

        /// <summary>
        /// Object type for an external identifier
        /// </summary>
        public const string ExternalIdentifierObjectType = "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier";
    }
}
