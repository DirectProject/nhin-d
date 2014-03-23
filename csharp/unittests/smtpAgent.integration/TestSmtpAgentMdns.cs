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
using Health.Direct.Agent;
using Health.Direct.Agent.Tests;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mail.Notifications;
using Health.Direct.Common.Mime;
using Health.Direct.Config.Client;
using Health.Direct.Config.Store;
using Xunit;

namespace Health.Direct.SmtpAgent.Integration.Tests
{
    /// <summary>
    /// run   ...\csharp\setenv.bat
    /// then  ...\csharp\msb.bat
    /// then  ...\csharp\gateway\devInstall\install_withservice.bat
    /// </summary>
    public class TestSmtpAgentMDNs : SmtpAgentTester
    {
        SmtpAgent m_agent;

        static TestSmtpAgentMDNs()
        {
            AgentTester.EnsureStandardMachineStores();        
        }

        public TestSmtpAgentMDNs()
        {
            //m_agent = SmtpAgentFactory.Create(base.GetSettingsPath("TestSmtpAgentConfigService.xml"));
            //m_agent = SmtpAgentFactory.Create(base.GetSettingsPath("TestSmtpAgentConfigServiceProd.xml"));
            m_agent = SmtpAgentFactory.Create(GetSettingsPath("TestSmtpAgentConfig.xml"));

        }

        
        /// <summary>
        /// Ensure MdnMonitor starts and MDN process types are updated.
        ///      Includes test for blocked dulicate MDNS.
        /// Request for final destination notification not sent.
        ///      missing this header Disposition-Notification-Options: X-DIRECT-FINAL-DESTINATION-DEeLIVERY=optional,true
        /// Gateway not set up to be the final destination.
        ///      Settings.Notifications.GatewayIsDestination = false
        /// </summary>
        [Fact]
        public void TestEndToEndStartMdnMonitor()
        {
            CleanMessages(m_agent.Settings);
            m_agent.Settings.InternalMessage.EnableRelay = true;
            m_agent.Settings.Notifications.AutoResponse = true;
            m_agent.Settings.Notifications.AlwaysAck = true;
            m_agent.Settings.MdnMonitor = new ClientSettings();
            m_agent.Settings.MdnMonitor.Url = "http://localhost:6692/MonitorService.svc/Dispositions";

            //
            // Process loopback messages.  Leaves un-encrypted mdns in pickup folder
            // Go ahead and pick them up and Process them as if they where being handled
            // by the SmtpAgent by way of (IIS)SMTP hand off.
            //
            var sendingMessage = LoadMessage(string.Format(TestMessage, Guid.NewGuid()));
            Assert.DoesNotThrow(() => RunEndToEndTest(sendingMessage));

            //
            // grab the clear text mdns and delete others.
            //
            bool foundMdns = false;
            foreach (var pickupMessage in PickupMessages())
            {
                string messageText = File.ReadAllText(pickupMessage);
                if (messageText.Contains("disposition-notification"))
                {
                    foundMdns = true;
                    Assert.DoesNotThrow(() => RunMdnOutBoundProcessingTest(LoadMessage(messageText)));
                }
            }
            Assert.True(foundMdns);

            //
            // Now the messages are encrypted and can be handled
            // Processed Mdn's will be recorded by the MdnMonitorService
            //
            foundMdns = false;
            foreach (var pickupMessage in PickupMessages())
            {
                foundMdns = true;
                string messageText = File.ReadAllText(pickupMessage);
                CDO.Message message = LoadMessage(messageText);
                Assert.DoesNotThrow(() => RunMdnInBoundProcessingTest(message));



                //
                // Prove we cannot send duplicate MDNs.
                //
                var duplicateMessage = LoadMessage(messageText);
                VerifyOutgoingMessage(duplicateMessage);         //Encryted Message
                m_agent.ProcessMessage(duplicateMessage);        //Decrypts Message
                //This proves we could not process the message because it is still encrypted
                //Could possibly check to see if it was dropped.  This integration test is getting ugly...
                VerifyOutgoingMessage(duplicateMessage);         //Encryted Message



                
                TestMdnsInProcessedStatus(message, false);
            }
            Assert.True(foundMdns);
            m_agent.Settings.InternalMessage.EnableRelay = false;
        }


        /// <summary>
        /// Ensure Mdns start process and dispatch
        /// Requesting delivery notification.
        ///      Disposition-Notification-Options: X-DIRECT-FINAL-DESTINATION-DEeLIVERY=optional,true
        /// Gateway not set up to be the final destination.  So test will manufacture dipatched MDNS representing
        /// the ultimate destination.
        ///      Settings.Notifications.GatewayIsDestination = false
        /// </summary>
        [Fact]
        public void TestEndToEndTimelyAndReliableStartMdnMonitor()
        {
            CleanMessages(m_agent.Settings);
            m_agent.Settings.InternalMessage.EnableRelay = true;
            m_agent.Settings.Notifications.AutoResponse = true;
            m_agent.Settings.Notifications.AlwaysAck = true;
            m_agent.Settings.MdnMonitor = new ClientSettings();
            m_agent.Settings.MdnMonitor.Url = "http://localhost:6692/MonitorService.svc/Dispositions";

            //
            // Process loopback messages.  Leaves un-encrypted mdns in pickup folder
            // Go ahead and pick them up and Process them as if they where being handled
            // by the SmtpAgent by way of (IIS)SMTP hand off.

            string textMessage = string.Format(TestMessageTimelyAndReliableMissingTo, Guid.NewGuid());


            var sendingMessage = LoadMessage(textMessage);

            Assert.DoesNotThrow(() => RunEndToEndTest(sendingMessage));

            //
            // grab the clear text mdns and delete others.
            //
            bool foundMdns = false;
            foreach (var pickupMessage in PickupMessages())
            {
                string messageText = File.ReadAllText(pickupMessage);
                if (messageText.Contains("disposition-notification"))
                {
                    foundMdns = true;
                    var cdoMessage = LoadMessage(messageText);
                    Assert.DoesNotThrow(() => RunMdnOutBoundProcessingTest(cdoMessage));
                }
            }
            Assert.True(foundMdns);
            //
            // Now the messages are encrypted and can be handled
            // Processed Mdn's will be recorded by the MdnMonitorService
            //
            bool foundFiles = false;
            foreach (var pickupMessage in PickupMessages())
            {
                foundFiles = true;
                string messageText = File.ReadAllText(pickupMessage);
                CDO.Message message = LoadMessage(messageText);
                Assert.DoesNotThrow(() => RunMdnInBoundProcessingTest(message));
                
                TestMdnsInProcessedStatus(message, true);
            }
            Assert.True(foundFiles);

            //
            // Prepare a Dispatched MDN manually as if this was a edge client
            //

            //
            // Original message needed to create RequestNotification which is 
            // needed to use the CreateNotificationMessages
            //
            var mailMessage = MailParser.ParseMessage(textMessage);
            mailMessage.RequestNotification();
            textMessage = mailMessage.ToString();

            var incoming = new IncomingMessage(textMessage);

            List<NotificationMessage> notificationMessages = GetNotificationMessages(incoming, MDNStandard.NotificationType.Dispatched);
            Assert.True(notificationMessages.Count == 2);
            
            //
            // Simulating a destination client sending a dispatched MDN
            //
            foreach (var notification in notificationMessages)
            {
                TestMdnTimelyAndReliableExtensionField(notification, true);

                var dispatchText = MimeSerializer.Default.Serialize(notification);
                CDO.Message message = LoadMessage(dispatchText);
                
                RunEndToEndTest(message);

                var duplicateMessage = LoadMessage(dispatchText);
                //
                // Prove we cannot send duplicate MDNs.
                //
                RunMdnOutBoundProcessingTest(duplicateMessage);
                VerifyOutgoingMessage(duplicateMessage);         //Encryted Message
                m_agent.ProcessMessage(duplicateMessage);        //Decrypts Message
                //This proves we could not process the message because it is still encrypted
                //Could possibly check to see if it was dropped.  This integration test is getting ugly...
                VerifyOutgoingMessage(duplicateMessage);         //Encryted Message

            }

            m_agent.Settings.InternalMessage.EnableRelay = false;
        }

        /// <summary>
        /// Ensure Mdns start process and dispatch if requesting delivery notification (TimelyAndReliable)
        /// Requesting delivery notification.
        ///      Disposition-Notification-Options: X-DIRECT-FINAL-DESTINATION-DEeLIVERY=optional,true
        /// Gateway is set up to be the final destination.  So Gatway is the final destination.
        ///      Settings.Notifications.GatewayIsDestination = true
        /// 
        /// Note: the message is missing the Disposition-Notification-To header.  But we accept it
        /// and respond to the sender.
        /// </summary>
        [Fact]
        public void TestEndToEnd_GatewayIsDestination_Is_True_And_TimelyAndReliable_Requested_No_DispositionNotificationToHeader()
        {
            CleanMessages(m_agent.Settings);
            m_agent.Settings.InternalMessage.EnableRelay = true;
            m_agent.Settings.Outgoing.EnableRelay = true;
            m_agent.Settings.Notifications.AutoResponse = true;
            m_agent.Settings.Notifications.AlwaysAck = true;
            m_agent.Settings.Notifications.GatewayIsDestination = true;
            m_agent.Settings.MdnMonitor = new ClientSettings();
            m_agent.Settings.MdnMonitor.Url = "http://localhost:6692/MonitorService.svc/Dispositions";

            //
            // Process loopback messages.  Leaves un-encrypted mdns in pickup folder
            // Go ahead and pick them up and Process them as if they where being handled
            // by the SmtpAgent by way of (IIS)SMTP hand off.
            //
            string textMessage = string.Format(TestMessageTimelyAndReliableMissingTo, Guid.NewGuid());
            var sendingMessage = LoadMessage(textMessage);
            var startEnvelope = new CDOSmtpMessage(sendingMessage).GetEnvelope();

            Assert.Null(startEnvelope.Message.Headers[MDNStandard.Headers.DispositionNotificationTo]);
            Assert.DoesNotThrow(() => RunEndToEndTest(sendingMessage));

            //
            // grab the clear text mdns and delete others.
            //
            foreach (var pickupMessage in PickupMessages())
            {
                string messageText = File.ReadAllText(pickupMessage);
                if (messageText.Contains("disposition-notification"))
                {
                    Assert.DoesNotThrow(() => RunMdnOutBoundProcessingTest(LoadMessage(messageText)));
                }
            }

            //
            // Now the messages are encrypted and can be handled
            // Processed Mdn's will be recorded by the MdnMonitorService
            //
            foreach (var pickupMessage in PickupMessages())
            {
                string messageText = File.ReadAllText(pickupMessage);
                CDO.Message message = LoadMessage(messageText);
                       
                Assert.DoesNotThrow(() => RunMdnInBoundProcessingTest(message));
                var envelope = new CDOSmtpMessage(message).GetEnvelope();
                var mdn = MDNParser.Parse(envelope.Message);
                if(mdn.Disposition.Notification == MDNStandard.NotificationType.Processed)
                {
                    TestMdnTimelyAndReliableExtensionField(mdn, false);
                }
                else if(mdn.Disposition.Notification == MDNStandard.NotificationType.Dispatched)
                {
                    TestMdnTimelyAndReliableExtensionField(mdn, true);
                }
                else
                {
                    Assert.True(false, "Unexpected Notification Type: " + mdn.Disposition.Notification);
                }
            }

            //
            // Test Mdn data is dispatched
            // Remember above (Settings.Notifications.GatewayIsDestination = true)
            //
            var messageEnvelope = new CDOSmtpMessage(sendingMessage).GetEnvelope();
            foreach (var recipient in messageEnvelope.Recipients)
            {
                var queryMdn = new Mdn(messageEnvelope.Message.IDValue
                        , recipient.Address
                        , messageEnvelope.Message.FromValue
                        , MdnStatus.Dispatched);


                var mdnManager = CreateConfigStore().Mdns;
                var mdn = mdnManager.Get(queryMdn.MdnIdentifier);
                Assert.NotNull(mdn);
                Assert.Equal("dispatched", mdn.Status, StringComparer.OrdinalIgnoreCase);
                Assert.Equal(true, mdn.NotifyDispatched);
                
            }

            m_agent.Settings.InternalMessage.EnableRelay = false;
        }


        /// <summary>
        /// Ensure Mdns start process and dispatch if requesting delivery notification (TimelyAndReliable)
        /// Requesting delivery notification.
        ///      Disposition-Notification-Options: X-DIRECT-FINAL-DESTINATION-DEeLIVERY=optional,true
        /// Gateway is set up to be the final destination.  So Gatway is the final destination.
        ///      Settings.Notifications.GatewayIsDestination = true
        /// 
        /// Note: the message is missing the Disposition-Notification-To header.  But we accept it
        /// and respond to the sender.
        /// </summary>
        [Fact]
        public void TestEndToEnd_GatewayIsDestination_Is_True_And_TimelyAndReliable_Requested()
        {
            CleanMessages(m_agent.Settings);
            m_agent.Settings.InternalMessage.EnableRelay = true;
            m_agent.Settings.Outgoing.EnableRelay = true;
            m_agent.Settings.Notifications.AutoResponse = true;
            m_agent.Settings.Notifications.AlwaysAck = true;
            m_agent.Settings.Notifications.GatewayIsDestination = true;
            m_agent.Settings.MdnMonitor = new ClientSettings();
            m_agent.Settings.MdnMonitor.Url = "http://localhost:6692/MonitorService.svc/Dispositions";

            //
            // Process loopback messages.  Leaves un-encrypted mdns in pickup folder
            // Go ahead and pick them up and Process them as if they where being handled
            // by the SmtpAgent by way of (IIS)SMTP hand off.
            //
            string textMessage = string.Format(TestMessageTimelyAndReliable, Guid.NewGuid());
            var sendingMessage = LoadMessage(textMessage);
            var startEnvelope = new CDOSmtpMessage(sendingMessage).GetEnvelope();
            Assert.NotNull(startEnvelope.Message.Headers[MDNStandard.Headers.DispositionNotificationTo]);
            var notify = startEnvelope.Message.Headers[MDNStandard.Headers.DispositionNotificationTo].Value;
            Assert.DoesNotThrow(() => RunEndToEndTest(sendingMessage));

            //
            // grab the clear text mdns and delete others.
            //
            foreach (var pickupMessage in PickupMessages())
            {
                string messageText = File.ReadAllText(pickupMessage);
                if (messageText.Contains("report-type=disposition-notification"))
                {
                    Assert.Contains("To:"+notify, messageText);
                    Assert.DoesNotThrow(() => RunMdnOutBoundProcessingTest(LoadMessage(messageText)));
                }
            }

            //
            // Now the messages are encrypted and can be handled
            // Processed Mdn's will be recorded by the MdnMonitorService
            //
            foreach (var pickupMessage in PickupMessages())
            {
                string messageText = File.ReadAllText(pickupMessage);
                CDO.Message message = LoadMessage(messageText);

                Assert.DoesNotThrow(() => RunMdnInBoundProcessingTest(message));
                var envelope = new CDOSmtpMessage(message).GetEnvelope();
                var mdn = MDNParser.Parse(envelope.Message);
                if (mdn.Disposition.Notification == MDNStandard.NotificationType.Processed)
                {
                    TestMdnTimelyAndReliableExtensionField(mdn, false);
                }
                else if (mdn.Disposition.Notification == MDNStandard.NotificationType.Dispatched)
                {
                    TestMdnTimelyAndReliableExtensionField(mdn, true);
                }
                else
                {
                    Assert.True(false, "Unexpected Notification Type: " + mdn.Disposition.Notification);
                }
            }

            //
            // Test Mdn data is dispatched
            // Remember above (Settings.Notifications.GatewayIsDestination = true)
            //
            var messageEnvelope = new CDOSmtpMessage(sendingMessage).GetEnvelope();
            foreach (var recipient in messageEnvelope.Recipients)
            {
                var queryMdn = new Mdn(messageEnvelope.Message.IDValue
                        , recipient.Address
                        , messageEnvelope.Message.FromValue
                        , MdnStatus.Dispatched);

                var mdnManager = CreateConfigStore().Mdns;
                var mdn = mdnManager.Get(queryMdn.MdnIdentifier);
                Assert.NotNull(mdn);
                Assert.Equal("dispatched", mdn.Status, StringComparer.OrdinalIgnoreCase);
                Assert.Equal(true, mdn.NotifyDispatched);
            }

            m_agent.Settings.InternalMessage.EnableRelay = false;
        }

        /// <summary>
        /// Ensure Mdns start process and dispatch if requesting delivery notification (TimelyAndReliable)
        /// Requesting delivery notification.
        ///      Disposition-Notification-Options: X-DIRECT-FINAL-DESTINATION-DEeLIVERY=optional,true
        /// Gateway is set up to be the final destination.  So Gatway is the final destination.
        ///      Settings.Notifications.GatewayIsDestination = true
        /// </summary>
        [Fact]
        public void TestEndToEnd_GatewayIsDestination_Is_True_And_TimelyAndReliable_Not_Requestd()
        {
            CleanMessages(m_agent.Settings);
            m_agent.Settings.InternalMessage.EnableRelay = true;
            m_agent.Settings.Outgoing.EnableRelay = true;
            m_agent.Settings.Notifications.AutoResponse = true;
            m_agent.Settings.Notifications.AlwaysAck = true;
            m_agent.Settings.Notifications.GatewayIsDestination = true;
            m_agent.Settings.MdnMonitor = new ClientSettings();
            m_agent.Settings.MdnMonitor.Url = "http://localhost:6692/MonitorService.svc/Dispositions";

            //
            // Process loopback messages.  Leaves un-encrypted mdns in pickup folder
            // Go ahead and pick them up and Process them as if they where being handled
            // by the SmtpAgent by way of (IIS)SMTP hand off.
            //
            string textMessage = string.Format(string.Format(TestMessage, Guid.NewGuid()), Guid.NewGuid());
            var sendingMessage = LoadMessage(textMessage);
            Assert.DoesNotThrow(() => RunEndToEndTest(sendingMessage));

            //
            // grab the clear text mdns and delete others.
            //
            foreach (var pickupMessage in PickupMessages())
            {
                string messageText = File.ReadAllText(pickupMessage);
                if (messageText.Contains("disposition-notification"))
                {
                    Assert.DoesNotThrow(() => RunMdnOutBoundProcessingTest(LoadMessage(messageText)));
                }
            }

            //
            // Now the messages are encrypted and can be handled
            // Processed Mdn's will be recorded by the MdnMonitorService
            //
            foreach (var pickupMessage in PickupMessages())
            {
                string messageText = File.ReadAllText(pickupMessage);
                CDO.Message message = LoadMessage(messageText);

                Assert.DoesNotThrow(() => RunMdnInBoundProcessingTest(message));
                var envelope = new CDOSmtpMessage(message).GetEnvelope();
                var mdn = MDNParser.Parse(envelope.Message);

                //
                // Only expect processed MDNs
                //
                Assert.Equal(MDNStandard.NotificationType.Processed, mdn.Disposition.Notification);
                TestMdnTimelyAndReliableExtensionField(mdn, false);
            }

            //
            // Test Mdn data is processed
            // Remember above (Settings.Notifications.GatewayIsDestination = true)
            // but message did not request TimelyAndReliable
            //
            var messageEnvelope = new CDOSmtpMessage(sendingMessage).GetEnvelope();
            foreach (var recipient in messageEnvelope.Recipients)
            {
                var queryMdn = new Mdn(messageEnvelope.Message.IDValue
                        , recipient.Address
                        , messageEnvelope.Message.FromValue
                        , MdnStatus.Processed);

                var mdnManager = CreateConfigStore().Mdns;
                var mdn = mdnManager.Get(queryMdn.MdnIdentifier);
                Assert.NotNull(mdn);
                Assert.Equal("processed", mdn.Status, StringComparer.OrdinalIgnoreCase);
                Assert.Equal(false, mdn.NotifyDispatched);
            }

            m_agent.Settings.InternalMessage.EnableRelay = false;
        }


        /// <summary>
        /// Even if a MDN was not recorded outgoing it can still be returned. 
        /// </summary>
        [Fact]
        public void TestMissingMdn()
        {
            CleanMessages(m_agent.Settings);
            m_agent.Settings.InternalMessage.EnableRelay = true;
            m_agent.Settings.Notifications.AutoResponse = true;
            m_agent.Settings.Notifications.AlwaysAck = true;
            m_agent.Settings.MdnMonitor = new ClientSettings();
            m_agent.Settings.MdnMonitor.Url = "http://localhost:6692/MonitorService.svc/Dispositions";

            string textMessage = string.Format(TestMessageTimelyAndReliableMissingTo, Guid.NewGuid());
            //
            // RequestNotification needed to use the CreateNotificationMessages
            //
            var mailMessage = MailParser.ParseMessage(textMessage);
            mailMessage.RequestNotification();
            textMessage = mailMessage.ToString();
            var incoming = new IncomingMessage(textMessage);

            List<NotificationMessage> notificationMessages = GetNotificationMessages(incoming, MDNStandard.NotificationType.Processed);
            Assert.True(notificationMessages.Count == 2);
            RunMdnProcessingForMissingStart(notificationMessages);

            notificationMessages = GetNotificationMessages(incoming, MDNStandard.NotificationType.Dispatched);
            Assert.True(notificationMessages.Count == 2);
            RunMdnProcessingForMissingStart(notificationMessages);
            
            notificationMessages = GetNotificationMessages(incoming, MDNStandard.NotificationType.Failed);
            Assert.True(notificationMessages.Count == 2);
            RunMdnProcessingForMissingStart(notificationMessages);

            notificationMessages = GetNotificationMessages(incoming, MDNStandard.NotificationType.Displayed); //No currently using.
            Assert.True(notificationMessages.Count == 2);
            RunMdnProcessingForMissingStart(notificationMessages);

            notificationMessages = GetNotificationMessages(incoming, MDNStandard.NotificationType.Deleted); //No currently using.
            Assert.True(notificationMessages.Count == 2);
            RunMdnProcessingForMissingStart(notificationMessages);


            m_agent.Settings.InternalMessage.EnableRelay = false;
        }

        
        
        private void RunMdnProcessingForMissingStart(IEnumerable<NotificationMessage> notificationMessages)
        {
            foreach (var notification in notificationMessages)
            {
                var mdnText = MimeSerializer.Default.Serialize(notification);
                CDO.Message message = LoadMessage(mdnText);
                m_agent.ProcessMessage(message);  //Loop back so it is encrypted       
                message = LoadMessage(message);
                VerifyOutgoingMessage(message);  //Encrypted
                m_agent.ProcessMessage(message);
                message = LoadMessage(message);  //Dycrpted
                
                VerifyIncomingMessage(message);        


                //Can't ensure message is deleted in this test because IIS SMTP is not hosting SmtpAgent.


                //
                // assert not in the monitor store.
                //
                var queryMdn = BuildMdnQueryFromMdn(LoadMessage(mdnText));
                queryMdn.Status = MdnStatus.Started;
                var mdnManager = CreateConfigStore().Mdns;
                var mdn = mdnManager.Get(queryMdn.MdnIdentifier);
                Assert.Null(mdn);
            }
        }

        private List<NotificationMessage> GetNotificationMessages(IncomingMessage incoming, MDNStandard.NotificationType notificationType)
        {
            return incoming.Message.CreateNotificationMessages(
                incoming.Recipients.AsMailAddresses(),
                sender => Notification.CreateAck
                              (
                                  new ReportingUserAgent
                                      (
                                      sender.Host
                                      , m_agent.Settings.Notifications.ProductName
                                      )
                                  , m_agent.Settings.Notifications.Text
                                  , notificationType)
                ).ToList();
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
                var smtpMessage = new CDOSmtpMessage(message).GetEnvelope();
                VerifyIncomingMessage(message);
            }
            else
            {
                VerifyOutgoingMessage(message);
            }
            return message;
        }

        void RunMdnOutBoundProcessingTest(CDO.Message message)
        {
            VerifyMdnIncomingMessage(message);      //Plain Text
            m_agent.ProcessMessage(message);        //Encrypts
            VerifyOutgoingMessage(message);    //Mdn looped back
        }

        void RunMdnInBoundProcessingTest(CDO.Message message)
        {
            VerifyOutgoingMessage(message);         //Encryted Message
            m_agent.ProcessMessage(message);        //Decrypts Message
            VerifyMdnIncomingMessage(message);    //Mdn looped back
        }

        static void TestMdnsInProcessedStatus(CDO.Message message, bool timelyAndReliable)
        {
            var queryMdn = BuildMdnQueryFromMdn(message);
            queryMdn.Status = MdnStatus.Processed;
            var mdnManager = CreateConfigStore().Mdns;
            var mdn = mdnManager.Get(queryMdn.MdnIdentifier);
            Assert.NotNull(mdn);
            Assert.Equal("processed", mdn.Status, StringComparer.OrdinalIgnoreCase);
            Assert.Equal(timelyAndReliable, mdn.NotifyDispatched);
        }

        static void TestMdnTimelyAndReliableExtensionField(NotificationMessage message, bool exists)
        {
            var mdn = MDNParser.Parse(message);
            TestMdnTimelyAndReliableExtensionField(mdn, exists);
        }

        static void TestMdnTimelyAndReliableExtensionField(Notification mdn, bool exists)
        {
            Console.WriteLine(mdn.Disposition);
            if(exists)
            {
                Assert.NotNull(mdn.SpecialFields[MDNStandard.DispositionOption_TimelyAndReliable]);
            }
            else
            {
                Assert.True(mdn.SpecialFields == null ||  mdn.SpecialFields[MDNStandard.DispositionOption_TimelyAndReliable] == null);
            }
        }
    }
}
