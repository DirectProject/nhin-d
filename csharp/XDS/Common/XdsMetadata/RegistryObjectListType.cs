// *********************************************************************
// Copyright © Epic Systems Corporation 2008
// *********************************************************************
// TITLE:   RegistryObjectListType.cs
// PURPOSE: Class representing the IHE XDS.b RegistryObjectList metadata element
// AUTHOR:  Justin Stauffer
// *********************************************************************


using System;
using System.Collections.Generic;
using System.Xml.Serialization;

namespace NHINDirect.XDS.Common.Metadata
{
	[System.Xml.Serialization.XmlTypeAttribute(Namespace = GlobalValues.ebXmlRIMNamespace)]
    [System.Xml.Serialization.XmlRootAttribute("RegistryObjectList", Namespace = GlobalValues.ebXmlRIMNamespace, IsNullable = false)]
	public class RegistryObjectListType
	{
		private List<ExtrinsicObjectType> _extrinsicObjects;
		private List<ClassificationType> _classifications;
		private List<AssociationType> _associations;
		private List<RegistryPackageType> _registryPackages;

		// TODO: ... enforce the order these show up
		// 


		public RegistryObjectListType()
		{
			_extrinsicObjects = new List<ExtrinsicObjectType>();
			_classifications = new List<ClassificationType>();
			_registryPackages = new List<RegistryPackageType>();
            _associations = new List<AssociationType>();
		}


		#region Properties


		[XmlElement("ExtrinsicObject")]
		public List<ExtrinsicObjectType> ExtrinsicObjects
		{
			get { return _extrinsicObjects; }
			set { _extrinsicObjects = value; }
		}

		[XmlElement("RegistryPackage")]
		public List<RegistryPackageType> RegistryPackages
		{
			get { return _registryPackages; }
			set { _registryPackages = value; }
		}

		[XmlElement("Classification")]
		public List<ClassificationType> Classifications
		{
			get { return _classifications; }
			set { _classifications = value; }
		}

		[XmlElement("Association")]
		public List<AssociationType> Associations
		{
			get { return _associations; }
			set { _associations = value; }
		}



		#endregion Properties
	}
}
