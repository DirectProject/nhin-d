/* 
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
using System.Security.Cryptography.X509Certificates;
using NHINDirect.Collections;
using NHINDirect.Certificates;
using NHINDirect.Cryptography;
using NHINDirect.Mime;
using NHINDirect.Mail;

namespace NHINDirect.Agent
{
    /// <summary>
    /// Represents a collection of <see cref="NHINDAddress"/> instances.
    /// </summary>
    public class NHINDAddressCollection : ObjectCollection<NHINDAddress>
    {
        const TrustEnforcementStatus DefaultMinTrustStatus = TrustEnforcementStatus.Success;
        
        public NHINDAddressCollection()
        {
        }

        /// <summary>
        /// Enumerates the <see cref="X509Certificate2"/> instances for all collected addresses.
        /// </summary>
        public IEnumerable<X509Certificate2> Certificates
        {
            get
            {
                for (int i = 0, count = this.Count; i < count; ++i)
                {
                    X509Certificate2Collection certs = this[i].Certificates;
                    if (certs != null)
                    {
                        for (int c = 0, cCount = certs.Count; c < cCount; ++c)
                        {
                            yield return certs[c];
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
        public IEnumerable<NHINDAddress> GetTrusted()
        {
            return this.GetTrusted(NHINDAddressCollection.DefaultMinTrustStatus);
        }

        /// <summary>
        /// Get the trusted members of this collection with a specified trust status.
        /// </summary>
        /// <param name="minTrustStatus">The <see cref="TrustEnforcementStatus"/> defined as minimally trustworthy.</param>
        /// <returns>An enumeration of the <see cref="NHINDAddress"/> instances that are trusted</returns>
        public IEnumerable<NHINDAddress> GetTrusted(TrustEnforcementStatus minTrustStatus)
        {
            return from address in this
                   where address.IsTrusted(minTrustStatus)
                   select address;
        }
        
        /// <summary>
        /// Get the untrusted addresses of this collection.
        /// </summary>
        /// <returns>An enumeration of untrusted <see cref="NHINDAddress"/> instances</returns>
        public IEnumerable<NHINDAddress> GetUntrusted()
        {
            return this.GetUntrusted(NHINDAddressCollection.DefaultMinTrustStatus);
        }

        /// <summary>
        /// Get the untrusted addresses of this collection.
        /// </summary>
        /// <param name="minTrustStatus">The <see cref="TrustEnforcementStatus"/> defined as minimally trustworthy.</param>
        /// <returns>An enumeration of untrusted <see cref="NHINDAddress"/> instances</returns>
        public IEnumerable<NHINDAddress> GetUntrusted(TrustEnforcementStatus minTrustStatus)
        {
            return from address in this
                   where !address.IsTrusted(minTrustStatus)
                   select address;
        }
        
        /// <summary>
        /// Does this collection of NHINDAddress contain only trustworthy addresses? 
        /// </summary>
        /// <returns><c>true</c> if all the addresses are trusted, <c>false</c> if at least one is untrusted</returns>        
        public bool IsTrusted()
        {
            return this.IsTrusted(NHINDAddressCollection.DefaultMinTrustStatus);
        }

        /// <summary>
        /// Does this collection of NHINDAddress contain only trustworthy addresses? 
        /// </summary>
        /// <param name="minTrustStatus">The <see cref="TrustEnforcementStatus"/> defined as minimally trustworthy.</param>
        /// <returns><c>true</c> if all the addresses are trusted, <c>false</c> if at least one is untrusted</returns>        
        public bool IsTrusted(TrustEnforcementStatus minTrustStatus)
        {
            return this.All(x => x.IsTrusted(minTrustStatus));
        }
        
        /// <summary>
        /// Removes untrusted addresses from this collection
        /// </summary>
        public void RemoveUntrusted()
        {
            this.RemoveUntrusted(NHINDAddressCollection.DefaultMinTrustStatus);
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
        /// <param name="addresses">The enumeration of <see cref="NHINDAddress"/> instances to remove</param>
        public void Remove(IEnumerable<NHINDAddress> addresses)
        {
            if (addresses == null)
            {
                return;
            }
            
            foreach(NHINDAddress address in addresses)
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
        /// A string represntation of the addresses.
        /// </summary>
        /// <returns>A string representing the address collection</returns>
        public override string ToString()
        {
            return this.ToMailAddressCollection().ToString();
        }
        
        internal static NHINDAddressCollection Create(IEnumerable<NHINDAddress> source)
        {
            NHINDAddressCollection addresses = null;
            foreach(NHINDAddress address in source)
            {
                if (addresses == null)
                {
                    addresses = new NHINDAddressCollection();
                }
                addresses.Add(address);
            }
            
            return addresses;
        }

        internal static NHINDAddressCollection Parse(Header addresses)
        {
            if (addresses == null)
            {
                return null;
            }
            return NHINDAddressCollection.Parse(addresses.Value);
        }
        
        public static NHINDAddressCollection Parse(string addresses)
        {
            return MailParser.ParseAddressCollection<NHINDAddress, NHINDAddressCollection>(addresses, x => new NHINDAddress(x));
        }

        public static NHINDAddressCollection ParseSmtpServerEnvelope(string addresses)
        {
            return MailParser.ParseSMTPServerEnvelopeAddresses<NHINDAddress, NHINDAddressCollection>(addresses, x => new NHINDAddress(x));
        }
    }
}
