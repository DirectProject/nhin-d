using System;
using System.Net.Http;
using Health.Direct.Config.Client;
using Microsoft.Extensions.Logging;
using Moq;
using Xunit;

namespace config.client.tests
{
    public class UnitTest1
    {
        [Fact]
        public void Test1()
        {
            var client = new HttpClient();
            client.BaseAddress = new Uri("http://localhost:5281");
            var domainService = new DomainService(client, new Mock<ILogger<DomainService>>().Object);

        }
    }
}