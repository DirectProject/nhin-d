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
using System.Threading;
using System.Net;
using System.Net.Sockets;

namespace DnsResponder
{
    public class UdpServer<TContext> : SocketServer<TContext>
        where TContext : UdpContext, new()
    {
        EventHandler<SocketAsyncEventArgs> m_receiveCompleteHandler;
        IServerApplication<TContext> m_application;
        IPEndPoint m_fromAny;
        
        public UdpServer(IPEndPoint endpoint, SocketServerSettings settings, IServerApplication<TContext> application)
            : this(endpoint, settings, application, null)
        {
        }

        public UdpServer(IPEndPoint endpoint, SocketServerSettings settings, IServerApplication<TContext> application, IWorkLoadThrottle workThrottle)
            : base(endpoint, new Socket(AddressFamily.InterNetwork, SocketType.Dgram, ProtocolType.Udp), settings, workThrottle)
        {
            if (application == null)
            {
                throw new ArgumentNullException();
            }
            
            m_application = application;
            m_receiveCompleteHandler = new EventHandler<SocketAsyncEventArgs>(this.ReceiveCompleted);
            m_fromAny = new IPEndPoint(IPAddress.Any, 0);
        }

        protected override void StartAccept(SocketAsyncEventArgs args)
        {
            TContext context = this.CreateContext();
            context.Socket = this.Socket;
            context.Init();
                  
            ArraySegment<byte> buffer = context.ReceiveBuffer;
            args.SetBuffer(buffer.Array, 0, buffer.Count);
            args.UserToken = context;

            if (!this.Socket.ReceiveFromAsync(args))
            {
                //
                // Call completed synchronously. 
                //
                this.ReceiveCompleted(null, args);
            }            
        }

        protected override void InitAcceptArgs(SocketAsyncEventArgs args)
        {
            args.RemoteEndPoint = m_fromAny;
            args.Completed += m_receiveCompleteHandler;
        }                
        
        void ReceiveCompleted(object sender, SocketAsyncEventArgs args)
        {
            int countRead = args.BytesTransferred;
            SocketError socketError = args.SocketError;
            IPEndPoint remoteEndpoint = (IPEndPoint) args.RemoteEndPoint;
            TContext context = (TContext)args.UserToken;
            //
            // Release the accept throttle so the listener thread can resume accepting connections
            //
            base.AcceptCompleted(args);

            if (socketError != SocketError.Success)
            {
                this.ProcessingComplete(context);
                return;
            }
            
            try
            {
                if (countRead > 0)
                {
                    context.BytesTransfered = countRead;
                    context.RemoteEndPoint = remoteEndpoint;
                    if (!m_application.Process(context))
                    {
                        context = null; // Completion will be asynchronous
                    }
                }
            }
            catch(Exception ex)
            {
                base.NotifyError(ex);
            }
            finally
            {
                if (context != null)
                {
                    this.ProcessingComplete(context);
                }
            }
        }
        
        protected override void  OnStart()
        {
            // Nothing to do                
        }

        protected override void OnStop()
        {
            // Nothing to do
        }
    }
}
