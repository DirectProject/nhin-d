using System;

using NHINDirect.Mime;

using Xunit;

namespace NHINDirect.Tests.Mime
{
	public class EntityPartFacts
	{
		[Fact]
		public void CallingEntityPartyWithNullTextThrowsArgumentNullException()
		{
			Assert.Throws<ArgumentNullException>(() => new Body((string) null));
		}

		[Fact]
		public void InsureToStringReturnsTheText()
		{
			const string text = "the quick brown fox";
			Assert.Equal(text, new Body(text).ToString());
		}
	}
}