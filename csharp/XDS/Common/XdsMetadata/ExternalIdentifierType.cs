// *********************************************************************
// Copyright © Epic Systems Corporation 2008
// *********************************************************************
// TITLE:   ExternalIdentifierType.cs
// PURPOSE: Class representing the IHE XDS.b ExternalIdentifier metadata element
// AUTHOR:  Vassil Peytchev
// *********************************************************************


using System;
using System.Xml.Serialization;

namespace NHINDirect.XDS.Common.Metadata
{
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = GlobalValues.ebXmlRIMNamespace)]
    [System.Xml.Serialization.XmlRootAttribute("ExternalIdentifier", Namespace = GlobalValues.ebXmlRIMNamespace, IsNullable = false)]
    public class ExternalIdentifierType : RegistryObjectType
    {
        #region fields
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        private string _registryObject;
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        private string _identificationScheme;
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        private string _value;
        #endregion

        public ExternalIdentifierType()
        {
        }

		public ExternalIdentifierType(string scheme, string value, string id, string registryObject, string name)
		{
			this.IdentificationScheme = scheme;
			this.Value = value;
			this.Id = id;
			this.RegistryObject = registryObject;

			this.Name = new InternationalStringType();
			this.Name.LocalizedString = new LocalizedStringType[1];
			this.Name.LocalizedString[0] = new LocalizedStringType(name);
		}

        #region properties
		[System.Xml.Serialization.XmlAttributeAttribute("registryObject",DataType="IDREF")]
        public string RegistryObject
        {
            get { return _registryObject; }
            set { _registryObject = value; }
        }

        [System.Xml.Serialization.XmlAttributeAttribute("identificationScheme",DataType="IDREF")]
        public string IdentificationScheme
        {
            get { return _identificationScheme; }
            set { _identificationScheme = value; }
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
