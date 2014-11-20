using System.Text.RegularExpressions;

using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Admin.Console.Tests.Models
{
    public class DomainModelFacts
    {
        [Theory]
        [InlineData("", false)]
        [InlineData("0123456789001234567890012345678900123456789001234567890012345678900123456789001234567890012345678900123456789001234567890", false)]
        [InlineData("nowhere", false)]
        [InlineData("oz.a", false)]
        [InlineData("a@b.com", false)]
        [InlineData("somewhere.com", true)]
        [InlineData("loud.co.uk", true)]
        public void Regex(string domain, bool expected)
        {
            var regex = new Regex(@"^([A-Za-z0-9\-]{1,63}\.)+[A-Za-z]{2,}$");
            Assert.Equal(expected, regex.Match(domain).Success);
        }
    }
}
