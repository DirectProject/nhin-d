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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

import org.nhindirect.stagent.mail.MimeEntity;
import org.nhindirect.stagent.mail.MimeStandard;

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
	private static final String DefaultExplanation = "Your message was successfully ";
	
	private MimeEntity explanation;
	private MimeEntity notification;
    private ReportingUserAgent reportingAgent;
    private MdnGateway gateway;
    private Disposition disposition;
    private MimeMultipart mmRep;
    private String finalRecipient;
    
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
    	try
    	{
		    explanation = new MimeEntity();
		    explanation.setHeader(MimeStandard.ContentTypeHeader, MimeStandard.MediaType.TextPlain);
		    
		    notification = new MimeEntity();
		    notification.setHeader(MimeStandard.ContentTypeHeader, MDNStandard.MediaType.DispositionNotification);		    
    	}
    	catch (MessagingException e) { /* no-op */}
    	
	    this.setDisposition(disposition, true);
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
    	String retVal = "";
    	try
    	{
    		ByteArrayOutputStream os = new ByteArrayOutputStream();
    		explanation.writeTo(os);
    		
    		retVal = new String(os.toByteArray(), "ASCII").trim();
    	}
    	catch (UnsupportedEncodingException e) {/* no-op */}
		catch (MessagingException e) {/* no-op */}
		catch (IOException e) {/* no-op */}
    	
    	return retVal;
	}

    /**
     * Sets the body part corresponding to the notification explanation.
     * @param explanation The body part corresponding to the notification explanation.
     */
	public void setExplanation(String explanation) 
	{		
		// only use this because the content type is already text/plain
		try
		{
			this.explanation.setText(explanation);			
			genMMRep();
		}
		catch (MessagingException e) {/* no-op */}
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
		try
		{
			if (reportingAgent != null)
				notification.setHeader(MDNStandard.Headers.ReportingAgent, reportingAgent.toString());
			else
				notification.removeHeader(MDNStandard.Headers.ReportingAgent);
			
			genMMRep();
		}
		catch (MessagingException e) {/* no-op */}
		
		this.reportingAgent = reportingAgent;
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
		try
		{
			if (gateway != null)
				notification.setHeader(MDNStandard.Headers.Gateway, gateway.toString());
			else
				notification.removeHeader(MDNStandard.Headers.Gateway);
			
			genMMRep();
		}
		catch (MessagingException e) {/* no-op */}
		
		this.gateway = gateway;
	}

	/**
	 * Gets the ID of the message that triggered this notification (optional).
	 * @return The ID of the message that triggered this notification
	 */
	public String getOriginalMessageId()
	{
		String retVal = null;
	
		try
		{
			retVal = notification.getHeader(MDNStandard.Headers.OriginalMessageID, null);
		}
		catch (MessagingException e) {/* no-op */}
		
		return retVal != null ? retVal : "";
	}
	
	/**
	 * Sets the ID of the message that triggered this notification (optional).
	 * @param messageId The ID of the message that triggered this notification
	 */
	public void setOriginalMessageId(String messageId)
	{
		if (messageId != null && !messageId.isEmpty())
			try
			{
				notification.setHeader(MDNStandard.Headers.OriginalMessageID, messageId);
				genMMRep();
			}
			catch (MessagingException e) {/* no-op */}
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
		try
		{
			if (recip != null && !recip.isEmpty())
				notification.setHeader(MDNStandard.Headers.FinalRecipient, recip);
			else
				notification.removeHeader(MDNStandard.Headers.FinalRecipient);
			
			genMMRep();
		}
		catch (MessagingException e) {/* no-op */}
		
		this.finalRecipient = recip;
	}
	
	/**
	 * Gets the {@link Disposition} for this instance.
	 * @return the {@link Disposition} for this instance
	 */
	public Disposition getDisposition() 
	{
		return disposition;
	}	
	
	/*
	 * set the disposition but optionally suppress generating the multipart... this
	 * is mainly used because of the constructor not setting all attributes before
	 * generating the mutlipart
	 */
	private void setDisposition(Disposition disposition, boolean supressMMGen)
	{
        if (disposition == null)
        {
            throw new IllegalArgumentException("value");
        }
        
		try
		{
			notification.setHeader(MDNStandard.Headers.Disposition, disposition.toString());
			if (!supressMMGen)
				genMMRep();
		}
		catch (MessagingException e) {/* no-op */}
		
		this.disposition = disposition;
	}
	
	/**
	 * Sets the {@link Disposition} for this instance.
	 * @param disposition The {@link Disposition} for this instance
	 */
	public void setDisposition(Disposition disposition) 
	{
		setDisposition(disposition, false);
	}

	/**
	 * Gets the value of the error header.
	 * @return The value of the error header.
	 */
	public String getError()
	{
		String retVal = null;
		
		try
		{
			String headers[] = notification.getHeader(MDNStandard.Headers.Error);
			if (headers != null && headers.length > 0)
				retVal = headers[0];
		}
		catch (MessagingException e) {/* no-op */}
		
		return retVal;
	}
	
	/**
	 * Sets the value of the error header.
	 * @param error The value of the error header.
	 */
	public void setError(String error)
	{
		if (error != null && !error.isEmpty())
			try
			{
				notification.setHeader(MDNStandard.Headers.Error, error);
				genMMRep();
			}
			catch (MessagingException e) {/* no-op */}
	}
	
	/**
	 * Returns a collection of body parts of the multipart report for this notification.
	 * @return A collection of body parts of the multipart report for this notification.
	 */
	public Collection<MimeEntity> getParts()
	{
		Collection<MimeEntity> retVal = new ArrayList<MimeEntity>();
		
		if (getExplanation().trim().isEmpty())
		{
			setExplanation(DefaultExplanation + disposition.getNotification());
		}
		
		retVal.add(explanation);
		retVal.add(notification);
		
		return retVal;
	}
	
	/**
	 * Gets the notification object as a MimeMultipart object;
	 * @return The notification object as a MimeMultipart object;
	 */
	public MimeMultipart getAsMultipart()
	{
    	if (mmRep == null)
    		genMMRep();
    	
    	return mmRep;
	}
	
	/**
	 * Serializes the notification to an array of bytes.
	 * @return byte array serialized form on this notification.
	 */
	public byte[] serializeToBytes()
	{
    	if (mmRep == null)
    		genMMRep();
    	
   	
    	ByteArrayOutputStream oStream = null;
    	try
    	{    	
    		
    		oStream = new ByteArrayOutputStream();
    		mmRep.writeTo(oStream);
    		oStream.flush();    		
    	}
		catch (MessagingException e) {/* no-op */}
		catch (IOException e) {/* no-op */}
		
		return oStream.toByteArray();
	}
	
	/**
	 * Gets an input stream of this notification that can be serialized.
	 * @return An input stream of this notification
	 */
	public InputStream getInputStream()
	{
    	if (mmRep == null)
    		genMMRep();
    	
    	ByteArrayOutputStream oStream = null;
    	try
    	{    	
    		oStream = new ByteArrayOutputStream();
    		mmRep.writeTo(oStream);
    		oStream.flush();    		
    	}
		catch (MessagingException e) {/* no-op */}
		catch (IOException e) {/* no-op */}
		
		return new ByteArrayInputStream(oStream.toByteArray());
	}
	
	/*
	 * Generates the multipart MIME representation of this object
	 */
	private void genMMRep()
	{
		mmRep = new MimeMultipart();

    	Collection<MimeEntity> parts = getParts();
    	try
    	{    	
    		for (MimeEntity part : parts)
    			mmRep.addBodyPart(part);
 		
    	}
		catch (MessagingException e) {/* no-op */}
	}
	
}
