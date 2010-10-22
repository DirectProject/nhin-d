using System;
using System.Collections.Generic;
using System.Runtime.InteropServices;

using Health.Direct.Common.Diagnostics;

namespace Health.Direct.Sample.ScriptAgent
{
    /// <summary>
    /// Event Handlers for the SMTP Service can be written in VBScript/JScript
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
    [ClassInterface(ClassInterfaceType.AutoDual)]
    [Guid("E48C52D5-1A7C-4e66-B8AE-2B08E4505829")]
    public class SmtpAgentEventHandler
    {    
        //
        // It is possible that this COM object will get created for every request - and is therefore meant to be very lightweight. 
        // Therefore, internally, we maintain a singleton of the actual agent and just proxy calls. 
        //
        static Dictionary<string, SmtpServiceAgent> s_agents = new Dictionary<string,SmtpServiceAgent>(StringComparer.OrdinalIgnoreCase);
        
        SmtpServiceAgent m_agent;
        private ILogger m_logger;

        public SmtpAgentEventHandler()
        {
            m_logger = Log.For(this);
        }
        
        internal SmtpServiceAgent Agent
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
        
        public bool Init(string name, string configFilePath)
        {            
            if (!this.IsAgentInitialized(m_agent, name, configFilePath))
            {
                m_agent = this.EnsureAgent(name, configFilePath);
            }
            
            return (m_agent != null);
        }

        SmtpServiceAgent EnsureAgent(string name, string configFilePath)
        {
            lock (s_agents)
            {
                SmtpServiceAgent agent = null;
                if (s_agents.TryGetValue(name, out agent) && !this.IsAgentInitialized(agent, name, configFilePath))
                {
                    agent = null;
                }
                
                if (agent == null)
                {
                    try
                    {
                        agent = new SmtpServiceAgent(name, configFilePath);
                        s_agents[name] = agent;
                    }
                    catch
                    {
                    }
                }
                
                return agent;
            }
        }
        
        /// <summary>
        /// VBScript etc don't handle Overloads too well
        /// </summary>
        public bool ProcessCDOMessage(CDO.Message message, ref bool isIncoming)
        {
            try
            {
                return this.Agent.ProcessMessage(message, ref isIncoming);
            }
            catch
            {
            }
            
            return false;
        }
        
        public string ProcessCDOMessageFile(string filePath, ref bool isIncoming)
        {
            try
            {
                CDO.Message message = Extensions.LoadCDOMessage(filePath);
                string messageText = message.GetMessageText();                
                return messageText = this.Agent.ProcessMessage(messageText, ref isIncoming);
            }
            catch(Exception error)
            {
                m_logger.Error(error);
            }

            return string.Empty;
        }
        
        public string ProcessMessage(string message, string rcptTo, string mailFrom, ref bool isIncoming)
        {
            try
            {
                return this.Agent.ProcessMessage(message, rcptTo, mailFrom, ref isIncoming);
            }
            catch
            {
            }

            return string.Empty;
        }

        public string ProcessMessageRaw(string messageText, ref bool isIncoming)
        {
            try
            {
                return this.Agent.ProcessMessage(messageText, ref isIncoming);
            }
            catch
            {
            }

            return string.Empty;
        }

        bool IsAgentInitialized(SmtpServiceAgent agent, string name, string configFilePath)
        {            
            return (    agent != null 
                        &&  agent.Name.Equals(name, StringComparison.OrdinalIgnoreCase)
                        &&  agent.ConfigFilePath.Equals(configFilePath, StringComparison.OrdinalIgnoreCase)
                   );
        }
    }
}