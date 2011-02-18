using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Health.Direct.Config.Client;
using Health.Direct.Common;
using Health.Direct.Common.DnsResolver;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.DnsResponder.Tests
{
    class DnsRecordStoreageServiceTest : TestBase
    {   

        DnsRecordStorageService m_store;

        public DnsRecordStorageService Store
        {
            get
            {
                return m_store;
            }
        }

        public static IEnumerable<object[]> DnsRecordDomainNamesTheoryData
        {
            get
            {
                foreach (String s in DnsRecordDomainNames)
                {
                    yield return new object[] { s };
                }
            }
        }

        public static IEnumerable<object[]> CertRecordOwnersTheoryData
        {
            get
            {
                foreach (String s in CertOwners)
                {
                    yield return new object[] { s };
                }
            }
        }

        public DnsRecordStoreageServiceTest()
        {
            m_store = new DnsRecordStorageService
                (
                new ClientSettings() {
                                         Url = "http://localhost:6693/RecordRetrievalService.svc/Records"
                                     });
            //----------------------------------------------------------------------------------------------------
            //---really only want to do this one time
            this.InitDnsRecords();
        }

        /*
        //The next 3 tests need to be updated to work with the container implementation, for now use the 
        //4 at the bottom to ensure that windows service for the dns responder works
         
        [Theory]
        [PropertyData("DnsRecordDomainNamesTheoryData")]
        public void TestGetDomainANAME(string domainName){

            Dump(string.Format("Attempting to resolve ANAME {0} from dbstore", domainName));
            DnsResponse dr = m_store.Get(
                    new Health.Direct.Common.DnsResolver.DnsRequest(
                            new Health.Direct.Common.DnsResolver.DnsQuestion(domainName
                            , Health.Direct.Common.DnsResolver.DnsStandard.RecordType.ANAME))
                            );
            Dump(string.Format("checking results for ANAME {0}", domainName));
            Assert.Equal(1, dr.AnswerRecords.Count);
            Assert.Equal(domainName, dr.AnswerRecords[0].Name);
            Assert.Equal(Health.Direct.Common.DnsResolver.DnsStandard.RecordType.ANAME
                , dr.AnswerRecords[0].Type);
        }

        
        [Theory]
        [PropertyData("DnsRecordDomainNamesTheoryData")]
        public void TestGetDomainMX(string domainName)
        {
            Dump(string.Format("Attempting to resolve MX {0} from dbstore", domainName));
            DnsResponse dr = m_store.Get(
                    new Health.Direct.Common.DnsResolver.DnsRequest(
                            new Health.Direct.Common.DnsResolver.DnsQuestion(domainName
                            , Health.Direct.Common.DnsResolver.DnsStandard.RecordType.MX))
                            );
            Dump(string.Format("checking results for MX {0}", domainName));
            Assert.Equal(1, dr.AnswerRecords.Count);
            Assert.Equal(domainName, dr.AnswerRecords[0].Name);
            Assert.Equal(Health.Direct.Common.DnsResolver.DnsStandard.RecordType.MX
                , dr.AnswerRecords[0].Type);
        }


        [Theory]
        [PropertyData("DnsRecordDomainNamesTheoryData")]
        public void TestGetDomainSOA(string domainName)
        {
            Dump(string.Format("Attempting to resolve SOA {0} from dbstore", domainName));
            DnsResponse dr = m_store.Get(
                    new Health.Direct.Common.DnsResolver.DnsRequest(
                            new Health.Direct.Common.DnsResolver.DnsQuestion(domainName
                            , Health.Direct.Common.DnsResolver.DnsStandard.RecordType.SOA))
                            );
            Dump(string.Format("checking results for SOA {0}", domainName));
            Assert.Equal(1, dr.AnswerRecords.Count);
            SOARecord soarec = ((SOARecord)dr.AnswerRecords[0]);
            Assert.Equal(domainName, soarec.Name);
            Assert.Equal(Health.Direct.Common.DnsResolver.DnsStandard.RecordType.SOA
                , dr.AnswerRecords[0].Type);
            
        }
        */

        /*
        ///for these have the dns responder windows service up and running
        /// <summary>
        /// Runs a live test against the dnsResponder service to see if service will actually resolve results for MX quesions
        /// </summary>
        /// <remarks>In order to run this you need to have the dnsResponder service running (which can run in debug mode)
        /// and records in the database must be populated (runs in constructor)</remarks>
        [Theory]
        [PropertyData("DnsRecordDomainNamesTheoryData")]
        public void TestDnsResolveMX(string domain)
        {
            DnsClient client = new DnsClient("127.0.0.1", 5353);
            client.Timeout = TimeSpan.FromSeconds(20);
            Dump(string.Format("attempting to resolve MX from dns server for {0}", domain));
            List<MXRecord> lst = client.ResolveMX(domain).ToList();
            Assert.Equal(1,lst.Count);
            Assert.Equal(domain
                , lst[0].Name);
        }


        /// <summary>
        /// Runs a live test against the dnsResponder service to see if service will actually resolve results for ANAME quesions
        /// </summary>
        /// <remarks>In order to run this you need to have the dnsResponder service running (which can run in debug mode)
        /// and records in the database must be populated (runs in constructor)</remarks>
        [Theory]
        [PropertyData("DnsRecordDomainNamesTheoryData")]
        public void TestDnsResolveANAME(string domain)
        {
            DnsClient client = new DnsClient("127.0.0.1", 5353);
            client.Timeout = TimeSpan.FromSeconds(20);
            Dump(string.Format("attempting to resolve ANAME from dns server for {0}", domain));
            List<AddressRecord> lst = client.ResolveA(domain).ToList();
            Assert.Equal(1, lst.Count);
            Assert.Equal(domain
                , lst[0].Name);
        }


        /// <summary>
        /// Runs a live test against the dnsResponder service to see if service will actually resolve results for SOA quesions
        /// </summary>
        /// <remarks>In order to run this you need to have the dnsResponder service running (which can run in debug mode)
        /// and records in the database must be populated (runs in constructor)</remarks>
        [Theory]
        [PropertyData("DnsRecordDomainNamesTheoryData")]
        public void TestDnsResolveSOA(string domain)
        {
            DnsClient client = new DnsClient("127.0.0.1", 5353);
            client.Timeout = TimeSpan.FromSeconds(20);
            Dump(string.Format("attempting to resolve SOA from dns server for {0}", domain));
            List<SOARecord> lst = client.ResolveSOA(domain).ToList();
            Assert.Equal(1, lst.Count);
            Assert.Equal(domain
                , lst[0].Name);
        }


        [Theory]
        [PropertyData("CertRecordOwnersTheoryData")]
        public void TestDnsResolveCERT(string owner)
        {
            //----------------------------------------------------------------------------------------------------
            //---init the cert records
            this.InitCertRecords();

            DnsClient client = new DnsClient("127.0.0.1", 5353);
            client.Timeout = TimeSpan.FromSeconds(20);
            IEnumerable<CertRecord> lstenm = client.ResolveCERT(owner);
            if (lstenm == null)
            {
                throw new Exception(string.Format("no data returned for {0}", owner));
            }
            List<CertRecord> lst = lstenm.ToList();
            Assert.Equal(3, lst.Count());
            Assert.Equal(owner.Replace('@','.')
                , lst[0].Name);
        }
        */
    }
}
