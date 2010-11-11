using Health.Direct.Config.Store;

namespace AdminMvc.Models.Repositories
{
    public interface IAnchorRepository : IRepository<Anchor>
    {
        Anchor ChangeStatus(Anchor anchor, EntityStatus status);
    }
}