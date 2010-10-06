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

package org.nhindirect.transform.document;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO: Write tests..
 * 
 * @author beau
 */
public class DirectDocumentTest extends TestCase
{
    private static final Log LOGGER = LogFactory.getFactory().getInstance(DirectDocumentTest.class);

    /**
     * Default constructor.
     * 
     * @param testName
     *            The test name.
     */
    public DirectDocumentTest(String testName)
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
     * 
     */
    public void testDirectDocumentMetadata()
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        displayMetadata(metadata);

        metadata.setMimeType("1");
        metadata.set_eot_id("2");
        metadata.set_eot_description("3");
        metadata.setCreationTime("4");
        metadata.setLanguageCode("5");
        metadata.setServiceStartTime("6");
        metadata.setServiceStopTime("7");
        metadata.setSourcePatientId("8");
        metadata.setSourcePatientInfo("9");
        metadata.setAuthorPerson("10");
        metadata.setAuthorInstitution("11");
        metadata.setAuthorRole("12");
        metadata.setAuthorSpecialty("13");
        metadata.setClassCode("14");
        metadata.setClassCode_localized("15");
        metadata.setConfidentialityCode("16");
        metadata.setConfidentialityCode_localized("17");
        metadata.setFormatCode("18");
        metadata.setFormatCode_localized("19");
        metadata.setHealthcareFacilityTypeCode("20");
        metadata.setHealthcareFacilityTypeCode_localized("21");
        metadata.setPracticeSettingCode("22");
        metadata.setPracticeSettingCode_localized("23");
        metadata.setLoinc("24");
        metadata.setLoinc_localized("25");
        metadata.setPatientId("26");
        metadata.setUniqueId("27");
        metadata.set_rpt_id("28");
        metadata.set_rpt_name("29");
        metadata.set_rpt_description("30");
        metadata.setSs_submissionTime("31");
        metadata.setSs_intendedRecipient("32");
        metadata.setSs_authorPerson("33");
        metadata.setSs_authorInstitution("34");
        metadata.setSs_authorRole("35");
        metadata.setSs_authorSpecialty("36");
        metadata.setContentTypeCode("37");
        metadata.setContentTypeCode_localized("38");
        metadata.setSs_uniqueId("39");
        metadata.setSs_sourceId("40");
        metadata.setSs_patientId("41");
        metadata.setSubmissionSetStatus("42");

        displayMetadata(metadata);
    }

    private void displayMetadata(DirectDocument.Metadata metadata)
    {
        LOGGER.info("mimeType                               " + metadata.getMimeType());
        LOGGER.info("_eot_id                                " + metadata.get_eot_id());
        LOGGER.info("_eot_description                       " + metadata.get_eot_description());
        LOGGER.info("creationTime                           " + metadata.getCreationTime());
        LOGGER.info("languageCode                           " + metadata.getLanguageCode());
        LOGGER.info("serviceStartTime                       " + metadata.getServiceStartTime());
        LOGGER.info("serviceStopTime                        " + metadata.getServiceStopTime());
        LOGGER.info("sourcePatientId                        " + metadata.getSourcePatientId());
        LOGGER.info("sourcePatientInfo                      " + metadata.getSourcePatientInfo());
        LOGGER.info("authorPerson                           " + metadata.getAuthorPerson());
        LOGGER.info("authorInstitution                      " + metadata.getAuthorInstitution());
        LOGGER.info("authorRole                             " + metadata.getAuthorRole());
        LOGGER.info("authorSpecialty                        " + metadata.getAuthorSpecialty());
        LOGGER.info("classCode                              " + metadata.getClassCode());
        LOGGER.info("classCode_localized                    " + metadata.getClassCode_localized());
        LOGGER.info("confidentialityCode                    " + metadata.getConfidentialityCode());
        LOGGER.info("confidentialityCode_localized          " + metadata.getConfidentialityCode_localized());
        LOGGER.info("formatCode                             " + metadata.getFormatCode());
        LOGGER.info("formatCode_localized                   " + metadata.getFormatCode_localized());
        LOGGER.info("healthcareFacilityTypeCode             " + metadata.getHealthcareFacilityTypeCode());
        LOGGER.info("healthcareFacilityTypeCode_localized   " + metadata.getHealthcareFacilityTypeCode_localized());
        LOGGER.info("practiceSettingCode                    " + metadata.getPracticeSettingCode());
        LOGGER.info("practiceSettingCode_localized          " + metadata.getPracticeSettingCode_localized());
        LOGGER.info("loinc                                  " + metadata.getLoinc());
        LOGGER.info("loinc_localized                        " + metadata.getLoinc_localized());
        LOGGER.info("patientId                              " + metadata.getPatientId());
        LOGGER.info("uniqueId                               " + metadata.getUniqueId());
        LOGGER.info("_rpt_id                                " + metadata.get_rpt_id());
        LOGGER.info("_rpt_name                              " + metadata.get_rpt_name());
        LOGGER.info("_rpt_description                       " + metadata.get_rpt_description());
        LOGGER.info("ss_submissionTime                      " + metadata.getSs_submissionTime());
        LOGGER.info("ss_intendedRecipient                   " + metadata.getSs_intendedRecipient());
        LOGGER.info("ss_authorPerson                        " + metadata.getSs_authorPerson());
        LOGGER.info("ss_authorInstitution                   " + metadata.getSs_authorInstitution());
        LOGGER.info("ss_authorRole                          " + metadata.getSs_authorRole());
        LOGGER.info("ss_authorSpecialty                     " + metadata.getSs_authorSpecialty());
        LOGGER.info("contentTypeCode                        " + metadata.getContentTypeCode());
        LOGGER.info("contentTypeCode_localized              " + metadata.getContentTypeCode_localized());
        LOGGER.info("ss_uniqueId                            " + metadata.getSs_uniqueId());
        LOGGER.info("ss_sourceId                            " + metadata.getSs_sourceId());
        LOGGER.info("ss_patientId                           " + metadata.getSs_patientId());
        LOGGER.info("submissionSetStatus                    " + metadata.getSubmissionSetStatus());
    }
}
