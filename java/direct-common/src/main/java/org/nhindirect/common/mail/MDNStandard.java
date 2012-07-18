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

package org.nhindirect.common.mail;

import java.io.InputStream;
import java.lang.reflect.Method;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides constants and utility functions for working with MDN
 * @author Greg Meyer
 * @author Umesh Madan
 * @since 1.1
 */
public class MDNStandard 
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(MDNStandard.class);
	
	private static Class<?> dsnClass;
	private static Method getHeaders;
	
    static
    {
    	try
    	{
    		dsnClass = MDNStandard.class.getClassLoader().loadClass("com.sun.mail.dsn.DispositionNotification");
    		getHeaders = dsnClass.getMethod("getNotifications");
    	}
    	catch (Exception e) {/* no-op */}
    }
	
	/**
	 * MIME types for MDN 
     * @author Greg Meyer
     * @author Umesh Madan
	 *
	 */
	public static class MediaType
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
	    
	    /**
	     * MIME parameter header for an report. 
	     */
	    public static final String ReportType = "report-type";
	    
	    /**
	     * Report parameter value indication an MDN message.
	     */
	    public static final String ReportTypeValueNotification = "disposition-notification";
	}
	
	/**
	 * Standard header names for MDN headers
     * @author Greg Meyer
     * @author Umesh Madan
	 *
	 */
    public static class Headers
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
     * Direct specific dispostion options for requesting timely and reliable messaging.
     */
    public static final String DispositionOption_TimelyAndReliable = "X-DIRECT-FINAL-DESTINATION-DELIVERY";
    
	/**
	 * Gets an MDN field such as the message dispostion from a MimeMessage.
	 * @param msg The message to retrieve the field from.
	 * @param field The field to extract from the message.
	 * @return The value of the field.  If the message is not an MDN message (<a href="http://tools.ietf.org/html/rfc3798">RFC 3798</a>) or the
	 * field does not exist, an empty string is returned.
	 */
	public static String getMDNField(MimeMessage msg, String field)
	{
		InternetHeaders headers = null;
		
		try
		{
			// use the help function from the Direct specification... will throw and
			// exception if the message is not an MDN message or cannot parse the fields
			headers = MDNStandard.getNotificationFieldsAsHeaders(msg);
		}
		catch (IllegalArgumentException e)
		{
			LOGGER.warn("Failed to retrieve MDN field from message.  Message may not be an MDN message.", e);
		}
		
		if (headers != null)
		{
			// disposition fields are returned from the helper function as a standard list of internet headers
			final String fieldValue = headers.getHeader(field, ",");
			if (fieldValue != null)
				return fieldValue;
		}
		
		return "";
	}
    
	/**
	 * Parses the notification part fields of a MDN MimeMessage message.  The message is expected to conform to the MDN specification
	 * as described in RFC3798.
	 * @return The notification part fields as a set of Internet headers. 
	 */		
	public static InternetHeaders getNotificationFieldsAsHeaders(MimeMessage message)
	{
		if (message == null)
			throw new IllegalArgumentException("Message can not be null");
		
		MimeMultipart mm = null;
		
		try
		{
			ByteArrayDataSource dataSource = new ByteArrayDataSource(message.getRawInputStream(), message.getContentType());
			mm = new MimeMultipart(dataSource);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Failed to parse notification fields.", e);
		}
		
		return getNotificationFieldsAsHeaders(mm);
	}	
	
	
	/**
	 * Parses the notification part fields of the MimeMultipart body of a MDN message.  The multipart is expected to conform to the MDN specification
	 * as described in RFC3798.
	 * @return The notification part fields as a set of Internet headers. 
	 */	
	public static InternetHeaders getNotificationFieldsAsHeaders(MimeMultipart mm)
	{
		InternetHeaders retVal = null;
		
		if (mm == null)
			throw new IllegalArgumentException("Multipart can not be null");
		
		try
		{
			if (mm.getCount() < 2)
				throw new IllegalArgumentException("Multipart can not be null");
			
			// the second part should be the notification
			BodyPart part = mm.getBodyPart(1);
			
			try
			{
				Object contecntObj = part.getContent();
				if (dsnClass != null && dsnClass.getCanonicalName().equals(contecntObj.getClass().getCanonicalName()))
				{
					retVal = (InternetHeaders)getHeaders.invoke(contecntObj);
					return retVal;
				}
			}
			catch(Exception e) {/* no-op */}
			
			if (!part.getContentType().equalsIgnoreCase(MDNStandard.MediaType.DispositionNotification))
				throw new IllegalArgumentException("Notification part content type is not " + MDNStandard.MediaType.DispositionNotification);
				
			// parse fields
			retVal = new InternetHeaders();	
			String[] fields = getPartContentBodyAsString(part).split("\r\n");
			for (String field : fields)
			{
				int idx = field.indexOf(":");
				if (idx > -1)
				{
					String name = field.substring(0, idx);
					String value = field.substring(idx + 1).trim();
					retVal.setHeader(name, value);
				}
			}

		}
		catch (MessagingException e)
		{
			throw new IllegalArgumentException("Failed to parse notification fields.", e);
		}
		
		return retVal;
		
	}		
	
	/*
	 * Gets the content of a body part as a string.  The content may internally be stored using several constructs such as a stream.
	 */
	///CLOVER:OFF
	protected static String getPartContentBodyAsString(BodyPart part)
	{
		try
		{
			Object content = part.getContent();
		
			if (content instanceof String)
				return content.toString();
			else if (content instanceof InputStream)
			{
				InputStream str = (InputStream)part.getContent();
				byte[] bytes = new byte[str.available()];
				str.read(bytes);
				return new String(bytes);
			}
			else
				return content.toString();
		}
		catch (Exception e) 
		{
			throw new IllegalArgumentException("Unable to handle get notification body as a string.", e);
		}
	}
	///CLOVER:ON
}
