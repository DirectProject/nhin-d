package org.nhindirect.monitor.processor;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DSNMailSender_constructorTest 
{
	@Test
	public void testContrust_defaultConstructor()
	{
		DSNMailSender sender = new DSNMailSender();
		
		assertNull(sender.gatewayHost);
		assertNull(sender.clientFactory);
		assertEquals(25, sender.gatewayPort);
	}
	
	@Test
	public void testContrust_parameterizedConstructor()
	{
		SMTPClientFactory factory = new SMTPClientFactory();
		
		DSNMailSender sender = new DSNMailSender("smtp://localhost", factory);
		
		assertEquals("localhost", sender.gatewayHost);
		assertEquals(factory, sender.clientFactory);
		assertEquals(25, sender.gatewayPort);
	}	
	
	@Test
	public void testContrust_parameterizedConstructor_customPort()
	{
		SMTPClientFactory factory = new SMTPClientFactory();
		
		DSNMailSender sender = new DSNMailSender("smtp://localhost:10026", factory);
		
		assertEquals("localhost", sender.gatewayHost);
		assertEquals(factory, sender.clientFactory);
		assertEquals(10026, sender.gatewayPort);
	}
	
	@Test
	public void testContrust_parameterizedConstructor_invaludURL()
	{
		SMTPClientFactory factory = new SMTPClientFactory();
		
		boolean exceptionOccured = false;
		
		try
		{
			new DSNMailSender("smtpewdf://localhost\\:10026", factory);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
	}
}
