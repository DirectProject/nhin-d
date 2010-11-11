using Health.Direct.Config.Store;

namespace AdminMvc.Models.Repositories
{
    public interface IDomainRepository : IRepository<Domain>
    {
        Domain GetByDomainName(string domainName);
    }
}