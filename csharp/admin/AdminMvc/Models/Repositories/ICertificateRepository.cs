using Health.Direct.Config.Store;

namespace AdminMvc.Models.Repositories
{
    public interface ICertificateRepository : IRepository<Certificate>
    {
        Certificate ChangeStatus(Certificate certificate, EntityStatus status);
    }
}