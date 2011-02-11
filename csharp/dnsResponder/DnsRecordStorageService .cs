/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico (chris.lomonico@surescripts.com)
    Umesh Madan     umeshma@microsoft.com
    
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
using Health.Direct.Config.Store;
using Health.Direct.Config.Client;
using Health.Direct.Config.Client.RecordRetrieval;

namespace Health.Direct.DnsResponder
{
    public class DnsRecordStorageService : IDnsStore
    {
        protected ClientSettings m_recordRetrievalServiceSettings = null;

        /// <summary>
        /// complex ctor expecting service settings
        /// </summary>
        /// <param name="domainManagerServiceSettings">ClientSettings for RecordRetrieval Service related data</param>
        public DnsRecordStorageService(ClientSettings recordRetrievalServiceSettings)
        {
            if (recordRetrievalServiceSettings == null)
            {
                throw new ArgumentNullException("recordRetrievalServiceSettings not supplied to constructor");
            }
            m_recordRetrievalServiceSettings = recordRetrievalServiceSettings;
        }

        /// <summary>
        /// Endpoint/Binding specifications in ClientSettings format for the Record Retrieval Service
        /// </summary>
        public ClientSettings RecordRetrievalServiceSettings
        {
            get
            {
                return m_recordRetrievalServiceSettings;
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
                throw new DnsServerException(DnsStandard.ResponseCode.NotImplemented);
            }
            
            DnsResponse response = this.ProcessRequest(request); 
            //
            // Usually need some post-processing on the response
            //   
            return this.ProcessResponse(response);    
        }

        #endregion
        
        DnsResponse ProcessRequest(DnsRequest request)
        {
            DnsStandard.RecordType questionType = request.Question.Type;
            DnsResponse response = new DnsResponse(request);
            response.Header.IsAuthoritativeAnswer = true;
            
            switch (response.Question.Type)
            {
                default:
                    throw new DnsServerException(DnsStandard.ResponseCode.NotImplemented);

                case DnsStandard.RecordType.ANAME:
                    ProcessANAMEQuestion(response);
                    break;
                case DnsStandard.RecordType.NS:
                    ProcessNSQuestion(response);
                    break;
                case DnsStandard.RecordType.MX:
                    ProcessMXQuestion(response);
                    break;
                case DnsStandard.RecordType.SOA:
                    ProcessSOAQuestion(response);
                    break;
                case DnsStandard.RecordType.CERT:
                    ProcessCERTQuestion(response);
                    break;
                case DnsStandard.RecordType.CNAME:
                    ProcessCNAMEQuestion(response);
                    break;                    
            }
            
            return response;
        }
        
        DnsResponse ProcessResponse(DnsResponse response)
        {
            if (!response.HasAnyRecords)
            {
                response = null; // This will cause the server to return a NameError
            }
            
            return response;
        }
        
        /// <summary>
        /// processes a ANAME Question, populated the response with any matching results pulled from the database store
        /// </summary>
        /// <param name="response">DnsResponse instance containing information about the question that will
        /// have any corresponding answer records populated upon return</param>
        protected void ProcessANAMEQuestion(DnsResponse response)
        {
            using (RecordRetrievalServiceClient client = m_recordRetrievalServiceSettings.CreateRecordRetrievalClient())
            {
                client.GetANAMERecords(response.Question.Domain, response.AnswerRecords);
                if (!response.HasAnswerRecords)
                {
                    client.GetCNAMERecords(response.Question.Domain, response.AnswerRecords);
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
            using (RecordRetrievalServiceClient client = m_recordRetrievalServiceSettings.CreateRecordRetrievalClient())
            {
                client.GetSOARecords(response.Question.Domain, response.AnswerRecords);
            }
        }

        /// <summary>
        /// processes a MX Question, populated the response with any matching results pulled from the database store
        /// </summary>
        /// <param name="response">DnsResponse instance containing information about the question that will
        /// have any corresponding answer records populated upon return</param>
        protected void ProcessMXQuestion(DnsResponse response)
        {
            using (RecordRetrievalServiceClient client = m_recordRetrievalServiceSettings.CreateRecordRetrievalClient())
            {
                client.GetMXRecords(response.Question.Domain, response.AnswerRecords);
                if (!response.HasAnswerRecords)
                {
                    return;
                }
                //
                // additionally return each MX record's IP address
                //
                foreach (MXRecord mxRecord in response.AnswerRecords.MX)
                {
                    client.GetANAMERecords(mxRecord.Exchange, response.AdditionalRecords);
                }
            }
        }

        /// <summary>
        /// processes a CERT Question, populated the response with any matching results pulled from the database store
        /// </summary>
        /// <param name="response">DnsResponse instance containing information about the question that will
        /// have any corresponding answer records populated upon return</param>
        void ProcessCERTQuestion(DnsResponse response)
        {
            using (RecordRetrievalServiceClient client = m_recordRetrievalServiceSettings.CreateRecordRetrievalClient())
            {
                Certificate[] certs = client.GetCertificatesForOwner(response.Question.Domain);
                foreach (Certificate cert in certs)
                {
                    response.AnswerRecords.Add(new CertRecord(new DnsX509Cert(cert.Data)));
                }
            }
        }
        
        void ProcessNSQuestion(DnsResponse response)
        {
            using (RecordRetrievalServiceClient client = m_recordRetrievalServiceSettings.CreateRecordRetrievalClient())
            {
                client.GetNSRecords(response.Question.Domain, response.AnswerRecords);
                if (!response.HasAnswerRecords)
                {
                    return;
                }
                //
                // Also resolve the NS Record's actual address, to save roundtrips
                //
                foreach(NSRecord record in response.AnswerRecords.NS)
                {
                    client.GetANAMERecords(record.NameServer, response.AdditionalRecords);
                }
            }
        }

        /// <summary>
        /// processes a CNAME Question, populated the response with any matching results pulled from the database store
        /// </summary>
        /// <param name="response">DnsResponse instance containing information about the question that will
        /// have any corresponding answer records populated upon return</param>
        protected void ProcessCNAMEQuestion(DnsResponse response)
        {
            using (RecordRetrievalServiceClient client = m_recordRetrievalServiceSettings.CreateRecordRetrievalClient())
            {
                client.GetCNAMERecords(response.Question.Domain, response.AnswerRecords);
            }
        }
    }
}
