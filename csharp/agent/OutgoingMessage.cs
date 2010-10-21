﻿/* 
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
using NHINDirect.Mail;
using NHINDirect.Mime;

namespace NHINDirect.Agent
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
        /// <param name="recipients">An <see cref="NHINDAddressCollection"/> of recipients, takes precedence over recipients in the message</param>
        /// <param name="sender">Sender <see cref="DirectAddress"/>, takes precendence over the <c>To</c> field in the message.</param>
        public OutgoingMessage(Message message, NHINDAddressCollection recipients, DirectAddress sender)
            : base(message, recipients, sender)
        {
        }

        /// <summary>
        /// Creates an instance from an RFC 5322 string, specifying recipients and sender.
        /// </summary>
        /// <param name="messageText">RFC 5322 message string to be prepped for send.</param>
        /// <param name="recipients">An <see cref="NHINDAddressCollection"/> of recipients, takes precedence over recipients in the message</param>
        /// <param name="sender">Sender <see cref="DirectAddress"/>, takes precendence over the <c>To</c> field in the message.</param>
        public OutgoingMessage(string messageText, NHINDAddressCollection recipients, DirectAddress sender)
            : base(messageText, recipients, sender)
        {
        }

        internal OutgoingMessage(Message message, string messageText)
            : base(message)
        {
        }

        internal OutgoingMessage(Message message, string messageText, NHINDAddressCollection recipients, DirectAddress sender)
            : base(message, recipients, sender)
        {
        }
        
        internal OutgoingMessage(MessageEnvelope envelope)
            : base(envelope)
        {
        }
    }
}
