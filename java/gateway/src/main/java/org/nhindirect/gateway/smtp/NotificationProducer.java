/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Umesh Madan     umeshma@microsoft.com
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


/**
 * Produces MND ack messages based on configuration settings.
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class NotificationProducer implements NotificationCreator
{


	private final NotificationSettings settings;
	
	/**
	 * Constructs a producer with the notification settings. 
	 * @param settings The notification configuration settings.
	 */
	public NotificationProducer(NotificationSettings settings)
	{
		this.settings = settings;
	}
	
	/**
	 * Creates an ack MDN.
	 * {@inheritDoc}
	 */
	public Notification createNotification(InternetAddress address) 
	{
		return createAck(address);
	}
	
	/**
	 * Produces an ack MDN message for the incoming message.
	 * @param envelope The incoming message that will have an MDN ack message sent to the senders.
	 * @return A collection of notification messages that will be sent to the incoming message senders. 
	 */
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
	
    /*
     * Creates an ack message.
     */
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
