using System;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using Health.Direct.Common.Diagnostics;
using Health.Direct.Config.Store;

namespace Health.Direct.Config.Service.Controllers
{
    [RoutePrefix("Domain")]
    public class DomainController : ApiController
    {
        private readonly ConfigStore _configStore;
        private readonly ILogger _logger;

        public DomainController()
        {
            _configStore = Service.Current.Store;
            _logger = Log.For(this);
        }

        [HttpPost]
        [Route("")]
        public HttpResponseMessage AddDomain([FromBody] Domain domain)
        {
            try
            {
                var newDomain = _configStore.Domains.Add(domain);

                return Request.CreateResponse(HttpStatusCode.Created, newDomain);
            }
            catch (Exception ex)
            {
                _logger.Info("GetDomain failed with exception.", ex);
                return Request.CreateResponse(HttpStatusCode.InternalServerError);
            }
            
        }

        [HttpPut]
        [Route("")]
        public HttpResponseMessage UpdateDomain([FromBody] Domain domain)
        {
            try
            {
                _configStore.Domains.Update(domain);

                return Request.CreateResponse(HttpStatusCode.OK);
            }
            catch (Exception ex)
            {
                _logger.Info("GetDomain failed with exception.", ex);
                return Request.CreateResponse(HttpStatusCode.InternalServerError);
            }
        }

        [HttpGet]
        [Route("Count")]
        public HttpResponseMessage GetDomainCount()
        {
            try
            {
                var count = _configStore.Domains.Count();

                return Request.CreateResponse(HttpStatusCode.OK, count);
            }
            catch (Exception ex)
            {
                _logger.Info("EnumerateDomains failed with exception.", ex);
                return Request.CreateResponse(HttpStatusCode.InternalServerError);
            }
        }

        [HttpGet]
        [Route("{id}")]
        public HttpResponseMessage GetDomain(long id)
        {
            try
            {
                var domain = _configStore.Domains.Get(id);

                return Request.CreateResponse(HttpStatusCode.OK, domain);
            }
            catch (Exception ex)
            {
                _logger.Info("GetDomain failed with exception.", ex);
                return Request.CreateResponse(HttpStatusCode.InternalServerError);
            }
        }

//        [HttpGet]
//        public HttpResponseMessage GetDomains(string[] domainNames, EntityStatus? status)
//        {
//            try
//            {
//                var domains = _configStore.Domains.Get(domainNames, status);
//
//                return Request.CreateResponse(HttpStatusCode.OK, domains);
//            }
//            catch (Exception ex)
//            {
//                _logger.Info("GetDomains failed with exception.", ex);
//                return Request.CreateResponse(HttpStatusCode.InternalServerError);
//            }
//        }

        [HttpGet]
        [Route("Agent/{agentName}/{status}")]
        public HttpResponseMessage GetAgentDomains(string agentName, EntityStatus? status)
        {
            try
            {
                var domains = _configStore.Domains.Get(agentName, status);

                return Request.CreateResponse(HttpStatusCode.OK, domains);
            }
            catch (Exception ex)
            {
                _logger.Info("GetAgentDomains failed with exception.", ex);
                return Request.CreateResponse(HttpStatusCode.InternalServerError);
            }
        }

        [HttpDelete]
        public HttpResponseMessage RemoveDomain(Domain domain)
        {
            try
            {
                _configStore.Domains.Remove(domain.Name);

                return Request.CreateResponse(HttpStatusCode.OK);
            }
            catch (Exception ex)
            {
                _logger.Info("RemoveDomain failed with exception.", ex);
                return Request.CreateResponse(HttpStatusCode.InternalServerError);
            }
        }

        [HttpGet]
        [Route("Enumerate/{lastDomainName}/{maxResults}")]
        public HttpResponseMessage EnumerateDomain(string lastDomainName, int maxResults)
        {
            try
            {
                var domains = _configStore.Domains.Get(lastDomainName, maxResults);

                return Request.CreateResponse(HttpStatusCode.OK, domains);
            }
            catch (Exception ex)
            {
                _logger.Info("EnumerateDomains failed with exception.", ex);
                return Request.CreateResponse(HttpStatusCode.InternalServerError);
            }
        }
    }
}
