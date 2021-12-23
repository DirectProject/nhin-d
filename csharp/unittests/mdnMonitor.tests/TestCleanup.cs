using System;
using System.Threading.Tasks;
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
    public class TestCleanup : ConfigStoreTestBase
    {
        public static IConfiguration InitConfiguration()
        {
            var config = new ConfigurationBuilder()
                .AddJsonFile("appsettings.json").Build();
            return config;
        }

        [Fact]
        public async Task TestDispatchedCleanup()
        {
            //
            // Sample data
            //
            MdnManager target = CreateManager();
            await InitOldMdnRecords();

            CleanDispositions dispositions = new CleanDispositions(new Mock<ILogger<CleanDispositions>>().Object);

            //Assert.Equal(41, await target.Count());
            
            //No records older than 10 days.
            IJobExecutionContext context = CreateCleanDispositionsIJobExecutionContext(11);
            await dispositions.Execute(context);
            Assert.Equal(91, await target.Count());

            //Should clean up 10 processed and 10 dispatched and their corresponding starts
            context = CreateCleanDispositionsIJobExecutionContext(9);
            await dispositions.Execute(context);
            Assert.Equal(51, await target.Count());

        }


        [Fact]
        public async Task TestTimeoutCleanup()
        {
            //
            // Sample data
            //
            MdnManager target = CreateManager();
            await InitOldMdnRecords();

            CleanDispositions dispositions = new CleanDispositions(new Mock<ILogger<CleanDispositions>>().Object);

            Assert.Equal(91, await target.Count());

            //No records older than 10 days.
            IJobExecutionContext context = CreateCleanTimeoutIJobExecutionContext(11);
            await dispositions.Execute(context);
            Assert.Equal(91, await target.Count());

            //Should clean up 10 dispatched timeout and 10 processed timeout and their corresponding starts
            context = CreateCleanTimeoutIJobExecutionContext(9);
            await dispositions.Execute(context);
            Assert.Equal(51, await target.Count());
        }


        protected virtual IJobExecutionContext CreateCleanDispositionsIJobExecutionContext(int days)
        {
            IOperableTrigger trigger = new SimpleTriggerImpl();

            IJobExecutionContext ctx = new JobExecutionContextImpl(
                null,
                CreateFiredBundleWithTypedJobDetail(typeof(MdnProcessedTimeout), trigger),
                null);
            
            ctx.JobDetail.JobDataMap.Put("Days", days);

            var mdnSettings = CreateMdnSettings();
            ctx.JobDetail.JobDataMap.Put(MdnSettings.ProductNameName, mdnSettings.ProductName);
            ctx.JobDetail.JobDataMap.Put(MdnSettings.ConnectionStringName, mdnSettings.ConnectionString);
            ctx.JobDetail.JobDataMap.Put(MdnSettings.QueryTimeoutName, mdnSettings.QueryTimeout);

            return ctx;
        }


        protected virtual IJobExecutionContext CreateCleanTimeoutIJobExecutionContext(int days)
        {
            IOperableTrigger trigger = new SimpleTriggerImpl();

            IJobExecutionContext ctx = new JobExecutionContextImpl(
                null,
                CreateFiredBundleWithTypedJobDetail(typeof(CleanDispositions), trigger),
                null);

            ctx.JobDetail.JobDataMap.Put("Days", days);

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
    }
}
