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
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType.Document;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Resource;
import javax.mail.util.ByteArrayDataSource;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

import org.apache.commons.lang.StringUtils;
import org.nhind.xdm.MailClient;
import org.nhind.xdm.impl.SmtpMailClient;
import org.nhindirect.xd.common.DirectDocuments;
import org.nhindirect.xd.common.DirectMessage;
import org.nhindirect.xd.proxy.DocumentRepositoryProxy;
import org.nhindirect.xd.proxy.ThreadData;
import org.nhindirect.xd.routing.RoutingResolver;
import org.nhindirect.xd.routing.impl.RoutingResolverImpl;
import org.nhindirect.xd.transform.XdsDirectDocumentsTransformer;
import org.nhindirect.xd.transform.impl.DefaultXdsDirectDocumentsTransformer;

import com.gsihealth.auditclient.AuditMessageGenerator;
import com.gsihealth.auditclient.type.AuditMethodEnum;

/**
 * Base class for handling incoming XDR requests.
 * 
 * @author Vince
 */
public abstract class DocumentRepositoryAbstract 
{
    @Resource
    protected WebServiceContext context;
    
    protected String endpoint = null;
    protected String messageId = null;
    protected String relatesTo = null;
    protected String action = null;
    protected String to = null;
    
    protected String directTo = null;
    protected String directFrom = null;
    
    private String thisHost = null;
    private String remoteHost = null;
    private String pid = null;
    private String from = null;
    private String suffix = null;
    private String replyEmail = null;

    private static final String PARAM_MAIL_HOST = "mailHost";
    private static final String PARAM_MAIL_USER = "mailUser";
    private static final String PARAM_MAIL_PASS = "mailPass";
    private static final String PARAM_AUDIT_METHOD = "auditMethod";
    private static final String PARAM_AUDIT_HOST = "auditHost";
    private static final String PARAM_AUDIT_PORT = "auditPort";
    private static final String PARAM_AUDIT_FILE = "auditFile";
    private static final String PARAM_CONFIG_SERVICE = "configService";
    
    private RoutingResolver resolver = null;
    
    private AuditMessageGenerator auditMessageGenerator = null;
    
    private MailClient mailClient = null;
    
    private XdsDirectDocumentsTransformer xdsDirectDocumentsTransformer = new DefaultXdsDirectDocumentsTransformer();

    private static final Logger LOGGER = Logger.getLogger(DocumentRepositoryAbstract.class.getPackage().getName());

    /**
     * Handle an incoming XDR request with a
     * ProvideAndRegisterDocumentSetRequestType object.
     * 
     * @param body
     *            The ProvideAndRegisterDocumentSetRequestType object
     *            representing an XDR message
     * @return a RegistryResponseType object
     */
    public abstract RegistryResponseType documentRepositoryProvideAndRegisterDocumentSetB(ProvideAndRegisterDocumentSetRequestType body);

    /**
     * Handle an incoming XDR request with a RetrieveDocumentSetRequestType
     * object
     * 
     * @param body
     *            The RetrieveDocumentSetRequestType object representing an XDR
     *            message
     * @return a RetrieveDocumentSetRequestType object
     */
    public abstract RetrieveDocumentSetResponseType documentRepositoryRetrieveDocumentSet(RetrieveDocumentSetRequestType body);

    /**
     * Handle an incoming ProvideAndRegisterDocumentSetRequestType object and
     * transform to XDM or relay to another XDR endponit.
     * 
     * @param prdst
     *            The incoming ProvideAndRegisterDocumentSetRequestType object
     * @return a RegistryResponseType object
     * @throws Exception
     */
    protected RegistryResponseType provideAndRegisterDocumentSet(ProvideAndRegisterDocumentSetRequestType prdst) throws Exception 
    {
        RegistryResponseType resp = null;
        
        try 
        {
            getHeaderData();
            @SuppressWarnings("unused")
            InitialContext ctx = new InitialContext();

            DirectDocuments documents = xdsDirectDocumentsTransformer.transform(prdst);
            
            // Get endpoints
            List<String> forwards = new ArrayList<String>();
            for (String recipient : documents.getSubmissionSet().getIntendedRecipient()) 
            {
                String address = StringUtils.remove(recipient, "|");
                forwards.add(StringUtils.splitPreserveAllTokens(address, "^")[0]);
            }

            messageId = UUID.randomUUID().toString();
            
            // TODO patID and subsetId
            String patId = "PATID TBD";
            String subsetId = "SUBSETID";
            getAuditMessageGenerator().provideAndRegisterAudit( messageId, remoteHost, endpoint, to, thisHost, patId, subsetId, pid);

            // Send to SMTP endpoints
            if (getResolver().hasSmtpEndpoints(forwards)) 
            {
                // Get a reply address
                replyEmail = documents.getSubmissionSet().getAuthorPerson();
                replyEmail = StringUtils.splitPreserveAllTokens(replyEmail, "^")[0];
                replyEmail = StringUtils.contains(replyEmail, "@") ? replyEmail : "nhindirect@nhindirect.org";

                LOGGER.info("SENDING EMAIL TO " + getResolver().getSmtpEndpoints(forwards) + " with message id "
                        + messageId);

                // Construct message wrapper
                DirectMessage message = new DirectMessage(replyEmail, getResolver().getSmtpEndpoints(forwards));
                message.setSubject("data");
                message.setBody("data attached");
                message.setDirectDocuments(documents);

                // Send mail
                MailClient mailClient = getMailClient();
                mailClient.mail(message, messageId, suffix);
            }

            // Send to XD endpoints
            for (String reqEndpoint : getResolver().getXdEndpoints(forwards)) 
            {
                String endpointUrl = getResolver().resolve(reqEndpoint);
                
                String to = StringUtils.remove(endpointUrl, "?wsdl");

                Long threadId = new Long(Thread.currentThread().getId());
                LOGGER.info("THREAD ID " + threadId);
                ThreadData threadData = new ThreadData(threadId);
                threadData.setTo(to);

                List<Document> docs = prdst.getDocument();
                Document oldDoc = docs.get(0);
                docs.clear();
                Document doc = new Document();
                doc.setId(oldDoc.getId());

                DataHandler dh = oldDoc.getValue();
                ByteArrayOutputStream buffOS = new ByteArrayOutputStream();
                dh.writeTo(buffOS);
                byte[] buff = buffOS.toByteArray();

                DataSource source = new ByteArrayDataSource(buff, "application/xml; charset=UTF-8");
                DataHandler dhnew = new DataHandler(source);
                doc.setValue(dhnew);

                docs.add(doc);

                LOGGER.info(" SENDING TO ENDPOINT " + to);

                DocumentRepositoryProxy proxy = new DocumentRepositoryProxy(endpointUrl, new RepositoryHandlerResolver());
                
                RegistryResponseType rrt = proxy.provideAndRegisterDocumentSetB(prdst);
                String test = rrt.getStatus();
                if (test.indexOf("Failure") >= 0) 
                {
                    throw new Exception("Failure Returned from XDR forward");
                }
                
                getAuditMessageGenerator().provideAndRegisterAuditSource( messageId, remoteHost, endpoint, to, thisHost, patId, subsetId, pid);
            }

            resp = getRepositoryProvideResponse(messageId);

            relatesTo = messageId;
            action = "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-bResponse";
            to = endpoint;

            setHeaderData();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            throw (e);
        }

        return resp;
    }

    /**
     * Create a RegistryResponseType object.
     * 
     * @param messageId
     *            The message ID
     * @return a RegistryResponseType object
     * @throws Exception
     */
    protected RegistryResponseType getRepositoryProvideResponse(String messageId) throws Exception {
        RegistryResponseType rrt = null;
        try { // Call Web Service Operation

            String status = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success";  // TODO initialize WS operation arguments here

            try {

                rrt = new RegistryResponseType();
                rrt.setStatus(status);


            } catch (Exception ex) {
                LOGGER.info("not sure what this ");
                ex.printStackTrace();
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return rrt;
    }

    private RoutingResolver getResolver()
    {
        if (resolver == null)
        {
            String configService = null;
            
            try
            {
                configService = getServletContext().getInitParameter(PARAM_CONFIG_SERVICE);
            }
            catch (Exception x)
            {
                // eat it
            }
            
            if (StringUtils.isNotBlank(configService))
            {
                try
                {
                    resolver = new RoutingResolverImpl(configService);
                }
                catch (Exception e)
                {
                    LOGGER.warning("Unable to create resolver from URL, falling back to default");
                    resolver = new RoutingResolverImpl();
                }
            }
            else
            {
                resolver = new RoutingResolverImpl();
            }
        }

        return resolver;
    }
    
    private ServletContext getServletContext()
    {
        return (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
    }

    private AuditMessageGenerator getAuditMessageGenerator()
    {
        if (auditMessageGenerator == null)
        {
            String auditMethod = getServletContext().getInitParameter(PARAM_AUDIT_METHOD);

            if (StringUtils.equals(auditMethod, AuditMethodEnum.SYSLOG.getMethod()))
            {
                String auditHost = getServletContext().getInitParameter(PARAM_AUDIT_HOST);
                String auditPort = getServletContext().getInitParameter(PARAM_AUDIT_PORT);

                auditMessageGenerator = new AuditMessageGenerator(auditHost, auditPort);
            }
            else if (StringUtils.equals(auditMethod, AuditMethodEnum.FILE.getMethod()))
            {
                String fileName = getServletContext().getInitParameter(PARAM_AUDIT_FILE);

                auditMessageGenerator = new AuditMessageGenerator(fileName);
            }
            else
            {
                throw new IllegalArgumentException("Unknown audit method.");
            }
        }
        
        return auditMessageGenerator;
    }
    
    private MailClient getMailClient()
    {
        if (mailClient == null)
        {
            String hostname = getServletContext().getInitParameter(PARAM_MAIL_HOST);
            String username = getServletContext().getInitParameter(PARAM_MAIL_USER);
            String password = getServletContext().getInitParameter(PARAM_MAIL_PASS);

            mailClient = new SmtpMailClient(hostname, username, password);
        }

        return mailClient;
    }
    
    /**
     * Set the value of mailClient.
     * 
     * @param mailClient
     *            the value of mailClient.
     */
    public void setMailClient(MailClient mailClient)
    {
        this.mailClient = mailClient; 
    }
    
    /**
     * Set the value of auditMessageGenerator.
     * 
     * @param auditMessageGenerator
     *            the value of auditMessageGenerator.
     */
    public void setAuditMessageGenerator(AuditMessageGenerator auditMessageGenerator)
    {
        this.auditMessageGenerator = auditMessageGenerator;
    }
    
    /**
     * Set the value of resolver.
     * 
     * @param resolver
     *            the value of resolver.
     */
    public void setResolver(RoutingResolver resolver)
    {
        this.resolver = resolver;
    }

    /**
     * Extract header values from a ThreadData object.
     */
    protected void getHeaderData() {
        Long threadId = new Long(Thread.currentThread().getId());
        LOGGER.info("DTHREAD ID " + threadId);

        ThreadData threadData = new ThreadData(threadId);
        this.endpoint = threadData.getReplyAddress();
        this.messageId = threadData.getMessageId();
        this.to = threadData.getTo();
        this.thisHost = threadData.getThisHost();
        this.remoteHost = threadData.getRemoteHost();
        this.pid = threadData.getPid();
        this.action = threadData.getAction();
        this.from = threadData.getFrom();
        
        this.directTo = threadData.getDirectTo();
        this.directFrom = threadData.getDirectFrom();

        LOGGER.info(threadData.toString());
    }

    /**
     * Build a ThreadData object with header information.
     */
    protected void setHeaderData() {
        Long threadId = new Long(Thread.currentThread().getId());
        LOGGER.info("THREAD ID " + threadId);

        ThreadData threadData = new ThreadData(threadId);
        threadData.setTo(this.to);
        threadData.setMessageId(this.messageId);
        threadData.setRelatesTo(this.relatesTo);
        threadData.setAction(this.action);
        threadData.setThisHost(this.thisHost);
        threadData.setRemoteHost(this.remoteHost);
        threadData.setPid(this.pid);
        threadData.setFrom(this.from);
        
        threadData.setDirectTo(this.directTo);
        threadData.setDirectFrom(this.directFrom);

        LOGGER.info(threadData.toString());
    }
}
