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
    public interface IHandler<TContext>
    {
        /// <summary>
        /// Return true if synchronously processed and completed. 
        /// If so, resources such as any allocated sockets will be shutdown
        /// To process asynchronously, return false and then call SocketServer.ProcessingComplete(context)
        /// </summary>
        /// <param name="context"></param>
        /// <returns></returns>
        bool Process(TContext context);
    }

    /// <summary>
    /// This - and all its accompanying generic socket code - should get yanked into a separate Dll
    /// </summary>
    public abstract class SocketServer
    {
        IPEndPoint m_endpoint;
        Socket m_socket;
        SocketServerSettings m_settings;
        SynchronizedObjectPool<SocketAsyncEventArgs> m_asyncArgsPool;
        IWorkLoadThrottle m_workThrottle;
        IWorkLoadThrottle m_outstandingAcceptThrottle;
        WaitCallback m_forcedAsyncCallback;
        
        Thread m_listenerThread;
        bool m_running = false;
        
        public SocketServer(IPEndPoint endpoint, Socket socket, SocketServerSettings settings)
            : this(endpoint, socket, settings, null)
        {
        }

        public SocketServer(IPEndPoint endpoint, Socket listenSocket, SocketServerSettings settings, IWorkLoadThrottle workThrottle)
        {
            if (endpoint == null || listenSocket == null || settings == null)
            {
                throw new ArgumentNullException();
            }
            
            settings.Validate();
            m_settings = settings;

            m_endpoint = endpoint;
            m_socket = listenSocket;
            m_workThrottle = workThrottle ?? settings.CreateRequestThrottle();
            m_outstandingAcceptThrottle = settings.CreateAcceptThrottle();
            m_asyncArgsPool = new SynchronizedObjectPool<SocketAsyncEventArgs>(m_settings.MaxOutstandingAccepts);
            m_forcedAsyncCallback = new WaitCallback(this.ForcedAsyncComplete);
                
            m_listenerThread = new Thread(this.Run);
        }
        
        /// <summary>
        /// Service status events
        /// </summary>
        public event Action Starting;
        public event Action Started;
        public event Action Stopping;
        public event Action Stopped;
        public event Action<Exception> Error;

        public IPEndPoint Endpoint
        {
            get
            {
                return m_endpoint;
            }
        }
        
        public SocketServerSettings Settings
        {
            get
            {
                return m_settings;
            }
        }
        
        public Socket Socket
        {
            get
            {
                return m_socket;
            }
        }
        
        public SynchronizedObjectPool<SocketAsyncEventArgs> AsyncArgsPool
        {
            get
            {
                return m_asyncArgsPool;
            }
        }
        
        public int OutstandingAcceptCount
        {
            get
            {
                return m_outstandingAcceptThrottle.WaitCount;
            }
        }
        
        public void Start()
        {
            this.Starting.SafeInvoke();

            m_socket.Bind(m_endpoint);
            
            this.OnStart();
            
            m_running = true;
            m_listenerThread.Start();
                        
            this.Started.SafeInvoke();
        }
        
        public bool Stop()
        {
            return this.Stop(Timeout.Infinite);
        }
        
        public bool Stop(int timeout)
        {
            this.Stopping.SafeInvoke();
            
            m_running = false;
            
            this.Socket.SafeClose();
            this.OnStop();

            if (m_listenerThread.Join(timeout))
            {            
                this.Stopped.SafeInvoke();
                return true;
            }
            
            return false;
        }

        void Run()
        {
            while (m_running)
            {
                try
                {
                    this.WaitForCapacity();
                    if (m_running)
                    {
                        m_outstandingAcceptThrottle.Wait();
                        if (m_running)
                        {
                            this.Accept();
                        }
                    }
                }
                catch (ThreadInterruptedException)
                {
                }
                catch (ThreadAbortException)
                {
                    m_running = false;
                }
                catch (Exception ex)
                {
                    this.NotifyError(ex);
                }
            }
        }
        
        void Accept()
        {
            try
            {
                this.StartAccept();
            }
            catch (Exception ex)
            {
                this.NotifyError(ex);
                this.AcceptCompleted();
            }
        }
        
        public void ProcessingComplete()
        {
            this.WorkCompleted();
        }

        protected bool ForceAsyncAccept(SocketAsyncEventArgs args)
        {
            try
            {
                ThreadPool.QueueUserWorkItem(m_forcedAsyncCallback, args);
                return true;
            }
            catch (Exception ex)
            {
                this.NotifyError(ex);
                this.ReleaseAsyncArgs(args);
                this.ProcessingComplete();
            }
            
            return false;
        }
        
        protected virtual void ForcedAsyncComplete(object state)
        {
        }
        
        void WaitForCapacity()
        {
            m_workThrottle.Wait();
        }        
        void WorkCompleted()
        {
            m_workThrottle.Completed();
        }
        
        /// <summary>
        /// Checks the async args pool first, else calls the allocator
        /// </summary>
        /// <param name="allocator"></param>
        /// <returns></returns>
        public SocketAsyncEventArgs CreateAsyncArgs(Func<SocketAsyncEventArgs> allocator)
        {
            SocketAsyncEventArgs args = m_asyncArgsPool.Get();
            if (args == null)
            {
                args = allocator();
            }
            else
            {
                args.AcceptSocket = null;
            }

            return args;
        }
        
        public void AcceptCompleted()
        {
            this.AcceptCompleted(null);
        }        
        
        protected void AcceptCompleted(SocketAsyncEventArgs args)
        {
            if (args != null)
            {
                this.ReleaseAsyncArgs(args);
            }
            m_outstandingAcceptThrottle.Completed();
        }
        
        protected void ReleaseAsyncArgs(SocketAsyncEventArgs args)
        {
            try
            {
                args.AcceptSocket = null;
                args.ClearBuffer();
                args.RemoteEndPoint = null;
                m_asyncArgsPool.Put(args);
            }
            catch
            {
            }
        }
            
        public void NotifyError(Exception ex)
        {
            if (this.Error != null)
            {
                this.Error.SafeInvoke(ex);
            }
        }
        
        protected abstract void OnStart();
        protected abstract void OnStop();        
        protected abstract void StartAccept();
    }
}