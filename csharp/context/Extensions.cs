/* 
 Copyright (c) 2010-2017, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook    Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/


using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using MimeKit;


namespace Health.Direct.Context
{
    ///<summary>
    /// Extension methods related to <see cref="Context"/>
    ///</summary>
    public static class Extensions
    {
        /// <summary>
        /// Find Direct <see cref="Context"/>
        /// </summary>
        /// <returns>A <see cref="Context"/> object if found.  Null if not found. </returns>
        public static Context DirectContext(this MimeMessage message)
        {
            foreach (var mimeEntity in message.Attachments.Where(a => a is MimePart))
            {
                if (mimeEntity.ContentId == message.DirectContextId())
                {
                    return ContextParser.Parse(mimeEntity as MimePart, "1.0");
                }
            }

            return null;
        }

        /// <summary>
        /// Find Direct <see cref="Context"/> by version.
        /// </summary>
        /// <param name="message"></param>
        /// <param name="version"></param>
        /// <returns></returns>
        public static MimePart DirectContext(this MimeMessage message, string version)
        {
            foreach (var mimeEntity in message.Attachments.Where(a => a is MimePart))
            {
                if (mimeEntity.ContentId == message.DirectContextId())
                {
                    if (version == ContextParser.Version)
                    {
                        return ContextParser.Parse(mimeEntity as MimePart, version);
                    }     
                    //
                    // Future versions
                    //               
                }
            }

            return null;
        }

        /// <summary>
        /// Tests if this message contains the <c>X-Direct-Context</c> header.
        /// </summary>
        /// <param name="message">The message to test.</param>
        /// <returns><c>true</c> if this message indicates conformance to the "Implementation Guide for Expressing Context in Direct Messaging"</returns>
        public static bool ContainsDirectContext(this MimeMessage message)
        {
            return message.Headers.Contains(MailStandard.Headers.DirectContext);
        }

        /// <summary>
        /// Find non Direct Context MimeEntities
        /// </summary>
        /// <returns>A <see cref="IEnumerable{MimeEntity}"/></returns>
        public static IEnumerable<MimePart> SelectEncapulations(this MimeMessage message)
        {
            var attachments = message
                .Attachments
                .Where(m => m is MimePart)
                .Skip(1);

            foreach (var mimeEntity in attachments)
            {
                var attachment = (MimePart) mimeEntity;

                yield return attachment;
            }
        }

        /// <summary>
        /// Decode a MimePart
        /// </summary>
        /// <param name="message"></param>
        /// <returns></returns>
        public static string DecodeBody(this MimePart message)
        {
            using (var stream = new MemoryStream())
            {
                message.ContentObject.DecodeTo(stream);
                stream.Position = 0;
                var sr = new StreamReader(stream);
                return sr.ReadToEnd();
            }
        }

        /// <summary>
        /// Assert string maps to a valid Enum selection.
        /// </summary>
        /// <param name="source"></param>
        /// <param name="error">Inject error type</param>
        /// <typeparam name="T"></typeparam>
        /// <returns></returns>
        public static T AssertEnum<T>(this string source, ContextError error)
        {
            var type = typeof(T);
            T result;

            try
            {
                result = (T) Enum.Parse(type, source, true);
            }
            catch { 
                throw new ContextException(error);
            }

            return result;
        }

        /// <summary>
        /// Helper for <see cref="string.IsNullOrWhiteSpace"/>
        /// </summary>
        /// <param name="item"></param>
        /// <returns></returns>
        public static bool IsNullOrWhiteSpace(this string item)
        {
            return string.IsNullOrWhiteSpace(item);
        }

        /// <summary>
        /// Returns The <c>X-Direct-Context</c> header value
        /// </summary>
        public static string DirectContextId(this MimeMessage message)
        {
            return message.Headers[MailStandard.Headers.DirectContext].TrimStart('<').TrimEnd('>');
        }

        /// <summary>
        /// Make a list from a single type.
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="source"></param>
        /// <returns></returns>
        public static List<T> ToList<T>(this T source)
        {
            var result = new List<T> {source};

            return result;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="sb"></param>
        /// <param name="name"></param>
        /// <param name="value"></param>
        public static void AppendHeader(this StringBuilder sb, string name, string value)
        {
            if (!value.IsNullOrWhiteSpace())
            {
                sb.AppendLine($"{name}: {value}");
            }
        }
    }
}
