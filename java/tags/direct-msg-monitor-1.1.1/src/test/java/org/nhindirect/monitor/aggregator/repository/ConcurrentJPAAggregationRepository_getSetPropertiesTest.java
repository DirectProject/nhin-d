package org.nhindirect.monitor.aggregator.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.nhindirect.monitor.aggregator.repository.ConcurrentJPAAggregationRepository;

public class ConcurrentJPAAggregationRepository_getSetPropertiesTest 
{

	@Test
	public void testGetSetRecoveryInterval()
	{
		ConcurrentJPAAggregationRepository repo = new ConcurrentJPAAggregationRepository();
		
		assertEquals(5000, repo.getRecoveryIntervalInMillis());
		
		repo.setRecoveryInterval(10);
		assertEquals(10, repo.getRecoveryIntervalInMillis());
		
		repo.setRecoveryInterval(10, TimeUnit.SECONDS);
		assertEquals(10000, repo.getRecoveryIntervalInMillis());
		
	}
	
	
	@Test
	public void testGetSetUseRecover()
	{
		ConcurrentJPAAggregationRepository repo = new ConcurrentJPAAggregationRepository();
		
		repo.setUseRecovery(true);
		assertTrue(repo.isUseRecovery());
		
		repo.setUseRecovery(false);
		assertFalse(repo.isUseRecovery());
		
	}
	
	@Test
	public void testGetSetDeadLetterQueue()
	{
		ConcurrentJPAAggregationRepository repo = new ConcurrentJPAAggregationRepository();
		
		repo.setDeadLetterUri("http://cerner.com");
		assertEquals("http://cerner.com", repo.getDeadLetterUri());
		
	}
	
	@Test
	public void testGetSetMaxDeliveries()
	{
		ConcurrentJPAAggregationRepository repo = new ConcurrentJPAAggregationRepository();
		
		repo.setMaximumRedeliveries(10);
		assertEquals(10, repo.getMaximumRedeliveries());
		
		repo.setMaximumRedeliveries(100);
		assertEquals(100, repo.getMaximumRedeliveries());
		
	}
}
