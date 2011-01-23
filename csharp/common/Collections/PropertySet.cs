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
using System.Linq;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using System.IO;

namespace Health.Direct.Common.Collections
{
    /*
     Dictionary that supports Xml Serialization.
     
     */
    /// <summary>
    /// A (N, V) Dictionary that is Xml Serializable!
    /// </summary>
    [XmlRoot]
    public class PropertySet : Dictionary<string, string>, IXmlSerializable, IPropertyCollection
    {
        /// <summary>
        /// Create a new Property Set
        /// </summary>
        public PropertySet()
        {
        }

        /// <summary>
        /// Create a new Property Set
        /// </summary>
        /// <param name="capacity">capacity</param>
        public PropertySet(int capacity)
            : base(capacity)
        {
        }
        
        /// <summary>
        /// Create a Property Set
        /// </summary>
        /// <param name="pairs">Initialize the set with these values</param>        
        public PropertySet(IEnumerable<KeyValuePair<string, string>> pairs)
        {
            this.Add(pairs);
        }
        
        /// <summary>
        /// Add values to the property set
        /// </summary>
        /// <param name="pairs">values to paid</param>
        public void Add(IEnumerable<KeyValuePair<string, string>> pairs)
        {
            if (pairs == null)
            {
                throw new ArgumentNullException("pairs");
            }

            foreach (KeyValuePair<string, string> pair in pairs)
            {
                this.Add(pair.Key, pair.Value);
            }
        }
                
        /// <summary>
        /// Retrieve the value for the given name, and convert it to the give T
        /// This handles most common type conversions (int, long, double etc).
        /// However, it is NOT designed to handle every possible conversion. 
        /// </summary>
        /// <typeparam name="T">Desired value type</typeparam>
        /// <param name="key">key</param>
        /// <returns>Typed value</returns>
        public T Get<T>(string key)
        {
            return ConvertTo<T>(this[key]);
        }
        
        /// <summary>
        /// If a key,value pair exists, return the value. 
        /// Else return the given default value
        /// </summary>
        /// <param name="key">key</param>
        /// <param name="defaultValue">default value</param>
        /// <returns>string value</returns>
        public string Get(string key, string defaultValue)
        {
            string value = null;            
            if (!this.TryGetValue(key, out value) || string.IsNullOrEmpty(value))
            {
                return defaultValue;
            }

            return value;
        }

        /// <summary>
        /// If a key,value pair exists, converts the value to the given Type. 
        /// Else return the given default value
        /// This handles most common type conversions (int, long, double etc).
        /// However, it is NOT designed to handle every possible conversion. 
        /// </summary>
        /// <typeparam name="T">Desired value Type</typeparam>
        /// <param name="key">key</param>
        /// <param name="defaultValue">default value</param>
        /// <returns>string value</returns>
        public T Get<T>(string key, T defaultValue)
        {
            string value = null;
            if (!this.TryGetValue(key, out value) || string.IsNullOrEmpty(value))
            {
                return defaultValue;
            }
            return ConvertTo<T>(value);
        }
                        
        /// <summary>
        /// Deserialize a property set from an Xml stream
        /// </summary>
        /// <param name="reader">Xml reader</param>
        public void ReadXml(XmlReader reader)
        {
            ReadXml(this, reader);
        }

        internal static void ReadXml(IPropertyCollection collection, XmlReader reader)
        {
            if (reader == null)
            {
                throw new ArgumentNullException();
            }

            reader.ReadStartElement();
            int depth = reader.Depth;
            reader.MoveToContent();
            while (reader.Depth == depth)
            {
                string name = reader.LocalName;
                string value = reader.ReadElementString();
                collection.Add(name, value);
                reader.MoveToContent();
            }
        }
        
        /// <summary>
        /// Serialize the property set into Xml
        /// </summary>
        /// <param name="writer">Xml Writer</param>
        public void WriteXml(XmlWriter writer)
        {
            WriteXml(this, writer);
        }
        
        internal static void WriteXml(IEnumerable<KeyValuePair<string, string>> kvPairs, XmlWriter writer)
        {
            if (writer == null)
            {
                throw new ArgumentNullException("writer");
            }

            foreach (KeyValuePair<string, string> kvPair in kvPairs)
            {
                writer.WriteElementString(kvPair.Key, kvPair.Value);
            }
        }

        #region IXmlSerializable Members
        
        /// <summary>
        /// Return the Schema. No schema is currently returned
        /// </summary>
        /// <returns>null</returns>
        public System.Xml.Schema.XmlSchema GetSchema()
        {
            return null;
        }
        #endregion

        static T ConvertTo<T>(string source)
        {
            Type type = typeof(T);

            if (type.IsEnum)
            {
                return (T)Enum.Parse(type, source, true);
            }

            if (type == typeof(Guid))
            {
                return (T)(object)new Guid(source);
            }

            if (type == typeof(TimeSpan))
            {
                return (T)(object)TimeSpan.Parse(source);
            }

            return (T)Convert.ChangeType(source, typeof(T));
        }
    }
    
    internal interface IPropertyCollection
    {
        void Add(string key, string value);
    }
}
