using System;
using System.Net.Http;
using System.Net.Http.Json;
using System.Threading.Tasks;
using Health.Direct.Config.Store;
using Health.Direct.Config.Store.Entity;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Storage;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Xunit;
using Xunit.Abstractions;

namespace Health.Direct.Config.Service.Rest.Tests
{
    public class ApiTestFixture : WebApplicationFactory<Program>
    {
        public ITestOutputHelper Output { get; set; }

        protected override IHost CreateHost(IHostBuilder builder)
        {
            var root = new InMemoryDatabaseRoot();

            builder.ConfigureServices(services =>
            {
                services.AddScoped(sp =>
                {
                    // Replace SQLite with the in memory provider for tests
                    return new DbContextOptionsBuilder<ConfigDatabase>()
                        .UseInMemoryDatabase("Tests", root)
                        .UseApplicationServiceProvider(sp)
                        .Options;
                });
            }).ConfigureLogging(logging =>
            {
                logging.ClearProviders();
                logging.AddXUnit(Output);
            });

            return base.CreateHost(builder);
        }
    }

    public class DomainControllerTest: IClassFixture<ApiTestFixture>
    {
        private readonly HttpClient _client;

        public DomainControllerTest(ApiTestFixture fixture, ITestOutputHelper output)
        {
            if (fixture == null) throw new ArgumentNullException(nameof(fixture));

            fixture.Output = output;
            _client = fixture.CreateClient();
        }

        [Fact]
        public async Task Test1()
        {
            //await InitDomainRecords();

            var response = await _client.GetFromJsonAsync<Domain>("api/domain/1");
            Assert.Null(response);
        }
    }

}