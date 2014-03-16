/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.
 
 Title:   ObjectRefType.cs
 Purpose: Class representing the IHE XD* ObjectRef metadata element
 Authors:
    Justin Stauffer     justin@epic.com
    Vassil Peytchev     vassil@epic.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System.Xml.Serialization;

namespace Health.Direct.Xd.Common.ebXml
{
    [XmlType(Namespace = GlobalValues.ebXmlRIMNamespace)]
    [XmlRoot("ObjectRef", Namespace = GlobalValues.ebXmlRIMNamespace, IsNullable = false)]
    public class ObjectRefType
    {
        private string _id;

        public ObjectRefType()
        {
        }

        public ObjectRefType(string id)
        {
            _id = id;
        }

        #region Properties

        [XmlAttribute("id", DataType = "ID")]
        public string Id
        {
            get { return _id; }
            set { _id = value; }
        }

        #endregion Properties
    }
}