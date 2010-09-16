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
    /// that is easy to debug and program
    /// </summary>        
    public class TcpServer
    {
        SocketServerSettings m_settings;
        IServerApplication m_application;

        Socket m_listenerSocket;
        SocketTable m_activeSockets;

        Thread m_listenerThread;
        IWorkLoadThrottle m_connectionThrottle;
        WaitCallback m_handlerCallback;
                
        bool m_running = false;

        public TcpServer(IPEndPoint endpoint, SocketServerSettings settings, IServerApplication application)
            : this(endpoint, settings, application, settings.CreateThrottle())
        {
        }
        
        public TcpServer(IPEndPoint endpoint, SocketServerSettings settings, IServerApplication application, IWorkLoadThrottle connectionThrottle)
        {
            if (endpoint == null || application == null || settings == null || connectionThrottle == null)
            {
                throw new ArgumentNullException();
            }
            
            settings.Validate();            
            m_settings = settings;

            m_listenerSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            m_listenerSocket.Bind(endpoint);
                        
            m_activeSockets = new SocketTable();
            m_connectionThrottle = connectionThrottle;
            m_handlerCallback = new WaitCallback(this.InvokeProcessConnection);

            m_application = application;
            
            m_listenerThread = new Thread(this.Run);
        }
        
        public event Action<TcpServer, Socket> ConnectionAccepted;
        public event Action<TcpServer, Socket> ConnectionClosed;        
        public event Action<TcpServer, Exception> Error;
        
        /// <summary>
        /// Start the server
        /// </summary>
        public void Start()
        {
            m_running = true;
            m_listenerSocket.Listen(m_settings.MaxPendingConnections);
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
        /// Stop the server. Stops all open connections
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
        /// Main thread.
        /// 1. Accepts Socket connections
        /// 2. Queues them for Asynchronous processing
        /// 
        /// </summary>                                
        void Run()
        {
            while (m_running)
            {
                try
                {
                    m_connectionThrottle.Wait();
                    
                    Socket socket = m_listenerSocket.Accept();
                    this.NotifyAccepted(socket);
                    
                    try
                    {
                        m_settings.ConfigureSocket(socket); // set up timeouts etc
                        
                        this.Dispatch(socket);  
                        
                        socket = null;
                    }
                    finally
                    {
                        if (socket != null)
                        {
                            socket.SafeClose();
                            this.NotifyClosed(socket);
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
        
        /// <summary>
        /// Dispatch the new socket by queueing a request
        /// </summary>
        /// <param name="socket"></param>
        void Dispatch(Socket socket)
        {
            ProcessingContext context = null;
            long socketID = 0;

            try
            {
                context = m_application.CreateContext();                
                
                socketID = m_activeSockets.Add(socket);                
                context.Init(this, socket, socketID);   
                
                ThreadPool.QueueUserWorkItem(m_handlerCallback, context);
                context = null;
            }
            finally
            {
                if (context != null)
                {
                    this.ReleaseContext(context);
                }
            }
        }
        
        /// <summary>
        /// Async socket processing
        /// </summary>
        void InvokeProcessConnection(object state)
        {
            ProcessingContext context = null;
            try
            {
                context = (ProcessingContext) state;
                m_application.Process(context);
            }
            catch(Exception ex)
            {
                this.NotifyError(ex);
            }
            finally
            {
                if (context != null)
                {
                    this.ReleaseContext(context);
                }
            }            
        }

        void ReleaseContext(ProcessingContext context)
        {
            try
            {
                if (context.HasValidSocket)
                {
                    m_activeSockets.Shutdown(context.SocketID, m_settings.SocketCloseTimeout);
                    this.NotifyClosed(context.Socket);
                }
                
                context.Clear();
                m_application.ReleaseContext(context);
            }
            catch (Exception ex)
            {
                this.NotifyError(ex);
            }
            finally
            {
                m_connectionThrottle.Completed();
            }
        }
        
        void NotifyAccepted(Socket socket)
        {
            if (this.ConnectionAccepted != null)
            {
                this.ConnectionAccepted.SafeInvoke(this, socket);
            }
        }
                
        void NotifyClosed(Socket socket)
        {
            if (this.ConnectionClosed != null)
            {
                this.ConnectionClosed.SafeInvoke(this, socket);
            }
        }
        
        void NotifyError(Exception ex)
        {
            if (this.Error != null)
            {
                this.Error.SafeInvoke(this, ex);
            }
        }
    }
}
