using System;
using System.Linq;

using Health.Direct.Config.Client.DomainManager;
using Health.Direct.Config.Store;

namespace AdminMvc.Models
{
    public class DomainRepository : Repository<Domain>
    {
        private readonly DomainManagerClient m_client;

        public DomainRepository()
        {
            m_client = new DomainManagerClient();
        }

        protected DomainManagerClient Client { get { return m_client; } }
        
        public override IQueryable<Domain> FindAll()
        {
            return Client.EnumerateDomains(null, int.MaxValue).AsQueryable();
        }

        public override Domain Add(Domain domain)
        {
            return Client.AddDomain(domain);
        }

        public override void Update(Domain domain)
        {
            Client.UpdateDomain(domain);
        }

        public override void Delete(Domain domain)
        {
            Client.RemoveDomain(domain.Name);
        }

        public override Domain Get(long id)
        {
            return Client.GetDomain(id);
        }

        public Domain GetByDomainName(string domainName)
        {
            return Client.GetDomains(new[] {domainName}, null).SingleOrDefault();
        }
    }
}
