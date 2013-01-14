/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    george cole     george.cole@allscripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Xml;
using System.Xml.Serialization;
using Health.Direct.Xd.Common;

namespace Health.Direct.Xdr
{
    [XmlRoot(Namespace = GlobalValues.IHEXDSbNamespace, ElementName = "ProvideAndRegisterDocumentSetRequest")]
    class ProvideAndRegisterRequest : IXmlSerializable
    {
        #region constructor
        public ProvideAndRegisterRequest()
        {
            submissionDocuments = new List<SubmissionDocument>();
        }
        #endregion

        #region properties
        private XmlNode m_submissionMetadata;

        public XmlNode submissionMetadata
        {
            get { return m_submissionMetadata; }
            set { m_submissionMetadata = value; }
        }

        private List<SubmissionDocument> m_submissionDocuments;

        public List<SubmissionDocument> submissionDocuments
        {
            get { return m_submissionDocuments; }
            set { m_submissionDocuments = value; }
        }
        #endregion

        #region IXmlSerializable Members

        public System.Xml.Schema.XmlSchema GetSchema()
        {
            throw new Exception("The method or operation is not implemented.");
        }

        /// <summary>
        /// convert the xml representation of this class into the document and metadata properties
        /// </summary>
        /// <param name="reader"></param>
        public void ReadXml(XmlReader reader)
        {
           // good place for debug logging
            try
            {
                // take the reader, turn into an XmlDocument, then parse out the metadata and documents
                XmlDocument xmldoc = new XmlDocument();
                xmldoc.LoadXml(reader.ReadOuterXml());
                submissionMetadata = xmldoc.SelectSingleNode(@".//*[local-name()='SubmitObjectsRequest']");     // cheating and ignoring namespaces
                if (submissionMetadata == null)
                {
                    // good place for debug logging
                    throw new Exception("P&RRequestXDSB Reader cannot find SubmitObjectsRequest");
                }
                XmlNodeList theDocs = xmldoc.SelectNodes(@".//*[local-name()='Document']");
                if (theDocs == null)
                {
                    // good place for debug logging
                    throw new Exception("P&RRequestXDSB Reader cannot find Document");
                }
                foreach (XmlNode aDoc in theDocs)
                {
                    SubmissionDocument theDoc = new SubmissionDocument();
                    theDoc.documentID = aDoc.Attributes["id"].Value.ToString();
                    theDoc.documentText = Convert.FromBase64String(aDoc.InnerText);
                    submissionDocuments.Add(theDoc);
                }
            }
            catch (Exception ex)
            {
                // good place for debug logging
                throw ex;
            }
            // good place for debug logging
        }

        /// <summary>
        /// generate the XML representation of this class
        /// </summary>
        /// <param name="writer"></param>
        public void WriteXml(XmlWriter writer)
        {
            // good place for debug logging
            // write the metadata into root element followed by all of the documents
            // notice that the root element already exists (created internally by .net)
            try
            {
                writer.WriteNode(submissionMetadata.CreateNavigator(), true);
                // now for each document
                foreach (SubmissionDocument thedoc in submissionDocuments)
                {
                    writer.WriteStartElement("Document");
                    writer.WriteAttributeString("id", thedoc.documentID);
                    writer.WriteBase64(thedoc.documentText, 0, thedoc.documentText.Length);
                    writer.WriteEndElement();
                }
            }
            catch (Exception ex)
            {
                // good place for debug logging
                throw;
            }
            // good place for debug logging
        }

        #endregion
    }
}
