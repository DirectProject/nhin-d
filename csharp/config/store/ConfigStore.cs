/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace NHINDirect.Config.Store
{
    public class ConfigStore
    {
        public const string Namespace = "http://www.nhindirect.org/config/store/082010";
        
        public static int DefaultTimeoutSeconds = 5;
        
        string m_connectString;
        int m_timeout;
        DomainManager m_domains;
        AddressManager m_addresses;
        CertificateManager m_certificates;
        AnchorManager m_anchors;
        
        public ConfigStore(string connectString)
            : this(connectString, DefaultTimeoutSeconds)
        {
        }
        
        public ConfigStore(string connectString, int timeout)
        {
            if (string.IsNullOrEmpty(connectString))
            {
                throw new ArgumentException("connectString");
            }
            if (timeout <= 0)
            {
                throw new ArgumentException("timeout");
            }
            m_timeout = timeout;
            m_connectString = connectString;
            m_domains = new DomainManager(this);
            m_addresses = new AddressManager(this);
            m_certificates = new CertificateManager(this);
            m_anchors = new AnchorManager(this);
        }        
                
        public DomainManager Domains
        {
            get
            {
                return m_domains;
            }
        }

        public AddressManager Addresses
        {
            get
            {
                return m_addresses;
            }
        }
        
        public CertificateManager Certificates
        {
            get
            {
                return m_certificates;
            }
        }        
        
        public AnchorManager Anchors
        {
            get
            {
                return m_anchors;
            }
        }
                                
        public ConfigDatabase CreateContext()
        {
            ConfigDatabase db = new ConfigDatabase(m_connectString);
            db.CommandTimeout = m_timeout;
            return db;
        }

        public ConfigDatabase CreateReadContext()
        {
            ConfigDatabase db = new ConfigDatabase(m_connectString);
            db.CommandTimeout = m_timeout;
            db.ObjectTrackingEnabled = false;            
            return db;
        }
    }
}
