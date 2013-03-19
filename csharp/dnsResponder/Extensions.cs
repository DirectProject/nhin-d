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
using System.Net;
using System.Net.Sockets;

namespace Health.Direct.DnsResponder
{
    public static class Extensions
    {
        public static void SafeClose(this Socket socket)
        {
            try
            {
                if (socket != null)
                {
                    socket.Close();
                }
            }
            catch
            {
            }
        }

        public static void SafeClose(this Socket socket, int timeout)
        {
            try
            {
                if (socket != null)
                {
                    if (timeout > 0)
                    {
                        socket.Close(timeout);
                    }
                    else
                    {
                        socket.Close();
                    }
                }
            }
            catch
            {
            }
        }

        public static void SafeShutdown(this Socket socket, SocketShutdown type)
        {
            try
            {
                if (socket != null)
                {
                    socket.Shutdown(type);
                }
            }
            catch
            {
            }
        }
        
        public static void SafeShutdownAndClose(this Socket socket, SocketShutdown shutdown, int timeout)
        {
            socket.SafeShutdown(shutdown);
            socket.SafeClose(timeout);
        }
        
        public static IPAddress GetIPAddress(this Socket socket)
        {
            return ((IPEndPoint) socket.RemoteEndPoint).Address;            
        }
        
        public static int GetReceiveTimeout(this Socket socket)
        {
            return (int) socket.GetSocketOption(SocketOptionLevel.Socket, SocketOptionName.ReceiveTimeout);
        }

        public static void SetReceiveTimeout(this Socket socket, int timeout)
        {
            socket.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.ReceiveTimeout, timeout);
        }

        public static int GetSendTimeout(this Socket socket)
        {
            return (int) socket.GetSocketOption(SocketOptionLevel.Socket, SocketOptionName.SendTimeout);
        }

        public static void SetSendTimeout(this Socket socket, int timeout)
        {
            socket.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.SendTimeout, timeout);
        }
        
        public static void ClearBuffer(this SocketAsyncEventArgs args)
        {
            args.SetBuffer(null, 0, 0);
        }
        
        public static void SafeInvoke(this Action action)
        {
            if (action != null)
            {
                try
                {
                    action();
                }
                catch
                {
                }
            }
        }

        public static void SafeInvoke<T1>(this Action<T1> action, T1 arg1)
        {
            if (action != null)
            {
                try
                {
                    action(arg1);
                }
                catch
                {
                }
            }
        }
        
        public static void SafeInvoke<T1, T2>(this Action<T1, T2> action, T1 arg1, T2 arg2)
        {
            if (action != null)
            {
                try
                {
                    action(arg1, arg2);
                }
                catch
                {
                }
            }
        }

        public static void SafeInvoke<T1, T2, T3>(this Action<T1, T2, T3> action, T1 arg1, T2 arg2, T3 arg3)
        {
            if (action != null)
            {
                try
                {
                    action(arg1, arg2, arg3);
                }
                catch
                {
                }
            }
        }
    }
}