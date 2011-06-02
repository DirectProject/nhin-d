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
using System.Linq;
using System.Text;
using CDO;
using ADODB;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.SmtpAgent;
using Health.Direct.Config.Tools.Command;

namespace Health.Direct.Tools.Agent
{
    /// <summary>
    /// Commands that are really useful for debugging
    /// Start the SMTP Agent - all of it - but hosted within a .NET Console
    /// You don't have to run SMTP Server to be able to step through the code. 
    /// Of course, you have to "push" messages through the agent manually by using the SmtpAgent_Process command
    /// </summary>
    public class SmtpAgentCommands
    {
        Health.Direct.SmtpAgent.SmtpAgent m_agent;

        public SmtpAgentCommands()
        {

        }

        public Health.Direct.SmtpAgent.SmtpAgent Agent
        {
            get
            {
                if (m_agent == null)
                {
                    throw new InvalidOperationException("Agent not started");
                }

                return m_agent;
            }
        }

        [Command(Name = "SmtpAgent_Start", Usage = "configFilePath")]
        public void StartAgent(string[] args)
        {
            if (m_agent != null)
            {
                return;
            }

            string configFile = args.GetRequiredValue(0);
            m_agent = SmtpAgentFactory.Create(configFile);
        }

        [Command(Name = "SmtpAgent_Stop")]
        public void StopAgent(string[] args)
        {
            m_agent = null;
        }

        [Command(Name = "SmtpAgent_Process", Usage = "messageFilepath")]
        public void ProcessMessage(string[] args)
        {
            IOFiles files = new IOFiles(args);
            CDO.Message message = Extensions.LoadCDOMessageFromText(files.Read());
            this.Agent.ProcessMessage(message);
            files.Write(message.GetMessageText());
        }

        [Command(Name = "SmtpAgent_ProcessEndToEnd", Usage = "messageFilepath")]
        public void ProcessEndtoEnd(string[] args)
        {
            IOFiles files = new IOFiles(args);
            //
            // Outgoing
            //
            CDO.Message outgoing = Extensions.LoadCDOMessageFromText(files.Read());
            this.Agent.ProcessMessage(outgoing);
            //
            // Incoming
            //
            CDO.Message incoming = Extensions.LoadCDOMessageFromText(outgoing.GetMessageText());
            this.Agent.ProcessMessage(incoming);
            //
            // Persist
            //
            files.Write(incoming.GetMessageText());
        }

        [Command(Name = "Mail_Send", Usage = "messageFilePath server port")]
        public void SendMail(string[] args)
        {
            CDOSmtpMessage smtpMessage = new CDOSmtpMessage(Extensions.LoadCDOMessage(args.GetRequiredValue(0)));
            //
            // Use SmtpRoute to get some free code coverage/easy test
            //
            SmtpMessageForwarder route = new SmtpMessageForwarder();
            SmtpSettings settings = new SmtpSettings()
            {
                Server = args.GetRequiredValue(1),
                Port = args.GetOptionalValue(2, -1)
            };
            route.Settings = settings;
            route.Receive(smtpMessage);
        }        
    }
}
