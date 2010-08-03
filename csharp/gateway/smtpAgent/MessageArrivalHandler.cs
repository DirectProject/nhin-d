using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Security.Cryptography.X509Certificates;
using System.Runtime.InteropServices;
using System.Net.Mail;
using NHINDirect.Agent;
using NHINDirect.Mail;
using CDO;
using ADODB;

namespace NHINDirect.SmtpAgent
{
    [ComVisible(true)]
    [Guid("D12E2532-7AD6-4fb2-9AD3-D2169C0BF5A7")]
    [InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
    public interface IMessageArrivalEventHandler
    {
        void Init(string configFilePath);
        void ProcessCDOMessage(CDO.Message message);
        void WriteLog(string message);
        void WriteError(string message);
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
        static Dictionary<string, SmtpAgent> s_agents = new Dictionary<string,SmtpAgent>(StringComparer.OrdinalIgnoreCase);
        
        SmtpAgent m_agent;

        public MessageArrivalEventHandler()
        {
               
        }
        
        internal SmtpAgent Agent
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
        
        public void Init(string configFilePath)
        {                    
            SmtpAgent agent = this.EnsureAgent(configFilePath);
            System.Threading.Interlocked.Exchange<SmtpAgent>(ref m_agent, agent);
        }

        SmtpAgent EnsureAgent(string configFilePath)
        {
            lock (s_agents)
            {
                SmtpAgent agent = null;
                if (!s_agents.TryGetValue(configFilePath, out agent))
                {
                    agent = null;
                }
                
                if (agent == null)
                {
                    try
                    {
                        agent = new SmtpAgent(configFilePath);
                        s_agents[configFilePath] = agent;
                    }
                    catch
                    {
                    }
                }
                
                return agent;
            }
        }
        
        public void ProcessCDOMessage(CDO.Message message)
        {
            this.Agent.ProcessMessage(message);
        }
                
        public void WriteLog(string message)
        {
            this.Agent.Log.WriteLine(message);
        }
        
        public void WriteError(string message)
        {
            this.Agent.Log.WriteError(message);
        }
    }    
}
