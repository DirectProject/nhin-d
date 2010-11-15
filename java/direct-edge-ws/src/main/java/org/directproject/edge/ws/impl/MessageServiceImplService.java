package org.directproject.edge.ws.impl;

/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
 Tim Jeffcoat    tjeffcoat@inpriva.com
 Pat Pyette      ppyette@inpriva.com

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

import org.nhindirect.schema.edge.ws.AddressType;

import org.nhindirect.schema.edge.ws.AttachmentType;
import org.nhindirect.schema.edge.ws.EmailType;
import org.nhindirect.schema.edge.ws.ErrorCodeType;
import org.nhindirect.schema.edge.ws.ErrorType;
import org.nhindirect.schema.edge.ws.SendResponseType;
import org.nhindirect.schema.edge.ws.StatusRefType;
import org.nhindirect.schema.edge.ws.StatusResponseType;
import org.nhindirect.wsdl.edge.ws.NhindirectWSEdgePort;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.Flags.Flag;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.SearchTerm;
import javax.mail.search.MessageIDTerm;
import javax.mail.Flags;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.common.util.Base64Exception;
import org.apache.cxf.common.util.Base64Utility;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;

import org.bouncycastle.util.encoders.Base64;

/**
 * Implementation of the Direct WS Edge protocol.
 * 
 * @author ppyette
 *
 */
public class MessageServiceImplService implements NhindirectWSEdgePort, InitializingBean
{
    private static final Log log           = LogFactory
                                                   .getLog(MessageServiceImplService.class);

    private String           smtpHost      = "";
    private String           imapHost      = "";
    
    private String           username      = "";
    private String           password      = "";
    
    private int              smtpPort      = 465;
    private int              imapPort      = 993;
    
    //TODO STARTTLS support does not work correctly!
    private String           useTLSforSMTP = "";
    private String           useTLSforIMAP = "";

    private Properties smtpProps    = new Properties();
    private Properties imapProps    = new Properties();
 
    public MessageServiceImplService()
    {
        if (log.isDebugEnabled()) log.debug("Instantiated");
    }

    @Override
    /**
     * Converts an incoming WS request into an email message and sends it to the configured 
     * email server
     */
    public SendResponseType sendMessage(EmailType body)
    {
        if (log.isDebugEnabled()) log.debug("Enter");
        
        SendResponseType response = new SendResponseType();
        
        checkAuth(response);
        
        if (response.getError() == null)
        {
         
            Multipart    mailBody;
            MimeBodyPart mainBody;
            MimeBodyPart mimeAttach;
    
            String fromaddress = body.getHead().getFrom().getAddress();
    
            try
            {
                InternetAddress addressFrom;
                addressFrom = new InternetAddress(fromaddress.toString());
                
                if (log.isDebugEnabled()) log.debug("Sender: " + addressFrom);
                
                InternetAddress[] addressTo = new InternetAddress[1];
                int i = 0;
                for (AddressType recipient : body.getHead().getTo())
                {
                    addressTo[i] = new InternetAddress(recipient.getAddress());
                    i++;
                    if (log.isDebugEnabled()) log.debug("Recipient: " + addressTo[i]);
                }
                
                Session session = Session.getInstance(smtpProps, new SMTPAuthenticator());
    
                // Build message object
                MimeMessage mimeMsg = new MimeMessage(session);
                mimeMsg.setFrom(addressFrom);
                mimeMsg.setRecipients(Message.RecipientType.TO, addressTo);
    
                if (body.getHead().getSubject() != null)
                {
                    mimeMsg.setSubject(body.getHead().getSubject());
                }
                else 
                {
                    mimeMsg.setSubject("Direct message");
                }
                
                mailBody = new MimeMultipart();

                mainBody = new MimeBodyPart();
                mainBody.setText(body.getBody().getText());
                mailBody.addBodyPart(mainBody);

                copyAttachments(body, mailBody);
    
                mimeMsg.setContent(mailBody);
    
                DirectMimeMessage dMsg = new DirectMimeMessage(mimeMsg, getSenderHost());
                dMsg.updateMessageID();
                
                Transport transport;
                
                if (getUseTLSforSMTP().startsWith("S"))
                {
                    transport = session.getTransport("smtps");
                }
                else
                {
                    transport = session.getTransport("smtp");
                }
                
                transport.connect();
                try
                {
                    transport.sendMessage(dMsg, addressTo);
                    
                    // Transport.send(dMsg);
                    response.setMessageID(dMsg.getMessageID());
                    transport.close();
                }
                finally 
                {
                    transport.close();
                }
               
            } 
            catch (AddressException e)
            {
                ErrorType et = new ErrorType();
                et.setCode(ErrorCodeType.ADDRESSING);
                et.setMessage(e.getMessage());
                response.setError(et);
                log.error(e);
            } 
            catch (MessagingException e)
            {
                ErrorType et = new ErrorType();
                et.setCode(ErrorCodeType.MESSAGING);
                et.setMessage(e.getMessage());
                response.setError(et);
                log.error(e);
            }
            catch (Exception e)
            {
                ErrorType et = new ErrorType();
                et.setCode(ErrorCodeType.SYSTEM);
                et.setMessage(e.getMessage());
                response.setError(et);
                log.error(e);
            }
        }

        return response;
    }

    /**
     * @param body - The incoming unmarshalled SOAP message
     * @param mailBody - The mail message that we're constructing
     * @throws IOException
     * @throws MessagingException
     */
    private void copyAttachments(EmailType body, Multipart mailBody)
            throws IOException, MessagingException
    {
        if (log.isDebugEnabled()) log.debug("Enter");
        
        MimeBodyPart mimeAttach;
        if (body.getBody().getAttachment().size() > 0)
        {
            for (AttachmentType document : body.getBody().getAttachment())
            {
                mimeAttach = new MimeBodyPart();

                if (log.isDebugEnabled()) log.debug("About to unencode base64 attachment");
                // Data is (by schema definition) supposed to be 
                // base64-encoded, so let's decode prior to sending.
                ByteArrayInputStream bais  = (ByteArrayInputStream)document.getContent().getContent();              
                
                byte[] content = new byte[bais.available()];
                log.info("Attachment is: " + content.length + " bytes");
                bais.read(content);
                bais.close();
                boolean isString = false;
                if ((document.getMimeType().startsWith("text")) ||
                    (document.getMimeType().endsWith("xml")))
                {
                    isString = true;
                }
               
                try 
                {  
                    DataHandler dh;
                    if (isString)
                    {
                        String attachment = new String(content);
                        dh = new DataHandler(attachment, document.getMimeType()); 
                    }
                    else
                    {
                        dh = new DataHandler(content, document.getMimeType());
                    }
                    mimeAttach.setDataHandler(dh);
                }
                catch (MessagingException e)
                {
                    log.error("Could not set content using supplied mimetype.", e);
                }
                // 
                mimeAttach.setFileName(document.getFilename());
                mailBody.addBodyPart(mimeAttach);
            }
        }
    }
    
    private String getSenderHost()
    {
        String result = "";
        if (username != null)
        {
            int at = username.indexOf("@");
            result = username.substring(at+1);
        }
        return result;
    }
    
    
    /**
     * Ensures that the request has been 
     * @throws Exception
     */
    private void checkAuth(StatusResponseType response) throws Exception
    {
        // Check for authorization before we do anything
        // This SHOULD be done at the security interceptor, but you 
        // never know.
        Authentication inAuth = SecurityContextHolder.getContext().getAuthentication();
        if (!inAuth.isAuthenticated())
        {
            log.warn("Unauthorized attempt to use the Message Service!");
            throw new Exception("Unauthorized user: " + inAuth.getName());
        }
        
        username = inAuth.getName();
        password = (String)inAuth.getCredentials();
    }
    
    /**
     * Ensures that the request has been 
     * @throws Exception
     */
    private void checkAuth(SendResponseType response) 
    {
        // Check for authorization before we do anything
        // This SHOULD be done at the security interceptor, but you 
        // never know.
        Authentication inAuth = SecurityContextHolder.getContext().getAuthentication();
        if (!inAuth.isAuthenticated())
        {
            ErrorType et = new ErrorType();
            et.setCode(ErrorCodeType.NOT_AUTH);
            et.setMessage("User: " + inAuth.getName() + " is not authorized to use this service");
            response.setError(et);
            log.warn("Unauthorized attempt to use the Message Service!");
        }
        
        username = inAuth.getName();
        password = (String)inAuth.getCredentials();
    }

    private void initProperties()
    { 
        smtpProps.setProperty("mail.smtp.host", getSmtpHost());
        smtpProps.setProperty("mail.smtp.auth", "true");
        smtpProps.setProperty("mail.smtp.port", Integer.toString(getSmtpPort())); 
        smtpProps.setProperty("mail.smtp.dsn.notify", "SUCCESS,FAILURE,DELAY");
        
        imapProps.setProperty("mail.imap.port", String.valueOf(getImapPort()));
        imapProps.setProperty("mail.imap.host", getImapHost());
        imapProps.setProperty("mail.imap.auth", "true");
             
        setSecureProperties();    
        
        log.info("Mail properties: " + printProperties());
    }

    /**
     * 
     */
    private void setSecureProperties()
    {
        if (getUseTLSforSMTP().startsWith("S"))
        {
            smtpProps.setProperty("mail.smtps.host", getSmtpHost());
            smtpProps.setProperty("mail.smtps.port", Integer.toString(getSmtpPort()));
            smtpProps.setProperty("mail.smtp.ssl.enable", "true");
            smtpProps.setProperty("mail.smtps.ssl.enable", "true");
            smtpProps.setProperty("mail.smtps.auth", "true");
            smtpProps.setProperty("mail.smtps.dsn.notify", "SUCCESS,FAILURE,DELAY");
           
            if (getUseTLSforSMTP().equals("STARTTLS"))
            {  
                // Haven't been able to get a connection to work as of yet.  Wants to connect to
                // port 25 regardless of settings.
                smtpProps.setProperty("mail.smtp.starttls.enabled","true");
                smtpProps.setProperty("mail.smtps.starttls.enabled","true");
            };
        }
        
        if (getUseTLSforIMAP().startsWith("S"))
        {
            imapProps.setProperty("mail.imaps.port", String.valueOf(getImapPort()));
            imapProps.setProperty("mail.imaps.host", getImapHost());
            imapProps.setProperty("mail.imaps.auth", "true");
            imapProps.setProperty("mail.imaps.ssl.enable", "true");   
            
            if (getUseTLSforIMAP().equals("STARTTLS"))
            {
                imapProps.put("mail.imap.starttls.enable", true);
                imapProps.put("mail.imaps.starttls.enable", true);
            }
        }
    }

    @Override
    public StatusResponseType requestStatus(StatusRefType body)
    {
        List<String> msgs = body.getMessageID();

        StatusResponseType response = new StatusResponseType();

        try
        {
            checkAuth(response);
            
            Authenticator auth = new SMTPAuthenticator();
            Session session = Session.getInstance(imapProps, auth);
            session.setDebug(true);
           
            

            if (msgs.size() > 0)
            {
                Store store = session.getStore(new javax.mail.URLName("imaps://"
                        + username));

                store.connect(getImapHost(), 
                              Integer.valueOf(getImapPort()).intValue(), 
                              username, 
                              password);

                for (int x = 0; x < msgs.size(); x++)
                {
                    String msgid = msgs.get(x);
                    MessageIDTerm messageIdTerm = new MessageIDTerm(msgid);

                    IMAPFolder folder = (IMAPFolder) store.getFolder("INBOX");
                    folder.open(Folder.READ_ONLY);

                    SearchTerm st = messageIdTerm;

                    IMAPMessage[] msgsearch = (IMAPMessage[]) folder.search(st);

                    if (msgsearch.length > 0)
                    {
                        Flags flags = msgsearch[0].getFlags();
                        Flag[] inboxflags = flags.getSystemFlags();
                        String[] listofflags = new String[inboxflags.length];
                        listofflags = setSystemFlags(inboxflags);
                        setMessageIdStatus(msgid, listofflags,
                                response.getMessageIDAndStatus());
                    }

                }
            }

        } 
        catch (AddressException e)
        {
            log.error(e);
        } 
        catch (MessagingException e)
        {
            log.error(e);
        }
        catch (Exception e)
        {
            log.error(e);
        }

        return response;
    }

    private void setMessageIdStatus(String msgid, String[] flags,
            List<Object> response)
    {
        if (flags.length > 0)
        {
            for (int y = 0; y < flags.length; y++)
            {
                String flg = flags[y];
                String tmp = msgid + "," + flg;
                response.add(tmp);
            }
        }
    }

    private String[] setSystemFlags(Flag[] inboxflags)
    {
        String[] listofflags = new String[inboxflags.length];
        if (inboxflags.length > 0)
        {
            for (int y = 0; y < inboxflags.length; y++)
            {
                String flag_text = "";
                if (inboxflags[0] == Flags.Flag.DELETED)
                {
                    flag_text = "Deleted";
                    listofflags[y] = flag_text;
                }
                if (inboxflags[0] == Flags.Flag.ANSWERED)
                {
                    flag_text = "Answered";
                    listofflags[y] = flag_text;
                }
                if (inboxflags[0] == Flags.Flag.DRAFT)
                {
                    flag_text = "Draft";
                    listofflags[y] = flag_text;
                }
                if (inboxflags[0] == Flags.Flag.FLAGGED)
                {
                    flag_text = "Marked";
                    listofflags[y] = flag_text;
                }
                if (inboxflags[0] == Flags.Flag.RECENT)
                {
                    flag_text = "Recent";
                    listofflags[y] = flag_text;
                }
                if (inboxflags[0] == Flags.Flag.SEEN)
                {
                    flag_text = "Read";
                    listofflags[y] = flag_text;
                }
                if (inboxflags[0] == Flags.Flag.USER)
                {
                    flag_text = "User flag ";
                    listofflags[y] = flag_text;
                }
            }
        }
        return listofflags;
    }

    public int getSmtpPort()
    {
        return smtpPort;
    }

    public void setSmtpPort(int smtpPort)
    {
        this.smtpPort = smtpPort;
    }

    public String getSmtpHost()
    {
        return smtpHost;
    }

    public void setSmtpHost(String smtphost)
    {
        this.smtpHost = smtphost;
    }

    public String getImapHost()
    {
        return imapHost;
    }

    public void setImapHost(String imaphost)
    {
        this.imapHost = imaphost;
    }

    public int getImapPort()
    {
        return imapPort;
    }

    public void setImapPort(int imapPort)
    {
        this.imapPort = imapPort;
    }

    public String getUseTLSforSMTP()
    {
        return useTLSforSMTP;
    }

    public void setUseTLSforSMTP(String useTLSforSMTP)
    {
        this.useTLSforSMTP = useTLSforSMTP;
    }

    public String getUseTLSforIMAP()
    {
        return useTLSforIMAP;
    }

    public void setUseTLSforIMAP(String useTLSforIMAP)
    {
        this.useTLSforIMAP = useTLSforIMAP;
    }
    
    public String printProperties()
    {
        StringBuffer result = new StringBuffer("SMTP Properties: \r\n");
        Set<String> names = smtpProps.stringPropertyNames();
        
        for (String key : names)
        {
            result.append(key)
                  .append(" = ")
                  .append(smtpProps.get(key))
                  .append("\r\n");
        }
        
        names = imapProps.stringPropertyNames();
        
        result.append("\r\nIMAP Properties: \r\n");  
        for (String key : names)
        {
            result.append(key)
                  .append(" = ")
                  .append(imapProps.get(key))
                  .append("\r\n");
        }
        return result.toString();
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        log.info("Properties set");
        validateProperties();
        initProperties();
    }
    
    private void validateProperties() throws Exception
    {
        if (getUseTLSforIMAP() == null) setUseTLSforIMAP("NONE");
        setUseTLSforIMAP(getUseTLSforIMAP().toUpperCase());
        
        if (!((getUseTLSforIMAP().equals("")) ||
            (getUseTLSforIMAP().equals("NONE")) ||
            (getUseTLSforIMAP().equals("SOCKET")) ||
            (getUseTLSforIMAP().equals("STARTTLS"))))
        {
            throw new InvalidPropertyException(this.getClass(),  "useTLSforIMAP", "Invalid valid for property.  Use one of 'NONE', 'SOCKET', or 'STARTTLS'"); 
        }
        
        if (getUseTLSforSMTP() == null) setUseTLSforSMTP("NONE");
        setUseTLSforSMTP(getUseTLSforSMTP().toUpperCase());
        
        if (!((getUseTLSforSMTP().equals("")) ||
                (getUseTLSforSMTP().equals("NONE")) ||
                (getUseTLSforSMTP().equals("SOCKET")) ||
                (getUseTLSforSMTP().equals("STARTTLS"))))
        {
            throw new InvalidPropertyException(this.getClass(),  "useTLSforSMTP", "Invalid valid for property.  Use one of 'NONE', 'SOCKET', or 'STARTTLS'"); 
        }
    }
    
    /**
     * SimpleAuthenticator is used to do simple authentication when the SMTP
     * server requires it.
     */
    private class SMTPAuthenticator extends javax.mail.Authenticator
    {
        /*
         * (non-Javadoc)
         * 
         * @see javax.mail.Authenticator#getPasswordAuthentication()
         */
        @Override
        public PasswordAuthentication getPasswordAuthentication()
        {
            return new PasswordAuthentication(username, password);
        }
    }

}
