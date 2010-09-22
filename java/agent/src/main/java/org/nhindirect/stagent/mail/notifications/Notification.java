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

public class Notification
{   
	private static final String DefaultExplanation = "Your message was successfully ";
	
	private MimeEntity explanation;
	private MimeEntity notification;
    private ReportingUserAgent reportingAgent;
    private MdnGateway gateway;
    private Disposition disposition;
    
    public Notification(NotificationType notification)
	{
    	this(new Disposition(notification));
	}
    
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
    	
	    this.disposition = disposition;
	}	
	

    public String getExplanation() 
    {
    	String retVal = "";
    	try
    	{
    		retVal = new String(explanation.getContentAsBytes(), "ASCII");
    	}
    	catch (UnsupportedEncodingException e) {/* no-op */}
		
    	return retVal;
	}

	public void setExplanation(String explanation) 
	{		
		// only use this because the content type is already text/plain
		try
		{
			this.explanation.setText(explanation);
		}
		catch (MessagingException e) {/* no-op */}
	}

	public ReportingUserAgent getReportingAgent() 
	{
		return reportingAgent;
	}

	public void setReportingAgent(ReportingUserAgent reportingAgent) 
	{
		try
		{
			if (reportingAgent != null)
				notification.setHeader(MDNStandard.Headers.ReportingAgent, reportingAgent.toString());
			else
				notification.removeHeader(MDNStandard.Headers.ReportingAgent);
		}
		catch (MessagingException e) {/* no-op */}
		
		this.reportingAgent = reportingAgent;
	}

	public MdnGateway getGateway() 
	{
		return gateway;
	}

	public void setGateway(MdnGateway gateway) 
	{
		try
		{
			if (gateway != null)
				notification.setHeader(MDNStandard.Headers.Gateway, gateway.toString());
			else
				notification.removeHeader(MDNStandard.Headers.Gateway);
		}
		catch (MessagingException e) {/* no-op */}
		
		this.gateway = gateway;
	}

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
	
	public void setOriginalMessageId(String messageId)
	{
		if (messageId != null && !messageId.isEmpty())
			try
			{
				notification.setHeader(MDNStandard.Headers.OriginalMessageID, messageId);
			}
			catch (MessagingException e) {/* no-op */}
	}
	
	public Disposition getDisposition() 
	{
		return disposition;
	}

	public void setDisposition(Disposition disposition) 
	{
        if (disposition == null)
        {
            throw new IllegalArgumentException("value");
        }
        
		try
		{
			notification.setHeader(MDNStandard.Headers.Disposition, disposition.toString());
		}
		catch (MessagingException e) {/* no-op */}
		
		this.disposition = disposition;
	}

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
	
	public void setError(String error)
	{
		if (error != null && !error.isEmpty())
			try
			{
				notification.setHeader(MDNStandard.Headers.Error, error);
			}
			catch (MessagingException e) {/* no-op */}
	}
	
	public Collection<MimeEntity> getParts()
	{
		Collection<MimeEntity> retVal = new ArrayList<MimeEntity>();
		
		if (getExplanation().isEmpty())
		{
			setExplanation(DefaultExplanation + disposition.getNotification());
		}
		
		retVal.add(explanation);
		retVal.add(notification);
		
		return retVal;
	}
	
	public InputStream getInputStream()
	{
    	MimeMultipart mm = new MimeMultipart();
    	
    	ByteArrayOutputStream oStream = null;
    	Collection<MimeEntity> parts = getParts();
    	try
    	{    	
    		for (MimeEntity part : parts)
    			mm.addBodyPart(part);
    		
    		oStream = new ByteArrayOutputStream();
    		mm.writeTo(oStream);
    		oStream.flush();    		
    	}
		catch (MessagingException e) {/* no-op */}
		catch (IOException e) {/* no-op */}
		
		return new ByteArrayInputStream(oStream.toByteArray());
	}
}
