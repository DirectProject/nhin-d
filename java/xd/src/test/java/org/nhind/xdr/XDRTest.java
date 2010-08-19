/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nhind.xdr;

import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import junit.framework.TestCase;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

/**
 *
 * @author vlewis
 */
public class XDRTest extends TestCase {

    public XDRTest(String testName) {
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
     * Test of documentRepositoryProvideAndRegisterDocumentSetB method, of class XDR.
     */
    public void testDocumentRepositoryProvideAndRegisterDocumentSetB() {
        System.out.println("documentRepositoryProvideAndRegisterDocumentSetB");
        QName qname = new QName("urn:ihe:iti:xds-b:2007", "ProvideAndRegisterDocumentSetRequestType");
        ProvideAndRegisterDocumentSetRequestType body = null;
        try {
            String request = getTestRequest();
            JAXBElement jb = (JAXBElement) unmarshalRequest(qname, request);
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

            sresult = marshalResponse(qname, result);
        } catch (Exception x) {
            x.printStackTrace();
            fail("Failed unmarshalling response");
        }
        // System.out.println(expResponse);
        //  System.out.println(sresult);
        assertTrue(sresult.indexOf("ResponseStatusType:Success") >= 0);

    }

    public Object unmarshalRequest(QName altName, String xml) {

        Object ret = null;
        try {
            //   javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(msg.getClass().getPackage().getName());
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(ihe.iti.xds_b._2007.ObjectFactory.class);
            javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();


            byte currentXMLBytes[] = xml.getBytes();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(currentXMLBytes);
            ret = unmarshaller.unmarshal(byteArrayInputStream);

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getPackage().getName()).log(Level.INFO, xml.substring(0, 50) + " Failed to Unmarshall. Exception msg=" + ex.getMessage());
            ex.printStackTrace();

        }
        return ret;
    }

    protected String marshalResponse(QName altName, Object jaxb) {

        String ret = null;
        try {

            javax.xml.bind.JAXBContext jc = javax.xml.bind.JAXBContext.newInstance(oasis.names.tc.ebxml_regrep.xsd.rs._3.ObjectFactory.class);
            Marshaller u = jc.createMarshaller();

            StringWriter sw = new StringWriter();
            u.marshal(new JAXBElement(altName, jaxb.getClass(), jaxb), sw);
            StringBuffer sb = sw.getBuffer();
            ret = new String(sb);

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getPackage().getName()).log(Level.INFO, "marshall. Exception msg=" + ex.getMessage());
            ex.printStackTrace();

        }
        return ret;
    }

    private String getTestRequest() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/request.xml");
        byte[] theBytes = new byte[is.available()];
        is.read(theBytes);
        return new String(theBytes);

    }
}
