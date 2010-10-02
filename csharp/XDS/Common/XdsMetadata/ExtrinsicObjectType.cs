// *********************************************************************
// Copyright © Epic Systems Corporation 2008
// *********************************************************************
// TITLE:   ExtrinsicObjectType.cs
// PURPOSE: Class representing the IHE XDS.b ExtrinsicObject metadata element
// AUTHOR:  Vassil Peytchev
// *********************************************************************

using System;
using System.Xml.Serialization;

namespace NHINDirect.XDS.Common.Metadata
{
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = GlobalValues.ebXmlRIMNamespace)]
    [System.Xml.Serialization.XmlRootAttribute("ExtrinsicObject", Namespace = GlobalValues.ebXmlRIMNamespace, IsNullable = false)]
    public class ExtrinsicObjectType : RegistryEntryType
    {
        #region fields
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        private string _mimeType;
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        private bool _isOpaque;
        #endregion

        public ExtrinsicObjectType()
        {
        }

        #region properties
		[System.Xml.Serialization.XmlAttributeAttribute("mimeType")]
        public string MimeType
        {
            get { return _mimeType; }
            set { _mimeType = value; }
        }

        [System.Xml.Serialization.XmlAttributeAttribute("isOpaque")]
        public bool IsOpaque
        {
            get { return _isOpaque; }
            set { _isOpaque = value; }
        }
        #endregion
    }
}
