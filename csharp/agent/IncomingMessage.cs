/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Mail;
using System.Security.Cryptography.Pkcs;
using System.Security.Cryptography.X509Certificates;
using NHINDirect.Mime;
using NHINDirect.Mail;

namespace NHINDirect.Agent
{
    /// <summary>
    /// Represents an incoming message, with sender, receivers, and message to be decrypted and verified.
    /// </summary>
    public class IncomingMessage : MessageEnvelope
    {
        SignedCms m_signatures;                             // All signatures + info about the signed blob etc
        MessageSignatureCollection m_senderSignatures;      // The sender's signatures, which are a subset of m_signatures
        
        /// <summary>
        /// Creates an instance from an RFC 5322 format message string.
        /// </summary>
        /// <param name="messageText">RFC 5322 message string, signed and encrypted.</param>
        public IncomingMessage(string messageText)
            : base(messageText)
        {
        }

        /// <summary>
        /// Creates an instance from a <see cref="Message"/> instance.
        /// </summary>
        /// <param name="message"><see cref="Message"/> instance, signed and encrypted.</param>
        public IncomingMessage(Message message)
            : base(message)
        {
        }

        /// <summary>
        /// Creates an instance from a <see cref="Message"/> instance, specifying recipients and sender.
        /// </summary>
        /// <param name="message"><see cref="Message"/> instance, signed and encrypted.</param>
        /// <param name="recipients">An <see cref="NHINDAddressCollection"/> of recipients, takes precedence over recipients in the message</param>
        /// <param name="sender">Sender <see cref="DirectAddress"/>, takes precendence over the <c>To</c> field in the message.</param>
        public IncomingMessage(Message message, NHINDAddressCollection recipients, DirectAddress sender)
            : base(message, recipients, sender)
        {
        }

        /// <summary>
        /// Creates an instance from an RFC 5322 format message string., specifying recipients and sender.
        /// </summary>
        /// <param name="messageText">RFC 5322 message string, signed and encrypted.</param>
        /// <param name="recipients">An <see cref="NHINDAddressCollection"/> of recipients, takes precedence over recipients in the message</param>
        /// <param name="sender">Sender <see cref="DirectAddress"/>, takes precendence over the <c>To</c> field in the message.</param>
        public IncomingMessage(string messageText, NHINDAddressCollection recipients, DirectAddress sender)
            : base(messageText, recipients, sender)
        {
        }        
         
        internal IncomingMessage(MessageEnvelope envelope)
            : base(envelope)
        {
        }
        
        /// <summary>
        /// Gets the signatures attached to this message in <see cref="SignedCms"/> format for manipulation with
        /// the raw .Net API.
        /// </summary>
        /// <remarks>The <c>SenderSignatures</c> property is generally easier to work with.</remarks>
        /// <value>A <see cref="SignedCms"/> instance representing signatures for this message.</value>
        public SignedCms Signatures
        {
            get
            {
                return this.m_signatures;
            }
            internal set
            {
                this.m_signatures = value;
            }
        }
        
        /// <summary>
        /// Gets if this message has signatures
        /// </summary>
        /// <value><c>true</c> if this message has signatures, <c>false</c> otherwise.</value>
        public bool HasSignatures
        {
            get
            {
                return (m_signatures != null);
            }
        }
        
        /// <summary>
        /// Gets the <see cref="MessageSignatureCollection"/> for this message
        /// </summary>
        /// <value>A <see cref="MessageSignatureCollection"/> of <see cref="MessageSignature"/> instances for each sender signature or <c>null</c> if there are no signatures.</value>
        public MessageSignatureCollection SenderSignatures
        {
            get
            {
                return m_senderSignatures;
            }
            internal set
            {
                m_senderSignatures = value;
            }
        }
        
        /// <summary>
        /// Gets if this message has sender signatures.
        /// </summary>
        /// <value><c>true</c> if this message has sender signatures, <c>false</c> otherwise.</value>
        public bool HasSenderSignatures
        {
            get
            {
                return (m_senderSignatures != null && m_senderSignatures.Count > 0);
            }
        }
    }
}
