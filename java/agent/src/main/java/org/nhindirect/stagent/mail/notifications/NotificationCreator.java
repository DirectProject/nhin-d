package org.nhindirect.stagent.mail.notifications;

import javax.mail.internet.InternetAddress;

public interface NotificationCreator 
{
	public Notification createNotification(InternetAddress address);
}
