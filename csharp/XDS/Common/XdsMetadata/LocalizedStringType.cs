/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.
 
 Title:   LocalizedStringType.cs
 Purpose: Class representing the IHE XD* Localized String type
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
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = GlobalValues.ebXmlRIMNamespace)]
    [System.Xml.Serialization.XmlRootAttribute("LocalizedString", Namespace = GlobalValues.ebXmlRIMNamespace, IsNullable = false)]
    public class LocalizedStringType
    {
		#region fields
		
		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private string _lang = "en-US";
		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private string _charset = "UTF-8";
		[System.Xml.Serialization.XmlIgnoreAttribute()]
		private string _value;

		#endregion

        public LocalizedStringType()
        {
        }

		public LocalizedStringType(string value)
		{
			this.Value = value;
		}

		#region properties
		//[System.Xml.Serialization.XmlAttributeAttribute(Form=System.Xml.Schema.XmlSchemaForm.Qualified, DataType="language")]
		[System.Xml.Serialization.XmlAttributeAttribute("lang", DataType="language")]
		[System.ComponentModel.DefaultValueAttribute("en-US")]
		public string Lang
		{
			get { return _lang; }
			set { _lang = value; }
		}
		[System.Xml.Serialization.XmlAttributeAttribute("charset")]
		[System.ComponentModel.DefaultValueAttribute("UTF-8")]
		public string Charset
		{
			get { return _charset; }
			set { _charset = value; }
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
