using System.Linq;

using Health.Direct.Config.Client.CertificateService;
using Health.Direct.Config.Store;

namespace AdminMvc.Models
{
    public class CertificateRepository : Repository<Certificate>
    {
        private readonly CertificateStoreClient m_client;

        public CertificateRepository()
        {
            m_client = new CertificateStoreClient();
        }

        protected CertificateStoreClient Client { get { return m_client; } }
        
        public override IQueryable<Certificate> FindAll()
        {
            return Client.EnumerateCertificates(0, int.MaxValue, null).AsQueryable();
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

        public override Certificate Add(Certificate certificate)
        {
            return Client.AddCertificate(certificate);
        }

        public override void Delete(Certificate certificate)
        {
            Client.RemoveCertificates(new[] {certificate.ID});
        }

        public override void Update(Certificate certificate)
        {
            Delete(certificate);
            Add(certificate);
        }

        public override Certificate Get(long id)
        {
            // TODO: Replace with GetCertificateById()
            return (from certificate in FindAll()
                    where certificate.ID == id
                    select certificate).SingleOrDefault();
        }
    }
}