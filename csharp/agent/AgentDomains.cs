using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Mail;
using NHINDirect.Mail;

namespace NHINDirect.Agent
{
    public class AgentDomains
    {
        //
        // Currently, we use this dictionary as a fast lookup table
        // In the future, we may maintain additional state for each domain
        //
        Dictionary<string, string> m_managedDomains;
                
        internal AgentDomains(string[] domains)
        {
            this.SetDomains(domains);
        }
        
        public IEnumerable<string> Domains
        {
            get
            {
                return m_managedDomains.Keys;
            }
        }
        
        public bool IsManaged(MailAddress address)
        {
            if (address == null)
            {
                throw new ArgumentNullException();
            }
            
            return this.IsManaged(address.Host);
        }
        
        public bool IsManaged(string domain)
        {
            if (string.IsNullOrEmpty(domain))
            {
                throw new ArgumentException();
            }
            
            return m_managedDomains.ContainsKey(domain);
        }

        void SetDomains(string[] domains)
        {
            if (!AgentDomains.Validate(domains))
            {
                throw new ArgumentException("domains");                
            }

            m_managedDomains = new Dictionary<string, string>(MailStandard.Comparer); // Case-IN-sensitive
            for (int i = 0; i < domains.Length; ++i)
            {
                string domain = domains[i];
                m_managedDomains[domain] = domain;
            }
        }
        
        internal static bool Validate(string[] domains)
        {
            if (domains == null || domains.Length == 0)
            {
                return false;
            }

            for (int i = 0; i < domains.Length; ++i)
            {
                string domain = domains[i];
                if (string.IsNullOrEmpty(domain))
                {
                    return false;
                }
            }
            
            return true;
        }
    }
}
