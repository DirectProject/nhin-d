// *********************************************************************
// Copyright © Epic Systems Corporation 2010
// *********************************************************************
// TITLE:   SubmitObjectsRequest.cs
// PURPOSE: Class representing the IHE XDS SubmitObjectsRequest metadata element
// AUTHOR:  Justin Stauffer
// *********************************************************************

using System;
using System.Xml.Serialization;
using System.Collections;
using System.Collections.Generic;

using System.ServiceModel.Channels;
using System.Runtime.Serialization;

using NHINDirect.XDS.Common.Metadata;


namespace NHINDirect.XDS.Common.Metadata
{

    //[DataContractAttribute(Name="SubmitObjectsRequest", Namespace=GlobalValues.ebXmlLCMNamespace)]
	[XmlRoot("SubmitObjectsRequest", Namespace = GlobalValues.ebXmlLCMNamespace, IsNullable = false)]
	public class SubmitObjectsRequest
	{
		private RegistryObjectListType _registryObjectList;

		public SubmitObjectsRequest()
		{
			// for XML serialization
			_registryObjectList = new RegistryObjectListType();
		}

		public SubmitObjectsRequest(RegistryObjectListType registryObjectList)
		{
			_registryObjectList = registryObjectList;
		}


		#region Properties

		//[DataMember(Name="RegistryObjectList")]
        [XmlElement("RegistryObjectList", Namespace = GlobalValues.ebXmlRIMNamespace)]
		public RegistryObjectListType RegistryObjectList
		{
			get { return _registryObjectList; }
			set { _registryObjectList = value; }
		}

		#endregion Properties


	}
}
