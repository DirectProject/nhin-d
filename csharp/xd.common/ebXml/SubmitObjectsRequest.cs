/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.
 
 Title:   SubmitObjectsRequest.cs
 Purpose: Class representing the IHE XD* SubmitObjectsRequest metadata element
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