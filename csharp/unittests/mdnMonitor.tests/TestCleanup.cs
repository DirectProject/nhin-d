using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Health.Direct.Config.Store;
using Health.Direct.Config.Store.Tests;
using Quartz;
using Quartz.Spi;
using Xunit;

namespace Health.Direct.MdnMonitor.MdnMonitor.Tests
{
    public class TestCleanup : ConfigStoreTestBase
    {
        [Fact]
        public void TestDispatchedCleanup()
        {
            //
            // Sample data
            //
            MdnManager target = CreateManager();
            InitOldMdnRecords();

            CleanDispositions dispositions = new CleanDispositions();

            //Assert.Equal(41, target.Count());
            
            //No records older than 10 days.
            JobExecutionContext context = CreateCleanDispositionsJobExecutionContext(11);
            dispositions.Execute(context);
            Assert.Equal(91, target.Count());

            //Should clean up 10 processed and 10 dispatched and their corresponding starts
            context = CreateCleanDispositionsJobExecutionContext(9);
            dispositions.Execute(context);
            Assert.Equal(51, target.Count());

        }


        [Fact]
        public void TestTimeoutCleanup()
        {
            //
            // Sample data
            //
            MdnManager target = CreateManager();
            InitOldMdnRecords();

            CleanDispositions dispositions = new CleanDispositions();

            Assert.Equal(91, target.Count());

            //No records older than 10 days.
            JobExecutionContext context = CreateCleanTimeoutJobExecutionContext(11);
            dispositions.Execute(context);
            Assert.Equal(91, target.Count());

            //Should clean up 10 dispatched timeout and 10 processed timeout and their corresponding starts
            context = CreateCleanTimeoutJobExecutionContext(9);
            dispositions.Execute(context);
            Assert.Equal(51, target.Count());
        }


        protected virtual JobExecutionContext CreateCleanDispositionsJobExecutionContext(int days)
        {
            SimpleTrigger trigger = new SimpleTrigger();

            JobExecutionContext ctx = new JobExecutionContext(
                null,
                CreateFiredBundleWithTypedJobDetail(typeof(MdnProcessedTimeout), trigger),
                null);
            ctx.JobDetail.JobDataMap.Put("Days", days);
            return ctx;
        }


        protected virtual JobExecutionContext CreateCleanTimeoutJobExecutionContext(int days)
        {
            SimpleTrigger trigger = new SimpleTrigger();

            JobExecutionContext ctx = new JobExecutionContext(
                null,
                CreateFiredBundleWithTypedJobDetail(typeof(CleanDispositions), trigger),
                null);
            ctx.JobDetail.JobDataMap.Put("Days", days);
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
    }
}
