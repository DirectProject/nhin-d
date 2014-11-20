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

package org.nhindirect.monitor.processor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

import javax.mail.internet.MimeMessage;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.smtp.SMTPClient;
import org.nhindirect.common.mail.MailStandard;

/**
 * Camel processor for sending DSN messages via an SMTP gateway.  Messages in the incoming exchange are expected to be of type
 * MimeMessage
 * @author Greg Meyer
 * @since 1.0
 */
public class DSNMailSender 
{
	protected static final int SMTP_OK = 250;
	
	protected SMTPClientFactory clientFactory;
	
	protected String gatewayHost;
	protected int gatewayPort = 25;
	protected String localhost = "localhost";
	
	/**
	 * Empty constructor
	 */
	public DSNMailSender()
	{
		try
		{
			localhost = InetAddress.getLocalHost().getHostName();
		}
		catch (Exception e)
		{
			
		}
	}
	
	/**
	 * Constructor
	 * @param gatewayURL The URL of the SMTP gateway
	 * @param clientFactory Client factory for generating SMTPClient objects
	 */
	public DSNMailSender(String gatewayURL, SMTPClientFactory clientFactory)
	{
		this();
		setSMTPClientFactory(clientFactory);
		setGatewayURL(gatewayURL);
		
	}
	
	/**
	 * Sets the SMTP gateway factory
	 * @param clientFactory The factory object
	 */
	public void setSMTPClientFactory(SMTPClientFactory clientFactory)
	{
		this.clientFactory = clientFactory;
	}
	
	/**
	 * Sets the SMTP gateway URL (ex: smtp://mailserver.domain.com:25).  If the port is not specified, then
	 * 25 is assumed.
	 * @param gatewayURL The SMTP gateway URL
	 */
	public void setGatewayURL(String gatewayURL)
	{
		try
		{
			final URI gateway = new URI(gatewayURL);
			if (gateway.getPort() > 0)
				gatewayPort = gateway.getPort();
			
			gatewayHost = gateway.getHost();
		}
		catch (URISyntaxException e)
		{
			throw new IllegalArgumentException("Invalid gateway URL.", e);
		}
	}
	
	/**
	 * Processor handler method.  This method sends the message to SMTP gateway specified in the gateway URL.
	 * The SMTP to and from headers and taken from the actual message.
	 * @param exchange The exchange that holds the message.
	 * @throws Exception
	 */
    @Handler
    public void sendMail(Exchange exchange) throws Exception
    {
    	if (gatewayHost == null || gatewayHost.isEmpty())
    		throw new IllegalStateException("Gateway URL is null or empty");
    	
    	// simple SMTP converation
    	if (clientFactory == null)
    		throw new IllegalStateException("SMTP client cannot be null");
    	
    	if (exchange.getIn() == null || exchange.getIn().getBody() == null)
    		return;
    	
    	final MimeMessage dsnMessage = (MimeMessage)exchange.getIn().getBody();
    	final String recpList = dsnMessage.getHeader(MailStandard.Headers.To, ",");
    	final String sender = dsnMessage.getHeader(MailStandard.Headers.From, ",");
    	
    	SMTPClient client = clientFactory.createInstance();
    	client.connect(gatewayHost, gatewayPort);
    	client.helo(localhost);
    	
    	
    	if (!client.setSender(sender))
    		throw new IOException("Failed to set sender.");
    	
    	final String[] recips = recpList.split(",");
    	for (String recip : recips)
    	{
    		if (!client. addRecipient(recip))
    			throw new IOException("Failed to set recipient " + recip);	
    	}
    	
    	final Writer writer = client.sendMessageData();
    
    	if (writer == null)
    		throw new IOException("Failed to get data body writer.");	
    

    	final ByteArrayOutputStream writerStream = new ByteArrayOutputStream();
    	
    	try
    	{
	    	dsnMessage.writeTo(writerStream);
	
	    	IOUtils.write(writerStream.toByteArray(), writer);
	    
	    	writer.close();
	    	
	    	client.completePendingCommand();
	    		
	    	if (client.getReplyCode() != SMTP_OK)
	    		throw new IOException("Failed complete data command with error code " + client.getReplyCode());    		    	    	
    	}
    	finally
    	{
    		IOUtils.closeQuietly(writerStream);
    		IOUtils.closeQuietly(writer);

	    	client.quit();
	    	client.disconnect();
    	}
    }
}
