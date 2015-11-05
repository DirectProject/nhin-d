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

package org.nhindirect.common.tx.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.common.mail.MDNStandard;
import org.nhindirect.common.mail.MailStandard;
import org.nhindirect.common.mail.MailUtil;
import org.nhindirect.common.mail.dsn.DSNStandard;
import org.nhindirect.common.tx.TxDetailParser;
import org.nhindirect.common.tx.TxUtil;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.common.tx.model.TxMessageType;

import com.sun.mail.dsn.DeliveryStatus;

public class DefaultTxDetailParser implements TxDetailParser
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(DefaultTxDetailParser.class);
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, TxDetail> getMessageDetails(InternetHeaders headers) 
	{
		Map<String, TxDetail> retVal = null;
		
		try
		{
			// convert into a MimeMessage with only the headers
			final MimeMessage msg = new MimeMessage((Session)null);
			
			final Enumeration<String> henum = headers.getAllHeaderLines();
			while (henum.hasMoreElements())
				msg.addHeaderLine(henum.nextElement());
			
			retVal = getMessageDetails(msg);
		}
		///CLOVER:OFF		
		catch (MessagingException e)
		{			

			LOGGER.warn("Failed to translate headers to MimeMessage.", e);

		}
		///CLOVER:ON
		
		return retVal;
	}

	public Map<String, TxDetail> getMessageDetails(InputStream stream)
	{
		Map<String, TxDetail> retVal = null;
		
		if (stream == null)
			throw new IllegalArgumentException("Input stream cannot be null");
		
		try
		{
			// convert into a MimeMessage
			final MimeMessage msg = new MimeMessage(null, stream);
			
			retVal = getMessageDetails(msg);
		}
		///CLOVER:OFF		
		catch (MessagingException e)
		{			

			LOGGER.warn("Failed to translate input stream into MimeMessage.", e);

		}
		///CLOVER:ON
		
		return retVal;		
	}
	
	@SuppressWarnings("incomplete-switch")
	@Override
	public Map<String, TxDetail> getMessageDetails(MimeMessage msg) 
	{
		Map<String, TxDetail> retVal = new HashMap<String, TxDetail>();
		
		// get the message id
		final String msgId = MailStandard.getHeader(msg, MailStandard.Headers.MessageID);
		if (!msgId.isEmpty())
			retVal.put(TxDetailType.MSG_ID.getType(), new TxDetail(TxDetailType.MSG_ID.getType(), msgId));
		
		// get the subject
		final String subject = MailStandard.getHeader(msg, MailStandard.Headers.Subject);
		if (!subject.isEmpty())
			retVal.put(TxDetailType.SUBJECT.getType(), new TxDetail(TxDetailType.SUBJECT.getType(), subject));
		
		// get the full headers as a string
		final String fullHeaders = getHeadersAsStringInternal(msg);
		if (!fullHeaders.isEmpty())
			retVal.put(TxDetailType.MSG_FULL_HEADERS.getType(), new TxDetail(TxDetailType.MSG_FULL_HEADERS.getType(), fullHeaders));		

		// get the from addresses
		try
		{			
			final String from = MailStandard.getHeader(msg, MailStandard.Headers.From);
			if (!from.isEmpty())
			{
				StringBuilder builder = new StringBuilder();
				
				int cnt = 0;
				for (InternetAddress addr :  (InternetAddress[])msg.getFrom())
				{
					// comma delimit multiple addresses
					if (cnt > 0)
						builder.append(",");
					
					builder.append(addr.getAddress().toLowerCase(Locale.getDefault()));
					++cnt;
				}
				
				retVal.put(TxDetailType.FROM.getType(),new TxDetail(TxDetailType.FROM.getType(), builder.toString()));
			}
		}
		/// CLOVER:OFF
		catch (MessagingException e)
		{
			LOGGER.warn("Failed to retrieve message sender list.", e);
		}
		/// CLOVER:ON
		
		// get the sender if it exists
		try
		{
			final InternetAddress sender = (InternetAddress)msg.getSender();
			if (sender != null)
				retVal.put(TxDetailType.SENDER.getType(), new TxDetail(TxDetailType.SENDER.toString(), sender.getAddress().toLowerCase(Locale.getDefault())));
				
		}
		/// CLOVER:OFF
		catch (MessagingException e)
		{
			LOGGER.warn("Failed to retrieve message sender", e);
		}		
		/// CLOVER:ON
		
		// get the recipient addresses
		try
		{			
			if (msg.getAllRecipients() != null)
			{
				StringBuilder builder = new StringBuilder();
				
				int cnt = 0;
				
				for (Address addr :  msg.getAllRecipients())
				{
					// comma delimit multiple addresses
					if (cnt > 0)
						builder.append(",");
					
					
					if (addr instanceof InternetAddress)
						builder.append(((InternetAddress)addr).getAddress().toLowerCase(Locale.getDefault()));
					++cnt;
				}
				
				retVal.put(TxDetailType.RECIPIENTS.getType(), new TxDetail(TxDetailType.RECIPIENTS.getType(), builder.toString()));
			}
		}
		/// CLOVER:OFF
		catch (MessagingException e)
		{
			LOGGER.warn("Failed to retrieve message recipient list.", e);
		}
		/// CLOVER:ON
		
		// get the message type
		final TxMessageType messageType = TxUtil.getMessageType(msg);
		if (messageType != TxMessageType.UNKNOWN)
		{
			//retVal.put(HEMCTxConstants.MESSAGE_TYPE, messageType);
			
			switch (messageType)
			{
				case MDN: 
				{
					// the disposition if a field in the second part of the MDN message
					final String disposition = MDNStandard.getMDNField(msg, MDNStandard.Headers.Disposition);
					if (!disposition.isEmpty())
						retVal.put(TxDetailType.DISPOSITION.getType(), new TxDetail(TxDetailType.DISPOSITION.getType(), disposition.toLowerCase(Locale.getDefault())));
					
					// the final recipients is a field in the second part of the MDN message
					final String finalRecipient = MDNStandard.getMDNField(msg, MDNStandard.Headers.FinalRecipient);
					if (!finalRecipient.isEmpty())
						retVal.put(TxDetailType.FINAL_RECIPIENTS.getType(), new TxDetail(TxDetailType.FINAL_RECIPIENTS.getType(), finalRecipient.toLowerCase(Locale.getDefault())));
					
					// the original message id if a field in the second part of the MDN message
					String origMsgId = MDNStandard.getMDNField(msg, MDNStandard.Headers.OriginalMessageID);
					
					if (origMsgId.isEmpty())
					{
						// it might be in a reply to header
						origMsgId = MailStandard.getHeader(msg, MailStandard.Headers.InReplyTo);
					}
					
					if (!origMsgId.isEmpty())
						retVal.put(TxDetailType.PARENT_MSG_ID.getType(), new TxDetail(TxDetailType.PARENT_MSG_ID.getType(), origMsgId));
					
					// check for X-DIRECT-FINAL-DESTINATION-DELIVER extension
					try
					{
						final InternetHeaders mdnHeaders = MDNStandard.getNotificationFieldsAsHeaders(msg);
						if (mdnHeaders.getHeader(MDNStandard.DispositionOption_TimelyAndReliable, ",") != null)
						{
							retVal.put(TxDetailType.DISPOSITION_OPTIONS.getType(), 
									new TxDetail(TxDetailType.DISPOSITION_OPTIONS.getType(), MDNStandard.DispositionOption_TimelyAndReliable));	
						}
					}
					// CLOVER:OFF
					catch (Exception e)
					{
						LOGGER.warn("Failed to retrieve MDN headers from message.  Message may not be an MDN message.", e);
					}
					// CLOVER:ON
					break;
				}
				case DSN:
				{
					// DSN messages do not necessarily have the original message id... 
					// the Original-Envelope-ID header does not reflect the message id
					try
					{
						final DeliveryStatus status = new DeliveryStatus(new ByteArrayInputStream(MailUtil.serializeToBytes(msg)));
						retVal.put(TxDetailType.FINAL_RECIPIENTS.getType(), new TxDetail(TxDetailType.FINAL_RECIPIENTS.getType(), 
								DSNStandard.getFinalRecipients(status).toLowerCase(Locale.getDefault())));
						// check at the message level
						boolean parentFound = false;
						
						
						final String origMsgId = DSNStandard.getHeaderValueFromDeliveryStatus(status, DSNStandard.Headers.OriginalMessageID);
						if (!origMsgId.isEmpty())
						{
						    parentFound = true;
						    retVal.put(TxDetailType.PARENT_MSG_ID.getType(), new TxDetail(TxDetailType.PARENT_MSG_ID, origMsgId));
						}
						
						if (!parentFound)
						{
							// it might be in a reply to header
							final String parentMsgId = MailStandard.getHeader(msg, MailStandard.Headers.InReplyTo);
							if (!parentMsgId.isEmpty())
								retVal.put(TxDetailType.PARENT_MSG_ID.getType(), new TxDetail(TxDetailType.PARENT_MSG_ID.getType(), parentMsgId));
							
						}
						
						// get the action
						final String action = DSNStandard.getHeaderValueFromDeliveryStatus(status, DSNStandard.Headers.Action);
						if (!action.isEmpty())
							retVal.put(TxDetailType.DSN_ACTION.getType(), new TxDetail(TxDetailType.DSN_ACTION.getType(), action.toLowerCase(Locale.getDefault())));
						
						// get the status
						final String dsnStatus = DSNStandard.getHeaderValueFromDeliveryStatus(status, DSNStandard.Headers.Status);
						if (!dsnStatus.isEmpty())
							retVal.put(TxDetailType.DSN_STATUS.getType(), new TxDetail(TxDetailType.DSN_STATUS.getType(), dsnStatus.toLowerCase(Locale.getDefault())));
					}
					///CLOVER:OFF
					catch (Exception e) 
					{
						LOGGER.warn("Could not get a requested field from the DSN message", e);
					}
					///CLOVER:ON
					break;
				}
			}
		}
		
		// check for the existence of disposition request options
		final String dispOption = MailStandard.getHeader(msg, MDNStandard.Headers.DispositionNotificationOptions);
		if (!dispOption.isEmpty())
			retVal.put(TxDetailType.DISPOSITION_OPTIONS.getType(), 
					new TxDetail(TxDetailType.DISPOSITION_OPTIONS.getType(), dispOption.toLowerCase(Locale.getDefault())));		
		
		return retVal;
	}
	
	protected String getHeadersAsStringInternal(MimeMessage msg)
	{
		return getHeadersAsString(msg);
	}
	
	@SuppressWarnings("unchecked")
	public static String getHeadersAsString(MimeMessage msg)
	{
		StringBuilder builder = new StringBuilder();
		
		try
		{
			Enumeration<String> headers = msg.getAllHeaderLines();
			while (headers.hasMoreElements())
				builder.append(headers.nextElement()).append("\r\n");
		}
		///CLOVER:OFF
		catch (MessagingException e)
		{
			// According to most SE runtimes, getAllHeaderLines will never thrown a MessagingException

			LOGGER.warn("Failed to builder message summary.", e);

		}
		///CLOVER:ON
		return builder.toString();
	}
}
