package org.nhind.mail.util;

import ihe.iti.xds_b._2007.DocumentRepositoryPortType;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import junit.framework.TestCase;

/**
 * Test class for methods in the DocumentRepositoryUtils class.
 * 
 * @author beau
 */
public class DocumentRepositoryUtilsTest extends TestCase {

    /**
     * Class logger.
     */
    private static final Logger LOGGER = Logger.getLogger(DocumentRepositoryUtilsTest.class.getName());

    /**
     * Constructor.
     * 
     * @param testName
     *            The test name
     */
    public DocumentRepositoryUtilsTest(String testName) {
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
     * Test the getDocumentRepositoryPortType method.
     */
    public void testGetDocumentRepositoryPortType() {
        try {
            @SuppressWarnings("unused")
            DocumentRepositoryPortType port = DocumentRepositoryUtils.getDocumentRepositoryPortType("endpoint");
            fail("Exception not thrown");
        } catch (Exception e) {
            // Exception should be thrown, as it looks for the WSDL inside the
            // jar (which has not been created)
            assertTrue(true);
        }
    }

    /**
     * Test the getDocumentRepositoryPortType method.
     */
    public void testGetDocumentRepositoryPortTypeWithURL() {
        URL url = null;

        try {
            url = new URL(DocumentRepositoryUtilsTest.class.getResource("."),
                    "../../../../XDS.b_DocumentRepositoryWSDLSynchMTOM.wsdl");
        } catch (MalformedURLException e) {
            LOGGER.severe("Unable to access WSDL");
            e.printStackTrace();
            fail("Exception thrown");
        }
        
        try {
            DocumentRepositoryPortType port = DocumentRepositoryUtils.getDocumentRepositoryPortType("endpoint", url);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown");
        }
    }
}
