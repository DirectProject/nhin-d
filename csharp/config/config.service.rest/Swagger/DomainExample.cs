using Health.Direct.Config.Store;
using Swashbuckle.AspNetCore.Filters;

namespace Health.Direct.Config.Rest.Swagger;

public class DomainExample : IExamplesProvider<List<Domain>>
{
    public List<Domain> GetExamples()
    {
        return new List<Domain>
            {
                new Domain
                {
                    Name = "domain1.test.com"
                },
                new Domain
                {
                    Name = "domain2.test.com",
                    Status = EntityStatus.Disabled,
                    AgentName = "SpecialAgent",
                    SecurityStandard = SecurityStandard.Fips1402
                }
            };
    }
}

