using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Sockets;
using DnsResolver;

namespace DnsResponder
{
    public class DnsUdpContext : UdpContext
    {
        DnsBuffer m_buffer;
        
        public DnsUdpContext()
        {
            m_buffer = new DnsBuffer(DnsStandard.MaxUdpMessageLength);
        }

        public DnsBuffer Buffer
        {
            get
            {
                return m_buffer;
            }
        }
        
        public override ArraySegment<byte> ReceiveBuffer
        {
            get 
            { 
                // Struct, cheap
                return new ArraySegment<byte>(m_buffer.Buffer, 0, m_buffer.Capacity);
            }
        }

        public override int BytesTransfered
        {
            set 
            { 
                m_buffer.Count = value;
            }
        }
        
        public override void Init()
        {
            m_buffer.Clear();
        }

        public void SendResponse()
        {
            this.Send(m_buffer);
        }

        public void Send(DnsBuffer buffer)
        {
            if (buffer == null)
            {
                throw new ArgumentNullException();
            }

            this.Socket.SendTo(buffer.Buffer, buffer.Count, SocketFlags.None, this.RemoteEndPoint);
        }
    }
}
