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

package org.nhindirect.xd.common;

import java.util.Arrays;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.xd.common.type.ClassCodeEnum;
import org.nhindirect.xd.common.type.FormatCodeEnum;
import org.nhindirect.xd.common.type.HealthcareFacilityTypeCodeEnum;
import org.nhindirect.xd.common.type.LoincEnum;
import org.nhindirect.xd.common.type.PracticeSettingCodeEnum;
import org.nhindirect.xd.transform.pojo.SimplePerson;
import org.nhindirect.xd.transform.util.type.MimeType;

/**
 * Test class for the DirectDocuments class.
 * 
 * @author beau
 */
public class DirectDocumentsTest extends TestCase
{
    @SuppressWarnings("unused")
    private static final Log LOGGER = LogFactory.getFactory().getInstance(DirectDocumentsTest.class);

    /**
     * Default constructor.
     * 
     * @param testName
     *            The test name.
     */
    public DirectDocumentsTest(String testName)
    {
        super(testName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * TODO: individual unit tests
     */
    public void testDirectDocuments()
    {
        // Create a collection of documents
        DirectDocuments documents = new DirectDocuments();
        
        documents.getSubmissionSet().setId("1");
        documents.getSubmissionSet().setName("2");
        documents.getSubmissionSet().setDescription("3");
        documents.getSubmissionSet().setSubmissionTime(new Date());
        documents.getSubmissionSet().setIntendedRecipient(Arrays.asList("5.1", "5.2"));
        documents.getSubmissionSet().setAuthorPerson("6");
        documents.getSubmissionSet().setAuthorInstitution(Arrays.asList("7.1", "7.2"));
        documents.getSubmissionSet().setAuthorRole("8");
        documents.getSubmissionSet().setAuthorSpecialty("9");
        documents.getSubmissionSet().setAuthorTelecommunication("10");
        documents.getSubmissionSet().setContentTypeCode("11");
        documents.getSubmissionSet().setContentTypeCode_localized("12");
        documents.getSubmissionSet().setUniqueId("13");
        documents.getSubmissionSet().setSourceId("14");
        documents.getSubmissionSet().setPatientId("xxx");
        
        DirectDocument2 doc1 = new DirectDocument2();
        doc1.setData(new String("this is some data for document 1").getBytes());
        
        DirectDocument2.Metadata metadata1 = doc1.getMetadata();
        metadata1.setMimeType(MimeType.TEXT_PLAIN.getType());
        metadata1.setId("1.2");
        metadata1.setDescription("1.3");
        metadata1.setCreationTime(new Date());
        metadata1.setLanguageCode("1.5");
        metadata1.setServiceStartTime(new Date());
        metadata1.setServiceStopTime(new Date());
        metadata1.setSourcePatient(new SimplePerson("1.Bob", "1.Smith"));
        metadata1.setAuthorPerson("1.10");
        metadata1.setAuthorInstitution(Arrays.asList("1.11.1", "1.11.2"));
        metadata1.setAuthorRole("1.12");
        metadata1.setAuthorSpecialty("1.13");
        metadata1.setClassCode(ClassCodeEnum.HISTORY_AND_PHYSICAL.getValue());
        metadata1.setClassCode_localized(ClassCodeEnum.HISTORY_AND_PHYSICAL.getValue());
        metadata1.setConfidentialityCode("1.16");
        metadata1.setConfidentialityCode_localized("1.17");
        metadata1.setFormatCode(FormatCodeEnum.CANCER_REGISTRY_CONTENT_CRC);
        metadata1.setHealthcareFacilityTypeCode(HealthcareFacilityTypeCodeEnum.OF.getValue());
        metadata1.setHealthcareFacilityTypeCode_localized(HealthcareFacilityTypeCodeEnum.OF.getValue());
        metadata1.setPracticeSettingCode(PracticeSettingCodeEnum.MULTIDISCIPLINARY.getValue());
        metadata1.setPracticeSettingCode_localized(PracticeSettingCodeEnum.MULTIDISCIPLINARY.getValue());
        metadata1.setLoinc(LoincEnum.LOINC_34133_9.getValue());
        metadata1.setLoinc_localized(LoincEnum.LOINC_34133_9.getValue());
        metadata1.setPatientId("xxx");
        metadata1.setUniqueId("1.27");
        
        DirectDocument2 doc2 = new DirectDocument2();
        doc2.setData(new String("and this is some data for document 2").getBytes());
        
        DirectDocument2.Metadata metadata2 = doc2.getMetadata();
        metadata1.setMimeType(MimeType.TEXT_XML.getType());
        metadata2.setId("2.2");
        metadata2.setDescription("2.3");
        metadata2.setCreationTime(new Date());
        metadata2.setLanguageCode("2.5");
        metadata2.setServiceStartTime(new Date());
        metadata2.setServiceStopTime(new Date());
        metadata2.setSourcePatient(new SimplePerson("2.Bob", "2.Smith"));
        metadata2.setAuthorPerson("2.10");
        metadata2.setAuthorInstitution(Arrays.asList("2.11.1", "2.11.2"));
        metadata2.setAuthorRole("2.12");
        metadata2.setAuthorSpecialty("2.13");
        metadata2.setClassCode(ClassCodeEnum.HISTORY_AND_PHYSICAL.getValue());
        metadata2.setClassCode_localized(ClassCodeEnum.HISTORY_AND_PHYSICAL.getValue());
        metadata2.setConfidentialityCode("2.16");
        metadata2.setConfidentialityCode_localized("2.17");
        metadata2.setFormatCode(FormatCodeEnum.HL7_CCD_DOCUMENT);
        metadata2.setHealthcareFacilityTypeCode(HealthcareFacilityTypeCodeEnum.OF.getValue());
        metadata2.setHealthcareFacilityTypeCode_localized(HealthcareFacilityTypeCodeEnum.OF.getValue());
        metadata2.setPracticeSettingCode(PracticeSettingCodeEnum.MULTIDISCIPLINARY.getValue());
        metadata2.setPracticeSettingCode_localized(PracticeSettingCodeEnum.MULTIDISCIPLINARY.getValue());
        metadata2.setLoinc(LoincEnum.LOINC_34133_9.getValue());
        metadata2.setLoinc_localized(LoincEnum.LOINC_34133_9.getValue());
        metadata2.setPatientId("xxx");
        metadata2.setUniqueId("2.27");
        
        documents.getDocuments().add(doc1);
        documents.getDocuments().add(doc2);
        
        System.out.println(documents.getSubmitObjectsRequestAsString());
    }

}
