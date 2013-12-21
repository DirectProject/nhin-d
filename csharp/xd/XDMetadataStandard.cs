/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Arien Malec     arien.malec@nhindirect.org
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System.Collections.Generic;

namespace Health.Direct.Xd
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
                                                                                  {XDAttribute.Author,UUIDs.DocumentAuthor},
                                                                                  {XDAttribute.EventCodeList, UUIDs.EventCode },
                                                                                  {XDAttribute.ClassCode,UUIDs.DocumentClass },
                                                                                  {XDAttribute.ConfidentialityCode, UUIDs.DocumentConfidentiality },
                                                                                  {XDAttribute.FormatCode, UUIDs.FormatCode },
                                                                                  {XDAttribute.FacilityType, UUIDs.FacilityCode },
                                                                                  {XDAttribute.PracticeSettingCode, UUIDs.PracticeSetting },
                                                                                  {XDAttribute.TypeCode, UUIDs.DocumentType },
                                                                                  {XDAttribute.ContentTypeCode, UUIDs.ContentTypeCode},
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

        // UUIDs

        /// <summary>
        /// XD* Metadata standard UUIDs.
        /// </summary>
        public struct UUIDs
        {

            /// <summary>
            /// UUID for a Submission Set classification
            /// </summary>
            public const string SubmissionSetClassification = "urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd";

            /// <summary>
            /// UUID for a document entry RegistryPackageType
            /// </summary>
            public const string DocumentEntry = "urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1";

            /// <summary>
            /// UUID for an author classification at the document level
            /// </summary>
            public const string DocumentAuthor = "urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d";


            /// <summary>
            /// UUID for an author classification at the submission set level
            /// </summary>
            public const string SubmissionSetAuthor = "urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d";

            /// <summary>
            /// UUID for a document class code classification
            /// </summary>
            public const string DocumentClass = "urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a";

            /// <summary>
            /// UUID for a document confidentiality code
            /// </summary>
            public const string DocumentConfidentiality = "urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f";

            /// <summary>
            /// UUID for an event code
            /// </summary>
            public const string EventCode = "urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4";
            /// <summary>
            /// UUID for a format code
            /// </summary>
            public const string FormatCode = "urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d";

            /// <summary>
            /// UUID for a facility code
            /// </summary>
            public const string FacilityCode = "urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1";

            /// <summary>
            /// UUID for a patient identifier extenral identity scheme.
            /// </summary>
            public const string DocumentEntryPatientIdentityScheme = "urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427";


            /// <summary>
            /// UUID for a unique ID identity scheme for a document
            /// </summary>
            public const string DocumentUniqueIdIdentityScheme = "urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab";


            /// <summary>
            /// UUID for a unique ID identity scheme for a submission set
            /// </summary>
            public const string SubmissionSetPatientId = "urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446";

            /// <summary>
            /// UUID for a source ID for a submission set
            /// </summary>
            public const string SubmissionSetSourceId = "urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832";

            /// <summary>
            /// UUID for a submission set unique ID external identifier
            /// </summary>
            public const string SubmissionSetUniqueId = "urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8";

            /// <summary>
            /// UUID for a practice setting classification scheme
            /// </summary>
            public const string PracticeSetting = "urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead";

            /// <summary>
            /// UUID for a document typeCode
            /// </summary>
            public const string DocumentType = "urn:uuid:f0306f51-975f-434e-a61c-c59651d33983";

            /// <summary>
            /// UUID for a submission set content type code
            /// </summary>
            public const string ContentTypeCode = "urn:uuid:aa543740-bdda-424e-8c96-df4873be8500";

        }



        /// <summary>
        /// XD*/ebXML standard XML namespaces
        /// </summary>
        public struct Ns
        {
            /// <summary>
            /// Registry Information Model namespace
            /// </summary>
            public const string Rim = "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0";

            /// <summary>
            /// LCM namespace
            /// </summary>
            public const string Lcm = "urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0";
        }

        // Element Names

        /// <summary>
        /// XD* Metadata standard Element names
        /// </summary>
        public struct Elts
        {

            /// <summary>
            /// Element name for a SubmitObjectsRequest root element
            /// </summary>
            public const string SubmitObjectsRequest = "SubmitObjectsRequest";

            /// <summary>
            /// Element name for RegistryObjectsList
            /// </summary>
            public const string RegistryObjectsList = "RegistryObjectList";

            /// <summary>
            /// Element name for a DocumentEntry object (ExtrinsicObject)
            /// </summary>
            public const string DocumentEntry = "ExtrinsicObject";

            /// <summary>
            /// Element name for a submission set (RegistryPackage)
            /// </summary>
            public const string SubmissionSet = "RegistryPackage";

            /// <summary>
            /// The element name for a classification
            /// </summary>
            public const string Classification = "Classification";

            /// <summary>
            /// The element name for a slot.
            /// </summary>
            public const string Slot = "Slot";

            /// <summary>
            /// Element name for a Name element
            /// </summary>
            public const string Name = "Name";

            /// <summary>
            /// Element name for a Description element
            /// </summary>
            public const string Description = "Description";

            /// <summary>
            /// Element name for a LocalizedString element
            /// </summary>
            public const string LocalizedString = "LocalizedString";

            /// <summary>
            /// Element name for an association
            /// </summary>
            public const string Association = "Association";

        }

        /// <summary>
        /// XD* Metadata standard attribute names
        /// </summary>
        public struct Attrs
        {

            /// <summary>
            /// The attribute name for XML id
            /// </summary>
            public const string Id = "id";

            /// <summary>
            /// The attribute name for document media type
            /// </summary>
            public const string MimeType = "mimeType";
            /// <summary>
            /// The attribute name for a slot name.
            /// </summary>
            public const string SlotName = "name";

            /// <summary>
            /// The attribute name for a classification scheme
            /// </summary>
            public const string ClassificationScheme = "classificationScheme";

            /// <summary>
            /// The attribute name for a classification node
            /// </summary>
            public const string ClassificationNode = "classificationNode";
            /// <summary>
            /// The attribute name for a node representation
            /// </summary>
            public const string NodeRepresentation = "nodeRepresentation";

            /// <summary>
            /// The attribute name for a classified object
            /// </summary>
            public const string ClassifiedObject = "classifiedObject";

            /// <summary>
            /// The attribute name for object type
            /// </summary>
            public const string ObjectType = "objectType";

            /// <summary>
            /// Association type attribute
            /// </summary>
            public const string AssociationType = "associationType";

            /// <summary>
            /// Association source object attribute
            /// </summary>
            public const string SourceObject = "sourceObject";

            /// <summary>
            /// Association target object attribute
            /// </summary>
            public const string TargetObject = "targetObject";

        }
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

            /// <summary>
            /// The slot name for submission set status
            /// </summary>
            public const string SubmissionSetStatus = "SubmissionSetStatus";
        }
        
        // Object types

        /// <summary>
        /// Object type for an external identifier
        /// </summary>
        public const string ExternalIdentifierObjectType = "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier";
    }
}