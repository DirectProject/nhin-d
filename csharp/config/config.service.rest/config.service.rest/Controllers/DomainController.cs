using System.Collections.Generic;
using System.Net;
using Health.Direct.Config.Store;
using Health.Direct.Config.Store.Entity;
using Microsoft.AspNetCore.Mvc;
using Swashbuckle.AspNetCore.Annotations;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace Health.Direct.Config.Rest.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class DomainController : ControllerBase
    {
        private IDomainManager _domainManager;
        private ILogger<DomainController> _logger;

        public DomainController(IDomainManager domainManager, ILogger<DomainController> logger)
        {
            _domainManager = domainManager;
            _logger = logger;
        }

        // GET: api/<DomainController>
        [HttpGet("page/{pageSize}/{lastDomainName}")]
        public async Task<IActionResult> EnumerateDomains([FromRoute]string lastDomainName, [FromRoute]int pageSize)
        {
            try
            {
                var domains = await _domainManager.Get(lastDomainName, pageSize);
               
                if (domains.Any())
                {
                    return Ok(domains);
                }

                return NotFound();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error calling {0}", nameof(Get));
                throw;
            }
        }

        /// <summary>
        /// Returns number of domains
        /// </summary>
        /// <returns>int</returns>
        [HttpGet("count")]
        [SwaggerResponse((int)HttpStatusCode.OK, Type = typeof(int), Description = "Returns number of domains")]
        [SwaggerResponse((int)HttpStatusCode.InternalServerError, Description = "Unexpected error")]
        public async Task<IActionResult> GetDomainCount()
        {
            var result = await _domainManager.Count();

            return Ok(result);
        }

        // GET api/<DomainController>/5
        [HttpGet("{id}")]
        public async Task<IActionResult> Get(int id)
        {
            try
            {
                var domain = await _domainManager.Get(id);
                if (domain == null)
                {
                    return NotFound();
                }

                return Ok(domain);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error calling {0}", nameof(Get));
                throw;
            }
        }



        /// <summary>
        /// GetByAgentName domain by domain names
        /// </summary>
        /// <param name="name">List of domain names</param>
        /// <param name="status"><see cref="EntityStatus" />
        /// </param>
        /// <returns>Returns Domains</returns>
        [HttpGet("domainNames")]
        [SwaggerResponse((int)HttpStatusCode.OK, Type = typeof(List<Domain>), Description = "Returns list of domains")]
        [SwaggerResponse((int)HttpStatusCode.BadRequest, Description = "Missing domain names")]
        [SwaggerResponse((int)HttpStatusCode.InternalServerError, Description = "Unexpected error")]
        public async Task<IActionResult> Get(
            [FromQuery(Name = "name")] string[] domainNames,
            [FromQuery] EntityStatus? status)
        {
            if(!domainNames.Any()) return BadRequest();

            var result = await _domainManager.Get(domainNames, status);

            return Ok(result);
        }

        /// <summary>
        /// GetByAgentName domains by agent name
        /// </summary>
        /// <param name="name">Network name</param>
        /// <returns><see cref="List{Domain}"/></returns>
        [HttpGet("agentName/{name}")]
        [SwaggerResponse((int)HttpStatusCode.OK, Type = typeof(List<Domain>), Description = "Returns list of domains")]
        [SwaggerResponse((int)HttpStatusCode.InternalServerError, Description = "Unexpected error")]
        public async Task<IActionResult> GetByAgentName(
            [FromRoute] string name,
            [FromQuery] EntityStatus? status)
        {
            var result = await _domainManager.GetByAgentName(name, status);

            if (!result.Any()) return NotFound();

            return Ok(result);
        }


        // POST api/<DomainController>
        [HttpPost]
        public async Task<IActionResult> Post([FromBody] Domain? domain)
        {
            try
            {
                var result = await _domainManager.Add(domain);

                return Created(result.ID.ToString(), result);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error calling {0}", nameof(Get));
                throw;
            }
        }

        // PUT api/<DomainController>/5
        [HttpPut("{id}")]
        public async Task<IActionResult> Put(int id, [FromBody] Domain domain)
        {
            try
            {
                await _domainManager.Update(domain);

                return Ok();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error calling {0}", nameof(Get));
                throw;
            }
        }

        // DELETE api/<DomainController>/5
        [HttpDelete("{domainName}")]
        public async Task<IActionResult> Delete(string domainName)
        {
            try
            {
                await _domainManager.Remove(domainName);

                return Ok();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error calling {0}", nameof(Get));
                throw;
            }
        }
    }
}
