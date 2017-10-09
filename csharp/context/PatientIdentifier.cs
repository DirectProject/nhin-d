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
    /// Represents a <c>pid-instance</c>. 
    /// </summary>
    /// <remarks>
    /// See <a href="http://wiki.directproject.org/file/detail/Implementation+Guide+for+Expressing+Context+in+Direct+Messaging+v1.0-DRAFT-2016122901.docx"> Context in Direct</a>.
    /// 
    /// patient-id-element = “patient-id:” pid-instance *(“;” pid-instance)
    /// pid-instance = pid-context “:” local-patient-id
    /// pid-context = &lt;Assigning Authority Domain ID or UUID as described in text below&gt;
    /// local-patient-id = &lt;printable ASCII characters other than whitespace and “;”&gt;
    /// 
    /// The sender of a Direct message who wishes to identify the patient identifier in its local context SHALL construct a pid-instance using its unique Assigning Authority OID and its local patient identifier.The pid-context value is a globally unique value for each issuer of patient identifiers.If the sender has an Assigning Authority Domain ID(AA) that it uses for transactions under the Integrating the Healthcare Enterprise Information Technology Infrastructure Technical Framework(IHE ITI TF), then the sender SHOULD use that ID as the pid-context.If such an AA is not used, then the sender MUST use a Name-Based Universally Unique Identifier(UUID) as defined in Section 4.3 of RFC 4122 using the sender’s Direct Address or Direct Domain as the input, with the resulting 16 octet UUID value expressed as an unsigned integer as the final value of an OID in the 2.25 OID arc(e.g., a UUID with an unsigned integer value of 123456789 would be expressed as 2.25.123456789). At most one patient-id-element is permitted in the metadata.All pid-instances are intended to represent the same patient in the corresponding pid-context, thus each context may correspond to at most one patient. Only one pid-instance is permitted per pid-context, i.e.a pid-context MUST NOT appear more than once in a single patient-id-element.A party MAY add, modify, or remove its own pid-instance when constructing the patient-id-element in responses.Otherwise, each pid-instance included in an incoming message MUST be included in the response. Each participant SHOULD include its preferred patient identifier as its local-patient-id.
    /// 
    /// Example:
    /// patient-id: 2.16.840.1.113883.19.999999:123456; 
    ///  2.16.840.1.113883.19.888888:75774
    /// </remarks>
    public class PatientInstance
    {
        /// <summary>
        /// <c>pid-context</c>
        /// </summary>
        public string PidContext { get; set; }
        /// <summary>
        /// <c>local-patient-id</c>
        /// printable ASCII characters other than whitespace and “;”
        /// </summary>
        public string LocalPatientId { get; set; }
    }
}