using System;
using System.Linq;

using Health.Direct.Config.Client.CertificateService;
using Health.Direct.Config.Store;

namespace AdminMvc.Models.Repositories
{
    public class CertificateRepository : ICertificateRepository
    {
        private readonly CertificateStoreClient m_client;

        public CertificateRepository()
        {
            m_client = new CertificateStoreClient();
        }

        protected CertificateStoreClient Client { get { return m_client; } }
        
        public IQueryable<Certificate> FindAll()
        {
            return Client.EnumerateCertificates(0, int.MaxValue, null).AsQueryable();
        }

        public Certificate Add(Certificate certificate)
        {
            return Client.AddCertificate(certificate);
        }

        public void Delete(Certificate certificate)
        {
            Client.RemoveCertificates(new[] {certificate.ID});
        }

        public void Update(Certificate certificate)
        {
            throw new NotSupportedException("Updates are not supported on Certificates");
        }

        public Certificate ChangeStatus(Certificate certificate, EntityStatus status)
        {
            Client.SetCertificateStatus(new[] { certificate.ID}, status);
            certificate.Status = status;
            return certificate;
        }

        public Certificate Get(long id)
        {
            // TODO: Replace with GetCertificateById()
            return (from certificate in FindAll()
                    where certificate.ID == id
                    select certificate).SingleOrDefault();
        }

        public Certificate Get(string owner, string thumbprint)
        {
            return (from certificate in Client.GetCertificatesForOwner(owner, null)
                    where certificate.Thumbprint == thumbprint
                    select certificate).SingleOrDefault();
        }
    }
}