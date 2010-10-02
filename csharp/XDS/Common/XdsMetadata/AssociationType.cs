// *********************************************************************
// Copyright © Epic Systems Corporation 2008
// *********************************************************************
// TITLE:   AssociationType.cs
// PURPOSE: Class representing the IHE XDS.b Association metadata element
// AUTHOR:  Vassil Peytchev
// *********************************************************************


using System;
using System.Xml.Serialization;


namespace NHINDirect.XDS.Common.Metadata
{
	//[System.Xml.Serialization.XmlTypeAttribute(Namespace = Epic.Edi.IHE.XDSb.Transactions.Transaction.IHERIMNamespace)]
	//public enum AssociationKind
	//{
	//    HasMember,
	//    RPLC,
	//    APND,
	//    XFRM,
	//    XFRM_RPLC,
	//    signs
	//}

	[System.Xml.Serialization.XmlTypeAttribute(Namespace = GlobalValues.ebXmlRIMNamespace)]
	static public class AssociationKind
	{
		public const string HasMember = "urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember";
		public const string RPLC = "urn:oasis:names:tc:ebxml-regrep:AssociationType:RPLC";
		public const string APND = "urn:oasis:names:tc:ebxml-regrep:AssociationType:APND";
		public const string XFRM = "urn:oasis:names:tc:ebxml-regrep:AssociationType:XFRM";
		public const string XFRM_RPLC = "urn:oasis:names:tc:ebxml-regrep:AssociationType:XFRM_RPLC";
		public const string signs = "urn:oasis:names:tc:ebxml-regrep:AssociationType:signs";
	}

	[System.Xml.Serialization.XmlTypeAttribute(Namespace = GlobalValues.ebXmlRIMNamespace)]
	[System.Xml.Serialization.XmlRootAttribute("Association", Namespace = GlobalValues.ebXmlRIMNamespace, IsNullable = false)]
    public class AssociationType : RegistryObjectType
    {
        #region fields
		[System.Xml.Serialization.XmlIgnoreAttribute()]
        private string _associationType;
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        private string _sourceObject;
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        private string _targetObject;
        #endregion

        public AssociationType()
        {
        }

		public AssociationType(string type, string source, string target, string id, SlotType[] slots)
		{
			this.Type = type;
			this.SourceObject = source;
			this.TargetObject = target;
			this.Id = id;
			if (slots != null) this.Slot = slots;
		}

        #region properties
        [System.Xml.Serialization.XmlAttributeAttribute("associationType")]
		public string Type
        {
            get { return _associationType; }
            set { _associationType = value; }
        }

        [System.Xml.Serialization.XmlAttributeAttribute("sourceObject",DataType="IDREF")]
        public string SourceObject
        {
            get { return _sourceObject; }
            set { _sourceObject = value; }
        }

        [System.Xml.Serialization.XmlAttributeAttribute("targetObject",DataType="IDREF")]
        public string TargetObject
        {
            get { return _targetObject; }
            set { _targetObject = value; }
        }
        #endregion


		#region Methods

		static public AssociationType CreateAssociation(string type, string source, string target,
			string id, SlotType[] slots)
		{
			AssociationType assoc = new AssociationType(type, source, target, id, slots);
			return assoc;
		}

		#endregion Methods
	}
}
