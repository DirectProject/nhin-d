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
import java.util.List;
import java.util.UUID;

import junit.framework.TestCase;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.xd.transform.pojo.SimplePerson;

/**
 * Unit tests for the DirectDocument2 class.
 * 
 * @author beau
 */
public class DirectDocument2Test extends TestCase
{
    private static final Log LOGGER = LogFactory.getFactory().getInstance(DirectDocument2Test.class);

    /**
     * Default constructor.
     * 
     * @param testName
     *            The test name.
     */
    public DirectDocument2Test(String testName)
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
     * Test mimeType.
     * 
     * @throws Exception
     */
    public void testMimeType() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setMimeType(value);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getMimeType());
    }

    /**
     * Test description.
     * 
     * @throws Exception
     */
    public void testDescription() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setDescription(value);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();
        
        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);
        
        assertEquals("Output does not match expected", value, metadata.getDescription());
    }

    /**
     * Test creationTime.
     * 
     * @throws Exception
     */
    public void testCreationTime() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        Date date = new Date(1, 2, 3, 4, 5, 6);
        metadata.setCreationTime(date);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", date.getYear(), metadata.getCreationTime().getYear());
        assertEquals("Output does not match expected", date.getMonth(), metadata.getCreationTime().getMonth());
        assertEquals("Output does not match expected", date.getDate(), metadata.getCreationTime().getDate());
        assertEquals("Output does not match expected", 0, metadata.getCreationTime().getHours());
        assertEquals("Output does not match expected", 0, metadata.getCreationTime().getMinutes());
        assertEquals("Output does not match expected", 0, metadata.getCreationTime().getSeconds());
    }

    /**
     * Test languageCode.
     * 
     * @throws Exception
     */
    public void testLanguageCode() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setLanguageCode(value);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getLanguageCode());
    }

    /**
     * Test serviceStartTime.
     * 
     * @throws Exception
     */
    public void testServiceStartTime() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        Date date = new Date(1, 2, 3, 4, 5, 6);
        metadata.setServiceStartTime(date);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", date.getYear(), metadata.getServiceStartTime().getYear());
        assertEquals("Output does not match expected", date.getMonth(), metadata.getServiceStartTime().getMonth());
        assertEquals("Output does not match expected", date.getDate(), metadata.getServiceStartTime().getDate());
        assertEquals("Output does not match expected", date.getHours(), metadata.getServiceStartTime().getHours());
        assertEquals("Output does not match expected", date.getMinutes(), metadata.getServiceStartTime().getMinutes());
        assertEquals("Output does not match expected", 0, metadata.getServiceStartTime().getSeconds());
    }

    /**
     * Test serviceStopTime.
     * 
     * @throws Exception
     */
    public void testServiceStopTime() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        Date date = new Date(1, 2, 3, 4, 5, 6);
        metadata.setServiceStopTime(date);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", date.getYear(), metadata.getServiceStopTime().getYear());
        assertEquals("Output does not match expected", date.getMonth(), metadata.getServiceStopTime().getMonth());
        assertEquals("Output does not match expected", date.getDate(), metadata.getServiceStopTime().getDate());
        assertEquals("Output does not match expected", date.getHours(), metadata.getServiceStopTime().getHours());
        assertEquals("Output does not match expected", date.getMinutes(), metadata.getServiceStopTime().getMinutes());
        assertEquals("Output does not match expected", 0, metadata.getServiceStopTime().getSeconds());
    }

    /**
     * Test sourcePatient.
     * 
     * @throws Exception
     */
/*    public void testSourcePatient() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        SimplePerson sourcePatient = new SimplePerson();
        sourcePatient.setFirstName("first");
        sourcePatient.setMiddleName("middle");
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

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", sourcePatient.getFirstName(), metadata.getSourcePatient().getFirstName());
        assertEquals("Output does not match expected", sourcePatient.getMiddleName(), metadata.getSourcePatient().getMiddleName());
        assertEquals("Output does not match expected", sourcePatient.getLastName(), metadata.getSourcePatient().getLastName());
        assertEquals("Output does not match expected", sourcePatient.getLocalId(), metadata.getSourcePatient().getLocalId());
        assertEquals("Output does not match expected", sourcePatient.getLocalOrg(), metadata.getSourcePatient().getLocalOrg());
        assertEquals("Output does not match expected", sourcePatient.getBirthDateTime(), metadata.getSourcePatient().getBirthDateTime());
        assertEquals("Output does not match expected", sourcePatient.getGenderCode(), metadata.getSourcePatient().getGenderCode());
        assertEquals("Output does not match expected", sourcePatient.getStreetAddress1(), metadata.getSourcePatient().getStreetAddress1());
        assertEquals("Output does not match expected", sourcePatient.getCity(), metadata.getSourcePatient().getCity());
        assertEquals("Output does not match expected", sourcePatient.getState(), metadata.getSourcePatient().getState());
        assertEquals("Output does not match expected", sourcePatient.getZipCode(), metadata.getSourcePatient().getZipCode());
    }
*/
    /**
     * Test authorPerson.
     * 
     * @throws Exception
     */
    public void testAuthorPerson() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setAuthorPerson(value);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getAuthorPerson());
    }

    /**
     * Test authorInstitution.
     * 
     * @throws Exception
     */
    public void testAuthorInstitution() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        List<String> values = Arrays.asList("input1", "input2");
        metadata.setAuthorInstitution(values);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", values.get(0), metadata.getAuthorInstitution().get(0));
        assertEquals("Output does not match expected", values.get(1), metadata.getAuthorInstitution().get(1));
    }

    /**
     * Test authorRole.
     * 
     * @throws Exception
     */
    public void testAuthorRole() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setAuthorRole(value);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getAuthorRole());
    }

    /**
     * Test authorSpecialty.
     * 
     * @throws Exception
     */
    public void testAuthorSpecialty() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setAuthorSpecialty(value);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getAuthorSpecialty());
    }

    /**
     * Test classCode.
     * 
     * @throws Exception
     */
    public void testClassCode() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setClassCode(value);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getClassCode());
        assertEquals("Output does not match expected", null, metadata.getClassCode_localized());
        
        metadata.setClassCode(value, true);

        eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getClassCode());
        assertEquals("Output does not match expected", value, metadata.getClassCode_localized());
    }

    /**
     * Test classCode_localized.
     * 
     * @throws Exception
     */
    public void testClassCode_localized() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setClassCode(UUID.randomUUID().toString());
        metadata.setClassCode_localized(value);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getClassCode_localized());
    }

    /**
     * Test confidentialityCode.
     * 
     * @throws Exception
     */
    public void testConfidentialityCode() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setConfidentialityCode(value);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getConfidentialityCode());
        assertEquals("Output does not match expected", null, metadata.getConfidentialityCode_localized());
        
        metadata.setConfidentialityCode(value, true);

        eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getConfidentialityCode());
        assertEquals("Output does not match expected", value, metadata.getConfidentialityCode_localized());
    }

    /**
     * Test confidentialityCode_localized.
     * 
     * @throws Exception
     */
    public void testConfidentialityCode_localized() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setConfidentialityCode(UUID.randomUUID().toString());
        metadata.setConfidentialityCode_localized(value);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getConfidentialityCode_localized());
    }

    /**
     * Test formatCode.
     * 
     * @throws Exception
     */
    public void testFormatCode() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setFormatCode(value);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getFormatCode());
        assertEquals("Output does not match expected", null, metadata.getFormatCode_localized());        

        metadata.setFormatCode(value, true);

        eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getFormatCode());
        assertEquals("Output does not match expected", value, metadata.getFormatCode_localized());    
    }

    /**
     * Test formatCode_localized.
     * 
     * @throws Exception
     */
    public void testFormatCode_localized() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setFormatCode(UUID.randomUUID().toString());
        metadata.setFormatCode_localized(value);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getFormatCode_localized());
    }

    /**
     * Test healthcareFacilityTypeCode.
     * 
     * @throws Exception
     */
    public void testHealthcareFacilityTypeCode() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setHealthcareFacilityTypeCode(value);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getHealthcareFacilityTypeCode());
        assertEquals("Output does not match expected", null, metadata.getHealthcareFacilityTypeCode_localized());
        
        metadata.setHealthcareFacilityTypeCode(value, true);

        eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getHealthcareFacilityTypeCode());
        assertEquals("Output does not match expected", value, metadata.getHealthcareFacilityTypeCode_localized());
    }

    /**
     * Test healthcareFacilityTypeCode_localized.
     * 
     * @throws Exception
     */
    public void testHealthcareFacilityTypeCode_localized() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setHealthcareFacilityTypeCode(UUID.randomUUID().toString());
        metadata.setHealthcareFacilityTypeCode_localized(value);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getHealthcareFacilityTypeCode_localized());
    }

    /**
     * Test practiceSettingCode.
     * 
     * @throws Exception
     */
    public void testPracticeSettingCode() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setPracticeSettingCode(value);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getPracticeSettingCode());
        assertEquals("Output does not match expected", null, metadata.getPracticeSettingCode_localized());
        
        metadata.setPracticeSettingCode(value, true);

        eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getPracticeSettingCode());
        assertEquals("Output does not match expected", value, metadata.getPracticeSettingCode_localized());
    }

    /**
     * Test practiceSettingCode_localized.
     * 
     * @throws Exception
     */
    public void testPracticeSettingCode_localized() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setPracticeSettingCode(UUID.randomUUID().toString());
        metadata.setPracticeSettingCode_localized(value);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getPracticeSettingCode_localized());
    }

    /**
     * Test loinc.
     * 
     * @throws Exception
     */
    public void testLoinc() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setLoinc(value);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getLoinc());
        assertEquals("Output does not match expected", null, metadata.getLoinc_localized());

        metadata.setLoinc(value, true);

        eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getLoinc());
        assertEquals("Output does not match expected", value, metadata.getLoinc_localized());
    }

    /**
     * Test loinc_localized.
     * 
     * @throws Exception
     */
    public void testLoinc_localized() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setLoinc(UUID.randomUUID().toString());
        metadata.setLoinc_localized(value);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getLoinc_localized());
    }

    /**
     * Test patientId.
     * 
     * @throws Exception
     */
    public void testPatientId() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setPatientId(value);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getPatientId());
    }

    /**
     * Test uniqueId.
     * 
     * @throws Exception
     */
    public void testUniqueId() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setUniqueId(value);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getUniqueId());
    }

    /**
     * Test hash.
     * 
     * @throws Exception
     */
    public void testHash() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        String value = "input";
        metadata.setHash(value);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getHash());
    }

    /**
     * Test size.
     * 
     * @throws Exception
     */
    public void testSize() throws Exception
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        Long value = 11L;
        metadata.setSize(value);

        ExtrinsicObjectType eot = metadata.generateExtrinsicObjectType();

        metadata = new DirectDocument2.Metadata();
        metadata.setValues(eot);

        assertEquals("Output does not match expected", value, metadata.getSize());
    }
    
    /**
     * Generic setter test.
     */
    public void testDirectDocument2Metadata()
    {
        DirectDocument2 document = new DirectDocument2();
        DirectDocument2.Metadata metadata = document.getMetadata();

        displayMetadata(metadata);

        metadata.setMimeType("1");
        metadata.setId("2");
        metadata.setDescription("3");
        metadata.setCreationTime(new Date());
        metadata.setLanguageCode("5");
        metadata.setServiceStartTime(new Date());
        metadata.setServiceStopTime(new Date());
        metadata.setSourcePatient(new SimplePerson("Bob", "Smith"));
        metadata.setAuthorPerson("10");
        metadata.setAuthorInstitution(Arrays.asList("11.1", "11.2"));
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
        metadata.setPracticeSettingCode("2");
        metadata.setPracticeSettingCode_localized("23");
        metadata.setLoinc("24");
        metadata.setLoinc_localized("25");
        metadata.setPatientId("26");
        metadata.setUniqueId("27");
        metadata.setHash("28");
        metadata.setSize(Long.valueOf("29"));

        metadata.setSubmissionSetStatus("42");

        displayMetadata(metadata);
    }

    private void displayMetadata(DirectDocument2.Metadata metadata)
    {
        LOGGER.info("mimeType                               " + metadata.getMimeType());
        LOGGER.info("id                                     " + metadata.getId());
        LOGGER.info("description                            " + metadata.getDescription());
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
        LOGGER.info("hash                                   " + metadata.getHash());
        LOGGER.info("size                                   " + metadata.getSize());
        LOGGER.info("submissionSetStatus                    " + metadata.getSubmissionSetStatus());
        
        LOGGER.info(metadata.toString());
    }
}
