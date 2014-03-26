using System.Web.Http;
using Health.Direct.Config.Store;

namespace Health.Direct.Config.Service.Controllers
{
    public class DomainController : ApiController
    {
        private readonly ConfigStore _configStore;

        public DomainController()
        {
            _configStore = Service.Current.Store;
        }

        public DomainController(ConfigStore configStore)
        {
            _configStore = configStore;
        }

        public Domain GetDomain(long id)
        {
            return _configStore.Domains.Get(id);
        }
    }
}
