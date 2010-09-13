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

package org.nhind.mail.service;

import junit.framework.TestCase;

/**
 * Test class for methods in the XDMXDSTransformer class.
 * 
 * @author beau
 */
public class XDMXDSTransformerTest extends TestCase {

    /**
     * Constructor
     * 
     * @param testName
     *            The test name
     */
    public XDMXDSTransformerTest(String testName) {
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
     * Test the matchName method.
     */
    public void testMatchName() {
        String zname = null;
        String subsetDirspec = null;
        String subsetFilespec = null;

        boolean output = false;

        zname = "SUBSETDIRSPEC\\FILE\\SPEC";
        subsetDirspec = "SUBSETDIRSPEC";
        subsetFilespec = "FILE/SPEC";
        output = XDMXDSTransformer.matchName(zname, subsetDirspec, subsetFilespec);
        assertEquals("Output does not match expected", true, output);

        zname = "SUBSETDIRSPEC/FILE/SPEC";
        subsetDirspec = "SUBSETDIRSPEC";
        subsetFilespec = "FILE/SPEC";
        output = XDMXDSTransformer.matchName(zname, subsetDirspec, subsetFilespec);
        assertEquals("Output does not match expected", true, output);

        zname = "ZNAME";
        subsetDirspec = "SUBSETDIRSPEC";
        subsetFilespec = "FILE/SPEC";
        output = XDMXDSTransformer.matchName(zname, subsetDirspec, subsetFilespec);
        assertEquals("Output does not match expected", false, output);
    }

}
