/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.nhindirect.nhindclient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;
import junit.framework.TestCase;

/**
 *
 * @author vlewis
 */
public class NHINDClientTest extends TestCase {
    
    public NHINDClientTest(String testName) {
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
     * Test of sendRefferal method, of class NHINDClient.
     */
    public void testSendRefferal() throws Exception {
        System.out.println("sendRefferal");
        String endpoint = "vlewis@lewistower.com";
        String doc = getDoc();
        String meta = getMeta();
        ArrayList docs = new ArrayList();
        docs.add(doc);
       
        NHINDClient instance = new NHINDClient();
        String messageId = UUID.randomUUID().toString();
        String expResult = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success";
        String result = instance.sendRefferal(endpoint, meta, docs, messageId);
        assertEquals(expResult, result);
       
      
    }

      public void testSendRefferal2() throws Exception {
        System.out.println("sendRefferal2");

        String doc = getDoc();
        String meta = getMeta();
        ArrayList docs = new ArrayList();
        docs.add(doc);

        NHINDClient instance = new NHINDClient();
        String messageId = UUID.randomUUID().toString();
        String endpoint = "http://localhost:8080/xd/services/DocumentRepository_Service";
        String result = instance.sendRefferal(endpoint, meta, docs, messageId);
        String expResult = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success";
        assertEquals(expResult, result);

    }

        private String getDoc() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/META-INF/main/resources/CCD.xml");
        byte[] theBytes = new byte[is.available()];
        is.read(theBytes);
        return new String(theBytes);

    }

    private String getMeta() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/META-INF/main/resources/meta.xml");
        byte[] theBytes = new byte[is.available()];
        is.read(theBytes);
        return new String(theBytes);

    }

}
