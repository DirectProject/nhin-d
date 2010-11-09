using System.Linq;

using Health.Direct.Config.Client.CertificateService;
using Health.Direct.Config.Store;

namespace AdminMvc.Models.Repositories
{
    public interface IAnchorRepository : IRepository<Anchor>
    {
    }

    public class AnchorRepository : IAnchorRepository
    {
        private readonly AnchorStoreClient m_client;

        public AnchorRepository()
        {
            m_client = new AnchorStoreClient();
        }

        protected AnchorStoreClient Client { get { return m_client; } }
        
        public IQueryable<Anchor> FindAll()
        {
            return Client.EnumerateAnchors(0, int.MaxValue, null).AsQueryable();
        }

        //public Address Add(AddressModel model)
        //{
        //    return Client.AddAddress(
        //        new Address
        //            {
        //                DisplayName = model.DisplayName,
        //                DomainID = model.DomainID,
        //                EmailAddress = model.EmailAddress,
        //                Type = model.Type
        //            });
        //}

        public Anchor Add(Anchor anchor)
        {
            return Client.AddAnchor(anchor);
        }

        public void Delete(Anchor anchor)
        {
            Client.RemoveAnchors(new[] {anchor.ID});
        }

        public void Update(Anchor anchor)
        {
            Delete(anchor);
            Add(anchor);
        }

        public Anchor Get(long id)
        {
            return Client.GetAnchors(new[] {id}, null).SingleOrDefault();
        }
    }
}