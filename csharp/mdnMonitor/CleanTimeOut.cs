using System;
using Quartz;


namespace Health.Direct.MdnMonitor
{
    public class CleanTimeOut : CleanupBase, IJob
    {
        public override void Execute(JobExecutionContext context)
        {
            Load();

            try
            {
                Console.WriteLine("---{0} executing.[{1}]", context.JobDetail.FullName, DateTime.Now.ToString("r"));
                         
                Store.Mdns.RemoveTimedOut();
            }
            catch(Exception e)
            {
                Logger.Error("--- Error in job!", e);
                var je = new JobExecutionException(e);
                throw je;
            }
        }
    }
}
