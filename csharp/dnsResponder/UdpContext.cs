using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Sockets;

namespace DnsResponder
{
    public class UdpContext : ServerProcessingContext
    {
        int m_bytesTransferred;
        
        public UdpContext()
        {
        }
                
        public virtual int BytesTransfered
        {
            get
            {
                return m_bytesTransferred;
            }
            set
            {
                m_bytesTransferred = value;
            }
        }
        
        public virtual ArraySegment<byte> ReceiveBuffer
        {
            get
            {
                throw new NotImplementedException();
            }            
        }

        public override void Init()
        {
        }

        public override void Clear()
        {
            m_bytesTransferred = 0;
        }
    }
}
