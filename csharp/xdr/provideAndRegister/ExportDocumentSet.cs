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
using System.Net;
using System.Net.Security;
using System.Text;
using System.Xml;
using System.Xml.XPath;
using System.Xml.Serialization;
using System.Runtime.Serialization;
using System.Security.Cryptography.X509Certificates;
using System.ServiceModel;
using System.ServiceModel.Channels;

using Health.Direct.Xd.Common;
using Health.Direct.Common.Metadata;

using WCF = System.ServiceModel.Channels;

using System.IO;

namespace Health.Direct.Xdr
{
    public class ExportDocumentSet: IExportDocumentSet
    {
        //# region for diagnostics
        //private ILogger m_Logger;

        //private ILogger Logger
        //{
        //    get { return m_Logger; }
        //}
        //#endregion


        #region IExportDocumentSet Members
        public ProvideAndRegisterResponse ProvideAndRegisterDocumentSet(DocumentPackage package, string endpointUrl, string certThumbprint)
        {
            EndpointAddress endpointAddress;
            X509Certificate2 clientCert;
            ProvideAndRegisterDocumentSetRequest pandRXDSBRequest;
            //m_Logger = Log.For(this);
            try
            {
                // create request
                pandRXDSBRequest = new ProvideAndRegisterDocumentSetRequest(package);
                // get an endpoint from the url string
                endpointAddress = new EndpointAddress(endpointUrl);
                // if this is https then get the client cert from the thumbprint
                clientCert = null;
                if ((endpointUrl.StartsWith("https")) && (!string.IsNullOrEmpty(certThumbprint)))
                {
                    clientCert = StaticHelpers.getCertFromThumbprint(certThumbprint);
                }

            }
            catch (Exception ex)
            {
                ProvideAndRegisterResponse pandRResponse = errorResponse(GlobalValues.CONST_ERROR_CODE_XDSRepositoryError, string.Format("error: {0}; stacktrace{1}", ex.Message, ex.StackTrace));
                return pandRResponse;
            }

            return exportXDSB(pandRXDSBRequest, endpointAddress, clientCert);

        }

        public ProvideAndRegisterResponse ProvideAndRegisterDocumentSet(DocumentPackage package, EndpointAddress endpointAddress, string certThumbprint)
        {
            X509Certificate2 clientCert;
            ProvideAndRegisterDocumentSetRequest pandRXDSBRequest;
            //m_Logger = Log.For(this);
            try
            {
                //  create request
                pandRXDSBRequest = new ProvideAndRegisterDocumentSetRequest(package);

                // if this is https then get the client cert from the thumbprint
                clientCert = null;
                if ((endpointAddress.Uri.ToString().StartsWith("https")) && (!string.IsNullOrEmpty(certThumbprint)))
                {
                    clientCert = StaticHelpers.getCertFromThumbprint(certThumbprint);
                }

            }
            catch (Exception ex)
            {
                ProvideAndRegisterResponse pandRResponse = errorResponse(GlobalValues.CONST_ERROR_CODE_XDSRepositoryError, string.Format("error: {0}; stacktrace{1}", ex.Message, ex.StackTrace));
                return pandRResponse;
            }

            return exportXDSB(pandRXDSBRequest, endpointAddress, clientCert);

        }

        public ProvideAndRegisterResponse ProvideAndRegisterDocumentSet(DocumentPackage package, EndpointAddress endpointAddress, X509Certificate2 clientCert)
        {
            ProvideAndRegisterDocumentSetRequest pandRXDSBRequest;
            //m_Logger = Log.For(this);
            try
            {
                // create request
                pandRXDSBRequest = new ProvideAndRegisterDocumentSetRequest(package);
            }
            catch (Exception ex)
            {
                ProvideAndRegisterResponse pandRResponse = errorResponse(GlobalValues.CONST_ERROR_CODE_XDSRepositoryError, string.Format("error: {0}; stacktrace{1}", ex.Message, ex.StackTrace));
                return pandRResponse;
            }

            return exportXDSB(pandRXDSBRequest, endpointAddress, clientCert);
        }

        
        #endregion

        #region private

        private ProvideAndRegisterResponse exportXDSB(ProvideAndRegisterDocumentSetRequest pandRXDSBRequest, System.ServiceModel.EndpointAddress endpointAddress, X509Certificate2 clientCert)
        {
            ProvideAndRegisterResponse pandRResponse = null;
            // setup a default, we blew it, error response
            pandRResponse = errorResponse(GlobalValues.CONST_ERROR_CODE_XDSRepositoryError, "");
            XDSRepositoryClient xdsRepClient = null;
            try
            {
                // four basic steps
                // 1) build the message
                // 2) create the client proxy (using our own binding (instead of depending on a web.config))
                // 3) send the message and get back the response
                // 4) interrogate the repository/xdr recipient response and create a response object

                //Logger.Debug("begin exportXDSB");
               
                // 1) build the message
                // setup the WCF in and output messages
                WCF.Message wcfInput, wcfOutput;
                XmlSerializer ser = new XmlSerializer(typeof(ProvideAndRegisterDocumentSetRequest));
                MemoryStream ms = new MemoryStream();
                ser.Serialize(ms, pandRXDSBRequest);
                ms.Position = 0;
                XmlReader requestReader = XmlReader.Create(ms);
                //string requestString = requestReader.ReadOuterXml();
                ms.Close();
                wcfInput = WCF.Message.CreateMessage(WCF.MessageVersion.Soap12WSAddressing10
                    , StaticHelpers.XDS_PANDR_ACTION    // the action
                    , requestReader);      // the body

                wcfOutput = WCF.Message.CreateMessage(WCF.MessageVersion.Soap12WSAddressing10, "");

                // 2) create the client proxy (using our own binding (instead of depending on a web.config))
                
                WSHttpBinding myBinding = new WSHttpBinding();
                // some basic binding properties, regardless of transport
                myBinding.MaxBufferPoolSize = 524288; 
                myBinding.MaxReceivedMessageSize = 67108864;
                myBinding.MessageEncoding = WSMessageEncoding.Mtom;
                myBinding.ReaderQuotas.MaxArrayLength = 16384;
                myBinding.ReaderQuotas.MaxBytesPerRead = 8192;
                myBinding.ReaderQuotas.MaxDepth = 32;
                myBinding.ReaderQuotas.MaxNameTableCharCount = 46384;
                myBinding.ReaderQuotas.MaxStringContentLength = 20000;
                
                // tls specifics
                if (clientCert != null)
                {
                    myBinding.Security.Mode = SecurityMode.Transport;
                    myBinding.Security.Transport.ClientCredentialType = HttpClientCredentialType.Certificate;
                    ServicePointManager.SecurityProtocol = SecurityProtocolType.Tls;
                }
                else
                {
                    myBinding.Security.Mode = SecurityMode.None;
                    myBinding.Security.Transport.ClientCredentialType = HttpClientCredentialType.Windows;
                }

                xdsRepClient = new XDSRepositoryClient(myBinding, endpointAddress);

                // tls certificate and callback
                if (clientCert != null)
                {
                    xdsRepClient.ClientCredentials.ClientCertificate.Certificate = clientCert;
                    ServicePointManager.ServerCertificateValidationCallback = new RemoteCertificateValidationCallback(StaticHelpers.RemoteCertificateValidation);
                }

                // 3) send the message and get back the response
                // 
                //Logger.Debug("sending message");
                //using (StreamWriter sw = new StreamWriter("tempDubugging"))
                //{
                //    sw.Write(string.Format("message to send: {0}", wcfInput.ToString()));
                //}
                wcfOutput = xdsRepClient.ProvideAndRegisterDocumentSet(wcfInput);

                // 4) interrogate the repository/xdr recipient response and create a response object
                pandRResponse = interrogateWCFResponse(wcfOutput);
            }
            catch (Exception ex)
            {
                // good point for debug logging
                //Logger.Error(string.Format("exportXDSB catches error: {0}; stack: {1}", ex.Message, ex.StackTrace));

                pandRResponse = errorResponse(GlobalValues.CONST_ERROR_CODE_XDSRepositoryError, string.Format("error: {0}; stacktrace{1}", ex.Message, ex.StackTrace));
                //throw;
            }
            if (xdsRepClient.State == CommunicationState.Opened)
            {
                xdsRepClient.Close();
            }
            //Logger.Debug("end exportXDSB");
            return pandRResponse;
        }

        private ProvideAndRegisterResponse errorResponse(string errorCode, string contextText)
        {
            ProvideAndRegisterResponse pandRResponse = new ProvideAndRegisterResponse();
            pandRResponse.Status = GlobalValues.CONST_RESPONSE_STATUS_TYPE_FAILURE;  // unless proven otherwise
            pandRResponse.RegistryErrorList = new RegistryErrorList();
            pandRResponse.RegistryErrorList.HighestSeverity = GlobalValues.CONST_SEVERITY_TYPE_ERROR;
            pandRResponse.RegistryErrorList.RegistryErrors = new List<RegistryError>(1);
            pandRResponse.RegistryErrorList.RegistryErrors.Add(new RegistryError());
            pandRResponse.RegistryErrorList.RegistryErrors[0].Severity = GlobalValues.CONST_SEVERITY_TYPE_ERROR;
            pandRResponse.RegistryErrorList.RegistryErrors[0].ErrorCode = errorCode;
            pandRResponse.RegistryErrorList.RegistryErrors[0].CodeContext = contextText;
            return pandRResponse;
        }

        private ProvideAndRegisterResponse interrogateWCFResponse(Message wcfOutput)
        {
            // 
            //Logger.Debug("begin interrogateWCFResponse");
            ProvideAndRegisterResponse pandRResponse = new ProvideAndRegisterResponse();
            try
            {
                XmlDictionaryReader dictReader = wcfOutput.GetReaderAtBodyContents();
                XmlDocument resultsDOM = new XmlDocument();
                resultsDOM.Load(dictReader);

                // handle the response status
                pandRResponse.Status = resultsDOM.DocumentElement.Attributes.GetNamedItem("status").InnerXml;

                // add any registry errors
                XmlNode registryErrorList = resultsDOM.DocumentElement.SelectSingleNode("//*[local-name()='RegistryErrorList']");
                if (registryErrorList == null)
                {
                    pandRResponse.RegistryErrorList = null;
                }
                else  // process all of the registry errors
                {
                    XmlNodeList registryErrors = registryErrorList.SelectNodes("//*[local-name()='RegistryError']");
                    pandRResponse.RegistryErrorList = new RegistryErrorList();
                    pandRResponse.RegistryErrorList.RegistryErrors = new List<RegistryError>();
                    RegistryError theRegistryError = null;
                    XmlNode temp;

                    temp = registryErrorList.Attributes.GetNamedItem("highestSeverity");
                    if (temp != null)
                    {
                        pandRResponse.RegistryErrorList.HighestSeverity = temp.InnerXml;
                    }
                    else  // shouldn't happen....highest should be present
                    {
                        pandRResponse.RegistryErrorList.HighestSeverity = "";
                    } // fi highestSeverity attribute exists

                    foreach (XmlNode aRegError in registryErrors)
                    {
                        theRegistryError = new RegistryError();
                        temp = aRegError.Attributes.GetNamedItem("errorCode");
                        if (temp != null)
                        {
                            theRegistryError.ErrorCode = temp.InnerXml;
                        }
                        else  // should not happen....error code should be present
                        {
                            theRegistryError.ErrorCode = "";
                        } // fi

                        temp = aRegError.Attributes.GetNamedItem("codeContext");
                        if (temp != null)
                        {
                            theRegistryError.CodeContext = temp.InnerXml;
                        }
                        else
                        {
                            theRegistryError.CodeContext = "";  
                        } // fi

                        temp = aRegError.Attributes.GetNamedItem("location");
                        if (temp != null)
                        {
                            theRegistryError.Location = temp.InnerXml;
                        }
                        else
                        {
                            theRegistryError.Location = "";
                        } // fi

                        temp = aRegError.Attributes.GetNamedItem("severity");
                        if (temp != null)
                        {
                            theRegistryError.Severity = temp.InnerXml;
                        }
                        else
                        {
                            theRegistryError.Severity = "";
                        } // fi
                        
                        // highest severity
                        if (string.IsNullOrEmpty(pandRResponse.RegistryErrorList.HighestSeverity))
                        {
                            pandRResponse.RegistryErrorList.HighestSeverity = theRegistryError.Severity;
                        }
                        else
                        {
                            //
                            if (pandRResponse.RegistryErrorList.HighestSeverity != GlobalValues.CONST_SEVERITY_TYPE_ERROR)
                            {
                                pandRResponse.RegistryErrorList.HighestSeverity = theRegistryError.Severity;
                            }
                        }

                        // add to the registry error list
                        pandRResponse.RegistryErrorList.RegistryErrors.Add(theRegistryError);
                    }

                }  // fi some registry errors
            }
            catch (Exception ex)
            {
                //Logger.Error(string.Format("interrogateWCFResponse catches error: {0}; stack: {1}", ex.Message, ex.StackTrace));
                pandRResponse = errorResponse(GlobalValues.CONST_ERROR_CODE_XDSRepositoryError, string.Format("error: {0}; stacktrace{1}", ex.Message, ex.StackTrace));
                //throw;
            }
            //Logger.Debug("end interrogateWCFResponse");
            return pandRResponse;
        }

        #endregion
    }
}
