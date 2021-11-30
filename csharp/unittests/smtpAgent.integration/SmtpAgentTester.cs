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
using System.IO;
using System.Linq;
using System.Net.Mime;
using Health.Direct.Agent;
using Health.Direct.Common.Cryptography;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mail.DSN;
using Health.Direct.Common.Mail.Notifications;
using Health.Direct.Config.Store;
using Health.Direct.Config.Store.Entity;
using Health.Direct.SmtpAgent.Config;
using Xunit;

namespace Health.Direct.SmtpAgent.Integration.Tests
{
    public class SmtpAgentTester
    {
        private const string ConnectionString = @"Data Source=(localdb)\.\ProjectsShare;Initial Catalog=DirectConfig;Integrated Security=SSPI;";


        public static string TestMessage =
            @"From: <toby@redmond.hsgincubator.com>
To: <biff@nhind.hsgincubator.com>, <bob@nhind.hsgincubator.com>
Subject: Simple Text Message
Message-ID: {0}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Yo. Wassup?";

        public static string TestMessageTimelyAndReliableMissingTo =
            @"From: <toby@redmond.hsgincubator.com>
To: <biff@nhind.hsgincubator.com>, <bob@nhind.hsgincubator.com>
Subject: Simple Text Message
Message-ID: {0}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Disposition-Notification-Options: X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true
Content-Type: text/plain

Yo. Wassup?";

        public static string TestMessageTimelyAndReliable =
            @"From: <toby@redmond.hsgincubator.com>
To: <biff@nhind.hsgincubator.com>, <bob@nhind.hsgincubator.com>
Subject: Simple Text Message
Message-ID: {0}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Disposition-Notification-Options: X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true
Disposition-Notification-To: notifythisguy@redmond.hsgincubator.com
Content-Type: text/plain

Yo. notify this guy?";

        public static string CrossDomainMessage =
            string.Format(
            @"From: <toby@redmond.hsgincubator.com>
To: <biff@nhind.hsgincubator.com>, <bob@nhind.hsgincubator.com>, <gm2552@securehealthemail.com>
Subject: Simple Text Message
Message-ID: {0}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Yo. Wassup?", Guid.NewGuid());

        
        public const string BadMessage =
            @"From: <toby@redmond.hsgincubator.com>
To: <xyz@untrusted.com>
Subject: Bad Text Message
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Bad message?";

        public static string ContainsUntrustedRecipientMessageNoTandR =
            string.Format(
            @"From: <toby@redmond.hsgincubator.com>
To: <xyz@untrusted.com>, biff@nhind.hsgincubator.com
Subject: Bad Text Message
Message-ID: {0}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Bad message?", Guid.NewGuid());

        public static string ContainsUntrustedRecipientMessageRequestTandR =
            string.Format(
            @"From: <toby@redmond.hsgincubator.com>
To: <xyz@untrusted.com>, biff@nhind.hsgincubator.com
Subject: Bad Text Message
Message-ID: {0}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Disposition-Notification-Options: X-DIRECT-FINAL-DESTINATION-DELIVERY
Content-Type: text/plain

Bad message?", Guid.NewGuid());

        public static string UntrustedRecipientMessage =
            string.Format(
            @"From: <toby@redmond.hsgincubator.com>
To: <xyz@untrusted.com>
Subject: Bad Text Message
Message-ID: {0}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Bad message?", Guid.NewGuid());

        public static string UnknownUsersMessage =
            string.Format(
            @"From: <toby@redmond.hsgincubator.com>
To: <frank@nhind.hsgincubator.com>, <joe@nhind.hsgincubator.com>
Subject: Unknown Users Text Message
Message-ID: {0}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Yo. Wassup?", Guid.NewGuid());

        public const string MultiToMessage =
            @"From: <toby@redmond.hsgincubator.com>
To: <biff@nhind.hsgincubator.com>, <bob@nhind.hsgincubator.com>, <gm2552@securehealthemail.com>, <nimbu@redmond.hsgincubator.com>, <pongo@redmond.hsgincubator.com>, <ryan@securehealthemail.com>, <frank@nhind.hsgincubator.com>
Subject: Simple Text Message
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Yo. Wassup?";

        //
        // From dual-use cert sender stored in config store to single-use cert receiver. 
        //
        public static string TestMessageDualUseToSingleUse =
                    @"From: <toby@redmond.hsgincubator.com>
To: drjoe@dev.singleuse.lab
Subject: Simple Text Message to HSM recipient
Message-ID: {0}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Yo. Wassup?";

        //
        // From single-use cert sender, to dual-use cert receiver stored in config store 
        //
        public static string TestMessageSingleUseToDualUse =
                    @"From: <toby@dev.singleuse.lab>
To: toby@redmond.hsgincubator.com
Subject: Simple Text Message to HSM recipient
Message-ID: {0}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Yo. Wassup?";

        //
        // From dual-use cert sender stored in config store to single-use cert receiver (keys stored in HSM). 
        //
        public static string TestMessageDualToHsm =
                    @"From: <toby@redmond.hsgincubator.com>
To: drjoe@hsm.DirectInt.lab
Subject: Simple Text Message to HSM recipient
Message-ID: {0}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Yo. Wassup?";

        //
        // From dual-use cert sender stored in config store to single-use certs (multiple domains) receiver (keys stored in HSM). 
        //
        public static string TestMessageDualToHsmMulti =
                    @"From: <toby@redmond.hsgincubator.com>
To: drjoe@hsm.DirectInt.lab, hobojoe@fha-crossover.DirectInt.lab
Subject: Simple Text Message to HSM recipient
Message-ID: {0}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Yo. Wassup?";

        //
        // From single-use cert sender (keys stored in HSM), to dual-use cert receiver stored in config store 
        //
        public static string TestMessageHsmToSoft =
                    @"From: drjoe@hsm.DirectInt.lab
To: <toby@redmond.hsgincubator.com>
Subject: Simple Text Message to HSM recipient
Message-ID: {0}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Yo. Wassup?";

        //
        // From single-use cert sender (keys stored in HSM), o single-use cert receiver (keys stored in HSM). 
        //
        public static string TestMessageHsmToHsm =
                    @"From: drjoe@hsm.DirectInt.lab
To: HoboJoe@hsm.DirectInt.lab
Subject: Simple Text Message to HSM recipient
Message-ID: {0}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Yo. Wassup?";

        public static string RecipientHostsMixedPublicCerts =
            @"From: <toby @redmond.hsgincubator.com>
To: <hobojoe@mixed.directmix.lab>
Subject: Simple Text Message
Message-ID: {0}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Recipient hosting both a hardware cert and a software cert issued by two different anchors.
";

        public const string TestPickupFolder = @"c:\inetpub\mailroot\testPickup";
        public const string TestIncomingFolder = @"c:\inetpub\mailroot\incoming";

        public SmtpAgentTester(){
            
            Directory.CreateDirectory(TestPickupFolder);
        }

        public void CleanMessages(SmtpAgentSettings settings)
        {
            CleanMessages(TestPickupFolder);
            CleanMessages(settings.Incoming);
            CleanMessages(settings.Outgoing);
            CleanMessages(settings.RawMessage);
            settings.IncomingRoutes.ToList().ForEach(
                route => {
                            if(route as FolderRoute != null)
                            {   
                                CleanMessages(((FolderRoute)route).CopyFolders);
                            }
                });
        }

        public void CleanMonitor()
        {
            var mdnManager = CreateConfigStore().Mdns;
            mdnManager.RemoveAll();
        }

        private void CleanMessages(string path)
        {
            var files = Directory.GetFiles(path);
            foreach(var file in files)
            {
                File.Delete(file);
            }
        }
        private void CleanMessages(string[] path)
        {
            foreach (var s in path)
            {
                CleanMessages(s);
            }
        }
        private void CleanMessages(MessageProcessingSettings settings)
        {
            if (settings == null) return;
            CleanMessages(settings.CopyFolder);
        }

        public IEnumerable<string> FileMessages(string pickupFolder)
        {
            foreach (var file in Directory.GetFiles(pickupFolder))
            {
                yield return file;
                File.Delete(file);
            }
        }

        public IEnumerable<string> PickupMessages()
        {
            foreach (var file in Directory.GetFiles(TestPickupFolder))
            {
                yield return file;
                File.Delete(file);
            } 
        }

        public IEnumerable<string> IncomingMessages()
        {
            foreach (var file in Directory.GetFiles(TestIncomingFolder))
            {
                yield return file;
                File.Delete(file);
            }
        }

        internal string MakeFilePath(string subPath)
        {
            return Path.Combine(Directory.GetCurrentDirectory(), subPath);
        }
        
        internal CDO.Message LoadMessage(string text)
        {
            return Extensions.LoadCDOMessageFromText(text);
        }
        
        internal CDO.Message LoadMessage(CDO.Message source)
        {
            return this.LoadMessage(source.GetMessageText());
        }
        
        internal void VerifyOutgoingMessage(CDO.Message message)
        {
            Assert.True(string.IsNullOrEmpty(message.Subject));
         
            ContentType contentType = new ContentType(message.GetContentType());
            Assert.True(SMIMEStandard.IsContentEncrypted(contentType));
        }
        
        internal void VerifyIncomingMessage(CDO.Message message)
        {
            ContentType contentType = new ContentType(message.GetContentType());
            Assert.False(SMIMEStandard.IsContentEncrypted(contentType));            
        }

        internal void VerifyMdnIncomingMessage(CDO.Message message)
        {
            var envelope = new CDOSmtpMessage(message).GetEnvelope();
            Assert.True(envelope.Message.IsMDN());
        }

        internal void VerifyDSNIncomingMessage(CDO.Message message)
        {
            var envelope = new CDOSmtpMessage(message).GetEnvelope();
            Assert.True(envelope.Message.IsDSN());
        }

        
        internal void ProcessEndToEnd(SmtpAgent agent, Message msg, out OutgoingMessage outgoing, out IncomingMessage incoming)
        {
            outgoing = agent.SecurityAgent.ProcessOutgoing(new MessageEnvelope(msg));
            incoming = agent.SecurityAgent.ProcessIncoming(new MessageEnvelope(outgoing.SerializeMessage()));            
        }
        
        protected string GetSettingsPath(string fileName)
        {
            string relativePath = Path.Combine("SmtpAgentTestFiles", fileName);
            relativePath = MakeFilePath(relativePath);
            return relativePath;
        }

        protected static Mdn BuildMdnQueryFromMdn(CDO.Message message)
        {
            var messageEnvelope = new CDOSmtpMessage(message).GetEnvelope();
            Assert.True(messageEnvelope.Message.IsMDN());
            var notification = MDNParser.Parse(messageEnvelope.Message);
            var originalMessageId = notification.OriginalMessageID;
            string originalSender = messageEnvelope.Recipients[0].Address;
            string originalRecipient = messageEnvelope.Sender.Address;
            return new Mdn(originalMessageId, originalRecipient, originalSender);
        }

        
        protected static Mdn BuildQueryFromDSN(CDO.Message message)
        {
            var messageEnvelope = new CDOSmtpMessage(message).GetEnvelope();
            Assert.True(messageEnvelope.Message.IsDSN());
            var mimeMessage = messageEnvelope.Message;
            var messageId = mimeMessage.IDValue;
            string sender = messageEnvelope.Sender.Address;
            string recipient = messageEnvelope.Recipients[0].Address;
            return new Mdn(messageId, recipient, sender);
        }

        protected static ConfigStore CreateConfigStore()
        {
            return new ConfigStore(ConnectionString);
        }

        internal CDO.Message RunEndToEndTest(CDO.Message message, SmtpAgent smtpAgent)
        {
            smtpAgent.ProcessMessage(message);
            message = LoadMessage(message);
            VerifyOutgoingMessage(message);

            smtpAgent.ProcessMessage(message);
            message = LoadMessage(message);

            if (smtpAgent.Settings.InternalMessage.EnableRelay)
            {
                VerifyIncomingMessage(message);
            }
            else
            {
                VerifyOutgoingMessage(message);
            }
            return message;
        }

        internal void RunMdnOutBoundProcessingTest(CDO.Message message, SmtpAgent smtpAgent)
        {
            VerifyMdnIncomingMessage(message);      //Plain Text
            smtpAgent.ProcessMessage(message);        //Encrypts
            VerifyOutgoingMessage(message);    //Mdn looped back
        }

        internal void RunMdnInBoundProcessingTest(CDO.Message message, SmtpAgent smtpAgent)
        {
            VerifyOutgoingMessage(message);         //Encryted Message
            smtpAgent.ProcessMessage(message);        //Decrypts Message
            VerifyMdnIncomingMessage(message);    //Mdn looped back
        }

        private static void TestMdnTimelyAndReliableExtensionField(NotificationMessage message, bool exists)
        {
            var mdn = MDNParser.Parse(message);
            TestMdnTimelyAndReliableExtensionField(mdn, exists);
        }

        protected static void TestMdnTimelyAndReliableExtensionField(Notification mdn, bool exists)
        {
            Console.WriteLine(mdn.Disposition);

            if (exists)
            {
                Assert.NotNull(mdn.SpecialFields[MDNStandard.DispositionOption_TimelyAndReliable]);
            }
            else
            {
                Assert.True(mdn.SpecialFields == null || mdn.SpecialFields[MDNStandard.DispositionOption_TimelyAndReliable] == null);
            }
        }
    }

    public class DummySmtpMessage : ISmtpMessage
    {
        public bool HasEnvelope
        {
            get { throw new NotImplementedException(); }
        }

        public MessageEnvelope GetEnvelope()
        {
            throw new NotImplementedException();
        }
        
        public string GetMessageText()
        {
            throw new NotImplementedException();
        }
        
        public string GetMailFrom()
        {
            throw new NotImplementedException();
        }

        public string GetRcptTo()
        {
            throw new NotImplementedException();
        }

        public void SetRcptTo(DirectAddressCollection recipients)
        {
            throw new NotImplementedException();
        }

        public void Update(string messageText)
        {
            throw new NotImplementedException();
        }

        public void Accept()
        {
            throw new NotImplementedException();
        }

        public void Reject()
        {
            throw new NotImplementedException();
        }

        public void Abort()
        {
            throw new NotImplementedException();
        }

        public void SaveToFile(string filePath)
        {
            throw new NotImplementedException();
        }
    }
}