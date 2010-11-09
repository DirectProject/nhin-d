/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico (chris.lomonico@surescripts.com)
    
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

using Health.Direct.Common.DnsResolver;
using Health.Direct.Config.Client;


namespace Health.Direct.DnsResponder
{
    public class DnsRecordStorageService : IDnsStore
    {

        protected ClientSettings m_dnsRecordManagerServiceSettings = null;
        protected ClientSettings m_certificateManagerServiceSettings = null;


        /// <summary>
        /// If this gateway is configured to interact with a DomainManager web Service. 
        /// </summary>
        public ClientSettings DomainManagerServiceSettings
        {
            get
            {
                return m_dnsRecordManagerServiceSettings;
            }
        }

        /// <summary>
        /// If this gateway is configured to interact with a DomainManager web Service. 
        /// </summary>
        public ClientSettings CertificateManagerServiceSettings
        {
            get
            {
                return m_certificateManagerServiceSettings;
            }
        }

        #region IDnsStore Members

        public DnsResponse Get(DnsRequest request)
        {
            if (request == null)
            {
                throw new ArgumentNullException();
            }

            DnsQuestion question = request.Question;
            if (question == null || question.Class != DnsStandard.Class.IN)
            {
                return null;
            }
            DnsStandard.RecordType questionType = request.Question.Type;
            DnsResponse response = new DnsResponse(request);

            switch (questionType)
            {
                case DnsStandard.RecordType.ANAME:
                    ProcessANAMEQuestion(response);
                    break;
                case DnsStandard.RecordType.MX:
                    ProcessMXQuestion(response);
                    break;
                case DnsStandard.RecordType.SOA:
                    ProcessSOAQuestion(response);
                    break;
                case DnsStandard.RecordType.CERT:
                    throw new NotImplementedException();
                default:
                    return response;
            }
            return response;
                                    
        }

        #endregion

        /// <summary>
        /// complex ctor expecting service settings
        /// </summary>
        /// <param name="domainManagerServiceSettings">ClientSettings for DnsRecordManager Service related data</param>
        /// <param name="certificateManagerServiceSettings">ClientSettings for Cert  Service related data></param>
        public DnsRecordStorageService(ClientSettings dnsRecordManagerServiceSettings
            , ClientSettings certificateManagerServiceSettings)
        {
            if (dnsRecordManagerServiceSettings == null || certificateManagerServiceSettings == null)
            {
                throw new ArgumentNullException("domainManagerServiceSettings and/or certificateManagerServiceSettings not supplied to constructor");
            }
            m_dnsRecordManagerServiceSettings = dnsRecordManagerServiceSettings;
            m_certificateManagerServiceSettings = certificateManagerServiceSettings;

        }

        /// <summary>
        /// processes a ANAME Question, populated the response with any matching results pulled from the database store
        /// </summary>
        /// <param name="response">DnsResponse instance containing information about the question that will
        /// have any corresponding answer records populated upon return</param>
        protected void ProcessANAMEQuestion(DnsResponse response)
        {
            using (DomainManagerService.DnsRecordManagerClient dnsClient = new Health.Direct.DnsResponder.DomainManagerService.DnsRecordManagerClient(
                this.DomainManagerServiceSettings.Binding
                , this.DomainManagerServiceSettings.Endpoint))
            {

                DomainManagerService.DnsRecord[] records = dnsClient.GetMatchingDnsRecordsByType(response.Question.Domain
                    , DnsStandard.RecordType.ANAME);
                foreach (DomainManagerService.DnsRecord record in records)
                {
                    DnsBufferReader rdr = new DnsBufferReader(record.RecordData
                        , 0
                        , record.RecordData.Length);
                    AddressRecord addressrec = AddressRecord.Deserialize(ref rdr) as AddressRecord;
                    if (addressrec != null)
                    {
                        response.AnswerRecords.Add(addressrec);
                    }
                }
            }
        }

        /// <summary>
        /// processes a SOA Question, populated the response with any matching results pulled from the database store
        /// </summary>
        /// <param name="response">DnsResponse instance containing information about the question that will
        /// have any corresponding answer records populated upon return</param>
        protected void ProcessSOAQuestion(DnsResponse response)
        {
            using (DomainManagerService.DnsRecordManagerClient dnsClient = new Health.Direct.DnsResponder.DomainManagerService.DnsRecordManagerClient(
                this.DomainManagerServiceSettings.Binding
                , this.DomainManagerServiceSettings.Endpoint))
            {

                DomainManagerService.DnsRecord[] records = dnsClient.GetMatchingDnsRecordsByType(response.Question.Domain
                    , DnsStandard.RecordType.SOA);
                foreach (DomainManagerService.DnsRecord record in records)
                {
                    DnsBufferReader rdr = new DnsBufferReader(record.RecordData
                        , 0
                        , record.RecordData.Length);
                    SOARecord soarec = SOARecord.Deserialize(ref rdr) as SOARecord;
                    if (soarec != null)
                    {
                        response.AnswerRecords.Add(soarec);
                    }
                }
            }
        }

        /// <summary>
        /// processes a MX Question, populated the response with any matching results pulled from the database store
        /// </summary>
        /// <param name="response">DnsResponse instance containing information about the question that will
        /// have any corresponding answer records populated upon return</param>
        protected void ProcessMXQuestion(DnsResponse response)
        {
            using (DomainManagerService.DnsRecordManagerClient dnsClient = new Health.Direct.DnsResponder.DomainManagerService.DnsRecordManagerClient(
                this.DomainManagerServiceSettings.Binding
                , this.DomainManagerServiceSettings.Endpoint))
            {

                DomainManagerService.DnsRecord[] records = dnsClient.GetMatchingDnsRecordsByType(response.Question.Domain
                    , DnsStandard.RecordType.MX);
                foreach (DomainManagerService.DnsRecord record in records)
                {
                    DnsBufferReader rdr = new DnsBufferReader(record.RecordData
                        , 0
                        , record.RecordData.Length);
                    MXRecord mxrec = MXRecord.Deserialize(ref rdr) as MXRecord;
                    if (mxrec != null)
                    {
                        response.AnswerRecords.Add(mxrec);
                    }
                }
            }
        }
        
        /// <summary>
        /// processes a CERT Question, populated the response with any matching results pulled from the database store
        /// </summary>
        /// <param name="response">DnsResponse instance containing information about the question that will
        /// have any corresponding answer records populated upon return</param>
        protected void ProcessCERTQuestion(DnsResponse response)
        {
            using (CertificateService.CertificateStoreClient certClient= new Health.Direct.DnsResponder.CertificateService.CertificateStoreClient(
                this.CertificateManagerServiceSettings.Binding
                , this.CertificateManagerServiceSettings.Endpoint))
            {
                Health.Direct.Config.Client.CertificateService.CertificateGetOptions options = new Health.Direct.Config.Client.CertificateService.CertificateGetOptions();
                options.IncludeData = true;
                options.IncludePrivateKey = false;

                CertificateService.Certificate[]  certs = certClient.GetCertificatesForOwner(response.Question.Domain
                    , options);
                foreach (CertificateService.Certificate cert in certs)
                {
                    response.AnswerRecords.Add(new CertRecord(new DnsX509Cert(cert.Data)));
                }
            }
        }
    }

}
