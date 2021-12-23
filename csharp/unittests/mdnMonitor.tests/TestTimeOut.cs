/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joe Shook	    jshook@kryptiq.com

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mail.DSN;
using Health.Direct.Config.Store;
using Health.Direct.Config.Store.Tests;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using Moq;
using Quartz;
using Quartz.Impl;
using Quartz.Impl.Triggers;
using Quartz.Spi;
using Xunit;

namespace Health.Direct.MdnMonitor.MdnMonitor.Tests
{
    public class TestTimeOut : ConfigStoreTestBase
    {
        const string PickupFolder = @"c:\inetpub\mailroot\testPickup";

        static TestTimeOut()
        {
            if (!Directory.Exists(PickupFolder))
            {
                Directory.CreateDirectory(PickupFolder);
            }
        }

        public static IConfiguration InitConfiguration()
        {
            var config = new ConfigurationBuilder()
                .AddJsonFile("appsettings.json").Build();
            return config;
        }

        [Fact]
        public async Task TestProcessedTimeOutToDSNFail()
        {
            //
            // Sample data
            //
            var target = CreateManager();
            await InitMdnRecords();
            CleanMessages(PickupFolder);
            
            //timespan and max records set
            var mdns = await target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 40);
            Assert.Equal(20, mdns.Count());


            MdnProcessedTimeout processedTimeout = new MdnProcessedTimeout(new Mock<ILogger<MdnProcessedTimeout>>().Object);

            //Execute unprocessed records over 11 minutes old.
            IJobExecutionContext context = CreateProcessedJobExecutionContext(11, 10);
            await processedTimeout.Execute(context); 

            //Nothing was processed 
            mdns = await target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 40);
            Assert.Equal(20, mdns.Count());

            //Execute unprocessed records over 10 minutes old.
            context = CreateProcessedJobExecutionContext(10, 10);
            await processedTimeout.Execute(context); 

            //10 records left
            mdns = await target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 40);
            Assert.Equal(10, mdns.Count);

            var files = Directory.GetFiles(PickupFolder);
            Assert.Equal(10, files.Count());

            //Do it again
            await processedTimeout.Execute(context);
            mdns = await target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 40);
            Assert.Empty(mdns);

            files = Directory.GetFiles(PickupFolder);
            Assert.Equal(20, files.Length);

            foreach (var file in files)
            {
                var loadedMessage = Message.Load(await File.ReadAllTextAsync(file));
                Assert.True(loadedMessage.IsDSN());
                Assert.Equal("multipart/report", loadedMessage.ParsedContentType.MediaType);
                Assert.Equal("Rejected:To dispatch or not dispatch", loadedMessage.SubjectValue);
                var dsnActual = DSNParser.Parse(loadedMessage);
                Assert.Equal(DSNStandard.DSNAction.Failed, dsnActual.PerRecipient.First().Action);
                Assert.Equal("5.4.71", dsnActual.PerRecipient.First().Status);
            }
            

        }


        [Fact]
        public async Task TestDispatchedTimeOutToDsnFail()
        {
            //
            // Sample data
            //
            MdnManager target = CreateManager();
            await InitMdnRecords();
            CleanMessages(PickupFolder);

            //timespan and max records set
            var mdns = await target.GetExpiredDispatched(TimeSpan.FromMinutes(10), 40);
            Assert.Equal(10, mdns.Count());

            MdnDispatchedTimeout dispatchedTimeout = new MdnDispatchedTimeout(new Mock<ILogger<MdnDispatchedTimeout>>().Object);

            //Execute unprocessed records over 11 minutes old.
            IJobExecutionContext context = CreateDispatchedJobExecutionContext(11, 5);
            await dispatchedTimeout.Execute(context);

            //Nothing was processed 
            mdns = await target.GetExpiredDispatched(TimeSpan.FromMinutes(10), 40);
            Assert.Equal(10, mdns.Count);

            var files = Directory.GetFiles(PickupFolder);
            Assert.Empty(files);

            //Execute unprocessed records over 10 minutes old.
            context = CreateDispatchedJobExecutionContext(10, 5);
            await dispatchedTimeout.Execute(context);
            
            //10 records left
            mdns = await target.GetExpiredDispatched(TimeSpan.FromMinutes(10), 40);
            Assert.Equal(5, mdns.Count());

            files = Directory.GetFiles(PickupFolder);
            Assert.Equal(5, files.Length);

            //Do it again
            await dispatchedTimeout.Execute(context);
            mdns = await target.GetExpiredDispatched(TimeSpan.FromMinutes(10), 40);
            Assert.Empty(mdns);

            files = Directory.GetFiles(PickupFolder);
            Assert.Equal(10, files.Length);

            foreach (var file in files)
            {
                var loadedMessage = Message.Load(await File.ReadAllTextAsync(file));
                Assert.True(loadedMessage.IsDSN());
                Assert.Equal("multipart/report", loadedMessage.ParsedContentType.MediaType);
                Assert.Equal("Rejected:To dispatch or not dispatch", loadedMessage.SubjectValue);
                var dsnActual = DSNParser.Parse(loadedMessage);
                Assert.Equal(DSNStandard.DSNAction.Failed, dsnActual.PerRecipient.First().Action);
                Assert.Equal("5.4.72", dsnActual.PerRecipient.First().Status);
            }
        }

        protected virtual IJobExecutionContext CreateProcessedJobExecutionContext(int minutes, int count)
        {
            IOperableTrigger trigger = new SimpleTriggerImpl();

            IJobExecutionContext ctx = new JobExecutionContextImpl(
                null,
                CreateFiredBundleWithTypedJobDetail(typeof(MdnProcessedTimeout), trigger),
                null);
            ctx.JobDetail.JobDataMap.Put("BulkCount", count);
            ctx.JobDetail.JobDataMap.Put("ExpiredMinutes", minutes);
            ctx.JobDetail.JobDataMap.Put("PickupFolder", PickupFolder);

            var mdnSettings = CreateMdnSettings();
            ctx.JobDetail.JobDataMap.Put(MdnSettings.ProductNameName, mdnSettings.ProductName);
            ctx.JobDetail.JobDataMap.Put(MdnSettings.ConnectionStringName, mdnSettings.ConnectionString);
            ctx.JobDetail.JobDataMap.Put(MdnSettings.QueryTimeoutName, mdnSettings.QueryTimeout);

            return ctx;
        }


        protected virtual IJobExecutionContext CreateDispatchedJobExecutionContext(int minutes, int count)
        {
            IOperableTrigger trigger = new SimpleTriggerImpl();

            IJobExecutionContext ctx = new JobExecutionContextImpl(
                null,
                CreateFiredBundleWithTypedJobDetail(typeof(MdnDispatchedTimeout), trigger),
                null);
            ctx.JobDetail.JobDataMap.Put(TimeoutSettings.BulkCountSetting, count);
            ctx.JobDetail.JobDataMap.Put(TimeoutSettings.ExpiredMinutesSetting, minutes);
            ctx.JobDetail.JobDataMap.Put(TimeoutSettings.PickupFolderSettings, PickupFolder);

            var mdnSettings = CreateMdnSettings();
            ctx.JobDetail.JobDataMap.Put(MdnSettings.ProductNameName, mdnSettings.ProductName);
            ctx.JobDetail.JobDataMap.Put(MdnSettings.ConnectionStringName, mdnSettings.ConnectionString);
            ctx.JobDetail.JobDataMap.Put(MdnSettings.QueryTimeoutName, mdnSettings.QueryTimeout);
            
            return ctx;
        }

        protected virtual MdnSettings CreateMdnSettings()
        {
            var config = InitConfiguration();
            var mdnSettings = config.GetSection("MdnSettings").Get<MdnSettings>();
            return mdnSettings;
        }


        /// <summary>
        /// Creates a simple fired bundle
        /// </summary>
        /// <param name="jobType">Type of job.</param>
        /// <param name="trigger">Trigger instance</param>
        /// <returns>Simple TriggerFiredBundle</returns>
        public static TriggerFiredBundle CreateFiredBundleWithTypedJobDetail(Type jobType, IOperableTrigger trigger)
        {
            IJobDetail jobDetail = new JobDetailImpl("jobName", "jobGroup", jobType);
            TriggerFiredBundle bundle = new TriggerFiredBundle(
                jobDetail, trigger, null, false, DateTimeOffset.UtcNow, null, null, null);
            return bundle;
        }


        private void CleanMessages(string path)
        {
            var files = Directory.GetFiles(path);
            foreach (var file in files)
            {
                File.Delete(file);
            }
        }
    }
}
