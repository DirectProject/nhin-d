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
    /// A pair of objects (poor man's Tuple)
    /// </summary>
    public class Pair<T1, T2>
    {
        private T1 m_first;
        private T2 m_second;

        /// <summary>
        /// Initializes a pair
        /// </summary>
        public Pair(T1 first, T2 second)
        {
            m_first = first;
            m_second = second;
        }

        /// <summary>
        /// The first item in the pair
        /// </summary>
        public T1 First { get { return m_first; } }
        /// <summary>
        /// The second item in the pair
        /// </summary>
        public T2 Second { get { return m_second; } }

        /// <summary>
        /// Makes a pair
        /// </summary>
        public static Pair<T1,T2> Make(T1 first, T2 second)
        {
            return new Pair<T1, T2>(first, second);
        }
    }

    /// <summary>
    /// Extension and other Methods for generating XElements for IHE XD* ebXML from metadata objects.
    /// </summary>
    public static class XDMetadataGenerator
    {

        private static Pair<Object, Func<XObject>> Map(Object o, Func<XObject> e)
        {
            return Pair<Object, Func<XObject>>.Make(o, e);
        }


        // Submission

        /// <summary>
        /// Returns an <see cref="XElement"/> for this package in IHE XD* ebXML
        /// </summary>
        public static XElement Generate(this DocumentPackage docPackage)
        {
            XNamespace lcm = XDMetadataStandard.Ns.Lcm;

            XElement submitObjectsRequest = new XElement(lcm + XDMetadataStandard.Elts.SubmitObjectsRequest,
                new XAttribute(XNamespace.Xmlns + "lcm", XDMetadataStandard.Ns.Lcm),
                new XAttribute("xmlns", XDMetadataStandard.Ns.Rim)); // RIM is the default NS

            XElement packageList = new XElement(XDMetadataStandard.Elts.RegistryObjectsList); 
            submitObjectsRequest.Add(packageList);
            XElement package = GeneratePackage(docPackage);
            packageList.Add(package);

            foreach (DocumentMetadata m in docPackage.Documents)
            {
                XElement doc = m.Generate();
                Association assoc = Association.OriginalDocumentAssociation(
                    package.AttributeValue(XDMetadataStandard.Attrs.Id),
                    doc.AttributeValue(XDMetadataStandard.Attrs.Id));
                packageList.Add(doc);
                packageList.Add(assoc);
            }
            return submitObjectsRequest;
        }

        /// <summary>
        /// Generates an <see cref="XElement"/> for a RegistryPackage
        /// </summary>
        public static XElement GeneratePackage(DocumentPackage dp)
        {
            string packageId = MakeUUID();
            XElement packageMetadata = new XElement(XDMetadataStandard.Elts.SubmissionSet,
                                                    new XAttribute(XDMetadataStandard.Attrs.Id, packageId),
                                                    new Classification(XDMetadataStandard.UUIDs.SubmissionSetClassification, packageId));

            List<Pair<Object, Func<XObject>>> specs = 
            new List<Pair<Object, Func<XObject>>>
            {   
                // XSD requires the following order: name, description, slots, classifications, external identifiers
                Map(dp.Title,             () => new Name(dp.Title)),
                Map(dp.Comments,          () => new Description(dp.Comments)),
                Map(dp.Author,            () => new MultiSlotClassification(XDMetadataStandard.UUIDs.SubmissionSetAuthor, "", packageId, AuthorSlots(dp.Author))),
                Map(dp.IntendedRecipients,() => new Slot(XDMetadataStandard.Slots.IntendedRecipient, dp.IntendedRecipients.Select(r => r.ToXONXCNXTN()))),
                Map(dp.SubmissionTime,    () => new Slot(XDMetadataStandard.Slots.SubmissionTime, dp.SubmissionTime.ToHL7Date())),
                Map(dp.ContentTypeCode,   () => new CodedValueClassification(XDAttribute.ContentTypeCode, packageId, dp.ContentTypeCode)),
                Map(dp.PatientId,         () => new ExternalIdentifier(XDMetadataStandard.UUIDs.SubmissionSetPatientId, dp.PatientId.ToEscapedCx(), "XDSSubmissionSet.patientId")),
                Map(dp.SourceId,          () => new ExternalIdentifier(XDMetadataStandard.UUIDs.SubmissionSetSourceId, dp.SourceId, "XDSSubmissionSet.sourceId")),
                Map(dp.UniqueId,          () => new ExternalIdentifier(XDMetadataStandard.UUIDs.SubmissionSetUniqueId, dp.UniqueId, "XDSSubmissionSet.uniqueId")),
            };

            foreach (Pair<Object, Func<XObject>> p in specs)
            {
                if (p.First != null) packageMetadata.Add(p.Second());
            }


            return packageMetadata;
        }



        // Document

        /// <summary>
        /// Returns an <see cref="XElement"/> for this document metadata in IHE XD* ebXML
        /// </summary>
        /// <param name="dm">This document metadata</param>
        /// <returns>An <see cref="XElement"/> for this document metadata in IHE XD* ebXML</returns>
        public static XElement Generate(this DocumentMetadata dm)
        {

            // TODO: this method cries out for a domain specific language, but this turns out to be harder to
            // do in C# than in Ruby :-)
            string documentName = MakeUUID();

            XElement docEbX = new XElement(
                XDMetadataStandard.Elts.DocumentEntry,
                new XAttribute(XDMetadataStandard.Attrs.Id, documentName),
                new XAttribute(XDMetadataStandard.Attrs.ObjectType, XDMetadataStandard.UUIDs.DocumentEntry));


            List<Pair<Object, Func<XObject>>> specs =
            new List<Pair<Object, Func<XObject>>>
            {
                Map(dm.MediaType,         () => new XAttribute(XDMetadataStandard.Attrs.MimeType, dm.MediaType) ),
                // XSD requires the following order: name, description, slots, classifications, external identifiers
                Map(dm.Title,             () => new Name(dm.Title) ),
                Map(dm.Comments,          () => new Description(dm.Comments)),
                Map(dm.Author,            () => new MultiSlotClassification(XDMetadataStandard.UUIDs.DocumentAuthor, "", documentName, AuthorSlots(dm.Author))),
                Map(dm.CreatedOn,         () => new Slot(XDMetadataStandard.Slots.CreationTime, dm.CreatedOn.ToHL7Date())),
                Map(dm.Hash,              () => new Slot(XDMetadataStandard.Slots.Hash, dm.Hash)),
                Map(dm.LanguageCode,      () => new Slot(XDMetadataStandard.Slots.LanguageCode, dm.LanguageCode)),
                Map(dm.LegalAuthenticator,() => new Slot(XDMetadataStandard.Slots.LegalAuthenticator, dm.LegalAuthenticator.ToXCN())),
                Map(dm.ServiceStart,      () => new Slot(XDMetadataStandard.Slots.ServiceStart, dm.ServiceStart.ToHL7Date())),
                Map(dm.ServiceStop,       () => new Slot(XDMetadataStandard.Slots.ServiceStop, dm.ServiceStop.ToHL7Date()) ),
                Map(dm.Size,              () => new Slot(XDMetadataStandard.Slots.Size, dm.Size.ToString()) ),
                Map(dm.SourcePtId,        () => new Slot(XDMetadataStandard.Slots.SourcePatientID, dm.SourcePtId.ToEscapedCx()) ),
                Map(dm.Patient,           () => new Slot(XDMetadataStandard.Slots.SourcePatientInfo, dm.Patient.ToSourcePatientInfoValues(dm.SourcePtId))),
                Map(dm.Uri,               () => new Slot(XDMetadataStandard.Slots.Uri, UriValues(dm.Uri))),
                Map(dm.Class,             () => new CodedValueClassification(XDAttribute.ClassCode, documentName, dm.Class)),
                Map(dm.Confidentiality,   () => new CodedValueClassification(XDAttribute.ConfidentialityCode, documentName, dm.Confidentiality)),
                Map(dm.EventCodes,        () => EventCodeClassifications(dm.EventCodes, documentName)),
                Map(dm.FormatCode,        () => new CodedValueClassification(XDAttribute.FormatCode, documentName, dm.FormatCode)),
                Map(dm.FacilityCode,      () => new CodedValueClassification(XDAttribute.FacilityType, documentName,dm.FacilityCode)),
                Map(dm.PracticeSetting,   () => new CodedValueClassification(XDAttribute.PracticeSettingCode, documentName, dm.PracticeSetting) ),
                // typeCode is the same as class.
                Map(dm.Class,             () => new CodedValueClassification(XDAttribute.TypeCode, documentName, dm.Class) ),
                Map(dm.PatientID,         () => new ExternalIdentifier(XDMetadataStandard.UUIDs.DocumentEntryPatientIdentityScheme, dm.PatientID.ToEscapedCx(), "XDSDocumentEntry.patientId")),
                Map(dm.UniqueId,          () => new ExternalIdentifier(XDMetadataStandard.UUIDs.DocumentUniqueIdIdentityScheme, dm.UniqueId,"XDSDocumentEntry.uniqueId")),
            };

            foreach (Pair<Object, Func<XObject>> p in specs)
            {
                if (p.First != null) docEbX.Add(p.Second());
            }

            return docEbX;
        }

        /// <summary>
        /// Generates Event Code coded value classifications for each event code
        /// </summary>
        public static XElement EventCodeClassifications(IEnumerable<CodedValue> values, string documentName)
        {
            var elts = from c in values
                       select new CodedValueClassification(XDAttribute.EventCodeList, documentName, c);
            if (elts.Count() == 0)
                return null;
            XElement e = null;
            foreach (XElement el in elts)
            {
                if (e == null) e = el;
                else e.AddAfterSelf (el);
            }
            return e;
        }

        // Author

        /// <summary>
        /// Slots for the Author classification
        /// </summary>
        public static IEnumerable<Slot> AuthorSlots(Author a)
        {
            if (a == null)
                yield break;
            if (a.Specialities != null)
                yield return new Slot(XDMetadataStandard.Slots.AuthorSpecialities, a.Specialities);
            if (a.Roles != null)
                yield return new Slot(XDMetadataStandard.Slots.AuthorRoles, a.Roles);
            if (a.Institutions != null)
                yield return new Slot(XDMetadataStandard.Slots.AuthorInstitutions, a.Institutions.Select(i => i.ToXON()));
            if (a.Person != null)
                yield return new Slot(XDMetadataStandard.Slots.AuthorPerson, a.Person.ToXCN());
        }

        /// <summary>
        /// Implements the URI break algorithm for the IHE XD* URI document attribute
        /// </summary>
        public static IEnumerable<string> UriValues(string Uri)
        {
            IEnumerable<string> strings = Uri.Break(128);
            if (strings.Count() == 1) return strings;
            return strings.Select((string s, int i) => String.Format("{0}|{1}", i + 1, s));
        }

        /// <summary>
        /// Makes a UUID in the urn:uuid:* format
        /// </summary>
        public static string MakeUUID()
        {
            return "urn:uuid:" + Guid.NewGuid();
        }
    }
}