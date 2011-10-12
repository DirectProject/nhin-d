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
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Net.Mail;
using System.Net.Mime;

using Health.Direct.Common.Mime;

namespace Health.Direct.Common.Mail
{
    /// <summary>
    /// Parses e-mail (RFC 5322) messages
    /// </summary>
    public static class MailParser
    {
        /// <summary>
        /// Parses RFC 5322 message <paramref name="messageText"/> returning a <see cref="Message"/>
        /// </summary>
        /// <param name="messageText">The RFC 5322 message text to parse</param>
        /// <returns>A <see cref="Message"/> containing the parsed message</returns>
        public static Message ParseMessage(string messageText)
        {
            return ParseMessage<Message>(messageText);
        }

        /// <summary>
        /// Parses RFC 5322 message <paramref name="messageText"/> returning an appropriate type parameter
        /// </summary>
        /// <param name="messageText">The RFC 5322 message text to parse</param>
        /// <typeparam name="T">The entity type to which to deserialize and parse.</typeparam>
        /// <returns>An object of parameterized type containing the parsed message</returns>
        public static T ParseMessage<T>(string messageText)
            where T : Message, new()
        {
            return MimeSerializer.Default.Deserialize<T>(messageText);
        }
        
        /// <summary>
        /// Parses a <see cref="ContentType"/> from the contents of a <c>Content-Type</c> header.
        /// </summary>
        /// <remarks>
        /// Callers of this method should be sure that the header is a <c>Content-Type</c> header, or
        /// be prepared to catch a <see cref="FormatException"/> exception.
        /// </remarks>
        /// <exception cref="FormatException">If the header value is not a valid <c>Content-Type</c> header value</exception>
        /// <param name="header">The <c>Content-Type</c> header to parse</param>
        /// <returns>A newly intialized <see cref="ContentType"/></returns>
        public static ContentType ParseContentType(Header header)
        {
            if (header == null)
            {
                return null;
            }

            return new ContentType(header.Value);
        }

        /// <summary>
        /// Parses a <see cref="ContentType"/> from the value of a <c>Content-Type</c> header.
        /// </summary>
        /// <remarks>
        /// Callers of this method should be sure that the header value is a <c>Content-Type</c> header value, or
        /// be prepared to catch a <see cref="FormatException"/> exception.
        /// </remarks>
        /// <exception cref="FormatException">If the header value is not a valid <c>Content-Type</c> header value</exception>
        /// <param name="headerValue">The <c>Content-Type</c> header's value to parse</param>
        /// <returns>A newly intialized <see cref="ContentType"/></returns>
        public static ContentType ParseContentType(string headerValue)
        {
            if (string.IsNullOrEmpty(headerValue))
            {
                return null;
            }

            return new ContentType(headerValue);
        }
        
        /// <summary>
        /// Parses a <see cref="MailAddress"/> from a header that contains a single
        /// mail address.
        /// </summary>
        /// <remarks>Most RFC 5322 headers may contain multiple address values. Unless
        /// the caller is sure that the header only contains a single address, using
        /// <see cref="ParseAddressCollection(Header)"/> may be safer.</remarks>
        /// <exception cref="FormatException">If the address value could not be recognized as a mail address</exception>
        /// <param name="header">The <c>Content-Type</c> header to parse</param>
        /// <returns>A newly constructed <see cref="MailAddress"/> containing the address</returns>
        public static MailAddress ParseMailAddress(Header header)
        {
            if (header == null)
            {
                return null;
            }

            return ParseMailAddress(header.Value);
        }

        /// <summary>
        /// Parses a <see cref="MailAddress"/> from a header value that contains a single
        /// mail address.
        /// </summary>
        /// <remarks>Most RFC 5322 headers may contain multiple address values. Unless
        /// the caller is sure that the header value only contains a single address, using
        /// <see cref="ParseAddressCollection(string)"/> may be safer.</remarks>
        /// <exception cref="FormatException">If the address value could not be recognized as a mail address</exception>
        /// <param name="headerValue">The <c>Content-Type</c> header value to parse</param>
        /// <returns>A newly constructed <see cref="MailAddress"/> containing the address</returns>
        public static MailAddress ParseMailAddress(string headerValue)
        {
            if (string.IsNullOrEmpty(headerValue))
            {
                return null;
            }

            return new MailAddress(headerValue);
        }

        /// <summary>
        /// Parses a <see cref="MailAddressCollection"/> from a header that contains
        /// mail addresses. (e.g., To, From, CC, BCC)
        /// </summary>
        /// <exception cref="FormatException">If an address value could not be recognized as a mail address</exception>
        /// <param name="header">The <c>Content-Type</c> header to parse</param>
        /// <returns>A newly constructed <see cref="MailAddressCollection"/> containing the addresses</returns>
        public static MailAddressCollection ParseAddressCollection(Header header)
        {
            if (header == null)
            {
                return null;
            }
            
            return ParseAddressCollection(header.Value);
        }

        /// <summary>
        /// Parses a header and returns a <typeparamref name="TCollection"/> of address entries of type <typeparamref name="T"/> that
        /// are subtypes of <see cref="MailAddress"/> and are constructed from mail address
        /// string values using <paramref name="constructor"/>
        /// </summary>
        /// <typeparam name="T">A <see cref="MailAddress"/> or subtype</typeparam>
        /// <typeparam name="TCollection">A subtype of <see cref="List{T}"/></typeparam>
        /// <returns>The collection of address instances</returns>
        /// <param name="header">The header containing the address elements</param>
        /// <param name="constructor">
        /// A function a single string mail address and returning a new instance of <typeparamref name="T"/> 
        /// constructed from that address
        /// </param>
        public static TCollection ParseAddressCollection<T, TCollection>(Header header, Func<string, T> constructor)
            where T : MailAddress
            where TCollection : Collection<T>, new()
        {
            if (header == null)
            {
                return null;
            }

            return ParseAddressCollection<T, TCollection>(header.Value, constructor);
        }

        /// <summary>
        /// Parses a header and adds to the supplied <paramref name="collection"/> address entries of type <typeparamref name="T"/> that
        /// are subtypes of <see cref="MailAddress"/> and are constructed from mail address
        /// string values using <paramref name="constructor"/>
        /// </summary>
        /// <typeparam name="T">A <see cref="MailAddress"/> or subtype</typeparam>
        /// <typeparam name="TCollection">A subtype of <see cref="List{T}"/></typeparam>
        /// <param name="collection">The collection to which to add elements. Must not be <c>null</c></param>
        /// <param name="header">The header containing the address elements</param>
        /// <param name="constructor">
        /// A function a single string mail address and returning a new instance of <typeparamref name="T"/> 
        /// constructed from that address
        /// </param>
        public static void ParseAddressCollection<T, TCollection>(TCollection collection, Header header, Func<string, T> constructor)
            where T : MailAddress
            where TCollection : class, IList<T>
        {
            if (header == null)
            {
                return;
            }

            if (collection == null)
            {
                throw new ArgumentNullException("collection");
            }
            
            ParseAddressCollection<T, TCollection>(collection, header.Value, constructor);
        }

        /// <summary>
        /// Parses a header value, returning a collection of <see cref="MailAddress"/> instances
        /// corresponding to the addresses in the header.
        /// </summary>
        /// <param name="headerValue">The header value to parse</param>
        /// <exception cref="FormatException">If a mail address value was misformed.</exception>
        /// <returns>The collection of addressses</returns>
        public static MailAddressCollection ParseAddressCollection(string headerValue)
        {
            return ParseAddressCollection<MailAddress, MailAddressCollection>(headerValue, x => new MailAddress(x));
        }

        /// <summary>
        /// Parses a header value and returns a <typeparamref name="TCollection"/> of address entries of type <typeparamref name="T"/> that
        /// are subtypes of <see cref="MailAddress"/> and are constructed from mail address
        /// string values using <paramref name="constructor"/>
        /// </summary>
        /// <typeparam name="T">A <see cref="MailAddress"/> or subtype</typeparam>
        /// <typeparam name="TCollection">A subtype of <see cref="List{T}"/></typeparam>
        /// <returns>The collection of address instances</returns>
        /// <param name="headerValue">The header value containing the address elements</param>
        /// <param name="constructor">
        /// A function a single string mail address and returning a new instance of <typeparamref name="T"/> 
        /// constructed from that address
        /// </param>
        public static TCollection ParseAddressCollection<T, TCollection>(string headerValue, Func<string, T> constructor)
            where T : MailAddress
            where TCollection : Collection<T>, new()
        {
            TCollection collection = new TCollection();
            ParseAddressCollection<T, TCollection>(collection, headerValue, constructor);
            return collection;
        }

        /// <summary>
        /// Parses a header and adds to the supplied <paramref name="collection"/> address entries of type <typeparamref name="T"/> that
        /// are subtypes of <see cref="MailAddress"/> and are constructed from mail address
        /// string values using <paramref name="constructor"/>
        /// </summary>
        /// <typeparam name="T">A <see cref="MailAddress"/> or subtype</typeparam>
        /// <typeparam name="TCollection">A subtype of <see cref="List{T}"/></typeparam>
        /// <param name="collection">The collection to which to add elements. Must not be <c>null</c></param>
        /// <param name="headerValue">The header value containing the address elements</param>
        /// <param name="constructor">
        /// A function a single string mail address and returning a new instance of <typeparamref name="T"/> 
        /// constructed from that address
        /// </param>
        public static void ParseAddressCollection<T, TCollection>(TCollection collection, string headerValue, Func<string, T> constructor)
            where T : MailAddress
            where TCollection : class, IList<T>
        {
            if (string.IsNullOrEmpty(headerValue))
            {
                return;
            }

            if (collection == null)
            {
                throw new ArgumentNullException("collection");
            }

            foreach (StringSegment part in ParseAddressSegments(headerValue, MailStandard.MailAddressSeparator))
            {
                if (!part.IsEmpty)
                {
                    collection.Add(constructor(MimeParser.SkipWhitespace(part).ToString()));
                }
            }
        }
        
        /// <summary>
        /// Parses an SMTP server line of address (e.g. RCPT TO, MAIL FROM) and adds to the supplied <paramref name="collection"/> address entries of type <typeparamref name="T"/> that
        /// are subtypes of <see cref="MailAddress"/> and are constructed from mail address
        /// string values using <paramref name="constructor"/>
        /// </summary>
        /// <remarks>
        /// SMTP Server envelope recipient lists look like:
        /// SMTP:example1@example.com;SMTP:example2@example.com;
        /// </remarks>
        /// <typeparam name="T">A <see cref="MailAddress"/> or subtype</typeparam>
        /// <typeparam name="TCollection">A subtype of <see cref="List{T}"/></typeparam>
        /// <param name="recipients">The SMTP list containing the address elements</param>
        /// <param name="constructor">
        /// A function a single string mail address and returning a new instance of <typeparamref name="T"/> 
        /// constructed from that address
        /// </param>
        /// <returns>A new collection of address instances</returns>
        public static TCollection ParseSMTPServerEnvelopeAddresses<T, TCollection>(string recipients, Func<string, T> constructor)
            where T : MailAddress
            where TCollection : Collection<T>, new()
        {
            if (string.IsNullOrEmpty(recipients))
            {
                return null;
            }
            
            recipients = recipients.Replace("SMTP:", string.Empty);

            TCollection collection = new TCollection();
            foreach (StringSegment part in ParseAddressSegments(recipients, ';'))
            {
                if (!part.IsEmpty)
                {
                    collection.Add(constructor(MimeParser.SkipWhitespace(part).ToString()));
                }
            }
            
            return collection;
        }

        static IEnumerable<StringSegment> ParseAddressSegments(string recipients, char separator)
        {
            return StringSegment.Split(recipients, separator, MailStandard.DQUOTE);
        }
    }
}