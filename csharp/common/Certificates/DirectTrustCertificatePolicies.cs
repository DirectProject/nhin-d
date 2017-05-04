/* 
 Copyright (c) 2017, Direct Project
 All rights reserved.

 Authors:
    Dávid Koronthály    koronthaly@hotmail.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

namespace Health.Direct.Common.Certificates
{
    /// <summary>
    /// Certificate policy OIDs, as defined by DirectTrust Community X.509 Certificate Policy version 1.2:
    /// http://wiki.directproject.org/file/view/DirectTrust+CP+V1-1.2.pdf/409137794/DirectTrust%20CP%20V1-1.2.pdf
    /// </summary>
    public static class DirectTrustCertificatePolicies
    {
        /// <summary>
        /// DirectTrust Community X.509 Certificate Policy
        /// See DirectTrust CP section 1.2 Document Name and Identification.
        /// </summary>
        public const string DTorgCPVersions = "1.3.6.1.4.1.41179.0.1";

        /// <summary>
        /// DirectTrust Community X.509 Certificate Policy version 1.2
        /// See DirectTrust CP section 1.2 Document Name and Identification.
        /// </summary>
        public const string DTorgCPVersion2 = "1.3.6.1.4.1.41179.0.1.2";

        /// <summary>
        /// DirectTrust Community X.509 Certificate Policy version 1.3
        /// See DirectTrust CP section 1.2 Document Name and Identification.
        /// </summary>
        public const string DTorgCPVersion3 = "1.3.6.1.4.1.41179.0.1.3";

        /// <summary>
        /// Levels of assurance: applicant's control over an email address.
        /// Equivalent to NIST 800-63-1 Level 1 or Kantara Level 1 or FBCA Rudimentary
        /// See DirectTrust CP section 3.2.3.1 Authentication of Human Subscribers.
        /// </summary>
        public const string DTorgLoA1 = "1.3.6.1.4.1.41179.1.1";

        /// <summary>
        /// Levels of assurance: applicant supplies his or her full legal name, an address of record, and date of birth.
        /// Equivalent to NIST 800-63-1 Level 2 or Kantara Level 2 or FBCA Basic.
        /// See DirectTrust CP section 3.2.3.1 Authentication of Human Subscribers.
        /// </summary>
        public const string DTorgLoA2 = "1.3.6.1.4.1.41179.1.2";

        /// <summary>
        /// Levels of assurance: applicant supplies his or her full legal name, an address of record, and date of birth.
        /// Equivalent to NIST 800-63-1 Level 3 or Kantara Level 3 or FBCA Basic or Medium.
        /// See DirectTrust CP section 3.2.3.1 Authentication of Human Subscribers.
        /// </summary>
        public const string DTorgLoA3 = "1.3.6.1.4.1.41179.1.3";

        /// <summary>
        /// Levels of assurance: applicant supplies his or her full legal name, an address of record, and date of birth.
        /// Equivalent to NIST 800-63-1 Level 4 or Kantara Level 4 or FBCA Medium.
        /// See DirectTrust CP section 3.2.3.1 Authentication of Human Subscribers.
        /// </summary>
        public const string DTorgLoA4 = "1.3.6.1.4.1.41179.1.4";

        /// <summary>
        /// Category: Covered Entity as defined in HIPAA.
        /// See DirectTrust CP section 3.2.2 Authentication of Organization Identity.
        /// </summary>
        public const string DTorgCE = "1.3.6.1.4.1.41179.2.1";

        /// <summary>
        /// Category: Business Associate (BA), as defined in HIPAA.
        /// See DirectTrust CP section 3.2.2 Authentication of Organization Identity.
        /// </summary>
        public const string DTorgBA = "1.3.6.1.4.1.41179.2.2";

        /// <summary>
        /// Category: non-HIPAA Healthcare Entity (HE).
        /// See DirectTrust CP section 3.2.2 Authentication of Organization Identity.
        /// </summary>
        public const string DTorgHE = "1.3.6.1.4.1.41179.2.3";

        /// <summary>
        /// Category: personal healthcare Direct message exchange.
        /// See DirectTrust CP section 3.2.2 Authentication of Organization Identity.
        /// </summary>
        public const string DTorgPatient = "1.3.6.1.4.1.41179.2.4";
    }
}
