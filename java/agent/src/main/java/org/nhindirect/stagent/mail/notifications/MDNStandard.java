package org.nhindirect.stagent.mail.notifications;

import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMessage;

import org.nhindirect.stagent.mail.MailStandard;
import org.nhindirect.stagent.mail.MimeEntity;

public class MDNStandard extends MailStandard 
{
	public static class MediaType extends MailStandard.MediaType
	{
	    public static final String ReportMessage = "multipart/report";
	
	    public static final String  DispositionReport = ReportMessage + "; report-type=disposition-notification";
	
	    public static final String  DispositionNotification = "message/disposition-notification";
	}
	
    public static class Headers extends MailStandard.Headers
    {

    	public static final String Disposition = "Disposition";

    	public static final String DispositionNotificationTo = "Disposition-Notification-To";

    	public static final String DispositionNotificationOptions = "Disposition-Notification-Options";

    	public static final String ReportingAgent = "Reporting-UA";

    	public static final String Gateway = "MDN-Gateway";

    	public static final String OriginalMessageID = "Original-Message-ID";

    	public static final String Failure = "Failure";

    	public static final String Error = "Error";

    	public static final String Warning = "Warning";    	
    }
    
    static final String Action_Manual = "manual-action";
    static final String Action_Automatic = "automatic-action";
    static final String Send_Manual = "MDN-sent-manually";
    static final String Send_Automatic = "MDN-sent-automatically";
    static final String Disposition_Displayed = "displayed";
    static final String Disposition_Processed = "processed";
    static final String Disposition_Deleted = "deleted";
    static final String Modifier_Error = "error";    
    
    static final String  ReportType = "report-type";
    static final String  ReportTypeValueNotification = "disposition-notification";  
    
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
    
    public static boolean isNotification(MimeEntity entity)
    {
        if (entity == null)
        {
            return false;
        }

        ContentType contentType = getContentType(entity);
        return contentType.match(MDNStandard.MediaType.DispositionNotification);        
    }    
    
    public static boolean isNotification(MimeMessage msg)
    {
        if (msg == null)
        {
            return false;
        }

        ContentType contentType = getContentType(msg);
        return contentType.match(MDNStandard.MediaType.DispositionNotification);        
    }        
    
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
        }
    }    
    
    private static ContentType getContentType(MimeMessage msg)
    {
    	try
    	{
    		return new ContentType(msg.getContentType());
    	}
    	catch (MessagingException e) {/* no-op */}
    	
    	return null;
    }  
    
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
