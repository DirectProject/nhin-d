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
using Health.Direct.Common.Metadata;
using Health.Direct.Xds.Common;
using Health.Direct.Xds.Common.XdsMetadata;

namespace Health.Direct.Xds
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

        public static ExtrinsicObjectType CreateDocumentEntry(DocumentMetadata document, string documentId)
        {
            ExtrinsicObjectType eo = new ExtrinsicObjectType();
            eo.Id = documentId;
            eo.ObjectType = GlobalValues.XDSDocumentEntryUUID; //ClassificationNode?
            eo.MimeType = document.MediaType;

            eo.Name = new InternationalStringType();
            eo.Name.LocalizedString = new LocalizedStringType[1];
            eo.Name.LocalizedString[0] = new LocalizedStringType();
            eo.Name.LocalizedString[0].Value = document.Title;

            SlotType[] slots = new SlotType[6];

            string[] values = new string[1];
            values[0] = Extensions.ToHL7Date(document.CreatedOn);
         
            slots[0] = new SlotType(SlotNameType.creationTime, values);

            values = new string[1];
            values[0] = document.LanguageCode;
            slots[1] = new SlotType(SlotNameType.languageCode, values);

            values = new string[1];
            values[0] = document.SourcePtId.ToCx();
            slots[2] = new SlotType(SlotNameType.sourcePatientId, values);

            //values = new string[5];
            //values[0] = "PID-3|" + _patID
            //values[1] = "PID-5|" + _patName;
            //values[2] = "PID-7|" + _patDOB;
            //values[3] = "PID-8|" + _patSex;
            //values[4] = "PID-11|" + _patAddress;
            values = (string[])document.Patient.ToSourcePatientInfoValues(document.SourcePtId);
            slots[3] = new SlotType(SlotNameType.sourcePatientInfo, values);

            values = new string[1];
            values[0] = document.Size.ToString();
            slots[4] = new SlotType(SlotNameType.size, values);

            values = new string[1];
            values[0] = document.Hash.ToString();
            slots[5] = new SlotType(SlotNameType.hash, values);

            eo.Slot = slots;

            string[] eiScheme = new string[2];
            string[] eiValue = new string[2];
            string[] eiName = new string[2];
            string[] eiId = new string[2];
            string[] registryObject = new string[2];
            eiScheme[0] = GlobalValues.XDSDocumentEntry_patientIdUUID;
            eiScheme[1] = GlobalValues.XDSDocumentEntry_uniqueIdUUID;
            eiValue[0] = document.PatientID.ToCx();
            eiValue[1] = document.UniqueId;
            eiName[0] = "XDSDocumentEntry.patientId";
            eiName[1] = "XDSDocumentEntry.uniqueId";
            eiId[0] = "ei01";
            eiId[1] = "ei02";
            registryObject[0] = eo.Id;
            registryObject[1] = eo.Id;

            eo.ExternalIdentifier = CreateEIDs(eiScheme, eiValue, eiName, eiId, registryObject);

            //Create Classifications for authorInstitution/authorPerson, class code, and type code
            eo.Classification = new ClassificationType[7];

            //Author Institution and Author Person
            //<rim:Classification id="cl01" classificationScheme="urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d" classifiedObject="Document01">
            //    <rim:Slot name="authorPerson">
            //        <rim:ValueList>
            //            <rim:Value>Gerald Smitty</rim:Value>
            //        </rim:ValueList>
            //    </rim:Slot>
            //    <rim:Slot name="authorInstitution">
            //        <rim:ValueList>
            //            <rim:Value>Cleveland Clinic</rim:Value>
            //            <rim:Value>Parma Community</rim:Value>
            //        </rim:ValueList>
            //    </rim:Slot>
            //</rim:Classification>
            slots = new SlotType[3];
            values = new string[document.Author.Institutions.Count];
            for (int i = 0; i < document.Author.Institutions.Count; i++)
            {
                values[i] = document.Author.Institutions[i].ToXON();
            }
            slots[0] = new SlotType(SlotNameType.authorInstitution, values);

            values = new string[1];
            values[0] = document.Author.Person.ToXCN();
            slots[1] = new SlotType(SlotNameType.authorPerson, values);

            values = new string[1];
            values[0] = document.Author.TelecomAddress.ToXTN();
            slots[2] = new SlotType(SlotNameType.authorTelecom, values);

            eo.Classification[0] = new ClassificationType("urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d",
                eo.Id, null, null,
                "cla01", null, slots);

            //Class Code
            slots = new SlotType[1];
            values = new string[1];
            values[0] = document.Class.Scheme;
            slots[0] = new SlotType(SlotNameType.codingScheme, values);
            eo.Classification[1] = new ClassificationType(GlobalValues.XDSDocumentEntry_classCodeUUID,
                eo.Id, null, document.Class.Code,
                "cl01", document.Class.Label, slots);

            //Confidentiality Code
            slots = new SlotType[1];
            values = new string[1];
            values[0] = document.Confidentiality.Scheme;
            slots[0] = new SlotType(SlotNameType.codingScheme, values);
            eo.Classification[2] = new ClassificationType(GlobalValues.XDSDocumentEntry_confidentialityCodeUUID,
                eo.Id, null, document.Confidentiality.Code, "cl02", document.Confidentiality.Label, slots);

            //Format Code
            slots = new SlotType[1];
            values = new string[1];
            values[0] = document.FormatCode.Scheme;
            slots[0] = new SlotType(SlotNameType.codingScheme, values);
            eo.Classification[3] = new ClassificationType(GlobalValues.XDSDocumentEntry_formatCodeUUID,
                eo.Id, null, document.FormatCode.Code, "cl03",
                document.FormatCode.Label, slots);

            //HealthCare Facility type code
            slots = new SlotType[1];
            values = new string[1];
            values[0] = document.FacilityCode.Scheme;
            slots[0] = new SlotType(SlotNameType.codingScheme, values);
            eo.Classification[4] = new ClassificationType(GlobalValues.XDSDocumentEntry_healthCareFacilityTypeCodeUUID,
                eo.Id, null, document.FacilityCode.Code, "cl04",
                document.FacilityCode.Label, slots);

            //Practice Setting Code
            slots = new SlotType[1];
            values = new string[1];
            values[0] = document.PracticeSetting.Scheme;
            slots[0] = new SlotType(SlotNameType.codingScheme, values);
            eo.Classification[5] = new ClassificationType(GlobalValues.XDSDocumentEntry_practiceSettingCodeUUID,
                eo.Id, null, document.PracticeSetting.Code, "cl05",
                document.PracticeSetting.Label, slots);

            //Type Code
            slots = new SlotType[1];
            values = new string[1];
            values[0] = document.Type.Scheme;
            slots[0] = new SlotType(SlotNameType.codingScheme, values);
            eo.Classification[6] = new ClassificationType("urn:uuid:f0306f51-975f-434e-a61c-c59651d33983",
                eo.Id, null, document.Type.Code, "cl06",
                document.Type.Label, slots);

            return eo;
        }

        /// <summary>
        /// Creates a new array of XDS ExternalIdentifier objects
        /// </summary>
        /// <param name="eiScheme">Array of Schemes for the External Identifier</param>
        /// <param name="eiValue">Array of Values for the External Identifier</param>
        /// <param name="eiName">Array of Names for the External Identifier</param>
        /// <returns>Returns the new array of XDS ExternalIdentifier objects</returns>
        public static ExternalIdentifierType[] CreateEIDs(string[] eiScheme, string[] eiValue, string[] eiName,
            string[] eiId, string[] registryObject)
        {
            ExternalIdentifierType ei = null;
            ExternalIdentifierType[] result = new ExternalIdentifierType[eiScheme.Length];
            for (int i = 0; i < eiScheme.Length; i++)
            {
                ei = new ExternalIdentifierType();
                ei.IdentificationScheme = eiScheme[i];
                ei.Id = eiId[i];
                ei.Value = eiValue[i];
                ei.Name = new InternationalStringType();
                ei.Name.LocalizedString = new LocalizedStringType[1];
                ei.Name.LocalizedString[0] = new LocalizedStringType();
                ei.Name.LocalizedString[0].Value = eiName[i];
                ei.RegistryObject = registryObject[i];

                result[i] = ei;
            }

            return result;
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
