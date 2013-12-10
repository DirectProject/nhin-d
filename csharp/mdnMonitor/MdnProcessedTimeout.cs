using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Mail;
using System.Transactions;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mail.DSN;
using Health.Direct.Common.Mail.Notifications;
using Health.Direct.Config.Store;
using Quartz;

namespace Health.Direct.MdnMonitor
{
    /// <summary>
    /// Find expired processed MDNs, mark expired and send failed DSNs
    /// </summary>
    public class MdnProcessedTimeout : Timeout, IStatefulJob //Will limit to one job at a time.
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
                foreach (var mdn in ExpiredMdns(settings))
                {
                    using (TransactionScope scope = new TransactionScope())
                    {
                        var message = CreateNotificationMessage(mdn, settings);
                        string filePath = Path.Combine(settings.PickupFolder, UniqueFileName());
                        MDNManager.TimeOut(mdn);
                        message.Save(filePath);
                        scope.Complete();
                    }
                }
            }
            catch (Exception e)
            {
                Logger.Error("Error in job!");
                Logger.Error(e.Message);
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
        protected override IList<Mdn> ExpiredMdns(TimeoutSettings settings)
        {
            IList<Mdn> mdns;

            mdns = MDNManager.GetExpiredProcessed(settings.ExpiredMinutes, settings.BulkCount).ToList();
            if (mdns.Count() > 0)
            {
                Logger.Debug("Processing {0} expired processed mdns", mdns.Count());
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
            // If you would rather send one DSN with muliple recipients then one could write their own Job.
            //
            var notification = new DSN(perMessage, new List<DSNPerRecipient> { perRecipient });
            var sender = new MailAddress(mdn.Sender);
            var notificationMessage = new DSNMessage(sender.Address, new MailAddress("Postmaster@" + sender.Host).Address, notification);
            notificationMessage.AssignMessageID();
            notificationMessage.SubjectValue = string.Format("{0}:{1}", "Rejected", mdn.SubjectValue);
            notificationMessage.Timestamp();
            return notificationMessage;
        }

    }
}
