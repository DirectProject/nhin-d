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
using System.Diagnostics;
using System.IO;
using Health.Direct.Agent;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Container;
using Health.Direct.Common.Cryptography;
using Health.Direct.Common.Diagnostics;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Mail;
using Health.Direct.Config.Client;
using Health.Direct.Config.Client.DomainManager;
using Health.Direct.Config.Store;
using Health.Direct.Common.Mail.Notifications;

namespace Health.Direct.SmtpAgent
{
    public class SmtpAgent
    {
        /// <summary>
        /// Headers used to pass down what was in the envelope 
        /// See documentation on MSDN
        /// </summary>
        public class XHeaders
        {
            public const string Receivers = "X-Receiver";
            public const string Sender = "X-Sender";
        }
        
        IAuditor m_auditor;
        ILogger m_logger;

        SmtpAgentSettings m_settings;
        DirectAgent m_agent;
        AgentDiagnostics m_diagnostics;
        MessageRouter m_router;
        ConfigService m_configService;
        NotificationProducer m_notifications;
                
        internal SmtpAgent(SmtpAgentSettings settings)
        {
            if (settings == null)
            {
                throw new ArgumentNullException("settings");
            }
            
            this.Init(settings);
        }
        
        public SmtpAgentSettings Settings
        {
            get
            {
                return m_settings;
            }
        }

        private ILogger Logger
        {
            get
            {
                return m_logger;
            }
        }

        private IAuditor Auditor
        {
            get
            {
                return m_auditor;
            }
        }
                
        public DirectAgent SecurityAgent
        {
            get
            {
                this.VerifyInitialized();
                return m_agent;
            }
        }
                
        public MessageRouter Router
        {
            get
            {
                return m_router;
            }
        }
        
        public AgentDomains Domains
        {
            get
            {
                return this.SecurityAgent.Domains;
            }            
        }
        
        void VerifyInitialized()
        {
            if (m_agent == null)
            {
                throw new SmtpAgentException(SmtpAgentError.NotInitialized);
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

            m_auditor = IoC.Resolve<IAuditor>();
            m_logger = Log.For(this);

            m_diagnostics = new AgentDiagnostics(this);
            m_configService = new ConfigService(m_settings);

            using (new MethodTracer(Logger))
            {
                try
                {
                    //
                    // First, setup defaults using Xml Config
                    //
                    this.InitDomains();
                    this.InitFolders();
                    this.InitRoutes();
                    this.InitNotifications();
                    //
                    // Call config service, if any was configured
                    //
                    this.InitDomainsFromConfigService();
                    //
                    // Finally, we can agent...
                    //
                    this.InitAgent();
                    this.SubscribeToAgentEvents();
                }
                catch (Exception ex)
                {
                    Logger.Error("While initializing {0}", ex.ToString());
                    throw;
                }
            }
        }
        
        void InitAgent()
        {
            using (new MethodTracer(Logger))
            {
                m_agent = m_settings.CreateAgent();
            }
        }
        
        void InitDomainsFromConfigService()
        {
            if (!m_settings.HasDomainManagerService)
            {
                Logger.Info("Domains not loaded from config service");
                return;
            }

            using (new MethodTracer(Logger))
            {
                Domain[] configuredDomains = m_configService.GetDomains(m_settings.Domains);
                if (configuredDomains.IsNullOrEmpty())
                {
                    Logger.Error("Returned configured domains was null or empty");
                    throw new SmtpAgentException(SmtpAgentError.ConfiguredDomainsMismatch);
                }
                if (configuredDomains.Length != m_settings.Domains.Length)
                {
                    Logger.Error("Returned configured domains did not match those listed in the settings file");
                    Logger.Error("from service={0} from settings={1}", 
                                 configuredDomains.Length, m_settings.Domains.Length);
                    throw new SmtpAgentException(SmtpAgentError.ConfiguredDomainsMismatch);
                }
            }
        }
        
        void InitDomains()
        {
        }
        
        void InitFolders()
        {
            using (new MethodTracer(Logger))
            {
                m_settings.EnsureFolders();
            }
        }
        
        void InitRoutes()
        {
            m_router = new MessageRouter(m_diagnostics);            
            if (!m_settings.HasRoutes)
            {
                return;
            }

            using (new MethodTracer(Logger))
            {
                m_router.Init(m_settings.IncomingRoutes);
            }
        }
        
        void InitNotifications()
        {
            using (new MethodTracer(Logger))
            {
                m_notifications = new NotificationProducer(m_settings.Notifications);
            }
        }
        
        void SubscribeToAgentEvents()
        {
            using (new MethodTracer(Logger))
            {
                m_agent.PreProcessOutgoing += this.OnPreProcessOutgoing;
                m_agent.PreProcessIncoming += this.OnPreProcessIncoming;
                m_agent.Error += m_diagnostics.OnGeneralError;
                m_agent.ErrorIncoming += m_diagnostics.OnIncomingError;
                m_agent.ErrorOutgoing += m_diagnostics.OnOutgoingError;
                
                this.SubscribeToResolverEvents(m_agent.PublicCertResolver);
                this.SubscribeToResolverEvents(m_agent.PrivateCertResolver);
                
                m_agent.TrustModel.CertChainValidator.Problem += m_diagnostics.OnCertificateProblem;
                m_agent.TrustModel.CertChainValidator.Untrusted += m_diagnostics.OnUntrustedCertificate;
            }
        }
        
        void SubscribeToResolverEvents(ICertificateResolver resolver)
        {
            CertificateResolverCollection resolvers = resolver as CertificateResolverCollection;
            if (resolvers != null)
            {
                resolvers.Error += m_diagnostics.OnResolverError;
                return;
            }

            DnsCertResolver dnsResolver = m_agent.PublicCertResolver as DnsCertResolver;
            if (dnsResolver != null)
            {
                dnsResolver.Error += m_diagnostics.OnResolverError;
                return;
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
                this.ProcessMessage(new CDOSmtpMessage(message));
            }
            catch
            {
                // Paranoia
                message.AbortMessage();
                throw;
            }
        }

        public void ProcessMessage(ISmtpMessage message)
        {
            bool? isIncoming = null;
            
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
                MessageEnvelope envelope = message.GetEnvelope();
                envelope = this.ProcessEnvelope(message, envelope);
                if (envelope == null)
                {
                    throw new SmtpAgentException(SmtpAgentError.InvalidEnvelopeFromAgent);
                }
                isIncoming = envelope is IncomingMessage;
                //
                // Capture envelope sender/receiver in the message
                //
                this.UpdateXHeaders(envelope);
                //
                // Replace the contents of the original message with what the agent gave us
                //
                this.UpdateMessageText(message, envelope);
                //
                // We did well...
                //
                this.AcceptMessage(message, isIncoming.Value);
                //
                // We may want want to update logs and do some final post processing
                //
                this.PostProcessMessage(message, envelope);
            }
            catch (Exception ex)
            {
                this.RejectMessage(message, isIncoming);
                Logger.Error("While processing message {0}", ex.ToString());
                throw;
            }
        }

        protected virtual void PreProcessMessage(ISmtpMessage message)
        {
            m_diagnostics.LogEnvelopeHeaders(message);
            
            this.CopyMessage(message, m_settings.RawMessage);
        }
                                
        protected virtual MessageEnvelope ProcessEnvelope(ISmtpMessage message, MessageEnvelope envelope)
        {      
            //
            // OUTGOING:
            //  Non-Encrypted messages from within the domain are treated as OUTGOING.
            //  Encrypted messages from within the domain are OPTIONALLY treated as Incoming
            //    - Only if InternalRelay is enabled
            // INCOMING:
            //  All messages sent by sources OUTSIDE the domain are ALWAYS treated as INCOMING
            //
            // The following boolean logic is the way it is to make it *easy to read*
            //
            bool isSenderInDomain = this.SecurityAgent.Domains.IsManaged(envelope.Sender);            
            bool isOutgoing;            
            if (isSenderInDomain)
            {
                isOutgoing = true;
                if (SMIMEStandard.IsEncrypted(envelope.Message))
                {
                    if (!m_settings.AllowInternalRelay)
                    {
                        throw new SmtpAgentException(SmtpAgentError.InternalRelayDisabled);
                    }
                    isOutgoing = false;
                }
            }
            else
            {
                isOutgoing = false;
            }
            
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
                            
            return envelope;
        }

        protected virtual void PostProcessMessage(ISmtpMessage message, MessageEnvelope envelope)
        {
            OutgoingMessage outgoing = envelope as OutgoingMessage;
            if (outgoing != null)
            {
                this.PostProcessOutgoing(message, outgoing);
            }
            else
            {
                this.PostProcessIncoming(message, (IncomingMessage) envelope);
            }
        }

        //---------------------------------------------------
        //
        //  Outgoing
        //
        //---------------------------------------------------

        void OnPreProcessOutgoing(OutgoingMessage message)
        {
            if (!m_settings.HasAddressManager)
            {
                return;
            }
            //
            // Verify that the sender is allowed to send
            //
            Address address = m_configService.GetAddress(message.Sender);
            if (address == null)
            {
                throw new AgentException(AgentError.UntrustedSender);
            }
            
            message.Sender.Tag = address;
        }
        
        public MessageEnvelope ProcessOutgoing(ISmtpMessage message)
        {
            if (message == null)
            {
                throw new ArgumentNullException("message");
            }
            
            return this.ProcessOutgoing(message, message.GetEnvelope());
        }
        
        protected virtual MessageEnvelope ProcessOutgoing(ISmtpMessage message, MessageEnvelope envelope)
        {
            OutgoingMessage outgoing = new OutgoingMessage(envelope);
            if (envelope.Message.IsMDN())
            {
                outgoing.UseIncomingTrustAnchors = this.Settings.Notifications.UseIncomingTrustAnchorsToSend;
            }
            
            envelope = this.SecurityAgent.ProcessOutgoing(outgoing);
            Logger.Debug("ProcessedOutgoing");
            return envelope;
        }

        void PostProcessOutgoing(ISmtpMessage message, OutgoingMessage envelope)
        {
            this.RelayInternal(message, envelope);
            
            if (envelope.HasRecipients)
            {            
                this.CopyMessage(message, m_settings.Outgoing);
            }
            
            if (m_settings.Outgoing.EnableRelay && envelope.HasRecipients)
            {
                message.SetRcptTo(envelope.Recipients);
                m_diagnostics.LogEnvelopeHeaders(message);
            }
            else
            {
                message.Abort();
            }
        }

        // If the message contains trusted internal recipients, drop a copy in the pickup folder, so the message can sent back
        // through the incoming pipeline
        protected void RelayInternal(ISmtpMessage message, MessageEnvelope envelope)
        {
            InternalMessageSettings settings = m_settings.InternalMessage;
            if (!(settings.EnableRelay && settings.HasPickupFolder))
            {
                return;
            }
            if (!envelope.HasDomainRecipients)
            {
                // No internal recipients
                return;
            }
            //
            // We have some trusted domain recipients. Drop a copy of the message into the message pickup folder
            // It will get passed back through the message processing loop and treated as Incoming
            //
            this.CopyMessage(message, settings.PickupFolder);
            //
            // Since we've routed the message ourselves, ensure there is no double delivery by removing them from
            // the recipient list
            //
            envelope.Recipients.Remove(envelope.DomainRecipients);
        }
        
        //---------------------------------------------------
        //
        //  Incoming
        //
        //---------------------------------------------------
        
        //
        // Event handler called by the agent
        // Here, if configured, we will verify that addresses are real. We don't always have to do that, especially if
        // say we are running as PURELY a gateway. However, if we are set up to e.g. route messages, then...
        //
        void OnPreProcessIncoming(IncomingMessage message)
        {
            this.VerifyDomainRecipientsRegistered(message);
        }
        
        /// <summary>
        /// Ensure that domain recipients are KNOWN - i.e. registered with the Config System
        /// If not, remove them.
        /// </summary>
        /// <param name="message"></param>
        void VerifyDomainRecipientsRegistered(IncomingMessage message)
        {            
            message.EnsureRecipientsCategorizedByDomain(this.SecurityAgent.Domains);
            if (!message.HasDomainRecipients)
            {
                throw new AgentException(AgentError.NoRecipients);
            }
            
            DirectAddressCollection recipients = message.DomainRecipients;
            if (this.Settings.MaxIncomingDomainRecipients > 0 && recipients.Count > this.Settings.MaxIncomingDomainRecipients)
            {
                throw new AgentException(AgentError.MaxDomainRecipients);
            }

            if (!m_settings.HasAddressManager)
            {
                // Address validation is turned off
                return;
            }

            Address[] resolved = m_configService.GetAddresses(recipients);
            if (resolved.IsNullOrEmpty())
            {
                throw new AgentException(AgentError.NoDomainRecipients);
            }

            // Remove any addresses that could not be resolved
            // Yes, this is currently n^2, but given the typical # of addresses, cost should be insignificant
            int i = 0;
            while (i < recipients.Count)
            {
                DirectAddress recipient = recipients[i];
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
        
        public MessageEnvelope ProcessIncoming(ISmtpMessage message)
        {
            if (message == null)
            {
                throw new ArgumentNullException("message");
            }
            
            return this.ProcessIncoming(message, message.GetEnvelope());
        }
        
        protected virtual MessageEnvelope ProcessIncoming(ISmtpMessage message, MessageEnvelope envelope)
        {            
            envelope = this.SecurityAgent.ProcessIncoming(envelope);
            Logger.Debug("ProcessedIncoming");

            return envelope;
        }

        void PostProcessIncoming(ISmtpMessage message, IncomingMessage envelope)
        {
            this.CopyMessage(message, m_settings.Incoming);
            
            if (envelope.HasDomainRecipients)
            {
                DirectAddressCollection routedRecipients = new DirectAddressCollection();                
                m_router.Route(message, envelope, routedRecipients); 
                
                this.SendNotifications(envelope, routedRecipients);
            }
            //
            // Any recipients that were handled by routes are no longer in the DomainRecipients collection (removed)
            // Smtp Server should continue process any domain recipients whose delivery were NOT handled by routes
            //
            if (m_settings.Incoming.EnableRelay && envelope.HasDomainRecipients)
            {
                this.SendNotifications(envelope, envelope.DomainRecipients);
                //
                // We only want the incoming message sent to trusted domain recipients
                // We are not allowing arbitrary relay
                //
                message.SetRcptTo(envelope.DomainRecipients);
                m_diagnostics.LogEnvelopeHeaders(message);
            }
            else
            {
                //
                // SMTP Server need not proceed with delivery because we already routed the message to all domain recipients
                //
                message.Abort();
            }
        }

        protected virtual void SendNotifications(IncomingMessage envelope, DirectAddressCollection senders)
        {
            if (!m_settings.InternalMessage.HasPickupFolder || !m_settings.Notifications.AutoResponse)
            {
                return;
            }

            //
            // Its ok if we fail on sending notifications - that should never cause us to not
            // deliver the message
            //
            try
            {
                m_notifications.Send(envelope, m_settings.InternalMessage.PickupFolder, senders);
            }
            catch (Exception ex)
            {
                Logger.Error("While sending notification {0}", ex.ToString());
            }
        }

        //---------------------------------------------------
        //
        //  Message Manipulation
        //
        //---------------------------------------------------
        protected virtual void UpdateXHeaders(MessageEnvelope envelope)
        {
            if (envelope is IncomingMessage && envelope.HasDomainRecipients)
            {
                Message message = envelope.Message;
                //
                // Inject the domain recipients & verified sender from the envelope into the message using an x-receiver + x-sender headers
                // These will be useful after the message is serialized and then deserialized for further processing
                //
                message.Headers.SetValue(XHeaders.Receivers, envelope.DomainRecipients.ToString());
                message.Headers.SetValue(XHeaders.Sender, envelope.Sender.ToString());
            }
        }
        
        protected virtual void UpdateMessageText(ISmtpMessage message, MessageEnvelope envelope)
        {
            string messageText = envelope.SerializeMessage();            
            if (string.IsNullOrEmpty(messageText))
            {
                throw new SmtpAgentException(SmtpAgentError.EmptyResultFromAgent);
            }
            
            message.Update(messageText);
        }
                                        
        protected virtual void AcceptMessage(ISmtpMessage message, bool incoming)
        {
            message.Accept();

            m_auditor.Log(AuditNames.Message.GetAcceptedMessage(incoming), this.BuildAuditLogString(message));
        }
        
        protected virtual void RejectMessage(ISmtpMessage message, bool? isIncoming)
        {
            try
            {
                message.Reject();

                m_auditor.Log(AuditNames.Message.GetRejectedMessage(isIncoming), this.BuildAuditLogString(message));
                
                Logger.Debug("Rejected Message");
                this.CopyMessage(message, m_settings.BadMessage);
            }
            catch
            {
            }
        }
                
        protected virtual void CopyMessage(ISmtpMessage message, MessageProcessingSettings settings)
        {
            if (settings.HasCopyFolder)
            {
                this.CopyMessage(message, settings.CopyFolder);
            }                
        }

        protected virtual void CopyMessage(ISmtpMessage message, string folderPath)
        {
            try
            {
                string uniqueFileName = Extensions.CreateUniqueFileName();
                message.SaveToFile(Path.Combine(folderPath, uniqueFileName));
            }
            catch (Exception ex)
            {
                Logger.Error("While copying message {0}", ex.ToString());
                Logger.Debug(ex);
            }
        }
        
        string BuildAuditLogString(ISmtpMessage message)
        {
            return string.Format("MAILFROM={0};RCPTTO={1}", message.GetMailFrom(), message.GetRcptTo());
        }
    }
}