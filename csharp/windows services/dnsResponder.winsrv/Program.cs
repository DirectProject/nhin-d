using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceProcess;
using System.Text;


namespace Health.Direct.DnsResponder.WinSrv
{
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        static void Main(string[] args)
        {
            ServiceBase[] ServicesToRun;
            DnsResponderWinSrv srv = new DnsResponderWinSrv();
            ServicesToRun = new ServiceBase[] { srv };
            if (Environment.UserInteractive)
            {
                srv.StartService(args);
                Console.WriteLine("DnsResponderWinSrv running, press any key to stop...");
                Console.Read();
                srv.Stop();
            }
            else
            {
                ServiceBase.Run(ServicesToRun);
            }


        }
    }
}
