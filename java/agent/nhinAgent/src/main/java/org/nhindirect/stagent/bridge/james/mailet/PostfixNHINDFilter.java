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
import org.nhindirect.stagent.NHINDAgent;
import org.nhindirect.stagent.cert.ICertificateService;
import org.nhindirect.stagent.cert.impl.KeyStoreCertificateService;
import org.nhindirect.stagent.parser.EntitySerializer;
import org.nhindirect.stagent.parser.Protocol;
import org.nhindirect.stagent.trust.impl.UniformTrustSettings;


/** 
 * This is a proof of concept bridge between an Apache James mail server and the NHIN agent implemented as
 * a Mailet.  
 * @author Greg Meyer
 *
 */
public class PostfixNHINDFilter extends GenericMailet
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(PostfixNHINDFilter.class);	
	
	private NHINDAgent agent;
	private Set<String> internalHISPDomains = new HashSet<String>();
	
	private String incomingHost;
	private String incomingPort;
	private String outgoingHost;
	private String outgoingPort;
	private boolean passThroughLocalRecips = true;
	
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
			
			ICertificateService certService = null;
			
			String certStoreClassName = getInitParameter("CertStoreImpl");
			
			LOGGER.debug("CertStoreImpl name: " + certStoreClassName);
			
			Class<?> certStoreClass = Class.forName(certStoreClassName);
			Object certStoreImpl = certStoreClass.newInstance();
			if (certStoreImpl instanceof KeyStoreCertificateService)
			{
				KeyStoreCertificateService certServiceHolder = (KeyStoreCertificateService)certStoreImpl;
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
			
			agent = new NHINDAgent(internalHISPDomains.iterator().next(),  certService, 
					certService, new UniformTrustSettings(archorCerts));					
			
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
			
				if (msg.getContentType().contains("multipart/signed") || msg.getContentType().contains(Protocol.EncryptedContentMediaTypeAlternative) || 
						msg.getContentType().contains(Protocol.EncryptedContentMediaType))
				{
					LOGGER.debug("Processing incoming message from external HISP.");					
					//System.out.println("Incoming message content:\r\n" +  mimeMessageToString(msg));
					
					
					String strippedMessage = agent.processIncoming(msg);
					
					LOGGER.debug("Message stripped.  Sending to forwarding service.");
					
					forwardMessageToSMTP(strippedMessage, false);
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
				if (msg.getContentType().contains("multipart/signed") || msg.getContentType().contains(Protocol.EncryptedContentMediaTypeAlternative) || 
						msg.getContentType().contains(Protocol.EncryptedContentMediaType))
				{
					// this is a signed message originating from our HISP
					// decrypt it and move it on
					LOGGER.debug("Processing signed message from internal HISP.");

					String strippedMessage = agent.processIncoming(msg);
					
					forwardMessageToSMTP(strippedMessage, false);
					
				}
				else
				{
					/*
					 * TODO:  Need to handle situations where recipients may include internal domain accounts.  Some
					 * email deployments may drop the messages directly into mailboxes which will leave encrypted messages
					 * in the mailboxes.
					 */
					LOGGER.debug("Processing outgoing message.  Sign and encrypt.");					
					
					String encryptedAndSignedMessage = agent.processOutgoing(mimeMessageToString(msg));		
					//System.out.println("\r\n\r\n\r\n" + encryptedAndSignedMessage);
					forwardMessageToSMTP(encryptedAndSignedMessage, true);
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

