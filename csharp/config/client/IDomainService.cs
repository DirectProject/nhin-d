using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Net.Http.Json;
using System.Threading;
using System.Threading.Tasks;
using Health.Direct.Config.Model;
using Health.Direct.Config.Store;
using Microsoft.Extensions.Logging;

namespace Health.Direct.Config.Client
{
    /// <summary>
    /// 
    /// </summary>
    public interface IDomainService
    {
        Task<Domain> AddDomain(Domain domain, CancellationToken token);

        Task<Domain> GetDomain(long id, CancellationToken token);

        Task<IEnumerable<Domain>> GetDomains(CancellationToken token);

        Task<Domain> UpdateDomain(Domain domain, CancellationToken token);

        Task DeleteDomain(long id, CancellationToken token);

        Task<long> Count();

        Task<IEnumerable<Domain>> GetByDomainNames(IEnumerable<string> domainNames, CancellationToken token, EntityStatus? status = null);

        Task<Domain> GetByDomainName(string domainName, CancellationToken token, EntityStatus? status = null);
        
        Task<IEnumerable<Domain>> GetByAgentName(string agentName, CancellationToken token);

        Task<bool> DomainExists(string domainName, CancellationToken token);
    }

    /// <summary>
    /// 
    /// </summary>
    public class DomainService : IDomainService
    {
        private readonly HttpClient _client;
        private readonly ILogger<DomainService> _logger;


        /// <summary>
        /// 
        /// </summary>
        /// <param name="client"></param>
        /// <param name="logger"></param>
        public DomainService(HttpClient client, ILogger<DomainService> logger)
        {
            _client = client;
            _logger = logger;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="domain"></param>
        /// <param name="token"></param>
        /// <returns></returns>
        public async Task<Domain> AddDomain(Domain domain, CancellationToken token)
        {
            try
            {
                var httpResponse = await _client.PostAsJsonAsync(nameof(Domain), domain, token);

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

        /// <summary>
        /// 
        /// </summary>
        /// <param name="id"></param>
        /// <param name="token"></param>
        /// <returns></returns>
        public Task<Domain> GetDomain(long id, CancellationToken token)
        {
            try
            {
                var httpResponse = _client.GetFromJsonAsync<Domain>($"{nameof(Domain)}/{id}", token);
                return httpResponse;
            }
            catch (Exception e)
            {
                _logger.LogError(e, "Error calling {0}", nameof(GetDomain));
                throw;
            }
        }

        public Task<IEnumerable<Domain>> GetDomains(CancellationToken token)
        {
            try
            {
                var httpResponse = _client.GetFromJsonAsync<IEnumerable<Domain>>(nameof(Domain), token);
                return httpResponse;
            }
            catch (Exception e)
            {
                _logger.LogError(e, "Error calling {0}", nameof(GetDomain));
                throw;
            }
        }

        public async Task<Domain> UpdateDomain(Domain domain, CancellationToken token)
        {
            try
            {
                var httpResponse = await _client.PutAsJsonAsync(nameof(Domain), domain, token);

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

        public async Task DeleteDomain(long id, CancellationToken token)
        {
            try
            {
                var httpResponse = await _client.DeleteAsync($"{nameof(Domain)}/{id}", token);

                if (!httpResponse.IsSuccessStatusCode)
                {
                    throw new Exception($"Failed with http response code {httpResponse.StatusCode}");
                }
            }
            catch (Exception e)
            {
                _logger.LogError(e, "Error calling {0}", nameof(GetDomain));
                throw;
            }
        }

        public Task<long> Count()
        {
            throw new NotImplementedException();
        }

        public Task<IEnumerable<Domain>> GetByDomainNames(IEnumerable<string> domainNames, CancellationToken token, EntityStatus? status = null)
        {
            throw new NotImplementedException();
        }


        public Task<Domain> GetByDomainName(string domainName, CancellationToken token, EntityStatus? status = null)
        {
            throw new NotImplementedException();
        }

        public Task<IEnumerable<Domain>> GetByAgentName(string agentName, CancellationToken token)
        {
            throw new NotImplementedException();
        }

        public Task<bool> DomainExists(string domainName, CancellationToken token)
        {
            throw new NotImplementedException();
        }
    }
}