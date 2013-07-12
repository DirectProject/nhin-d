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
using System.IO;
using System.Net.Mail;
using Health.Direct;
using Health.Direct.Config.Tools;
using Health.Direct.Common.Mail;
using Health.Direct.Agent;
using Health.Direct.Agent.Config;
using Health.Direct.Common.Mime;
using Health.Direct.Common.Certificates;
using Health.Direct.Config.Tools.Command;
using Health.Direct.SmtpAgent;

namespace Health.Direct.Tools.Agent
{
    public class AgentCommands
    {
        DirectAgent m_agent;
        
        public AgentCommands()
        {
        }
        
        public DirectAgent Agent
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

        const string Agent_Start_Usage =
            "Start a DirectAgent - uses DirectAgent object directly. Used for debugging"
            + Constants.CRLF + "path|domain [isDomain : true if treat prev parameter as domain name, otherwise it's a config file name.]";

        [Command(Name="Agent_Start", Usage=Agent_Start_Usage)]
        public void StartAgent(string[] args)
        {
            if (m_agent != null)
            {
                return;
            }
            
            string pathOrDomain = args.GetOptionalValue(0, "nhind.hsgincubator.com");
            bool isDomain = args.GetOptionalValue<bool>(1, true);

            if (isDomain)
            {
                m_agent = new DirectAgent(pathOrDomain);
            }
            else
            {
                AgentSettings settings = AgentSettings.LoadFile(pathOrDomain);
                m_agent = settings.CreateAgent();
            }
        }
        
        [Command(Name = "Agent_Stop")]
        public void StopAgent(string[] args)
        {
            m_agent = null;   
        }        
        
        [Command(Name = "Agent_Process_Incoming")]
        public void ProcessIncoming(string[] args)
        {
            IOFiles files = new IOFiles(args);
            IncomingMessage message = this.Agent.ProcessIncoming(files.Read());
            files.Write(message.Message);
        }

        [Command(Name = "Agent_Process_Outgoing")]
        public void ProcessOutgoing(string[] args)
        {
            IOFiles files = new IOFiles(args);
            OutgoingMessage message = this.Agent.ProcessOutgoing(files.Read());
            files.Write(message.Message);
        }

        [Command(Name = "Agent_Send_Outgoing")]
        public void SendOutgoing(string[] args)
        {
            string sourceFile = args.GetRequiredValue(0);
            string smtpServer = args.GetRequiredValue(1);
            
            OutgoingMessage outgoing = this.Agent.ProcessOutgoing(File.ReadAllText(sourceFile));
            outgoing.Send(smtpServer);
        }
    }
}
