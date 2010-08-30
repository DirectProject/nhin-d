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

package org.nhind.xdr;

import junit.framework.TestCase;

/**
 * Test class for methods in DocumentRegistry.
 * 
 * @author beau
 */
public class DocumentRegistryTest extends TestCase {

    /**
     * Constructor
     * 
     * @param testName
     *            The test name
     */
    public DocumentRegistryTest(String testName) {
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
     * Test the getAuthorEmail method.
     */
    public void testGetAuthorEmail() {
        String authorEmail = null;
        DocumentRegistry dr = new DocumentRegistry();

        dr.setAuthor("vincent.lewis@gsihealth.com^Allscripts^Provider^^^^^^&amp;1.3.6.1.4.1.21367.3100.1&amp;ISO");
        authorEmail = dr.getAuthorEmail();
        assertEquals("Email does not match expected value", "vincent.lewis@gsihealth.com", authorEmail);

        dr.setAuthor("nhin-d@nologs.org");
        authorEmail = dr.getAuthorEmail();
        assertEquals("Email does not match expected value", "nhin-d@nologs.org", authorEmail);

        dr.setAuthor("John Smith");
        authorEmail = dr.getAuthorEmail();
        assertEquals("Email does not match expected value", "postmaster@nhindirect.org", authorEmail);

        dr.setAuthor("");
        authorEmail = dr.getAuthorEmail();
        assertEquals("Email does not match expected value", "postmaster@nhindirect.org", authorEmail);

        dr.setAuthor(null);
        authorEmail = dr.getAuthorEmail();
        assertEquals("Email does not match expected value", "postmaster@nhindirect.org", authorEmail);
    }

    /**
     * Test the formatDateForMDM method.
     */
    public void testFormatDateForMDM() {
        String input = null;
        String output = null;

        input = "1/1/2001";
        output = DocumentRegistry.formatDateForMDM(input);
        assertEquals("Output does not match expected", input, output);

        input = "20010101000000+222";
        output = DocumentRegistry.formatDateForMDM(input);
        assertEquals("Output does not match expected", "01/01/2001", output);

        input = "20010101 00:00:00";
        output = DocumentRegistry.formatDateForMDM(input);
        assertEquals("Output does not match expected", "01/01/2001", output);

        input = "20010101T00:00:00";
        output = DocumentRegistry.formatDateForMDM(input);
        assertEquals("Output does not match expected", "01/01/2001", output);

        input = "20010101000000";
        output = DocumentRegistry.formatDateForMDM(input);
        assertEquals("Output does not match expected", "01/01/2001", output);

        try {
            input = null;
            output = DocumentRegistry.formatDateForMDM(input);
            fail("Exception not thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        try {
            input = "";
            output = DocumentRegistry.formatDateForMDM(input);
            fail("Exception not thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        try {
            input = " ";
            output = DocumentRegistry.formatDateForMDM(input);
            fail("Exception not thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        input = ";";
        output = DocumentRegistry.formatDateForMDM(input);
        assertEquals("Output does not match expected", input, output);
    }

}
