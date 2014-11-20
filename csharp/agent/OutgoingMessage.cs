/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Joe Shook       jshook@kryptiq.com

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Mail;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mail.DSN;
using Health.Direct.Common.Mail.Notifications;

namespace Health.Direct.Agent
{
    /// <summary>
    /// Represents a message to be prepped for sending (generally an unencrypted message
    /// to be signed and encrypted).
    /// </summary>
    public class OutgoingMessage : MessageEnvelope
    {        
        /// <summary>
        /// Creates an instance from a <see cref="Message"/>
        /// </summary>
        /// <param name="message"></param>
        public OutgoingMessage(Message message)
            : base(message)
        {
        }

        /// <summary>
        /// Creates an instance from a <see cref="Message"/> instance, specifying recipients and sender.
        /// </summary>
        /// <param name="message"><see cref="Message"/> instance representing the message to be prepped for send.</param>
        /// <param name="recipients">An <see cref="DirectAddressCollection"/> of recipients, takes precedence over recipients in the message</param>
        /// <param name="sender">Sender <see cref="DirectAddress"/>, takes precendence over the <c>To</c> field in the message.</param>
        public OutgoingMessage(Message message, DirectAddressCollection recipients, DirectAddress sender)
            : base(message, recipients, sender)
        {
        }


        /// <summary>
        /// Creates an instance from a <see cref="Message"/> instance, specifying recipients and sender.
        /// </summary>
        /// <param name="message"><see cref="Message"/> instance representing the message to be prepped for send.</param>
        /// <param name="recipients">An <see cref="DirectAddressCollection"/> of recipients, takes precedence over recipients in the message</param>
        /// <param name="rejectedRecipients">An <see cref="DirectAddressCollection"/> of rejected recipients</param>
        /// <param name="sender">Sender <see cref="DirectAddress"/>, takes precendence over the <c>To</c> field in the message.</param>
        /// <param name="usingDeliveryStatus">Indicate if message requests DeliveryStatus</param>
        public OutgoingMessage(Message message, DirectAddressCollection recipients, DirectAddressCollection rejectedRecipients, 
            DirectAddress sender, bool usingDeliveryStatus)
            : base(message, recipients, rejectedRecipients, sender)
        {
            this.UsingDeliveryStatus = usingDeliveryStatus;
        }

        /// <summary>
        /// Creates an instance from an RFC 5322 string, specifying recipients and sender.
        /// </summary>
        /// <param name="messageText">RFC 5322 message string to be prepped for send.</param>
        /// <param name="recipients">An <see cref="DirectAddressCollection"/> of recipients, takes precedence over recipients in the message</param>
        /// <param name="sender">Sender <see cref="DirectAddress"/>, takes precendence over the <c>To</c> field in the message.</param>
        public OutgoingMessage(string messageText, DirectAddressCollection recipients, DirectAddress sender)
            : base(messageText, recipients, sender)
        {
        }

        

        internal OutgoingMessage(Message message, string messageText)
            : base(message)
        {
        }
        
        internal OutgoingMessage(Message message, string messageText, DirectAddressCollection recipients, DirectAddress sender)
            : base(message, recipients, sender)
        {
        }
        
        /// <summary>
        /// Create a new OutgoingMessage envelope from the given message envelope
        /// </summary>
        /// <param name="envelope">source envelope</param>        
        public OutgoingMessage(MessageEnvelope envelope)
            : base(envelope)
        {
        }
        
        /// <summary>
        /// Use this where the outgoing message is not 'really' a new outgoing message, but an ack or system message
        /// that provides success/failure or other system status in response to the incoming message. 
        /// Classic use case:
        ///  * A sends message to B
        ///  * B must send MDN Acks to A, because Direct requires it. 
        ///  * But B does not want to allow any other messages to be sent to A
        ///  
        ///  B adds A's trust Anchor with "Incoming" enabled but "Outgoing" disabled. 
        ///  Since the ACK is really part of the "incoming" message exchange, B could choose to use its Incoming trust
        ///  settings for the purpose. 
        /// 
        /// </summary>
        public bool UseIncomingTrustAnchors = false;


        /// <summary>
        /// Is message a Message Disposition Notificaton
        /// </summary>
        /// <remarks>
        /// Track MDN message type.
        /// This is set when the message type is discoverable in the decrypted form.
        /// </remarks>
        public bool? IsMDN = null;


        /// <summary>
        /// Is message a Deliery Status Notificaton
        /// </summary>
        /// <remarks>
        /// Track DSN message type.
        /// This is set when the message type is discoverable in the decrypted form.
        /// </remarks>
        public bool? IsDSN = null;

        /// <summary>
        /// Is Message requesting timely and reliable delivery.
        /// </summary>
        /// <remarks>
        /// Track the existance of X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true
        /// in the Disposition-Notification-Options header, RFC 3798 2.2 and
        /// Implementation Guide for Delivery Notification in Direct v1.0, 1.3
        /// This is set when the message type is discoverable in the decrypted form.
        /// </remarks>
        public bool? IsTimelyAndReliable;

        /// <summary>
        /// Is system configured for reliable delivery.  And if required is message requesting timely and reliable delivery.
        /// </summary>
        public bool UsingDeliveryStatus = false;
    }
}