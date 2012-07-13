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

public class DSNMailSender 
{
	protected static final int SMTP_OK = 250;
	
	protected SMTPClientFactory clientFactory;
	
	protected String gatewayHost;
	protected int gatewayPort = 25;
	protected String localhost = "localhost";
	
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
	
	public DSNMailSender(String gatewayURL, SMTPClientFactory clientFactory)
	{
		this();
		setSMTPClientFactory(clientFactory);
		setGatewayURL(gatewayURL);
		
	}
	
	public void setSMTPClientFactory(SMTPClientFactory clientFactory)
	{
		this.clientFactory = clientFactory;
	}
	
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
	
    @Handler
    public void sendMail(Exchange exchange) throws Exception
    {
    	if (gatewayHost == null || gatewayHost.isEmpty())
    		throw new IllegalStateException("Gateway URL is null or empty");
    	
    	// simple SMTP converation
    	if (clientFactory == null)
    		throw new IllegalStateException("SMTP client cannot be null");
    	
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
