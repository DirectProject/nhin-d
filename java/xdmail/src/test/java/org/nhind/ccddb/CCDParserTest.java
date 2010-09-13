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

package org.nhind.ccddb;

import junit.framework.TestCase;

/**
 * 
 * @author vlewis
 */
public class CCDParserTest extends TestCase {

    public CCDParserTest(String testName) {
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
     * Test of parseCCD method, of class CCDParser.
     */
    public void testParseCCD() throws Exception {
        System.out.println("parseCCD");
        String ccdXml = "<ClinicalDocument>Test</ClinicalDocument>";
        CCDParser instance = new CCDParser();
        // instance.parseCCD(ccdXml);

    }

    /**
     * Test of getPatientId method, of class CCDParser.
     */
    public void testGetPatientId() {
        System.out.println("getPatientId");
        CCDParser instance = new CCDParser();
        String expResult = "";
        String result = instance.getPatientId();
        // assertEquals(expResult, result);
        // TODO review the generated test code

    }

    /**
     * Test of getOrgId method, of class CCDParser.
     */
    public void testGetOrgId() {
        System.out.println("getOrgId");
        CCDParser instance = new CCDParser();
        String expResult = "";
        String result = instance.getOrgId();
        // assertEquals(expResult, result);
        // TODO review the generated test code

    }

}
