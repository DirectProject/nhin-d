/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Joe Shook       jshook@kryptiq.com
 * 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Xml;
using System.Xml.Serialization;
using System.ServiceModel;
using Health.Direct.Config.Client.CertificateService;
using Health.Direct.Config.Client.DomainManager;
using Health.Direct.Config.Client.MonitorService;
using Health.Direct.Config.Client.RecordRetrieval;
using Health.Direct.Config.Client.SettingsManager;

namespace Health.Direct.Config.Client
{
    /// <summary>
    /// I can't believe we have to write this manually. 
    /// We need configuration to create WCF Bindings. Unfortunately:
    /// Since we will run within SmtpSvc, there is no app.config. 
    /// One could create an inetinfo.exe.config, I suppose, but that seems equally fishy
    /// The guidance from the WCF team is to roll your own for now.
    /// 
    /// These properties map to the WCF BasicHttpBinding
    /// 
    /// </summary>
    [XmlType]
    public class ClientSettings
    {
        int m_maxReceivedMessageSize = int.MaxValue;   // No limits by default
        string m_url;
        bool m_secure;
        int m_receiveTimeout = -1;
        int m_sendTimeout = -1;
        EndpointAddress m_endpoint;
        BasicHttpBinding m_binding;

        /// <summary>
        /// The Service Url
        /// </summary>
        [XmlElement]
        public string Url
        {
            get
            {
                return m_url;
            }
            set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new ArgumentException("value was null or empty", "value");
                }
                m_url = value;
                m_endpoint = new EndpointAddress(m_url);
                m_binding = null;
            }
        }

        [XmlElement]
        public int MaxReceivedMessageSize
        {
            get
            {
                return m_maxReceivedMessageSize;
            }
            set
            {
                if (value < 1)
                {
                    throw new ArgumentException("value was less than 1", "value");
                }
                m_maxReceivedMessageSize = value;
            }
        }

        [XmlElement]
        public bool Secure
        {
            get
            {
                return m_secure;
            }
            set
            {
                m_secure = value;
            }
        }

        [XmlElement("ReceiveTimeout")]
        public int ReceiveTimeoutSeconds
        {
            get
            {
                return m_receiveTimeout;
            }
            set
            {
                m_receiveTimeout = value;
            }
        }

        [XmlElement("SendTimeout")]
        public int SendTimeoutSeconds
        {
            get
            {
                return m_sendTimeout;
            }
            set
            {
                m_sendTimeout = value;
            }
        }

        [XmlIgnore]
        public EndpointAddress Endpoint
        {
            get
            {
                return m_endpoint;
            }
        }

        [XmlIgnore]
        public BasicHttpBinding Binding
        {
            get
            {
                this.EnsureBinding();
                return m_binding;
            }
        }

        /// <summary>
        /// Optional settings
        /// </summary>
        [XmlAnyElement]
        public XmlNode Settings
        {
            get;
            set;
        }

        /// <summary>
        /// Are optional settings specified?
        /// </summary>
        [XmlIgnore]
        public bool HasSettings
        {
            get
            {
                return (this.Settings != null);
            }
        }

        public void SetHost(string host, int port)
        {
            Uri current = new Uri(this.Url);
            UriBuilder builder = new UriBuilder(current);
            builder.Host = host;
            if (port > 0)
            {
                builder.Port = port;
            }
            this.Url = builder.ToString();
        }

        public void Validate()
        {
            if (string.IsNullOrEmpty(this.Url))
            {
                throw new ArgumentException("Invalid ServiceUrl");
            }
        }

        void EnsureBinding()
        {
            if (m_binding != null)
            {
                return;
            }

            m_binding = BindingFactory.CreateBasic(m_maxReceivedMessageSize, m_secure);
            if (m_receiveTimeout > 0)
            {
                m_binding.ReceiveTimeout = TimeSpan.FromSeconds(m_receiveTimeout);
            }
            if (m_sendTimeout > 0)
            {
                m_binding.SendTimeout = TimeSpan.FromSeconds(m_sendTimeout);
            }
        }

        public DomainManagerClient CreateDomainManagerClient()
        {
            return new DomainManagerClient(this.Binding, this.Endpoint);
        }

        public AddressManagerClient CreateAddressManagerClient()
        {
            return new AddressManagerClient(this.Binding, this.Endpoint);
        }

        public DnsRecordManagerClient CreateDnsRecordManagerClient()
        {
            return new DnsRecordManagerClient(this.Binding, this.Endpoint);
        }

        public CertificateStoreClient CreateCertificateStoreClient()
        {
            return new CertificateStoreClient(this.Binding, this.Endpoint);
        }

        public AnchorStoreClient CreateAnchorStoreClient()
        {
            return new AnchorStoreClient(this.Binding, this.Endpoint);
        }

        public BundleStoreClient CreateBundleStoreClient()
        {
            return new BundleStoreClient(this.Binding, this.Endpoint);
        }

        public RecordRetrievalServiceClient CreateRecordRetrievalClient()
        {
            return new RecordRetrievalServiceClient(this.Binding
                , this.Endpoint);
        }
        
        public PropertyManagerClient CreatePropertyManagerClient()
        {
            return new PropertyManagerClient(this.Binding, this.Endpoint);
        }

        public BlobManagerClient CreateBlobManagerClient()
        {
            return new BlobManagerClient(this.Binding, this.Endpoint);
        }

        public MdnMonitorClient CreateMdnMonitorClient()
        {
            return new MdnMonitorClient(this.Binding, this.Endpoint);
        }
    }
}