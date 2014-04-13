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
            var settings = Load(context);


            try
            {
                Store.Mdns.RemoveTimedOut(TimeSpan.FromDays(settings.Days), settings.BulkCount);
            }
            catch(Exception e)
            {
                Logger.Error("Error in job!");
                Logger.Error(e.Message);
                var je = new JobExecutionException(e);
                throw je;
            }
        }
    }
}
