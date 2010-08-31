package org.nhind.util;

import java.util.List;

import junit.framework.TestCase;

public class HL7UtilsTest extends TestCase {

    /**
     * Constructor
     * 
     * @param testName
     *            The test name
     */
    public HL7UtilsTest(String testName) {
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
     * Test the split method.
     */
    public void testSplit() {
        List<String> tokens = HL7Utils.split(null, "^");
        assertNotNull("Returned list is null", tokens);
        assertEquals("Returned list size does not match expected", 0, tokens.size());

        tokens = HL7Utils.split("PID-3|1111111111^^^&amp;GSIHealth&amp;ISO", "*");
        assertEquals("Returned list size does not match expected", 1, tokens.size());
        assertEquals("Returned token does not match expected", "PID-3|1111111111^^^&amp;GSIHealth&amp;ISO", tokens
                .get(0));

        tokens = HL7Utils.split("PID-3|1111111111^^^&amp;GSIHealth&amp;ISO", "|");
        assertEquals("Returned list size does not match expected", 2, tokens.size());
        assertEquals("Returned token does not match expected", "PID-3", tokens.get(0));
        assertEquals("Returned token does not match expected", "1111111111^^^&amp;GSIHealth&amp;ISO", tokens.get(1));

        tokens = HL7Utils.split("PID-3|1111111111^^^&amp;GSIHealth&amp;ISO", "^");
        assertEquals("Returned list size does not match expected", 4, tokens.size());
        assertEquals("Returned token does not match expected", "PID-3|1111111111", tokens.get(0));
        assertEquals("Returned token does not match expected", "", tokens.get(1));
        assertEquals("Returned token does not match expected", "", tokens.get(2));
        assertEquals("Returned token does not match expected", "&amp;GSIHealth&amp;ISO", tokens.get(3));

        try {
            tokens = HL7Utils.split("PID-3|1111111111^^^&amp;GSIHealth&amp;ISO", null);
            fail("Expected exception not thrown");
        } catch (IllegalArgumentException e) {
        }

        try {
            tokens = HL7Utils.split("PID-3|1111111111^^^&amp;GSIHealth&amp;ISO", "");
            fail("Expected exception not thrown");
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Test the returnField method.
     */
    public void testReturnField() {
        String input = null;
        String output = null;

        input = "PID-3|1111111111^^^&amp;GSIHealth&amp;ISO";
        output = HL7Utils.returnField(input, "|", 0);
        assertEquals("Actual output does not match expected", "PID-3|1111111111^^^&amp;GSIHealth&amp;ISO", output);
        output = HL7Utils.returnField(input, "|", 1);
        assertEquals("Actual output does not match expected", "PID-3", output);
        output = HL7Utils.returnField(input, "|", 2);
        assertEquals("Actual output does not match expected", "1111111111^^^&amp;GSIHealth&amp;ISO", output);
        output = HL7Utils.returnField(input, "|", 3);
        assertEquals("Actual output does not match expected", "PID-3|1111111111^^^&amp;GSIHealth&amp;ISO", output);

        input = "PID-3|1111111111^^^&amp;GSIHealth&amp;ISO";
        output = HL7Utils.returnField(input, "^", 1);
        assertEquals("Actual output does not match expected", "PID-3|1111111111", output);
        output = HL7Utils.returnField(input, "^", 2);
        assertEquals("Actual output does not match expected", "&amp;GSIHealth&amp;ISO", output);

        input = "PID-3|1111111111^^^&amp;GSIHealth&amp;ISO";
        output = HL7Utils.returnField(input, "^|", 1);
        assertEquals("Actual output does not match expected", "PID-3", output);
        output = HL7Utils.returnField(input, "^|", 2);
        assertEquals("Actual output does not match expected", "1111111111", output);

        input = "";
        output = HL7Utils.returnField(input, "|", 1);
        assertEquals("Actual output does not match expected", "", output);

        try {
            input = null;
            output = HL7Utils.returnField(input, "|", 0);
            fail("Exception was not thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        input = "PID-3|1111111111^^^&amp;GSIHealth&amp;ISO";
        output = HL7Utils.returnField(input, "", 0);
        assertEquals("Actual output does not match expected", "PID-3|1111111111^^^&amp;GSIHealth&amp;ISO", output);

        try {
            input = "PID-3|1111111111^^^&amp;GSIHealth&amp;ISO";
            output = HL7Utils.returnField(input, null, 0);
            fail("Exception was not thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }
}
