/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Data.SqlClient;
using System.Linq;
using Xunit;

namespace Health.Direct.Config.Store.Tests
{
    public class MdnManagerFacts : ConfigStoreTestBase
    {
        private static MdnManager CreateManager()
        {
            return new MdnManager(CreateConfigStore());
        }

        /// <summary>
        ///A test for Store
        ///</summary>
        [Fact]
        public void StoreTest()
        {
            MdnManager target = CreateManager();
            ConfigStore actual = target.Store;
            Assert.Equal(target.Store, actual);
        }



        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact]
        public void AddTest()
        {
            MdnManager target = CreateManager();
            target.RemoveAll();
            Assert.Equal(0, target.Count());
            string messageId = Guid.NewGuid().ToString();
            var mdn = BuildMdn(messageId, "Name1@nhind.hsgincubator.com", "Name1@domain1.test.com", "To dispatch or not dispatch", null);

            target.Start(new Mdn[]{mdn});
            mdn = target.Get(mdn.MdnIdentifier);
            Assert.NotNull(mdn);
            Assert.Equal("To dispatch or not dispatch", mdn.SubjectValue);
        }

        /// <summary>
        ///A test for duplicate key violation.
        ///</summary>
        [Fact]
        public void AddExceptionTest()
        {
            MdnManager target = CreateManager();
            InitMdnRecords();
            string messageId = Guid.NewGuid().ToString();
            Mdn mdn = BuildMdn("945cc145-431c-4119-a8c6-7f557e52fd7d", "Name1@nhind.hsgincubator.com", "Name1@domain1.test.com", "To dispatch or not dispatch", null);
            Assert.Contains("Cannot insert duplicate key"
                , Assert.Throws<SqlException>(() => target.Start(new Mdn[] { mdn })).Message);
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetByMdnIdentifierTest()
        {
            MdnManager target = CreateManager();
            InitMdnRecords();
            Assert.Equal(41, target.Count());
            Assert.NotNull(target.Get("5F2C321259195D4A04E494FEAA15B4BD"));
        }

        /// <summary>
        ///A test for Update
        ///</summary>
        [Fact]
        public void UpdateStatusTest()
        {
            MdnManager target = CreateManager();
            InitMdnRecords();
            var mdn = target.Get("5F2C321259195D4A04E494FEAA15B4BD");
            Assert.Null(mdn.Status);

            mdn.Status = MdnStatus.Processed;
            target.Update(mdn);
            mdn = target.Get("5F2C321259195D4A04E494FEAA15B4BD");
            Assert.Equal(MdnStatus.Processed, mdn.Status);
            mdn.Status = MdnStatus.Dispatched;
            target.Update(mdn);
            mdn = target.Get("5F2C321259195D4A04E494FEAA15B4BD");
            Assert.Equal(MdnStatus.Dispatched, mdn.Status);

        }

        /// <summary>
        ///A test for Update
        ///</summary>
        [Fact]
        public void UpdateDispatchedTest()
        {
            MdnManager target = CreateManager();
            InitMdnRecords();
            var mdn = target.Get("5F2C321259195D4A04E494FEAA15B4BD");
            Assert.Null(mdn.Status);

            //
            // Skip processed step is allowed.
            // An external timer will move to Timed out
            //
            mdn.Status = MdnStatus.Dispatched;
            target.Update(mdn);
            mdn = target.Get("5F2C321259195D4A04E494FEAA15B4BD");
            Assert.Equal(MdnStatus.Dispatched, mdn.Status);

        }


        /// <summary>
        ///A test for Update
        ///</summary>
        [Fact]
        public void UpdateTimeoutTest()
        {
            MdnManager target = CreateManager();
            InitMdnRecords();
            var mdn = target.Get("5F2C321259195D4A04E494FEAA15B4BD");
            Assert.Null(mdn.Status);
            Assert.False(mdn.Timedout);

            //
            // Skip processed step is allowed.
            // An external timer will move to Timed out
            //
            mdn.Timedout = true;
            target.Update(mdn);
            mdn = target.Get("5F2C321259195D4A04E494FEAA15B4BD");
            Assert.True(mdn.Timedout);
        }

        
        

        /// <summary>
        ///A test for Expired Mdn Processed
        ///</summary>
        [Fact]
        public void GetProcessExpiredTest()
        {
            MdnManager target = CreateManager();
            InitMdnRecords();

            //timespan and max records set
            var mdns = target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 10);
            Assert.Equal(10, mdns.Count());

            //timespan and max records set
            mdns = target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 5);
            Assert.Equal(5, mdns.Count());

            //timespan and max records set
            mdns = target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 40);
            Assert.Equal(20, mdns.Count());

            //timespan and max records set
            mdns = target.GetExpiredProcessed(TimeSpan.FromMinutes(11), 40);
            Assert.Equal(0, mdns.Count());


            //default maxrecords set
            mdns = target.GetExpiredProcessed(TimeSpan.FromMinutes(10));
            Assert.Equal(10, mdns.Count());

            //default expieredLimit and maxResults
            mdns = target.GetExpiredProcessed();
            Assert.Equal(10, mdns.Count());
                       
            
        }

        /// <summary>
        ///A test for Expired Mdn Processed
        ///</summary>
        [Fact]
        public void ProcessingProcessExpiredTest()
        {
            MdnManager target = CreateManager();
            InitMdnRecords();

            //timespan and max records set
            var mdns = target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 2);
            Assert.Equal(2, mdns.Count());

            foreach (var mdn in mdns)
            {
                mdn.Timedout = true;
            }
            target.Update(mdns);

            mdns = target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 20);
            Assert.Equal(18, mdns.Count());

            foreach (var mdn in mdns)
            {
                mdn.Timedout = true;
            }
            target.Update(mdns);

            mdns = target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 20);
            Assert.Equal(0, mdns.Count());
        }


        /// <summary>
        ///A test for expired Mdn Dispatched
        ///</summary>
        [Fact]
        public void GetDispatchedExpiredTest()
        {
            MdnManager target = CreateManager();
            InitMdnRecords();

            //timespan and max records set
            var mdns = target.GetExpiredDispatched(TimeSpan.FromMinutes(10), 20);
            Assert.Equal(10, mdns.Count());

            //timespan and max records set
            mdns = target.GetExpiredDispatched(TimeSpan.FromMinutes(10), 10);
            Assert.Equal(10, mdns.Count());

            //timespan and max records set
            //Nothing this old yet.
            mdns = target.GetExpiredDispatched(TimeSpan.FromMinutes(11), 10);
            Assert.Equal(0, mdns.Count());

            //timespan and max records set
            mdns = target.GetExpiredDispatched(TimeSpan.FromMinutes(10), 5);
            Assert.Equal(5, mdns.Count());

            //default expiredLimit (10 minutes) and maxResults (10)
            mdns = target.GetExpiredDispatched();
            Assert.Equal(10, mdns.Count());
        }


        /// <summary>
        ///A test for Expired Mdn Dispatched
        ///</summary>
        [Fact]
        public void ProcessingDispatchedExpiredTest()
        {
            MdnManager target = CreateManager();
            InitMdnRecords();

            
            //timespan and max records set
            var mdns = target.GetExpiredDispatched(TimeSpan.FromMinutes(10), 2);
            Assert.Equal(2, mdns.Count());

            foreach (var mdn in mdns)
            {
                mdn.Timedout = true; 
            }
            target.Update(mdns);

            mdns = target.GetExpiredDispatched(TimeSpan.FromMinutes(10), 20);
            Assert.Equal(8, mdns.Count());

            foreach (var mdn in mdns)
            {
                mdn.Timedout = true;
            }
            target.Update(mdns);

            mdns = target.GetExpiredDispatched(TimeSpan.FromMinutes(10), 10);
            Assert.Equal(0, mdns.Count());
        }

        

        /// <summary>
        ///A test for expired Mdn Dispatched Timer
        ///</summary>
        [Fact]
        public void GetTimeoutTest()
        {
            MdnManager target = CreateManager();
            InitMdnRecords();

            //This would run often enough to keep the Mdns table peformant
            var mdns = target.GetTimedOut();
            Assert.Equal(0, mdns.Count());

            mdns = target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 2);
            Assert.Equal(2, mdns.Count());

            foreach (var mdn in mdns)
            {
                mdn.Timedout = true;
            }
            target.Update(mdns);

            mdns = target.GetTimedOut();
            Assert.Equal(2, mdns.Count());

            target.RemoveTimedOut();
            mdns = target.GetTimedOut();
            Assert.Equal(0, mdns.Count());


            //
            //Two seperate updates
            //

            //Update thread one
            mdns = target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 2);
            Assert.Equal(2, mdns.Count());

            foreach (var mdn in mdns)
            {
                mdn.Timedout = true;
            }
            target.Update(mdns);

            //update thread two
            mdns = target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 2);
            Assert.Equal(2, mdns.Count());

            foreach (var mdn in mdns)
            {
                mdn.Timedout = true;
            }
            target.Update(mdns);



            mdns = target.GetTimedOut();
            Assert.Equal(4, mdns.Count());

            target.RemoveTimedOut();
            mdns = target.GetTimedOut();
            Assert.Equal(0, mdns.Count());
        }

        /// <summary>
        ///A test for expired Mdn Dispatched Timer
        ///</summary>
        [Fact]
        public void CleanProcessedAndDispatched()
        {
            MdnManager target = CreateManager();
            InitMdnRecords();

            //
            // Ensure all test data is procesed or dispatched 
            //
            {
                var mdns = target.GetExpiredProcessed(TimeSpan.FromMinutes(1), 100);
                foreach (var mdn in mdns)
                {
                    mdn.Status = MdnStatus.Processed;
                    target.Update(mdn);
                }

                mdns = target.GetExpiredDispatched(TimeSpan.FromMinutes(0), 100);
                foreach (var mdn in mdns)
                {
                    mdn.Status = MdnStatus.Dispatched;
                    target.Update(mdn);
                }

                Mdn mdnx = target.Get("5F2C321259195D4A04E494FEAA15B4BD");
                mdnx.Status = MdnStatus.Processed;
                target.Update(mdnx);
            }
            Assert.Equal(41,target.Count());
            target.RemoveDispositions();
            Assert.Equal(0, target.Count());
        }

    }
}
