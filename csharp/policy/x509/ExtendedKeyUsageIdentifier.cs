/* 
 Copyright (c) 2013, Direct Project
 All rights reserved.

 Authors:
    Joe Shook      jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;

namespace Health.Direct.Policy.X509
{
    /// <summary>
    /// Object identifiers (OIDs) of extended key usages used in the extended key usage certificate extension. 
    ///
    /// From RFC 5280 section 4.2.1.12
    /// This extension indicates one or more purposes for which the certified
    /// public key may be used, in addition to or in place of the basic
    /// purposes indicated in the key usage extension.  In general, this
    /// extension will appear only in end entity certificates.  This
    /// extension is defined as follows:
    /// </summary>
    public static class ExtendedKeyUsageStandard
    {
        /// <summary>
        /// Any use
        /// </summary>
        public static string AnyExtendedKeyUsage = "2.5.29.37.0";
        /// <summary>
        /// TLS WWW server authentication
        /// <para>
        /// Key usage bits that may be consistent: digitalSignature, keyEncipherment or keyAgreement
        /// </para>
        /// </summary>
        public static string IdKpServerAuth = "1.3.6.1.5.5.7.3.1";
        /// <summary>
        /// TLS WWW client authentication
        /// <para>
        /// Key usage bits that may be consistent: digitalSignature and/or keyAgreement
        /// </para>
        /// </summary>
        public static string IdKpClientAuth = "1.3.6.1.5.5.7.3.2";
        /// <summary>
        /// Signing of downloadable executable code
        /// <para>
        /// Key usage bits that may be consistent: digitalSignature
        /// </para>
        /// </summary>
        public static string IdKpCodeSigning = "1.3.6.1.5.5.7.3.3";
        /// <summary>
        /// Email protection
        /// <para>
        /// Key usage bits that may be consistent: digitalSignature, nonRepudiation, and/or (keyEncipherment or keyAgreement)
        /// </para>
        /// </summary>
        public static string IdKpEmailProtection = "1.3.6.1.5.5.7.3.4";
        [Obsolete]
        public static string IdKpIpsecEndSystem = "1.3.6.1.5.5.7.3.5";
        [Obsolete]
        public static string IdKpIpsecTunnel = "1.3.6.1.5.5.7.3.6";
        [Obsolete]
        public static string IdKpIpsecUser = "1.3.6.1.5.5.7.3.7";
        /// <summary>
        /// Binding the hash of an object to a time
        /// <para>
        /// Key usage bits that may be consistent: digitalSignature and/or nonRepudiation
        /// </para>
        /// </summary>
        public static string IdKpTimeStamping = "1.3.6.1.5.5.7.3.8";
        /// <summary>
        /// Signing OCSP responses
        /// <para>
        /// Key usage bits that may be consistent: digitalSignature and/or nonRepudiation
        /// </para>
        /// </summary>
        public static string IdKpOcspSigning = "1.3.6.1.5.5.7.3.9";
        /// <summary>
        /// Data Validation and Certification Server
        /// <para>
        /// From RFC 3029: The data validation and certficate server is a trusted third party (TTP)
        /// </para>
        /// </summary>
        public static string IdKpDvcs = "1.3.6.1.5.5.7.3.10";
        /// <summary>
        /// http://www.imc.org/ietf-pkix/pkix-oid.asn
        /// http://oid-info.com/get/1.3.6.1.5.5.7.3.11
        /// </summary>
        public static string IdKpSbgpCertAaaerverAuth = "1.3.6.1.5.5.7.3.11";
        /// <summary>
        /// http://www.imc.org/ietf-pkix/pkix-oid.asn
        /// http://oid-info.com/get/1.3.6.1.5.5.7.3.12
        /// </summary>
        public static string IdKpScvpResponder = "1.3.6.1.5.5.7.3.12";
        /// <summary>
        /// http://www.imc.org/ietf-pkix/pkix-oid.asn
        /// http://oid-info.com/get/1.3.6.1.5.5.7.3.13
        /// </summary>
        public static string IdKpEapOverPpp = "1.3.6.1.5.5.7.3.13";
        /// <summary>
        /// http://www.imc.org/ietf-pkix/pkix-oid.asn
        /// http://oid-info.com/get/1.3.6.1.5.5.7.3.14
        /// </summary>
        public static string IdKpEapOverLan = "1.3.6.1.5.5.7.3.14";
        /// <summary>
        /// http://www.imc.org/ietf-pkix/pkix-oid.asn
        /// http://oid-info.com/get/1.3.6.1.5.5.7.3.15
        /// </summary>
        public static string IdKpScvpServer = "1.3.6.1.5.5.7.3.15";
        /// <summary>
        /// http://www.imc.org/ietf-pkix/pkix-oid.asn
        /// http://oid-info.com/get/1.3.6.1.5.5.7.3.16
        /// </summary>
        public static string IdKpScvpClient = "1.3.6.1.5.5.7.3.16";
        /// <summary>
        /// http://www.imc.org/ietf-pkix/pkix-oid.asn
        /// http://oid-info.com/get/1.3.6.1.5.5.7.3.17
        /// </summary>
        public static string IdKpIpsecIke = "1.3.6.1.5.5.7.3.17";
        /// <summary>
        /// http://www.imc.org/ietf-pkix/pkix-oid.asn
        /// http://oid-info.com/get/1.3.6.1.5.5.7.3.18
        /// </summary>
        public static string IdKpCapWapAc = "1.3.6.1.5.5.7.3.18";
        /// <summary>
        /// http://www.imc.org/ietf-pkix/pkix-oid.asn
        /// http://oid-info.com/get/1.3.6.1.5.5.7.3.19
        /// </summary>
        public static string IdKpCapWapWpt = "1.3.6.1.5.5.7.3.19";

    }
}
