using System;
using System.Linq;

using Health.Direct.Config.Client.CertificateService;
using Health.Direct.Config.Store;

namespace AdminMvc.Models.Repositories
{
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
            throw new NotSupportedException("Updating anchors not supported");
        }

        public Anchor Get(long id)
        {
            return Client.GetAnchors(new[] {id}, null).SingleOrDefault();
        }

        public Anchor ChangeStatus(Anchor anchor, EntityStatus status)
        {
            Client.SetAnchorStatus(new[] { anchor.ID }, status);
            anchor.Status = status;
            return anchor;
        }
    }
}