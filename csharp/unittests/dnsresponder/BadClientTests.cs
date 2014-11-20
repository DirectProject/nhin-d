using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Health.Direct.Common.DnsResolver;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.DnsResponder.Tests
{
    public class BadClientTests
    {
        public const int DefaultIterations = 25;
        public const int DefaultWaitMs = 60 * 1000;
        [Fact]
        public void ConnectAndDrop()
        {
            TestServer.Default.Counters.InitWait(DefaultIterations);

            for (int i = 0; i < DefaultIterations; ++i)
            {
                BadTcpClient client = TestServer.Default.CreateBadTcpClient();
                client.Connect();
                client.Close();
            }
            
            Assert.DoesNotThrow(() => TestServer.Default.Counters.Wait.WaitOne(DefaultWaitMs));  
            Assert.True(TestServer.Default.Counters.IsConnectionBalanced);
            Assert.True(TestServer.Default.AreMaxTcpAcceptsOutstanding());
        }

        [Fact]
        public void ConnectSendLengthAndDrop()
        {
            TestServer.Default.Counters.InitWait(DefaultIterations);
            Random rand = new Random();

            for (int i = 0; i < DefaultIterations; ++i)
            {
                BadTcpClient client = TestServer.Default.CreateBadTcpClient();
                client.Connect();
                client.SendLength((ushort) rand.Next(10, 60));
                client.Close();
            }

            Assert.DoesNotThrow(() => TestServer.Default.Counters.Wait.WaitOne(DefaultWaitMs));
            Assert.True(TestServer.Default.Counters.IsConnectionBalanced);
            Assert.True(TestServer.Default.AreMaxTcpAcceptsOutstanding());
        }
        
        [Fact]
        public void SendGarbage()
        {
            TestServer.Default.Counters.InitWait(DefaultIterations);
            Random rand = new Random();

            for (int i = 0; i < DefaultIterations; ++i)
            {
                BadTcpClient client = TestServer.Default.CreateBadTcpClient();
                client.Connect();
                
                ushort length = (ushort)rand.Next(10, 60);
                client.SendLength(length);                
                client.SendGarbage(length);
                
                client.Close();
            }

            Assert.DoesNotThrow(() => TestServer.Default.Counters.Wait.WaitOne(DefaultWaitMs));
            Assert.True(TestServer.Default.Counters.IsConnectionBalanced);
            Assert.True(TestServer.Default.AreMaxTcpAcceptsOutstanding());
        }

        [Fact]
        public void SendWrongSize()
        {
            TestServer.Default.Counters.InitWait(DefaultIterations);
            Random rand = new Random();

            for (int i = 0; i < DefaultIterations; ++i)
            {
                BadTcpClient client = TestServer.Default.CreateBadTcpClient();
                client.Connect();

                ushort length = (ushort)rand.Next(1, 60);
                client.SendLength(length);
                client.SendGarbage(rand.Next(1, 60));

                client.Close();
            }

            Assert.DoesNotThrow(() => TestServer.Default.Counters.Wait.WaitOne(DefaultWaitMs));
            Assert.True(TestServer.Default.Counters.IsConnectionBalanced);
            Assert.True(TestServer.Default.AreMaxTcpAcceptsOutstanding());
        }
    }
}
