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
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.tx.TxUtil;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.gateway.GatewayConfiguration;
import org.nhindirect.gateway.smtp.GatewayState;
import org.nhindirect.gateway.smtp.MessageProcessResult;
import org.nhindirect.gateway.smtp.SmtpAgent;
import org.nhindirect.gateway.smtp.SmtpAgentException;
import org.nhindirect.gateway.smtp.SmtpAgentFactory;
import org.nhindirect.gateway.smtp.config.SmptAgentConfigFactory;
import org.nhindirect.gateway.smtp.config.SmtpAgentConfig;
import org.nhindirect.gateway.smtp.dsn.DSNCreator;
import org.nhindirect.gateway.smtp.dsn.provider.RejectedRecipientDSNCreatorProvider;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.NHINDAddressCollection;
import org.nhindirect.stagent.cryptography.SMIMEStandard;
import org.nhindirect.stagent.mail.notifications.NotificationMessage;
import org.nhindirect.stagent.options.OptionsManager;

import com.google.inject.Module;
import com.google.inject.Provider;

/**
 * Apache James mailet for the enforcing the NHINDirect security and trust specification.  The mailed sits between
 * the James SMTP stack and the security and trust agent.
 * @author Greg Meyer
 * @since 1.0
 */
public class NHINDSecurityAndTrustMailet extends AbstractNotificationAwareMailet 
{    
	private static final Log LOGGER = LogFactory.getFactory().getInstance(NHINDSecurityAndTrustMailet.class);	
	
	protected static final String GENERAL_DSN_OPTION = "General";
	protected static final String RELIABLE_DSN_OPTION = "ReliableAndTimely";
	
	protected SmtpAgent agent;
	protected boolean autoDSNForGeneral  = false;
	protected boolean autoDSNForTimelyAndReliable  = false;
	
	static
	{		
		initJVMParams();
	}
	
	private synchronized static void initJVMParams()
	{
		/*
		 * Mailet configuration parameters
		 */
		final Map<String, String> JVM_PARAMS = new HashMap<String, String>();
		JVM_PARAMS.put(SecurityAndTrustMailetOptions.MONITORING_SERVICE_URL_PARAM, "org.nhindirect.gateway.smtp.james.mailet.TxServiceURL");
		JVM_PARAMS.put(SecurityAndTrustMailetOptions.AUTO_DSN_FAILURE_CREATION_PARAM, "org.nhindirect.gateway.smtp.james.mailet.AutoDSNFailueCreation");
		
		OptionsManager.addInitParameters(JVM_PARAMS);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws MessagingException
	{
		LOGGER.info("Initializing NHINDSecurityAndTrustMailet");
		
		super.init();
		
		// Get the configuration URL
		final String configURLParam = getInitParameter(SecurityAndTrustMailetOptions.CONFIG_URL_PARAM);
		
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
		
		Collection<Module> modules = getInitModules();

		try
		{
			final Provider<SmtpAgentConfig> configProvider = this.getConfigProvider();
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
	
		
		// get the DSN creation options
		// default is RELIABLE_DSN_OPTION
		final String dnsCreateOptions =  GatewayConfiguration.getConfigurationParam(SecurityAndTrustMailetOptions.AUTO_DSN_FAILURE_CREATION_PARAM,
				this, RELIABLE_DSN_OPTION); 
	
		for (String dsnOption : dnsCreateOptions.split(","))
		{
			if (dsnOption.equalsIgnoreCase(RELIABLE_DSN_OPTION))
				autoDSNForTimelyAndReliable = true;
			else if(dsnOption.equalsIgnoreCase(GENERAL_DSN_OPTION))
				autoDSNForGeneral = true;
		}
				
		// set the agent and config in the Gateway state
		final GatewayState gwState = GatewayState.getInstance();
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
		
			Tx txToMonitor = null;
			
			LOGGER.trace("Entering service(Mail mail)");
			
			onPreprocessMessage(mail);
			
			final MimeMessage msg = mail.getMessage();
			
			final NHINDAddressCollection recipients = getMailRecipients(mail);
			
			// get the sender
			final NHINDAddress sender = getMailSender(mail);
			
			LOGGER.info("Proccessing incoming message from sender " + sender.toString());
			MessageProcessResult result = null;
					
			final boolean isOutgoing = this.isOutgoing(msg, sender);
			
			// if the message is outgoing, then the tracking information must be
			// gathered now before the message is transformed
			if (isOutgoing)
				txToMonitor = getTxToTrack(msg, sender, recipients);
			
			// recipients can get modified by the security and trust agent, so make a local copy
			// before processing
			final NHINDAddressCollection originalRecipList = NHINDAddressCollection.create(recipients);
			
			try
			{
				// process the message with the agent stack
				LOGGER.trace("Calling agent.processMessage");
				result = agent.processMessage(msg, recipients, sender);
				LOGGER.trace("Finished calling agent.processMessage");
				
				if (result == null)
				{				
					LOGGER.error("Failed to process message.  processMessage returned null.");		
					
					onMessageRejected(mail, originalRecipList, sender, isOutgoing, txToMonitor, null);
					
					mail.setState(Mail.GHOST);
					
					LOGGER.trace("Exiting service(Mail mail)");
					return;
				}
			}	
			catch (Exception e)
			{
				// catch all
				
				LOGGER.error("Failed to process message: " + e.getMessage(), e);					
				
				onMessageRejected(mail, originalRecipList, sender, isOutgoing, txToMonitor, e);
				
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
	
				onMessageRejected(mail, recipients, sender, null);
	
				mail.setState(Mail.GHOST);
	
				return;
			}
			
			// remove reject recipients from the RCTP headers
			if (result.getProcessedMessage().getRejectedRecipients() != null && 
					result.getProcessedMessage().getRejectedRecipients().size() > 0 && mail.getRecipients() != null &&
					mail.getRecipients().size() > 0)
			{
				
				final Collection<MailAddress> newRCPTList = new ArrayList<MailAddress>();
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
			final Collection<NotificationMessage> notifications = result.getNotificationMessages();
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
			
			// track message
			trackMessage(txToMonitor, isOutgoing);
			
			onPostprocessMessage(mail, result, isOutgoing, txToMonitor);
			
			LOGGER.trace("Exiting service(Mail mail)");
		}
		finally
		{
			GatewayState.getInstance().unlockFromProcessing();
		}
	}
	
	
	
	/*
	 * 
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
	 * Overridable method for custom processing when a message is rejected by the SMTP agent.  Includes the tracking information
	 * if available and the message direction.  For passivity, this method calls {@link #onMessageRejected(Mail, NHINDAddressCollection, NHINDAddress, Throwable)}
	 * by default after performing its operations.
	 * @param message The mail message that the agent attempted to process. 
	 * @param recipients A collection of recipients that this message was intended to be delievered to.
	 * @param sender The sender of the message.
	 * @param isOutgoing Indicate the direction of the message: incoming or outgoing.
	 * @param tx Contains tracking information if available.  Generally this information will only be available for outgoing messages
	 * as rejected incoming messages more than likely will not have been decrypted yet.
	 * @param t Exception thrown by the agent when the message was rejected.  May be null;
	 */
	protected void onMessageRejected(Mail mail, NHINDAddressCollection recipients, NHINDAddress sender, boolean isOutgoing,
			Tx tx, Throwable t)
	{
		// if this is an outgoing IMF message, then we may need to send a DSN message
		boolean sendDSN = false;
		if (isOutgoing && tx != null && tx.getMsgType() == TxMessageType.IMF)
		{
			final boolean timely = TxUtil.isReliableAndTimelyRequested(tx);
			if ((timely && this.autoDSNForTimelyAndReliable) ||
					(!timely && this.autoDSNForGeneral))
				sendDSN = true;
		}
		
		if (sendDSN)
			sendDSN(tx, recipients);
		
		this.onMessageRejected(mail, recipients, sender, t);
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
	
	/**
	 * Overridable method for custom processing after the message has been processed by the SMTP agent.  Includes the tracking information
	 * if available and the message direction.  For passivity, this method calls {@link #onPostprocessMessage(Mail, MessageProcessResult)}
	 * by default after performing its operations.
	 * @param mail The incoming mail message.  The contents of the message may have changed from when it was originally
	 * received. 
	 * @param result Contains results of the message processing including the resulting message.
	 * @param isOutgoing Indicate the direction of the message: incoming or outgoing.
	 * @param tx Contains tracking information if available.
	 */
	protected void onPostprocessMessage(Mail mail, MessageProcessResult result, boolean isOutgoing, Tx tx)
	{
		// if there are rejected recipients and an outgoing IMF message, then we may need to send a DSN message
		boolean sendDSN = false;
		if (isOutgoing && tx != null && tx.getMsgType() == TxMessageType.IMF && result.getProcessedMessage().hasRejectedRecipients())
		{
			final boolean timely = TxUtil.isReliableAndTimelyRequested(tx);
			if ((timely && this.autoDSNForTimelyAndReliable) ||
					(!timely && this.autoDSNForGeneral))
				sendDSN = true;
		}
		
		if (sendDSN)
			sendDSN(tx, result.getProcessedMessage().getRejectedRecipients());
		
		this.onPostprocessMessage(mail, result);
	}
		
	
	/**
	 * Determines if a message is incoming or outgoing based on the domains available in the configured agent
	 * and the sender of the message.
	 * @param msg The message that is being processed.
	 * @param sender The sender of the message.
	 * @return true if the message is determined to be outgoing; false otherwise
	 */
	protected boolean isOutgoing(MimeMessage msg, NHINDAddress sender)
	{		
		if (agent.getAgent() == null || agent.getAgent().getDomains() == null)
			return false;
		
		// if the sender is not from our domain, then is has to be an incoming message
		if (!sender.isInDomain(agent.getAgent().getDomains()))
			return false;
		else
		{
			// depending on the SMTP stack configuration, a message with a sender from our domain
			// may still be an incoming message... check if the message is encrypted
			if (SMIMEStandard.isEncrypted(msg))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Provider<DSNCreator> getDSNProvider() 
	{
		return new RejectedRecipientDSNCreatorProvider(this);
	}
	
	/**
	 * Tracks message that meet the following qualifications
	 * <br>
	 * 1. Outgoing IMF message
	 * @param tx The message to monitor and track
	 * @param isOutgoing Indicates the message direction: incoming or outgoing
	 */
	protected void trackMessage(Tx tx, boolean isOutgoing)
	{
		// only track the following message..
		// 1. Outgoing IMF message
		boolean track = false;
		if (tx != null)
		{
			switch (tx.getMsgType())
			{
				case IMF:
				{
					track = isOutgoing;
					break;
				}
			}
		}
		
		if (track)
		{
			try
			{
				txService.trackMessage(tx);
			}
			catch (ServiceException ex)
			{
				LOGGER.warn("Failed to submit message to monitoring service.", ex);
			}
		}
		
	}

	
	/**
	 * Shutsdown the gateway and cleans up resources associated with it.
	 */
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
