package org.nhindirect.gateway.smtp;

import javax.mail.internet.InternetAddress;

import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.mail.notifications.Notification;
import org.nhindirect.stagent.mail.notifications.NotificationType;
import org.nhindirect.stagent.mail.notifications.ReportingUserAgent;

public class ReliableDispatchedNotificationProducer extends NotificationProducer
{
	public ReliableDispatchedNotificationProducer(NotificationSettings settings)
	{
		super(settings);
	}
	
    protected Notification createAck(InternetAddress address)
    {
        Notification notification = new Notification(NotificationType.Dispatched);
        if (settings.hasText())
        {
            notification.setExplanation(settings.getText());
        }
                
        notification.setReportingAgent(new ReportingUserAgent(NHINDAddress.getHost(address), settings.getProductName()));            
        return notification;
    }
}
