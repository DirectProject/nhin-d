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

namespace NHINDirect.Certificates
{
    public abstract class CertificateStore : IX509CertificateStore, ICertificateResolver
    {
        Predicate<X509Certificate2> m_criteria;
        CertificateResolver m_resolver;
        
        protected CertificateStore()
        {
            m_resolver = new CertificateResolver(this);
        }
        
        protected CertificateStore(Predicate<X509Certificate2> criteria)
            : this()
        {
            m_criteria = criteria;
        }
        
        public Predicate<X509Certificate2> Criteria
        {
            get
            {
                return m_criteria;
            }
            set
            {
                m_criteria = value;
            }
        }

        public abstract X509Certificate2Collection this[string subjectName]
        {
            get;
        }
        
        public abstract bool Contains(X509Certificate2 cert);
        public abstract void Add(X509Certificate2 cert);
        public abstract void Remove(X509Certificate2 cert);

        public bool MatchesCriteria(X509Certificate2 cert)
        {
            if (cert == null)
            {
                return false;
            }

            return (m_criteria == null || m_criteria(cert));
        }

        protected void ValidateCriteria(X509Certificate2 cert)
        {
            if (!this.MatchesCriteria(cert))
            {
                throw new ArgumentException("Criteria mismatch");
            }
        }

        public void CopyFrom(X509Store source)
        {
            if (source == null)
            {
                throw new ArgumentNullException();
            }

            this.Update(source.Certificates.Enumerate(this.Criteria));
        }

        public void CopyFrom(X509Store source, Predicate<X509Certificate2> criteria)
        {
            if (source == null)
            {
                throw new ArgumentNullException();
            }

            this.Update(source.Certificates.Enumerate(criteria));
        }

        public void Add(IEnumerable<X509Certificate2> certs)
        {
            if (certs == null)
            {
                throw new ArgumentNullException();
            }
            foreach(X509Certificate2 cert in certs)
            {
                this.Add(cert);
            }
        }
        
        public void Add(X509Certificate2Collection certs)
        {
            if (certs == null)
            {
                throw new ArgumentNullException();
            }
            for (int i = 0, count = certs.Count; i < count; ++i)
            {
                this.Add(certs[i]);
            }        
        }

        public void ImportKeyFile(string filePath, X509KeyStorageFlags flags)
        {
            this.ImportKeyFile(filePath, null, flags);
        }

        public void ImportKeyFile(string filePath, string password, X509KeyStorageFlags flags)
        {
            X509Certificate2Collection certs = new X509Certificate2Collection();
            certs.Import(filePath, password, flags);
            this.Add(certs);
        }

        public void ExportKeyFile(string filePath, string password, X509ContentType type)
        {
            X509Certificate2Collection certs = new X509Certificate2Collection();
            certs.Add(this);
            byte[] blob = certs.Export(type, password);
            
            System.IO.File.WriteAllBytes(filePath, blob);
        }
               
        public void Remove(IEnumerable<X509Certificate2> certs)
        {
            if (certs == null)
            {
                throw new ArgumentNullException();
            }
            foreach (X509Certificate2 cert in certs)
            {
                this.Remove(cert);
            }
        }

        public void Remove(X509Certificate2Collection certs)
        {
            if (certs == null)
            {
                throw new ArgumentNullException();
            }
            for (int i = 0, count = certs.Count; i < count; ++i)
            {
                this.Remove(certs[i]);
            }
        }
        
        public void Remove(string subjectName)
        {
            X509Certificate2Collection certs = this[subjectName];
            if (certs != null)
            {
                this.Remove(certs);
            }
        }
        
        public void Update(X509Certificate2 cert)
        {
            this.ValidateCriteria(cert);
            if (this.Contains(cert))
            {
                this.Remove(cert);
            }
            this.Add(cert);
        }

        public void Update(IEnumerable<X509Certificate2> certs)
        {
            if (certs == null)
            {
                throw new ArgumentNullException();
            }
            foreach (X509Certificate2 cert in certs)
            {
                this.Update(cert);
            }
        }
        
        public virtual X509Certificate2Collection GetAllCertificates()
        {
            X509Certificate2Collection certs = new X509Certificate2Collection();
            certs.Add(this);
            return certs;
        }
        
        public abstract IEnumerator<X509Certificate2> GetEnumerator();

        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
            return this.GetEnumerator();
        }

        //---------------------------------------------
        //
        // ICertificateResolver
        //
        //---------------------------------------------
        public X509Certificate2Collection GetCertificates(MailAddress address)
        {
            return m_resolver.GetCertificates(address);
        }

        public CertificateIndex Index()
        {
            return new CertificateIndex(this);
        }

        public virtual void Dispose()
        {
        }
    }
}
