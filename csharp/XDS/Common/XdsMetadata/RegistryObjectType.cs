// *********************************************************************
// Copyright © Epic Systems Corporation 2006
// *********************************************************************
// TITLE:   RegistryObjectType.cs
// PURPOSE: Class representing the IHE XDS RegistryObject metadata element
// AUTHOR:  Vassil Peytchev
// REVISION HISTORY:
// *jjs 08/08/06 111979 - Created for submitting CDA documents to adocument registry using the IHE specified protocol
// *********************************************************************


using System;
using System.Xml.Serialization;

namespace NHINDirect.XDS.Common.Metadata
{
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = GlobalValues.ebXmlRIMNamespace)]
	[System.Xml.Serialization.XmlIncludeAttribute(typeof(InternationalStringType))]
	[System.Xml.Serialization.XmlIncludeAttribute(typeof(SlotType))]
	[System.Xml.Serialization.XmlIncludeAttribute(typeof(ExternalIdentifierType))]
	[System.Xml.Serialization.XmlIncludeAttribute(typeof(ClassificationType))]
    [System.Xml.Serialization.XmlRootAttribute("RegistryObject", Namespace = GlobalValues.ebXmlRIMNamespace, IsNullable = false)]
    public class RegistryObjectType
	{
		#region fields
		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private InternationalStringType _name;
    
		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private InternationalStringType _description;
    
		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private SlotType[] _slot;
    
		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private ClassificationType[] _classification;
    
		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private ExternalIdentifierType[] _externalIdentifier;
    
		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private string _accessControlPolicy;
    
		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private string _id;
    
		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private string _objectType;
		#endregion

        public RegistryObjectType()
        {
        }

		#region properties

		[System.Xml.Serialization.XmlElementAttribute("Slot")]
		public SlotType[] Slot
		{
			get { return _slot; }
			set { _slot = value; }
		}

		[System.Xml.Serialization.XmlElementAttribute("Name")]
		public InternationalStringType Name
		{
			get { return _name; }
			set { _name = value; }
		}

		[System.Xml.Serialization.XmlElementAttribute("Description")]
		public InternationalStringType Description
		{
			get { return _description; }
			set { _description = value; }
		}

		[System.Xml.Serialization.XmlElementAttribute("Classification")]
		public ClassificationType[] Classification
		{
			get { return _classification; }
			set { _classification = value; }
		}

		[System.Xml.Serialization.XmlElementAttribute("ExternalIdentifier")]
		public ExternalIdentifierType[] ExternalIdentifier
		{
			get { return _externalIdentifier; }
			set { _externalIdentifier = value; }
		}

		[System.Xml.Serialization.XmlAttributeAttribute("accessControlPolicy",DataType="IDREF")]
		public string AccessControlPolicy
		{
			get { return _accessControlPolicy; }
			set { _accessControlPolicy = value; }
		}

		[System.Xml.Serialization.XmlAttributeAttribute("id",DataType="ID")]
		public string Id
		{
			get { return _id; }
			set { _id = value; }
		}

		[System.Xml.Serialization.XmlAttributeAttribute("objectType")]
		public string ObjectType
		{
			get { return _objectType; }
			set { _objectType = value; }
		}

		#endregion

    }
}
