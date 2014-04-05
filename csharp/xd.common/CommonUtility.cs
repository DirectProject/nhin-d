/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Vassil Peytchev     vassil@epic.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System.Xml;

namespace Health.Direct.Xd.Common
{
    public static class CommonUtility
    {
        public static XmlDocument ConstructRegistryErrorResponse(string status, string requestId, string codeContext, string errorCode, string severity, string location)
        {
            XmlDocument xmlDocRegistryResponse = null;
            RegistryErrorList objRegistryErrorList = new RegistryErrorList();
            RegistryError objRegistryError = new RegistryError();

            objRegistryError.CodeContext = codeContext;
            objRegistryError.ErrorCode = errorCode;
            objRegistryError.Location = location;
            objRegistryError.Severity = severity;

            objRegistryErrorList.RegistryErrors.Add(objRegistryError);

            xmlDocRegistryResponse = ConstructRegistryErrorResponse(status, requestId, objRegistryErrorList);

            return xmlDocRegistryResponse;
        }


        public static XmlDocument ConstructRegistryErrorResponse(string status, string requestId, RegistryErrorList objRegistryErrorList)
        {
            XmlElement eltRegistryErrorList = null;
            XmlElement eltRegistryError = null;
            XmlElement eltRegistryResponse = null;
            XmlAttribute attrib = null;
            XmlDocument xmlDocRegistryResponse = new XmlDocument();

            eltRegistryResponse = xmlDocRegistryResponse.CreateElement("tns:RegistryResponse", @GlobalValues.ebXmlRS3Namespace);
            xmlDocRegistryResponse.AppendChild(eltRegistryResponse);

            //tns
            attrib = xmlDocRegistryResponse.CreateAttribute("xmlns:tns");
            attrib.Value = GlobalValues.ebXmlRS3Namespace;
            eltRegistryResponse.Attributes.Append(attrib);

            //rim
            attrib = xmlDocRegistryResponse.CreateAttribute("xmlns:rim");
            attrib.Value = GlobalValues.ebXmlRIMNamespace;
            eltRegistryResponse.Attributes.Append(attrib);

            attrib = xmlDocRegistryResponse.CreateAttribute("status");
            attrib.Value = status;
            eltRegistryResponse.Attributes.Append(attrib);

            if (!string.IsNullOrEmpty(requestId))
            {
                attrib = xmlDocRegistryResponse.CreateAttribute("requestId");
                attrib.Value = requestId;
                eltRegistryResponse.Attributes.Append(attrib);
            }

            //RegistryErrorList
            eltRegistryErrorList = xmlDocRegistryResponse.CreateElement("tns:RegistryErrorList", @GlobalValues.ebXmlRS3Namespace);

            //Append RegistryErrorList
            eltRegistryResponse.AppendChild(eltRegistryErrorList);

            //highestSeverity
            attrib = xmlDocRegistryResponse.CreateAttribute("highestSeverity");
            attrib.Value = objRegistryErrorList.HighestSeverity;
            eltRegistryErrorList.Attributes.Append(attrib);

            for (int count = 0; count < objRegistryErrorList.RegistryErrors.Count; count++)
            {

                //RegistryError
                eltRegistryError = xmlDocRegistryResponse.CreateElement("tns:RegistryError", @GlobalValues.ebXmlRS3Namespace);

                //codeContext
                attrib = xmlDocRegistryResponse.CreateAttribute("codeContext");
                attrib.Value = objRegistryErrorList.RegistryErrors[count].CodeContext;
                eltRegistryError.Attributes.Append(attrib);

                //errorCode
                attrib = xmlDocRegistryResponse.CreateAttribute("errorCode");
                attrib.Value = objRegistryErrorList.RegistryErrors[count].ErrorCode;
                eltRegistryError.Attributes.Append(attrib);

                //severity
                attrib = xmlDocRegistryResponse.CreateAttribute("severity");
                attrib.Value = objRegistryErrorList.RegistryErrors[count].Severity;
                eltRegistryError.Attributes.Append(attrib);

                if (!string.IsNullOrEmpty(objRegistryErrorList.RegistryErrors[count].Location))
                {
                    //location
                    attrib = xmlDocRegistryResponse.CreateAttribute("location");
                    attrib.Value = objRegistryErrorList.RegistryErrors[count].Location;
                    eltRegistryError.Attributes.Append(attrib);
                }

                //Append RegistryError
                eltRegistryErrorList.AppendChild(eltRegistryError);
            }

            return xmlDocRegistryResponse;
        }

        public static XmlDocument ConstructRegistrySuccessResponse()
        {
            XmlDocument objRegistryDocument = null;

            objRegistryDocument = new XmlDocument();
            //XmlElement rootElement = objRegistryDocument.CreateElement("RegistryResponse");
            XmlElement rootElement = objRegistryDocument.CreateElement("RegistryResponse");

            ////xmlns:tns
            //rootElement.Attributes.Append(objRegistryDocument.CreateAttribute("xmlns:tns"));
            //rootElement.Attributes["xmlns:tns"].Value = "urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0";

            //xmlns
            rootElement.Attributes.Append(objRegistryDocument.CreateAttribute("xmlns"));
            rootElement.Attributes["xmlns"].Value = GlobalValues.ebXmlRS3Namespace;


            //xmlns:rim
            //rootElement.Attributes.Append(objRegistryDocument.CreateAttribute("xmlns:rim"));
            //rootElement.Attributes["xmlns:rim"].Value = GlobalValues.CONST_XML_NAMESPACE_x;


            //status
            rootElement.Attributes.Append(objRegistryDocument.CreateAttribute("status"));
            rootElement.Attributes["status"].Value = GlobalValues.CONST_RESPONSE_STATUS_TYPE_SUCCESS;

            //Append Child
            objRegistryDocument.AppendChild(rootElement);

            return objRegistryDocument;
        }

    }
}