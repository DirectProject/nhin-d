package org.nhindirect.common.audit.provider;

import org.junit.Test;
import org.nhindirect.common.audit.Auditor;
import org.nhindirect.common.audit.impl.LoggingAuditor;
import org.nhindirect.common.audit.impl.MultiProviderAuditor;
import org.nhindirect.common.audit.impl.NoOpAuditor;
import org.nhindirect.common.audit.provider.LoggingAuditorProvider;
import org.nhindirect.common.audit.provider.MultiProviderAuditorProvider;
import org.nhindirect.common.audit.provider.NoOpAuditorProvider;

import com.google.inject.Provider;


import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class MultiProviderAuditorProviderTest 
{
	@Test
	public void testCreateProviderFromAuditorArray()
	{
		MultiProviderAuditorProvider provider = new MultiProviderAuditorProvider(new Auditor[] {new NoOpAuditor(), new LoggingAuditor()});
		assertNotNull(provider);
		
		assertNotNull(provider.get());
		assertTrue(provider.get() instanceof MultiProviderAuditor);
	}
	
	@Test
	public void testCreateProvider_EmptyArray_AssertException()
	{
		boolean exceptionOccured = false;
				
		try
		{
			new MultiProviderAuditorProvider(new Auditor[] {});
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
	
	@Test
	public void testCreateProvider_NullArray_AssertException()
	{
		boolean exceptionOccured = false;
				
		try
		{
			new MultiProviderAuditorProvider((Auditor[])null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}		
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateProviderFromAuditorcollection()
	{
		Provider<Auditor>[] provs = new Provider[2];
		provs[0] = new NoOpAuditorProvider();
		provs[1] = new LoggingAuditorProvider();
		
		MultiProviderAuditorProvider provider = new MultiProviderAuditorProvider(provs);
		assertNotNull(provider);
		
		assertNotNull(provider.get());
		assertTrue(provider.get() instanceof MultiProviderAuditor);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateProvider_EmptyProviderArray_AssertException()
	{
		boolean exceptionOccured = false;
				
		try
		{
			new MultiProviderAuditorProvider(new Provider[] {});
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
	
	@Test
	public void testCreateProvider_NullProviderArray_AssertException()
	{
		boolean exceptionOccured = false;
				
		try
		{
			new MultiProviderAuditorProvider((Provider<Auditor>[])null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}		
}
