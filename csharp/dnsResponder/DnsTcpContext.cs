/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using DnsResolver;
using System.Net;
using System.Net.Sockets;

namespace DnsResponder
{
    public class DnsTcpContext : ProcessingContext
    {
        DnsServer m_server;
        DnsBuffer m_buffer;
        DnsBuffer m_sizeBuffer;
        
        /// <summary>
        /// Create a new processing context. 
        /// This constructor can also be used to build contexts for pooling
        /// </summary>
        /// <param name="server"></param>        
        public DnsTcpContext(DnsServer server)
        {
            if (server == null)
            {
                throw new ArgumentNullException();
            }
            m_server = server;
            m_buffer = new DnsBuffer(server.Settings.InitialBufferSize);
            m_sizeBuffer = new DnsBuffer(2);
        }
        
        public DnsBuffer DnsBuffer
        {
            get
            {
                return m_buffer;
            }
        }
        
        public DnsServer Server
        {
            get
            {
                return m_server;
            }
        }
                
        public IPEndPoint Endpoint
        {
            get
            {
                return this.Socket.RemoteEndPoint as IPEndPoint;
            }
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
        /// Synchronous receive Dns Message into this.DnsBuffer over TCP
        /// TCP messages are preceded by a 2 byte message SIZE 
        /// </summary>
        public void ReceiveMessage()
        {
            ushort requestSize = this.ReceiveSize();
            if (requestSize > m_server.Settings.MaxRequestSize || requestSize == 0)
            {
                throw new DnsServerException(DnsStandard.ResponseCode.Refused);
            }
            
            this.Receive(m_buffer, requestSize);            
        }

        public void Receive(int count)
        {
            this.Receive(m_buffer, count);
        }

        public void Receive(DnsBuffer buffer, int count)
        {
            if (buffer == null)
            {
                throw new ArgumentNullException();
            }
            
            buffer.Clear();
            buffer.ReserveCapacity(count);
            int countRead = this.Socket.Receive(buffer.Buffer, count, SocketFlags.None);
            if (countRead != count)
            {
                throw new DnsServerException(DnsStandard.ResponseCode.Refused);
            }
            buffer.Count = countRead;
        }
                
        /// <summary>
        /// Synchronous receive of a request
        /// </summary>
        public DnsRequest ReceiveRequest()
        {
            this.ReceiveMessage();
            return this.DeserializeRequest();
        }
        
        public DnsHeader DeserializeHeader()
        {
            DnsBufferReader reader = m_buffer.CreateReader();
            return new DnsHeader(ref reader);
        }
        
        public DnsRequest DeserializeRequest()
        {
            DnsRequest request = new DnsRequest(m_buffer.CreateReader());
            this.Validate(request);
            return request;
        }
        
        ushort ReceiveSize()
        {
            this.Receive(m_sizeBuffer, 2);
            DnsBufferReader reader = m_sizeBuffer.CreateReader();
            return reader.ReadUShort();
        }
                
        void Validate(DnsRequest request)
        {
            request.Validate();
            if (request.Header.QuestionCount > m_server.Settings.MaxQuestionCount)
            {
                throw new DnsServerException(DnsStandard.ResponseCode.Refused);
            }
        }
        
        //----------------
        //
        // Send
        //
        //----------------        
        public void SendResponse(DnsResponse response)
        {       
            this.Serialize(response);     
            if (m_buffer.Count > ushort.MaxValue)
            {
                response.Truncate();
                this.Serialize(response);
            }

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
