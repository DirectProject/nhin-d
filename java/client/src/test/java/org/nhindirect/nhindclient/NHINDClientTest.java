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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import junit.framework.TestCase;

import org.nhindirect.DirectMessage;
import org.nhindirect.nhindclient.config.NHINDClientConfig;
import org.nhindirect.nhindclient.impl.NHINDClientImpl;

/**
 * 
 * @author vlewis
 */
public class NHINDClientTest extends TestCase
{

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
     * Test of sendRefferal method, of class NHINDClient.
     */
    public void testSendRefferal() throws Exception
    {
        System.out.println("sendRefferal");
        String endpoint = "vlewis@lewistower.com";
        String doc = getDoc();
        String meta = getMeta();
        ArrayList docs = new ArrayList();
        docs.add(doc);

        NHINDClientImpl instance = new NHINDClientImpl(NHINDClientConfig.DEFAULT);
        String messageId = UUID.randomUUID().toString();
        String expResult = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success";
        String result = instance.send(endpoint, meta, docs, messageId);
        assertEquals(expResult, result);
    }

//    public void testSendRefferal2() throws Exception
//    {
//        System.out.println("sendRefferal2");
//
//        String doc = getDoc();
//        String meta = getMeta();
//        ArrayList docs = new ArrayList();
//        docs.add(doc);
//
//        NHINDClientImpl instance = new NHINDClientImpl("gmail-smtp.l.google.com");
//        String messageId = UUID.randomUUID().toString();
//        String endpoint = "http://ELS4055:8080/xd/services/DocumentRepository_Service";
//        String result = instance.send(endpoint, meta, docs, messageId);
//        String expResult = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success";
//        assertEquals(expResult, result);
//    }

    private String getDoc() throws Exception
    {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("CCD.xml");
        byte[] theBytes = new byte[is.available()];
        is.read(theBytes);
        return new String(theBytes);
    }

    private String getMeta() throws Exception
    {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("meta.xml");
        byte[] theBytes = new byte[is.available()];
        is.read(theBytes);
        return new String(theBytes);
    }

    public void testSend() throws Exception
    {
        String sender = "lewistower1@gmail.com";
        Collection<String> receivers = Arrays.asList("beau+receiver@id84.com", "beau+receiver2@id84.com",
                "http://ELS4055:8080/xd/services/DocumentRepository_Service");
//        Collection<String> receivers = Arrays.asList("beau+receiver@nologs.org", "beau+receiver2@nologs.org");     

        DirectMessage message = new DirectMessage(sender, receivers);

        message.setBody("data is attached");
        message.addDocument(getDoc(), getMeta());
        message.addDocument(getDoc(), getMeta());

        NHINDClient client = new NHINDClientImpl(NHINDClientConfig.DEFAULT);
        client.send(message);
    }
}
