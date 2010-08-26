using System;
using System.Collections.Generic;

using NHINDirect.Mime;

using Xunit;

namespace NHINDirect.Tests.Mime
{
	public class HeaderCollectionFacts
	{
		private readonly HeaderCollection m_headers;

		public HeaderCollectionFacts()
		{
			m_headers = new HeaderCollection(new[] {new Header("key", "value")});
		}

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
		public void AddFilteredByHeaderStringsThrowsArgumentNullException()
		{
			var headers = new HeaderCollection();
			var ex = Assert.Throws<ArgumentNullException>(() => headers.Add(null, header => true));
			Assert.Equal("source", ex.ParamName);
		}

		[Fact]
		public void AddThrowsArgumentNullException2()
		{
			var headers = new HeaderCollection();
			var ex = Assert.Throws<ArgumentNullException>(() => headers.Add(null, new string[0]));
			Assert.Equal("source", ex.ParamName);
		}

		[Fact]
		public void SetThrowsArgumentNullException()
		{
			var headers = new HeaderCollection();
			var ex = Assert.Throws<ArgumentNullException>(() => headers.Set(null));
			Assert.Equal("header", ex.ParamName);
		}

		[Fact]
		public void ItemSetWithNullRemovesHeader()
		{
			Assert.NotNull(m_headers["key"]);
			Assert.Equal("value", m_headers["key"].Value);

			m_headers["key"] = null;
			Assert.Null(m_headers["key"]);
		}

		[Fact]
		public void GetValueReturnsNullWhenHeaderNotFound()
		{
			Assert.Null(m_headers.GetValue("unknown"));
		}

        [Fact]
        public void FilteredVersionOfAdd()
        {
            var a = new Header("a", "a");
            var b = new Header("b", "b");
            var aa = new Header("aa", "c");
            Header[] headers =  new Header[] { a, b, aa };
            HeaderCollection coll = new HeaderCollection();

            coll.Add(headers, h => h.Name.StartsWith("a"));

            Assert.True(coll.Contains(a));
            Assert.True(coll.Contains(aa));
            Assert.False(coll.Contains(b));
        }

        [Fact]
        public void StringArrayFilteredVersionOfAdd()
        {
            var a = new Header("a", "a");
            var b = new Header("b", "b");
            var aa = new Header("aa", "c");
            var headers = new Header[] { a, b, aa };
            HeaderCollection coll = new HeaderCollection();

            coll.Add(headers, new string[] { "a", "aa" });
            Assert.True(coll.Contains(a));
            Assert.True(coll.Contains(aa));
            Assert.False(coll.Contains(b));
        }

	}
}