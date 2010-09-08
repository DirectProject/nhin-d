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
using System.Linq;
using System.Text;
using System.Threading;

namespace DnsResolver
{
    /// <summary>
    /// A Dns Request (query) issued to the server
    /// </summary>
    public class DnsRequest : DnsMessage
    {
        /// <summary>
        /// Initializes a query for domain 
        /// </summary>
        /// <param name="qType">The record type to query</param>
        /// <param name="domain">The domain to query.</param>
        public DnsRequest(Dns.RecordType qType, string domain)
            : base(qType, domain)
        {
            this.Header.IsRequest = true;
            this.Header.IsTruncated = false;
            this.Header.ResponseCode = Dns.ResponseCode.SUCCESS;
        }
        
        public DnsRequest(DnsBufferReader reader)
            : base(ref reader)
        {
        }

        protected override void Deserialize(ref DnsBufferReader reader)
        {
            base.Deserialize(ref reader);
            
            this.Validate();            
        }   
        
        public override void Validate()
        {
            base.Validate();
            
            DnsHeader header = this.Header;
            if (
                    !header.IsRequest
                || header.AnswerCount != 0
                || header.AdditionalAnswerCount != 0
                || header.NameServerAnswerCount != 0
                || header.IsTruncated
            )                
            {
                throw new DnsProtocolException(DnsProtocolError.InvalidRequest);
            }
        }
             
        /// <summary>
        /// Creates a new A request (query)
        /// </summary>
        /// <param name="domain">The domain to query.</param>
        /// <returns>The newly created request instance.</returns>
        public static DnsRequest CreateA(string domain)
        {
            return new DnsRequest(Dns.RecordType.ANAME, domain);
        }

        /// <summary>
        /// Creates a new PTR request (query)
        /// </summary>
        /// <param name="domain">The domain to query.</param>
        /// <returns>The newly created request instance.</returns>
        public static DnsRequest CreatePTR(string domain)
        {
            return new DnsRequest(Dns.RecordType.PTR, domain);
        }

        /// <summary>
        /// Creates a new NS request (query)
        /// </summary>
        /// <param name="domain">The domain to query.</param>
        /// <returns>The newly created request instance.</returns>
        public static DnsRequest CreateNS(string domain)
        {
            return new DnsRequest(Dns.RecordType.NS, domain);
        }

        /// <summary>
        /// Creates a new MX request (query)
        /// </summary>
        /// <param name="domain">The domain to query.</param>
        /// <returns>The newly created request instance.</returns>
        public static DnsRequest CreateMX(string domain)
        {
            return new DnsRequest(Dns.RecordType.MX, domain);
        }

        /// <summary>
        /// Creates a new TXT request (query)
        /// </summary>
        /// <param name="domain">The domain to query.</param>
        /// <returns>The newly created request instance.</returns>
        public static DnsRequest CreateTXT(string domain)
        {
            return new DnsRequest(Dns.RecordType.TXT, domain);
        }

        /// <summary>
        /// Creates a new CERT request (query)
        /// </summary>
        /// <param name="domain">The domain to query.</param>
        /// <returns>The newly created request instance.</returns>
        public static DnsRequest CreateCERT(string domain)
        {
            return new DnsRequest(Dns.RecordType.CERT, domain);
        }

        /// <summary>
        /// Creates a new SOA request (query)
        /// </summary>
        /// <param name="domain">The domain to query.</param>
        /// <returns>The newly created request instance.</returns>
        public static DnsRequest CreateSOA(string domain)
        {
            return new DnsRequest(Dns.RecordType.SOA, domain);
        }
    }
}
