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
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.nhind.xdr.config.XdConfig;
import org.nhindirect.xd.common.DirectDocuments;
import org.nhindirect.xd.common.DirectMessage;
import org.nhindirect.xd.proxy.DocumentRepositoryProxy;
import org.nhindirect.xd.routing.RoutingResolver;
import org.nhindirect.xd.routing.impl.RoutingResolverImpl;
import org.nhindirect.xd.soap.DirectSOAPHandlerResolver;
import org.nhindirect.xd.soap.ThreadData;
import org.nhindirect.xd.transform.XdsDirectDocumentsTransformer;
import org.nhindirect.xd.transform.impl.DefaultXdsDirectDocumentsTransformer;

import com.gsihealth.auditclient.AuditMessageGenerator;
import com.gsihealth.auditclient.type.AuditMethodEnum;
import org.nhindirect.xd.transform.parse.ParserHL7;
import org.nhindirect.xd.soap.SafeThreadData;


/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Vincent Lewis     vincent.lewis@gsihealth.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/
public abstract class DocumentRepositoryAbstract 
{
    @Resource
    protected WebServiceContext context;

    private static final String PARAM_CONFIG_SERVICE = "configService";
    
    private XdConfig config = null;
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
    protected RegistryResponseType provideAndRegisterDocumentSet(ProvideAndRegisterDocumentSetRequestType prdst, SafeThreadData threadData) throws Exception 
    {
        RegistryResponseType resp = null;
        
        try 
        {
            @SuppressWarnings("unused")
            InitialContext ctx = new InitialContext();

            DirectDocuments documents = xdsDirectDocumentsTransformer.transform(prdst);
            
            List<String> forwards = new ArrayList<String>();
                        
            // Get endpoints (first check direct:to header, then go to intendedRecipients)
            if (StringUtils.isNotBlank(threadData.getDirectTo()))
                forwards = Arrays.asList((new URI(threadData.getDirectTo()).getSchemeSpecificPart()));
            else
            {
                forwards = ParserHL7.parseDirectRecipients(documents);
               
            }

           // messageId = UUID.randomUUID().toString();  remove this , its is not righ,
            //we should keep the message id of the original message for a lot of reasons vpl
            
            // TODO patID and subsetId  for atn
            String patId = threadData.getMessageId();
            String subsetId = threadData.getMessageId();
            getAuditMessageGenerator().provideAndRegisterAudit( threadData.getMessageId(), threadData.getRemoteHost(), threadData.getRelatesTo(), threadData.getTo(), 
                    threadData.getThisHost(), patId, subsetId, threadData.getPid());

            // Send to SMTP endpoints
            if (getResolver().hasSmtpEndpoints(forwards)) 
            {
                String replyEmail;
                // Get a reply address (first check direct:from header, then go to authorPerson)
                if (StringUtils.isNotBlank(threadData.getDirectFrom()))
                    replyEmail = (new URI(threadData.getDirectFrom())).getSchemeSpecificPart();
                else
                {
                   // replyEmail = documents.getSubmissionSet().getAuthorPerson();
                    replyEmail = documents.getSubmissionSet().getAuthorTelecommunication();

                 //   replyEmail = StringUtils.splitPreserveAllTokens(replyEmail, "^")[0];
                    replyEmail = ParserHL7.parseXTN(replyEmail);
                    replyEmail = StringUtils.contains(replyEmail, "@") ? replyEmail : "nhindirect@nhindirect.org";
                }

                LOGGER.info("SENDING EMAIL TO " + getResolver().getSmtpEndpoints(forwards) + " with message id "
                        + threadData.getMessageId());

                // Construct message wrapper
                DirectMessage message = new DirectMessage(replyEmail, getResolver().getSmtpEndpoints(forwards));
                message.setSubject("XD* Originated Message");
                message.setBody("Please find the attached XDM file.");
                message.setDirectDocuments(documents);

                // Send mail
                MailClient mailClient = getMailClient();
                String fileName = threadData.getMessageId().replaceAll("urn:uuid:", "");
                mailClient.mail(message, fileName, threadData.getSuffix());
                getAuditMessageGenerator().provideAndRegisterAuditSource( threadData.getMessageId(), threadData.getRemoteHost(), threadData.getRelatesTo(), 
                        threadData.getTo(), threadData.getThisHost(), patId, subsetId, threadData.getPid());
            }

            // Send to XD endpoints
            for (String reqEndpoint : getResolver().getXdEndpoints(forwards)) 
            {
                String endpointUrl = getResolver().resolve(reqEndpoint);
                
                String to = StringUtils.remove(endpointUrl, "?wsdl");

                threadData.setTo(to);
                threadData.setDirectTo(to);
                threadData.save();

                List<Document> docs = prdst.getDocument();
                
                // Make a copy of the original documents
                List<Document> originalDocs = new ArrayList<Document>();
                for (Document d : docs)
                    originalDocs.add(d);
                
                // Clear document list
                docs.clear();
                
                // Re-add documents
                for (Document d : originalDocs) 
                {
                    Document doc = new Document();
                    doc.setId(d.getId());

                    DataHandler dh = d.getValue();
                    ByteArrayOutputStream buffOS = new ByteArrayOutputStream();
                    dh.writeTo(buffOS);
                    byte[] buff = buffOS.toByteArray();

                    DataSource source = new ByteArrayDataSource(buff, documents.getDocument(d.getId()).getMetadata().getMimeType());
                    DataHandler dhnew = new DataHandler(source);
                    doc.setValue(dhnew);
    
                    docs.add(doc);
                }

                LOGGER.info(" SENDING TO ENDPOINT " + to);

                DocumentRepositoryProxy proxy = new DocumentRepositoryProxy(endpointUrl, new DirectSOAPHandlerResolver());
                
                RegistryResponseType rrt = proxy.provideAndRegisterDocumentSetB(prdst);
                String test = rrt.getStatus();
                if (test.indexOf("Failure") >= 0) 
                {
                    String error = "";
                    try{
                        error = rrt.getRegistryErrorList().getRegistryError().get(0).getCodeContext();
                    }catch(Exception x){}
                    throw new Exception("Failure Returned from XDR forward:" + error);
                }
                
                getAuditMessageGenerator().provideAndRegisterAuditSource( threadData.getMessageId(), threadData.getRemoteHost(), threadData.getRelatesTo(), threadData.getTo(), 
                        threadData.getThisHost(), patId, subsetId, threadData.getPid());
            }

            resp = getRepositoryProvideResponse(threadData.getMessageId());

            String relatesTo = threadData.getRelatesTo();
            threadData.setRelatesTo( threadData.getMessageId() );
            threadData.setAction( "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-bResponse" );
            threadData.setTo(null);
            threadData.save();

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
                    config = new XdConfig(configService);
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
        ServletContext sc = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
        return sc;
    }

    private AuditMessageGenerator getAuditMessageGenerator()
    {
        if (auditMessageGenerator == null)
        {
             if(config==null){
                config = getConfig();
            }
            String auditMethod = config.getAuditMethod();

            if (StringUtils.equals(auditMethod, AuditMethodEnum.SYSLOG.getMethod()))
            {
                String auditHost = config.getAuditHost();
                String auditPort = config.getAuditPort();

                auditMessageGenerator = new AuditMessageGenerator(auditHost, auditPort);
            }
            else if (StringUtils.equals(auditMethod, AuditMethodEnum.FILE.getMethod()))
            {
                String fileName = config.getAuditFile();

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
        if(config==null){
            config = getConfig();
        }
        String hostname = config.getMailHost();
        String username = config.getMailUser();
        String password = config.getMailPass();

        return new SmtpMailClient(hostname, username, password);
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
    
    private XdConfig getConfig()
    {
            XdConfig lconfig =null;
            String configService = null;
            
            try
            {
                configService = getServletContext().getInitParameter(PARAM_CONFIG_SERVICE);
            }
            catch (Exception e)
            {
                // TODO: define a custom exception
                throw new RuntimeException("Unable to find XD configuration URL", e);
            }
            
            if (StringUtils.isNotBlank(configService))
            {
                try
                {
                    lconfig = new XdConfig(configService);
                }
                catch (Exception e)
                {
                    // TODO: define a custom exception
                    throw new RuntimeException("Unable to create config from URL", e);
                }
            }
            else
            {
                // TODO: define a custom exception
                throw new RuntimeException("Configuration URL is blank");
            }
        
        
        return lconfig;
    }
    
    public void setConfig(XdConfig config)
    {
        this.config = config;
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

}


