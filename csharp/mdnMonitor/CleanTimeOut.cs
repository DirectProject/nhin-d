using System;
using Quartz;


namespace Health.Direct.MdnMonitor
{
    /// <summary>
    /// Clean up incomplete MDNs
    /// </summary>
    public class CleanTimeOut : Cleanup, IJob
    {
        /// <summary>
        /// Entry point called when trigger fires.
        /// </summary>
        /// <param name="context"></param>
        public void Execute(JobExecutionContext context)
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
