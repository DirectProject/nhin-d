package org.nhindirect.stagent.mail.notifications;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Arrays;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.nhindirect.stagent.mail.MailStandard;
import org.nhindirect.stagent.mail.Message;


public class NotificationHelper 
{
    public static boolean hasNotificationRequest(Message message)
    {
    	String[] header = null;
    	
    	try
    	{
    		header = message.getHeader(MDNStandard.Headers.DispositionNotificationTo);
    	}
    	catch (MessagingException e) {/* no-op */}
        
    	return (header != null && header.length > 0);
    }
    
    public static boolean isMDN(Message message)
    {
        return MDNStandard.isReport(message);
    }
    
    public static boolean shouldIssueNotification(Message message)
    {
        return (!isMDN(message) && hasNotificationRequest(message));
    }    
    
    public static String getNotificationDestination(Message message)
    {
    	String retVal = "";
    	
    	try
    	{
    		retVal = message.getHeader(MDNStandard.Headers.DispositionNotificationTo, ",");
    	}
    	catch (MessagingException e) {/* no-op */}
    	
        return retVal != null ? retVal : "";
    }    
    
    public static Collection<InternetAddress> getNotificationDestinationAddresses(Message message)
    {
    	String rawAddresses = getNotificationDestination(message);
    	
    	if (rawAddresses.isEmpty())
    		return Collections.emptyList();
    	
    	InternetAddress[] addresses = null;
    	try
    	{
    		addresses = InternetAddress.parse(rawAddresses);
    	}
    	catch (AddressException e) {/* no-op */}
    	
    	Collection<InternetAddress> retVal;
    	
    	if (addresses != null)
    		retVal = Arrays.asList(addresses);
    	else
    		retVal = Collections.emptyList();
    	
    	return retVal;   	
    }
    
    public static void requestNotification(Message message)
    {
        if (isMDN(message))
        {
            throw new IllegalStateException("Cannot request an MDN for an MDN");
        }
        
        try
        {
        	message.setHeader(MDNStandard.Headers.DispositionNotificationTo, message.getHeader(MailStandard.Headers.From, ","));
        }
    	catch (MessagingException e) {/* no-op */}
    }
       
    public static NotificationMessage createNotificationMessage(Message message, InternetAddress from, Notification notification)
    {
        if (from == null)
        {
            throw new IllegalArgumentException();
        }

        if (notification == null)
        {
            throw new IllegalArgumentException();
        }

        if (!shouldIssueNotification(message))
        {
            return null;
        }
        
        NotificationMessage notificationMessage = NotificationMessage.createNotificationFor(message, notification);
        if (notificationMessage != null)
        {
        	try
        	{
        		notificationMessage.setFrom(from);
        	}
        	catch (MessagingException e) {/* no-op */}
        }
        
        return notificationMessage;
    }    
    
    public static Collection<NotificationMessage> createNotificationMessages(Message message, Collection<InternetAddress> senders, 
    		NotificationCreator notificationCreator)
    {
        if (senders == null)
        {
            throw new IllegalArgumentException();
        }
        if (notificationCreator == null)
        {
            throw new IllegalArgumentException();
        }
        
        if (!shouldIssueNotification(message))
        {
            return Collections.emptyList();
        }
        
        Collection<NotificationMessage> retVal = new ArrayList<NotificationMessage>(); 
        
        for (InternetAddress sender : senders)
        {
            Notification notification = notificationCreator.createNotification(sender);
            NotificationMessage notificationMessage = createNotificationMessage(message, sender, notification);
            if (notificationMessage != null)
            {
            	retVal.add(notificationMessage);
            }
        }
        
        return retVal;
    }    
    
    public static String asString(TriggerType type)
    {
        return MDNStandard.toString(type);
    }   
    
    public static String asString(SendType type)
    {
        return MDNStandard.toString(type);
    }

    public static String asString (NotificationType type)
    {
        return MDNStandard.toString(type);
    }
}
