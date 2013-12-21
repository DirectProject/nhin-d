/* 
 Copyright (c) 2013, Direct Project
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
using System.Linq;
using System.Text;
using System.IO;
using System.Net.Mail;
using System.Net.Mime;
using Health.Direct.Common;
using Health.Direct.Common.Collections;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Extensions;

namespace Health.Direct.Common.Mime
{
    /// <summary>
    /// Fields maintains a collection of RFC 5322 fields ("headers")
    /// Line folding and everything else associated with headers is automatically supported. 
    /// You can use fields to embed structured data within a mail message or within text attachments. 
    ///
    /// Field are name: value pairs. 
    /// Example:
    ///     field1: Some field data
    ///     field2: x="abc";b="pqr"
    /// 
    /// Deserialize:
    ///     Use Deserialize methods. 
    ///     
    /// Serialize:
    ///     ToString
    ///     ToAttachment() 
    ///     ToMimeEntity()
    ///
    /// Fields can contain optional parameters. To extract parameters from a field value, use the DeseriaizeParameters()
    /// method
    ///  
    /// </summary>
    public class MimeFields : ICollection<KeyValuePair<string, string>>
    {
        HeaderCollection m_fields = new HeaderCollection();
        
        /// <summary>
        /// Constructs an empty fields object
        /// </summary>
        public MimeFields()
        {
        }
        
        /// <summary>
        /// Count of fields
        /// </summary>        
        public int Count
        {
            get { return m_fields.Count;}
        }
        
        /// <summary>
        /// Always returns false
        /// </summary>
        public bool IsReadOnly
        {
            get { return false;}
        }
        
        /// <summary>
        /// Get or set a field at a particular index in the collection
        /// </summary>
        /// <param name="index">index of field to set</param>
        /// <returns>The field name and value</returns>
        public KeyValuePair<string, string> this[int index]
        {
            get
            {
                Header header = m_fields[index];
                return new KeyValuePair<string,string>(header.Name, header.ValueRaw);
            }
            set
            {
                m_fields[index] = new Header(value);
            }
        }
        
        /// <summary>
        /// Get or set the first instance of a field with the given name
        /// </summary>
        /// <param name="fieldName">field name to search for</param>
        /// <returns>string value of the field</returns>                
        public string this[string fieldName]
        {
            get 
            { 
                return m_fields.GetValueRaw(fieldName);
            }
            set
            {
                m_fields.SetValue(fieldName, value);
            }
        }
        
        /// <summary>
        /// Deserialize the given fieldContent and add any fields into the current field collection
        /// </summary>
        /// <param name="fieldContent">string containing fields</param>
        public void Deserialize(string fieldContent)
        {
            if (fieldContent == null)
            {
                throw new ArgumentNullException("fieldContent");
            }
            m_fields.Clear();
            fieldContent = fieldContent.TrimStart();
            m_fields.Add(MimeSerializer.Default.DeserializeHeaders(fieldContent));
        }

        /// <summary>
        /// Deserialize the given fieldContent and add any fields into the current field collection
        /// </summary>
        /// <param name="fieldContent">Stream containing fields</param>
        public void Deserialize(Stream fieldContent)
        {
            if (fieldContent == null)
            {
                throw new ArgumentNullException("fieldContent");
            }
            using (StreamReader reader = new StreamReader(fieldContent))
            {
                string fieldText = reader.ReadToEnd();
                this.Deserialize(fieldText);                
            }
        }

        /// <summary>
        /// Deserialize the body of the Mime part and add any fields into the current field collection
        /// </summary>
        /// <param name="mimePart">MimePart whose Body contains fields</param>
        public void Deserialize(MimeEntity mimePart)
        {
            if (mimePart == null)
            {
                throw new ArgumentNullException("mimePart");
            }
            if (!mimePart.HasBody)
            {
                return;
            }
            this.Deserialize(mimePart.Body.Text);
        }

        /// <summary>
        /// Deserialize the body of the Mime part and add any fields into the current field collection
        /// </summary>
        /// <param name="attachment">Attachment chose text body contains fields</param>
        public void Deserialize(Attachment attachment)
        {
            if (attachment == null)
            {
                throw new ArgumentNullException("attachment");
            }
            this.Deserialize(attachment.StringContent());
        }
        
        /// <summary>
        /// Add a name and value...
        /// </summary>
        /// <param name="fieldName">field name</param>
        /// <param name="value">field value</param>
        public void Add(string fieldName, string value)
        {
            if (string.IsNullOrEmpty(fieldName))
            {
                throw new ArgumentException("fieldName");
            }

            m_fields.Add(fieldName, value);
        }
        
        /// <summary>
        /// Add a field
        /// </summary>
        /// <param name="field"></param>
        public void Add(KeyValuePair<string, string> field)
        {
            m_fields.Add(field);
        }
        
        /// <summary>
        /// Clear collection
        /// </summary>                  
        public void Clear()
        {
            m_fields.Clear();
        }
        
        /// <summary>
        /// Return the index of the first field with this name
        /// </summary>
        /// <param name="fieldName">field name</param>
        /// <returns>-1 if field not found. Else >= 0</returns>
        public int IndexOfFirst(string fieldName)
        {
            return m_fields.IndexOf(fieldName);
        }

        /// <summary>
        /// Return the index of the first matching field
        /// </summary>
        /// <param name="field"></param>
        /// <returns>-1 if field not found. Else >= 0</returns>
        public int IndexOfFirst(KeyValuePair<string, string> field)
        {
            for (int i = 0, count = this.Count; i < count; ++i)
            {
                KeyValuePair<string, string> foundField = this[i];
                if (MimeStandard.Equals(foundField.Key, field.Key) &&
                    MimeStandard.Equals(foundField.Value, field.Value)
                   )
                {
                    return i;
                }
            }

            return -1;
        }
        
        /// <summary>
        /// Return true if this collection contains at least 1 field with this name
        /// </summary>
        /// <param name="fieldName"></param>
        /// <returns>true if found</returns>
        public bool Contains(string fieldName)
        {
            return (this.IndexOfFirst(fieldName) >= 0);
        }
        
        /// <summary>
        /// Return true if this collection contains at least one matching field
        /// </summary>
        /// <param name="field"></param>
        /// <returns>true if found</returns>
        public bool Contains(KeyValuePair<string, string> field)
        {
            return (this.IndexOfFirst(field) >= 0);
        }
        
        /// <summary>
        /// Returns the values for all fields mathcing this field name
        /// </summary>
        /// <param name="fieldName"></param>
        /// <returns>An enumerator of field values</returns>                
        public IEnumerable<string> GetValueForField(string fieldName)
        {
            if (string.IsNullOrEmpty(fieldName))
            {
                throw new ArgumentException("fieldName");
            }
            return (
                from field in m_fields
                where field.IsNamed(fieldName)
                select field.ValueRaw
            );
        }
        
        /// <summary>
        /// Remove the field with the given fieldName
        /// </summary>
        /// <param name="fieldName"></param>
        public void Remove(string fieldName)
        {
            if (string.IsNullOrEmpty(fieldName))
            {
                throw new ArgumentException("fieldName");
            }
            m_fields.Remove((h) => h.IsNamed(fieldName));
        }
        
        /// <summary>
        /// Remove the field at the given position
        /// </summary>
        /// <param name="indexAt"></param>
        public void RemoveAt(int indexAt)
        {
            m_fields.RemoveAt(indexAt);
        }
        
        /// <summary>
        /// Remove the first field that matches the given field
        /// </summary>
        /// <param name="field"></param>
        public bool Remove(KeyValuePair<string, string> field)
        {
            int removeAt = this.IndexOfFirst(field);
            if (removeAt >= 0)
            {
                this.RemoveAt(removeAt);
                return true;
            }
            
            return false;
        }
        
        /// <summary>
        /// Copy fields to the given array
        /// </summary>
        /// <param name="array"></param>
        /// <param name="arrayIndex"></param>
        public void CopyTo(KeyValuePair<string, string>[] array, int arrayIndex)
        {
            if (array == null)
            {
                throw new ArgumentNullException("array");
            }
            
            for (int i = 0, count = Math.Min(array.Length - arrayIndex, this.Count); i < count; ++i)
            {
                array[i + arrayIndex] = this[i];
            }
        }
        
        /// <summary>
        /// Returns an enumeration of all fields in this collection
        /// </summary>
        /// <returns></returns>
        public IEnumerator<KeyValuePair<string, string>> GetEnumerator()
        {
            foreach(Header header in m_fields)
            {
                yield return new KeyValuePair<string, string>(header.Name, header.ValueRaw);
            }
        }
        
        /// <summary>
        /// Serialize fields to a string
        /// </summary>
        /// <returns></returns>
        public string Serialize()
        {
            return m_fields.ToString();
        }
        
        /// <summary>
        /// Serialize fields to the given text writer
        /// </summary>
        /// <param name="writer"></param>
        public void Serialize(TextWriter writer)
        {
            if (writer == null)
            { 
                throw new ArgumentNullException("writer");
            }
            using (MimeWriter mimeWriter = new MimeWriter(writer))
            {
                mimeWriter.Write(m_fields);
            }
        }
        
        /// <summary>
        /// Serializes the fields to their wire format
        /// </summary>
        /// <returns>Fields serialized to wire format</returns>
        public override string ToString()
        {
            return this.Serialize();
        }
        
        /// <summary>
        /// Serializes fields into a text attachment with the given name
        /// </summary>
        /// <param name="name">Attachment name</param>
        /// <returns></returns>                    
        public Attachment ToAttachment(string name)
        {   
            if (string.IsNullOrEmpty(name))
            {
                throw new ArgumentException("name");
            }
            
            string content = this.ToString();
            return Extensions.CreateMailAttachmentFromString(content, name);
        }
        
        /// <summary>
        /// Serializes the fields into a MimeEntity with the given name
        /// </summary>
        /// <param name="name"></param>
        /// <returns></returns>
        public MimeEntity ToMimeEntity(string name)
        {
            string body = this.ToString();
 
            ContentType mimeType = new ContentType(MediaTypeNames.Text.Plain);
            mimeType.Name = name;
            
            ContentDisposition disposition = new ContentDisposition("attachment");
            disposition.FileName = name;
            
            MimeEntity entity = new MimeEntity(body, mimeType.ToString());
            entity.ContentDisposition = disposition.ToString();
            
            return entity;
        }

        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
            return this.GetEnumerator();
        }
    }
}
