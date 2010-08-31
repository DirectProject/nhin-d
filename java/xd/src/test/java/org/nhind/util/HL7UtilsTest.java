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
