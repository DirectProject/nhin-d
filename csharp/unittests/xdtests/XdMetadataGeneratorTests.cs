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

using Xunit;

namespace Health.Direct.Xd.Tests
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

        XElement TestDocXElement
        {
            get
            {
                return Examples.TestDocument.Generate();
            }
        }

        [Fact]
        public void DocumentHasId()
        {
            Assert.NotNull(TestDocXElement.AttributeValue(XDMetadataStandard.Attrs.Id));
        }

        [Fact]
        public void DocumentHasAuthorClassification()
        {
            Assert.NotEmpty(TestDocXElement.Classifications(XDMetadataStandard.UUIDs.DocumentAuthor));
        }

        [Fact]
        public void DocumentHasClassCodeClassification()
        {
            Assert.NotEmpty(TestDocXElement.Classifications(XDMetadataStandard.UUIDs.DocumentClass));
        }
        
        // all the code value stuff uses the same generator, so no need to test each coded attr.
        [Fact]
        public void DocumentClassCodeValueIsCorrect()
        {
            XElement node = TestDocXElement.Classifications(XDMetadataStandard.UUIDs.DocumentClass).First();
            string code = node.Attribute(XDMetadataStandard.Attrs.NodeRepresentation).Value;
            Assert.Equal(C80ClassCodeUtils.Decode(C80ClassCode.TransferOfCareReferralNote).Key, code);
        }

        [Fact]
        public void DocumentHasDescription()
        {
            Assert.NotEmpty(TestDocXElement.Descendants("Description"));
        }

        [Fact]
        public void DocumentHasConfidentialtyCodeClassification()
        {
            Assert.NotEmpty(TestDocXElement.Classifications(XDMetadataStandard.UUIDs.DocumentConfidentiality));
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
            Assert.NotEmpty(TestDocXElement.Classifications(XDMetadataStandard.UUIDs.EventCode));
        }

        [Fact]
        public void DocumentEntryCodeHasCorrectValue()
        {
            Assert.Equal("foo", TestDocXElement.Classification(XDMetadataStandard.UUIDs.EventCode).Attribute(XDMetadataStandard.Attrs.NodeRepresentation).Value);
        }

        [Fact]
        public void DocumentHasFormatCodeClassification()
        {
            Assert.NotNull(TestDocXElement.Classification(XDMetadataStandard.UUIDs.FormatCode));
        }

        [Fact]
        public void DocumentFormatCodeHasCorrectValue()
        {
            Assert.Equal(C80FormatCodeUtils.Decode(C80FormatCode.CareManagement).Key, TestDocXElement.Classification(XDMetadataStandard.UUIDs.FormatCode).Attribute(XDMetadataStandard.Attrs.NodeRepresentation).Value);
        }

        [Fact]
        public void DocumentHasHashSlot()
        {
            Assert.NotEmpty(TestDocXElement.Slots("hash"));
        }

        [Fact]
        public void DocumentHasCorrectHash()
        {
            Assert.Equal("09AA8C84097F63E900B8C3FDEDE24BE69EFEA6DC", TestDocXElement.SlotValue("hash"));
        }

        [Fact]
        public void DocumentHasFacilityCodeClassification()
        {
            Assert.NotNull(TestDocXElement.Classification(XDMetadataStandard.UUIDs.FacilityCode));
        }

        [Fact]
        public void DocumentFacilityCodeHasCorrectValue()
        {
            Assert.Equal(C80FacilityCodeUtils.Decode(C80FacilityCodes.PrivatePhysiciansGroupOffice).Key,
                         TestDocXElement.Classification(XDMetadataStandard.UUIDs.FacilityCode).Attribute(XDMetadataStandard.Attrs.NodeRepresentation).Value);
        }

        [Fact]
        public void DocumentHasLanguageCode()
        {
            Assert.Equal("en-us", TestDocXElement.SlotValue(XDMetadataStandard.Slots.LanguageCode));
        }

        [Fact]
        public void DocumentHasAuthenticator()
        {
            Assert.NotNull(TestDocXElement.Slot(XDMetadataStandard.Slots.LegalAuthenticator));
        }

        [Fact]
        public void DocumentAuthenticatorHasCorrectValue()
        {
            Assert.Equal(Examples.TestDocument.LegalAuthenticator.ToXCN(), TestDocXElement.SlotValue(XDMetadataStandard.Slots.LegalAuthenticator));
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
            Assert.NotEmpty(TestDocXElement.ExternalIdentifiers(XDMetadataStandard.UUIDs.DocumentEntryPatientIdentityScheme));
        }

        [Fact]
        public void DocumentPatientIDHasCorrectValue()
        {
            XElement idElts = TestDocXElement.ExternalIdentifiers(XDMetadataStandard.UUIDs.DocumentEntryPatientIdentityScheme).First();
            PatientID id = PatientID.FromEscapedCx(idElts.Attribute("value").Value);
            Assert.True(Examples.TestDocument.PatientID.Equals(id));
        }

        [Fact]
        public void DocumentHasPracticeSettingCode()
        {
            Assert.NotNull(TestDocXElement.Classification(XDMetadataStandard.UUIDs.PracticeSetting));
        }

        [Fact]
        public void PracticeSettingCodeHasCorrectValue()
        {
            Assert.Equal(C80SpecialtyCodeUtils.Decode(C80ClinicalSpecialties.FamilyPractice).Key,
                         TestDocXElement.Classification(XDMetadataStandard.UUIDs.PracticeSetting).Attribute(XDMetadataStandard.Attrs.NodeRepresentation).Value);
        }

        [Fact]
        public void DocumentHasServiceStart()
        {
            Assert.NotNull(TestDocXElement.Slot(XDMetadataStandard.Slots.ServiceStart));
        }

        [Fact]
        public void ServiceStartHasCorrectValue()
        {
            Assert.Equal(Examples.TestDocument.ServiceStart.ToHL7Date(), TestDocXElement.SlotValue(XDMetadataStandard.Slots.ServiceStart));
        }
        [Fact]
        public void DocumentHasServiceStop()
        {
            Assert.NotNull(TestDocXElement.Slot(XDMetadataStandard.Slots.ServiceStop));
        }

        [Fact]
        public void ServiceStopHasCorrectValue()
        {
            Assert.Equal(Examples.TestDocument.ServiceStop.ToHL7Date(), TestDocXElement.SlotValue(XDMetadataStandard.Slots.ServiceStop));
        }

        [Fact]
        public void DocumentHasSlotSize()
        {
            Assert.NotNull(TestDocXElement.Slot(XDMetadataStandard.Slots.Size));
        }

        [Fact]
        public void SlotSizeHasCorrectValue()
        {
            Assert.Equal(Examples.TestDocument.Size, Int32.Parse(TestDocXElement.SlotValue(XDMetadataStandard.Slots.Size)));
        }

        [Fact]
        public void DocumentHasSourcePtIDSlot()
        {
            Assert.NotNull(TestDocXElement.Slot(XDMetadataStandard.Slots.SourcePatientID));
        }

        [Fact]
        public void SourcePatientIDHasCorrectValue()
        {
            PatientID expected = new PatientID("XYZ", "PDQ", "foo");
            PatientID actual = PatientID.FromEscapedCx(TestDocXElement.SlotValue(XDMetadataStandard.Slots.SourcePatientID));
            Assert.True(expected.Equals(actual));
        }

        [Fact]
        public void DocumentHasPatientInfoSlot()
        {
            Assert.NotNull(TestDocXElement.Slot(XDMetadataStandard.Slots.SourcePatientInfo));
        }

        [Fact]
        public void PatientInfoSlotHasCorrectPatient()
        {
            Assert.Contains("PID-8|M", TestDocXElement.SlotValues(XDMetadataStandard.Slots.SourcePatientInfo));
        }

        [Fact]
        public void DocumentHasTitle()
        {
            Assert.NotNull(TestDocXElement.Element(XDMetadataStandard.Elts.Name));
        }

        [Fact]
        public void TitleIsCorrect()
        {
            Assert.Equal(Examples.TestDocument.Title, TestDocXElement.NameValue());
        }

        [Fact]
        public void DocumentHasUniqueId()
        {
            Assert.NotEmpty(TestDocXElement.ExternalIdentifiers(XDMetadataStandard.UUIDs.DocumentUniqueIdIdentityScheme));
        }

        [Fact]
        public void UniqueIdHasCorrectValue()
        {
            Assert.Equal("abc123xyz", TestDocXElement.ExternalIdentifiers(XDMetadataStandard.UUIDs.DocumentUniqueIdIdentityScheme).First().Attribute("value").Value);
        }

        [Fact]
        public void DocumentHasURISlot()
        {
            Assert.NotNull(TestDocXElement.Slot(XDMetadataStandard.Slots.Uri));
        }

        [Fact]
        public void EmptyRegistryPackage()
        {
            DocumentPackage package = new DocumentPackage();
            Assert.DoesNotThrow(() => XDMetadataGenerator.GeneratePackage(package));
        }


        [Fact]
        public void PackageHasAuthor()
        {
            Assert.NotNull(Examples.TestPackageXElement.Classification(XDMetadataStandard.UUIDs.SubmissionSetAuthor));
        }

        [Fact]
        public void PackageHasComments()
        {
            Assert.NotEmpty(Examples.TestPackageXElement.Descendants("Description"));
        }

        [Fact]
        public void PackageHasContentTypeCode()
        {
            Assert.NotNull(Examples.TestPackageXElement.Classification(XDMetadataStandard.UUIDs.ContentTypeCode));
        }

        [Fact]
        public void PackageHasId()
        {
            Assert.NotNull(Examples.TestPackageXElement.Attribute("id"));
        }

        [Fact]
        public void PackageHasIntendedRecipient()
        {
            Assert.NotNull(Examples.TestPackageXElement.Slot(XDMetadataStandard.Slots.IntendedRecipient));
        }

        [Fact]
        public void IntendedRecipientIsCorrect()
        {
            Assert.Equal(Examples.TestPackage.IntendedRecipients.First().ToXONXCNXTN(), Examples.TestPackageXElement.SlotValues(XDMetadataStandard.Slots.IntendedRecipient).First());
        }

        [Fact]
        public void PackageHasPatientId()
        {
            Assert.NotNull(Examples.TestPackageXElement.ExternalIdentifierValue(XDMetadataStandard.UUIDs.SubmissionSetSourceId));
        }

        [Fact]
        public void PackageHasSourceId()
        {
            Assert.NotNull(Examples.TestPackageXElement.ExternalIdentifierValue(XDMetadataStandard.UUIDs.SubmissionSetSourceId));
        }

        [Fact]
        public void PackageHasSubmissionTime()
        {
            Assert.NotNull(Examples.TestPackageXElement.SlotValue(XDMetadataStandard.Slots.SubmissionTime));
        }

        [Fact]
        public void PackageHasTitle()
        {
            Assert.NotEmpty(Examples.TestPackageXElement.Descendants("Name"));
        }

        [Fact]
        public void PackageHasUniqueId()
        {
            Assert.NotNull(Examples.TestPackageXElement.ExternalIdentifierValue(XDMetadataStandard.UUIDs.SubmissionSetUniqueId));
        }

        
        XElement m_testPackage;
        XElement TestSubmitObjectsXElement
        {
            get
            {
                if (m_testPackage == null)
                    m_testPackage = Examples.TestPackage.Generate();
                return m_testPackage;
            }
        }

        [Fact]
        public void SubmitObjectsHasProperlyClassifiedRegistryPackage()
        {
            XElement elt = TestSubmitObjectsXElement.DescendantsAnyNs(XDMetadataStandard.Elts.SubmissionSet).First();
            Assert.NotNull(elt.Attribute(XDMetadataStandard.Attrs.Id).Value);
            string id = elt.Attribute(XDMetadataStandard.Attrs.Id).Value;
            IEnumerable<XElement> classifications = from el in elt.DescendantsAnyNs(XDMetadataStandard.Elts.Classification)
                                                    where (string) el.Attribute(XDMetadataStandard.Attrs.ClassificationNode) == XDMetadataStandard.UUIDs.SubmissionSetClassification
                                                    select el;
            Assert.NotEmpty(classifications);
            Assert.Equal(id, classifications.First().AttributeValue(XDMetadataStandard.Attrs.ClassifiedObject));
        }

        [Fact]
        public void SubmitObjectsHasDocument()
        {
            IEnumerable<XElement> docXElements = from el in TestSubmitObjectsXElement.DescendantsAnyNs(XDMetadataStandard.Elts.DocumentEntry)
                                                 where (string)el.Attribute("objectType") == XDMetadataStandard.UUIDs.DocumentEntry
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