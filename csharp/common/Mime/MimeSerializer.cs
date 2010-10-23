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

namespace Health.Direct.Common.Mime
{
    /// <summary>
    /// Implements serialization/deserialization for MIME and RFC 5322 entities
    /// </summary>
    public abstract class MimeSerializer
    {
        /// <summary>
        /// The default serializer.
        /// </summary>
        static MimeSerializer s_default = new DefaultSerializer();

        /// <summary>
        /// Gets and sets the default serializer to use.
        /// </summary>
        public static MimeSerializer Default
        {
            get
            {
                return s_default;
            }
            set
            {
                if (value == null)
                {
                    throw new ArgumentNullException("value");
                }

                System.Threading.Interlocked.Exchange(ref s_default, value);
            }
        }
        
        /// <summary>
        /// Serializes an entity as an RFC 5322 or MIME document to a named file.
        /// </summary>
        /// <param name="entity">The entity to serialize</param>
        /// <param name="filePath">The file to which to write the new message document</param>
        public virtual void Serialize(MimeEntity entity, string filePath)
        {
            using(Stream stream = File.OpenWrite(filePath))
            {
                Serialize(entity, stream);
            }
        }

        /// <summary>
        /// Serializes an entity as an RFC 5322 or MIME document to a stream
        /// </summary>
        /// <param name="entity">The entity to serialize</param>
        /// <param name="stream">The stream to which to write the new message document</param>
        public virtual void Serialize(MimeEntity entity, Stream stream)
        {
            using (StreamWriter writer = new StreamWriter(stream, Encoding.ASCII))
            {
                Serialize(entity, writer);
            }
        }

        /// <summary>
        /// Serializes an entity as an RFC 5322 or MIME document and returns it as a <see cref="string"/>
        /// </summary>
        /// <param name="entity">The entity to serialize</param>
        /// <returns>A string RFC 5322 or MIME representation of the entity </returns>
        public virtual string Serialize(MimeEntity entity)
        {
            byte[] asciiBytes = SerializeToBytes(entity);
            return Encoding.ASCII.GetString(asciiBytes);
        }

        /// <summary>
        /// Serializes an entity as an RFC 5322 or MIME document and returns it as a byte array/>
        /// </summary>
        /// <param name="entity">The entity to serialize</param>
        /// <returns>A string RFC 5322 or MIME representation of the entity </returns>
        public virtual byte[] SerializeToBytes(MimeEntity entity)
        {
            using (MemoryStream stream = new MemoryStream())
            {
                Serialize(entity, stream);
                return stream.ToArray();
            }
        }


        /// <summary>
        /// Serializes an entity as an RFC 5322 or MIME document to a <see cref="TextWriter"/>
        /// </summary>
        /// <param name="entity">The entity to serialize</param>
        /// <param name="writer">The writer to which to write the new message document</param>
        public abstract void Serialize(MimeEntity entity, TextWriter writer);

        /// <summary>
        /// Serializes an enumeration of entities as MIME multipart body to a <see cref="TextWriter"/>
        /// </summary>
        /// <remarks>Only the body is written; to be a valid MIME entity, appropriate headers must be written as well</remarks>
        /// <param name="entities">The entities to serialize</param>
        /// <param name="boundary">The mutipart boundary</param>
        /// <param name="writer">The writer to which to write the new message document</param>
        public abstract void Serialize(IEnumerable<MimeEntity> entities, string boundary, TextWriter writer);

        /// <summary>
        /// Serializes an enumeration of entities as MIME multipart body and returns it as a <see cref="string"/>
        /// </summary>
        /// <remarks>Only the body is written; to be a valid MIME entity, appropriate headers must be written as well</remarks>
        /// <param name="entities">The entities to serialize</param>
        /// <param name="boundary">The mutipart boundary</param>
        /// <returns>A <see cref="string"/> representation of the multipart body</returns>
        public virtual string Serialize(IEnumerable<MimeEntity> entities, string boundary)
        {
            byte[] asciiBytes = SerializeToBytes(entities, boundary);
            return Encoding.ASCII.GetString(asciiBytes);
        }

        /// <summary>
        /// Serializes an enumeration of entities as MIME multipart body to a <see cref="Stream"/>
        /// </summary>
        /// <remarks>Only the body is written; to be a valid MIME entity, appropriate headers must be written as well</remarks>
        /// <param name="entities">The entities to serialize</param>
        /// <param name="boundary">The mutipart boundary</param>
        /// <param name="stream">The stream to which to write the new message document</param>
        public virtual void Serialize(IEnumerable<MimeEntity> entities, string boundary, Stream stream)
        {
            using (StreamWriter writer = new StreamWriter(stream, Encoding.ASCII))
            {
                Serialize(entities, boundary, writer);
            }
        }

        /// <summary>
        /// Serializes an enumeration of entities as MIME multipart body to a <see cref="byte"/> array
        /// </summary>
        /// <remarks>Only the body is written; to be a valid MIME entity, appropriate headers must be written as well</remarks>
        /// <param name="entities">The entities to serialize</param>
        /// <param name="boundary">The mutipart boundary</param>
        /// <returns>An array of raw serialized data.</returns>
        public virtual byte[] SerializeToBytes(IEnumerable<MimeEntity> entities, string boundary)
        {
            using (MemoryStream stream = new MemoryStream())
            {
                Serialize(entities, boundary, stream);
                return stream.ToArray();
            }
        }

        /// <summary>
        /// Deserializes and parses RFC 5322 or MIME text from <paramref name="stream"/>
        /// </summary>
        /// <typeparam name="T">The entity type to which to deserialize.</typeparam>
        /// <param name="stream">The <see cref="Stream"/> providing the source data</param>
        /// <returns>The deserialized and parsed entity</returns>
        public virtual T Deserialize<T>(Stream stream)
            where T : MimeEntity, new()
        {
            if (stream == null)
            {
                throw new ArgumentNullException("stream");
            }

            using (StreamReader reader = new StreamReader(stream, Encoding.ASCII))
            {
                return Deserialize<T>(reader);
            }
        }

        /// <summary>
        /// Deserializes and parses RFC 5322 or MIME text from <paramref name="reader"/>
        /// </summary>
        /// <typeparam name="T">The entity type to which to deserialize.</typeparam>
        /// <param name="reader">The <see cref="TextReader"/> providing the source data</param>
        /// <returns>The deserialized and parsed entity</returns>
        public virtual T Deserialize<T>(TextReader reader)
            where T : MimeEntity, new()
        {
            if (reader == null)
            {
                throw new ArgumentNullException("reader");
            }
            
            return Deserialize<T>(reader.ReadToEnd());
        }

        /// <summary>
        /// Deserializes and parses <paramref name="messageText"/>
        /// </summary>
        /// <typeparam name="T">The entity type to which to deserialize.</typeparam>
        /// <param name="messageText">The <see cref="string"/> representing the source text</param>
        /// <returns>The deserialized and parsed entity</returns>
        public virtual T Deserialize<T>(string messageText)
            where T : MimeEntity, new()
        {
            if (string.IsNullOrEmpty(messageText))
            {
                throw new ArgumentNullException("messageText");
            }
            
            return Deserialize<T>(new StringSegment(messageText));
        }

        /// <summary>
        /// Deserializes and parses <paramref name="messageBytes"/>
        /// </summary>
        /// <typeparam name="T">The entity type to which to deserialize.</typeparam>
        /// <param name="messageBytes">The <see cref="byte"/> array providing the source data</param>
        /// <returns>The deserialized and parsed entity</returns>
        public virtual T Deserialize<T>(byte[] messageBytes)
            where T : MimeEntity, new()
        {
            if (messageBytes == null)
            {
                throw new ArgumentNullException("messageBytes");
            }
            if (messageBytes.Length == 0)
            {
                throw new ArgumentException("messageBytes contained was empty", "messageBytes");
            }
            
            using (MemoryStream stream = new MemoryStream(messageBytes))
            {
                return Deserialize<T>(stream);
            }            
        }

        /// <summary>
        /// Deserializes and parses <paramref name="messageText"/>
        /// </summary>
        /// <typeparam name="T">The entity type to which to deserialize.</typeparam>
        /// <param name="messageText">The <see cref="StringSegment"/> representing the source text</param>
        /// <returns>The deserialized and parsed entity</returns>
        public abstract T Deserialize<T>(StringSegment messageText)
            where T : MimeEntity, new();


        /// <summary>
        /// Deserializes and parses a header block supplied as <paramref name="messageText"/>.
        /// </summary>
        /// <param name="messageText">The header block.</param>
        /// <returns>The deserialized and parsed <see cref="Header"/> enumeration</returns>
        public virtual IEnumerable<Header> DeserializeHeaders(string messageText)
        {
            return DeserializeHeaders(new StringSegment(messageText));
        }


        /// <summary>
        /// Deserializes and parses a header block supplied as <paramref name="messageText"/>.
        /// </summary>
        /// <param name="messageText">The header block.</param>
        /// <returns>The deserialized and parsed <see cref="Header"/> enumeration</returns>
        public abstract IEnumerable<Header> DeserializeHeaders(StringSegment messageText);

        /// <summary>
        /// Splits a value by the supplied <paramref name="separator"/> <see cref="char"/>.
        /// </summary>
        /// <param name="headerText">The string to split.</param>
        /// <param name="separator">The <see cref="char"/> to split by.</param>
        /// <returns>The split <see cref="StringSegment"/> instances</returns>
        public abstract IEnumerable<StringSegment> SplitHeaderValue(string headerText, char separator);

        /// <summary>
        /// Splits a header string <paramref name="headerText"/> into header name and value.
        /// </summary>
        /// <param name="headerText">The header line</param>
        /// <returns>A pair where the key is the header name, and the value the header value.</returns>
        public abstract KeyValuePair<string, string> SplitHeader(string headerText);

        /// <summary>
        /// Joins a header name and value as a header
        /// </summary>
        /// <param name="name">The header name</param>
        /// <param name="value">The header value</param>
        /// <returns>A <see cref="string"/> representation of the header</returns>
        public virtual string JoinHeader(string name, string value)
        {
            return JoinHeader(new KeyValuePair<string,string>(name, value));
        }

        /// <summary>
        /// Joins a pair as a header
        /// </summary>
        /// <param name="headerText">The pair where the key is the header name and the value is the header value</param>
        /// <returns>A <see cref="string"/> representation of the header</returns>
        public abstract string JoinHeader(KeyValuePair<string, string> headerText);

    }
}