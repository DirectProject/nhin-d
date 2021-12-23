using System;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using Quartz;

namespace Health.Direct.MdnMonitor
{
    /// <summary>
    /// Clean up completed MDNs
    /// Completed definition:
    ///     Processed but delivery confirmation not requested
    ///     Dispatched
    /// </summary>
    public class CleanDispositions : Cleanup<CleanDispositions>, IJob
    {
        private ILogger<CleanDispositions> _logger;

        /// <summary>
        /// Create CleanDisposition Quartz.net job
        /// </summary>
        /// <param name="logger"></param>
        public CleanDispositions(ILogger<CleanDispositions> logger)
            : base(logger) { }

        /// <summary>
        /// Entry point called when trigger fires.
        /// </summary>
        /// <param name="context"></param>
        public async Task Execute(IJobExecutionContext context)
        {
            var settings = Load(context);

            try
            {
                await Store.Mdns.RemoveDispositions(TimeSpan.FromDays(settings.Days), settings.BulkCount);
            }
            catch (Exception e)
            {
                _logger.LogError(e, "Error in job!");
                var je = new JobExecutionException(e);
                throw je;
            }
        }
    }
}
