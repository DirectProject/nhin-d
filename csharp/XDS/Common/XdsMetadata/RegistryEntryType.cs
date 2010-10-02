// *********************************************************************
// Copyright © Epic Systems Corporation 2008
// *********************************************************************
// TITLE:   RegistryEntryType.cs
// PURPOSE: Class representing the IHE XDS.b RegistryEntry metadata element
// AUTHOR:  Vassil Peytchev
// *********************************************************************


using System;
using System.Xml.Serialization;

namespace NHINDirect.XDS.Common.Metadata
{
	[System.Xml.Serialization.XmlTypeAttribute(Namespace=GlobalValues.ebXmlRIMNamespace)]
	public enum RegistryEntryStability 
	{
    
		Dynamic,
		DynamicCompatible,
		Static
	}

	static public class RegistryEntryStatus
	{
		public const string Approved = "urn:oasis:names:tc:ebxml-regrep:StatusType:Approved";
		public const string Submitted = "urn:oasis:names:tc:ebxml-regrep:StatusType:Submitted";
		public const string Deprecated = "urn:oasis:names:tc:ebxml-regrep:StatusType:Deprecated";
		public const string Withdrawn = "urn:oasis:names:tc:ebxml-regrep:StatusType:Withdrawn";
	}

    [System.Xml.Serialization.XmlTypeAttribute(Namespace = GlobalValues.ebXmlRIMNamespace)]
    [System.Xml.Serialization.XmlRootAttribute("RegistryEntry", Namespace = GlobalValues.ebXmlRIMNamespace, IsNullable = false)]
    public class RegistryEntryType : RegistryObjectType
    {
		#region fields
		/*
		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private System.DateTime _expiration;
        */
		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private string _majorVersion = "1";
    
		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private string _minorVersion = "0";
    
		//[System.Xml.Serialization.XmlIgnoreAttribute()]
		//private RegistryEntryStability _stability;

		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private string _status;
    
		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private string _userVersion;
		#endregion

        public RegistryEntryType()
        {
			_status = RegistryEntryStatus.Approved;  // set default status to approved
        }

		#region properties
		/* 
		[System.Xml.Serialization.XmlAttributeAttribute("expiration")]
		public System.DateTime Expiration
		{
			get { return _expiration; }
			set { _expiration = value; }
		}
		*/
    
		[System.Xml.Serialization.XmlAttributeAttribute("majorVersion",DataType="integer")]
		[System.ComponentModel.DefaultValueAttribute("1")]
		public string MajorVersion
		{
			get { return _majorVersion; }
			set { _majorVersion = value; }
		}
    
		[System.Xml.Serialization.XmlAttributeAttribute("minorVersion",DataType="integer")]
		[System.ComponentModel.DefaultValueAttribute("0")]
		public string MinorVersion
		{
			get { return _minorVersion; }
			set { _minorVersion = value; }
		}
    
		//[System.Xml.Serialization.XmlAttributeAttribute("stability")]
		//public RegistryEntryStability Stability
		//{
		//    get { return _stability; }
		//    set { _stability = value; }
		//}

		[System.Xml.Serialization.XmlAttributeAttribute("status")]
		public string Status
		{
			get { return _status; }
			set { _status = value; }
		}
    
		[System.Xml.Serialization.XmlAttributeAttribute("userVersion")]
		public string UserVersion
		{
			get { return _userVersion; }
			set { _userVersion = value; }
		}
		#endregion
    }
}
