using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Net.Http.Json;
using System.Threading.Tasks;
using FluentAssertions;
using Health.Direct.Config.Store;
using Health.Direct.Config.Store.Entity;
using Health.Direct.Config.Store.Tests;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.AspNetCore.WebUtilities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Storage;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Org.BouncyCastle.Crypto.Engines;
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
                services.AddDbContext<DirectDbContext>(options => 
                    options.UseInMemoryDatabase("Tests", root));

                var sp = services.BuildServiceProvider();

                using (var scope = sp.CreateScope())
                {
                    var scopedServices = scope.ServiceProvider;
                    var db = scopedServices.GetRequiredService<DirectDbContext>();
                    var logger = scopedServices
                        .GetRequiredService<ILogger<WebApplicationFactory<Program>>>();

                    db.Database.EnsureDeleted();
                    db.Database.EnsureCreated();

                    SeedData(db);
                }

            }).ConfigureLogging(logging =>
            {
                logging.ClearProviders();
                logging.AddXUnit(Output);
            });

            return base.CreateHost(builder);
        }

        private void SeedData(DirectDbContext db)
        {
            ConfigStoreTestBase.InitDomainRecords(db);
            
            
            var domain = new Domain()
            {
                AgentName = "TestAgent",
                Name = "GetDomainById.test",
                Status = EntityStatus.Enabled
            };

            db.Domains.Add(domain);
            db.SaveChanges();
        }
    }

    public class DomainControllerTest: ConfigStoreTestBase, IClassFixture<ApiTestFixture>
    {
        private readonly HttpClient _client;

        public DomainControllerTest(ApiTestFixture fixture, ITestOutputHelper output)
        {
            if (fixture == null) throw new ArgumentNullException(nameof(fixture));

            fixture.Output = output;
            _client = fixture.CreateClient();
        }

        [Fact]
        public async Task GetDomainCount()
        {
            // Act
            var result = await _client.GetFromJsonAsync<int>("/api/domain/count");
            
            // Assert
            result.Should().BeGreaterThan(0);
        }

        [Fact]
        public async Task GetDomainById()
        {
            // Act
            var result = await _client.GetFromJsonAsync<Domain>("api/domain/11");
            
            // Assert
            result.AgentName.Should().BeEquivalentTo("TestAgent");
        }

        [Fact]
        public async Task GetDomainByName()
        {
            // Act
            var result = await _client.GetFromJsonAsync<Domain>("api/domain/name");

            // Assert
            result.AgentName.Should().BeEquivalentTo("TestAgent");
        }

        [Fact]
        public async Task GetDomainsByNameTest()
        {
            // Act
            var result = await _client.GetFromJsonAsync<List<Domain>>("api/domain/domainNames?name=GetDomainById.test");

            // Assert
            result.Single().Status.Should().Be(EntityStatus.Enabled);
            result.Single().AgentName.Should().BeEquivalentTo("TestAgent");

            // Act
            var url = QueryHelpers.AddQueryString("/api/domains/domainNames", "name", "domain1.test.com");
            url = QueryHelpers.AddQueryString(url, "name", "GetDomainById.test");
            url = QueryHelpers.AddQueryString(url, "status", "New");

            // Assert
            result.Single().Status.Should().Be(EntityStatus.Enabled);
            result.Single().AgentName.Should().BeEquivalentTo("TestAgent");

            // Assert BadRequest
            
            var exception = await Assert.ThrowsAsync<HttpRequestException>(async () => 
                await _client.GetFromJsonAsync<List<Domain>>("api/domain/domainNames"));

            exception.StatusCode.Should().Be(HttpStatusCode.BadRequest);
        }

        [Fact]
        public async Task GetDomainByAgentName()
        {
            // Act
            var result = await _client.GetFromJsonAsync<List<Domain>>("api/domain/agentName/TestAgent");

            // Assert
            result.Single().Status.Should().Be(EntityStatus.Enabled);
            result.Single().AgentName.Should().BeEquivalentTo("TestAgent");
        }
    }
}