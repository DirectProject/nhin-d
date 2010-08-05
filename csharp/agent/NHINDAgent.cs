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
using System.Net.Mail;
using System.Security.Cryptography.Pkcs;
using System.Security.Cryptography.X509Certificates;
using NHINDirect.Mail;
using NHINDirect.Mime;
using NHINDirect.Certificates;
using NHINDirect.Cryptography;

namespace NHINDirect.Agent
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
	/// var agent = NHINDAgent("hie.example.com", localcerts, dnsresolver, trustanchors);
	/// 
	/// IncomingMessage incoming = agent.ProcessIncoming(incomingmsg);
	/// if (incoming.HasRejectedRecipients)
	/// {
	///     foreach(recipient in incoming.RejectedRecipients)
	///     {
	///         Console.WriteLine("Rejected {0}", recipient.Address);
	///     }
	/// }
	/// OutgoingMessage outgoing = agent.ProcessOutgoing(outgoingmsg);
	/// if (outgoing.HasRejectedRecipients)
	/// {
	///     foreach(recipient in outgoing.RejectedRecipeints)
	///     {
	///         Console.WriteLine("Rejected {0}", recipient.Address);
	///     }
	/// }
	/// </code>
	/// </example>
	/// 
    public class NHINDAgent
    {
        SMIMECryptographer m_cryptographer;
        ICertificateResolver m_privateCertResolver;
        ICertificateResolver m_publicCertResolver;
        ITrustAnchorResolver m_trustAnchors;
        TrustModel m_trustModel;
        TrustEnforcementStatus m_minTrustRequirement;
        string m_domain;
        //
        // Options
        //
        bool m_encryptionEnabled = true;
        bool m_wrappingEnabled = true;
        bool m_allowNonWrappedIncoming = true;
        
		/// <summary>
		/// Creates an NHINDAgent instance using local certificate stores and the standard trust and cryptography models.
		/// </summary>
		/// <param name="domain">
		/// The local domain name managed by this agent.
		/// </param>
        public NHINDAgent(string domain)
            : this(domain, SystemX509Store.OpenPrivate().Index(), 
                           SystemX509Store.OpenExternal().Index(),
                           TrustAnchorResolver.CreateDefault())
        {
        }
		
		/// <summary>
		/// Creates an NHINDAgent instance, specifying private, external and trust anchor certificate stores, and
		/// and defaulting to the standard trust and cryptography models.
		/// </summary>
		/// <param name="domain">
		/// The local domain name managed by this agent.
		/// </param>
		/// <param name="privateCerts">
		/// An <see cref="NHINDirect.Certificates.ICertificateResolver"/> instance providing private certificates
		/// for senders of outgoing messages and receivers of incoming messages.
		/// </param>
		/// <param name="publicCerts">
		/// An <see cref="NHINDirect.Certificates.ICertificateResolver"/> instance providing public certificates 
		/// for receivers of outgoing messages and senders of incoming messages. 
		/// </param>
		/// <param name="anchors">
		/// An <see cref="NHINDirect.Certificates.ITrustAnchorResolver"/> instance providing trust anchors.
		/// </param>
        public NHINDAgent(string domain, ICertificateResolver privateCerts, ICertificateResolver publicCerts, ITrustAnchorResolver anchors)
            : this(domain, privateCerts, publicCerts, anchors, TrustModel.Default, SMIMECryptographer.Default)
        {
        }

		/// <summary>
		/// Creates an NHINDAgent instance, specifying private, external and trust anchor certificate stores, and 
		/// trust and cryptography models.
		/// </summary>
		/// <param name="domain">
		/// The local domain name managed by this agent.
		/// </param>
		/// <param name="privateCerts">
		/// An <see cref="NHINDirect.Certificates.ICertificateResolver"/> instance providing private certificates
		/// for senders of outgoing messages and receivers of incoming messages.
		/// </param>
		/// <param name="publicCerts">
		/// An <see cref="NHINDirect.Certificates.ICertificateResolver"/> instance providing public certificates 
		/// for receivers of outgoing messages and senders of incoming messages. 
		/// </param>
		/// <param name="anchors">
		/// An <see cref="NHINDirect.Certificates.ITrustAnchorResolver"/> instance providing trust anchors.
		/// </param>
		/// <param name="trustModel">
		/// An instance or subclass of <see cref="NHINDirect.Agent.TrustModel"/> providing a custom trust model.
		/// </param>
		/// <param name="cryptographer">
		/// An instance or subclass of <see cref="NHINDirect.Cryptography.SMIMECryptographer"/> providing a custom cryptography model.
		/// </param>
        public NHINDAgent(string domain, ICertificateResolver privateCerts, ICertificateResolver publicCerts, ITrustAnchorResolver anchors, TrustModel trustModel, SMIMECryptographer cryptographer)
        {
            if (string.IsNullOrEmpty(domain))
            {
                throw new ArgumentException("domain");
            }
            
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
            
            this.m_domain = domain;
            this.m_privateCertResolver = privateCerts;
            this.m_publicCertResolver = publicCerts;
            this.m_cryptographer = cryptographer;
            this.m_trustAnchors = anchors;
            this.m_trustModel = trustModel;
            this.m_minTrustRequirement = TrustEnforcementStatus.Success_Offline;
        }

		/// <summary>
		/// The domain this agent is managing.
		/// </summary>
		/// <value>A string value providing a fully qualified domain name.</value>
        public string Domain
        {
            get
            {
                return this.m_domain;
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
                return this.m_cryptographer;
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
                return this.m_encryptionEnabled;
            }
            set
            {
                this.m_encryptionEnabled = value;
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
                return this.m_wrappingEnabled;
            }
            set
            {
                this.m_wrappingEnabled = value;
            }
        }
        
		/// <summary>
		/// Gets or sets whether this agent allows non-fully wrapped incoming messages
		/// (e.g., where the content-type of the incoming message is not <c>message/rfc822</c> 
		/// </summary>
		/// <value><c>true</c> if this agent allows non-wrapped incoming messages; <c>false<c> if only wrapped messages are allowed</value>
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
		/// The <see cref="NHINDirect.Certificates.ICertificateResolver" instance used for resolving public certificates.
		/// </value>
        public ICertificateResolver PublicCertResolver
        {
            get
            {
                return this.m_publicCertResolver;
            }
        }

		/// <summary>
		/// Gets the private certificate resolver (set in the constructor). 
		/// </summary>
		/// <value>
		/// The <see cref="NHINDirect.Certificates.ICertificateResolver" instance used for resolving private certificates.
		/// </value>
        public ICertificateResolver PrivateCertResolver
        {
            get
            {
                return this.m_privateCertResolver;
            }
        }

		/// <summary>
		/// Getst the trust anchor resolver (set in the constructor). 
		/// </summary>
		/// <value>
		/// The <see cref="NHINDirect.Certificates.ITrustAnchorResolver" instance used for resolving trust anchors.
		/// </value>		
        public ITrustAnchorResolver TrustAnchors
        {
            get
            {
                return this.m_trustAnchors;
            }
        }

        /// <summary>
        /// Messages must satisfy this minimum trust status
        /// </summary>
        public TrustEnforcementStatus MinTrustRequirement
        {
            get
            {
                return this.m_minTrustRequirement;
            }
            set
            {
                if (value < TrustEnforcementStatus.Success_Offline)
                {
                    throw new ArgumentException();
                }
                this.m_minTrustRequirement = value;
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
        public event Action<NHINDAgent, Exception> Error;
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
                		
		/// <summary>
		/// Generic message processing, autodetecting if message is incoming or outgoing
		/// </summary>
		/// <param name="messageText">
		/// RFC 5322 formatted message string to process
		/// </param>
		/// <param name="isIncoming">
		/// Reference boolean, will be set <c>true</c> if the message was detected as incoming,
		/// <c>false</c> if the message was detected as outgoing.
		/// </param>
		/// <returns>
		/// A <see cref="MessageEnvelope"/> instance; may be cast to <see cref="IncomingMessage"/> or
		/// <see cref="OutgoingMessage"/> based on the value of <c>isIncoming</c>.
		/// </returns>
        public MessageEnvelope Process(string messageText, ref bool isIncoming)
        {
            return this.Process(new MessageEnvelope(messageText), ref isIncoming);
        }
        
        public MessageEnvelope Process(string messageText, NHINDAddressCollection recipients, NHINDAddress sender, ref bool isIncoming)
        {
            return this.Process(new MessageEnvelope(messageText, recipients, sender), ref isIncoming);
        }

        public MessageEnvelope Process(MessageEnvelope envelope, ref bool isIncoming)
        {
            if (envelope == null)
            {
                throw new ArgumentNullException();
            }
            
            this.CheckEnvelopeAddresses(envelope);

            if (SMIMEStandard.IsEncrypted(envelope.Message))
            {
                isIncoming = true;
                IncomingMessage incoming = new IncomingMessage(envelope);
                envelope.Clear();
                
                this.ProcessIncoming(incoming);
                return incoming;
            }

            isIncoming = false;
            OutgoingMessage outgoing = new OutgoingMessage(envelope);
            envelope.Clear();
            
            this.ProcessOutgoing(outgoing);
            return outgoing;
        }
                              
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
                throw new ArgumentException();
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
		/// A <see cref="NHINDAddressCollection"/> instance representing recipient addresses.
		/// </param>
		/// <param name="sender">
		/// An <see cref="NHINDAddress"/> instance representing the sender address
		/// </param>
		/// <returns>
		/// An <see cref="IncomingMessage"/> instance with the trust verified decrypted and verified message.
		/// </returns>
        public IncomingMessage ProcessIncoming(string messageText, NHINDAddressCollection recipients, NHINDAddress sender)
        {
            this.CheckEnvelopeAddresses(recipients, sender);
            
            IncomingMessage message = new IncomingMessage(messageText, recipients, sender);
            return this.ProcessIncoming(message);                    
        }

        public IncomingMessage ProcessIncoming(MessageEnvelope envelope)
        {
            if (envelope == null)
            {
                throw new ArgumentNullException();
            }
            
            this.CheckEnvelopeAddresses(envelope);
            return this.ProcessIncoming(new IncomingMessage(envelope));
        }
        
        public IncomingMessage ProcessIncoming(IncomingMessage message)
        {
            if (message == null)
            {
                throw new ArgumentException();
            }

            message.Agent = this;
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
                throw new AgentException(AgentError.UntrustedSender);
            }

            message.CategorizeRecipients(this.Domain);
            if (!message.HasDomainRecipients)
            {
                throw new AgentException(AgentError.NoTrustedRecipients);
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
            //
            // Enforce trust requirements, including checking signatures
            //
            this.m_trustModel.Enforce(message);
            //
            // Remove any untrusted recipients...
            //
            if (message.HasDomainRecipients)
            {
                message.CategorizeRecipients(this.m_minTrustRequirement);
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
            NHINDAddressCollection recipients = message.DomainRecipients;
            for (int i = 0, count = recipients.Count; i < count; ++i)
            {
                NHINDAddress recipient = recipients[i];
                recipient.Certificates = this.ResolvePrivateCerts(recipient, false);
                recipient.TrustAnchors = this.m_trustAnchors.IncomingAnchors.GetCertificates(recipient);
            }
        }

        void DecryptSignedContent(IncomingMessage message)
        {   
            MimeEntity decryptedEntity = this.DecryptMessage(message);
            SignedCms signatures;
            MimeEntity payload;
            
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
            
            message.Signatures = signatures;
            //
            // Alter body to contain actual content. Also clean up mime headers on the message that were there to support
            // signatures etc
            //
            HeaderCollection headers = message.Message.Headers;
            message.Message.Headers = headers.SelectNonMimeHeaders();
            message.Message.ApplyBody(payload); // this will merge in content + content specific mime headers
        }

        MimeEntity DecryptMessage(IncomingMessage message)
        {
            MimeEntity decryptedEntity = null;
            if (this.m_encryptionEnabled)
            {
                //
                // Yes, this can be optimized heavily for multiple certs. 
                // But we will start with the easy to understand simple version
                //            
                // Decrypt and parse message body into a signature entity - the envelope that contains our data + signature
                // We can use the cert of any ONE of the recipients to decrypt
                // So basically, we'll try until we find one, or we just run out...
                //
                foreach (X509Certificate2 cert in message.DomainRecipients.Certificates)
                {
                    try
                    {
                        decryptedEntity = this.m_cryptographer.Decrypt(message.Message, cert);
                        break;
                    }
                    catch
                    {
                    }
                }
            }
            else
            {
                decryptedEntity = message.Message;
            }
            
            if (decryptedEntity == null)
            {
                throw new AgentException(AgentError.UntrustedMessage);
            }

            return decryptedEntity;
        }

        //-------------------------------------------------------------------
        //
        // OUTGOING MESSAGE
        //
        //-------------------------------------------------------------------        
        public OutgoingMessage ProcessOutgoing(string messageText)
        {
            if (string.IsNullOrEmpty(messageText))
            {
                throw new ArgumentException();
            }

            OutgoingMessage message = new OutgoingMessage(this.WrapMessage(messageText));
            
            return this.ProcessOutgoing(message);
        }
        
        public OutgoingMessage ProcessOutgoing(string messageText, NHINDAddressCollection recipients, NHINDAddress sender)
        {
            this.CheckEnvelopeAddresses(recipients, sender);

            OutgoingMessage message = new OutgoingMessage(this.WrapMessage(messageText), recipients, sender);            
            return this.ProcessOutgoing(message);            
        }

        public OutgoingMessage ProcessOutgoing(MessageEnvelope envelope)
        {
            if (envelope == null)
            {
                throw new ArgumentNullException();
            }
            
            this.CheckEnvelopeAddresses(envelope);

            OutgoingMessage message = new OutgoingMessage(envelope);
            return this.ProcessOutgoing(message);
        }
        
        public OutgoingMessage ProcessOutgoing(OutgoingMessage message)
        {
            if (message == null)
            {
                throw new ArgumentException();
            }

            message.Agent = this;            
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
                throw new AgentException(AgentError.MissingFrom);
            }
            
            this.BindAddresses(message);
            if (!message.HasRecipients)
            {
                throw new AgentException(AgentError.MissingTo);
            }            
            message.CategorizeRecipients(m_domain);
            //
            // Enforce the trust model.
            //
            this.m_trustModel.Enforce(message);
            //
            // Remove any non-trusted recipients
            //
            message.CategorizeRecipients(this.m_minTrustRequirement);
            if (!message.HasRecipients)
            {
                throw new AgentException(AgentError.NoTrustedRecipients);
            }
            //
            // Finally, sign and encrypt the message
            //
            this.SignAndEncryptMessage(message);
            //
            // Not all recipients may be trusted. Remove them from Routing headers
            //
            message.UpdateRoutingHeaders();
        }

        void BindAddresses(OutgoingMessage message)
        {
            //
            // Retrieving the sender's private certificate is requied for encryption
            //
            message.Sender.TrustAnchors = this.m_trustAnchors.OutgoingAnchors.GetCertificates(message.Sender);
            message.Sender.Certificates = this.ResolvePrivateCerts(message.Sender, true);
            //
            // Bind each recipient's certs
            //
            NHINDAddressCollection recipients = message.Recipients;
            for (int i = 0, count = recipients.Count; i < count; ++i)
            {
                NHINDAddress recipient = recipients[i];
                recipient.Certificates = this.ResolvePublicCerts(recipient, false);
            }
        }
        
        Message WrapMessage(string messageText)
        {
            if (!m_wrappingEnabled)
            {
                return MimeSerializer.Default.Deserialize<Message>(messageText);
            }
            
            return WrappedMessage.Create(messageText, NHINDStandard.MailHeadersUsed);            
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
            
            return WrappedMessage.Create(message, NHINDStandard.MailHeadersUsed);
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
            SignedEntity signedEntity = this.m_cryptographer.Sign(message.Message, message.Sender.Certificates);

            if (this.m_encryptionEnabled)
            {
                //
                // Encrypt the outbound message with all known trusted certs
                //
                MimeEntity encryptedEntity = this.m_cryptographer.Encrypt(signedEntity, message.Recipients.GetCertificates());
                //
                // Alter message content to contain encrypted data
                //
                message.Message.ApplyBody(encryptedEntity);
            }
            else
            {
                message.Message.ApplyBody(signedEntity);
            }
        }

        X509Certificate2Collection ResolvePrivateCerts(MailAddress address, bool required)
        {
            X509Certificate2Collection certs = null;
            try
            {
                certs = this.m_privateCertResolver.GetCertificates(address);
                if (certs == null && required)
                {
                    throw new AgentException(AgentError.UnknownRecipient);
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
                cert = this.m_publicCertResolver.GetCertificates(address);
                if (cert == null && required)
                {
                    throw new AgentException(AgentError.UnknownRecipient);
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
        
        void CheckEnvelopeAddresses(MessageEnvelope envelope)
        {
            this.CheckEnvelopeAddresses(envelope.Recipients, envelope.Sender);
        }
        
        void CheckEnvelopeAddresses(NHINDAddressCollection recipients, NHINDAddress sender)
        {
            if (recipients == null || recipients.Count == 0)
            {
                throw new AgentException(AgentError.NoRecipients);
            }
            if (sender == null)
            {
                throw new AgentException(AgentError.NoSender);
            }
            
            recipients.SetSource(AddressSource.RcptTo);
            sender.Source = AddressSource.MailFrom;
        }
                
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
                if (this.ErrorIncoming != null)
                {
                    this.ErrorIncoming(message, ex);
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
                if (this.ErrorOutgoing != null)
                {
                    this.ErrorOutgoing(message, ex);
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
                if (this.Error != null)
                {
                    this.Error(this, ex);
                }
            }
            catch
            {
            }
        }
    }
}
