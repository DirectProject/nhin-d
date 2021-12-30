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

using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Net.Http.Json;
using System.Threading.Tasks;
using FluentAssertions;
using Health.Direct.Config.Store;
using Health.Direct.Config.Model;
using Health.Direct.Config.Store.Tests;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.AspNetCore.WebUtilities;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Xunit;
using Xunit.Abstractions;

namespace Health.Direct.Config.Service.Rest.Tests;

public class ApiTestFixture : WebApplicationFactory<Program>
{
    public ITestOutputHelper Output { get; set; }

    protected override IHost CreateHost(IHostBuilder builder)
    {
        builder.ConfigureServices(services =>
        {
                // Remove the app's ApplicationDbContext registration.
                var descriptor = services.SingleOrDefault(
                d => d.ServiceType ==
                     typeof(DbContextOptions<DirectDbContext>));

            if (descriptor != null)
            {
                services.Remove(descriptor);
            }

            services.AddDbContext<DirectDbContext>(options =>
                ConfigStoreTestBase.DbContextOptions(options));

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


        var domain = new Store.Entity.Domain()
        {
            AgentName = "TestAgent",
            Name = "GetDomainById.test",
            Status = Store.EntityStatus.Enabled
        };

        db.Domains.Add(domain);

        domain = new Store.Entity.Domain()
        {
            AgentName = "TestAgent",
            Name = "GetDomainByName.test",
            Status = Store.EntityStatus.Enabled
        };

        db.Domains.Add(domain);

        db.SaveChanges();
    }
}

public class DomainControllerTest : ConfigStoreTestBase, IClassFixture<ApiTestFixture>
{
    private readonly HttpClient _client;
    private string _dbProviderName;

    public DomainControllerTest(ApiTestFixture fixture, ITestOutputHelper output)
    {
        if (fixture == null) throw new ArgumentNullException(nameof(fixture));

        fixture.Output = output;
        _client = fixture.CreateClient();
        _dbProviderName = Environment.GetEnvironmentVariable("Config.Store.DbProvider");
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
        var result = await _client.GetFromJsonAsync<Domain>("api/domain/domainName/GetDomainByName.test");

        // Assert
        result.AgentName.Should().BeEquivalentTo("TestAgent");
    }

    [Fact]
    public async Task GetDomainsByName()
    {
        // Act single
        var result = await _client.GetFromJsonAsync<List<Domain>>("api/domain/domainNames?name=GetDomainById.test");

        // Assert single
        result.Single().Status.Should().Be(EntityStatus.Enabled);
        result.Single().AgentName.Should().BeEquivalentTo("TestAgent");

        // Act two with status filter
        var url = QueryHelpers.AddQueryString("/api/domains/domainNames", "name", "domain1.test.com");
        url = QueryHelpers.AddQueryString(url, "name", "GetDomainById.test");
        url = QueryHelpers.AddQueryString(url, "status", "New");

        // Assert one New status
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
        result.All(d => d.Status == EntityStatus.Enabled).Should().BeTrue();
        result.All(d => d.AgentName == "TestAgent").Should().BeTrue();
    }

    [Fact]
    public async Task AddDomainTest()
    {
        // Arrange
        var domain = new Domain()
        {
            Name = "Add.Domain.test",
            AgentName = "SpecialAgent",
            SecurityStandard = SecurityStandard.Fips1402,
        };

        // Act
        var response = await _client.PostAsJsonAsync<Domain>("/api/domain", domain);

        // Assert
        response.EnsureSuccessStatusCode();
        var result = await response.Content.ReadFromJsonAsync<Domain>();

        result.Name.Should().BeEquivalentTo(domain.Name);
        result.Status.Should().Be(EntityStatus.New);
        result.AgentName.Should().Be("SpecialAgent");
        result.SecurityStandard.Should().Be(SecurityStandard.Fips1402);

        // Act 
        response = await _client.PostAsJsonAsync<Domain>("/api/domain", domain);
        var exception = Record.Exception(() => response.EnsureSuccessStatusCode());
        Assert.NotNull(exception);
        Assert.IsType<HttpRequestException>(exception);
        var problemDetails = await response.Content.ReadFromJsonAsync<ProblemDetails>();

        
        // Assert Duplicate
        problemDetails.Status.Should().Be(StatusCodes.Status409Conflict);
        problemDetails.Detail.Should().Be(ConfigStoreError.UniqueConstraint.ToString());
        problemDetails.Type.Should().Be("https://httpstatuses.com/409");
       

        // Arrange
        domain = new Domain()
        {
            Name = "Add.Domain.test.Add.Domain.test.Add.Domain.test.Add.Domain.test.Add.Domain.test.Add.Domain.test.Add.Domain.test.Add.Domain.test.Add.Domain.test.Add.Domain.test.Add.Domain.test.Add.Domain.test.Add.Domain.test.Add.Domain.test.Add.Domain.test.Add.Domain.test.Add.Domain.test",
            AgentName = "SpecialAgent",
            SecurityStandard = SecurityStandard.Fips1402,
        };

        // Act 
        response = await _client.PostAsJsonAsync<Domain>("/api/domain", domain);
        exception = Record.Exception(() => response.EnsureSuccessStatusCode());
        Assert.NotNull(exception);
        Assert.IsType<HttpRequestException>(exception);
        problemDetails = await response.Content.ReadFromJsonAsync<ProblemDetails>();

        // Assert validation
        problemDetails.Status.Should().Be(StatusCodes.Status400BadRequest);
        problemDetails.Detail.Should().Be("Error=DomainNameLength");
        problemDetails.Type.Should().Be("https://httpstatuses.com/400");
    }
}
