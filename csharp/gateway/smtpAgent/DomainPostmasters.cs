using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Mail;
using NHINDirect.Mail;
using NHINDirect.Agent;

namespace NHINDirect.SmtpAgent
{
    internal class DomainPostmasters : IEnumerable<MailAddress>
    {
        const string DefaultPostmasterUserName = "postmaster";
        
        Dictionary<string, MailAddress> m_postmasters;
           
        internal DomainPostmasters()
        {
            m_postmasters = new Dictionary<string,MailAddress>(StringComparer.OrdinalIgnoreCase);
        }
        
        internal void Init(IEnumerable<string> domains, string[] postmasters)
        {
            if (postmasters != null && postmasters.Length > 0)
            {
                for (int i = 0; i < postmasters.Length; ++i)
                {
                    MailAddress address = new MailAddress(postmasters[i]);
                    m_postmasters[address.Host] = address;
                }                        
            }
            
            foreach(string domain in domains)
            {
                if (!m_postmasters.ContainsKey(domain))
                {
                    m_postmasters[domain] = new MailAddress(string.Format("{0}@{1}", DefaultPostmasterUserName, domain));
                }
            }
        }
        
        internal bool IsPostmaster(MailAddress address)
        {
            MailAddress postmaster = null;
            if (!m_postmasters.TryGetValue(address.Host, out postmaster))
            {
                return false;
            }
            
            return (MailStandard.Equals(address.User, postmaster.User));
        }
        
        internal bool IsPostmaster(string address)
        {
            return this.IsPostmaster(new MailAddress(address));
        }

        public IEnumerator<MailAddress> GetEnumerator()
        {
            return m_postmasters.Values.GetEnumerator();
        }

        #region IEnumerable Members

        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
            return this.GetEnumerator();
        }

        #endregion
    }
}
