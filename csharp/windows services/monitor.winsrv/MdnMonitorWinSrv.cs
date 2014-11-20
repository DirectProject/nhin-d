using System;
using System.ServiceProcess;
using System.Threading;
using Quartz;
using Quartz.Impl;

namespace Health.Direct.Monitor.WinSrv
{
    public partial class MdnMonitorWinSrv : ServiceBase
    {

        Diagnostics m_diagnostics;
        private ISchedulerFactory m_schedulerfactory;
        private IScheduler m_scheduler;

        public MdnMonitorWinSrv()
        {
            InitializeComponent();

            try
            {
                m_diagnostics = new Diagnostics(this);
            }
            catch (Exception ex)
            {
                Diagnostics.WriteEventLog(ex);
                throw;
            }
        }
               
        

        /// <summary>
        /// method to initialize fields utilized by the service
        /// </summary>
        private void InitializeService()
        {
            m_diagnostics.ServiceInitializing();
           
            m_schedulerfactory = new StdSchedulerFactory();
            m_scheduler = m_schedulerfactory.GetScheduler();
            
            m_diagnostics.ServiceInitializingComplete();
        }


        public void StartService(string[] args)
        {
            try
            {
                InitializeService();

                m_diagnostics.ServerStarting();

                m_scheduler.Start();
                try
                {
                    Thread.Sleep(3000);
                }
                catch (ThreadInterruptedException)
                {
                }

                m_diagnostics.ServerStarted();
            }
            catch (Exception ex)
            {
                Diagnostics.WriteEventLog(ex);
                throw;
            }
        }
        public void StopService()
        {
            try
            {
                m_diagnostics.ServerStopping();

                m_scheduler.Shutdown(true);

                m_diagnostics.ServerStopped();
            }
            catch (Exception ex)
            {
                Diagnostics.WriteEventLog(ex);
                throw;
            }
        }

        protected override void OnStart(string[] args)
        {
            StartService(args);
        }

        protected override void OnStop()
        {
            StopService();
        }
    }
}
