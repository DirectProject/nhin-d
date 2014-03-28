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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.nhindirect.stagent.NHINDException;
import org.nhindirect.stagent.mail.MailStandard;
import org.nhindirect.stagent.mail.MimeEntity;

import org.apache.mailet.base.mail.MimeMultipartReport;


/**
 * Represents notification (MDN) content.
 * <p>
 * The {@link NotificationMessage} represents the actually sendable MDN.
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class Notification
{   
	private static final String DefaultExplanation = "Your message was successfully processed.";
	
    private String explanation;
	private Disposition disposition;
    
	private ReportingUserAgent reportingAgent;
    private MdnGateway gateway;
    private String originalMsgId;
    private String finalRecipient;
    private String error;
    
    private MimeMultipartReport report;
    
    /**
     * Initializes a new instance of the supplied notification type.
     * @param notification The notification disposition for this instance.
     */
    public Notification(NotificationType notification)
	{
    	this(new Disposition(notification));
	}
    
    /**
     * Initializes a new instance with the supplied {@link Disposition}
     * @param disposition The notification disposition for this instance.
     */
    public Notification(Disposition disposition)
	{    
    	this.explanation = DefaultExplanation;
    	this.disposition = disposition;
    	this.originalMsgId = "";
    	this.finalRecipient = "";
    	this.error = "";
    	
    	updateReport();
	}	

    private void updateReport()
    {
    	try
    	{
    		report = MDNFactory.create(explanation, this.reportingAgent != null ? reportingAgent.getName() : "", 
    				this.reportingAgent != null ? reportingAgent.getProduct() : "", "",
    						this.finalRecipient != null ? finalRecipient : "", 
    						this.originalMsgId != null ? this.originalMsgId : "", 
    						this.error != null ? this.error : "", 
    						gateway, disposition);
    		
        	report.getBodyPart(1).setHeader(MailStandard.Headers.ContentType, MDNStandard.MediaType.DispositionNotification);
    	}
    	catch (MessagingException e) { /* no-op */}
    }
    
    /**
     * Gets the body part corresponding to the notification explanation.
     * <p>
     * From RFC 3798, section 3, item b:<p>
     * <i>
     * The first component of the multipart/report contains a human-
     * readable explanation of the MDN, as described in [RFC-REPORT].
     * </i>
     * @return The body part corresponding to the notification explanation.
     */
    public String getExplanation() 
    {
    	return explanation;
	}

    /**
     * Sets the body part corresponding to the notification explanation.
     * @param explanation The body part corresponding to the notification explanation.
     */
	public void setExplanation(String explanation) 
	{		
		this.explanation = explanation;
		updateReport();
	}

	/**
	 * Gets the reporting agent that triggered this notification (optional).
	 * @return The reporting agent that triggered this notification.
	 */
	public ReportingUserAgent getReportingAgent() 
	{
		return reportingAgent;
	}

	/**
	 * Sets the reporting agent that triggered this notification (optional).
	 * @param reportingAgent The reporting agent that triggered this notification
	 */
	public void setReportingAgent(ReportingUserAgent reportingAgent) 
	{		
		this.reportingAgent = reportingAgent;
		updateReport();
	}

	/**
	 * Gets the gateway that triggered this notification (optional).
	 * @return The gateway that triggered this notification
	 */
	public MdnGateway getGateway() 
	{
		return gateway;
	}

	/**
	 * Sets the gateway that triggered this notification (optional).
	 * @param gateway The gateway that triggered this notification
	 */
	public void setGateway(MdnGateway gateway) 
	{

		this.gateway = gateway;
		updateReport();
	}

	/**
	 * Gets the ID of the message that triggered this notification (optional).
	 * @return The ID of the message that triggered this notification
	 */
	public String getOriginalMessageId()
	{
		return originalMsgId;
	}
	
	/**
	 * Sets the ID of the message that triggered this notification (optional).
	 * @param messageId The ID of the message that triggered this notification
	 */
	public void setOriginalMessageId(String messageId)
	{
		this.originalMsgId = messageId;
		updateReport();
		
	}
	
	/**
	 * Gets the final recipient for this instance.
	 * @return the final recipient for this instance
	 */
	public String getFinalRecipeint() 
	{
		return finalRecipient;
	}

	/**
	 * Sets final recipient.
	 * @param messageId The final recipient.
	 */
	public void setFinalRecipient(String recip)
	{
		this.finalRecipient = recip;
		updateReport();
	}
	
	/**
	 * Gets the {@link Disposition} for this instance.
	 * @return the {@link Disposition} for this instance
	 */
	public Disposition getDisposition() 
	{
		return disposition;
	}	
	
	
	/**
	 * Sets the {@link Disposition} for this instance.
	 * @param disposition The {@link Disposition} for this instance
	 */
	public void setDisposition(Disposition disposition) 
	{
		//setDisposition(disposition, false);
		this.disposition = disposition;
		updateReport();
	}

	/**
	 * Gets the value of the error header.
	 * @return The value of the error header.
	 */
	public String getError()
	{

		return error;
	}
	
	/**
	 * Sets the value of the error header.
	 * @param error The value of the error header.
	 */
	public void setError(String error)
	{
	   this.error = error;
	   updateReport();
	}
	
	/**
	 * Returns a collection of body parts of the multipart report for this notification.
	 * @return A collection of body parts of the multipart report for this notification.
	 */
	public Collection<MimeEntity> getParts()
	{
		
		if (report == null)
			updateReport();
		
		Collection<MimeEntity> retVal = new ArrayList<MimeEntity>();
		
		try
		{
			for (int i = 0; i < report.getCount(); ++i)
			{
		    	ByteArrayOutputStream oStream = null;
		    	try
		    	{    	
		    		oStream = new ByteArrayOutputStream();
		    		report.getBodyPart(i).writeTo(oStream);
		    		oStream.flush();    		
		    	}
				catch (MessagingException e) {}
				catch (IOException e) {}
				
				InputStream str = new ByteArrayInputStream(oStream.toByteArray());					
				retVal.add(new MimeEntity(str));
			}
		}
		catch (MessagingException e) {/* */}	
		
		return retVal;
	}
	
	/**
	 * Gets the notification object as a MimeMultipart object;
	 * @return The notification object as a MimeMultipart object;
	 */
	public MimeMultipart getAsMultipart()
	{

		if (report == null)
			updateReport();
		
		return report;
	}
	
	/**
	 * Serializes the notification to an array of bytes.
	 * @return byte array serialized form on this notification.
	 */
	public byte[] serializeToBytes()
	{		
		if (report == null)
			updateReport();
		
    	ByteArrayOutputStream oStream = null;
    	try
    	{    	
    		
    		oStream = new ByteArrayOutputStream();
    		report.writeTo(oStream);
    		oStream.flush();    		
    	}
		catch (MessagingException e) {}
		catch (IOException e) {}
		
		return oStream.toByteArray();		
	}
	
	/**
	 * Gets an input stream of this notification that can be serialized.
	 * @return An input stream of this notification
	 */
	public InputStream getInputStream()
	{
		if (report == null)
			updateReport();
		
    	ByteArrayOutputStream oStream = null;
    	try
    	{    	
    		oStream = new ByteArrayOutputStream();
    		report.writeTo(oStream);
    		oStream.flush();    		
    	}
		catch (MessagingException e) {}
		catch (IOException e) {}
		
		return new ByteArrayInputStream(oStream.toByteArray());		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return new String(serializeToBytes());
	}
	
	/**
	 * Parses the notification part fields into InternetHeaders.
	 * @return The notification part fields as a set of Internet headers. 
	 */
	public InternetHeaders getNotificationFieldsAsHeaders()
	{
		if (report == null)
			updateReport();
		
		return getNotificationFieldsAsHeaders(report);
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
			throw new NHINDException("Failed to parse notification fields.", e);
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
			throw new NHINDException("Failed to parse notification fields.", e);
		}
		
		return retVal;
		
	}	
	
	/*
	 * Gets the content of a body part as a string.  The content may internally be stored using several constructs such as a stream.
	 */
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
			throw new NHINDException("Unable to handle get notification body as a string.", e);
		}
	}
}
