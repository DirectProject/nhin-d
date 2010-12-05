/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.
 
 Title:   RegistryEntryType.cs
 Purpose: Class representing the IHE XD* RegistryEntry metadata element
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
    [System.Xml.Serialization.XmlType(Namespace=GlobalValues.ebXmlRIMNamespace)]
    public enum RegistryEntryStability 
    {
    
        Dynamic,
        DynamicCompatible,
        Static
    }

    static public class RegistryEntryStatus
    {
        public const string Approved = "urn:oasis:names:tc:ebxml-regrep:StatusType:Approved";
        public const string Submitted = "urn:oasis:names:tc:ebxml-regrep:StatusType:Submitted";
        public const string Deprecated = "urn:oasis:names:tc:ebxml-regrep:StatusType:Deprecated";
        public const string Withdrawn = "urn:oasis:names:tc:ebxml-regrep:StatusType:Withdrawn";
    }

    [System.Xml.Serialization.XmlType(Namespace = GlobalValues.ebXmlRIMNamespace)]
    [System.Xml.Serialization.XmlRoot("RegistryEntry", Namespace = GlobalValues.ebXmlRIMNamespace, IsNullable = false)]
    public class RegistryEntryType : RegistryObjectType
    {
        #region fields
        /*
		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private System.DateTime _expiration;
        */
        [System.Xml.Serialization.XmlIgnore()]
        private string _majorVersion = "1";
    
        [System.Xml.Serialization.XmlIgnore()]
        private string _minorVersion = "0";
    
        //[System.Xml.Serialization.XmlIgnoreAttribute()]
        //private RegistryEntryStability _stability;

        [System.Xml.Serialization.XmlIgnore()]
        private string _status;
    
        [System.Xml.Serialization.XmlIgnore()]
        private string _userVersion;
        #endregion

        public RegistryEntryType()
        {
            _status = RegistryEntryStatus.Approved;  // set default status to approved
        }

        #region properties
        /* 
		[System.Xml.Serialization.XmlAttributeAttribute("expiration")]
		public System.DateTime Expiration
		{
			get { return _expiration; }
			set { _expiration = value; }
		}
		*/
    
        [System.Xml.Serialization.XmlAttribute("majorVersion",DataType="integer")]
        [System.ComponentModel.DefaultValue("1")]
        public string MajorVersion
        {
            get { return _majorVersion; }
            set { _majorVersion = value; }
        }
    
        [System.Xml.Serialization.XmlAttribute("minorVersion",DataType="integer")]
        [System.ComponentModel.DefaultValue("0")]
        public string MinorVersion
        {
            get { return _minorVersion; }
            set { _minorVersion = value; }
        }
    
        //[System.Xml.Serialization.XmlAttributeAttribute("stability")]
        //public RegistryEntryStability Stability
        //{
        //    get { return _stability; }
        //    set { _stability = value; }
        //}

        [System.Xml.Serialization.XmlAttribute("status")]
        public string Status
        {
            get { return _status; }
            set { _status = value; }
        }
    
        [System.Xml.Serialization.XmlAttribute("userVersion")]
        public string UserVersion
        {
            get { return _userVersion; }
            set { _userVersion = value; }
        }
        #endregion
    }
}