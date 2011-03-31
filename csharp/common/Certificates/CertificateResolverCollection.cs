using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Collections;

namespace Health.Direct.Common.Certificates
{
    /// <summary>
    /// A collection of Certificate Resolvers, which is itself a certificate resolver
    /// This allows you to chain together a series of resolvers. 
    /// Example: You could have Dns as your primary resolver, then fallback to a backup resolver that
    /// users a machine store. 
    /// </summary>
    public class CertificateResolverCollection : ObjectCollection<ICertificateResolver>, ICertificateResolver
    {
        /// <summary>
        /// Creates a new CertificateResolverCollection
        /// </summary>
        public CertificateResolverCollection()
        {
            this.CatchExceptions = true;
        }
        
        /// <summary>
        /// If true, will catch exceptions and fall through to the next resolver in line
        /// Allows you to set up a pipeline of backup resolvers
        /// True by default
        /// </summary>
        public bool CatchExceptions
        {
            get;
            set;
        }
        
        /// <summary>
        /// Event to subscribe to for notification of errors.
        /// </summary>
        public event Action<ICertificateResolver, Exception> Error;

        /// <summary>
        /// Resolves X509 certificates for a mail address.
        /// </summary>
        /// <param name="address">The <see cref="MailAddress"/> instance to resolve. Will try each
        /// resolver in the collection until one returns matches.</param>
        /// <returns>An <see cref="X509Certificate2Collection"/> of X509 certifiates for the address,
        /// or <c>null</c> if no certificates are found.</returns>        
        public X509Certificate2Collection GetCertificates(MailAddress address)
        {
            X509Certificate2Collection matches = null;
            foreach(ICertificateResolver resolver in this)
            {
                try
                {
                    matches = resolver.GetCertificates(address);
                    if (!matches.IsNullOrEmpty())
                    {
                        break;
                    }
                }
                catch(Exception ex)
                {
                    this.NotifyException(resolver, ex);
                    if (!this.CatchExceptions)
                    {
                        throw;
                    }
                }
            }
            
            return matches;
        }

        /// <summary>
        /// Returns the valid certificates for a domain. Will try each resolver in the collection until one returns matches
        /// </summary>
        /// <param name="domain">domain</param>
        /// <returns>
        /// A <see cref="System.Security.Cryptography.X509Certificates.X509Certificate2Collection"/> or null if there are no matches.
        /// </returns>
        public X509Certificate2Collection GetCertificatesForDomain(string domain)
        {
            X509Certificate2Collection matches = null;
            foreach (ICertificateResolver resolver in this)
            {
                try
                {
                    matches = resolver.GetCertificatesForDomain(domain);
                    if (!matches.IsNullOrEmpty())
                    {
                        break;
                    }
                }
                catch(Exception ex)
                {
                    this.NotifyException(resolver, ex);
                    if (!this.CatchExceptions)
                    {
                        throw;
                    }
                }
            }

            return matches;
        }

        void NotifyException(ICertificateResolver resolver, Exception ex)
        {
            var errorHandler = this.Error;
            if (errorHandler != null)
            {
                try
                {
                    errorHandler(resolver, ex);
                }
                catch
                {
                }
            }
        }
    }
}
