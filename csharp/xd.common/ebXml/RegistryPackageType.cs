/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.
 
 Title:   RegistryPackageType.cs
 Purpose: Class representing the IHE XD* RegistryPackage metadata element
 Authors:
    Justin Stauffer     justin@epic.com
    Vassil Peytchev     vassil@epic.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System.Xml.Serialization;

namespace Health.Direct.Xd.Common.ebXml
{
    [System.Xml.Serialization.XmlType(Namespace = GlobalValues.ebXmlRIMNamespace)]
    [System.Xml.Serialization.XmlRoot("RegistryPackage", Namespace = GlobalValues.ebXmlRIMNamespace, IsNullable = false)]
    public class RegistryPackageType : RegistryEntryType
    {
        #region fields
        [System.Xml.Serialization.XmlIgnore()]
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