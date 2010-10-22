using System;
using System.Collections.Generic;
using System.Threading;
using System.Diagnostics;

using Health.Direct.Common.Resolver;

namespace Health.Direct.DnsResponder.Tests
{
    public class Tester
    {
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
            Debug.Assert(m_thread == null);
            
            m_thread = new Thread(this.ThreadProc);
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
    }
}