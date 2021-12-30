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

using System.Collections.Generic;
using System.Threading.Tasks;
using Xunit;

namespace Health.Direct.Config.Store.Tests
{
    [Collection("ManagerFacts")]
    public class DnsRecordManagerFacts : ConfigStoreTestBase
    {
        private readonly DirectDbContext _dbContext;
        private readonly DnsRecordManager _dnsRecordManager;

        public DnsRecordManagerFacts()
        {
            _dbContext = CreateConfigDatabase();
            _dnsRecordManager = new DnsRecordManager(_dbContext);
        }

        [Fact]
        public async Task GetTest10()
        {
            await InitDnsRecords(_dbContext);

            
            List<DnsRecord> recs = await _dnsRecordManager.Get("microsoft.com");
            Assert.Equal(3, recs.Count);
            recs = await _dnsRecordManager.Get("microsoft11.com");
            Assert.Equal(0, recs.Count);

            recs = await _dnsRecordManager.Get("microsoft.com", Common.DnsResolver.DnsStandard.RecordType.AAAA);
            Assert.Equal(0, recs.Count);
            recs = await _dnsRecordManager.Get("microsoft.com", Common.DnsResolver.DnsStandard.RecordType.SOA);
            Assert.Equal(1, recs.Count);
            recs = await _dnsRecordManager.Get("microsoft.com", Common.DnsResolver.DnsStandard.RecordType.MX);
            Assert.Equal(1, recs.Count);
            recs = await _dnsRecordManager.Get("microsoft.com", Common.DnsResolver.DnsStandard.RecordType.ANAME);
            Assert.Equal(1, recs.Count);
        }

        /*
        /// <summary>
        ///A test for Update
        ///</summary>
        [Fact()]
        public void UpdateTest2()
        {
            InitDnsRecords();

            
            DnsRecord dnsRecord = await _dnsRecordManager.GetByAgentName(1);
            dnsRecord.RecordData = System.Text.Encoding.UTF8.GetBytes("this is a test");
            dnsRecord.Notes = "these are the notes";
            dnsRecord.DomainName = "someothername.com";
            _dnsRecordManager.Update(dnsRecord);

            dnsRecord = await _dnsRecordManager.GetByAgentName(1);
            Assert.Equal("these are notes", dnsRecord.Notes);
 

        /// <summary>
        ///A test for Update
        ///</summary>
        [Fact()]
        public void UpdateTest()
        {
            
            
            IEnumerable<DnsRecord> dnsRecords = null; 
            _dnsRecordManager.Update(dnsRecords);
            
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact()]
        public void RemoveTest3()
        {
            
            
            long recordID = 0; 
            _dnsRecordManager.Remove(recordID);
            
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact()]
        public void RemoveTest2()
        {
            
            
            DnsRecord dnsRecord = null; 
            _dnsRecordManager.Remove(dnsRecord);
            
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact()]
        public void RemoveTest1()
        {
            
            
            DirectDbContext db = null; 
            DnsRecord dnsRecord = null; 
            _dnsRecordManager.Remove(db, dnsRecord);
            
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact()]
        public void RemoveTest()
        {
            
            
            DirectDbContext db = null; 
            long recordID = 0; 
            _dnsRecordManager.Remove(db, recordID);
            
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact()]
        public void GetTest4()
        {
            
            
            long[] recordIDs = null; 
            DnsRecord[] expected = null; 
            DnsRecord[] actual;
            actual = await _dnsRecordManager.GetByAgentName(recordIDs);
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact()]
        public void GetTest3()
        {
            
            
            DirectDbContext db = null; 
            long recordID = 0; 
            DnsRecord expected = null; 
            DnsRecord actual;
            actual = await _dnsRecordManager.GetByAgentName(db, recordID);
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact()]
        public void GetTest2()
        {
            
            
            long recordID = 0; 
            DnsRecord expected = null; 
            DnsRecord actual;
            actual = await _dnsRecordManager.GetByAgentName(recordID);
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact()]
        public void GetTest1()
        {
            
            
            long lastRecordID = 0; 
            int maxResults = 0;
            Health.Direct.Common.DnsResolver.DnsStandard.RecordType typeID = Health.Direct.Common.DnsResolver.DnsStandard.RecordType.AAAA;
            DnsRecord[] expected = null; 
            DnsRecord[] actual;
            actual = await _dnsRecordManager.GetByAgentName(lastRecordID, maxResults, typeID);
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact()]
        public void GetTest()
        {
            
            
            DirectDbContext db = null; 
            long lastRecordID = 0; 
            int maxResults = 0;
            Health.Direct.Common.DnsResolver.DnsStandard.RecordType typeID = Health.Direct.Common.DnsResolver.DnsStandard.RecordType.AAAA;
            IEnumerable<DnsRecord> expected = null; 
            IEnumerable<DnsRecord> actual;
            actual = await _dnsRecordManager.GetByAgentName(db, lastRecordID, maxResults, typeID);
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for Count
        ///</summary>
        [Fact()]
        public void CountTest()
        {

            
            Nullable<Health.Direct.Common.DnsResolver.DnsStandard.RecordType> recordType = new Nullable<Health.Direct.Common.DnsResolver.DnsStandard.RecordType>(); 
            int expected = 0; 
            int actual;
            actual = _dnsRecordManager.Count(recordType);
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact()]
        public void AddTest3()
        {
            
            
            DirectDbContext db = null; 
            DnsRecord record = null; 
            _dnsRecordManager.Add(db, record);
            
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact()]
        public void AddTest2()
        {
            
            
            DnsRecord record = null; 
            _dnsRecordManager.Add(record);
            
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact()]
        public void AddTest1()
        {
            
            
            DirectDbContext db = null; 
            DnsRecord[] dnsRecords = null; 
            _dnsRecordManager.Add(db, dnsRecords);
            
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact()]
        public void AddTest()
        {
            
            
            DnsRecord[] dnsRecords = null; 
            _dnsRecordManager.Add(dnsRecords);
            
        }

        /// <summary>
        ///A test for DnsRecordManager Constructor
        ///</summary>
        [Fact()]
        public void DnsRecordManagerConstructorTest()
        {

            
        }
        */
    }
}
