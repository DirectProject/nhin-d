/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico chris.lomonico@surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.IO;
using System.Net.Sockets;
using System.Text;
using Microsoft.Win32;

using DnsResolver;
using NHINDirect.Caching;

using Xunit;
using Xunit.Extensions;

namespace NHINDirect.Tests.Caching
{

    public class DnsResponseCacheFacts : TestingBase
	{

        protected static string m_filePath = Environment.CurrentDirectory + @"\metadata\dns responses";
        protected int m_basettl = 10;
        protected DnsResponseCache m_drrc;
        protected List<DnsResponse> m_responses;

        #region public DnsResponseCacheFacts()
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.31.2010 6:29:56 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// Initializes a new instance of the <b>DnsResponseCache</b> class.
        /// </summary>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public DnsResponseCacheFacts()
        {
            this.m_drrc = new DnsResponseCache();
        }
        #endregion

        #region public static IEnumerable<string> MockDomainResponses
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.30.2010 6:58:27 PM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// Gets the MockDomainResponses of the DnsResponseCache
        /// </summary>
        /// <value></value>
        /// <remarks>these names should match up with the *.bin files that hold dumps from actual dns responses</remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public static IEnumerable<string> MockDomainResponses
        {
            get
            {
                yield return "dns.hsgincubator.com";
                yield return "hvnhind.hsgincubator.com";
                yield return "nhind.hsgincubator.com";
                yield return "www.apple.com";
                yield return "www.google.com";
                yield return "www.microsoft.com";
                yield return "www.yahoo.com";
            }
        }
        #endregion

        #region protected virtual void PopulateMockDnsARecordResponseEntries()
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.30.2010 7:03:23 PM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// returns a list of dns response entries that has been loaded from the files that are part of the solution
        /// </summary>
        /// <remarks>verifies that list has been properly loaded up note that these are only A record responses</remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        protected virtual void PopulateMockDnsARecordResponseEntries()
        {
            m_responses = new List<DnsResponse>();
            foreach (string s in MockDomainResponses)
            {
                byte[] buff = null;
                DnsResponse dr = null;

                string fileName = String.Format(@"{0}\{1}.bin", m_filePath, s);
                using (FileStream fs = new FileStream(fileName, FileMode.Open, FileAccess.Read))
                {
                    Dump(String.Format("checking [{0}]", fileName));
                    buff = new BinaryReader(fs).ReadBytes((int)new FileInfo(fileName).Length);
                    DnsBufferReader reader = new DnsBufferReader(buff, 0, buff.Count());
                    //----------------------------------------------------------------------------------------------------
                    //---get a dr 
                    dr = new DnsResponse(ref reader);
                    m_responses.Add(dr);
                }
                //----------------------------------------------------------------------------------------------------
                //---ensure that the qusetion QName matches the name of the mocked entry
                Assert.Equal(s.ToLower(), dr.Question.QName.ToLower());
            }
        }
        #endregion

        #region protected virtual void ForceTtlTime(int seconds)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.30.2010 7:39:49 PM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// simple method to force a specific ttl on the A and CERT records in the responses
        /// </summary>
        /// <param name="seconds">int value containing the seconds to set for the ttl</param>
        /// <param name="responses">list containing DnsResponse entries</param>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        protected virtual void ForceTtlTime(int seconds)
        {
            //----------------------------------------------------------------------------------------------------
            //---right now just really dealing with the A and CERT Records; proof of concept, as more if 
            //---one really feels it is necessary
            if (m_responses == null)
            {
                throw new Exception("List has not been initialized");
            }

            this.Dump(String.Format("Forcing ttl for all ANAME records to [{0}] seconds", seconds));
            foreach (DnsResponse dr in this.m_responses)
            {
                foreach (AddressRecord c in dr.AnswerRecords.A)
                {
                    c.TTL = seconds;
                }


            }
        }
        #endregion

        #region public void ValidatedMockDnsResponsEntries()
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.30.2010 7:35:34 PM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// simple test to ensure proper loading of mocked entries
        /// </summary>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        [Fact]
        public void ValidatedMockDnsResponsEntries()
        {
            this.PopulateMockDnsARecordResponseEntries();
        }
        #endregion

        #region public void PopulateBasicCache()
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.30.2010 7:51:31 PM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// this routine populates the cache with items and confirms that they are in there
        /// </summary>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public void PopulateBasicCache()
        {
            this.m_drrc = new DnsResponseCache();
            Dump(String.Format(new string('-', 50)));
            //----------------------------------------------------------------------------------------------------
            //---populate the entires
            this.PopulateMockDnsARecordResponseEntries();
            
            //----------------------------------------------------------------------------------------------------
            //---force static TTL
            this.ForceTtlTime(m_basettl);

            foreach (DnsResponse dr in this.m_responses)
            {
                m_drrc.Put(dr);
            }
            Dump(String.Format(new string('-', 50)));
            Dump(String.Format("Checking for {0} items stored in cache"
                , this.m_responses.Count()));
            //----------------------------------------------------------------------------------------------------
            //---make sure that the cache count reflects 10 entries
            Assert.Equal(this.m_responses.Count()
                , this.m_drrc.CacheCount);
            Dump(String.Format(new string('-', 50)));
            Dump(String.Format("Checking all entries in cache to make sure they exist as expected"));

            //----------------------------------------------------------------------------------------------------
            //---check the entries to make sure that all are found in the cache
            foreach (DnsResponse dr in this.m_responses)
            {
                DnsResponse res = this.m_drrc.Get(dr.Question);
                //----------------------------------------------------------------------------------------------------
                //---make sure that the expect entry was found
                Assert.NotNull(res);
            }
        }
        #endregion

        #region public void CheckBasicCacheFeatures()
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.25.2010 10:08:59 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// test mocks standing up a DnsResponseCache instance, checks to make sure items are stored; verifies single retrieval
        /// of each item and ensures that all items can be deleted in one call
        /// </summary>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        [Fact]
        public void CheckBasicCacheFeatures()
        {
            try
            {

                //----------------------------------------------------------------------------------------------------
                //---populate the cache
                PopulateBasicCache();

                //----------------------------------------------------------------------------------------------------
                //---clean out the cache
                Dump(String.Format(new string('-', 50)));
                Dump(String.Format("Cleaning Cache out"));
                this.m_drrc.RemoveAll();
                Dump(String.Format("there are [{0}] items left in cache"
                    , m_drrc.CacheCount));
                Assert.Equal(0
                    , m_drrc.CacheCount);

            }
            finally
            {
                this.m_drrc.RemoveAll();
            }


        }
        #endregion

        #region public void CacheRemovalTest()
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.25.2010 10:51:43 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// checks to ensure that items can be removed from the cache and that counts match up
        /// </summary>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        [Fact]
        public void CacheRemovalTest()
        {
            try
            {

                //----------------------------------------------------------------------------------------------------
                //---populate the cache
                this.PopulateBasicCache();

                
                //----------------------------------------------------------------------------------------------------
                //---get the first response, we are going to remove it
                DnsResponse dr = this.m_responses[0];

                //----------------------------------------------------------------------------------------------------
                //--make sure that the item is in there based on the response (should be cause it was tested above)
                Dump(String.Format("By Dns Response - checking to make sure that [{0}] is in cache", dr.Question.QName));
                Assert.NotNull(this.m_drrc.Get(dr));

                //----------------------------------------------------------------------------------------------------
                //--try to remove item from the cache that is not in there by the dns response
                this.m_drrc.Remove(dr);

                //----------------------------------------------------------------------------------------------------
                //---cache should have 1 less item now
                Dump(String.Format("By Dns Response - checking to make sure that cache count is [{0}]", this.m_responses.Count() - 1));
                Assert.Equal(this.m_drrc.CacheCount
                    , this.m_responses.Count() - 1);

                //----------------------------------------------------------------------------------------------------
                //---make sure that the item is not in there by the dns response
                Dump(String.Format("By Dns Response - checking to make sure that [{0}] is in NOT cache", dr.Question.QName));
                Assert.Null(this.m_drrc.Get(dr));

                //----------------------------------------------------------------------------------------------------
                //---put the record back in
                this.m_drrc.Put(dr);

                //----------------------------------------------------------------------------------------------------
                //--make sure that the item is in there based on the response (should be cause it was tested above)
                Dump(String.Format("By Question - checking to make sure that [{0}] is in cache", dr.Question.QName));
                Assert.NotNull(this.m_drrc.Get(dr.Question));

                //----------------------------------------------------------------------------------------------------
                //--try to remove item from the cache that is not in there by the dns response
                this.m_drrc.Remove(dr);

                //----------------------------------------------------------------------------------------------------
                //---cache should have 1 less item now
                Dump(String.Format("By Question - checking to make sure that cache count is [{0}]", this.m_responses.Count() - 1));
                Assert.Equal(this.m_drrc.CacheCount
                    , this.m_responses.Count() - 1);

                //----------------------------------------------------------------------------------------------------
                //---make sure that the item is not in there by the dns response
                Dump(String.Format("By Question - checking to make sure that [{0}] is in NOT cache", dr.Question.QName));
                Assert.Null(this.m_drrc.Get(dr.Question));


            }
            finally
            {
                this.m_drrc.RemoveAll();
            }

        }
        #endregion
        
        #region public void CacheDuplicateTest()
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.25.2010 11:29:05 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// checks for duplicates inside the cache and how they are handled
        /// </summary>
        /// <remarks>dups should not add more than one record, instead they should update the ttl and the respective value</remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
                this.PopulateBasicCache();

                //----------------------------------------------------------------------------------------------------
                //---cache should have 1 less item now
                Dump(String.Format("[{0}] records in cache before updating a dup record", this.m_drrc.CacheCount));


                //----------------------------------------------------------------------------------------------------
                //---now attempt to add an item to the cache that is already there 
                DnsResponse dr = this.m_responses[0];
                this.m_drrc.Put(dr);

                //----------------------------------------------------------------------------------------------------
                //---cache should have 1 less item now
                Dump(String.Format("[{0}] records in cache after updating with dup record", this.m_drrc.CacheCount));

               //----------------------------------------------------------------------------------------------------
               //---make sure that there are only the X number of items in the cache
                Assert.Equal(this.m_responses.Count
                    , this.m_drrc.CacheCount);


            }
            finally
            {
                this.m_drrc.RemoveAll();
            }

        }
        #endregion

        #region public void CheckTtl()
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.25.2010 11:34:32 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// checks to make sure that ttl is respected in the cache
        /// </summary>
        /// <remarks>adds item to the cache with speficied ttl, ensures that item exists, sleeps and checks to make sure item was
        /// removed from the cache
        /// from what I can tell so far, the notification of the removal of an item from the cache is delayed; not the exact time</remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
                this.PopulateBasicCache();

                //----------------------------------------------------------------------------------------------------
                //---make a copy of the list for tracking when items are expired from the cche
                Dictionary<string, DnsResponse> copy = this.m_responses.ToDictionary(k => string.Format("{0}.{1}", k.Question.QType.ToString(), k.Question.QName).ToLower());

                //----------------------------------------------------------------------------------------------------
                //---add an event handler for cache item expired, we want to watch for when an item has ticked out
                //---of the cache
                this.m_drrc.CacheItemExpired += delegate(object sender, string key)
                {
                    //----------------------------------------------------------------------------------------------------
                    //---only waiting for one to expire
                    this.Dump(string.Format("{0} has expired in the cache", key));
                    copy.Remove(key);
                };

                //----------------------------------------------------------------------------------------------------
                //---cache should have 1 less item now
                Dump(String.Format("[{0}] records in cache", this.m_drrc.CacheCount));

                //----------------------------------------------------------------------------------------------------
                //---sleep for 10 seconds cause for some reason the callbacks are a little lazy on updating the 
                //---numbered items in the cache

                this.Dump("Waiting for expiration or 30 seconds");

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
                Console.Write("\r\n");

                Dump(String.Format("[{0}] records in cache after wait"
                    , this.m_drrc.CacheCount));

                Assert.Equal(0, this.m_drrc.CacheCount);
            }
            finally
            {
                this.m_drrc.RemoveAll();
            }

            DumpSuccess("Test completed successfully");

        }
        #endregion

 

	}
}