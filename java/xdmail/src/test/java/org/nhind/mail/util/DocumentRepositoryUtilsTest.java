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
