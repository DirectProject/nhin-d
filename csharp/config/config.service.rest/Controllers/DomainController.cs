/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     Joseph.Shook@Surescripts.com

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System.Net;
using Health.Direct.Config.Rest.Swagger;
using Health.Direct.Config.Store;
using Hellang.Middleware.ProblemDetails;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Swashbuckle.AspNetCore.Annotations;
using Swashbuckle.AspNetCore.Filters;

namespace Health.Direct.Config.Rest.Controllers;

[Route("api/[controller]")]
[ApiController]
public class DomainController : ControllerBase
{
    private readonly IDomainManager _domainManager;
    private readonly ILogger<DomainController> _logger;

    public DomainController(IDomainManager domainManager, ILogger<DomainController> logger)
    {
        _domainManager = domainManager;
        _logger = logger;
    }

    // GET: api/<DomainController>/page/<pageSize>?lastDomain
    [HttpGet("page/{pageSize}")]
    [SwaggerResponse((int)HttpStatusCode.OK, Type = typeof(List<Domain>), Description = "Returns list of domains")]
    [SwaggerResponse((int)HttpStatusCode.InternalServerError, Description = "Unexpected error")]
    public async Task<IActionResult> PageDomains(
        [FromRoute] int pageSize,
        [FromQuery] string? lastDomainName,
        CancellationToken token)
    {
        var domains = await _domainManager.Get(lastDomainName ?? string.Empty, pageSize, token);

        if (domains.Any())
        {
            return Ok(domains);
        }

        return NotFound();
    }

    // GET: api/<DomainController>/count
    [HttpGet("count")]
    [SwaggerResponse((int)HttpStatusCode.OK, Type = typeof(int), Description = "Returns number of domains")]
    [SwaggerResponse((int)HttpStatusCode.InternalServerError, Description = "Unexpected error")]
    public async Task<IActionResult> GetDomainCount(CancellationToken token)
    {
        var result = await _domainManager.Count(token);

        return Ok(result);
    }

    [HttpGet("{id}")]
    [SwaggerResponseExample((int)HttpStatusCode.OK, typeof(DomainExample))]
    [SwaggerResponse((int)HttpStatusCode.OK, Type = typeof(Domain), Description = "Returns a domain")]
    [SwaggerResponse((int)HttpStatusCode.InternalServerError, Description = "Unexpected error")]
    public async Task<IActionResult> Get(long id, CancellationToken token)
    {
        var domain = await _domainManager.Get(id, token);
        if (domain == null)
        {
            return NotFound();
        }

        return Ok(domain);
    }

    [HttpGet("byDomainName/{name}")]
    [SwaggerResponseExample((int)HttpStatusCode.OK, typeof(DomainExample))]
    [SwaggerResponse((int)HttpStatusCode.OK, Type = typeof(Domain), Description = "Returns a domain")]
    [SwaggerResponse((int)HttpStatusCode.InternalServerError, Description = "Unexpected error")]
    public async Task<IActionResult> Get(string name, CancellationToken token)
    {
        var domain = await _domainManager.Get(name, token);
        if (domain == null)
        {
            return NotFound();
        }

        return Ok(domain);
    }


    [HttpGet("byDomainNames")]
    [SwaggerResponse((int)HttpStatusCode.OK, Type = typeof(List<Domain>), Description = "Returns list of domains")]
    [SwaggerResponse((int)HttpStatusCode.BadRequest, Description = "Missing domain names")]
    [SwaggerResponse((int)HttpStatusCode.InternalServerError, Description = "Unexpected error")]
    public async Task<IActionResult> Get(
        [FromQuery(Name = "name")] List<string> domainNames,
        [FromQuery] EntityStatus? status,
        CancellationToken token)
    {
        if (!domainNames.Any()) return BadRequest();

        var result = await _domainManager.Get(domainNames, status, token);

        return Ok(result);
    }


    [HttpGet("byAgentName/{name}")]
    [SwaggerResponse((int)HttpStatusCode.OK, Type = typeof(List<Domain>), Description = "Returns list of domains")]
    [SwaggerResponse((int)HttpStatusCode.InternalServerError, Description = "Unexpected error")]
    public async Task<IActionResult> GetByAgentName(
        [FromRoute] string name,
        [FromQuery] EntityStatus? status,
        CancellationToken token)
    {
        var result = await _domainManager.GetByAgentName(name, status, token);

        if (!result.Any()) return NotFound();

        return Ok(result);
    }


    [HttpGet("domainNames")]
    [HttpGet("domainNames/{agentName}")]
    [SwaggerResponse((int)HttpStatusCode.OK, Type = typeof(List<Domain>), Description = "Returns list of domains")]
    [SwaggerResponse((int)HttpStatusCode.InternalServerError, Description = "Unexpected error")]
    public async Task<IActionResult> GetDomainNames(
        [FromRoute] string? agentName,
        CancellationToken token)
    {
        var result = agentName == null
            ? await _domainManager.GetDomainNames(token)
            : await _domainManager.GetDomainNames(agentName, token);

        if (!result.Any()) return NotFound();

        return Ok(result);
    }


    [HttpPost("")]
    [SwaggerResponse((int)HttpStatusCode.Created, Type = typeof(Domain), Description = "Returns created domain")]
    [SwaggerResponse((int)HttpStatusCode.InternalServerError, Description = "Unexpected error")]

    public async Task<IActionResult> Post([FromBody] Domain domain, CancellationToken token)
    {
        try
        {
            var result = await _domainManager.Add(domain, token);

            return Created(result.ID.ToString(), result);
        }
        catch (DbUpdateException ex)
        {
            _logger.LogError(ex, "Error calling {0}", nameof(Get));

            if (ex.InnerException != null &&
                (ex.InnerException.Message.Contains("duplicate")
                 || ex.InnerException.Message.Contains("UNIQUE")))
            {
                var problemDetails = new ProblemDetails()
                {
                    Status = StatusCodes.Status409Conflict,
                    Detail = ConfigStoreError.UniqueConstraint.ToString(),
                    Type = "https://httpstatuses.com/409"
                };

                throw new ProblemDetailsException(problemDetails);
            }
            else
            {
                var problemDetails = new ProblemDetails()
                {
                    Status = StatusCodes.Status400BadRequest,
                    Detail = "Check the logs",
                    Type = "https://httpstatuses.com/400"
                };

                throw new ProblemDetailsException(problemDetails);
            }
        }
        catch (ConfigStoreException ex)
        {
            _logger.LogError(ex, "Error calling {0}", nameof(Get));
            ProblemDetails problemDetails;

            if (ex.Error is ConfigStoreError.UniqueConstraint
                or ConfigStoreError.Conflict)
            {
                problemDetails = new ProblemDetails()
                {
                    Status = StatusCodes.Status409Conflict,
                    Detail = ex.Error.ToString(),
                    Type = "https://httpstatuses.com/409"
                };
            }
            else
            {
                problemDetails = new ProblemDetails()
                {
                    Status = StatusCodes.Status400BadRequest,
                    Detail = ex.Error.ToString(),
                    Type = "https://httpstatuses.com/400"
                };
            }

            throw new ProblemDetailsException(problemDetails);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error calling {0}", nameof(Get));
            throw;
        }
    }

    // PUT api/<DomainController>/5
    [HttpPut("{id}")]
    [SwaggerResponse((int)HttpStatusCode.OK, Description = "Returns 200")]
    [SwaggerResponse((int)HttpStatusCode.BadRequest, Description = "Missing or invalid domain object")]
    [SwaggerResponse((int)HttpStatusCode.InternalServerError, Description = "Unexpected error")]
    public async Task<IActionResult> Put([FromRoute] long id, [FromBody] Domain domain, CancellationToken token)
    {
        await _domainManager.Update(domain, token);

        return NoContent(); // success 204 but not returning the result.
    }

    // DELETE api/<DomainController>/5
    [HttpDelete("{id}")]
    [SwaggerResponse((int)HttpStatusCode.OK, Description = "Returns 200")]
    [SwaggerResponse((int)HttpStatusCode.BadRequest, Description = "Missing or invalid domain object")]
    [SwaggerResponse((int)HttpStatusCode.InternalServerError, Description = "Unexpected error")]
    public async Task<IActionResult> Delete([FromRoute] long id, CancellationToken token)
    {
        var response = await _domainManager.Remove(id, token);

        if (response)
        {
            return Ok();
        }

        return NotFound();
    }
}