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
using System.Xml.Serialization;
using Health.Direct.Common.Collections;
using Health.Direct.Common.Extensions;
using Xunit;

namespace Health.Direct.Common.Tests.Collection
{
    public class PropertyFacts
    {
        public PropertyFacts()
        {
        }
        
        public IEnumerable<KeyValuePair<string, string>> Pairs
        {
            get
            {
                for (int i = 0; i < 10; ++i)
                {
                    string istring = i.ToString();
                    yield return new KeyValuePair<string, string>("key_" + istring, "value_" + istring);
                }
            }
        }
        
        [Fact]
        public void BagSerializeText()
        {
            PropertyBag bag = this.CreateBag();
            
            XmlSerializer serializer = new XmlSerializer(typeof(PropertyBag));
            string xml = null;
            
            Assert.DoesNotThrow(() => xml = serializer.ToXml(bag));            
            Assert.True(!string.IsNullOrEmpty(xml));
            
            PropertyBag bag2 = null;
            Assert.DoesNotThrow(() => bag2 = (PropertyBag) serializer.FromXml(xml));
            Assert.NotNull(bag2);
            
            Assert.True(Compare(bag, bag2));
        }

        [Fact]
        public void BagSerializeBytes()
        {
            PropertyBag bag = this.CreateBag();

            XmlSerializer serializer = new XmlSerializer(typeof(PropertyBag));
            byte[] xml = null;

            Assert.DoesNotThrow(() => xml = serializer.ToBytes(bag));
            Assert.True(!xml.IsNullOrEmpty());

            PropertyBag bag2 = null;
            Assert.DoesNotThrow(() => bag2 = (PropertyBag)serializer.FromBytes(xml));
            Assert.NotNull(bag2);

            Assert.True(Compare(bag, bag2));
        }

        [Fact]
        public void SetSerializeText()
        {
            PropertySet set = this.CreateSet();

            XmlSerializer serializer = new XmlSerializer(typeof(PropertySet));
            string xml = null;

            Assert.DoesNotThrow(() => xml = serializer.ToXml(set));
            Assert.True(!string.IsNullOrEmpty(xml));

            PropertySet set2 = null;
            Assert.DoesNotThrow(() => set2 = (PropertySet) serializer.FromXml(xml));
            Assert.NotNull(set2);

            Assert.True(Compare(set, set2));
        }

        [Fact]
        public void SetSerializeBytes()
        {
            PropertyBag bag = this.CreateBag();

            XmlSerializer serializer = new XmlSerializer(typeof(PropertyBag));
            byte[] xml = null;

            Assert.DoesNotThrow(() => xml = serializer.ToBytes(bag));
            Assert.True(!xml.IsNullOrEmpty());

            PropertyBag bag2 = null;
            Assert.DoesNotThrow(() => bag2 = (PropertyBag)serializer.FromBytes(xml));
            Assert.NotNull(bag2);

            Assert.True(Compare(bag, bag2));
        }
        
        PropertyBag CreateBag()
        {
            PropertyBag bag = new PropertyBag(this.Pairs);
            return bag;
        }
        
        PropertySet CreateSet()
        {
            PropertySet set = new PropertySet(this.Pairs);
            return set;
        }
        
        bool Compare(PropertyBag x, PropertyBag y)
        {
            var common = Common(x, y);
            return (x.Count == common.Count());
        }

        bool Compare(PropertySet x, PropertySet y)
        {
            var common = Common(x, y);
            return (x.Count == common.Count());
        }
        
        IEnumerable<KeyValuePair<string, string>> Common(PropertyBag x, PropertyBag y)
        {
            return Common(x.KeyValuePairs, y.KeyValuePairs);
        }

        IEnumerable<KeyValuePair<string, string>> Common(IEnumerable<KeyValuePair<string, string>> x, IEnumerable<KeyValuePair<string, string>> y)
        {
            return
            from px in x
            join py in y on px.Key equals py.Key
            where px.Value == py.Value
            select px;
        }
    }
}
