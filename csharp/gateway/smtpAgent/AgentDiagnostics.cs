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
using System.Text;
using System.Security.Cryptography.Pkcs;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Agent;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Diagnostics;
using Health.Direct.Common.Extensions;

namespace Health.Direct.SmtpAgent
{
    internal class AgentDiagnostics
    {
        private readonly ILogger m_logger;
        SmtpAgent m_agent;
        
        internal AgentDiagnostics(SmtpAgent agent)
        {
            m_agent = agent;
            m_logger = Log.For(this);
        }

        internal ILogger Logger
        {
            get
            {
                return m_logger;
            }
        }

        internal void OnGeneralError(DirectAgent agent, Exception error)
        {
            if (Logger.IsDebugEnabled)
            {
                Logger.Error(error);
            }
            else
            {
                Logger.Error(error.Message);
            }
        }

        internal void OnOutgoingError(OutgoingMessage message, Exception error)
        {
            if (Logger.IsDebugEnabled)
            {
                Logger.Error(this.BuildVerboseErrorMessage("OUTGOING", message, error));
            }
            else
            {
                Logger.Error("OUTGOING_ERROR {0}", error.Message);
            }
        }

        internal void OnIncomingError(IncomingMessage message, Exception error)
        {
            if (Logger.IsDebugEnabled)
            {
                Logger.Error(this.BuildVerboseErrorMessage("INCOMING", message, error));
            }
            else
            {
                Logger.Error("INCOMING_ERROR {0}", error.Message);
            }
        }

        internal void OnResolverError(ICertificateResolver resolver, Exception error)
        {
            Logger.Error("RESOLVER ERROR {0}, {1}", resolver.GetType().Name, error.Message);
        }
        
        internal void OnCertificateProblem(X509ChainElement chainElement)
        {
            if (chainElement != null)
            {
                Logger.Error("Chain Element has problem {0}", this.Summarize(chainElement));
            }
        }

        internal void OnUntrustedCertificate(X509Certificate2 cert)
        {
            if (cert != null)
            {
                Logger.Error("Untrusted certificate {0}", cert.Subject);
            }
        }
        
        internal void LogEnvelopeHeaders(ISmtpMessage message)
        {       
            if (Logger.IsDebugEnabled && message.HasEnvelope)
            {     
                Logger.Debug(this.SummarizeEnvelopeHeaders(message));
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
            if (envelope.HasOtherRecipients)
            {
                builder.Append("OTHER RECIPIENTS=").AppendLine(envelope.OtherRecipients);
            }

            IncomingMessage incoming = envelope as IncomingMessage;
            if (incoming != null)
            {
                CollectSignatures(builder, incoming); 
            }
        }

        void CollectSignatures(StringBuilder builder, IncomingMessage message)
        {
            if (!message.HasSignatures)
            {
                return;
            }

            SignerInfoCollection allSigners = message.Signatures.SignerInfos;
            foreach (SignerInfo signer in allSigners)
            {
                X509Certificate2 cert = signer.Certificate;
                string output = string.Format(
                    "E/CN={0},ValidFrom={1},ValidTo={2},IssuerName={3},Version={4},Thumbprint={5}",
                    cert.ExtractEmailNameOrName(), 
                    cert.GetEffectiveDateString(), 
                    cert.GetExpirationDateString(), 
                    cert.Issuer,
                    cert.Version,
                    cert.Thumbprint);

                builder.Append("CERT:").AppendLine(output);                
            }
        }
        string CollectNoCertInformation(DirectAddressCollection recipients)
        {
            if (recipients.IsNullOrEmpty())
            {
                return string.Empty;
            }
            
            StringBuilder builder = new StringBuilder();
            foreach(DirectAddress recipient in recipients)
            {
                if (!recipient.HasCertificates)
                {
                    builder.Append(recipient.Address).Append(';');
                }
            }
            
            return builder.ToString();
        }
        
        internal string Summarize(X509ChainElement chainElement)
        {
            X509ChainStatusFlags problemFlags = m_agent.SecurityAgent.TrustModel.CertChainValidator.ProblemFlags;
            
            StringBuilder builder = new StringBuilder();
            builder.Append(chainElement.Certificate.ExtractEmailNameOrName());
            builder.Append(";");
            foreach(X509ChainStatus status in chainElement.ChainElementStatus)
            {
                if ((status.Status & problemFlags) != 0)
                {
                    builder.Append(status.Status);
                }
            }
            
            return builder.ToString();
        }
    }
}