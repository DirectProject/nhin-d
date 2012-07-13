package org.nhindirect.common.mail.dsn;

import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import org.nhindirect.common.mail.MailStandard;
import org.nhindirect.common.mail.dsn.DSNStandard.DSNAction;
import org.nhindirect.common.mail.dsn.DSNStandard.MtaNameType;

import com.sun.mail.dsn.DeliveryStatus;
import com.sun.mail.dsn.MultipartReport;

/**
 * Generator for creating DSN messages
 * 
 * @author Bret Fulcher <BF3174@cerner.com>
 * @author Greg Meyer
 * @since 1.1
 */
public class DSNGenerator 
{
    private static final int DELIVERY_STATUS_MULTIPART_INDEX = 1;
    
    protected final String subjectPrefix;
    
    /**
     * Constructor
     * @param subjectPrefix A prefix of the message subject
     */
    public DSNGenerator(String subjectPrefix)
    {
    	this.subjectPrefix = subjectPrefix;
    }
    
    /**
     * Creates a DSN message message.
     * @param originalSender The original sender of the message
     * @param originalSubject The subject of the original message
     * @param postmaster The postmaster address that the DSN message will be from
     * @param recipientDSNHeaders A list of recipient DSN headers to populate the delivery status part of the DSN message
     * @param messageDSNHeaders  The message DSN headers to populate the delivery status part of the DSN message
     * @param humanReadableText The human readable part (the first part) or the DSN message
     * @return A mime message containing the full DSN message
     * @throws MessagingException
     */
	public MimeMessage createDSNMessage(InternetAddress originalSender, String originalSubject,
			InternetAddress postmaster, List<DSNRecipientHeaders> recipientDSNHeaders,
		      DSNMessageHeaders messageDSNHeaders, MimeBodyPart humanReadableText) throws MessagingException
	{
        
        final DeliveryStatus deliveryStatus = createDeliveryStatus(recipientDSNHeaders, messageDSNHeaders);

        
        // assemble multipart report
        final MultipartReport multipartReport = new MultipartReport("", deliveryStatus);
        
        // set name of the delivery status file
        multipartReport.getBodyPart(DELIVERY_STATUS_MULTIPART_INDEX).setFileName("status.dat");

        // set text body part
        multipartReport.setTextBodyPart(humanReadableText);

        // create mime message to send from the MultipartReport
	    final Properties properties = new Properties();
	    properties.setProperty("mail.from", postmaster.getAddress());
	      
	    final Session session = Session.getInstance(properties);
	      
	    final MimeMessage destinationMessage = new MimeMessage(session);
	    destinationMessage.setContent(multipartReport);
	    destinationMessage.setFrom(postmaster);
        destinationMessage.addRecipient(RecipientType.TO, originalSender);
	    destinationMessage.setSubject(subjectPrefix + originalSubject);
	    destinationMessage.setHeader(MailStandard.Headers.InReplyTo, messageDSNHeaders.getOriginalMessageId());
        destinationMessage.saveChanges();

        return destinationMessage;
	}
	
	/**
	 * Creates the deliver status section of the message
	 * @param recipientDSNHeaders
	 * @param messageDSNHeaders
	 * @return
	 * @throws MessagingException
	 */
    protected DeliveryStatus createDeliveryStatus(List<DSNRecipientHeaders> recipientDSNHeaders,
    		DSNMessageHeaders messageDSNHeaders) throws MessagingException
    {
        final DeliveryStatus deliveryStatus = new DeliveryStatus();
    	for (DSNRecipientHeaders dsnHeaders : recipientDSNHeaders) 
    	{
            final InternetHeaders recipHeaders = new InternetHeaders();
            final DSNAction dsnAction = dsnHeaders.getAction();
            recipHeaders.addHeader(DSNStandard.Headers.Action, "" + dsnAction);
            final Address finalRecipient = dsnHeaders.getFinalRecipient();
            
            if (finalRecipient != null) 
            {
            	recipHeaders.addHeader(DSNStandard.Headers.FinalRecipient, finalRecipient.getType()
                  + ";" + finalRecipient.toString());
            }
            String status = dsnHeaders.getStatus();
            recipHeaders.addHeader(DSNStandard.Headers.Status, status);
            deliveryStatus.addRecipientDSN(recipHeaders);
        }
    	
        final InternetHeaders messageHeaders = new InternetHeaders();
        final MtaNameType mtaNameType = messageDSNHeaders.getMtaNameType();
        final String reportingMta = messageDSNHeaders.getReportingMta();
        messageHeaders.addHeader(DSNStandard.Headers.ReportingMTA, mtaNameType + ";"
            + reportingMta);
        
        // add a custom header with original message id
        final String originalMessageId = messageDSNHeaders.getOriginalMessageId();
        messageHeaders.addHeader(DSNStandard.Headers.OriginalMessageID, originalMessageId);
        deliveryStatus.setMessageDSN(messageHeaders);
    	
    	return deliveryStatus;
    }
	
}
