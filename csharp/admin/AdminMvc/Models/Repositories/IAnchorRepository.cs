using Health.Direct.Config.Store;

namespace AdminMvc.Models.Repositories
{
    public interface IAnchorRepository : IRepository<Anchor>
    {
        Anchor Get(string owner, string thumbprint);
    }
}