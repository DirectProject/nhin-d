// *********************************************************************
// Copyright © Epic Systems Corporation 2008
// *********************************************************************
// TITLE:   ClassificationType.cs
// PURPOSE: Class representing the IHE XDS.b Classification metadata element
// AUTHOR:  Justin Stauffer
// *********************************************************************

using System;
using System.Xml.Serialization;

namespace NHINDirect.XDS.Common.Metadata
{
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = GlobalValues.ebXmlRIMNamespace)]
    [System.Xml.Serialization.XmlRootAttribute("ClassificationType", Namespace = GlobalValues.ebXmlRIMNamespace, IsNullable = false)]
    public class ClassificationType : RegistryObjectType
	{

		#region fields

		[System.Xml.Serialization.XmlIgnoreAttribute()]
        private string _classificationScheme;
        
		[System.Xml.Serialization.XmlIgnoreAttribute()]
        private string _classifiedObject;
        
		[System.Xml.Serialization.XmlIgnoreAttribute()]
        private string _classificationNode;
        
		[System.Xml.Serialization.XmlIgnoreAttribute()]
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
		[System.Xml.Serialization.XmlAttributeAttribute("classificationScheme",DataType="IDREF")]
        public string ClassificationScheme
        {
            get { return _classificationScheme; }
            set { _classificationScheme = value; }
        }

        [System.Xml.Serialization.XmlAttributeAttribute("classifiedObject",DataType="IDREF")]
        public string ClassifiedObject
        {
            get { return _classifiedObject; }
            set { _classifiedObject = value; }
        }

        [System.Xml.Serialization.XmlAttributeAttribute("classificationNode",DataType="IDREF")]
        public string ClassificationNode
        {
            get { return _classificationNode; }
            set { _classificationNode = value; }
        }

        [System.Xml.Serialization.XmlAttributeAttribute("nodeRepresentation")]
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
