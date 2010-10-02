/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Umesh Madan     umeshma@microsoft.com
   Greg Meyer      gm2552@cerner.com
 
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

package org.nhindirect.stagent.bridge.james.mailet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mailet.Mail;
import org.apache.mailet.base.GenericMailet;
import org.nhindirect.stagent.IncomingMessage;
import org.nhindirect.stagent.DefaultNHINDAgent;
import org.nhindirect.stagent.OutgoingMessage;
import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.cert.impl.KeyStoreCertificateStore;
import org.nhindirect.stagent.cryptography.SMIMEStandard;
import org.nhindirect.stagent.parser.EntitySerializer;
import org.nhindirect.stagent.trust.DefaultTrustAnchorResolver;


/** 
 * This is a proof of concept bridge between an Apache James mail server and the NHIN agent implemented as
 * a Mailet.  
 * @author Greg Meyer
 *
 */
public class PostfixNHINDFilter extends GenericMailet
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(PostfixNHINDFilter.class);	
	
	private DefaultNHINDAgent agent;
	private Set<String> internalHISPDomains = new HashSet<String>();
	
	private String incomingHost;
	private String incomingPort;
	private String outgoingHost;
	private String outgoingPort;
	//private boolean passThroughLocalRecips = true;
	
	public void init() throws ParseException
	{
		LOGGER.info("Initializing PostfixNHINDFilter");
		
		/*
		 * TODO: implement configurable pattern to create store instances
		 */
		try
		{
			String domains = getInitParameter("InternalHispDomains");
			String trustCertAliases = getInitParameter("TrustAnchorAliases");
			
			Collection<X509Certificate> archorCerts = new ArrayList<X509Certificate>();
			
			StringTokenizer tokenizer = new StringTokenizer(domains, ",");
			while (tokenizer.hasMoreTokens())
				internalHISPDomains.add(tokenizer.nextToken());
			
			outgoingHost = getInitParameter("OutgoingHost");
			outgoingPort = getInitParameter("OutgoingPort");

			incomingHost = getInitParameter("IncomingHost");
			incomingPort = getInitParameter("IncomingPort");
			
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("OutgoingHost: " + outgoingHost);
				LOGGER.debug("OutgoingPort: " + outgoingPort);
				LOGGER.debug("IncomingHost: " + incomingHost);
				LOGGER.debug("IncomingPort: " + incomingPort);
			}
			
			CertificateResolver certService = null;
			
			String certStoreClassName = getInitParameter("CertStoreImpl");
			
			LOGGER.debug("CertStoreImpl name: " + certStoreClassName);
			
			Class<?> certStoreClass = Class.forName(certStoreClassName);
			Object certStoreImpl = certStoreClass.newInstance();
			if (certStoreImpl instanceof KeyStoreCertificateStore)
			{
				KeyStoreCertificateStore certServiceHolder = (KeyStoreCertificateStore)certStoreImpl;
				certServiceHolder.setKeyStoreFile(getInitParameter("KeyStoreFile"));
				certServiceHolder.setKeyStorePassword(getInitParameter("KeyStorePW"));	
				certServiceHolder.setPrivateKeyPassword(getInitParameter("KeyStorePKPW"));
				
				certServiceHolder.loadKeyStore();
				
				certService = certServiceHolder;
				
				LOGGER.debug("Successfully create cert store");
				
				tokenizer = new StringTokenizer(trustCertAliases, ",");
				while (tokenizer.hasMoreTokens())
				{
					String token = tokenizer.nextToken();
					X509Certificate anchorCert = certServiceHolder.getByAlias(token);
					if (anchorCert != null)
					{
						System.out.println("Loading archor CN: " + anchorCert.getSubjectDN().getName());
						archorCerts.add(anchorCert);
					}
				}
			}
			
			LOGGER.debug("Creating NHIND agent.");
			
			// hard coded to use the uniform trust settings
			
			agent = new DefaultNHINDAgent(internalHISPDomains.iterator().next(),  certService, 
					certService, new DefaultTrustAnchorResolver(archorCerts));					
			
			LOGGER.debug("Successfully create NHIND agent");
			
			LOGGER.info("PostfixNHINDFilter initialization complete.");
			
		}
		catch (Exception e)
		{
			LOGGER.error("Error initializing PostfixNHINDFilter", e);
		}
	}
	
	public void service(Mail mail) throws MessagingException
	{		
		LOGGER.debug("Handling incoming message.");
		
		String domain = "";
		MimeMessage msg = mail.getMessage();		
		
		if (LOGGER.isDebugEnabled())
			dumpRawEmail(msg);
		
		
		// determine if this message needs to be encrypted/signed or decryped/verified
		InternetAddress sender = mail.getSender().toInternetAddress();//(InternetAddress)msg.getSender();

		int index = sender.getAddress().indexOf("@"); 
		if (index > -1)
			domain = sender.getAddress().substring(index + 1);
		
		if (domain.length() > 0 && !internalHISPDomains.contains(domain))
		{
			// incoming message came from another HISP source or potentially an untrusted source
			// ensure it is an smime signed message
			try
			{
			
				if (msg.getContentType().contains("multipart/signed") || msg.getContentType().contains(SMIMEStandard.EncryptedContentMediaTypeAlternative) || 
						msg.getContentType().contains(SMIMEStandard.EncryptedContentMediaType))
				{
					LOGGER.debug("Processing incoming message from external HISP.");					
					//System.out.println("Incoming message content:\r\n" +  mimeMessageToString(msg));
					
					
					IncomingMessage strippedMessage = agent.processIncoming(msg);
					
					LOGGER.debug("Message stripped.  Sending to forwarding service.");
					
					forwardMessageToSMTP(strippedMessage.getMessage().toString(), false);
				}
				else
				{
					LOGGER.debug("Message did not originate from this HISP and is not a signed message.  Discarding message.");
					
					// bail... this is not a valid incoming message
				}
			}
			catch (Throwable e)
			{
				LOGGER.error("Error handling message: " + e.getMessage(), e);
			}
		}
		else
		{			
			// mail is originating from inside the HISP
			try
			{
				if (msg.getContentType().contains("multipart/signed") || msg.getContentType().contains(SMIMEStandard.EncryptedContentMediaTypeAlternative) || 
						msg.getContentType().contains(SMIMEStandard.EncryptedContentMediaType))
				{
					// this is a signed message originating from our HISP
					// decrypt it and move it on
					LOGGER.debug("Processing signed message from internal HISP.");

					IncomingMessage strippedMessage = agent.processIncoming(msg);
					
					forwardMessageToSMTP(strippedMessage.getMessage().toString(), false);
					
				}
				else
				{
					/*
					 * TODO:  Need to handle situations where recipients may include internal domain accounts.  Some
					 * email deployments may drop the messages directly into mailboxes which will leave encrypted messages
					 * in the mailboxes.
					 */
					LOGGER.debug("Processing outgoing message.  Sign and encrypt.");					
					
					OutgoingMessage encryptedAndSignedMessage = agent.processOutgoing(mimeMessageToString(msg));		
					//System.out.println("\r\n\r\n\r\n" + encryptedAndSignedMessage);
					forwardMessageToSMTP(encryptedAndSignedMessage.getMessage().toString(), true);
				}
			}
			catch (Exception e)
			{
				LOGGER.error("Error handling message: " + e.getMessage(), e);
			}
			

		}
		
		LOGGER.debug("Exiting service.");
		
		mail.setState(Mail.GHOST);
	}
	
	private void forwardMessageToSMTP(String rawMessage, boolean outgoing)
	{
		try
		{
			
	        Properties p = new Properties();
	        
	        if (outgoing)
	        {
	        	p.put("mail.smtp.host",  (outgoingHost == null || outgoingHost.length() == 0) ? "localhost" : outgoingHost);
	        	p.put("mail.smtp.port", (outgoingPort == null || outgoingPort.length() == 0) ? "25" : outgoingPort);
	        }
	        else
	        {
	        	p.put("mail.smtp.host",  (incomingHost == null || incomingHost.length() == 0) ? "localhost" : incomingHost);
	        	p.put("mail.smtp.port", (incomingPort == null || incomingPort.length() == 0) ? "25" : incomingPort);
	        }
	        	
	        LOGGER.debug("Forwording processed message to " + p.getProperty("mail.smtp.host") + ":" +  p.getProperty("mail.smtp.port"));
	        
	        // start a session
	        javax.mail.Session session = javax.mail.Session.getInstance(p, null);
	
	        // create the message
	        MimeMessage message = new MimeMessage(session, new ByteArrayInputStream(rawMessage.getBytes("ASCII")));	        
	        
	        Transport trans = session.getTransport("smtp");
	        trans.connect();
	        
            message.saveChanges();
            trans.sendMessage(message, message.getAllRecipients());
            trans.close();
	        
		}
		catch (Throwable e)
		{
			LOGGER.error("Error fowarding message to SMTP server: " + e.getMessage(), e);
		}
	}
	
	private String mimeMessageToString(MimeMessage msg)
	{
		String retVal = "";
		try
		{    	    
    	    retVal = EntitySerializer.Default.serialize(msg);
		}
		catch (Exception e)
		{
			LOGGER.error("Error converting MimeMessage to string: " + e.getMessage(), e);
		}
		
		return retVal;
	}
	
	private void dumpRawEmail(MimeMessage msg)
	{		
    	String path = System.getProperty("user.dir") + "/tmp";
    	File tmpDir = new File(path);
    	
    	if (!tmpDir.exists())
    	{
    		if (!tmpDir.mkdir())
    			return;
    			
    	}    	
    	System.currentTimeMillis();
    	
    	File outFile = new File(path + "/rawJamesIncomingMessage_" + System.currentTimeMillis() + ".eml");
    	
    	try
    	{
    		String str = mimeMessageToString(msg);
        	
        	FileOutputStream oStream = new FileOutputStream(outFile);

        	oStream.write(str.getBytes());
        	oStream.flush();
        	oStream.close();        	
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}		
	}
	
}

