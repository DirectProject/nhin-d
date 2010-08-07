using System;

using NHINDirect.Mime;

using Xunit;
using Xunit.Extensions;

namespace nhinCommonTests.Mime
{
	public class StringSegmentTest
	{
		[Fact]
		public void DefaultConstructor()
		{
			var segment = new StringSegment();
			Assert.Equal(0, segment.Length);

			Assert.True(segment.IsEmpty);
			Assert.True(segment.IsNull);
		}

		[Fact]
		public void Null()
		{
			var segment = new StringSegment(null);
			Assert.Equal(0, segment.Length);
			Assert.True(segment.IsEmpty);
			Assert.True(segment.IsNull);
		}

		[Fact]
		public void Empty()
		{
			var segment = new StringSegment("");
			Assert.Equal(0, segment.Length);
			Assert.True(segment.IsEmpty);
			Assert.False(segment.IsNull);
		}

		private const string TestContent = "The quick brown fox jumped over the lazy dog.";

		[Theory]
		[InlineData(TestContent, 4, 8, "quick")]
		public void Segmented(string content, int start, int end, string expectedSegment)
		{
			var segment = new StringSegment(content, start, end);
			Assert.Equal(end, segment.EndIndex);
			Assert.False(segment.IsEmpty);
			Assert.False(segment.IsNull);
			Assert.Equal(expectedSegment.Length, segment.Length);
			Assert.Equal(TestContent, segment.Source);
			Assert.Equal(start, segment.StartIndex);
			Assert.Equal(expectedSegment, segment.ToString());
		}

		[Fact]
		public void NullSource()
		{
			var ex = Assert.Throws<ArgumentNullException>(() => new StringSegment(null, 0, 0));
			Assert.Equal("source", ex.ParamName);
		}

		[Theory]
		[InlineData("lorem ipsum", -1)]
		[InlineData("lorem ipsum", 12)]
		public void InvalidStartIndex(string source, int startIndex)
		{
			var ex = Assert.Throws<ArgumentException>(() => new StringSegment(source, startIndex, 5));
			Assert.Equal("startIndex", ex.ParamName);
		}

		[Theory]
		[InlineData("lorem ipsum", -2)]
		[InlineData("lorem ipsum", 11)]
		public void InvalidEndIndex(string source, int endIndex)
		{
			var ex = Assert.Throws<ArgumentException>(() => new StringSegment(source, 0, endIndex));
			Assert.Equal("endIndex", ex.ParamName);
		}
	}
}
