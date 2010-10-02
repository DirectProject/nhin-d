// *********************************************************************
// Copyright © Epic Systems Corporation 2008
// *********************************************************************
// TITLE:   LocalizedStringType.cs
// PURPOSE: Class representing the IHE XDS.b Localized String type
// AUTHOR:  Vassil Peytchev
// *********************************************************************


using System;
using System.Xml.Serialization;

namespace NHINDirect.XDS.Common.Metadata
{
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = GlobalValues.ebXmlRIMNamespace)]
    [System.Xml.Serialization.XmlRootAttribute("LocalizedString", Namespace = GlobalValues.ebXmlRIMNamespace, IsNullable = false)]
    public class LocalizedStringType
    {
		#region fields
		
		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private string _lang = "en-US";
		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private string _charset = "UTF-8";
		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private string _value;

		#endregion

        public LocalizedStringType()
        {
        }

		public LocalizedStringType(string value)
		{
			this.Value = value;
		}

		#region properties
		//[System.Xml.Serialization.XmlAttributeAttribute(Form=System.Xml.Schema.XmlSchemaForm.Qualified, DataType="language")]
		[System.Xml.Serialization.XmlAttributeAttribute("lang", DataType="language")]
		[System.ComponentModel.DefaultValueAttribute("en-US")]
		public string Lang
		{
			get { return _lang; }
			set { _lang = value; }
		}
		[System.Xml.Serialization.XmlAttributeAttribute("charset")]
		[System.ComponentModel.DefaultValueAttribute("UTF-8")]
		public string Charset
		{
			get { return _charset; }
			set { _charset = value; }
		}
		[System.Xml.Serialization.XmlAttributeAttribute("value")]
		public string Value
		{
			get { return _value; }
			set { _value = value; }
		}	
		#endregion
    }
}
