package org.nhindirect.gateway.smtp.dsn.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Address;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.apache.mailet.Mailet;
import org.nhindirect.common.mail.dsn.DSNFailureTextBodyPartGenerator;
import org.nhindirect.common.mail.dsn.DSNGenerator;
import org.nhindirect.common.mail.dsn.DSNMessageHeaders;
import org.nhindirect.common.mail.dsn.DSNRecipientHeaders;
import org.nhindirect.common.mail.dsn.DSNStandard.DSNAction;
import org.nhindirect.common.mail.dsn.DSNStandard.DSNStatus;
import org.nhindirect.common.mail.dsn.DSNStandard.MtaNameType;
import org.nhindirect.common.mail.dsn.impl.DefaultDSNFailureTextBodyPartGenerator;
import org.nhindirect.common.mail.dsn.impl.HumanReadableTextAssemblerFactory;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.gateway.smtp.dsn.DSNCreator;
import org.nhindirect.gateway.smtp.james.mailet.SecurityAndTrustMailetOptions;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.NHINDAddressCollection;
import org.nhindirect.stagent.options.OptionsManager;

public class RejectedRecipientDSNCreator implements DSNCreator 
{
	protected final DSNGenerator generator;
	protected final String postmasterMailbox;
	protected final String reportingMta;
	protected final DSNFailureTextBodyPartGenerator textGenerator;
	
	static
	{		
		initJVMParams();
	}
	
	private synchronized static void initJVMParams()
	{
		/*
		 * Mailet configuration parameters
		 */
		final Map<String, String> JVM_PARAMS = new HashMap<String, String>();
		JVM_PARAMS.put(RejectedRecipientDSNCreatorOptions.DSN_FAILED_PREFIX, "org.nhindirect.gateway.smtp.dsn.impl.DSNFailedPrevis");
		JVM_PARAMS.put(RejectedRecipientDSNCreatorOptions.DSN_MTA_NAME, "org.nhindirect.gateway.smtp.dsn.imp.DSNMTAName");
		JVM_PARAMS.put(RejectedRecipientDSNCreatorOptions.DSN_POSTMASTER, "org.nhindirect.gateway.smtp.dsn.impl.DNSPostmaster");
		JVM_PARAMS.put(RejectedRecipientDSNCreatorOptions.DSN_FAILED_RECIP_TITLE, "org.nhindirect.gateway.smtp.dsn.impl.DSNFaileRecipTitle");
		JVM_PARAMS.put(RejectedRecipientDSNCreatorOptions.DSN_FAILED_ERROR_MESSAGE, "org.nhindirect.gateway.smtp.dsn.impl.DSNFailedErrorMessage");
		JVM_PARAMS.put(RejectedRecipientDSNCreatorOptions.DSN_FAILED_HEADER, "org.nhindirect.gateway.smtp.dsn.impl.DSNFailedHeader");
		JVM_PARAMS.put(RejectedRecipientDSNCreatorOptions.DSN_FAILED_FOOTER, "org.nhindirect.gateway.smtp.dsn.impl.DSNFailedFooter");
		
		OptionsManager.addInitParameters(JVM_PARAMS);
	}
	
	public RejectedRecipientDSNCreator(DSNGenerator generator, String postmasterMailbox, String reportingMta, 
			DSNFailureTextBodyPartGenerator textGenerator)
	{
		this.generator = generator;
		this.postmasterMailbox = postmasterMailbox;
		this.reportingMta = reportingMta;
		this.textGenerator = textGenerator;
	}
	
	public RejectedRecipientDSNCreator(Mailet mailet)
	{
		
		generator = new DSNGenerator(SecurityAndTrustMailetOptions.getConfigurationParam(RejectedRecipientDSNCreatorOptions.DSN_FAILED_PREFIX, 
				mailet, RejectedRecipientDSNCreatorOptions.DEFAULT_PREFIX));
		
		postmasterMailbox = SecurityAndTrustMailetOptions.getConfigurationParam(RejectedRecipientDSNCreatorOptions.DSN_POSTMASTER, 
				mailet, RejectedRecipientDSNCreatorOptions.DEFAULT_POSTMASTER);
		
		reportingMta = SecurityAndTrustMailetOptions.getConfigurationParam(RejectedRecipientDSNCreatorOptions.DSN_MTA_NAME, 
				mailet, RejectedRecipientDSNCreatorOptions.DEFAULT_MTA_NAME);
		
		
		textGenerator = new DefaultDSNFailureTextBodyPartGenerator(
				SecurityAndTrustMailetOptions.getConfigurationParam(RejectedRecipientDSNCreatorOptions.DSN_FAILED_HEADER, 
						mailet, RejectedRecipientDSNCreatorOptions.DEFAULT_HEADER), 
				SecurityAndTrustMailetOptions.getConfigurationParam(RejectedRecipientDSNCreatorOptions.DSN_FAILED_FOOTER, 
						mailet, RejectedRecipientDSNCreatorOptions.DEFAULT_FOOTER), 
				SecurityAndTrustMailetOptions.getConfigurationParam(RejectedRecipientDSNCreatorOptions.DSN_FAILED_RECIP_TITLE, 
						mailet, RejectedRecipientDSNCreatorOptions.DEFAULT_FAILED_RECIP_TITLE), 
					RejectedRecipientDSNCreatorOptions.DEFAULT_ERROR_MESSAGE_TITLE,
				SecurityAndTrustMailetOptions.getConfigurationParam(RejectedRecipientDSNCreatorOptions.DSN_FAILED_ERROR_MESSAGE, 
								mailet, RejectedRecipientDSNCreatorOptions.DEFAULT_ERROR_MESSAGE_TITLE),
			    HumanReadableTextAssemblerFactory.getInstance());
	}

	@Override
	public MimeMessage createDSNFailure(Tx tx, NHINDAddressCollection failedRecipeints) throws MessagingException
	{
	    InternetAddress originalSender = null;
	    String originalSubject = "";
	    InternetAddress postmaster = null;
	    String originalMessageId = "";
	    Enumeration<Header> fullMessageHeaders = null;
	    
	    final List<DSNRecipientHeaders> recipientDSNHeaders = new ArrayList<DSNRecipientHeaders>();
	    final List<Address> failedRecipAddresses = new ArrayList<Address>();
	    
	    final TxDetail sender = tx.getDetail(TxDetailType.FROM);
	    if (sender != null)
	    {
	    	originalSender = new InternetAddress(sender.getDetailValue());
	    	postmaster = new InternetAddress(postmasterMailbox + "@" + getAddressDomain(originalSender));
	    }
	    
	    final TxDetail subject = tx.getDetail(TxDetailType.SUBJECT);
	    if (subject != null)
	    	originalSubject = subject.getDetailValue();
	    
	    
	    for (NHINDAddress incompleteRecip : failedRecipeints)
	    {
	    	
	    	DSNRecipientHeaders dsnRecipHeaders = 
	    			new DSNRecipientHeaders(DSNAction.FAILED, 
	    			DSNStatus.getStatus(DSNStatus.PERMANENT, DSNStatus.UNDEFINED_STATUS), incompleteRecip);
	    	
	    	recipientDSNHeaders.add(dsnRecipHeaders);
	    	failedRecipAddresses.add(incompleteRecip);
	    }
	    
	    ///CLOVER:OFF
	    final TxDetail origMessId = tx.getDetail(TxDetailType.MSG_ID);
	    if (origMessId != null)
	    	originalMessageId = origMessId.getDetailValue();
	    ///CLOVER:ON
	    
	    final DSNMessageHeaders messageDSNHeaders = new DSNMessageHeaders(reportingMta, originalMessageId, MtaNameType.DNS);
	    
	    final TxDetail fullHeaders = tx.getDetail(TxDetailType.MSG_FULL_HEADERS);
	    if (fullHeaders != null)
	    	fullMessageHeaders = this.convertStringToHeaders(fullHeaders.getDetailValue());
	    
	    final MimeBodyPart textBodyPart = textGenerator.generate(originalSender, failedRecipAddresses, fullMessageHeaders);
	    
	    return generator.createDSNMessage(originalSender, originalSubject, postmaster, recipientDSNHeaders, messageDSNHeaders, textBodyPart);

	}
	
    /**
     * Get the domain of an email address
     * @param theAddress
     * @return
     */
    ///CLOVER:OFF
    protected String getAddressDomain(InternetAddress theAddress)
    {
    	String retVal = "";
    	
    	// remove any extra information such as < and >
    	String address = theAddress.getAddress();
    	int index;
    	if ((index = address.indexOf('<')) > -1)
    		address = address.substring(index + 1);
    	
    	if ((index = address.indexOf('>')) > -1)
    		address = address.substring(0, index); 
    	
    	index = address.indexOf("@");
    	if (index >= 0)
    		retVal = address.substring(index + 1);
    	
    	return retVal;
    }
    ///CLOVER:ON
    
    /**
     * Converts a string to an enumeration of Internet headers.
     * @param asString The string to convert.
     * @return
     */
    ///CLOVER:OFF
    @SuppressWarnings("unchecked")
	protected Enumeration<Header> convertStringToHeaders(String asString)
    {
    	final InputStream str = IOUtils.toInputStream(asString);	
    	try
    	{
    	   	InternetHeaders headers = new InternetHeaders(str);
    	
    	   	return headers.getAllHeaders();
    	}
    	catch (Exception e)
    	{
    		// log warning
    	}
    	finally
    	{
        	IOUtils.closeQuietly(str);    		
    	}
    	
    	return null;
    }    
    ///CLOVER:ON
}
