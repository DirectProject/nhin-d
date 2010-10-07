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

import java.util.Date;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.xd.transform.pojo.SimplePerson;

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

    public void testMimeType() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setMimeType(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getMimeType());
    }

    public void testEotDescription() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.set_eot_description(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.get_eot_description());
    }

    public void testCreationTime() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        Date date = new Date(1, 2, 3, 4, 5, 6);
        metadata.setCreationTime(date);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", date.getYear(), metadata.getCreationTime().getYear());
        assertEquals("Output does not match expected", date.getMonth(), metadata.getCreationTime().getMonth());
        assertEquals("Output does not match expected", date.getDate(), metadata.getCreationTime().getDate());
        assertEquals("Output does not match expected", 0, metadata.getCreationTime().getHours());
        assertEquals("Output does not match expected", 0, metadata.getCreationTime().getMinutes());
        assertEquals("Output does not match expected", 0, metadata.getCreationTime().getSeconds());
    }

    public void testLanguageCode() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setLanguageCode(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getLanguageCode());
    }

    public void testServiceStartTime() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        Date date = new Date(1, 2, 3, 4, 5, 6);
        metadata.setServiceStartTime(date);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", date.getYear(), metadata.getServiceStartTime().getYear());
        assertEquals("Output does not match expected", date.getMonth(), metadata.getServiceStartTime().getMonth());
        assertEquals("Output does not match expected", date.getDate(), metadata.getServiceStartTime().getDate());
        assertEquals("Output does not match expected", date.getHours(), metadata.getServiceStartTime().getHours());
        assertEquals("Output does not match expected", date.getMinutes(), metadata.getServiceStartTime().getMinutes());
        assertEquals("Output does not match expected", 0, metadata.getServiceStartTime().getSeconds());
    }

    public void testServiceStopTime() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        Date date = new Date(1, 2, 3, 4, 5, 6);
        metadata.setServiceStopTime(date);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", date.getYear(), metadata.getServiceStopTime().getYear());
        assertEquals("Output does not match expected", date.getMonth(), metadata.getServiceStopTime().getMonth());
        assertEquals("Output does not match expected", date.getDate(), metadata.getServiceStopTime().getDate());
        assertEquals("Output does not match expected", date.getHours(), metadata.getServiceStopTime().getHours());
        assertEquals("Output does not match expected", date.getMinutes(), metadata.getServiceStopTime().getMinutes());
        assertEquals("Output does not match expected", 0, metadata.getServiceStopTime().getSeconds());
    }

    public void testSourcePatient() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        SimplePerson sourcePatient = new SimplePerson();
        sourcePatient.setFirstName("first");
        sourcePatient.setLastName("last");
        sourcePatient.setLocalId("localId");
        sourcePatient.setLocalOrg("localOrg");
        sourcePatient.setBirthDateTime("19560527");
        sourcePatient.setGenderCode("M");
        sourcePatient.setStreetAddress1("street");
        sourcePatient.setCity("city");
        sourcePatient.setState("state");
        sourcePatient.setZipCode("zip");

        metadata.setSourcePatient(sourcePatient);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", sourcePatient.getFirstName(), metadata.getSourcePatient()
                .getFirstName());
        assertEquals("Output does not match expected", sourcePatient.getLastName(), metadata.getSourcePatient()
                .getLastName());
        assertEquals("Output does not match expected", sourcePatient.getLocalId(), metadata.getSourcePatient()
                .getLocalId());
        assertEquals("Output does not match expected", sourcePatient.getLocalOrg(), metadata.getSourcePatient()
                .getLocalOrg());
        assertEquals("Output does not match expected", sourcePatient.getBirthDateTime(), metadata.getSourcePatient()
                .getBirthDateTime());
        assertEquals("Output does not match expected", sourcePatient.getGenderCode(), metadata.getSourcePatient()
                .getGenderCode());
        assertEquals("Output does not match expected", sourcePatient.getStreetAddress1(), metadata.getSourcePatient()
                .getStreetAddress1());
        assertEquals("Output does not match expected", sourcePatient.getCity(), metadata.getSourcePatient().getCity());
        assertEquals("Output does not match expected", sourcePatient.getState(), metadata.getSourcePatient().getState());
        assertEquals("Output does not match expected", sourcePatient.getZipCode(), metadata.getSourcePatient()
                .getZipCode());
    }

    public void testAuthorPerson() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setAuthorPerson(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getAuthorPerson());
    }

    public void testAuthorInstitution() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setAuthorInstitution(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getAuthorInstitution());
    }

    public void testAuthorRole() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setAuthorRole(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getAuthorRole());
    }

    public void testAuthorSpecialty() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setAuthorSpecialty(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getAuthorSpecialty());
    }

    public void testClassCode() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setClassCode(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getClassCode());
    }

    public void testClassCode_localized() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setClassCode_localized(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getClassCode_localized());
    }

    public void testConfidentialityCode() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setConfidentialityCode(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getConfidentialityCode());
    }

    public void testConfidentialityCode_localized() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setConfidentialityCode_localized(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getConfidentialityCode_localized());
    }

    public void testFormatCode() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setFormatCode(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getFormatCode());
    }

    public void testFormatCode_localized() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setFormatCode_localized(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getFormatCode_localized());
    }

    public void testHealthcareFacilityTypeCode() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setHealthcareFacilityTypeCode(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getHealthcareFacilityTypeCode());
    }

    public void testHealthcareFacilityTypeCode_localized() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setHealthcareFacilityTypeCode_localized(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getHealthcareFacilityTypeCode_localized());
    }

    public void testPracticeSettingCode() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setPracticeSettingCode(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getPracticeSettingCode());
    }

    public void testPracticeSettingCode_localized() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setPracticeSettingCode_localized(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getPracticeSettingCode_localized());
    }

    public void testLoinc() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setLoinc(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getLoinc());
    }

    public void testLoinc_localized() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setLoinc_localized(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getLoinc_localized());
    }

    public void testPatientId() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setPatientId(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getPatientId());
    }

    public void testUniqueId() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setUniqueId(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getUniqueId());
    }

    public void test_rpt_name() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.set_rpt_name(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.get_rpt_name());
    }

    public void test_rpt_description() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.set_rpt_description(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.get_rpt_description());
    }

    public void testSs_submissionTime() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        Date date = new Date(1, 2, 3, 4, 5, 6);
        metadata.setSs_submissionTime(date);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", date.getYear(), metadata.getSs_submissionTime().getYear());
        assertEquals("Output does not match expected", date.getMonth(), metadata.getSs_submissionTime().getMonth());
        assertEquals("Output does not match expected", date.getDate(), metadata.getSs_submissionTime().getDate());
        assertEquals("Output does not match expected", date.getHours(), metadata.getSs_submissionTime().getHours());
        assertEquals("Output does not match expected", date.getMinutes(), metadata.getSs_submissionTime().getMinutes());
        assertEquals("Output does not match expected", date.getSeconds(), metadata.getSs_submissionTime().getSeconds());
    }

    public void testSs_intendedRecipient() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setSs_intendedRecipient(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getSs_intendedRecipient());
    }
    
    public void testSs_authorPerson() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setSs_authorPerson(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getSs_authorPerson());
    }
    
    public void testSs_authorInstitution() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setSs_authorInstitution(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getSs_authorInstitution());
    }
    
    public void testSs_authorRole() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setSs_authorRole(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getSs_authorRole());
    }
    
    public void testSs_authorSpecialty() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setSs_authorSpecialty(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getSs_authorSpecialty());
    }

    public void testContentTypeCode() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setContentTypeCode(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getContentTypeCode());
    }

    public void testContentTypeCode_localized() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setContentTypeCode_localized(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getContentTypeCode_localized());
    }
    
    public void testSs_uniqueId() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setSs_uniqueId(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getSs_uniqueId());
    }
    
    public void testSs_sourceId() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setSs_sourceId(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getSs_sourceId());
    }
    
    public void testSs_patientId() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setSs_patientId(value);

        String xml = metadata.toString();

        metadata = document.new Metadata();
        metadata.setValues(xml);

        assertEquals("Output does not match expected", value, metadata.getSs_patientId());
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
        metadata.set_eot_description("3");
        metadata.setCreationTime(new Date());
        metadata.setLanguageCode("5");
        metadata.setServiceStartTime(new Date());
        metadata.setServiceStopTime(new Date());
        metadata.setSourcePatient(new SimplePerson("Bob", "Smith"));
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
        metadata.set_rpt_name("29");
        metadata.set_rpt_description("30");
        metadata.setSs_submissionTime(new Date());
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
        LOGGER.info("sourcePatient                          " + metadata.getSourcePatient().getLastName() + ", " + metadata.getSourcePatient().getFirstName());
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
