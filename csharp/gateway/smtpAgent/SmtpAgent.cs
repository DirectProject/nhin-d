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
using System.IO;
using NHINDirect.Agent;
using NHINDirect.Agent.Config;
using NHINDirect.Certificates;
using NHINDirect.Diagnostics;
using NHINDirect.Mail;
using NHINDirect.Mime;
using NHINDirect.Config.Store;
using NHINDirect.Config.Client.DomainManager;
using System.Diagnostics;
using CDO;
using ADODB;

namespace NHINDirect.SmtpAgent
{    
    public class SmtpAgent
    {
        SmtpAgentSettings m_settings;
        NHINDAgent m_agent;
        DomainPostmasters m_postmasters;
        AgentDiagnostics m_diagnostics;
        MessageRouter m_router;
        BounceMessageCreator m_outgoingBounceFactory;
        BounceMessageCreator m_incomingBounceFactory;
        
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
                return m_diagnostics.Log;
            }
        }
        
        public MessageRouter Router
        {
            get
            {
                return m_router;
            }
        }
        
        void VerifyInitialized()
        {
            if (m_agent == null)
            {
                throw new InvalidOperationException("Not initialized");
            }
        }

        /// <summary>
        /// Write an informational line to the log
        /// </summary>
        public void LogStatus(string message)
        {
            m_diagnostics.LogStatus(message);
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
            
            m_diagnostics = new AgentDiagnostics(new LogFile(m_settings.LogSettings.CreateWriter()), m_settings.LogVerbose);
            
            this.LogStatus("Init_Begin");
            try
            {
                this.InitPostmasters();
                this.InitFolders();
                this.InitRoutes();                
                this.InitBounceFactories();
                this.InitAgent();
                this.SubscribeToAgentEvents();
     
                this.LogStatus("Init_End");
            }
            catch (Exception error)
            {
                this.LogStatus("Init_Failed");
                m_diagnostics.LogError(error);
                throw;
            }
        }
        
        void InitAgent()
        {
            this.LogStatus("CreateAgent_Begin");
            
            m_agent = m_settings.CreateAgent();            
            this.LogStatus("CreateAgent_End");            
        }
        
        void InitPostmasters()
        {
            this.LogStatus("InitPostmasters_Begin");
            m_postmasters = new DomainPostmasters();
            m_postmasters.Init(m_settings.Domains, m_settings.Postmasters);
            this.LogStatus("InitPostmasters_End");
        }
        
        void InitFolders()
        {
            this.LogStatus("InitFolder_Begin");

            m_settings.RawMessage.EnsureFolders();
            m_settings.Incoming.EnsureFolders();
            m_settings.Outgoing.EnsureFolders();
            m_settings.BadMessage.EnsureFolders();

            this.LogStatus("InitFolder_End");        
        }
        
        void InitRoutes()
        {
            m_router = new MessageRouter(m_diagnostics);            
            if (!m_settings.HasRoutes)
            {
                return;
            }
            
            this.LogStatus("InitRoutes_Begin");
            
            m_router.SetRoutes(m_settings.IncomingRoutes);            
            
            this.LogStatus("InitRoutes_End");
        }
        
        void SubscribeToAgentEvents()
        {
            this.LogStatus("SubscribingToEvents_Begin");

            m_agent.PreProcessIncoming += this.OnPreProcessIncoming;
            m_agent.ErrorIncoming += m_diagnostics.OnIncomingError;
            m_agent.ErrorOutgoing += m_diagnostics.OnOutgoingError;
            
            DnsCertResolver dnsResolver = m_agent.PublicCertResolver as DnsCertResolver;
            if (dnsResolver != null)
            {
                dnsResolver.Error += m_diagnostics.OnDnsError;
            }

            this.LogStatus("SubscribingToEvents_End");
        }
        
        void InitBounceFactories()
        {
            if (!m_settings.HasMessageBounceSettings)
            {
                return;
            }

            if (m_settings.MessageBounce.EnableForOutgoing)
            {
                m_outgoingBounceFactory = new BounceMessageCreator(m_settings.MessageBounce.OutgoingTemplate);
            }
            if (m_settings.MessageBounce.EnableForIncoming)
            {
                m_incomingBounceFactory = new BounceMessageCreator(m_settings.MessageBounce.IncomingTemplate);
            }
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
                envelope = this.ProcessEnvelope(message, envelope);
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
                // We did well...
                //
                this.AcceptMessage(message);
                //
                // We may want want to update logs and do some final post processing
                //
                this.PostProcessMessage(message, envelope);
            }
            catch (Exception ex)
            {
                this.RejectMessage(message);
                m_diagnostics.LogError(ex);
                throw;
            }
        }

        protected virtual void PreProcessMessage(CDO.Message message)
        {
            this.LogStatus("Message Received from: " + message.From);
            this.CopyMessageToFolder(message, m_settings.RawMessage);
        }
                                
        protected virtual MessageEnvelope ProcessEnvelope(CDO.Message message, MessageEnvelope envelope)
        {            
            //
            // Messages from within the domain are always treated as OUTGOING
            // All messages sent by sources OUTSIDE the domain are always treated as INCOMING
            //
            bool isOutgoing = this.Agent.Domains.IsManaged(envelope.Sender);
            AgentException noTrustException = null;
            try
            {
                if (isOutgoing)
                {
                    envelope = this.ProcessOutgoing(message, envelope);
                }
                else
                {
                    envelope = this.ProcessIncoming(message, envelope);
                }                
                if (envelope == null)
                {
                    throw new SmtpAgentException(SmtpAgentError.InvalidEnvelopeFromAgent);
                }
                
                if (envelope.HasRejectedRecipients)
                {
                    this.GenerateBounces(envelope);
                }
                                
                return envelope;
            }
            catch(AgentException agentEx)
            {
                if (agentEx.Error != AgentError.NoTrustedRecipients)
                {
                    throw;
                }
                
                noTrustException = agentEx;
            }
            
            if (noTrustException != null)
            {
                this.GenerateBounces(envelope, isOutgoing);                
                throw noTrustException; // Rethrow...
            }
            
            return null;     
        }
        
        protected virtual MessageEnvelope ProcessOutgoing(CDO.Message message, MessageEnvelope envelope)
        {
            envelope = this.Agent.ProcessOutgoing(envelope);
            this.LogStatus("ProcessedOutgoing");
            return envelope;
        }
        
        //
        // Event handler called by the agent
        // Here, if configured, we will verify that addresses are real. We don't always have to do that, especially if
        // say we are running as PURELY a gateway. However, if we are set up to e.g. route messages, then...
        //
        void OnPreProcessIncoming(IncomingMessage message)
        {
            if (!m_settings.HasAddressManager)
            {
                return;
            }
            
            message.EnsureRecipientsCategorizedByDomain(this.Agent.Domains);

            NHINDAddressCollection recipients = message.DomainRecipients;
            Address[] resolved = this.ResolveAddresses(recipients);
            if (resolved.IsNullOrEmpty())
            {
                throw new AgentException(AgentError.NoRecipients);
            }            
            // Remove any addresses that could not be resolved
            // Yes, this is currently n^2, but given the typical # of addresses, cost should be insignificant
            int i = 0;
            while (i < recipients.Count)
            {
                NHINDAddress recipient = recipients[i];
                int iAddress = Array.FindIndex<Address>(resolved, x => x.Match(recipient));
                if (iAddress >= 0)
                {
                    ++i; // Found
                    recipient.Tag = resolved[iAddress];
                }
                else
                {
                    recipients.RemoveAt(i);
                }
            }
        }
        
        protected virtual MessageEnvelope ProcessIncoming(CDO.Message message, MessageEnvelope envelope)
        {            
            envelope = this.Agent.ProcessIncoming(envelope);
            this.LogStatus("ProcessedIncoming");
            //
            // Need to update the envelope (if any) to ensure the message is delivered only to trusted valid recipients
            //
            message.SetEnvelopeRecipients(envelope.DomainRecipients.ToMailAddressCollection());
            
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
                this.CopyMessageToFolder(message, m_settings.Outgoing);
                relay = m_settings.Outgoing.EnableRelay;
            }
            else
            {
                bool fullyRouted = m_router.Route(message, envelope);
                this.CopyMessageToFolder(message, m_settings.Incoming);
                relay = fullyRouted ? false : m_settings.Incoming.EnableRelay;
            }

            if (!relay)
            {
                //
                // Turn relay off for debugging and diagnostics
                //
                m_diagnostics.LogStatus(isOutgoing ? "Outgoing Relay disabled" : "Incoming Relay Disabled");
                message.AbortMessage();
                return;
            }
        }
                
        protected virtual void AcceptMessage(CDO.Message message)
        {
            message.SetMessageStatus(CdoMessageStat.cdoStatSuccess);
        }
        
        protected virtual void RejectMessage(CDO.Message message)
        {
            try
            {
                message.AbortMessage();
                this.LogStatus("Rejected Message");

                this.CopyMessageToFolder(message, m_settings.BadMessage);
            }
            catch
            {
            }
        }

        void CopyMessageToFolder(CDO.Message message, MessageProcessingSettings settings)
        {
            try
            {
                MessageRouter.CopyMessageToFolder(message, settings);
            }
            catch (Exception ex)
            {
                m_diagnostics.LogError(ex);
            }
        }

        Address[] ResolveAddresses(NHINDAddressCollection addresses)
        {
            string[] emailAddresses = (
                from address in addresses
                select address.Address
            ).ToArray();

            AddressManagerClient client = new AddressManagerClient(m_settings.AddressManager.Binding, m_settings.AddressManager.Endpoint);
            return client.GetAddresses(emailAddresses);
        }                                                

        //--------------------------------------
        //
        // Envelope Manipulation
        // 
        //--------------------------------------
        
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

            string sender = message.GetEnvelopeSender();
            if (string.IsNullOrEmpty(sender))
            {
                throw new NotSupportedException("Sender required");
            }
            //
            // In SMTP Server, the MAIL TO (sender) in the envelope can be empty if the message is from the server postmaster 
            // The actual postmaster address is found in the message itself
            //
            if (Extensions.IsSenderLocalPostmaster(sender))
            {
                return false;
            }
            string recipients = message.GetEnvelopeRecipients();
            if (string.IsNullOrEmpty(recipients))
            {
                throw new NotSupportedException("Recipients required");
            }

            recipientAddresses = NHINDAddressCollection.ParseSmtpServerEnvelope(recipients);
            senderAddress = new NHINDAddress(sender);

            return true;
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
            string sender = message.GetEnvelopeSender();
            if (string.IsNullOrEmpty(sender))
            {
                return false;
            }

            return (Extensions.IsSenderLocalPostmaster(sender) || m_postmasters.IsPostmaster(sender));
        }

        //---------------------------------------------------
        //
        //  Bounce Handling
        //
        //---------------------------------------------------
        void GenerateBounces(MessageEnvelope envelope)
        {
            this.GenerateBounces(envelope, (envelope is OutgoingMessage));
        }
        
        void GenerateBounces(MessageEnvelope envelope, bool isOutgoing)
        {
            envelope.EnsureRecipientsCategorizedByDomain(this.Agent.Domains);

            if (isOutgoing)
            {
                this.GenerateBouncesForOutgoing(envelope);
            }
            else
            {
                this.GenerateBouncesForIncoming(envelope);
            }
        }                

        protected virtual void GenerateBouncesForOutgoing(MessageEnvelope envelope)
        {
            if (m_outgoingBounceFactory == null)
            {
                return;
            }

            try
            {                
                MailMessage bounceMessage = m_outgoingBounceFactory.Create(envelope, m_postmasters[envelope.Sender]);
                if (bounceMessage != null)
                {
                    this.LogStatus("Bounced Outgoing");
                    bounceMessage.SendToFolder(m_settings.MessageBounce.MailPickupFolder);
                }
            }
            catch(Exception ex)
            {
                m_diagnostics.LogError(ex);
            }
        }

        protected virtual void GenerateBouncesForIncoming(MessageEnvelope envelope)
        {
            if (m_incomingBounceFactory == null)
            {
                return;
            }
        
            try
            {                
                if (!envelope.HasDomainRecipients)
                {
                    return;
                }
                
                MailAddress firstDomainRecipient = envelope.DomainRecipients[0];
                MailMessage bounceMessage = m_incomingBounceFactory.Create(envelope, m_postmasters[firstDomainRecipient]);
                if (bounceMessage != null)
                {
                    this.LogStatus("Bounced Incoming");
                    bounceMessage.SendToFolder(m_settings.MessageBounce.MailPickupFolder);
                }
            }
            catch(Exception ex)
            {
                m_diagnostics.LogError(ex);
            }
        }        
    }
}
