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

import org.apache.commons.lang.StringUtils;

/**
 * Enumeration for data identifying an External Identifier element.
 * 
 * @author beau
 */
public enum ExternalIdentifierTypeEnum
{
    DOC_PATIENT_ID("ei01", "urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427", "XDSDocumentEntry.patientId"), 
    DOC_UNIQUE_ID("ei02", "urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab", "XDSDocumentEntry.uniqueId"),
    SS_UNIQUE_ID("ei01", "urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8", "XDSSubmissionSet.uniqueId"),
    SS_SOURCE_ID("ei02", "urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832", "XDSSubmissionSet.sourceId"),
    SS_PATIENT_ID("ei03", "urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446", "XDSSubmissionSet.patientId");

    private String identificationId;
    private String identificationScheme;
    private String localizedString;

    private ExternalIdentifierTypeEnum(String identificationId, String identificationScheme, String localizedString)
    {
        this.identificationId = identificationId;
        this.identificationScheme = identificationScheme;
        this.localizedString = localizedString;
    }

    /**
     * Check to see if the given identificationScheme matches the current
     * object.
     * 
     * @param identificationScheme
     *            the identificationScheme to check.
     * @return true if the values match, false otherwise.
     */
    public boolean matchesScheme(String identificationScheme)
    {
        if (StringUtils.equals(this.identificationScheme, identificationScheme))
            return true;

        return false;
    }
    
    /**
     * Get the value of identificationId.
     * 
     * @return the value of identificationId.
     */
    public String getIdentificationId()
    {
        return this.identificationId;
    }

    /**
     * Get the value of identificationScheme.
     * 
     * @return the value of identificationScheme.
     */
    public String getIdentificationScheme()
    {
        return this.identificationScheme;
    }
    
    public String getLocalizedString()
    {
        return this.localizedString;
    }
}