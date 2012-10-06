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
using System.Collections.Specialized;
using System.Linq;
using System.Text;
using System.Xml;
using System.Xml.Serialization;

namespace Health.Direct.Common.Collections
{
    /// <summary>
    /// A NameValueCollection that is Xml Serializable
    /// </summary>
    [XmlRoot]
    public class PropertyBag : NameValueCollection, IXmlSerializable, IPropertyCollection
    {
        /// <summary>
        /// Construct a new property bag
        /// </summary>
        public PropertyBag()
            : base()
        {
        }
        
        /// <summary>
        /// Construct a new property bag
        /// </summary>
        /// <param name="capacity">capacity</param>
        public PropertyBag(int capacity)
            : base(capacity)
        {
        }
        
        /// <summary>
        /// Construct a new property bag
        /// </summary>
        /// <param name="pairs">source values to populate the bag with</param>
        public PropertyBag(IEnumerable<KeyValuePair<string, string>> pairs)
        {
            if (pairs == null)
            {
                throw new ArgumentNullException("pairs");
            }
            
            foreach(KeyValuePair<string, string> pair in pairs)
            {
                this.Add(pair.Key, pair.Value);
            }
        }
        
        /// <summary>
        /// Enumerate all key value pairs
        /// </summary>
        /// <returns>An enumerator for key,value pairs in this collection</returns>
        public IEnumerable<KeyValuePair<string, string>> KeyValuePairs
        {
            get
            {
                foreach(string key in this.Keys)
                {
                    yield return new KeyValuePair<string, string>(key, this[key]);
                }
            }
        }
        
        /// <summary>
        /// Add a value to the property bag
        /// </summary>
        /// <param name="pair">value to add</param>        
        public void Add(KeyValuePair<string, string> pair)
        {
            base.Add(pair.Key, pair.Value);
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
                this.Add(pair);
            }
        }
        
        /// <summary>
        /// Deserialize a property bag from an Xml stream
        /// </summary>
        /// <param name="reader">Xml reader</param>
        public void ReadXml(XmlReader reader)
        {
            PropertySet.ReadXml(this, reader);
        }

        /// <summary>
        /// Serialize the property set into Xml
        /// </summary>
        /// <param name="writer">Xml Writer</param>
        public void WriteXml(XmlWriter writer)
        {
            PropertySet.WriteXml(this.KeyValuePairs, writer);
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
    }
}
