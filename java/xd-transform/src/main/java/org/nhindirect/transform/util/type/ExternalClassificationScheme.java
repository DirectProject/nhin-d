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

package org.nhindirect.transform.util.type;

/**
 * Enumeration for data identifying an External Classification Scheme element.
 * 
 * @author beau
 */
public enum ExternalClassificationScheme
{
    DOCUMENT_ENTRY_AUTHOR("c101", "urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d"), 
    DOCUMENT_ENTRY_CLASS_CODE("c102", "uuid:41a5887f-8865-4c09-adf7-e362475b143a"),
    DOCUMENT_ENTRY_FORMAT_CODE("c104", "urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d"),        
    DOCUMENT_ENTRY_FACILITY_CODE("c105", "urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1"),         
    DOCUMENT_ENTRY_PRACTICE_SETTING_CODE("c106", "urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead"),    
    DOCUMENT_ENTRY_TYPE_CODE("c107", "urn:uuid:f0306f51-975f-434e-a61c-c59651d33983"),
    SUBMISSION_SET_AUTHOR("c101", "urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d"),
    SUBMISSION_SET_CONTENT_TYPE_CODE("c102", "urn:uuid:aa543740-bdda-424e-8c96-df4873be8500");

    private String classificationId;
    private String classificationScheme;

    private ExternalClassificationScheme(String classificationId, String classificationScheme)
    {
        this.classificationId = classificationId;
        this.classificationScheme = classificationScheme;
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
}
