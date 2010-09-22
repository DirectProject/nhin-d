package org.nhindirect.gateway.smtp;

import java.util.Collection;
import java.util.Collections;

import javax.mail.internet.InternetAddress;

import org.nhindirect.stagent.IncomingMessage;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.mail.notifications.Notification;
import org.nhindirect.stagent.mail.notifications.NotificationCreator;
import org.nhindirect.stagent.mail.notifications.NotificationHelper;
import org.nhindirect.stagent.mail.notifications.NotificationMessage;
import org.nhindirect.stagent.mail.notifications.NotificationType;
import org.nhindirect.stagent.mail.notifications.ReportingUserAgent;



public class NotificationProducer implements NotificationCreator
{


	private final NotificationSettings settings;
	
	public NotificationProducer(NotificationSettings settings)
	{
		this.settings = settings;
	}
	
	public Notification createNotification(InternetAddress address) 
	{
		return createAck(address);
	}
	
    public Collection<NotificationMessage> produce(IncomingMessage envelope)
    {
        if (envelope == null)
        {
            throw new IllegalArgumentException();
        }

        if (!settings.isAutoResponse() || !envelope.hasDomainRecipients() || !NotificationHelper.shouldIssueNotification(envelope.getMessage()))
        {
            return Collections.emptyList();
        }

        Collection<InternetAddress> senders = envelope.getDomainRecipients().toInternetAddressCollection();
        Collection<NotificationMessage> notifications = NotificationHelper.createNotificationMessages(envelope.getMessage(), senders, this); 
        
        return notifications;
    }	
	
    private Notification createAck(InternetAddress address)
    {
        Notification notification = new Notification(NotificationType.Processed);
        if (settings.hasText())
        {
            notification.setExplanation(settings.getText());
        }
                
        notification.setReportingAgent(new ReportingUserAgent(NHINDAddress.getHost(address), settings.getProductName()));            
        return notification;
    }
	
}
