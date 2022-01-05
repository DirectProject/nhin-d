using FluentAssertions;
using Health.Direct.Config.Client;
using Health.Direct.Config.Store;
using Health.Direct.Config.Store.Tests;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Logging;
using Moq;
using Xunit;
using Domain = Health.Direct.Config.Model.Domain;

namespace config.client.tests
{
    public class DomainServiceTestFixture : ConfigStoreTestBase, IAsyncLifetime
    {
        /// <summary>
        /// Called immediately after the class has been created, before it is used.
        /// </summary>
        public async Task InitializeAsync()
        {
            var optionsBuilder = new DbContextOptionsBuilder<DirectDbContext>();
            DbContextOptions(optionsBuilder);
            var dbContext = new DirectDbContext(optionsBuilder.Options);
            await dbContext.Database.EnsureDeletedAsync();
            await dbContext.Database.EnsureCreatedAsync();
            await SeedData(dbContext);

            // Arrange
            var client = new HttpClient();
            client.BaseAddress = new Uri("http://localhost:5281");
            DomainService = new DomainService(client, new Mock<ILogger<DomainService>>().Object);
        }

        /// <summary>
        /// Called when an object is no longer needed. Called just before <see cref="M:System.IDisposable.Dispose" />
        /// if the class also implements that.
        /// </summary>
        public Task DisposeAsync()
        {
            DomainService.Dispose();
            return Task.CompletedTask;
        }

        public IDomainService DomainService { get; set;}

        private async Task SeedData(DirectDbContext db)
        {
            ConfigStoreTestBase.InitDomainRecords(db);


            var domain = new Health.Direct.Config.Store.Entity.Domain
            {
                AgentName = "TestAgent",
                Name = "GetDomainById.test",
                Status = EntityStatus.Enabled
            };

            db.Domains.Add(domain);

            domain = new Health.Direct.Config.Store.Entity.Domain
            {
                Name = "GetDomainByName.test",
                AgentName = "TestAgent",
                Status = EntityStatus.Enabled
            };

            db.Domains.Add(domain);

            domain = new Health.Direct.Config.Store.Entity.Domain
            {
                Name = "xyz.deleteDomain.test.direct",
                AgentName = "TestAgent"
            };

            db.Domains.Add(domain);

            domain = new Health.Direct.Config.Store.Entity.Domain
            {
                Name = "updateDomain.test.direct",
                AgentName = "TestAgent"
            };

            db.Domains.Add(domain);

            await db.SaveChangesAsync();
        }
    }

    public class DomainServiceClientFacts : IClassFixture<DomainServiceTestFixture>
    {
        private readonly IDomainService _domainService;

        public DomainServiceClientFacts(DomainServiceTestFixture testFixture)
        {
            _domainService = testFixture.DomainService;
        }

        [Fact]
        public async Task GetDomainCount()
        {
            // Act
            var result = await _domainService.Count();

            // Assert
            result.Should().BeGreaterThan(0);
        }

        [Fact]
        public async Task GetDomainByName()
        {
            // Act
            var result = await _domainService.GetByDomainName("GetDomainByName.test");

            // Assert
            result!.AgentName.Should().BeEquivalentTo("TestAgent");

            // Act
            result = await _domainService.GetByDomainName("not.hear.test");

            // Assert
            result.Should().Be(null);
        }

        [Fact]
        public async Task GetDomainsByName()
        {
            // Act single
            // Arrange
            var domains = new List<string> { "domain1.test.com", "GetDomainById.test" };

            // Act two with status filter
            var result = await _domainService.GetByDomainNames(domains);

            // Assert one New status
            result!.ToList().Count.Should().Be(2);

        }

        [Fact]
        public async Task GetDomainByAgentName()
        {
            // Act
            var domains = (await _domainService.GetByAgentName("TestAgent")).ToList();

            // Assert
            domains.All(d => d.AgentName == "TestAgent").Should().BeTrue();
            domains.Any(d => d.Status == EntityStatus.Enabled).Should().BeTrue();

            // Act
            var result = await _domainService.GetByAgentName("TestAgent", EntityStatus.Enabled);

            // Assert
            result!.All(d => d.Status == EntityStatus.Enabled).Should().BeTrue();

            // Act
            result = await _domainService.GetByAgentName("NoAgent");

            // Assert
            result.Should().BeNull();

            // Arrange Cancellation
            var token = new CancellationTokenSource(11).Token;
            await Task.Delay(10, token);

            // Act
            var exception = await Assert.ThrowsAnyAsync<OperationCanceledException>(async () =>
                await _domainService.GetByAgentName("TestAgentCancel", null, token));

            // Assert Exception
            exception.Message.Should().Contain("canceled");
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
            var result = await _domainService.AddDomain(domain);

            // Assert
            result.Name.Should().Be(domain.Name);
            result.Status.Should().Be(EntityStatus.New);
            result.AgentName.Should().Be("SpecialAgent");
            result.SecurityStandard.Should().Be(SecurityStandard.Fips1402);
        }

        [Fact]
        public async Task DeleteDomainTest()
        {
            // Arrange
            var result = await _domainService.GetByDomainName("xyz.deleteDomain.test.direct");
            Assert.NotNull(result);

            Assert.True(await _domainService.DeleteDomain(result.ID));
        }

        [Fact]
        public async Task UpdateDomainTest()
        {
            // Arrange
            var domain = await _domainService.GetByDomainName("updateDomain.test.direct");
            Assert.NotNull(domain);
            domain.AgentName.Should().Be("TestAgent");
            domain.AgentName = "TestAgent2";

            // Act
            var response = await _domainService.UpdateDomain(domain);
            Assert.NotNull(domain);

            // Assert
            domain.AgentName.Should().Be("TestAgent2");

            // Act
            domain = await _domainService.GetByDomainName("updateDomain.test.direct");

            // Assert
            Assert.NotNull(domain);
            domain.AgentName.Should().Be("TestAgent2");

            // Arrange Cancellation
            var token = new CancellationTokenSource(11).Token;
            await Task.Delay(10, token);

            // Act
            var exception = await Assert.ThrowsAnyAsync<OperationCanceledException>(async () =>
                await _domainService.UpdateDomain(domain, token));

            // Assert Exception
            exception.Message.Should().Contain("canceled");
        }

        [Fact]
        public async Task GetDomainNames()
        {
            // Act get enabled domain names
            var results = await _domainService.GetDomainNames();

            // Assert 
            results.Count().Should().Be(2);

            // // Act
            results = await _domainService.GetDomainNames("TestAgent");

            // Assert 
            results.Count().Should().Be(2);
        }

        [Fact]
        public async Task PageDomains()
        {
            // Act
            var results = await _domainService.PageDomains(null, 2);

            // Assert
            results.Count().Should().Be(2);

            // Act
            results = await _domainService.PageDomains("domain1.test.com", 2);

            var domains = results.ToList();
            domains.First().Name.Should().Be("domain10.test.com");
            domains.Last().Name.Should().Be("domain2.test.com");
            
            // Act
            results = await _domainService.PageDomains(domains.Last().Name, 2);
            
            // Assert
            domains = results.ToList();
            domains.Count.Should().Be(2);
            domains.First().Name.Should().Be("domain3.test.com");
            domains.Last().Name.Should().Be("domain4.test.com");

            // Act
            results = await _domainService.PageDomains("xyz.not.hear.test", 2);

            // Assert
            results.Should().BeEmpty();

        }
    }
}