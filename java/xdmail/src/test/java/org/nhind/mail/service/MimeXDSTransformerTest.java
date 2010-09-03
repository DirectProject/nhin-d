/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nhind.mail.service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import junit.framework.TestCase;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;

import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;
import org.nhind.util.XMLUtils;


/**
 *
 * @author vlewis
 */
public class MimeXDSTransformerTest extends TestCase {

    public MimeXDSTransformerTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    /**
     * Test of getSubmitObjectsRequest method, of class MimeXDSTransformer.
     */
    public void testGetSubmitObjectsRequest() throws Exception{
        System.out.println("getSubmitObjectsRequest");
        String patientId = "AAA";
        String orgId = "BBB";
        String objId = "a.b.c";
        SimplePerson person = getSimplePerson();
        String subject = "DDD";
        String formatCode = "TEXT";
        String mimeType = "text/plain";
        MimeXDSTransformer instance = new MimeXDSTransformer();
        String expResult = getTestSubmit();
        String docId = "eabf9010-c6e4-49e9-ae5a-b62368977cf1";
        String subId = "eabf9010-c6e4-49e9-ae5a-b62368977cf2";
        String sentDate =  "20100101000000";
        String from = "vlewis@lewistower.com";
        String auth = "vlewis@lewistower.com";
        SubmitObjectsRequest result = instance.getSubmitObjectsRequest(patientId, orgId, person, subject,sentDate,docId,subId, formatCode, mimeType, from, auth);

        //getSubmitObjectsRequest(String patientId, String orgId, SimplePerson person, String subject, String sentDate, String docId, String subId, String formatCode, String mimeType) {
        QName qname = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0", "SubmitObjectsRequest");

        String sresult = XMLUtils.marshal(qname, result, oasis.names.tc.ebxml_regrep.xsd.lcm._3.ObjectFactory.class);
        System.out.println(sresult);

       // assertEquals(expResult, sresult);

    }

  
    /**
     * Test of makePatientSlot method, of class MimeXDSTransformer.
     */
    public void testMakePatientSlot() {
        System.out.println("makePatientSlot");
        String name = "A";
        SimplePerson patient = getSimplePerson();
        String patientId = "B";
        String orgId = "C";
        MimeXDSTransformer instance = new MimeXDSTransformer();
        SlotType1 expResult = null;
        SlotType1 result = instance.makePatientSlot(name, patient, patientId, orgId);
        //TODO  make this work
       // assertEquals(expResult, result);
    
      
    }

    /**
     * Test of addClassifications method, of class MimeXDSTransformer.
     */
    public void testAddClassifications() {
        System.out.println("addClassifications");
        List classifs = new ArrayList();
        String docId = "A";
        String id = "B";
        String scheme = "C";
        String rep = "D";
        ArrayList slotNames = null;
        ArrayList slotValues = null;
        ArrayList snames = null;
         slotNames = new ArrayList();
        slotNames.add("codingScheme");
        slotValues = new ArrayList();
        slotValues.add("eventCodeList");
        snames = new ArrayList();
        snames.add("12345");
        MimeXDSTransformer instance = new MimeXDSTransformer();
        instance.addClassifications(classifs, docId, id, scheme, rep, slotNames, slotValues, snames);
      
    }

    /**
     * Test of addExternalIds method, of class MimeXDSTransformer.
     */
    public void testAddExternalIds() {
        System.out.println("addExternalIds");
        List extIds = new ArrayList();
        String docId = "A";
        String scheme = "B";
        String id = "C";
        String sname = "D";
        String value = "E";
        MimeXDSTransformer instance = new MimeXDSTransformer();
        instance.addExternalIds(extIds, docId, scheme, id, sname, value);
     
    }

    /**
     * Test of formatDate method, of class MimeXDSTransformer.
     */
    public void testFormatDate() {
        System.out.println("formatDate");
        Date edate = null;
        try{
         edate = DatatypeFactory.newInstance().newXMLGregorianCalendar("2010-08-26T00:00:00.000-05:00").toGregorianCalendar().getTime();
        }catch(Exception x){

        }
       
        MimeXDSTransformer instance = new MimeXDSTransformer();
        String expResult = "20100826010000";
        String result = instance.formatDate(edate);
        assertEquals(expResult, result);
       
    }

    /**
     * Test of formatDateFromMDM method, of class MimeXDSTransformer.
     */
    public void testFormatDateFromMDM() {
        System.out.println("formatDateFromMDM");
        String value = "01/01/2000";
        MimeXDSTransformer instance = new MimeXDSTransformer();
        String expResult = "20000101000000";
        String result = instance.formatDateFromMDM(value);
        assertEquals(expResult, result);

    }

    /**
     * Test of makeSlot method, of class MimeXDSTransformer.
     */
    public void testMakeSlot() {
        System.out.println("makeSlot");
        String name = "";
        String value = "";
        MimeXDSTransformer instance = new MimeXDSTransformer();
        SlotType1 expResult = null;
        SlotType1 result = instance.makeSlot(name, value);
        //TODO make this work
     //   assertEquals(expResult, result);
        
    }

    private SimplePerson getSimplePerson() {
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

    protected String formatDateForMDM(Date date) {
        String formout = "MM/dd/yyyy";
        String ret = null;

        SimpleDateFormat dateOut = new SimpleDateFormat(formout);

        try {

            ret = dateOut.format(date);
        } catch (Exception x) {
            x.printStackTrace();
        }
        return ret;
    }
      private String getTestSubmit() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/submitobjectrequest.xml");
        byte[] theBytes = new byte[is.available()];
        is.read(theBytes);
        return new String(theBytes);
    }
}
