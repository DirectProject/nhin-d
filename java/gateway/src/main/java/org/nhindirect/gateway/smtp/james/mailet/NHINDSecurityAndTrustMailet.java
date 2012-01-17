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
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.base.GenericMailet;
import org.nhindirect.gateway.smtp.DefaultSmtpAgent;
import org.nhindirect.gateway.smtp.GatewayState;
import org.nhindirect.gateway.smtp.MessageProcessResult;
import org.nhindirect.gateway.smtp.SmtpAgent;
import org.nhindirect.gateway.smtp.SmtpAgentException;
import org.nhindirect.gateway.smtp.SmtpAgentFactory;
import org.nhindirect.gateway.smtp.config.SmptAgentConfigFactory;
import org.nhindirect.gateway.smtp.config.SmtpAgentConfig;
import org.nhindirect.stagent.AddressSource;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.NHINDAddressCollection;
import org.nhindirect.stagent.mail.notifications.NotificationMessage;

import com.google.inject.Module;
import com.google.inject.Provider;

/**
 * Apache James mailet for the enforcing the NHINDirect security and trust specification.  The mailed sits between
 * the James SMTP stack and the security and trust agent.
 * @author Greg Meyer
 */
public class NHINDSecurityAndTrustMailet extends GenericMailet 
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(NHINDSecurityAndTrustMailet.class);	
	protected SmtpAgent agent;
	
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
			Collection<Module> modules = getInitModules();
			Provider<SmtpAgentConfig> configProvider = this.getConfigProvider();
			agent = SmtpAgentFactory.createAgent(configURL, configProvider, null, modules);
			
		}
		catch (SmtpAgentException e)
		{
			LOGGER.error("Failed to create the SMTP agent: " + e.getMessage(), e);
			throw new MessagingException("Failed to create the SMTP agent: " + e.getMessage(), e);
		}		
		
		// this should never happen because an exception should be thrown by Guice or one of the providers, but check
		// just in case...
		///CLOVER:OFF
		if (agent == null)
		{
			LOGGER.error("Failed to create the SMTP agent. Reason unknown.");
			throw new MessagingException("Failed to create the SMTP agent.  Reason unknown.");
		}	
		///CLOVER:ON
		
		// set the agent and config in the Gateway state
		GatewayState gwState = GatewayState.getInstance();
		if (gwState.isAgentSettingManagerRunning())
			gwState.stopAgentSettingsManager();
		
		gwState.setSmtpAgent(agent);
		gwState.setSmptAgentConfig(SmptAgentConfigFactory.createSmtpAgentConfig(configURL, this.getConfigProvider(), null));
		gwState.startAgentSettingsManager();
		
		LOGGER.info("NHINDSecurityAndTrustMailet initialization complete.");
	}

	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void service(Mail mail) throws MessagingException 
	{ 		
		GatewayState.getInstance().lockForProcessing();
		try
		{
		
			LOGGER.trace("Entering service(Mail mail)");
			
			onPreprocessMessage(mail);
			
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
					
					recipients.add(new NHINDAddress(addr.toString(), (AddressSource)null));
				}
			}
			else
			{
				for (MailAddress addr : recips)
				{
					recipients.add(new NHINDAddress(addr.toString(), (AddressSource)null));
				}
			}
			
			// get the sender
			InternetAddress senderAddr = NHINDSecurityAndTrustMailet.getSender(mail);
			if (senderAddr == null)
				throw new MessagingException("Failed to process message.  The sender cannot be null or empty.");
							
				// not the best way to do this
			NHINDAddress sender = new NHINDAddress(senderAddr, AddressSource.From);	
			
			LOGGER.info("Proccessing incoming message from sender " + sender.toString());
			MessageProcessResult result = null;
					
			try
			{
				// process the message with the agent stack
				LOGGER.trace("Calling agent.processMessage");
				result = agent.processMessage(msg, recipients, sender);
				LOGGER.trace("Finished calling agent.processMessage");
				
				if (result == null)
				{				
					LOGGER.error("Failed to process message.  processMessage returned null.");		
					
					onMessageRejected(mail, recipients, sender, null);
					
					mail.setState(Mail.GHOST);
					
					LOGGER.trace("Exiting service(Mail mail)");
					return;
				}
			}	
			catch (Exception e)
			{
				// catch all
				
				LOGGER.error("Failed to process message: " + e.getMessage(), e);					
				
				onMessageRejected(mail, recipients, sender, e);
				
				mail.setState(Mail.GHOST);
				LOGGER.trace("Exiting service(Mail mail)");
				
				if (e instanceof SmtpAgentException)
					throw (SmtpAgentException)e;
				
				throw new MessagingException("Failed to process message: " + e.getMessage());
	
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
	
				onMessageRejected(mail, recipients, sender, null);
	
				mail.setState(Mail.GHOST);
	
				return;
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
			
			onPostprocessMessage(mail, result);
			
			LOGGER.trace("Exiting service(Mail mail)");
		}
		finally
		{
			GatewayState.getInstance().unlockFromProcessing();
		}
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
	
	/**
	 * Gets a collection of Guice {@link @Module Modules} to create custom bindings for Agent creation.  If this method returns null,
	 * the Mailet will use the simple {@link SmtpAgentFactory#createAgent(URL)} method to create the {@link SmtpAgent) and 
	 * {@link NHINDAgent}.
	 * @return A collection of Guice Modules.
	 */
	protected Collection<Module> getInitModules()
	{
		return null;
	}
	
	/**
	 * Gets a custom configuration provider.  If this is null, the system will us a default provider.
	 * @return Gets a custom configuration provider.
	 */
	protected Provider<SmtpAgentConfig> getConfigProvider()
	{
		return null;
	}
	
	/**
	 * Overridable method for custom processing before the message is submitted to the SMTP agent.  
	 * @param mail The incoming mail message.
	 */
	protected void onPreprocessMessage(Mail mail)
	{
		/* no-op */
	}
	
	/**
	 * Overridable method for custom processing when a message is rejected by the SMTP agent.
	 * @param message The mail message that the agent attempted to process. 
	 * @param recipients A collection of recipients that this message was intended to be delievered to.
	 * @param sender The sender of the message.
	 * @param t Exception thrown by the agent when the message was rejected.  May be null;
	 */
	protected void onMessageRejected(Mail mail, NHINDAddressCollection recipients, NHINDAddress sender, Throwable t)
	{
		/* no-op */
	}
	
	/**
	 * Overridable method for custom processing after the message has been processed by the SMTP agent.  
	 * @param mail The incoming mail message.  The contents of the message may have changed from when it was originally
	 * received. 
	 * @param result Contains results of the message processing including the resulting message.
	 */
	protected void onPostprocessMessage(Mail mail, MessageProcessResult result)
	{
		/* no-op */
	}
	
	public static InternetAddress getSender(Mail mail) 
	{
		InternetAddress retVal = null;
		
		if (mail.getSender() != null)
			retVal = mail.getSender().toInternetAddress();	
		else
		{
			// try to get the sender from the message
			Address[] senderAddr = null;
			try
			{
				if (mail.getMessage() == null)
					return null;
				
				senderAddr = mail.getMessage().getFrom();
				if (senderAddr == null || senderAddr.length == 0)
					return null;
			}
			catch (MessagingException e)
			{
				return null;
			}
						
			// not the best way to do this
			retVal = (InternetAddress)senderAddr[0];	
		}
	
		return retVal;
	}
	
	public void shutdown()
	{
		GatewayState.getInstance().lockForUpdating();
		try
		{
			// place holder for shutdown code
		}
		finally
		{
			GatewayState.getInstance().unlockFromUpdating();
		}
	}
}
