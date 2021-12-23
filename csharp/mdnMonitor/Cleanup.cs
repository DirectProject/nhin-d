using System;
using Health.Direct.Config.Store;
using Microsoft.Extensions.Logging;
using Quartz;

namespace Health.Direct.MdnMonitor;

/// <summary>
/// An object with execution code to clean up expired data.
/// </summary>
public abstract class Cleanup<T>
{
    protected ILogger<T> Logger;

    /// <summary>
    /// Base for Quartz.Net Cleanup jobs.
    /// </summary>
    /// <param name="logger"></param>
    protected Cleanup(ILogger<T> logger)
    {
        Logger = logger;
    }

    /// <summary>
    /// Config Store 
    /// Configured in <c>Load</c>
    /// </summary>
    public ConfigStore Store { get; private set; }


    /// <summary>
    /// Load application settings.
    /// </summary>
    protected CleanupSettings Load(IJobExecutionContext context)
    {
        try
        {
            var settings = new CleanupSettings(context);
            Store = new ConfigStore(settings.ConnectionString, settings.QueryTimeout);

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
