/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;

namespace NHINDirect.Certificates
{
	/// <summary>
	/// Supports resolution of certificates by address.
	/// </summary>
    public class CertificateResolver : ICertificateResolver
    {
        IX509CertificateIndex m_certIndex;
        
	/// <summary>
	/// Creates a certificate resolver from a certificate index instance. 
	/// </summary>
	/// <param name="index">
	/// An index instance providing <see cref="IX509CertificateIndex"/>
	/// </param>
        public CertificateResolver(IX509CertificateIndex index)
        {
            if (index == null)
            {
                throw new ArgumentNullException();
            }
        
            m_certIndex = index;
        }
        
		/// <summary>
		/// Gets a collection of certificates by mail address. 
		/// </summary>
		/// <param name="address">
		/// The <see cref="MailAddress"/> for which to retrieve certificates.
		/// </param>
		/// <returns>
		/// The <see cref="X509Certificate2Collection"/> of certificates for the requested address.
		/// </returns>
        public X509Certificate2Collection GetCertificates(MailAddress address)
        {
            if (address == null)
            {
                throw new ArgumentNullException();
            }
            
            X509Certificate2Collection addressCerts = m_certIndex[address.Address];
            X509Certificate2Collection hostCerts = m_certIndex[address.Host];
            if (addressCerts == null && hostCerts == null)
            {
                return null;
            }

            X509Certificate2Collection matches = new X509Certificate2Collection();
            if (addressCerts != null)
            {
                matches.Add(addressCerts);
            }
            if (hostCerts != null)
            {
                matches.Add(hostCerts);
            }

            return matches;            
        }
        
    }
}
