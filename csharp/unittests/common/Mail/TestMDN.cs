/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Linq;
using System.Net.Mail;
using System.IO;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mail.Notifications;
using Health.Direct.Common.Mime;

using Xunit;

namespace Health.Direct.Common.Tests.Mail
{
    /// <summary>
    /// NOTE: Some of the functionality in <see cref="Health.Direct.Common.Mail.Notifications"/> 
    /// is exercised from Tests in smtpAgentTests
    /// Specifically, extension methods such as CreateNotification...
    /// At some point, we'll move the tests around.
    /// </summary>
    public class TestMDN
    {
        [Fact]
        public void TestDisposition()
        {
            Disposition disposition = new Disposition(MDNStandard.NotificationType.Processed);
            Assert.True(disposition.ToString() == "automatic-action/MDN-sent-automatically;processed");

            disposition = new Disposition(MDNStandard.NotificationType.Processed, true);
            Assert.True(disposition.ToString() == "automatic-action/MDN-sent-automatically;processed/error");
                        
            disposition = new Disposition(MDNStandard.TriggerType.Automatic, MDNStandard.SendType.UserMediated, MDNStandard.NotificationType.Displayed, true);
            Assert.True(disposition.ToString() == "automatic-action/MDN-sent-manually;displayed/error");
            
            disposition = new Disposition(MDNStandard.TriggerType.UserInitiated, MDNStandard.SendType.UserMediated, MDNStandard.NotificationType.Displayed, true);
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
            
            Assert.True(!string.IsNullOrEmpty(notificationMessage.SubjectValue));
                                    
            MimeEntity[] mdnEntities = notificationMessage.GetParts().ToArray();
            this.Verify(mdnEntities);
            
            this.VerifyProcessedNotification(mdnEntities[1], notification);
            
            HeaderCollection fields = this.GetNotificationFields(mdnEntities[1]);
            Assert.NotNull(fields[MDNStandard.Fields.FinalRecipient]);
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
            Assert.Throws<MDNException>(() => notificationMessage.RequestNotification());
            Assert.Null(notificationMessage.CreateNotificationMessage(from, notification));
        }

        [Fact]
        public void TestSerialization()
        {
            Message source = this.CreateSourceMessage();            
            Notification notification = this.CreateProcessedNotification();
            NotificationMessage notificationMessage = source.CreateNotificationMessage(new MailAddress(source.FromValue), notification);
            
            var path = Path.GetTempFileName();
            try
            {
                notificationMessage.Save(path);
                Message loadedMessage = Message.Load(File.ReadAllText(path));
                Assert.True(loadedMessage.IsMDN());
                Assert.Equal(notificationMessage.ParsedContentType.MediaType, loadedMessage.ParsedContentType.MediaType);
                Assert.Equal(notificationMessage.SubjectValue, loadedMessage.SubjectValue);
            }
            finally
            {
                File.Delete(path);
            }
        }
                
        [Fact]
        public void TestParser()
        {
            Notification notification = this.CreateProcessedNotification();
            notification.Error = "Whoops!";            
            this.SendAndParse(notification);
        }

        [Fact]
        public void TestParserWithExplanation()
        {
            Notification notification = this.CreateProcessedNotification(true);
            notification.Error = "0x323d235";
            notification.Explanation = "Tut, tut. We messed up.";
            this.SendAndParse(notification);
        }
        
        [Fact]
        public void TestAck()
        {
            Message message = this.CreateSourceMessage();
            
            Notification ack = null;
            Assert.DoesNotThrow(() => ack = Notification.CreateAck(new ReportingUserAgent("Unit Tester", "The Direct Project"), 
                                                                   "Cheers"));
            Assert.True(ack.Disposition.Notification == MDNStandard.NotificationType.Processed);
            Assert.NotNull(ack.ReportingAgent);            
            Assert.Equal(ack.Explanation, "Cheers");
            
            SendAndParse(ack);
        }
        
        void SendAndParse(Notification source)
        {
            Message message = this.CreateSourceMessage();
            NotificationMessage notificationMessage = message.CreateNotificationMessage(new MailAddress(message.FromValue), source);
            Parse(source, notificationMessage);
        }

        void Parse(Notification source, NotificationMessage notificationMessage)
        {
            Notification parsed = null;
            Assert.DoesNotThrow(() => parsed = MDNParser.Parse(source));
            Assert.NotNull(parsed.Disposition);
            VerifyEqual(source, parsed);
        }
        
        void VerifyEqual(Notification x, Notification y)
        {
            if (x.ReportingAgent != null)
            {
                Assert.Equal(x.ReportingAgent.Name, y.ReportingAgent.Name);
                Assert.Equal(x.ReportingAgent.Product, y.ReportingAgent.Product);
            }
            if (x.Gateway != null)
            {
                Assert.Equal(x.Gateway.Type, y.Gateway.Type);
                Assert.Equal(x.Gateway.Domain, y.Gateway.Domain);
            }
            Assert.Equal(x.Explanation, y.Explanation);
            if (x.FinalRecipient != null)
            {
                Assert.Equal(x.FinalRecipient.ToString(), y.FinalRecipient.ToString());
            }
            if (x.Error != null)
            {
                Assert.Equal(x.Error, y.Error);
            }
            VerifyEqual(x.Disposition, y.Disposition);
        }
        
        void VerifyEqual(Disposition x, Disposition y)
        {
            Assert.Equal(x.TriggerType, y.TriggerType);
            Assert.Equal(x.SendType, y.SendType);
            Assert.Equal(x.Notification, y.Notification);
            Assert.Equal(x.IsError, y.IsError);
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
            return CreateProcessedNotification(false);
        }
        
        Notification CreateProcessedNotification(bool isError)
        {
            Notification notification = new Notification(MDNStandard.NotificationType.Processed, isError);
            notification.OriginalMessageID = "Message In a Bottle";
            notification.Gateway = new MdnGateway("gateway.example.com", "smtp");
            if (isError)
            {
                notification.Error = ErrorMessage;
            }
            notification.Explanation = NotificationExplanation;
            
            return notification;
        }
        
        /// <summary>
        /// Verify a "Processed message" MDN notification 
        /// </summary>
        void VerifyProcessedNotification(MimeEntity notificationEntity, Notification notification)
        {
            HeaderCollection fields = this.GetNotificationFields(notificationEntity);
            Assert.NotEmpty(fields);

            Assert.True(fields.HasHeader(MDNStandard.Fields.Disposition, notification.Disposition.ToString()));
            Assert.True(fields.HasHeader(MDNStandard.Fields.Gateway, "smtp;gateway.example.com"));
            Assert.True(fields.HasHeader(MDNStandard.Fields.OriginalMessageID, OriginalID));
            if (fields[MDNStandard.Fields.Error] != null)
            {
                Assert.True(fields.HasHeader(MDNStandard.Fields.Error, ErrorMessage));
            }
        }
                
        HeaderCollection GetNotificationFields(MimeEntity notificationEntity)
        {
            return MDNParser.ParseMDNFields(notificationEntity);
        }
    }
}