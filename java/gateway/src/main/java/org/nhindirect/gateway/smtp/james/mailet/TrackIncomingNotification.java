package org.nhindirect.gateway.smtp.james.mailet;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mailet.Mail;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.gateway.smtp.dsn.DSNCreator;
import org.nhindirect.gateway.smtp.dsn.provider.FailedDeliveryDSNCreatorProvider;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.NHINDAddressCollection;
import org.nhindirect.stagent.options.OptionsManager;

import com.google.inject.Provider;

public class TrackIncomingNotification extends AbstractNotificationAwareMailet
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(TrackIncomingNotification.class);	

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
		
		OptionsManager.addInitParameters(JVM_PARAMS);
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws MessagingException
	{
		super.init();
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void service(Mail mail) throws MessagingException 
	{ 		
		LOGGER.debug("Calling track incoming notification service");
		final MimeMessage msg = mail.getMessage();

		final NHINDAddressCollection recipients = getMailRecipients(mail);
		
		final NHINDAddress sender = getMailSender(mail);
		
		final Tx txToMonitor = getTxToTrack(msg, sender, recipients);
	
		// track message
		if (txToMonitor != null && (txToMonitor.getMsgType() == TxMessageType.DSN || 
				txToMonitor.getMsgType() == TxMessageType.MDN))
		{			
			try
			{
				txService.trackMessage(txToMonitor);
			}
			catch (ServiceException ex)
			{
				LOGGER.warn("Failed to submit message to monitoring service.", ex);
			}
		}
		
		LOGGER.debug("Exiting track incoming notification service");
	}
	

	
	@Override
	protected Provider<DSNCreator> getDSNProvider() 
	{
		return new FailedDeliveryDSNCreatorProvider(this);
	}
}
