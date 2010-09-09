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
            TcpTest test = new TcpTest();
            test.Run();
            
            Console.ReadLine();
        }
    }    
    
}
