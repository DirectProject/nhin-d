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

import java.lang.reflect.Method;
import java.util.Collection;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mailet.Mail;
import org.apache.mailet.Mailet;
import org.apache.mailet.MailetConfig;
import org.nhindirect.common.mail.MDNStandard;
import org.nhindirect.common.tx.TxUtil;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.gateway.smtp.NotificationProducer;
import org.nhindirect.gateway.smtp.NotificationSettings;
import org.nhindirect.gateway.smtp.ReliableDispatchedNotificationProducer;
import org.nhindirect.gateway.smtp.dsn.DSNCreator;
import org.nhindirect.gateway.smtp.dsn.provider.FailedDeliveryDSNCreatorProvider;
import org.nhindirect.stagent.IncomingMessage;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.NHINDAddressCollection;
import org.nhindirect.stagent.mail.Message;
import org.nhindirect.stagent.mail.notifications.NotificationMessage;


import com.google.inject.Provider;

/**
 * This mailet override the built in Apache James LocalDelivery mailet and sends an MDN dispatched message on successful delivery to a local mailbox
 * if the message request timely and reliable message delivery.
 * In addition, it also sends a DSN failure message if the message cannot be placed into the local mailbox.
 * @author Greg Meyer
 * @since 2.0
 */
public class TimelyAndReliableLocalDelivery extends AbstractNotificationAwareMailet
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(TimelyAndReliableLocalDelivery.class);	
	
	protected static final String RELIABLE_DELIVERY_OPTION = MDNStandard.DispositionOption_TimelyAndReliable + "=optional,true";
	
	protected Object localDeliveryMailet;
	
	protected Method serviceMethod;
	
	protected NotificationProducer notificationProducer;
	
	/**
	 * {@inheritDoc}
	 */
	public void init() throws MessagingException
	{
		super.init();
		
		try
		{
			// create an instance of the local delivery if we can
			Class<?> clazz = TimelyAndReliableLocalDelivery.class.getClassLoader().loadClass("org.apache.james.transport.mailets.LocalDelivery");
			localDeliveryMailet = clazz.newInstance();
			
			Method initMethod = Mailet.class.getDeclaredMethod("init", MailetConfig.class);
			serviceMethod = Mailet.class.getDeclaredMethod("service", Mail.class);
			
			initMethod.invoke(localDeliveryMailet, this.getMailetConfig());
		}
		catch (Exception e)
		{
			throw new MessagingException("Failed to initialize TimelyAndReliableLocalDelivery.", e);
		}
		
		notificationProducer = new ReliableDispatchedNotificationProducer(new NotificationSettings(true, "Local Direct Delivery Agent", ""));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void service(Mail mail) throws MessagingException 
	{
		LOGGER.debug("Calling timely and reliable service method.");
		
		boolean deliverySuccessful = false;
		
		final MimeMessage msg = mail.getMessage();
		final boolean isReliableAndTimely = TxUtil.isReliableAndTimelyRequested(msg);
		
		final NHINDAddressCollection recipients = getMailRecipients(mail);
								
		final NHINDAddress sender = getMailSender(mail);
		
		
		try
		{
			serviceMethod.invoke(localDeliveryMailet, mail);
			deliverySuccessful = true;
		}
		catch (Exception e)
		{
			throw new MessagingException("Failed to invoke service method.", e);
		}
		
		final Tx txToTrack = this.getTxToTrack(msg, sender, recipients);
		
		if (isReliableAndTimely)
		{
			if (deliverySuccessful)
			{
				// send back an MDN dispatched message
				final Collection<NotificationMessage> notifications = 
						notificationProducer.produce(new IncomingMessage(new Message(msg), recipients,  sender));
				if (notifications != null && notifications.size() > 0)
				{
					LOGGER.debug("Sending MDN \"dispathed\" messages");
					// create a message for each notification and put it on James "stack"
					for (NotificationMessage message : notifications)
					{
						try
						{
							message.setHeader(MDNStandard.Headers.DispositionNotificationOptions, RELIABLE_DELIVERY_OPTION);
							message.saveChanges();
							getMailetContext().sendMail(message);
						}
						catch (Throwable t)
						{
							// don't kill the process if this fails
							LOGGER.error("Error sending MDN dispatched message.", t);
						}
					}
				}
				
			}
			else
			{
				// create a DSN message
				this.sendDSN(txToTrack, recipients);
			}
		}
		
		LOGGER.debug("Exiting timely and reliable service method.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Provider<DSNCreator> getDSNProvider() 
	{
		return new FailedDeliveryDSNCreatorProvider(this);
	}
	
	
}
