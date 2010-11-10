using Autofac;

namespace AdminMvc.Models.Repositories
{
    public class RepositoryModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder.RegisterType<AddressRepository>().As<IAddressRepository>();
            builder.RegisterType<AnchorRepository>().As<IAnchorRepository>();
            builder.RegisterType<CertificateRepository>().As<ICertificateRepository>();
            builder.RegisterType<DomainRepository>().As<IDomainRepository>();
        }
    }
}