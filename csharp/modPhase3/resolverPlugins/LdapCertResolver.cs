/* 
 Copyright (c) 2013, Direct Project
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
using System.Collections.Generic;
using System.DirectoryServices.Protocols;
using System.Net;
using System.Linq;
using System.Net.Mail;
using System.Net.NetworkInformation;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Dns;
using Health.Direct.Common.DnsResolver;
using Health.Direct.ModSpec3.ResolverPlugins.Cert;
using Health.Direct.ModSpec3.ResolverPlugins.Ldap;

namespace Health.Direct.ModSpec3.ResolverPlugins
{
    /// <summary>
    /// Implements a certificate resolver using one or more public LDAP server using DNS SRV to resolve server locations
    /// </summary>
    public class LdapCertResolver : ICertificateResolver
    {

        private static readonly String LDAP_SRV_PREFIX = "_ldap._tcp.";

        private static readonly Int32 LdapProtoVersion = 3;

        private static readonly TimeSpan DefaultTimeout = TimeSpan.FromSeconds(5);
        private readonly bool m_cacheEnabled;
        private readonly IPAddress m_dnsServerIp;
        private readonly TimeSpan m_timeout;
        private int m_maxRetries = 1;
        X509ChainPolicy m_policy;

        /// <summary>
        /// Chain validations status treated as failing trust validation with the certificate.
        /// </summary>
        public static readonly X509ChainStatusFlags DefaultProblemFlags =
            X509ChainStatusFlags.NotTimeValid |
            X509ChainStatusFlags.Revoked |
            X509ChainStatusFlags.NotSignatureValid |
            X509ChainStatusFlags.InvalidBasicConstraints |
            X509ChainStatusFlags.CtlNotTimeValid |
            X509ChainStatusFlags.CtlNotSignatureValid;

        X509ChainStatusFlags m_problemFlags;


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
                dnsServerIp = GetLocalServerDns(dnsServerIp);
            }
            if (dnsServerIp == null)
                throw new ArgumentNullException("dnsServerIp");

            if (timeout.Ticks < 0)
            {
                throw new ArgumentException("timeout value was less than zero", "timeout");
            }

            m_dnsServerIp = dnsServerIp;
            m_timeout = timeout;
            m_cacheEnabled = cacheEnabled;

            m_policy = new X509ChainPolicy();
            m_policy.VerificationFlags = (X509VerificationFlags.IgnoreWrongUsage);

            m_problemFlags = DefaultProblemFlags;

        }

        /// <summary>
        /// Find the first dns from the active network interfaces.
        /// </summary>
        /// <param name="dnsServerIp"></param>
        /// <returns></returns>
        private IPAddress GetLocalServerDns(IPAddress dnsServerIp)
        {
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
            return dnsServerIp;
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

        /// <summary>
        /// Gets and sets the <see cref="X509ChainStatusFlags"/> for this validator
        /// </summary>
        public X509ChainStatusFlags ProblemFlags
        {
            get
            {
                return m_problemFlags;
            }
            set
            {
                m_problemFlags = value;
            }
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
            IEnumerable<SRVRecord> srvRecords;

            // get the location of the LDAP servers using DNS SRV
            using (var client = CreateDnsClient())
            {
                var lookupName = GetSrvLdapLookupName(address.Address);
                srvRecords = RequestSrv(client, lookupName);
            }
            if (srvRecords == null) return null;

            X509Certificate2Collection validCerts = null;
           
            foreach (var srvRecord in srvRecords)
            {
                // get address certs from Ldap
                var certs = GetCertificatesBySubect(srvRecord, address.Address);
                validCerts = Validator.FilterValidCerts(certs, m_policy, ProblemFlags, NotifyException);
                if(!validCerts.IsNullOrEmpty()) break;

                // get org certs from Ldap
                certs = GetCertificatesBySubect(srvRecord, address.Host);
                validCerts = Validator.FilterValidCerts(certs, m_policy, ProblemFlags, NotifyException);
                if (!validCerts.IsNullOrEmpty()) break;
            }
            return validCerts;
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
            IEnumerable<SRVRecord> srvRecords;
            X509Certificate2Collection validCerts = null;

            // get the location of the LDAP servers using DNS SRV
            using (var client = CreateDnsClient())
            {
                srvRecords = RequestSrv(client, GetSrvLdapLookupName(domain));
            }

            foreach (var srvRecord in srvRecords)
            {
                var certs = GetCertificatesBySubect(srvRecord, domain);
                validCerts = Validator.FilterValidCerts(certs, m_policy, ProblemFlags, NotifyException);
                if (!validCerts.IsNullOrEmpty()) break;
            }
            return validCerts;
        }

        #endregion

        static IEnumerable<SRVRecord> RequestSrv(DnsClient client, string domain)
        {
            var dnsRequest = new DnsRequest(DnsStandard.RecordType.SRV, domain);
            var response = client.Resolve(dnsRequest);
            if (response == null || !response.HasAnswerRecords)
            {
                return null;
            }

            return response.AnswerRecords.SRV
                .OrderBy(r => r.Priority)
                .OrderByDescending(r => r.Weight);
        }

        /// <summary>
        /// Event to subscribe to for notification of errors.
        /// </summary>
        public event Action<ICertificateResolver, Exception> Error;

        /// <summary>
        /// Resolves X509 certificates for a specific subject.  May either be an address or a domain name.
        /// </summary>
        /// <param name="srvRecord">Resolve <see cref="SRVRecord"/> to resolve. </param>
        /// /// <param name="subjectName">The <see cref="String"/> subject to resolve. </param>
        /// <returns>An <see cref="X509Certificate2Collection"/> of X509 certifiates for the address,
        /// or <c>null</c> if no certificates are found.</returns>
        X509Certificate2Collection GetCertificatesBySubect(SRVRecord srvRecord, string subjectName)
        {
            var retVal = new X509Certificate2Collection();

            // get the LDAP connection from the SRV records

            using (var connection = GetLdapConnection(srvRecord))
            {
                if (connection != null)
                {
                    // gate the base naming contexts
                    var distNames = GetBaseNamingContext(connection);

                    foreach (var dn in distNames)
                    {
                        // search each base context
                        var request = Search.MimeCertRequest(dn, subjectName);
                        try
                        {
                            SetCerts(connection, request, retVal);
                        }
                        catch (LdapCertResolverException ldapEx)
                        {
                            this.Error.NotifyEvent(this, new LdapCertResolverException(ldapEx.Error, subjectName + srvRecord, ldapEx.InnerException));
                        }
                        catch (Exception ex)
                        {
                            this.Error.NotifyEvent(this, ex);
                        }
                    }
                }
            }
            return retVal;
        }

        private void SetCerts(LdapConnection connection, SearchRequest request, X509Certificate2Collection retVal)
        {
            // send the LDAP request using the mail attribute as the search filter and return the userCertificate attribute
            var response = (SearchResponse)connection.SendRequest(request);
            if (response != null && response.Entries.Count > 0)
            {
                foreach (SearchResultEntry entry in response.Entries)
                {
                    SetCerts(entry, retVal);
                }
            }
        }

        private void SetCerts(SearchResultEntry entry, X509Certificate2Collection retVal)
        {
            if (entry.Attributes.Values == null || entry.Attributes.Count <= 0)
            {
                throw new LdapCertResolverException(LDAPError.NoUserCertificateAttribute);
            }
            foreach (DirectoryAttribute entryAttr in entry.Attributes.Values)
            {
                if (entryAttr.Count > 0)
                {
                    // search could possibly return more than one entry and each entry may contain
                    // more that one certificates
                    foreach (object t in entryAttr)
                    {
                        try
                        {
                            var cert = new X509Certificate2((byte[])t);
                            retVal.Add(cert);
                        }
                        catch (Exception ex)
                        {
                            this.Error.NotifyEvent(this, ex);
                        }
                    }
                }
            }
        }

        private static string GetSrvLdapLookupName(string subjectName)
        {
            String domainName;
            int index;
            if ((index = subjectName.IndexOf("@")) > -1)
                domainName = subjectName.Substring(index + 1);
            else
                domainName = subjectName;

            return LDAP_SRV_PREFIX + domainName;
        }

        /// <summary>
        /// Get the base distiguished names that will be searched for certificate resolution.
        /// </summary>
        /// <param name="connection">The <see cref="LdapConnection"/> connection to the LDAP server that will be searched.. </param>
        /// <returns>A List of strings representing the base distiguished names of the LDAP server.</returns>
        protected List<String> GetBaseNamingContext(LdapConnection connection)
        {
            var retVal = new List<String>();

            // get the base DNs
            var request = Search.NamingContextRequest();

            var searchResponse = (SearchResponse)connection.SendRequest(request);
            if (searchResponse == null || searchResponse.Entries == null || searchResponse.Entries.Count == 0)
            {
                return null;
            }
            try
            {
                foreach (SearchResultEntry entry in searchResponse.Entries)
                {
                    if (entry.Attributes != null && entry.Attributes.Values != null && entry.Attributes.Count > 0)
                    {
                        foreach (DirectoryAttribute entryAttr in entry.Attributes.Values)
                        {
                            SetAttribute(entryAttr, retVal);
                        }
                    }
                }
            }
            catch (Exception ldapEx)
            {
                this.Error.NotifyEvent(this, ldapEx);
            }
            return retVal;
        }

        private static void SetAttribute(DirectoryAttribute entryAttr, List<string> retVal)
        {
            if (entryAttr.Count > 0)
            {
                // search the attributes for the context names and add them the return value
                for (int i = 0; i < entryAttr.Count; ++i)
                {
                    retVal.Add((String)entryAttr[i]);
                }
            }
        }

        /// <summary>
        /// Creates a connection to an LDAP server based on the DNS SRV resolution of the lookup name.
        /// </summary>
        /// <param name="srvRecord">Resolver <see cref="SRVRecord"/></param>
        /// <returns>An <see cref="LdapConnection"/> to the server that will be searched for certificates.</returns>
        protected LdapConnection GetLdapConnection(SRVRecord srvRecord)
        {
            LdapConnection retVal;

            var ldapIdentifier = new LdapDirectoryIdentifier(srvRecord.Target, srvRecord.Port);
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
            }
            catch (Exception ex)
            {
                // didn't connenct.... go onto the next record
                this.Error.NotifyEvent(this, new LdapCertResolverException(LDAPError.BindFailure, srvRecord.ToString(), ex));
                retVal = null;
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
            this.Error.NotifyEvent(this, ex);
        }
    }
}