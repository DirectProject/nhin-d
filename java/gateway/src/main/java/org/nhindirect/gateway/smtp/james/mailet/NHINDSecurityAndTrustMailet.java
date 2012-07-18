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
import java.util.Locale;
import java.util.Map;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.base.GenericMailet;
import org.nhindirect.common.mail.MDNStandard;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.tx.TxDetailParser;
import org.nhindirect.common.tx.TxService;
import org.nhindirect.common.tx.TxUtil;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.common.tx.module.DefaultTxDetailParserModule;
import org.nhindirect.common.tx.module.ProviderTxServiceModule;
import org.nhindirect.common.tx.provider.NoOpTxServiceClientProvider;
import org.nhindirect.common.tx.provider.RESTTxServiceClientProvider;
import org.nhindirect.gateway.smtp.GatewayState;
import org.nhindirect.gateway.smtp.MessageProcessResult;
import org.nhindirect.gateway.smtp.SmtpAgent;
import org.nhindirect.gateway.smtp.SmtpAgentException;
import org.nhindirect.gateway.smtp.SmtpAgentFactory;
import org.nhindirect.gateway.smtp.config.SmptAgentConfigFactory;
import org.nhindirect.gateway.smtp.config.SmtpAgentConfig;
import org.nhindirect.gateway.smtp.dsn.DSNCreator;
import org.nhindirect.gateway.smtp.dsn.impl.RejectedRecipientDSNCreator;
import org.nhindirect.stagent.AddressSource;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.NHINDAddressCollection;
import org.nhindirect.stagent.cryptography.SMIMEStandard;
import org.nhindirect.stagent.mail.notifications.NotificationMessage;
import org.nhindirect.stagent.options.OptionsManager;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;

/**
 * Apache James mailet for the enforcing the NHINDirect security and trust specification.  The mailed sits between
 * the James SMTP stack and the security and trust agent.
 * @author Greg Meyer
 * @since 1.0
 */
public class NHINDSecurityAndTrustMailet extends GenericMailet 
{    
	private static final Log LOGGER = LogFactory.getFactory().getInstance(NHINDSecurityAndTrustMailet.class);	
	
	protected static final String GENERAL_DSN_OPTION = "General";
	protected static final String RELIABLE_DSN_OPTION = "ReliableAndTimely";
	
	protected SmtpAgent agent;
	protected TxDetailParser txParser;
	protected TxService txService;	
	protected boolean consumeMDNProcessed;
	protected boolean autoDSNForGeneral  = false;
	protected boolean autoDSNForTimelyAndReliable  = false;
	protected DSNCreator dsnCreator;
	
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
		JVM_PARAMS.put(SecurityAndTrustMailetOptions.CONSUME_MND_PROCESSED_PARAM, "org.nhindirect.gateway.smtp.james.mailet.ConsumeMDNProcessed");
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
		
		// create the Tx services
		createTxServices(modules);
		
		// get the consume processed MDN message setting
		// default is faluse
		consumeMDNProcessed = SecurityAndTrustMailetOptions.getConfigurationParamAsBoolean(SecurityAndTrustMailetOptions.CONSUME_MND_PROCESSED_PARAM,
				this, false);
		
		// get the DSN creation options
		// default is RELIABLE_DSN_OPTION
		final String dnsCreateOptions =  SecurityAndTrustMailetOptions.getConfigurationParam(SecurityAndTrustMailetOptions.AUTO_DSN_FAILURE_CREATION_PARAM,
				this, RELIABLE_DSN_OPTION); 
	
		for (String dsnOption : dnsCreateOptions.split(","))
		{
			if (dsnOption.equalsIgnoreCase(RELIABLE_DSN_OPTION))
				autoDSNForTimelyAndReliable = true;
			else if(dsnOption.equalsIgnoreCase(GENERAL_DSN_OPTION))
				autoDSNForGeneral = true;
		}
		
		// create the DSN creator
		// TODO: maybe do this with Guice to be consistent with the creation of other objects
		dsnCreator = new RejectedRecipientDSNCreator(this);
		
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
			
			final NHINDAddressCollection recipients = new NHINDAddressCollection();		
			
			final MimeMessage msg = mail.getMessage();
			
			// uses the RCPT TO commands
			final Collection<MailAddress> recips = mail.getRecipients();
			if (recips == null || recips.size() == 0)
			{
				// fall back to the mime message list of recipients
				final Address[] recipsAddr = msg.getAllRecipients();
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
			final InternetAddress senderAddr = NHINDSecurityAndTrustMailet.getSender(mail);
			if (senderAddr == null)
				throw new MessagingException("Failed to process message.  The sender cannot be null or empty.");
							
				// not the best way to do this
			final NHINDAddress sender = new NHINDAddress(senderAddr, AddressSource.From);	
			
			LOGGER.info("Proccessing incoming message from sender " + sender.toString());
			MessageProcessResult result = null;
					
			final boolean isOutgoing = this.isOutgoing(msg, sender);
			
			// if the message is outgoing, then the tracking information must be
			// gathered now before the message is transformed
			if (isOutgoing)
				txToMonitor = getTxToTrack(msg, sender);
			
			try
			{
				// process the message with the agent stack
				LOGGER.trace("Calling agent.processMessage");
				result = agent.processMessage(msg, recipients, sender);
				LOGGER.trace("Finished calling agent.processMessage");
				
				if (result == null)
				{				
					LOGGER.error("Failed to process message.  processMessage returned null.");		
					
					onMessageRejected(mail, recipients, sender, isOutgoing, txToMonitor, null);
					
					mail.setState(Mail.GHOST);
					
					LOGGER.trace("Exiting service(Mail mail)");
					return;
				}
			}	
			catch (Exception e)
			{
				// catch all
				
				LOGGER.error("Failed to process message: " + e.getMessage(), e);					
				
				onMessageRejected(mail, recipients, sender, isOutgoing, txToMonitor, e);
				
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
			
			
			// determine now if the message is incoming.... if it is incoming,
			// then the message has been decrypted and the tracking information can
			// be extraced
			if (!isOutgoing)
				txToMonitor = getTxToTrack(result.getProcessedMessage().getMessage(), sender);
			
			// track message
			trackMessage(txToMonitor, isOutgoing);
			
			// determine if this is a message message that needs to be consumed
			if (consumeMessage(txToMonitor, isOutgoing))
				mail.setState(Mail.GHOST);
			
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
	
	protected void sendDSN(Tx tx,  NHINDAddressCollection failedRecipeints)
	{
		try
		{
			final MimeMessage msg = dsnCreator.createDSNFailure(tx, failedRecipeints);
			this.getMailetContext().sendMail(msg);
		}
		catch (Throwable e)
		{
			// don't kill the process if this fails
			LOGGER.error("Error sending DSN failure message.", e);
		}
	}
	
	/**
	 * Gets the sender attribute of a Mail message
	 * @param mail The message to retrive the sender from
	 * @return The message sender.
	 */
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
	 * Creates a trackable monitoring object for a message. 
	 * @param msg The message that is being processed
	 * @param sender The sender of the message
	 * @return A trackable Tx object.
	 */
	protected Tx getTxToTrack(MimeMessage msg, NHINDAddress sender)
	{		
		if (this.txParser == null)
			return null;
				
		try
		{	
			final Map<String, TxDetail> details = txParser.getMessageDetails(msg);
			return new Tx(TxUtil.getMessageType(msg), details);
		}
		///CLOVER:OFF
		catch (Exception e)
		{
			LOGGER.warn("Failed to parse message to Tx object.", e);
			return null;
		}
		///CLOVER:ON
	}
	
	/**
	 * Creates Guice modules to instantiate the default tracking service.  If a tracking/monitoring service URL is present in either the
	 * mailet configuration MessageMonitoringServiceURL parameter or in the agent OptionsParameter named org.nhindirect.gateway.smtp.james.mailet.TxServiceURL, 
	 * then an instance of the RESTful Tx service will be created.  Otherwise, a NoOp Tx service will be created resulting in
	 * no messages being tracked and monitoried.
	 * @return A collection of Guice modules to be used by an injector to create the TxService instance.
	 */
	protected Collection<Module> createDefaultTxServiceModules()
	{
		final Collection<Module> modules = new ArrayList<Module>();
		modules.add(DefaultTxDetailParserModule.create());
		
		
		// default implementation will use try to use the REST based service
		// must first determine if the REST service url exist
		final String monitoringURLParam = 
				SecurityAndTrustMailetOptions.getConfigurationParam(SecurityAndTrustMailetOptions.MONITORING_SERVICE_URL_PARAM, this, "");
		
		if (!monitoringURLParam.isEmpty())
			modules.add(ProviderTxServiceModule.create(new RESTTxServiceClientProvider(monitoringURLParam)));
		else
		{
			LOGGER.info("MessageMonitoringServiceURL is null or empty.  Will fall back to the the NoOp message monitor.");
			// use the no-op provider if the service URL is not available
			modules.add(ProviderTxServiceModule.create(new NoOpTxServiceClientProvider()));
		}
		
		return modules;
	}

	
	/**
	 * Tracks message that meet the following qualifications
	 * <br>
	 * 1. Outgoing IMF message
	 * <br>
	 * 2. Incoming MDN message
	 * <br>
	 * 3. Incoming DSN message
	 * @param tx The message to monitor and track
	 * @param isOutgoing Indicates the message direction: incoming or outgoing
	 */
	protected void trackMessage(Tx tx, boolean isOutgoing)
	{
		// only track the following message..
		// 1. Outgoing IMF message
		// 2. Incoming MDN message
		// 3. Incoming DSN message
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
				case DSN:
				case MDN:
				{
					track = !isOutgoing;
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
	 * Creates the Tx services
	 * @param initModules The initial Guice modules created by the init() method
	 */
	protected void createTxServices(final Collection<Module> initModules)
	{
		Collection<Module> modules = initModules;
		
		// now try to get the TxParser
		// attempt to use the existing modules first
		final boolean usingDefaultTxServiceMoudles = (modules == null);
		if (modules == null)
		{
			// create a default module for the TxService
			modules = createDefaultTxServiceModules();
		}
		
		Injector txInjector = Guice.createInjector(modules);
		try
		{
			txParser = txInjector.getInstance(TxDetailParser.class);
			txService = txInjector.getInstance(TxService.class);
		}
		catch (Exception e)
		{

			LOGGER.debug("First attempt to create message monitoring service failed.", e);
			if (!usingDefaultTxServiceMoudles)
				LOGGER.debug("Will attempt to create from default Tx service Guice module.");
			///CLOVER:OFF
			else
				LOGGER.warn("Monitoring service already attempted to use the defualt Tx service Guice module.  Monitoring will be disabled.");
			///CLOVER:ON

		}
		
		// if we can't create the parser or the service, and we haven't already use the default service Guice module, then
		// try again using the default Guice module
		if ((txParser == null || txService == null) && !usingDefaultTxServiceMoudles)
		{
			try
			{
				// create a default module for the TxService
				modules = createDefaultTxServiceModules();
				txInjector = Guice.createInjector(modules);
				if (txParser == null)
					txParser = txInjector.getInstance(TxDetailParser.class);
				
				if (txService == null)
					txService = txInjector.getInstance(TxService.class);
				
			}
			///CLOVER:OFF
			catch (Exception e)
			{
				LOGGER.warn("Failed to create message monitoring service.  Monitoring will be disabled");
			}
			///CLOVER:ON
		}		
	}
	
	/**
	 * Determines if a message should be consumed by the gateway
	 * @param tx The message that is being evaluated
	 * @param isOutgoing Indicates the message directions: incoming or outgoing
	 * @return true if the message should be consumed by the gateway; false otherwise
	 */
	protected boolean consumeMessage(Tx tx, boolean isOutgoing)
	{
		boolean consumeMessage = false;
		
		if (tx != null)
		{
			switch (tx.getMsgType())
			{
				case MDN:
				{
					// incoming MDN message
					if (consumeMDNProcessed && !isOutgoing)
					{
						// check for "processed" disposition
						final TxDetail detail = tx.getDetail(TxDetailType.DISPOSITION);
						if (detail != null)
						{
							if (detail.getDetailValue().contains(MDNStandard.Disposition_Processed.toLowerCase(Locale.getDefault())))
								consumeMessage = true;
						}		
					}
					break;
				}				
			}
		}
		
		return consumeMessage;
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
