/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Sean Nolan      seannol@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;

namespace Health.Direct.Common.DnsResolver
{
    /// <summary>
    ///   DNS client resolver, handling a variety of DNS request types and using TCP by default.
    /// </summary>
    /// 
    /// <example>
    ///   This example demonstrates basic resolver features.
    ///   <code>
    ///     const string DnsServer = "8.8.8.8";
    ///     const string ExampleDomain = "example.org";
    ///     var client = new DnsClient(DnsServer);
    ///
    ///     Console.WriteLine("Host addresses for " + ExampleDomain);
    ///     foreach(IPAddress addr in client.GetHostAddresses(ExampleDomain)
    ///     {
    ///         Console.WriteLine(addr.ToString());
    ///     }
    ///
    ///     Console.WriteLine("Name Server addresses for " + ExampleDomain);
    ///     foreach(IPAddress addr in client.GetNameServers(ExampleDomain)
    ///     {
    ///         Console.WriteLine(addr.ToString());
    ///     }
    ///
    ///     Console.WriteLine("Authority Server addresses for " + ExampleDomain);
    ///     foreach(IPAddress addr in client.GetAuthorityServers(ExampleDomain)
    ///     {
    ///         Console.WriteLine(addr.ToString());
    ///     }
    ///   </code>
    /// </example>
    /// 
    /// <example>
    ///   This example uses the shorthand methods to resolve CERT records
    ///   <code>
    ///     const string LocalDnsIp = "127.0.0.1";
    ///     const string ExampleDomain = "bob.example.org"; // for bob@example.org
    ///     var client = new DnsClient(LocalDnsIp);
    ///     try { IEnumerable&lt;CertRecord&gt; certrecs = client.ResolveCERT(ExampleDomain); }
    ///     catch (DnsException error)
    ///     {
    ///         //handle error
    ///     }
    ///         
    ///     foreach (var certrec in certrecs) 
    ///     {
    ///         if (certrec.CertType == CertRecord.X509)
    ///         {
    ///             X509Certificate2 cert = certrec.Cert.Certificate;
    ///         }
    ///     }
    ///    </code>
    /// </example>
    /// <example>
    ///   This example uses the full power of the DnsClient library to resolve CERT records.
    ///   <code>
    ///     const string LocalDnsIp = "127.0.0.1";
    ///     const string ExampleDomain = "bob.example.org"; // for bob@example.org
    ///     var client = new DnsClient(LocalDnsIp);
    ///     var req = new DnsRequest(Dns.RecordType.CERT, ExampleDomain); 
    ///     try { DnsResponse resp = client.Resolve(req); }
    ///     catch (DnsException error)
    ///     {
    ///         // handle error
    ///     }
    ///     if (resp != null &amp;&amp; resp.HasAnswerRecords)
    ///     {
    ///         foreach (var certrec in response.AnswerRecords.CERT)
    ///         {
    ///             if (certrec.CertType == CertRecord.X509)
    ///             {
    ///                 X509Certificate2 cert = certrec.Cert.Certificate;
    ///             }
    ///         }
    ///      }
    ///   </code> 
    /// </example>
    public class DnsClient : IDisposable
    {
        /// <summary>
        /// Default Receive Timeout
        /// </summary>
        public static readonly TimeSpan DefaultTimeout = TimeSpan.FromSeconds(5);
        
        IPEndPoint m_dnsServer;
        Socket m_udpSocket;
        DnsBuffer m_requestBuffer;
        DnsBuffer m_responseBuffer;
        DnsBuffer m_lengthBuffer;
        TimeSpan m_timeout;
        TimeSpan? m_postFailurePause;
        int m_maxRetries = 3;
        Random m_requestIDGenerator;
        
        /// <summary>
        ///   Creates a DnsClient instance specifying a string representation of the IP address and using the default DNS port.
        /// </summary>
        /// <param name="server">
        /// A string representation of the DNS server IP address.
        /// </param>
        /// <example><c>var client = new DnsClient("8.8.8.8");</c></example>
        public DnsClient(string server)
            : this(server, DnsStandard.DnsPort)
        {
        }
        
        /// <summary>
        ///   Creates a DnsClient instance specifying the DNS port.
        /// </summary>
        /// <param name="server">
        /// A string representation of the DNS server IP address.
        /// </param>
        /// <param name="port">
        /// The port to use for the DNS requests.
        /// </param>
        /// <example><c>var client = new DnsClient("8.8.8.8", 8888);</c></example>
        public DnsClient(string server, int port)
            : this(new IPEndPoint(IPAddress.Parse(server), port))
        {
        }

        /// <summary>
        /// Creates a DnsClient instance specifying an IPAddress representation of the IP address.
        /// </summary>
        /// <param name="server">
        /// The IPAddress of the DNS server. A <see cref="IPAddress"/>
        /// </param>
        public DnsClient(IPAddress server)
            : this(new IPEndPoint(server, DnsStandard.DnsPort))
        {
        }
        
        /// <summary>
        /// Creates a DnsClient instance specifying an IPEndPoint representation of the IP address and port.
        /// </summary>
        /// <param name="server">
        /// A <see cref="IPEndPoint"/>
        /// </param>
        public DnsClient(IPEndPoint server)
            : this(server, DnsClient.DefaultTimeout, DnsStandard.MaxUdpMessageLength * 2)
        {
        }
        
        /// <summary>
        /// Creates a DnsClient instance specifying an IPEndPoint representation of the IP address and port and 
        /// specifying the timeout and buffer size to use.
        /// </summary>
        /// <param name="server">
        /// A <see cref="IPEndPoint"/>
        /// </param>
        /// <param name="timeout">
        /// Timeout value.
        /// </param>
        /// <param name="maxUdpBufferSize">
        /// Maximum buffer size to use for UDP communication.
        /// </param>
        public DnsClient(IPEndPoint server, TimeSpan timeout, int maxUdpBufferSize)
        {         
            if (server == null)
            {
                throw new ArgumentNullException("server");
            }   
            
            this.m_dnsServer = server;
            m_lengthBuffer = new DnsBuffer(2);
            m_requestBuffer = new DnsBuffer(1024);
            m_responseBuffer = new DnsBuffer(maxUdpBufferSize);
            this.Timeout = timeout;
            this.UseUDPFirst = true;
            m_requestIDGenerator = new Random();
        }
        
        /// <summary>
        /// Specifies whether to try UDP first, before TCP attempts. 
        /// </summary>
        /// <value>A boolean specifying if UDP should be tried first. Defaults to true. If the UDP attempt fails due 
        /// to a truncated response, and there are more retries available, will always fail over to TCP.</value>
        public bool UseUDPFirst
        {
            get;
            set;
        }
        
        /// <summary>
        /// DNS server to use for DNS requests. 
        /// </summary>
        public IPEndPoint Server
        {
            get
            {
                return m_dnsServer;
            }            
            set
            {
                if (value == null)
                {
                    throw new ArgumentNullException("value");
                }
                m_dnsServer = value;
            }            
        }
                        
        /// <summary>
        /// Timeout interval for DNS requests. 
        /// </summary>
        /// <value>A positive value indicating the span before timeout</value>
        public TimeSpan Timeout
        {
            get
            {
                return this.m_timeout;
            }
            set
            {
                if (value.Ticks < 1)
                {
                    throw new ArgumentException("value less than 1", "value");
                }
                
                m_timeout = value;
            }
        }
        
        /// <summary>
        /// Pause for this long in between UDP retries
        /// </summary>
        public TimeSpan? RetryPause
        {
            get
            {
                return m_postFailurePause;
            }
            set
            {
                m_postFailurePause = value;
            }
        }
        
        private int TimeoutInMilliseconds
        {
            get { return (int)Timeout.TotalMilliseconds; }
        }

        /// <summary>
        /// The number of retries to attempt.
        /// </summary>
        public int MaxRetries
        {
            get
            {
                return m_maxRetries;
            }
            set
            {
                if (value < 1)
                {
                    throw new ArgumentException("value was less than 1", "value");
                }
                
                m_maxRetries = value;
            }
        }

        /// <summary>
        /// Event to which to subscribe for notification of errors.
        /// </summary>
        public event Action<DnsClient, Exception> Error;
        
		
        /// <summary>
        /// Resolves a DNS Request and returns a DnsResponse response instance.
        /// </summary>
        /// <param name="request">
        /// The DNS Request to resolve. See <see cref="DnsRequest"/>
        /// </param>
        /// <returns>
        /// A DNS Response instance representing the response. See <see cref="DnsResponse"/>
        /// </returns>
        /// <exception cref="DnsException">Thrown for network failure (e.g. max retries exceeded) or badly formed responses.</exception>
        public virtual DnsResponse Resolve(DnsRequest request)
        {
            if (request == null)
            {
                throw new ArgumentNullException("request");
            }
            
            request.Validate();
               
            request.RequestID = this.NextID();
            this.SerializeRequest(request);

            bool useUDPFirst = this.UseUDPFirst;
            int attempt = 0;
            int maxAttempts = this.m_maxRetries + 1;
            while (attempt < maxAttempts)
            {
                attempt++;
                try
                {
                    if (useUDPFirst)
                    {
                        this.ExecuteUDP();
                    }
                    else
                    {
                        this.ExecuteTCP();
                    }
                    
                    DnsResponse response = this.DeserializeResponse();
                    response.Validate();

                    if (request.RequestID != response.RequestID || !response.Question.Equals(request.Question))
                    {
                        // Hmm. Not a response to any query we'd sent. 
                        // Could be a spoof, a server misbehaving, or some other socket (UDP) oddity (delayed message etc)
                        // If this is TCP, then this should not happen and we will hit max attempts and stop
                        // Ignore and retry
                        continue;
                    }
                    
                    if (response.IsNameError)
                    {
                        return response;
                    }
                    
                    if (!response.IsSuccess)
                    {
                        throw new DnsServerException(response.Header.ResponseCode);
                    }
                    
                    if (!response.Header.IsTruncated)
                    {
                        return response;
                    }
                    
                    useUDPFirst = false;
                }
                catch(DnsServerException se)
                {
                    //
                    // Server failures deserve a retry
                    //
                    if (se.ResponseCode != DnsStandard.ResponseCode.ServerFailure)
                    {
                        throw;
                    }
                }
                catch(DnsProtocolException pe)
                {
                    //
                    // Random failures also deserve a retry
                    //
                    if (pe.Error != DnsProtocolError.Failed)
                    {
                        throw;
                    }
                }
                catch(DnsException)
                {
                    throw;
                }
                catch(Exception error)
                {
                    //
                    // Typically, a socket exception or network error...
                    //
                    if (attempt >= maxAttempts)
                    {
                        throw;
                    }
                    
                    this.NotifyError(error);
                }
                
                this.CloseUdpSocket();
                
                if (m_postFailurePause != null)
                {
                    System.Threading.Thread.Sleep(m_postFailurePause.Value);
                }            
            }

            throw new DnsProtocolException(DnsProtocolError.MaxAttemptsReached, Server.Address);
        }
        
        /// <summary>
        /// Convenience method resolving and returning A RRs. 
        /// </summary>
        /// <param name="domain">
        /// The domain name to resolve.
        /// </param>
        /// <returns>
        /// An enumeration of Address Records. See <see cref="AddressRecord"/>
        /// </returns>
        public IEnumerable<AddressRecord> ResolveA(string domain)
        {
            DnsResponse response = this.Resolve(DnsRequest.CreateA(domain));
            if (response == null || !response.HasAnswerRecords)
            {
                return null;
            }            
            return response.AnswerRecords.A;
        }

        /// <summary>
        /// Convenience method resolving and returning PTR RRs. 
        /// </summary>
        /// <param name="domain">
        /// The domain name to resolve.
        /// </param>
        /// <returns>
        /// An enumeration of PTR Records. See <see cref="PtrRecord"/>
        /// </returns>
        public IEnumerable<PtrRecord> ResolvePTR(string domain)
        {
            DnsResponse response = this.Resolve(DnsRequest.CreatePTR(domain));
            if (response == null || !response.HasAnswerRecords)
            {
                return null;
            }
            return response.AnswerRecords.PTR;
        }

        /// <summary>
        /// Convenience method resolving and returning NS RRs. 
        /// </summary>
        /// <param name="domain">
        /// The domain name to resolve.
        /// </param>
        /// <returns>
        /// An enumeration of NS Records. See <see cref="NSRecord"/>
        /// </returns>
        public IEnumerable<NSRecord> ResolveNS(string domain)
        {
            DnsResponse response = this.Resolve(DnsRequest.CreateNS(domain));
            if (response == null || !response.HasNameServerRecords)
            {
                return null;
            }
            return response.AnswerRecords.NS;
        }
          
        /// <summary>
        /// Convenience method resolving and returning MX RRs. 
        /// </summary>
        /// <param name="emailDomain">
        /// The domain name to resolve.
        /// </param>
        /// <returns>
        /// An enumeration of MX Records. See <see cref="MXRecord"/>
        /// </returns>
        public IEnumerable<MXRecord> ResolveMX(string emailDomain)
        {
            DnsResponse response = this.Resolve(DnsRequest.CreateMX(emailDomain));
            if (response == null || !response.HasAnswerRecords)
            {
                return null;
            }            
            return response.AnswerRecords.MX;
        }

        /// <summary>
        /// Convenience method resolving and returning TXT RRs. 
        /// </summary>
        /// <param name="domain">
        /// The domain name to resolve.
        /// </param>
        /// <returns>
        /// An enumeration of TXT Records. See <see cref="TextRecord"/>
        /// </returns>
        public IEnumerable<TextRecord> ResolveTXT(string domain)
        {
            DnsResponse response = this.Resolve(DnsRequest.CreateTXT(domain));
            if (response == null || !response.HasAnswerRecords)
            {
                return null;
            }
            return response.AnswerRecords.TXT;
        }
                
        /// <summary>
        /// Convenience method resolving and returning CERT RRs. 
        /// </summary>
        /// <param name="domain">
        /// The domain name to resolve.
        /// </param>
        /// <returns>
        /// An enumeration of CERT Records. See <see cref="CertRecord"/>
        /// </returns>
        public IEnumerable<CertRecord> ResolveCERT(string domain)
        {
            DnsResponse response = this.Resolve(DnsRequest.CreateCERT(domain));
            if (response == null || !response.HasAnswerRecords)
            {
                return null;
            }
                                
            return response.AnswerRecords.CERT;
        }


        /// <summary>
        /// Resolves CERT records for <paramref name="domain"/> from the authoritative
        /// nameservers for the domain.
        /// </summary>
        /// <remarks>
        /// This method may be used if the typical nameserver used does not handle CERT records.
        /// </remarks>
        /// <param name="domain">The domain for which to retrieve CERT records.</param>
        /// <returns>An enumeration of <see cref="CertRecord"/> instances.</returns>
        public  IEnumerable<CertRecord> ResolveCERTFromNameServer(string domain)
        {
            IEnumerable<IPAddress> nameServers = this.GetNameServers(domain);
            if (nameServers != null)
            {
                foreach (IPAddress nameserver in nameServers)
                {
                    try
                    {
                        IEnumerable<CertRecord> certs = this.ResolveCERTFromNameServer(domain, nameserver);
                        if (certs != null)
                        {
                            return certs;
                        }
                    }
                    catch (DnsException)
                    {
                        throw;
                    }
                }
            }
            
            return null;
        }


        /// <summary>
        /// Resolves CERT records for <paramref name="domain"/> from the authoritative
        /// nameservers for the domain.
        /// </summary>
        /// <remarks>
        /// This method may be used if the typical nameserver used does not handle CERT records.
        /// </remarks>
        /// <param name="domain">The domain for which to retrieve CERT records.</param>
        /// <param name="nameserver">The nameserver from which to retrieve authoratative nameservers
        /// for the domain.</param>
        /// <returns>An enumeration of <see cref="CertRecord"/> instances.</returns>
        public virtual IEnumerable<CertRecord> ResolveCERTFromNameServer(string domain, IPAddress nameserver)
        {
            if (string.IsNullOrEmpty(domain))
            {
                throw new ArgumentException("value was null or empty", "domain");
            }
            if (nameserver == null)
            {
                throw new ArgumentNullException("nameserver");
            }
            
            using (DnsClient client = new DnsClient(nameserver))
            {
                client.UseUDPFirst = false;
                return client.ResolveCERT(domain);                
            }
        }
        
        /// <summary>
        /// Convenience method resolving and returning SOA RRs. 
        /// </summary>
        /// <param name="domain">
        /// The domain name to resolve.
        /// </param>
        /// <returns>
        /// An enumeration of SOA Records. See <see cref="SOARecord"/>
        /// </returns>
        public IEnumerable<SOARecord> ResolveSOA(string domain)
        {
            DnsResponse response = this.Resolve(DnsRequest.CreateSOA(domain));
            if (response != null)
            {
                if (response.HasNameServerRecords)
                {
                    return response.NameServerRecords.SOA;
                }
                
                if (response.HasAnswerRecords)
                {
                    return response.AnswerRecords.SOA;            
                }
            }
                        
            return null;
        }

        /// <summary>
        /// Resolves a domain name. 
        /// </summary>
        /// <param name="hostNameOrAddress">
        /// The domain name to resolve (takes an string IP address as well and returns a parsed IPAddress entry).
        /// </param>
        /// <returns>
        /// An enumeration of IPAddress instances. See <see cref="System.Net.IPAddress"/>
        /// </returns>
        public IEnumerable<IPAddress> ResolveHostAddresses(string hostNameOrAddress)
        {
            IPAddress address = IPAddress.None;
            if (IPAddress.TryParse(hostNameOrAddress, out address))
            {
                yield return address;
            }
            
            IEnumerable<AddressRecord> addressRecords = this.ResolveA(hostNameOrAddress);
            if (addressRecords != null)
            {
                foreach(AddressRecord record in addressRecords)
                {
                    yield return record.IPAddress;
                }
            }
        }
        
        /// <summary>
        /// Resolves an enumeration of domain names.
        /// </summary>
        /// <param name="hostNamesOrAddresses">
        /// An enumeration of domain name to resolve (takes string IP addresses as well and returns a parsed IPAddress entry).
        /// </param>
        /// <returns>
        /// An enumeration of IPAddress instances. See <see cref="System.Net.IPAddress"/>
        /// </returns>
        public IEnumerable<IPAddress> ResolveHostAddresses(IEnumerable<string> hostNamesOrAddresses)
        {
            if (hostNamesOrAddresses == null)
            {
                throw new ArgumentNullException("hostNamesOrAddresses");
            }

            foreach (string hostName in hostNamesOrAddresses)
            {
                IEnumerable<IPAddress> addresses = null;
                try
                {
                    addresses = this.ResolveHostAddresses(hostName);
                }
                catch
                {
                }
                if (addresses != null)
                {
                    foreach(IPAddress address in addresses)
                    {
                        yield return address;
                    }
                }
            }
        }

        /// <summary>
        /// Duplicates the behavior of System.Net.Dns.GetHostAddresses.
        /// </summary>
        /// <param name="hostNameOrAddress">
        /// A domain name to resolve (takes a string IP address as well and returns a parsed IPAddress entry).
        /// </param>
        /// <returns>
        /// A array of IPAddress instances. See <see cref="System.Net.IPAddress"/>
        /// </returns>

        public IPAddress[] GetHostAddresses(string hostNameOrAddress)
        {
            IEnumerable<IPAddress> addresses = this.ResolveHostAddresses(hostNameOrAddress);
            if (addresses != null)
            {
                return addresses.ToArray<IPAddress>();
            }

            return null;
        }
                
        /// <summary>
        /// Resolves authority servers for a domain name.
        /// </summary>
        /// <param name="domain">
        /// A domain name to resolve.
        /// </param>
        /// <returns>
        /// A array of IPAddress instances of authority servers for the domain. See <see cref="System.Net.IPAddress"/>
        /// </returns>
        public IEnumerable<IPAddress> GetAuthorityServers(string domain)
        {
            IEnumerable<SOARecord> soaRecords = this.ResolveSOA(domain);
            if (soaRecords != null)
            {
                return this.ResolveHostAddresses(
                    from record in soaRecords
                    select record.DomainName
                    );
            }
            
            return null;
        }

        /// <summary>
        /// Resolves name servers for a domain name.
        /// </summary>
        /// <param name="domain">
        /// A domain name to resolve.
        /// </param>
        /// <returns>
        /// An enumeration of IPAddress instances of name servers for the domain. See <see cref="System.Net.IPAddress"/>
        /// </returns>
        public IEnumerable<IPAddress> GetNameServers(string domain)
        {
            return this.ResolveHostAddresses(this.GetNameServerNames(domain));
        }
        
        /// <summary>
        /// Resolves all the name server names for a given domain
        /// </summary>
        /// <param name="domain"></param>
        /// <returns>An enumeration of name server names</returns>
        public IEnumerable<string> GetNameServerNames(string domain)
        {
            DnsResponse response = this.Resolve(DnsRequest.CreateNS(domain));
            if (response == null)
            {
                yield break;
            }
            
            IEnumerable<DnsResourceRecord> nsRecords = null;
            if (response.HasAnswerRecords)
            {
                nsRecords = response.AnswerRecords;
            }
            else if (response.HasNameServerRecords)
            {
                nsRecords = response.NameServerRecords;
            }
            if (nsRecords == null)
            {
                yield break;
            }
            
            string serverName = null;
            foreach (DnsResourceRecord record in nsRecords)
            {
                serverName = null;
                switch (record.Type)
                {
                    default:
                        break;

                    case DnsStandard.RecordType.NS:
                        serverName = ((NSRecord)record).NameServer;
                        break;

                    case DnsStandard.RecordType.SOA:
                        serverName = ((SOARecord)record).DomainName;
                        break;
                }

                if (serverName != null)
                {
                    yield return serverName;
                }
            }
        }
		
        /// <summary>
        /// Closes network resources for this instance. 
        /// </summary>
        public void Close()
        {
            this.CloseUdpSocket();
        }
        
        void CloseUdpSocket()
        {
            if (m_udpSocket != null)
            {
                m_udpSocket.Close();
                m_udpSocket = null;
            }
        }
        
        void SerializeRequest(DnsRequest request)
        {
            m_requestBuffer.Clear();
            request.Serialize(m_requestBuffer);
        }

        DnsResponse DeserializeResponse()
        {
            return new DnsResponse(m_responseBuffer.CreateReader());
        }
                
        //--------------------------------------------
        //
        // UDP Transport
        //
        //--------------------------------------------
        void ExecuteUDP()
        {
            this.CloseUdpSocket();
            this.EnsureUdpSocket();
            
            m_udpSocket.Send(m_requestBuffer.Buffer, m_requestBuffer.Count, SocketFlags.None);            
            m_responseBuffer.Count = m_udpSocket.Receive(m_responseBuffer.Buffer);
        }
        
        void EnsureUdpSocket()
        {
            if (m_udpSocket != null)
            {
                return;
            }
            
            m_udpSocket = new Socket(AddressFamily.InterNetwork, SocketType.Dgram, ProtocolType.Udp);
            m_udpSocket.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.ReceiveTimeout, this.TimeoutInMilliseconds);
            
            m_udpSocket.Connect(m_dnsServer);
        }
        
        //--------------------------------------------
        //
        // TCP Transport
        //
        //--------------------------------------------
        void ExecuteTCP()
        {
            using(Socket tcpSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp))
            {
                tcpSocket.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.SendTimeout, this.TimeoutInMilliseconds);
                tcpSocket.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.ReceiveTimeout, this.TimeoutInMilliseconds);
                
                tcpSocket.Connect(m_dnsServer);
                
                try
                {
                    m_lengthBuffer.Clear();
                    m_lengthBuffer.AddUshort((ushort)m_requestBuffer.Count);
                    
                    tcpSocket.Send(m_lengthBuffer.Buffer, m_lengthBuffer.Count, SocketFlags.None);
                    tcpSocket.Send(m_requestBuffer.Buffer, m_requestBuffer.Count, SocketFlags.None);
                    //
                    // First, receive the response message length
                    //
                    m_lengthBuffer.Clear();
                    m_lengthBuffer.Count = this.ReadTcp(tcpSocket, m_lengthBuffer.Buffer, m_lengthBuffer.Capacity);
                    //
                    // Read length of the response to come
                    //
                    DnsBufferReader reader = m_lengthBuffer.CreateReader();
                    ushort responseSize = reader.ReadUShort();
                    m_responseBuffer.ReserveCapacity(responseSize);
                    //
                    // Now receive the real response
                    //
                    m_responseBuffer.Count = this.ReadTcp(tcpSocket, m_responseBuffer.Buffer, responseSize);
                }
                finally
                {
                    tcpSocket.Shutdown(SocketShutdown.Both);
                }
            }
        }

        int ReadTcp(Socket tcpSocket, byte[] buffer, int countToRead)
        {
            int countRead = 0;
            while (countRead < countToRead)
            {
                int countReceived = 0;
                if ((countReceived = tcpSocket.Receive(buffer, countRead, countToRead - countRead, SocketFlags.None)) <= 0)
                {
                    break;
                }
                countRead += countReceived;
            }
            if (countRead != countToRead)
            {
                throw new DnsProtocolException(DnsProtocolError.Failed);
            }            
            return countRead;
        }
                        
        void NotifyError(Exception ex)
        {
            if (this.Error != null)
            {
                try
                {
                    this.Error(this, ex);
                }
                catch
                {
                }
            }
        }
        
        ushort NextID()
        {
            return (ushort) m_requestIDGenerator.Next(ushort.MinValue, ushort.MaxValue);
        }

        #region IDisposable Members

        /// <summary>
        /// Frees resources for this instance.
        /// </summary>
        public void Dispose()
        {
            this.Close();
        }

        #endregion
    }
}