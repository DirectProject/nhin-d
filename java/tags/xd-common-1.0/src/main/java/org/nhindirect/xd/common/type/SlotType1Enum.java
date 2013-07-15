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
 * Valid SlotType1 values.
 * 
 * @author beau
 */
public enum SlotType1Enum
{
    CREATION_TIME("creationTime"),
    LANGUAGE_CODE("languageCode"),
    SERVICE_START_TIME("serviceStartTime"),
    SERVICE_STOP_TIME("serviceStopTime"),
    SOURCE_PATIENT_ID("sourcePatientId"),
    SOURCE_PATIENT_INFO("sourcePatientInfo"),
    AUTHOR_PERSON("authorPerson"),
    AUTHOR_INSTITUTION("authorInstitution"),
    AUTHOR_ROLE("authorRole"),
    AUTHOR_SPECIALTY("authorSpecialty"),
    AUTHOR_TELECOMMUNICATION("authorTelecommunication"),
    CODING_SCHEME("codingScheme"),
    SUBMISSION_TIME("submissionTime"),
    INTENDED_RECIPIENT("intendedRecipient"),
    SUBMISSION_SET_STATUS("SubmissionSetStatus"),
    HASH("hash"),
    SIZE("size");

    private String name;

    private SlotType1Enum(String name)
    {
        this.name = name;
    }
    
    /**
     * Check to see if a given string matches the current object name.
     * 
     * @param name
     *            The string to compare.
     * @return true if the values match, false otherwise.
     */
    public boolean matches(String name)
    {
        if (StringUtils.equals(this.name, name))
            return true;

        return false;
    }

    /**
     * Get the value of name.
     * 
     * @return the value of name.
     */
    public String getName()
    {
        return this.name;
    }
}
