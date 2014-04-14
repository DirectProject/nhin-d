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

package org.nhindirect.xd.transform.impl;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.xd.transform.pojo.SimplePerson;

/**
 * Test class for methods in the MimeXDSTransformer class.
 * 
 * @author vlewis
 */
public class DefaultMimeXdsTransformerTest extends TestCase
{
    @SuppressWarnings("unused")
    private static final Log LOGGER = LogFactory.getFactory().getInstance(DefaultMimeXdsTransformerTest.class);

    /**
     * Default constructor.
     * 
     * @param testName
     *            The test name.
     */
    public DefaultMimeXdsTransformerTest(String testName)
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
    
    public void testDummy()
    {
        assertTrue(true);
    }

    /*
     * ****************************************
     * Methods removed, but tests may be reused
     * ****************************************
     */
    
//    /**
//     * Test of getSubmitObjectsRequest method, of class MimeXDSTransformer.
//     */
//    public void testGetSubmitObjectsRequest() throws Exception
//    {
//        LOGGER.info("Begin testGetSubmitObjectsRequest");
//
//        DefaultMimeXdsTransformer transformer = new DefaultMimeXdsTransformer();
//
//        SubmitObjectsRequest result = null;
//
//        String patientId = "AAA";
//        String orgId = "BBB";
//        String subject = "DDD";
//        String formatCode = "TEXT";
//        String mimeType = MimeType.TEXT_PLAIN.getType();
//        String docId = "eabf9010-c6e4-49e9-ae5a-b62368977cf1";
//        String subId = "eabf9010-c6e4-49e9-ae5a-b62368977cf2";
//        String sentDate = "20100101000000";
//        String from = "vlewis@lewistower.com";
//        String auth = "vlewis@lewistower.com";
//
//        SimplePerson person = getSimplePerson();
//
//        result = transformer.getSubmitObjectsRequest(patientId, orgId, person, subject, sentDate, docId, subId,
//                formatCode, mimeType, from, auth);
//
//        QName qname = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0", "SubmitObjectsRequest");
//        String sresult = XmlUtils.marshal(qname, result, oasis.names.tc.ebxml_regrep.xsd.lcm._3.ObjectFactory.class);
//
//        LOGGER.info("Output: " + sresult);
//
//        // TODO: assertions
//    }
//
//    /**
//     * Test of makePatientSlot method, of class MimeXDSTransformer.
//     */
//    public void testMakePatientSlot()
//    {
//        LOGGER.info("Begin testMakePatientSlot");
//
//        DefaultMimeXdsTransformer transformer = new DefaultMimeXdsTransformer();
//
//        SimplePerson patient = getSimplePerson();
//
//        String name = "A";
//        String patientId = "B";
//        String orgId = "C";
//
//        SlotType1 result = transformer.makePatientSlot(name, patient, patientId, orgId);
//
//        assertTrue("Result is null", result != null);
//        assertEquals("Name does not match expected", name, result.getName());
//
//        ValueListType values = result.getValueList();
//        assertTrue("Values is null", values != null);
//        assertTrue("Values valueis null", values.getValue() != null);
//        assertEquals("Values size does not match expected", 5, values.getValue().size());
//
//        // TODO: Additional tests for valid HL7 strings
//    }
//
//    /**
//     * Test of addClassifications method, of class MimeXDSTransformer.
//     */
//    public void testAddClassifications()
//    {
//        LOGGER.info("Begin testAddClassifications");
//
//        DefaultMimeXdsTransformer transformer = new DefaultMimeXdsTransformer();
//
//        List<ClassificationType> classifs = null;
//
//        String docId = "A";
//        String rep = "D";
//
//        ClassificationTypeEnum classificationTypeEnum = ClassificationTypeEnum.DOC_AUTHOR;
//
//        List<String> slotNames = Arrays.asList("codingScheme");
//        List<String> slotValues = Arrays.asList("eventCodeList");
//        List<String> snames = Arrays.asList("12345");
//
//        try
//        {
//            transformer.addClassifications(classifs, docId, classificationTypeEnum, rep, slotNames, slotValues,
//                    snames);
//            fail("Exception not thrown");
//        }
//        catch (IllegalArgumentException e)
//        {
//            assertTrue(true);
//        }
//
//        classifs = new ArrayList<ClassificationType>();
//        transformer.addClassifications(classifs, docId, classificationTypeEnum, rep, slotNames, slotValues,
//                snames);
//
//        assertTrue("List is null", classifs != null);
//        assertEquals("List size does not match expected", 1, classifs.size());
//
//        ClassificationType ct = classifs.get(0);
//        assertEquals("ClassifiedObject does not match expected", docId, ct.getClassifiedObject());
//        assertEquals("ClassificationScheme does not match expected", classificationTypeEnum
//                .getClassificationScheme(), ct.getClassificationScheme());
//        assertEquals("Id does not match expected", classificationTypeEnum.getClassificationId(), ct.getId());
//        assertEquals("NodeRepresentation does not match expected", rep, ct.getNodeRepresentation());
//
//        List<SlotType1> slots = ct.getSlot();
//        assertTrue("Slots list is null", slots != null);
//        assertEquals("Slots list size does not match expected", 1, slots.size());
//
//        assertEquals("Name does not match expected", "codingScheme", slots.get(0).getName());
//        assertEquals("Value does not match expected", "eventCodeList", slots.get(0).getValueList().getValue().get(0));
//
//        InternationalStringType s = ct.getName();
//        assertEquals("Sname does not match expected", "12345", s.getLocalizedString().get(0).getValue());
//    }
//
//    /**
//     * Test of addExternalIds method, of class MimeXDSTransformer.
//     */
//    public void testAddExternalIds()
//    {
//        LOGGER.info("Begin testAddExternalIds");
//
//        DefaultMimeXdsTransformer transformer = new DefaultMimeXdsTransformer();
//
//        List<ExternalIdentifierType> extIds = null;
//
//        String docId = "A";
//        String value = "E";
//
//        ExternalIdentifierTypeEnum externalIdentifier = ExternalIdentifierTypeEnum.DOC_PATIENT_ID;
//
//        try
//        {
//            transformer.addExternalIds(extIds, docId, externalIdentifier, value);
//            fail("Exception not thrown");
//        }
//        catch (IllegalArgumentException e)
//        {
//            assertTrue(true);
//        }
//
//        extIds = new ArrayList<ExternalIdentifierType>();
//        transformer.addExternalIds(extIds, docId, externalIdentifier, value);
//
//        assertTrue("List is null", extIds != null);
//        assertEquals("List size does not match expected", 1, extIds.size());
//
//        ExternalIdentifierType ei = extIds.get(0);
//
//        assertEquals("RegistryObject does not match expected", docId, ei.getRegistryObject());
//        assertEquals("IdentificationScheme does not match expected", externalIdentifier.getIdentificationScheme(), ei
//                .getIdentificationScheme());
//        assertEquals("Id does not match expected", externalIdentifier.getIdentificationId(), ei.getId());
//        assertEquals("Value does not match expected", value, ei.getValue());
//
//        InternationalStringType s = ei.getName();
//        assertEquals("Sname does not match expected", externalIdentifier.getLocalizedString(), s.getLocalizedString()
//                .get(0).getValue());
//    }
//
//    /**
//     * Test of formatDate method, of class MimeXDSTransformer.
//     */
//    public void testFormatDate()
//    {
//        LOGGER.info("Begin testFormatDate");
//
//        DefaultMimeXdsTransformer transformer = new DefaultMimeXdsTransformer();
//
//        Date edate = null;
//
//        try
//        {
//            edate = DatatypeFactory.newInstance().newXMLGregorianCalendar("2010-08-26T00:00:00.000-05:00")
//                    .toGregorianCalendar().getTime();
//        }
//        catch (Exception x)
//        {
//            fail("Test setup failed");
//        }
//
//        String result = transformer.formatDate(edate);
//        assertEquals("Output does not match expected", "20100826010000", result);
//    }
//
//    /**
//     * Test of formatDateFromMDM method, of class MimeXDSTransformer.
//     */
//    public void testFormatDateFromMDM()
//    {
//        LOGGER.info("Begin testFormatDateFromMDM");
//
//        DefaultMimeXdsTransformer transformer = new DefaultMimeXdsTransformer();
//
//        String value = null;
//        String result = null;
//
//        value = "01/01/2000";
//        result = transformer.formatDateFromMDM(value);
//        assertEquals("Output does not match expected", "20000101000000", result);
//
//        value = "01/01/2000+1000";
//        result = transformer.formatDateFromMDM(value);
//        assertEquals("Output does not match expected", "20000101000000", result);
//    }
//
//    /**
//     * Test of makeSlot method, of class MimeXDSTransformer.
//     */
//    public void testMakeSlot()
//    {
//        LOGGER.info("Begin testMakeSlot");
//
//        DefaultMimeXdsTransformer transformer = new DefaultMimeXdsTransformer();
//
//        String name = "testMakeSlot-name";
//        String value = "testMakeSlot-value";
//
//        SlotType1 result = transformer.makeSlot(name, value);
//
//        assertEquals("Name does not match expected value", name, result.getName());
//
//        assertTrue("ValueList is null", result.getValueList() != null);
//        assertTrue("List is null", result.getValueList().getValue() != null);
//        assertEquals("List size does not match expected", 1, result.getValueList().getValue().size());
//        assertEquals("Value does not match expected value", value, result.getValueList().getValue().get(0));
//    }

    /*
     * Begin private methods
     * -----------------------------------------------------------------
     */

    /**
     * Helper method to create a SimplePerson object.
     * 
     * @return a SimplePerson object.
     */
    @SuppressWarnings("unused")
    private SimplePerson getSimplePerson()
    {
        SimplePerson patient = new SimplePerson();

        patient.setFirstName("A");
        patient.setLastName("B");
        patient.setBirthDateTime(formatDateForMDM(new Date()));
        patient.setStreetAddress1("C");
        patient.setCity("D");
        patient.setState("E");
        patient.setZipCode("F");
        patient.setGenderCode("G");

        return patient;
    }

    /**
     * Helper method to format a Date object.
     * 
     * @param date
     *            The date object to format.
     * @return a formatted date.
     */
    private String formatDateForMDM(Date date)
    {
        String formout = "MM/dd/yyyy";
        String ret = null;

        SimpleDateFormat dateOut = new SimpleDateFormat(formout);

        try
        {

            ret = dateOut.format(date);
        }
        catch (Exception x)
        {
            x.printStackTrace();
        }
        return ret;
    }

    /**
     * Get a mock SubmitObjectRequest from an xml file.
     * 
     * @return a mock SubmitObjectRequest string.
     * @throws Exception
     */
    @SuppressWarnings("unused")
    private String getTestSubmit() throws Exception
    {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("submitobjectrequest.xml");
        byte[] theBytes = new byte[is.available()];
        is.read(theBytes);
        return new String(theBytes);
    }

}
