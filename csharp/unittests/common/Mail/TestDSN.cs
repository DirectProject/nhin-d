/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joe Shook	    jshook@kryptiq.com

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Mail;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mail.DSN;
using Health.Direct.Common.Mail.Notifications;
using Health.Direct.Common.Mime;
using Xunit;

namespace Health.Direct.Common.Tests.Mail
{
    public class TestDSN
    {
        private string ReportingMtaName;
        const string OriginalID = "Message In a Bottle";
        const string Postmaster = "postmaster@redmond.hsgincubator.com";
        const string Subject = "Sound Grenade";

       

        [Fact]
        public void TestFailedDeliveryStatusNotification()
        {
            Message source = this.CreateSourceMessage();

            DSN statusNotification = this.CreateFailedStatusNotification();
            DSNMessage dsnMessage = source.CreateStatusMessage(new MailAddress(Postmaster), statusNotification);

            Assert.NotNull(dsnMessage);
            Console.WriteLine(dsnMessage);

            Assert.True(dsnMessage.IsDSN());
            Assert.True(dsnMessage.IsMultiPart);

            Assert.Equal(source.Headers.GetValue(MailStandard.Headers.From), dsnMessage.ToValue);
            Assert.Equal(Postmaster, dsnMessage.FromValue);

            Assert.Equal("Rejected:" + source.SubjectValue, dsnMessage.SubjectValue);

            Assert.True(dsnMessage.HasHeader(MimeStandard.VersionHeader));
            Assert.True(dsnMessage.HasHeader(MailStandard.Headers.Date));

            MimeEntity[] mdnEntities = dsnMessage.GetParts().ToArray();
            this.Verify(mdnEntities);

            this.VerifyDSNHeaders(mdnEntities[1], statusNotification);

            //Serialize and Deserialize
            Message message = MimeSerializer.Default.Deserialize<Message>(dsnMessage.ToString());
            Console.WriteLine(message);
        }

        [Fact]
        public void TestNotificationOnNotication_FailedDeliveryStatus()
        {
            Message source = this.CreateSourceMessage();
            DSN statusNotification = this.CreateFailedStatusNotification();
            DSNMessage dsnMessage = source.CreateStatusMessage(new MailAddress(Postmaster), statusNotification);
            //
            // Shouldn never be able to issue a MDN for a DSN
            //
            Assert.Throws<DSNException>(() => dsnMessage.RequestNotification());
            Assert.Null(dsnMessage.CreateNotificationMessage(new MailAddress(Postmaster), statusNotification));
        }

        [Fact]
        public void TestDSNDeserialization()
        {
            string dsnMessage =
                @"MIME-Version:1.0
To:toby@redmond.hsgincubator.com
From:postmaster@redmond.hsgincubator.com
Content-Type:multipart/report; boundary=8bacf4b73bef45f2a4f481c20c00e664; report-type=delivery-status
Message-ID:d6bff591-598c-4b20-ac6a-25da27f4918c
Subject:Rejected:Sound Grenade
Date:31 Oct 2012 14:00:52 -07:00


--8bacf4b73bef45f2a4f481c20c00e664
Content-Type:text/plain

Delivery Status Notification
--8bacf4b73bef45f2a4f481c20c00e664
Content-Type:message/delivery-status

Reporting-MTA: dns;reporting_mta_name
X-Original-Message-ID: Message In a Bottle

Final-Recipient: rfc822;User1@kryptiq.com
Action: failed
Status: 5.0.0

Original-Recipient: rfc822;arathib@vnet.ibm.com
Final-Recipient: rfc822;arathib@vnet.ibm.com
Action: failed
Status: 5.0.0 (permanent failure)
Diagnostic-Code: smtp;  550 'arathib@vnet.IBM.COM' is not a 
 registered gateway user
Remote-MTA: dns; vnet.ibm.com

Final-Recipient: rfc822;User3@kryptiq.com
Action: failed
Status: 5.0.0

--8bacf4b73bef45f2a4f481c20c00e664--

";
            Message loadedMessage = Message.Load(dsnMessage);
            Console.WriteLine(loadedMessage);

            Assert.True(loadedMessage.IsDSN());
            Assert.Equal(loadedMessage.ParsedContentType.MediaType, loadedMessage.ParsedContentType.MediaType);
            Assert.Equal(loadedMessage.SubjectValue, loadedMessage.SubjectValue);
            Assert.True(loadedMessage.HasHeader(MimeStandard.VersionHeader));
            Assert.True(loadedMessage.HasHeader(MailStandard.Headers.Date));
            Assert.True(loadedMessage.Headers.Count(x => (MimeStandard.Equals(x.Name, MimeStandard.VersionHeader))) == 1);
            var dsnActual = DSNParser.Parse(loadedMessage);
            var recipients = dsnActual.PerRecipient.ToList();
            Assert.Equal("smtp;  550 'arathib@vnet.IBM.COM' is not a registered gateway user",
                         recipients[1].OtherFields.GetValue(DSNStandard.Fields.DiagnosticCode));

        }

        [Fact]
        public void TestFailedDeliveryStatusSerialization()
        {
            Message source = this.CreateSourceMessage();
            
            DSN dsnExpected = this.CreateFailedStatusNotification(3);
            DSNMessage dsnMessage = source.CreateStatusMessage(new MailAddress(Postmaster), dsnExpected);
            Console.WriteLine(dsnMessage);
            var path = Path.GetTempFileName();
            try
            {
                dsnMessage.Save(path);
                Message loadedMessage = Message.Load(File.ReadAllText(path));
                Assert.True(loadedMessage.IsDSN());
                Assert.Equal(dsnMessage.ParsedContentType.MediaType, loadedMessage.ParsedContentType.MediaType);
                Assert.Equal(dsnMessage.SubjectValue, loadedMessage.SubjectValue);
                Assert.True(loadedMessage.HasHeader(MimeStandard.VersionHeader));
                Assert.True(loadedMessage.HasHeader(MailStandard.Headers.Date));
                Assert.True(loadedMessage.Headers.Count(x => (MimeStandard.Equals(x.Name, MimeStandard.VersionHeader))) == 1);
                var dsnActual = DSNParser.Parse(loadedMessage);
                
                VerifyEqual(dsnExpected, dsnActual);
            }
            finally
            {
                File.Delete(path);
            }
        }

        private void VerifyEqual(DSN dsnExpected, DSN dsnActual)
        {
            Assert.Equal(dsnExpected.PerMessage.ReportingMtaName, dsnActual.PerMessage.ReportingMtaName);
            Assert.Equal(dsnExpected.PerMessage.OriginalMessageId, dsnActual.PerMessage.OriginalMessageId);

            Assert.Equal(dsnExpected.PerRecipient.Count(), dsnActual.PerRecipient.Count());

            Assert.Equal(dsnExpected.PerRecipient.First().FinalRecipient, dsnActual.PerRecipient.First().FinalRecipient);
            Assert.Equal(dsnExpected.PerRecipient.First().Action, dsnActual.PerRecipient.First().Action);
            Assert.Equal(dsnExpected.PerRecipient.First().Status, dsnActual.PerRecipient.First().Status);
        }


        /// <summary>
        /// Verify Disposition-Type MDN notification 
        /// </summary>
        void VerifyDSNHeaders(MimeEntity notificationEntity, DSN notification)
        {
            HeaderCollection fields = DSNParser.ParseDSNFields(notificationEntity);
            Assert.NotEmpty(fields);
            //
            // perMessage
            //
            Assert.True(fields.HasHeader(DSNStandard.Fields.ReportingMTA, "dns;" + ReportingMtaName));
            Assert.True(fields.HasHeader(DSNStandard.Fields.OriginalMessageID, OriginalID));

            //
            // perRecipient
            //
            Assert.True(fields.HasHeader(DSNStandard.Fields.FinalRecipient, 
                                         "rfc822;" + notification.PerRecipient.First().FinalRecipient.Address));
            Assert.True(fields.HasHeader(DSNStandard.Fields.Action, "failed"));
            Assert.True(fields.HasHeader(DSNStandard.Fields.Status, "5.0.0"));
            
        }
              
        DSN CreateFailedStatusNotification()
        {
            return CreateFailedStatusNotification(1);
        }

        DSN CreateFailedStatusNotification(int recipients)
        {
            ReportingMtaName = "reporting_mta_name";
            var perMessage = new DSNPerMessage(ReportingMtaName, OriginalID);

            var perRecipients = new List<DSNPerRecipient>();
            for (int i = 1; i <= recipients; i++)
            {
                perRecipients.Add(new DSNPerRecipient(DSNStandard.DSNAction.Failed, DSNStandard.DSNStatus.Permanent
                                                      , DSNStandard.DSNStatus.UNDEFINED_STATUS,
                                                      new MailAddress(String.Format("User{0}@kryptiq.com", i))));
                
            }
            
            var dsn = new DSN(perMessage, perRecipients);

            return dsn;
        }

        Message CreateSourceMessage()
        {
            Message source = new Message("bob@nhind.hsgincubator.com", "toby@redmond.hsgincubator.com", "Test message");
            source.Headers.Add(MailStandard.Headers.Subject, Subject);
            source.Headers.Add(MailStandard.Headers.MessageID, OriginalID);

            return source;
        }

        public string ReadMessageText(string messageFilePath)
        {
            if (!Path.IsPathRooted(messageFilePath))
            {
                messageFilePath = Path.Combine("DsnTestMessages", messageFilePath);
            }

            return File.ReadAllText(messageFilePath);
        }

        void Verify(MimeEntity[] dsnEntities)
        {
            Assert.True(dsnEntities.Length == 2);
            //text/plain
            //future work to support text/html via a provider model.
            Assert.True(dsnEntities[0].ParsedContentType.IsMediaType(DSNStandard.MediaType.TextPlain));
            Assert.True(dsnEntities[1].ParsedContentType.IsMediaType(DSNStandard.MediaType.DSNDeliveryStatus));
        }

    }
}
