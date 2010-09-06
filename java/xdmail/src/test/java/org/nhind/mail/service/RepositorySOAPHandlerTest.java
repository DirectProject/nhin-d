package org.nhind.mail.service;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;

import junit.framework.TestCase;

/**
 * Test class for methods in RepositorySOAPHandler.
 * 
 * @author beau
 */
public class RepositorySOAPHandlerTest extends TestCase {

    /**
     * Constructor
     * 
     * @param testName
     *            The test name
     */
    public RepositorySOAPHandlerTest(String testName) {
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
     * Test the getHeaders method.
     */
    public void testGetHeaders() {
        RepositorySOAPHandler handler = new RepositorySOAPHandler();

        Set<QName> headers = handler.getHeaders();
        assertEquals("Number of elements does not match expected", 3, headers.size());

        if (!headers.contains(new QName("http://www.w3.org/2005/08/addressing", "Action"))) {
            fail("Headers missing expected object");
        }
        if (!headers.contains(new QName("http://www.w3.org/2005/08/addressing", "To"))) {
            fail("Headers missing expected object");
        }
        if (!headers.contains(new QName(
                "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security"))) {
            fail("Headers missing expected object");
        }
    }

    /**
     * Test the getMessageEncoding method.
     */
    public void testGetMessageEncoding() {
        String output = null;
        SOAPMessage message = null;
        RepositorySOAPHandler handler = new RepositorySOAPHandler();

        try {
            MessageFactory mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
            message = mf.createMessage();

            message.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "ISO-8859-1");
            assertNotNull("Test setup failed", message.getProperty(SOAPMessage.CHARACTER_SET_ENCODING));
            output = handler.getMessageEncoding(message);
            assertEquals("Message encoding does not match expected", "ISO-8859-1", output);

            message.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, null);
            assertNull("Test setup failed", message.getProperty(SOAPMessage.CHARACTER_SET_ENCODING));
            output = handler.getMessageEncoding(message);
            assertEquals("Message encoding does not match expected", "utf-8", output);
        } catch (Exception e) {
            fail("Exception thrown during mock SOAPMessage creation/handling.");
            e.printStackTrace();
        }
    }

}
