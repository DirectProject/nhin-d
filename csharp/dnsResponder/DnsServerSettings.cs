/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
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
    public class DnsServerSettings
    {
        public const short DefaultMaxRequestSize = 1024 * 16;
        public const byte DefaultMaxQuestionCount = 1;
        public const int DefaultTTLSeconds = 3600;
        public const DnsResolutionMode DefaultResolutionMode = DnsResolutionMode.RecordStorageService;

        IPEndPoint m_endpoint;

        string m_address = "0.0.0.0";
        int m_port = DnsStandard.DnsPort;
        SocketServerSettings m_tcpServerSettings;
        SocketServerSettings m_udpServerSettings;
        byte m_maxQuestionCount = DefaultMaxQuestionCount;
        short m_maxRequestSize = DefaultMaxRequestSize;
        int m_defaultTTL = DefaultTTLSeconds;
        DnsResolutionMode m_resolutionMode = DefaultResolutionMode;

        [XmlElement]
        public string Address
        {
            get
            {
                return m_address; 
            }
            set
            {
                m_address = value;
                m_endpoint = null;
            }
        }
        
        [XmlElement]
        public int Port
        {
            get
            {
                return m_port;
            }
            set
            {
                m_port = value;
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
        public int DefaultTTL
        {
            get
            {
                return m_defaultTTL;
            }
            set
            {
                if (value > 0)
                {
                    m_defaultTTL = value;
                }
            }
        }

        [XmlElement]
        public DnsResolutionMode ResolutionMode
        {
            get
            {
                return m_resolutionMode;                 
            }
            set
            {
                m_resolutionMode = value;                
            }
        }

        [XmlElement]
        [ConfigurationProperty("TcpServerSettings", IsRequired = false)]
        public SocketServerSettings TcpServerSettings
        {
            get
            {

                if (m_tcpServerSettings == null)
                {
                    m_tcpServerSettings = new SocketServerSettings();
                }
                return m_tcpServerSettings;
            }
            set
            {
                if (value == null)
                {
                    throw new ArgumentNullException();
                }

                m_tcpServerSettings = value;
            }
        }

        [XmlElement]
        [ConfigurationProperty("UdpServerSettings", IsRequired = false)]
        public SocketServerSettings UdpServerSettings
        {
            get
            {

                if (m_udpServerSettings == null)
                {
                    m_udpServerSettings = new SocketServerSettings();
                }
                return m_udpServerSettings;
            }
            set
            {
                if (value == null)
                {
                    throw new ArgumentNullException();
                }

                m_udpServerSettings = value;
            }

        }            

        [XmlElement]
        public byte MaxQuestionCount
        {
            get
            {
                return m_maxQuestionCount;
            }
            set
            {
                m_maxQuestionCount = value;
            }
        }
        
        [XmlElement]
        public short MaxRequestSize
        {
            get
            {
                return m_maxRequestSize;
            }
            set
            {
                m_maxRequestSize = value;
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