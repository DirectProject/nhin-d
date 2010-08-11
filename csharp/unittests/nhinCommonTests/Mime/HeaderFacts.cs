using System.Collections.Generic;

using NHINDirect.Mime;

using Xunit;
using Xunit.Extensions;

namespace NHINDirect.Tests.Mime
{
	public class HeaderFacts
	{
		[Fact]
		public void HeaderConstrcutor()
		{
			var pair = new KeyValuePair<string, string>("key", "value");
			var header = new Header(pair);
			Assert.Equal("key", header.Name);
			Assert.Equal("value", header.Value);
		}

		[Theory]
		[InlineData("key", true)]
		[InlineData("KEY", true)]
		[InlineData("NAME", false)]
		[InlineData(null, false)]
		[InlineData("", false)]
		public void IsHeaderName(string name, bool expected)
		{
			var pair = new KeyValuePair<string, string>("key", "value");
			var header = new Header(pair);
			Assert.Equal(expected, header.IsHeaderName(name));
		}

		[Theory]
		[InlineData("to", true)]
		[InlineData("TO", true)]
		[InlineData("from", true)]
		[InlineData("cc", true)]
		[InlineData("subject", true)]
		[InlineData("date", false)]
		public void IsHeaderNameAnyOf(string name, bool expected)
		{
			var pair = new KeyValuePair<string, string>(name, "value");
			var header = new Header(pair);
			Assert.Equal(expected, header.IsHeaderNameOneOf(new[] {"to", "from", "cc", "subject"}));
		}

		[Fact]
		public void IsHeaderNameAnyOfWIthNullArray()
		{
			var pair = new KeyValuePair<string, string>("key", "value");
			var header = new Header(pair);
			Assert.False(header.IsHeaderNameOneOf(null));
		}

		[Fact]
		public void CloneHeader()
		{
			var pair = new KeyValuePair<string, string>("key", "value");
			var header = new Header(pair);

			var clone = header.Clone();

			Assert.Equal(header.Name, clone.Name);
			Assert.Equal(header.Value, clone.Value);
		}

	}
}