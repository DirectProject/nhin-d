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
using System.Linq;
using System.Net.Mail;
using System.IO;
using System.Security.Cryptography;
using Health.Direct.Common.Cryptography;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Extensions;
using System.Xml.Serialization;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Common.Tests.Certificates
{
    public class CertificateFacts
    {
        [Fact]
        public void AnchorMetadataSerialize()
        {
            AnchorMetadata metadata = new AnchorMetadata();
            metadata.RequiredOids = new Oid[] {
                new Oid(SMIMECryptographer.CryptoOids.SHA1.Value, "SHA1 Digest Algorithm"),
                new Oid(SMIMECryptographer.CryptoOids.SHA256.Value, "SHA256 Digest Algorithm"),
            };
            metadata.BundleSource = "Toby's bundle";
            
            string xml = null;
            Assert.DoesNotThrow(() => xml = metadata.ToXml());
            
            AnchorMetadata metadataParsed = null;
            Assert.DoesNotThrow(() => metadataParsed = (AnchorMetadata) new XmlSerializer(typeof(AnchorMetadata)).FromXml(xml));
            
            Assert.True(metadataParsed.HasRequiredOids);
            Assert.True(metadata.RequiredOids.Length == metadataParsed.RequiredOids.Length);            
            for (int i = 0; i < metadata.RequiredOids.Length; ++i)
            {
                Assert.Equal(metadata.RequiredOids[i].Value, metadataParsed.RequiredOids[i].Value);
                Assert.Equal(metadata.RequiredOids[i].FriendlyName, metadataParsed.RequiredOids[i].FriendlyName);
            }
            
            Assert.Equal(metadata.BundleSource, metadataParsed.BundleSource);
        }
    }
}
