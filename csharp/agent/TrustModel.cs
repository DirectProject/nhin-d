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
using NHINDirect.Certificates;

namespace NHINDirect.Agent
{
    public enum TrustEnforcementStatus
    {
        Failed = -1,
        Unknown = 0,
        Success_Offline = 1,            // Signature valid, signing cert is trusted, but could not retrieve cert directly from source
        Success_ThumbprintMismatch = 2, // Signature valid, signing cert is trusted, but the signing cert and the source cert did not match
        Success= 3,                     // Signature valid, siging cert trusted, and certs match perfectly
    }
        
    public class TrustModel
    {
        public static readonly TrustModel Default = new TrustModel();
        
        TrustChainValidator m_certChainValidator;
                                
        public TrustModel()
            : this(new TrustChainValidator())
        {
        }
        
        public TrustModel(TrustChainValidator validator)
        {
            if (validator == null)
            {
                throw new ArgumentNullException();
            }
            
            m_certChainValidator = validator;
        }
        
        public TrustChainValidator CertChainValidator
        {
            get
            {
                return m_certChainValidator;
            }
        }
                                
        public void Enforce(IncomingMessage message)
        {
            if (message == null)
            {
                throw new ArgumentNullException();
            }
            
            if (!message.HasSignatures)
            {
                throw new AgentException(AgentError.UntrustedMessage);
            }
            //
            // The message could have multiple signatures, including, possibly, some not by the sender
            // 
            this.FindSenderSignatures(message);
            if (!message.HasSenderSignatures)
            {
                throw new AgentException(AgentError.MissingSenderSignature);
            }
            // 
            // For each recipient, find at least one valid sender signature that the recipient trusts
            //
            NHINDAddress sender = message.Sender;
            NHINDAddressCollection recipients = message.DomainRecipients;
            for (int i = 0, count = recipients.Count; i < count; ++i)
            {
                NHINDAddress recipient = recipients[i];
                recipient.Status = TrustEnforcementStatus.Failed;
                //
                // First, find a signature that this recipient trusts
                //
                MessageSignature trustedSignature = this.FindTrustedSignature(message, recipient.TrustAnchors);
                //
                // Then, verify that signer's signature
                //
                if (trustedSignature != null)
                {
                    if (trustedSignature.IsThumbprintVerified != null)
                    {
                        recipient.Status = (trustedSignature.IsThumbprintVerified.Value) ? TrustEnforcementStatus.Success : TrustEnforcementStatus.Success_ThumbprintMismatch;
                    }
                    else
                    {
                        recipient.Status = TrustEnforcementStatus.Success_Offline;
                    }
                } 
            }            
        }
        
        public void Enforce(OutgoingMessage message)
        {
            if (message == null)
            {
                throw new ArgumentNullException();
            }
            
            NHINDAddress sender = message.Sender;
            NHINDAddressCollection recipients = message.Recipients;
            
            for (int i = 0, count = recipients.Count; i < count; ++i)
            {
                NHINDAddress recipient = recipients[i];
                recipient.Status = TrustEnforcementStatus.Failed;    
                //
                // The recipient should have at least one certificate that the sender trusts. Otherwise we don't trust this recipient
                //
                recipient.Certificates = this.FindTrustedCerts(recipient.Certificates, sender.TrustAnchors);
                if (recipient.HasCertificates)
                {
                    recipient.Status = TrustEnforcementStatus.Success;
                }
            }
        }
        
        X509Certificate2Collection FindTrustedCerts(X509Certificate2Collection certs, X509Certificate2Collection anchors)
        {
            if (certs == null)
            {
                return null;
            }
            
            X509Certificate2Collection trustedCerts = null;
            for (int i = 0, count = certs.Count; i < count; ++i)
            {
                X509Certificate2 cert = certs[i];
                if (m_certChainValidator.IsTrustedCertificate(cert, anchors))
                {
                    if (count == 1)
                    {
                        trustedCerts = certs;
                        break;
                    }
                    
                    if (trustedCerts == null)
                    {
                        trustedCerts = new X509Certificate2Collection();
                    }
                    trustedCerts.Add(cert);
                }
            }
            
            return trustedCerts;
        }

        void FindSenderSignatures(IncomingMessage message)
        {
            message.SenderSignatures = null;
            
            NHINDAddress sender = message.Sender;
            SignerInfoCollection allSigners = message.Signatures.SignerInfos;
            MessageSignatureCollection senderSignatures = null;
            bool match;
            
            for (int i = 0, count = allSigners.Count; i < count; ++i)
            {
                SignerInfo signer = allSigners[i];
                bool isOrgCertificate = false;
                
                match = signer.Certificate.MatchEmailNameOrName(sender.Address);
                if (!match)
                {
                    match = signer.Certificate.MatchEmailNameOrName(sender.Host);
                    isOrgCertificate = match;
                }
                
                if (match)
                {
                    if (senderSignatures == null)
                    {
                        senderSignatures = new MessageSignatureCollection();
                    }                    
                    senderSignatures.Add(new MessageSignature(signer, isOrgCertificate));
                }
            }
            
            message.SenderSignatures = senderSignatures;
        }

        MessageSignature FindTrustedSignature(IncomingMessage message, X509Certificate2Collection anchors)
        {                        
            NHINDAddress sender = message.Sender;
            MessageSignatureCollection signatures = message.SenderSignatures;
            MessageSignature lastTrustedSignature = null;
            
            for (int i = 0, count = signatures.Count; i < count; ++i)
            {
                MessageSignature signature = signatures[i];
                if (m_certChainValidator.IsTrustedCertificate(signature.Certificate, anchors) && signature.CheckSignature())
                {
                    if (!sender.HasCertificates)
                    {
                        // Can't really check thumbprints etc. So, this is about as good as its going to get
                        return signature;
                    }
                    
                    if (signature.CheckThumbprint(sender))
                    {
                        return signature;
                    }            
                    //
                    // We'll save this guy, but keep looking for a signer whose thumbprint we can verify
                    // If we can't find one, we'll use the last trusted signer we found.. and just mark the recipient's trust
                    // enforcement status as Success_ThumbprintMismatch
                    //
                    lastTrustedSignature = signature;
                }
            }
            
            return lastTrustedSignature;
        }
    }        
}
