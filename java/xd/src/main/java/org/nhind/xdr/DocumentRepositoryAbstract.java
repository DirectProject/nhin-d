/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nhind.xdr;

import ihe.iti.xds_b._2007.DocumentRepositoryPortType;
import ihe.iti.xds_b._2007.DocumentRepositoryService;
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
import javax.mail.util.ByteArrayDataSource;
import javax.naming.InitialContext;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;

import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

import org.nhind.util.XMLUtils;
import org.nhind.xdm.SMTPMailClient;

/**
 * Base class for handling incoming XDR requests.
 * 
 * @author Vince
 */
public abstract class DocumentRepositoryAbstract {

    protected WebServiceContext mywscontext;
    
    protected String endpoint = null;
    protected String messageId = null;
    protected String relatesTo = null;
    protected String action = null;
    protected String to = null;
    
    private String thisHost = null;
    private String remoteHost = null;
    private String pid = null;
    private String from = null;
    private String suffix = null;
    private String replyEmail = null;

    /**
     * Class logger
     */
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
    protected RegistryResponseType provideAndRegisterDocumentSet(ProvideAndRegisterDocumentSetRequestType prdst) throws Exception {
        RegistryResponseType resp = null;
        try {
            getHeaderData();
            
            @SuppressWarnings("unused")
            InitialContext ctx = new InitialContext();
            
            QName qname = new QName("urn:ihe:iti:xds-b:2007", "ProvideAndRegisterDocumentSet_bRequest");
            String body = XMLUtils.marshal(qname, prdst, ihe.iti.xds_b._2007.ObjectFactory.class);
            QName sname = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0", "SubmitObjectsRequest");
            SubmitObjectsRequest sor = prdst.getSubmitObjectsRequest();
            String meta = XMLUtils.marshal(sname, sor,  oasis.names.tc.ebxml_regrep.xsd.lcm._3.ObjectFactory.class);
            List<String> forwards = provideAndRegister(prdst);

            String rmessageId = fowardMessage( body, forwards, replyEmail, prdst, messageId, endpoint, suffix, meta);

            resp = getRepositoryProvideResponse(rmessageId);

            relatesTo = messageId;
            action = "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-bResponse";
            messageId = rmessageId;
            to = endpoint;
            
            setHeaderData();
        } catch (Exception e) {
            e.printStackTrace();
            throw (e);
        }

        return resp;
    }

    /**
     * Handle an incoming ProvideAndRegisterDocumentSetRequestType object and
     * transform to XDM or relay to another XDR endponit.
     * 
     * @param prdst
     *            The incoming ProvideAndRegisterDocumentSetRequestType object
     * @return a RegistryResponseType object
     * @throws Exception
     */
    protected List<String> provideAndRegister(ProvideAndRegisterDocumentSetRequestType prdst) throws Exception {
        List<String> forwards = null;

        try {
            @SuppressWarnings("unused")
            InitialContext ctx = new InitialContext();

            DocumentRegistry drr = new DocumentRegistry();
            String mimeType = drr.parseRegistry(prdst);
            forwards = drr.getForwards();
            replyEmail = drr.getAuthorEmail();
            if (mimeType.indexOf("pdf") >= 0) {
                suffix = "pdf";
            } else {
                suffix = "xml";
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw (e);
        }

        return forwards;
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

    /**
     * Forward a message to an email recipient or to an XDR relay endpoint.
     * 
     * @param body
     *            The message body
     * @param forwards
     *            The list of email recipients and relay endponits
     * @param replyEmail
     *            The reply-to email address
     * @param prdst
     *            The ProvideAndRegisterDocumentSetRequestType object
     * @param messageId
     *            The message ID
     * @param endpoint
     *            ?? TODO Unused param
     * @param suffix
     *            The file extension of the XDR document
     * @param meta
     *            The XDR metadata
     * @return a message ID
     * @throws Exception
     */
    public String fowardMessage( String body, List<String> forwards, String replyEmail, ProvideAndRegisterDocumentSetRequestType prdst, String messageId, String endpoint, String suffix, String meta) throws Exception {

        try {
            messageId = UUID.randomUUID().toString();
            boolean requestForward = true;

            if (requestForward && forwards.size() > 0) {

                forwardRepositoryRequest( messageId, forwards, prdst, replyEmail, body, suffix, meta);
            }

        } catch (Exception x) {
            x.printStackTrace();
            throw x;
        }
        return messageId;
    }

    /**
     * @param type
     *            ?? TODO What is this? It's not used anywhere down the line. A
     *            literal value of '4' is being passed here
     * @param messageId
     *            The message ID
     * @param provideEndpoints
     *            The list of email recipients and relay endponits
     * @param prdst
     *            The ProvideAndRegisterDocumentSetRequestType object
     * @param fromEmail
     *            The reply-to email address
     * @param body
     *            The message body
     * @param suffix
     *            The file extension of the XDR document
     * @param meta
     *            The XDR metadata
     * @throws Exception
     */
    private void forwardRepositoryRequest( String messageId, List<String> provideEndpoints, ProvideAndRegisterDocumentSetRequestType prdst, String fromEmail, String body, String suffix, String meta) throws Exception {
        try {
            for (String reqEndpoint : provideEndpoints) {
                if (reqEndpoint.indexOf('@') > 0) {
                    byte[] docs = getDocs(prdst);
                    mailDocument(reqEndpoint, fromEmail, messageId, docs, suffix, meta.getBytes());
                } else if (reqEndpoint.equals("local")) {
                    //nothing
                } else {

                    String to = reqEndpoint;
                    to = to.replace("?wsdl", "");
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
                    DocumentRepositoryService service = new DocumentRepositoryService();
                    service.setHandlerResolver(new RepositoryHandlerResolver());


                    DocumentRepositoryPortType port = service.getDocumentRepositoryPortSoap12(new MTOMFeature(true, 1));

                    BindingProvider bp = (BindingProvider) port;
                    SOAPBinding binding = (SOAPBinding) bp.getBinding();
                    binding.setMTOMEnabled(true);

                    bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, reqEndpoint);


                    RegistryResponseType rrt = port.documentRepositoryProvideAndRegisterDocumentSetB(prdst);
                    String test = rrt.getStatus();
                    if (test.indexOf("Failure") >= 0) {
                        throw new Exception("Failure Returned from XDR forward");
                    }


                }
            }


        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    /**
     * Mail a recipient the XDR data.
     * 
     * @param email
     *            The email recipient
     * @param from
     *            The reply-to email address
     * @param messageId
     *            The message ID
     * @param message
     *            The raw message to be sent
     * @param suffix
     *            The file extension of the XDR document
     * @param meta
     *            The XDR metadata
     * @throws Exception
     */
    private void mailDocument(String email, String from, String messageId, byte[] message, String suffix, byte[] meta) throws Exception {
        SMTPMailClient smc = new SMTPMailClient();
        LOGGER.info("SENDING EMAIL TO " + email + " with message id " + messageId);
        List<String> recipients = new ArrayList<String>();
        recipients.add(email);

        smc.postMail(recipients, "data", messageId, "data attached", message, from, suffix, meta);
    }

    /**
     * Extract the documents from the XDR message.
     * 
     * @param prdst
     *            The XDR message
     * @return the raw documents from the XDR message
     */
    private byte[] getDocs(ProvideAndRegisterDocumentSetRequestType prdst) {
        List<Document> documents = prdst.getDocument();

        byte[] ret = null;
        try {
            for (ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType.Document doc : documents) {
                DataHandler dh = doc.getValue();
                ByteArrayOutputStream buffOS = new ByteArrayOutputStream();
                dh.writeTo(buffOS);
                ret = buffOS.toByteArray();

            }
        } catch (Exception x) {
            x.printStackTrace();
        }
        return ret;
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
        
        LOGGER.info(threadData.toString());
    }
}
