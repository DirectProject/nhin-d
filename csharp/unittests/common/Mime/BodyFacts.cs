using NHINDirect.Mime;

using Xunit;

namespace NHINDirect.Tests.Mime
{
	public class BodyFacts
	{
		[Fact]
		public void DefaultConstructor()
		{
			var body = new Body();
			Assert.Equal(MimePartType.Body, body.Type);
			Assert.Equal(0, body.SourceText.Length);
			Assert.Equal("", body.Text);
		}
	}
}