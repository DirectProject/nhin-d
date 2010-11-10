using System;
using System.Collections.Generic;
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
            Delete(anchor);
            Add(anchor);
        }

        public Anchor Get(long id)
        {
            return Client.GetAnchors(new[] {id}, null).SingleOrDefault();
        }

        public Anchor Get(string owner, string thumbprint)
        {
            return (from anchor in Client.GetAnchorsForOwner(owner, null)
                    where anchor.Thumbprint == thumbprint
                    select anchor).SingleOrDefault();
        }
    }
}