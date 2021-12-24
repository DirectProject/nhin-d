using Health.Direct.Config.Store;
using Health.Direct.Config.Store.Entity;
using Microsoft.AspNetCore.Mvc;

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
        public async Task<IActionResult> EnumerateDomains(string lastDomainName, int pagesize)
        {
            try
            {
                var domains = await _domainManager.Get(lastDomainName, pagesize);
               
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

        // POST api/<DomainController>
        [HttpPost]
        public async Task<IActionResult> Post([FromBody] Domain domain)
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
