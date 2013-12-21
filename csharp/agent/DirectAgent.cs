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
using System.Collections.Generic;
using System.Net.Mail;
using System.Security.Cryptography.Pkcs;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Cryptography;
using Health.Direct.Common.Domains;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mime;

namespace Health.Direct.Agent
{
    /// <summary> 
    /// Master client for mail encryption/decryption and signature management. 
    /// </summary> 
    ///  
    /// <example> 
    /// This example demonstrates a typical use of the Agent, using local certificate management for private certificates, 
    /// DNS management for remote certificates, and a local store of trust anchors. 
    /// <code> 
    /// CertificateIndex localcerts = SystemX509Store.OpenPrivate().Index(); 
    /// var dnsresolver = new DnsCertResolver("8.8.8.8"); 
    /// var trustanchors = TrustAnchorResolver.CreateDefault(); 
    /// var ougoingmsg = File.ReadAllText("outgoing.eml"); // plaintext RFC 5322 email message 
    /// var incomingmsg = File.ReadAllText("incoming.eml"); // signed and encrypted S/MIME message 
    /// var agent = DirectAgent("hie.example.com", localcerts, dnsresolver, trustanchors); 
    ///  
    /// IncomingMessage incoming = agent.ProcessIncoming(incomingmsg); 
    /// if (incoming.HasRejectedRecipients) 
    /// { 
    ///     foreach(recipient in incoming.RejectedRecipients) 
    ///     { 
    ///         Console.WriteLine("Rejected {0}", recipient.PostalAddress); 
    ///     } 
    /// } 
    /// OutgoingMessage outgoing = agent.ProcessOutgoing(outgoingmsg); 
    /// if (outgoing.HasRejectedRecipients) 
    /// { 
    ///     foreach(recipient in outgoing.RejectedRecipients) 
    ///     { 
    ///         Console.WriteLine("Rejected {0}", recipient.PostalAddress); 
    ///     } 
    /// } 
    /// </code> 
    /// </example> 
    ///  
    public class DirectAgent
    {
        SMIMECryptographer m_cryptographer;
        ICertificateResolver m_privateCertResolver;
        ICertificateResolver m_publicCertResolver;
        ITrustAnchorResolver m_trustAnchors;
        TrustModel m_trustModel;
        TrustEnforcementStatus m_minTrustRequirement;
        AgentDomains m_managedDomains;         
        //
        // Options
        //
        bool m_encryptionEnabled = true;
        bool m_wrappingEnabled = true;
        bool m_allowNonWrappedIncoming = true;

        /// <summary> 
        /// Creates a DirectAgent instance using local certificate stores and the standard trust and cryptography models. 
        /// </summary> 
        /// <param name="domain"> 
        /// The local domain name managed by this agent. 
        /// </param> 
        public DirectAgent(string domain)
            : this(new StaticDomainResolver(domain), SystemX509Store.OpenPrivate().CreateResolver(), 
                   SystemX509Store.OpenExternal().CreateResolver(),
                   TrustAnchorResolver.CreateDefault())
        {
        }
		
        /// <summary>
        /// Creates a DirectAgent instance, specifying private, external and trust anchor certificate stores, and
        /// and defaulting to the standard trust and cryptography models.
        /// </summary>
        /// <param name="domain">
        /// The local domain name managed by this agent.
        /// </param>
        /// <param name="privateCerts">
        /// An <see cref="ICertificateResolver"/> instance providing private certificates
        /// for senders of outgoing messages and receivers of incoming messages.
        /// </param>
        /// <param name="publicCerts">
        /// An <see cref="ICertificateResolver"/> instance providing public certificates 
        /// for receivers of outgoing messages and senders of incoming messages. 
        /// </param>
        /// <param name="anchors">
        /// An <see cref="ITrustAnchorResolver"/> instance providing trust anchors.
        /// </param>
        public DirectAgent(string domain, ICertificateResolver privateCerts, ICertificateResolver publicCerts, ITrustAnchorResolver anchors)
            : this(new StaticDomainResolver(domain), privateCerts, publicCerts, anchors, TrustModel.Default, SMIMECryptographer.Default)
        {
        }

        /// <summary>
        /// Creates a DirectAgent instance, specifying private, external and trust anchor certificate stores, and
        /// and defaulting to the standard trust and cryptography models.
        /// </summary>
        /// <param name="domainResolver">
        /// An <see cref="IDomainResolver"/> instance providing array of local domain name managed by this agent.
        /// </param>
        /// <param name="privateCerts">
        /// An <see cref="ICertificateResolver"/> instance providing private certificates
        /// for senders of outgoing messages and receivers of incoming messages.
        /// </param>
        /// <param name="publicCerts">
        /// An <see cref="ICertificateResolver"/> instance providing public certificates 
        /// for receivers of outgoing messages and senders of incoming messages. 
        /// </param>
        /// <param name="anchors">
        /// An <see cref="ITrustAnchorResolver"/> instance providing trust anchors.
        /// </param>
        public DirectAgent(IDomainResolver domainResolver, ICertificateResolver privateCerts, ICertificateResolver publicCerts, ITrustAnchorResolver anchors)
            : this(domainResolver, privateCerts, publicCerts, anchors, TrustModel.Default, SMIMECryptographer.Default)
        {
        }

        

        /// <summary>
        /// Creates a DirectAgent instance, specifying private, external and trust anchor certificate stores, and 
        /// trust and cryptography models.
        /// </summary>
        /// <param name="domainResolver">
        /// An <see cref="IDomainResolver"/> instance providing array of local domain name managed by this agent.
        /// </param>
        /// <param name="privateCerts">
        /// An <see cref="ICertificateResolver"/> instance providing private certificates
        /// for senders of outgoing messages and receivers of incoming messages.
        /// </param>
        /// <param name="publicCerts">
        /// An <see cref="ICertificateResolver"/> instance providing public certificates 
        /// for receivers of outgoing messages and senders of incoming messages. 
        /// </param>
        /// <param name="anchors">
        /// An <see cref="ITrustAnchorResolver"/> instance providing trust anchors.
        /// </param>
        /// <param name="trustModel">
        /// An instance or subclass of <see cref="SMIMECryptographer"/> providing a custom trust model.
        /// </param>
        /// <param name="cryptographer">
        /// An instance or subclass of <see cref="Health.Direct.Agent"/> providing a custom cryptography model.
        /// </param>
        public DirectAgent(IDomainResolver domainResolver, ICertificateResolver privateCerts, ICertificateResolver publicCerts, ITrustAnchorResolver anchors, TrustModel trustModel, SMIMECryptographer cryptographer)
        {
            m_managedDomains = new AgentDomains(domainResolver);

            if (privateCerts == null)
            {
                throw new ArgumentNullException("privateCerts");
            }
            if (publicCerts == null)
            {
                throw new ArgumentNullException("publicCerts");
            }
            if (anchors == null)
            {
                throw new ArgumentNullException("anchors");
            }
            if (trustModel == null)
            {
                throw new ArgumentNullException("trustModel");
            }
            if (cryptographer == null)
            {
                throw new ArgumentNullException("cryptographer");
            }

            m_privateCertResolver = privateCerts;
            m_publicCertResolver = publicCerts;
            m_cryptographer = cryptographer;
            m_trustAnchors = anchors;
            m_trustModel = trustModel;
            if (!m_trustModel.CertChainValidator.HasCertificateResolver)
            {
                m_trustModel.CertChainValidator.IssuerResolver = m_publicCertResolver;
            }
            
            m_minTrustRequirement = TrustEnforcementStatus.Success;
        }
        
        /// <summary>
        /// The domainS this agent is managing.
        /// </summary>
        /// <value>An enumeration of string values, each providing a fully qualified domain name.</value>
        public AgentDomains Domains
        {
            get
            {
                return m_managedDomains;
            }
        }

        /// <summary>
        /// Gets the cryptographic model used by this agent.
        /// </summary>
        /// <value>The cryptographic model used by this agent.</value>
        public SMIMECryptographer Cryptographer
        {
            get
            {
                return m_cryptographer;
            }
        }

        /// <summary>
        /// Gets or sets whether this agent uses message encryption.
        /// </summary>
        /// <value><c>true</c> (default) if the agent encrypts, <c>false</c> if the agent does not.</value>
        public bool EncryptMessages
        {
            get
            {
                return m_encryptionEnabled;
            }
            set
            {
                m_encryptionEnabled = value;
            }
        }
        
        /// <summary>
        /// Gets or sets whether this agent wraps the entire message or just the content package. 
        /// </summary>
        /// <value><c>true</c> if the agent wraps the entire message (including headers) prior to encryption; 
        /// <c>false</c> if the agent just signs and encrypts the content package.</value>
        public bool WrapMessages
        {
            get
            {
                return m_wrappingEnabled;
            }
            set
            {
                m_wrappingEnabled = value;
            }
        }
        
        /// <summary>
        /// Should this agent allow incoming messages that are not wrapped <c>message/822</c> MIME entities?
        /// </summary>
        public bool AllowNonWrappedIncoming
        {
            get
            {
                return m_allowNonWrappedIncoming;
            }
            set
            {
                m_allowNonWrappedIncoming = value;
            }
        }

        /// <summary> 
        /// Gets the public certificate resolver (set in the constructor).  
        /// </summary> 
        /// <value> 
        /// The <see cref="ICertificateResolver"/> instance used for resolving public certificates. 
        /// </value> 
        public ICertificateResolver PublicCertResolver
        {
            get
            {
                return m_publicCertResolver;
            }
        }

        /// <summary> 
        /// Gets the private certificate resolver (set in the constructor).  
        /// </summary> 
        /// <value> 
        /// The <see cref="ICertificateResolver"/> instance used for resolving private certificates. 
        /// </value> 
        public ICertificateResolver PrivateCertResolver
        {
            get
            {
                return m_privateCertResolver;
            }
        }
        /// <summary> 
        /// Getst the trust anchor resolver (set in the constructor).  
        /// </summary> 
        /// <value> 
        /// The <see cref="ITrustAnchorResolver"/> instance used for resolving trust anchors. 
        /// </value>
        public ITrustAnchorResolver TrustAnchors
        {
            get
            {
                return m_trustAnchors;
            }
        }

        /// <summary>
        /// Messages must satisfy this minimum trust status
        /// </summary>
        public TrustEnforcementStatus MinTrustRequirement
        {
            get
            {
                return m_minTrustRequirement;
            }
            set
            {
                if (value < TrustEnforcementStatus.Success)
                {
                    throw new ArgumentException("value has a non-successful status", "value");
                }
                m_minTrustRequirement = value;
            }
        }
        
        /// <summary>
        /// Returns the currently configured trust model 
        /// </summary>
        public TrustModel TrustModel
        {
            get
            {
                return m_trustModel;
            }
        }
        
        //
        // You can participate in the agent pipeline by subscribing to these events
        // You can choose to do FURTHER post-processing on the message:
        //   - adding headers
        //   - throwing exceptions
        // If you throw an exception, message processing is ABORTED
        //
        /// <summary> 
        /// Subscribe to this event for notification when the Agent raises an exception.  
        /// </summary> 
        public event Action<DirectAgent, Exception> Error;
        /// <summary> 
        /// Subscribe to this event for pre-processing of the <see cref="IncomingMessage"/>, including adding or modifying headers. 
        /// Throwing an exception pre-process will abort message processing. 
        /// </summary> 
        public event Action<IncomingMessage> PreProcessIncoming;
        /// <summary> 
        /// Subscribe to this event for post-processing of the <see cref="IncomingMessage"/>, including adding or modifying headers. 
        /// Throwing an exception post-process will abort message processing. 
        /// </summary> 
        public event Action<IncomingMessage> PostProcessIncoming;
        /// <summary> 
        /// Subscribe to this event for notification when <see cref="IncomingMessage"/> processing raises an exception. 
        /// </summary> 
        public event Action<IncomingMessage, Exception> ErrorIncoming;
        /// <summary> 
        /// Subscribe to this event for pre-processing of the <see cref="OutgoingMessage"/>, including adding or modifying headers. 
        /// Throwing an exception pre-process will abort message processing. 
        /// </summary> 
        public event Action<OutgoingMessage> PreProcessOutgoing;
        /// <summary> 
        /// Subscribe to this event for post-processing of the <see cref="OutgoingMessage"/>, including adding or modifying headers. 
        /// Throwing an exception post-process will abort message processing. 
        /// </summary> 
        public event Action<OutgoingMessage> PostProcessOutgoing;
        /// <summary> 
        /// Subscribe to this event for notification when <see cref="OutgoingMessage"/> processing raises an exception. 
        /// </summary> 
        public event Action<OutgoingMessage, Exception> ErrorOutgoing;

        //-------------------------------------------------------------------
        //
        // INCOMING MESSAGE
        //
        //-------------------------------------------------------------------

        /// <summary> 
        /// Decrypts and verifies trust in signed and encrypted RFC 5322 formatted message 
        /// </summary> 
        /// <param name="messageText"> 
        /// An RFC 5322 formatted message string 
        /// </param> 
        /// <returns> 
        /// An <see cref="IncomingMessage"/> instance containing the decrypted message. 
        /// </returns> 
        public IncomingMessage ProcessIncoming(string messageText)
        {
            if (string.IsNullOrEmpty(messageText))
            {
                throw new ArgumentException("value was null or empty", "messageText");
            }

            return this.ProcessIncoming(new IncomingMessage(messageText));
        }

        /// <summary> 
        /// Decrypts and verifies trust in a signed and encrypted RFC 5322 formatted message, providing a sender and recipient addresses. 
        /// The provided sender and recipient addresses will be used instead of the header information in the <c>messageText</c>. 
        /// </summary> 
        /// <param name="messageText"> 
        /// An RFC 5322 formatted message string. 
        /// </param> 
        /// <param name="recipients"> 
        /// A <see cref="DirectAddressCollection"/> instance representing recipient addresses. 
        /// </param> 
        /// <param name="sender"> 
        /// An <see cref="DirectAddress"/> instance representing the sender address 
        /// </param> 
        /// <returns> 
        /// An <see cref="IncomingMessage"/> instance with the trust verified decrypted and verified message. 
        /// </returns> 
        public IncomingMessage ProcessIncoming(string messageText, DirectAddressCollection recipients, DirectAddress sender)
        {
            IncomingMessage message = new IncomingMessage(messageText, recipients, sender);
            return this.ProcessIncoming(message);                    
        }

        /// <summary>
        /// Decrypts and verifies trust in a MessageEnvelope instance with signed and encrypted message content.
        /// </summary>
        /// <param name="envelope">
        /// A <see cref="MessageEnvelope"/> instance with signed and encrypted content for decryption and trust verification.
        /// </param>
        /// <returns>
        /// An <see cref="IncomingMessage"/> instance with the trust verified decrypted and verified message. 
        /// </returns>
        public IncomingMessage ProcessIncoming(MessageEnvelope envelope)
        {
            if (envelope == null)
            {
                throw new ArgumentNullException("envelope");
            }
            
            return this.ProcessIncoming(new IncomingMessage(envelope));
        }
        
        /// <summary>
        /// Decrypts and verifies trust in an IncomingMessage instance with signed and encrypted message content.
        /// </summary>
        /// <param name="message">
        /// A <see cref="IncomingMessage"/> instance with signed and encrypted content for decryption and trust verification.
        /// </param>
        /// <returns>
        /// An <see cref="IncomingMessage"/> instance with the trust verified decrypted and verified message. 
        /// </returns>
        public IncomingMessage ProcessIncoming(IncomingMessage message)
        {
            if (message == null)
            {
                throw new ArgumentNullException("message");
            }

            try
            {
                message.Validate();

                this.Notify(message, this.PreProcessIncoming);

                this.ProcessMessage(message);

                this.Notify(message, this.PostProcessIncoming);
            }
            catch (Exception error)
            {
                this.Notify(message, error);
                throw;  // rethrow error
            }
            
            return message;
        }

        void ProcessMessage(IncomingMessage message)
        {
            if (message.Sender == null)
            {
                throw new AgentException(AgentError.NoSender);
            }            
            if (!message.HasRecipients)
            {
                throw new AgentException(AgentError.NoRecipients);
            }            
            message.EnsureRecipientsCategorizedByDomain(m_managedDomains);
            if (!message.HasDomainRecipients)
            {
                throw new AgentException(AgentError.NoDomainRecipients);
            }            
            //
            // Map each address to its certificates/trust settings
            //
            this.BindAddresses(message);
            //
            // Decrypt the message, extract the signature and original content
            //
            this.DecryptSignedContent(message);
            //
            // The standard requires that the original message be wrapped to protect headers
            //
            message.Message = this.UnwrapMessage(message.Message);
            this.ValidateRoutingHeaders(message);
            //
            // Enforce trust requirements, including checking signatures
            //
            m_trustModel.Enforce(message);
            //
            // Remove any untrusted recipients...
            //
            if (message.HasDomainRecipients)
            {
                message.CategorizeRecipientsByTrust(m_minTrustRequirement);
            }
            if (!message.HasDomainRecipients)
            {
                throw new AgentException(AgentError.NoTrustedRecipients);
            }
            //
            // Some recipients may not trust this message. Remove them from the To list to prevent accidental message delivery
            //
            message.UpdateRoutingHeaders();
        }

        void BindAddresses(IncomingMessage message)
        {
            //
            // Bind each recpient's certs and trust settings
            //
            DirectAddressCollection recipients = message.DomainRecipients;
            for (int i = 0, count = recipients.Count; i < count; ++i)
            {
                DirectAddress recipient = recipients[i];
                recipient.Certificates = this.ResolvePrivateCerts(recipient, m_encryptionEnabled);
                recipient.TrustAnchors = m_trustAnchors.IncomingAnchors.GetCertificates(recipient);
            }
        }

        void DecryptSignedContent(IncomingMessage message)
        {
            SignedCms signatures = null;
            MimeEntity payload = null;
            bool success = false;
            
            
            if (m_encryptionEnabled)
            {
                //
                // This can be optimized for multiple private keys and recipients where the same certs
                // are shared across recipients (org certs). But we will start with the easy to understand simple version
                //            
                // Decrypt and parse message body into a signature entity - the envelope that contains our data + signature
                // If we fail to decrypt for any recipient, we are going to treat the message as possibly compromised and reject
                // it entirely
                //
                foreach(DirectAddress recipient in message.DomainRecipients)
                {
                    success = false;
                    signatures = null;
                    payload = null;                    
                    success = this.DecryptSignedContent(message, recipient, out signatures, out payload);
                    if (!success)
                    {
                        // Any failures.. stop immediately. If we could not decrypt the message for any recipient,
                        // then the message is suspicious and is rejected
                        break;
                    }
                }
            }
            else
            {
                success = this.DecryptSignatures(message, null, out signatures, out payload);
            }            
            if (!success)
            {
                throw new AgentException(AgentError.InvalidEncryption);
            }
            
            if (signatures == null || payload == null)
            {
                throw new AgentException(AgentError.UntrustedMessage);
            }
            
            message.Signatures = signatures;
            //
            // Alter body to contain actual content. Also clean up mime headers on the message that were there to support
            // signatures etc
            //
            HeaderCollection headers = message.Message.Headers;
            message.Message.Headers = headers.SelectNonMimeHeaders();
            message.Message.UpdateBody(payload); // this will merge in content + content specific mime headers
        }

        bool DecryptSignedContent(IncomingMessage message, DirectAddress recipient, out SignedCms signatures, out MimeEntity payload)
        {
            signatures = null;
            payload = null;
            foreach (X509Certificate2 cert in recipient.Certificates)
            {
                try
                {
                    if (this.DecryptSignatures(message, cert, out signatures, out payload))
                    {
                        // Decrypted and extracted signatures successfully
                        return true;
                    }
                }
                catch(Exception ex)
                {
                    this.Notify(message, ex);
                }
            }
            
            return false;
        }
                
        /// <summary>
        /// Decrypt (optionally) the given message and try to extract signatures
        /// </summary>
        bool DecryptSignatures(IncomingMessage message, X509Certificate2 certificate, out SignedCms signatures, out MimeEntity payload)
        {
            MimeEntity decryptedEntity = null;
            signatures = null;
            payload = null;
            
            if (certificate != null)
            {
                decryptedEntity = m_cryptographer.DecryptEntity(message.GetEncryptedBytes(m_cryptographer), certificate);
            }
            else
            {
                decryptedEntity = message.Message;
            }
            if (decryptedEntity == null)
            {
                return false;
            }

            if (SMIMEStandard.IsContentEnvelopedSignature(decryptedEntity.ParsedContentType))
            {
                signatures = m_cryptographer.DeserializeEnvelopedSignature(decryptedEntity);
                payload = MimeSerializer.Default.Deserialize<MimeEntity>(signatures.ContentInfo.Content);
            }
            else if (SMIMEStandard.IsContentMultipartSignature(decryptedEntity.ParsedContentType))
            {
                SignedEntity signedEntity = SignedEntity.Load(decryptedEntity);
                signatures = m_cryptographer.DeserializeDetachedSignature(signedEntity);
                payload = signedEntity.Content;
            }
            else
            {
                throw new AgentException(AgentError.UnsignedMessage);
            }
            
            return true;
        }
        
        void ValidateRoutingHeaders(IncomingMessage message)
        {
            if (!message.AreAddressesInRoutingHeaders(message.DomainRecipients))
            {
                throw new AgentException(AgentError.RecipientMismatch);
            }
        }
        
        //-------------------------------------------------------------------
        //
        // OUTGOING MESSAGE
        //
        //------------------------------------------------------------------- 
		
        /// <summary> 
        /// Encrypts, verifies recipient trust, and signs an RFC 5322 formatted message 
        /// </summary> 
        /// <param name="messageText"> 
        /// An RFC 5322 formatted message string 
        /// </param> 
        /// <returns> 
        /// An <see cref="OutgoingMessage"/> instance containing the encrypted and trust verified message. 
        /// </returns> 
        public OutgoingMessage ProcessOutgoing(string messageText)
        {
            if (string.IsNullOrEmpty(messageText))
            {
                throw new ArgumentException("value was null or empty", "messageText");
            }

            OutgoingMessage message = new OutgoingMessage(this.WrapMessage(messageText));
            
            return this.ProcessOutgoing(message);
        }
        
        /// <summary>
        /// Encrypts, verifies recipient trust, and signs an RFC 5322 formatted message 
        /// The provided sender and recipient addresses will be used instead of the header information in the <c>messageText</c>. 
        /// </summary>
        /// <param name="messageText">
        /// An RFC 5322 formatted message string 
        /// </param>
        /// <param name="recipients">
        /// An <see cref="DirectAddressCollection"/> instance specifying message recipients.
        /// </param>
        /// <param name="sender">
        /// An <see cref="DirectAddress"/> instance specifying message sender
        /// </param>
        /// <returns>
        /// An <see cref="OutgoingMessage"/> instance containing the encrypted and trust verified message.
        /// </returns>
        public OutgoingMessage ProcessOutgoing(string messageText, DirectAddressCollection recipients, DirectAddress sender)
        {
            OutgoingMessage message = new OutgoingMessage(this.WrapMessage(messageText), recipients, sender);            
            return this.ProcessOutgoing(message);            
        }

        /// <summary>
        /// Encrypts, verifies recipient trust, and signs a MessageEnvelope containing a message to prepare for send.
        /// </summary>
        /// <param name="envelope">
        /// A <see cref="MessageEnvelope"/> instance containing the message to prepare for send.
        /// </param>
        /// <returns>
        /// An <see cref="OutgoingMessage"/> instance containing the encrypted and trust verified message.
        /// </returns>
        public OutgoingMessage ProcessOutgoing(MessageEnvelope envelope)
        {
            if (envelope == null)
            {
                throw new ArgumentNullException("envelope");
            }
            
            OutgoingMessage message = new OutgoingMessage(envelope);
            return this.ProcessOutgoing(message);
        }
        
        /// <summary>
        /// Encrypts, verifies recipient trust, and signs an OutgoingMessage containing a message to prepare for send.
        /// </summary>
        /// <param name="message">
        /// An <see cref="OutgoingMessage"/> instance containing the message to prepare for send.
        /// </param>
        /// <returns>
        /// An <see cref="OutgoingMessage"/> instance containing the encrypted and trust verified message.
        /// </returns>
        public OutgoingMessage ProcessOutgoing(OutgoingMessage message)
        {
            if (message == null)
            {
                throw new ArgumentNullException("message");
            }

            try
            {
                message.Validate();

                this.Notify(message, this.PreProcessOutgoing);

                this.ProcessMessage(message);

                this.Notify(message, this.PostProcessOutgoing);
            }
            catch (Exception error)
            {
                this.Notify(message, error);
                throw;
            }
            
            return message;
        }

        void ProcessMessage(OutgoingMessage message)
        {
            if (!WrappedMessage.IsWrapped(message.Message))
            {
                message.Message = message.HasRawMessage ? this.WrapMessage(message.RawMessage) : this.WrapMessage(message.Message);
            }
            
            if (message.Sender == null)
            {
                throw new OutgoingAgentException(AgentError.NoSender);
            }            
            if (!message.HasRecipients)
            {
                throw new OutgoingAgentException(AgentError.NoRecipients);
            }
            //
            // Ensure we support this sender's domain
            //
            if (!m_managedDomains.IsManaged(message.Sender))
            {
                throw new OutgoingAgentException(AgentError.UntrustedSender);
            }
            //
            // Categorize recipients as local/external
            //
            message.EnsureRecipientsCategorizedByDomain(m_managedDomains);
            //
            // Bind addresses to Certs etc
            //
            this.BindAddresses(message);
            if (!message.HasRecipients)
            {
                throw new OutgoingAgentException(AgentError.MissingTo);
            }            
            //
            // Enforce the trust model.
            //
            m_trustModel.Enforce(message);
            //
            // Remove any non-trusted recipients
            //
            message.CategorizeRecipientsByTrust(m_minTrustRequirement);
            if (!message.HasRecipients)
            {
                throw new OutgoingAgentException(AgentError.NoTrustedRecipients);
            }
            //
            // And update routing headers to remove any recipients we had yanked
            //
            message.UpdateRoutingHeaders();
            //
            // Finally, sign and encrypt the message
            //
            this.SignAndEncryptMessage(message);
        }

        void BindAddresses(OutgoingMessage message)
        {
            //
            // Retrieving the sender's private certificate is requied for encryption
            //
            if (message.UseIncomingTrustAnchors)
            {
                message.Sender.TrustAnchors = m_trustAnchors.IncomingAnchors.GetCertificates(message.Sender);
            }
            else
            {
                message.Sender.TrustAnchors = m_trustAnchors.OutgoingAnchors.GetCertificates(message.Sender);
            }
            message.Sender.Certificates = this.ResolvePrivateCerts(message.Sender, true);
            //
            // Bind each recipient's certs
            //
            DirectAddressCollection recipients = message.Recipients;
            for (int i = 0, count = recipients.Count; i < count; ++i)
            {
                DirectAddress recipient = recipients[i];
                X509Certificate2Collection certificates = this.ResolvePublicCerts(recipient, false);
                recipient.Certificates = certificates;
                if(certificates != null)
                {
                    recipient.ResolvedCertificates = true;
                }
            }
        }
        
        Message WrapMessage(string messageText)
        {
            if (!m_wrappingEnabled)
            {
                return MimeSerializer.Default.Deserialize<Message>(messageText);
            }
            
            return WrappedMessage.Create(messageText, DirectStandard.MailHeadersUsed);            
        }

        Message WrapMessage(Message message)
        {
            if (!m_wrappingEnabled)
            {
                return message;
            }
            
            if (WrappedMessage.IsWrapped(message))
            {
                return message;
            }
            
            return WrappedMessage.Create(message, DirectStandard.MailHeadersUsed);
        }
        
        Message UnwrapMessage(Message message)
        {
            if (!m_wrappingEnabled)
            {
                return message;
            }
            
            if (m_allowNonWrappedIncoming && !WrappedMessage.IsWrapped(message))
            {
                return message;
            }
            
            return WrappedMessage.ExtractInner(message);
        }
        //
        // First sign, THEN encrypt the message
        //
        void SignAndEncryptMessage(OutgoingMessage message)
        {
            SignedEntity signedEntity = m_cryptographer.Sign(message.Message, message.Sender.Certificates);

            if (m_encryptionEnabled)
            {
                //
                // Encrypt the outbound message with all known trusted certs
                //
                MimeEntity encryptedEntity = m_cryptographer.Encrypt(signedEntity, message.Recipients.GetCertificates());
                //
                // Alter message content to contain encrypted data
                //
                message.Message.UpdateBody(encryptedEntity);
            }
            else
            {
                message.Message.UpdateBody(signedEntity);
            }
        }

        X509Certificate2Collection ResolvePrivateCerts(MailAddress address, bool required)
        {
            X509Certificate2Collection certs = null;
            try
            {
                certs = m_privateCertResolver.GetCertificates(address);
                if (required && certs.IsNullOrEmpty())
                {
                    throw new AgentException(AgentError.CouldNotResolvePrivateKey, address.Address);
                }
            }
            catch (Exception ex)
            {
                if (required)
                {
                    throw;
                }
                this.Notify(ex); // for logging, tracking etc...
            }

            return certs;
        }

        X509Certificate2Collection ResolvePublicCerts(MailAddress address, bool required)
        {
            X509Certificate2Collection cert = null;
            try
            {
                cert = m_publicCertResolver.GetCertificates(address);
                if (cert == null && required)
                {
                    throw new AgentException(AgentError.CouldNotResolvePublicCert, address.Address);
                }
            }
            catch (Exception ex)
            {
                if (required)
                {
                    throw;
                }
                this.Notify(ex); // for logging, tracking etc...
            }

            return cert;
        }
        
        //-----------------------------
        //
        // Events
        //
        //-----------------------------                
        void Notify(IncomingMessage message, Action<IncomingMessage> eventHandler)
        {
            //
            // exceptions are interpreted as: abort message
            //
            if (eventHandler != null)
            {
                eventHandler(message);
            }
        }

        void Notify(OutgoingMessage message, Action<OutgoingMessage> eventHandler)
        {
            //
            // exceptions are interpreted as: abort message
            //
            if (eventHandler != null)
            {
                eventHandler(message);
            }
        }

        void Notify(IncomingMessage message, Exception ex)
        {
            try
            {
                Action<IncomingMessage, Exception> errorIncoming = ErrorIncoming;
                if (errorIncoming != null)
                {
                    errorIncoming(message, ex);
                }
            }
            catch
            {
            }
        }

        void Notify(OutgoingMessage message, Exception ex)
        {
            try
            {
                Action<OutgoingMessage, Exception> errorOutgoing = ErrorOutgoing;
                if (errorOutgoing != null)
                {
                    errorOutgoing(message, ex);
                }
            }
            catch
            {
            }
        }

        void Notify(Exception ex)
        {
            try
            {
                var error = this.Error;
                if (error != null)
                {
                    error(this, ex);
                }
            }
            catch
            {
            }
        }
    }
}