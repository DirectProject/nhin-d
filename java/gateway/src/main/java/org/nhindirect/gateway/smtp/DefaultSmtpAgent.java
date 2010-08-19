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
import java.util.Locale;
import java.util.UUID;

import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.gateway.smtp.annotation.IncomingBounceCreator;
import org.nhindirect.gateway.smtp.annotation.OutgoingBounceCreator;
import org.nhindirect.gateway.smtp.annotation.SmtpAgentVerboseLogging;
import org.nhindirect.stagent.AgentError;
import org.nhindirect.stagent.AgentException;
import org.nhindirect.stagent.DefaultMessageEnvelope;
import org.nhindirect.stagent.NHINDAgent;
import org.nhindirect.stagent.IncomingMessage;
import org.nhindirect.stagent.MessageEnvelope;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.NHINDAddressCollection;
import org.nhindirect.stagent.OutgoingMessage;
import org.nhindirect.stagent.mail.Message;
import org.nhindirect.stagent.parser.EntitySerializer;

import com.google.inject.Inject;

/**
 * {@inheritDoc}
 */
public class DefaultSmtpAgent implements SmtpAgent
{	
	private static final Log LOGGER = LogFactory.getFactory().getInstance(DefaultSmtpAgent.class);
	
	private final NHINDAgent agent;
	private final SmtpAgentSettings settings;
    private final BounceMessageCreator outgoingBounceFactory;
    private final BounceMessageCreator incomingBounceFactory;
	private boolean logVerbose = true;
	
	/**
	 * 
	 * @param settings
	 * @param agent
	 * @param outgoingBounceFactory
	 * @param incomingBounceFactory
	 */
	@Inject
	public DefaultSmtpAgent(SmtpAgentSettings settings, NHINDAgent agent, 
			@OutgoingBounceCreator BounceMessageCreator outgoingBounceFactory,
			@IncomingBounceCreator BounceMessageCreator incomingBounceFactory)
	{
		if (settings == null || agent == null)
			throw new IllegalArgumentException("Setting and/or agent cannot be null.");
		
		this.settings = settings;
		this.agent = agent;
		this.outgoingBounceFactory = outgoingBounceFactory;
		this.incomingBounceFactory = incomingBounceFactory;
	}
	
	/**
	 * 
	 * @param logVerbose
	 */
	@Inject(optional=true)
	public void setVerboseLogging(@SmtpAgentVerboseLogging boolean logVerbose)
	{
		this.logVerbose = logVerbose;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isVerboseLogging()
	{
		return logVerbose;
	}
	
	private void logStatus(String message)
	{
		if (logVerbose)
			LOGGER.info(message);
	}
	
	
	public NHINDAgent getAgent()
	{
		return this.agent;
	}
	
	public SmtpAgentSettings getSmtpAgentSettings()
	{
		return this.settings;
	}
	
	public BounceMessageCreator getIncomingBounceCreator()
	{
		return this.incomingBounceFactory;
	}

	public BounceMessageCreator getOutgoingBounceCreator()
	{
		return this.outgoingBounceFactory;
	}
	
	/**
	 * 
	 * @param message
	 * @param recipients
	 * @param sender
	 */
	public MessageProcessResult processMessage(MimeMessage message, NHINDAddressCollection recipients, NHINDAddress sender)
	{
		MessageProcessResult retVal = null;
		
		verifyInitialized();
		
		preProcessMessage(message, sender);

		try
		{
			DefaultMessageEnvelope envelopeToProcess = new DefaultMessageEnvelope(new Message(message), recipients, sender);			
			envelopeToProcess.setAgent(agent);
			
			// should always result in either a non null object or an exception
			retVal = processEnvelope(envelopeToProcess);
			
			if (retVal.getProcessedMessage() != null)
				postProcessMessage(retVal.getProcessedMessage());						
		}
		catch (SmtpAgentException e)
		{
			// rethrow
			throw e;
		}
		catch (Exception e)
		{
			throw new SmtpAgentException(SmtpAgentError.Unknown, e);
		}
		
		return retVal;
	}
	
	private void verifyInitialized()
	{
		if (agent == null)
			throw new SmtpAgentException(SmtpAgentError.Uninitialized, "SmtpAgent not fully initialized: Security and Trust agent is null");
	}
	
	private void preProcessMessage(MimeMessage message, NHINDAddress sender)
	{
		logStatus("Message Recieved from: " + sender.getAddress());
		copyMessage(message, settings.getRawMessageSettings());		
	}
	
    protected MessageProcessResult processEnvelope(MessageEnvelope envelope)
    {
    	MessageProcessResult retVal = null;
    	MessageEnvelope processedMessage = null;
    	MessageEnvelope bouceMessage = null;
    	boolean isOutgoing = envelope.getSender().isInDomain(agent.getDomains());
    	
    	try
    	{    		    		
    		processedMessage = (isOutgoing) ? agent.processOutgoing(envelope) : agent.processIncoming(envelope);
    		if  (processedMessage == null)
    			throw new SmtpAgentException(SmtpAgentError.InvalidEnvelopeFromAgent);
    		
    		if (processedMessage.hasRejectedRecipients())
    			bouceMessage = generateBounces(processedMessage);
    		
    		
    		// this looks really ugly!
    		retVal = new MessageProcessResult(processedMessage, (processedMessage != null && processedMessage instanceof OutgoingMessage) ? bouceMessage: null,
    				(processedMessage != null && processedMessage instanceof IncomingMessage) ? bouceMessage: null);
    	}
    	catch (AgentException e)
    	{
    		if (e.getError() != AgentError.NoTrustedRecipients)
    			throw e;
    		
    		bouceMessage = generateBounces(envelope, isOutgoing);
    		
    		retVal = new MessageProcessResult(null, (isOutgoing) ? bouceMessage: null,
    				(!isOutgoing) ? bouceMessage: null);    		    		    				
    	}
    	
    	return retVal;
    }
	
    private MessageEnvelope generateBounces(MessageEnvelope envelope)
    {
        return generateBounces(envelope, (envelope instanceof OutgoingMessage));
    }    

    private MessageEnvelope generateBounces(MessageEnvelope envelope, boolean isOutgoing)
    {
    	MessageEnvelope retVal = null;
    	
    	envelope.ensureRecipientsCategorizedByDomain(agent.getDomains());
    	
    	retVal = isOutgoing ? generateBouncesForOutgoing(envelope) : generateBouncesForIncoming(envelope);    	
    	
    	return retVal;
    }    

    

    private MessageEnvelope generateBouncesForOutgoing(MessageEnvelope envelope)
    {
    	MessageEnvelope retVal = null;
    	
        if (outgoingBounceFactory == null)
        {
            return null;
        }

        try
        {                
        	DomainPostmaster postmaster = settings.getDomainPostmasters().get(envelope.getSender().getHost().toUpperCase(Locale.getDefault()));
        	if (postmaster != null)
        	{	        	
	            MimeMessage bounceMessage = outgoingBounceFactory.create(envelope, postmaster.getPostmaster());
	            if (bounceMessage != null)
	            {
	                this.logStatus("Bounced Outgoing");
	                retVal = new DefaultMessageEnvelope(new Message(bounceMessage));              
	            }
	            
	            if (outgoingBounceFactory.getMessageTemplate().isEncryptionRequired())
	            	retVal = this.processBounceMessage(retVal);
        	}
        }
        catch(Exception ex)
        {
        	/*
        	 * TODO: Add exception handling
        	 */
        }
        
        return retVal;
    }

    private MessageEnvelope generateBouncesForIncoming(MessageEnvelope envelope)
    {
    	MessageEnvelope retVal = null;
    	
        if (incomingBounceFactory == null)
        {
            return null;
        }
    
        try
        {                
            if (!envelope.hasDomainRecipients())
            {
                return null;
            }
            
            NHINDAddress firstDomainRecipient = envelope.getDomainRecipients().get(0);
            MimeMessage bounceMessage = incomingBounceFactory.create(envelope,
            		settings.getDomainPostmasters().get(firstDomainRecipient.getHost().toUpperCase(Locale.getDefault())).getPostmaster()); 
            		
            if (bounceMessage != null)
            {
                this.logStatus("Bounced Incoming");
                retVal = new DefaultMessageEnvelope(new Message(bounceMessage)); 
            }
            
            if (incomingBounceFactory.getMessageTemplate().isEncryptionRequired())
            	retVal = this.processBounceMessage(retVal);            
        }
        catch(Exception ex)
        {
        	/*
        	 * TODO: Add exception handling
        	 */
        }
        
        return retVal;
    }    
    
    private void postProcessMessage(MessageEnvelope envelope)
    {    	
        boolean isOutgoing = (envelope instanceof OutgoingMessage);

        this.copyMessage(envelope.getMessage(), 
            		(isOutgoing) ? settings.getOutgoingMessageSettings() : settings.getIncomingMessageSettings());
    }    
    
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
	
	private MessageEnvelope processBounceMessage(MessageEnvelope bounceMessage)
	{
		MessageEnvelope retVal = null;
		
		try
		{
			retVal = agent.processIncoming(bounceMessage);
		}
		catch (Throwable t)
		{
			 return null;  // don't send a non-encrypted message or else we could get caught in bounce ping-pong
		}
		
		return retVal;
	}
	
	private String generateUniqueFileName()
	{
		return UUID.randomUUID().toString() + ".eml";
	}
}
