using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.ServiceModel;
using System.ServiceModel.Channels;
using System.Text;
using System.Xml;

using Health.Direct.Common.Diagnostics;
using Health.Direct.Xd.Common;

namespace Health.Direct.Xdr
{
    public class ReceiveDocumentSet : IReceiveDocumentSet
    {
        Message IReceiveDocumentSet.ReceiveDocumentSet(Message msgRequest)
        {
            Message msgResponse = null;
            XmlDocument xmlDocRequest = null;
            XmlDocument xmlDocResponse = null;
            XDSHelper xdsHelper = null;
            StringDictionary stringDictionary = null;

            ILogger logger = Log.For(this);

            try
            {
                xdsHelper = new XDSHelper();

                //Request XmlDocument
                xmlDocRequest = new XmlDocument();
                xmlDocRequest.Load(msgRequest.GetReaderAtBodyContents());

                //Process Message will Construct Response for Register Transaction Set-B
                //Recieves a message from Provide and register document set B(With no errors from Repository)
                // changes for PnR - register document set async
                xmlDocResponse = ProcessReceiveDocumentSet(xmlDocRequest, msgRequest.Headers.MessageId, msgRequest.Headers.MessageVersion, out stringDictionary);
            }
            catch (ServerTooBusyException serverTooBusyException)
            {
                logger.Error("Server Too Busy", serverTooBusyException);
                //Construct Error Response
                xmlDocResponse = CommonUtility.ConstructRegistryErrorResponse(GlobalValues.CONST_RESPONSE_STATUS_TYPE_FAILURE, string.Empty, GlobalValues.CONST_ERROR_CODE_XDSRepositoryTooBusyException, GlobalValues.CONST_ERROR_CODE_XDSRepositoryTooBusyException, GlobalValues.CONST_SEVERITY_TYPE_ERROR, string.Empty);
            }
            catch (TimeoutException timeoutException)
            {
                logger.Error("Timeout processing document submission", timeoutException);
                //Construct Error Response
                xmlDocResponse = CommonUtility.ConstructRegistryErrorResponse(GlobalValues.CONST_RESPONSE_STATUS_TYPE_FAILURE, string.Empty, GlobalValues.CONST_ERROR_CODE_TimeOut, GlobalValues.CONST_ERROR_CODE_TimeOut, GlobalValues.CONST_SEVERITY_TYPE_ERROR, string.Empty);
            }
            catch (System.ServiceModel.Security.SecurityAccessDeniedException AuthorizationException)
            {
                logger.Error("Access denied", AuthorizationException);
                //Construct Error Response
                xmlDocResponse = CommonUtility.ConstructRegistryErrorResponse(GlobalValues.CONST_RESPONSE_STATUS_TYPE_FAILURE, string.Empty, GlobalValues.CONST_ERROR_CODE_XDSAuthorizationException, GlobalValues.CONST_ERROR_CODE_XDSAuthorizationException, GlobalValues.CONST_SEVERITY_TYPE_ERROR, string.Empty);
            }
            catch (OutOfMemoryException RepositoryOutOfResources)
            {
                logger.Error("Out of resources", RepositoryOutOfResources);
                //Construct Error Response
                xmlDocResponse = CommonUtility.ConstructRegistryErrorResponse(GlobalValues.CONST_RESPONSE_STATUS_TYPE_FAILURE, string.Empty, GlobalValues.CONST_ERROR_CODE_XDSRepositoryOutOfResources, GlobalValues.CONST_ERROR_CODE_XDSRepositoryOutOfResources, GlobalValues.CONST_SEVERITY_TYPE_ERROR, string.Empty);
            }
            catch (Exception ex)
            {
                if (ex.Message == GlobalValues.CONST_ERROR_CODE_XDSMissingDocumentMetadata)
                {
                    //Construct Error Response
                    xmlDocResponse = CommonUtility.ConstructRegistryErrorResponse(GlobalValues.CONST_RESPONSE_STATUS_TYPE_FAILURE, string.Empty, "XDSDocumentEntry document exists in metadata with no corresponding metatdata", GlobalValues.CONST_ERROR_CODE_XDSMissingDocumentMetadata, GlobalValues.CONST_SEVERITY_TYPE_ERROR, string.Empty);

                }
                else if (ex.Message == GlobalValues.CONST_ERROR_CODE_XDSMissingDocumentAttachment)
                {
                    //Construct Error Response
                    xmlDocResponse = CommonUtility.ConstructRegistryErrorResponse(GlobalValues.CONST_RESPONSE_STATUS_TYPE_FAILURE, string.Empty, "XDSDocumentEntry exists in metadata with no corresponding attached document", GlobalValues.CONST_ERROR_CODE_XDSMissingDocumentAttachment, GlobalValues.CONST_SEVERITY_TYPE_ERROR, string.Empty);
                }
                else if (ex.Message == GlobalValues.CONST_ERROR_CODE_XDSInvalidRequest)
                {
                    //Construct Error Response
                    xmlDocResponse = CommonUtility.ConstructRegistryErrorResponse(GlobalValues.CONST_RESPONSE_STATUS_TYPE_FAILURE, string.Empty, "XDSInvalidRequest - DcoumentId is not unique.", GlobalValues.CONST_ERROR_CODE_XDSInvalidRequest, GlobalValues.CONST_SEVERITY_TYPE_ERROR, string.Empty);
                }
                else if (ex.Message == GlobalValues.CONST_ERROR_CODE_XDSRepositoryMetadataError)
                {
                    //Construct Error Response
                    xmlDocResponse = CommonUtility.ConstructRegistryErrorResponse(GlobalValues.CONST_RESPONSE_STATUS_TYPE_FAILURE, string.Empty, "Error occurred in Parsing the Metadata.", GlobalValues.CONST_ERROR_CODE_XDSRepositoryMetadataError, GlobalValues.CONST_SEVERITY_TYPE_ERROR, string.Empty);
                }
                else if (ex.Message == GlobalValues.CONST_ERROR_CODE_XDSRegistryNotAvailable)
                {
                    //Construct Error Response
                    xmlDocResponse = CommonUtility.ConstructRegistryErrorResponse(GlobalValues.CONST_RESPONSE_STATUS_TYPE_FAILURE, string.Empty, "Repository was unable to access the Registry.", GlobalValues.CONST_ERROR_CODE_XDSRegistryNotAvailable, GlobalValues.CONST_SEVERITY_TYPE_ERROR, string.Empty);
                }
                else
                {
                    //Construct Error Response
                    xmlDocResponse = CommonUtility.ConstructRegistryErrorResponse(GlobalValues.CONST_RESPONSE_STATUS_TYPE_FAILURE, string.Empty, GlobalValues.CONST_ERROR_CODE_XDSRepositoryError, GlobalValues.CONST_ERROR_CODE_XDSRepositoryError, GlobalValues.CONST_SEVERITY_TYPE_ERROR, string.Empty);
                }
            }

            try
            {
                msgResponse = Message.CreateMessage(msgRequest.Headers.MessageVersion, GlobalValues.CONST_ACTION_ProvideAndRegisterDocumentSet_bResponse, new XmlNodeReader(xmlDocResponse));

                return msgResponse;
            }
            catch (Exception ex)
            {
                logger.Error("Unexpected error", ex);
                throw ex;
            }
        }
        // Process the document set that was received
        private XmlDocument ProcessReceiveDocumentSet(XmlDocument xmlDocRequest, System.Xml.UniqueId messageID, MessageVersion msgVersion, out StringDictionary atnaParameterValues)
        {
            XmlDocument xmlDocResponse = null;
            Message registryMessage = null;
            XmlDocument xmlDocRegistryResponse = null;
            XDSHelper xdsHelper = null;
            XmlElement eltProvideAndRegDocSet = null;
            XmlNodeList nodeListExtrinsicObject = null;
            XmlNodeList nodeListDocument = null;
            XmlNode nodeSubmissionSet = null;
            XmlNode nodeExternalIdentifier = null;
            List<Document> docList = null;
            StringBuilder sbMetaData = null;
            string xpathExternalIdentifierDocument = @".//*[local-name()='ExtrinsicObject'][@id='$id$']/*[local-name()='ExternalIdentifier'][@identificationScheme='$identificationScheme$']";
            string xpath = null;
            string entryUUID = null;
            string uniqueID = null;
            string eventOutcomeIndicator = "0";

            atnaParameterValues = new StringDictionary();

            try
            {
                xdsHelper = new XDSHelper();
                xpathExternalIdentifierDocument = xpathExternalIdentifierDocument.Replace("$identificationScheme$", GlobalValues.XDSDocumentEntry_uniqueIdUUID);

                // get the root document element of the XML Document which is metadata plus MTOM encoded documents
                eltProvideAndRegDocSet = xmlDocRequest.DocumentElement;

                // get the list of nodes from the root document element that are ExtrinsicObject nodes
                nodeListExtrinsicObject = eltProvideAndRegDocSet.SelectNodes(".//*[local-name()='ExtrinsicObject']");

                // get the list of nodes from the root doc element that are Document nodes
                nodeListDocument = eltProvideAndRegDocSet.SelectNodes(".//*[local-name()='Document']");

                // get the Submission Set node from the document element
                nodeSubmissionSet = eltProvideAndRegDocSet.SelectSingleNode("//*[local-name()='SubmitObjectsRequest']/*[local-name()=\"RegistryObjectList\"]/*[local-name()='RegistryPackage']/*[local-name()='ExternalIdentifier'][@identificationScheme='urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8']/@value");

                //Proceed further only if any ExtrinsicObject Exists(with attachments)
                if (nodeListExtrinsicObject == null)
                {
                    throw new Exception();
                }

                // check each ExtrinsicObject node document Id and match with attached documents to see if anything is missing
                if (xdsHelper.IsMissingDocumentAttachment(eltProvideAndRegDocSet))
                {
                    throw new Exception(GlobalValues.CONST_ERROR_CODE_XDSMissingDocumentAttachment);
                }

                //if (repositoryLogic.IsMissingDocumentMetadata(nodeListExtrinsicObject, eltProvideAndRegDocSet))
                if (xdsHelper.IsMissingDocumentMetadata(eltProvideAndRegDocSet))
                {
                    throw new Exception(GlobalValues.CONST_ERROR_CODE_XDSMissingDocumentMetadata);
                }

                // does each document have a unique id?
                if (xdsHelper.IsDuplicateUniqueID(eltProvideAndRegDocSet))
                {
                    throw new Exception(GlobalValues.CONST_ERROR_CODE_XDSRepositoryDuplicateUniqueIdInMessage);
                }

                // if we have metadata for each document and actual documents (this seems redundant with the earlier checks - refactor?)
                if (nodeListExtrinsicObject.Count > 0 && eltProvideAndRegDocSet.SelectNodes(@"//*[local-name()='Document']").Count > 0)
                {

                    //No document OR Metadata is Missing

                    // DocumentEntry object should be replaced by the new Document/Message object we want to use to carry this info
//                    lstDocumentEntry = new List<DocumentEntry>();
                    sbMetaData = new StringBuilder();
                    int loopCount = 0;
                    foreach (XmlNode node in nodeListExtrinsicObject)
                    {
//                        DocumentEntry objDocumentEntry = new DocumentEntry();

                        if (node.Attributes["mimeType"].Value == string.Empty)
                            throw new Exception(GlobalValues.CONST_ERROR_CODE_XDSRepositoryMetadataError);

//                        objDocumentEntry.MimeType = node.Attributes["mimeType"].Value;

                        entryUUID = node.Attributes["id"].Value;
                        xpath = xpathExternalIdentifierDocument.Replace("$id$", node.Attributes["id"].Value);
                        nodeExternalIdentifier = eltProvideAndRegDocSet.SelectSingleNode(xpath);
                        uniqueID = nodeExternalIdentifier.Attributes["value"].Value;

                        if (string.IsNullOrEmpty(uniqueID))
                            throw new Exception(GlobalValues.CONST_ERROR_CODE_XDSRepositoryMetadataError);

//                        objDocumentEntry.EntryUUID = entryUUID;
//                        objDocumentEntry.UniqueID = uniqueID;
//                        objDocumentEntry.Hash = xdsHelper.GetHashCode(xmlDocRequest, entryUUID);
//                        objDocumentEntry.Content = xdsHelper.GetDocumentContentStream(xmlDocRequest, entryUUID);
//                        objDocumentEntry.Size = (int)objDocumentEntry.Content.Length;

                        // CP-ITI-419
                        string docHash = string.Empty;
                        XmlNode docHashNode = eltProvideAndRegDocSet.SelectNodes("//*[local-name()='SubmitObjectsRequest']/*[local-name()=\"RegistryObjectList\"]/*[local-name()='ExtrinsicObject']/*[local-name()='Slot'][@name='hash']")[loopCount];
                        if (docHashNode != null)
                            docHash = docHashNode.InnerText.Trim();

                        string docSize = string.Empty;
                        XmlNode docSizeNode = eltProvideAndRegDocSet.SelectNodes("//*[local-name()='SubmitObjectsRequest']/*[local-name()=\"RegistryObjectList\"]/*[local-name()='ExtrinsicObject']/*[local-name()='Slot'][@name='size']")[loopCount];
                        if (docSizeNode != null)
                            docSize = docSizeNode.InnerText.Trim();

//                        if ((!string.IsNullOrEmpty(docHash) && (docHash.ToLower() != objDocumentEntry.Hash.ToLower()))
//                            || (!string.IsNullOrEmpty(docSize) && (Convert.ToInt32(docSize) != objDocumentEntry.Size)))
                        {
                            xmlDocResponse = CommonUtility.ConstructRegistryErrorResponse(GlobalValues.CONST_RESPONSE_STATUS_TYPE_FAILURE, string.Empty, "Size/hash of document not matching Metadata.", GlobalValues.CONST_ERROR_CODE_XDSRepositoryMetadataError, GlobalValues.CONST_SEVERITY_TYPE_ERROR, string.Empty);
                            eventOutcomeIndicator = "8";
                            atnaParameterValues.Add("$EventIdentification.EventOutcomeIndicator$", eventOutcomeIndicator);
                            return xmlDocResponse;
                        }
                        // CP-ITI-419

                        //                       lstDocumentEntry.Add(objDocumentEntry);

                    }

                }
                else
                {
                    xmlDocRegistryResponse = new XmlDocument();
                    xmlDocRegistryResponse.Load(registryMessage.GetReaderAtBodyContents());

                    XmlNode errorList = xmlDocRegistryResponse.SelectSingleNode(@"//*[local-name()='RegistryError']");
                    string errorCode = null;

                    if (errorList != null)
                        errorCode = errorList.Attributes["errorCode"].Value;


                    //Assign Registry Response to Repository Response
                    xmlDocResponse = xmlDocRegistryResponse;

                }
            }
            catch
            {
                throw;
            }
            try
            {
                return xmlDocResponse;
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
    }
}