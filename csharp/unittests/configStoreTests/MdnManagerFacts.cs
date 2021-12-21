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
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Threading.Tasks;
using Health.Direct.Config.Store.Entity;
using Microsoft.Data.SqlClient;
using Xunit;
using Xunit.Abstractions;
using DbUpdateConcurrencyException = Microsoft.EntityFrameworkCore.DbUpdateConcurrencyException;
using DbUpdateException = Microsoft.EntityFrameworkCore.DbUpdateException;

namespace Health.Direct.Config.Store.Tests
{

    [Collection("ManagerFacts")]

    public class MdnManagerFacts : ConfigStoreTestBase
    {
        private readonly ITestOutputHelper _testOutputHelper;

        public MdnManagerFacts(ITestOutputHelper testOutputHelper)
        {
            _testOutputHelper = testOutputHelper;
        }

        /// <summary>
        ///A test for Store
        ///</summary>
        [Fact]
        public void StoreTest()
        {
            var target = CreateManager();
            var actual = target.Store;
            Assert.Equal(target.Store, actual);
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact]
        public async Task AddTest()
        {
            var target = CreateManager();
            await using (var db = CreateConfigDatabase())
            {
                await MdnUtil.RemoveAll(db);
            }
            Assert.Equal(0, await target.Count());
            string messageId = Guid.NewGuid().ToString();
            var mdn = BuildMdn(messageId, "Name1@nhind.hsgincubator.com", "Name1@domain1.test.com", "To dispatch or not dispatch", MdnStatus.Started);

            await target.Start(new Mdn[] { mdn });
            mdn = await target.Get(mdn.MdnIdentifier);
            Assert.Equal(MdnStatus.Started, mdn.Status);
            Assert.Equal("To dispatch or not dispatch", mdn.SubjectValue);
        }

        /// <summary>
        ///A test for duplicate key violation.
        ///</summary>
        [Fact]
        public async Task AddExceptionTest()
        {
            var target = CreateManager();
            await InitMdnRecords();
            string messageId = Guid.NewGuid().ToString();
            var mdn = BuildMdn("945cc145-431c-4119-a8c6-7f557e52fd7d", "Name1@nhind.hsgincubator.com", "Name1@domain1.test.com", "To dispatch or not dispatch", MdnStatus.Started);
            Assert.Contains(
                "Cannot insert duplicate key", 
                Assert.ThrowsAsync<DbUpdateException>(async () => 
                    await target
                        .Start(new Mdn[] { mdn })).Result.InnerException?.Message ?? string.Empty);
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public async Task GetByMdnIdentifierTest()
        {
            var target = CreateManager();
            await InitMdnRecords();
            Assert.Equal(61, await target.Count());
            Assert.NotNull(await target.Get("9C2458C2370E2C00E2E8701EE3064B6B"));
        }

        /// <summary>
        ///A test for Update
        ///</summary>
        [Fact]
        public async Task UpdateStatusTest()
        {
            var target = CreateManager();
            await InitMdnRecords();
            var mdn = await target.Get("9C2458C2370E2C00E2E8701EE3064B6B");
            Assert.Equal(MdnStatus.Started, mdn.Status);

            mdn.Status = MdnStatus.Processed;
            mdn.Id = 0;
            await target.Update(mdn);
            mdn = await target.Get("0335BF2715F5607DE9FC5BF249BEF7F9");
            Assert.Equal(MdnStatus.Processed, mdn.Status);
            mdn.Status = MdnStatus.Dispatched;
            mdn.Id = 0;
            await target.Update(mdn);
            mdn = await target.Get("543AE91DFFDE40754BCB0A11CEEED059");
            Assert.Equal(MdnStatus.Dispatched, mdn.Status);

        }

        /// <summary>
        ///A test for Update
        ///</summary>
        [Fact]
        public async Task UpdateDispatchedTest()
        {
            var target = CreateManager();
            await InitMdnRecords();
            var mdn = await target.Get("9C2458C2370E2C00E2E8701EE3064B6B");
            Assert.Equal(MdnStatus.Started, mdn.Status);

            //
            // Skip processed step is allowed.
            // An external timer will move to Timed out
            //
            mdn.Status = MdnStatus.Dispatched;
            mdn.Id = 0;
            await target.Update(mdn);
            mdn = await target.Get("543AE91DFFDE40754BCB0A11CEEED059");
            Assert.Equal(MdnStatus.Dispatched, mdn.Status);

        }

        /// <summary>
        ///A test for Update
        ///</summary>
        [Fact]
        public async Task UpdateTimeoutTest()
        {
            var target = CreateManager();
            await InitMdnRecords();
            var mdn = await target.Get("9C2458C2370E2C00E2E8701EE3064B6B");
            Assert.Equal(MdnStatus.Started, mdn.Status);

            //
            // Skip processed step is allowed.
            // An external timer will move to Timed out
            //

            await target.TimeOut(mdn);
            mdn = await target.Get("68167934227A8EBF247E6AC345CC02D1");
            Assert.Equal(MdnStatus.TimedOut, mdn.Status);
        }

        /// <summary>
        ///A test for Expired Mdn Processed
        ///</summary>
        [Fact]
        public async Task GetProcessExpiredTest()
        {
            var target = CreateManager();
            await InitMdnRecords();

            //timespan and max records set
            var mdns = await target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 10);
            Assert.Equal(10, mdns.Count);

            //timespan and max records set
            mdns = await target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 5);
            Assert.Equal(5, mdns.Count);

            //timespan and max records set
            mdns = await target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 40);
            
            Assert.Equal(20, mdns.Count);

            //timespan and max records set
            mdns = await target.GetExpiredProcessed(TimeSpan.FromMinutes(11), 40);
            Assert.Equal(0, mdns.Count);

            //default maxrecords set
            mdns = await target.GetExpiredProcessed(TimeSpan.FromMinutes(10));
            Assert.Equal(10, mdns.Count);

            //default expieredLimit and maxResults
            mdns = await target.GetExpiredProcessed();
            Assert.Equal(10, mdns.Count);
        }

        /// <summary>
        ///A test for Expired Mdn Processed
        ///</summary>
        [Fact]
        public async Task ProcessingProcessExpiredTest()
        {
            var target = CreateManager();
            await InitMdnRecords();

            //timespan and max records set
            var mdns = await target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 2);
            Assert.Equal(2, mdns.Count);
            await target.TimeOut(mdns);
            mdns = await target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 20);
            Assert.Equal(18, mdns.Count);
            await target.TimeOut(mdns);
            mdns = await target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 20);
            Assert.Empty(mdns);
        }


        /// <summary>
        ///A test for expired Mdn Dispatched
        ///</summary>
        [Fact]
        public async Task GetDispatchedExpiredTest()
        {
            var target = CreateManager();
            await InitMdnRecords();

            //timespan and max records set
            var mdns = await target.GetExpiredDispatched(TimeSpan.FromMinutes(10), 20);
            Assert.Equal(10, mdns.Count);

            //timespan and max records set
            mdns = await target.GetExpiredDispatched(TimeSpan.FromMinutes(10), 10);
            Assert.Equal(10, mdns.Count);

            //timespan and max records set
            //Nothing this old yet.
            mdns = await target.GetExpiredDispatched(TimeSpan.FromMinutes(11), 10);
            Assert.Equal(0, mdns.Count);

            //timespan and max records set
            mdns = await target.GetExpiredDispatched(TimeSpan.FromMinutes(10), 5);
            Assert.Equal(5, mdns.Count);

            //default expiredLimit (10 minutes) and maxResults (10)
            mdns = await target.GetExpiredDispatched();
            Assert.Equal(10, mdns.Count);
        }

        /// <summary>
        ///A test for Expired Mdn Dispatched
        ///</summary>
        [Fact]
        public async Task ProcessingDispatchedExpiredTest()
        {
            var target = CreateManager();
            await InitMdnRecords();

            //timespan and max records set
            var mdns = await target.GetExpiredDispatched(TimeSpan.FromMinutes(10), 2);
            Assert.Equal(2, mdns.Count);

            await target.TimeOut(mdns);

            mdns = await target.GetExpiredDispatched(TimeSpan.FromMinutes(10), 20);
            Assert.Equal(8, mdns.Count);

            await target.TimeOut(mdns);

            mdns = await target.GetExpiredDispatched(TimeSpan.FromMinutes(10), 10);
            Assert.Equal(0, mdns.Count);
        }

        /// <summary>
        ///A test for expired Mdn Dispatched Timer
        ///</summary>
        [Fact]
        public async Task GetTimeoutTest()
        {
            var target = CreateManager();
            await InitMdnRecords();

            //This would run often enough to keep the Mdns table peformant
            List<Mdn> mdns = await target.GetTimedOut();
            Assert.Empty(mdns);

            mdns = await target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 2);
            Assert.Equal(2, mdns.Count);

            await target.TimeOut(mdns);

            mdns = await target.GetTimedOut();
            Assert.Equal(2, mdns.Count);

            await target.RemoveTimedOut(TimeSpan.FromSeconds(1), 100);
            mdns = await target.GetTimedOut();
            Assert.Empty(mdns);

            //
            //Two separate updates
            //

            //Update thread one
            mdns = await target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 2);
            Assert.Equal(2, mdns.Count);

            await target.TimeOut(mdns);

            //update thread two
            mdns = await target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 2);
            Assert.Equal(2, mdns.Count);

            await target.TimeOut(mdns);

            mdns = await target.GetTimedOut();
            Assert.Equal(4, mdns.Count);

            await target.RemoveTimedOut(TimeSpan.FromSeconds(1), 100);
            mdns = await target.GetTimedOut();
            Assert.Empty(mdns);
        }

        /// <summary>
        ///A test for expired Mdn Dispatched Timer
        ///</summary>
        [Fact]
        public async Task CleanProcessedAndDispatched()
        {
            var target = CreateManager();
            await InitMdnRecords();

            //
            // Ensure all test data is procesed or dispatched 
            //
            {
                var mdns = await target.GetExpiredProcessed(TimeSpan.FromMinutes(0), 100);
                foreach (var mdn in mdns)
                {
                    mdn.Status = MdnStatus.Processed;
                    mdn.Id = 0;
                    await target.Update(mdn);
                }

                mdns = await target.GetExpiredDispatched(TimeSpan.FromMinutes(0), 100);
                foreach (var mdn in mdns)
                {
                    mdn.Status = MdnStatus.Dispatched;
                    mdn.Id = 0;
                    await target.Update(mdn);
                }
            }
            Assert.Empty((await target.GetExpiredProcessed(TimeSpan.FromMinutes(0), 100)));
            Assert.Empty((await target.GetExpiredDispatched(TimeSpan.FromMinutes(0), 100)));

            System.Threading.Thread.Sleep(1000);
            await target.RemoveDispositions(TimeSpan.FromSeconds(.001), 100);
            Assert.Equal(0, await target.Count());
        }

        /// <summary>
        ///A test for expired Mdn Dispatched Timer
        ///</summary>
        [Fact]
        public async Task DuplicateMdnTest()
        {
            var target = CreateManager();
            await InitMdnRecords();

            string messageId = Guid.NewGuid().ToString();
            Mdn mdn = BuildMdn("945cc145-431c-4119-a8c6-7f557e52fd7d", "Name1@nhind.hsgincubator.com", "Name1@domain1.test.com", "To dispatch or not dispatch", "pRocessed");

            //Record first processed.
            await target.Update(new Mdn[] { mdn });

            mdn.Id = 0;
            //Throw duplicate processed
            Assert.Equal(ConfigStoreError.DuplicateProcessedMdn, 
                Assert.ThrowsAsync<ConfigStoreException>(async () =>
                    await target.Update(mdn)).Result.Error);

            mdn.Id = 0;
            //Record first dispatched.
            mdn.Status = "disPatched";
            await target.Update(mdn);

            mdn.Id = 0;
            //Throw duplicate dispatched
            Assert.Equal(ConfigStoreError.DuplicateDispatchedMdn, 
                Assert.ThrowsAsync<ConfigStoreException>(async () =>
                    await target.Update(mdn)).Result.Error);

            mdn = BuildMdn(Guid.NewGuid().ToString(), "Name1@nhind.hsgincubator.com", "FailedTest@domain1.test.com", "To dispatch or not dispatch", "fAiled");
            await target.Start(new Mdn[] { mdn });

            Assert.Equal(ConfigStoreError.DuplicateFailedMdn, 
                Assert.ThrowsAsync<ConfigStoreException>(async () =>
                    await target.Update(mdn)).Result.Error);
        }

        /// <summary>
        ///A test for expired Mdn Dispatched Timer
        ///</summary>
        //[Fact ] May want this check to go into the timeout job.
        public async Task MissingAggregateMdnTest()
        {
            var target = CreateManager();
            await InitMdnRecords();

            string messageId = Guid.NewGuid().ToString();
            Mdn mdn = BuildMdn("945cc145-431c-4119-a8c6-7f557e52fd7d", "Name1@nhind.hsgincubator.com",
                               "Missing@domain1.test.com", "To dispatch or not dispatch", "pRocessed");

            Assert.Equal(ConfigStoreError.MdnUncorrelated, Assert.ThrowsAsync<ConfigStoreException>(async () => await target.Update(new Mdn[] { mdn })).GetAwaiter().GetResult().Error);
        }
    }
}
