using System;
using System.Net.Http;
using System.Threading.Tasks;
using Health.Direct.Config.Model;
using Steeltoe.Common.Discovery;
using Steeltoe.Discovery;
using System.Net.Http.Json;

namespace Health.Direct.Config.Client
{
    public interface IPropertyService
    {
        Task<Property> GetPropertyByName(string name);
    }

    public class PropertyService : IPropertyService
    {
        DiscoveryHttpClientHandler _handler;
        private const string PropertiesUrl = "https://localhost:5221/api/properties";

        public PropertyService(IDiscoveryClient client)
        {
            _handler = new DiscoveryHttpClientHandler(client);
        }
        
        public Task<Property> GetPropertyByName(string name)
        {
            var client = GetClient();

            try
            {
                //
                // https://www.stevejgordon.co.uk/sending-and-receiving-json-using-httpclient-with-system-net-http-json
                // Todo:  What happens when 404 is returned and I don't want the client to get an error.
                // Plus logging.
                // Look into Hellang.Middleware.ProblemDetails 
                //
                var httpResponse = client.GetFromJsonAsync<Property>($"{PropertiesUrl}/{name}");

                return httpResponse;
            }
            catch (Exception e)
            {
                Console.WriteLine(e);
                throw;
            }
        }

        private HttpClient GetClient()
        {
            // WARNING: do NOT create a new HttpClient for every request in your code
            // -- you may experience socket exhaustion if you do!
            var client = new HttpClient(_handler, false);

            return client;
        }
    }
}
