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

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mailet.Mail;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.gateway.GatewayConfiguration;
import org.nhindirect.gateway.smtp.dsn.DSNCreator;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.NHINDAddressCollection;
import org.nhindirect.stagent.mail.notifications.MDNStandard;
import org.nhindirect.stagent.options.OptionsManager;

import com.google.inject.Provider;

/**
 * This mailet determines if a notification messages need to be suppressed from delivery to the original message's edge client.
 * @author Greg Meyer
 * @since 2.0
 */
public class NotificationSuppressor extends AbstractNotificationAwareMailet
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(NotificationSuppressor.class);	
	protected boolean consumeMDNProcessed;
	
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
		JVM_PARAMS.put(SecurityAndTrustMailetOptions.CONSUME_MND_PROCESSED_PARAM, "org.nhindirect.gateway.smtp.james.mailet.ConsumeMDNProcessed");
		
		OptionsManager.addInitParameters(JVM_PARAMS);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws MessagingException
	{
		LOGGER.info("Initializing NotificationSupressor");
		
		super.init();
			
		// get the consume processed MDN message setting
		// default is false
		consumeMDNProcessed = GatewayConfiguration.getConfigurationParamAsBoolean(SecurityAndTrustMailetOptions.CONSUME_MND_PROCESSED_PARAM,
				this, false);
		

		LOGGER.info("NotificationSupressor initialization complete.");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void service(Mail mail) throws MessagingException 
	{ 		
		boolean suppress = false;
		
		final MimeMessage msg = mail.getMessage();
		
		final NHINDAddressCollection recipients = getMailRecipients(mail);
		
		final NHINDAddress sender = getMailSender(mail);
			
		final Tx txToTrack = getTxToTrack(msg, sender, recipients);		
		
		if (txToTrack != null)
		{
			try
			{
				// first check if this a MDN processed message and if the consume processed flag is turned on
				final TxDetail detail = txToTrack.getDetail(TxDetailType.DISPOSITION);
				if (consumeMDNProcessed && txToTrack.getMsgType() == TxMessageType.MDN 
						&& detail != null && detail.getDetailValue().contains(MDNStandard.Disposition_Processed))
					suppress = true;
				// if the first rule does not apply, then go to the tx Service to see if the message should be suppressed
				else if (txService != null && txToTrack != null && txService.suppressNotification(txToTrack))
					suppress = true;
			}
			catch (ServiceException e)
			{
				// failing to call the txService should not result in an exception being thrown
				// from this service.
				LOGGER.warn("Failed to get notification suppression status from service.  Message will assume to not need supressing.");
			}
		}
		
		if (suppress)
			mail.setState(Mail.GHOST);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Provider<DSNCreator> getDSNProvider() 
	{
		return null;
	}
}
