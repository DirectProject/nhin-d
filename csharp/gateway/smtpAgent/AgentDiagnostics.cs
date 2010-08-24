/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using NHINDirect.Agent;
using NHINDirect.Certificates;
using NHINDirect.Diagnostics;

namespace NHINDirect.SmtpAgent
{
    internal class AgentDiagnostics
    {
        const string EventLogName = "nhinMessageSink";
        
        LogFile m_log;
        bool m_logVerbose; 
        
        internal AgentDiagnostics(LogFile log, bool logVerbose)
        {
            m_log = log;
            m_logVerbose = logVerbose;
        }
        
        internal LogFile Log
        {
            get
            {
                return m_log;
            }
        }

        internal void LogStatus(string message)
        {
            if (m_logVerbose)
            {
                m_log.WriteLine(message);
            }
        }
        
        internal void LogError(Exception ex)
        {
            m_log.WriteError(ex);
        }
        
        internal static void WriteEventLog(string message)
        {
            EventLog.WriteEntry(EventLogName, message);
        }

        internal static void WriteEventLog(Exception ex)
        {
            EventLog.WriteEntry(EventLogName, ex.ToString(), EventLogEntryType.Error);
        }
        
        internal void OnOutgoingError(OutgoingMessage message, Exception error)
        {
            if (m_logVerbose)
            {
                m_log.WriteError(this.BuildVerboseErrorMessage("OUTGOING", message, error));
            }
            else
            {
                m_log.WriteError(error);
            }
        }

        internal void OnIncomingError(IncomingMessage message, Exception error)
        {
            if (m_logVerbose)
            {
                m_log.WriteError(this.BuildVerboseErrorMessage("INCOMING", message, error));
            }
            else
            {
                m_log.WriteError(error);
            }
        }

        internal void OnDnsError(DnsCertResolver service, Exception error)
        {
            m_log.WriteError(error);
        }

        internal string BuildVerboseErrorMessage(string message, MessageEnvelope envelope, Exception ex)
        {
            StringBuilder builder = new StringBuilder();
            builder.AppendLine(message);
            this.SummarizeHeaders(builder, envelope);
            builder.AppendLine(ex.ToString());
            return builder.ToString();
        }

        internal void SummarizeHeaders(StringBuilder builder, MessageEnvelope envelope)
        {
            if (envelope.HasRecipients)
            {
                builder.AppendFormat("RECIPIENTS={0}", envelope.Recipients.ToString());
                builder.AppendLine();
            }
            if (envelope.HasDomainRecipients)
            {
                builder.AppendFormat("DOMAIN RECIPIENTS={0}", envelope.DomainRecipients.ToString());
                builder.AppendLine();
            }
            if (envelope.HasRejectedRecipients)
            {
                builder.AppendFormat("REJECTED RECIPIENTS={0}", envelope.RejectedRecipients.ToString());
                builder.AppendLine();
                builder.AppendFormat("NO CERTS={0}", this.CollectNoCertInformation(envelope.RejectedRecipients));
                builder.AppendLine();
            }
            if (envelope.HasRejectedRecipients)
            {
                builder.AppendFormat("OTHER RECIPIENTS={0}", envelope.OtherRecipients.ToString());
                builder.AppendLine();
            }
        }
        
        string CollectNoCertInformation(NHINDAddressCollection recipients)
        {
            if (recipients.IsNullOrEmpty())
            {
                return string.Empty;
            }
            
            StringBuilder builder = new StringBuilder();
            foreach(NHINDAddress recipient in recipients)
            {
                if (!recipient.HasCertificates)
                {
                    builder.Append(recipient.Address);
                    builder.Append(';');
                }
            }
            
            return builder.ToString();
        }
    }
}
