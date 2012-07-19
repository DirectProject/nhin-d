package org.nhindirect.gateway.smtp.james.mailet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import org.nhindirect.common.tx.TxDetailParser;
import org.nhindirect.common.tx.TxService;
import org.nhindirect.common.tx.TxUtil;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.common.tx.module.DefaultTxDetailParserModule;
import org.nhindirect.common.tx.module.ProviderTxServiceModule;
import org.nhindirect.common.tx.provider.NoOpTxServiceClientProvider;
import org.nhindirect.common.tx.provider.RESTTxServiceClientProvider;
import org.nhindirect.gateway.GatewayConfiguration;
import org.nhindirect.gateway.smtp.dsn.DSNCreator;
import org.nhindirect.gateway.smtp.dsn.module.DSNCreatorProviderModule;
import org.nhindirect.stagent.AddressSource;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.NHINDAddressCollection;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;

public abstract class AbstractNotificationAwareMailet extends GenericMailet
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(AbstractNotificationAwareMailet.class);	
	
	protected DSNCreator dsnCreator;
	protected TxDetailParser txParser;
	protected TxService txService;	
	
	public void init() throws MessagingException
	{
		super.init();

		Collection<Module> modules = getInitModules();
		
		// create the Tx services
		createTxServices(modules);
		
		// create the DSN creator
		createDSNGenerator(modules);

	}
	
	/**
	 * Gets a collection of Guice {@link @Module Modules} to create custom bindings for object creation.
	 * @return A collection of Guice Modules.
	 */
	protected Collection<Module> getInitModules()
	{
		return null;
	}
	
	/**
	 * Creates the DSNCreator object
	 * @param initModules The initial Guice modules created by the init() method
	 */
	protected void createDSNGenerator(final Collection<Module> initModules)
	{
		Collection<Module> modules = initModules;
		
		// attempt to use the existing modules first
		final boolean usingDefaultMoudles = (modules == null);
		if (modules == null)
		{	
			// create a default module
			final Provider<DSNCreator> provider = getDSNProvider();
			final DSNCreatorProviderModule module = DSNCreatorProviderModule.create(provider);
			modules = Arrays.asList((Module)module);
		}
		
		Injector dsnInjector = Guice.createInjector(modules);
		try
		{
			dsnCreator = dsnInjector.getInstance(DSNCreator.class);
		}
		catch (Exception e)
		{
			///CLOVER:OFF
			LOGGER.debug("First attempt to create DSNCreator failed.", e);
			if (!usingDefaultMoudles)
				LOGGER.debug("Will attempt to create from default DSNCreator from Guice module.");
			else
				LOGGER.warn("DSNCreator already attempted to use the defualt DSNCreator Guice module.  DSN creation will be disabled.");
			///CLOVER:ON

		}
		
		// if we can't create the parser or the service, and we haven't already use the default service Guice module, then
		// try again using the default Guice module
		if (dsnCreator == null && !usingDefaultMoudles)
		{
			try
			{
				// create a default module
				final Provider<DSNCreator> provider = getDSNProvider();
				final DSNCreatorProviderModule module = DSNCreatorProviderModule.create(provider);
				modules = Arrays.asList((Module)module);
				
				dsnInjector = Guice.createInjector(modules);
				dsnCreator = dsnInjector.getInstance(DSNCreator.class);
				
			}
			///CLOVER:OFF
			catch (Exception e)
			{
				LOGGER.warn("Failed to create DSNCreator.  DSN creation will be disabled.");
			}
			///CLOVER:ON
		}	
	}
	
	protected abstract Provider<DSNCreator> getDSNProvider();
	
	protected void sendDSN(Tx tx, NHINDAddressCollection undeliveredRecipeints)
	{
		try
		{
			if (dsnCreator != null)
			{
				final MimeMessage msg = dsnCreator.createDSNFailure(tx, undeliveredRecipeints);
				this.getMailetContext().sendMail(msg);
			}
		}
		catch (Throwable e)
		{
			// don't kill the process if this fails
			LOGGER.error("Error sending DSN failure message.", e);
		}
	}
	
	/**
	 * Creates a trackable monitoring object for a message. 
	 * @param msg The message that is being processed
	 * @param sender The sender of the message
	 * @return A trackable Tx object.
	 */
	protected Tx getTxToTrack(MimeMessage msg, NHINDAddress sender, NHINDAddressCollection recipients)
	{		
		if (this.txParser == null)
			return null;
				
		try
		{	
			
			final Map<String, TxDetail> details = txParser.getMessageDetails(msg);
			
			if (sender != null)
				details.put(TxDetailType.FROM.getType(), new TxDetail(TxDetailType.FROM, sender.getAddress().toLowerCase(Locale.getDefault())));
			if (recipients != null && !recipients.isEmpty())
				details.put(TxDetailType.RECIPIENTS.getType(), new TxDetail(TxDetailType.RECIPIENTS, recipients.toString().toLowerCase(Locale.getDefault())));
			
			
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
				GatewayConfiguration.getConfigurationParam(SecurityAndTrustMailetOptions.MONITORING_SERVICE_URL_PARAM, this, "");
		
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

	@SuppressWarnings("unchecked")
	protected NHINDAddressCollection getMailRecipients(Mail mail) throws MessagingException
	{
		final NHINDAddressCollection recipients = new NHINDAddressCollection();		
		
		// uses the RCPT TO commands
		final Collection<MailAddress> recips = mail.getRecipients();
		if (recips == null || recips.size() == 0)
		{
			// fall back to the mime message list of recipients
			final Address[] recipsAddr = mail.getMessage().getAllRecipients();
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
		
		return recipients;
	}
	
	protected NHINDAddress getMailSender(Mail mail) throws MessagingException
	{
		// get the sender
		final InternetAddress senderAddr = AbstractNotificationAwareMailet.getSender(mail);
		if (senderAddr == null)
			throw new MessagingException("Failed to process message.  The sender cannot be null or empty.");
						
			// not the best way to do this
		return new NHINDAddress(senderAddr, AddressSource.From);
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
}
