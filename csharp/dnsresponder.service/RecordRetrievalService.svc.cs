/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico      chris.lomonico@surescripts.com
  
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
            Logger.Error(string.Format("While performing {0}()", methodName), ex);

            ConfigStoreFault fault = ConfigStoreFault.ToFault(ex);
            return new FaultException<ConfigStoreFault>(fault, new FaultReason(fault.ToString()));
        }

        #region IRecordRetrievalService Members

        public DnsRecord[] GetMatchingDnsRecords(string domainName, Health.Direct.Common.DnsResolver.DnsStandard.RecordType typeID)
        {
            return Store.DnsRecords.Get(domainName
                , typeID);
        }

        public Health.Direct.Config.Store.Certificate[] GetCertificatesForOwner(string owner, CertificateGetOptions options)
        {
            try
            {
                options = options ?? CertificateGetOptions.Default;
                return this.ApplyGetOptions(Store.Certificates.Get(owner, options.Status), options);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetCertificatesForOwner", ex);
            }
        }

        #endregion


        Certificate[] ApplyGetOptions(Certificate[] certs, CertificateGetOptions options)
        {
            if (certs == null)
            {
                return null;
            }

            return (from cert in
                        (from cert in certs
                         where cert != null
                         select ApplyGetOptions(cert, options)
                        )
                    where cert != null
                    select cert).ToArray();
        }

        Certificate ApplyGetOptions(Certificate cert, CertificateGetOptions options)
        {
            if (options == null)
            {
                options = CertificateGetOptions.Default;
            }

            return options.ApplyTo(cert);
        }

    }
}
