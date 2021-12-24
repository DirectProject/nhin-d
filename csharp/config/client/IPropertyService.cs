using System;
using System.Net.Http;
using System.Threading.Tasks;
using Health.Direct.Config.Model;
using System.Net.Http.Json;
using System.Threading;
using Microsoft.Extensions.Logging;

namespace Health.Direct.Config.Client
{
    public interface IPropertyService
    {
        Task<Property> GetPropertyByName(string name, CancellationToken token);
    }

    public class PropertyService : IPropertyService
    {
        private readonly HttpClient _client;
        private readonly ILogger<PropertyService> _logger;

        public PropertyService(HttpClient client, ILogger<PropertyService> logger)
        {
            _client = client;
            _logger = logger;
        }

        public Task<Property> GetPropertyByName(string name, CancellationToken token = default)
        {
            try
            {
                var httpResponse = _client.GetFromJsonAsync<Property>(name, token);
                return httpResponse;
            }
            catch (Exception e)
            {
                _logger.LogError(e, "Error calling {0}", nameof(GetPropertyByName));
                throw;
            }
        }
    }
}
