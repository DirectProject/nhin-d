using System.Linq;

using Health.Direct.Config.Client.CertificateService;
using Health.Direct.Config.Store;

namespace AdminMvc.Models
{
    public class AnchorRepository : Repository<Anchor>
    {
        private readonly AnchorStoreClient m_client;

        public AnchorRepository()
        {
            m_client = new AnchorStoreClient();
        }

        protected AnchorStoreClient Client { get { return m_client; } }
        
        public override IQueryable<Anchor> FindAll()
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

        public override Anchor Add(Anchor anchor)
        {
            return Client.AddAnchor(anchor);
        }

        public override void Delete(Anchor anchor)
        {
            Client.RemoveAnchors(new[] {anchor.ID});
        }

        public override void Update(Anchor anchor)
        {
            Delete(anchor);
            Add(anchor);
        }

        public override Anchor Get(long id)
        {
            return Client.GetAnchors(new[] {id}, null).SingleOrDefault();
        }
    }
}