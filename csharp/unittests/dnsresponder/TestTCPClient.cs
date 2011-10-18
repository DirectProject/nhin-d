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
using System.Linq;
using System.Text;
using System.IO;
using System.Net.Sockets;
using Health.Direct.Common.DnsResolver;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.DnsResponder.Tests
{
    /// <summary>
    /// A test client using sockets directly, instead of DnsClient
    /// </summary>
    public class TestTCPClient
    {
        Socket m_socket;

        public TestTCPClient(Socket socket)
        {
            m_socket = socket;
            m_socket.NoDelay = true;
            m_socket.DontFragment = false;
            this.SendChunk = 8;
            this.ReceiveChunk = 8;
        }
        
        public int SendChunk;
        public int ReceiveChunk;

        public DnsResourceRecord Receive()
        {
            DnsBuffer responseBuffer = this.ReceiveBuffer();            
            DnsResponse response = new DnsResponse(responseBuffer.CreateReader());
            Assert.True(response.AnswerRecords.Count != 0);
            return response.AnswerRecords[0];
        }
        
        public DnsRequest ReceiveRequest()
        {
            DnsBuffer responseBuffer = this.ReceiveBuffer();
            DnsRequest request = new DnsRequest(responseBuffer.CreateReader());
            Assert.True(!string.IsNullOrEmpty(request.Question.Domain));
            return request;
        }
        
        public ushort ReceiveLength()
        {
            DnsBuffer lengthBuffer = new DnsBuffer(2);
            lengthBuffer.Count = m_socket.Receive(lengthBuffer.Buffer, 2, SocketFlags.None);
            DnsBufferReader reader = lengthBuffer.CreateReader();
            return reader.ReadUShort();
        }

        public DnsBuffer ReceiveBuffer()
        {
            ushort length = this.ReceiveLength();
            DnsBuffer buffer = this.ReceiveBuffer(length);
            Assert.True(buffer.Count == length);
            return buffer;
        }
                
        public DnsBuffer ReceiveBuffer(ushort length)
        {
            DnsBuffer buffer = new DnsBuffer(length);
            int countRead = 0;
            while (buffer.Count < length)
            {
                countRead = m_socket.Receive(buffer.Buffer, buffer.Count, Math.Min(this.ReceiveChunk, length - buffer.Count), SocketFlags.None);
                if (countRead <= 0)
                {
                    break;
                }
                buffer.Count += countRead;
            }
            
            return buffer;
        }

        public void Send(DnsRequest request)
        {
            DnsBuffer requestBuffer = new DnsBuffer();
            request.Serialize(requestBuffer);
            this.SendBuffer(requestBuffer);
        }
        
        public void Send(DnsResponse response)
        {
            DnsBuffer buffer = new DnsBuffer();
            response.Serialize(buffer);
            this.SendBuffer(buffer);
        }
        
        public void SendLength(ushort count)
        {
            DnsBuffer lengthBuffer = new DnsBuffer(2);
            lengthBuffer.AddUshort(count);
            m_socket.Send(lengthBuffer.Buffer, lengthBuffer.Count, SocketFlags.None);
        }
    
        public void SendBuffer(DnsBuffer buffer)        
        {
            // Send size first
            this.SendLength((ushort)buffer.Count);

            int sendChunk = buffer.Count;
            if (this.SendChunk > 0)
            {
                sendChunk = Math.Min(this.SendChunk, buffer.Count / 2);
            }

            int countSent = 0;
            while (countSent < buffer.Count)
            {
                int countToSend = Math.Min(sendChunk, buffer.Count - countSent);
                countSent += m_socket.Send(buffer.Buffer, countSent, countToSend, SocketFlags.None);
            }

            Assert.True(countSent == buffer.Count);
        }
    }
}
