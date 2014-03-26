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
using System.Threading;
using System.Security.Cryptography.X509Certificates;

namespace Health.Direct.Common.Certificates
{
    /// <summary>
    /// Represents an index of certificates by subject.
    /// </summary>
    public class CertificateIndex : IX509CertificateIndex
    {
        IX509CertificateStore m_store;
        CertificateDictionary m_certIndex;        
        /// <summary>
        /// Creates an instance of an index initialized from an <see cref="IX509CertificateStore"/>
        /// </summary>
        /// <param name="store">The <see cref="IX509CertificateStore"/> instance to intialize the index from</param>
        public CertificateIndex(IX509CertificateStore store)
        {
            if (store == null)
            {
                throw new ArgumentNullException("store");
            }
            
            m_store = store;
            this.Refresh();
        }
        
        /// <summary>
        /// Retrns the certificates for a subjectName.
        /// </summary>
        /// <param name="subjectName">The subject name to lookup for certificates</param>
        /// <returns>The <see cref="X509Certificate2Collection"/> for the subject, or <c>null</c>
        /// if none are found.</returns>
        public X509Certificate2Collection this[string subjectName]
        {
            get
            {
                CertificateDictionary index = m_certIndex;
                
                X509Certificate2Collection matches;
                if (!index.TryGetValue(subjectName, out matches))
                {
                    matches = null;
                }
                return matches;
            }
        }
        
        /// <summary>
        /// Gets the subjectNames indexed by this index
        /// </summary>
        public IEnumerable<string> Keys
        {
            get
            {
                CertificateDictionary index = m_certIndex;
                return index.Keys;
            }
        }

        /// <summary>
        /// Refreshes the index (if the underlying store has changed).
        /// </summary>
        public void Refresh()
        {
            CertificateDictionary newIndex = this.Load(m_store);
            Interlocked.Exchange(ref m_certIndex, newIndex);
        }

        CertificateDictionary Load(IEnumerable<X509Certificate2> certs)
        {
            CertificateDictionary certIndex = new CertificateDictionary();
            if (certs != null)
            {
                foreach (X509Certificate2 cert in certs)
                {
                    string name = cert.ExtractEmailNameOrName();
                    X509Certificate2Collection list = null;
                    if (!certIndex.TryGetValue(name, out list))
                    {
                        list = new X509Certificate2Collection();
                        certIndex[name] = list;
                    }
                    list.Add(cert);
                }
            }
            //
            // Make the index readonly
            //
            return certIndex;
        }
    }

    internal class CertificateDictionary : Dictionary<string, X509Certificate2Collection>
    {
        internal CertificateDictionary()
            : base(StringComparer.OrdinalIgnoreCase)
        {
        }
    }
}