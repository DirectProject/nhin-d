/* 
 Copyright (c) 2016, Direct Project
 All rights reserved.

 Authors:
    Joe Shook      Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/


using System;
using System.Collections.Generic;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using BCX509 = Org.BouncyCastle.X509;

namespace Health.Direct.Hsm
{
    public class CertificateUtilities
    {
        /// <summary>
        /// BouncyCastle certificate parser
        /// </summary>
        private static readonly BCX509.X509CertificateParser m_x509CertificateParser = new BCX509.X509CertificateParser();

        /// <summary>
        /// Converts raw  System.Security.Cryptography.X509Certificates <see cref="X509Certificate2"/> data to the instance of BouncyCastle X509Certificate class
        /// </summary>
        /// <param name="data">Raw certificate data</param>
        /// <returns>Instance of BouncyCastle X509Certificate class</returns>
        public static BCX509.X509Certificate ToBouncyCastleObject(byte[] data)
        {
            if (data == null)
                throw new ArgumentNullException(nameof(data));

            BCX509.X509Certificate bcCert = m_x509CertificateParser.ReadCertificate(data);

            if (bcCert == null)
                throw new CryptographicException("Cannot find the requested object.");

            return bcCert;
        }


        public static ICollection<BCX509.X509Certificate> BuildBouncyCastleCollection(
            X509Certificate2Collection signingCertificates)
        {
            ICollection<BCX509.X509Certificate> bcCerts = new List<BCX509.X509Certificate>();
            
            foreach (var signingCertificate in signingCertificates)
            {
                bcCerts.Add(ToBouncyCastleObject(signingCertificate.RawData));
            }

            return bcCerts;
        }
    }
}
