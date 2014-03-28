using System;
using System.IO;
using System.Threading;

using Health.Direct.Common.Diagnostics;

using Xunit;

namespace Health.Direct.Common.Tests.Diagnostics
{
    public class RolloverFacts
    {
        [Fact(Skip = "Long running test used to test the file rollover.")]
        //[Fact]
        public void Rollover()
        {
            var files = Directory.GetFiles(".", "common-tests*.log");
            foreach (var file in files)
            {
                File.Delete(file);
            }

            var logger = Log.For(this);
            Assert.NotNull(logger);

            logger.Debug("The time is - {0:HH:mm:ss.fff}", DateTime.Now);

            string sentinal = DateTime.Now.ToString("yyyyMMddHHmm");
            while (DateTime.Now.ToString("yyyyMMddHHmm") == sentinal)
            {
                Thread.Sleep(1000);
                Assert.True(File.Exists(string.Format("common-tests-{0:yyyyMMdd}.log", DateTime.Now)));
                logger.Debug("The time is - {0:HH:mm:ss.fff}", DateTime.Now);
            }

            Assert.True(File.Exists(string.Format("common-tests-{0:yyyyMMdd}.000.log", DateTime.Now)));
        }
    }
}
