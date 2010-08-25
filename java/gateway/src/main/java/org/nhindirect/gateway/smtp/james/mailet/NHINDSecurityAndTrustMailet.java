package org.nhindirect.gateway.smtp.james.mailet;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.base.GenericMailet;
import org.nhindirect.gateway.smtp.MessageProcessResult;
import org.nhindirect.gateway.smtp.SmtpAgent;
import org.nhindirect.gateway.smtp.SmtpAgentException;
import org.nhindirect.gateway.smtp.SmtpAgentFactory;
import org.nhindirect.stagent.AddressSource;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.NHINDAddressCollection;

public class NHINDSecurityAndTrustMailet extends GenericMailet 
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(NHINDSecurityAndTrustMailet.class);	
	private SmtpAgent agent;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws MessagingException
	{
		LOGGER.info("Initializing NHINDSecurityAndTrustMailet");
		
		// Get the configuration URL
		String configURLParam = getInitParameter("ConfigURL");
		
		if (configURLParam == null || configURLParam.isEmpty())
		{
			LOGGER.error("NHINDSecurityAndTrustMailet Configuration URL cannot be empty or null.");
			throw new MessagingException("NHINDSecurityAndTrustMailet Configuration URL cannot be empty or null.");
		}	
		
		// parse into a URL and validate it is properly formed
		URL configURL = null;
		try
		{
			configURL = new URL(configURLParam);
		}
		catch (MalformedURLException ex)
		{
			LOGGER.error("Invalid configuration URL:" + ex.getMessage(), ex);
			throw new MessagingException("NHINDSecurityAndTrustMailet Configuration URL cannot be empty or null.", ex);
		}
		
		/*
		 * TODO: Add logic to determine if a different SmtpAgentConfig provider should be used base on the URL protocol.
		 * Or maybe the agent factory needs to determine what configuration provider to use?
		 */		
		try
		{
			agent = SmtpAgentFactory.createAgent(configURL);
			
		}
		catch (SmtpAgentException e)
		{
			LOGGER.error("Failed to create the SMTP agent: " + e.getMessage(), e);
			throw new MessagingException("Failed to create the SMTP agent: " + e.getMessage(), e);
		}
		
		// this should never happen because an exception should be thrown by Guice or one of the providers, but check
		// just in case...
		if (agent == null)
		{
			LOGGER.error("Failed to create the SMTP agent. Reason unknown.");
			throw new MessagingException("Failed to create the SMTP agent.  Reason unknown.");
		}		
	}

	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void service(Mail mail) throws MessagingException 
	{ 
		NHINDAddressCollection recipients = new NHINDAddressCollection();		
		
		MimeMessage msg = mail.getMessage();
		
		// uses the RCPT TO commands
		Collection<MailAddress> recips = mail.getRecipients();
		if (recips == null || recips.size() == 0)
		{
			// fall back to the mime message list of recipients
			Address[] recipsAddr = msg.getAllRecipients();
			for (Address addr : recipsAddr)
			{
				
				recipients.add(new NHINDAddress(addr.toString(), getAddressSource(addr)));
			}
		}
		else
		{
			for (MailAddress addr : recips)
			{
				recipients.add(new NHINDAddress(addr.toString(), getAddressSource(addr.toInternetAddress())));
			}
		}
		
		// get the sender
		NHINDAddress sender = new NHINDAddress(mail.getSender().toInternetAddress(), AddressSource.From);				
		
		// process the message with the agent stack
		MessageProcessResult result = agent.processMessage(msg, recipients, sender);
		
		try
		{
			if (result == null)
			{
				/*
				 * TODO: Handle exception... GHOST the message for now and eat it
				 */
				LOGGER.error("Failed to process message.  processMessage returned null.");				
				mail.setState(Mail.GHOST);
				return;
			}
		}
		catch (Throwable e)
		{
			// catch all
			/*
			 * TODO: Handle exception... GHOST the message for now and eat it
			 */
			LOGGER.error("Failed to process message: " + e.getMessage(), e);					
			mail.setState(Mail.GHOST);
			return;			
		}
		
		
		if (result.getProcessedMessage() != null)
		{
			mail.setMessage(result.getProcessedMessage().getMessage());
		}
		else
		{
			/*
			 * TODO: Handle exception... GHOST the message for now and eat it
			 */		
			mail.setState(Mail.GHOST);
		}
		
		// check to see if we need to do anything with DSN messages
		if (result.getIncomingBounceMessage() != null)
		{
			/*
			 * TODO: handle incoming bounces
			 */
		}
		
		if (result.getOutgoingBounceMessage() != null)
		{
			/*
			 * TODO: handle out going bounces
			 */
		}
		
	}
	
	/*
	 * Convert the address type to an AddressSource 
	 */
	private AddressSource getAddressSource(Address addr)
	{
		
		String type = addr.getType();
		
		if (type.equalsIgnoreCase(Message.RecipientType.TO.toString()))
		{
			return AddressSource.To;
		}
		else if (type.equalsIgnoreCase(Message.RecipientType.CC.toString()))
		{
			return AddressSource.CC;
		}
		else if (type.equalsIgnoreCase(Message.RecipientType.BCC.toString()))
		{
			return AddressSource.BCC;
		}
		else
			return null;
	}
}
