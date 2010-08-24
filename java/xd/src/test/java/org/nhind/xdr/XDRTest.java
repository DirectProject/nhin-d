/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nhind.xdr;

import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;

import java.io.InputStream;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import junit.framework.TestCase;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

import org.nhind.util.XMLUtils;

/**
 *
 * @author vlewis
 */
public class XDRTest extends TestCase {

    /**
     * Constructor
     * 
     * @param testName The test name
     */
    public XDRTest(String testName) {
        super(testName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of documentRepositoryProvideAndRegisterDocumentSetB method, of class XDR.
     */
    public void testDocumentRepositoryProvideAndRegisterDocumentSetB() {
        System.out.println("documentRepositoryProvideAndRegisterDocumentSetB");
        QName qname = new QName("urn:ihe:iti:xds-b:2007", "ProvideAndRegisterDocumentSetRequestType");
        ProvideAndRegisterDocumentSetRequestType body = null;
        try {
            String request = getTestRequest();
            JAXBElement jb = (JAXBElement) XMLUtils.unmarshal(request, ihe.iti.xds_b._2007.ObjectFactory.class);
            body = (ProvideAndRegisterDocumentSetRequestType) jb.getValue();
        } catch (Exception x) {
            x.printStackTrace();
            fail("Failed unmarshalling request");
        }
        XDR instance = new XDR();

        RegistryResponseType result = instance.documentRepositoryProvideAndRegisterDocumentSetB(body);

        String sresult = null;

        try {
            qname = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0", "RegistryResponseType");

            sresult = XMLUtils.marshal(qname, result, oasis.names.tc.ebxml_regrep.xsd.rs._3.ObjectFactory.class);
        } catch (Exception x) {
            x.printStackTrace();
            fail("Failed unmarshalling response");
        }

        // System.out.println(sresult);
        assertTrue(sresult.indexOf("ResponseStatusType:Success") >= 0);

    }

    /**
     * Return the test request.xml as a string.
     * 
     * @return the test request.xml as a string
     * @throws Exception
     */
    private String getTestRequest() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/request.xml");
        byte[] theBytes = new byte[is.available()];
        is.read(theBytes);
        return new String(theBytes);

    }
}
