/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.
 Title:   ClassificationType.cs
 Purpose: Class representing the IHE XD* Association metadata element
 Authors:
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
    [System.Xml.Serialization.XmlRoot("ClassificationType", Namespace = GlobalValues.ebXmlRIMNamespace, IsNullable = false)]
    public class ClassificationType : RegistryObjectType
    {

        #region fields

        [System.Xml.Serialization.XmlIgnore()]
        private string _classificationScheme;
        
        [System.Xml.Serialization.XmlIgnore()]
        private string _classifiedObject;
        
        [System.Xml.Serialization.XmlIgnore()]
        private string _classificationNode;
        
        [System.Xml.Serialization.XmlIgnore()]
        private string _nodeRepresentation;

        #endregion

        public ClassificationType()
        {
        }

        public ClassificationType(string cScheme, string cObject, string cNode, string nRepr, string id, string name, SlotType[] slots)
        {
            if (cScheme != null) this.ClassificationScheme = cScheme;
            if (cObject != null) this.ClassifiedObject = cObject;
            if (cNode != null) this.ClassificationNode = cNode;
            if (nRepr != null) this.NodeRepresentation = nRepr;
            if (id != null) this.Id = id;
            if (name != null)
            {
                this.Name = new InternationalStringType();
                this.Name.LocalizedString = new LocalizedStringType[1];
                this.Name.LocalizedString[0] = new LocalizedStringType();
                this.Name.LocalizedString[0].Value = name;
            }
            if (slots != null) this.Slot = slots;
        }

        #region properties
        [System.Xml.Serialization.XmlAttribute("classificationScheme",DataType="IDREF")]
        public string ClassificationScheme
        {
            get { return _classificationScheme; }
            set { _classificationScheme = value; }
        }

        [System.Xml.Serialization.XmlAttribute("classifiedObject",DataType="IDREF")]
        public string ClassifiedObject
        {
            get { return _classifiedObject; }
            set { _classifiedObject = value; }
        }

        [System.Xml.Serialization.XmlAttribute("classificationNode",DataType="IDREF")]
        public string ClassificationNode
        {
            get { return _classificationNode; }
            set { _classificationNode = value; }
        }

        [System.Xml.Serialization.XmlAttribute("nodeRepresentation")]
        public string NodeRepresentation
        {
            get { return _nodeRepresentation; }
            set { _nodeRepresentation = value; }
        }

        #endregion



        #region Methods

        /// <summary>
        /// Creates a new XDS.b Classification object
        /// </summary>
        /// <param name="cScheme">Classification Scheme</param>
        /// <param name="cObject">Classification Object</param>
        /// <param name="cNode">Classification Node</param>
        /// <param name="nRepr">Classification Representation</param>
        /// <param name="name">Classification Name</param>
        /// <param name="slots">Classification Slots</param>
        /// <returns>The new XDS Classification object.</returns>
        public static ClassificationType CreateClassification(string cScheme, string cObject, string cNode, string nRepr, string id, string name, SlotType[] slots)
        {
            ClassificationType c = new ClassificationType(cScheme, cObject, cNode, nRepr, id, name, slots);
            return c;
        }

        #endregion Methods
    }
}