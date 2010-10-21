/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Xml.Serialization;

using NHINDirect.Agent;
using NHINDirect.Config.Store;

namespace Health.Direct.SmtpAgent
{
    public class MessageRoute : MessageProcessingSettings
    {
        [XmlElement]
        public string AddressType
        {
            get;          
            set;
        }

        public override void Validate()
        {
            base.Validate();
            if (string.IsNullOrEmpty(this.AddressType))
            {
                throw new ArgumentException("Missing address type");
            }
        }
    }

    public class MessageRouter : IEnumerable<MessageRoute>
    {
        AgentDiagnostics m_diagnostics;
        Dictionary<string, MessageRoute> m_routes;   // addressType, messageRouteSettings

        internal MessageRouter(AgentDiagnostics diagnostics)
        {
            m_diagnostics = diagnostics;
            m_routes = new Dictionary<string, MessageRoute>(StringComparer.OrdinalIgnoreCase);
        }

        /// <summary>
        /// Return the route for an address type
        /// </summary>
        public MessageRoute this[string addressType]
        {
            get
            {
                if (string.IsNullOrEmpty(addressType))
                {
                    return null;
                }
                
                MessageRoute settings = null;
                if (!m_routes.TryGetValue(addressType ?? string.Empty, out settings))
                {
                    settings = null;
                }
                return settings;
            }
        }

        public void SetRoutes(IEnumerable<MessageRoute> settings)
        {
            if (settings == null)
            {
                throw new ArgumentNullException("settings");
            }

            foreach (MessageRoute setting in settings)
            {
                setting.EnsureFolders();
                m_routes[setting.AddressType] = setting;
            }
        }
        
        /// <summary>
        /// Outgoing messages are routed based off envelope.Recipients
        /// </summary>
        public void Route(ISmtpMessage message, OutgoingMessage envelope, Action<ISmtpMessage, MessageRoute> action)
        {
            if (message == null)
            {
                throw new ArgumentNullException("message");
            }
            if (envelope == null)
            {
                throw new ArgumentNullException("envelope");
            }
            if (action == null)
            {
                throw new ArgumentNullException("action");
            }

            if (envelope.HasRecipients)
            {
                this.Route(message, envelope.Recipients, action);
            }
        }
        
        /// <summary>
        /// For incoming messages, we only route to DomainRecipients
        /// </summary>
        public void Route(ISmtpMessage message, IncomingMessage envelope, Action<ISmtpMessage, MessageRoute> action)
        {
            if (message == null)
            {
                throw new ArgumentNullException("message");
            }
            if (envelope == null)
            {
                throw new ArgumentNullException("envelope");
            }
            if (action == null)
            {
                throw new ArgumentNullException("action");
            }
            
            if (envelope.HasDomainRecipients)
            {
                this.Route(message, envelope.DomainRecipients, action);
            }
        }

        void Route(ISmtpMessage message, NHINDAddressCollection recipients, Action<ISmtpMessage, MessageRoute> action)
        {
            Dictionary<string, MessageRoute> matches = new Dictionary<string, MessageRoute>(StringComparer.OrdinalIgnoreCase);
            int i = 0;
            while (i < recipients.Count)
            {
                DirectAddress recipient = recipients[i];
                Address address = recipient.Tag as Address;
                if (address != null)
                {
                    MessageRoute route = this[address.Type];
                    if (route != null)
                    {
                        matches[address.Type] = route;
                        recipients.RemoveAt(i);
                        continue;
                    }
                }

                ++i;
            }

            foreach (MessageRoute route in matches.Values)
            {
                action(message, route);
            }
        }
        
        public IEnumerator<MessageRoute> GetEnumerator()
        {
            return m_routes.Values.GetEnumerator();
        }

        #region IEnumerable Members

        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
            return this.GetEnumerator();
        }

        #endregion
    }
}