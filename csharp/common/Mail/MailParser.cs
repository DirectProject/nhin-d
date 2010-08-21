/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Net.Mail;
using System.Net.Mime;
using NHINDirect.Mime;

namespace NHINDirect.Mail
{
    public static class MailParser
    {
        public static Message ParseMessage(string messageText)
        {
            return ParseMessage<Message>(messageText);
        }
        
        public static T ParseMessage<T>(string messageText)
            where T : Message, new()
        {
            return MimeSerializer.Default.Deserialize<T>(messageText);
        }
        
        public static ContentType ParseContentType(Header header)
        {
            if (header == null)
            {
                return null;
            }

            return new ContentType(header.Value);
        }

        public static ContentType ParseContentType(string headerValue)
        {
            if (string.IsNullOrEmpty(headerValue))
            {
                return null;
            }

            return new ContentType(headerValue);
        }
        
        public static MailAddress ParseMailAddress(Header header)
        {
            if (header == null)
            {
                return null;
            }

            return ParseMailAddress(header.Value);
        }

        public static MailAddress ParseMailAddress(string headerValue)
        {
            if (string.IsNullOrEmpty(headerValue))
            {
                return null;
            }

            return new MailAddress(headerValue);
        }
        
        public static MailAddressCollection ParseAddressCollection(Header header)
        {
            if (header == null)
            {
                return null;
            }
            
            return ParseAddressCollection(header.Value);
        }

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

        public static void ParseAddressCollection<T, TCollection>(TCollection collection, Header header, Func<string, T> constructor)
            where T : MailAddress
            where TCollection : IList<T>
        {
            if (header == null)
            {
                return;
            }
            
            ParseAddressCollection<T, TCollection>(collection, header.Value, constructor);
        }

        public static MailAddressCollection ParseAddressCollection(string headerValue)
        {
            return ParseAddressCollection<MailAddress, MailAddressCollection>(headerValue, x => new MailAddress(x));
        }

        public static TCollection ParseAddressCollection<T, TCollection>(string headerValue, Func<string, T> constructor)
            where T : MailAddress
            where TCollection : Collection<T>, new()
        {
            TCollection collection = new TCollection();
            ParseAddressCollection<T, TCollection>(collection, headerValue, constructor);
            return collection;
        }

        public static void ParseAddressCollection<T, TCollection>(TCollection collection, string headerValue, Func<string, T> constructor)
            where T : MailAddress
            where TCollection : IList<T>
        {
            if (string.IsNullOrEmpty(headerValue))
            {
                return;
            }

            foreach (StringSegment part in MimeSerializer.Default.SplitHeader(headerValue, MailStandard.MailAddressSeparator))
            {
                if (!part.IsEmpty)
                {
                    collection.Add(constructor(MimeParser.SkipWhitespace(part).ToString()));
                }
            }
        }
        
        /// <summary>
        /// SMTP Server envelope recipient lists look like:
        /// SMTP:example1@example.com;SMTP:example2@example.com;
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="headerValue"></param>
        /// <param name="constructor"></param>
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
            foreach (StringSegment part in MimeSerializer.Default.SplitHeader(recipients, ';'))
            {
                if (!part.IsEmpty)
                {
                    collection.Add(constructor(MimeParser.SkipWhitespace(part).ToString()));
                }
            }
            
            return collection;
        }
    }
}
