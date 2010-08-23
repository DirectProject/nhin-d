using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;
using System.ServiceModel;

namespace NHINDirect.Config.Client
{
    /// <summary>
    /// I can't believe we have to write this manually. We need configuration to create WCF Bindings. 
    /// Since we will run within SmtpSvc, there is no app.config. 
    /// One could create an inetinfo.exe.config, I suppose, but that seems equally fishy
    /// The guidance from the WCF team is to roll your own
    /// </summary>
    [XmlType]
    public class ClientSettings
    {
        int m_maxReceivedMessageSize = int.MaxValue;   // No limits by default
        string m_url;
        EndpointAddress m_endpoint;
        BasicHttpBinding m_binding;
        
        public ClientSettings()
        {
        }
        
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
                    throw new ArgumentException();
                }
                m_url = value;
                m_endpoint = new EndpointAddress(m_url);
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
                if (value <= 0)
                {
                    throw new ArgumentException();
                }
                m_maxReceivedMessageSize = value;
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
                if (m_binding == null)
                {
                    m_binding = BindingFactory.CreateBasic(m_maxReceivedMessageSize);
                }
                
                return m_binding;
            }
        }
        
        public void Validate()
        {
            if (string.IsNullOrEmpty(this.Url))
            {
                throw new ArgumentException("Invalid ServiceUrl");
            }
        }
    }
}
