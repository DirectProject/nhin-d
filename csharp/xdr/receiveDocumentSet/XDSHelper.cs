using System;
using System.Collections.Generic;
using System.Xml;
using System.IO;
using System.Xml.Schema;

using Health.Direct.Common.Diagnostics;

namespace Health.Direct.Xdr
{
    public class XDSHelper
    {
        private readonly ILogger m_logger;
        bool IsSchemaError = false;

        public XDSHelper()
        {
            m_logger = Log.For(this);
        }

        //To create slots for meta data.
        public XmlDocument CreateRepositoryMetadata(XmlDocument xmlDocMsgBody, string slotName, string slotValue, string documentID)
        {
            XmlDocument xmlDocRepositoryMetadata = new XmlDocument();
            try
            {
                xmlDocRepositoryMetadata.LoadXml(xmlDocMsgBody.OuterXml);

                XmlElement rootElement = xmlDocRepositoryMetadata.DocumentElement;
                XmlNodeList extrinsicObjects = rootElement.SelectNodes(".//*[local-name()='ExtrinsicObject']");
                XmlNode submitObjReqNode = rootElement.SelectSingleNode(@"//*[local-name()='SubmitObjectsRequest']");

                for (int nodeCount = 0; nodeCount < extrinsicObjects.Count; nodeCount++)
                {

                    XmlNode xn = extrinsicObjects[nodeCount];

                    if (xn.Attributes["id"].Value == documentID)
                    {
                        XmlElement eSlot = xmlDocRepositoryMetadata.CreateElement("Slot", "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0");
                        XmlAttribute att = xmlDocRepositoryMetadata.CreateAttribute("name");
                        att.Value = slotName;
                        eSlot.Attributes.Append(att);

                        XmlElement eVal = xmlDocRepositoryMetadata.CreateElement("ValueList", "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0");
                        eSlot.AppendChild(eVal);

                        XmlElement eValue = xmlDocRepositoryMetadata.CreateElement("Value", "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0");
                        eValue.InnerText = slotValue;

                        eVal.AppendChild(eValue);

                        //xn.AppendChild(eSlot);
                        xn.InsertAfter(eSlot, null);

                    }
                }
            }
            catch (Exception ex)
            {
                m_logger.Error("Unexpected error", ex);
                throw;
            }
            return xmlDocRepositoryMetadata;
        }


        //To get the binary content from the docuement as byte[]
        public byte[] GetDocumentContent(XmlDocument xdsDoc, string xdsUniqueId)
        {
            byte[] documentContent = null;
            try
            {
                XmlElement rootElement = xdsDoc.DocumentElement;
                XmlNodeList xdsDocuments = rootElement.SelectNodes(@"//*[local-name()='Document']");
                foreach (XmlNode xdsDocument in xdsDocuments)
                {
                    if (xdsDocument.Attributes["id"].Value == xdsUniqueId)
                    {
                        string strContent = xdsDocument.InnerText;
                        documentContent = System.Text.Encoding.ASCII.GetBytes(strContent);
                    }

                }
            }
            catch (Exception ex)
            {
                m_logger.Error("Unexpected error", ex);
                throw;
            }
            return documentContent;

        }

        public XmlDocument CreateRepositorySlotElement(XmlDocument xmlDocRequest, string slotName, string slotValue, string documentEntryUUID)
        {
            try
            {
                XmlElement eltRoot = null;
                XmlNode nodeExtrinsicObject = null;
                XmlNode nodeExtrinsicObjectSlotValue = null;
                string xpathExtrinsicObject = @".//*[local-name()='ExtrinsicObject'][@id='$id$']";
                string xpathExtrinsicObjectSlotValue = @".//*[local-name()='ExtrinsicObject'][@id='$id$']/*[local-name()='Slot'][@name='$name$']/*[local-name()='ValueList']/*[local-name()='Value']";

                //Root Element
                eltRoot = xmlDocRequest.DocumentElement;

                //ExtrinsicObject element of particular id/entryUUID
                xpathExtrinsicObject = xpathExtrinsicObject.Replace("$id$", documentEntryUUID);
                nodeExtrinsicObject = eltRoot.SelectSingleNode(xpathExtrinsicObject);

                //ExtrinsicObject->Slot->ValueList->Value
                xpathExtrinsicObjectSlotValue = xpathExtrinsicObjectSlotValue.Replace("$id$", documentEntryUUID);
                xpathExtrinsicObjectSlotValue = xpathExtrinsicObjectSlotValue.Replace("$name$", slotName);
                nodeExtrinsicObjectSlotValue = eltRoot.SelectSingleNode(xpathExtrinsicObjectSlotValue);

                if (nodeExtrinsicObject == null)
                    return xmlDocRequest;

                if (nodeExtrinsicObjectSlotValue != null)
                {
                    nodeExtrinsicObjectSlotValue.InnerText = slotValue;
                }
                else
                {
                    XmlElement eltSlot = xmlDocRequest.CreateElement("Slot", "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0");
                    XmlAttribute attribName = xmlDocRequest.CreateAttribute("name");
                    attribName.Value = slotName;
                    eltSlot.Attributes.Append(attribName);

                    XmlElement eltValueList = xmlDocRequest.CreateElement("ValueList", "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0");
                    eltSlot.AppendChild(eltValueList);

                    XmlElement eltValue = xmlDocRequest.CreateElement("Value", "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0");
                    eltValue.InnerText = slotValue;
                    eltValueList.AppendChild(eltValue);

                    nodeExtrinsicObject.InsertAfter(eltSlot, null);
                }

                return xmlDocRequest;
            }
            catch (Exception ex)
            {
                m_logger.Error("Unexpected error", ex);
                throw;
            }
        }
        public Stream GetDocumentContentStream(XmlDocument xmlDocRequest, string documentUniqueId)
        {
            Stream documentContentStream = null;
            string documentData = null;

            XmlElement eltRoot = null;
            XmlNodeList nodeListDocument = null;

            try
            {
                eltRoot = xmlDocRequest.DocumentElement;
                nodeListDocument = eltRoot.SelectNodes(@"//*[local-name()='Document']");

                foreach (XmlNode nodeDocument in nodeListDocument)
                {
                    if (nodeDocument.Attributes["id"].Value == documentUniqueId)
                    {
                        documentData = nodeDocument.InnerText;
                        documentContentStream = new MemoryStream(Convert.FromBase64String(documentData));
                        break;
                    }

                }
            }
            catch (Exception ex)
            {
                m_logger.Error("Unexpected error", ex);
                throw;
            }

            return documentContentStream;
        }

        public bool IsSchemaValid(XmlDocument xdoc)
        {
            bool IsSchemaValid = true;
            try
            {

                string xsdTNS = "urn:ihe:iti:xds-b:2007";
                string xsdPath = Path.GetDirectoryName(System.Reflection.Assembly.GetExecutingAssembly().Location) + @"\Schemas\IHEXDS.xsd";

                XmlReaderSettings settings = new XmlReaderSettings();
                settings.ValidationEventHandler += new ValidationEventHandler(settings_ValidationEventHandler);
                settings.ValidationType = ValidationType.Schema;
                settings.Schemas.Add(xsdTNS, xsdPath);
                XmlReader reader = XmlReader.Create(new StringReader(xdoc.OuterXml), settings);
                while (reader.Read())
                {
                    //Let Reader read all contents
                }
                /* Original was if (!IsSchemaError) 
                 * then IsSchemaValid = false - 
                 * this is wrong, as IsSchemaError is only true when a schema error is throw. */
                if (IsSchemaError)
                {
                    IsSchemaValid = false;
                }


            }
            catch (Exception ex)
            {
                m_logger.Error("Error attempting to validate schema", ex);
                IsSchemaValid = false;
                throw ex;
            }
            return IsSchemaValid;
        }

        void settings_ValidationEventHandler(object sender, ValidationEventArgs e)
        {
            //TODO::Event Log for this schema
            IsSchemaError = true;

        }

        // Check the XML document root element to see if the number of referenced documents is the same as the number of document attachments
        public bool IsMissingDocumentAttachment(XmlElement eltProvideAndRegDocSet)
        {
            bool isMissingDocumentAttachment = false;
            XmlNodeList nodeListExtrinsicObject = null;
            XmlNodeList nodeListDocument = null;
            List<string> lstExtrinsicObjectId = new List<string>();
            List<string> lstDocumentId = new List<string>();

            nodeListExtrinsicObject = eltProvideAndRegDocSet.SelectNodes(".//*[local-name()='ExtrinsicObject']");

            nodeListDocument = eltProvideAndRegDocSet.SelectNodes(".//*[local-name()='Document']");

            foreach (XmlNode node in nodeListExtrinsicObject)
            {
                lstExtrinsicObjectId.Add(node.Attributes["id"].Value);
            }

            foreach (XmlNode node in nodeListDocument)
            {
                lstDocumentId.Add(node.Attributes["id"].Value);
            }

            if (lstDocumentId.Count < lstExtrinsicObjectId.Count)
            {
                isMissingDocumentAttachment = true;
                return isMissingDocumentAttachment;
            }

            for (int count = 0; count < lstExtrinsicObjectId.Count; count++)
            {
                if (!lstDocumentId.Contains(lstExtrinsicObjectId[count]))
                {
                    isMissingDocumentAttachment = true;
                    break;
                }
            }

            return isMissingDocumentAttachment;
        }

        // does every document have a unique id?
        public bool IsDuplicateUniqueID(XmlNode rootElement)
        {
            bool IsTrue = false;
            try
            {
                XmlNodeList nodeListSubmitObjectsRequest = rootElement.SelectNodes(".//*[local-name()='SubmitObjectsRequest']//@id");
                List<string> lstSubmitObjectsRequestValue = new List<string>();

                foreach (XmlNode n in nodeListSubmitObjectsRequest)
                {
                    lstSubmitObjectsRequestValue.Add(n.Value);
                }
                for (int count = 0; count < lstSubmitObjectsRequestValue.Count; count++)
                {

                    int index = lstSubmitObjectsRequestValue.IndexOf(lstSubmitObjectsRequestValue[count], count + 1);
                    if (index != -1)
                    {
                        IsTrue = true;
                        break;
                    }

                }

            }
            catch
            {
                throw;
            }


            return IsTrue;
        }

        // TODO: this seems to do the same thing as isMissingDocumentAttachments - should be refactored
        public bool IsMissingDocumentMetadata(XmlElement eltProvideAndRegDocSet)
        {
            bool isMissingDocumentMetadata = false;
            XmlNodeList nodeListExtrinsicObject = null;
            XmlNodeList nodeListDocument = null;
            List<string> lstExtrinsicObjectId = new List<string>();
            List<string> lstDocumentId = new List<string>();

            nodeListExtrinsicObject = eltProvideAndRegDocSet.SelectNodes(".//*[local-name()='ExtrinsicObject']");

            nodeListDocument = eltProvideAndRegDocSet.SelectNodes(".//*[local-name()='Document']");

            foreach (XmlNode node in nodeListExtrinsicObject)
            {
                lstExtrinsicObjectId.Add(node.Attributes["id"].Value);
            }

            foreach (XmlNode node in nodeListDocument)
            {
                lstDocumentId.Add(node.Attributes["id"].Value);
            }

            if (lstExtrinsicObjectId.Count < lstDocumentId.Count)
            {
                isMissingDocumentMetadata = true;
                return isMissingDocumentMetadata;
            }

            for (int count = 0; count < lstDocumentId.Count; count++)
            {
                if (!lstExtrinsicObjectId.Contains(lstDocumentId[count]))
                {
                    isMissingDocumentMetadata = true;
                    break;
                }
            }

            return isMissingDocumentMetadata;
        }


    }
}