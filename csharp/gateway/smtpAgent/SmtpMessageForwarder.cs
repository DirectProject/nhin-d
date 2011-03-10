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
using System.Xml.Serialization;
using Health.Direct.Common.Container;
using Health.Direct.Common.Routing;

namespace Health.Direct.SmtpAgent
{
    /// <summary>
    /// A sample receiver that can forward the given message to another Smtp Server/Port combination
    /// You can use this receiver with a PluginRoute
    /// </summary>
    /*
        <Receiver>
            <TypeName>Health.Direct.SmtpAgent.SmtpMessageForwarder, Health.Direct.SmtpAgent</TypeName>
            <Settings>
                <AddressType>SMTP</AddressType>
                <Server>foo.xyz</Server>
                <Port>33</Port>
            </Settings>
          </Receiver>
     */
    public class SmtpMessageForwarder : IReceiver<ISmtpMessage>, IPlugin
    {
        SmtpSettings m_settings;
        
        public SmtpMessageForwarder()
        {
        }
        
        public SmtpSettings Settings
        {
            get
            {
                return  m_settings;
            }
            set
            {
                if (value == null)
                {
                    throw new SmtpAgentException(SmtpAgentError.InvalidSmtpForwarderSettings);
                }
                value.Validate();
                m_settings = value;
            }
        }
                
        public void Init(PluginDefinition pluginDef)
        {
            this.Settings = pluginDef.DeserializeSettings<SmtpSettings>();
        }

        public bool Receive(ISmtpMessage message)
        {
            CDOSmtpMessage cdoSmtpMessage = message as CDOSmtpMessage;
            CDO.Message cdoMessage = null;
            if (cdoSmtpMessage != null)
            {
                cdoMessage = cdoSmtpMessage.InnerMessage;
            }
            if (cdoMessage == null)
            {
                cdoMessage = Extensions.LoadCDOMessageFromText(message.GetMessageText());
            }

            cdoMessage.Send(this.Settings.Server, this.Settings.Port);
            return true;
        }
    }

    [XmlType("SmtpSettings")]    
    public class SmtpSettings
    {
        public SmtpSettings()
        {
        }
        /// <summary>
        /// Target Smtp Server
        /// </summary>
        [XmlElement]
        public string Server
        {
            get;
            set;
        }

        /// <summary>
        /// Target port (optional). Ignored if leq 0
        /// </summary>
        [XmlElement]
        public int Port
        {
            get;
            set;
        }

        public void Validate()
        {
            if (string.IsNullOrEmpty(this.Server))
            {
                throw new SmtpAgentException(SmtpAgentError.InvalidSmtpForwarderSettings);
            }
        }
    }
}
