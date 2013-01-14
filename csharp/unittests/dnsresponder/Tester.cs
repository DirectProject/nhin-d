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
using System.Collections.Generic;
using System.Threading;
using System.Diagnostics;
using System.Net.Sockets;
using Health.Direct.Common.DnsResolver;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.DnsResponder.Tests
{
    public class Tester
    {
        public const bool UseUdp = true;
        public const bool UseTcp = false;
        
        public static IEnumerable<T> Repeater<T>(IEnumerable<T> inner, int count)
        {
            for (int i = 0; i < count; ++i)
            {
                foreach(T item in inner)
                {
                    yield return item;
                }
            }
        }
        
        public static IEnumerable<AddressRecord> ResolveA(TestServer server, string domain, bool useUDP)
        {
            using (DnsClient client = server.CreateClient())
            {
                client.UseUDPFirst = useUDP;
                return client.ResolveA(domain);
            }
        }

        public static bool Equals<T>(IEnumerable<T> x, IEnumerable<DnsResourceRecord> y)
            where T : DnsResourceRecord
        {
            foreach (T ix in x)
            {
                bool found = false;
                foreach (T iy in y)
                {
                    if (ix.Equals(iy))
                    {
                        found = true;
                        break;
                    }
                }
                if (!found)
                {
                    return false;
                }
            }

            return true;
        }
    }

    public class TestThread
    {
        TestServer m_server;
        int m_success;
        int m_failure;
        bool m_udp;
        DnsStandard.RecordType m_type;
        TimeSpan m_timeout;
        Thread m_thread;

        public TestThread(DnsStandard.RecordType type, bool udp)
            : this(type, udp, TestServer.Default)
        {
        }
        
        public TestThread(DnsStandard.RecordType type, bool udp, TestServer server)
            : this(type, udp, server, TimeSpan.FromMilliseconds(1000))
        {
        }

        public TestThread(DnsStandard.RecordType type, bool udp, TestServer server, TimeSpan timeout)
        {
            m_server = server;
            m_udp = udp;
            m_type = type;
            m_timeout = timeout;
        }
        
        public int Success
        {
            get
            {
                return m_success;
            }
        }
        
        public int Failure
        {
            get
            {
                return m_failure;
            }
        }
                        
        public void Start(IEnumerable<string> domains)
        {
            this.Start(domains, this.ThreadProc);
        }

        public void Start(IEnumerable<string> domains, ParameterizedThreadStart proc)
        {
            Debug.Assert(m_thread == null);

            m_thread = new Thread(proc);
            m_thread.Start(domains);
        }
        
        public void Stop()
        {
            Debug.Assert(m_thread != null);            
            m_thread.Join();
        }
        
        public void ThreadProc(object state)
        {
            IEnumerable<string> domains = (IEnumerable<string>) state;
            using(DnsClient client = m_server.CreateClient())
            {
                client.UseUDPFirst = m_udp;
                client.Timeout = m_timeout;
                foreach(string domain in domains)
                {
                    try
                    {
                        DnsResponse response = client.Resolve(new DnsRequest(m_type, domain));
                        if (response.IsSuccess && response.HasAnswerRecords)
                        {
                            m_success++;
                        }               
                        else
                        {
                            m_failure++;
                        }     
                    }
                    catch
                    {
                        m_failure++;
                    }
                }
            }
        }

        public void TcpSocketDropper(object state)
        {
            IEnumerable<string> domains = (IEnumerable<string>)state;
            Random random = new Random();
            byte[] sizeBlock = new byte[2] {0, 32};
            foreach (string domain in domains)
            {
                try
                {
                    using(Socket socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp))
                    {
                        socket.NoDelay = true;
                        socket.Connect(m_server.Server.Settings.Address, m_server.Server.Settings.Port);
                        if (random.NextDouble() > 0.5)
                        {
                            socket.Send(sizeBlock);
                        }
                        m_success++;
                        socket.Close(0);
                    }
                }
                catch
                {
                    m_failure++;
                }
            }
        }
    }
}