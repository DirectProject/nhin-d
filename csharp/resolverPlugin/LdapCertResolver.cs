/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Jon Renolds     jrenolds68@gmail.com
    Joseph Shook    jshook@kryptiq.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections.Generic;
using System.DirectoryServices.Protocols;
using System.Net;
using System.Net.Mail;
using System.Net.NetworkInformation;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Dns;
using Health.Direct.Common.DnsResolver;

namespace Health.Direct.ResolverPlugins
{
    /// <summary>
    /// Implements a certificate resolver using one or more public LDAP server using DNS SRV to resolve server locations
    /// </summary>
    public class LdapCertResolver : ICertificateResolver 
    {
        private static readonly String CERT_ATTRIBUTE_BINARY = "userSMIMECertificate;binary";
        private static readonly String CERT_ATTRIBUTE = "userSMIMECertificate";
        private static readonly String NAMING_CONTEXTS_ATTRIBUTE = "namingContexts";
        private static readonly String WILDCARD_OBJECT_CLASS_SEARCH = "objectclass=*";
        private static readonly String EMAIL_ATTRIBUTE = "mail";
        private static readonly String LDAP_SRV_PREFIX = "_ldap._tcp.";

        private static readonly Int32 LdapProtoVersion = 3;

        private static readonly TimeSpan DefaultTimeout = TimeSpan.FromSeconds(5);
        private readonly bool m_cacheEnabled;
        private readonly IPAddress m_dnsServerIp;
        private readonly TimeSpan m_timeout;
        private int m_maxRetries = 1;


        /// <summary>
        /// Create an LDAP certificate resolver, using default timeout and default DNS server for SRV lookup
        /// </summary>
        public LdapCertResolver()
            : this(null, DefaultTimeout, true)
        {
        }

        /// <summary>
        /// Create an LDAP certificate resolver, using timeout and default DNS server for SRV lookup
        /// </summary>
        /// <param name="dnsServerIp">An <see cref="IPAddress"/> IP address of the DNS server to use for DNS lookups</param>
        /// <param name="timeout">Timeout value for DNS and LDAP lookups</param>
        public LdapCertResolver(IPAddress dnsServerIp, TimeSpan timeout)
            : this(dnsServerIp, timeout, true)
        {
        }

        /// <summary>
        /// Create an LDAP certificate resolver, using timeout and default DNS server for SRV lookup
        /// </summary>
        /// <param name="dnsServerIp">An <see cref="IPAddress"/> IP address of the DNS server to use for DNS lookups</param>
        /// <param name="timeout">Timeout value for DNS and LDAP lookups</param>
        /// <param name="cacheEnabled">boolean flag indicating whether or not to use the DNS client with cache</param>
        public LdapCertResolver(IPAddress dnsServerIp, TimeSpan timeout, bool cacheEnabled)
        {
            if (dnsServerIp == null)
            {
                // a DNS server was not provided, so try to resolve one from
                // an active network interfaces
                NetworkInterface[] nics = NetworkInterface.GetAllNetworkInterfaces();
                foreach (NetworkInterface ni in nics)
                {
                    if (ni.OperationalStatus == OperationalStatus.Up)
                    {
                        IPAddressCollection ips = ni.GetIPProperties().DnsAddresses;
                        foreach (IPAddress ip in ips)
                        {
                            dnsServerIp = ip;
                            break;
                        }
                    }
                    if (dnsServerIp != null)
                        break;
                }
            }
            if (dnsServerIp == null)
                throw new ArgumentNullException("dnsServerIP could not be resolved");

            if (timeout.Ticks < 0)
            {
                throw new ArgumentException("timeout value was less than zero", "timeout");
            }

            m_dnsServerIp = dnsServerIp;
            m_timeout = timeout;
            m_cacheEnabled = cacheEnabled;
        }



        /// <summary>
        /// The DNS <see cref="IPAddress"/> resolved against for SRV records.
        /// </summary>
        public IPAddress Server
        {
            get { return m_dnsServerIp; }
        }

        /// <summary>
        /// Timeout in milliseconds.
        /// </summary>
        public TimeSpan Timeout
        {
            get { return m_timeout; }
        }

        #region ICertificateResolver Members

        /// <summary>
        /// Resolves X509 certificates for a mail address.
        /// </summary>
        /// <param name="address">The <see cref="MailAddress"/> instance to resolve. </param>
        /// <returns>An <see cref="X509Certificate2Collection"/> of X509 certifiates for the address,
        /// or <c>null</c> if no certificates are found.</returns>
        public X509Certificate2Collection GetCertificates(MailAddress address)
        {
            //
            // First, try to resolve the full email address directly
            //                
            X509Certificate2Collection certs = GetCertificatesBySubect(address.Address);
            if (certs.IsNullOrEmpty())
            {
                //
                // No certificates found. Perhaps certificates are available at the the (Domain) level
                //
                certs = GetCertificatesBySubect(address.Host);
            }

            return certs;
        }

        /// <summary>
        /// Resolves X509 certificates for a domain.
        /// </summary>
        /// <param name="domain">The domain for which certificates should be resolved.</param>
        /// <returns>An <see cref="X509Certificate2Collection"/> of X509 certifiates for the address,
        /// or <c>null</c> if no certificates are found.</returns>
        public X509Certificate2Collection GetCertificatesForDomain(string domain)
        {
            if (string.IsNullOrEmpty(domain))
            {
                throw new ArgumentException("domain");
            }

            return GetCertificatesBySubect(domain);
        }

        #endregion

        /// <summary>
        /// Event to subscribe to for notification of errors.
        /// </summary>
        public event Action<ICertificateResolver, Exception> Error;

        /// <summary>
        /// Resolves X509 certificates for a specific subject.  May either be an address or a domain name.
        /// </summary>
        /// <param name="subjectName">The <see cref="String"/> subject to resolve. </param>
        /// <returns>An <see cref="X509Certificate2Collection"/> of X509 certifiates for the address,
        /// or <c>null</c> if no certificates are found.</returns>
        protected X509Certificate2Collection GetCertificatesBySubect(String subjectName)
        {
            var retVal = new X509Certificate2Collection();

            // find by host
            String domainName;
            int index;
            if ((index = subjectName.IndexOf("@")) > -1)
                domainName = subjectName.Substring(index + 1);
            else
                domainName = subjectName;

            string lookupName = LDAP_SRV_PREFIX + domainName;

            // get the LDAP connection from the SRV records
            LdapConnection connection = GetLdapConnection(lookupName);

            if (connection != null)
            {
                // gate the base naming contexts
                List<String> distNames = getBaseNamingContext(connection);

                foreach (String dn in distNames)
                {
                    // search each base context
                    var request = new SearchRequest(dn, EMAIL_ATTRIBUTE + "=" + subjectName, SearchScope.Subtree,
                                                    new[] {CERT_ATTRIBUTE, CERT_ATTRIBUTE_BINARY});

                    try
                    {
                        // send the LDAP request using the mail attribute as the search filter and return the smimeUserCertificate attribute
                        var response = (SearchResponse) connection.SendRequest(request);
                        if (response != null && response.Entries.Count > 0)
                        {
                            foreach (SearchResultEntry entry in response.Entries)
                            {
                                if (entry.Attributes.Count > 0)
                                {
                                    foreach (DirectoryAttribute entryAttr in entry.Attributes.Values)
                                    {
                                        if (entryAttr.Count > 0)
                                        {
                                            // search could possibly return more than one entry and each entry may contain
                                            // more that one certificates
                                            for (int i = 0; i < entryAttr.Count; ++i)
                                            {
                                                try
                                                {
                                                    var cert = new X509Certificate2((byte[]) entryAttr[i]);
                                                    retVal.Add(cert);
                                                }
                                                catch (Exception ex)
                                                {
                                                    NotifyException(ex);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception ldapEx)
                    {
                        NotifyException(ldapEx);
                    }
                }

                connection.Dispose();
            }

            return retVal;
        }

        /// <summary>
        /// Get the base distiguished names that will be searched for certificate resolution.
        /// </summary>
        /// <param name="connection">The <see cref="LdapConnection"/> connection to the LDAP server that will be searched.. </param>
        /// <returns>A <see cref="List<String>"/> of string representing the base distiguished names of the LDAP server.</returns>
        protected List<String> getBaseNamingContext(LdapConnection connection)
        {
            var retVal = new List<String>();

            // get the base DNs
            var request = new SearchRequest("", WILDCARD_OBJECT_CLASS_SEARCH, SearchScope.Base,
                                            new[] {NAMING_CONTEXTS_ATTRIBUTE});
            var searchResponse = (SearchResponse) connection.SendRequest(request);

            try
            {
                if (searchResponse != null && searchResponse.Entries.Count > 0)
                {
                    foreach (SearchResultEntry entry in searchResponse.Entries)
                    {
                        if (entry.Attributes.Count > 0)
                        {
                            foreach (DirectoryAttribute entryAttr in entry.Attributes.Values)
                            {
                                if (entryAttr.Count > 0)
                                {
                                    // search the attributes for the context names and add them the return value
                                    for (int i = 0; i < entryAttr.Count; ++i)
                                    {
                                        retVal.Add((String) entryAttr[i]);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception ldapEx)
            {
                NotifyException(ldapEx);
            }
            return retVal;
        }

        /// <summary>
        /// Creates a connection to an LDAP server based on the DNS SRV resolution of the lookup name.
        /// </summary>
        /// <param name="lookupName">The <see cref="String"/> lookup name used for DNS SRV resolution of the LDAP servers. </param>
        /// <returns>An <see cref="LdapConnection"/> to the server that will be searched for certificates.</returns>
        protected LdapConnection GetLdapConnection(String lookupName)
        {
            LdapConnection retVal = null;

            // get an instance of the DNS client
            using (var client = CreateDnsClient())
            {
                // get the location of the LDAP servers using DNS SRV
                var request = new DnsRequest(DnsStandard.RecordType.SRV, lookupName);
                var response = client.Resolve(request);


                if (response != null && response.HasAnswerRecords)
                {
                    // create the LDAP client
                    // try each record until we get one that connects
                    foreach (SRVRecord srvRec in response.AnswerRecords.SRV)
                    {
                        var ldapIdentifier = new LdapDirectoryIdentifier(srvRec.Target, srvRec.Port);
                        try
                        {
                            retVal = new LdapConnection(ldapIdentifier);
                            retVal.AuthType = AuthType.Anonymous; // use anonymous bind
                            retVal.SessionOptions.ProtocolVersion = LdapProtoVersion;

                            if (Timeout.Ticks > 0)
                            {
                                retVal.Timeout = Timeout;
                            }
                            retVal.Bind();

                            break;
                        }
                        catch (Exception)
                        {
                            // didn't connenct.... go onto the next record
                            retVal = null;
                        }
                    }
                }
            }

            return retVal;
        }

        DnsClient CreateDnsClient()
        {
            DnsClient client = (m_cacheEnabled) ? new DnsClientWithCache(m_dnsServerIp) : new DnsClient(m_dnsServerIp);
            if (Timeout.Ticks > 0)
            {
                client.Timeout = Timeout;
            }

            client.UseUDPFirst = true;
            client.MaxRetries = m_maxRetries;

            return client;
        }
         
        void NotifyException(Exception ex)
        {
            Action<ICertificateResolver, Exception> errorHandler = Error;
            if (errorHandler != null)
            {
                try
                {
                    errorHandler(this, ex);
                }
                catch
                {
                }
            }
        }

        
    }
}