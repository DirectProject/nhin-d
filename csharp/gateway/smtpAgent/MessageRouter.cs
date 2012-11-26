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
using System.Linq;
using System.Collections.Generic;
using System.Xml.Serialization;
using System.IO;
using Health.Direct.Agent;
using Health.Direct.Common.Diagnostics;
using Health.Direct.Config.Store;
using Health.Direct.Common.Extensions;
using System.Threading;

namespace Health.Direct.SmtpAgent
{
    /// <summary>
    /// A simple Message Router
    ///  Currently, aps AddressType ==> Route
    ///  
    /// The router assumes that you are NOT going to alter routes or tables at runtime, so it eschews locks
    /// If you need to update the routing tables on the fly, then you will need to rework this or write a new router
    /// </summary>
    public class MessageRouter : IEnumerable<Route>
    {
        AgentDiagnostics m_diagnostics;
        Dictionary<string, Route> m_routes;   // addressType, messageRouteSettings
        
        internal MessageRouter(AgentDiagnostics diagnostics)
        {
            m_diagnostics = diagnostics;
            m_routes = new Dictionary<string, Route>(StringComparer.OrdinalIgnoreCase);
        }
        
        internal ILogger Logger
        {
            get
            {
                return m_diagnostics.Logger;
            }
        }
        
        public int Count
        {
            get
            {
                return m_routes.Count;
            }
        }
        
        /// <summary>
        /// Return the route for an address type
        /// </summary>
        public Route this[string addressType]
        {
            get
            {
                if (string.IsNullOrEmpty(addressType))
                {
                    return null;
                }
                
                Route route = null;
                if (!m_routes.TryGetValue(addressType ?? string.Empty, out route))
                {
                    route = null;
                }
                
                return route;
            }
        }

        public void Init(IEnumerable<Route> routes)
        {
            if (routes == null)
            {
                throw new ArgumentNullException("routes");
            }

            foreach(Route route in routes)
            {                
                m_routes[route.AddressType] = route;
                route.Init();
            }
        }
        
        /// <summary>
        /// Outgoing messages are routed based off envelope.Recipients
        /// </summary>
        public void Route(ISmtpMessage message, OutgoingMessage envelope)
        {
            if (message == null)
            {
                throw new ArgumentNullException("message");
            }
            if (envelope == null)
            {
                throw new ArgumentNullException("envelope");
            }

            if (envelope.HasRecipients)
            {
                this.Route(message, envelope.Recipients, null);
            }
        }
        
        /// <summary>
        /// For incoming messages, we only route to DomainRecipients
        /// </summary>
        /// <param name="message">message</param>
        /// <param name="envelope">message envelope</param>
        /// <param name="routedRecipients">(Optional) - if not null, returns a list of recipients who matched routes</param>
        public void Route(ISmtpMessage message, IncomingMessage envelope, DirectAddressCollection routedRecipients)
        {
            if (message == null)
            {
                throw new ArgumentNullException("message");
            }
            if (envelope == null)
            {
                throw new ArgumentNullException("envelope");
            }            
            if (envelope.HasDomainRecipients)
            {
                this.Route(message, envelope.DomainRecipients, routedRecipients);
            }
        }

        void Route(ISmtpMessage message, DirectAddressCollection recipients, DirectAddressCollection routedRecipients)
        {
            Dictionary<string, Route> matchedRoutes = new Dictionary<string, Route>(StringComparer.OrdinalIgnoreCase);
            int i = 0;
            //
            // First, find all routes that match
            // We'll remove recipients that were routed from the recipients list so 
            // SMTP server does not itself try to deliver to them
            // 
            while (i < recipients.Count)
            {
                DirectAddress recipient = recipients[i];
                Address address = recipient.Tag as Address;
                if (address != null)
                {
                    Route route = this[address.Type];
                    if (route != null)
                    {
                        matchedRoutes[address.Type] = route;
                        recipients.RemoveAt(i);
                        if (routedRecipients != null)
                        {
                            recipient.Tag = route.AddressType;  // Reference for failed delivery
                            routedRecipients.Add(recipient);    // Add the routed recipient to the list
                        }
                        continue;
                    }
                }

                ++i;
            }              
            if (matchedRoutes.Count == 0)
            {
                return;
            }
            
            this.Route(message, matchedRoutes.Values);          
        }
        
        void Route(ISmtpMessage message, IEnumerable<Route> matchedRoutes)
        {
            foreach (Route route in matchedRoutes)
            {
                this.Route(message, route);
            }
        }
        
        void Route(ISmtpMessage message, Route route)
        {
            try
            {
                if (!route.Process(message))
                {
                    route.FailedDelivery = true;
                    m_diagnostics.Logger.Error("Routing Error {0}", route.AddressType);
                }
            }
            catch(Exception ex)
            {
                m_diagnostics.Logger.Error("Routing Error {0}, {1}", route.AddressType, ex);
            }
        }
        
        public IEnumerator<Route> GetEnumerator()
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