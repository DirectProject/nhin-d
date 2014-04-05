/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Ali Emami       aliemami@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Net.Sockets;
using Health.Direct.Common.Diagnostics;
using Health.Direct.Common.DnsResolver;

namespace Health.Direct.DnsResponder.WinSrv
{
    internal class Diagnostics
    {
        public const string EventLogSourceName = "Health.Direct.DnsResponder.WinSrv";

        readonly ILogger m_logger;
        
        internal Diagnostics(DnsResponderWinSrv service)
        {
            m_logger = Log.For(service);
        }
        
        internal ILogger Logger
        {
            get
            {
                return m_logger;
            }
        }
        
        internal void ServiceInitializing()
        {
            this.WriteEvent("Service initializing");
        }

        internal void ServiceInitializingComplete(DnsServerSettings settings)
        {
            this.WriteEvent(string.Format(
                "Service initialized successfully. ResolutionMode={0}", 
                settings.ResolutionMode));
        }
        
        internal void ServerStarting()
        {
            this.WriteEvent("Server starting");
        }
        internal void ServerStarted()
        {
            this.WriteEvent("Server started successfully");
        }
        internal void ServerStopping()
        {
            this.WriteEvent("Server stopping");
        }
        internal void ServerStopped()
        {
            this.WriteEvent("Server stopped");
        }
        
        internal void HookEvents(DnsServer server)
        {
            server.Error += this.OnDnsError;
            if (server.UDPResponder != null)
            {
                server.UDPResponder.Received += this.OnUdpRequest;
                server.UDPResponder.Responding += this.OnUdpResponse;
            }
            if (server.TCPResponder != null)
            {
                server.TCPResponder.Received += this.OnTcpRequest;
                server.TCPResponder.Responding += this.OnTcpResponse;
            }
        }

        internal void OnDnsError(Exception ex)
        {
            //
            // Socket exceptions can get thrown due to clients getting closed, sockets being dropped, all kinds of issues
            // So during runtime, almost entirely noise
            //
            SocketException se = ex as SocketException;
            if (se != null)
            {
                if (m_logger.IsTraceEnabled)
                {
                    m_logger.Error("Socket Error {0}", se.ToString());
                }
                
                return;
            }
                        
            m_logger.Error("Dns Server {0}", ex.ToString());
        }
        
        void OnUdpRequest(DnsRequest request)
        {
            if (m_logger.IsTraceEnabled)
            {
                m_logger.Debug(this.Summarize("UDP", request));
            }
            else if (m_logger.IsDebugEnabled)
            {
                m_logger.Info("UDP Request {0} {1}", request.Question.Domain, request.Question.Type);
            }
        }
        
        void OnUdpResponse(DnsResponse response)
        {
            if (m_logger.IsTraceEnabled)
            {
                m_logger.Debug(this.Summarize("UDP", response));
            }
            else if (m_logger.IsDebugEnabled)
            {
                m_logger.Info("UDP Response {0} {1}", response.Question.Domain, response.Header.ResponseCode);
            }
        }

        void OnTcpRequest(DnsRequest request)
        {
            if (m_logger.IsTraceEnabled)
            {
                m_logger.Debug(this.Summarize("TCP", request));
            }
            else if (m_logger.IsDebugEnabled)
            {
                m_logger.Info("TCP Request {0} {1}", request.Question.Domain, request.Question.Type);
            }
        }

        void OnTcpResponse(DnsResponse response)
        {
            if (m_logger.IsTraceEnabled)
            {
                m_logger.Debug(this.Summarize("TCP", response));
            }
            else if (m_logger.IsDebugEnabled)
            {
                m_logger.Info("TCP Response {0} {1}", response.Question.Domain, response.Header.ResponseCode);
            }
        }
                
        string Summarize(string type, DnsRequest request)
        {
            using (StringWriter writer = new StringWriter())
            {
                writer.WriteLine();
                writer.Write(type);
                writer.WriteLine(" Request");
                DnsRecordPrinter printer = new DnsRecordPrinter(writer);
                printer.Print(request);
                return writer.ToString();
            }
        }

        string Summarize(string type, DnsResponse response)
        {
            using(StringWriter writer = new StringWriter())
            {
                writer.WriteLine();
                writer.Write(type);
                writer.WriteLine(" Response");
                DnsRecordPrinter printer = new DnsRecordPrinter(writer);
                printer.Print(response);
                return writer.ToString();
            }
        }
        
        internal void WriteEvent(string message)
        {
            m_logger.Info(message);
            WriteEventLog(message);
        }
        
        internal void WriteEvent(Exception ex)
        {
            m_logger.Error(ex);
            WriteEventLog(ex);
        }
        
        internal static void WriteEventLog(string message)
        {
            EventLogHelper.WriteInformation(EventLogSourceName, message);
        }

        internal static void WriteEventLog(Exception ex)
        {
            EventLogHelper.WriteError(EventLogSourceName, ex.ToString());
        }
    }
}
