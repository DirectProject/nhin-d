/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico  chris.lomonico@surescripts.com
    Ali Emami       aliemami@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Net;
using System.Xml.Serialization;
using System.Configuration;

using Health.Direct.Common.DnsResolver;

namespace Health.Direct.DnsResponder
{
    public class DnsServerSettingsSection : ConfigurationSection
    {
        public const short DefaultMaxRequestSize = 1024 * 16;
        public const byte DefaultMaxQuestionCount = 1;
        
        IPEndPoint m_endpoint;

        public DnsServerSettingsSection()
        {
        }

        public DnsServerSettings AsDnsServerSettings()
        {
            DnsServerSettings settings = new DnsServerSettings();
            settings.Address = this.Address;
            settings.Port = this.Port;
            settings.MaxQuestionCount = this.MaxQuestionCount;
            settings.MaxRequestSize = this.MaxRequestSize;
            settings.DefaultTTL = this.DefaultTTL;
            settings.ResolutionMode = this.ResolutionMode;
            settings.TcpServerSettings = this.TcpServerSettings.AsSocketServerSettings();
            settings.UdpServerSettings = this.UdpServerSettings.AsSocketServerSettings();
            return settings;

        }

        [ConfigurationProperty("Address", DefaultValue = "0.0.0.0", IsRequired = true)]
        public string Address
        {
            get
            {
                return (this["Address"] != null) ? (string)this["Address"] : null; 
            }
            set
            {
                this["Address"] = value;
                m_endpoint = null;
            }
        }
        

        [ConfigurationProperty("Port", DefaultValue = DnsStandard.DnsPort, IsRequired = false)]
        public int Port
        {
            get
            {
                return (int)this["Port"];
            }
            set
            {
                this["Port"] = value;
                m_endpoint = null;
            }
        }
        
        public IPEndPoint Endpoint
        {
            get
            {
                if (m_endpoint == null)
                {
                    m_endpoint = new IPEndPoint(IPAddress.Parse(this.Address), this.Port);
                }
                
                return m_endpoint;
            }
        }

        [ConfigurationProperty("DefaultTTL", DefaultValue = DnsServerSettings.DefaultTTLSeconds, IsRequired = false)]
        public int DefaultTTL
        {
            get
            {
                return (int) this["DefaultTTL"];
            }
            set
            {
                this["DefaultTTL"] = value;
            }
        }

        [ConfigurationProperty("ResolutionMode", DefaultValue = DnsResolutionMode.RecordStorageService, IsRequired = false)]
        public DnsResolutionMode ResolutionMode
        {
            get
            {

                return (DnsResolutionMode) this["ResolutionMode"];
            }
            set
            {
                this["ResolutionMode"] = value;
            }
        }
        
        [ConfigurationProperty("TcpServerSettings", IsRequired = false)]
        public SocketServerSettingsElement TcpServerSettings
        {
            get
            {

                if (this["TcpServerSettings"] == null)
                {
                    this["TcpServerSettings"] = new SocketServerSettingsElement();
                }
                return (SocketServerSettingsElement)this["TcpServerSettings"];
            }
            set
            {
                if (value == null)
                {
                    throw new ArgumentNullException();
                }

                this["TcpServerSettings"] = value;
            }
        }


        [ConfigurationProperty("UdpServerSettings", IsRequired = false)]
        public SocketServerSettingsElement UdpServerSettings
        {
            get
            {

                if (this["UdpServerSettings"] == null)
                {
                    this["UdpServerSettings"] = new SocketServerSettingsElement();
                }
                return (SocketServerSettingsElement)this["UdpServerSettings"];
            }
            set
            {
                if (value == null)
                {
                    throw new ArgumentNullException();
                }

                this["UdpServerSettings"] = value;
            }

        }            


        [ConfigurationProperty("MaxQuestionCount", DefaultValue = DefaultMaxQuestionCount, IsRequired = false)]
        public byte MaxQuestionCount
        {
            get
            {
                return (byte)this["MaxQuestionCount"];
            }
            set
            {
                this["MaxQuestionCount"] = value;
            }
        }
        

        [ConfigurationProperty("MaxRequestSize", DefaultValue = DefaultMaxRequestSize, IsRequired = false)]
        public short MaxRequestSize
        {
            get
            {
                return (short)this["MaxRequestSize"];
            }
            set
            {
                this["MaxRequestSize"] = value;
            }
        }

        public static DnsServerSettingsSection GetSection()
        {
            return ((DnsServerSettingsSection)ConfigurationManager.GetSection("ServiceSettingsGroup/DnsServerSettings"));
        }
    }
}
