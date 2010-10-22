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
using System.Text;
using System.Security.Cryptography.X509Certificates;
using System.Xml;
using System.Net.Security;

namespace Health.Direct.XDS
{
    class StaticHelpers
    {
        public const string XDS_PANDR_DEFAULT_DOCUMENTID = "theDocument";
        public const string XDS_PANDR_ACTION = "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-b";


        public static X509Certificate2 getCertFromThumbprint(String thumbprint)
        {
            X509Store store = new X509Store(StoreName.My, StoreLocation.LocalMachine);
            store.Open(OpenFlags.ReadOnly);
            X509Certificate2Collection coll = store.Certificates.Find(X509FindType.FindByThumbprint, thumbprint, true);
            if (coll.Count > 0)
            {
                return coll[0];
            }
            return null;
        }

        public static ProvideAndRegisterRequest CreatePandRRequest(XmlDocument metadata, XmlDocument documentToExport)
        {
            return createARequest(metadata, documentToExport.OuterXml);
        }

        public static ProvideAndRegisterRequest CreatePandRRequest(string metadata, string documentToExport)
        {
            try
            {
                XmlDocument metadataXmlDoc = new XmlDocument();
                metadataXmlDoc.LoadXml(metadata);
                return createARequest(metadataXmlDoc, documentToExport);
            }
            catch (Exception ex)
            {
                // todo - logging
                throw ex;
            }
        }


        /// <summary>
        /// This method is a callback method used to validate the client certificate(https)
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="certificate"></param>
        /// <param name="chain"></param>
        /// <param name="sslPolicyErrors"></param>
        /// <returns></returns>
        public static bool RemoteCertificateValidation(object sender, System.Security.Cryptography.X509Certificates.X509Certificate certificate, System.Security.Cryptography.X509Certificates.X509Chain chain, System.Net.Security.SslPolicyErrors sslPolicyErrors)
        {
            X509Certificate2 cert2;

            try
            {
                cert2 = new X509Certificate2(certificate);
                // todo:  we should logAtnaEvent(AuditType.CertificateNormal, ((HttpWebRequest)sender).RequestUri.ToString(), ctx);
                // todo: debug logging
            }
            catch (Exception ex)
            {
                // todo: debug logging
                // todo we should logAtnaEvent(AuditType.CertificateUnregistered, ((HttpWebRequest)sender).RequestUri.ToString(), ctx);
                // todo: debug logging Logger.Error("IHE", "cert problem 408");
                return false;
            }
            if (cert2.NotAfter < DateTime.Now)
            {
                // todo: debug logging Logger.Error("IHE", "Expiration Date Issue: " + cert2.NotAfter.ToString());
                // todo we should logAtnaEvent(AuditType.CertificateExpired, ((HttpWebRequest)sender).RequestUri.ToString(), ctx);
                return false;
            }

            // todo:  how to handle whether or not to ignore CN mismatch:  typically this is an option
            String cnMismatchOption = "true";
            try
            {
                // todo - get the option setting cnMismatchOption = ...
            }
            catch (Exception e)
            {
                //Logger.Warn("IHEHTTPUtility", "No cnMismatchOption set for Default, Default");
            }
            // if no errors at all
            if (sslPolicyErrors == SslPolicyErrors.None)
                return true;
            // if just a name mismatch and we're allowing them
            if ((sslPolicyErrors == SslPolicyErrors.RemoteCertificateNameMismatch) && (cnMismatchOption.ToLower() == "true"))
                return true;
            // if there is no available certificate
            if ((sslPolicyErrors & SslPolicyErrors.RemoteCertificateNotAvailable) == SslPolicyErrors.RemoteCertificateNotAvailable)
            {
                // todo: debug logging Logger.Error("IHE", "Remote certificate unavailable");
                // todo we should logAtnaEvent(AuditType.CertificateUnregistered, ((HttpWebRequest)sender).RequestUri.ToString(), ctx);
                return false;
            }
            if ((sslPolicyErrors & SslPolicyErrors.RemoteCertificateChainErrors) == SslPolicyErrors.RemoteCertificateChainErrors)
            {
                // todo: debug logging Logger.Warn("IHE", "Allowed Error: " + sslPolicyErrors.ToString());
                // todo we should logAtnaEvent(AuditType.CertificateUnregistered, ((HttpWebRequest)sender).RequestUri.ToString(), ctx);
                return true;
            }
            return false;
        }

        #region private
        private static ProvideAndRegisterRequest createARequest(XmlDocument metadata, string documentToExport)
        {
            try
            {
                ProvideAndRegisterRequest pandrXDSBRequest = new ProvideAndRegisterRequest();
                pandrXDSBRequest.submissionMetadata = metadata.DocumentElement;
                SubmissionDocument theSubmissionDocument = new SubmissionDocument();
                theSubmissionDocument.documentID = XDS_PANDR_DEFAULT_DOCUMENTID;
                theSubmissionDocument.documentText = ASCIIEncoding.UTF8.GetBytes(documentToExport);
                pandrXDSBRequest.submissionDocuments.Add(theSubmissionDocument);
                return pandrXDSBRequest;

            }
            catch (Exception ex)
            {
                // todo - logging
                throw ex;
            }
        }
        #endregion

    }
}
