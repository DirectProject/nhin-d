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
using System.Net.Sockets;
using System.Threading;
using System.Net;
using Health.Direct.Common.DnsResolver;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.DnsResponder.Tests
{
    public class TestServer
    {
        public static readonly DnsStandard.RecordType[] NotSupported = new DnsStandard.RecordType[]
        {
            DnsStandard.RecordType.AXFR,
            DnsStandard.RecordType.HINFO,
            DnsStandard.RecordType.AAAA,
            DnsStandard.RecordType.MAILA,
            DnsStandard.RecordType.MB,
            DnsStandard.RecordType.PTR,
            DnsStandard.RecordType.WKS
        };
        
        public static readonly DnsServerSettings DefaultSettings;
        public static TestServer Default;

        DnsServerSettings m_settings;
        DnsServer m_server;
        
        public Counters Counters = new Counters();
        
        static TestServer()
        {
            DefaultSettings = new DnsServerSettings();
            DefaultSettings.Address = "127.0.0.1";
            DefaultSettings.Port = 5353;
            DefaultSettings.TcpServerSettings.MaxOutstandingAccepts = 4;
            DefaultSettings.TcpServerSettings.MaxActiveRequests = 16;
            DefaultSettings.TcpServerSettings.ReceiveTimeout = 60 * 1000;
            DefaultSettings.TcpServerSettings.SendTimeout= 60 * 1000;
            
            DefaultSettings.UdpServerSettings.MaxOutstandingAccepts = 4;
            DefaultSettings.UdpServerSettings.MaxActiveRequests = 16;
            DefaultSettings.UdpServerSettings.ReceiveTimeout = 60 * 1000;
            DefaultSettings.UdpServerSettings.SendTimeout = 60 * 1000;

            Default = new TestServer(TestStore.Default.Store);
            Default.Server.Start();
        }
        
        public TestServer(IDnsStore store)
            : this(store, TestServer.DefaultSettings)
        {
        }

        public TestServer(IDnsStore store, DnsServerSettings settings)
        {
            m_settings = settings;
            
            m_server = new DnsServer(store, settings);
            m_server.UDPResponder.Server.Error += UDPServer_Error;
            m_server.TCPResponder.Server.Error += TCPServer_Error;
            m_server.TCPResponder.Server.ConnectionAccepted += TCP_Accept;
            m_server.TCPResponder.Server.ConnectionClosed += TCP_Complete;
        }
        
        public DnsServer Server
        {
            get
            {
                return m_server;
            }
        }
        
        public DnsClient CreateClient()
        {
            DnsServerSettings settings = this.Server.Settings;
            DnsClient client = new DnsClient(settings.Address, settings.Port);
            return client;
        }
        
        public BadTcpClient CreateBadTcpClient()
        {
            return new BadTcpClient(this.Server.Settings.Address, this.Server.Settings.Port);
        }
        
        public Socket CreateTCPSocket()
        {
            DnsServerSettings settings = this.Server.Settings;
            Socket socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            socket.Connect(settings.Address, settings.Port);        
            return socket;
        }
        
        void TCPServer_Error(Exception ex)
        {
            //Console.WriteLine("TCP ERROR {0}", ex);
            Interlocked.Increment(ref this.Counters.TCPErrors);
        }

        void UDPServer_Error(Exception ex)
        {
            //Console.WriteLine("UDP ERROR {0}", ex);
            Interlocked.Increment(ref this.Counters.UDPErrors);
        }
        
        void TCP_Accept(Socket socket)
        {
            this.Counters.Accepted();
        }

        void TCP_Complete(Socket socket)
        {
            this.Counters.Completed();
        }
        
        public bool AreMaxTcpAcceptsOutstanding()
        {
            Thread.Sleep(1000);
            return (this.Server.Settings.TcpServerSettings.MaxOutstandingAccepts  == 
                        this.Server.TCPResponder.Server.OutstandingAcceptCount);
        }

        public bool AreMaxUdpAcceptsOutstanding()
        {
            Thread.Sleep(1000);
            return (this.Server.Settings.UdpServerSettings.MaxOutstandingAccepts ==
                        this.Server.UDPResponder.Server.OutstandingAcceptCount);
        }
    }
    
    public class Counters
    {
        public long CountAccepted = 0;
        public long CountClosed = 0;
        public long CountExpected = 0;
        public long UDPErrors = 0;
        public long TCPErrors = 0;
        public AutoResetEvent Wait;
               
        public bool IsConnectionBalanced
        {
            get {return (CountAccepted == CountClosed);}
        }
        
        public void Clear()
        {
            CountAccepted = 0;
            CountClosed = 0;
            CountExpected = 0;
            UDPErrors = 0;
            TCPErrors = 0;
        }
        
        public void InitWait(int requestCount)
        {
            this.Clear();
            this.CountExpected = requestCount;
            if (this.Wait != null)
            {
                this.Wait.Reset();
            }
            else
            {
                this.Wait = new AutoResetEvent(false);
            }
        }
        
        public void Accepted()
        {
            Interlocked.Increment(ref CountAccepted);
        }
        
        public void Completed()
        {
            if (Interlocked.Increment(ref CountClosed) == this.CountExpected)
            {
                this.Wait.Set();
            }
        }
        
        public void AssertConnectionBalanced()
        {
            Assert.True(this.IsConnectionBalanced);
        }
    }
    
    public class FakeServer : IDisposable
    {
        TcpListener m_listener;
        AsyncCallback m_callback;        
        IDnsStore m_store;

        public FakeServer(IDnsStore store, int port)
        {
            m_store = store;
            m_listener = new TcpListener(IPAddress.Parse("127.0.0.1"), port);
            m_callback = this.HandleRequest;
        }
                
        public void Start()
        {
            m_listener.Start();
        }
        
        public void BeginAccept()
        {
            m_listener.BeginAcceptSocket(this.HandleRequest, this);
        }
        
        void HandleRequest(IAsyncResult result)
        {
            try
            {
                using(Socket socket = m_listener.EndAcceptSocket(result))
                {
                    TestTCPClient client = new TestTCPClient(socket); // Chunk sends
                    DnsRequest request = client.ReceiveRequest();
                    DnsResponse response = null;
                    try
                    {
                        response = m_store.Get(request);
                        if (response == null)
                        {
                            response = new DnsResponse(request);
                            response.Header.ResponseCode = DnsStandard.ResponseCode.NameError;
                        }
                    }
                    catch
                    {
                        response = new DnsResponse(request);
                        response.Header.ResponseCode = DnsStandard.ResponseCode.NameError;
                    }
                    
                    if (response != null)
                    {
                        client.Send(response);
                    }
                }
            }
            catch
            {
            }
        }


        public void Dispose()
        {
            if (m_listener != null)
            {
                m_listener.Stop();
                m_listener = null;
            }
        }

    }
}