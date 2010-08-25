/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nhind.xdm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
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

/**
 * This class handles the packaging and sending of XDM data over SMTP.
 * 
 * @author vlewis
 */
public class SMTPMailClient {

    private MimeMessage mmessage;
    private Multipart mailBody;
    private MimeBodyPart mainBody;
    private MimeBodyPart mimeAttach;
    static final int BUFFER = 2048;
    private static final String SMTP_HOST_NAME = "gmail-smtp.l.google.com";
    private static final String SMTP_AUTH_USER = "lewistower1@gmail.com";
    private static final String SMTP_AUTH_PWD = "hadron106";
 
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
    public void postMail(List<String> recipients, String subject, String messageId, String body,
            byte[] message, String from, String suffix, byte[] meta) throws MessagingException {
        boolean debug = false;
        java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

        // Set the host SMTP address
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST_NAME);
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
            File zipout = getZip(message, suffix, meta);
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
     * @return a reference to the created .zip file.
     */
    private File getZip(byte[] attachment, String suffix, byte[] meta) {
        File temp = null;
        
        try {
            BufferedInputStream origin = null;
            temp = new File("xdm.zip");
            FileOutputStream dest = new FileOutputStream(temp);

            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            out.setMethod(ZipOutputStream.DEFLATED);
            byte data[] = new byte[BUFFER];

            byte[] bytevals = attachment;
            InputStream byteis = new ByteArrayInputStream(bytevals);
            origin = new BufferedInputStream(byteis);

            ZipEntry entry = new ZipEntry("SUBSET01\\DOCUMENT." + suffix);
            out.putNextEntry(entry);
            int count = 0;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }

            
            bytevals = meta;
            byteis = new ByteArrayInputStream(bytevals);
            origin = new BufferedInputStream(byteis);

            entry = new ZipEntry("SUBSET01\\METADATA.xml");
            out.putNextEntry(entry);
            count = 0;
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

        String index = "<html xmlns=\"http://www.w3.org/1999/xhtml\" > <head>" +
                " <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
                "<title>XDM Message</title>" +
                "</head><body>" +
                "<h1>XDM Message</h1>" +
                "<p>This package contains an XDS message.  The message was created by " +
                "Happy Valley Clinic and is solely intended for the intended" +
                "recipients listed in the XDS Metadata.  Any other use is forbidden.</p>" +
                "<h2>Package Contents</h2>" +
                "<ul>" +
                "<li><a href=\"README.TXT\">README.TXT</a> - creator contact information and other general information</li>" +
                "<li><a href=\"SUBSET01/\">SUBSET01/</a> - XDS Submission Set 1" +
                "<ul>" +
                "<li><a href=\"SUBSET01/METADATA.XML\">SUBSET01/METADATA.XML</a> - XDS information about the content, recipient, author, etc.</li>" +
                "<li><a href=\"SUBSET01/DOCUMENT.XXX\">SUBSET01/DOCUMENT.XXX</a> - document payload in XXX format</li>" +
                "</ul>" +
                "</li>" +
                "</ul>" +
                "</body></html>";
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
        InputStream is = this.getClass().getResourceAsStream("/CCD.xsl");
        byte[] theBytes = new byte[is.available()];
        is.read(theBytes);
        return theBytes;

    }
}
