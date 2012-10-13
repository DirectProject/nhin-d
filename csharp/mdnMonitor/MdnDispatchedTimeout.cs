using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mail.DSN;
using Health.Direct.Common.Mail.Notifications;
using Health.Direct.Config.Store;
using Quartz;

namespace Health.Direct.MdnMonitor
{
    /// <summary>
    /// Find expired dispatched MDNs, mark expired and send a failed DSNs
    /// </summary>
    public class MdnDispatchedTimeout : Timeout, IStatefulJob //Will limit to one job at a time.
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
                Console.WriteLine("---{0} executing.[{1}]", context.JobDetail.FullName, DateTime.Now.ToString("r"));

                foreach (var mdn in ExpiredMdns(settings))
                {
                    Console.WriteLine("\t dispatch: " + mdn.Id);

                    var message = CreateNotificationMessage(mdn, settings);
                    mdn.Timedout = true;

                    using (var db = Store.CreateContext())
                    {
                        db.BeginTransaction();
                        Store.Mdns.Update(db, mdn);
                        db.SubmitChanges();

                        string filePath = Path.Combine(settings.PickupFolder, UniqueFileName());
                        message.Save(filePath);

                        db.Commit();
                    }
                }
            }
            catch(Exception e)
            {
                Logger.Error("--- Error in job!", e);
                var je = new JobExecutionException(e);
                throw je;
            }
        }

        /// <summary>
        /// Retrieve expired records.
        ///     Records without dispatched notification and older than the <c>ExpiredMinutes</c>
        ///     Load a limited amount of record set by <c>BulkCount</c>
        /// </summary>
        /// <param name="settings"></param>
        /// <returns></returns>
        protected override IList<Mdn> ExpiredMdns(TimeoutSettings settings)
        {
            IList<Mdn> mdns;
            using (var db = Store.CreateReadContext())
            {
                mdns = db.Mdns.GetExpiredDispatched(settings.ExpiredMinutes, settings.BulkCount).ToList();
                if(mdns.Count() > 0)
                {
                    Logger.Debug("Processing {0} expired dispatched mdns", mdns.Count());
                }
            }
            return mdns;
        }

        static DSNMessage CreateNotificationMessage(Mdn mdn, TimeoutSettings settings)
        {
            var perMessage = new DSNPerMessage(settings.ProductName, mdn.MessageId);
            var perRecipient = new DSNPerRecipient(DSNStandard.DSNAction.Failed, DSNStandard.DSNStatus.Permanent
                                                   , DSNStandard.DSNStatus.UNDEFINED_STATUS,
                                                   MailParser.ParseMailAddress(mdn.Recipient));
            //
            // The nature of Mdn storage in config store does not result in a list of perRecipients
            // If you would rather send one DSN with muliple recipients then one could write their own Job.
            //
            var notification = new DSN(perMessage, new List<DSNPerRecipient> { perRecipient });

            var notificationMessage = new DSNMessage(mdn.Recipient, mdn.Sender, notification);
            notificationMessage.IDValue = StringExtensions.UniqueString();
            notificationMessage.SubjectValue = string.Format("{0}:{1}", "Rejected", mdn.SubjectValue);
            notificationMessage.Timestamp();
            return notificationMessage;
        }
    }
}
