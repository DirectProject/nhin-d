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
using System.Xml;
using System.Xml.Linq;

using Xunit;

using NHINDirect.Metadata;
using NHINDirect.Xd;

namespace NHINDirect.Tests.xdTests
{
    public class XdMetadataConsumerTests
    {

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
                    m_docMeta.Author.Institutions.Add(new Institution("Direct U"));
                    m_docMeta.Class = new ClassCode(Metadata.C80ClassCode.TransferOfCareReferralNote);
                    m_docMeta.Comments = "This is a nice document";
                    m_docMeta.Confidentiality = new ConfidentialtyCode(Metadata.C80Confidentialty.Normal);
                    m_docMeta.CreatedOn = new DateTime(2010, 01, 01, 05, 10, 00, DateTimeKind.Utc);
                    var evtCodes = new List<CodedValue>();
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
                        Address = new PostalAddress { Street = "150 Main St", City = "Anywhere", State = "CA", Zip = "90000" }
                    };
                    m_docMeta.Title = "The foo document";
                    m_docMeta.UniqueId = "abc123xyz";
                    m_docMeta.Uri = "http://www.google.com?q=the+foo+document";
                }
                return m_docMeta;
            }
        }

        XElement m_testDocXEl;
        public XElement TestDocumentXEl
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



        [Fact]
        public void ConsumerConsumesAuthorPerson()
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(TestDocumentXEl);
            Assert.NotNull(TestDocument.Author);
            Assert.NotNull(TestDocument.Author.Person);
            Assert.Equal(TestDocument.Author.Person.ToString(), doc.Author.Person.ToString());
            Assert.True(TestDocument.Author.Person.Equals(doc.Author.Person));
        }

    }
}
