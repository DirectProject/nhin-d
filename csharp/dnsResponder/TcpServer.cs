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
    /// <summary>
    /// A *SIMPLE* (emphasis) socket server that lets you build a perfectly adequate MULTI-THREADED Request/Response server 
    /// that is easy to debug and program.
    /// 
    /// The job of this class is reliably, scalably and asynchronously listen for socket connections
    /// It also supports request throttling, socket management and cleanup.
    /// 
    /// It hands off sockets to you, at which point you can do as you please. 
    /// </summary>        
    public class TcpServer<TContext>
        where TContext : TcpContext, new()
    {
        SocketServerSettings m_settings;
        IServerApplication<TContext> m_application;

        Socket m_listenerSocket;
        SocketTable m_activeSockets;
        ObjectAllocator<TContext> m_contextPool;
        SynchronizedObjectPool<SocketAsyncEventArgs> m_asyncArgsPool;
        
        Thread m_listenerThread;
        WorkThrottle m_acceptThrottle;
        IWorkLoadThrottle m_connectionThrottle;
        WaitCallback m_forcedAsyncCallback;
                
        bool m_running = false;

        public TcpServer(IPEndPoint endpoint, SocketServerSettings settings, IServerApplication<TContext> application)
            : this(endpoint, settings, application, settings.CreateRequestThrottle())
        {
        }
        
        public TcpServer(IPEndPoint endpoint, SocketServerSettings settings, IServerApplication<TContext> application, IWorkLoadThrottle connectionThrottle)
        {
            if (endpoint == null || application == null || settings == null || connectionThrottle == null)
            {
                throw new ArgumentNullException();
            }
            
            settings.Validate();            
            m_settings = settings;

            m_activeSockets = new SocketTable();
            m_forcedAsyncCallback = new WaitCallback(this.ForcedAsyncComplete);
    
            m_listenerSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            m_listenerSocket.Bind(endpoint);
            
            m_acceptThrottle = new WorkThrottle(m_settings.MaxOutstandingAccepts);
            m_contextPool = new ObjectAllocator<TContext>(m_settings.MaxActiveRequests);
            m_asyncArgsPool = new SynchronizedObjectPool<SocketAsyncEventArgs>(m_settings.MaxActiveRequests);
            m_connectionThrottle = connectionThrottle;
            m_application = application;
            
            m_listenerThread = new Thread(this.Run);
        }
        
        public SocketServerSettings Settings
        {
            get
            {
                return m_settings;
            }
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
        public event Action<Exception> Error;
        
        /// <summary>
        /// Start the server
        /// </summary>
        public void Start()
        {
            m_running = true;
            m_listenerSocket.Listen(m_settings.MaxConnectionBacklog);

            m_listenerThread.Start();
        }
        
        /// <summary>
        /// Stop the server. Stops all open connections
        /// </summary>
        public void Stop()
        {
            this.Stop(Timeout.Infinite);
        }
        
        /// <summary>
        /// Stop the server. Stops all open connections, and waits for a clean shutdown
        /// </summary>
        public bool Stop(int timeout)
        {
            m_running = false;            
            this.Close();
            return m_listenerThread.Join(timeout);            
        }
        
        public void Close()
        {
            this.ShutdownListener();
            m_activeSockets.Shutdown();
        }
        
        void ShutdownListener()
        {
            m_listenerSocket.SafeClose();
        }
        
        /// <summary>
        /// Thread for accepting Socket connections
        /// </summary>                                
        void Run()
        {
            while (m_running)
            {
                try
                {
                    m_connectionThrottle.Wait();
                    if (m_running)
                    {
                        m_acceptThrottle.Wait();
                        if (m_running)
                        {
                            this.StartAccept();
                        }
                    }
                }
                catch(ThreadInterruptedException)
                {
                    m_running = false;
                }
                catch(ThreadAbortException)
                {
                    m_running = false;
                }
                catch (Exception ex)
                {
                    this.NotifyError(ex);
                }
            }    
        }
        
        void StartAccept()
        {
            SocketAsyncEventArgs args = this.CreateAsyncArgs();
            if (!m_listenerSocket.AcceptAsync(args))
            {
                //
                // Call completed synchronously. Force async 
                //
                this.ForceAsyncAccept(args);
                                
                m_acceptThrottle.Completed();
            }
        }

        void AcceptConnection(object sender, SocketAsyncEventArgs args)
        {
            Socket socket = args.AcceptSocket;
            SocketError socketError = args.SocketError;
            
            this.ReleaseAsyncArgs(args);
            
            if (sender != null)
            {
                //
                // sender != null if call completed asynchronously. 
                // Release the accept throttle so the listener thread can resume accepting connections
                //
                m_acceptThrottle.Completed();
            }
            //
            // If there was an error...
            //
            if (socket == null || socketError != SocketError.Success)
            {
                this.ReleaseConnectionThrottle();
                return;
            }
    
            this.AcceptConnection(socket);
        }

        void AcceptConnection(Socket socket)
        {
            try
            {
                //
                // Fire the connection accepted event
                //
                this.ConnectionAccepted.SafeInvoke(socket);
                //
                // Configure the socket for timeouts before dispatch
                // 
                m_settings.ConfigureSocket(socket); 
                //
                // Ok, we can do some work now
                //
                this.Dispatch(socket);

                socket = null;
            }
            finally
            {
                if (socket != null)  // This is non-null if there was an exception and we could not dispatch the socket cleanly
                {
                    socket.SafeClose();
                    this.ConnectionClosed.SafeInvoke(socket);
                }
            }
        }

        void ForceAsyncAccept(SocketAsyncEventArgs args)
        {
            try
            {
                ThreadPool.QueueUserWorkItem(m_forcedAsyncCallback, args);
            }
            catch(Exception ex)
            {
                this.NotifyError(ex);
                this.ReleaseAsyncArgs(args);  
                this.ReleaseConnectionThrottle(); 
            }
        }
                        
        void ForcedAsyncComplete(object state)
        {
            this.AcceptConnection(null, (SocketAsyncEventArgs) state);
        }
                        
        /// <summary>
        /// Dispatch the new socket for synchronous request handling
        /// </summary>
        /// <param name="socket"></param>
        void Dispatch(Socket socket)
        {
            TContext context = null;
            long socketID = 0;

            socketID = m_activeSockets.Add(socket);                
            try
            {
                context = this.CreateContext(socket, socketID);                    
                socketID = 0;                
                
                bool completed = m_application.Process(context);
                if (!completed)
                {  
                    // Processing will be completed asynchronously. Application will call ProcessingComplete
                    context = null;
                }
            }
            finally
            {
                //
                // The socket table is safe - it will not throw if we try to shut down an already shutdown socket. 
                // In the very very rare case if that happens
                //
                if (context != null)
                {
                    this.ProcessingComplete(context);
                }
                if (socketID > 0)
                {
                    m_activeSockets.Shutdown(socketID);
                }
            }
        }
        
        public void ProcessingComplete(TContext context)
        {
            if (context == null)
            {
                throw new ArgumentNullException();
            }
            
            this.ReleaseContext(context);
        }
                
        TContext CreateContext(Socket socket, long socketID)
        {
            TContext context = m_contextPool.Get();
            context.Init(socket, socketID);            
            return context;
        }
        
        void ReleaseContext(TContext context)
        {            
            try
            {
                if (context.HasValidSocket)
                {
                    m_activeSockets.Shutdown(context.SocketID, m_settings.SocketCloseTimeout);
                    this.ConnectionClosed.SafeInvoke(context.Socket);
                }
                context.Clear();
                m_contextPool.Put(context);
            }
            catch (Exception ex)
            {
                this.NotifyError(ex);
            }
            finally
            {
                this.ReleaseConnectionThrottle();
            }
        }
        
        void ReleaseConnectionThrottle()
        {
            try
            {
                m_connectionThrottle.Completed();
            }
            catch(Exception ex)
            {
                this.NotifyError(ex);
            }
        }
                        
        SocketAsyncEventArgs CreateAsyncArgs()
        {
            SocketAsyncEventArgs args = m_asyncArgsPool.Get();
            if (args == null)
            {
                args = new SocketAsyncEventArgs();
                args.Completed += this.AcceptConnection;        
            }
            else
            {
                args.AcceptSocket = null;
            }
            
            return args;
        }
        
        void ReleaseAsyncArgs(SocketAsyncEventArgs args)
        {
            try
            {
                args.AcceptSocket = null;
                m_asyncArgsPool.Put(args);
            }
            catch(Exception ex)
            {
                this.NotifyError(ex);
            }
        }
                                
        void NotifyError(Exception ex)
        {
            this.Error.SafeInvoke(ex);
        }
    }
}
