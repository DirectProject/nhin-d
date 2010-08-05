/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Mail;
using NHINDirect.Agent.Config;
using NHINDirect.Diagnostics;
using System.Xml.Serialization;

namespace NHINDirect.SmtpAgent
{
    [XmlType("SmtpAgentConfig")]
    public class SmtpAgentSettings : AgentSettings
    {
        string m_postmasterAddress;
        RawMessageSettings m_rawMessageSettings;
        ProcessIncomingSettings m_incomingSettings;
        ProcessOutgoingSettings m_outgoingSettings;
        ProcessBadMessageSettings m_badMessageSettings;
        
        public SmtpAgentSettings()
        {
        }
        
        [XmlElement("Log")]
        public LogFileSettings LogSettings
        {
            get;
            set;
        }
        
        [XmlElement("Postmaster")]
        public string PostmasterAddress
        {
            get
            {
                return m_postmasterAddress;
            }
            set
            {
                m_postmasterAddress = value;
            }
        }
                 
        [XmlElement("RawMessage")]
        public RawMessageSettings RawMessage
        {
            get
            {
                if (m_rawMessageSettings == null)
                {
                    m_rawMessageSettings = new RawMessageSettings();
                }
                
                return m_rawMessageSettings;
            }
            set
            {
                m_rawMessageSettings = value;
            }
        }
        
        [XmlElement("ProcessIncoming")]
        public ProcessIncomingSettings Incoming
        {
            get
            {
                if (m_incomingSettings == null)
                {
                    m_incomingSettings = new ProcessIncomingSettings();
                }
                
                return m_incomingSettings;
            }
            set
            {
                m_incomingSettings = value;
            }
        }

        [XmlElement("ProcessOutgoing")]
        public ProcessOutgoingSettings Outgoing
        {
            get
            {
                if (m_outgoingSettings == null)
                {
                    m_outgoingSettings = new ProcessOutgoingSettings();
                }
                
                return m_outgoingSettings;
            }
            set
            {
                m_outgoingSettings = value;
            }
        }

        [XmlElement("BadMessage")]
        public ProcessBadMessageSettings BadMessage
        {
            get
            {
                if (m_badMessageSettings == null)
                {
                    m_badMessageSettings = new ProcessBadMessageSettings();
                }

                return m_badMessageSettings;
            }
            set
            {
                m_badMessageSettings = value;
            }
        }
        
        public override void Validate()
        {
            if (string.IsNullOrEmpty(m_postmasterAddress))
            {
                throw new ArgumentNullException("Local postmaster not specified");
            }
            if (this.LogSettings == null)
            {
                throw new ArgumentNullException("Log Settings not specified");
            }
            this.LogSettings.Validate();            
            this.RawMessage.Validate();            
            this.BadMessage.Validate();
            this.Incoming.Validate();
            this.Outgoing.Validate();
        }
        
        public static SmtpAgentSettings LoadFile(string configFilePath)
        {
            return AgentSettings.LoadFile<SmtpAgentSettings>(configFilePath);
        }
    }
}
