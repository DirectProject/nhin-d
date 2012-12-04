/* 
 * Copyright (c) 2010, NHIN Direct Project
 * All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in the 
 *    documentation and/or other materials provided with the distribution.  
 * 3. Neither the name of the the NHIN Direct Project (nhindirect.org)
 *    nor the names of its contributors may be used to endorse or promote products 
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY 
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.nhindirect.xd.common.type;

/**
 * Valid FormatCode values.
 * 
 * @author beau
 */
public enum FormatCodeEnum
{
    HL7_CCD_DOCUMENT("urn:ihe:pcc:xphr:2007", "HL7 CCD Document"),
    IHE_ANTEPARTUM_SUMMARY("urn:ihe:pcc:aps:2007", "IHE Antepartum Summary"),
    PDF_EMBEDDED_IN_CDA_PER_XDS_SD_PROFILE("urn:ihe:iti:xds-sd:pdf:2008", "PDF embedded in CDA per XDS-SD profile"),
    TEXT_EMBEDDED_IN_CDA_PER_XDS_SD_PROFILE("urn:ihe:iti:xds-sd:text:2008", "Text embedded in CDA per XDS-SD profile"),
    XDS_MEDICAL_SUMMARIES("urn:ihe:pcc:xds-ms:2007", "XDS Medical Summaries"),
    PERSONAL_HEALTH_RECORDS("urn:ihe:pcc:xphr:2007", "Personal Health Records"),
    EMERGENCY_DEPARTMENT_REFERRAL_EDR("urn:ihe:pcc:edr:2007", "Emergency Department Referral (EDR)"),
    EMERGENCY_DEPARTMENT_ENCOUNTER_SUMMARY_EDES("urn:ihe:pcc:edes:2007", "Emergency Department Encounter Summary (EDES)"),
    ANTEPARTUM_RECORD_APR_HISTORY_AND_PHYSICAL("urn:ihe:pcc:apr:handp:2008", "Antepartum Record (APR) - History and Physical"),
    ANTEPARTUM_RECORD_APR_LABORATORY("urn:ihe:pcc:apr:lab:2008", "Antepartum Record (APR) - Laboratory"),
    ANTEPARTUM_RECORD_APR_EDUCATION("urn:ihe:pcc:apr:edu:2008", "Antepartum Record (APR) - Education"),
    IMMUNIZATION_REGISTRY_CONTENT_CRC("urn:ihe:pcc:irc:2008", "Immunization Registry Content (IRC)"),
    CANCER_REGISTRY_CONTENT_CRC("urn:ihe:pcc:crc:2008", "Cancer Registry Content (CRC)"),
    CARE_MANAGEMENT_CM("urn:ihe:pcc:cm:2008", "Care Management (CM)"),
    BASIC_PATIENT_PRIVACY_CONSENTS("urn:ihe:iti:bppc:2007", "Basic Patient Privacy Consents"),
    BASIC_PATIENT_PRIVACY_CONSENTS_WITH_SCANNED_DOCUMENT("urn:ihe:iti:bppc-sd:2007", "Basic Patient Privacy Consents with Scanned Document"),
    CDA_LABORATORY_REPORT("urn:ihe:lab:xd-lab:2008", "CDA Laboratory Report"),
    ACCESS_CONSENT_POLICY_DOCUMENT_FORMATTED_AS_XACML("urn:nhin:names:acp:XACML", "Access Consent Policy document formatted as XACML");
    
    private String conceptCode;
    private String conceptName;

    private FormatCodeEnum(String conceptCode, String conceptName)
    {
        this.conceptCode = conceptCode;
        this.conceptName = conceptName;
    }

    /**
     * Return the value of conceptCode.
     * 
     * @return the conceptCode.
     */
    public String getConceptCode()
    {
        return conceptCode;
    }

    /**
     * Return the value of conceptName.
     * 
     * @return the conceptName.
     */
    public String getConceptName()
    {
        return conceptName;
    }
}
