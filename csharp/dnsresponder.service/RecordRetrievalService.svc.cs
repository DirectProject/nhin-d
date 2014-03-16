/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico      chris.lomonico@surescripts.com
    Umesh Madan         umeshma@microsoft.com
    Ali Emami           aliemami@microsoft.com  

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.Text;
using Health.Direct.Common.Diagnostics;
using Health.Direct.Config.Store;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.DnsResolver;

namespace Health.Direct.DnsResponder.Service
{
    public class RecordRetrievalService : IRecordRetrievalService
    {
        private readonly ConfigStore m_store;
        private readonly ILogger m_logger;

        public RecordRetrievalService()
        {
            try
            {
                m_store = Service.Current.Store;
                m_logger = Log.For(this);
            }
            catch (Exception ex)
            {
                WriteToEventLog(ex);
                throw;
            }
        }

        private static void WriteToEventLog(Exception ex)
        {
            const string source = "Health.Direct.Config.Service";

            EventLogHelper.WriteError(source, ex.Message);
            EventLogHelper.WriteError(source, ex.GetBaseException().ToString());
        }

        private ILogger Logger
        {
            get { return m_logger; }
        }

        protected ConfigStore Store
        {
            get { return m_store; }
        }

        protected FaultException<ConfigStoreFault> CreateFault(string methodName, Exception ex)
        {
            Logger.Error("While performing {0}(); {1}", methodName, ex);

            ConfigStoreFault fault = ConfigStoreFault.ToFault(ex);
            return new FaultException<ConfigStoreFault>(fault, new FaultReason(fault.ToString()));
        }

        public DnsRecord[] GetMatchingDnsRecords(string domainName, Health.Direct.Common.DnsResolver.DnsStandard.RecordType typeID)
        {
            DnsRecord[] records = Store.DnsRecords.Get(domainName, typeID);

            if (!records.IsNullOrEmpty())
            {
                return records; 
            }            

            // For NS and SOA records, check if we own the question domain. 
            if (typeID == DnsStandard.RecordType.SOA ||
                typeID == DnsStandard.RecordType.NS)
            {   
                string owningDomain = QuestionDomainToOwnedDomain(domainName);

                if (owningDomain == null)
                {
                    return null; 
                }

                records = Store.DnsRecords.Get(owningDomain, typeID);

                // apply the question's domain before returning the records.
                foreach (DnsRecord record in records)
                {
                    DnsResourceRecord newRecord = record.Deserialize();
                    newRecord.Name = domainName;

                    record.DomainName = domainName;
                    record.RecordData = newRecord.Serialize();
                }
            }   

            return records;
        }

        public Health.Direct.Config.Store.Certificate[] GetCertificatesForOwner(string domain)
        {
            if (string.IsNullOrEmpty(domain))
            {
                return null;
            }
            
            try
            {
                if (this.IsExactDomain(domain))
                {
                    return FetchCertificates(domain);
                }
                
                Certificate[] certs = null;
                //
                // Convert domain to email
                //
                string emailOwner = this.DomainToEmail(domain);
                if (!string.IsNullOrEmpty(emailOwner))
                {
                    certs = FetchCertificates(emailOwner);
                }
                if (certs.IsNullOrEmpty())
                {
                    //
                    // Could not find certs by email. Try the raw string as is
                    //
                    certs = FetchCertificates(domain);
                }
                
                return certs;
            }
            catch (Exception ex)
            {
                throw CreateFault("GetCertificatesForOwner", ex);
            }
        }
        
        Certificate[] FetchCertificates(string owner)
        {
            Certificate[] certs = Store.Certificates.Get(owner, EntityStatus.Enabled);
            if (certs != null)
            {
                foreach (Certificate cert in certs)
                {
                    cert.ExcludePrivateKey();
                }
            }
            
            return certs;
        }
                
        bool IsExactDomain(string domain)
        {
            return Service.Current.Domains.Contains(domain, StringComparer.OrdinalIgnoreCase);
        }
        
        string DomainToEmail(string domain)
        {
            //
            // Dotted domains are sorted by length
            // The first match is the longest match & the right one
            // We've prepended all domain names with '.'
            //
            string[] dottedDomains = Service.Current.DottedDomains;
            for (int i = 0; i < dottedDomains.Length; ++i)
            {
                int pos = domain.IndexOf(dottedDomains[i]);
                if (pos > 0) // only if there is at least one char in the mail address
                {
                    // There needs to be an API to do this!
                    return domain.Substring(0, pos) + '@' + Service.Current.Domains[i];
                }
            }            
            //
            // Can't be one of ours. Do not respond
            //   
            return null;
        }

        string QuestionDomainToOwnedDomain(string questionDomain)
        {
            string[] ownedDomains = Service.Current.Domains;
            for (int i = 0; i < ownedDomains.Length; i++)
            {
                if (questionDomain.EndsWith(ownedDomains[i], StringComparison.OrdinalIgnoreCase))
                {
                    return ownedDomains[i]; 
                }
            }

            return null;
        }
    }
}
