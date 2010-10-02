// *********************************************************************
// Copyright © Epic Systems Corporation 2008
// *********************************************************************
// TITLE:   InternationalStringType.cs
// PURPOSE: Class representing the IHE XDS.b International String type
// AUTHOR:  Vassil Peytchev
// *********************************************************************


using System;
using System.Xml.Serialization;

namespace NHINDirect.XDS.Common.Metadata
{
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = GlobalValues.ebXmlRIMNamespace)]
    [System.Xml.Serialization.XmlRootAttribute("International", Namespace = GlobalValues.ebXmlRIMNamespace, IsNullable = false)]
	public class InternationalStringType
	{
		#region fields
		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private LocalizedStringType[] _localizedString;
		#endregion

		public InternationalStringType()
		{
		}

		public InternationalStringType(LocalizedStringType[] localizedStrings)
		{
			this.LocalizedString = localizedStrings;
		}

		#region properties
		[System.Xml.Serialization.XmlElementAttribute("LocalizedString")]
		public LocalizedStringType[] LocalizedString
		{
			get { return _localizedString; }
			set { _localizedString = value; }
		}
		#endregion
	}
}
