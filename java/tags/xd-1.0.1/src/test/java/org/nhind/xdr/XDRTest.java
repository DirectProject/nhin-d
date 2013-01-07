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

import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType;
import ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;
import java.util.logging.Logger;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import junit.framework.TestCase;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

import org.nhind.xdr.config.XdConfig;
import org.nhindirect.xd.routing.impl.RoutingResolverImpl;
import org.nhindirect.xd.transform.util.XmlUtils;

import com.gsihealth.auditclient.AuditMessageGenerator;

/**
 *
 * @author vlewis
 */
public class XDRTest extends TestCase 
{
    private static final Logger LOGGER = Logger.getLogger(XDRTest.class.getPackage().getName());
    
    /**
     * Constructor
     * 
     * @param testName The test name
     */
    public XDRTest(String testName) {
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
     * Test of documentRepositoryProvideAndRegisterDocumentSetB method, of class XDR.
     */
    public void testDocumentRepositoryProvideAndRegisterDocumentSetB() throws Exception {
        System.out.println("documentRepositoryProvideAndRegisterDocumentSetB");
        QName qname = new QName("urn:ihe:iti:xds-b:2007", "ProvideAndRegisterDocumentSetRequestType");
        ProvideAndRegisterDocumentSetRequestType body = null;
        try {
            String request = getTestRequest();
            JAXBElement jb = (JAXBElement) XmlUtils.unmarshal(request, ihe.iti.xds_b._2007.ObjectFactory.class);
            body = (ProvideAndRegisterDocumentSetRequestType) jb.getValue();
        } catch (Exception x) {
            x.printStackTrace();
            fail("Failed unmarshalling request");
        }
        
        DocumentRepositoryAbstract instance = new XDR();
        
        // Set test objects
        instance.setAuditMessageGenerator(new AuditMessageGenerator(getLogfile()));
        // instance.setMailClient(new SmtpMailClient("gmail-smtp.l.google.com", "lewistower1@gmail.com", "hadron106"));
        instance.setResolver(new RoutingResolverImpl());
        
        XdConfig config = new XdConfig();
        config.setMailHost("gmail-smtp.l.google.com");
        config.setMailUser("lewistower1@gmail.com");
        config.setMailPass("hadron106");
        
        instance.setConfig(config);

        RegistryResponseType result = instance.documentRepositoryProvideAndRegisterDocumentSetB(body);


        if (result.getStatus().contains("Failure"))
        {
        	// some organizational firewalls may block this test, so bail out gracefully if that happens
        	return;
        }
        
        String sresult = null;

        try {
            qname = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0", "RegistryResponseType");

            sresult = XmlUtils.marshal(qname, result, oasis.names.tc.ebxml_regrep.xsd.rs._3.ObjectFactory.class);
        } catch (Exception x) {
            x.printStackTrace();
            fail("Failed unmarshalling response");
        }

        // System.out.println(sresult);
        assertTrue(sresult.indexOf("ResponseStatusType:Success") >= 0);

    }

    /**
     * Test the documentRepositoryRetrieveDocumentSet method.
     */
    public void testDocumentRepositoryRetrieveDocumentSet() throws Exception {
        try {
            DocumentRepositoryAbstract instance = new XDR();

            // Set test objects
            instance.setAuditMessageGenerator(new AuditMessageGenerator(getLogfile()));
            // instance.setMailClient(new SmtpMailClient("gmail-smtp.l.google.com", "lewistower1@gmail.com", "hadron106"));
            
            XdConfig config = new XdConfig();
            config.setMailHost("gmail-smtp.l.google.com");
            config.setMailUser("lewistower1@gmail.com");
            config.setMailPass("hadron106");
            
            instance.setConfig(config);
            
            @SuppressWarnings("unused")
            RetrieveDocumentSetResponseType response = null;
            RetrieveDocumentSetRequestType body = new RetrieveDocumentSetRequestType();

            response = instance.documentRepositoryRetrieveDocumentSet(body);
            fail("Exception not thrown");
        } catch (UnsupportedOperationException e) {
            assertTrue(true);
        }
    }
    
    /**
     * Return the test request.xml as a string.
     * 
     * @return the test request.xml as a string
     * @throws Exception
     */
    private String getTestRequest() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/request.xml");
        byte[] theBytes = new byte[is.available()];
        is.read(theBytes);
        return new String(theBytes);

    }
    
    private String getLogfile() throws Exception
    {
        String file = File.createTempFile("xdaudit." + UUID.randomUUID().toString(), ".log").getAbsolutePath();
        
        LOGGER.info("Logging to file : " + file);
        
        return file;
    }
}
