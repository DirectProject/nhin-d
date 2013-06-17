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
using Health.Direct.Common;

namespace Health.Direct.SmtpAgent
{
    public enum SmtpAgentError
    {
        Unknown = 0,
        NotInitialized,
        MissingPostmaster,
        MissingLogSettings,
        InvalidEnvelopeFromAgent,
        EmptyResultFromAgent,
        MissingMailPickupFolder,
        MailPickupFolderDoesNotExist,
        MissingCertResolverClientSettings,
        MissingAnchorResolverClientSettings,
        NoAddressManager,
        //
        // Thrown if domains in Xml file not found in Config Service
        //
        ConfiguredDomainsMismatch,   
        NoSenderInEnvelope,
        NoRecipientsInEnvelope,
        MissingAddressTypeInRoute,
        /// <summary>
        /// FolderRoute has no actual target folders specified
        /// </summary>
        NoFoldersInRoute,
        /// <summary>
        /// SmtpMessageReceiver has no smtp server specified
        /// </summary>
        InvalidSmtpForwarderSettings,
        /// <summary>
        /// Empty plugin routes
        /// </summary>
        NoReceiversInPluginRoute,
        /// <summary>
        /// plugin routes not correct set up
        /// </summary>
        InvalidPluginRoute,
        /// <summary>
        /// An Encrypted Message came from WITHIN the domain... and internal relay was disabled
        /// </summary>
        InternalRelayDisabled,
        /// <summary>
        /// AutoDsnOption is unknown.
        /// </summary>
        UnkownAutoDsnOption
    }

    public class SmtpAgentException : DirectException<SmtpAgentError>
    {
        public SmtpAgentException(SmtpAgentError error)
            : base(error)
        {
        }
    }
}