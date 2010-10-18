/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.
 
 Title:   AssociationType.cs
 Purpose: Class representing the IHE XD* Association metadata element
 Authors:
    Vassil Peytchev     vassil@epic.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Xml.Serialization;


namespace NHINDirect.XDS.Common.Metadata
{
	//[System.Xml.Serialization.XmlTypeAttribute(Namespace = Epic.Edi.IHE.XDSb.Transactions.Transaction.IHERIMNamespace)]
	//public enum AssociationKind
	//{
	//    HasMember,
	//    RPLC,
	//    APND,
	//    XFRM,
	//    XFRM_RPLC,
	//    signs
	//}

	[System.Xml.Serialization.XmlTypeAttribute(Namespace = GlobalValues.ebXmlRIMNamespace)]
	static public class AssociationKind
	{
		public const string HasMember = "urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember";
		public const string RPLC = "urn:oasis:names:tc:ebxml-regrep:AssociationType:RPLC";
		public const string APND = "urn:oasis:names:tc:ebxml-regrep:AssociationType:APND";
		public const string XFRM = "urn:oasis:names:tc:ebxml-regrep:AssociationType:XFRM";
		public const string XFRM_RPLC = "urn:oasis:names:tc:ebxml-regrep:AssociationType:XFRM_RPLC";
		public const string signs = "urn:oasis:names:tc:ebxml-regrep:AssociationType:signs";
	}

	[System.Xml.Serialization.XmlTypeAttribute(Namespace = GlobalValues.ebXmlRIMNamespace)]
	[System.Xml.Serialization.XmlRootAttribute("Association", Namespace = GlobalValues.ebXmlRIMNamespace, IsNullable = false)]
    public class AssociationType : RegistryObjectType
    {
        #region fields
		[System.Xml.Serialization.XmlIgnoreAttribute()]
        private string _associationType;
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        private string _sourceObject;
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        private string _targetObject;
        #endregion

        public AssociationType()
        {
        }

		public AssociationType(string type, string source, string target, string id, SlotType[] slots)
		{
			this.Type = type;
			this.SourceObject = source;
			this.TargetObject = target;
			this.Id = id;
			if (slots != null) this.Slot = slots;
		}

        #region properties
        [System.Xml.Serialization.XmlAttributeAttribute("associationType")]
		public string Type
        {
            get { return _associationType; }
            set { _associationType = value; }
        }

        [System.Xml.Serialization.XmlAttributeAttribute("sourceObject",DataType="IDREF")]
        public string SourceObject
        {
            get { return _sourceObject; }
            set { _sourceObject = value; }
        }

        [System.Xml.Serialization.XmlAttributeAttribute("targetObject",DataType="IDREF")]
        public string TargetObject
        {
            get { return _targetObject; }
            set { _targetObject = value; }
        }
        #endregion


		#region Methods

		static public AssociationType CreateAssociation(string type, string source, string target,
			string id, SlotType[] slots)
		{
			AssociationType assoc = new AssociationType(type, source, target, id, slots);
			return assoc;
		}

		#endregion Methods
	}
}
