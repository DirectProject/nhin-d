/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections;
using System.Text;
using System.Xml.Serialization;
using System.IO;
using System.Xml;
using System.Globalization;

namespace Health.Direct.Common.Extensions
{
    /// <summary>
    /// Extensions <see cref="string"/>
    /// </summary>
    public static class StringExtensions
    {
        /// <summary>
        /// Tests if the second string is contained by the first string with the supplied <paramref name="comparison"/> operator
        /// </summary>
        /// <param name="x">The base string.</param>
        /// <param name="y">The string to test if it is contained in the base string</param>
        /// <param name="comparison">The comparison operator.</param>
        /// <returns><c>true</c> if the second string is contained in the first with respect to the supplied form of
        /// string comparison, <c>false</c> otherwise</returns>
        public static bool Contains(this string x, string y, StringComparison comparison)
        {
            return (x.IndexOf(y, comparison) >= 0);
        }
        
        /// <summary>
        /// Creates a unique string by (a) Generating a new GUID (b) Turning it into a string
        /// </summary>
        /// <returns>string value</returns>
        public static string UniqueString()
        {
            return Guid.NewGuid().ToString("D");
        }
    }

    /// <summary>
    /// Extensions <see cref="Array"/>
    /// </summary>
    public static class ArrayExtensions
    {
        /// <summary>
        /// Tests if this array is <c>null</c> or has 0 entries.
        /// </summary>
        /// <param name="array">The array to test.</param>
        /// <returns><c>true</c> if the array is <c>null</c> or has 0 entries</returns>
        public static bool IsNullOrEmpty(this Array array)
        {
            return (array == null || array.Length == 0);
        }
    }

    /// <summary>
    /// Extensions for <see cref="ICollection"/>
    /// </summary>
    public static class CollectionExtensions
    {
        /// <summary>
        /// Tests if this collection is <c>null</c> or has 0 entries.
        /// </summary>
        /// <param name="collection">The collection to test.</param>
        /// <returns><c>true</c> if the collection is <c>null</c> or has 0 entries</returns>
        public static bool IsNullOrEmpty(this ICollection collection)
        {
            return (collection == null || collection.Count == 0);
        }
    }

    /// <summary>
    /// Extensions for <see cref="StringBuilder"/>
    /// </summary>
    public static class StringBuilderExtensions
    {
        /// <summary>
        /// Appends the string version (ToString) of the value with a newline.
        /// </summary>
        /// <param name="builder">This builder</param>
        /// <param name="value">The object whose string representation to add</param>
        public static void AppendLine(this StringBuilder builder, object value)
        {
            builder.AppendLine(value.ToString());
        }

        /// <summary>
        /// Appends the string version (ToString) of the value with a newline
        /// </summary>
        /// <param name="builder">This builder</param>
        /// <param name="value">The object whose string representation to add</param>
        public static void AppendLine<T>(this StringBuilder builder, T value)
        {
            builder.AppendLine(value.ToString());            
        }

        /// <summary>
        /// Appends the string returned by processing a composite format string to this instance with a newline.
        /// The formatting string contains zero or more format items; each format item is replaced by the string
        /// representation of a corresponding argument in a parameter array.
        /// </summary>
        /// <remarks>See documentation for <see cref="StringBuilder.AppendFormat(string, object[])"/></remarks>
        /// <param name="builder">This builder</param>
        /// <param name="format">A composite format string (see Remarks).</param>
        /// <param name="args">An array of objects to format.</param>
        public static void AppendLineFormat(this StringBuilder builder, string format, params object[] args)
        {
            builder.AppendFormat(format, args);
            builder.AppendLine();
        }

        /// <summary>
        /// Appends the string version (ToString) of the value
        /// </summary>
        /// <param name="builder">This builder</param>
        /// <param name="value">The object whose string representation to add</param>
        public static void Append<T>(this StringBuilder builder, T value)
        {
            builder.Append(value.ToString());
        }
    }
    
    /// <summary>
    /// Extensions for the <see cref="XmlSerializer"/>
    /// </summary>
    public static class XmlSerializerExtensions
    {
        static XmlWriterSettings s_writerSettings = new XmlWriterSettings()
        {
            Indent = false,
            OmitXmlDeclaration = true            
        };
        
        static XmlReaderSettings s_readerSettings = new XmlReaderSettings()
        {
            ConformanceLevel = ConformanceLevel.Fragment
        };
        
        /// <summary>
        /// Serialize the given object to a byte array
        /// </summary>
        /// <param name="serializer">This serializer</param>
        /// <param name="obj">object to serialize</param>
        /// <returns>Serialized bytes</returns>
        public static byte[] ToBytes(this XmlSerializer serializer, object obj)
        {
            using(MemoryStream stream = new MemoryStream())
            {
                using (XmlWriter xmlWriter = XmlWriter.Create(stream, s_writerSettings))
                {
                    serializer.Serialize(stream, obj);
                }
                return stream.ToArray();
            }
        }

        /// <summary>
        /// Serialize the given object to a string
        /// </summary>
        /// <param name="serializer">This serializer</param>
        /// <param name="obj">object to serialize</param>
        /// <returns>Serialized bytes</returns>
        public static string ToXml(this XmlSerializer serializer, object obj)
        {
            using (StringWriter writer = new StringWriter())
            {
                using(XmlWriter xmlWriter = XmlWriter.Create(writer, s_writerSettings))
                {
                    serializer.Serialize(writer, obj);
                }
                return writer.ToString();
            }
        }
        
        /// <summary>
        /// Deserialize the given  object from a byte array
        /// </summary>
        /// <param name="serializer">This serializer</param>
        /// <param name="bytes">bytes from which to deserialize the object</param>
        /// <returns>deserialized object</returns>
        public static object FromBytes(this XmlSerializer serializer, byte[] bytes)
        {
            if (bytes.IsNullOrEmpty())
            {
                throw new ArgumentException("bytes");
            }
            
            using(MemoryStream stream = new MemoryStream(bytes))
            {
                using(XmlReader reader = XmlReader.Create(stream, s_readerSettings))
                {
                    return serializer.Deserialize(reader);
                }
            }
        }

        /// <summary>
        /// Deserialize the given  object from an Xml
        /// </summary>
        /// <param name="serializer">This serializer</param>
        /// <param name="xml">xml from which to deserialize the object</param>
        /// <returns>deserialized object</returns>
        public static object FromXml(this XmlSerializer serializer, string xml)
        {
            if (string.IsNullOrEmpty(xml))
            {
                throw new ArgumentException("xml");
            }

            using (StringReader reader = new StringReader(xml))
            {
                using(XmlReader xmlReader = XmlReader.Create(reader, s_readerSettings))
                {
                    return serializer.Deserialize(xmlReader);
                }
            }
        }
    }

    /// <summary>
    /// Extensions for <see cref="DateTime"/>
    /// </summary>
    public static class DateTimeExtensions
    {
        /// <summary>
        /// Serializes the given DateTime to an RFC822 compliant Date Time string
        /// </summary>
        /// <param name="dateTime">DateTime</param>
        /// <returns>RFC822 compliant date time string</returns>
        public static string ToRFC822String(this DateTime dateTime)
        {
            // Its crazy we have to do this...
            string dateTimeString = dateTime.ToString("ddd, d MMM yyyy HH:mm:ss zzz", CultureInfo.InvariantCulture);
            string rfc822Date = dateTimeString.Remove(dateTimeString.LastIndexOf(':'), 1);
            return rfc822Date;
        }
    }
}