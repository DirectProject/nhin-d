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
using System.Xml.Linq;

using NHINDirect.Xd;
using NHINDirect.Metadata;
using Xunit;
using Xunit.Extensions;

namespace NHINDirect.Tests.xdTests
{
    public class XdMetadataGeneratorTests
    {


        [Fact]
        public void NoDocumentMetadata()
        {
            DocumentMetadata empty = new DocumentMetadata();
            Assert.DoesNotThrow(() => empty.Generate());
        }


        [Fact]
        public void EmptyMetadataDocumentHasNoDescendents()
        {
            XElement empty = new DocumentMetadata().Generate();
            Assert.Empty(empty.Descendants());
        }

        DocumentMetadata m_docMeta;
        DocumentMetadata TestDocument
        {
            get
            {
                if (m_docMeta == null)
                {
                    m_docMeta = new DocumentMetadata();
                    m_docMeta.Author = new Author();
                    m_docMeta.Author.Person = new Person { First = "Tom", Last = "Jones", Degree = "M.D." };
                    m_docMeta.Author.Institutions.Add("Direct U");
                    m_docMeta.Class = new ClassCode(Metadata.C80ClassCode.TransferOfCareReferralNote);
                    m_docMeta.Comments = "This is a nice document";
                    m_docMeta.Confidentiality = new ConfidentialtyCode(Metadata.C80Confidentialty.Normal);
                    m_docMeta.CreatedOn = new DateTime(2010, 01, 01, 05, 10, 00, DateTimeKind.Utc);
                    var evtCodes =  new List<CodedValue>();
                    evtCodes.Add(new CodedValue("foo", "bar"));
                    m_docMeta.EventCodes = evtCodes;
                    m_docMeta.FormatCode = new FormatCode(Metadata.C80FormatCode.CareManagement);
                    m_docMeta.Hash = "da39a3ee5e6b4b0d3255bfef95601890afd80709";
                    m_docMeta.FaciltyCode = new FacilityCode(C80FacilityCodes.PrivatePhysiciansGroupOffice);
                    m_docMeta.LanguageCode = "en-us";
                    m_docMeta.LegalAuthenticator = new Person { First = "Marcus", Last = "Welby", Degree = "M.D", Prefix = "Dr." };
                    m_docMeta.MediaType = "text/plain";
                    m_docMeta.PatientID = new PatientID("ABC", "123", "foo");
                    m_docMeta.ServiceStart = new DateTime(2010, 01, 01, 05, 10, 00, DateTimeKind.Utc);
                    m_docMeta.ServiceStop = new DateTime(2010, 01, 01, 05, 10, 00, DateTimeKind.Utc);
                    m_docMeta.PracticeSetting = new SpecialtyCode(C80ClinicalSpecialties.FamilyPractice);
                    m_docMeta.Size = 1000;
                    m_docMeta.SourcePtId = new PatientID("XYZ", "PDQ", "foo");
                    m_docMeta.Patient = new Person
                    {
                        First = "Bob",
                        Last = "Smith",
                        Sex = Sex.Male,
                        Dob = DateTime.Parse("05/05/1975"),
                        Address = new Address { Street = "150 Main St", City = "Anywhere", State = "CA", Zip = "90000" }
                    };
                    m_docMeta.Title = "The foo document";
                    m_docMeta.UniqueId = "abc123xyz";
                    m_docMeta.Uri = "http://www.google.com?q=the+foo+document";
                }
                return m_docMeta;
            }
        }

        XElement TestDocXElement
        {
            get
            {
                return TestDocument.Generate();
            }
        }

        [Fact]
        public void DocumentHasAuthorClassification()
        {
            Assert.NotEmpty(TestDocXElement.Classifications(XDMetadataStandard.DocumentAuthorUUID));
        }

        [Fact]
        public void DocumentHasClassCodeClassification()
        {
            Assert.NotEmpty(TestDocXElement.Classifications(XDMetadataStandard.DocumentClassUUID));
        }
        
        // all the code value stuff uses the same generator, so no need to test each coded attr.
        [Fact]
        public void DocumentClassCodeValueIsCorrect()
        {
            XElement node = TestDocXElement.Classifications(XDMetadataStandard.DocumentClassUUID).First();
            string code = node.Descendants("Value").First().Value;
            Assert.Equal(ClassCode.Decode(Metadata.C80ClassCode.TransferOfCareReferralNote).Key, code);
        }

        [Fact]
        public void DocumentHasDescription()
        {
            Assert.NotEmpty(TestDocXElement.Descendants("Description"));
        }

        [Fact]
        public void DocumentHasConfidentialtyCodeClassification()
        {
            Assert.NotEmpty(TestDocXElement.Classifications(XDMetadataStandard.DocumentConfidentialityUUID));
        }

        [Fact]
        public void DocumentCreatedOnCorrect()
        {
            string date = TestDocXElement.Slots("creationTime").Descendants("Value").First().Value;
            Assert.Equal("20100101051000", date);
        }

        [Fact]
        public void DocumentHasEventCodeClassification()
        {
            Assert.NotEmpty(TestDocXElement.Classifications(XDMetadataStandard.EventCodeUUID));
        }

        [Fact]
        public void DocumentEntryCodeHasCorrectValue()
        {
            Assert.Equal("foo", TestDocXElement.Classification(XDMetadataStandard.EventCodeUUID).Descendants("Value").First().Value);
        }

        [Fact]
        public void DocumentHasFormatCodeClassification()
        {
            Assert.NotNull(TestDocXElement.Classification(XDMetadataStandard.FormatCodeUUID));
        }

        [Fact]
        public void DocumentFormatCodeHasCorrectValue()
        {
            Assert.Equal(FormatCode.Decode(C80FormatCode.CareManagement).Key, TestDocXElement.Classification(XDMetadataStandard.FormatCodeUUID).Descendants("Value").First().Value);
        }

        [Fact]
        public void DocumentHasHashSlot()
        {
            Assert.NotEmpty(TestDocXElement.Slots("hash"));
        }

        [Fact]
        public void DocumentHasCorrectHash()
        {
            Assert.Equal("da39a3ee5e6b4b0d3255bfef95601890afd80709", TestDocXElement.SlotValue("hash"));
        }

        [Fact]
        public void DocumentHasFacilityCodeClassification()
        {
            Assert.NotNull(TestDocXElement.Classification(XDMetadataStandard.FacilityCodeUUID));
        }

        [Fact]
        public void DocumentFacilityCodeHasCorrectValue()
        {
            Assert.Equal(FacilityCode.Decode(C80FacilityCodes.PrivatePhysiciansGroupOffice).Key,
                TestDocXElement.Classification(XDMetadataStandard.FacilityCodeUUID).Descendants("Value").First().Value);
        }

        [Fact]
        public void DocumentHasLanguageCode()
        {
            Assert.Equal("en-us", TestDocXElement.SlotValue(XDMetadataStandard.LanguageCodeSlot));
        }

        [Fact]
        public void DocumentHasAuthenticator()
        {
            Assert.NotNull(TestDocXElement.Slot(XDMetadataStandard.LegalAuthenticatorSlot));
        }

        [Fact]
        public void DocumentAuthenticatorHasCorrectValue()
        {
            Assert.Equal(TestDocument.LegalAuthenticator.ToXCN(), TestDocXElement.SlotValue(XDMetadataStandard.LegalAuthenticatorSlot));
        }

        [Fact]
        public void DocumentHasMediaType()
        {
            Assert.NotNull(TestDocXElement.Attribute("mimeType"));
        }

        [Fact]
        public void DocumentMediaTypeCorect()
        {
            Assert.Equal("text/plain", TestDocXElement.Attribute("mimeType").Value);
        }

        [Fact]
        public void DocumentHasPatientID()
        {
            Assert.NotEmpty(TestDocXElement.ExternalIdentifiers(XDMetadataStandard.PatientIdentitySchemeUUID));
        }

        [Fact]
        public void DocumentPatientIDHasCorrectValue()
        {
            XElement idElts = TestDocXElement.ExternalIdentifiers(XDMetadataStandard.PatientIdentitySchemeUUID).First();
            PatientID id = PatientID.FromEscapedCx(idElts.Attribute("value").Value);
            Assert.True(TestDocument.PatientID.Equals(id));
        }

        [Fact]
        public void DocumentHasPracticeSettingCode()
        {
            Assert.NotNull(TestDocXElement.Classification(XDMetadataStandard.PracticeSettingUUID));
        }

        [Fact]
        public void PracticeSettingCodeHasCorrectValue()
        {
            Assert.Equal(SpecialtyCode.Decode(C80ClinicalSpecialties.FamilyPractice).Key,
                TestDocXElement.Classification(XDMetadataStandard.PracticeSettingUUID).SlotValue("codingScheme"));
        }

        [Fact]
        public void DocumentHasServiceStart()
        {
            Assert.NotNull(TestDocXElement.Slot(XDMetadataStandard.ServiceStartSlot));
        }

        [Fact]
        public void ServiceStartHasCorrectValue()
        {
            Assert.Equal(TestDocument.ServiceStart.Value.ToHL7Date(), TestDocXElement.SlotValue(XDMetadataStandard.ServiceStartSlot));
        }
        [Fact]
        public void DocumentHasServiceStop()
        {
            Assert.NotNull(TestDocXElement.Slot(XDMetadataStandard.ServiceStopSlot));
        }

        [Fact]
        public void ServiceStopHasCorrectValue()
        {
            Assert.Equal(TestDocument.ServiceStop.Value.ToHL7Date(), TestDocXElement.SlotValue(XDMetadataStandard.ServiceStopSlot));
        }

        [Fact]
        public void DocumentHasSizeSlot()
        {
            Assert.NotNull(TestDocXElement.Slot(XDMetadataStandard.SizeSlot));
        }

        [Fact]
        public void SizeSlotHasCorrectValue()
        {
            Assert.Equal(TestDocument.Size, Int32.Parse(TestDocXElement.SlotValue(XDMetadataStandard.SizeSlot)));
        }

        [Fact]
        public void DocumentHasSourcePtIDSlot()
        {
            Assert.NotNull(TestDocXElement.Slot(XDMetadataStandard.SourcePatientIDSlot));
        }

        [Fact]
        public void SourcePatientIDHasCorrectValue()
        {
            PatientID expected = new PatientID("XYZ", "PDQ", "foo");
            PatientID actual = PatientID.FromEscapedCx(TestDocXElement.SlotValue(XDMetadataStandard.SourcePatientIDSlot));
            Assert.True(expected.Equals(actual));
        }

        [Fact]
        public void DocumentHasPatientInfoSlot()
        {
            Assert.NotNull(TestDocXElement.Slot(XDMetadataStandard.SourcePatientInfoSlot));
        }

        [Fact]
        public void PatientInfoSlotHasCorrectPatient()
        {
            Assert.Contains("PID-8|M", TestDocXElement.SlotValues(XDMetadataStandard.SourcePatientInfoSlot));
        }

        [Fact]
        public void DocumentHasTitle()
        {
            Assert.NotNull(TestDocXElement.Element(XDMetadataStandard.NameElt));
        }

        [Fact]
        public void TitleIsCorrect()
        {
            Assert.Equal(TestDocument.Title, TestDocXElement.Element(XDMetadataStandard.NameElt).Descendants("LocalizedString").First().Attribute("value").Value);
        }

        [Fact]
        public void DocumentHasUniqueId()
        {
            Assert.NotEmpty(TestDocXElement.ExternalIdentifiers(XDMetadataStandard.DocumentUniqueIdIdentitySchemeUUID));
        }

        [Fact]
        public void UniqueIdHasCorrectValue()
        {
            Assert.Equal("abc123xyz", TestDocXElement.ExternalIdentifiers(XDMetadataStandard.DocumentUniqueIdIdentitySchemeUUID).First().Attribute("value").Value);
        }

        [Fact]
        public void DocumentHasURISlot()
        {
            Assert.NotNull(TestDocXElement.Slot(XDMetadataStandard.UriSlot));
        }

        [Fact]
        public void EmptyRegistryPackage()
        {
            DocumentPackage package = new DocumentPackage();
            Assert.DoesNotThrow(() => XDMetadataGenerator.GeneratePackage(package));
        }

        DocumentPackage m_package;

        DocumentPackage TestPackage
        {
            get 
            {
                if (m_package == null)
                {
                    m_package = new DocumentPackage();
                    m_package.Author = new Author {Person = new Person {First = "Bob", Last = "Smith", Degree="M.D."}};
                    m_package.Comments = "This is a super cool package";
                    m_package.ContentTypeCode = new ClassCode(C80ClassCode.ConsultationNote);
                    m_package.Documents.Add(TestDocument);
                    m_package.PatientId = new PatientID("abc", "123", "xyz");
                    m_package.SourceId = "0.1.2.3.4.5.6.7.8.1000";
                    m_package.SubmissionTime = DateTime.Now;
                    m_package.Title = "Title: Awesome";
                    m_package.UniqueId = "0.1.2.3.4.5.6.7.8.1001";
                }
                return m_package;
            }
        }

        XElement m_packageXEl;

        XElement TestPackageXElement
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


        [Fact]
        public void PackageHasAuthor()
        {
            Assert.NotNull(TestPackageXElement.Classification(XDMetadataStandard.SubmissionSetAuthorUUID));
        }

        [Fact]
        public void PackageHasComments()
        {
            Assert.NotEmpty(TestPackageXElement.Descendants("Description"));
        }

        [Fact]
        public void PackageHasContentTypeCode()
        {
            Assert.NotNull(TestPackageXElement.Classification(XDMetadataStandard.ContentTypeCodeUUID));
        }

        [Fact]
        public void PackageHasId()
        {
            Assert.NotNull(TestPackageXElement.Attribute("id"));
        }

        [Fact]
        public void PackageHasPatientId()
        {
            Assert.NotNull(TestPackageXElement.ExternalIdentifierValue(XDMetadataStandard.SubmissionSetSourceIdUUID));
        }

        [Fact]
        public void PackageHasSourceId()
        {
            Assert.NotNull(TestPackageXElement.ExternalIdentifierValue(XDMetadataStandard.SubmissionSetSourceIdUUID));
        }

        [Fact]
        public void PackageHasSubmissionTime()
        {
            Assert.NotNull(TestPackageXElement.SlotValue(XDMetadataStandard.SubmissionTimeSlot));
        }

        [Fact]
        public void PackageHasTitle()
        {
            Assert.NotEmpty(TestPackageXElement.Descendants("Name"));
        }

        [Fact]
        public void PackageHasUniqueId()
        {
            Assert.NotNull(TestPackageXElement.ExternalIdentifierValue(XDMetadataStandard.SubmissionSetUniqueIdUUID));
        }

        
        XElement m_testPackage;
        XElement TestSubmitObjectsXElement
        {
            get
            {
                if (m_testPackage == null)
                    m_testPackage = TestPackage.Generate();
                return m_testPackage;
            }
        }

        [Fact]
        public void SubmitObjectsHasProperlyClassifiedRegistryPackage()
        {
            XElement elt = TestSubmitObjectsXElement.Descendants("RegistryPackage").First();
            Assert.NotNull(elt.Attribute("id").Value);
            string id = elt.Attribute("id").Value;
            IEnumerable<XElement> classifications = from el in elt.Descendants("Classification")
                                       where (string) el.Attribute("classificationNode") == XDMetadataStandard.SubmissionSetClassificationUUID
                                       select el;
            Assert.NotEmpty(classifications);
            Assert.Equal(id, classifications.First().Attribute("classifiedObject").Value);
        }

        [Fact]
        public void SubmitObjectsHasDocument()
        {
            IEnumerable<XElement> docXElements = from el in TestSubmitObjectsXElement.Descendants("ExtrinsicObjectType")
                                                 where (string)el.Attribute("objectType") == XDMetadataStandard.DocumentEntryUUID
                                                 select el;
            Assert.NotEmpty(docXElements);
        }

        [Fact]
        public void UriValuesBreaksCorrectly()
        {
            string FakeUri = Enumerable.Repeat("0123456789", 13).Aggregate("", (string a, string s) => a + s);
            IEnumerable<string> strings = XDMetadataGenerator.UriValues(FakeUri);
            Assert.Equal("2|89", strings.Last());
        }

    }
}
