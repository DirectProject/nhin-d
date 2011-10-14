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

namespace Health.Direct.DnsResponder
{
    public class TcpContext
    {
        Socket m_socket;
        long m_socketID;

        public TcpContext()
        {
        }

        public Socket Socket
        {
            get
            {
                return m_socket;
            }
        }

        internal long SocketID
        {
            get
            {
                return m_socketID;
            }
        }
        
        internal bool HasValidSocket
        {
            get
            {
                return (m_socketID > 0 && m_socket != null);
            }
        }
        
        internal void Init(Socket socket, long socketID)
        {
            this.Clear();
            
            m_socket = socket;
            m_socketID = socketID;
        }

        /// <summary>
        /// Clear gets called when Contexts are POOLED. 
        /// Override in your custom context
        /// </summary>
        public virtual void Clear()
        {
            m_socketID = -1;
            m_socket = null;
        }

        /// <summary>
        /// Synchronously read count bytes into the given buffer. 
        /// Return true if count bytes was successfully read
        /// </summary>
        /// <param name="buffer"></param>
        /// <param name="count"></param>
        /// <returns></returns>
        public bool Receive(byte[] buffer, int count)
        {
            if (buffer == null)
            {
                throw new ArgumentNullException();
            }

            int countRead = 0;
            while (countRead < count)
            {
                int countReceived = 0;
                if ((countReceived = this.Socket.Receive(buffer, countRead, count - countRead, SocketFlags.None)) <= 0)
                {
                    break;
                }
                countRead += countReceived;
            }

            return (countRead == count);
        }       
    }
}