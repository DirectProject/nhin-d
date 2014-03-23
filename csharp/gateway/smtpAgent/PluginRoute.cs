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
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Container;
using Health.Direct.Common.Routing;

namespace Health.Direct.SmtpAgent
{
    /// <summary>
    /// A PluginRoute hands ISmtpMessage to an Assembly/Type that is loaded dynamically
    /// PluginRoute works with Receivers which implement 2 interfaces:
    ///  - IReceiver_of_ISmtpMessage   // see Health.Direct.Common.Routing.IReceiver
    ///  - IPlugin        (Optional)   // see Health.Direct.Common.Container.IPlugin
    /// Look at the SmtpMessageReceiver object for an example
    /// For each route, you specify the set of receivers in Config
    /// </summary>
    /*
        Receivers are objects that must implement IReceiver<ISmtpMessage>.
        You specifiy receivers like so:
        Sample in UnitTests:
            csharp\unitTests\smtpagent\SmtpAgentTestFiles\TestPlugin.xml
            csharp\unitTests\smtpagent\TestRouter.cs
       <SmtpAgentConfig>
          .....
          <IncomingRoutes>
            ....
            <PluginRoute>
              <AddressType>SMTP</AddressType>
              <Receiver>
                <TypeName>Health.Direct.SmtpAgent.SmtpMessageForwarder, Health.Direct.SmtpAgent</TypeName>
                <Settings>
                    <AddressType>SMTP</AddressType>
                    <Server>foo.xyz</Server>
                    <Port>33</Port>
                </Settings>
              </Receiver>
              <Receiver>
                <TypeName>Health.Direct.SmtpAgent.SmtpMessageForwarder, Health.Direct.SmtpAgent</TypeName>
                <Settings>
                    <AddressType>SMTP</AddressType>
                    <Server>bar.xyz</Server>
                    <Port>39</Port>
                </Settings>
              </Receiver>
            </PluginRoute>
    */
    public class PluginRoute : Route
    {
        PluginDefinition[] m_plugins;
        LoadBalancer<ISmtpMessage> m_loadBalancer;
        
        public PluginRoute()
        {
        }
        
        /// <summary>
        /// Defines the receivers for this plugin route
        /// The receivers are loaded dynamically
        /// </summary>
        [XmlElement("Receiver")]   
        public PluginDefinition[] ReceiverDefinitions
        {
            get
            {
                return m_plugins;
            }
            set
            {
                m_plugins = value;
                this.Receivers = this.CreateReceivers();
            }
        }
        
        [XmlIgnore]
        public bool HasReceiverDefinitions
        {
            get
            {
                return (!this.ReceiverDefinitions.IsNullOrEmpty());
            }
        }

        /// <summary>
        /// Zero or more message receivers. If multiple receivers, will loadbalance over them
        /// </summary>    
        [XmlIgnore]
        public IReceiver<ISmtpMessage>[] Receivers
        {
            get;
            set;
        }

        /// <summary>
        /// Are receivers actually specified? 
        /// </summary>
        [XmlIgnore]
        public bool HasReceivers
        {
            get
            {
                return (!this.Receivers.IsNullOrEmpty());
            }
        }

        public override void Validate()
        {
            base.Validate();
            
            if (this.ReceiverDefinitions.IsNullOrEmpty())
            {
                throw new SmtpAgentException(SmtpAgentError.NoReceiversInPluginRoute);
            }
            for (int i = 0; i < this.ReceiverDefinitions.Length; ++i)
            {
                if (this.ReceiverDefinitions[i] == null || !this.ReceiverDefinitions[i].HasTypeName)
                {
                    throw new SmtpAgentException(SmtpAgentError.InvalidPluginRoute);
                }
            }            
        }
        
        public override void Init()
        {
            m_loadBalancer = new LoadBalancer<ISmtpMessage>(this.Receivers);
        }

        /// <summary>
        /// If receivers specified, hands it to the next receiver (load balanced)
        /// Otherwise, returns false
        /// </summary>
        /// <param name="message"></param>
        /// <returns></returns>
        public override bool Process(ISmtpMessage message)
        {
            return m_loadBalancer.Process(message);
        }

        IReceiver<ISmtpMessage>[] CreateReceivers()
        {
            if (!this.HasReceiverDefinitions)
            {
                return null;
            }
            
            IReceiver<ISmtpMessage>[] receivers = new IReceiver<ISmtpMessage>[this.ReceiverDefinitions.Length];
            for (int i = 0; i < this.ReceiverDefinitions.Length; ++i)
            {
                receivers[i] = this.ReceiverDefinitions[i].Create<IReceiver<ISmtpMessage>>();
            }
            
            return receivers;
        }
    }
}
