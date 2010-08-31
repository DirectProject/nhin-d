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
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Net.Mail;
using System.Security.Cryptography;
using System.Security.Cryptography.Pkcs;
using System.Security.Cryptography.X509Certificates;

namespace NHINDirect.Certificates
{
    /// <summary>
    /// Extend the public APIs for some crypto objects by attaching these handy methods
    /// Also includes some static 'factory' type methods
    /// </summary>
    public static class Extensions
    {
        //---------------------------------------
        //
        // X509Store
        //
        //---------------------------------------
        public static X509Store OpenStoreRead(StoreLocation location)
        {
            X509Store store = new X509Store(location);
            store.Open(OpenFlags.OpenExistingOnly | OpenFlags.ReadOnly);
            return store;
        }

        public static X509Store OpenStoreReadWrite(StoreLocation location)
        {
            X509Store store = new X509Store(location);
            store.Open(OpenFlags.ReadWrite);
            return store;
        }

        public static X509Store OpenStoreRead(string storeName, StoreLocation location)
        {
            X509Store store = new X509Store(storeName, location);
            store.Open(OpenFlags.OpenExistingOnly | OpenFlags.ReadOnly);
            return store;
        }

        public static X509Store OpenStoreReadWrite(string storeName, StoreLocation location)
        {
            X509Store store = new X509Store(storeName, location);
            store.Open(OpenFlags.ReadWrite);
            return store;
        }
        
        public static void Add(this X509Store store, IEnumerable<X509Certificate2> certs)
        {
            if (certs == null)
            {
                throw new ArgumentNullException();
            }
            
            foreach(X509Certificate2 cert in certs)
            {
                store.Add(cert);
            }
        }

        //---------------------------------------
        //
        // X509Certificate2Collection Extensions
        //
        //---------------------------------------        
        public static bool IsNullOrEmpty(this X509Certificate2Collection certs)
        {
            return (certs == null || certs.Count == 0);
        }

        public static void Add(this X509Certificate2Collection certs, X509Certificate2Collection newCerts)
        {
            if (newCerts == null)
            {
                throw new ArgumentNullException();
            }

            foreach (X509Certificate2 cert in newCerts)
            {
                certs.Add(cert);
            }
        }

        public static void Add(this X509Certificate2Collection certs, IEnumerable<X509Certificate2> newCerts)
        {
            if (newCerts == null)
            {
                throw new ArgumentNullException();
            }
            
            foreach(X509Certificate2 cert in newCerts)
            {
                certs.Add(cert);
            }
        }
        
        public static IEnumerable<X509Certificate2> Enumerate(this X509Certificate2Collection certs)
        {
            return certs.Enumerate(null);
        }
        
        public static IEnumerable<X509Certificate2> Enumerate(this X509Certificate2Collection certs, Predicate<X509Certificate2> filter)
        {
            foreach(X509Certificate2 cert in certs)
            {
                if (filter == null || filter(cert))
                {
                    yield return cert;
                }
            }
        }
        
        public static int IndexOf(this X509Certificate2Collection certs, Predicate<X509Certificate2> matcher)
        {
            if (matcher == null)
            {
                throw new ArgumentNullException();
            }
            
            for (int i = 0, count = certs.Count; i < count; ++i)
            {
                if (matcher(certs[i]))
                {
                    return i;
                }
            }
            
            return -1;
        }

        public static X509Certificate2Collection Where(this X509Certificate2Collection certs, Func<X509Certificate2, bool> match)
        {
            X509Certificate2Collection matchingCerts = null;

            foreach (X509Certificate2 cert in certs)
            {
                if (match(cert))
                {
                    matchingCerts = matchingCerts ?? new X509Certificate2Collection();
                    matchingCerts.Add(cert);
                }
            }

            return matchingCerts;
        }

        public static X509Certificate2 Find(this X509Certificate2Collection certs, Predicate<X509Certificate2> matcher)
        {
            int index = certs.IndexOf(matcher);
            if (index >= 0)
            {
                return certs[index];
            }
            
            return null;
        }

        public static X509Certificate2 FindByName(this X509Certificate2Collection certs, string name)
        {
            if (string.IsNullOrEmpty(name))
            {
                throw new ArgumentException();
            }
            
            string distinguishedName = "CN=" + name;
            return certs.Find(x => x.Subject.Contains(distinguishedName, StringComparison.OrdinalIgnoreCase));
        }

        public static X509Certificate2 FindByThumbprint(this X509Certificate2Collection certs, string thumbprint)
        {
            if (string.IsNullOrEmpty(thumbprint))
            {
                throw new ArgumentException();
            }

            return certs.Find(x => x.Thumbprint == thumbprint);
        }
                        
        /// <summary>
        /// Return the first certificate with an applicable date range is 
        /// </summary>
        public static X509Certificate2 FindUsable(this X509Certificate2Collection certs, DateTime usageTime)
        {
            for (int i = 0, count = certs.Count; i < count; ++i)
            {
                X509Certificate2 cert = certs[i];
                if (cert.HasValidDateRange(usageTime))
                {
                    return cert;
                }
            }
            
            return null;
        }

        public static X509Certificate2 FindUsable(this X509Certificate2Collection certs)
        {
            return certs.FindUsable(DateTime.Now);
        }
        
        public static X509Certificate2 FindUsable(IEnumerable<X509Certificate2> certs)
        {
            DateTime now = DateTime.Now;
            foreach(X509Certificate2 cert in certs)
            {
                if (cert.HasValidDateRange(now))
                {
                    return cert;
                }
            }
            
            return null;
        }
        
        //---------------------------------------
        //
        // X509Certificate Extensions
        //
        //---------------------------------------
        public static X509Certificate2 LoadCert(string subjectName, StoreLocation location)
        {
            X509Store store = OpenStoreRead(location);
            try
            {
                X509Certificate2Collection certs = store.Certificates;
                return certs.FindByName(subjectName);
            }
            finally
            {
                if (store != null)
                {
                    store.Close();
                }
            }
        }

        const string SubjectNamePrefix = "CN=";
        const string EmailNamePrefix = "E=";
        
        public static bool MatchName(this X509Certificate2 cert, string name)
        {
            if (string.IsNullOrEmpty(name))
            {
                throw new ArgumentException();
            }

            string distinguishedName = SubjectNamePrefix + name;
            return cert.Subject.Contains(distinguishedName, StringComparison.OrdinalIgnoreCase);
        }

        public static bool MatchEmailName(this X509Certificate2 cert, string name)
        {
            if (string.IsNullOrEmpty(name))
            {
                throw new ArgumentException();
            }

            string distinguishedName = EmailNamePrefix + name;
            return cert.Subject.Contains(distinguishedName, StringComparison.OrdinalIgnoreCase);
        }
        
        public static bool MatchEmailNameOrName(this X509Certificate2 cert, string name)
        {
            return (cert.MatchEmailName(name) || cert.MatchName(name));
        }
        
        public static string ExtractName(this X509Certificate2 cert)
        {
            string[] parts = cert.Subject.Split(',');
            if (parts != null)
            {
                for (int i = 0; i < parts.Length; ++i)
                {
                    int index = parts[i].IndexOf(SubjectNamePrefix);
                    if (index >= 0)
                    {
                        return parts[i].Substring(index + SubjectNamePrefix.Length).Trim();
                    }                               
                }
            }
                        
            return null;
        }

        public static bool NameContains(this X509Certificate2 cert, string name)
        {
            if (string.IsNullOrEmpty(name))
            {
                throw new ArgumentException();
            }

            string distinguishedName = cert.ExtractName();
            if (string.IsNullOrEmpty(distinguishedName))
            {
                return false;
            }

            return (distinguishedName.Contains(name, StringComparison.OrdinalIgnoreCase));
        }
        
        public static string ExtractEmailNameOrName(this X509Certificate2 cert)
        {
            string[] parts = cert.Subject.Split(',');
            if (parts != null)
            {
                for (int i = 0; i < parts.Length; ++i)
                {
                    string prefix = EmailNamePrefix;
                    int index = parts[i].IndexOf(prefix);
                    if (index < 0)
                    {
                        prefix = SubjectNamePrefix;
                        index = parts[i].IndexOf(prefix);
                    }
                    if (index >= 0)
                    {
                        return parts[i].Substring(index + prefix.Length).Trim();
                    }
                }
            }

            return null;
        }
        
        public static bool HasValidDateRange(this X509Certificate2 cert)
        {
            return cert.HasValidDateRange(DateTime.Now);     
        }

        public static bool HasValidDateRange(this X509Certificate2 cert, DateTime now)
        {
            //
            // Times are in local time
            //
            return (cert.NotBefore < now && now < cert.NotAfter);
        }
        
        public static bool HasExpired(this X509Certificate2 cert)
        {
            return cert.HasExpired(DateTime.Now);
        }

        public static bool HasExpired(this X509Certificate2 cert, DateTime now)
        {
            //
            // Times are in local time
            //
            return (now < cert.NotAfter);
        }            
        
        public static X509Extension FindExtension(this X509Certificate2 cert, string friendlyName)
        {
            if (string.IsNullOrEmpty(friendlyName))
            {
                throw new ArgumentException();
            }
            
            X509ExtensionCollection extensions = cert.Extensions;
            if (extensions != null)
            {
                for (int i = 0, count = extensions.Count; i < count; ++i)
                {
                    X509Extension extension = extensions[i];
                    if (extension.Oid.FriendlyName.Equals(friendlyName, StringComparison.OrdinalIgnoreCase))
                    {
                        return extension;
                    }
                }
            }
            
            return null;
        }

        public static X509BasicConstraintsExtension FindBasicExtension(this X509Certificate2 cert)
        {
            return (X509BasicConstraintsExtension) cert.FindExtension("Basic Constraints");
        }

        public static X509KeyUsageExtension FindKeyUsageExtension(this X509Certificate2 cert)
        {
            return (X509KeyUsageExtension) cert.FindExtension("Key Usage");
        }
        
        public static bool IsCertificateAuthority(this X509Certificate2 cert)
        {
            X509BasicConstraintsExtension extension = cert.FindBasicExtension();            
            return (extension != null) ? extension.CertificateAuthority : false;
        }

        //---------------------------------------
        //
        // OidCollection
        //
        //---------------------------------------
        public static void Add(this OidCollection oids, OidCollection newOids)
        {
            if (newOids == null)
            {
                throw new ArgumentNullException();
            }
            
            for (int i = 0, count = newOids.Count; i < count; ++i)
            {
                oids.Add(newOids[i]);
            }
        }

        public static void Add(this OidCollection oids, IEnumerable<Oid> newOids)
        {
            if (newOids == null)
            {
                throw new ArgumentNullException();
            }

            foreach(Oid oid in oids)
            {
                oids.Add(oid);
            }
        }
        
        //---------------------------------------
        //
        // X509ChainPolicy
        //
        //---------------------------------------
        public static X509ChainPolicy Clone(this X509ChainPolicy policy)
        {
            X509ChainPolicy newPolicy = new X509ChainPolicy();
            newPolicy.ApplicationPolicy.Add(policy.ApplicationPolicy);
            newPolicy.CertificatePolicy.Add(policy.CertificatePolicy);
            newPolicy.ExtraStore.Add(policy.ExtraStore);
            newPolicy.RevocationFlag = policy.RevocationFlag;
            newPolicy.RevocationMode = policy.RevocationMode;
            newPolicy.UrlRetrievalTimeout = policy.UrlRetrievalTimeout;
            newPolicy.VerificationFlags = policy.VerificationFlags;

            return newPolicy;
        }

        //---------------------------------------
        //
        // X509ChainElementCollection
        //
        //---------------------------------------
        public static bool IsNullOrEmpty(this X509ChainElementCollection chainElements)
        {
            return (chainElements == null || chainElements.Count == 0);
        }
    }
 }
