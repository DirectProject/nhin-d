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

import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetHeaders;

import org.nhindirect.stagent.mail.MailStandard;
import org.nhindirect.stagent.mail.Message;

/**
 * Represents a message disposition notification (MDN) sent to a message sender, as per <a href="http://tools.ietf.org/html/rfc3798">RFC 3798</a> 
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class NotificationMessage extends Message 
{
	/**
	 * Initializes an MDN to the specified recipient.
	 * @param to The MDN recipient.
	 * @param notification The notification to send.
	 * @throws MessagingException
	 */
    public NotificationMessage(String to, Notification notification) throws MessagingException 
	{
    	this(to, null, notification);
	}
    
    /**
     * Initializes an MDN to the specified recipient.
     * @param to The MDN recipient.
     * @param from The original message receiver who is sending the MDN
     * @param notification The notification to send.
     * @throws MessagingException
     */
    public NotificationMessage(String to, String from, Notification notification) throws MessagingException 
    {
    	super(createHeaders(to, from, notification), notification.serializeToBytes());
    	
    } 
    
    private static InternetHeaders createHeaders(String to, String from, Notification notification) throws MessagingException
    {
    	InternetHeaders headers = new InternetHeaders();
    	
    	if (to != null && !to.isEmpty())
    		headers.addHeader(MailStandard.Headers.To, to);
    	
    	if (from != null && !from.isEmpty())
    		headers.addHeader(MailStandard.Headers.From, from);
    	
    	// get the boundary
    	ContentType type = new ContentType(notification.getAsMultipart().getContentType());
    	String boundary = type.getParameter("boundary");	
    	
    	headers.addHeader(MailStandard.Headers.ContentType, MDNStandard.MediaType.DispositionReport + 
    			"; boundary=" + boundary);    	
    	
    	return headers;

    }
    
    /**
     * Takes a message and constructs an MDN.
     * @param message The message to send notification about.
     * @param notification The notification to create.
     * @return The MDN.
     */
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
