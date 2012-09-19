using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Quartz;

namespace Health.Direct.MdnMonitor
{
    public class CleanDispositions : CleanupBase, IJob
    {
        public override void Execute(JobExecutionContext context)
        {
            Load();

            try
            {
                Console.WriteLine("---{0} executing.[{1}]", context.JobDetail.FullName, DateTime.Now.ToString("r"));

                Store.Mdns.RemoveDispositions();
            }
            catch (Exception e)
            {
                Logger.Error("--- Error in job!", e);
                var je = new JobExecutionException(e);
                throw je;
            }
        }
    }
}
