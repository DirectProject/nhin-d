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
using System.Collections.Generic;
using System.Net.Mail;

using Health.Direct.Agent;
using Health.Direct.Agent.Tests;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mime;

using Xunit;
using Xunit.Extensions;

namespace Health.Direct.SmtpAgent.Tests
{
    public class TestHandler : SmtpAgentTester
    {
        MessageArrivalEventHandler m_handler;
        
        static TestHandler()
        {
            AgentTester.EnsureStandardMachineStores();
        }
        
        public TestHandler()
        {
            m_handler = new MessageArrivalEventHandler();
            m_handler.InitFromConfigFile(MakeFilePath("TestSmtpAgentConfig.xml"));
        }

        public static IEnumerable<object[]> EndToEndParams
        {
            get
            {
                yield return new[] { string.Format(TestMessage, Guid.NewGuid()) };
            }
        }

        [Fact]
        public void Test()
        {
            Assert.DoesNotThrow(() => m_handler.ProcessCDOMessage(this.LoadMessage(string.Format(TestMessage, Guid.NewGuid()))));
            Assert.Throws<OutgoingAgentException>(() => m_handler.ProcessCDOMessage(this.LoadMessage(SmtpAgentTester.BadMessage)));
        }
        
        [Theory]
        [PropertyData("EndToEndParams")]     
        public void TestEndToEnd(string messageText)
        {
            CDO.Message message = this.LoadMessage(messageText);
            
            string originalSubject = message.Subject;
            string originalContentType = message.GetContentType();
            
            //
            // Outgoing
            //
            Assert.DoesNotThrow(() => m_handler.ProcessCDOMessage(message));            
            
            message = this.LoadMessage(message); // re-load the message
            base.VerifyOutgoingMessage(message);
            //
            // Incoming
            //
            Assert.DoesNotThrow(() => m_handler.ProcessCDOMessage(message));
            
            message = this.LoadMessage(message); // re-load the message            
            base.VerifyIncomingMessage(message);
            
            Assert.True(message.Subject.Equals(originalSubject));
            Assert.True(MimeStandard.Equals(message.GetContentType(), originalContentType));             

            Message mailMessage = MailParser.ParseMessage(message.GetMessageText());
            string header = mailMessage.Headers.GetValue(SmtpAgent.XHeaders.Receivers);
            Assert.DoesNotThrow(() => MailParser.ParseAddressCollection(header));
            MailAddressCollection addresses = MailParser.ParseAddressCollection(header);
            Assert.True(addresses.Count > 0);
        }
    }
}