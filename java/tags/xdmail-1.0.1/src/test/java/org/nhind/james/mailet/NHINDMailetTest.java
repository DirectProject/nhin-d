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

package org.nhind.james.mailet;

import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import junit.framework.TestCase;

import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.MailetConfig;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit3.JUnit3Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.nhind.mail.service.DocumentRepository;
import org.nhind.testutils.MockMailetConfig;
import org.nhindirect.xd.routing.RoutingResolver;
import org.nhindirect.xd.transform.MimeXdsTransformer;
import org.nhindirect.xd.transform.impl.DefaultMimeXdsTransformer;


/**
 * Test class for methods in the NHINDMailet class.
 * 
 * @author beau
 */
public class NHINDMailetTest extends TestCase {

    /**
     * Constructor.
     * 
     * @param testName
     *            The test name.
     */
    public NHINDMailetTest(String testName) {
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
     * Test the init method.
     */
    public void testInit() {
        final String endpointUrl = "http://www.endpoint.url/";

        DirectXdMailet mailet = new DirectXdMailet();

        Map<String, String> params = new HashMap<String, String>();
        params.put("EndpointURL", endpointUrl);
        MailetConfig mailetConfig = new MockMailetConfig(params, "MailetName");

        try {
            mailet.init(mailetConfig);
        } catch (MessagingException e) {
            fail("Test setup failed");
        }

        try {
            mailet.init();
            assertEquals("EndpointURL value does not match expected", endpointUrl, mailet.getEndpointUrl());
        } catch (MessagingException e) {
            fail("Exception thrown");
        }

        try {
            params.clear();
            params.put("EndpointURL", "");
            mailet.init();
            fail("Exception not thrown");
        } catch (MessagingException e) {
            assertTrue(true);
        }

        try {
            params.clear();
            mailet.init();
            fail("Exception not thrown");
        } catch (MessagingException e) {
            assertTrue(true);
        }
    }

    /**
     * Test the getMimeXDSTransformer and setMimeXDSTransformer methods.
     */
    public void testGetSetMimeXDSTransformer() {
        DirectXdMailet mailet = new DirectXdMailet();
        MimeXdsTransformer transformer = null;

        transformer = mailet.getMimeXDSTransformer();
        assertTrue("MimeXDSTransformer is null", transformer != null);

        transformer = new DefaultMimeXdsTransformer();
        mailet.setMimeXDSTransformer(transformer);
        MimeXdsTransformer output = mailet.getMimeXDSTransformer();
        assertTrue("Output references a different object", transformer == output);
    }
    
    // TODO: Come back and redo this unit test to support the new method
    
//    /**
//     * Tests the service method using mock objects.
//     * 
//     * @throws Exception
//     */
//    public void testService() throws Exception {
//        Mockery context = new JUnit3Mockery() {{
//            setImposteriser(ClassImposteriser.INSTANCE);
//        }};
//        
//        final MimeXdsTransformer mimeXDSTransformer = context.mock(MimeXdsTransformer.class);
//        final DocumentRepository forwardXds = context.mock(DocumentRepository.class);
//        final RoutingResolver resolver = context.mock(RoutingResolver.class);
//
//        final Session session = null;
//        final MimeMessage message = new MimeMessage(session);
//        final Mail mail = new MockMail(message);
//
//        final String endpointUrl = "endpointUrl";
//        final ProvideAndRegisterDocumentSetRequestType prds = new ProvideAndRegisterDocumentSetRequestType();
//        
//        final List<ProvideAndRegisterDocumentSetRequestType> prdsList = new ArrayList<ProvideAndRegisterDocumentSetRequestType>();
//        prdsList.add(prds);
//        
//        final String response = "success";
//        
//        final RuntimeException e = new RuntimeException();
//
//        NHINDMailet mailet = new NHINDMailet();
//        mailet.setMimeXDSTransformer(mimeXDSTransformer);
//        mailet.setDocumentRepository(forwardXds);
//        mailet.setResolver(resolver);
//        
//        // expectations
//        context.checking(new Expectations() {
//            {
//                oneOf(mimeXDSTransformer).transform(message);
//                will(returnValue(prdsList));
//                oneOf(forwardXds).forwardRequest(endpointUrl, prds);
//                will(returnValue(response));
//                
//                oneOf(mimeXDSTransformer).transform(message);
//                will(throwException(e));                
//            }
//        });
//
//        try {
//            mailet.setEndpointUrl(null);
//            mailet.service(mail);
//            fail("Exception not thrown");
//        } catch (MessagingException ex) {
//            assertTrue(true);
//        }
//        
//        mailet.setEndpointUrl(endpointUrl);
//        mailet.service(mail);
//        
//        try {
//            mailet.service(mail);        
//            fail("Exception now thrown");
//        } catch (RuntimeException ex) {
//            assertTrue(true);
//        }
//    }
    
    /**
     * Mock mail class.
     * 
     * @author beau
     */
    @SuppressWarnings("serial")
    private class MockMail implements Mail 
    {
        private MimeMessage mimeMessage;
        
        public MockMail(MimeMessage mimeMessage) {
            this.mimeMessage = mimeMessage;
        }
        
        public Serializable getAttribute(String arg0) 
        {
            return null;
        }

        @SuppressWarnings("unchecked")
        public Iterator getAttributeNames() {
            return null;
        }

        public String getErrorMessage() {
            return null;
        }

        public Date getLastUpdated() {
            return null;
        }

        public MimeMessage getMessage() throws MessagingException 
        {
            return this.mimeMessage;
        }

        public long getMessageSize() throws MessagingException {
            return 0;
        }

        public String getName() {
            return null;
        }

        @SuppressWarnings("unchecked")
        public Collection getRecipients() 
        {
            return Arrays.asList("xd@address.com", "smtp@address.com");
        }

        public String getRemoteAddr() 
        {
            return null;
        }

        public String getRemoteHost() {
            return null;
        }

        public MailAddress getSender() 
        {
            return null;
        }

        public String getState() 
        {
            return null;
        }

        public boolean hasAttributes() {
            return false;
        }

        public void removeAllAttributes() {
        }

        public Serializable removeAttribute(String arg0) {
            return null;
        }

        public Serializable setAttribute(String arg0, Serializable arg1) {
            return null;
        }

        public void setErrorMessage(String arg0) {
        }

        public void setLastUpdated(Date arg0) {
        }

        public void setMessage(MimeMessage msg) 
        {
            this.mimeMessage = msg;
        }

        public void setName(String arg0) {
        }

        @SuppressWarnings("unchecked")
        public void setRecipients(Collection arg0) 
        {
        }

        public void setState(String state) 
        {
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            throw new CloneNotSupportedException();
        }
    }

}
