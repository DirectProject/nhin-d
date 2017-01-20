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
using Xunit.Extensions;

namespace Health.Direct.Xd.Tests
{
    public class XdMetadataConsumerTests
    {
        public static IEnumerable<object[]> TestData(object[] data)
        {
            yield return new object[] { Examples.RoundTripDocument, data[0] };
            yield return new object[] { Examples.KnownValidDocument, data[1] };
        }

        public static IEnumerable<object[]> AuthorPersonData
        {
            get
            {
                return TestData( new object[] {  Examples.TestDocument.Author.Person, new Person { First = "Gerald", Last = "Smitty", Prefix = "MD", Suffix = "Dr" }});
            }
        }


        [Theory]
        [MemberData("AuthorPersonData")]
        public void ConsumerConsumesAuthorPerson(XElement documentXEl, Person expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            Assert.Equal(expected.ToString(), doc.Author.Person.ToString());
            Assert.True(expected.Equals(doc.Author.Person));
        }

        public static IEnumerable<object[]> AuthorInstitionData
        {
            get
            {
                yield return new object[] { Examples.RoundTripDocument, Examples.TestDocument.Author.Institutions };
                List<Institution> expectedInsts = new List<Institution> { new Institution("Clevland Clinic"), new Institution("Parma Community") };
                yield return new object[] { Examples.KnownValidDocument, expectedInsts };
            }
        }


        [Theory]
        [MemberData("AuthorInstitionData")]
        public void ConsumerConsumesAuthorInstition(XElement documentXel, List<Institution> expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXel);
            Assert.Equal(expected.Count, doc.Author.Institutions.Count);
            foreach (Institution i in expected)
            {
                Assert.Contains(i, doc.Author.Institutions);
            }
        }

        public static IEnumerable<object[]> AuthorRoleData
        {
            get
            {
                yield return new object[] { Examples.RoundTripDocument, Examples.TestDocument.Author.Roles };
                yield return new object[] { Examples.KnownValidDocument, new List<string> { "Primary Surgon" } };
            }
        }

        [Theory]
        [MemberData("AuthorRoleData")]
        public void ConsumerConsumesAuthorRole(XElement documentXEl, List<string> expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            Assert.Equal(expected.Count, doc.Author.Roles.Count);
            foreach (string s in expected)
                Assert.Contains(s, doc.Author.Roles);
        }

        public static IEnumerable<object[]> AuthorSpecialitiesData
        {
            get
            {
                yield return new object[] { Examples.RoundTripDocument, Examples.TestDocument.Author.Specialities };
                yield return new object[] { Examples.KnownValidDocument, new List<string> { "Orthopedic" } };
            }
        }

        [Theory]
        [MemberData("AuthorSpecialitiesData")]
        public void ConsumerConsumesAuthorSpecialties(XElement documentXEl, List<string> expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            Assert.Equal(expected.Count, doc.Author.Specialities.Count);
            foreach (string s in expected)
                Assert.Contains(s, doc.Author.Specialities);
        }

        public static IEnumerable<object[]> ClassData
        {
            get
            {
                yield return new object[] { Examples.RoundTripDocument, Examples.TestDocument.Class };
                yield return new object[] { Examples.KnownValidDocument, new CodedValue("Operative", "Operative", "Connect-a-thon classCodes") };
            }
        }

        [Theory]
        [MemberData("ClassData")]
        public void ConsumerConsumesClass(XElement documentXEl, CodedValue code)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            Assert.Equal(code.ToString(), doc.Class.ToString());
            Assert.True(code.Equals(doc.Class));
        }

        public static IEnumerable<object[]> CommentsData
        {
            get
            {
                yield return new object[] { Examples.RoundTripDocument, Examples.TestDocument.Comments };
                yield return new object[] { Examples.KnownValidDocument, "Test document" };
            }
        }

        [Theory]
        [MemberData("CommentsData")]
        public void ConsumerConsumesComments(XElement documentXEl, string expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            Assert.Equal(expected, doc.Comments);
        }

        public static IEnumerable<object[]> ConfidentialityData
        {
            get
            {
                return TestData(new object[] { Examples.TestDocument.Confidentiality, new CodedValue("1.3.6.1.4.1.21367.2006.7.104", "Solar Drug Trial", "Connect-a-thon confidentialityCodes") });
            }
        }
        [Theory]
        [MemberData("ConfidentialityData")]
        public void ConsumerConsumesConfidentialty(XElement documentXEl, CodedValue expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            Assert.Equal(expected, doc.Confidentiality);
        }

        public static IEnumerable<object[]> CreatedOnData
        {
            get
            {
                return TestData(new object[] { Examples.TestDocument.CreatedOn.Value, new DateTime(2008, 07, 01) });
            }
        }

        [Theory]
        [MemberData("CreatedOnData")]
        public void ConsumerConsumesCreatedOn(XElement documentXEl, DateTime? expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            Assert.Equal(expected, doc.CreatedOn);
        }

        public static IEnumerable<object[]> EventCodesData
        {
            get
            {
                return TestData(new object[] { Examples.TestDocument.EventCodes, null });
            }
        }

        [Theory]
        [MemberData("EventCodesData")]
        public void ConsumerConsumesEventCodes(XElement documentXEl, IEnumerable<CodedValue> expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            if (expected == null)
                Assert.True(doc.EventCodes == null || doc.EventCodes.Count() == 0);
            else
            {
                foreach (CodedValue cv in expected)
                    Assert.Contains(cv, doc.EventCodes);
            }
        }

        public static IEnumerable<object[]> FacilityCodeData
        {
            get
            {
                return TestData(new object[] { Examples.TestDocument.FacilityCode, new CodedValue("Hospital Setting", "Hospital Setting", "Connect-a-thon healthcareFacilityTypeCodes")});
            }
        }

        [Theory]
        [MemberData("FacilityCodeData")]
        public void ConsumerConsumesFacility(XElement documentXEl, CodedValue expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            Assert.Equal(expected, doc.FacilityCode);
        }

        public static IEnumerable<object[]> FormatCodeData
        {
            get
            {
                return TestData(new object[] { Examples.TestDocument.FormatCode, new CodedValue("CDAR2/IHE 1.0", "CDAR2/IHE 1.0", "Connect-a-thon formatCodes") });
            }
        }

        [Theory]
        [MemberData("FormatCodeData")]
        public void ConsumerConsumesFormat(XElement documentXEl, CodedValue expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            Assert.Equal(expected, doc.FormatCode);
        }

        public static IEnumerable<object[]> HashData
        {
            get
            {
                return TestData(new object[] { Examples.TestDocument.Hash, null });
            }
        }

        [Theory]
        [MemberData("HashData")]
        public void ConsumerConsumesHashCode(XElement documentXEl, string expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            Assert.Equal(expected, doc.Hash);
        }

        public static IEnumerable<object[]> LanguageCodeData
        {
            get
            {
                return TestData(new object[] { Examples.TestDocument.LanguageCode, "en-us" });
            }
        }

        [Theory]
        [MemberData("LanguageCodeData")]
        public void ConsumerConsumesLanguageCode(XElement documentXEl, string expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            Assert.Equal(expected, doc.LanguageCode);
        }

        public static IEnumerable<object[]> LegalAuthenticatorData
        {
            get
            {
                return TestData(new object[] { Examples.TestDocument.LegalAuthenticator, null });
            }
        }

        [Theory]
        [MemberData("LegalAuthenticatorData")]
        public void ConsumerConsumesLegalAuthenticator(XElement documentXEl, Person expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            Assert.Equal(expected, doc.LegalAuthenticator);
        }

        public static IEnumerable<object[]> MediaTypeData
        {
            get
            {
                return TestData(new object[] { Examples.TestDocument.MediaType, "text/plain" });
            }
        }

        [Theory]
        [MemberData("MediaTypeData")]
        public void ConsumerConsumesMediaType(XElement documentXEl, string expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            Assert.Equal(expected, doc.MediaType);
        }


        public static IEnumerable<object[]> PatientIdData
        {
            get
            {
                return TestData(new object[] { Examples.TestDocument.PatientID, new PatientID("498ef443e7ac4a6", "1.3.6.1.4.1.21367.2005.3.7", "ISO") });
            }
        }

        [Theory]
        [MemberData("PatientIdData")]
        public void ConsumerConsumesPatientId(XElement documentXEl, PatientID expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            Assert.Equal(expected, doc.PatientID);
        }

        public static IEnumerable<object[]> ServiceStartData
        {
            get
            {
                return TestData(new object[] { Examples.TestDocument.ServiceStart, new DateTime(2008, 06, 28, 11, 00, 00,00) });
            }
        }

        [Theory]
        [MemberData("ServiceStartData")]
        public void ConsumerConsumesServiceStart(XElement documentXEl, DateTime? expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            Assert.Equal(expected, doc.ServiceStart);
        }

        public static IEnumerable<object[]> ServiceStopData
        {
            get
            {
                return TestData(new object[] { Examples.TestDocument.ServiceStop, new DateTime(2008, 06, 28, 15, 00, 00, 00) });
            }
        }

        [Theory]
        [MemberData("ServiceStopData")]
        public void ConsumerConsumesServiceStop(XElement documentXEl, DateTime? expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            Assert.Equal(expected, doc.ServiceStop);
        }

        public static IEnumerable<object[]> PracticeSettingCodeData
        {
            get
            {
                return TestData(new object[] { Examples.TestDocument.PracticeSetting, new CodedValue("Laboratory", "Laboratory", "Connect-a-thon practiceSettingCodes") });
            }
        }

        [Theory]
        [MemberData("PracticeSettingCodeData")]
        public void ConsumerConsumesPracticeSetting(XElement documentXEl, CodedValue expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            Assert.Equal(expected, doc.PracticeSetting);
        }

        public static IEnumerable<object[]> SizeData
        {
            get
            {
                return TestData(new object[] { Examples.TestDocument.Size, null });
            }
        }

        [Theory]
        [MemberData("SizeData")]
        public void ConsumerConsumesSize (XElement documentXEl, int? expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            Assert.Equal(expected, doc.Size);
        }

        public static IEnumerable<object[]> SourcePatientIdData
        {
            get
            {
                return TestData(new object[] { Examples.TestDocument.SourcePtId, new PatientID("89765a87b", "fj34r", "abc") });
            }
        }

        [Theory]
        [MemberData("SourcePatientIdData")]
        public void ConsumerConsumesSourcePatientId(XElement documentXEl, PatientID expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            Assert.Equal(expected, doc.SourcePtId);
        }

        public static IEnumerable<object[]> SourcePatientInfoData
        {
            get
            {
                return TestData(new object[] { Examples.TestDocument.Patient, new Person { First = "Joe", Last = "Doe", Dob = new DateTime(1956,05,27), Sex = Sex.Male, Address = new PostalAddress {Street = "100 Main St Metropolis", City = "Chicago", State = "IL", Zip = "44130" } }});
            }
        }

        [Theory]
        [MemberData("SourcePatientInfoData")]
        public void ConsumerConsumesPatient(XElement documentXEl, Person expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            Assert.Equal(expected, doc.Patient);
            Assert.Equal(expected.Address, doc.Patient.Address);
        }


        public static IEnumerable<object[]> TitleData
        {
            get
            {
                return TestData(new object[] { Examples.TestDocument.Title, "Test" });
            }
        }

        [Theory]
        [MemberData("TitleData")]
        public void ConsumerConsumesTitle(XElement documentXEl, String expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            Assert.Equal(expected, doc.Title);
        }

        public static IEnumerable<object[]> UniqueIdData
        {
            get
            {
                return TestData(new object[] { Examples.TestDocument.UniqueId, "1.3.6.1.4.1.21367.2005.3.3.1" });
            }
        }

        [Theory]
        [MemberData("UniqueIdData")]
        public void ConsumerConsumesUniqueId(XElement documentXEl, String expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            Assert.Equal(expected, doc.UniqueId);
        }

        public static IEnumerable<object[]> UriData
        {
            get
            {
                return TestData(new object[] { Examples.TestDocument.Uri, null });
            }
        }

        [Theory]
        [MemberData("UriData")]
        public void ConsumerConsumesUri(XElement documentXEl, String expected)
        {
            DocumentMetadata doc = XDMetadataConsumer.ConsumeDocument(documentXEl);
            Assert.Equal(expected, doc.Uri);
        }

        static IEnumerable<object[]> PackageTestData(object a, object b)
        {
            yield return new object[] { Examples.RoundTripPackage, a };
            yield return new object[] { Examples.KnownValidPackage, b };
        }



        public static IEnumerable<object[]> PackageAuthorData
        {
            get
            {
                Author expected = new Author();
                expected.Person = new Person { First = "Gerald", Last = "Smitty", Prefix = "MD", Suffix = "Dr" };
                expected.Institutions.AddRange(new List<Institution> { new Institution("Clevland Clinic"), new Institution("Parma Community") });
                expected.Roles.Add("Primary Surgon");
                expected.Specialities.Add("Orthopedic");

                return PackageTestData(Examples.TestPackage.Author, expected);
            }
        }

        [Fact]
        public void PackageHasOneDocument()
        {
            DocumentPackage package = XDMetadataConsumer.Consume(Examples.RoundTripPackage);
            Assert.Equal(1, package.Documents.Count);
        }


        [Theory]
        [MemberData("PackageAuthorData")]
        public void ConsumerConsumesPackageAuthor(XElement xl, Author expected)
        {
            DocumentPackage package = XDMetadataConsumer.Consume(xl);
            Assert.Equal(package.Author, expected);
        }


        public static IEnumerable<object[]> PackageComments
        {
            get
            {
                return PackageTestData(Examples.TestPackage.Comments, "Test submission" );
            }
        }


        [Theory]
        [MemberData("PackageComments")]
        public void ConsumerConsumesPackageComments(XElement xl, string expected)
        {
            DocumentPackage package = XDMetadataConsumer.Consume(xl);
            Assert.Equal(package.Comments, expected);
        }

        public static IEnumerable<object[]> PackageTypeCode
        {
            get
            {
                return PackageTestData(Examples.TestPackage.ContentTypeCode, new CodedValue("Discharge summarization", "Discharge summarization", "Connect-a-thon contentTypeCodes"));
            }
        }


        [Theory]
        [MemberData("PackageTypeCode")]
        public void ConsumerConsumesPackageTypeCode(XElement xl, CodedValue expected)
        {
            DocumentPackage package = XDMetadataConsumer.Consume(xl);
            Assert.Equal(package.ContentTypeCode, expected);
        }

        public static IEnumerable<object[]> PackagePatientId
        {
            get
            {
                return PackageTestData(Examples.TestPackage.PatientId, new PatientID("498ef443e7ac4a6", "1.3.6.1.4.1.21367.2005.3.7", "ISO"));
            }
        }


        [Theory]
        [MemberData("PackagePatientId")]
        public void ConsumerConsumesPackagePatientId(XElement xl, PatientID expected)
        {
            DocumentPackage package = XDMetadataConsumer.Consume(xl);
            Assert.Equal(package.PatientId, expected);
        }

        public static IEnumerable<object[]> PackageSourceId
        {
            get
            {
                return PackageTestData(Examples.TestPackage.SourceId, "1.3.6.1.4.1.21367.2005.3.999.900");
            }
        }


        [Theory]
        [MemberData("PackageSourceId")]
        public void ConsumerConsumesPackageSourceId(XElement xl, string expected)
        {
            DocumentPackage package = XDMetadataConsumer.Consume(xl);
            Assert.Equal(package.SourceId, expected);
        }

        public static IEnumerable<object[]> PackageSubmissionTime
        {
            get
            {
                return PackageTestData(Examples.TestPackage.SubmissionTime.Value.ToUniversalTime(), new DateTime(2008,08,26,00,00,00,DateTimeKind.Utc) );
            }
        }


        [Theory]
        [MemberData("PackageSubmissionTime")]
        public void ConsumerConsumesPackageSubmissionTime(XElement xl, DateTime? expected)
        {
            DocumentPackage package = XDMetadataConsumer.Consume(xl);
            if (package.SubmissionTime == null)
                Assert.Null(expected);
            else
            {
                // .Net sometimes translates identical dates with slightly different numbers of Ticks.
                TimeSpan ts = package.SubmissionTime.Value.Subtract(expected.Value);
                Assert.True(ts.Ticks < TimeSpan.TicksPerSecond);
            }
        }


        public static IEnumerable<object[]> PackageTitle
        {
            get
            {
                return PackageTestData(Examples.TestPackage.Title, "Test 1");
            }
        }


        [Theory]
        [MemberData("PackageTitle")]
        public void ConsumerConsumesPackageTitle(XElement xl, string expected)
        {
            DocumentPackage package = XDMetadataConsumer.Consume(xl);
            Assert.Equal(package.Title, expected);
        }

        public static IEnumerable<object[]> PackageUniqueId
        {
            get
            {
                return PackageTestData(Examples.TestPackage.UniqueId, "1.3.6.1.4.1.21367.2005.3.1.1");
            }
        }


        [Theory]
        [MemberData("PackageUniqueId")]
        public void ConsumerConsumesPackageUniqueId(XElement xl, string expected)
        {
            DocumentPackage package = XDMetadataConsumer.Consume(xl);
            Assert.Equal(package.UniqueId, expected);
        }

        public static IEnumerable<object[]> PackageRecipients
        {
            get
            {
                return PackageTestData(Examples.TestPackage.IntendedRecipients, new List<Recipient> { });
            }
        }


        [Theory]
        [MemberData("PackageRecipients")]
        public void ConsumerConsumesPackageRecipients(XElement xl, List<Recipient> expected)
        {
            DocumentPackage package = XDMetadataConsumer.Consume(xl);
            Assert.Equal(package.IntendedRecipients.Aggregate("", (a, s) => a + " " + s),
                         expected.Aggregate("", (a, s) => a + " " + s));
            Assert.Equal(package.IntendedRecipients.Count, expected.Count);
            foreach (Recipient r in package.IntendedRecipients)
                Assert.Contains(r, expected);
        }



    }
}