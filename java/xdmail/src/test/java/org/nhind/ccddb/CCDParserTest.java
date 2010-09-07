package org.nhind.ccddb;

import junit.framework.TestCase;

/**
 * 
 * @author vlewis
 */
public class CCDParserTest extends TestCase {

    public CCDParserTest(String testName) {
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
     * Test of parseCCD method, of class CCDParser.
     */
    public void testParseCCD() throws Exception {
        System.out.println("parseCCD");
        String ccdXml = "<ClinicalDocument>Test</ClinicalDocument>";
        CCDParser instance = new CCDParser();
        // instance.parseCCD(ccdXml);

    }

    /**
     * Test of getPatientId method, of class CCDParser.
     */
    public void testGetPatientId() {
        System.out.println("getPatientId");
        CCDParser instance = new CCDParser();
        String expResult = "";
        String result = instance.getPatientId();
        // assertEquals(expResult, result);
        // TODO review the generated test code

    }

    /**
     * Test of getOrgId method, of class CCDParser.
     */
    public void testGetOrgId() {
        System.out.println("getOrgId");
        CCDParser instance = new CCDParser();
        String expResult = "";
        String result = instance.getOrgId();
        // assertEquals(expResult, result);
        // TODO review the generated test code

    }

}
