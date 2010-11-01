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

namespace Health.Direct.Xd
{
    /// <summary>
    /// Consumes XElements to create document and document package metadata
    /// </summary>
    public class XDMetadataConsumer
    {

        /// <summary>
        /// Creates a document package from an XML XElement representation
        /// </summary>
        public static DocumentPackage Consume(XElement docPackageXElement)
        {

            DocumentPackage docPackage = ConsumePackage(docPackageXElement.SubmissionSet());

            IEnumerable<XElement> docXEls = docPackageXElement.DocumentEntries();

            foreach (XElement docXEl in docXEls)
            {
                docPackage.Documents.Add(ConsumeDocument(docXEl));
            }

            return docPackage;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="packageXEl"></param>
        /// <returns></returns>
        public static DocumentPackage ConsumePackage(XElement packageXEl)
        {
            DocumentPackage package = new DocumentPackage();
            package.Author = ConsumeAuthor(packageXEl.Classification(XDMetadataStandard.UUIDs.SubmissionSetAuthor));
            package.Comments = packageXEl.DescriptionValue();
            package.ContentTypeCode = ConsumeCodedValue(packageXEl.Classification(XDMetadataStandard.UUIDs.ContentTypeCode));
            package.IntendedRecipients.AddAll(packageXEl.SlotValues<Recipient>(XDMetadataStandard.Slots.IntendedRecipient, r => Recipient.FromXONXCNXTN(r)));
            package.PatientId = packageXEl.ExternalIdentifierValue<PatientID>(XDMetadataStandard.UUIDs.SubmissionSetPatientId, s => PatientID.FromEscapedCx(s));
            package.SourceId = packageXEl.ExternalIdentifierValue(XDMetadataStandard.UUIDs.SubmissionSetSourceId);
            package.SubmissionTime = packageXEl.SlotValue<DateTime?>(XDMetadataStandard.Slots.SubmissionTime, s => HL7Util.DateTimeFromHL7Value(s));
            package.Title = packageXEl.NameValue();
            package.UniqueId = packageXEl.ExternalIdentifierValue(XDMetadataStandard.UUIDs.SubmissionSetUniqueId);

            return package;
        }

        /// <summary>
        /// Creates a document from an XML XElement representation
        /// </summary>
        public static DocumentMetadata ConsumeDocument(XElement docXEl)
        {
            DocumentMetadata doc = new DocumentMetadata();
            doc.Author = ConsumeAuthor(docXEl.Classification(XDMetadataStandard.UUIDs.DocumentAuthor));
            doc.Class = ConsumeCodedValue(docXEl.Classification(XDMetadataStandard.UUIDs.DocumentClass));
            doc.Comments = docXEl.DescriptionValue();
            doc.Confidentiality = ConsumeCodedValue(docXEl.Classification(XDMetadataStandard.UUIDs.DocumentConfidentiality));
            doc.CreatedOn = docXEl.SlotValue<DateTime?>(XDMetadataStandard.Slots.CreationTime, s => HL7Util.DateTimeFromHL7Value(s));
            doc.EventCodes = docXEl.Classifications(XDMetadataStandard.UUIDs.EventCode).Select(c => ConsumeCodedValue(c));
            doc.FormatCode = ConsumeCodedValue(docXEl.Classification(XDMetadataStandard.UUIDs.FormatCode));
            doc.FacilityCode = ConsumeCodedValue(docXEl.Classification(XDMetadataStandard.UUIDs.FacilityCode));
            doc.Hash = docXEl.SlotValue(XDMetadataStandard.Slots.Hash);
            doc.LanguageCode = docXEl.SlotValue(XDMetadataStandard.Slots.LanguageCode);
            doc.LegalAuthenticator = docXEl.SlotValue<Person>(XDMetadataStandard.Slots.LegalAuthenticator, s => Person.FromXCN(s));
            doc.MediaType = docXEl.AttributeValue(XDMetadataStandard.Attrs.MimeType);
            doc.PatientID = docXEl.ExternalIdentifierValue(XDMetadataStandard.UUIDs.DocumentEntryPatientIdentityScheme, s => PatientID.FromEscapedCx(s));
            doc.ServiceStart = docXEl.SlotValue<DateTime?>(XDMetadataStandard.Slots.ServiceStart, s => HL7Util.DateTimeFromHL7Value(s));
            doc.ServiceStop = docXEl.SlotValue<DateTime?>(XDMetadataStandard.Slots.ServiceStop, s => HL7Util.DateTimeFromHL7Value(s));
            doc.PracticeSetting = ConsumeCodedValue(docXEl.Classification(XDMetadataStandard.UUIDs.PracticeSetting));
            doc.Size = docXEl.SlotValue<int?>(XDMetadataStandard.Slots.Size, s => Parse(s));
            doc.SourcePtId = docXEl.SlotValue<PatientID>(XDMetadataStandard.Slots.SourcePatientID, s => PatientID.FromEscapedCx(s));
            doc.Patient = Person.FromSourcePatientInfoValues(docXEl.SlotValues(XDMetadataStandard.Slots.SourcePatientInfo));
            doc.Title = docXEl.NameValue();
            // Ignore TypeCode
            doc.UniqueId = docXEl.ExternalIdentifierValue(XDMetadataStandard.UUIDs.DocumentUniqueIdIdentityScheme);
            doc.Uri = ConsumeUriValues(docXEl.SlotValues(XDMetadataStandard.Slots.Uri));

            return doc;
        }

        static Author ConsumeAuthor(XElement authorXEl)
        {
            Author a = new Author();
            a.Person = Person.FromXCN(authorXEl.SlotValue(XDMetadataStandard.Slots.AuthorPerson));
            foreach (Institution i in (authorXEl.SlotValues(XDMetadataStandard.Slots.AuthorInstitutions).Select(i => Institution.FromXON(i))))
            {
                a.Institutions.Add(i);
            }
            foreach (string s in authorXEl.SlotValues(XDMetadataStandard.Slots.AuthorRoles))
            {
                a.Roles.Add(s);
            }
            foreach (string s in authorXEl.SlotValues(XDMetadataStandard.Slots.AuthorSpecialities))
            {
                a.Specialities.Add(s);
            }
            return a;
        }

        static CodedValue ConsumeCodedValue(XElement codedValueClassification)
        {
            XAttribute nodeRep = codedValueClassification.Attribute(XDMetadataStandard.Attrs.NodeRepresentation);
            string codingScheme = codedValueClassification.SlotValue(XDMetadataStandard.Slots.CodingScheme);
            string codeLabel = codedValueClassification.NameValue();
            //TODO: should be more specific parsing error
            if (nodeRep == null || codingScheme == null || codeLabel == null) throw new ArgumentException();
            return new CodedValue(nodeRep.Value, codeLabel, codingScheme);
        }

        static int? Parse(string s)
        {
            int i;
            bool worked = Int32.TryParse(s, out i);
            return worked ? i as int? : null;
        }

        /// <summary>
        /// Takes an IHE formatted multivalue Uri string and returns the joined URI
        /// </summary>
        /// <remarks>
        /// The values are:
        /// (1) A single value of "uri"
        /// (2) A set of values, each one is of the form "n|uripart", where n must range from 0->n but where the set
        /// is not guaranteed to be ordered. In this form, n may equal 1 :-)
        /// </remarks>
        static string ConsumeUriValues(IEnumerable<string> values)
        {
            if (values == null) return null;
            if (values.Count() == 1)
            {
                string[] vals = values.First().Split('|');
                return (vals.Length == 1) ? vals[0] : vals[1];
            }
            List<string> valList = values.ToList<string>();
            valList.Sort();
            //TODO: Assumes strings properly formatted; should return parsing error
            return Extensions.Join("", valList.Select(s => s.Split('|')[1]));
        }


    }
}