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

package org.nhindirect.xd.transform.util;

import junit.framework.TestCase;

import org.nhindirect.xd.transform.util.type.MimeType;

/**
 * Test class for the MimeType enumeration.
 * 
 * @author beau
 */
public class MimeTypeTest extends TestCase
{

    /**
     * Constructor.
     * 
     * @param testName
     *            The test name
     */
    public MimeTypeTest(String testName)
    {
        super(testName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Test the getType method;
     */
    public void testGetType()
    {
        String s = "application/ccr";
        MimeType m = MimeType.APPLICATION_CCR;

        assertEquals("Get method did not return expected value", s, m.getType());
    }

    /**
     * Test the matches method.
     */
    public void testMatches()
    {
        // Test matches works for all values
        for (MimeType m : MimeType.values())
        {
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
