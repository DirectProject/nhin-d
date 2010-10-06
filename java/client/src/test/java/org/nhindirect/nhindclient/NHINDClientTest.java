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

package org.nhindirect.nhindclient;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.nhindclient.config.NHINDClientConfig;
import org.nhindirect.nhindclient.impl.NHINDClientImpl;
import org.nhindirect.xd.transform.document.DirectDocument;
import org.nhindirect.xd.transform.document.DirectMessage;

/**
 * 
 * @author vlewis
 */
public class NHINDClientTest extends TestCase
{
    @SuppressWarnings("unused")
    private static final Log LOGGER = LogFactory.getFactory().getInstance(NHINDClientTest.class);

    public NHINDClientTest(String testName)
    {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Quick integration test for the NHINDClient class.
     * 
     * @throws Exception
     */
    public void testClient() throws Exception
    {
        String sender = "lewistower1@gmail.com";
        // Collection<String> receivers = Arrays.asList("beau+receiver@nologs.org", "beau+receiver2@nologs.org", "http://ELS4055:8080/xd/services/DocumentRepository_Service");
        Collection<String> receivers = Arrays.asList("beau+receiver@nologs.org", "beau+receiver2@nologs.org");

        DirectMessage message = new DirectMessage(sender, receivers);

        message.setSubject("This is a test message (subject)");
        message.setBody("Please find the attached data.");

        DirectDocument document1 = new DirectDocument(getDocumentAsFile());
        DirectDocument.Metadata metadata1 = document1.getMetadata();
        metadata1.extractFromSubmitObjectsRequestXml(getMeta());
        message.addDocument(document1);

        DirectDocument document2 = new DirectDocument(getDocumentAsFile());
        DirectDocument.Metadata metadata2 = document2.getMetadata();
        metadata2.extractFromSubmitObjectsRequestXml(getMeta());
        metadata2.setSs_intendedRecipient("|beau+document2@nologs.org^Smith^John^^^Dr^^^&amp;1.3.6.1.4.1.21367.3100.1&amp;ISO");
        message.addDocument(document2);

        NHINDClient client = new NHINDClientImpl(NHINDClientConfig.DEFAULT);
        client.send(message);
    }

    private File getDocumentAsFile()
    {
        return new File(this.getClass().getClassLoader().getResource("CCD.xml").getPath());
    }

    private String getMeta() throws Exception
    {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("meta.xml");
        byte[] theBytes = new byte[is.available()];
        is.read(theBytes);
        return new String(theBytes);
    }
}
