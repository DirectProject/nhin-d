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
using System.Threading;
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;

namespace NHINDirect.Certificates
{    
    public class CertificateIndex : IX509CertificateIndex, ICertificateResolver
    {
        IX509CertificateStore m_store;
        CertificateDictionary m_certIndex;
        CertificateResolver m_resolver;
        
        public CertificateIndex(IX509CertificateStore store)
        {
            if (store == null)
            {
                throw new ArgumentNullException();
            }
            
            m_store = store;
            this.Refresh();
            
            m_resolver = new CertificateResolver(this);
        }
        
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
        
        public IEnumerable<string> Keys
        {
            get
            {
                CertificateDictionary index = m_certIndex;
                return index.Keys;
            }
        }

        public X509Certificate2Collection GetCertificates(MailAddress address)
        {
            return m_resolver.GetCertificates(address);
        }

        public X509Certificate2Collection GetCertificates(string address)
        {
            return this.GetCertificates(new MailAddress(address));
        }

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

        public IEnumerator<X509Certificate2> GetEnumerator()
        {
            foreach(X509Certificate2Collection certCollection in m_certIndex.Values)
            {
                for (int i = 0, count = certCollection.Count; i < count; ++i)
                {
                    yield return certCollection[i];
                }
            }
        }

        #region IEnumerable Members

        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
           return this.GetEnumerator();
        }

        #endregion
    }
    
    internal class CertificateDictionary : Dictionary<string, X509Certificate2Collection>
    {
        internal CertificateDictionary()
            : base(StringComparer.OrdinalIgnoreCase)
        {
        }
    }

}
