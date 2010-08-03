﻿/* 
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
using NHINDirect.Mime;
using NHINDirect.Mail;

namespace NHINDirect.Agent
{
    public class IncomingMessage : MessageEnvelope
    {
        SignedCms m_signatures;                             // All signatures + info about the signed blob etc
        MessageSignatureCollection m_senderSignatures;      // The sender's signatures, which are a subset of m_signatures
        
        public IncomingMessage(string messageText)
            : base(messageText)
        {
        }
        
        public IncomingMessage(Message message)
            : base(message)
        {
        }

        public IncomingMessage(Message message, NHINDAddressCollection recipients, NHINDAddress sender)
            : base(message, recipients, sender)
        {
        }

        public IncomingMessage(string messageText, NHINDAddressCollection recipients, NHINDAddress sender)
            : base(messageText, recipients, sender)
        {
        }        
         
        internal IncomingMessage(MessageEnvelope envelope)
            : base(envelope)
        {
        }
               
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
        
        public bool HasSignatures
        {
            get
            {
                return (m_signatures != null);
            }
        }
        
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
        
        public bool HasSenderSignatures
        {
            get
            {
                return (m_senderSignatures != null && m_senderSignatures.Count > 0);
            }
        }
        
        internal override void CategorizeRecipients(TrustEnforcementStatus minTrustStatus)
        {
            base.CategorizeRecipients(minTrustStatus);
            this.DomainRecipients.RemoveUntrusted(minTrustStatus);
        }
    }
}
