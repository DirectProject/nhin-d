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
    /// This - and all its accompanying generic socket code - should get yanked into a separate Dll
    /// </summary>
    public abstract class SocketServer<T>
        where T : ServerProcessingContext, new()
    {
        IPEndPoint m_endpoint;
        Socket m_socket;
        SocketServerSettings m_settings;
        SynchronizedObjectPool<SocketAsyncEventArgs> m_asyncArgsPool;
        SynchronizedObjectPool<T> m_contextPool;
        IWorkLoadThrottle m_workThrottle;
        IWorkLoadThrottle m_outstandingAcceptThrottle;
        
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
            m_contextPool = new SynchronizedObjectPool<T>(m_settings.MaxActiveRequests); 
            
            m_listenerThread = new Thread(this.Run);
        }
        
        public event Action Starting;
        public event Action Started;
        public event Action Stopping;
        public event Action Stopped;
        
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
                
        public event Action<Exception> Error;
        
        public void Start()
        {
            this.Starting.SafeInvoke();
            
            this.OnStart();
            
            m_running = true;
            m_listenerThread.Start();
            m_socket.Bind(m_endpoint);
                        
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
            
            m_socket.Close();
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
                            this.StartAccept(this.CreateAsyncArgs());
                        }
                    }
                }
                catch (ThreadInterruptedException)
                {
                    m_running = false;
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
        
        void WaitForCapacity()
        {
            m_workThrottle.Wait();
        }        
        void WorkCompleted()
        {
            m_workThrottle.Completed();
        }
        
        protected T CreateContext()
        {
            T context = m_contextPool.Get();
            if (context == null)
            {
                context = new T();
                context.Init();
            }
            
            return context;
        }
        /// <summary>
        /// Call this if you process buffers asynchronously
        /// </summary>
        /// <param name="buffer"></param>
        public void ProcessingComplete(T context)
        {
            if (context == null)
            {
                throw new ArgumentNullException();
            }
            
            try
            {
                context.Clear();
                m_contextPool.Put(context);
            }
            catch (Exception ex)
            {
                this.NotifyError(ex);
            }
            this.WorkCompleted();
        }
        
        /// <summary>
        /// Some socket operations will complete synchrnously. To keep the model consistent, we may want to force these sync
        /// operations to be handled asynchronously.
        /// </summary>
        /// <param name="args"></param>
        /// <param name="asyncCallback"></param>
        protected void ForceAsyncComplete(SocketAsyncEventArgs args, WaitCallback asyncCallback)
        {
            ThreadPool.QueueUserWorkItem(asyncCallback, args);
        }

        protected SocketAsyncEventArgs CreateAsyncArgs()
        {
            SocketAsyncEventArgs args = m_asyncArgsPool.Get();
            if (args == null)
            {
                args = new SocketAsyncEventArgs();
                this.InitAcceptArgs(args);
            }
            else
            {
                args.AcceptSocket = null;
            }

            return args;
        }
        protected abstract void InitAcceptArgs(SocketAsyncEventArgs args);
        protected void AcceptCompleted(SocketAsyncEventArgs args)
        {
            args.AcceptSocket = null;
            m_asyncArgsPool.Put(args);
            m_outstandingAcceptThrottle.Completed();
        }
                
        protected void NotifyError(Exception ex)
        {
            if (this.Error != null)
            {
                this.Error.SafeInvoke(ex);
            }
        }
        
        protected abstract void OnStart();
        protected abstract void OnStop();        
        protected abstract void StartAccept(SocketAsyncEventArgs args);
    }
}
