using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Mail;
using System.Threading.Tasks;
using System.Transactions;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mail.DSN;
using Health.Direct.Config.Store.Entity;
using Microsoft.Extensions.Logging;
using Quartz;

namespace Health.Direct.MdnMonitor;

/// <summary>
/// Find expired processed MDNs, mark expired and send failed DSNs
/// </summary>
[DisallowConcurrentExecution]
public class MdnProcessedTimeout : Timeout<MdnProcessedTimeout>, IJob
{
    /// <summary>
    /// Create MdnProcessedTimeout Quartz.net job
    /// </summary>
    /// <param name="logger"></param>
    public MdnProcessedTimeout(ILogger<MdnProcessedTimeout> logger)
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
            foreach (var mdn in await ExpiredMdns(settings))
            {
                var message = CreateNotificationMessage(mdn, settings);
                string filePath = Path.Combine(settings.PickupFolder, UniqueFileName());
                await MdnManager.TimeOut(mdn);
                message.Save(filePath);
            }
        }
        catch (Exception e)
        {
            Logger.LogError(e, "Error in job!");
            var je = new JobExecutionException(e);
            throw je;
        }
    }

    /// <summary>
    /// Retrieve expired records.
    ///     Records without processed notification and older than the <c>ExpiredMinutes</c>
    ///     Load a limited amount of record set by <c>BulkCount</c>
    /// </summary>
    /// <param name="settings"></param>
    /// <returns></returns>
    protected override async Task<IList<Mdn>> ExpiredMdns(TimeoutSettings settings)
    {
        IList<Mdn> mdns = await MdnManager.GetExpiredProcessed(settings.ExpiredMinutes, settings.BulkCount);

        if (mdns.Any())
        {
            Logger.LogDebug("Processing {0} expired processed MDNs", mdns.Count);
        }

        return mdns;
    }

    static DSNMessage CreateNotificationMessage(Mdn mdn, TimeoutSettings settings)
    {
        var perMessage = new DSNPerMessage(settings.ProductName, mdn.MessageId);
        var perRecipient = new DSNPerRecipient(DSNStandard.DSNAction.Failed, DSNStandard.DSNStatus.Permanent
                                               , DSNStandard.DSNStatus.NETWORK_EXPIRED_PROCESSED,
                                               MailParser.ParseMailAddress(mdn.Recipient));
        //
        // The nature of Mdn storage in config store does not result in a list of perRecipients
        // If you would rather send one DSN with multiple recipients then one could write their own Job.
        //
        var notification = new DSN(perMessage, new List<DSNPerRecipient> { perRecipient });
        var sender = new MailAddress(mdn.Sender);
        var notificationMessage = new DSNMessage(sender.Address, new MailAddress("Postmaster@" + sender.Host).Address, notification);
        notificationMessage.AssignMessageID();
        notificationMessage.SubjectValue = $"Rejected:{mdn.SubjectValue}";
        notificationMessage.Timestamp();
        return notificationMessage;
    }

}

