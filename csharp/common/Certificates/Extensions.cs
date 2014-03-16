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
using System.Net.Mail;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.Security.Cryptography.Pkcs;
using Health.Direct.Common.Extensions;

namespace Health.Direct.Common.Certificates
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

        /// <summary>
        /// Adds an enumeration of certificates to a store
        /// </summary>
        /// <param name="store">The store to which to add certifiates</param>
        /// <param name="certs">The enumeration of <see cref="X509Certificate2"/> instances to add</param>
        public static void Add(this X509Store store, IEnumerable<X509Certificate2> certs)
        {
            if (certs == null)
            {
                throw new ArgumentNullException("certs");
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
        /// <summary>
        /// Tests if the collection is <c>null</c> or has 0 entries.
        /// </summary>
        /// <param name="certs">The collection to test.</param>
        /// <returns><c>true</c> if the collection is null or has 0 entries, <c>false</c> if the collection has 1 or more entries</returns>
        public static bool IsNullOrEmpty(this X509Certificate2Collection certs)
        {
            return (certs == null || certs.Count == 0);
        }

        /// <summary>
        /// Adds certificates from the supplied collection to this collection.
        /// </summary>
        /// <param name="certs">The collection to which to add certificates.</param>
        /// <param name="newCerts">The collection from which to add certificates.</param>
        public static void Add(this X509Certificate2Collection certs, X509Certificate2Collection newCerts)
        {
            if (newCerts == null)
            {
                throw new ArgumentNullException("newCerts");
            }

            foreach (X509Certificate2 cert in newCerts)
            {
                certs.Add(cert);
            }
        }

        /// <summary>
        /// Adds certificates from the supplied enumeration to this collection.
        /// </summary>
        /// <param name="certs">The collection to which to add certificates.</param>
        /// <param name="newCerts">The enumeration from which to add certificates.</param>
        public static void Add(this X509Certificate2Collection certs, IEnumerable<X509Certificate2> newCerts)
        {
            if (newCerts == null)
            {
                throw new ArgumentNullException("newCerts");
            }
            
            foreach(X509Certificate2 cert in newCerts)
            {
                certs.Add(cert);
            }
        }
        
        /// <summary>
        /// Supplies an enumeration for this collection.
        /// </summary>
        /// <param name="certs">The collection to enumerate.</param>
        /// <returns>The enumerator for this collection.</returns>
        public static IEnumerable<X509Certificate2> Enumerate(this X509Certificate2Collection certs)
        {
            return certs.Enumerate(null);
        }

        /// <summary>
        /// Supplies an filtered enumeration for this collection.
        /// </summary>
        /// <param name="certs">The collection to enumerate.</param>
        /// <param name="filter">The filter testing each element of the source collection for enumeration. Elements for which the filter returns <c>false</c> will not be returned by the enumerator.</param>
        /// <returns>The enumerator for this collection.</returns>
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
        
        /// <summary>
        /// Returns the index of the first certificate matching the supplied <paramref name="matcher"/>.
        /// </summary>
        /// <param name="certs">The source collection to test.</param>
        /// <param name="matcher">The matching predicate for which the index of the first matching element will be returned.</param>
        /// <returns>The zero-based index of the first matching element, or -1 if no matching elements are found</returns>
        public static int IndexOf(this X509Certificate2Collection certs, Predicate<X509Certificate2> matcher)
        {
            if (matcher == null)
            {
                throw new ArgumentNullException("matcher");
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

        /// <summary>
        /// Returns a subset of this collection whose elements match the supplied <paramref name="match"/> function.
        /// </summary>
        /// <param name="certs">The source collection.</param>
        /// <param name="match">The predicate for which all elements that return <c>will</c> be selected.</param>
        /// <returns>The collection of matched elements, or <c>null</c> if no matched elements are found.</returns>
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

        /// <summary>
        /// Returns the first element matching the supplied predicate.
        /// </summary>
        /// <param name="certs">The source collection to test.</param>
        /// <param name="matcher">The matching predicate for which the first matching element will be returned.</param>
        /// <returns>The first matching element, or <c>null</c> if no matching elements are found.</returns>
        public static X509Certificate2 Find(this X509Certificate2Collection certs, Predicate<X509Certificate2> matcher)
        {
            int index = certs.IndexOf(matcher);
            if (index >= 0)
            {
                return certs[index];
            }
            
            return null;
        }

        /// <summary>
        /// Returns the first certificate in this collection that matches <paramref name="name"/> by <c>CN</c>
        /// </summary>
        /// <param name="certs">The certificates to search</param>
        /// <param name="name">The name value to test against the <c>CN</c></param>
        /// <returns>The first matching element, or <c>null</c> if no matching elements are found.</returns>
        public static X509Certificate2 FindByName(this X509Certificate2Collection certs, string name)
        {
            if (string.IsNullOrEmpty(name))
            {
                throw new ArgumentException("value was null or empty", "name");
            }
            
            return certs.Find(x => x.MatchName(name));
        }

        /// <summary>
        /// Return the first matching element whose certificate thumbprint matches the supplied <paramref name="thumbprint"/>
        /// </summary>
        /// <param name="certs">The source collection to test.</param>
        /// <param name="thumbprint">The certificate thumbprint, as a string, to test against the source collection</param>
        /// <returns>The first matching element, or <c>null</c> if no matching elements are found.</returns>
        public static X509Certificate2 FindByThumbprint(this X509Certificate2Collection certs, string thumbprint)
        {
            if (string.IsNullOrEmpty(thumbprint))
            {
                throw new ArgumentException("value was null or empty", "thumbprint");
            }

            return certs.Find(x => x.Thumbprint == thumbprint);
        }
        
        /// <summary>
        /// Returns true if the collection contains at least one certificate with the given thumbprint
        /// </summary>
        /// <param name="certs">The source collection to test</param>
        /// <param name="thumbprint">The certificate thumbprint, as a string, to test against the source collection</param>
        /// <returns>true if found, otherwise false</returns>
        public static bool ContainsThumbprint(this X509Certificate2Collection certs, string thumbprint)
        {
            return (certs.FindByThumbprint(thumbprint) != null);
        }
                        
        /// <summary>
        /// Return the first certificate that is valid on the supplied <paramref name="usageTime"/>
        /// </summary>
        /// <param name="certs">The certificates to search</param>
        /// <param name="usageTime">The date to test the supplied certificates against.</param>
        /// <returns>The first maching certificate, or <c>null</c> if none match</returns>
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

        /// <summary>
        /// Return the first certificate that is currently valid
        /// </summary>
        /// <param name="certs">The certificates to search</param>
        /// <returns>The first maching certificate, or <c>null</c> if none match</returns>
        public static X509Certificate2 FindUsable(this X509Certificate2Collection certs)
        {
            return certs.FindUsable(DateTime.Now);
        }


        /// <summary>
        /// Return the first certificate that is currently valid
        /// </summary>
        /// <param name="certs">The certificates to search</param>
        /// <returns>The first maching certificate, or <c>null</c> if none match</returns>
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
        
        /// <summary>
        /// Call Dispose or Reset on all certs in the collection - to free up system resource usage. 
        /// </summary>
        /// <param name="certs">Certificate collection</param>
        /// <param name="catchExceptions">if true, will handle exceptions and keep going...</param>
        public static void Close(this X509Certificate2Collection certs, bool catchExceptions)
        {
            //
            // If the collection is disposable, use that..
            //
            IDisposable disposable = certs as IDisposable;
            if (disposable != null)
            {
                disposable.Dispose();
                return;
            }            
            //
            // else, dispose manually
            //
            foreach(X509Certificate2 cert in certs)
            {
                try
                {
                    if (cert != null)
                    {
                        cert.Close();
                    }
                }
                catch
                {
                    if (!catchExceptions)
                    {
                        throw;
                    }
                }
            }
        }
               
        //---------------------------------------
        //
        // X509Certificate Extensions
        //
        //---------------------------------------
        /// <summary>
        /// Tests the supplied certificate against a <c>CN</c> value
        /// </summary>
        /// <param name="cert">The certificate to test</param>
        /// <param name="name">The <c>CN</c> value to test</param>
        /// <returns><c>true</c> if the certificate matches by subject name, <c>false</c> otherwise.</returns>
        public static bool MatchName(this X509Certificate2 cert, string name)
        {
            if (string.IsNullOrEmpty(name))
            {
                throw new ArgumentException("value was null or empty", "name");
            }

            string distinguishedName = cert.GetNameInfo(X509NameType.SimpleName, false);
            if (string.IsNullOrEmpty(distinguishedName))
            {
                return false;
            }
            return string.Equals(distinguishedName, name, StringComparison.OrdinalIgnoreCase);
        }

        /// <summary>
        /// Tests the supplied certificate against a <c>E</c> (email) value
        /// </summary>
        /// <param name="cert">The certificate to test</param>
        /// <param name="name">The <c>E</c> value to test</param>
        /// <returns><c>true</c> if the certificate matches by email, <c>false</c> otherwise.</returns>
        public static bool MatchEmailName(this X509Certificate2 cert, string name)
        {
            if (string.IsNullOrEmpty(name))
            {
                throw new ArgumentException("value was null or empty", "name");
            }
            
            string distinguishedName = cert.GetNameInfo(X509NameType.EmailName, false);
            if (string.IsNullOrEmpty(distinguishedName))
            {
                return false;
            }
            return string.Equals(distinguishedName, name, StringComparison.OrdinalIgnoreCase);
        }

        /// <summary>
        /// Tests the supplied certificate against a <c>DNS</c> value
        /// </summary>
        /// <param name="cert">The certificate to test</param>
        /// <param name="name">The <c>DNS</c> value to test</param>
        /// <returns><c>true</c> if the certificate matches by DNS domain, <c>false</c> otherwise.</returns>
        public static bool MatchDnsName(this X509Certificate2 cert, string name)
        {
            if (string.IsNullOrEmpty(name))
            {
                throw new ArgumentException("value was null or empty", "name");
            }

            string distinguishedName = cert.GetNameInfo(X509NameType.DnsName, false);
            if (string.IsNullOrEmpty(distinguishedName))
            {
                return false;
            }
            return string.Equals(distinguishedName, name, StringComparison.OrdinalIgnoreCase);
        }
        
        /// <summary>
        /// Matches a certificate by either subject name or email.
        /// </summary>
        /// <param name="cert">The certificate to test</param>
        /// <param name="name">The subject name or email address to test</param>
        /// <returns><c>true</c> if the certificate matches by email or subject name, <c>false</c> otherwise.</returns>
        public static bool MatchEmailNameOrName(this X509Certificate2 cert, string name)
        {
            return (cert.MatchEmailName(name) || cert.MatchName(name));
        }

        /// <summary>
        /// Matches a certificate by either subject name, email, or dns name
        /// </summary>
        /// <param name="cert">The certificate to test</param>
        /// <param name="name">The subject name or email address to test</param>
        /// <returns><c>true</c> if the certificate matches by email or subject name, <c>false</c> otherwise.</returns>
        public static bool MatchDnsOrEmailOrName(this X509Certificate2 cert, string name)
        {
            return (cert.MatchDnsName(name) || cert.MatchEmailName(name)); 
        }

        /// <summary>
        /// Tests the supplied certificate if the subject name begins with the supplied <paramref name="name"/> string
        /// </summary>
        /// <param name="cert">The certificate to test.</param>
        /// <param name="name">The <see cref="string"/> to test against the <c>CN</c> value</param>
        /// <returns><c>true</c> if the supplied certificate's subject name matches the supplied <paramref name="name"/>, <c>false</c> otherwise.</returns>
        public static bool NameContains(this X509Certificate2 cert, string name)
        {
            if (string.IsNullOrEmpty(name))
            {
                throw new ArgumentException("value was null or empty", "name");
            }

            string distinguishedName = cert.GetNameInfo(X509NameType.SimpleName, false);
            if (string.IsNullOrEmpty(distinguishedName))
            {
                return false;
            }

            return (distinguishedName.Contains(name, StringComparison.OrdinalIgnoreCase));
        }

        /// <summary>
        /// Extracts the email (<c>E</c> (by preference) or subject name (<c>CN</c>) value from this certificate.
        /// </summary>
        /// <param name="cert">The certificate from which to extract the name</param>
        /// <returns>The <c>E</c> value, or the <c>CN</c> value if <c>E</c> is not found or <c>null</c> if neither are found</returns>
        public static string ExtractEmailNameOrName(this X509Certificate2 cert)
        {
            return cert.ExtractEmailNameOrName(false);
        }

        /// <summary>
        /// Extracts the email (<c>E</c> (by preference) or subject name (<c>CN</c>) value from this certificate.
        /// </summary>
        /// <param name="cert">The certificate from which to extract the name</param>
        /// <param name="issuer">issuer</param>
        /// <returns>The <c>E</c> value, or the <c>CN</c> value if <c>E</c> is not found or <c>null</c> if neither are found</returns>
        public static string ExtractEmailNameOrName(this X509Certificate2 cert, bool issuer)
        {
            string name = cert.GetNameInfo(X509NameType.EmailName, issuer);
            if (string.IsNullOrEmpty(name))
            {
                name = cert.GetNameInfo(X509NameType.SimpleName, issuer);
            }

            return name;
        }
        
        /// <summary>
        /// Tests if the supplied certificate has a currently valid date.
        /// </summary>
        /// <param name="cert">The certificate to test</param>
        /// <returns><c>true</c> if it is valid by date as of now, <c>false</c> otherwise.</returns>
        public static bool HasValidDateRange(this X509Certificate2 cert)
        {
            return cert.HasValidDateRange(DateTime.Now);     
        }

        /// <summary>
        /// Tests if the supplied certificate is valid by date as of the supplied <see cref="DateTime"/>
        /// </summary>
        /// <param name="cert">The certificate to test</param>
        /// <param name="time">The <see cref="DateTime"/> to test this certificate against.</param>
        /// <returns><c>true</c> if it is valid by date, <c>false</c> otherwise.</returns>
        public static bool HasValidDateRange(this X509Certificate2 cert, DateTime time)
        {
            //
            // Times are in local time
            //
            return (cert.NotBefore < time && time < cert.NotAfter);
        }
        
        /// <summary>
        /// Tests if a certificate has expired at the current time.
        /// </summary>
        /// <param name="cert">The certificate to test</param>
        /// <returns><c>true</c> if the certificate is currently expired, <c>false if not</c></returns>
        public static bool HasExpired(this X509Certificate2 cert)
        {
            return cert.HasExpired(DateTime.Now);
        }

        /// <summary>
        /// Tests if a certificate has expired at the indicated <see cref="DateTime"/>.
        /// </summary>
        /// <param name="cert">The certificate to test</param>
        /// <param name="time">The <see cref="DateTime"/> to test this certificate against.</param>
        /// <returns><c>true</c> if the certificate is expired at the indicated <see cref="DateTime"/>, <c>false if not</c></returns>
        public static bool HasExpired(this X509Certificate2 cert, DateTime time)
        {
            //
            // Times are in local time
            //
            return (time < cert.NotAfter);
        }            
        
        // TODO: Should be name ExtractExtension to be consistent with ExtractFoo above
        /// <summary>
        /// Extracts a certificate extension valid by name
        /// </summary>
        /// <param name="cert">The certificate from which to extract the extension value.</param>
        /// <param name="friendlyName">The extension name (see <see cref="Oid"/>) to test.</param>
        /// <returns>The <see cref="X509Extension"/> value of the extension or <c>null</c> if none is found.</returns>
        public static X509Extension FindExtension(this X509Certificate2 cert, string friendlyName)
        {
            if (string.IsNullOrEmpty(friendlyName))
            {
                throw new ArgumentException("value was null or empty", "friendlyName");
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

        /// <summary>
        /// Extracts the "Basic Constraints" extension value
        /// </summary>
        /// <param name="cert">The certificate from which to extract the "Basic Constraints"</param>
        /// <returns>The <see cref="X509BasicConstraintsExtension"/> for this certificate, or <c>null</c>
        /// if not found.</returns>
        public static X509BasicConstraintsExtension FindBasicExtension(this X509Certificate2 cert)
        {
            return (X509BasicConstraintsExtension) cert.FindExtension("Basic Constraints");
        }

        /// <summary>
        /// Extracts the "Key Usage" extension value
        /// </summary>
        /// <param name="cert">The certificate from which to extract the "Key Usage"</param>
        /// <returns>The <see cref="X509KeyUsageExtension"/> for this certificate, or <c>null</c>
        /// if not found.</returns>
        public static X509KeyUsageExtension FindKeyUsageExtension(this X509Certificate2 cert)
        {
            return (X509KeyUsageExtension) cert.FindExtension("Key Usage");
        }
        
        /// <summary>
        /// Tests if this certificate is a CA cert.
        /// </summary>
        /// <param name="cert">The certificate to test</param>
        /// <returns><c>true</c> if the Basic Constraints for this certificate indicate it is a CA cert, <c>false</c> otherwise</returns>
        public static bool IsCertificateAuthority(this X509Certificate2 cert)
        {
            X509BasicConstraintsExtension extension = cert.FindBasicExtension();            
            return (extension != null) ? extension.CertificateAuthority : false;
        }

        /// <summary>
        /// If a certificate is issued by this anchor, then it must proffer these additional Oids to be truly trusted
        /// </summary>    
        public static Oid[] GetRequiredOidsForIssuedCerts(this X509Certificate2 cert)        
        {
            AnchorX509Certificate2 anchorCert = cert as AnchorX509Certificate2;
            if (anchorCert != null && anchorCert.HasMetadata)
            {
                return anchorCert.Metadata.RequiredOids;
            }
            
            return null;
        }

        //---------------------------------------
        //
        // OidCollection
        //
        //---------------------------------------
        /// <summary>
        /// Adds a collection of <see cref="Oid"/> instances to this collection.
        /// </summary>
        /// <param name="oids">The collection to which to add values</param>
        /// <param name="newOids">The collection to add from</param>
        public static void Add(this OidCollection oids, OidCollection newOids)
        {
            if (newOids == null)
            {
                throw new ArgumentNullException("newOids");
            }
            
            for (int i = 0, count = newOids.Count; i < count; ++i)
            {
                oids.Add(newOids[i]);
            }
        }

        /// <summary>
        /// Adds an enumeration of <see cref="Oid"/> instances to this collection.
        /// </summary>
        /// <param name="oids">The collection to which to add values</param>
        /// <param name="newOids">The enumeration to add from</param>
        public static void Add(this OidCollection oids, IEnumerable<Oid> newOids)
        {
            if (newOids == null)
            {
                throw new ArgumentNullException("newOids");
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
        /// <summary>
        /// Performs a shallow clone or the specified <see cref="X509ChainPolicy"/>
        /// </summary>
        /// <param name="policy">The instance to clone.</param>
        /// <returns>The shallow cloned instance.</returns>
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

        /// <summary>
        /// Tests if the collection is null or contains 0 elements.
        /// </summary>
        /// <param name="chainElements">The collection to test</param>
        /// <returns><c>true</c> if the collection is null or has 0 elements, <c>false</c> otherwise.</returns>
        public static bool IsNullOrEmpty(this X509ChainElementCollection chainElements)
        {
            return (chainElements == null || chainElements.Count == 0);
        }

        //---------------------------------------
        //
        // ICertificateResolver
        //
        //---------------------------------------
        /// <summary>
        /// Calls GetCertificates, catches exceptions
        /// Returns null if exceptions
        /// </summary>
        /// <param name="resolver">certificate resolver</param>
        /// <param name="address">Retrieve certificates for this address</param>
        /// <returns>
        /// A <see cref="System.Security.Cryptography.X509Certificates.X509Certificate2Collection"/> or null if there are no matches.
        /// </returns>
        public static X509Certificate2Collection SafeGetCertificates(this ICertificateResolver resolver, MailAddress address)
        {
            try
            {
                return resolver.GetCertificates(address);
            }
            catch
            {
            }
            
            return null;
        }

        /// <summary>
        /// Calls GetCertificates, catches exceptions
        /// Returns null if exceptions
        /// </summary>
        /// <param name="resolver">certificate resolver</param>
        /// <param name="emailOrDomain">Retrieve certificates for this domain</param>
        /// <returns>
        /// A <see cref="System.Security.Cryptography.X509Certificates.X509Certificate2Collection"/> or null if there are no matches.
        /// </returns>
        public static X509Certificate2Collection SafeGetCertificates(this ICertificateResolver resolver, string emailOrDomain)
        {
            try
            {
                MailAddress address = new MailAddress(emailOrDomain);
                X509Certificate2Collection matches = resolver.GetCertificates(address);
                if (!matches.IsNullOrEmpty())
                {
                    return matches;
                }

                emailOrDomain = address.Host;
            }
            catch
            {
            }
 
            try
            {
                return resolver.GetCertificatesForDomain(emailOrDomain);
            }
            catch
            {
            }

            return null;
        }
        
        /// <summary>
        /// If the certificate is disposable, disposes it.
        /// Else calls Reset
        /// </summary>
        /// <param name="cert">cert to dispose</param>
        public static void Close(this X509Certificate2 cert)
        {
            IDisposable disposable = cert as IDisposable;
            if (disposable != null)
            {
                disposable.Dispose();
            }
            else
            {
                cert.Reset();
            }            
        }
        
        /// <summary>
        /// Used by Resolvers to fire events
        /// </summary>
        /// <param name="handler">Event handler to fire, if any subscribers</param>
        /// <param name="resolver">resolver for which this is an event handler</param>
        /// <param name="ex">exception to notify</param>
        public static void NotifyEvent(this Action<ICertificateResolver, Exception> handler, ICertificateResolver resolver, Exception ex)
        {
            if (handler != null)
            {
                try
                {
                    handler(resolver, ex);
                }
                catch
                {
                }
            }
        }
 
        //---------------------------------------
        //
        // PKCS
        //
        //---------------------------------------
        /// <summary>
        /// Checks if the given SignedCms object has signatures
        /// </summary>
        /// <param name="cms">Cms object</param>
        /// <returns>true if signatures present</returns>
        public static bool HasSignatures(this SignedCms cms)
        {
            return (!cms.SignerInfos.IsNullOrEmpty());
        }
        
        /// <summary>
        /// Checks if the given SignedCms object has encapsulated content
        /// </summary>
        /// <param name="cms">Cms object</param>
        /// <returns>true if content present</returns>
        public static bool HasContent(this SignedCms cms)
        {
            return (cms.ContentInfo != null && !cms.ContentInfo.Content.IsNullOrEmpty());
        }
    }
}