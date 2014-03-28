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
using Health.Direct.Common.DnsResolver;
using Health.Direct.Common.Extensions;

namespace Health.Direct.DnsResponder
{
    public interface IDnsContext
    {
        DnsBuffer DnsBuffer
        {
            get;
        }
        
        void ReceiveRequest();
        void SendResponse();
    }
    
    public abstract class DnsResponder
    {
        IDnsStore m_store;
        DnsServerSettings m_settings;
                
        public DnsResponder(IDnsStore store, DnsServerSettings settings)
        {
            if (store == null)
            {
                throw new ArgumentNullException("store");
            }
            if (settings == null)
            {
                throw new ArgumentNullException("settings");
            }
            
            m_store = store;
            m_settings = settings;
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

                response = m_store.Get(request);
                if (response == null || !response.IsSuccess)
                {
                    throw new DnsServerException(DnsStandard.ResponseCode.NameError);
                }
                
                this.FixupTTL(response);
            }
            catch (DnsProtocolException)
            {
                response = this.ProcessError(request, DnsStandard.ResponseCode.FormatError);
            }
            catch (DnsServerException serverEx)
            {
                response = this.ProcessError(request, serverEx.ResponseCode);
            }
            catch(Exception ex)
            {
                this.HandleException(ex);
                response = this.ProcessError(request, DnsStandard.ResponseCode.ServerFailure);
            }
            
            this.Responding.SafeInvoke(response);
            
            return response;
        }
        
        public void RequestResponse(IDnsContext context, ushort maxBufferLength)
        {
            if (context == null)
            {
                throw new ArgumentNullException();
            }

            try
            {
                //
                // If we fail at parsing or receiving the request, then any exceptions will get logged and
                // the socket will be silently closed
                // 
                context.ReceiveRequest();

                DnsResponse response = this.ProcessRequest(context.DnsBuffer);
                if (response != null)
                {
                    this.Serialize(response, context.DnsBuffer, maxBufferLength);
                    context.SendResponse();
                }
            }
            catch (IndexOutOfRangeException)
            {
                // Valid exception thrown during processing when bad requests are sent...
                // We won't dignify bad requests with a response
            }
            catch (DnsServerException)
            {
                // Valid exception thrown during processing when bad requests are sent...
                // We won't dignify bad requests with a response
            }
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

            if (request.Header.QuestionCount > this.Settings.MaxQuestionCount)
            {
                throw new DnsServerException(DnsStandard.ResponseCode.Refused);
            }
        }
        
        void FixupTTL(DnsResponse response)
        {
            if (response.HasAnswerRecords)
            {
                this.FixupTTL(response.AnswerRecords);
            }
            if (response.HasAdditionalRecords)
            {
                this.FixupTTL(response.AdditionalRecords);
            }
            if (response.HasNameServerRecords)
            {
                this.FixupTTL(response.NameServerRecords);
            }
        }
        
        void FixupTTL(DnsResourceRecordCollection matches)
        {
            if (matches.IsNullOrEmpty())
            {
                return;
            }
            
            for (int i = 0, count = matches.Count; i < count; ++i)
            {
                DnsResourceRecord match = matches[i];
                if (match.TTL <= 0)
                {
                    match.TTL = this.Settings.DefaultTTL;
                }
            } 
        }
        
        protected virtual void HandleException(Exception ex)
        {
        }
    }
}