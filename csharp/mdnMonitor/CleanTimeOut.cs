using System;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;
using Quartz;


namespace Health.Direct.MdnMonitor;

/// <summary>
/// Clean up incomplete MDNs
/// </summary>
public class CleanTimeOut : Cleanup<CleanTimeOut>, IJob
{
    /// <summary>
    /// Create CleanTimeOut Quartz.net job
    /// </summary>
    /// <param name="logger"></param>
    public CleanTimeOut(ILogger<CleanTimeOut> logger)
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
            await Store.Mdns.RemoveTimedOut(TimeSpan.FromDays(settings.Days), settings.BulkCount);
        }
        catch (Exception e)
        {
            Logger.LogError(e, "Error in job!");
            var je = new JobExecutionException(e);
            throw je;
        }
    }
}

