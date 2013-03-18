/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Joe Shook	    jshook@kryptiq.com

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
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
        public void TestNotification_AssertProcessed()
        {
            Notification notificationExpected = new Notification(MDNStandard.NotificationType.Processed);
            Assert.True(notificationExpected.ContentType.IsMediaType(MDNStandard.MediaType.ReportMessage));
            Assert.True(notificationExpected.ContentType.HasParameter("report-type", "disposition-notification"));

            Notification notificationActual = this.CreateProcessedNotification();


            MimeEntity[] mdnEntities = notificationActual.ToArray();            
            this.Verify(mdnEntities);
            
            Assert.True(mdnEntities[0].Body.Text == NotificationExplanation);

            this.VerifyDispositionTypeNotification(mdnEntities[1], notificationExpected);

            Assert.DoesNotThrow(() => notificationActual.ToEntity());

            Assert.DoesNotThrow(() => MimeSerializer.Default.Serialize(notificationActual.ToEntity()));            
        }

        [Fact]
        public void TestNotification_AssertDispatched()
        {
            Notification notificationExpected = new Notification(MDNStandard.NotificationType.Dispatched);
            Assert.True(notificationExpected.ContentType.IsMediaType(MDNStandard.MediaType.ReportMessage));
            Assert.True(notificationExpected.ContentType.HasParameter("report-type", "disposition-notification"));

            Notification notificationActual = this.CreateDispatchedNotification();

            MimeEntity[] mdnEntities = notificationActual.ToArray();
            this.Verify(mdnEntities);

            Assert.True(mdnEntities[0].Body.Text == NotificationExplanation);

            this.VerifyDispositionTypeNotification(mdnEntities[1], notificationExpected);

            Assert.DoesNotThrow(() => notificationActual.ToEntity());

            Assert.DoesNotThrow(() => MimeSerializer.Default.Serialize(notificationActual.ToEntity()));  
        }

        [Fact]
        public void TestProcessedNotificationMessage()
        {
            Message source = this.CreateSourceMessage();
            
            Notification notification = this.CreateProcessedNotification();
            NotificationMessage notificationMessage = source.CreateNotificationMessage(new MailAddress(source.FromValue), notification);
            
            Console.WriteLine(notificationMessage);
            Assert.True(notificationMessage.IsMDN());
            Assert.False(notificationMessage.ShouldIssueNotification());
                                    
            Assert.True(notificationMessage.IsMultiPart);
            Assert.True(notificationMessage.ToValue == source.Headers.GetValue(MDNStandard.Headers.DispositionNotificationTo));
            
            Assert.True(!string.IsNullOrEmpty(notificationMessage.SubjectValue));
            
            Assert.True(notificationMessage.HasHeader(MimeStandard.VersionHeader));
            Assert.True(notificationMessage.HasHeader(MailStandard.Headers.Date));
            
            MimeEntity[] mdnEntities = notificationMessage.GetParts().ToArray();
            this.Verify(mdnEntities);
            
            this.VerifyDispositionTypeNotification(mdnEntities[1], notification);
            
            HeaderCollection fields = this.GetNotificationFields(mdnEntities[1]);
            Assert.NotNull(fields[MDNStandard.Fields.FinalRecipient]);
        }

        [Fact]
        public void TestDispatchedNotificationMessage()
        {
            Message source = this.CreateSourceMessage();

            Notification notification = this.CreateDispatchedNotification();
            NotificationMessage notificationMessage = source.CreateNotificationMessage(new MailAddress(source.FromValue), notification);

            Assert.True(notificationMessage.IsMDN());
            Assert.False(notificationMessage.ShouldIssueNotification());

            Assert.True(notificationMessage.IsMultiPart);
            Assert.True(notificationMessage.ToValue == source.Headers.GetValue(MDNStandard.Headers.DispositionNotificationTo));

            Assert.True(!string.IsNullOrEmpty(notificationMessage.SubjectValue));

            Assert.True(notificationMessage.HasHeader(MimeStandard.VersionHeader));
            Assert.True(notificationMessage.HasHeader(MailStandard.Headers.Date));

            MimeEntity[] mdnEntities = notificationMessage.GetParts().ToArray();
            this.Verify(mdnEntities);

            this.VerifyDispositionTypeNotification(mdnEntities[1], notification);

            HeaderCollection fields = this.GetNotificationFields(mdnEntities[1]);
            Assert.NotNull(fields[MDNStandard.Fields.FinalRecipient]);
        }
            

        [Fact]
        public void TestNotificationOnNotication_Processed()
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
        public void TestNotificationOnNotication_Dispatched()
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
        public void TestSerialization_Processed()
        {
            Disposition expectedDisposition = new Disposition(MDNStandard.NotificationType.Processed);

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
                Assert.True(loadedMessage.HasHeader(MimeStandard.VersionHeader));
                Assert.True(loadedMessage.HasHeader(MailStandard.Headers.Date));
                Assert.True(loadedMessage.Headers.Count(x => (MimeStandard.Equals(x.Name, MimeStandard.VersionHeader))) == 1);
                var mdn = MDNParser.Parse(loadedMessage);
                VerifyEqual(expectedDisposition, mdn.Disposition);
            }
            finally
            {
                File.Delete(path);
            }
        }

        [Fact]
        public void TestDispostionTypeDeserializationNoTrim()
        {
            //Notice the leading space at processed
            var message =
                @"MIME-Version:1.0
To:toby@redmond.hsgincubator.com
From:toby@redmond.hsgincubator.com
Content-Type:multipart/report; boundary=d027c90b736247f6908bb9558cbc5926; report-type=disposition-notification
Subject:processed
Message-ID:0c0918d7-5bfa-48e4-91c0-6bffc7660967
Date:7 Dec 2012 12:50:26 -08:00


--d027c90b736247f6908bb9558cbc5926
Content-Type:text/plain

Synchronicity
--d027c90b736247f6908bb9558cbc5926
Content-Type:message/disposition-notification

Disposition:automatic-action/MDN-sent-automatically; processed
Original-Message-ID:Message In a Bottle
MDN-Gateway:smtp;gateway.example.com
Final-Recipient:rfc822;toby@redmond.hsgincubator.com

--d027c90b736247f6908bb9558cbc5926--";
            Message loadedMessage = Message.Load(message);
            var mdn = MDNParser.Parse(loadedMessage);

            Assert.Equal("processed", mdn.Disposition.Notification.ToString(), StringComparer.OrdinalIgnoreCase);
        }

        [Fact]
        public void TestDispositionTypeFormats()
        {
            //
            // Only testing ability to read untrimmed disposition types.
            //

            Assert.DoesNotThrow(() => MDNParser.ParseDisposition("automatic-action/MDN-sent-automatically; processed /error "));
            Assert.DoesNotThrow(() => MDNParser.ParseDisposition("automatic-action/MDN-sent-automatically; failed /error "));

            Assert.DoesNotThrow(() => MDNParser.ParseDisposition("automatic-action/MDN-sent-automatically; processed / error "));
            Assert.DoesNotThrow(() => MDNParser.ParseDisposition("automatic-action/MDN-sent-automatically; failed / error "));
            Assert.DoesNotThrow(() => MDNParser.ParseDisposition("automatic-action/MDN-sent-automatically; processed / anything "));

            //normal
            Assert.DoesNotThrow(() => MDNParser.ParseDisposition("automatic-action/MDN-sent-automatically; processed/modifier1,modifier2 "));
            //forgiving...
            Assert.DoesNotThrow(() => MDNParser.ParseDisposition("automatic-action/MDN-sent-automatically; processed / modifier1 , modifier2 "));

            Assert.Throws <MDNException>(() => MDNParser.ParseDisposition("automatic-action/MDN-sent-automatically; fallen / error "));
            
        }

        [Fact]
        public void TestSerialization_Dispatched()
        {
            Disposition expectedDisposition = new Disposition(MDNStandard.NotificationType.Dispatched);

            Message source = this.CreateSourceMessage();
            Notification notification = this.CreateDispatchedNotification();
            NotificationMessage notificationMessage = source.CreateNotificationMessage(new MailAddress(source.FromValue), notification);

            var path = Path.GetTempFileName();
            try
            {
                notificationMessage.Save(path);
                Message loadedMessage = Message.Load(File.ReadAllText(path));
                Assert.True(loadedMessage.IsMDN());
                Assert.Equal(notificationMessage.ParsedContentType.MediaType, loadedMessage.ParsedContentType.MediaType);
                Assert.Equal(notificationMessage.SubjectValue, loadedMessage.SubjectValue);
                Assert.True(loadedMessage.HasHeader(MimeStandard.VersionHeader));
                Assert.True(loadedMessage.HasHeader(MailStandard.Headers.Date));
                Assert.True(loadedMessage.Headers.Count(x => (MimeStandard.Equals(x.Name, MimeStandard.VersionHeader))) == 1);
                var mdn = MDNParser.Parse(loadedMessage);
                VerifyEqual(expectedDisposition, mdn.Disposition);
            }
            finally
            {
                File.Delete(path);
            }
        }
                
        [Fact]
        public void TestParser_Processed()
        {
            Notification notification = this.CreateProcessedNotification();
            notification.OriginalRecipient = new MailAddress("original@nhind.hsgincubator.com");
            notification.Error = "Whoops!";            
            this.SendAndParse(notification);
        }

        [Fact]
        public void TestParser_Dispatched()
        {
            Notification notification = this.CreateDispatchedNotification();
            notification.Error = "Whoops!";
            this.SendAndParse(notification);
        }


        [Fact]
        public void TestParserWithExplanation_Processed()
        {
            Notification notification = this.CreateProcessedNotification(true);
            notification.Error = "0x323d235";
            notification.Explanation = "Tut, tut. We messed up.";
            this.SendAndParse(notification);
        }

        [Fact]
        public void TestParserWithExplanation_Dispatched()
        {
            Notification notification = this.CreateDispatchedNotification(true);
            notification.Error = "0x323d235";
            notification.Explanation = "Tut, tut. We messed up.";
            this.SendAndParse(notification);
        }

        /// <summary>
        /// Note there is no TextAck_Dispatched.
        /// At this point dispatched will be handled outside the direct gateway.
        /// An example of a dispatched notification will be in the MdnMonitor Windows Service.
        /// </summary>
        [Fact]
        public void TestAck_Processed()
        {
            Message message = this.CreateSourceMessage();
            
            Notification ack = null;
            Assert.DoesNotThrow(() => ack = Notification.CreateAck(new ReportingUserAgent("Unit Tester", "The Direct Project"), 
                                                                   "Cheers", MDNStandard.NotificationType.Processed));
            Assert.True(ack.Disposition.Notification == MDNStandard.NotificationType.Processed);
            Assert.NotNull(ack.ReportingAgent);            
            Assert.Equal(ack.Explanation, "Cheers");
            
            SendAndParse(ack);
        }

        /// <summary>
        /// Not relying on SendAndParse method...  
        /// </summary>
        [Fact]
        public void TestWarning_AssertWarning_Explicit()
        {
            Notification notification = CreateProcessedNotification();
            notification.Warning = "Xunit warning";
            MimeEntity[] mimeEntities = notification.ToArray();
            
            Assert.NotNull(mimeEntities);
            Assert.Equal(2, mimeEntities.Count());
            
            MimeEntity bodyEntity = mimeEntities[1];
            Assert.True(bodyEntity.ContentType.StartsWith("message/disposition-notification"));
            HeaderCollection fields = this.GetNotificationFields(bodyEntity);
            Assert.Null(fields[MDNStandard.Fields.FinalRecipient]);
            Assert.NotNull(fields[MDNStandard.Fields.Warning]);
            Assert.Equal("Xunit warning", fields.GetValue(MDNStandard.Fields.Warning));

        }

        [Fact]
        public void TestWarning_AssertWarning()
        {
            Notification notification = this.CreateProcessedNotification();
            notification.Warning = "Xunit warning";
            this.SendAndParse(notification);
        }

        [Fact]
        public void TestFailure_AssertFailure()
        {
            Notification notification = this.CreateProcessedNotification();
            notification.Failure = "Xunit failure";
            this.SendAndParse(notification);
        }

        [Fact]
        public void TestExtensions_AssertExtensions()
        {
            Notification notification = this.CreateProcessedNotification();

            notification.SpecialFields = new HeaderCollection()
                                             {
                                                 //Header value of empty fails JoinHeader in DefaultSerializer.  Research this...
                                                 //Constructor with StringSegment does not fail.  
                                                 new Header(new StringSegment("X-Test1:")),
                                                 new Header(new StringSegment("X-Test2:MyValue"))
                                             };
            this.SendAndParse(notification);
        }

        [Fact]
        public void TestExtensions_ExtensionsSerialization()
        {
            Disposition expectedDisposition = new Disposition(MDNStandard.NotificationType.Processed);

            Message source = this.CreateSourceMessage();
            Notification notification = this.CreateProcessedNotification();
            notification.SpecialFields = new HeaderCollection()
                                             {
                                                 //Header value of empty fails JoinHeader in DefaultSerializer.  Research this...
                                                 //Constructor with StringSegment does not fail.  
                                                 new Header(new StringSegment("X-Test1:")),
                                                 new Header(new StringSegment("X-Test2:MyValue"))
                                             };
            notification.OriginalRecipient = new MailAddress("original@nhind.hsgincubator.com");
            NotificationMessage notificationMessage = source.CreateNotificationMessage(new MailAddress(source.FromValue), notification);

            var path = Path.GetTempFileName();
            try
            {
                notificationMessage.Save(path);
                Message loadedMessage = Message.Load(File.ReadAllText(path));
                Assert.True(loadedMessage.IsMDN());
                Assert.Equal(notificationMessage.ParsedContentType.MediaType, loadedMessage.ParsedContentType.MediaType);
                Assert.Equal(notificationMessage.SubjectValue, loadedMessage.SubjectValue);
                Assert.True(loadedMessage.HasHeader(MimeStandard.VersionHeader));
                Assert.True(loadedMessage.HasHeader(MailStandard.Headers.Date));
                Assert.True(loadedMessage.Headers.Count(x => (MimeStandard.Equals(x.Name, MimeStandard.VersionHeader))) == 1);
               
                var mdn = MDNParser.Parse(loadedMessage);
                VerifyEqual(expectedDisposition, mdn.Disposition);
                Assert.NotNull(mdn.SpecialFields["X-Test1"]);
                Assert.Equal("", mdn.SpecialFields["X-Test1"].ValueRaw);
                Assert.Equal("MyValue", mdn.SpecialFields["X-Test2"].Value);
                Assert.Equal(notification.OriginalRecipient, mdn.OriginalRecipient);
            }
            finally
            {
                File.Delete(path);
            }
        }


        [Fact]
        public void TestDispatchTimelyAndReliableExtension_AssertExtension()
        {
            Message source = this.CreateSourceMessage();
            Notification notification = this.CreateDispatchedNotification();
            NotificationMessage notificationMessage = source.CreateNotificationMessage(new MailAddress(source.FromValue), notification);

            var mdn = MDNParser.Parse(notificationMessage);
            Assert.NotNull(mdn.SpecialFields[MDNStandard.DispositionOption_TimelyAndReliable]);
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
            if (x.OriginalRecipient != null)
            {
                Assert.Equal(x.OriginalRecipient.ToString(), y.OriginalRecipient.ToString());
            }
            if (x.Error != null)
            {
                Assert.Equal(x.Error, y.Error);
            }
            if (x.Warning != null)
            {
                Assert.Equal(x.Warning, y.Warning);
            }
            if (x.Failure != null)
            {
                Assert.Equal(x.Failure, y.Failure);
            }
            if (x.SpecialFields != null)
            {
                VerifyEqual(x.SpecialFields, y.SpecialFields);
            }
            VerifyEqual(x.Disposition, y.Disposition);
        }

        void VerifyEqual(IList<Header> x, IList<Header> y)
        {
            Assert.True(x.Count() > 0);
            Assert.True(y.Count() > 0);
            Assert.Equal(x.Count(), y.Count());
            for (int i = 0; i < x.Count(); i++)
            {
                Assert.Equal(x[i].Name, y[i].Name);
                Assert.Equal(x[i].ValueRaw, y[i].ValueRaw );
            }
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

        Notification CreateDispatchedNotification()
        {
            return CreateDispatchedNotification(false);
        }

        Notification CreateDispatchedNotification(bool isError)
        {
            Notification notification = new Notification(MDNStandard.NotificationType.Dispatched, isError);
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
        /// Verify Disposition-Type MDN notification 
        /// </summary>
        void VerifyDispositionTypeNotification(MimeEntity notificationEntity, Notification notification)
        {
            HeaderCollection fields = this.GetNotificationFields(notificationEntity);
            Assert.NotEmpty(fields);

            Assert.True(fields.HasHeader(MDNStandard.Fields.Disposition, notification.Disposition.ToString()), 
                string.Format("Expected a contained Disposition-Type of {0}", notification.Disposition));
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