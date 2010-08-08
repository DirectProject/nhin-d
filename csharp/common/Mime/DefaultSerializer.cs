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
using System.Text;
using System.IO;

using NHINDirect.Mail;

namespace NHINDirect.Mime
{
    public class DefaultSerializer : MimeSerializer
    {
    	public override string Serialize(MimeEntity entity)
        {
            var message = entity as Message;
            if (message != null)
            {
                //
                // Already ASCII encoded. We can just serialize to text...
                //
                using(var writer = new StringWriter())
                {
                    Serialize(entity, writer);
                    return writer.ToString();
                }
                
            }
            
            return base.Serialize(entity);
        }
        
        public override void Serialize(MimeEntity entity, TextWriter writer)
        {
            if (entity == null)
            {
                throw new ArgumentNullException("entity");
            }

            var entityWriter = new MimeWriter(writer);
            Serialize(entity, entityWriter);
        }

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

        public override void Serialize(IEnumerable<MimeEntity> entities, string boundary, TextWriter writer)
        {
            if (entities == null)
            {
                throw new ArgumentNullException("entities");
            }

            var entityWriter = new MimeWriter(writer);
            foreach (MimeEntity entity in entities)
            {
                entityWriter.WriteMimeBoundary(boundary, false);
                Serialize(entity, entityWriter);
            }
            entityWriter.WriteMimeBoundary(boundary, true);
        }

        public override T Deserialize<T>(StringSegment messageText)
        {
            return MimeParser.Read<T>(messageText);
        }

        public override IEnumerable<Header> DeserializeHeaders(StringSegment messageText)
        {
            return MimeParser.ReadHeaders(messageText);
        }
        
        public override IEnumerable<StringSegment> SplitHeader(string headerText, char separator)
        {
           return MimeParser.ReadHeaderParts(headerText, separator);
        }
        
        public override KeyValuePair<string, string> SplitHeader(string headerText)
        {
            return MimeParser.ReadNameValue(headerText);
        }
        
        public override string JoinHeader(KeyValuePair<string, string> headerText)
        {
            if (string.IsNullOrEmpty(headerText.Key))
            {
                throw new ArgumentException("headerText.Key was null or empty");
            }
            if (string.IsNullOrEmpty(headerText.Value))
            {
				throw new ArgumentException("headerText.Value was null or empty");
			}

            var builder = new StringBuilder();
            builder.Append(headerText.Key);
            builder.Append(MimeStandard.NameValueSeparator);
            builder.Append(headerText.Value);
            return builder.ToString();
        }
    }
}
