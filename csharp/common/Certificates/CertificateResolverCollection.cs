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
        /// The resolver loops through each resolver in the list until it is successful
        /// </summary>
        public enum TryNextCriteria
        {
            /// <summary>
            /// Try the next resolver if the current one was not successful
            /// </summary>
            NotFound = 0x01,
            /// <summary>
            /// Try the next resolver if the current one through an exception
            /// </summary>
            Exception = 0x02,
            /// <summary>
            /// Continue always
            /// </summary>
            Always = NotFound | Exception
        }
        /// <summary>
        /// Creates a new CertificateResolverCollection
        /// </summary>
        public CertificateResolverCollection()
        {
            this.TryNextWhen = TryNextCriteria.NotFound;
        }
        
        /// <summary>
        /// If one resolver fails to return certificates, fall through to the next one in order and retries
        /// This property defines when to fall through
        /// </summary>
        public TryNextCriteria TryNextWhen;
        
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
        public virtual X509Certificate2Collection GetCertificates(MailAddress address)
        {
            X509Certificate2Collection matches = null;
            foreach(ICertificateResolver resolver in this)
            {
                try
                {
                    matches = resolver.GetCertificates(address);
                    if (this.IsDone(matches))
                    {
                        break;
                    }
                }
                catch(Exception ex)
                {
                    this.Error.NotifyEvent(resolver, ex);
                    if (this.IsDone(ex))
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
        public virtual X509Certificate2Collection GetCertificatesForDomain(string domain)
        {
            X509Certificate2Collection matches = null;
            foreach (ICertificateResolver resolver in this)
            {
                try
                {
                    matches = resolver.GetCertificatesForDomain(domain);
                    if (this.IsDone(matches))
                    {
                        break;
                    }
                }
                catch(Exception ex)
                {
                    this.Error.NotifyEvent(resolver, ex);
                    if (this.IsDone(ex))
                    {
                        throw;
                    }
                }
            }

            return matches;
        }
        
        bool IsDone(X509Certificate2Collection matches)
        {
            // Stop if we got some matches
            // Also stop if no matches, and we were not set up to continue looking
            return (!matches.IsNullOrEmpty() || (this.TryNextWhen & TryNextCriteria.NotFound) == 0);
        }
        
        bool IsDone(Exception ex)
        {
            // Stop trying if we had an exception and we were not set up to continue on exceptions
            return (ex != null && (this.TryNextWhen & TryNextCriteria.Exception) == 0);
        }

        /// <summary>
        /// Notify any subscribers of an error in this resolver
        /// </summary>
        /// <param name="resolver">Resolver with the error</param>
        /// <param name="ex">The error</param>
        protected void NotifyException(ICertificateResolver resolver, Exception ex)
        {
            this.Error.NotifyEvent(resolver, ex);
        }

    }
}
