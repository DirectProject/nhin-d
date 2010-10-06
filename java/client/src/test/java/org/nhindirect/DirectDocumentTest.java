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

package org.nhindirect;

import java.io.InputStream;

import junit.framework.TestCase;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;

import org.nhindirect.transform.util.XmlUtils;

/**
 * Test class for methods in the DirectDocument class.
 * 
 * @author beau
 */
public class DirectDocumentTest extends TestCase
{

    /**
     * @param testName
     */
    public DirectDocumentTest(String testName)
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
     * Test the consume method, passing a SubmitObjectsRequest object.
     * 
     * @throws Exception
     */
    public void testConsume_submitObjectsRequest() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        document.setData("document data");

        metadata.consume(getMetadata());

        metadata.printValues();
    }

    /**
     * Test the consume method, passing a String.
     * 
     * @throws Exception
     */
    public void testConsume_string() throws Exception
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        document.setData("document data");

        metadata.consume(getMetadataAsString());

        metadata.printValues();
    }

    private String getMetadataAsString() throws Exception
    {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("meta.xml");
        byte[] theBytes = new byte[is.available()];
        is.read(theBytes);

        return new String(theBytes);
    }

    private SubmitObjectsRequest getMetadata() throws Exception
    {
        return (SubmitObjectsRequest) XmlUtils.unmarshal(getMetadataAsString(),
                oasis.names.tc.ebxml_regrep.xsd.lcm._3.ObjectFactory.class);
    }

}
