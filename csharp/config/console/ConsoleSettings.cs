using System;
using System.IO;
using System.Xml.Serialization;

using Health.Direct.Config.Client;

namespace Health.Direct.Config.Console
{
    [XmlRoot("ConsoleSettings")]
    public class ConsoleSettings
    {
        [XmlElement]
        public ClientSettings DomainManager
        {
            get;
            set;
        }

        [XmlElement]
        public ClientSettings AddressManager
        {
            get;
            set;
        }

        [XmlElement]
        public ClientSettings AnchorManager
        {
            get;
            set;
        }

        [XmlElement]
        public ClientSettings CertificateManager
        {
            get;
            set;
        }
        
        public void SetHost(string host,  int port)
        {
            this.DomainManager.SetHost(host, port);
            this.AddressManager.SetHost(host, port);
            this.CertificateManager.SetHost(host, port);
            this.AnchorManager.SetHost(host, port);
        }
        
        public void Validate()
        {
            if (this.DomainManager == null)
            {
                throw new ArgumentException("Invalid DomainManager Config");
            }
            this.DomainManager.Validate();
            if (this.AddressManager == null)
            {
                throw new ArgumentException("Invalid AddressManager Config");
            }
            this.AddressManager.Validate();
            if (this.AnchorManager == null)
            {
                throw new ArgumentException("Invalid AnchorManager Config");
            }
            this.AnchorManager.Validate();
            if (this.CertificateManager == null)
            {
                throw new ArgumentException("Invalid CertificateManager Config");
            }
            this.CertificateManager.Validate();
        }
        
        public static ConsoleSettings Load()
        {
            return ConsoleSettings.Load("ConfigConsoleSettings.xml");
        }
        
        public static ConsoleSettings Load(string path)
        {
            XmlSerializer serializer = new XmlSerializer(typeof(ConsoleSettings));
            using(Stream stream = File.OpenRead(path))
            {
                ConsoleSettings settings = (ConsoleSettings) serializer.Deserialize(stream);
                settings.Validate();
                return settings;
            }
        }
    }
}