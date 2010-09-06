package org.nhind.mail.util;

import junit.framework.TestCase;

/**
 * Test class for the MimeType enumeration.
 * 
 * @author beau
 */
public class MimeTypeTest extends TestCase {

    /**
     * Constructor
     * 
     * @param testName
     *            The test name
     */
    public MimeTypeTest(String testName) {
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
     * Test the getType method;
     */
    public void testGetType() {
        String s = "application/ccr";
        MimeType m = MimeType.APPLICATION_CCR;

        assertEquals("Get method did not return expected value", s, m.getType());
    }

    /**
     * Test the matches method.
     */
    public void testMatches() {
        // Test matches works for all values
        for (MimeType m : MimeType.values()) {
            assertTrue("Matches method does not correctly match elements", m.matches(m.getType()));
        }

        String s = null;
        MimeType m = MimeType.TEXT_PLAIN;

        s = "text";
        assertEquals("Output does not match expected", false, m.matches(s));

        s = "text/plain";
        assertEquals("Output does not match expected", true, m.matches(s));

        s = "text/plain; charset=UTF-8";
        assertEquals("Output does not match expected", true, m.matches(s));
    }
}
