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
using System.Runtime.InteropServices;
using System.Net.Mail;
using NHINDirect.Agent;
using NHINDirect.Agent.Config;
using NHINDirect.Certificates;
using NHINDirect.Diagnostics;
using NHINDirect.Mail;
using NHINDirect.Mime;
using System.Diagnostics;
using CDO;
using ADODB;

namespace NHINDirect.SmtpAgent
{    
    public class SmtpAgent
    {
        SmtpAgentSettings m_settings;
        NHINDAgent m_agent;
        LogFile m_log;
        DomainPostmasters m_postmasters;
                
        public SmtpAgent(SmtpAgentSettings settings)
        {
            if (settings == null)
            {
                throw new ArgumentNullException();
            }
            
            this.Init(settings);
        }
        
        public NHINDAgent Agent
        {
            get
            {
                this.VerifyInitialized();
                return m_agent;
            }
        }
        
        public LogFile Log
        {
            get
            {
                return m_log;
            }
        }

        void VerifyInitialized()
        {
            if (m_agent == null)
            {
                throw new InvalidOperationException("Not initialized");
            }
        }        
        //---------------------------------------------------
        //
        //  Agent Initialization
        //
        //---------------------------------------------------
        void Init(SmtpAgentSettings settings)
        {
            m_settings = settings;
            m_settings.Validate();
            
            m_log = new LogFile(m_settings.LogSettings.CreateWriter());
            
            m_log.WriteLine("Init_Begin");
            try
            {
                this.InitAgent();
                
                m_log.WriteLine("Init_End");
            }
            catch (Exception error)
            {
                m_log.WriteLine("Init_Failed");
                m_log.WriteError(error);
                throw;
            }
        }
        
        void InitAgent()
        {
            m_log.WriteLine("InitPostmasters_Begin");
            m_postmasters = new DomainPostmasters();
            m_postmasters.Init(m_settings.Domains, m_settings.Postmasters);
            m_log.WriteLine("InitPostmasters_End");

            this.InitFolders();            

            m_log.WriteLine("CreateAgent_Begin");
            m_agent = m_settings.CreateAgent();
            m_log.WriteLine("CreateAgent_End");

            this.SubscribeToAgentEvents();
        }
                
        void InitFolders()
        {
            m_log.WriteLine("InitFolder_Begin");

            m_settings.RawMessage.EnsureFolders();
            m_settings.Incoming.EnsureFolders();
            m_settings.Outgoing.EnsureFolders();
            m_settings.BadMessage.EnsureFolders();

            m_log.WriteLine("InitFolder_End");        
        }
        
        void SubscribeToAgentEvents()
        {
            m_log.WriteLine("SubscribingToEvents_Begin");

            DnsCertResolver dnsResolver = m_agent.PublicCertResolver as DnsCertResolver;
            if (dnsResolver != null)
            {
                dnsResolver.Error += this.WriteDnsError;
            }

            m_log.WriteLine("SubscribingToEvents_End");
        }
                
        //---------------------------------------------------
        //
        //  Message Processing
        //
        //---------------------------------------------------
        public void ProcessMessage(CDO.Message message)
        {            
            try
            {
                this.VerifyInitialized();
                //
                // Preprocessing may involve housekeeping like logging message arrival
                //
                this.PreProcessMessage(message);
                //
                // Let the agent do its thing
                //
                MessageEnvelope envelope = this.CreateEnvelope(message);
                envelope = this.ProcessEnvelope(envelope);
                if (envelope == null)
                {
                    throw new SmtpAgentException(SmtpAgentError.InvalidEnvelopeFromAgent);
                }
                //
                // Internal only messages from the postmaster can be passed through in the clear
                // They are invariably local delivery notification errors
                //
                if (!this.IsInternalPostmasterMessage(envelope))
                {
                    //
                    // Replace the contents of the original message with what the agent gave us
                    //
                    this.UpdateMessageText(message, envelope);
                }
                //
                // We did good...
                //
                message.SetMessageStatus(CdoMessageStat.cdoStatSuccess);
                //
                // We may want want to update logs and do some final post processing
                //
                this.PostProcessMessage(message, envelope);
            }
            catch (Exception ex)
            {
                this.RejectMessage(message);
                m_log.WriteError(ex);
                throw;
            }
        }

        protected virtual void PreProcessMessage(CDO.Message message)
        {
            m_log.WriteLine("Message Received");
            this.CopyMessage(message, m_settings.RawMessage);
        }
                                
        protected virtual MessageEnvelope ProcessEnvelope(MessageEnvelope envelope)
        {
            //
            // Messages from within the domain are always treated as OUTGOING
            // All messages sent by sources OUTSIDE the domain are always treated as INCOMING
            //
            bool isOutgoing = this.Agent.Domains.IsManaged(envelope.Sender);
            if (isOutgoing)
            {
                envelope = this.Agent.ProcessOutgoing(envelope);
            }
            else
            {
                envelope = this.Agent.ProcessIncoming(envelope);
            }
            
            if (envelope == null)
            {
                throw new SmtpAgentException(SmtpAgentError.InvalidEnvelopeFromAgent);
            }
            
            m_log.WriteLine(isOutgoing ? "ProcessedOutgoing": "ProcessedIncoming");
                        
            return envelope;
        }
        
        protected virtual void UpdateMessageText(CDO.Message message, MessageEnvelope envelope)
        {
            string messageText = envelope.SerializeMessage();
            if (string.IsNullOrEmpty(messageText))
            {
                throw new SmtpAgentException(SmtpAgentError.EmptyResultFromAgent);
            }
            message.SetMessageText(messageText, true);
        }
                
        protected virtual void PostProcessMessage(CDO.Message message, MessageEnvelope envelope)
        {
            bool relay = true;
            bool isOutgoing = envelope is OutgoingMessage;
            if (isOutgoing)
            {
                this.CopyMessage(message, m_settings.Outgoing);
                relay = m_settings.Outgoing.EnableRelay;
            }
            else
            {
                this.CopyMessage(message, m_settings.Incoming);
                relay = m_settings.Incoming.EnableRelay;
            }

            if (!relay)
            {
                //
                // Turn relay off for debugging and diagnostics
                //
                m_log.WriteLine(isOutgoing ? "Outoing Relay disabled" : "Incoming Relay Disabled");
                message.AbortMessage();
                return;
            }
        }

        protected virtual void RejectMessage(CDO.Message message)
        {
            try
            {
                message.AbortMessage();
                m_log.WriteLine("Rejected Message");

                this.CopyMessage(message, m_settings.BadMessage);
            }
            catch
            {
            }
        }
        
        void GenerateBounces(CDO.Message message, MessageEnvelope envelope)
        {
            if (envelope is OutgoingMessage)
            {
                this.GenerateBouncesForOutoing(message, envelope);
            }
            else
            {
                this.GenerateBouncesForIncoming(message, envelope);
            }
        }

        protected virtual void GenerateBouncesForOutoing(CDO.Message message, MessageEnvelope envelope)
        {
            //
            // TODO
            //
        }

        protected virtual void GenerateBouncesForIncoming(CDO.Message message, MessageEnvelope envelope)
        {
            //
            // TODO
            //
        }
        
        MessageEnvelope CreateEnvelope(CDO.Message message)
        {
            NHINDAddressCollection recipientAddresses = null;
            NHINDAddress senderAddress = null;
            MessageEnvelope envelope;

            string messageText = message.GetMessageText();

            if (this.ExtractEnvelopeFields(message, ref recipientAddresses, ref senderAddress))
            {
                envelope = new MessageEnvelope(messageText, recipientAddresses, senderAddress);
            }
            else
            {
                envelope = new MessageEnvelope(messageText);
            }
            
            return envelope;
        }
        
        bool IsInternalPostmasterMessage(MessageEnvelope envelope)
        {
            if (!m_postmasters.IsPostmaster(envelope.Sender))
            {
                return false;
            }
            
            return (envelope.DomainRecipients.Count == envelope.Recipients.Count);
        }
        
        bool IsSenderPostmaster(CDO.Message message)
        {
            string sender = this.GetEnvelopeSender(message);
            if (string.IsNullOrEmpty(sender))
            {
                return false;
            }
            
            return (this.IsSenderLocalPostmaster(sender) ||  m_postmasters.IsPostmaster(sender));
        }
        
        void CopyMessage(CDO.Message message, MessageProcessingSettings settings)
        {
            if (!settings.HasCopyFolder)
            {
                return;
            }

            this.CopyMessage(message, settings.CopyFolder);
        }

        void CopyMessage(CDO.Message message, string folderPath)
        {
            try
            {
                string fileName = Guid.NewGuid().ToString("D") + ".eml";
                message.SaveToFile(System.IO.Path.Combine(folderPath, fileName));
            }
            catch (Exception ex)
            {
                m_log.WriteError(ex);
            }
        }
                                        
        //---------------------------------------------------
        //
        //  Helpers
        //
        //---------------------------------------------------

        const string EnvelopeField_Recipients = @"http://schemas.microsoft.com/cdo/smtpenvelope/recipientlist";
        const string EnvelopeField_Sender = @"http://schemas.microsoft.com/cdo/smtpenvelope/senderemailaddress";
        const string EnvelopeSender_LocalPostmaster = "<>";

        bool ExtractEnvelopeFields(CDO.Message message, ref NHINDAddressCollection recipientAddresses, ref NHINDAddress senderAddress)
        {
            Fields fields = message.GetEnvelopeFields();
            if (fields == null || fields.Count == 0)
            {
                //
                // No envelope
                //
                return false;
            }

            recipientAddresses = null;
            senderAddress = null;

            string sender = this.GetEnvelopeSender(message);
            if (string.IsNullOrEmpty(sender))
            {
                throw new NotSupportedException("Sender required");
            }
            //
            // In SMTP Server, the MAIL TO (sender) in the envelope can be empty if the message is from the server postmaster 
            // The actual postmaster address is found in the message itself
            //
            if (this.IsSenderLocalPostmaster(sender))
            {
                return false;
            }
            string recipients = this.GetEnvelopeRecipients(message);
            if (string.IsNullOrEmpty(recipients))
            {
                throw new NotSupportedException("Recipients required");
            }
            
            recipientAddresses = NHINDAddressCollection.ParseSmtpServerEnvelope(recipients);
            senderAddress = new NHINDAddress(sender);

            return true;
        }
        
        string GetEnvelopeRecipients(CDO.Message message)
        {
            return this.GetEnvelopeField(message, EnvelopeField_Recipients);
        }
        
        string GetEnvelopeSender(CDO.Message message)
        {
            return this.GetEnvelopeField(message, EnvelopeField_Sender);
        }

        //
        // In SMTP Server, the sender address can be empty if the message is from the postmaster
        //
        bool IsSenderLocalPostmaster(string sender)
        {
            return (sender == SmtpAgent.EnvelopeSender_LocalPostmaster);
        }
        
        string GetEnvelopeField(CDO.Message message, string name)
        {
            Fields fields = message.GetEnvelopeFields();
            if (fields == null || fields.Count == 0)
            {
                return null;
            }

            return fields.GetStringValue(name);
        }

        void WriteDnsError(DnsCertResolver service, Exception error)
        {
            m_log.WriteError(error);
        }
    }
}
