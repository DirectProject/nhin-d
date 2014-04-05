using FluentAssertions;
using Xunit;

namespace Health.Direct.Policy.Tests
{
    public class PolicyValueFactory_GetInstanceTest
    {
        [Fact]
        public void testGetInstance_assertValue()
        {
            IPolicyValue<int> value = PolicyValueFactory.GetInstance(12345);
            value.GetPolicyValue().Should().Be(12345);
            value.Should().Be(PolicyValueFactory.GetInstance(12345));

            IPolicyValue<string> value2 = PolicyValueFactory.GetInstance("12345");
            value2.GetPolicyValue().Should().Be("12345");
            value2.Should().Be(PolicyValueFactory.GetInstance("12345"));
        }
	
    }
}