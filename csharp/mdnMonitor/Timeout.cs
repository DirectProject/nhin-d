using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Health.Direct.Common.Extensions;
using Health.Direct.Config.Store;
using Health.Direct.Config.Store.Entity;
using Microsoft.Extensions.Logging;
using Quartz;

namespace Health.Direct.MdnMonitor;

///<summary>
/// An object holding timeout conditions and execution code to act on those conditions.
///</summary>
public abstract class Timeout<T>
{
    protected ILogger<T> Logger;

    /// <summary>
    /// Base for Quartz.Net Cleanup jobs.
    /// </summary>
    /// <param name="logger"></param>
    protected Timeout(ILogger<T> logger)
    {
        Logger = logger;
    }

    /// <summary>
    /// Reference <see cref="MdnManager"/> for access to data store.
    /// </summary>
    protected MdnManager MdnManager { get; set; }


    /// <summary>
    /// Config Store 
    /// Configured in <c>Load</c>
    /// </summary>
    protected ConfigStore Store { get; private set; }


    /// <summary>
    /// Get queued mdns that are considered expired based on type of <c>Timeout</c>
    /// </summary>
    /// <param name="settings"></param>
    /// <returns></returns>
    protected abstract Task<IList<Mdn>> ExpiredMdns(TimeoutSettings settings);

    /// <summary>
    /// Generate unique mime file name.
    /// </summary>
    /// <returns></returns>
    protected string UniqueFileName()
    {
        return StringExtensions.UniqueString() + ".eml";
    }

    /// <summary>
    /// Load application settings and job settings
    /// </summary>
    /// <param name="context"></param>
    /// <returns></returns>
    protected TimeoutSettings Load(IJobExecutionContext context)
    {
        try
        {
            var settings = new TimeoutSettings(context);
            Store = new ConfigStore(settings.ConnectionString, settings.QueryTimeout);

            MdnManager = new MdnManager(new ConfigStore(settings.ConnectionString));

            return settings;
        }
        catch (Exception e)
        {
            Logger.LogError(e, "Failed to load Cleanup application settings");
            var je = new JobExecutionException(e);
            je.UnscheduleAllTriggers = true;
            throw je;
        }
    }
}
