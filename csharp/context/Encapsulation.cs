/* 
 Copyright (c) 2010-2017, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook    Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

namespace Health.Direct.Context
{
    /// <summary>
    /// 3.7 Encapsulation of Other Data Types
    /// This metadata element MUST be included when Direct is used as a transport to encapsulate other message types such as an HTTP request or response, or HL7 v2 message or acknowledgement. 
    /// The encapsulated-message-type "http" MUST be used for encapsulated HTTP RESTful transactions such as with HL7 Fast Healthcare Interoperability Resources(FHIR) or encapsulated HTTP SOAP transactions such as transactions defined by the Integrating the Healthcare Enterprise(IHE) IT Infrastructure Technical Framework.The encapsulated-message-type "hl7v2" MUST be used for encapsulated HL7 V2 transactions.
    /// To encapsulate an HTTP transaction or its response, the HTTP headers and, if applicable, any POST data or other content SHALL be included in one or more separate attachments with the MIME type of application/x-direct-encapsulated+http.To encapsulate an HL7 V2 message or its response, the HL7 V2 message data SHALL be included in one or more separate attachments with MIME type of application/x-direct-encapsulated+hl7v2.Encapsulated HL7 V2 messages SHALL NOT include any control codes required by the HL7 V2 low level protocol (LLP).
    /// When more than one encapsulated message is included by the sender, the recipient MUST process the attachments in the order that the corresponding MIME attachments were included by the sender.A recipient MUST process each encapsulated message even if an earlier message results in an error, and MUST include the encapsulated responses in the same order in the response message.Thus, the sender MUST NOT assemble a sequence of attachments where the message encapsulated in one attachment depends upon or assumes the successful processing of an earlier attachment in the sequence. If the assembled responses would exceed a sender’s outgoing message size limits, the sender MAY send a failure message instead.
    /// This guide does not provide a mechanism to insert the results of one encapsulated transaction into the next encapsulated transaction when multiple transactions are included in a single message.
    /// </summary>
    public class Encapsulation
    {
        /// <summary>
        /// http or hl7v2
        /// </summary>
        public string Type { get; set; }

    }
}
