/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.IO;
using Health.Direct.Common.Caching;
using Health.Direct.Common.DnsResolver;

using Xunit;

namespace Health.Direct.Common.Tests.Caching
{
    public class DnsResponseCacheFacts : TestingBase
    {
        // set this to true if dump statements are needed for debugging purposes
        private const bool DumpIsEnabled = false;


        private const string m_filePath = @"..\..\..\..\common.metadata\dnsresponses";

        private int m_basettl = 10;
        private DnsResponseCache m_drrc;
        private List<DnsResponse> m_responses;

        /// <summary>
        /// Initializes a new instance of the <see cref="DnsResponseCache"/> class.
        /// </summary>
        public DnsResponseCacheFacts() : base(DumpIsEnabled)
        {
        }

        /// <summary>
        /// Gets the MockDomainResponses of the DnsResponseCache
        /// Remarks: 
        /// these names should match up with the *.bin files that hold dumps from actual dns responses
        /// </summary>
        public static IEnumerable<string> MockDomainResponses
        {
            get
            {
                yield return "aname.hvnhind.hsgincubator.com";
                yield return "aname.nhind.hsgincubator.com";
                yield return "aname.apple.com";
                yield return "aname.google.com";
                yield return "aname.microsoft.com";
                yield return "aname.yahoo.com";
            }
        }

        /// <summary>
        /// Returns a list of dns response entries that has been loaded from the files that are part of the solution
        /// </summary>
        /// <remarks>
        /// Verifies that list has been properly loaded up. These are only A record responses.
        /// </remarks>
        protected void PopulateMockDnsARecordResponseEntries()
        {
            m_responses = new List<DnsResponse>();
            foreach (string s in MockDomainResponses)
            {
                byte[] buff;
                DnsResponse dr;

                string fileName = Path.GetFullPath(Path.Combine(m_filePath, s + ".bin"));
                using (FileStream fs = new FileStream(fileName, FileMode.Open, FileAccess.Read))
                {
                    Dump("checking [{0}]", fileName);
                    buff = new BinaryReader(fs).ReadBytes((int)new FileInfo(fileName).Length);
                    DnsBufferReader reader = new DnsBufferReader(buff, 0, buff.Count());

                    // get a dr 
                    dr = new DnsResponse(reader);
                    m_responses.Add(dr);
                }

                // ensure that the qusetion QName matches the name of the mocked entry
                Assert.True(dr.Question.Domain.ToLower().Contains(s.ToLower().Replace("aname.","")));
            }
        }

        protected virtual void ConvertDnsResponseToBin()
        {

        }

        /// <summary>
        /// Simple method to force a specific ttl on the A and CERT records in the responses
        /// </summary>
        /// <param name="seconds">int value containing the seconds to set for the ttl</param>
        protected virtual void ForceTtlTime(int seconds)
        {
            // right now just really dealing with the A and CERT Records; proof of concept, as more if 
            // one really feels it is necessary
            Assert.True(m_responses != null, "List has not been initialized");

            Dump("Forcing ttl for all ANAME records to [{0}] seconds", seconds);

            foreach (DnsResponse dr in m_responses)
            {
                foreach (AddressRecord c in dr.AnswerRecords.A)
                {
                    c.TTL = seconds;
                }
            }
        }

        /// <summary>
        /// Simple test to ensure proper loading of mocked entries
        /// </summary>
        [Fact]
        public void ValidatedMockDnsResponsEntries()
        {
            PopulateMockDnsARecordResponseEntries();
        }

        /// <summary>
        /// this routine populates the cache with items and confirms that they are in there
        /// </summary>
        public void PopulateBasicCache()
        {
            m_drrc = new DnsResponseCache(Guid.NewGuid().ToString("D"));

            DumpSuccess("");

            // populate the entires
            PopulateMockDnsARecordResponseEntries();
            
            // force static TTL
            ForceTtlTime(m_basettl);

            foreach (DnsResponse dr in m_responses)
            {
                m_drrc.Put(dr);
            }

            DumpSuccess("Checking for {0} items stored in cache", m_responses.Count());

            // make sure that the cache count reflects 10 entries
            Assert.Equal(m_responses.Count(), m_drrc.CacheCount);

            DumpSuccess("Checking all entries in cache to make sure they exist as expected");

            // check the entries to make sure that all are found in the cache
            foreach (DnsResponse dr in m_responses)
            {
                DnsResponse res = m_drrc.Get(dr.Question);

                // make sure that the expect entry was found
                Assert.NotNull(res);
            }
        }

        /// <summary>
        /// Test mocks standing up a DnsResponseCache instance, checks to make sure items are stored; verifies single retrieval
        /// of each item and ensures that all items can be deleted in one call
        /// </summary>
        [Fact]
        public void CheckBasicCacheFeatures()
        {
            //try
            {
                // populate the cache
                PopulateBasicCache();

                // clean out the cache
                DumpSuccess("Cleaning Cache out");

                m_drrc.RemoveAll();
                Dump("There are [{0}] items left in cache", m_drrc.CacheCount);
                Assert.Equal(0, m_drrc.CacheCount);
            }
            //finally
            //{
            //    m_drrc.RemoveAll();
            //}
        }

        /// <summary>
        /// checks to ensure that items can be removed from the cache and that counts match up
        /// </summary>
        [Fact]
        public void CacheRemovalTest()
        {
            try
            {

                //----------------------------------------------------------------------------------------------------
                //---populate the cache
                PopulateBasicCache();

                
                //----------------------------------------------------------------------------------------------------
                //---get the first response, we are going to remove it
                DnsResponse dr = m_responses[0];

                //----------------------------------------------------------------------------------------------------
                //--make sure that the item is in there based on the response (should be cause it was tested above)
                Dump(string.Format("By Dns Response - checking to make sure that [{0}] is in cache", dr.Question.Domain));
                Assert.NotNull(m_drrc.Get(dr));

                //----------------------------------------------------------------------------------------------------
                //--try to remove item from the cache that is not in there by the dns response
                m_drrc.Remove(dr);

                //----------------------------------------------------------------------------------------------------
                //---cache should have 1 less item now
                Dump(string.Format("By Dns Response - checking to make sure that cache count is [{0}]", m_responses.Count() - 1));
                Assert.Equal(m_drrc.CacheCount
                             , m_responses.Count() - 1);

                //----------------------------------------------------------------------------------------------------
                //---make sure that the item is not in there by the dns response
                Dump(string.Format("By Dns Response - checking to make sure that [{0}] is in NOT cache", dr.Question.Domain));
                Assert.Null(m_drrc.Get(dr));

                //----------------------------------------------------------------------------------------------------
                //---put the record back in
                m_drrc.Put(dr);

                //----------------------------------------------------------------------------------------------------
                //--make sure that the item is in there based on the response (should be cause it was tested above)
                Dump(string.Format("By Question - checking to make sure that [{0}] is in cache", dr.Question.Domain));
                Assert.NotNull(m_drrc.Get(dr.Question));

                //----------------------------------------------------------------------------------------------------
                //--try to remove item from the cache that is not in there by the dns response
                m_drrc.Remove(dr);

                //----------------------------------------------------------------------------------------------------
                //---cache should have 1 less item now
                Dump(string.Format("By Question - checking to make sure that cache count is [{0}]", m_responses.Count() - 1));
                Assert.Equal(m_drrc.CacheCount
                             , m_responses.Count() - 1);

                //----------------------------------------------------------------------------------------------------
                //---make sure that the item is not in there by the dns response
                Dump(string.Format("By Question - checking to make sure that [{0}] is in NOT cache", dr.Question.Domain));
                Assert.Null(m_drrc.Get(dr.Question));


            }
            finally
            {
                m_drrc.RemoveAll();
            }

        }


        /// <summary>
        /// checks for duplicates inside the cache and how they are handled
        /// Remarks:
        /// dups should not add more than one record, instead they should update the ttl and the respective value
        /// </summary>
        [Fact]
        public void CacheDuplicateTest()
        {


            try
            {
                //----------------------------------------------------------------------------------------------------
                //---set the ttl to 30 seconds
                m_basettl = 30;

                //----------------------------------------------------------------------------------------------------
                //---populate the cache
                PopulateBasicCache();

                //----------------------------------------------------------------------------------------------------
                //---cache should have 1 less item now
                Dump(string.Format("[{0}] records in cache before updating a dup record", m_drrc.CacheCount));


                //----------------------------------------------------------------------------------------------------
                //---now attempt to add an item to the cache that is already there 
                DnsResponse dr = m_responses[0];
                m_drrc.Put(dr);

                //----------------------------------------------------------------------------------------------------
                //---cache should have 1 less item now
                Dump(string.Format("[{0}] records in cache after updating with dup record", m_drrc.CacheCount));

                //----------------------------------------------------------------------------------------------------
                //---make sure that there are only the X number of items in the cache
                Assert.Equal(m_responses.Count
                             , m_drrc.CacheCount);


            }
            finally
            {
                m_drrc.RemoveAll();
            }

        }


        /// <summary>
        /// checks to make sure that ttl is respected in the cache
        /// Remarks:
        /// adds item to the cache with speficied ttl, ensures that item exists, sleeps and checks to make sure item was
        /// removed from the cache
        /// from what I can tell so far, the notification of the removal of an item from the cache is delayed; not the exact time
        /// 
        /// this is commented out for now as it slows down testing, feel free to use as needed
        /// </summary>
        /*
        [Fact]
        public void CheckTtl()
        {

            try
            {
                //----------------------------------------------------------------------------------------------------
                //---set the ttl to 30 seconds
                m_basettl = 2;

                //----------------------------------------------------------------------------------------------------
                //---populate the cache
                PopulateBasicCache();

                //----------------------------------------------------------------------------------------------------
                //---make a copy of the list for tracking when items are expired from the cche
                Dictionary<string, DnsResponse> copy = m_responses.ToDictionary(k => string.Format("{0}.{1}", k.Question.Type.ToString(), k.Question.Domain).ToLower());

                //----------------------------------------------------------------------------------------------------
                //---add an event handler for cache item expired, we want to watch for when an item has ticked out
                //---of the cache
                m_drrc.CacheItemExpired += delegate(object sender, string key)
                {
                    //----------------------------------------------------------------------------------------------------
                    //---only waiting for one to expire
                    Dump(string.Format("{0} has expired in the cache", key));
                    copy.Remove(key);
                };

                //----------------------------------------------------------------------------------------------------
                //---cache should have 1 less item now
                Dump(string.Format("[{0}] records in cache", m_drrc.CacheCount));

                //----------------------------------------------------------------------------------------------------
                //---sleep for 10 seconds cause for some reason the callbacks are a little lazy on updating the 
                //---numbered items in the cache

                Dump("Waiting for expiration or 30 seconds");

                int cnt = 0;
                while (copy.Count > 0)
                {
                    System.Threading.Thread.Sleep(1);
                    cnt++;
                    if (cnt == 30000)
                    {
                        break;
                    }
                }
                Dump("");

                Dump("[{0}] records in cache after wait", m_drrc.CacheCount);

                Assert.Equal(0, m_drrc.CacheCount);
            }
            finally
            {
                m_drrc.RemoveAll();
            }

            DumpSuccess("Test completed successfully");

        }
        */
    }
}