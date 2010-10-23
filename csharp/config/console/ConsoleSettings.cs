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