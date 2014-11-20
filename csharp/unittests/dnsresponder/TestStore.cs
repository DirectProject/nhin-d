/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Text;
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
            store.Add(new AddressRecord("localhost", "127.0.0.1"));
            store.Add(new AddressRecord("bigrecord.com", "1.2.3.4"));
            
            const string BigString = "0123456789abcdefghijklmnop";
            TextRecord txt = new TextRecord("goo.com", new string[] { BigString, "One_" + BigString, "Two_" + BigString});
            store.Add(txt);
            
            StringBuilder builder = new StringBuilder(64);
            for (int i = 0; i < 64; ++i)
            {
                builder.Append('a');
            }
            string bigTextString = builder.ToString();
            string[] textStrings = new string[128];
            for (int i = 0; i < textStrings.Length; ++i)
            {
                textStrings[i] = bigTextString;
            }
            store.Add(new TextRecord("bigrecord.com", textStrings)); 
            
            store.Add(new SRVRecord("foo.com", 20, 353, "x.y.z.com"));            
            store.Add(new MXRecord("goo.com", "foo.bar.xyz"));
        }
    }
    
    /// <summary>
    /// VERY basic MOCK store, to test out Client retry
    /// </summary>
    public class FailureStore : IDnsStore
    {
        IDnsStore m_innerStore;
        long m_requestCount = 0;
        
        public FailureStore(IDnsStore innerStore)
        {
            m_innerStore = innerStore;            
        }
        
        public IDnsStore Inner
        {
            get { return m_innerStore; }
        }
        
        /// <summary>
        /// Success for every second request by default
        /// </summary>
        public int SuccessInterval = 2;
        public DnsStandard.ResponseCode ErrorCode = DnsStandard.ResponseCode.ServerFailure;
        public bool ThrowDnsException = true;
               
        public DnsResponse Get(DnsRequest request)
        {
            long requestNumber = System.Threading.Interlocked.Increment(ref m_requestCount);
            if (this.SuccessInterval <= 0 || (requestNumber % this.SuccessInterval) != 0)
            {
                if (this.ThrowDnsException)
                {
                    throw new DnsServerException(ErrorCode);
                }
                
                throw new InvalidOperationException();
            }
            
            return m_innerStore.Get(request);
        }
    }
}