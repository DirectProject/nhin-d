using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mail.Notifications;
using Health.Direct.Config.Store;
using Quartz;

namespace Health.Direct.MdnMonitor
{
    public class MdnProcessedTimeout : TimeoutBase, IStatefulJob //Will limit to one job at a time.
    {
        public override void Execute(JobExecutionContext context)
        {
            var settings = Load(context);
            
            try
            {
                Console.WriteLine("---{0} executing.[{1}]", context.JobDetail.FullName, DateTime.Now.ToString("r"));

                foreach (var mdn in ExpiredMdns(settings))
                {
                    Console.WriteLine("\t process: " + mdn.Id);
                    
                    var message = CreateNotificationMessage(mdn, settings);
                    mdn.Timedout = true;

                    using(var db = Store.CreateContext())
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
            catch (Exception e)
            {
                Logger.Error("--- Error in job!", e);
                var je = new JobExecutionException(e);
                throw je;
            }
        }

        protected override IList<Mdn> ExpiredMdns(TimeoutSettings settings)
        {
            IList<Mdn> mdns;
            using (var db = Store.CreateReadContext())
            {
                mdns = db.Mdns.GetExpiredProcessed(settings.ExpiredMinutes, settings.BulkCount).ToList();
                if (mdns.Count() > 0)
                {
                    Logger.Debug("Processing {0} expired processed mdns", mdns.Count());
                }
            }
            return mdns;
        }

        static NotificationMessage CreateNotificationMessage(Mdn mdn, TimeoutSettings settings)
        {
            var notification = new Notification(MDNStandard.NotificationType.Processed, true);
            notification.OriginalMessageID = mdn.MessageId;
            notification.Gateway = new MdnGateway(MailParser.ParseMailAddress(mdn.Sender).Host, "smtp");
            notification.Error = settings.ErrorCode;
            notification.Explanation = "processed message timed out.";
            notification.FinalRecipient = MailParser.ParseMailAddress(mdn.Recipient);


            var notificationMessage = new NotificationMessage(mdn.Recipient, mdn.Sender, notification);
            notificationMessage.IDValue = StringExtensions.UniqueString();

            string originalSubject = mdn.SubjectValue;
            if (!string.IsNullOrEmpty(originalSubject))
            {
                notificationMessage.SubjectValue = string.Format("{0}:{1}", notification.Disposition.Notification.AsString(), originalSubject);
            }

            notificationMessage.Timestamp();
            
            return notificationMessage;
        }

    }
}
