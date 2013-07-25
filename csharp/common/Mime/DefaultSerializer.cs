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
using System.Text;
using System.IO;

using Health.Direct.Common.Mail;

namespace Health.Direct.Common.Mime
{
    /// <summary>
    /// The basic serializer/deserialize to and from RFC 5322 messages.
    /// </summary>
    public class DefaultSerializer : MimeSerializer
    {
        /// <summary>
        /// Serializes the <paramref name="entity"/> as an RFC 5322 message string
        /// </summary>
        /// <param name="entity">The <see cref="MimeEntity"/> to serialize</param>
        /// <returns>An RFC 5322 string for the <paramref name="entity"/></returns>
        public override string Serialize(MimeEntity entity)
        {
            Message message = entity as Message;
            if (message != null)
            {
                //
                // Already ASCII encoded. We can just serialize to text...
                //
                using(StringWriter writer = new StringWriter())
                {
                    Serialize(entity, writer);
                    return writer.ToString();
                }
                
            }
            
            return base.Serialize(entity);
        }

        /// <summary>
        /// Serializes the <paramref name="entity"/> as RFC 5322 text to <paramref name="writer"/>
        /// </summary>
        /// <param name="entity">The <see cref="MimeEntity"/> to serialize</param>
        /// <param name="writer">The <see cref="TextWriter"/> to which to serialize</param>
        public override void Serialize(MimeEntity entity, TextWriter writer)
        {
            if (entity == null)
            {
                throw new ArgumentNullException("entity");
            }

            using (MimeWriter entityWriter = new MimeWriter(writer))
            {
                Serialize(entity, entityWriter);
            }
        }

        /// <summary>
        /// Serializes the <paramref name="entity"/> as RFC 5322 text to <paramref name="writer"/>
        /// </summary>
        /// <param name="entity">The <see cref="MimeEntity"/> to serialize</param>
        /// <param name="entityWriter">The <see cref="MimeWriter"/> to which to serialize</param>
        public void Serialize(MimeEntity entity, MimeWriter entityWriter)
        {
            if (entity == null)
            {
                throw new ArgumentNullException("entity");
            }

            if (entityWriter == null)
            {
                throw new ArgumentNullException("entityWriter");
            }

            if (entity.HasHeaders)
            {
                entityWriter.Write(entity.Headers);
                entityWriter.WriteCRLF();
            }

            if (entity.HasBody)
            {
                entityWriter.Write(entity.Body);
                if (entity is Message)
                {
                    entityWriter.WriteCRLF();
                }
            }
        }


        /// <summary>
        /// Seralizes an enumeration of <paramref name="entities"/> as a multipart body.
        /// </summary>
        /// <remarks>Mutipart MIME headers not included.</remarks>
        /// <param name="entities">The entities to write as multipart body parts</param>
        /// <param name="boundary">The multipart boundary string</param>
        /// <param name="writer">The <see cref="TextWriter"/> to which to serialize.</param>
        public override void Serialize(IEnumerable<MimeEntity> entities, string boundary, TextWriter writer)
        {
            if (entities == null)
            {
                throw new ArgumentNullException("entities");
            }

            using (MimeWriter entityWriter = new MimeWriter(writer))
            {
                foreach (MimeEntity entity in entities)
                {
                    entityWriter.WriteMimeBoundary(boundary, false);
                    Serialize(entity, entityWriter);
                }
                entityWriter.WriteMimeBoundary(boundary, true);
            }
        }

        /// <summary>
        /// Deserializes and parses <paramref name="messageText"/>
        /// </summary>
        /// <typeparam name="T">The entity type to which to deserialize.</typeparam>
        /// <param name="messageText">The <see cref="StringSegment"/> representing the source text</param>
        /// <returns>The deserialized and parsed entity</returns>
        public override T Deserialize<T>(StringSegment messageText)
        {
            return MimeParser.Read<T>(messageText);
        }


        /// <summary>
        /// Deserializes and parses a header block supplied as <paramref name="messageText"/>.
        /// </summary>
        /// <param name="messageText">The header block.</param>
        /// <returns>The deserialized and parsed <see cref="Header"/> enumeration</returns>
        public override IEnumerable<Header> DeserializeHeaders(StringSegment messageText)
        {
            return MimeParser.ReadHeaders(messageText);
        }

        /// <summary>
        /// Splits a value by the supplied <paramref name="separator"/> <see cref="char"/>.
        /// </summary>
        /// <param name="headerText">The string to split.</param>
        /// <param name="separator">The <see cref="char"/> to split by.</param>
        /// <returns>The split <see cref="StringSegment"/> instances</returns>
        public override IEnumerable<StringSegment> SplitHeaderValue(string headerText, char separator)
        {
            return StringSegment.Split(headerText, separator);
        }
        
        /// <summary>
        /// Splits a header string <paramref name="headerText"/> into header name and value.
        /// </summary>
        /// <param name="headerText">The header line</param>
        /// <returns>A pair where the key is the header name, and the value the header value.</returns>
        public override KeyValuePair<string, string> SplitHeader(string headerText)
        {
            return MimeParser.ReadNameValue(headerText);
        }
                
        /// <summary>
        /// Joins a pair as a header
        /// </summary>
        /// <param name="headerPair">The pair where the key is the header name and the value is the header value</param>
        /// <returns>A <see cref="string"/> representation of the header</returns>
        public override string JoinHeader(KeyValuePair<string, string> headerPair)
        {
            if (string.IsNullOrEmpty(headerPair.Key))
            {
                throw new ArgumentException("headerText.Key was null or empty");
            }
            if (string.IsNullOrEmpty(headerPair.Value))
            {
                throw new ArgumentException("headerText.Value was null or empty");
            }

            StringBuilder builder = new StringBuilder();
            builder.Append(headerPair.Key);
            builder.Append(MimeStandard.NameValueSeparator);
            builder.Append(headerPair.Value);
            return builder.ToString();
        }
    }
}