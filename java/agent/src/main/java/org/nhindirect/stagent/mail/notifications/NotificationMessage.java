package org.nhindirect.stagent.mail.notifications;

import java.util.UUID;

import javax.mail.MessagingException;

import org.nhindirect.stagent.mail.MailStandard;
import org.nhindirect.stagent.mail.Message;

public class NotificationMessage extends Message 
{
    public NotificationMessage(String to, Notification notification) throws MessagingException 
	{
    	this(to, null, notification);
	}
    
    public NotificationMessage(String to, String from, Notification notification) throws MessagingException 
    {
    	super(notification.getInputStream());
    	
    	if (to != null && !to.isEmpty())
    		this.setHeader(MailStandard.Headers.To, to);
    	
    	if (from != null && !from.isEmpty())
    		this.setHeader(MailStandard.Headers.From, from);
    		
    	this.setHeader(MailStandard.Headers.ContentType, MDNStandard.MediaType.DispositionReport);
    } 
    
    public static NotificationMessage createNotificationFor(Message message, Notification notification)
    {
        if (message == null)
        {
            throw new IllegalArgumentException();
        }

        if (notification == null)
        {
            throw new IllegalArgumentException();
        }
        //
        // Verify that the message is not itself an MDN!
        //
        if (NotificationHelper.isMDN(message))
        {
            throw new IllegalArgumentException("Message is an MDN");
        }
        
        String notifyTo = NotificationHelper.getNotificationDestination(message);
        if (notifyTo == null || notifyTo.isEmpty())
        {
            throw new IllegalArgumentException("Invalid Disposition-Notification-To Header");
        }
        
        NotificationMessage notificationMessage = null;
        
        try
        {
	        String originalMessageID = message.getMessageID();
	        if (originalMessageID != null && !originalMessageID.isEmpty())
	        {
	            notification.setOriginalMessageId(originalMessageID);
	        }
	        
	        notificationMessage = new NotificationMessage(notifyTo, notification);
	        notificationMessage.setHeader(MailStandard.Headers.MessageID, UUID.randomUUID().toString());
        }
        catch (MessagingException e) {/* no-op */}
        
        return notificationMessage;
    }    
}
