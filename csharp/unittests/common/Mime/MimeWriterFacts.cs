using System;
using System.IO;

using NHINDirect.Mime;

using Xunit;

namespace NHINDirect.Tests.Mime
{
	public class MimeWriterFacts : IDisposable
	{
		private readonly MimeWriter m_writer;

		public MimeWriterFacts()
		{
			m_writer = new MimeWriter(new StringWriter());
		}

		public void Dispose()
		{
			m_writer.Dispose();
		}

		[Fact]
		public void ConstructorWithNullThrowsArgumentNullException()
		{
			var ex = Assert.Throws<ArgumentNullException>(() => new MimeWriter(null));
			Assert.Equal("writer", ex.ParamName);
		}

		[Fact]
		public void SetWriterWithNullThrowsArgumentNullException()
		{
			var ex = Assert.Throws<ArgumentNullException>(() => m_writer.SetWriter(null));
			Assert.Equal("writer", ex.ParamName);
		}

		[Fact]
		public void WriteWithNullHeaderCollectionThrowsArgumentNullException()
		{
			var ex = Assert.Throws<ArgumentNullException>(() => m_writer.Write((HeaderCollection)null));
			Assert.Equal("headers", ex.ParamName);
		}

		[Fact]
		public void WriteWithNullHeaderThrowsArgumentNullException()
		{
			var ex = Assert.Throws<ArgumentNullException>(() => m_writer.Write((Header)null));
			Assert.Equal("header", ex.ParamName);
		}

		[Fact]
		public void WriteWithNullBodyThrowsArgumentNullException()
		{
			var ex = Assert.Throws<ArgumentNullException>(() => m_writer.Write((Body)null));
			Assert.Equal("body", ex.ParamName);
		}
	}
}