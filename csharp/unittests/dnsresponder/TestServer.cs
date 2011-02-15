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

using Health.Direct.Common.DnsResolver;

namespace Health.Direct.DnsResponder.Tests
{
    public class TestServer
    {
        public static readonly DnsServerSettings DefaultSettings;
        public static TestServer Default;
        
        DnsServerSettings m_settings;
        DnsServer m_server;
        
        static TestServer()
        {
            DefaultSettings = new DnsServerSettings();
            DefaultSettings.Address = "127.0.0.1";
            DefaultSettings.Port = 5353;
            DefaultSettings.TcpServerSettings.MaxOutstandingAccepts = 4;
            DefaultSettings.TcpServerSettings.MaxActiveRequests = 16;
            DefaultSettings.UdpServerSettings.MaxOutstandingAccepts = 4;
            DefaultSettings.UdpServerSettings.MaxActiveRequests = 16;

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
        
        void TCPServer_Error(Exception ex)
        {
            //Console.WriteLine("TCP ERROR {0}", ex);
        }

        void UDPServer_Error(Exception ex)
        {
            //Console.WriteLine("UDP ERROR {0}", ex);
        }
    }
}