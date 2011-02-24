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

package org.nhindirect.gateway.smtp;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.common.audit.AuditContext;
import org.nhindirect.common.audit.AuditEvent;
import org.nhindirect.common.audit.Auditor;
import org.nhindirect.common.audit.AuditorFactory;
import org.nhindirect.common.audit.DefaultAuditContext;
import org.nhindirect.stagent.DefaultMessageEnvelope;
import org.nhindirect.stagent.IncomingMessage;
import org.nhindirect.stagent.NHINDAgent;
import org.nhindirect.stagent.MessageEnvelope;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.NHINDAddressCollection;
import org.nhindirect.stagent.NHINDException;
import org.nhindirect.stagent.OutgoingMessage;
import org.nhindirect.stagent.cryptography.SMIMEStandard;
import org.nhindirect.stagent.mail.MailStandard;
import org.nhindirect.stagent.mail.Message;
import org.nhindirect.stagent.mail.notifications.MDNStandard;
import org.nhindirect.stagent.mail.notifications.NotificationHelper;
import org.nhindirect.stagent.mail.notifications.NotificationMessage;
import org.nhindirect.stagent.parser.EntitySerializer;

import com.google.inject.Inject;



/**
 * Default implementation of the SmtpAgent interface.
 * {@inheritDoc}
 */
public class DefaultSmtpAgent implements SmtpAgent
{	
	private static final Log LOGGER = LogFactory.getFactory().getInstance(DefaultSmtpAgent.class);
	
	private static final String PRINICPAL;
	
	private static final String EVENT_NAME = "SMTP Direct Message Processing";
	
	private static final String INCOMING_MESSAGE_TYPE = "Incoming Direct Message";
	private static final String OUTGOING_MESSAGE_TYPE = "Outgoing Direct Message";
	private static final String PRODUCE_MDN_TYPE = "Produce Direct MDN Message";
	private static final String MDN_RECEIVED_TYPE = "Received Direct MDN Message";
	private static final String REJECTED_RECIP_TYPE = "Rejected Direct Message Recipients";
	private static final String REJECTED_MESSAGE_TYPE = "Rejected Direct Message";
	
	private static final String[] DEFAULT_HEADER_CONTEXT = {MailStandard.Headers.MessageID, MailStandard.Headers.From, MailStandard.Headers.To};
	
	private static final String[] MDN_HEADER_CONTEXT = {MailStandard.Headers.MessageID, MailStandard.Headers.From, 
		MailStandard.Headers.To, MDNStandard.Headers.Disposition};
	
	private static final String[] MDN_RECEIVED_CONTEXT = {MailStandard.Headers.MessageID, MailStandard.Headers.From, 
		MailStandard.Headers.To, MDNStandard.Headers.OriginalMessageID, MDNStandard.Headers.Disposition, MDNStandard.Headers.FinalRecipient,};
	
	private final NHINDAgent agent;
	private final SmtpAgentSettings settings;
	private Auditor auditor;
	
	static
	{

		String host = "";
		
		try 
		{
			host = InetAddress.getLocalHost().getHostName();
			host = "@" + host;
		}
		catch (UnknownHostException e)
		{
			LOGGER.warn("Coulnd not get host name: " + e.getMessage());
		}
		
		PRINICPAL = "STAgent" + host;
	}
	
	/**
	 * Constructs an Smtp agent with settings and an instance of the security and trust agent.
	 * @param settings The SMTP agent configuration settings.
	 * @param agent An instance of the security and trust agent.
	 */
	@Inject
	public DefaultSmtpAgent(SmtpAgentSettings settings, NHINDAgent agent)
	{
		this(settings, agent, null);
	}
	
	/**
	 * Constructs an Smtp agent with settings, an instance of the security and trust agent, 
	 * and an event auditor.
	 * @param settings The SMTP agent configuration settings.
	 * @param agent An instance of the security and trust agent.
	 * @param auditor The auditor used to log auditable events.
	 */
	public DefaultSmtpAgent(SmtpAgentSettings settings, NHINDAgent agent, Auditor auditor)
	{
		if (settings == null || agent == null)
			throw new IllegalArgumentException("Setting and/or agent cannot be null.");
		
		this.settings = settings;
		this.agent = agent;
		
		if (auditor == null)
			this.auditor = AuditorFactory.createAuditor();  // use the default auditor
	}	
	
	/**
	 * Gets a references to the security and trust agent used by the SmtpAgent.
	 * @return A references to the security and trust agent used by the SmtpAgent
	 */
	public NHINDAgent getAgent()
	{
		return this.agent;
	}
	
	/**
	 * Gets the configuration settings of the SmtpAgent.
	 * @return The configuration settings of the SmtpAgent.
	 */
	public SmtpAgentSettings getSmtpAgentSettings()
	{
		return this.settings;
	}
	
	/**
	 * Sets the auditor used to log auditable events.
	 * @param auditor The auditor used to log auditable events.
	 */
    @Inject(optional=true)
	public void setAuditor(Auditor auditor)
	{
		this.auditor = auditor;
	}
	
	/**
	 * Gets the auditor used to log auditable events.
	 * @param auditor The auditor used to log auditable events.
	 */
	public Auditor getAuditor()
	{
		return auditor;
	}
	
	/**
	 * Processes an message from an SMTP stack.  The bridge component between the SMTP stack and the SMTP agent is responsible for
	 * extracting the message, the recipient list, and the sender.  In some cases, the routing headers may have different information than
	 * what is populated in the SMTP MAIL FROM and RCTP TO headers.  In these cases, the SMTP headers should be favored over the routing
	 * headers in the message and passed as the recipient collection and sender to this method.
	 * @param message The message in the SMTP envelope.
	 * @param recipients The recipients of the message.  The RCTP TO headers should be used over the message routing headers.
	 * @param sender The send of the message. The MAIL FROM header should be used over the From: routing header in the message.
	 */
	public MessageProcessResult processMessage(MimeMessage message, NHINDAddressCollection recipients, NHINDAddress sender)
	{
		LOGGER.trace("Entering processMessage(MimeMessage, NHINDAddressCollection, NHINDAddress");
		
		MessageProcessResult retVal = null;
		
		verifyInitialized();
		
		preProcessMessage(message, sender);

		DefaultMessageEnvelope envelopeToProcess = null;
		try
		{
			envelopeToProcess = new DefaultMessageEnvelope(new Message(message), recipients, sender);			
			envelopeToProcess.setAgent(agent);
			
			// should always result in either a non null object or an exception
			MessageEnvelope processEvn = processEnvelope(envelopeToProcess);
			retVal = new MessageProcessResult(processEvn, null);
			
			if (retVal.getProcessedMessage() != null)
				postProcessMessage(retVal);						
		}
		catch (SmtpAgentException e)
		{
			// rethrow
			LOGGER.trace("Exiting processMessage(MimeMessage, NHINDAddressCollection, NHINDAddress", e);
			throw e;
		}
		catch (Exception e)
		{
			// audit the message rejection
			if (envelopeToProcess != null)
			{
				Collection<AuditContext> contexts = createContextCollectionFromMessage(envelopeToProcess,
					Arrays.asList(DEFAULT_HEADER_CONTEXT));
				
				if (e instanceof NHINDException)
				{
					if (((NHINDException)e).getError() != null)
						contexts.add(new DefaultAuditContext("rejected reason", ((NHINDException)e).getError().toString()));
				}
				auditor.audit(PRINICPAL, new AuditEvent(EVENT_NAME, REJECTED_MESSAGE_TYPE), contexts);
			}
			
			LOGGER.trace("Exiting processMessage(MimeMessage, NHINDAddressCollection, NHINDAddress", e);
			throw new SmtpAgentException(SmtpAgentError.Unknown, e);
		}
		
		LOGGER.trace("Exiting processMessage(MimeMessage, NHINDAddressCollection, NHINDAddress");
		return retVal;
	}
	
	/*
	 * Validate the SmtpAgent is valid.
	 */
	private void verifyInitialized()
	{
		if (agent == null)
			throw new SmtpAgentException(SmtpAgentError.Uninitialized, "SmtpAgent not fully initialized: Security and Trust agent is null");
	}

	private void preProcessMessage(MimeMessage message, NHINDAddress sender)
	{
		LOGGER.debug("Message Recieved from: " + sender.getAddress());
		copyMessage(message, settings.getRawMessageSettings());		
	}
	
	/*
	 * Determines if the message is outgoing or incoming.  Need to take in account that the sender and recipient may be from the same domain.
	 */
	public boolean isOutgoing(MessageEnvelope envelope)
	{		
		// if the sender is not from our domain, then is has to be an incoming message
		if (!envelope.getSender().isInDomain(agent.getDomains()))
			return false;
		else
		{
			// depending on the SMTP stack configuration, a message with a sender from our domain
			// may still be an incoming message... check if the message is encrypted
			if (SMIMEStandard.isEncrypted(envelope.getMessage()))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/*
	 * Processes a message using the securty and trust agent.
	 */
    protected MessageEnvelope processEnvelope(MessageEnvelope envelope)
    {
    	
    	MessageEnvelope processedMessage = null;
    	boolean isOutgoing = isOutgoing(envelope);
    	
		if (isOutgoing)
		{
			if (auditor != null)
			{
				Collection<AuditContext> contexts = createContextCollectionFromMessage(envelope,
						Arrays.asList(DEFAULT_HEADER_CONTEXT));
				
				auditor.audit(PRINICPAL, new AuditEvent(EVENT_NAME, OUTGOING_MESSAGE_TYPE), contexts);
			}	
			LOGGER.debug("Sending outgoing message from " + envelope.getSender().toString() + " to STAgent");
		}
		else
		{
			if (auditor != null)
			{
				Collection<AuditContext> contexts = createContextCollectionFromMessage(envelope,
						Arrays.asList(DEFAULT_HEADER_CONTEXT));
				
				auditor.audit(PRINICPAL, new AuditEvent(EVENT_NAME, INCOMING_MESSAGE_TYPE), contexts);
			}				
			LOGGER.debug("Sending incoming message from " + envelope.getSender().toString() + " to STAgent");
		}

		
		processedMessage = (isOutgoing) ? agent.processOutgoing(envelope) : agent.processIncoming(envelope);
		
		return processedMessage;		
    }
	

    
    
    private void postProcessMessage(MessageProcessResult result)
    {    	
        boolean isOutgoing = (result.getProcessedMessage() instanceof OutgoingMessage);

        // check for rejected recipients
        if (auditor != null && result.getProcessedMessage().getRejectedRecipients() != null
        		&& result.getProcessedMessage().getRejectedRecipients().size() > 0)
        {
			Collection<AuditContext> contexts = createContextCollectionFromMessage(result.getProcessedMessage(),
					Arrays.asList(DEFAULT_HEADER_CONTEXT));
        	StringBuffer rejectedRecips = new StringBuffer();
        	
        	int cnt = 0;
        	for (NHINDAddress address : result.getProcessedMessage().getRejectedRecipients())
        	{
        		rejectedRecips.append(address.getAddress());
        		
        		if (++cnt < result.getProcessedMessage().getRejectedRecipients().size())
        			rejectedRecips.append(", ");
        	}
        	
        	contexts.add(new DefaultAuditContext("rejected recipients", rejectedRecips.toString()));
        	auditor.audit(PRINICPAL, new AuditEvent(EVENT_NAME, REJECTED_RECIP_TYPE), contexts);
        }
        
        if (isOutgoing)
        	postProcessOutgoingMessage(result);
        else
        	postProcessIncomingMessage(result);
    }    
    
    private void postProcessOutgoingMessage(MessageProcessResult result)
    {
    	if (result.getProcessedMessage().hasRecipients())
    		copyMessage(result.getProcessedMessage().getMessage(), settings.getOutgoingMessageSettings());
    }
    
    private void postProcessIncomingMessage(MessageProcessResult result)
    {
        this.copyMessage(result.getProcessedMessage().getMessage(), settings.getIncomingMessageSettings());
        
        // check if we need to create notification messages
        try
        {
        	if (settings.getNotificationProducer() != null)
        	{
        		result.setNotificationMessages(settings.getNotificationProducer().
        				produce((IncomingMessage)result.getProcessedMessage()));
        		
        		if (result.getNotificationMessages() != null && auditor != null)
        		{
        			for (NotificationMessage noteMsg : result.getNotificationMessages())
        			{
                		Collection<AuditContext> contexts = createContextCollectionFromMessage(noteMsg, Arrays.asList(MDN_HEADER_CONTEXT));
                		auditor.audit(PRINICPAL, new AuditEvent(EVENT_NAME, PRODUCE_MDN_TYPE), contexts);
        			}
        		}

        	}        	
        }
        catch (Exception e)
        {
        	// don't bail on the whole process if we can't create notifications messages
        	LOGGER.error("Failed to create notification messages.", e);
        }
        
    	// check if this is an incoming MDN message... is so, audit it
    	if (NotificationHelper.isMDN(result.getProcessedMessage().getMessage()))
    	{
    		Collection<AuditContext> contexts = createContextCollectionFromMessage(result.getProcessedMessage(), Arrays.asList(MDN_RECEIVED_CONTEXT));
    		auditor.audit(PRINICPAL, new AuditEvent(EVENT_NAME, MDN_RECEIVED_TYPE), contexts);
    	}        
    }
    
    /*
     * Copy the content of message into a configured folder.
     */
	private void copyMessage(MimeMessage message, MessageProcessingSettings settings)
	{		
        if (settings != null && settings.hasSaveMessageFolder())
        {
        	File fl = new File(settings.getSaveMessageFolder().getAbsolutePath() + File.separator + generateUniqueFileName());

        	try
        	{
        		FileUtils.writeStringToFile(fl, EntitySerializer.Default.serialize(message));
        	}
        	catch (IOException e)
        	{
        		/*
        		 * TODO: Add exception handling
        		 */
        	}

        }
	}
	
	/*
	 * Generate a unique file name using a UUID.
	 */
	private String generateUniqueFileName()
	{
		return UUID.randomUUID().toString() + ".eml";
	}
	
	/*
	 * Create a collection of context from a message
	 */
	private Collection<AuditContext> createContextCollectionFromMessage(Message msg, Collection<String> headers)
	{
		return  createContextCollectionFromMessage(new DefaultMessageEnvelope(msg), headers);
	}	
	
	/*
	 * Create a collection of context from a message envelope
	 */
	private Collection<AuditContext> createContextCollectionFromMessage(MessageEnvelope env, Collection<String> headers)
	{
		Collection<AuditContext> retVal = new ArrayList<AuditContext>();
		
		for (String header : headers)
		{
			try
			{
				String theHeader = null;
				if (header.equals(MailStandard.Headers.From))
					theHeader = env.getSender().getAddress();
				else if (header.equals(MDNStandard.Headers.Disposition) || 
						header.equals(MDNStandard.Headers.OriginalMessageID) ||
						header.equals(MDNStandard.Headers.FinalRecipient))
				{
					Message msg = env.getMessage();
					MimeBodyPart part = getNotificationPart(msg);
					if (part != null)
					{
						theHeader = part.getHeader(header, ",");
					}
					else
						theHeader = env.getMessage().getHeader(header, ",");
				}			
				else
					theHeader = env.getMessage().getHeader(header, ",");
				
				if (theHeader != null && !theHeader.isEmpty())
				{
					retVal.add(new DefaultAuditContext(header, theHeader));
				}
			}
			catch (MessagingException e)
			{
				LOGGER.warn("Error retrieving header " + header + " from the message.");
			}
		}
		
		return retVal;
	}
	
	/*
	 * try to get the notification part of an multipart/report message
	 */
	private MimeBodyPart getNotificationPart(Message noteMessage)
	{
		MimeBodyPart retVal = null;
		
		try
		{
			ByteArrayDataSource dataSource = new ByteArrayDataSource(noteMessage.getRawInputStream(), noteMessage.getContentType());
		
			MimeMultipart dispMsg = new MimeMultipart(dataSource);
			retVal = (MimeBodyPart)dispMsg.getBodyPart(1);
		}
		catch (Exception e)
		{
			
		}
		
		return retVal;
	}
}
