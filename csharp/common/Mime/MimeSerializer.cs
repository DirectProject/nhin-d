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
using System.Linq;
using System.Text;
using System.IO;

namespace NHINDirect.Mime
{    
    public abstract class MimeSerializer
    {
        public static MimeSerializer s_default = new DefaultSerializer();
        
        public MimeSerializer()
        {
        }

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
                    throw new ArgumentNullException();
                }

                System.Threading.Interlocked.Exchange<MimeSerializer>(ref s_default, value);
            }
        }
                
        public virtual void Serialize(MimeEntity entity, string filePath)
        {
            using(Stream stream = File.OpenWrite(filePath))
            {
                this.Serialize(entity, stream);
            }
        }

        public virtual void Serialize(MimeEntity entity, Stream stream)
        {
            using (StreamWriter writer = new StreamWriter(stream, Encoding.ASCII))
            {
                this.Serialize(entity, writer);
            }
        }

        public virtual string Serialize(MimeEntity entity)
        {
            byte[] asciiBytes = this.SerializeToBytes(entity);
            return Encoding.ASCII.GetString(asciiBytes);
        }

        public virtual byte[] SerializeToBytes(MimeEntity entity)
        {
            using (MemoryStream stream = new MemoryStream())
            {
                this.Serialize(entity, stream);
                return stream.ToArray();
            }
        }

        public abstract void Serialize(MimeEntity entity, TextWriter writer);
        public abstract void Serialize(IEnumerable<MimeEntity> entities, string boundary, TextWriter writer);

        public virtual string Serialize(IEnumerable<MimeEntity> entities, string boundary)
        {
            byte[] asciiBytes = this.SerializeToBytes(entities, boundary);
            return Encoding.ASCII.GetString(asciiBytes);
        }

        public virtual void Serialize(IEnumerable<MimeEntity> entities, string boundary, Stream stream)
        {
            using (StreamWriter writer = new StreamWriter(stream, Encoding.ASCII))
            {
                this.Serialize(entities, boundary, writer);
            }
        }

        public virtual byte[] SerializeToBytes(IEnumerable<MimeEntity> entities, string boundary)
        {
            using (MemoryStream stream = new MemoryStream())
            {
                this.Serialize(entities, boundary, stream);
                return stream.ToArray();
            }
        }

        public virtual T Deserialize<T>(Stream stream)
            where T : MimeEntity, new()
        {
            if (stream == null)
            {
                throw new ArgumentException();
            }

            using (StreamReader reader = new StreamReader(stream, Encoding.ASCII))
            {
                return this.Deserialize<T>(reader);
            }
        }
        
        public virtual T Deserialize<T>(TextReader reader)
            where T : MimeEntity, new()
        {
            if (reader == null)
            {
                throw new ArgumentNullException();
            }
            
            return this.Deserialize<T>(reader.ReadToEnd());
        }

        public virtual T Deserialize<T>(string messageText)
            where T : MimeEntity, new()
        {
            if (string.IsNullOrEmpty(messageText))
            {
                throw new ArgumentNullException();
            }
            
            return this.Deserialize<T>(new StringSegment(messageText));
        }
        
        public virtual T Deserialize<T>(byte[] messageBytes)
            where T : MimeEntity, new()
        {
            if (messageBytes == null || messageBytes.Length == 0)
            {
                throw new ArgumentException();
            }
            
            using (MemoryStream stream = new MemoryStream(messageBytes))
            {
                return this.Deserialize<T>(stream);
            }            
        }

        public abstract T Deserialize<T>(StringSegment messageText)
                    where T : MimeEntity, new();

        public virtual IEnumerable<Header> DeserializeHeaders(string messageText)
        {
            return DeserializeHeaders(new StringSegment(messageText));
        }
                
        public abstract IEnumerable<Header> DeserializeHeaders(StringSegment messageText);
                
        public abstract IEnumerable<StringSegment> SplitHeader(string headerText, char separator);
        public abstract KeyValuePair<string, string> SplitHeader(string headerText);
        
        public virtual string JoinHeader(string name, string value)
        {
            return JoinHeader(new KeyValuePair<string,string>(name, value));
        }        
        
        public abstract string JoinHeader(KeyValuePair<string, string> headerText);

    }
}
