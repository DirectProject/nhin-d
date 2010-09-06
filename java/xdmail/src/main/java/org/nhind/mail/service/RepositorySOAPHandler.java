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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;


/**
 * This class handles the SOAP-Requests before they reach the
 * Web Service Operation. It is possible to read and manipulate
 * the SOAP-Message.
 *
 * @author Siegfried Bolz
 */
public class RepositorySOAPHandler implements SOAPHandler<SOAPMessageContext> {

    protected String endpoint;
    protected String messageId;
    protected String relatesTo;
    protected String action;
    protected String to;
    protected String remoteHost;
    protected String thisHost;
    protected String pid;
    protected String from;
    private static boolean first = true;

    /**
     * Class logger
     */
    private static final Logger LOGGER = Logger.getLogger(RepositorySOAPHandler.class.getPackage().getName());
    
    /**
     * Is called after constructing the handler and before executing any othe method.
     */
    @PostConstruct
    public void init() {
        if (first) {

            first = false;
         //   Properties properties = new Properties();
            try {
               // loadProperties("system.properties", properties);
               // Properties sysprop = System.getProperties();
               // sysprop.putAll(properties);
               // LOGGER.info(properties.toString());
            } catch (Exception exception) {
                LOGGER.info("Problem with properties file");
            }
        }
    }

    /* (non-Javadoc)
     * @see javax.xml.ws.handler.Handler#close(javax.xml.ws.handler.MessageContext)
     */
    @Override
    public void close(MessageContext messageContext) {
    }

    /**
     * Is executed before this handler is being destroyed -
     * means after close() has been executed.
     */
    @PreDestroy
    public void destroy() {
    }

    /**
     * This method handles the incoming and outgoing SOAP-Message.
     * It's an excellent point to manipulate the SOAP.
     *
     * @param SOAPMessageContext
     * @return boolean
     */
    @Override
    public boolean handleMessage(SOAPMessageContext context) {

        //Inquire incoming or outgoing message.
        boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        try {

            if (outbound) {
                getHeaderData();

                SOAPMessage msg = ((SOAPMessageContext) context).getMessage();
                //  dumpSOAPMessage(msg);

                SOAPPart sp = msg.getSOAPPart();

                //edit Envelope
                SOAPEnvelope env = sp.getEnvelope();
                SOAPHeader sh = env.addHeader();
                
                @SuppressWarnings("unused")
                SOAPBody sb = env.getBody();

                if (action != null) {
                    QName qname = new QName("http://www.w3.org/2005/08/addressing", "Action");
                    SOAPHeaderElement saction = sh.addHeaderElement(qname);
                    boolean must = true;

                    saction.setMustUnderstand(must);
                    saction.setValue(action);
                }
                if (relatesTo != null) {
                    QName qname = new QName("http://www.w3.org/2005/08/addressing", "RelatesTo");
                    SOAPHeaderElement relates = sh.addHeaderElement(qname);
                    relates.setValue(relatesTo);
                }
                if (from != null) {
                    QName qname = new QName("http://www.w3.org/2005/08/addressing", "From");
                    QName child = new QName("http://www.w3.org/2005/08/addressing", "Address");
                    SOAPHeaderElement efrom = sh.addHeaderElement(qname);
                    SOAPElement address = efrom.addChildElement(child);
                    address.setValue(from);

                }

                if (messageId != null) {
                    QName qname = new QName("http://www.w3.org/2005/08/addressing", "MessageID");
                    SOAPHeaderElement message = sh.addHeaderElement(qname);
                    message.setValue(messageId);
                }
                if (to != null) {
                    QName qname = new QName("http://www.w3.org/2005/08/addressing", "To");
                    SOAPHeaderElement sto = sh.addHeaderElement(qname);
                    sto.setValue(to);
                }


            } else {
               //should not be any inbound


            }

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param fileName
     * @param properties
     * @throws IOException
     */
    @SuppressWarnings("unused")
    private void loadProperties(String fileName, Properties properties) throws IOException {

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("META-INF/" + fileName);
        properties.load(inputStream);
    }

    /**
     * Returns the <code>Set</code> of supported SOAP headers
     */
    @Override
    public Set<QName> getHeaders() {
        Set<QName> set = new HashSet<QName>();
        
        QName qname = new QName("http://www.w3.org/2005/08/addressing", "Action");
        set.add(qname);
        
        qname = new QName("http://www.w3.org/2005/08/addressing", "To");
        set.add(qname);
        
        qname = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security");
        set.add(qname);
        
        return set;
    }

    /**
     * Returns the message encoding (e.g. utf-8)
     *
     * @param msg
     * @return
     * @throws javax.xml.soap.SOAPException
     */
    protected String getMessageEncoding(SOAPMessage msg) throws SOAPException {
        String encoding = "utf-8";
        if (msg.getProperty(SOAPMessage.CHARACTER_SET_ENCODING) != null) {
            encoding = msg.getProperty(SOAPMessage.CHARACTER_SET_ENCODING).toString();
        }
        return encoding;
    }

    /**
     * Dump SOAP Message to console
     *
     * @param msg
     */
    protected void dumpSOAPMessage(SOAPMessage msg) {
        if (msg == null) {
            LOGGER.info("SOAP Message is null");
            return;
        }
        LOGGER.info("");
        LOGGER.info("--------------------");
        LOGGER.info(" DUMP OF SOAP MESSAGE");
        LOGGER.info("--------------------");
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            msg.writeTo(baos);
            LOGGER.info(baos.toString(getMessageEncoding(msg)));

            // show included values
            String values = msg.getSOAPBody().getTextContent();
            LOGGER.info("Included values:" + values);
        } catch (SOAPException e) {
            LOGGER.info("Unable to dump soap message");
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.info("Unable to dump soap message");
            e.printStackTrace();
        }
    }

    /**
     * Handles SOAP-Errors.
     *
     * @param context
     * @return
     */
    @Override
    public boolean handleFault(SOAPMessageContext context) {
        LOGGER.info("ServerSOAPHandler.handleFault");
        boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outbound) {
            LOGGER.info("Direction=outbound (handleFault)");
        } else {
            LOGGER.info("Direction=inbound (handleFault)");
        }

        try {
            @SuppressWarnings("unused")
            SOAPMessage msg = ((SOAPMessageContext) context).getMessage();
            
            if (context.getMessage().getSOAPBody().getFault() != null) {
                String detailName = null;
                try {
                    detailName = context.getMessage().getSOAPBody().getFault().getDetail().getFirstChild().getLocalName();
                    LOGGER.info("detailName=" + detailName);
                } catch (Exception e) {
                }
            }
        } catch (SOAPException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * @param faultString
     * @param clientFault
     * @return
     */
    @SuppressWarnings("unused")
    private SOAPFaultException createSOAPFaultException(String faultString,
            Boolean clientFault) {
        try {
            String faultCode = clientFault ? "Client" : "Server";
            SOAPFault fault = SOAPFactory.newInstance().createFault();
            fault.setFaultString(faultString);
            fault.setFaultCode(new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE, faultCode));
            return new SOAPFaultException(fault);
        } catch (SOAPException e) {
            throw new RuntimeException("Error creating SOAP Fault message, faultString: " + faultString);
        }

    }

    /**
     * Extract header values from a ThreadData object.
     */
    protected void getHeaderData() {
        Long threadId = Long.valueOf(Thread.currentThread().getId());
        LOGGER.fine("GTHREAD ID " + threadId);

        ThreadData threadData = new ThreadData(threadId);
        messageId = threadData.getMessageId();
        to = threadData.getTo();
        relatesTo = threadData.getRelatesTo();
        action = threadData.getAction();
        thisHost = threadData.getThisHost();
        remoteHost = threadData.getRemoteHost();
        pid = threadData.getPid();
        from = threadData.getFrom();
    }

    /**
     * Build a ThreadData object with header information.
     */
    protected void setHeaderData() {
        Long threadId = Long.valueOf(Thread.currentThread().getId());
        LOGGER.fine("GTHREAD ID " + threadId);
        
        ThreadData threadData = new ThreadData(threadId);
        threadData.setReplyAddress(endpoint);
        threadData.setMessageId(messageId);
        threadData.setAction(action);
        threadData.setThisHost(thisHost);
        threadData.setRemoteHost(remoteHost);
        threadData.setTo(to);
        threadData.setPid(pid);
        threadData.setFrom(from);
    }

    /**
     * Get the current process ID.
     * 
     * @return the current process ID
     */
    public String getPID() {
        String processName =
                ManagementFactory.getRuntimeMXBean().getName();
        return processName.split("@")[0];
    }
} // .EOF

