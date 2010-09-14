using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Security.Cryptography.X509Certificates;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using NHINDirect.Certificates;
using DnsResolver;

namespace DnsResponder
{
    class DnsTest
    {
        static void Main(string[] args)
        {
            ServerTest test = new ServerTest();
            test.Run(true);
            
            Console.WriteLine("Shutdown complete. Hit return to exit");            
            Console.ReadLine();
        }
    }    
}
