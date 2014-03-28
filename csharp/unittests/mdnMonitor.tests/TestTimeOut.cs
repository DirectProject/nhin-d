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
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mail.DSN;
using Health.Direct.Config.Store;
using Health.Direct.Config.Store.Tests;
using Quartz;
using Quartz.Spi;
using Xunit;

namespace Health.Direct.MdnMonitor.MdnMonitor.Tests
{
    public class TestTimeOut : ConfigStoreTestBase
    {
        const string PickupFolder = @"c:\inetpub\mailroot\testPickup";

        [Fact]
        public void TestProcessedTimeOutToDSNFail()
        {
            //
            // Sample data
            //
            MdnManager target = CreateManager();
            InitMdnRecords();
            CleanMessages(PickupFolder);
            
            //timespan and max records set
            var mdns = target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 40);
            Assert.Equal(20, mdns.Count());


            MdnProcessedTimeout processedTimeout = new MdnProcessedTimeout();

            //Execute unprocessed records over 11 minutes old.
            JobExecutionContext context = CreateProcessedJobExecutionContext(11, 10);
            processedTimeout.Execute(context); 

            //Nothing was processed 
            mdns = target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 40);
            Assert.Equal(20, mdns.Count());

            //Execute unprocessed records over 10 minutes old.
            context = CreateProcessedJobExecutionContext(10, 10);
            processedTimeout.Execute(context); 

            //10 records left
            mdns = target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 40);
            Assert.Equal(10, mdns.Count());

            var files = Directory.GetFiles(PickupFolder);
            Assert.Equal(10, files.Count());

            //Do it again
            processedTimeout.Execute(context);
            mdns = target.GetExpiredProcessed(TimeSpan.FromMinutes(10), 40);
            Assert.Equal(0, mdns.Count());

            files = Directory.GetFiles(PickupFolder);
            Assert.Equal(20, files.Count());

            foreach (var file in files)
            {
                Message loadedMessage = Message.Load(File.ReadAllText(file));
                Assert.True(loadedMessage.IsDSN());
                Assert.Equal("multipart/report", loadedMessage.ParsedContentType.MediaType);
                Assert.Equal("Rejected:To dispatch or not dispatch", loadedMessage.SubjectValue);
                var dsnActual = DSNParser.Parse(loadedMessage);
                Assert.Equal(DSNStandard.DSNAction.Failed, dsnActual.PerRecipient.First().Action);
                Assert.Equal("5.4.71", dsnActual.PerRecipient.First().Status);
            }
            

        }


        [Fact]
        public void TestDispatchedTimeOutToDSNFail()
        {
            //
            // Sample data
            //
            MdnManager target = CreateManager();
            InitMdnRecords();
            CleanMessages(PickupFolder);

            //timespan and max records set
            var mdns = target.GetExpiredDispatched(TimeSpan.FromMinutes(10), 40);
            Assert.Equal(10, mdns.Count());

            MdnDispatchedTimeout dispatchedTimeout = new MdnDispatchedTimeout();

            //Execute unprocessed records over 11 minutes old.
            JobExecutionContext context = CreateDispatchedJobExecutionContext(11, 5);
            dispatchedTimeout.Execute(context);

            //Nothing was processed 
            mdns = target.GetExpiredDispatched(TimeSpan.FromMinutes(10), 40);
            Assert.Equal(10, mdns.Count());

            var files = Directory.GetFiles(PickupFolder);
            Assert.Equal(0, files.Count());

            //Execute unprocessed records over 10 minutes old.
            context = CreateDispatchedJobExecutionContext(10, 5);
            dispatchedTimeout.Execute(context);
            
            //10 records left
            mdns = target.GetExpiredDispatched(TimeSpan.FromMinutes(10), 40);
            Assert.Equal(5, mdns.Count());

            files = Directory.GetFiles(PickupFolder);
            Assert.Equal(5, files.Count());

            //Do it again
            dispatchedTimeout.Execute(context);
            mdns = target.GetExpiredDispatched(TimeSpan.FromMinutes(10), 40);
            Assert.Equal(0, mdns.Count());

            files = Directory.GetFiles(PickupFolder);
            Assert.Equal(10, files.Count());

            foreach (var file in files)
            {
                Message loadedMessage = Message.Load(File.ReadAllText(file));
                Assert.True(loadedMessage.IsDSN());
                Assert.Equal("multipart/report", loadedMessage.ParsedContentType.MediaType);
                Assert.Equal("Rejected:To dispatch or not dispatch", loadedMessage.SubjectValue);
                var dsnActual = DSNParser.Parse(loadedMessage);
                Assert.Equal(DSNStandard.DSNAction.Failed, dsnActual.PerRecipient.First().Action);
                Assert.Equal("5.4.72", dsnActual.PerRecipient.First().Status);
            }
        }


        protected virtual JobExecutionContext CreateProcessedJobExecutionContext(int minutes, int count)
        {
            SimpleTrigger trigger = new SimpleTrigger();
            
            JobExecutionContext ctx = new JobExecutionContext(
                null,
                CreateFiredBundleWithTypedJobDetail(typeof(MdnProcessedTimeout), trigger),
                null);
            ctx.JobDetail.JobDataMap.Put("BulkCount", count);
            ctx.JobDetail.JobDataMap.Put("ExpiredMinutes", minutes);
            ctx.JobDetail.JobDataMap.Put("PickupFolder", PickupFolder);
            return ctx;
        }


        protected virtual JobExecutionContext CreateDispatchedJobExecutionContext(int minutes, int count)
        {
            SimpleTrigger trigger = new SimpleTrigger();
            
            JobExecutionContext ctx = new JobExecutionContext(
                null,
                CreateFiredBundleWithTypedJobDetail(typeof(MdnDispatchedTimeout), trigger),
                null);
            ctx.JobDetail.JobDataMap.Put("BulkCount", count);
            ctx.JobDetail.JobDataMap.Put("ExpiredMinutes", minutes);
            ctx.JobDetail.JobDataMap.Put("PickupFolder", PickupFolder);
            return ctx;
        }

        

        /// <summary>
        /// Creates a simple fired bundle
        /// </summary>
        /// <param name="jobType">Type of job.</param>
        /// <param name="trigger">Trigger instance</param>
        /// <returns>Simple TriggerFiredBundle</returns>
        public static TriggerFiredBundle CreateFiredBundleWithTypedJobDetail(Type jobType, Trigger trigger)
        {
            JobDetail jobDetail = new JobDetail("jobName", "jobGroup", jobType);
            TriggerFiredBundle bundle = new TriggerFiredBundle(
                jobDetail, trigger, null, false, null, null, null, null);
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
