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
using System.Net;
using System.Net.Sockets;

using Health.Direct.Common.DnsResolver;

namespace Health.Direct.DnsResponder
{
    public class DnsTcpContext : TcpContext, IDnsContext
    {
        DnsResponderTCP m_responder;
        DnsBuffer m_buffer;
        DnsBuffer m_sizeBuffer;
        
        /// <summary>
        /// Create a new processing context. 
        /// This constructor can also be used to build contexts for pooling
        /// </summary>
        /// <param name="server"></param>        
        public DnsTcpContext()
        {
            m_buffer = new DnsBuffer(DnsStandard.MaxUdpMessageLength);
            m_sizeBuffer = new DnsBuffer(2);
        }
        
        public DnsBuffer DnsBuffer
        {
            get
            {
                return m_buffer;
            }
        }

        public DnsResponderTCP Responder
        {
            get
            {
                if (m_responder == null)
                {
                    throw new InvalidOperationException("Not initialized");
                }
                return m_responder;
            }
        }

        public IPEndPoint Endpoint
        {
            get
            {
                return this.Socket.RemoteEndPoint as IPEndPoint;
            }
        }
        
        public void Init(DnsResponderTCP responder)
        {
            if (responder == null)
            {
                throw new ArgumentNullException();
            }

            this.Clear();
            m_responder = responder;
            this.ReserveCapacity(responder.Settings.TcpServerSettings.ReadBufferSize);
        }
                                
        public void ReserveCapacity(int count)
        {
            m_buffer.ReserveCapacity(count);
        }
        
        public override void Clear()
        {
            m_buffer.Clear();
            m_sizeBuffer.Clear();
        }

        //-----------------------
        // 
        // Receive
        //        
        //-----------------------
        
        /// <summary>
        /// Receive bytes into this context's built in buffer
        /// </summary>
        /// <param name="count"></param>
        public void Receive(int count)
        {
            this.Receive(m_buffer, count);
        }
        
        /// <summary>
        /// Receive bytes into the given buffer
        /// </summary>
        /// <param name="buffer"></param>
        /// <param name="count"></param>
        public void Receive(DnsBuffer buffer, int count)
        {
            if (buffer == null)
            {
                throw new ArgumentNullException();
            }
            
            buffer.Clear();
            buffer.ReserveCapacity(count);
            if (!base.Receive(buffer.Buffer, count))
            {
                throw new DnsServerException(DnsStandard.ResponseCode.Refused);
            }
            buffer.Count = count;
        }
                
        /// <summary>
        /// Synchronous receive of a request
        /// </summary>
        public void ReceiveRequest()
        {            
            ushort requestSize = this.ReceiveSize();
            if (requestSize <= 0 || requestSize > this.Responder.Settings.MaxRequestSize)
            {
                throw new DnsServerException(DnsStandard.ResponseCode.Refused);
            }

            this.Receive(m_buffer, requestSize);
        }
        
        ushort ReceiveSize()
        {
            this.Receive(m_sizeBuffer, 2);
            DnsBufferReader reader = m_sizeBuffer.CreateReader();
            return reader.ReadUShort();
        }
                        
        //----------------
        //
        // Send
        //
        //----------------        
        public void SendResponse()
        {       
            this.SendSize((ushort)m_buffer.Count);
            this.Send(m_buffer);
        }
        
        public void Send(DnsBuffer buffer)
        {
            if (buffer == null)
            {
                throw new ArgumentNullException();
            }
            
            this.Socket.Send(buffer.Buffer, buffer.Count, SocketFlags.None);
        }
        
        void SendSize(ushort size)
        {            
            m_sizeBuffer.Clear();
            m_sizeBuffer.AddUshort(size);
            this.Send(m_sizeBuffer);    
        }
        
        void Serialize(DnsResponse response)
        {
            m_buffer.Clear();
            response.Serialize(m_buffer);        
        }
    }
}