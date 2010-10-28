using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;

namespace Health.Direct.Common.Certificates
{
    public class CombinationResolver : ICertificateResolver
    {
        List<ICertificateResolver> m_resolvers;
        
        public CombinationResolver(IEnumerable<ICertificateResolver> resolvers)
        {
            if (resolvers == null)
            {
                throw new ArgumentNullException("resolvers");
            }
            
            m_resolvers = new List<ICertificateResolver>(resolvers);
        }

        public X509Certificate2Collection GetCertificates(MailAddress address)
        {
            X509Certificate2Collection matches = null;
            for (int i = 0, count = m_resolvers.Count; i < count; ++i)
            {
                matches = m_resolvers[i].GetCertificates(address);
                if (!matches.IsNullOrEmpty())
                {
                    break;
                }
            }

            return matches;
        }

        public X509Certificate2Collection GetCertificatesForDomain(string domain)
        {
            X509Certificate2Collection matches = null;
            for (int i = 0, count = m_resolvers.Count; i < count; ++i)
            {
                matches = m_resolvers[i].GetCertificatesForDomain(domain);
                if (!matches.IsNullOrEmpty())
                {
                    break;
                }
            }

            return matches;
        }
    }
}
