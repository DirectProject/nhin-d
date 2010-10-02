// *********************************************************************
// Copyright © Epic Systems Corporation 2008
// *********************************************************************
// TITLE:   ObjectRefType.cs
// PURPOSE: Class representing the IHE XDS.b OjectRef metadata element
// AUTHOR:  Justin Stauffer
// *********************************************************************


using System;
using System.Xml.Serialization;

namespace NHINDirect.XDS.Common.Metadata
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
