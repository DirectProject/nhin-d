using System;
using Quartz;

namespace Health.Direct.MdnMonitor
{
    /// <summary>
    /// Clean up completed MDNs
    /// Completed definition:
    ///     Processed but delivery confirmation not requested
    ///     Dispatched
    /// </summary>
    public class CleanDispositions : Cleanup, IJob
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
