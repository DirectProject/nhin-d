// *********************************************************************
// Copyright © Epic Systems Corporation 2008
// *********************************************************************
// TITLE:   ValueList.cs
// PURPOSE: Class representing the IHE XDS.b ValueList metadata element
// AUTHOR:  Vassil Peytchev
// *********************************************************************

using System;
using System.Xml.Serialization;

namespace NHINDirect.XDS.Common.Metadata
{
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = GlobalValues.ebXmlRIMNamespace)]
    [System.Xml.Serialization.XmlRootAttribute("ValueList", Namespace = GlobalValues.ebXmlRIMNamespace, IsNullable = false)]
    public class ValueListType
    {
		#region fields

		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private string[] _value;

		#endregion


		public ValueListType()
        {
        }
		

		#region properties

		[System.Xml.Serialization.XmlElementAttribute("Value")]
		public string[] Value
		{
			get { return _value; }
			set { _value = value; }
		}

		#endregion
    }
}
