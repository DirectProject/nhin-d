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
using System;
using System.Collections.Generic;
using System.Linq;
using System.Xml.Linq;

using Health.Direct.Common.Metadata;

namespace Health.Direct.Xd.Tests
{
    public static class Examples
    {
        /// <summary>
        /// An XML file that validates against the NIST validator
        /// </summary>
        const string VALID_XML = @"<tns:SubmitObjectsRequest xmlns:tns=""urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0""> <RegistryObjectList xmlns=""urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0""> <ObjectRef id=""urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd""></ObjectRef><ObjectRef id=""urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446""></ObjectRef> <ObjectRef id=""urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832""></ObjectRef> <ObjectRef id=""urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8""></ObjectRef> <ObjectRef id=""urn:uuid:aa543740-bdda-424e-8c96-df4873be8500""></ObjectRef> <ObjectRef id=""urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d""></ObjectRef> <ObjectRef id=""urn:uuid:d9d542f3-6cc4-48b6-8870-ea235fbc94c2""></ObjectRef> <ObjectRef id=""urn:uuid:75df8f67-9973-4fbe-a900-df66cefecc5a""></ObjectRef> <ObjectRef id=""urn:uuid:f64ffdf0-4b97-4e06-b79f-a52b38ec2f8a""></ObjectRef> <ObjectRef id=""urn:uuid:1ba97051-7806-41a8-a48b-8fce7af683c5""></ObjectRef> <ObjectRef id=""urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab""></ObjectRef> <ObjectRef id=""urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427""></ObjectRef> <ObjectRef id=""urn:uuid:f0306f51-975f-434e-a61c-c59651d33983""></ObjectRef> <ObjectRef id=""urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead""></ObjectRef> <ObjectRef id=""urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1""></ObjectRef> <ObjectRef id=""urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d""></ObjectRef> <ObjectRef id=""urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f""></ObjectRef> <ObjectRef id=""urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4""></ObjectRef> <ObjectRef id=""urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a""></ObjectRef> <ObjectRef id=""urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d""></ObjectRef> <ObjectRef id=""urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1""></ObjectRef><ExtrinsicObject id=""urn:uuid:fc104250-3992-4c43-94e2-aa77fa4e2f99"" mimeType=""text/plain"" objectType=""urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1""> <Slot name=""creationTime""> <ValueList> <Value>20080701</Value> </ValueList> </Slot> <Slot name=""languageCode""> <ValueList> <Value>en-us</Value> </ValueList> </Slot> <Slot name=""serviceStartTime""> <ValueList> <Value>200806281100</Value> </ValueList> </Slot> <Slot name=""serviceStopTime""> <ValueList> <Value>200806281500</Value> </ValueList> </Slot> <Slot name=""sourcePatientId""> <ValueList> <Value>89765a87b^^^&amp;fj34r&amp;abc</Value> </ValueList> </Slot> <Slot name=""sourcePatientInfo""> <ValueList> <Value>PID-3|498ef443e7ac4a6^^^&amp;1.3.6.1.4.1.21367.2005.3.7&amp;ISO</Value> <Value>PID-5|498ef443e7ac4a6^Doe^Joe^^</Value> <Value>PID-7|19560527</Value> <Value>PID-8|M</Value> <Value>PID-11|100 Main St Metropolis^^Chicago^IL^44130^USA</Value> </ValueList> </Slot> <Name> <LocalizedString value=""Test""></LocalizedString> </Name> <Description> <LocalizedString value=""Test document""></LocalizedString> </Description> <Classification id=""urn:uuid:e5fc43c1-971e-4d12-81b7-b0ccbf11429a"" classificationScheme=""urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d"" classifiedObject=""urn:uuid:fc104250-3992-4c43-94e2-aa77fa4e2f99"" nodeRepresentation=""""> <Slot name=""authorPerson""> <ValueList> <Value>^Smitty^Gerald^^Dr^MD</Value> </ValueList> </Slot> <Slot name=""authorInstitution""> <ValueList> <Value>Clevland Clinic</Value> <Value>Parma Community</Value> </ValueList> </Slot> <Slot name=""authorRole""> <ValueList> <Value>Primary Surgon</Value> </ValueList> </Slot> <Slot name=""authorSpecialty""> <ValueList> <Value>Orthopedic</Value> </ValueList> </Slot> </Classification> <Classification id=""urn:uuid:c8063776-598a-40aa-bd67-670c28c568f3"" classificationScheme=""urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a"" classifiedObject=""urn:uuid:fc104250-3992-4c43-94e2-aa77fa4e2f99"" nodeRepresentation=""Operative""> <Slot name=""codingScheme""> <ValueList> <Value>Connect-a-thon classCodes</Value> </ValueList> </Slot> <Name> <LocalizedString value=""Operative""></LocalizedString> </Name> </Classification> <Classification id=""urn:uuid:4eda17ba-84ec-4785-94b6-f052ce7f7c7e"" classificationScheme=""urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f"" classifiedObject=""urn:uuid:fc104250-3992-4c43-94e2-aa77fa4e2f99"" nodeRepresentation=""1.3.6.1.4.1.21367.2006.7.104""> <Slot name=""codingScheme""> <ValueList> <Value>Connect-a-thon confidentialityCodes</Value> </ValueList> </Slot> <Name> <LocalizedString value=""Solar Drug Trial""></LocalizedString> </Name> </Classification> <Classification id=""urn:uuid:19f47eed-9c7a-4b8a-a1d4-87be37dafbd9"" classificationScheme=""urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d"" classifiedObject=""urn:uuid:fc104250-3992-4c43-94e2-aa77fa4e2f99"" nodeRepresentation=""CDAR2/IHE 1.0""> <Slot name=""codingScheme""> <ValueList> <Value>Connect-a-thon formatCodes</Value> </ValueList> </Slot> <Name> <LocalizedString value=""CDAR2/IHE 1.0""></LocalizedString> </Name> </Classification> <Classification id=""urn:uuid:a7fd06b1-e2ad-46b6-9fe6-112f09c2338b"" classificationScheme=""urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1"" classifiedObject=""urn:uuid:fc104250-3992-4c43-94e2-aa77fa4e2f99"" nodeRepresentation=""Hospital Setting""> <Slot name=""codingScheme""> <ValueList> <Value>Connect-a-thon healthcareFacilityTypeCodes</Value> </ValueList> </Slot> <Name> <LocalizedString value=""Hospital Setting""></LocalizedString> </Name> </Classification> <Classification id=""urn:uuid:517e636c-2f36-4f2d-bf36-db49c6939aa4"" classificationScheme=""urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead"" classifiedObject=""urn:uuid:fc104250-3992-4c43-94e2-aa77fa4e2f99"" nodeRepresentation=""Laboratory""> <Slot name=""codingScheme""> <ValueList> <Value>Connect-a-thon practiceSettingCodes</Value> </ValueList> </Slot> <Name> <LocalizedString value=""Laboratory""></LocalizedString> </Name> </Classification> <Classification id=""urn:uuid:f23227a4-28c2-476b-94ee-b59dffe0d0b6"" classificationScheme=""urn:uuid:f0306f51-975f-434e-a61c-c59651d33983"" classifiedObject=""urn:uuid:fc104250-3992-4c43-94e2-aa77fa4e2f99"" nodeRepresentation=""Laboratory Report""> <Slot name=""codingScheme""> <ValueList> <Value>Connect-a-thon TypeCode</Value> </ValueList> </Slot> <Name> <LocalizedString value=""Laboratory Report""></LocalizedString> </Name> </Classification> <ExternalIdentifier id=""urn:uuid:393e719b-bbf3-4706-89fd-11a49bc3dc10"" registryObject=""urn:uuid:fc104250-3992-4c43-94e2-aa77fa4e2f99"" identificationScheme=""urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427"" value=""498ef443e7ac4a6^^^&amp;1.3.6.1.4.1.21367.2005.3.7&amp;ISO""> <Name> <LocalizedString value=""XDSDocumentEntry.patientId""></LocalizedString> </Name> </ExternalIdentifier> <ExternalIdentifier id=""urn:uuid:832871ce-1ecd-46fd-aa55-dd55eb7f4979"" registryObject=""urn:uuid:fc104250-3992-4c43-94e2-aa77fa4e2f99"" identificationScheme=""urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab"" value=""1.3.6.1.4.1.21367.2005.3.3.1""> <Name> <LocalizedString value=""XDSDocumentEntry.uniqueId""></LocalizedString> </Name> </ExternalIdentifier> </ExtrinsicObject><RegistryPackage id=""urn:uuid:0e888973-fb58-463b-9ea1-5d7e74d47439""> <Name> <LocalizedString value=""folder1""></LocalizedString> </Name> <Description> <LocalizedString value=""Test folder""></LocalizedString> </Description> <Classification id=""urn:uuid:adff66e9-916c-41e8-ac08-fca47736cf06"" classificationScheme=""urn:uuid:1ba97051-7806-41a8-a48b-8fce7af683c5"" classifiedObject=""urn:uuid:0e888973-fb58-463b-9ea1-5d7e74d47439"" nodeRepresentation=""Cardiology""> <Slot name=""codingScheme""> <ValueList> <Value>Connect-a-thon codeList</Value> </ValueList> </Slot> <Name> <LocalizedString value=""Cardiology""></LocalizedString> </Name> </Classification> <ExternalIdentifier id=""urn:uuid:3cff7103-d00c-4e3f-974d-1356ae211db0"" registryObject=""urn:uuid:0e888973-fb58-463b-9ea1-5d7e74d47439"" identificationScheme=""urn:uuid:f64ffdf0-4b97-4e06-b79f-a52b38ec2f8a"" value=""498ef443e7ac4a6^^^&amp;1.3.6.1.4.1.21367.2005.3.7&amp;ISO""> <Name> <LocalizedString value=""XDSFolder.patientId""></LocalizedString> </Name> </ExternalIdentifier> <ExternalIdentifier id=""urn:uuid:118dbac4-06ab-45a2-b0df-b3447e882032"" registryObject=""urn:uuid:0e888973-fb58-463b-9ea1-5d7e74d47439"" identificationScheme=""urn:uuid:75df8f67-9973-4fbe-a900-df66cefecc5a"" value=""1.3.6.1.4.1.21367.2005.8.8.1""> <Name> <LocalizedString value=""XDSFolder.uniqueId""></LocalizedString> </Name> </ExternalIdentifier> </RegistryPackage> <Classification id=""urn:uuid:a56b8725-a82d-4c01-b316-4b7517324d09"" classifiedObject=""urn:uuid:0e888973-fb58-463b-9ea1-5d7e74d47439"" classificationNode=""urn:uuid:d9d542f3-6cc4-48b6-8870-ea235fbc94c2""></Classification><RegistryPackage id=""urn:uuid:2247dd93-d206-48e4-94ca-af611f10e1c4""> <Slot name=""submissionTime""> <ValueList> <Value>20080826</Value> </ValueList> </Slot> <Name> <LocalizedString value=""Test 1""></LocalizedString> </Name> <Description> <LocalizedString value=""Test submission""></LocalizedString> </Description> <Classification id=""urn:uuid:0443feb6-e744-4b8f-ab26-89e0c816bfe1"" classificationScheme=""urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d"" classifiedObject=""urn:uuid:2247dd93-d206-48e4-94ca-af611f10e1c4"" nodeRepresentation=""""> <Slot name=""authorPerson""> <ValueList> <Value>^Smitty^Gerald^^Dr^MD</Value> </ValueList> </Slot> <Slot name=""authorInstitution""> <ValueList> <Value>Clevland Clinic</Value> <Value>Parma Community</Value> </ValueList> </Slot> <Slot name=""authorRole""> <ValueList> <Value>Primary Surgon</Value> </ValueList> </Slot> <Slot name=""authorSpecialty""> <ValueList> <Value>Orthopedic</Value> </ValueList> </Slot> </Classification> <Classification id=""urn:uuid:1847e406-df43-455d-9235-f712174683f9"" classificationScheme=""urn:uuid:aa543740-bdda-424e-8c96-df4873be8500"" classifiedObject=""urn:uuid:2247dd93-d206-48e4-94ca-af611f10e1c4"" nodeRepresentation=""Discharge summarization""> <Slot name=""codingScheme""> <ValueList> <Value>Connect-a-thon contentTypeCodes</Value> </ValueList> </Slot> <Name> <LocalizedString value=""Discharge summarization""></LocalizedString> </Name> </Classification> <ExternalIdentifier id=""urn:uuid:2e4f67b9-a7c8-40bc-88cc-435fac16f235"" registryObject=""urn:uuid:2247dd93-d206-48e4-94ca-af611f10e1c4"" identificationScheme=""urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8"" value=""1.3.6.1.4.1.21367.2005.3.1.1""> <Name> <LocalizedString value=""XDSSubmissionSet.uniqueId""></LocalizedString> </Name> </ExternalIdentifier> <ExternalIdentifier id=""urn:uuid:8ca620f6-5ef4-43a2-b68b-f12c0e0d36ed"" registryObject=""urn:uuid:2247dd93-d206-48e4-94ca-af611f10e1c4"" identificationScheme=""urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832"" value=""1.3.6.1.4.1.21367.2005.3.999.900""> <Name> <LocalizedString value=""XDSSubmissionSet.sourceId""></LocalizedString> </Name> </ExternalIdentifier> <ExternalIdentifier id=""urn:uuid:799bd713-f63b-4c43-9a32-3bd0ae4101e2"" registryObject=""urn:uuid:2247dd93-d206-48e4-94ca-af611f10e1c4"" identificationScheme=""urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446"" value=""498ef443e7ac4a6^^^&amp;1.3.6.1.4.1.21367.2005.3.7&amp;ISO""> <Name> <LocalizedString value=""XDSSubmissionSet.patientId""></LocalizedString> </Name> </ExternalIdentifier> </RegistryPackage> <Classification id=""urn:uuid:a4c776bf-c006-494d-ab42-cab74887b81b"" classifiedObject=""urn:uuid:2247dd93-d206-48e4-94ca-af611f10e1c4"" classificationNode=""urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd""></Classification> <Association id=""urn:uuid:97a5a89d-2acd-4312-a68e-2649c32859d8"" associationType=""urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember"" sourceObject=""urn:uuid:0e888973-fb58-463b-9ea1-5d7e74d47439"" targetObject=""urn:uuid:fc104250-3992-4c43-94e2-aa77fa4e2f99""> </Association> <Association id=""urn:uuid:311b2e9b-d9a3-4d0a-9c41-990a90e7aecf"" associationType=""urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember"" sourceObject=""urn:uuid:2247dd93-d206-48e4-94ca-af611f10e1c4"" targetObject=""urn:uuid:fc104250-3992-4c43-94e2-aa77fa4e2f99""> <Slot name=""SubmissionSetStatus""> <ValueList> <Value>Original</Value> </ValueList> </Slot> </Association> <Association id=""urn:uuid:b93163d6-81c7-4298-8f15-f82e77d8df6d"" associationType=""urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember"" sourceObject=""urn:uuid:2247dd93-d206-48e4-94ca-af611f10e1c4"" targetObject=""urn:uuid:0e888973-fb58-463b-9ea1-5d7e74d47439""> <Slot name=""SubmissionSetStatus""> <ValueList> <Value>Original</Value> </ValueList> </Slot> </Association> </RegistryObjectList> </tns:SubmitObjectsRequest>";
        static XElement m_knownValid;
        public static XElement KnownValidPackage
        {
            get
            {
                if (m_knownValid == null)
                {
                    m_knownValid = XElement.Load(new System.IO.StringReader(VALID_XML));
                }
                return m_knownValid;
            }
        }

        static XElement m_knownValidDoc;
        public static XElement KnownValidDocument
        {
            get
            {
                if (m_knownValidDoc == null)
                {
                    m_knownValidDoc = KnownValidPackage.DocumentEntries().First();
                }
                return m_knownValidDoc;
            }
        }

        static DocumentMetadata m_docMeta;
        public static DocumentMetadata TestDocument
        {
            get
            {
                if (m_docMeta == null)
                {
                    m_docMeta = new DocumentMetadata();
                    m_docMeta.SetDocument("Dear Dr. Smith\nYour patient is fine.\nDr. Jones");
                    m_docMeta.Author = new Author();
                    m_docMeta.Author.Person = new Person { First = "Tom", Last = "Jones", Degree = "M.D." };
                    m_docMeta.Author.Institutions.Add(new Institution("Direct U"));
                    m_docMeta.Class = C80ClassCode.TransferOfCareReferralNote.ToCodedValue();
                    m_docMeta.Comments = "This is a nice document";
                    m_docMeta.Confidentiality = C80Confidentialty.Normal.ToCodedValue();
                    m_docMeta.CreatedOn = new DateTime(2010, 01, 01, 05, 10, 00, DateTimeKind.Utc);
                    var evtCodes = new List<CodedValue>();
                    evtCodes.Add(new CodedValue("foo", "bar", "test"));
                    m_docMeta.EventCodes = evtCodes;
                    m_docMeta.FormatCode = C80FormatCode.CareManagement.ToCodedValue();
                    m_docMeta.FacilityCode = C80FacilityCodes.PrivatePhysiciansGroupOffice.ToCodedValue();
                    m_docMeta.LanguageCode = "en-us";
                    m_docMeta.LegalAuthenticator = new Person { First = "Marcus", Last = "Welby", Degree = "M.D", Prefix = "Dr." };
                    m_docMeta.MediaType = "text/plain";
                    m_docMeta.PatientID = new PatientID("ABC", "123", "foo");
                    m_docMeta.ServiceStart = new DateTime(2010, 01, 01, 05, 10, 00, DateTimeKind.Utc);
                    m_docMeta.ServiceStop = new DateTime(2010, 01, 01, 05, 10, 00, DateTimeKind.Utc);
                    m_docMeta.PracticeSetting = C80ClinicalSpecialties.FamilyPractice.ToCodedValue();
                    m_docMeta.SourcePtId = new PatientID("XYZ", "PDQ", "foo");
                    m_docMeta.Patient = new Person
                                            {
                                                First = "Bob",
                                                Last = "Smith",
                                                Sex = Sex.Male,
                                                Dob = new DateTime(1975, 05, 05, 00, 00, 00, DateTimeKind.Utc),
                                                Address = new PostalAddress { Street = "150 Main St", City = "Anywhere", State = "CA", Zip = "90000" }
                                            };
                    m_docMeta.Title = "The foo document";
                    m_docMeta.UniqueId = "abc123xyz";
                    m_docMeta.Uri = "http://www.google.com?q=the+foo+document";
                }
                return m_docMeta;
            }
        }

        static DocumentPackage m_package;

        public static DocumentPackage TestPackage
        {
            get
            {
                if (m_package == null)
                {
                    m_package = new DocumentPackage();
                    m_package.Author = new Author { Person = new Person { First = "Bob", Last = "Smith", Degree = "M.D." } };
                    m_package.Comments = "This is a super cool package";
                    m_package.ContentTypeCode = C80ClassCode.ConsultationNote.ToCodedValue();
                    m_package.Documents.Add(Examples.TestDocument);
                    m_package.PatientId = new PatientID("abc", "123", "xyz");
                    m_package.IntendedRecipients.Add(new Recipient { Person = new Person { First = "Dan", Last = "Brown" }, Institution = new Institution("Louvre", "France") });
                    m_package.SourceId = "0.1.2.3.4.5.6.7.8.1000";
                    m_package.SubmissionTime = DateTime.Now;
                    m_package.Title = "Title: Awesome";
                    m_package.UniqueId = "0.1.2.3.4.5.6.7.8.1001";
                }
                return m_package;
            }
        }

        static XElement m_packageXEl;

        public static XElement TestPackageXElement
        {
            get
            {
                if (m_packageXEl == null)
                {
                    m_packageXEl = XDMetadataGenerator.GeneratePackage(TestPackage);
                }
                return m_packageXEl;
            }
        }



        static XElement m_testDocXEl;
        public static XElement RoundTripDocument
        {
            get
            {
                if (m_testDocXEl == null)
                {
                    m_testDocXEl = XDMetadataGenerator.Generate(TestDocument);
                }
                return m_testDocXEl;
            }
        }

        static XElement m_roundTripPackage;
        public static XElement RoundTripPackage
        {
            get
            {
                if (m_roundTripPackage == null)
                {
                    m_roundTripPackage = XDMetadataGenerator.Generate(TestPackage);
                }
                return m_roundTripPackage;
            }
        }
    }
}