/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico  chris.lomonico@surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using Xunit;

namespace Health.Direct.Config.Store.Tests
{
    public class DnsRecordManagerFacts : ConfigStoreTestBase
    {
        private static new DnsRecordManager CreateManager()
        {
            return new DnsRecordManager(CreateConfigStore());
        }

        /// <summary>
        ///A test for Store
        ///</summary>
        [Fact]
        public void StoreTest()
        {
            DnsRecordManager mgr = CreateManager();
            ConfigStore actual = mgr.Store;
            Assert.Equal(mgr.Store, actual);
        }

        [Fact]
        public void GetTest10()
        {
            InitDnsRecords();

            DnsRecordManager mgr = CreateManager();
            DnsRecord[] recs = mgr.Get("microsoft.com");
            Assert.Equal(3, recs.Length);
            recs = mgr.Get("microsoft11.com");
            Assert.Equal(0, recs.Length);

            recs = mgr.Get("microsoft.com"
                , Common.DnsResolver.DnsStandard.RecordType.AAAA);
            Assert.Equal(0, recs.Length);
            recs = mgr.Get("microsoft.com"
                , Common.DnsResolver.DnsStandard.RecordType.SOA);
            Assert.Equal(1, recs.Length);
            recs = mgr.Get("microsoft.com"
                , Common.DnsResolver.DnsStandard.RecordType.MX);
            Assert.Equal(1, recs.Length);
            recs = mgr.Get("microsoft.com"
                , Common.DnsResolver.DnsStandard.RecordType.ANAME);
            Assert.Equal(1, recs.Length);
        }

        /*
        /// <summary>
        ///A test for Update
        ///</summary>
        [Fact()]
        public void UpdateTest2()
        {
            InitDnsRecords();

            DnsRecordManager mgr = CreateManager();
            DnsRecord dnsRecord = mgr.Get(1);
            dnsRecord.RecordData = System.Text.Encoding.UTF8.GetBytes("this is a test");
            dnsRecord.Notes = "these are the notes";
            dnsRecord.DomainName = "someothername.com";
            mgr.Update(dnsRecord);

            dnsRecord = mgr.Get(1);
            Assert.Equal("these are notes", dnsRecord.Notes);
 

        /// <summary>
        ///A test for Update
        ///</summary>
        [Fact()]
        public void UpdateTest()
        {
            
            DnsRecordManager mgr = CreateManager();
            IEnumerable<DnsRecord> dnsRecords = null; 
            mgr.Update(dnsRecords);
            
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact()]
        public void RemoveTest3()
        {
            
            DnsRecordManager mgr = CreateManager();
            long recordID = 0; 
            mgr.Remove(recordID);
            
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact()]
        public void RemoveTest2()
        {
            
            DnsRecordManager mgr = CreateManager();
            DnsRecord dnsRecord = null; 
            mgr.Remove(dnsRecord);
            
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact()]
        public void RemoveTest1()
        {
            
            DnsRecordManager mgr = CreateManager();
            ConfigDatabase db = null; 
            DnsRecord dnsRecord = null; 
            mgr.Remove(db, dnsRecord);
            
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact()]
        public void RemoveTest()
        {
            
            DnsRecordManager mgr = CreateManager();
            ConfigDatabase db = null; 
            long recordID = 0; 
            mgr.Remove(db, recordID);
            
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact()]
        public void GetTest4()
        {
            
            DnsRecordManager mgr = CreateManager();
            long[] recordIDs = null; 
            DnsRecord[] expected = null; 
            DnsRecord[] actual;
            actual = mgr.Get(recordIDs);
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact()]
        public void GetTest3()
        {
            
            DnsRecordManager mgr = CreateManager();
            ConfigDatabase db = null; 
            long recordID = 0; 
            DnsRecord expected = null; 
            DnsRecord actual;
            actual = mgr.Get(db, recordID);
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact()]
        public void GetTest2()
        {
            
            DnsRecordManager mgr = CreateManager();
            long recordID = 0; 
            DnsRecord expected = null; 
            DnsRecord actual;
            actual = mgr.Get(recordID);
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact()]
        public void GetTest1()
        {
            
            DnsRecordManager mgr = CreateManager();
            long lastRecordID = 0; 
            int maxResults = 0;
            Health.Direct.Common.DnsResolver.DnsStandard.RecordType typeID = Health.Direct.Common.DnsResolver.DnsStandard.RecordType.AAAA;
            DnsRecord[] expected = null; 
            DnsRecord[] actual;
            actual = mgr.Get(lastRecordID, maxResults, typeID);
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact()]
        public void GetTest()
        {
            
            DnsRecordManager mgr = CreateManager();
            ConfigDatabase db = null; 
            long lastRecordID = 0; 
            int maxResults = 0;
            Health.Direct.Common.DnsResolver.DnsStandard.RecordType typeID = Health.Direct.Common.DnsResolver.DnsStandard.RecordType.AAAA;
            IEnumerable<DnsRecord> expected = null; 
            IEnumerable<DnsRecord> actual;
            actual = mgr.Get(db, lastRecordID, maxResults, typeID);
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for Count
        ///</summary>
        [Fact()]
        public void CountTest()
        {

            DnsRecordManager mgr = CreateManager();
            Nullable<Health.Direct.Common.DnsResolver.DnsStandard.RecordType> recordType = new Nullable<Health.Direct.Common.DnsResolver.DnsStandard.RecordType>(); 
            int expected = 0; 
            int actual;
            actual = mgr.Count(recordType);
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact()]
        public void AddTest3()
        {
            
            DnsRecordManager mgr = CreateManager();
            ConfigDatabase db = null; 
            DnsRecord record = null; 
            mgr.Add(db, record);
            
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact()]
        public void AddTest2()
        {
            
            DnsRecordManager mgr = CreateManager();
            DnsRecord record = null; 
            mgr.Add(record);
            
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact()]
        public void AddTest1()
        {
            
            DnsRecordManager mgr = CreateManager();
            ConfigDatabase db = null; 
            DnsRecord[] dnsRecords = null; 
            mgr.Add(db, dnsRecords);
            
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact()]
        public void AddTest()
        {
            
            DnsRecordManager mgr = CreateManager();
            DnsRecord[] dnsRecords = null; 
            mgr.Add(dnsRecords);
            
        }

        /// <summary>
        ///A test for DnsRecordManager Constructor
        ///</summary>
        [Fact()]
        public void DnsRecordManagerConstructorTest()
        {

            DnsRecordManager mgr = CreateManager();
        }
        */
    }
}
