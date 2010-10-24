using System.Collections.Generic;
using System.Linq;

using Health.Direct.Common.DnsResolver;

namespace Health.Direct.DnsResponder.Tests
{
    public class TestStore
    {
        public static TestStore Default;
        
        MemoryStore m_store;
        
        static TestStore()
        {
            Default = new TestStore();
            TestStore.AddTestRecords(Default.Store.Records);
        }
        
        public TestStore()
        {
            m_store = new MemoryStore();
        }
        
        public MemoryStore Store
        {
            get
            {
                return m_store;
            }
        }        
        
        public int Count
        {
            get
            {
                return m_store.Records.Count;
            }
        }
        
        public IEnumerable<string> Domains
        {
            get
            {
                return m_store.Records.Domains;
            }
        }
        
        public IEnumerable<DnsResourceRecord> Records
        {
            get
            {
                return m_store.Records.Records;
            }
        }
        
        public IEnumerable<AddressRecord> AddressRecords
        {
            get
            {
                return (
                           from record in this.Records
                           where record.Type == DnsStandard.RecordType.ANAME
                           select (AddressRecord) record
                       );
            }
        }
        
        public IEnumerable<NSRecord> NSRecords
        {
            get
            {
                return (
                           from record in this.Records
                           where record.Type == DnsStandard.RecordType.NS
                           select (NSRecord) record
                       );
            }
        }
    
        public static void AddTestRecords(DnsRecordTable store)
        {
            // Addresses
            store.Add(new AddressRecord("foo.com", "192.169.0.1"));
            store.Add(new AddressRecord("foo.com", "192.169.0.2"));
            store.Add(new AddressRecord("foo.com", "192.169.0.3"));
            store.Add(new AddressRecord("bar.com", "192.168.0.1"));
            store.Add(new AddressRecord("goo.com", "192.167.0.1"));
            store.Add(new AddressRecord("goo.com", "192.167.0.2"));
        }
    }
}