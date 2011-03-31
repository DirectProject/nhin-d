using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using Health.Direct.Common.DnsResolver;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.DnsResponder.Tests
{
    public class BadTcpClient : IDisposable
    {        
        Socket m_socket;
        string m_host;
        int m_port;
        
        public BadTcpClient(string host, int port)
        {
            m_socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            m_host = host;
            m_port = port;
        }
        
        public void Connect()
        {
            try
            {
                m_socket.Connect(m_host, m_port);
            }
            catch
            {
            }
        }
        
        public void SendLength(ushort length)
        {
            DnsBuffer buffer = new DnsBuffer(2);
            buffer.AddUshort(length);
            this.Send(buffer.Buffer);
        }
        
        public void Send(byte[] bytes)
        {
            m_socket.Send(bytes);   
        }

        public void SendGarbage(int length)
        {
            this.Send(CreateGarbage(length));
        }
        
        public void SendSlowGarbage(int length, int pauseMs)
        {
            byte[] buffer = CreateGarbage(length);
            for (int i = 0; i < buffer.Length; ++i)
            {
                System.Threading.Thread.Sleep(pauseMs);
                m_socket.Send(buffer, i, 1, SocketFlags.None);
            }
        }
        
        public void ConnectAndPause(int pauseMs)
        {
            this.Connect();
            System.Threading.Thread.Sleep(pauseMs);
        }
                
        public void Close()
        {
            try
            {
                m_socket.Close();
            }
            catch
            {
            }
        }
        
        public static byte[] CreateGarbage(int length)
        {
            byte[] buffer = new byte[length];
            Random rand = new Random();
            rand.NextBytes(buffer);
            return buffer;
        }

        #region IDisposable Members

        public void Dispose()
        {
            if (m_socket != null)
            {
                m_socket.Close();
            }
        }

        #endregion
    }
}
