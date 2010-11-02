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
 * Valid ClassificationType values.
 * 
 * @author beau
 */
public enum ClassificationTypeEnum
{
    DOC_AUTHOR("c101", "urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d", null), 
    DOC_CLASS_CODE("c102", "urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a", "classCode"),
    DOC_CONFIDENTIALITY_CODE("c103", "urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f", "Connect-a-thon confidentialityCodes"),
    DOC_FORMAT_CODE("c104", "urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d", "Connect-a-thon confidentialityCodes"),        
    DOC_HEALTHCARE_FACILITY_TYPE_CODE("c105", "urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1", "Connect-a-thon healthcareFacilityTypeCodes"),         
    DOC_PRACTICE_SETTING_CODE("c106", "urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead", "Connect-a-thon practiceSettingCodes"),    
    DOC_LOINC("c107", "urn:uuid:f0306f51-975f-434e-a61c-c59651d33983", "LOINC"),
    SS_AUTHOR("c108", "urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d", null),
    SS_CONTENT_TYPE_CODE("c109", "urn:uuid:aa543740-bdda-424e-8c96-df4873be8500", "Connect-a-thon contentTypeCodes"),
    SS("cl10", "urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd", null);

    private String classificationId;
    private String classificationScheme;
    private String codingScheme;

    private ClassificationTypeEnum(String classificationId, String classificationScheme, String codingScheme)
    {
        this.classificationId = classificationId;
        this.classificationScheme = classificationScheme;
        this.codingScheme = codingScheme;
    }
    
    /**
     * Check to see if the given classificationScheme matches the current
     * object.
     * 
     * @param classificationScheme
     *            the classificationScheme to check.
     * @return true if the values match, false otherwise.
     */
    public boolean matchesScheme(String classificationScheme)
    {
        if (StringUtils.equals(this.classificationScheme, classificationScheme))
            return true;

        return false;
    }

    /**
     * Get the value of classificationId.
     * 
     * @return the value of classificationId.
     */
    public String getClassificationId()
    {
        return this.classificationId;
    }

    /**
     * Get the value of classificationScheme.
     * 
     * @return the value of classificationScheme.
     */
    public String getClassificationScheme()
    {
        return this.classificationScheme;
    }

    /**
     * Get the value of codingScheme.
     * 
     * @return the value of codingScheme.
     */
    public String getCodingScheme()
    {
        return this.codingScheme;
    }
}
