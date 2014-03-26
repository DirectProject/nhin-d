/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook     
   
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Diagnostics;
using System.Linq;
using System.Net;
using System.Net.NetworkInformation;
using System.Net.Sockets;
using System.Runtime.InteropServices;
using System.Text;


namespace Health.Direct.Install.Tools
{

    [ComVisible(true), GuidAttribute("C3156D52-41A1-47be-9E15-C22E0B402C1F")]
    [InterfaceType(ComInterfaceType.InterfaceIsDual)]
    public interface ISmtp
    {
        bool TestConnection(string host, int port);
    }

    [ComVisible(true), GuidAttribute("F15B7252-46B6-4cb1-95CA-31DEBB7E6D1C")]
    [ProgId("Direct.Installer.SmtpTools")]
    [ClassInterface(ClassInterfaceType.None)]
    public class Smtp : ISmtp
    {
        
        public bool TestConnection(string host, int port)
        {
            if (TestNicInterfaceConnection(host, port))
            {
                return true;
            }
                      
            IPHostEntry ipHostEntry = Dns.GetHostEntry(Dns.GetHostName());
            return ipHostEntry.AddressList.Any(ip => TestNicInterfaceConnection(ip.ToString(), port));
        }

        private static bool TestNicInterfaceConnection(string host, int port)
        {
            IPHostEntry hostEntry = Dns.GetHostEntry(host);
            IPEndPoint smtpServer = new IPEndPoint(hostEntry.AddressList.IpV4(0), port);
            using (Socket tcpSocket = new Socket(smtpServer.AddressFamily, SocketType.Stream, ProtocolType.Tcp))
            {
                tcpSocket.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.SendTimeout, 5000);
                tcpSocket.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.ReceiveTimeout, 5000);

                try
                {
                    tcpSocket.Connect(smtpServer);
                    if (!CheckResponse(tcpSocket, 220)) //220 response is success
                    {
                        return false;
                    }

                    // send HELO and test the response for code 250 = proper response
                    SendData(tcpSocket, string.Format("HELO {0}\r\n", Dns.GetHostName()));
                    if (!CheckResponse(tcpSocket, 250))
                    {
                        return false;
                    }

                    return true;
                }
                    catch
                    {
                    }
                finally
                {
                    //Need a logger injected.
                }
                return false;
            }
        }

        private static void SendData(Socket socket, string data)
        {
            byte[] dataArray = Encoding.ASCII.GetBytes(data);
            socket.Send(dataArray, 0, dataArray.Length, SocketFlags.None);
        }

        private static bool CheckResponse(Socket socket, int expectedCode)
        {
            var sw = new Stopwatch();
            sw.Start();
            while (socket.Available == 0)
            {
                if(sw.ElapsedMilliseconds > 1000)
                {
                    return false;
                }
                System.Threading.Thread.Sleep(100);
            }
            byte[] responseArray = new byte[1024];
            socket.Receive(responseArray, 0, socket.Available, SocketFlags.None);
            string responseData = Encoding.ASCII.GetString(responseArray);
            int responseCode = Convert.ToInt32(responseData.Substring(0, 3));
            if (responseCode == expectedCode)
            {
                return true;
            }
            return false;
        }

    }

}