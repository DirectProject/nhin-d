package org.nhindirect.gateway.smtp.james.mailet;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.mailet.Mail;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.gateway.smtp.dsn.DSNCreator;
import org.nhindirect.gateway.smtp.dsn.provider.FailedDeliveryDSNCreatorProvider;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.NHINDAddressCollection;

import com.google.inject.Provider;

public class DirectBounce extends AbstractNotificationAwareMailet
{	
	public void init() throws MessagingException
	{
		super.init();
	}

	@Override
	public void service(Mail mail) throws MessagingException 
	{	
		final MimeMessage msg = mail.getMessage();
		
		final NHINDAddressCollection recipients = getMailRecipients(mail);
		
		final NHINDAddress sender = getMailSender(mail);
			
		final Tx txToTrack = getTxToTrack(msg, sender, recipients);
		

		// create a DSN message
		this.sendDSN(txToTrack, recipients);

	}
		
	@Override
	protected Provider<DSNCreator> getDSNProvider() 
	{
		return new FailedDeliveryDSNCreatorProvider(this);
	}
	
}
