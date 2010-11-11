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

package org.nhindirect.xdm;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.nhindirect.xd.common.DirectDocuments;

/**
 * This class handles the packaging and sending of XDM data over SMTP.
 * 
 * @author vlewis
 */
public class XDMMailClient
{

    private MimeMessage mmessage;
    private Multipart mailBody;
    private MimeBodyPart mainBody;
    private MimeBodyPart mimeAttach;

    private String smtpHostName;
    private String smtpAuthUser;
    private String smtpAuthPwd;

    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger.getLogger(XDMMailClient.class.getPackage().getName());

    public XDMMailClient(String smtpHostName, String smtpAuthUser, String smtpAuthPwd)
    {
        this.smtpHostName = smtpHostName;
        this.smtpAuthUser = smtpAuthUser;
        this.smtpAuthPwd = smtpAuthPwd;
    }

    public void sendMail(String from, Collection<String> recipients, DirectDocuments documents, String body,
            String suffix) throws MessagingException
    {
        String messageId = UUID.randomUUID().toString();

        sendMail(messageId, from, (List<String>) recipients, body, documents, suffix);
    }

    public void sendMail(String from, List<String> recipients, String body, DirectDocuments documents, String suffix)
            throws MessagingException
    {
        String messageId = UUID.randomUUID().toString();
        sendMail(messageId, from, recipients, body, documents, suffix);
    }

    /**
     * Create and send a message over SMTP.
     * 
     * @param recipients
     *            The list of recipient addresses for the mail message.
     * @param subject
     *            The subject of the mail message.
     * @param messageId
     *            The message ID.
     * @param body
     *            The body body of the message.
     * @param message
     *            The data to be zipped and attached to the mail message.
     * @param from
     *            The sender of the mail message.
     * @param suffix
     *            The suffix of the data to be zipped and attached to the mail
     *            message.
     * @param meta
     *            The metadata to be included in the zip and attached to the
     *            mail message.
     * @throws MessagingException
     */
    public void sendMail(String messageId, String from, List<String> recipients, String body,
            DirectDocuments documents, String suffix) throws MessagingException
    {
        boolean debug = false;
        java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

        String subject = "data";

        // Set the host SMTP address
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHostName);
        props.put("mail.smtp.auth", "true");

        Authenticator auth = new SMTPAuthenticator();
        Session session = Session.getInstance(props, auth);

        session.setDebug(debug);

        InternetAddress addressFrom = new InternetAddress(from);

        InternetAddress[] addressTo = new InternetAddress[recipients.size()];
        int i = 0;
        for (String recipient : recipients)
        {
            addressTo[i++] = new InternetAddress(recipient);
        }

        // Build message object
        mmessage = new MimeMessage(session);
        mmessage.setFrom(addressFrom);
        mmessage.setRecipients(Message.RecipientType.TO, addressTo);
        mmessage.setSubject(subject);

        mailBody = new MimeMultipart();

        mainBody = new MimeBodyPart();
        mainBody.setDataHandler(new DataHandler(body, "text/plain"));
        mailBody.addBodyPart(mainBody);

        try
        {
            mimeAttach = new MimeBodyPart();
            mimeAttach.attachFile(documents.toXdmPackage(messageId).toFile());
        }
        catch (IOException e)
        {
            throw new MessagingException("Unable to create/attach xdm file", e);
        }

        mailBody.addBodyPart(mimeAttach);

        mmessage.setContent(mailBody);
        Transport.send(mmessage);

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
            String username = smtpAuthUser;
            String password = smtpAuthPwd;
            return new PasswordAuthentication(username, password);
        }
    }

}
