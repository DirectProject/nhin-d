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

using NHINDirect.Metadata;

namespace NHINDirect.Xd
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

            DocumentPackage docPackage = new DocumentPackage();

            IEnumerable<XElement> docXEls = docPackageXElement.DocumentEntries();

            foreach (XElement docXEl in docXEls)
            {
                docPackage.Documents.Add(ConsumeDocument(docXEl));
            }

            return docPackage;
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
            XAttribute nodeRep = codedValueClassification.Attribute(XDMetadataStandard.NodeRepresentationAttr);
            string codingScheme = codedValueClassification.SlotValue(XDMetadataStandard.Slots.CodingScheme);
            string codeLabel = codedValueClassification.NameValue();
            if (nodeRep == null || codingScheme == null || codeLabel == null) throw new ArgumentException();
            return new CodedValue(nodeRep.Value, codeLabel, codingScheme);
        }
    }
}
