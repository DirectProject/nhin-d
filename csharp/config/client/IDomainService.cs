using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Net.Http.Json;
using System.Runtime.CompilerServices;
using System.Threading;
using System.Threading.Tasks;
using Health.Direct.Config.Model;
using Health.Direct.Config.Store;
using Microsoft.AspNetCore.WebUtilities;
using Microsoft.Extensions.Logging;

namespace Health.Direct.Config.Client
{
    /// <summary>
    /// 
    /// </summary>
    public interface IDomainService: IDisposable
    {
        /// <summary>
        /// Add a <see cref="Domain"/>
        /// </summary>
        /// <param name="domain"></param>
        /// <param name="token"></param>
        /// <returns></returns>
        Task<Domain> AddDomain(Domain domain, CancellationToken token = default);

        /// <summary>
        /// Get a <see cref="Domain"/> by id
        /// </summary>
        /// <param name="id"></param>
        /// <param name="token"></param>
        /// <returns></returns>
        Task<Domain> GetDomain(long id, CancellationToken token = default);

        /// <summary>
        /// Get all <see cref="Domain"/>s
        /// </summary>
        /// <param name="token"></param>
        /// <returns></returns>
        Task<IEnumerable<Domain>> GetDomains(CancellationToken token = default);


        /// <summary>
        /// Get all <see cref="Domain"/>s
        /// </summary>
        /// <param name="token"></param>
        /// <returns></returns>
        Task<IEnumerable<string>> GetDomainNames(CancellationToken token = default);

        /// <summary>
        /// Get all <see cref="Domain"/>s
        /// </summary>
        /// <param name="agentName"></param>
        /// <param name="token"></param>
        /// <returns></returns>
        Task<IEnumerable<string>> GetDomainNames(string agentName, CancellationToken token = default);


        /// <summary>
        /// Update <see cref="Domain"/>
        /// </summary>
        /// <param name="domain"></param>
        /// <param name="token"></param>
        /// <returns></returns>
        Task<bool> UpdateDomain(Domain domain, CancellationToken token = default);

        /// <summary>
        /// Delete <see cref="Domain"/>
        /// </summary>
        /// <param name="id"></param>
        /// <param name="token"></param>
        /// <returns></returns>
        Task<bool> DeleteDomain(long id, CancellationToken token = default);

        /// <summary>
        /// Get count of <see cref="Domain"/>s
        /// </summary>
        /// <returns></returns>
        Task<int> Count();

        /// <summary>
        /// Get <see cref="Domain"/> by domain names.
        /// </summary>
        /// <param name="domainNames"></param>
        /// <param name="token"></param>
        /// <param name="status"></param>
        /// <returns></returns>
        Task<IEnumerable<Domain>> GetByDomainNames(IEnumerable<string> domainNames, EntityStatus? status = null, CancellationToken token = default);

        /// <summary>
        /// get <see cref="Domain"/> by domain name.
        /// </summary>
        /// <param name="domainName"></param>
        /// <param name="status"></param>
        /// <param name="token"></param>
        /// <returns></returns>
        Task<Domain> GetByDomainName(string domainName, EntityStatus? status = null, CancellationToken token = default);

        /// <summary>
        /// Get <see cref="Domain"/> by agent name.
        /// </summary>
        /// <param name="agentName"></param>
        /// <param name="status"></param>
        /// <param name="token"></param>
        /// <returns></returns>
        Task<IEnumerable<Domain>> GetByAgentName(string agentName, EntityStatus? status = null, CancellationToken token = default);

        /// <summary>
        /// Check if <see cref="Domain"/> exists by domain name.
        /// </summary>
        /// <param name="domainName"></param>
        /// <param name="token"></param>
        /// <returns></returns>
        Task<bool> DomainExists(string domainName, CancellationToken token = default);

        /// <summary>
        /// Request domains by page
        /// </summary>
        /// <param name="lastDomain"></param>
        /// <param name="page"></param>
        /// <param name="token"></param>
        /// <returns></returns>
        Task<IEnumerable<Domain>> PageDomains(string lastDomain, int page, CancellationToken token = default);
    }

    /// <summary>
    /// Domain Client for config REST service.
    /// </summary>
    public class DomainService : IDomainService
    {
        private readonly HttpClient _client;
        private readonly ILogger<DomainService> _logger;
        
        /// <summary>
        /// Create new Domain Client.
        /// DI friendly.  
        /// </summary>
        /// <param name="client"></param>
        /// <param name="logger"></param>
        public DomainService(HttpClient client, ILogger<DomainService> logger)
        {
            _client = client;
            _logger = logger;
        }

        /// <inheritdoc />
        public async Task<Domain> AddDomain(Domain domain, CancellationToken token = default)
        {
            try
            {
                var httpResponse = await _client.PostAsJsonAsync("/api/domain", domain, token);

                if (httpResponse.IsSuccessStatusCode)
                {
                    var responseMessage = await httpResponse.Content.ReadFromJsonAsync<Domain>(cancellationToken: token);
                    return responseMessage;
                }
                
                throw new Exception($"Failed with http response code {httpResponse.StatusCode}");
            }
            catch (Exception e)
            {
                _logger.LogError(e, "Error calling {0}", nameof(AddDomain));
                throw;
            }
        }

        /// <inheritdoc />
        public Task<Domain> GetDomain(long id, CancellationToken token = default)
        {
            try
            {
                var httpResponse = _client.GetFromJsonAsync<Domain>($"/api/domain/{id}", token);
                return httpResponse;
            }
            catch (Exception e)
            {
                _logger.LogError(e, "Error calling {0}", nameof(GetDomain));
                throw;
            }
        }

        /// <inheritdoc />
        public Task<IEnumerable<Domain>> GetDomains(CancellationToken token = default)
        {
            try
            {
                return _client.GetFromJsonAsync<IEnumerable<Domain>>("/api/Domain", token);
            }
            catch (Exception e)
            {
                _logger.LogError(e, "Error calling {0}", nameof(GetDomain));
                throw;
            }
        }

        /// <summary>
        /// Get all <see cref="Domain"/>s
        /// </summary>
        /// <param name="token"></param>
        /// <returns></returns>
        public Task<IEnumerable<string>> GetDomainNames(CancellationToken token = default)
        {
            try
            {
                return _client.GetFromJsonAsync<IEnumerable<string>>("/api/Domain/domainNames", token);
            }
            catch (Exception e)
            {
                _logger.LogError(e, "Error calling {0}", nameof(GetDomain));
                throw;
            }
        }

        /// <summary>
        /// Get all <see cref="Domain"/>s
        /// </summary>
        /// <param name="agentName"></param>
        /// <param name="token"></param>
        /// <returns></returns>
        public Task<IEnumerable<string>> GetDomainNames(string agentName, CancellationToken token = default)
        {
            try
            {
                return _client.GetFromJsonAsync<IEnumerable<string>>($"/api/Domain/domainNames/{agentName}", token);
            }
            catch (Exception e)
            {
                _logger.LogError(e, "Error calling {0}", nameof(GetDomain));
                throw;
            }
        }

        /// <inheritdoc />
        public async Task<bool> UpdateDomain(Domain domain, CancellationToken token = default)
        {
            try
            {
                var httpResponse = await _client.PutAsJsonAsync($"/api/Domain/{domain.ID}", domain, token);

                if (httpResponse.IsSuccessStatusCode)
                {
                    return true;
                }
                else
                {
                    _logger.LogWarning("StatusCode: {0}, calling {1} with domain id {2}", httpResponse.IsSuccessStatusCode, nameof(UpdateDomain), domain?.ID);
                    return false;
                }
            }
            catch (Exception e)
            {
                _logger.LogError(e, "Error calling {0}", nameof(AddDomain));
                throw;
            }
        }

        /// <inheritdoc />
        public async Task<bool> DeleteDomain(long id, CancellationToken token = default)
        {
            try
            {
                var httpResponse = await _client.DeleteAsync($"/api/domain/{id}", token);

                if (!httpResponse.IsSuccessStatusCode)
                {
                    throw new Exception($"Failed with http response code {httpResponse.StatusCode}");
                }

                return true;
            }
            catch (Exception e)
            {
                _logger.LogError(e, "Error calling {0}", nameof(GetDomain));
                throw;
            }
        }

        /// <inheritdoc />
        public Task<int> Count()
        {
            return _client.GetFromJsonAsync<int>("/api/domain/count");
        }

        /// <inheritdoc />
        public async Task<IEnumerable<Domain>> GetByDomainNames(
            IEnumerable<string> domainNames, 
            EntityStatus? status = null,
            CancellationToken token = default)
        {
            try
            {
                var url = "/api/domain/byDomainNames";

                foreach (var domainName in domainNames)
                {
                    url = QueryHelpers.AddQueryString(url, "name", domainName);
                }

                var response = await _client.GetAsync(url, token);

                if (response.StatusCode == HttpStatusCode.NotFound)
                {
                    return null;
                }

                return await response.Content.ReadFromJsonAsync<IEnumerable<Domain>>(cancellationToken: token);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error calling {0}", nameof(GetByDomainName));
                throw;
            }
        }


        /// <inheritdoc />
        public async Task<Domain> GetByDomainName(string domainName, EntityStatus? status = null, CancellationToken token = default)
        {
            try
            {
                var response =  await _client.GetAsync($"api/domain/byDomainName/{domainName}", token);

                if (response.StatusCode == HttpStatusCode.NotFound)
                {
                    return null;
                }

                return await response.Content.ReadFromJsonAsync<Domain>(cancellationToken: token);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error calling {0}", nameof(GetByDomainName));
                throw;
            }
        }

        /// <inheritdoc />
        public async Task<IEnumerable<Domain>> GetByAgentName(
            string agentName, 
            EntityStatus? status = null, 
            CancellationToken token = default)
        {
            try
            {
                var response = await _client.GetAsync($"api/domain/byAgentName/{agentName}?status={status}", token);

                if (response.StatusCode == HttpStatusCode.NotFound)
                {
                    return null;
                }

                return await response.Content.ReadFromJsonAsync<IEnumerable<Domain>>(cancellationToken: token);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error calling {0}", nameof(GetByDomainName));
                throw;
            }
        }

        /// <inheritdoc />
        public async Task<bool> DomainExists(string domainName, CancellationToken token = default)
        {
            return (await GetByDomainName(domainName, null, token)) != null;
        }


        /// <inheritdoc />
        public async Task<IEnumerable<Domain>> PageDomains(string lastDomain, int page, CancellationToken token = default)
        {
            try
            {
                var httpResponse = await _client.GetAsync($"api/domain/page/{page}?lastDomainName={lastDomain}", cancellationToken: token);
                
                if (httpResponse.IsSuccessStatusCode)
                {
                    return await httpResponse.Content.ReadFromJsonAsync<IEnumerable<Domain>>(cancellationToken: token);
                }
                
                return new List<Domain>();
            }
            catch (Exception e)
            {
                _logger.LogError(e, "Error calling {0}", nameof(GetDomain));
                throw;
            }
        }
        private void ReleaseUnmanagedResources()
        {
            _client?.Dispose();
        }

        private void Dispose(bool disposing)
        {
            ReleaseUnmanagedResources();
            if (disposing)
            {
                _client?.Dispose();
            }
        }

        /// <summary>Performs application-defined tasks associated with freeing, releasing, or resetting unmanaged resources.</summary>
        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        /// <summary>Allows an object to try to free resources and perform other cleanup operations before it is reclaimed by garbage collection.</summary>
        ~DomainService()
        {
            Dispose(false);
        }
    }
}