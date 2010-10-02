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

package org.nhindirect.transform.util;

/**
 * General constants for XD* related activities.
 * 
 * @author beau
 */
public class XdConstants
{
    public static final String IDENTIFIABLE_TYPE_NS = "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0";

    /*---------------------------------------- */

    public static final String EXTRINSIC_OBJECT_TYPE = "ExtrinsicObject";
    public static final String REGISTRY_PACKAGE_TYPE = "RegistryPackage";
    public static final String CLASSIFICATION_TYPE = "Classification";
    public static final String ASSOCIATION_TYPE_1 = "Association";

    public static final String CODING_SCHEME = "codingScheme";

    public static final String LOINC = "LOINC";

    public static final String CCD_XMLNS = "urn:hl7-org:v3";
    public static final String CCD_EXTENSION = "POCD_HD000040";

    public static final String CREATION_TIME = "creationTime";
    public static final String SOURCE_PATIENT_ID = "sourcePatientId";
    public static final String SOURCE_PATIENT_INFO = "sourcePatientInfo";

    public static final String AUTHOR_PERSON = "authorPerson";
    public static final String AUTHOR_INSTITUTION = "authorInstitution";
    public static final String AUTHOR_ROLE = "authorRole";
    
    public static final String DEFAULT_PRACTICE_SETTING_CODE = "Multidisciplinary";
    public static final String DEFAULT_FACILITY_CODE = "OF";
    public static final String DEFAULT_CLASS_CODE = "History and Physical";
    public static final String DEFAULT_LOINC_CODE = "34133-9";
}
