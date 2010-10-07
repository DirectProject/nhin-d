using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.Xml.Serialization;

namespace NHINDirect.XDS
{
    [XmlRoot(Namespace = "urn:ihe:iti:xds-b:2007", ElementName = "ProvideAndRegisterDocumentSetRequest")]
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
