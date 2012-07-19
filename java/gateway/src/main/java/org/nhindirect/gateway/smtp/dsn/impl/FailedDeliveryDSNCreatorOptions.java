package org.nhindirect.gateway.smtp.dsn.impl;

public class FailedDeliveryDSNCreatorOptions 
{
	public static final String DEFAULT_PREFIX = "Undeliverable: ";
	
	public static final String DEFAULT_MTA_NAME = "Local Delivery Service";
	
	public static final String DEFAULT_POSTMASTER = "postmaster";
	
	public static final String DEFAULT_FAILED_RECIP_TITLE = "Failed Recipients:";
	
	public static final String DEFAULT_ERROR_MESSAGE_TITLE = "Error Message:";
	
	public static final String DEFAULT_ERROR_MESSAGE = "The Direct address that you tried to reach could not be located or does not exist. Try double-checking the recipient's email address for typos or unnecessary spaces. Learn more by visiting our help.";

	public static final String DEFAULT_HEADER = "We were permanently unable to deliver your message to the following recipients.  Please contact your system administrator with further questions.";
	
	public static final String DEFAULT_FOOTER = "";
	
	
	public static final String DSN_FAILED_PREFIX = "DeliveryFailureMessageDSNPrefix";
	
	public static final String DSN_MTA_NAME = "DeliveryFailureMessageDSNMTAName";
	
	public static final String DSN_POSTMASTER = "DeliveryFailureMessageDSNPostmaster";
	
	public static final String DSN_FAILED_RECIP_TITLE = "DeliveryFailureMessageDSNFailedRecipTitle";
	
	public static final String DSN_FAILED_ERROR_MESSAGE = "DeliveryFailureMessageDSNErrorMessage";
	
	public static final String DSN_FAILED_HEADER = "DeliveryFailureMessageDSNHeader";
	
	public static final String DSN_FAILED_FOOTER = "DeliveryFailureMessageDSNFooter";
}
