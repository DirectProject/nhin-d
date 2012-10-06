package org.nhindirect.gateway.smtp;

import junit.framework.TestCase;

public class NotificationSettingsTest extends TestCase 
{
	public void testDefaultConstructor()
	{
		NotificationSettings settings = new NotificationSettings();
		
		assertTrue(settings.isAutoResponse());
		assertEquals("Security Agent", settings.getProductName());
		assertEquals("", settings.getText());
		assertFalse(settings.hasText());
	}
	
	public void testConstructor_setAutoResponseTrue()
	{
		NotificationSettings settings = new NotificationSettings(true);
		
		assertTrue(settings.isAutoResponse());
		assertEquals("Security Agent", settings.getProductName());
		assertEquals("", settings.getText());
		assertFalse(settings.hasText());
	}	

	public void testConstructor_setAutoResponseFalse()
	{
		NotificationSettings settings = new NotificationSettings(false);
		
		assertFalse(settings.isAutoResponse());
		assertEquals("Security Agent", settings.getProductName());
		assertEquals("", settings.getText());
		assertFalse(settings.hasText());
	}	

	
	public void testConstructor_nullProductName()
	{
		NotificationSettings settings = new NotificationSettings(false, null, "");

		assertFalse(settings.isAutoResponse());
		assertEquals("Security Agent", settings.getProductName());
		assertEquals("", settings.getText());
		assertFalse(settings.hasText());
	}	
	
	public void testConstructor_emptyProductName()
	{
		NotificationSettings settings = new NotificationSettings(false, "", "");

		assertFalse(settings.isAutoResponse());
		assertEquals("Security Agent", settings.getProductName());
		assertEquals("", settings.getText());
		assertFalse(settings.hasText());
	}	
	
	public void testConstructor_setProductName()
	{
		NotificationSettings settings = new NotificationSettings(true, "Test Product", "");

		assertTrue(settings.isAutoResponse());
		assertEquals("Test Product", settings.getProductName());
		assertEquals("", settings.getText());
		assertFalse(settings.hasText());
	}	
	
	public void testConstructor_nullText()
	{
		NotificationSettings settings = new NotificationSettings(false, null, null);

		assertFalse(settings.isAutoResponse());
		assertEquals("Security Agent", settings.getProductName());
		assertEquals("", settings.getText());
		assertFalse(settings.hasText());
	}	
	
	public void testConstructor_emptyText()
	{
		NotificationSettings settings = new NotificationSettings(true, "Test Product", "");

		assertTrue(settings.isAutoResponse());
		assertEquals("Test Product", settings.getProductName());
		assertEquals("", settings.getText());
		assertFalse(settings.hasText());
	}		
	
	public void testConstructor_setText()
	{
		NotificationSettings settings = new NotificationSettings(true, "Test Product", "Test Text");

		assertTrue(settings.isAutoResponse());
		assertEquals("Test Product", settings.getProductName());
		assertEquals("Test Text", settings.getText());
		assertTrue(settings.hasText());
	}		
}
