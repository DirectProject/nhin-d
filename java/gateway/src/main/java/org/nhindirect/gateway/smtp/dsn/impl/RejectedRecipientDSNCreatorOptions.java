package org.nhindirect.gateway.smtp.dsn.impl;

public class RejectedRecipientDSNCreatorOptions 
{
	public static final String DEFAULT_PREFIX = "Undeliverable: ";
	
	public static final String DEFAULT_MTA_NAME = "Security Agent";
	
	public static final String DEFAULT_POSTMASTER = "postmaster";
	
	public static final String DEFAULT_FAILED_RECIP_TITLE = "Failed recipient(s):";
	
	public static final String DEFAULT_ERROR_MESSAGE_TITLE = "";
	
	public static final String DEFAULT_ERROR_MESSAGE = "The Direct address that you tried to reach is not trusted. Try double-checking the recipient's email address for typos or unnecessary spaces. Learn more by visiting our help.";

	public static final String DEFAULT_HEADER = "%original_sender_tag%,<br/>";
	
	public static final String DEFAULT_FOOTER = "<u>Troubleshooting Information</u></b><br/><br/>%headers_tag%";
	
	
	public static final String DSN_FAILED_PREFIX = "RejectedMessageDSNPrefix";
	
	public static final String DSN_MTA_NAME = "RejectedMessageDSNMTAName";
	
	public static final String DSN_POSTMASTER = "RejectedMessageDSNPostmaster";
	
	public static final String DSN_FAILED_RECIP_TITLE = "RejectedMessageDSNFailedRecipTitle";
	
	public static final String DSN_FAILED_ERROR_MESSAGE = "RejectedMessageDSNErrorMessage";
	
	public static final String DSN_FAILED_HEADER = "RejectedMessageDSNHeader";
	
	public static final String DSN_FAILED_FOOTER = "RejectedMessageDSNFooter";
}
