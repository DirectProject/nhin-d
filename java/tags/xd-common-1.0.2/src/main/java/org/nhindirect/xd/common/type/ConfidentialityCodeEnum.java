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
 * Enumeration representing valid confidentiality codes.
 * 
 * @author beau
 */
public enum ConfidentialityCodeEnum
{
    BUSINESS("B", "Business"),
    CLINICIAN("D", "Clinician"),
    INDIVIDUAL("I", "Individual"),
    LOW("L", "LOW"),
    NORMAL("N", "Normal"),
    RESTRICTED("R", "Restricted"),
    VERY_RESTRICTED("V", "Very Restricted"),
    SUBSTANCE_ABUSE_RELATED("ETH", "Substance Abuse Related"),
    HIV_RELATED("HIV", "HIV related"),
    PSYCHIATRY_RELATED("PSY", "Psychiatry related"),
    SEXUAL_AND_DOMESTIC_VIOLENCE_RELATED("SDV", "Sexual and Domestic Violence related"),
    CELEBRITY("C", "Celebrity"),
    SENSITIVE("S", "Sensitive");

    private String conceptCode;
    private String conceptName;

    private ConfidentialityCodeEnum(String conceptCode, String conceptName)
    {
        this.conceptCode = conceptCode;
        this.conceptName = conceptName;
    }

    /**
     * @return the conceptCode
     */
    public String getConceptCode()
    {
        return conceptCode;
    }

    /**
     * @return the conceptName
     */
    public String getConceptName()
    {
        return conceptName;
    }
}
