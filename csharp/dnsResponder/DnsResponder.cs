/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;

using Health.Direct.Common.DnsResolver;

namespace Health.Direct.DnsResponder
{
    public abstract class DnsResponder
    {
        DnsServer m_server;
        DnsServerSettings m_settings;
        
        public DnsResponder(DnsServer server)
        {
            if (server == null)
            {
                throw new ArgumentNullException();
            }
            
            m_server = server;
            m_settings = server.Settings;
        }
                
        public DnsServerSettings Settings
        {
            get
            {
                return m_settings;
            }
        }

        public event Action<DnsRequest> Received;
        public event Action<DnsResponse> Responding;
        
        public abstract void Start();
        public abstract void Stop();
        
        public DnsResponse ProcessRequest(DnsBuffer buffer)
        {
            if (buffer == null)
            {
                throw new ArgumentNullException();
            }
            return this.ProcessRequest(this.DeserializeRequest(buffer.Buffer, buffer.Count));
        }
                
        public DnsResponse ProcessRequest(DnsRequest request)
        {
            if (request == null)
            {
                throw new ArgumentNullException();
            }
            
            this.Received.SafeInvoke(request);
            
            DnsResponse response = null;
            try
            {
                this.Validate(request);

                response = m_server.Store.Get(request);
                if (response == null || !response.IsSuccess)
                {
                    throw new DnsServerException(DnsStandard.ResponseCode.NameError);
                }                
            }
            catch (DnsProtocolException)
            {
                response = this.ProcessError(request, DnsStandard.ResponseCode.FormatError);
            }
            catch (DnsServerException serverEx)
            {
                response = this.ProcessError(request, serverEx.ResponseCode);
            }

            this.Responding.SafeInvoke(response);
            
            return response;
        }

        DnsResponse ProcessError(DnsRequest request, DnsStandard.ResponseCode code)
        {
            DnsResponse errorResponse = new DnsResponse(request);
            errorResponse.Header.ResponseCode = code;
            return errorResponse;
        }

        protected DnsRequest DeserializeRequest(byte[] buffer, int requestSize)
        {
            if (requestSize <= 0 || requestSize > this.Settings.MaxRequestSize)
            {
                throw new DnsServerException(DnsStandard.ResponseCode.Refused);
            }

            return new DnsRequest(new DnsBufferReader(buffer, 0, requestSize));
        }
                
        protected void Serialize(DnsResponse response, DnsBuffer buffer, int maxResponse)
        {
            buffer.Clear();
            response.Serialize(buffer);
            if (buffer.Count > maxResponse)
            {
                response.Truncate();
                buffer.Clear();
                response.Serialize(buffer);
            }
        }

        protected void Validate(DnsRequest request)
        {
            request.Validate();
            if (request.Header.QuestionCount < 1)
            {
                throw new DnsProtocolException(DnsProtocolError.InvalidQuestionCount);
            }

            if (request.Header.QuestionCount > m_server.Settings.MaxQuestionCount)
            {
                throw new DnsServerException(DnsStandard.ResponseCode.Refused);
            }
        }
    }
}