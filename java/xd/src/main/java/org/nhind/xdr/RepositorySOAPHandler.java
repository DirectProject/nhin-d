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

import java.io.ByteArrayOutputStream;
import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletRequest;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.commons.lang.StringUtils;
import org.nhindirect.xd.proxy.ThreadData;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class handles the SOAP-Requests before they reach the Web Service
 * Operation. It is possible to read and manipulate the SOAP-Message.
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
    
    protected String directFrom;
    protected String directTo;

    /**
     * Class logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RepositorySOAPHandler.class.getPackage().getName());
    
    /**
     * Is called after constructing the handler and before executing any othe
     * method.
     */
    @PostConstruct
    public void init() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.xml.ws.handler.Handler#close(javax.xml.ws.handler.MessageContext)
     */
    @Override
    public void close(MessageContext messageContext) {
    }

    /**
     * Is executed before this handler is being destroyed - means after close()
     * has been executed.
     */
    @PreDestroy
    public void destroy() {
    }

    /**
     * This method handles the incoming and outgoing SOAP-Message. It's an
     * excellent point to manipulate the SOAP.
     * 
     * @param SOAPMessageContext
     *            The SOAPMessageContext object.
     * @return true for successful handling, false otherwise.
     */
    @Override
    public boolean handleMessage(SOAPMessageContext context) {

        //Inquire incoming or outgoing message.
        boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        try {

            if (outbound) {
                getHeaderData();

                SOAPMessage msg = ((SOAPMessageContext) context).getMessage();
                // dumpSOAPMessage(msg);

                SOAPPart sp = msg.getSOAPPart();

                // edit Envelope
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
                
                if (StringUtils.isNotBlank(directFrom) && StringUtils.isNotBlank(directTo))
                {
                    SOAPHeaderElement directHeader = sh.addHeaderElement(new QName("urn:direct:addressing", "addressBlock"));
                    directHeader.setPrefix("direct");
                    directHeader.setRole("urn:direct:addressing:destination");
                    directHeader.setRelay(true);

                    SOAPElement directFromElement = directHeader.addChildElement(new QName("from"));
                    directFromElement.setPrefix("direct");
                    directFromElement.setValue(StringUtils.contains(directFrom, "mailto:") ? directFrom : "mailto:" + directFrom);

                    SOAPElement directToElement = directHeader.addChildElement(new QName("to"));
                    directToElement.setPrefix("direct");
                    directToElement.setValue(StringUtils.contains(directTo, "mailto:") ? directTo : "mailto:" + directTo);
                }

            } else {
                // INBOUND
                messageId = null;
                action = null;
                endpoint = null;
                from = null;
                to = null;
                remoteHost = null;
                thisHost = null;
                pid = null;
                relatesTo = null;
                
                directFrom = null;
                directTo = null;
             
                LOGGER.info("Direction=inbound (handleMessage)");

                SOAPMessage msg = ((SOAPMessageContext) context).getMessage();
                dumpSOAPMessage(msg);

                ServletRequest sr = (ServletRequest) context.get(MessageContext.SERVLET_REQUEST);
                if (sr != null) {
                    remoteHost = sr.getRemoteHost();
                    thisHost = sr.getServerName();
                    pid = getPID();
                }

                SOAPPart sp = msg.getSOAPPart();

                //edit Envelope
                SOAPEnvelope env = sp.getEnvelope();
                SOAPHeader sh = env.getHeader();
                
                @SuppressWarnings("unchecked")
                Iterator<Node> it = sh.extractAllHeaderElements();
                while (it.hasNext())
                {
                    Node header = it.next();
                    LOGGER.info(header.getNodeName());

                    if (StringUtils.contains(header.toString(), "MessageID"))
                    {
                        messageId = header.getTextContent();
                        LOGGER.info(messageId);
                    }
                    else if (StringUtils.contains(header.toString(), "Action"))
                    {
                        action = header.getTextContent();
                        LOGGER.info(action);
                    }
                    else if (StringUtils.contains(header.toString(), "RelatesTo"))
                    {
                        relatesTo = header.getTextContent();
                        LOGGER.info(action);
                    }
                    else if (StringUtils.contains(header.toString(), "ReplyTo"))
                    {
                        NodeList reps = header.getChildNodes();
                        for (int i = 0; i < reps.getLength(); i++)
                        {
                            Node address = reps.item(i);
                            LOGGER.info(address.getNodeName());
                            if (StringUtils.contains(address.getNodeName(), "Address"))
                            {
                                endpoint = address.getTextContent();
                                LOGGER.info(endpoint);
                            }
                        }
                    }
                    else if (StringUtils.contains(header.toString(), "From"))
                    {
                        NodeList reps = header.getChildNodes();
                        for (int i = 0; i < reps.getLength(); i++)
                        {
                            Node address = reps.item(i);
                            LOGGER.info(address.getNodeName());
                            if (StringUtils.contains(address.getNodeName(), "Address"))
                            {
                                from = address.getTextContent();
                                LOGGER.info(from);
                            }
                        }
                    }
                    else if (StringUtils.contains(header.toString(), "To")) // must be after ReplyTo
                    {
                        to = header.getTextContent();
                        LOGGER.info(to);
                    }
                    else if (StringUtils.contains(header.toString(), "addressBlock"))
                    {
                        NodeList childNodes = header.getChildNodes();
                        
                        for (int i = 0; i < childNodes.getLength(); i++)
                        {
                            Node node = childNodes.item(i);
                            LOGGER.info(node.getNodeName());
                            
                            if (StringUtils.contains(node.getNodeName(), "from"))
                            {
                                directFrom = node.getTextContent();
                                LOGGER.info(directFrom);
                            }
                            else if (StringUtils.contains(node.getNodeName(), "to"));
                            {
                                directTo = node.getTextContent();
                                LOGGER.info(directTo);
                            }
                        }
                    }
                }
                
                setHeaderData();
            }

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Returns the <code>Set</code> of supported SOAP headers.
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
     * Returns the message encoding (e.g. utf-8).
     * 
     * @param msg
     *            The SOAPMessage object.
     * @return the message encoding.
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
     * Dump SOAP Message to console.
     * 
     * @param msg
     *            The SOAPMessage object.
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles SOAP-Errors.
     * 
     * @param context
     *            The SOAPMessageContext object.
     * @return true for successful fault handling, false otherwise.
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
            // dumpSOAPMessage(msg);
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
     * Extract header values from a ThreadData object.
     */
    protected void getHeaderData() {
        Long threadId = new Long(Thread.currentThread().getId());
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
        
        directFrom = threadData.getDirectFrom();
        directTo = threadData.getDirectTo();
    }

    /**
     * Build a ThreadData object with header information.
     */
    protected void setHeaderData() {
        Long threadId = new Long(Thread.currentThread().getId());
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
        
        threadData.setDirectFrom(directFrom);
        threadData.setDirectTo(directTo);
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
}