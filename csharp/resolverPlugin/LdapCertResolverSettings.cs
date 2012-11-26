/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook    jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Net;
using System.Xml.Serialization;
using Health.Direct.Agent.Config;
using Health.Direct.Common.Certificates;

namespace Health.Direct.ResolverPlugins
{
    /// <summary>
    /// Configuration for a LDAP store-based certificate resolver.
    /// </summary>
    [XmlType("LdapCertificateStore")]
    public class LdapCertResolverSettings : CertResolverSettings
    {

        bool m_cache = false;

        /// <summary>
        /// Creates an instance. Normally called through XML deserialization.
        /// </summary>
        public LdapCertResolverSettings()
        {
        }

        /// <summary>
        /// The IP address (in <c>string</c> form) used by the resolver.
        /// </summary>
        [XmlElement]
        public string ServerIP
        {
            get;
            set;
        }

        /// <summary>
        /// The Backup IP address (in <c>string</c> form) used by the resolver.
        /// </summary>
        [XmlElement]
        public string BackupServerIP
        {
            get;
            set;
        }

        
        /// <summary>
        /// The timeout interval used by the resolver.
        /// </summary>
        [XmlElement("Timeout")]
        public int TimeoutMilliseconds
        {
            get;
            set;
        }

        /// <summary>
        /// Cache results to improve lookup performance. Default is false.
        /// </summary>
        [XmlElement]
        public bool Cache
        {
            get
            {
                return m_cache;
            }
            set
            {
                m_cache = value;
            }
        }


        /// <summary>
        /// Validates configuration settings.
        /// </summary>
        public override void Validate()
        {
            //nothing to validate
            //ServerIP and (BackupServerIP enhancement) are not required.  The LdapCertResolver discovers network bound dns entries if not supplied.
        }   
                     
        /// <summary>
        /// Creates the maachine store based certificate resolver.
        /// </summary>
        /// <returns>An instance of a machine-based certificate store resolver.</returns>
        public override ICertificateResolver CreateResolver()
        {
            Validate();

            if (string.IsNullOrEmpty(ServerIP) && string.IsNullOrEmpty(BackupServerIP))
            {
                return new LdapCertResolver(null,
                                        TimeSpan.FromMilliseconds(TimeoutMilliseconds)
                                        , m_cache);
            }

            if (string.IsNullOrEmpty(BackupServerIP))
            {
                return CreateResolver(ServerIP);
            }

            CertificateResolverCollection resolvers = new CertificateResolverCollection();
            resolvers.TryNextWhen = CertificateResolverCollection.TryNextCriteria.Exception;
            resolvers.Add(this.CreateResolver(this.ServerIP));
            resolvers.Add(this.CreateResolver(this.BackupServerIP));
            return resolvers;
            
        }

        ICertificateResolver CreateResolver(string ip)
        {
            return new LdapCertResolver(IPAddress.Parse(ip),
                                        TimeSpan.FromMilliseconds(TimeoutMilliseconds)
                                        , m_cache);
            
        }
    }
}