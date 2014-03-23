/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.
 
 Title:   RegistryObjectType.cs
 Purpose: Class representing the IHE XD* RegistryObject metadata element
 Authors:
    Justin Stauffer     justin@epic.com
    Vassil Peytchev     vassil@epic.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

namespace Health.Direct.Xd.Common.ebXml
{
    [System.Xml.Serialization.XmlType(Namespace = GlobalValues.ebXmlRIMNamespace)]
    [System.Xml.Serialization.XmlInclude(typeof(InternationalStringType))]
    [System.Xml.Serialization.XmlInclude(typeof(SlotType))]
    [System.Xml.Serialization.XmlInclude(typeof(ExternalIdentifierType))]
    [System.Xml.Serialization.XmlInclude(typeof(ClassificationType))]
    [System.Xml.Serialization.XmlRoot("RegistryObject", Namespace = GlobalValues.ebXmlRIMNamespace, IsNullable = false)]
    public class RegistryObjectType
    {
        #region fields
        [System.Xml.Serialization.XmlIgnore()]
        private InternationalStringType _name;
    
        [System.Xml.Serialization.XmlIgnore()]
        private InternationalStringType _description;
    
        [System.Xml.Serialization.XmlIgnore()]
        private SlotType[] _slot;
    
        [System.Xml.Serialization.XmlIgnore()]
        private ClassificationType[] _classification;
    
        [System.Xml.Serialization.XmlIgnore()]
        private ExternalIdentifierType[] _externalIdentifier;
    
        [System.Xml.Serialization.XmlIgnore()]
        private string _accessControlPolicy;
    
        [System.Xml.Serialization.XmlIgnore()]
        private string _id;
    
        [System.Xml.Serialization.XmlIgnore()]
        private string _objectType;
        #endregion

        public RegistryObjectType()
        {
        }

        #region properties

        [System.Xml.Serialization.XmlElement("Slot")]
        public SlotType[] Slot
        {
            get { return _slot; }
            set { _slot = value; }
        }

        [System.Xml.Serialization.XmlElement("Name")]
        public InternationalStringType Name
        {
            get { return _name; }
            set { _name = value; }
        }

        [System.Xml.Serialization.XmlElement("Description")]
        public InternationalStringType Description
        {
            get { return _description; }
            set { _description = value; }
        }

        [System.Xml.Serialization.XmlElement("Classification")]
        public ClassificationType[] Classification
        {
            get { return _classification; }
            set { _classification = value; }
        }

        [System.Xml.Serialization.XmlElement("ExternalIdentifier")]
        public ExternalIdentifierType[] ExternalIdentifier
        {
            get { return _externalIdentifier; }
            set { _externalIdentifier = value; }
        }

        [System.Xml.Serialization.XmlAttribute("accessControlPolicy",DataType="IDREF")]
        public string AccessControlPolicy
        {
            get { return _accessControlPolicy; }
            set { _accessControlPolicy = value; }
        }

        [System.Xml.Serialization.XmlAttribute("id",DataType="ID")]
        public string Id
        {
            get { return _id; }
            set { _id = value; }
        }

        [System.Xml.Serialization.XmlAttribute("objectType")]
        public string ObjectType
        {
            get { return _objectType; }
            set { _objectType = value; }
        }

        #endregion

    }
}