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

import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMessage;

import org.nhindirect.stagent.mail.MailStandard;
import org.nhindirect.stagent.mail.MimeEntity;

/**
 * Provides constants and utility functions for working with MDN
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class MDNStandard extends MailStandard 
{
	/**
	 * MIME types for MDN 
     * @author Greg Meyer
     * @author Umesh Madan
	 *
	 */
	public static class MediaType extends MailStandard.MediaType
	{
		/**
		 * Base MIME type for an MDN
		 */
	    public static final String ReportMessage = "multipart/report";
	
	    /**
	     * MIME type with qualifier for a disposition report.
	     */
	    public static final String  DispositionReport = ReportMessage + "; report-type=disposition-notification";
	
	    /**
	     * MIME type for the disposition notification body part of the multipart/report report
	     */
	    public static final String  DispositionNotification = "message/disposition-notification";
	}
	
	/**
	 * Standard header names for MDN headers
     * @author Greg Meyer
     * @author Umesh Madan
	 *
	 */
    public static class Headers extends MailStandard.Headers
    {

    	/**
    	 * Disposition header field name.
    	 * <p>
    	 * RFC 3798, Disposition field, 3.2.6
    	 */
    	public static final String Disposition = "Disposition";

    	/**
    	 * Disposition-Notification-To header name
    	 * <p>
    	 * RFC 3798, The Disposition-Notification-To Header, 2.1
    	 */
    	public static final String DispositionNotificationTo = "Disposition-Notification-To";

    	/**
    	 * Disposition-Notification-Options header name
    	 * <p>
    	 * RFC 3798, The Disposition-Notification-Options Header, 2.2
    	 */
    	public static final String DispositionNotificationOptions = "Disposition-Notification-Options";

    	/**
    	 * Reporting-UA field name (value is the Health Internet Addresa and software that triggered notification) 
    	 * <p>
    	 * RFC 3798, The Reporting-UA field, 3.2.1
    	 */
    	public static final String ReportingAgent = "Reporting-UA";

    	/**
    	 * MDN-Gateway field name (for SMTP to non-SMTP gateways -- e.g., XDD to SMTP)
    	 * <p>
    	 * RFC 3798, The MDN-Gateway field, 3.2.2
    	 */
    	public static final String Gateway = "MDN-Gateway";

    	/**
    	 * Original-Message-ID field name (value is message for which notification is being sent)
    	 * <p>
    	 * RFC 3798, Original-Message-ID field, 3.2.5
    	 */
    	public static final String OriginalMessageID = "Original-Message-ID";

    	/**
    	 * Final recipient field name.
    	 */
    	public static final String FinalRecipient = "Final-Recipient";    	
    	
    	/**
    	 * Failure field name, value is original failure text (e.g., exception)
    	 * <p>
    	 * RFC 3798, Failure, Error and Warning fields, 3.2.7
    	 */
    	public static final String Failure = "Failure";

    	/**
    	 * Error field name, value is original error text (e.g., HL7 error report)
    	 * <p>
    	 * RFC 3798, Failure, Error and Warning fields, 3.2.7
    	 */
    	public static final String Error = "Error";

    	/**
    	 * Warning field name, value is original warning text
    	 * <p>
    	 * RFC 3798, Failure, Error and Warning fields, 3.2.7
    	 */
    	public static final String Warning = "Warning";    	
    }
        
    public static final String Action_Manual = "manual-action";
    public static final String Action_Automatic = "automatic-action";
    public static final String Send_Manual = "MDN-sent-manually";
    public static final String Send_Automatic = "MDN-sent-automatically";
    public static final String Disposition_Displayed = "displayed";
    public static final String Disposition_Processed = "processed";
    public static final String Disposition_Dispatched = "dispatched";  
    public static final String Disposition_Deleted = "deleted";
    public static final String Disposition_Denied = "denied";    
    public static final String Disposition_Error = "error";  
    public static final String Modifier_Error = "error";    
    
    public static final String  ReportType = "report-type";
    public static final String  ReportTypeValueNotification = "disposition-notification";  
    
    /**
     * Tests the entity to see if it contains an MDN request.
     * @param entity The entity to test.
     * @return true if the entity contains an MDN request. false otherwise.
     */
    public static boolean hasMDNRequest(MimeEntity entity)
    {
        if (entity == null)
        {
            return false;
        }
        
        String[] headers = null;
        try
        {
        	headers = entity.getHeader(Headers.DispositionNotificationTo);
        	
        }
        catch (MessagingException e)
        {
        	return false;
        }
        
        return headers != null && headers.length > 0;
    }    
    
    /**
     * Tests the message to see if it contains an MDN request.
     * @param The message to test. 
     * @return true if the message contains an MDN request. false otherwise.
     */
    public static boolean hasMDNRequest(MimeMessage msg)
    {
        if (msg == null)
        {
            return false;
        }
        
        String[] headers = null;
        try
        {
        	headers = msg.getHeader(Headers.DispositionNotificationTo);
        	
        }
        catch (MessagingException e)
        {
        	return false;
        }
        
        return headers != null && headers.length > 0;
    }      
    
    /**
     * Tests the entity to see if it is an MDN.
     * <p>
     * MDN status is indicated by the appropriate main body Content-Type. The multipart body
     * will contain the actual disposition notification.
     * @param entity The entity to test
     * @return true if the entity is an MDN. false otherwise
     * @see #isNotification(MimeEntity)
     */
    public static boolean isReport(MimeEntity entity)
    {
        if (entity == null)
        {
            return false;
        }

        ContentType contentType = getContentType(entity);

        return (contentType.match(MDNStandard.MediaType.ReportMessage) && 
        		contentType.getParameter(MDNStandard.ReportType) != null && 
        		contentType.getParameter(MDNStandard.ReportType).equalsIgnoreCase(MDNStandard.ReportTypeValueNotification));
    }  
    
    /**
     * Tests the message to see if it is an MDN.
     * <p>
     * MDN status is indicated by the appropriate main body Content-Type. The multipart body
     * will contain the actual disposition notification.
     * @param msg The message to test
     * @return true if the message is an MDN. false otherwise
     * @see #isNotification(MimeMessage)
     */
    public static boolean isReport(MimeMessage msg)
    {
        if (msg == null)
        {
            return false;
        }

        ContentType contentType = getContentType(msg);

        return (contentType.match(MDNStandard.MediaType.ReportMessage) && 
        		contentType.getParameter(MDNStandard.ReportType) != null && 
        		contentType.getParameter(MDNStandard.ReportType).equalsIgnoreCase(MDNStandard.ReportTypeValueNotification));
    }  
    
    /**
     * Tests the entity to determine if it is a disposition notification body part.
     * <p>
     * Notification status is indicated by the appropriate Content-Type. The notification
     * section will be a body part of the appropriate MDN report multipart body.
     * @param entity The entity to test
     * @return true if this body part is an MDN notification. false otherwise
     */
    public static boolean isNotification(MimeEntity entity)
    {
        if (entity == null)
        {
            return false;
        }

        ContentType contentType = getContentType(entity);
        return contentType.match(MDNStandard.MediaType.DispositionNotification);        
    }    
    
    /**
     * Tests the message to determine if it is a disposition notification body part.
     * <p>
     * Notification status is indicated by the appropriate Content-Type. The notification
     * section will be a body part of the appropriate MDN report multipart body.
     * @param msg The message to test
     * @return true if this body part is an MDN notification. false otherwise
     */    
    public static boolean isNotification(MimeMessage msg)
    {
        if (msg == null)
        {
            return false;
        }

        ContentType contentType = getContentType(msg);
        return contentType.match(MDNStandard.MediaType.DispositionNotification);        
    }        
    
    /**
     * Provides the appropriate Disposition header value for the mode.
     * @param mode The mode to translate
     * @return A string representation suitable for inclusion in the action mode section of the Disposition header value
     */
    public static String toString(TriggerType mode)
    {
        switch(mode)
        {
            default:
                throw new IllegalArgumentException();
            
            case Automatic:
                return Action_Automatic;
            
            case UserInitiated:
                return Action_Manual;
        }
    }

    /**
     * Provides the appropriate Disposition header value for the mode
     * @param mode The mode to translate
     * @return A string representation suitable for inclusion in the sending mode section of the Disposition header value
     */
    public static String toString(SendType mode)
    {
        switch(mode)
        {
            default:
                throw new IllegalArgumentException();

            case Automatic:
                return Send_Automatic;

            case UserMediated:
                return Send_Manual;
        }
    }

    /**
     * Provides the appropriate Disposition header value for the type
     * @param type The type to translate
     * @return A string representation suitable for inclusion in the disposition type section of the Disposition header value
     */
    public static String toString(NotificationType type)
    {
        switch (type)
        {
            default:
                throw new IllegalArgumentException();

            case Processed:
                return Disposition_Processed;

            case Displayed:
                return Disposition_Displayed;
            
            case Deleted:
                return Disposition_Deleted;
              
            case Dispatched:
                return Disposition_Dispatched; 
                
            case Denied:
                return Disposition_Denied;  
                
            case Error:
                return Disposition_Error;         
        }
    }    
    
    /*
     * Gets the content type of the message
     */
    private static ContentType getContentType(MimeMessage msg)
    {
    	try
    	{
    		return new ContentType(msg.getContentType());
    	}
    	catch (MessagingException e) {/* no-op */}
    	
    	return null;
    }  
    
    /*
     * Gets the content type of the entity
     */
    private static ContentType getContentType(MimeEntity entity)
    {
    	try
    	{
    		return new ContentType(entity.getContentType());
    	}
    	catch (MessagingException e) {/* no-op */}
    	
    	return null;
    }       
}
