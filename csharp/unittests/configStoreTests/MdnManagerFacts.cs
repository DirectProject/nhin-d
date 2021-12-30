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
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using Xunit;


namespace Health.Direct.Config.Store.Tests
{

    [Collection("ManagerFacts")]
    public class MdnManagerFacts : ConfigStoreTestBase
    {
        private readonly DirectDbContext _dbContext;
        private readonly MdnManager _mdnManager;

        public MdnManagerFacts()
        {
            _dbContext = CreateConfigDatabase();
            _dbContext.Database.EnsureCreated();
            _mdnManager = new MdnManager(_dbContext);
        }


        /// <summary>
        ///A test for Add
        ///</summary>
        [SkippableFact]
        public async Task AddTest()
        {
            Skip.IfNot(_dbContext.Database.IsInMemory());
            Skip.IfNot(_dbContext.Database.IsSqlServer() || _dbContext.Database.IsNpgsql());

            Assert.Equal(0, await _mdnManager.Count());
            string messageId = Guid.NewGuid().ToString();
            var mdn = BuildMdn(messageId, "Name1@nhind.hsgincubator.com", "Name1@domain1.test.com", "To dispatch or not dispatch", MdnStatus.Started);

            await _mdnManager.Start(new[] { mdn });
            mdn = await _mdnManager.Get(mdn.MdnIdentifier);
            Assert.Equal(MdnStatus.Started, mdn.Status);
            Assert.Equal("To dispatch or not dispatch", mdn.SubjectValue);
        }

        /// <summary>
        ///A test for duplicate key violation.
        ///</summary>
        [SkippableFact ]
        public async Task AddExceptionTest()
        {
            Skip.IfNot(_dbContext.Database.IsSqlServer() || _dbContext.Database.IsNpgsql());

            await InitMdnRecords(_dbContext);
            string messageId = Guid.NewGuid().ToString();
            var mdn = BuildMdn("945cc145-431c-4119-a8c6-7f557e52fd7d", "Name1@nhind.hsgincubator.com", "Name1@domain1.test.com", "To dispatch or not dispatch", MdnStatus.Started);
            await Assert.ThrowsAsync<DbUpdateException>(async () => 
                    await _mdnManager
                        .Start(new Mdn[] { mdn }));
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [SkippableFact ]
        public async Task GetByMdnIdentifierTest()
        {
            Skip.IfNot(_dbContext.Database.IsSqlServer() || _dbContext.Database.IsNpgsql());

            await InitMdnRecords(_dbContext);
            Assert.Equal(61, await _mdnManager.Count());
            Assert.NotNull(await _mdnManager.Get("9C2458C2370E2C00E2E8701EE3064B6B"));
        }

        /// <summary>
        ///A test for Update
        ///</summary>
        [SkippableFact ]
        public async Task UpdateStatusTest()
        {
            Skip.IfNot(_dbContext.Database.IsSqlServer() || _dbContext.Database.IsNpgsql());

            await InitMdnRecords(_dbContext);
            var mdn = await _mdnManager.Get("9C2458C2370E2C00E2E8701EE3064B6B");
            Assert.Equal(MdnStatus.Started, mdn.Status);

            mdn.Status = MdnStatus.Processed;
            mdn.Id = 0;
            await _mdnManager.Update(mdn);
            mdn = await _mdnManager.Get("0335BF2715F5607DE9FC5BF249BEF7F9");
            Assert.Equal(MdnStatus.Processed, mdn.Status);
            mdn.Status = MdnStatus.Dispatched;
            mdn.Id = 0;
            await _mdnManager.Update(mdn);
            mdn = await _mdnManager.Get("543AE91DFFDE40754BCB0A11CEEED059");
            Assert.Equal(MdnStatus.Dispatched, mdn.Status);

        }

        /// <summary>
        ///A test for Update
        ///</summary>
        [SkippableFact ]
        public async Task UpdateDispatchedTest()
        {
            Skip.IfNot(_dbContext.Database.IsSqlServer() || _dbContext.Database.IsNpgsql());

            await InitMdnRecords(_dbContext);
            var mdn = await _mdnManager.Get("9C2458C2370E2C00E2E8701EE3064B6B");
            Assert.Equal(MdnStatus.Started, mdn.Status);

            //
            // Skip processed step is allowed.
            // An external timer will move to Timed out
            //
            mdn.Status = MdnStatus.Dispatched;
            mdn.Id = 0;
            await _mdnManager.Update(mdn);
            mdn = await _mdnManager.Get("543AE91DFFDE40754BCB0A11CEEED059");
            Assert.Equal(MdnStatus.Dispatched, mdn.Status);

        }

        /// <summary>
        ///A test for Update
        ///</summary>
        [SkippableFact ]
        public async Task UpdateTimeoutTest()
        {
            Skip.IfNot(_dbContext.Database.IsSqlServer() || _dbContext.Database.IsNpgsql());

            await InitMdnRecords(_dbContext);
            var mdn = await _mdnManager.Get("9C2458C2370E2C00E2E8701EE3064B6B");
            Assert.Equal(MdnStatus.Started, mdn.Status);

            //
            // Skip processed step is allowed.
            // An external timer will move to Timed out
            //

            await _mdnManager.TimeOut(mdn);
            mdn = await _mdnManager.Get("68167934227A8EBF247E6AC345CC02D1");
            Assert.Equal(MdnStatus.TimedOut, mdn.Status);
        }

        /// <summary>
        ///A test for Expired Mdn Processed
        ///</summary>
        [SkippableFact ]
        public async Task GetProcessExpiredTest()
        {
            Skip.IfNot(_dbContext.Database.IsSqlServer() || _dbContext.Database.IsNpgsql());

            await InitMdnRecords(_dbContext);

            //timespan and max records set
            var mdns = await _mdnManager.GetExpiredProcessed(TimeSpan.FromMinutes(10), 10);
            Assert.Equal(10, mdns.Count);

            //timespan and max records set
            mdns = await _mdnManager.GetExpiredProcessed(TimeSpan.FromMinutes(10), 5);
            Assert.Equal(5, mdns.Count);

            //timespan and max records set
            mdns = await _mdnManager.GetExpiredProcessed(TimeSpan.FromMinutes(10), 40);
            
            Assert.Equal(20, mdns.Count);

            //timespan and max records set
            mdns = await _mdnManager.GetExpiredProcessed(TimeSpan.FromMinutes(11), 40);
            Assert.Empty(mdns);

            //default max records set
            mdns = await _mdnManager.GetExpiredProcessed(TimeSpan.FromMinutes(10));
            Assert.Equal(10, mdns.Count);

            //default expiredLimit and maxResults
            mdns = await _mdnManager.GetExpiredProcessed();
            Assert.Equal(10, mdns.Count);
        }

        /// <summary>
        ///A test for Expired Mdn Processed
        ///</summary>
        [SkippableFact ]
        public async Task ProcessingProcessExpiredTest()
        {
            Skip.IfNot(_dbContext.Database.IsSqlServer() || _dbContext.Database.IsNpgsql());

            await InitMdnRecords(_dbContext);

            //timespan and max records set
            var mdns = await _mdnManager.GetExpiredProcessed(TimeSpan.FromMinutes(10), 2);
            Assert.Equal(2, mdns.Count);
            await _mdnManager.TimeOut(mdns);
            mdns = await _mdnManager.GetExpiredProcessed(TimeSpan.FromMinutes(10), 20);
            Assert.Equal(18, mdns.Count);
            await _mdnManager.TimeOut(mdns);
            mdns = await _mdnManager.GetExpiredProcessed(TimeSpan.FromMinutes(10), 20);
            Assert.Empty(mdns);
        }


        /// <summary>
        ///A test for expired Mdn Dispatched
        ///</summary>
        [SkippableFact ]
        public async Task GetDispatchedExpiredTest()
        {
            Skip.IfNot(_dbContext.Database.IsSqlServer() || _dbContext.Database.IsNpgsql());

            await InitMdnRecords(_dbContext);

            //timespan and max records set
            var mdns = await _mdnManager.GetExpiredDispatched(TimeSpan.FromMinutes(10), 20);
            Assert.Equal(10, mdns.Count);

            //timespan and max records set
            mdns = await _mdnManager.GetExpiredDispatched(TimeSpan.FromMinutes(10), 10);
            Assert.Equal(10, mdns.Count);

            //timespan and max records set
            //Nothing this old yet.
            mdns = await _mdnManager.GetExpiredDispatched(TimeSpan.FromMinutes(11), 10);
            Assert.Empty(mdns);

            //timespan and max records set
            mdns = await _mdnManager.GetExpiredDispatched(TimeSpan.FromMinutes(10), 5);
            Assert.Equal(5, mdns.Count);

            //default expiredLimit (10 minutes) and maxResults (10)
            mdns = await _mdnManager.GetExpiredDispatched();
            Assert.Equal(10, mdns.Count);
        }

        /// <summary>
        ///A test for Expired Mdn Dispatched
        ///</summary>
        [SkippableFact ]
        public async Task ProcessingDispatchedExpiredTest()
        {
            Skip.IfNot(_dbContext.Database.IsSqlServer() || _dbContext.Database.IsNpgsql());

            await InitMdnRecords(_dbContext);

            //timespan and max records set
            var mdns = await _mdnManager.GetExpiredDispatched(TimeSpan.FromMinutes(10), 2);
            Assert.Equal(2, mdns.Count);

            await _mdnManager.TimeOut(mdns);

            mdns = await _mdnManager.GetExpiredDispatched(TimeSpan.FromMinutes(10), 20);
            Assert.Equal(8, mdns.Count);

            await _mdnManager.TimeOut(mdns);

            mdns = await _mdnManager.GetExpiredDispatched(TimeSpan.FromMinutes(10), 10);
            Assert.Empty(mdns);
        }

        /// <summary>
        ///A test for expired Mdn Dispatched Timer
        ///</summary>
        [SkippableFact ]
        public async Task GetTimeoutTest()
        {
            Skip.IfNot(_dbContext.Database.IsSqlServer() || _dbContext.Database.IsNpgsql());

            await InitMdnRecords(_dbContext);

            //This would run often enough to keep the Mdns table peformant
            var mdns = await _mdnManager.GetTimedOut();
            Assert.Empty(mdns);

            mdns = await _mdnManager.GetExpiredProcessed(TimeSpan.FromMinutes(10), 2);
            Assert.Equal(2, mdns.Count);

            await _mdnManager.TimeOut(mdns);

            mdns = await _mdnManager.GetTimedOut();
            Assert.Equal(2, mdns.Count);

            await _mdnManager.RemoveTimedOut(TimeSpan.FromSeconds(1), 100);
            mdns = await _mdnManager.GetTimedOut();
            Assert.Empty(mdns);

            //
            //Two separate updates
            //

            //Update thread one
            mdns = await _mdnManager.GetExpiredProcessed(TimeSpan.FromMinutes(10), 2);
            Assert.Equal(2, mdns.Count);

            await _mdnManager.TimeOut(mdns);

            //update thread two
            mdns = await _mdnManager.GetExpiredProcessed(TimeSpan.FromMinutes(10), 2);
            Assert.Equal(2, mdns.Count);

            await _mdnManager.TimeOut(mdns);

            mdns = await _mdnManager.GetTimedOut();
            Assert.Equal(4, mdns.Count);

            await _mdnManager.RemoveTimedOut(TimeSpan.FromSeconds(1), 100);
            mdns = await _mdnManager.GetTimedOut();
            Assert.Empty(mdns);
        }

        /// <summary>
        ///A test for expired Mdn Dispatched Timer
        ///</summary>
        [SkippableFact ]
        public async Task CleanProcessedAndDispatched()
        {
            Skip.IfNot(_dbContext.Database.IsSqlServer() || _dbContext.Database.IsNpgsql());

            await InitMdnRecords(_dbContext);

            //
            // Ensure all test data is processed or dispatched 
            //
            {
                var mdns = await _mdnManager.GetExpiredProcessed(TimeSpan.FromMinutes(0), 100);
                foreach (var mdn in mdns)
                {
                    mdn.Status = MdnStatus.Processed;
                    mdn.Id = 0;
                    await _mdnManager.Update(mdn);
                }

                mdns = await _mdnManager.GetExpiredDispatched(TimeSpan.FromMinutes(0), 100);
                foreach (var mdn in mdns)
                {
                    mdn.Status = MdnStatus.Dispatched;
                    mdn.Id = 0;
                    await _mdnManager.Update(mdn);
                }
            }
            Assert.Empty((await _mdnManager.GetExpiredProcessed(TimeSpan.FromMinutes(0), 100)));
            Assert.Empty((await _mdnManager.GetExpiredDispatched(TimeSpan.FromMinutes(0), 100)));

            System.Threading.Thread.Sleep(1000);
            await _mdnManager.RemoveDispositions(TimeSpan.FromSeconds(.001), 100);
            Assert.Equal(0, await _mdnManager.Count());
        }

        /// <summary>
        ///A test for expired Mdn Dispatched Timer
        ///</summary>
        [SkippableFact ]
        public async Task DuplicateMdnTest()
        {
            Skip.IfNot(_dbContext.Database.IsSqlServer() || _dbContext.Database.IsNpgsql());

            string messageId = Guid.NewGuid().ToString();
            Mdn mdn = BuildMdn("945cc145-431c-4119-a8c6-7f557e52fd7d", "Name1@nhind.hsgincubator.com", "Name1@domain1.test.com", "To dispatch or not dispatch", "pRocessed");

            //Record first processed.
            await _mdnManager.Update(new[] { mdn });

            mdn.Id = 0;
            //Throw duplicate processed
            Assert.Equal(ConfigStoreError.DuplicateProcessedMdn, 
                Assert.ThrowsAsync<ConfigStoreException>(async () =>
                    await _mdnManager.Update(mdn)).Result.Error);

            mdn.Id = 0;
            //Record first dispatched.
            mdn.Status = "disPatched";
            await _mdnManager.Update(mdn);

            mdn.Id = 0;
            //Throw duplicate dispatched
            Assert.Equal(ConfigStoreError.DuplicateDispatchedMdn, 
                Assert.ThrowsAsync<ConfigStoreException>(async () =>
                    await _mdnManager.Update(mdn)).Result.Error);

            _dbContext.ChangeTracker.Clear();
            mdn = BuildMdn(Guid.NewGuid().ToString(), "Name1@nhind.hsgincubator.com", "FailedTest@domain1.test.com", "To dispatch or not dispatch", "fAiled");
            await _mdnManager.Start(new[] { mdn });

            Assert.Equal(ConfigStoreError.DuplicateFailedMdn, 
                Assert.ThrowsAsync<ConfigStoreException>(async () =>
                    await _mdnManager.Update(mdn)).Result.Error);
        }

        /// <summary>
        ///A test for expired Mdn Dispatched Timer
        ///</summary>
        //[Fact ] May want this check to go into the timeout job.
        public async Task MissingAggregateMdnTest()
        {
            
            await InitMdnRecords(_dbContext);

            string messageId = Guid.NewGuid().ToString();
            Mdn mdn = BuildMdn("945cc145-431c-4119-a8c6-7f557e52fd7d", "Name1@nhind.hsgincubator.com",
                               "Missing@domain1.test.com", "To dispatch or not dispatch", "pRocessed");

            Assert.Equal(ConfigStoreError.MdnUncorrelated, Assert.ThrowsAsync<ConfigStoreException>(async () => await _mdnManager.Update(new Mdn[] { mdn })).GetAwaiter().GetResult().Error);
        }
    }
}
