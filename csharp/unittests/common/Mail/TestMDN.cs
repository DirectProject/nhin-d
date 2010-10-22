/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Linq;
using System.Net.Mail;

using Health.Direct.Common.Mail;
using Health.Direct.Common.Mail.Notifications;
using Health.Direct.Common.Mime;

using Xunit;

namespace Health.Direct.Common.Tests.Mail
{
    /// <summary>
    /// NOTE: Some of the functionality in NHINDirect.Mail.Notifications is exercised from Tests in smtpAgentTests
    /// Specifically, extension methods such as CreateNotification...
    /// At some point, we'll move the tests around
    /// </summary>
    public class TestMDN
    {
        [Fact]
        public void TestDisposition()
        {
            Disposition disposition = new Disposition(MDNStandard.NotificationType.Processed);
            Assert.True(disposition.ToString() == "automatic-action/MDN-sent-automatically;processed");
            
            disposition.IsError = true;
            Assert.True(disposition.ToString() == "automatic-action/MDN-sent-automatically;processed/error");
                        
            disposition.Notification = MDNStandard.NotificationType.Displayed;
            disposition.SendType = MDNStandard.SendType.UserMediated;
            Assert.True(disposition.ToString() == "automatic-action/MDN-sent-manually;displayed/error");
            
            disposition.TriggerType = MDNStandard.TriggerType.UserInitiated;
            Assert.True(disposition.ToString() == "manual-action/MDN-sent-manually;displayed/error");
        }

        const string OriginalID = "Message In a Bottle";
        const string ErrorMessage = "Bring on The Night";
        const string NotificationExplanation = "Synchronicity";
        
        [Fact]
        public void TestNotification()
        {
            Notification notification = new Notification(MDNStandard.NotificationType.Processed);            
            Assert.True(notification.ContentType.IsMediaType(MDNStandard.MediaType.ReportMessage));
            Assert.True(notification.ContentType.HasParameter("report-type", "disposition-notification"));
                        
            notification = this.CreateProcessedNotification();
            
            MimeEntity[] mdnEntities = notification.ToArray();            
            this.Verify(mdnEntities);
            
            Assert.True(mdnEntities[0].Body.Text == NotificationExplanation);
            
            this.VerifyProcessedNotification(mdnEntities[1], notification);
            
            Assert.DoesNotThrow(() => notification.ToEntity());
            
            Assert.DoesNotThrow(() => MimeSerializer.Default.Serialize(notification.ToEntity()));            
        }
        
        [Fact]
        public void TestNotificationMessage()
        {
            Message source = this.CreateSourceMessage();
            
            Notification notification = this.CreateProcessedNotification();
            NotificationMessage notificationMessage = source.CreateNotificationMessage(new MailAddress(source.FromValue), notification);
            
            Assert.True(notificationMessage.IsMDN());
            Assert.False(notificationMessage.ShouldIssueNotification());
                                    
            Assert.True(notificationMessage.IsMultiPart);
            Assert.True(notificationMessage.ToValue == source.Headers.GetValue(MDNStandard.Headers.DispositionNotificationTo));
                        
            MimeEntity[] mdnEntities = notificationMessage.GetParts().ToArray();
            this.Verify(mdnEntities);
            
            this.VerifyProcessedNotification(mdnEntities[1], notification);
        }
        
        [Fact]
        public void TestNotificationOnNotication()
        {
            Message source = this.CreateSourceMessage();
            Notification notification = this.CreateProcessedNotification();
            MailAddress from = new MailAddress(source.FromValue);
            NotificationMessage notificationMessage = source.CreateNotificationMessage(from, notification);
            //
            // Shouldn never be able to issue a notification for a notification
            //
            Assert.Throws<NotSupportedException>(() => notificationMessage.RequestNotification());
            Assert.Null(notificationMessage.CreateNotificationMessage(from, notification));
        }
        
        Message CreateSourceMessage()
        {
            Message source = new Message("bob@nhind.hsgincubator.com", "toby@redmond.hsgincubator.com", "Test message");
            source.Headers.Add(MDNStandard.Headers.DispositionNotificationTo, "toby@redmond.hsgincubator.com");
            source.Headers.Add(MailStandard.Headers.MessageID, OriginalID);
            
            return source;
        }
                
        void Verify(MimeEntity[] mdnEntities)
        {
            Assert.True(mdnEntities.Length == 2);
            Assert.True(mdnEntities[1].ParsedContentType.IsMediaType(MDNStandard.MediaType.DispositionNotification));
        }
                
        Notification CreateProcessedNotification()
        {
            Notification notification = new Notification(MDNStandard.NotificationType.Processed);
            notification.OriginalMessageID = "Message In a Bottle";
            notification.Gateway = new MdnGateway("gateway.example.com", "smtp");
            notification.Error = ErrorMessage;
            notification.Explanation = NotificationExplanation;
            
            return notification;
        }
        
        void VerifyProcessedNotification(MimeEntity notificationEntity, Notification notification)
        {
            notificationEntity.HasHeader(MDNStandard.Headers.Disposition, notification.Disposition.ToString());
            notificationEntity.HasHeader(MDNStandard.Headers.Gateway, "smtp;le gateway");
            notificationEntity.HasHeader(MDNStandard.Headers.OriginalMessageID, OriginalID);
            notificationEntity.HasHeader(MDNStandard.Headers.Error, ErrorMessage);
        }
    }
}