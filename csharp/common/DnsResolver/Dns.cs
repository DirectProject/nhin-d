/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Sean Nolan      seannol@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;

namespace Health.Direct.Common.DnsResolver
{
    /// <summary>
    /// Constants and enumerations for working with the DNS.
    /// </summary>
    public class DnsStandard
    {
        /// <summary>
        /// The standard DNS port
        /// </summary>
        public const int DnsPort = 53;
        /// <summary>
        /// The ID for an invalid DNS request.
        /// </summary>
        public const short InvalidRequestID = 0;
        /// <summary>
        /// The maximum length for a DNS label
        /// </summary>
        /// <remarks>
        /// RFC 1035, Section 3.1
        /// </remarks>
        public const int MaxLabelLength = 63;
        /// <summary>
        /// TTL of 0
        /// </summary>
        public const int NoCache = 0;   
        /// <summary>
        /// Maximum size of a UDP Message
        /// </summary>
        public const int MaxUdpMessageLength = 512; // bytes      
        /// <summary>
        /// Record types for DNS RR
        /// </summary>
        public enum RecordType : short
        {
            /// <summary>
            /// Record type for A records, a host address
            /// </summary>
            /// <remarks>
            /// RFC 1035, Section 3.2.2
            /// </remarks>
            ANAME = 1,
            /// <summary>
            /// Record type for NS, an authoritative name server
            /// </summary>
            /// <remarks>
            /// RFC 1035, Section 3.2.2
            /// </remarks>
            NS = 2,
            /// <summary>
            /// Obsolete mail destination
            /// </summary>
            /// <remarks>
            /// Obsoleted by MX, RFC 1035, Section 3.2.2
            /// </remarks>
            MD = 3,      // a mail destinatoin (obsolete, use MX)
            /// <summary>
            /// Obsolete mail forwarder
            /// </summary>
            /// <remarks>
            /// Obsoleted by MX, RFC 1035, Section 3.2.2
            /// </remarks>
            MF = 4,
            /// <summary>
            /// Record type for CNAME, the canonical name for an alias
            /// </summary>
            /// <remarks>
            /// RFC 1035, Section 3.2.2
            /// </remarks>
            CNAME = 5,
            /// <summary>
            /// Record type for SOA, Start of Authority: Information about a zone
            /// </summary>
            /// <remarks>
            /// RFC 1035, Section 3.2.2
            /// </remarks>
            SOA = 6,
            /// <summary>
            /// A mailbox domain name (EXPERIMENTALOBSOLETE)
            /// </summary>
            /// <remarks>
            /// RFC 1035, Section 3.2.2
            /// </remarks>
            MB = 7,
            /// <summary>
            /// A mail group member (EXPERIMENTAL/OBSOLETE)
            /// </summary>
            /// <remarks>
            /// RFC 1035, Section 3.2.2
            /// </remarks>
            MG = 8,
            /// <summary>
            /// A mail rename domain (EXPERIMENTAL/OBSOLETE)
            /// </summary>
            /// <remarks>
            /// RFC 1035, Section 3.2.2
            /// </remarks>
            MR = 9,
            /// <summary>
            ///  A null RR (EXPERIMENTAL)
            /// </summary>
            /// <remarks>
            /// RFC 1035, Section 3.2.2
            /// </remarks>
            NULL = 10,
            /// <summary>
            /// A well known service description
            /// </summary>
            /// <remarks>
            /// RFC 1035, Section 3.2.2
            /// </remarks>
            WKS = 11,
            /// <summary>
            /// Record type for PTR, A domain name pointer
            /// </summary>
            /// <remarks>
            /// RFC 1035, Section 3.2.2
            /// </remarks>
            PTR = 12,
            /// <summary>
            /// Host information
            /// </summary>
            /// <remarks>
            /// RFC 1035, Section 3.2.2
            /// </remarks>
            HINFO = 13,
            /// <summary>
            /// Mailbox or mail list information
            /// </summary>
            /// <remarks>
            /// RFC 1035, Section 3.2.2
            /// </remarks>
            MINFO = 14,
            /// <summary>
            /// Record type for MX, mail exchange
            /// </summary>
            /// <remarks>
            /// RFC 1035, Section 3.2.2
            /// </remarks>
            MX = 15,
            /// <summary>
            /// Record type for TXT, text strings
            /// </summary>
            /// <remarks>
            /// RFC 1035, Section 3.2.2
            /// </remarks>
            TXT = 16,
            /// <summary>
            /// Record type for AAAA, IPv6 address records
            /// </summary>
            /// <remarks>
            /// RFC 3596, Section 2.1
            /// </remarks>
            AAAA = 28,
            /// <summary>
            /// Record type for SRV, location of services
            /// </summary>
            /// <remarks>
            /// RFC 2782, IANA Considerations
            /// </remarks>
            SRV = 33, 
            /// <summary>
            /// Record type for CERT records, certificates in the DNS
            /// </summary>
            /// <remarks>RFC 4398, Section 2</remarks>
            CERT = 37,   // CERT records
            // QTYPE
            /// <summary>
            /// Record type for a request for a transfer of an entire zone
            /// </summary>
            AXFR = 252,
            /// <summary>
            /// Record type for a request for mailbox-related records (MB, MG or MR)
            /// </summary>
            MAILB = 253,
            /// <summary>
            /// Record type for a request for mail agent RRs (obsolete - see MX)
            /// </summary>
            MAILA = 254,
            /// <summary>
            /// Record type for a request for all records (*)
            /// </summary>
            STAR = 255, 
        }
        
        /// <summary>
        /// The class of DNS record, in practice, always IN
        /// </summary>
        public enum Class : short
        {
            // CLASS
            /// <summary>
            /// CLASS constant for IN
            /// </summary>
            /// <remarks>RFC 1035, Section 3.2.4</remarks>
            IN = 1,      // the Internet
            /// <summary>
            /// Class code for the CSNET class (obsolete, used only for examples in some obsolete RFCs)
            /// </summary>
            CS = 2, 
            /// <summary>
            /// CLASS code for the CHAOS class (practially obsolete)
            /// </summary>
            CH = 3,
            /// <summary>
            /// Class code for Hesiod records (practically obsolete).
            /// </summary>
            HS = 4,      // Hesiod [Dyer 87]

            // QCLASS
            /// <summary>
            /// QCLASS code for any class
            /// </summary>
            STAR = 255,
        }

        /// <summary>
        /// Constants for DNS opcodes used in the header of DNS messages
        /// </summary>
        /// <remarks>
        /// RFC 1035, 4.1.1:
        /// A four bit field that specifies kind of query in this
        /// message.  This value is set by the originator of a query
        /// and copied into the response.
        /// </remarks>
        public enum OpCode
        {
            /// <summary>
            /// OPCODE for a standard query
            /// </summary>
            Query = 0,  
            /// <summary>
            /// OPCODE for an inverse query
            /// </summary>
            IQuery = 1,  
            /// <summary>
            /// OPCODE for a server status request
            /// </summary>
            Status = 2,
        }

        /// <summary>
        /// Constants for RCODE, header values used to signal success of response
        /// </summary>
        /// <remarks>
        /// RFC 1035, 4.1.1
        /// 
        /// </remarks>
        public enum ResponseCode
        {
            /// <summary>
            /// RCODE for no error conditions
            /// </summary>
            Success = 0,
            /// <summary>
            /// RCODE when the name server was unable to interpret the query
            /// </summary>
            FormatError = 1,
            /// <summary>
            /// RCODE when the server was unable to process the query due to problems with the name server
            /// </summary>
            ServerFailure = 2,
            /// <summary>
            /// RCODE from authoritative servers, when the domain name does not exist
            /// </summary>
            NameError = 3,
            /// <summary>
            /// RCODE when the name server does not implement the requested query
            /// </summary>
            NotImplemented = 4,
            /// <summary>
            /// RCODE   when the name server refuses to perform the requested operation for policy reasons.
            /// </summary>
            Refused = 5,
        }
        
        /// <summary>
        /// Implements string comparison for DNS values (e.g., domain names)
        /// </summary>
        public static int Compare(string x, string y)
        {
            return string.Compare(x, y, StringComparison.OrdinalIgnoreCase);
        }

        /// <summary>
        /// Implements string equality for DNS values (e.g., domain names)
        /// </summary>
        public static bool Equals(string x, string y)
        {
            return string.Equals(x, y, StringComparison.OrdinalIgnoreCase);
        }
    }
}