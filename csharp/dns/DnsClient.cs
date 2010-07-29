/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Sean Nolan      seannol@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using Microsoft.Win32;

namespace DnsResolver
{
    //
    // Not thread safe
    //
	/// <summary>
	///   Basic DNS client resolver, handling a variety of DNS request types.
    /// </summary>
    /// 
    /// <example>
    ///   This example uses the shorthand methods to resolve CERT records
    ///   <code>
	///     const string LocalDnsIp = "127.0.0.1";
	///     const string ExampleDomain = "bob.example.org"; // for bob@example.org
	///     var client = new DnsClient(LocalDnsIp);
	///     foreach (var certrec in client.ResolveCERT(ExampleDomain))
	///     {
	///         if (certrec.CertType == CertRecord.X509)
	///         {
	///             byte [] rawdata = certrec.Data;
	///              // create X509 certificate from rawdata
	///         }
	///     }
	///    </code>
	///   </example
    /// <example>
    ///   This example uses the full power of the DnsClient library
	///   <code>
	///     const string LocalDnsIp = "127.0.0.1";
	///     const string ExampleDomain = "bob.example.org"; // for bob@example.org
	///     var client = new DnsClient(LocalDnsIp);
	///     var req = new DnsRequest(Dns.RecordType.CERT, ExampleDomain);
	///     var resp = client.Resolve(req);
	///     if (resp != null && resp.HasAnswerRecords)
	///     {
	///         foreach (var certrec in response.AnswerRecords.CERT)
	///         {
	///             if (certrec.CertType == CertRecord.X509)
	///             {
	///                 byte [] rawdata = certrec.Data;
	///                 // create X509 certificate from rawdata
	///             }
	///         }
	///      }
	///   </code> 
	/// </example>
    public class DnsClient : IDisposable
    {
        public const int DefaultTimeoutMs = 2000; // 2 seconds

        IPEndPoint m_dnsServer;
        Socket m_udpSocket;
        DnsBuffer m_requestBuffer;
        DnsBuffer m_responseBuffer;
        DnsBuffer m_lengthBuffer;
        int m_timeout;
        int m_maxRetries = 3;
        ushort m_requestID = ushort.MinValue;
        
        public DnsClient(string server)
            : this(server, Dns.DNS_PORT)
        {
        }
        
        public DnsClient(string server, int port)
            : this(new IPEndPoint(IPAddress.Parse(server), port))
        {
        }

        public DnsClient(IPAddress server)
            : this(new IPEndPoint(server, Dns.DNS_PORT))
        {
        }
                        
        public DnsClient(IPEndPoint server)
            : this(server, DnsClient.DefaultTimeoutMs, 0x10000)
        {
        }
        
        public DnsClient(IPEndPoint server, int timeoutMillis, int maxBufferSize)
        {         
            if (server == null)
            {
                throw new ArgumentNullException();
            }   
            
            this.m_dnsServer = server;
            m_lengthBuffer = new DnsBuffer(2);
            m_requestBuffer = new DnsBuffer(1024);
            m_responseBuffer = new DnsBuffer(maxBufferSize);
            this.Timeout = timeoutMillis;
            this.UseUDP = true;
        }
        
        public bool UseUDP
        {
            get;
            set;
        }
        
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
                    throw new ArgumentNullException();
                }
                m_dnsServer = value;
            }            
        }
        
        public int Timeout
        {
            get
            {
                return this.m_timeout;
            }
            set
            {
                if (value <= 0)
                {
                    throw new ArgumentException();
                }
                
                m_timeout = value;
            }
        }
        
        public int MaxRetries
        {
            get
            {
                return m_maxRetries;
            }
            set
            {
                if (value <= 0)
                {
                    throw new ArgumentException();
                }
                
                m_maxRetries = value;
            }
        }
        
        public event Action<DnsClient, Exception> Error;
        
        public DnsResponse Resolve(DnsRequest request)
        {
            if (request == null)
            {
                throw new ArgumentNullException();
            }
            
            request.RequestID = this.NextID();
            this.SerializeRequest(request);
            
            int attempt = 0;
            bool useUDP = this.UseUDP;
            while (attempt < this.m_maxRetries)
            {
                attempt++;
                try
                {
                    if (useUDP)
                    {
                        this.ExecuteUDP();
                    }
                    else
                    {
                        this.ExecuteTCP();
                    }
                    
                    DnsResponse response = this.DeserializeResponse();
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
                    
                    useUDP = false;
                    attempt = m_maxRetries - 1;
                }
                catch(DnsException)
                {
                    throw;
                }
                catch(Exception error)
                {
                    if (attempt >= m_maxRetries)
                    {
                        throw;
                    }
                    
                    this.NotifyError(error);
                }            
            }
            
            throw new DnsProtocolException(DnsProtocolError.MaxAttemptsReached);
        }
        
        public IEnumerable<AddressRecord> ResolveA(string domain)
        {
            DnsResponse response = this.Resolve(DnsRequest.CreateA(domain));
            if (response == null || !response.HasAnswerRecords)
            {
                return null;
            }            
            return response.AnswerRecords.A;
        }

        public IEnumerable<PtrRecord> ResolvePTR(string domain)
        {
            DnsResponse response = this.Resolve(DnsRequest.CreatePTR(domain));
            if (response == null || !response.HasAnswerRecords)
            {
                return null;
            }
            return response.AnswerRecords.PTR;
        }

        public IEnumerable<NSRecord> ResolveNS(string domain)
        {
            DnsResponse response = this.Resolve(DnsRequest.CreateNS(domain));
            if (response == null || !response.HasNameServerRecords)
            {
                return null;
            }
            return response.AnswerRecords.NS;
        }
          
        public IEnumerable<MXRecord> ResolveMX(string emailDomain)
        {
            DnsResponse response = this.Resolve(DnsRequest.CreateMX(emailDomain));
            if (response == null || !response.HasAnswerRecords)
            {
                return null;
            }            
            return response.AnswerRecords.MX;
        }

        public IEnumerable<TextRecord> ResolveTXT(string domain)
        {
            DnsResponse response = this.Resolve(DnsRequest.CreateTXT(domain));
            if (response == null || !response.HasAnswerRecords)
            {
                return null;
            }
            return response.AnswerRecords.TXT;
        }
                
        public IEnumerable<CertRecord> ResolveCERT(string domain)
        {
            DnsResponse response = this.Resolve(DnsRequest.CreateCERT(domain));
            if (response == null || !response.HasAnswerRecords)
            {
                return null;
            }
                                
            return response.AnswerRecords.CERT;
        }
        
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
        
        public IEnumerable<IPAddress> ResolveHostAddresses(IEnumerable<string> hostNamesOrAddresses)
        {
            if (hostNamesOrAddresses == null)
            {
                throw new ArgumentNullException();
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

        //
        // Duplicates the behavior of System.Net.Dns.GetHostAddresses
        //
        public IPAddress[] GetHostAddresses(string hostNameOrAddress)
        {
            IEnumerable<IPAddress> addresses = this.ResolveHostAddresses(hostNameOrAddress);
            if (addresses != null)
            {
                return addresses.ToArray<IPAddress>();
            }

            return null;
        }
                
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

        public IEnumerable<IPAddress> GetNameServers(string domain)
        {
            IEnumerable<NSRecord> nsRecords = this.ResolveNS(domain);
            if (nsRecords != null)
            {
                return this.ResolveHostAddresses(
                    from record in nsRecords
                    select record.NameServer
                );
            }

            return null;
        }
                                
        public void Close()
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
            request.ToBytes(m_requestBuffer);
        }

        DnsResponse DeserializeResponse()
        {
            DnsBufferReader reader = new DnsBufferReader(m_responseBuffer.Buffer, 0, m_responseBuffer.Count);
            return new DnsResponse(ref reader);
        }
        
        void ValidateRequestID()
        {
            if (m_requestBuffer[0] != m_responseBuffer[0] || m_requestBuffer[1] != m_responseBuffer[1])
            {
                throw new DnsProtocolException(DnsProtocolError.RequestIDMismatch);
            }
        }
                
        //--------------------------------------------
        //
        // UDP Transport
        //
        //--------------------------------------------
        void EnsureUDP()
        {
        }
                
        void ExecuteUDP()
        {
            if (m_udpSocket == null)
            {
                m_udpSocket = new Socket(AddressFamily.InterNetwork, SocketType.Dgram, ProtocolType.Udp);
                m_udpSocket.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.ReceiveTimeout, this.m_timeout);
            }
            m_udpSocket.SendTo(m_requestBuffer.Buffer, m_requestBuffer.Count, SocketFlags.None, m_dnsServer);

            m_responseBuffer.Count = m_udpSocket.Receive(m_responseBuffer.Buffer);

            this.ValidateRequestID();
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
                tcpSocket.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.ReceiveTimeout, this.m_timeout);
                
                tcpSocket.Connect(m_dnsServer);
                
                m_lengthBuffer.Clear();
                m_lengthBuffer.AddUshort((ushort)m_requestBuffer.Count);
                
                tcpSocket.Send(m_lengthBuffer.Buffer, m_lengthBuffer.Count, SocketFlags.None);
                tcpSocket.Send(m_requestBuffer.Buffer, m_requestBuffer.Count, SocketFlags.None);

                //
                // First, receive the response message length
                //
                m_lengthBuffer.Clear();
                tcpSocket.Receive(m_lengthBuffer.Buffer, m_lengthBuffer.Capacity, SocketFlags.None);
                ushort responseSize = (ushort)(m_lengthBuffer.Buffer[0] << 8 | m_lengthBuffer.Buffer[1]);  // Network order
                //
                // Now receive the real response
                //
                m_responseBuffer.Count = tcpSocket.Receive(m_responseBuffer.Buffer, responseSize, SocketFlags.None);
                if (m_responseBuffer.Count != responseSize)
                {
                    throw new DnsProtocolException(DnsProtocolError.Failed);
                }
                
                this.ValidateRequestID();
            }
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
            if (this.m_requestID == (ushort) ushort.MaxValue)
            {
                this.m_requestID = ushort.MinValue;
                return this.m_requestID;
            }
            
            this.m_requestID++;
            return this.m_requestID;
        }

        #region IDisposable Members

        public void Dispose()
        {
            this.Close();
        }

        #endregion
    }
}
