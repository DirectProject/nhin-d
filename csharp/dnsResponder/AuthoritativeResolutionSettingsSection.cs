/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Ali Emami   aliemami@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System.Configuration;
using System.Linq;
using Health.Direct.Common.DnsResolver;
using System;

namespace Health.Direct.DnsResponder
{
    public class AuthoritativeResolutionSettingsSection : ConfigurationSection
    {
        public AuthoritativeResolutionSettingsSection()
        {
        }

        public AuthoritativeResolutionSettings AsAuthoritativeResolutionSettings()
        {
            AuthoritativeResolutionSettings settings = new AuthoritativeResolutionSettings();
            settings.Cache = this.Cache;
            settings.TimeoutMilliseconds = this.TimeoutMilliseconds;
            settings.DnsResolutionPort = this.DnsResolutionPort;
            
            DnsIPEndpointSettings[] endpoints = new DnsIPEndpointSettings[this.PrimaryNameServers.Count];

            int i = 0; 
            foreach (DnsIPEndpointElement element in this.PrimaryNameServers)
            {
                endpoints[i] = element.AsDnsIPEndpointSettings();
                i++;
            }
            settings.PrimaryNameServer = endpoints;
            
            return settings;
        }

        /// <summary>
        /// Cache DNS resolution results. 
        /// </summary>
        [ConfigurationProperty("Cache", DefaultValue = "false", IsRequired = false)]
        public bool Cache
        {
            get { return (bool) this["Cache"]; }
            set { this["Cache"] = value; }
        }

        /// <summary>
        /// The timeout in milliseconds for DNS queries.
        /// </summary>
        [ConfigurationProperty("TimeoutMilliseconds", DefaultValue = 5000, IsRequired = false)]
        public int TimeoutMilliseconds
        {
            get { return (int) this["TimeoutMilliseconds"]; }
            set { this["TimeoutMilliseconds"] = value; }
        }

        /// <summary>
        /// The port used to resolve DNS requests against the authoritative name servers.
        /// </summary>
        [ConfigurationProperty("DnsResolutionPort", DefaultValue = DnsStandard.DnsPort, IsRequired = false)]
        public int DnsResolutionPort
        {
            get { return (int) this["DnsResolutionPort"]; }
            set { this["DnsResolutionPort"] = value; }
        }

        /// <summary>
        /// The primary name servers used to lookup the authoritative name servers for 
        /// a DNS query.
        /// </summary>        
        [ConfigurationProperty("PrimaryNameServers", IsRequired = true, IsDefaultCollection=false)]
        [ConfigurationCollection(typeof(DnsIPEndpointElementCollection))]
        public DnsIPEndpointElementCollection PrimaryNameServers
        {
            get
            {
                if (this["PrimaryNameServers"] == null)
                {
                    this["PrimaryNameServers"] = new DnsIPEndpointElementCollection(); 
                }

                return (DnsIPEndpointElementCollection) this["PrimaryNameServers"]; 
            }
            set
            {
                if (value == null)
                {
                    throw new ArgumentNullException("value"); 
                }

                this["PrimaryNameServers"] = value;
            }
        }

        public static AuthoritativeResolutionSettingsSection GetSection()
        {
            return ((AuthoritativeResolutionSettingsSection)ConfigurationManager.GetSection(
                "ServiceSettingsGroup/AuthoritativeResolutionSettings"));
        }
    }
}
