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

namespace Health.Direct.DnsResponder
{
    public class DnsUdpServer : SocketServer
    {
        IHandler<DnsUdpContext> m_requestHandler;
        EventHandler<SocketAsyncEventArgs> m_receiveCompleteHandler;
        IPEndPoint m_fromAny;
        SynchronizedObjectPool<DnsUdpContext> m_contextPool;
        
        public DnsUdpServer(IPEndPoint endpoint, SocketServerSettings settings, IHandler<DnsUdpContext> requestHandler)
            : this(endpoint, settings, requestHandler, null)
        {
        }

        public DnsUdpServer(IPEndPoint endpoint, SocketServerSettings settings, IHandler<DnsUdpContext> requestHandler, IWorkLoadThrottle workThrottle)
            : base(endpoint, new Socket(AddressFamily.InterNetwork, SocketType.Dgram, ProtocolType.Udp), settings, workThrottle)
        {
            if (requestHandler == null)
            {
                throw new ArgumentNullException();
            }
            
            m_requestHandler = requestHandler;
            m_receiveCompleteHandler = new EventHandler<SocketAsyncEventArgs>(this.ReceiveCompleted);
            m_fromAny = new IPEndPoint(IPAddress.Any, 0);
            m_contextPool = new SynchronizedObjectPool<DnsUdpContext>(this.Settings.MaxActiveRequests);
        }

        public SynchronizedObjectPool<DnsUdpContext> ContextPool
        {
            get
            {
                return m_contextPool;
            }
        }

        protected override void StartAccept()
        {
            SocketAsyncEventArgs args = base.CreateAsyncArgs(this.AllocNewAsyncArgs);
            args.RemoteEndPoint = m_fromAny;
            
            DnsUdpContext context = this.CreateContext();
            context.Socket = this.Socket;
                  
            args.SetBuffer(context.DnsBuffer.Buffer, 0, context.DnsBuffer.Capacity);
            args.UserToken = context;
            
            if (!this.Socket.ReceiveFromAsync(args))
            {   
                //
                // Call completed synchronously. Force async 
                //
                base.ForceAsyncAccept(args);
                base.AcceptCompleted();
            }
        }

        SocketAsyncEventArgs AllocNewAsyncArgs()
        {
            SocketAsyncEventArgs args = new SocketAsyncEventArgs();
            args.Completed += m_receiveCompleteHandler;
            
            return args;
        }                
        
        void ReceiveCompleted(object sender, SocketAsyncEventArgs args)
        {
            bool synchronousCompletion = true;
            DnsUdpContext context = null;
            
            try
            {
                int countRead = args.BytesTransferred;
                SocketError socketError = args.SocketError;
                IPEndPoint remoteEndpoint = (IPEndPoint)args.RemoteEndPoint;
                context = (DnsUdpContext)args.UserToken;
                //
                // Release the accept throttle so the listener thread can resume accepting connections
                //
                if (sender != null)
                {
                    base.AcceptCompleted(args); //async completion. Free the args..
                }
                else
                {
                    this.ReleaseAsyncArgs(args);
                }

                if (socketError == SocketError.Success && countRead > 0)
                {
                    context.BytesTransfered = countRead;
                    context.RemoteEndPoint = remoteEndpoint;
                    synchronousCompletion = m_requestHandler.Process(context);
                    //
                    // If completion is async, handler will call ProcessingComplete
                    //
                }
            }
            catch (SocketException)
            {
                //
                // Eat socket exceptions silently, as they happen all the time - often when the other
                // party does something abrupt
                //
            }
            catch(Exception ex)
            {
                base.NotifyError(ex);
            }
            finally
            {
                if (synchronousCompletion)
                {
                    this.ProcessingComplete(context);
                }
            }
        }

        protected override void ForcedAsyncComplete(object state)
        {
            this.ReceiveCompleted(null, (SocketAsyncEventArgs) state);
        }

        protected DnsUdpContext CreateContext()
        {
            DnsUdpContext context = m_contextPool.Get();
            if (context == null)
            {
                context = new DnsUdpContext();
            }
            context.Init();
            
            return context;
        }
        
        public void ProcessingComplete(DnsUdpContext context)
        {
            try
            {
                if (context != null)
                {
                    this.ReleaseContext(context);
                }
            }
            catch(Exception ex)
            {
                this.NotifyError(ex);
            }
            finally
            {
                base.ProcessingComplete();
            }
        }
        
        void ReleaseContext(DnsUdpContext context)
        {
            context.Reset();
            m_contextPool.Put(context);
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