/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Arien Malec     arien.malec@nhindirect.org
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System.Collections.Generic;

namespace Health.Direct.Common.Metadata
{
    /// <summary>
    /// C80 format codes
    /// </summary>
    public enum C80FormatCode
    {
        /// <summary>
        /// C80 code for Emergency Department Encounter Summary (EDES) (urn:ihe:pcc:edes:2007)
        /// </summary>
        EmergencyDepartmentEncounterSummary,
        /// <summary>
        /// C80 code for Text embedded in CDA per XDS-SD profile (urn:ihe:iti:xds-sd:text:2008)
        /// </summary>
        TextEmbeddedInCDA,
        /// <summary>
        /// C80 code for XDS Medical Summaries (urn:ihe:pcc:xds-ms:2007)
        /// </summary>
        XDSMedicalSummaries,
        /// <summary>
        /// C80 code for Immunization Registry Content (IRC) (urn:ihe:pcc: irc:2008)
        /// </summary>
        ImmunizationRegistryContent,
        /// <summary>
        /// C80 code for PDF embedded in CDA per XDS-SD profile (urn:ihe:iti:xds-sd:pdf:2008)
        /// </summary>
        PDFEmbeddedInCDA,
        /// <summary>
        /// C80 code for Personal Health Records (urn:ihe:pcc:xphr:2007)
        /// </summary>
        PersonalHealthRecords,
        /// <summary>
        /// C80 code for Care Management (CM) (urn:ihe:pcc:cm:2008)
        /// </summary>
        CareManagement,
        /// <summary>
        /// C80 code for IHE Antepartum Summary (urn:ihe:pcc:aps:2007)
        /// </summary>
        IHEAntepartumSummary,
        /// <summary>
        /// C80 code for Basic Patient Privacy Consents with Scanned Document (urn:ihe:iti:bppc-sd:2007)
        /// </summary>
        BasicPatientPrivacyConsentsWithScannedDocument,
        /// <summary>
        /// C80 code for Antepartum Record (APR) - Education (urn:ihe:pcc:apr:edu:2008)
        /// </summary>
        AntepartumRecordEducation,
        /// <summary>
        /// C80 code for Antepartum Record (APR) - Laboratory (urn:ihe:pcc:apr:lab:2008)
        /// </summary>
        AntepartumRecordLaboratory,
        /// <summary>
        /// C80 code for Emergency Department Referral (EDR) (urn:ihe:pcc:edr:2007)
        /// </summary>
        EmergencyDepartmentReferral,
        /// <summary>
        /// C80 code for Access Consent Policy document formatted as XACML (urn:nhin:names:acp:XACML)
        /// </summary>
        AccessConsentPolicyDocumentXACML,
        /// <summary>
        /// C80 code for Basic Patient Privacy Consents (urn:ihe:iti:bppc:2007)
        /// </summary>
        BasicPatientPrivacyConsents,
        /// <summary>
        /// C80 code for Antepartum Record (APR) - History and Physical (urn:ihe:pcc:apr:handp:2008)
        /// </summary>
        AntepartumRecordHistoryAndPhysical,
        /// <summary>
        /// C80 code for CDA Laboratory Report (urn:ihe:lab:xd-lab:2008)
        /// </summary>
        CDALaboratoryReport,
        /// <summary>
        /// C80 code for Cancer Registry Content (CRC) (urn:ihe:pcc:crc:2008)
        /// </summary>
        CancerRegistryContent
    }

    /// <summary>
    /// Represents a coded document format code
    /// </summary>
    public static class C80FormatCodeUtils
    {

        /// <summary>
        /// Returns a <see cref="CodedValue"/> for the code
        /// </summary>
        public static CodedValue ToCodedValue(this C80FormatCode code)
        {
            KeyValuePair<string, string> pair = Decode(code);
            return new CodedValue(pair.Key, pair.Value, "2.16.840.1.113883.3.88.12.80.73");
        }

        private static Dictionary<C80FormatCode, KeyValuePair<string, string>> m_C80FormatCode_mappings
            = new Dictionary<C80FormatCode, KeyValuePair<string, string>>()
                  {
                      {
                          C80FormatCode.EmergencyDepartmentEncounterSummary,
                          new KeyValuePair<string, string>("urn:ihe:pcc:edes:2007",
                                                           "Emergency Department Encounter Summary (EDES)")
                          },
                      {
                          C80FormatCode.TextEmbeddedInCDA,
                          new KeyValuePair<string, string>("urn:ihe:iti:xds-sd:text:2008",
                                                           "Text embedded in CDA per XDS-SD profile")
                          },
                      {
                          C80FormatCode.XDSMedicalSummaries,
                          new KeyValuePair<string, string>("urn:ihe:pcc:xds-ms:2007", "XDS Medical Summaries")
                          },
                      {
                          C80FormatCode.ImmunizationRegistryContent,
                          new KeyValuePair<string, string>("urn:ihe:pcc: irc:2008",
                                                           "Immunization Registry Content (IRC)")
                          },
                      {
                          C80FormatCode.PDFEmbeddedInCDA,
                          new KeyValuePair<string, string>("urn:ihe:iti:xds-sd:pdf:2008",
                                                           "PDF embedded in CDA per XDS-SD profile")
                          },
                      {
                          C80FormatCode.PersonalHealthRecords,
                          new KeyValuePair<string, string>("urn:ihe:pcc:xphr:2007", "Personal Health Records")
                          },
                      {
                          C80FormatCode.CareManagement,
                          new KeyValuePair<string, string>("urn:ihe:pcc:cm:2008", "Care Management (CM)")
                          },
                      {
                          C80FormatCode.IHEAntepartumSummary,
                          new KeyValuePair<string, string>("urn:ihe:pcc:aps:2007", "IHE Antepartum Summary")
                          },
                      {
                          C80FormatCode.BasicPatientPrivacyConsentsWithScannedDocument,
                          new KeyValuePair<string, string>("urn:ihe:iti:bppc-sd:2007",
                                                           "Basic Patient Privacy Consents with Scanned Document")
                          },
                      {
                          C80FormatCode.AntepartumRecordEducation,
                          new KeyValuePair<string, string>("urn:ihe:pcc:apr:edu:2008",
                                                           "Antepartum Record (APR) - Education")
                          },
                      {
                          C80FormatCode.AntepartumRecordLaboratory,
                          new KeyValuePair<string, string>("urn:ihe:pcc:apr:lab:2008",
                                                           "Antepartum Record (APR) - Laboratory")
                          },
                      {
                          C80FormatCode.EmergencyDepartmentReferral,
                          new KeyValuePair<string, string>("urn:ihe:pcc:edr:2007", "Emergency Department Referral (EDR)")
                          },
                      {
                          C80FormatCode.AccessConsentPolicyDocumentXACML,
                          new KeyValuePair<string, string>("urn:nhin:names:acp:XACML",
                                                           "Access Consent Policy document formatted as XACML")
                          },
                      {
                          C80FormatCode.BasicPatientPrivacyConsents,
                          new KeyValuePair<string, string>("urn:ihe:iti:bppc:2007", "Basic Patient Privacy Consents")
                          },
                      {
                          C80FormatCode.AntepartumRecordHistoryAndPhysical,
                          new KeyValuePair<string, string>("urn:ihe:pcc:apr:handp:2008",
                                                           "Antepartum Record (APR) - History and Physical")
                          },
                      {
                          C80FormatCode.CDALaboratoryReport,
                          new KeyValuePair<string, string>("urn:ihe:lab:xd-lab:2008", "CDA Laboratory Report")
                          },
                      {
                          C80FormatCode.CancerRegistryContent,
                          new KeyValuePair<string, string>("urn:ihe:pcc:crc:2008", "Cancer Registry Content (CRC)")
                          }
                  };

        /// <summary>
        /// Returns the code/label pair for the provided enumeration code
        /// </summary>
        public static KeyValuePair<string, string> Decode(C80FormatCode code)
        {
            return m_C80FormatCode_mappings[code];
        }
    }
}