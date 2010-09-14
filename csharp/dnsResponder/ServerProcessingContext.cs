using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Sockets;

namespace DnsResponder
{
    public abstract class ServerProcessingContext
    {
        Socket m_socket;
        IPEndPoint m_remoteEndpoint;
        
        public ServerProcessingContext()
        {
        }

        public Socket Socket
        {
            get
            {
                return m_socket;
            }
            internal set
            {
                m_socket = value;
            }
        }

        public IPEndPoint RemoteEndPoint
        {
            get
            {
                return m_remoteEndpoint;
            }
            internal set
            {
                m_remoteEndpoint = value;
            }
        }

        public abstract void Init();
        
        public virtual void Clear()
        {
            m_remoteEndpoint = null;
            m_socket = null;
        }
    }
}
