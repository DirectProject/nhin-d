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
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Collections;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mime;

namespace Health.Direct.Agent
{
    /// <summary>
    /// Represents a collection of <see cref="DirectAddress"/> instances.
    /// </summary>
    public class DirectAddressCollection : ObjectCollection<DirectAddress>
    {
        /// <summary>
        /// The minimum trust enforcement status treated as indicated successful trust.
        /// </summary>
        const TrustEnforcementStatus DefaultMinTrustStatus = TrustEnforcementStatus.Success;
        
        /// <summary>
        /// Creates an empty collection.
        /// </summary>
        public DirectAddressCollection()
        {
        }
        
        /// <summary>
        /// Creates a shallow copy of the given source
        /// </summary>
        /// <param name="source">source collection</param>
        public DirectAddressCollection(IEnumerable<DirectAddress> source)
            : base(source)
        {
        }
        
        /// <summary>
        /// Enumerates the <see cref="X509Certificate2"/> instances for all collected addresses.
        /// </summary>
        public IEnumerable<X509Certificate2> Certificates
        {
            get
            {
                foreach (DirectAddress addr in this)
                {
                    if (addr.HasCertificates)
                    {
                        foreach (X509Certificate2 cert in addr.Certificates)
                        {
                            yield return cert;
                        }
                    }
                }
            }
        }
        
        /// <summary>
        /// The <see cref="X509Certificate2Collection"/> of all certificates for all signatures.
        /// </summary>
        /// <returns>An <see cref="X509Certificate2Collection"/></returns>
        public X509Certificate2Collection GetCertificates()
        {
            X509Certificate2Collection certs = new X509Certificate2Collection();
            certs.Add(this.Certificates);
            return certs;
        }
                
        /// <summary>
        /// Get the trustworthy members of this collection
        /// </summary>
        public IEnumerable<DirectAddress> GetTrusted()
        {
            return this.GetTrusted(DirectAddressCollection.DefaultMinTrustStatus);
        }

        /// <summary>
        /// Get the trusted members of this collection with a specified trust status.
        /// </summary>
        /// <param name="minTrustStatus">The <see cref="TrustEnforcementStatus"/> defined as minimally trustworthy.</param>
        /// <returns>An enumeration of the <see cref="DirectAddress"/> instances that are trusted</returns>
        public IEnumerable<DirectAddress> GetTrusted(TrustEnforcementStatus minTrustStatus)
        {
            return from address in this
                   where address.IsTrusted(minTrustStatus)
                   select address;
        }
        
        /// <summary>
        /// Get the untrusted addresses of this collection.
        /// </summary>
        /// <returns>An enumeration of untrusted <see cref="DirectAddress"/> instances</returns>
        public IEnumerable<DirectAddress> GetUntrusted()
        {
            return this.GetUntrusted(DirectAddressCollection.DefaultMinTrustStatus);
        }

        /// <summary>
        /// Get the untrusted addresses of this collection.
        /// </summary>
        /// <param name="minTrustStatus">The <see cref="TrustEnforcementStatus"/> defined as minimally trustworthy.</param>
        /// <returns>An enumeration of untrusted <see cref="DirectAddress"/> instances</returns>
        public IEnumerable<DirectAddress> GetUntrusted(TrustEnforcementStatus minTrustStatus)
        {
            return from address in this
                   where !address.IsTrusted(minTrustStatus)
                   select address;
        }
        
        /// <summary>
        /// Does this collection of DirectAddress contain only trustworthy addresses? 
        /// </summary>
        /// <returns><c>true</c> if all the addresses are trusted, <c>false</c> if at least one is untrusted</returns>        
        public bool IsTrusted()
        {
            return this.IsTrusted(DirectAddressCollection.DefaultMinTrustStatus);
        }

        /// <summary>
        /// Does this collection of DirectAddress contain only trustworthy addresses? 
        /// </summary>
        /// <param name="minTrustStatus">The <see cref="TrustEnforcementStatus"/> defined as minimally trustworthy.</param>
        /// <returns><c>true</c> if all the addresses are trusted, <c>false</c> if the collection is empty or at least one is untrusted</returns>        
        public bool IsTrusted(TrustEnforcementStatus minTrustStatus)
        {
            return this.Count > 0 && this.All(x => x.IsTrusted(minTrustStatus));
        }
        
        /// <summary>
        /// Removes untrusted addresses from this collection
        /// </summary>
        public void RemoveUntrusted()
        {
            this.RemoveUntrusted(DirectAddressCollection.DefaultMinTrustStatus);
        }

        /// <summary>
        /// Removes untrusted addresses from this collection
        /// </summary>
        /// <param name="minTrustStatus">The <see cref="TrustEnforcementStatus"/> defined as minimally trustworthy.</param>
        public void RemoveUntrusted(TrustEnforcementStatus minTrustStatus)
        {
            // Remove anybody who is not trusted
            this.RemoveExcept(x => x.IsTrusted(minTrustStatus));
        }
        
        /// <summary>
        /// Removes an enumeration of address
        /// </summary>
        /// <param name="addresses">The enumeration of <see cref="DirectAddress"/> instances to remove</param>
        public void Remove(IEnumerable<DirectAddress> addresses)
        {
            if (addresses == null)
            {
                return;
            }
            
            foreach(DirectAddress address in addresses)
            {
                this.Remove(address);
            }
        }
        
        /// <summary>
        /// Transforms the collection to a collection of <see cref="MailAddress"/> instances
        /// </summary>
        /// <returns>The <see cref="MailAddressCollection"/> corresponding to this collection</returns>
        public MailAddressCollection ToMailAddressCollection()
        {
            MailAddressCollection addresses = new MailAddressCollection();
            for (int i = 0, count = this.Count; i < count; ++i)
            {
                addresses.Add(this[i]);
            }
            
            return addresses;
        }
        
        /// <summary>
        /// Enumerates addresses as MailAddress (to get around limitation in .NET 3.5 generics)
        /// </summary>
        /// <returns>An enumerator of mail addresses</returns>
        public IEnumerable<MailAddress> AsMailAddresses()
        {
            for (int i = 0, count = this.Count; i < count; ++i)
            {
                yield return this[i];
            }
        }
                
        /// <summary>
        /// A <see cref="string"/> representation of the addresses.
        /// </summary>
        /// <returns>A string representing the address collection</returns>
        public override string ToString()
        {
            return this.ToMailAddressCollection().ToString();
        }

        /// <summary>
        /// A <see cref="string"/> representation of the addresses with line folding.
        /// </summary>
        /// <returns>A string representing the address collection</returns>
        public string ToStringWithFolding()
        {
            return this.ToMailAddressCollection().ToStringWithFolding();
        }
        
        /// <summary>
        /// Turns this address collection into a <see cref="Header"/>
        /// </summary>
        /// <param name="headerName">header name</param>
        /// <returns>Header object, OR null if this collection is empty</returns>
        public Header ToHeader(string headerName)
        {
            if (string.IsNullOrEmpty(headerName))
            {
                throw new ArgumentException("headerName");
            }
            if (this.Count == 0)
            {
                return null;
            }
            
            string foldedValue = this.ToStringWithFolding();
            string unfoldedValue = this.ToString();
            return new Header(headerName, foldedValue, unfoldedValue);
        }
        
        internal static DirectAddressCollection Create(IEnumerable<DirectAddress> source)
        {
            DirectAddressCollection addresses = null;
            foreach(DirectAddress address in source)
            {
                if (addresses == null)
                {
                    addresses = new DirectAddressCollection();
                }
                addresses.Add(address);
            }
            
            return addresses;
        }

        internal static DirectAddressCollection Parse(Header addresses)
        {
            if (addresses == null)
            {
                return null;
            }
            return DirectAddressCollection.Parse(addresses.Value);
        }
        
        /// <summary>
        /// Parse a string representation of an address list
        /// </summary>
        /// <param name="addresses">The string representation, as in a <c>To:</c> header</param>
        /// <returns>The collection corresponding to the address list.</returns>
        public static DirectAddressCollection Parse(string addresses)
        {
            return MailParser.ParseAddressCollection<DirectAddress, DirectAddressCollection>(addresses, x => new DirectAddress(x));
        }

        /// <summary>
        /// Parses a string representation of an address list in the format provided in an SMTP session
        /// </summary>
        /// <param name="addresses">The string representation of an SMTP <c>RCPT TO</c> command</param>
        /// <returns>The collection corresponding to the address list.</returns>
        public static DirectAddressCollection ParseSmtpServerEnvelope(string addresses)
        {
            return MailParser.ParseSMTPServerEnvelopeAddresses<DirectAddress, DirectAddressCollection>(addresses, x => new DirectAddress(x));
        }
        
        /// <summary>
        /// Locate the index of the matching address
        /// </summary>
        /// <param name="address">address string</param>
        /// <returns>index of the matching DirectAddress object. If not found, returns -1</returns>
        public int IndexOf(string address)
        {
            if (string.IsNullOrEmpty(address))
            {
                throw new ArgumentException("address");
            }
            
            for (int i = 0, count = this.Count; i < count; ++i)
            {
                if (MimeStandard.Equals(address, this[i].Address))
                {
                    return i;
                }
            }
            
            return -1;
        }
        
        /// <summary>
        /// Return true if this collection contains a matching address
        /// </summary>
        /// <param name="address">address string</param>
        /// <returns>true or false</returns>
        public bool Contains(string address)
        {
            return (this.IndexOf(address) >= 0);
        }
    }
}