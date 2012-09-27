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
using System.Text;
using Health.Direct.Agent.Tests;
using Health.Direct.Config.Client;
using Xunit;

namespace Health.Direct.SmtpAgent.Tests
{
    public class TestSmtpAgentDSNs : SmtpAgentTester
    {
        SmtpAgent m_agent;


        static TestSmtpAgentDSNs()
        {
            AgentTester.EnsureStandardMachineStores();        
        }

        public TestSmtpAgentDSNs()
        {
            //m_agent = SmtpAgentFactory.Create(base.GetSettingsPath("TestSmtpAgentConfigService.xml"));
            //m_agent = SmtpAgentFactory.Create(base.GetSettingsPath("TestSmtpAgentConfigServiceProd.xml"));
            m_agent = SmtpAgentFactory.Create(GetSettingsPath("TestSmtpAgentConfig.xml"));
        }


        /// <summary>
        /// Generation of DSN bounce messages for rejected outgoing message for security and trust reasons
        /// </summary>
        [Fact]
        public void TestFailedDSN_SecurityAndTrustOutGoingOnly()
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
            var sendingMessage = LoadMessage(ContainsUntrustedRecipientMessage);
            Assert.DoesNotThrow(() => RunEndToEndTest(sendingMessage));

            //
            // grab the clear text dsn and delete others.
            //
            bool foundDsn = false;
            foreach (var pickupMessage in PickupMessages())
            {
                string messageText = File.ReadAllText(pickupMessage);
                if (messageText.Contains("message/delivery-status"))
                {
                    foundDsn = true;
                    Assert.DoesNotThrow(() => RunMdnOutBoundProcessingTest(LoadMessage(messageText)));
                }
            }
            Assert.True(foundDsn);

            //
            // Now the messages are encrypted and can be handled
            //
            foundDsn = false;
            foreach (var pickupMessage in PickupMessages())
            {
                foundDsn = true;
                string messageText = File.ReadAllText(pickupMessage);
                CDO.Message message = LoadMessage(messageText);
                Assert.DoesNotThrow(() => RunMdnInBoundProcessingTest(message));
            }
            Assert.True(foundDsn);
            m_agent.Settings.InternalMessage.EnableRelay = false;
        }

        /// <summary>
        /// Generation of DSN bounce messages for messages that cannot be delivered via incomingRoute.
        /// </summary>
        [Fact]
        public void TestFailedDsnOutGoingOnly()
        {
            Assert.False(true, "Not implemented");
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

    }
}
