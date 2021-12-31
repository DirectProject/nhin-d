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
using Microsoft.EntityFrameworkCore.Infrastructure;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Moq;
using Xunit;
using Xunit.Abstractions;

namespace Health.Direct.Config.Service.Rest.Tests;

public class ApiTestFixture : WebApplicationFactory<Program>
{
    public ITestOutputHelper? Output { get; set; }

    protected override IHost CreateHost(IHostBuilder builder)
    {
        builder.ConfigureServices(services =>
        {
            // Remove the ApplicationDbContext registration.
            var descriptor = services.SingleOrDefault(
            d => d.ServiceType ==
                 typeof(DbContextOptions<DirectDbContext>));

            if (descriptor != null)
            {
                services.Remove(descriptor);
            }
            
            services.AddDbContext<DirectDbContext>(ConfigStoreTestBase.DbContextOptions);
            
            descriptor = services.SingleOrDefault(
                d => d.ServiceType ==
                     typeof(IDomainManager));

            if (descriptor != null)
            {
                services.Remove(descriptor);
            }

            services.AddScoped<IDomainManager>(sp =>
            {
                var mock = new Mock<DomainManager>(sp.GetRequiredService<DirectDbContext>()){ CallBase = true };
                mock.SetupAllProperties();
                mock.Setup(e => e.Remove(It.Is<string>(s => s == "throw.domain.test"))).Throws<Exception>();
                mock.Setup(e => e.Update(It.Is<Store.Entity.Domain>(d => d.Name == "throw.domain.test"))).Throws<Exception>();
                mock.Setup(e => e.Add(It.Is<Store.Entity.Domain>(d => d.Name == "throw.domain.test"))).Throws<Exception>();
                mock.Setup(e => e.Get(It.Is<List<string>>(s => s.Any(n => n == "throw.domain.test")), null)).Throws<Exception>();
                mock.Setup(e => e.Get(It.Is<string>(s => s == "throw.domain.test"))).Throws<Exception>();
                mock.Setup(e => e.Get(It.Is<long>(s => s == 999))).Throws<Exception>();
                mock.Setup(e => e.Get(It.Is<string>(s => s == "throw.domain.test"), It.IsAny<int>())).Throws<Exception>();

                return mock.Object;
            }); 
            
            var sp = services.BuildServiceProvider();

            using var scope = sp.CreateScope();
            {
                var scopedServices = scope.ServiceProvider;
                var db = scopedServices.GetRequiredService<DirectDbContext>();
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


        var domain = new Store.Entity.Domain
        {
            AgentName = "TestAgent",
            Name = "GetDomainById.test",
            Status = EntityStatus.Enabled
        };

        db.Domains.Add(domain);

        domain = new Store.Entity.Domain
        {
            Name = "GetDomainByName.test",
            AgentName = "TestAgent",
            Status = EntityStatus.Enabled
        };

        db.Domains.Add(domain);

        domain = new Store.Entity.Domain
        {
            Name = "xyz.deleteDomain.test.direct",
            AgentName = "TestAgent"
        };

        db.Domains.Add(domain);

        domain = new Store.Entity.Domain
        {
            Name = "updateDomain.test.direct",
            AgentName = "TestAgent"
        };

        db.Domains.Add(domain);

        db.SaveChanges();
    }
}

public class DomainControllerTest : ConfigStoreTestBase, IClassFixture<ApiTestFixture>
{
    private readonly HttpClient _client;

    public DomainControllerTest(ApiTestFixture fixture, ITestOutputHelper? output)
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
        result!.AgentName.Should().BeEquivalentTo("TestAgent");

        // Act
        var response = await _client.GetAsync("api/domain/1999");

        // Assert
        response.StatusCode.Should().Be(HttpStatusCode.NotFound);

        // Act
        var exception = await Assert.ThrowsAsync<HttpRequestException>(async () =>
            await _client.GetFromJsonAsync<Domain>("api/domain/999"));
        // Assert Exception
        exception.StatusCode.Should().Be(HttpStatusCode.InternalServerError);
    }

    [Fact]
    public async Task GetDomainByName()
    {
        // Act
        var result = await _client.GetFromJsonAsync<Domain>("api/domain/domainName/GetDomainByName.test");

        // Assert
        result!.AgentName.Should().BeEquivalentTo("TestAgent");

        // Act
        var response = await _client.GetAsync("api/domain/domainName/not.hear.test");

        // Assert
        response.StatusCode.Should().Be(HttpStatusCode.NotFound);

        // Act
        var exception = await Assert.ThrowsAsync<HttpRequestException>(async () =>
            await _client.GetFromJsonAsync<List<Domain>>("api/domain/domainName/throw.domain.test"));
        // Assert Exception
        exception.StatusCode.Should().Be(HttpStatusCode.InternalServerError);
    }

    [Fact]
    public async Task GetDomainsByName()
    {
        // Act
        var exception = await Assert.ThrowsAsync<HttpRequestException>(async () =>
            await _client.GetFromJsonAsync<List<Domain>>("api/domain/domainNames?name=throw.domain.test"));
        // Assert Exception
        exception.StatusCode.Should().Be(HttpStatusCode.InternalServerError);

        // Act single
        var result = await _client.GetFromJsonAsync<List<Domain>>("api/domain/domainNames?name=GetDomainById.test");

        // Assert single
        result!.Single().Status.Should().Be(EntityStatus.Enabled);
        result!.Single().AgentName.Should().BeEquivalentTo("TestAgent");


        // Arrange
        var url = QueryHelpers.AddQueryString("/api/domain/domainNames", "name", "domain1.test.com");
        url = QueryHelpers.AddQueryString(url, "name", "GetDomainById.test");
        
        // Act two with status filter
        result = await _client.GetFromJsonAsync<List<Domain>>(url);

        // Assert one New status
        result!.Count.Should().Be(2);

        // Arrange
        url = QueryHelpers.AddQueryString("/api/domain/domainNames", "name", "domain1.test.com");
        url = QueryHelpers.AddQueryString(url, "name", "GetDomainById.test");
        url = QueryHelpers.AddQueryString(url, "status", "Enabled");

        // Act two with status filter
        result = await _client.GetFromJsonAsync<List<Domain>>(url);

        // Assert one New status
        result!.Single().Status.Should().Be(EntityStatus.Enabled);
        result!.Single().AgentName.Should().BeEquivalentTo("TestAgent");

        // Act
        exception = await Assert.ThrowsAsync<HttpRequestException>(async () =>
            await _client.GetFromJsonAsync<List<Domain>>("api/domain/domainNames"));
        // Assert BadRequest
        exception.StatusCode.Should().Be(HttpStatusCode.BadRequest);
        
    }

    [Fact]
    public async Task GetDomainByAgentName()
    {
        // Act
        var result = await _client.GetFromJsonAsync<List<Domain>>("api/domain/agentName/TestAgent?status=Enabled");

        // Assert
        result!.All(d => d.Status == EntityStatus.Enabled).Should().BeTrue();
        result!.All(d => d.AgentName == "TestAgent").Should().BeTrue();


        // Act
        var response = await _client.GetAsync("api/domain/agentName/NoAgent");

        // Assert
        response.StatusCode.Should().Be(HttpStatusCode.NotFound);
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
        var response = await _client.PostAsJsonAsync("/api/domain", domain);

        // Assert
        response.EnsureSuccessStatusCode();
        var result = await response.Content.ReadFromJsonAsync<Domain>();

        result!.Name.Should().BeEquivalentTo(domain.Name);
        result.Status.Should().Be(EntityStatus.New);
        result.AgentName.Should().Be("SpecialAgent");
        result.SecurityStandard.Should().Be(SecurityStandard.Fips1402);

        // Act 
        response = await _client.PostAsJsonAsync("/api/domain", domain);
        var responseInner = response;
        var exception = Record.Exception(() => responseInner.EnsureSuccessStatusCode());
        Assert.NotNull(exception);
        Assert.IsType<HttpRequestException>(exception);
        var problemDetails = await response.Content.ReadFromJsonAsync<ProblemDetails>();

        
        // Assert Duplicate
        problemDetails!.Status.Should().Be(StatusCodes.Status409Conflict);
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
        response = await _client.PostAsJsonAsync("/api/domain", domain);
        var responseInner2 = response;
        exception = Record.Exception(() => responseInner2.EnsureSuccessStatusCode());
        Assert.NotNull(exception);
        Assert.IsType<HttpRequestException>(exception);
        problemDetails = await response.Content.ReadFromJsonAsync<ProblemDetails>();

        // Assert validation
        problemDetails!.Status.Should().Be(StatusCodes.Status400BadRequest);
        problemDetails.Detail.Should().Be($"Error={ConfigStoreError.DomainNameLength}");
        problemDetails.Type.Should().Be("https://httpstatuses.com/400");


        // Arrange Exception
        domain.Name = "throw.domain.test";
        // Act
        response = await _client.PostAsJsonAsync($"/api/domain", domain);
        // Assert Exception
        response.StatusCode.Should().Be(HttpStatusCode.InternalServerError);


        // Arrange Invalid Email Exception
        domain.Name = "@_throw.domain.test";
        // Act
        response = await _client.PostAsJsonAsync($"/api/domain", domain);
        // Assert Exception
        var responseInner3 = response;
        exception = Record.Exception(() => responseInner3.EnsureSuccessStatusCode());
        Assert.NotNull(exception);
        Assert.IsType<HttpRequestException>(exception);
        problemDetails = await response.Content.ReadFromJsonAsync<ProblemDetails>();

        // Assert validation
        problemDetails!.Status.Should().Be(StatusCodes.Status400BadRequest);
        problemDetails.Detail.Should().Be($"{ConfigStoreError.InvalidDomain}");
        problemDetails.Type.Should().Be("https://httpstatuses.com/400");
    }

    [Fact]
    public async Task DeleteDomainTest()
    {
        // Arrange
        var domain = new Domain()
        {
            Name = "xyz.deleteDomain.test.direct",
            AgentName = "TestAgent"
        };

        var result = await _client.GetFromJsonAsync<List<Domain>>("api/domain/domainNames?name=xyz.deleteDomain.test.direct");
        Assert.NotNull(result);
        Assert.True(result!.Any());
        
        // Act
        var deleteResult = await _client.DeleteAsync($"/api/domain/xyz.deleteDomain.test.direct");

        // Assert
        deleteResult.EnsureSuccessStatusCode();

        result = await _client.GetFromJsonAsync<List<Domain>>("api/domain/domainNames?name=xyz.deleteDomain.test.direct");
        result!.Any().Should().BeFalse();

        // Act 
        deleteResult = await _client.DeleteAsync($"/api/domain/xyx.deleteDomain.test.direct");
        // Assert not found
        deleteResult.StatusCode.Should().Be(HttpStatusCode.NotFound);

        // Act
        deleteResult = await _client.DeleteAsync($"/api/domain/throw.domain.test");
        // Assert Exception
        deleteResult.StatusCode.Should().Be(HttpStatusCode.InternalServerError);
    }

    [Fact]
    public async Task UpdateDomainTest()
    {
        // Arrange
        var domains = await _client.GetFromJsonAsync<List<Domain>>("api/domain/domainNames?name=updateDomain.test.direct");
        Assert.NotNull(domains);
        Assert.True(domains!.Any());
        var domain = domains.Single();
        domain.AgentName.Should().Be("TestAgent");
        domain.AgentName = "TestAgent2";

        // Act
        var response = await _client.PutAsJsonAsync($"/api/domain/{domain.ID}", domain);
        response.EnsureSuccessStatusCode();

        // Assert
        domains = await _client.GetFromJsonAsync<List<Domain>>("api/domain/domainNames?name=updateDomain.test.direct");
        Assert.NotNull(domains);
        domains!.Single().AgentName.Should().Be("TestAgent2");

        // Arrange Exception
        domain.Name = "throw.domain.test";
        // Act
        response = await _client.PutAsJsonAsync($"/api/domain/{domain.ID}", domain);
        // Assert Exception
        response.StatusCode.Should().Be(HttpStatusCode.InternalServerError);
    }

    [Fact]
    public async Task PageDomains()
    {
        // Act
        var results = await _client.GetFromJsonAsync<List<Domain>>("api/domain/page/2");

        // Assert
        results!.Count.Should().Be(2);

        // Act
        results = await _client.GetFromJsonAsync<List<Domain>>($"api/domain/page/2?lastDomainName=domain1.test.com");

        results.First().Name.Should().Be("domain10.test.com");
        results.Last().Name.Should().Be("domain2.test.com");

        // Act
        results = await _client.GetFromJsonAsync<List<Domain>>($"api/domain/page/2?lastDomainName={results.Last().Name}");

        // Assert
        results!.Count.Should().Be(2);
        results.First().Name.Should().Be("domain3.test.com");
        results.Last().Name.Should().Be("domain4.test.com");

        // Act
        var response = await _client.GetAsync("api/domain/page/2/?LastDomainName=xyz.not.hear.test");

        // Assert
        response.StatusCode.Should().Be(HttpStatusCode.NotFound);

        // Act
        var exception = await Assert.ThrowsAsync<HttpRequestException>(async () =>
            await _client.GetFromJsonAsync<List<Domain>>("api/domain/page/2?lastDomainName=throw.domain.test"));
        // Assert Exception
        exception.StatusCode.Should().Be(HttpStatusCode.InternalServerError);
    }
}
