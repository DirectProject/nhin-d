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
using System.Runtime.InteropServices;

using Health.Direct.Common.Diagnostics;

namespace Health.Direct.SmtpAgent
{
    [ComVisible(true)]
    [Guid("D12E2532-7AD6-4fb2-9AD3-D2169C0BF5A7")]
    [InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
    public interface IMessageArrivalEventHandler
    {
        void InitFromConfigFile(string configFilePath);
        void ProcessCDOMessage(CDO.Message message);
    }

    /// <summary>
    /// Event Handlers for the SMTP Service are written in C++
    /// Those event handlers can talk to the agent using this COM Interop object
    /// 
    /// The scripts may not be cached - each script instance could be created independantly. 
    /// That means that multiple instances of this COM object can both be created and exist simultaneously
    /// To ensure that all these instances talk to a SINGLE Agent object:
    ///  - We internally maintain a singleton of the actual agent and just proxy calls. 
    ///  - Keep this object as lightweight as possible
    ///
    /// </summary>
    [ComVisible(true)]
    [Guid("974FACC9-EE64-4440-BDE8-15A331FDD297")]
    [ClassInterface(ClassInterfaceType.None)]
    public class MessageArrivalEventHandler : IMessageArrivalEventHandler
    {    
        //
        // It is possible that this COM object will get created for every request - and is therefore meant to be very lightweight. 
        // Therefore, internally, we maintain a singleton of the actual agent and just proxy calls. 
        //
        private static readonly Dictionary<string, Health.Direct.SmtpAgent.SmtpAgent> s_agents = new Dictionary<string,Health.Direct.SmtpAgent.SmtpAgent>(StringComparer.OrdinalIgnoreCase);

        // it's bad practice to lock on the object you're controlling access to.
        private static readonly object s_agentsSync = new object();


        Health.Direct.SmtpAgent.SmtpAgent m_agent;
        private ILogger m_logger;

        private ILogger Logger
        {
            get
            {
                if (m_logger == null)
                {
                    m_logger = Log.For(this);
                }
                return m_logger;
            }
        }

        internal Health.Direct.SmtpAgent.SmtpAgent Agent
        {
            get
            {
                if (m_agent == null)
                {
                    throw new InvalidOperationException("Not initialized");
                }
            
                return m_agent;
            }
        }
        
        public void InitFromConfigFile(string configFilePath)
        {
            Health.Direct.SmtpAgent.SmtpAgent agent = this.EnsureAgent(configFilePath);
            System.Threading.Interlocked.Exchange<Health.Direct.SmtpAgent.SmtpAgent>(ref m_agent, agent);
        }

        Health.Direct.SmtpAgent.SmtpAgent EnsureAgent(string configFilePath)
        {
            lock (s_agentsSync)
            {
                Health.Direct.SmtpAgent.SmtpAgent agent = null;

                if (!s_agents.TryGetValue(configFilePath, out agent))
                {
                    try
                    {
                        agent = SmtpAgentFactory.Create(configFilePath);
                        s_agents[configFilePath] = agent;
                    }
                    catch (Exception ex)
                    {
                        Logger.Fatal("While EnsuringAgent with path - " + configFilePath, ex);
                        throw;
                    }
                }
                
                return agent;
            }
        }
        
        public void ProcessCDOMessage(CDO.Message message)
        {
            try
            {
                this.Agent.ProcessMessage(message);
            }
            catch (Exception ex)
            {
                Logger.Fatal("While ProcessCDOMessage", ex);

                //
                // Paranoia of last resort. A malconfigured or malfunctioning agent should NEVER let ANY messages through
                //
                try
                {
                    message.AbortMessage();
                }
                catch (Exception ex2)
                {
                    Logger.Fatal("While aborting message", ex2);
                }

                throw;
            }
        }
    }
}