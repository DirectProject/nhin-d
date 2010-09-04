using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Xml.Serialization;
using NHINDirect.Config.Client;

namespace NHINDirect.Config.Command
{
    [XmlRoot("ConsoleSettings")]
    public class ConsoleSettings
    {
        public ConsoleSettings()
        {
        }
        
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
