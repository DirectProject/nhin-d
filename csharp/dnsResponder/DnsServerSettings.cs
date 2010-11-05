/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Chris Lomonico  chris.lomonico@surescripts.com
 
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
    public class DnsServerSettings : ConfigurationSection
    {
        public const short DefaultMaxRequestSize = 1024 * 16;
        public const byte DefaultMaxQuestionCount = 1;
        
        IPEndPoint m_endpoint;
        
        public DnsServerSettings()
        {
        }
        
        [XmlElement]
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
        
        [XmlElement]
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
        
        [XmlIgnore]
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
        
        [XmlElement]
        [ConfigurationProperty("TcpServerSettings", IsRequired = false)]
        public SocketServerSettings TcpServerSettings
        {
            get
            {

                if (this["TcpServerSettings"] == null)
                {
                    this["TcpServerSettings"] = new SocketServerSettings();
                }
                return (SocketServerSettings)this["TcpServerSettings"];
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

        [XmlElement]
        [ConfigurationProperty("UdpServerSettings", IsRequired = false)]
        public SocketServerSettings UdpServerSettings
        {
            get
            {

                if (this["UdpServerSettings"] == null)
                {
                    this["UdpServerSettings"] = new SocketServerSettings();
                }
                return (SocketServerSettings)this["UdpServerSettings"];
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

        [XmlElement]
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
        
        [XmlElement]
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
                        
        public void Validate()
        {
            this.TcpServerSettings.Validate();
            if (this.MaxQuestionCount <= 0)
            {
                throw new ArgumentException("MaxQuestionCount");
            }            
            if (this.MaxRequestSize <= 0)
            {
                throw new ArgumentException("MaxRequestSize");
            }
        }
    }
}