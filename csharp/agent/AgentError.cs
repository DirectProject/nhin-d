/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Ali Emami       aliemami@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

namespace Health.Direct.Agent
{
    /// <summary>
    /// Enumeration of types of agent errors, used in <see cref="AgentException"/>
    /// </summary>
    public enum AgentError
    {
        /// <summary>
        /// Unexpected or other unknown error status.
        /// </summary>
        Unexpected = 0,
        /// <summary>
        /// A message is missing the required <c>To:</c> header
        /// </summary>
        MissingTo,
        /// <summary>
        /// A message is missing the required <c>From:</c> header
        /// </summary>
        MissingFrom,
        /// <summary>
        /// A message is missing the message body.
        /// </summary>
        MissingMessage,        
        /// <summary>
        /// The message was asked to set a null or empty recipients list.
        /// </summary>
        NoRecipients,
        /// <summary>
        /// The message was asked to set a null or missing sender.
        /// </summary>
        NoSender,        
        /// <summary>
        /// The encrypted message was malformed.
        /// </summary>
        InvalidEncryption,
        /// <summary>
        /// This message is not trusted (could not be decrypted or is unsigned)
        /// </summary>
        UntrustedMessage,
        /// <summary>
        /// The sender of this message is not trusted.
        /// </summary>
        UntrustedSender,        
        /// <summary>
        /// This message has no signatures.
        /// </summary>
        UnsignedMessage,
        /// <summary>
        /// The message is missing sender signatures
        /// </summary>
        MissingSenderSignature,                
        /// <summary>
        /// The message did not contain recipients in a domain managed by this agent
        /// </summary>
        NoDomainRecipients,        
        /// <summary>
        /// There are no trusted recipients for this message after trust enforcement.
        /// </summary>
        NoTrustedRecipients,
        /// <summary>
        /// Couldn not resolve the public certificate for the given address
        /// </summary>
        CouldNotResolvePublicCert,
        /// <summary>
        /// Could not resolve private keys for the an address
        /// </summary>
        CouldNotResolvePrivateKey,
        /// <summary>
        /// The MaxDomainRecipients limit was exceeded
        /// </summary>
        MaxDomainRecipients,
        /// <summary>
        /// The recipients in the envelope were not found in raw message headers
        /// </summary>
        RecipientMismatch
    }
}