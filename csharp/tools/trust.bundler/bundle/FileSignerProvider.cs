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
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Security;
using System.Security.Cryptography.X509Certificates;
using Org.BouncyCastle.Cms;
using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Security;
using Org.BouncyCastle.X509.Store;
using X509Certificate = Org.BouncyCastle.X509.X509Certificate;

namespace Health.Direct.Trust
{
    /// <summary>
    /// File system implementation of ISignProvider 
    /// </summary>
    public class FileSignerProvider : ISignProvider
    {
        private readonly string _signature;
        private readonly SecureString _key;

        /// <summary>
        /// Create a new FileSignerProvider
        /// </summary>
        /// <param name="signature">Signature/s file or folder location</param>
        /// <param name="key">Optional secure key to signature.</param>
        public FileSignerProvider(string signature, SecureString key = null)
        {
            _signature = signature;
            _key = key;
        }

        public string Signature
        {
            get { return _signature; }
        }

        public SecureString Key
        {
            get { return _key; }
        }

        public byte[] Sign(byte[] cmsData)
        {
            IList certs = new List<X509Certificate>();

            byte[] signBytes = File.ReadAllBytes(GetFile());
            X509Certificate2 signCert = new X509Certificate2(signBytes, Key, X509KeyStorageFlags.MachineKeySet | X509KeyStorageFlags.Exportable);
            certs.Add(DotNetUtilities.FromX509Certificate(signCert));
            IX509Store x509Certs = X509StoreFactory.Create("Certificate/Collection", new X509CollectionStoreParameters(certs));

            CmsSignedDataGenerator gen = new CmsSignedDataGenerator();
            AsymmetricCipherKeyPair pair = DotNetUtilities.GetKeyPair(signCert.PrivateKey);
            X509Certificate bX509Certificate = DotNetUtilities.FromX509Certificate(signCert);
            gen.AddSigner(pair.Private, bX509Certificate, CmsSignedGenerator.DigestSha1);
            gen.AddSigner(pair.Private, bX509Certificate, CmsSignedGenerator.DigestSha256);
            CmsSignedData unsignedData = new CmsSignedData(cmsData);

            
            gen.AddCertificates(x509Certs);
            CmsProcessable msg = new CmsProcessableByteArray(unsignedData.GetEncoded());
            CmsSignedData cmsSignedData = gen.Generate(CmsSignedGenerator.Data, msg, true);
            
            byte[] p7MData = cmsSignedData.GetEncoded();
            return p7MData;
        }

        
        private string GetFile()
        {
            if (File.Exists(Signature))
            {
                return Signature;
            }
            return Directory.GetFiles(Signature).First();
        }
    }
}