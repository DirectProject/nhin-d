// *********************************************************************
// Copyright © Epic Systems Corporation 2008
// *********************************************************************
// TITLE:   RegistryPackageType.cs
// PURPOSE: Class representing the IHE XDS.b RegistryPackage metadata element
// AUTHOR:  Justin Stauffer
// *********************************************************************


using System;
using System.Xml.Serialization;

namespace NHINDirect.XDS.Common.Metadata
{
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = GlobalValues.ebXmlRIMNamespace)]
    [System.Xml.Serialization.XmlRootAttribute("RegistryPackage", Namespace = GlobalValues.ebXmlRIMNamespace, IsNullable = false)]
    public class RegistryPackageType : RegistryEntryType
    {
		#region fields
		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private RegistryObjectListType _registryObjectList;
		#endregion

        public RegistryPackageType()
        {
        }

		public RegistryPackageType(string id, string name, string[] eiScheme, string[] eiValue, string[] eiId,
			string[] eiRegistryObject, string[] eiName, SlotType[] slots, ClassificationType[] classifications,
			string status)
		{
			this.Id = id;

			this.Name = new InternationalStringType();
			this.Name.LocalizedString = new LocalizedStringType[1];
			this.Name.LocalizedString[0] = new LocalizedStringType();
			this.Name.LocalizedString[0].Value = name;

			this.Status = status;

			ExternalIdentifierType ei = null;
			this.ExternalIdentifier = new ExternalIdentifierType[eiScheme.Length];
			for (int i = 0; i < eiScheme.Length; i++)
			{
				ei = new ExternalIdentifierType(eiScheme[i], eiValue[i], eiId[i], eiRegistryObject[i], eiName[i]);
				this.ExternalIdentifier[i] = ei;
			}

			if (slots != null)
			{
				this.Slot = slots;
			}

			if (classifications != null)
			{
				this.Classification = classifications;
			}
		}

		#region Properties

		[XmlElement("RegistryObjectList")]
		public RegistryObjectListType RegistryObjectList
		{
			get { return _registryObjectList; }
			set { _registryObjectList = value; }
		}
		
		#endregion Properties

		
		#region Methods

		public static RegistryPackageType CreateRegistryPackage(string id, string name, string[] eiScheme,
			string[] eiValue, string[] eiName, SlotType[] slots, ClassificationType[] classifications, string status)
		{
			RegistryPackageType rp = new RegistryPackageType(id, name, eiScheme, eiValue, null, null,
				eiName, slots, classifications, status);
			return rp;
		}

		#endregion Methods
	}
}
