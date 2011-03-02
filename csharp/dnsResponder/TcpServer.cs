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
using System.Threading;
using System.Net;
using System.Net.Sockets;

namespace Health.Direct.DnsResponder
{
    /// <summary>
    /// A *SIMPLE* (emphasis) socket server that lets you build a perfectly adequate MULTI-THREADED Request/Response server 
    /// that is easy to debug and program.
    /// 
    /// The job of this class is reliably, scalably and asynchronously listen for socket connections
    /// It also supports request throttling, socket management and cleanup.
    /// 
    /// It hands off sockets to you, at which point you can do as you please. 
    /// </summary>        
    public class TcpServer<TContext> : SocketServer
        where TContext : TcpContext, new()
    {
        IHandler<TContext> m_handler;

        SocketTable m_activeSockets;
        SynchronizedObjectPool<TContext> m_contextPool;        
        EventHandler<SocketAsyncEventArgs> m_completeHandler;
        WaitCallback m_forcedAsyncCallback;

        public TcpServer(IPEndPoint endpoint, SocketServerSettings settings, IHandler<TContext> application)
            : this(endpoint, settings, application, null)
        {
        }
        
        public TcpServer(IPEndPoint endpoint, SocketServerSettings settings, IHandler<TContext> application, IWorkLoadThrottle connectionThrottle)
            : base(endpoint, new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp), settings, connectionThrottle)
        {
            m_activeSockets = new SocketTable();
            m_forcedAsyncCallback = new WaitCallback(this.ForcedAsyncComplete);
            m_contextPool = new SynchronizedObjectPool<TContext>(this.Settings.MaxActiveRequests);
            m_completeHandler = new EventHandler<SocketAsyncEventArgs>(this.AcceptConnection);
            m_handler = application;
        }
        
        public SocketTable ActionConnections
        {
            get
            {
                return m_activeSockets;
            }
        }
        
        public event Action<Socket> ConnectionAccepted;
        public event Action<Socket> ConnectionClosed;

        protected override void OnStart()
        {        
            this.Socket.Listen(this.Settings.MaxConnectionBacklog);
        }
        
        /// <summary>
        /// Stop the server. Stops all open connections, and waits for a clean shutdown
        /// </summary>
        protected override void OnStop()
        {
            m_activeSockets.Shutdown();
        }
                
        protected override void StartAccept()
        {
            SocketAsyncEventArgs args = this.CreateAsyncArgs(this.AllocNewAsyncArgs);
            if (!this.Socket.AcceptAsync(args))
            {
                //
                // Call completed synchronously. Force async 
                //
                base.ForceAsyncAccept(args);
                                
                base.AcceptCompleted();                                
            }
        }

        void AcceptConnection(object sender, SocketAsyncEventArgs args)
        {
            Socket socket = null;
            try
            {
                socket = args.AcceptSocket;
                SocketError socketError = args.SocketError;
                            
                if (sender != null)
                {
                    //
                    // sender != null if call completed asynchronously. 
                    // Release the accept throttle so the listener thread can resume accepting connections
                    //
                    base.AcceptCompleted(args);
                }
                else
                {
                    base.ReleaseAsyncArgs(args);
                }

                //
                // If there was an error...
                //
                if (socket == null || socketError != SocketError.Success)
                {
                    base.ProcessingComplete();
                    return;
                }

                this.ConnectionAccepted.SafeInvoke(socket);

                this.Dispatch(socket);
                
                socket = null;
            }
            catch(Exception ex)
            {
                this.NotifyError(ex);
            }
            finally
            {
                if (socket != null)
                {
                    socket.SafeShutdownAndClose(SocketShutdown.Both, this.Settings.SocketCloseTimeout);
                    this.ConnectionClosed.SafeInvoke(socket);
                }
            }
        }
                        
        protected override void ForcedAsyncComplete(object state)
        {
            this.AcceptConnection(null, (SocketAsyncEventArgs) state);
        }
                        
        /// <summary>
        /// Dispatch the new socket for synchronous request handling
        /// </summary>
        /// <param name="socket"></param>
        void Dispatch(Socket socket)
        {
            bool synchronousCompletion = true;
            TContext context = null;
            long socketID = 0;

            socketID = m_activeSockets.Add(socket);

            try
            {
                this.Settings.ConfigureSocket(socket);
                               
                context = this.CreateContext(socket, socketID);                    
                socketID = 0;                

                synchronousCompletion = m_handler.Process(context);
                //
                // If synchronousCompletion is false, then:
                // Processing will be completed ASYNCHRONOUSLY. 
                // Application will call ProcessingComplete
                //
            }
            catch (SocketException)
            {
                //
                // Eat these socket exceptions silently, as they happen all the time
                // including when the client randomly closes the socket, etc..
                //
            }
            catch(Exception ex)
            {
                this.NotifyError(ex);
            }
            finally
            {
                //
                // The socket table is safe - it will not throw if we try to shut down an already shutdown socket. 
                // In the very very rare case if that happens
                //
                if (synchronousCompletion)
                {
                    this.ProcessingComplete(context);
                }
                if (socketID > 0)
                {
                    m_activeSockets.Shutdown(socketID, this.Settings.SocketCloseTimeout);
                    this.ConnectionClosed.SafeInvoke(socket);
                }
            }
        }
        
        public void ProcessingComplete(TContext context)
        {
            try
            {
                if (context != null)
                {
                    if (context.HasValidSocket)
                    {
                        m_activeSockets.Shutdown(context.SocketID, this.Settings.SocketCloseTimeout);
                        this.ConnectionClosed.SafeInvoke(context.Socket);
                    }
                    this.ReleaseContext(context);
                }
            }
            catch (Exception ex)
            {
                this.NotifyError(ex);
            }
            finally
            {
                base.ProcessingComplete();
            }            
        }
                
        TContext CreateContext(Socket socket, long socketID)
        {
            TContext context = m_contextPool.Get();
            if (context == null)
            {
                context = new TContext();
            }
            context.Init(socket, socketID);            
            return context;
        }
        
        void ReleaseContext(TContext context)
        {    
            try
            {        
                context.Clear();
                m_contextPool.Put(context);
            }
            catch
            {
            }
        }

        SocketAsyncEventArgs AllocNewAsyncArgs()
        {
            SocketAsyncEventArgs args = new SocketAsyncEventArgs();
            args.Completed += m_completeHandler;

            return args;
        }                
    }
}