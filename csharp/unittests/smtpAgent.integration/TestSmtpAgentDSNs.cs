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
using System.Net.Mime;
using Health.Direct.Agent;
using Health.Direct.Agent.Tests;
using Health.Direct.Common.Cryptography;
using Health.Direct.Common.Mail.DSN;
using Health.Direct.Common.Mail.Notifications;
using Health.Direct.Config.Client;
using Health.Direct.Config.Store;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.SmtpAgent.Integration.Tests
{
    /// <summary>
    /// run   ...\csharp\setenv.bat
    /// then  ...\csharp\msb.bat
    /// then  ...\csharp\gateway\devInstall\install_withservice.bat
    /// </summary>
    public class TestSmtpAgentDSNs : SmtpAgentTester
    {
        SmtpAgent m_agent;


        static TestSmtpAgentDSNs()
        {
            AgentTester.EnsureStandardMachineStores();        
        }

        public TestSmtpAgentDSNs()
        {
            m_agent = SmtpAgentFactory.Create(GetSettingsPath("TestSmtpAgentConfig.xml"));
        }


        /// <summary>
        /// Generation of DSN bounce messages for rejected outgoing message for security and trust reasons
        /// Select AutoDsnOption of Always and do not request TimelyAndReliable.
        /// </summary>
        [Fact]
        public void TestFailedDSN_SecurityAndTrustOutGoingOnly_AlwaysGenerate()
        {
            CleanMessages(m_agent.Settings);
            CleanMonitor();

            m_agent.Settings.InternalMessage.EnableRelay = true;
            m_agent.Settings.Notifications.AutoResponse = false; //don't care.  This is MDN specific
            m_agent.Settings.Notifications.AlwaysAck = false; //don't care.  This is MDN specific
            m_agent.Settings.Notifications.AutoDsnFailureCreation = 
                NotificationSettings.AutoDsnOption.Always.ToString(); 
            //m_agent.Settings.AddressManager = new ClientSettings();
            //m_agent.Settings.AddressManager.Url = "http://localhost:6692/DomainManagerService.svc/Addresses";
            m_agent.Settings.MdnMonitor = new ClientSettings();
            m_agent.Settings.MdnMonitor.Url = "http://localhost:6692/MonitorService.svc/Dispositions";

            //
            // Process loopback messages.  Leaves un-encrypted mdns in pickup folder
            // Go ahead and pick them up and Process them as if they where being handled
            // by the SmtpAgent by way of (IIS)SMTP hand off.
            //
            var sendingMessage = LoadMessage(ContainsUntrustedRecipientMessageNoTandR);
            Assert.DoesNotThrow(() => RunEndToEndTest(sendingMessage));

            //
            // grab the clear text dsn and delete others.
            // Process them as outgoing messages
            //
            bool foundDsn = false;
            foreach (var pickupMessage in PickupMessages())
            {
                string messageText = File.ReadAllText(pickupMessage);
                if (messageText.Contains("message/delivery-status"))
                {
                    foundDsn = true;
                    Assert.DoesNotThrow(() => RunMdnOutBoundProcessingTest(LoadMessage(messageText)));

                    //
                    // assert not in the monitor store.
                    // DSN messages are not monitored.
                    //
                    var queryMdn = BuildQueryFromDSN(LoadMessage(messageText));
                    queryMdn.Status = MdnStatus.Started;
                    var mdnManager = CreateConfigStore().Mdns;
                    var mdn = mdnManager.Get(queryMdn.MdnIdentifier);
                    Assert.Null(mdn);

                }
            }
            Assert.True(foundDsn);

            //
            // Now the messages are encrypted and can be handled as inbound messages.
            //
            foundDsn = false;
            foreach (var pickupMessage in PickupMessages())
            {
                foundDsn = true;
                string messageText = File.ReadAllText(pickupMessage);
                CDO.Message message = LoadMessage(messageText);
                Assert.DoesNotThrow(() => RunMdnInBoundProcessingTest(message));
                var dsnMessage = new CDOSmtpMessage(message).GetEnvelope();
                Assert.True(dsnMessage.Message.IsDSN());
                Assert.False(dsnMessage.Message.IsMDN());
            }
            Assert.True(foundDsn);

            //Ensure no MDNs where created by the DSN.
            Assert.True(PickupMessages().Count() == 0);

            m_agent.Settings.InternalMessage.EnableRelay = false;
        }


        /// <summary>
        /// Generation of DSN bounce messages for rejected outgoing message for security and trust reasons
        /// Select AutoDsnOption of Always and do not request TimelyAndReliable.
        /// Only one recipient.
        /// This interesting because in the past if no trusted recipients exist the message is dropped
        /// in the bad messages folder.
        /// </summary>
        [Theory]
        [PropertyData("UntrustedRecipientMessages")]
        public void TestFailedDSN_SecurityAndTrustOutGoingOnly_AlwaysGenerate_AllRecipientsRejected(
            string untrustedRecipientMessage
            , List<DSNPerRecipient> perRecipientExpected)
        {
            CleanMessages(m_agent.Settings);
            CleanMonitor();

            m_agent.Settings.InternalMessage.EnableRelay = true;
            m_agent.Settings.Notifications.AutoResponse = false; //don't care.  This is MDN specific
            m_agent.Settings.Notifications.AlwaysAck = false; //don't care.  This is MDN specific
            m_agent.Settings.Notifications.AutoDsnFailureCreation =
                NotificationSettings.AutoDsnOption.Always.ToString();
            //m_agent.Settings.AddressManager = new ClientSettings();
            //m_agent.Settings.AddressManager.Url = "http://localhost:6692/DomainManagerService.svc/Addresses";
            m_agent.Settings.MdnMonitor = new ClientSettings();
            m_agent.Settings.MdnMonitor.Url = "http://localhost:6692/MonitorService.svc/Dispositions";

            //
            // Process loopback messages.  Leaves un-encrypted mdns in pickup folder
            // Go ahead and pick them up and Process them as if they where being handled
            // by the SmtpAgent by way of (IIS)SMTP hand off.
            //
            var sendingMessage = LoadMessage(untrustedRecipientMessage);
            Assert.Equal(
                string.Format("Error={0}", AgentError.NoTrustedRecipients), 
                Assert.Throws<OutgoingAgentException>(() => m_agent.ProcessMessage(sendingMessage)).Message
                );
            
            //No trusted recipients so not encrypted.
            ContentType contentType = new ContentType(sendingMessage.GetContentType());
            Assert.False(SMIMEStandard.IsContentEncrypted(contentType));


            //
            // grab the clear text dsn and delete others.
            // Process them as outgoing messages
            //
            bool foundDsn = false;
            foreach (var pickupMessage in PickupMessages())
            {
                string messageText = File.ReadAllText(pickupMessage);
                if (messageText.Contains("message/delivery-status"))
                {
                    foundDsn = true;
                    Assert.DoesNotThrow(() => RunMdnOutBoundProcessingTest(LoadMessage(messageText)));

                    //
                    // assert not in the monitor store.
                    // DSN messages are not monitored.
                    //
                    var queryMdn = BuildQueryFromDSN(LoadMessage(messageText));
                    queryMdn.Status = MdnStatus.Started;
                    var mdnManager = CreateConfigStore().Mdns;
                    var mdn = mdnManager.Get(queryMdn.MdnIdentifier);
                    Assert.Null(mdn);

                }
            }
            Assert.True(foundDsn);

            //
            // Now the messages are encrypted and can be handled as inbound messages.
            //
            foundDsn = false;
            foreach (var pickupMessage in PickupMessages())
            {
                foundDsn = true;
                string messageText = File.ReadAllText(pickupMessage);
                CDO.Message message = LoadMessage(messageText);
                Assert.DoesNotThrow(() => RunMdnInBoundProcessingTest(message));
                var dsnMessage = new CDOSmtpMessage(message).GetEnvelope();
                Assert.True(dsnMessage.Message.IsDSN());
                Assert.False(dsnMessage.Message.IsMDN());

                var dsn = DSNParser.Parse(dsnMessage.Message);
                foreach (var perRecipient in dsn.PerRecipient)
                {
                    Assert.Equal(perRecipientExpected.Count, dsn.PerRecipient.Count());
                    string finalRecipient = perRecipient.FinalRecipient.Address;
                    var expectedPerRecipient = perRecipientExpected.Find(d => d.FinalRecipient.Address == finalRecipient);
                    Assert.Equal(expectedPerRecipient.Action, perRecipient.Action);
                    Assert.Equal(expectedPerRecipient.Status, perRecipient.Status);
                }
            }
            Assert.True(foundDsn);

            //Ensure no MDNs where created by the DSN.
            Assert.True(PickupMessages().Count() == 0);

            m_agent.Settings.InternalMessage.EnableRelay = false;
        }

        /// <summary>
        /// Generation of DSN bounce messages for rejected outgoing message for security and trust reasons
        /// if the message request Timeley and Reliable.  
        /// Select AutoDsnOption of TimelyAndReliable.
        /// Mime message in this test does not request TimelyAndReliable.
        /// </summary>
        [Fact]
        public void TestFailedDSN_SecurityAndTrustOutGoingOnly_TimelyAndReliable_missingRequest()
        {
            CleanMessages(m_agent.Settings);
            CleanMonitor();

            m_agent.Settings.InternalMessage.EnableRelay = true;
            m_agent.Settings.Notifications.AutoResponse = true;
            m_agent.Settings.Notifications.AlwaysAck = true;
            m_agent.Settings.Notifications.AutoDsnFailureCreation =
                NotificationSettings.AutoDsnOption.TimelyAndReliable.ToString();
            //m_agent.Settings.AddressManager = new ClientSettings();
            //m_agent.Settings.AddressManager.Url = "http://localhost:6692/DomainManagerService.svc/Addresses";
            m_agent.Settings.MdnMonitor = new ClientSettings();
            m_agent.Settings.MdnMonitor.Url = "http://localhost:6692/MonitorService.svc/Dispositions";

            //
            // Process loopback messages.  Leaves un-encrypted mdns in pickup folder
            // Go ahead and pick them up and Process them as if they where being handled
            // by the SmtpAgent by way of (IIS)SMTP hand off.
            //
            var sendingMessage = LoadMessage(ContainsUntrustedRecipientMessageNoTandR);
            Assert.DoesNotThrow(() => RunEndToEndTest(sendingMessage));

            //
            // grab the clear text dsn and delete others.
            // Process them as outgoing messages
            //
            bool foundDsn = false;
            foreach (var pickupMessage in PickupMessages())
            {
                string messageText = File.ReadAllText(pickupMessage);
                if (messageText.Contains("message/delivery-status"))
                {
                    foundDsn = true;
                    Assert.DoesNotThrow(() => RunMdnOutBoundProcessingTest(LoadMessage(messageText)));

                    //
                    // assert not in the monitor store.
                    // DSN messages are not monitored.
                    //
                    var queryMdn = BuildQueryFromDSN(LoadMessage(messageText));
                    var mdnManager = CreateConfigStore().Mdns;
                    var mdn = mdnManager.Get(queryMdn.MdnIdentifier);
                    Assert.Null(mdn);

                }
            }
            Assert.False(foundDsn);

            m_agent.Settings.InternalMessage.EnableRelay = false;
        }


        /// <summary>
        /// Generation of DSN bounce messages for rejected outgoing message for security and trust reasons
        /// Select AutoDsnOption of TimelyAndReliable and request TimelyAndReliable.
        /// </summary>
        [Fact]
        public void TestFailedDSN_SecurityAndTrustOutGoingOnly_GenerateOnlyIfRequested()
        {
            CleanMessages(m_agent.Settings);
            CleanMonitor();

            m_agent.Settings.InternalMessage.EnableRelay = true;
            m_agent.Settings.Notifications.AutoResponse = true;
            m_agent.Settings.Notifications.AlwaysAck = true;
            //
            // Do not need to set AutoDsnOption to TimelyAndReliable as it is the default setting.
            //
            //m_agent.Settings.Notifications.AutoDsnFailureCreation =
            //    NotificationSettings.AutoDsnOption.TimelyAndReliable.ToString();
            //m_agent.Settings.AddressManager = new ClientSettings();
            //m_agent.Settings.AddressManager.Url = "http://localhost:6692/DomainManagerService.svc/Addresses";
            m_agent.Settings.MdnMonitor = new ClientSettings();
            m_agent.Settings.MdnMonitor.Url = "http://localhost:6692/MonitorService.svc/Dispositions";

            //
            // Process loopback messages.  Leaves un-encrypted mdns in pickup folder
            // Go ahead and pick them up and Process them as if they where being handled
            // by the SmtpAgent by way of (IIS)SMTP hand off.
            //
            var sendingMessage = LoadMessage(ContainsUntrustedRecipientMessageRequestTandR);
            Assert.DoesNotThrow(() => RunEndToEndTest(sendingMessage));

            //
            // grab the clear text dsn and delete others.
            // Process them as outgoing messages
            //
            bool foundDsn = false;
            foreach (var pickupMessage in PickupMessages())
            {
                string messageText = File.ReadAllText(pickupMessage);
                if (messageText.Contains("message/delivery-status"))
                {
                    foundDsn = true;
                    Assert.DoesNotThrow(() => RunMdnOutBoundProcessingTest(LoadMessage(messageText)));

                    //
                    // assert not in the monitor store.
                    // DSN messages are not monitored.
                    //
                    var queryMdn = BuildQueryFromDSN(LoadMessage(messageText));
                    queryMdn.Status = MdnStatus.Started;
                    var mdnManager = CreateConfigStore().Mdns;
                    var mdn = mdnManager.Get(queryMdn.MdnIdentifier);
                    Assert.Null(mdn);

                }
            }
            Assert.True(foundDsn);

            //
            // Now the messages are encrypted and can be handled as inbound messages.
            //
            foundDsn = false;
            foreach (var pickupMessage in PickupMessages())
            {
                foundDsn = true;
                string messageText = File.ReadAllText(pickupMessage);
                CDO.Message message = LoadMessage(messageText);
                Assert.DoesNotThrow(() => RunMdnInBoundProcessingTest(message));
                var dsnMessage = new CDOSmtpMessage(message).GetEnvelope();
                Assert.True(dsnMessage.Message.IsDSN());
                Assert.False(dsnMessage.Message.IsMDN());
            }
            Assert.True(foundDsn);

            //Ensure no MDNs where created by the DSN.
            Assert.True(PickupMessages().Count() == 0);

            m_agent.Settings.InternalMessage.EnableRelay = false;
        }
        
        /// <summary>
        /// Generation of DSN bounce messages for messages that cannot be delivered via incomingRoute.
        /// Need more research to figure out this scenario.
        /// </summary>
        [Theory]
        [PropertyData("UnDeliverableRecipientMessages")]
        public void TestFinalDestinationDelivery(string unDeliverableRecipientMessage
            , List<DSNPerRecipient> perRecipientExpected)
        {
            CleanMessages(m_agent.Settings);
            CleanMonitor();

            m_agent.Settings.InternalMessage.EnableRelay = true;
            m_agent.Settings.Notifications.AutoResponse = true;
            m_agent.Settings.Notifications.AlwaysAck = true;
            //
            // Do not need to set AutoDsnOption to TimelyAndReliable as it is the default setting.
            //
            //m_agent.Settings.Notifications.AutoDsnFailureCreation =
            //    NotificationSettings.AutoDsnOption.TimelyAndReliable.ToString();
            m_agent.Settings.AddressManager = new ClientSettings();
            m_agent.Settings.AddressManager.Url = "http://localhost:6692/DomainManagerService.svc/Addresses";
            m_agent.Settings.MdnMonitor = new ClientSettings();
            m_agent.Settings.MdnMonitor.Url = "http://localhost:6692/MonitorService.svc/Dispositions";

            foreach (FolderRoute route in m_agent.Settings.IncomingRoutes.Where(route => route.AddressType == "Throw"))
            {
                route.CopyMessageHandler = ThrowCopy;
            }
 
            //
            // Process loopback messages.  Leaves un-encrypted mdns in pickup folder
            // Go ahead and pick them up and Process them as if they where being handled
            // by the SmtpAgent by way of (IIS)SMTP hand off.
            //
            var sendingMessage = LoadMessage(unDeliverableRecipientMessage);
            Assert.DoesNotThrow(() => RunEndToEndTest(sendingMessage));

            var foundDsn = false;
            foreach (var pickupMessage in PickupMessages())
            {
                string messageText = File.ReadAllText(pickupMessage);
                CDO.Message cdoMessage = LoadMessage(messageText);
                var message = new CDOSmtpMessage(cdoMessage).GetEnvelope();
                if(message.Message.IsDSN())
                {
                    foundDsn = true;

                    var dsn = DSNParser.Parse(message.Message);
                    foreach (var perRecipient in dsn.PerRecipient)
                    {
                        Assert.Equal(perRecipientExpected.Count, dsn.PerRecipient.Count());
                        string finalRecipient = perRecipient.FinalRecipient.Address;
                        var expectedPerRecipient =
                            perRecipientExpected.Find(d => d.FinalRecipient.Address == finalRecipient);
                        Assert.Equal(expectedPerRecipient.Action, perRecipient.Action);
                        Assert.Equal(expectedPerRecipient.Status, perRecipient.Status);
                    }
                }

            }
            Assert.True(foundDsn);


            m_agent.Settings.InternalMessage.EnableRelay = false;
        }


        static bool ThrowCopy(ISmtpMessage message, string destinationFolder)
        {
            throw new DirectoryNotFoundException(destinationFolder);
        }

        void RunMdnOutBoundProcessingTest(CDO.Message message)
        {
            VerifyDSNIncomingMessage(message);      //Plain Text
            m_agent.ProcessMessage(message);        //Encrypts
            VerifyOutgoingMessage(message);    //Mdn looped back
        }

        void RunMdnInBoundProcessingTest(CDO.Message message)
        {
            VerifyOutgoingMessage(message);         //Encryted Message
            m_agent.ProcessMessage(message);        //Decrypts Message
            VerifyDSNIncomingMessage(message);    //Mdn looped back
        }

        CDO.Message RunEndToEndTest(CDO.Message message)
        {
            m_agent.ProcessMessage(message);
            message = LoadMessage(message);
            VerifyOutgoingMessage(message);

            m_agent.ProcessMessage(message);
            message = LoadMessage(message);

            if (m_agent.Settings.InternalMessage.EnableRelay)
            {
                VerifyIncomingMessage(message);
            }
            else
            {
                VerifyOutgoingMessage(message);
            }
            return message;
        }

        /// <summary>
        /// List of mime messages with untrusted recipients.
        /// </summary>
        public static IEnumerable<object[]> UntrustedRecipientMessages
        {
            get
            {
                yield return new object[]
                                 {
                                     string.Format(@"From: <toby@redmond.hsgincubator.com>
To: <drbob@dontknowyou.com>
Subject: Bad Text Message
Message-ID: {0}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Bad message?", Guid.NewGuid())
             , new List<DSNPerRecipient>
             {
                new DSNPerRecipient(DSNStandard.DSNAction.Failed, 5, DSNStandard.DSNStatus.UNSECURED_STATUS, new MailAddress("drbob@dontknowyou.com"))
             }
                    };

                yield return new object[]
                                 {
                                     string.Format(@"From: <toby@redmond.hsgincubator.com>
To: <xyz@Direct.NoAnchor.Hobo.lab>
Subject: Bad Text Message
Message-ID: {0}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Bad message?", Guid.NewGuid())
             , new List<DSNPerRecipient>
             {
                new DSNPerRecipient(DSNStandard.DSNAction.Failed, 5, DSNStandard.DSNStatus.UNTRUSTED_STATUS, new MailAddress("xyz@Direct.NoAnchor.Hobo.lab"))
             }
                                 };

                yield return new object[]
                                 {
                                     string.Format(@"From: <toby@redmond.hsgincubator.com>
To: <xyz@Direct.NoAnchor.Hobo.lab>, drbob@dontknowyou.com
Subject: Bad Text Message
Message-ID: {0}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Bad message?", Guid.NewGuid())
             , new List<DSNPerRecipient>
             {
                new DSNPerRecipient(DSNStandard.DSNAction.Failed, 5, DSNStandard.DSNStatus.UNTRUSTED_STATUS, new MailAddress("xyz@Direct.NoAnchor.Hobo.lab")),
                new DSNPerRecipient(DSNStandard.DSNAction.Failed, 5, DSNStandard.DSNStatus.UNSECURED_STATUS, new MailAddress("drbob@dontknowyou.com"))
             }
                    };
            }
        }


        /// <summary>
        /// List of mime messages with untrusted recipients.
        /// </summary>
        public static IEnumerable<object[]> UnDeliverableRecipientMessages
        {
            get
            {
                yield return new object[]
                                 {
            string.Format(@"From: <toby@redmond.hsgincubator.com>
To: throw@nhind.hsgincubator.com
Subject: Bad Text Message
Message-ID: {0}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Disposition-Notification-Options: X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true
Content-Type: text/plain

Bad message?", Guid.NewGuid())
             , new List<DSNPerRecipient>
             {
                new DSNPerRecipient(DSNStandard.DSNAction.Failed, 5, DSNStandard.DSNStatus.DELIVERY_OTHER, new MailAddress("throw@nhind.hsgincubator.com"))
             }
                    };

                yield return new object[]
                                 {
                                     string.Format(@"From: <toby@redmond.hsgincubator.com>
To: throw@nhind.hsgincubator.com, <biff@nhind.hsgincubator.com>, <bob@nhind.hsgincubator.com>
Subject: Bad Text Message
Message-ID: {0}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Bad message?", Guid.NewGuid())
             , new List<DSNPerRecipient>
             {
                new DSNPerRecipient(DSNStandard.DSNAction.Failed, 5, DSNStandard.DSNStatus.DELIVERY_OTHER, new MailAddress("throw@nhind.hsgincubator.com"))
             }
                                 };

                yield return new object[]
                                 {
                                     string.Format(@"From: <toby@redmond.hsgincubator.com>
To: throw@nhind.hsgincubator.com, <biff@nhind.hsgincubator.com>, <bob@nhind.hsgincubator.com>, throw@redmond.hsgincubator.com
Subject: Bad Text Message
Message-ID: {0}
Date: Mon, 10 May 2010 14:53:27 -0700
MIME-Version: 1.0
Content-Type: text/plain

Bad message?", Guid.NewGuid())
             , new List<DSNPerRecipient>
             {
                new DSNPerRecipient(DSNStandard.DSNAction.Failed, 5, DSNStandard.DSNStatus.DELIVERY_OTHER, new MailAddress("throw@nhind.hsgincubator.com")),
                new DSNPerRecipient(DSNStandard.DSNAction.Failed, 5, DSNStandard.DSNStatus.DELIVERY_OTHER, new MailAddress("throw@redmond.hsgincubator.com"))
             }
                    };
            
            }
        }
    }
}
