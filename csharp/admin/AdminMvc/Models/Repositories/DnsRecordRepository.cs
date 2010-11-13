using System.Linq;

using Health.Direct.Common.DnsResolver;
using Health.Direct.Config.Client.DomainManager;
using Health.Direct.Config.Store;

namespace Health.Direct.Admin.Console.Models.Repositories
{
    public class DnsRecordRepository : IDnsRecordRepository
    {
        private readonly IDnsRecordManager m_client;

        public DnsRecordRepository(IDnsRecordManager client)
        {
            m_client = client;
        }

        protected IDnsRecordManager Client { get { return m_client; } }
        
        public IQueryable<DnsRecord> FindAll()
        {
            return Client.EnumerateDnsRecords(0, int.MaxValue, DnsStandard.RecordType.MX).AsQueryable();
        }

        public DnsRecord Add(DnsRecord record)
        {
            return Client.AddDnsRecord(record);
        }

        public void Update(DnsRecord address)
        {
            Client.UpdateDnsRecord(address);
        }

        public void Delete(DnsRecord record)
        {
            Client.RemoveDnsRecord(record);
        }

        public DnsRecord Get(long id)
        {
            return Client.GetDnsRecord(id);
        }
    }
}