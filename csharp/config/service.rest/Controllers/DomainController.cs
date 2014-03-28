/* 
 Copyright (c) 2014, Direct Project
 All rights reserved.

 Authors:
    Gerald Aden     gerald.aden@surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

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

        [HttpGet]
        [Route("")]
        public HttpResponseMessage GetDomains([FromUri] string[] domainNames, EntityStatus? status)
        {
            try
            {
                var domains = _configStore.Domains.Get(domainNames, status);

                return Request.CreateResponse(HttpStatusCode.OK, domains);
            }
            catch (Exception ex)
            {
                _logger.Info("GetDomains failed with exception.", ex);
                return Request.CreateResponse(HttpStatusCode.InternalServerError);
            }
        }

        [HttpGet]
        [Route("Agent/{agentName}/{status?}")]
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
        [Route("")]
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
