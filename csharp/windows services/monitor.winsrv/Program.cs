using System;
using System.ServiceProcess;

namespace Health.Direct.Monitor.WinSrv
{
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        static void Main(string[] args)
        {
            var srv = new MdnMonitorWinSrv();
            var servicesToRun = new ServiceBase[] { srv };
            if(Environment.UserInteractive)
            {
                srv.StartService(args);
                Console.WriteLine("MdnMonitorWinSrv running, press Enter key to stop...");
                Console.Read();
                srv.Stop();
            }
            else
            {
                ServiceBase.Run(servicesToRun);
            }
        }
    }
}
