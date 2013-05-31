/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
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

package org.nhindirect.gateway.smtp.dsn.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

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
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.gateway.smtp.dsn.DSNCreator;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.NHINDAddressCollection;

/**
 * Abstract implementation of the DSNCreator interface.
 * @author Greg Meyer
 *
 */
public abstract class AbstractDSNCreator implements DSNCreator 
{
	protected Mailet mailet;
	protected DSNGenerator generator;
	protected String postmasterMailbox;
	protected String reportingMta;
	protected DSNFailureTextBodyPartGenerator textGenerator;
	protected String dsnStatus;
	
	@Override
	public MimeMessage createDSNFailure(Tx tx, NHINDAddressCollection failedRecipeints) throws MessagingException
	{
		final Collection<MimeMessage> dsnMessages = createDSNFailure(tx, failedRecipeints, true);
		
		if (dsnMessages != null && dsnMessages.size() > 0)
			return dsnMessages.iterator().next();
		else
			return null;
	}
	
	@Override
	public Collection<MimeMessage> createDSNFailure(Tx tx, NHINDAddressCollection failedRecipeints, boolean useSenderDomainForPostmaster) throws MessagingException
	{
		Collection<MimeMessage> retVal = new ArrayList<MimeMessage>();
	    InternetAddress originalSender = null;
	    String originalSubject = "";
	   
	    String originalMessageId = "";
	    Enumeration<Header> fullMessageHeaders = null;
	    
	    final List<Address> failedRecipAddresses = new ArrayList<Address>();
	    
	    final TxDetail subject = tx.getDetail(TxDetailType.SUBJECT);
	    if (subject != null)
	    	originalSubject = subject.getDetailValue();
	    
	    ///CLOVER:OFF
	    final TxDetail origMessId = tx.getDetail(TxDetailType.MSG_ID);
	    if (origMessId != null)
	    	originalMessageId = origMessId.getDetailValue();
	    ///CLOVER:ON
	    
	    final TxDetail fullHeaders = tx.getDetail(TxDetailType.MSG_FULL_HEADERS);
	    if (fullHeaders != null)
	    	fullMessageHeaders = this.convertStringToHeaders(fullHeaders.getDetailValue());
	    
	    final DSNMessageHeaders messageDSNHeaders = new DSNMessageHeaders(reportingMta, originalMessageId, MtaNameType.DNS);
	    
	    final TxDetail sender = tx.getDetail(TxDetailType.FROM);
	    if (sender != null)
	    	originalSender = new InternetAddress(sender.getDetailValue());
	    
	    final Map<InternetAddress, Collection<NHINDAddress>> dsnMessagePostmasterToFailedRecipMap = 
	    		groupPostMasterAndFailedRecips(sender, failedRecipeints, useSenderDomainForPostmaster);
	    
	    if (dsnMessagePostmasterToFailedRecipMap.size() > 0)
	    {
	    	for (Entry<InternetAddress, Collection<NHINDAddress>> entry: dsnMessagePostmasterToFailedRecipMap.entrySet())
	    	{	
	    	    final List<DSNRecipientHeaders> recipientDSNHeaders = new ArrayList<DSNRecipientHeaders>();
			    for (NHINDAddress incompleteRecip : entry.getValue())
			    {
			    	
			    	final DSNRecipientHeaders dsnRecipHeaders = 
			    			new DSNRecipientHeaders(DSNAction.FAILED, 
			    			DSNStatus.getStatus(DSNStatus.PERMANENT, dsnStatus), incompleteRecip);
			    	
			    	recipientDSNHeaders.add(dsnRecipHeaders);
			    	failedRecipAddresses.add(incompleteRecip);
			    	
			    }
			    
			    final MimeBodyPart textBodyPart = textGenerator.generate(originalSender, failedRecipAddresses, fullMessageHeaders);
			    final MimeMessage dsnMessage = 
			    		generator.createDSNMessage(originalSender, originalSubject, entry.getKey(), recipientDSNHeaders, messageDSNHeaders, textBodyPart);
			    
			    retVal.add(dsnMessage);
	    	}
	    }

	    return retVal;

	}
	
	/**
	 * Groups postmasters with the proper list of failed recipients
	 * @param sender The sender of the original message
	 * @param failedRecipeints List of all failed recipients
	 * @param useSenderAsPostmaster Indicates if the sender's domain should be used as the postmaster domain.
	 * @return Map grouping a collection of failed recipients with a postmaster.  If the useSenderAsPostmaster flag is true, all failed recipients with be associated
	 * with one postmaster (using the senders domain).  Otherwise, a postmaster address is created per unique failed recipient domain and failed recipients are grouped by domain.
	 */
	protected Map<InternetAddress, Collection<NHINDAddress>> groupPostMasterAndFailedRecips(TxDetail sender, NHINDAddressCollection failedRecipeints, 
			boolean useSenderAsPostmaster) throws MessagingException
	{
		final Map<InternetAddress, Collection<NHINDAddress>> postmasterToFailed = new HashMap<InternetAddress, Collection<NHINDAddress>>();
		
		if (useSenderAsPostmaster)
		{
			// just group all failed recipients into one collection
			if (sender != null)
			{
		    	final InternetAddress originalSender = new InternetAddress(sender.getDetailValue());
		    	final InternetAddress postmaster = new InternetAddress(postmasterMailbox + "@" + getAddressDomain(originalSender));
		    	
		    	postmasterToFailed.put(postmaster, failedRecipeints);
			}
		}
		else
		{
			// group by domain
			for (NHINDAddress failedRecipeint : failedRecipeints)
			{
				// get the postmaster for the failed recip
		    	final InternetAddress postmaster = new InternetAddress(postmasterMailbox + "@" + failedRecipeint.getHost().toLowerCase(Locale.getDefault()));
		    	Collection<NHINDAddress> group = postmasterToFailed.get(postmaster);
		    	if (group == null)
		    	{
		    		group = new ArrayList<NHINDAddress>();
		    		postmasterToFailed.put(postmaster, group);
		    	}
		    	group.add(failedRecipeint);
				
			}
		}
		
		return postmasterToFailed;
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
