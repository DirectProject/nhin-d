// *********************************************************************
// Copyright © Epic Systems Corporation 2008
// *********************************************************************
// TITLE:   SlotType.cs
// PURPOSE: Class representing the IHE XDS.b Slot metadata element
// AUTHOR:  Vassil Peytchev
// *********************************************************************

using System;
using System.Xml.Serialization;

namespace NHINDirect.XDS.Common.Metadata
{
	#region Enums
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = GlobalValues.ebXmlRIMNamespace)]
	public enum SlotNameType
	{
		authorInstitution,
		authorInstitutionId,
		authorPerson,
		authorRole,
		authorSpecialty,
		creationTime,
		hash,
		languageCode,
		legalAuthenticator,
		serviceStartTime,
		serviceStopTime,
		size,
		sourcePatientId,
		sourcePatientInfo,
		URI,

		//external classifications
		codingScheme,

		// XDSSubmissionSet 
		//authorInstitution,
		submissionTime,

		// XDSFolder
		lastUpdateTime,

		// HasMember Association
		SubmissionSetStatus
	}
	#endregion

    [System.Xml.Serialization.XmlTypeAttribute(Namespace = GlobalValues.ebXmlRIMNamespace)]
    [System.Xml.Serialization.XmlRootAttribute("Slot", Namespace = GlobalValues.ebXmlRIMNamespace, IsNullable = false)]
	public class SlotType
	{
		#region fields
		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private ValueListType _valueList;

		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private SlotNameType _name;

		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private string _slotType;
		#endregion

		public SlotType()
		{
		}

		/// <summary>
		/// Creates a new XDS Slot object with the given name and values.
		/// </summary>
		/// <param name="name">name of the slot</param>
		/// <param name="values">values to include in the slot object</param>
		/// <returns>Returns the new SlotType object</returns>
		public SlotType(SlotNameType name, string[] values)
		{
			this.Name = name;
			this.ValueList = new ValueListType();
			this.ValueList.Value = values;
		}

		#region properties
		[System.Xml.Serialization.XmlElementAttribute("ValueList")]
		public ValueListType ValueList
		{
			get { return _valueList; }
			set { _valueList = value; }
		}

		[System.Xml.Serialization.XmlAttributeAttribute("name")]
		public SlotNameType Name
		{
			get { return _name; }
			set { _name = value; }
		}

		[System.Xml.Serialization.XmlAttributeAttribute("slotType")]
		public string Type
		{
			get { return _slotType; }
			set { _slotType = value; }
		}
		#endregion


		#region Methods

		/// <summary>
		/// Creates a new XDS Slot object with the given name and values.
		/// </summary>
		/// <param name="name">name of the slot</param>
		/// <param name="values">values to include in the slot object</param>
		/// <returns>Returns the new SlotType object</returns>
		public static SlotType CreateSlot(SlotNameType name, string[] values)
		{
			SlotType slot = new SlotType(name, values);
			return slot;
		}

		#endregion Methods
	}
}
