using System.Web.Mvc;

using AdminMvc.Controllers;
using AdminMvc.Models.Repositories;

using Xunit;

namespace AdminMvc.Tests.Controllers
{
    public class DomainsControllerFacts
    {
        public class Index
        {
            [Fact]
            public void ReturnsViewResultWithDefaultViewName()
            {
                // Arrange
                var controller = new DomainsController(new DomainRepository());

                // Act
                var result = controller.Index(0);

                // Assert
                var viewResult = Assert.IsType<ViewResult>(result);
                Assert.Empty(viewResult.ViewName);
            }
        }
    }
}