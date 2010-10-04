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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

import org.apache.commons.lang.StringUtils;

/**
 * This class handles the packaging and sending of XDM data over SMTP.
 * 
 * @author vlewis
 */
public class XDMMailClient {

    private MimeMessage mmessage;
    private Multipart mailBody;
    private MimeBodyPart mainBody;
    private MimeBodyPart mimeAttach;
    static final int BUFFER = 2048;
    private String hostName = null;
    private static final String SMTP_HOST_NAME = "gmail-smtp.l.google.com";
    private static final String SMTP_AUTH_USER = "lewistower1@gmail.com";
    private static final String SMTP_AUTH_PWD = "hadron106";
    /**
     * Class logger.
     */
    private static final Logger LOGGER = Logger.getLogger(XDMMailClient.class.getPackage().getName());

    public XDMMailClient() {
        hostName = SMTP_HOST_NAME;
    }

    public XDMMailClient(String hostName) {
        this.hostName = hostName;
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
    public void sendMail( String messageId,String from , List<String> recipients,  String meta, String body, List<String> docs, String suffix) throws MessagingException {
        boolean debug = false;
        java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

        String subject = "data";


        // Set the host SMTP address
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", hostName);
        props.put("mail.smtp.auth", "true");

        Authenticator auth = new SMTPAuthenticator();
        Session session = Session.getInstance(props, auth);

        session.setDebug(debug);

        InternetAddress addressFrom = new InternetAddress(from);

        InternetAddress[] addressTo = new InternetAddress[recipients.size()];
        int i = 0;
        for (String recipient : recipients) {
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

        mimeAttach = new MimeBodyPart();

        try {

            File zipout = getZip(docs, suffix, meta.getBytes(), messageId);
            mimeAttach.attachFile(zipout);

        } catch (Exception x) {
            x.printStackTrace();
        }
        // mimeAttach.setFileName(fds.getName());
        mailBody.addBodyPart(mimeAttach);

        mmessage.setContent(mailBody);
        Transport.send(mmessage);

    }

    /**
     * Write data to a .zip file and return the Flie object.
     * 
     * @param attachment
     *            The attachment data to be included in the .zip file.
     * @param suffix
     *            The suffix for the attachment data.
     * @param meta
     *            The metadata to be included in the .zip file.
     * @param messageId
     *            Unique string representing the message ID, used as part of the
     *            zip filename for thread safety.
     * @return a reference to the created .zip file.
     */
    private File getZip(List<String> docs, String suffix, byte[] meta, String messageId) {
        File temp = null;

        if (StringUtils.isBlank(messageId)) {
            messageId = UUID.randomUUID().toString();

            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("Message ID not provided, using random ID (" + messageId + ")");
            }
        }

        try {
            BufferedInputStream origin = null;
            temp = new File(messageId + "-xdm.zip");
            FileOutputStream dest = new FileOutputStream(temp);

            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            out.setMethod(ZipOutputStream.DEFLATED);

            Iterator<String> it = docs.iterator();
            int dcount = 0;
            while (it.hasNext()) {
                byte[] attachment = it.next().getBytes();

                byte data[] = new byte[BUFFER];

                byte[] bytevals = attachment;
                InputStream byteis = new ByteArrayInputStream(bytevals);
                origin = new BufferedInputStream(byteis);

                //ZipEntry entry = new ZipEntry("SUBSET01\\DOCUMENT"+ dcount +"." + suffix);
                ZipEntry entry = new ZipEntry("SUBSET01\\DOCUMENT." + suffix);
                dcount++;
                out.putNextEntry(entry);
                int count = 0;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }

            }
            byte[] bytevals = meta;
            InputStream byteis = new ByteArrayInputStream(bytevals);
            origin = new BufferedInputStream(byteis);

            ZipEntry entry = new ZipEntry("SUBSET01\\METADATA.xml");
            out.putNextEntry(entry);
            int count = 0;
            byte data[] = new byte[BUFFER];
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }

            String index = getIndex(suffix);
            bytevals = index.getBytes();
            byteis = new ByteArrayInputStream(bytevals);
            origin = new BufferedInputStream(byteis);

            entry = new ZipEntry("INDEX.htm");
            out.putNextEntry(entry);
            count = 0;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }


            String readme = getReadme();
            bytevals = readme.getBytes();
            byteis = new ByteArrayInputStream(bytevals);
            origin = new BufferedInputStream(byteis);

            entry = new ZipEntry("README.txt");
            out.putNextEntry(entry);
            count = 0;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }


            if (suffix.equals("xml")) {
                bytevals = getXsl();
                byteis = new ByteArrayInputStream(bytevals);
                origin = new BufferedInputStream(byteis);

                entry = new ZipEntry("SUBSET01\\CCD.xsl");
                out.putNextEntry(entry);
                count = 0;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
            }

            origin.close();

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;

    }

    /**
     * SimpleAuthenticator is used to do simple authentication when the SMTP
     * server requires it.
     */
    private class SMTPAuthenticator extends javax.mail.Authenticator {

        /*
         * (non-Javadoc)
         * 
         * @see javax.mail.Authenticator#getPasswordAuthentication()
         */
        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            String username = SMTP_AUTH_USER;
            String password = SMTP_AUTH_PWD;
            return new PasswordAuthentication(username, password);
        }
    }

    /**
     * Create the readme string for the XDM package.
     * 
     * @return a string to be used as the readme for the XDM package.
     */
    private String getReadme() {
        return "NHIN Direct - IHE Team - Implementation. This XDM message was created via the web interface.  Please view INDEX.HTM for links to the files and metadata that make up this message. ";
    }

    /**
     * Create the index file for the XDM package.
     * 
     * @param type
     *            The suffix for the attachment included in the XDM package.
     * @return a string to be used as the index for the XDM package.
     */
    private String getIndex(String type) {

        String index = "<html xmlns=\"http://www.w3.org/1999/xhtml\" > <head>"
                + " <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
                + "<title>XDM Message</title>"
                + "</head><body>"
                + "<h1>XDM Message</h1>"
                + "<p>This package contains an XDS message.  The message was created by "
                + "Happy Valley Clinic and is solely intended for the intended"
                + "recipients listed in the XDS Metadata.  Any other use is forbidden.</p>"
                + "<h2>Package Contents</h2>"
                + "<ul>"
                + "<li><a href=\"README.TXT\">README.TXT</a> - creator contact information and other general information</li>"
                + "<li><a href=\"SUBSET01/\">SUBSET01/</a> - XDS Submission Set 1"
                + "<ul>"
                + "<li><a href=\"SUBSET01/METADATA.XML\">SUBSET01/METADATA.XML</a> - XDS information about the content, recipient, author, etc.</li>"
                + "<li><a href=\"SUBSET01/DOCUMENT.XXX\">SUBSET01/DOCUMENT.XXX</a> - document payload in XXX format</li>"
                + "</ul>"
                + "</li>"
                + "</ul>"
                + "</body></html>";
        String ret = index.replace("XXX", type);
        return ret;
    }

    /**
     * Return the CCD.xsl file as an array of bytes.
     * 
     * @return the CCD.xsl file as an array of bytes.
     * @throws Exception
     */
    private byte[] getXsl() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/META-INF/main/resources/CCD.xsl");
        byte[] theBytes = new byte[is.available()];
        is.read(theBytes);
        return theBytes;

    }
}
