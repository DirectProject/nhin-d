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
using System.Xml.Linq;

namespace Health.Direct.Xd
{
    /// <summary>
    /// Provides an <see cref="XElement"/> for an IHE compliant ebXML classification
    /// </summary>
    public class Classification : XElement
    {

        private Classification()
            : base("Classification")
        {}

        /// <summary>
        /// Initializes a node classification with the specified classificationNode attribute
        /// </summary>
        public Classification(string node, string classifiedObject)
            : this()
        {
            InitializeContentBase(classifiedObject);
            this.Add(new XAttribute("classificationNode", node));
        }

        /// <summary>
        /// Initializes a classification with the supplied content, interpreted as an <see cref="XElement"/>
        /// </summary>
        /// <param name="scheme">The classification scheme UUID</param>
        /// <param name="nodeRepresentation">The node representation </param>
        /// <param name="classifiedObject">The classified content</param>
        /// <param name="content">The content (interpreted like the equivalent parameter of <see cref="XElement"/></param>
        public Classification(string scheme, string nodeRepresentation, string classifiedObject, params Object[] content)
            : this()
        {
            InitializeContentScheme(scheme, nodeRepresentation, classifiedObject, content);            
        }

        /// <summary>
        /// Initalizes a classification with the supplied content, looking up scheme and node representation for
        /// the XD attribute type provided
        /// </summary>
        /// <param name="attr">The XD attribute</param>
        /// <param name="nodeRep">The node representation for this classification</param>
        /// <param name="classifiedObject">The object being classified</param>
        /// <param name="content">Included nodes in this classification</param>
        public Classification(XDAttribute attr, string nodeRep, string classifiedObject, params Object[] content)
            : this ()
        {
            if (!XDMetadataStandard.IsClassification(attr))
            {
                throw new ArgumentException("Invalid Classification Type");
            }
            string scheme = XDMetadataStandard.ClassificationUUIDs[attr];
            InitializeContentScheme(scheme, nodeRep, classifiedObject, content);
        }

        private void InitializeContentScheme(string scheme, string nodeRepresentation, string classifiedObject, params Object[] content)
        {
            InitializeContentBase(classifiedObject);
            this.Add(new XAttribute("classificationScheme", scheme),
                     new XAttribute("nodeRepresentation", nodeRepresentation),
                     content);
        }

        private void InitializeContentBase(string classifiedObject)
        {
            this.Add(new XAttribute(XDMetadataStandard.Attrs.ClassifiedObject, classifiedObject),
                     new XAttribute("objectType", "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification"));
        }


    }
}