/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
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

package org.nhindirect.gateway.smtp.james.mailet;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
import org.nhindirect.gateway.smtp.SmtpAgentSettings;
import org.nhindirect.stagent.AddressSource;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.NHINDAddressCollection;
import org.nhindirect.stagent.mail.notifications.NotificationMessage;

/**
 * Apache James mailet for the enforcing the NHINDirect security and trust specification.  The mailed sits between
 * the James SMTP stack and the security and trust agent.
 * @author Greg Meyer
 */
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

		LOGGER.info("NHINDSecurityAndTrustMailet initialization complete.");
	}

	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void service(Mail mail) throws MessagingException 
	{ 		
		LOGGER.trace("Entering service(Mail mail)");
		
		
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
		
		LOGGER.info("Proccessing incoming message from sender " + mail.getSender().toInternetAddress());
		
		// process the message with the agent stack
		LOGGER.trace("Calling agent.processMessage");
		MessageProcessResult result = agent.processMessage(msg, recipients, sender);
		LOGGER.trace("Finished calling agent.processMessage");
		
		try
		{
			if (result == null)
			{
				/*
				 * TODO: Handle exception... GHOST the message for now and eat it
				 */
				LOGGER.error("Failed to process message.  processMessage returned null.");				
				mail.setState(Mail.GHOST);
				
				LOGGER.trace("Exiting service(Mail mail)");
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
			LOGGER.trace("Exiting service(Mail mail)");
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
			LOGGER.debug("Processed message is null.  GHOST and eat the message.");
			mail.setState(Mail.GHOST);
		}
		
		// remove reject recipients from the RCTP headers
		if (result.getProcessedMessage().getRejectedRecipients() != null && 
				result.getProcessedMessage().getRejectedRecipients().size() > 0 && mail.getRecipients() != null &&
				mail.getRecipients().size() > 0)
		{
			
			Collection<MailAddress> newRCPTList = new ArrayList<MailAddress>();
			for (MailAddress rctpAdd : (Collection<MailAddress>)mail.getRecipients())
			{
				if (!isRcptRejected(rctpAdd, result.getProcessedMessage().getRejectedRecipients()))
				{
					newRCPTList.add(rctpAdd);
				}
			}
			
			mail.setRecipients(newRCPTList);
		}
		
		/*
		 * Handle sending MDN messages
		 */
		Collection<NotificationMessage> notifications = result.getNotificationMessages();
		if (notifications != null && notifications.size() > 0)
		{
			LOGGER.info("MDN messages requested.  Sending MDN \"processed\" messages");
			// create a message for each notification and put it on James "stack"
			for (NotificationMessage message : notifications)
			{
				try
				{
					this.getMailetContext().sendMail(message);
				}
				catch (Throwable t)
				{
					// don't kill the process if this fails
					LOGGER.error("Error sending MDN message.", t);
				}
			}
		}
		
		
		LOGGER.trace("Exiting service(Mail mail)");
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
	
	/*
	 * Determine if the recipient has been rejected
	 * 
	 * @param rejectedRecips
	 */
	private boolean isRcptRejected(MailAddress rctpAdd, NHINDAddressCollection rejectedRecips)
	{
		for (NHINDAddress rejectedRecip : rejectedRecips)
			if (rejectedRecip.getAddress().equals(rctpAdd.toInternetAddress().toString()))
				return true;
		
		return false;
	}
}
