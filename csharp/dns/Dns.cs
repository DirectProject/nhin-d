/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Sean Nolan      seannol@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Text;

namespace DnsResolver
{
    /// <summary>
    /// Enums taken from DnsRFCs. Please reference them
    /// </summary>
    public class Dns
    {
        public const int DNS_PORT = 53;
        public const short INVALID_REQUEST_ID = 0;
        public const int MAXLABELLENGTH = 63;
        public const int NOCACHE = 0;   // TTL of 0
        
        /// <summary>
        /// Dns Record Type
        /// </summary>
        public enum RecordType : short
        {
            // TYPE
            ANAME = 1,   // a host address
            NS = 2,      // an authoritative name server
            MD = 3,      // a mail destinatoin (obsolete, use MX)
            MF = 4,      // a mail forwarder (obsolete, use MX)
            CNAME = 5,   // the canonical name for an alias
            SOA = 6,     // marks the start of a zone of authority
            MB = 7,      // a mailbox domain name (EXPERIMENTAL)
            MG = 8,      // a mail group member (EXPERIMENTAL)
            MR = 9,      // a mail rename domain name (EXPERIMENTAL)
            NULL = 10,   // a null RR (EXPERIMENTAL)
            WKS = 11,    // a well known service description
            PTR = 12,    // a domain name pointer
            HINFO = 13,  // host information
            MINFO = 14,  // mailbox or mail list information
            MX = 15,     // mail exchange
            TXT = 16,    // text strings
            CERT = 37,   // CERT records
            // QTYPE
            AXFR = 252,  // A request for a transfer of an entire zone
            MAILB = 253, // A request for mailbox-related records (MB, MG or MR)
            MAILA = 254, // A request for mail agent RRs (obsolete - see MX)
            STAR = 255,  // A request for all records (*)
        }
        
        /// <summary>
        /// Dns Record Class
        /// </summary>
        public enum Class : short
        {
            // CLASS
            IN = 1,      // the Internet
            CS = 2,      // the CSNET class (obsolete, used only for examples in some obsolete RFCs)
            CH = 3,      // the CHAOS class
            HS = 4,      // Hesiod [Dyer 87]

            // QCLASS
            STAR = 255,  // any class (*)
        }

        public enum OpCode
        {
            QUERY = 0,   // a standard query 
            IQUERY = 1,  // an inverse query 
            STATUS = 2,  // a server status request
        }

        public enum ResponseCode
        {
            SUCCESS = 0,
            FORMAT_ERROR = 1,
            SERVER_FAILURE = 2,
            NAME_ERROR = 3,
            NOT_IMPLEMENTED = 4,
            REFUSED = 5,
        }
        
        public static int Compare(string x, string y)
        {
            return string.Compare(x, y, StringComparison.OrdinalIgnoreCase);
        }        
        
        public static bool Equals(string x, string y)
        {
            return string.Equals(x, y, StringComparison.OrdinalIgnoreCase);
        }
    }
}
