/* 
 Copyright (c) 2017, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook      Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Configuration;
using System.ServiceModel;
using Health.Direct.Config.Client;

namespace Health.Direct.DnsResponder
{
    public class CertPolicyServiceResolverSettingsSection : ConfigurationSection
    {
        [ConfigurationProperty("ClientSettings", IsRequired = true)]
        public ClientSettingsElement ClientSettings
        {
            get
            {
                return (ClientSettingsElement)this["ClientSettings"];
            }
            set
            {
                this["ClientSettings"] = value;
            }
        }

        [ConfigurationProperty("CacheSettings", IsRequired = false)]
        public CacheSettingsElement CacheSettings
        {
            get
            {
                return (CacheSettingsElement)this["CacheSettings"];
            }
            set
            {
                this["CacheSettings"] = value;
            }
        }

        public CertPolicyResolver CreateResolver()
        {
            CertPolicyResolver resolver = new CertPolicyResolver(this);

            return resolver;
        }
    }

    public class ClientSettingsElement : ConfigurationElement
    {
        EndpointAddress m_endpoint;
        BasicHttpBinding m_binding;

        public ClientSettingsElement()
        {
        }

        [ConfigurationProperty("Url", DefaultValue = "undefined", IsRequired = true)]
        public string Url
        {
            get
            {
                return (string)this["Url"];
            }
            set
            {
                this["Url"] = value;
            }
        }

        [ConfigurationProperty("MaxReceivedMessageSize", DefaultValue = Int32.MaxValue, IsRequired = false)]
        public int MaxReceivedMessageSize
        {
            get
            {
                return (int)this["MaxReceivedMessageSize"];
            }
            set
            {
                this["MaxReceivedMessageSize"] = value;
            }
        }

        [ConfigurationProperty("Secure", DefaultValue = false, IsRequired = false)]
        public bool Secure
        {
            get
            {
                return (bool)this["Secure"];
            }
            set
            {
                this["Secure"] = value;
            }
        }

        [ConfigurationProperty("ReceiveTimeout", DefaultValue = -1, IsRequired = false)]
        public int ReceiveTimeoutSeconds
        {
            get
            {
                return (int)this["ReceiveTimeout"];
            }
            set
            {
                this["ReceiveTimeout"] = value;
            }
        }

        [ConfigurationProperty("SendTimeout", DefaultValue = -1, IsRequired = false)]
        public int SendTimeoutSeconds
        {
            get
            {
                return (int)this["SendTimeout"];
            }
            set
            {
                this["SendTimeoutSeconds"] = value;
            }
        }

        public EndpointAddress Endpoint
        {
            get
            {
                m_endpoint = new EndpointAddress(Url);
                m_binding = null;
                return m_endpoint;
            }
        }

        public BasicHttpBinding Binding
        {
            get
            {
                this.EnsureBinding();
                return m_binding;
            }
        }

        void EnsureBinding()
        {
            if (m_binding != null)
            {
                return;
            }

            m_binding = BindingFactory.CreateBasic((int)this["MaxReceivedMessageSize"], (bool)this["Secure"]);
            if ((int)this["ReceiveTimeout"] > 0)
            {
                m_binding.ReceiveTimeout = TimeSpan.FromSeconds((int)this["ReceiveTimeout"]);
            }
            if ((int)this["SendTimeout"] > 0)
            {
                m_binding.SendTimeout = TimeSpan.FromSeconds((int)this["SendTimeout"]);
            }
        }
    }

    public class CacheSettingsElement : ConfigurationElement
    {
        /// <summary>
        /// true if caching is enabled. 
        /// </summary>
        [ConfigurationProperty("Cache", DefaultValue = false, IsRequired = false)]
        public bool Cache
        {
            get
            {
                return (bool)this["Cache"];
            }
            set
            {
                this["Cache"] = value;
            }
        }

        /// <summary>
        /// The time in seconds to cache results. 
        /// </summary>
        [ConfigurationProperty("CacheTTLSeconds", DefaultValue = 5, IsRequired = false)]
        public int? CacheTTLSeconds
        {
            get
            {
                return (int)this["CacheTTLSeconds"];
            }
            set
            {
                this["CacheTTLSeconds"] = value;
            }
        }
    }
}
