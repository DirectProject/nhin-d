using System;
using System.Collections.Generic;

using NHINDirect.Mime;

using Xunit;

namespace NHINDirect.Tests.Mime
{
	public class HeaderCollectionFacts
	{
		[Fact]
		public void IndexOfThrowsArgumentException()
		{
			var headers = new HeaderCollection();
			var ex = Assert.Throws<ArgumentException>(() => headers.IndexOf(null));
			Assert.Equal("name", ex.ParamName);
		}

		[Fact]
		public void AddThrowsArgumentNullException()
		{
			var headers = new HeaderCollection();
			var ex = Assert.Throws<ArgumentNullException>(() => headers.Add(null));
			Assert.Equal("headers", ex.ParamName);
		}

		[Fact]
		public void AddUpdateThrowsArgumentNullException()
		{
			var headers = new HeaderCollection();
			var ex = Assert.Throws<ArgumentNullException>(() => headers.AddUpdate((IEnumerable<Header>)null));
			Assert.Equal("headers", ex.ParamName);
		}

		[Fact]
		public void AddUpdateThrowsArgumentNullException2()
		{
			var headers = new HeaderCollection();
			var ex = Assert.Throws<ArgumentNullException>(() => headers.AddUpdate((IEnumerable<KeyValuePair<string,string>>)null));
			Assert.Equal("headers", ex.ParamName);
		}

		[Fact]
		public void CopyFromThrowsArgumentNullException()
		{
			var headers = new HeaderCollection();
			var ex = Assert.Throws<ArgumentNullException>(() => headers.CopyFrom(null, header => true));
			Assert.Equal("source", ex.ParamName);
		}

		[Fact]
		public void CopyFromThrowsArgumentNullException2()
		{
			var headers = new HeaderCollection();
			var ex = Assert.Throws<ArgumentNullException>(() => headers.CopyFrom(null, new string[0]));
			Assert.Equal("source", ex.ParamName);
		}

		[Fact]
		public void SetThrowsArgumentNullException()
		{
			var headers = new HeaderCollection();
			var ex = Assert.Throws<ArgumentNullException>(() => headers.Set(null));
			Assert.Equal("header", ex.ParamName);
		}
	}
}