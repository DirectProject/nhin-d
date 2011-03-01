/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.
 
 Title:   SlotType.cs
 Purpose: Class representing the IHE XD* Slot metadata element
 Authors:
    Vassil Peytchev     vassil@epic.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

namespace Health.Direct.Xd.Common.ebXml
{
    [System.Xml.Serialization.XmlType(Namespace = GlobalValues.ebXmlRIMNamespace)]
    public enum SlotNameType
    {
        authorInstitution,
        authorTelecom,
        authorPerson,
        authorRole,
        authorSpecialty,
        creationTime,
        hash,
        intendedRecipient,
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

    [System.Xml.Serialization.XmlType(Namespace = GlobalValues.ebXmlRIMNamespace)]
    [System.Xml.Serialization.XmlRoot("Slot", Namespace = GlobalValues.ebXmlRIMNamespace, IsNullable = false)]
    public class SlotType
    {
        #region fields
        [System.Xml.Serialization.XmlIgnore()]
        private ValueListType _valueList;

        [System.Xml.Serialization.XmlIgnore()]
        private SlotNameType _name;

        [System.Xml.Serialization.XmlIgnore()]
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
        [System.Xml.Serialization.XmlElement("ValueList")]
        public ValueListType ValueList
        {
            get { return _valueList; }
            set { _valueList = value; }
        }

        [System.Xml.Serialization.XmlAttribute("name")]
        public SlotNameType Name
        {
            get { return _name; }
            set { _name = value; }
        }

        [System.Xml.Serialization.XmlAttribute("slotType")]
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