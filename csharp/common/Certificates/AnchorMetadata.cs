/* 
 Copyright (c) 2013, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.Xml.Serialization;
using Health.Direct.Common.Extensions;

namespace Health.Direct.Common.Certificates
{
    /// <summary>
    /// Anchor metadata. XmlSerializable for easy stashing in blobs and elsewhere
    /// </summary>
    [XmlType("AnchorMetadata")]
    public class AnchorMetadata
    {
        /// <summary>
        /// Construct a new AnchorMetadata object
        /// </summary>
        public AnchorMetadata()
        {
        }

        /// <summary>
        /// OPTIONAL
        /// If a certificate is issued by this anchor, then it must proffer these additional Oids to be truly trusted
        /// </summary>    
        [XmlArray]
        [XmlArrayItem("Oid", typeof(Oid))]
        public Oid[] RequiredOids
        {
            get; set;
        }

        /// <summary>
        /// OPTIONAL
        /// Bundle in which this Anchor originated. 
        /// </summary>
        [XmlElement]
        public string BundleSource
        {
            get;
            set;
        }
        
        /// <summary>
        /// Returns true if any issued certificates must contain these Oids
        /// </summary>
        [XmlIgnore]
        public bool HasRequiredOids
        {
            get { return !this.RequiredOids.IsNullOrEmpty();}
        }
    }
}
