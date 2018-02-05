using System;
using System.Globalization;
using System.IO;
using System.Text;

namespace Health.Direct.Xd
{
	/// <summary>
	/// An implementation of <see cref="StringWriter"/> that allows the output encoding to be specified.
	/// </summary>
	public class StringWriterWithEncoding : StringWriter
	{
		/// <summary>
		/// Gets the <see cref="Encoding"/> in which the output is written.
		/// </summary>
		public override Encoding Encoding { get; }

		/// <summary>
		/// Creates a new <see cref="StringWriter"/> that uses the given <see cref="Encoding"/> for its output.
		/// </summary>
		/// <param name="encoding">The <see cref="Encoding"/> in which the output is written.</param>
		public StringWriterWithEncoding(Encoding encoding) : this(new StringBuilder(), CultureInfo.CurrentCulture, encoding)
		{
		}

		/// <summary>
		/// Creates a new <see cref="StringWriter"/> that uses the given <see cref="Encoding"/> for its output.
		/// </summary>
		/// <param name="formatProvider">The object that controls formatting.</param>
		/// <param name="encoding">The <see cref="Encoding"/> in which the output is written.</param>
		public StringWriterWithEncoding(IFormatProvider formatProvider, Encoding encoding) : this(new StringBuilder(), formatProvider, encoding)
		{
		}

		/// <summary>
		/// Creates a new <see cref="StringWriter"/> that uses the given <see cref="Encoding"/> for its output.
		/// </summary>
		/// <param name="sb">A <see cref="StringBuilder"/> to write to.</param>
		/// <param name="encoding">The <see cref="Encoding"/> in which the output is written.</param>
		public StringWriterWithEncoding(StringBuilder sb, Encoding encoding) : this(sb, CultureInfo.CurrentCulture, encoding)
		{
		}

		/// <summary>
		/// Creates a new <see cref="StringWriter"/> that uses the given <see cref="Encoding"/> for its output.
		/// </summary>
		/// <param name="sb">A <see cref="StringBuilder"/> to write to.</param>
		/// <param name="formatProvider">The object that controls formatting.</param>
		/// <param name="encoding">The <see cref="Encoding"/> in which the output is written.</param>
		public StringWriterWithEncoding(StringBuilder sb, IFormatProvider formatProvider, Encoding encoding) : base(sb, formatProvider)
		{
			Encoding = encoding;
		}
	}
}
