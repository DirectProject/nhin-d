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

/**
 * Utility and extension methods for MDN specific message logic. 
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class NotificationHelper 
{
	/**
	 * Tests if this message has requested MDN notification.
	 * <p>
	 * The received message may be tested to see if it has a message disposition notification
     * request, based on the Disposition-Notification-To header.
	 * @param message The message to test.
	 * @return true if this message has requested disposition notification. false otherwise
	 */
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
    
    /**
     * Tests if this message is an MDN
     * @param message The message to test.
     * @return true if this message in an MDN.  false otherwise
     */
    public static boolean isMDN(Message message)
    {
        return MDNStandard.isReport(message);
    }
    
    /**
     * Returns true if the user agent should issue a notification for this message.
     * <p>
     * Tests the message to see if it has a message disposition notification
     * request, based on the Disposition-Notification-To header
     * Additionally, verifies that the message is NOT itself an MDN. As per <a href="http://tools.ietf.org/html/rfc3798">RFC 3798</a>, 
     * agents should never issue an MDN in response to an MDN
     * @param message The message to test.
     * @return true if the a notification should be issued.  false otherwise
     * @deprecated as of version 1.1.  
     * The Direct Project model infers that a notification message should be sent regardless if
     * a notification request is requested.  The gateway agent model should now determine whether or not to send a notification
     * based on the following criteria:
     * <b>1)<b> A preference in the gateway indicates that the agent/gateway should send notifications.
     * <b>2)<b> This message is not a MDN messages.  This can be determined by calling  {@link #isMDN(Message)}.
     */
    public static boolean shouldIssueNotification(Message message)
    {
        return (!isMDN(message) && hasNotificationRequest(message));
    }    
    
    /**
     * Gets the value of the Disposition-Notification-To header, which indicates where
     * the original UA requested notification be sent.  If the header is not present, then
     * the From header is used.
     * @param message The message to get the destination from.
     * @return The value of the header (which will be a comma separated list of addresses)
     */
    public static String getNotificationDestination(Message message)
    {
    	String retVal = "";
    	
    	try
    	{
    		retVal = message.getHeader(MDNStandard.Headers.DispositionNotificationTo, ",");
    		
    		if (retVal == null || retVal.isEmpty())
    		{
    			retVal = message.getHeader(MDNStandard.Headers.From, ",");
    		}
    	}
    	catch (MessagingException e) {/* no-op */}
    	
        return retVal != null ? retVal : "";
    }    
    
    /**
     * Gets the mail addresses contained in the Disposition-Notification-To header, which indicates where
     * the original UA requested notification be sent.
     * @param message The message to get the destination from.
     * @return A collection of InternetAddresses, or an empty list if no header was found
     */
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
    
    /**
     * Sets the header values for this message to request message disposition notification.
     * @param message The message for which to set the disposition request headers.
     */
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
       
    /**
     * Creates an MDN Notification message for the given message
     * @param message source message
     * @param from Address this notification is from
     * @param notification The notification to be embedded in the message
     * @return Null if no notification should be issued
     */
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

        if (isMDN(message))
        {
            return null;
        }
        
        notification.setFinalRecipient(from.getAddress());
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
    
    /**
     * Creates a notification message (MDN) for the given message to the senders.
     * @param message The message for which to send notification
     * @param senders The message senders to which to send notification
     * @param notificationCreator Interface for creating notification objects from addresses
     * @return A collection of notification messages
     */
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
        
        if (isMDN(message))
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
    
    /**
     * Provides the appropriate Disposition header value for the type.
     * @param type The mode to translate
     * @return A string representation suitable for inclusion in the action mode section of the Disposition header value
     */
    public static String asString(TriggerType type)
    {
        return MDNStandard.toString(type);
    }   
    
    /**
     * Provides the appropriate Disposition header value for the type
     * @param type The mode to translate
     * @return A string representation suitable for inclusion in the sending mode section of the Disposition header value
     */
    public static String asString(SendType type)
    {
        return MDNStandard.toString(type);
    }

    /**
     * Provides the appropriate Disposition header value for the type
     * @param type The type to translate
     * @return A string representation suitable for inclusion in the disposition type section of the Disposition header value
     */
    public static String asString (NotificationType type)
    {
        return MDNStandard.toString(type);
    }
}
