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
            this.SendChunk = 64;
            this.ReceiveChunk = 64;
        }
        
        public int SendChunk;
        public int ReceiveChunk;
        
        public void Send(DnsRequest request)
        {
            DnsBuffer requestBuffer = new DnsBuffer();
            request.Serialize(requestBuffer);
            //
            // Send size first
            //
            this.SendLength((ushort) requestBuffer.Count);
                        
            int sendChunk = requestBuffer.Count;
            if (this.SendChunk > 0)
            {
                sendChunk = Math.Min(this.SendChunk, requestBuffer.Count / 2 );
            }
            
            int countSent = 0;
            while (countSent < requestBuffer.Count)
            {
                int countToSend = Math.Min(sendChunk, requestBuffer.Count - countSent);
                countSent += m_socket.Send(requestBuffer.Buffer, countSent, countToSend, SocketFlags.None);
            }
            
            Assert.True(countSent == requestBuffer.Count);
        }

        public DnsResourceRecord Receive()
        {
            ushort length = this.ReceiveLength();
            
            DnsBuffer responseBuffer = new DnsBuffer(length);
            int countRead = 0;
            while (responseBuffer.Count < length)
            {
                countRead = m_socket.Receive(responseBuffer.Buffer, responseBuffer.Count, Math.Min(this.ReceiveChunk, length - responseBuffer.Count), SocketFlags.None);
                if (countRead <= 0)
                {
                    break;
                }
                responseBuffer.Count += countRead;
            }
            
            Assert.True(responseBuffer.Count == length);
            
            DnsResponse response = new DnsResponse(responseBuffer.CreateReader());
            Assert.True(response.AnswerRecords.Count != 0);
            return response.AnswerRecords[0];
        }
        
        public void SendLength(ushort count)
        {
            DnsBuffer lengthBuffer = new DnsBuffer(2);
            lengthBuffer.AddUshort(count);
            m_socket.Send(lengthBuffer.Buffer, lengthBuffer.Count, SocketFlags.None);
        }

        public ushort ReceiveLength()
        {
            DnsBuffer lengthBuffer = new DnsBuffer(2);
            lengthBuffer.Count = m_socket.Receive(lengthBuffer.Buffer, 2, SocketFlags.None);
            DnsBufferReader reader = lengthBuffer.CreateReader();
            return reader.ReadUShort();
        }
    }
}
