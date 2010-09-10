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
using System.Net;
using System.Net.Sockets;
using System.Threading;
using DnsResolver;

namespace DnsResponder
{
    /// <summary>
    /// Simple DNS TCP Responder
    /// </summary>
    public class DnsResponderTCP : DnsResponder, IServerApplication
    {
        SynchronizedObjectPool<DnsTcpContext> m_contextPool;
        
        public DnsResponderTCP(DnsServer server)
            : base(server)
        {
            m_contextPool = new SynchronizedObjectPool<DnsTcpContext>(this.Settings.ServerSettings.MaxActiveRequests);
        }
        
        public ProcessingContext CreateContext()
        {
            DnsTcpContext context = m_contextPool.Get();
            if (context == null)
            {
                context = new DnsTcpContext(this.Server);
            }
            else
            {
                context.Clear();
            }
            
            return context;
        }

        public void ReleaseContext(ProcessingContext context)
        {
            m_contextPool.Put((DnsTcpContext) context);
        }

        public void Process(ProcessingContext context)
        {
            //
            // All unhandled (or loggable) instructions fall through to the SocketServer
            //
            this.ProcessRequest((DnsTcpContext) context);
        }
                
        void ProcessRequest(DnsTcpContext context)
        {
            //
            // If we fail at parsing or receiving the request, then any exceptions will get logged and
            // the socket will be silently closed
            // 
            DnsRequest request = context.ReceiveRequest();  // pool requests object if neeeded
            base.NotifyReceived(request);
            
            try
            {
                DnsResponse response = base.ProcessRequest(request);
                if (response == null || !response.IsSuccess)
                {
                    throw new DnsServerException(DnsStandard.ResponseCode.NameError);
                }
                
                base.NotifyResponse(response);
                
                context.SendResponse(response);
            }
            catch (DnsProtocolException)
            {
                this.ProcessError(context, request, DnsStandard.ResponseCode.FormatError);
            }
            catch (DnsServerException serverEx)
            {
                this.ProcessError(context, request, serverEx.ResponseCode);
            }
        }

        void ProcessError(DnsTcpContext context, DnsRequest request, DnsStandard.ResponseCode code)
        {
            DnsResponse errorResponse = base.ProcessError(request, code);
            
            base.NotifyResponse(errorResponse);
            
            context.SendResponse(errorResponse);
        }
    }
}
