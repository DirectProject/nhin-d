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
using System.Text;

using NHINDirect.Agent;
using NHINDirect.Certificates;
using NHINDirect.Container;
using NHINDirect.Diagnostics;

namespace NHINDirect.SmtpAgent
{
    internal class AgentDiagnostics
    {
        const string EventLogName = "nhinMessageSink";

    	private readonly ILogger m_logger;
        
        internal AgentDiagnostics()
        {
        	m_logger = IoC.Resolve<ILogFactory>().GetLogger(GetType());
        }

		private ILogger Log
		{
			get
			{
				return m_logger;
			}
		}

        //internal static void WriteEventLog(string message)
        //{
        //    EventLog.WriteEntry(EventLogName, message);
        //}

        //internal static void WriteEventLog(Exception ex)
        //{
        //    EventLog.WriteEntry(EventLogName, ex.ToString(), EventLogEntryType.Error);
        //}
        
        internal void OnOutgoingError(OutgoingMessage message, Exception error)
        {
            if (Log.IsDebugEnabled)
            {
                Log.Error(this.BuildVerboseErrorMessage("OUTGOING", message, error));
            }
            else
            {
                Log.Error("OnOutgoingError", error);
            }
        }

        internal void OnIncomingError(IncomingMessage message, Exception error)
        {
            if (Log.IsDebugEnabled)
            {
                Log.Error(this.BuildVerboseErrorMessage("INCOMING", message, error));
            }
            else
            {
                Log.Error("OnIncomingError", error);
            }
        }

        internal void OnDnsError(ICertificateResolver resolver, Exception error)
        {
            Log.Error("OnDnsError", error);
        }
        
        internal void LogEnvelopeHeaders(ISmtpMessage message)
        {       
            if (Log.IsDebugEnabled && message.HasEnvelope)
            {     
                Log.Debug(this.SummarizeEnvelopeHeaders(message));
            }
        }
        
        internal string SummarizeEnvelopeHeaders(ISmtpMessage message)
        {
            string mailFrom = message.GetMailFrom();
            string rcptTo = message.GetRcptTo();
            return string.Format("MAILFROM={0};RCPTTO={1}", mailFrom, rcptTo);
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
                builder.Append("RECIPIENTS=").AppendLine(envelope.Recipients);
            }
            if (envelope.HasDomainRecipients)
            {
                builder.Append("DOMAIN RECIPIENTS=").AppendLine(envelope.DomainRecipients);
            }
            if (envelope.HasRejectedRecipients)
            {
                builder.Append("REJECTED RECIPIENTS=").AppendLine(envelope.RejectedRecipients);
                builder.Append("NO CERTS=").AppendLine(this.CollectNoCertInformation(envelope.RejectedRecipients));
            }
            if (envelope.HasRejectedRecipients)
            {
                builder.Append("OTHER RECIPIENTS=").AppendLine(envelope.OtherRecipients);
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
                    builder.Append(recipient.Address).Append(';');
                }
            }
            
            return builder.ToString();
        }
    }
}
