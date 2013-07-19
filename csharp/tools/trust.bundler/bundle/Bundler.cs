/* 
 Copyright (c) 2013, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     jshook@kryptiq.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System.Collections;
using System.IO;
using System.Reflection;
using System.Text;
using System.Xml.Linq;
using System.Xml.Schema;
using Org.BouncyCastle.Cms;
using Org.BouncyCastle.X509.Store;

namespace Health.Direct.Trust
{
    /// <summary>
    /// A Bundler generates Cms structured data according to
    /// "Implementation Guide for Direct Project Trust Bundle Distribution", Version 1.0.
    /// http://wiki.directproject.org/Trust+Bundle+IG+Consensus+Page+-+Round+2
    /// </summary>
    public class Bundler
    {

        /// <summary>
        /// Create a trust bundle.
        /// </summary>
        public byte[] Create(IResourceProvider bundleResources)
        {
            CmsSignedData s;
            var validMetadata = ValidMetaData(bundleResources.Metadata);

            IList certs = bundleResources.LoadCertificates();
            IX509Store x509Certs = X509StoreFactory.Create("Certificate/Collection", new X509CollectionStoreParameters(certs));
            CmsSignedDataGenerator gen = new CmsSignedDataGenerator();
            gen.AddCertificates(x509Certs);
            
            if (!string.IsNullOrEmpty(validMetadata))
            {
                byte[] metadataBytes = Encoding.ASCII.GetBytes(validMetadata);
                CmsProcessable msg = new CmsProcessableByteArray(metadataBytes);
                s = gen.Generate(CmsSignedGenerator.Data, msg, true);
            }
            else{ 
                s = gen.Generate(CmsSignedGenerator.Data, null, false);
            }
            
            var p7BData = s.GetEncoded();
            return p7BData;
        }


        /// <summary>
        /// Create a signed trust bundle.
        /// </summary>
        /// <param name="bundleResources">Provide a <see cref="IResourceProvider"/> to load anchors and storage destination.</param>
        /// <param name="signProvider">Provide a <see cref="ISignProvider"/> to sign the trust bundle</param>
        public byte[] Create(IResourceProvider bundleResources, ISignProvider signProvider)
        {
            byte[] p7Bdata = Create(bundleResources);
            byte[] p7MData = signProvider.Sign(p7Bdata);

            return p7MData;
        }

        /// <summary>
        /// Create a signed trust bundle.
        /// </summary>
        /// <param name="p7Bdata">Cms data structure</param>
        /// <param name="signProvider">Provide a <see cref="ISignProvider"/> to sign the trust bundle</param>
        public byte[] Sign(byte[] p7Bdata, ISignProvider signProvider)
        {
            byte[] p7MData = signProvider.Sign(p7Bdata);
            return p7MData;
        }
        
        
        private string ValidMetaData(string metadata)
        {
            if (string.IsNullOrEmpty(metadata))
            {
                return null;
            }

            XmlSchemaSet schemas = new XmlSchemaSet();
            
            Assembly myAssembly = Assembly.GetExecutingAssembly();
            using (Stream stream = myAssembly.GetManifestResourceStream("Health.Direct.Trust.TrustBundleMetadata.xsd"))
            {
                XmlSchema schema = XmlSchema.Read(stream, null);
                schemas.Add(schema);
            }


            XDocument metaDoc = XDocument.Parse(metadata);
            
            metaDoc.Validate(schemas, (o, e) =>
            {
                throw new XmlSchemaValidationException("Invalid metadata", e.Exception, e.Exception.LineNumber,
                                                       e.Exception.LinePosition);
            });

            return metadata;
        }


    }
}
