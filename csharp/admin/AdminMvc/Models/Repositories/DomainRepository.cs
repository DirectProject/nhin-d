using System.Linq;

using Health.Direct.Config.Client.DomainManager;
using Health.Direct.Config.Store;

namespace AdminMvc.Models.Repositories
{
    public interface IDomainRepository : IRepository<Domain>
    {
        Domain GetByDomainName(string domainName);
    }

    public class DomainRepository : IDomainRepository
    {
        private readonly DomainManagerClient m_client;

        public DomainRepository()
        {
            m_client = new DomainManagerClient();
        }

        protected DomainManagerClient Client { get { return m_client; } }
        
        public IQueryable<Domain> FindAll()
        {
            return Client.EnumerateDomains(null, int.MaxValue).AsQueryable();
        }

        public Domain Add(Domain domain)
        {
            return Client.AddDomain(domain);
        }

        public void Update(Domain domain)
        {
            Client.UpdateDomain(domain);
        }

        public void Delete(Domain domain)
        {
            // TODO: this should be moved to the server-side implementation to hide this detail from the client
            new AddressManagerClient().RemoveDomainAddresses(domain.ID);
            Client.RemoveDomain(domain.Name);
        }

        public Domain Get(long id)
        {
            return Client.GetDomain(id);
        }

        public Domain GetByDomainName(string domainName)
        {
            return Client.GetDomains(new[] {domainName}, null).SingleOrDefault();
        }
    }
}