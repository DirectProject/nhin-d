package org.nhindirect.monitor.aggregator.repository;

import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.nhindirect.monitor.aggregator.repository.ConcurrentJPAAggregationRepository;
import org.nhindirect.monitor.dao.AggregationDAO;


public class ConcurrentJPAAggregationRepository_doStartTest 
{
	@Test
	public void testDoStart_emptyAggregation_assertNoException() throws Exception
	{
		AggregationDAO dao = mock(AggregationDAO.class);
		when(dao.getAggregationKeys()).thenReturn(new ArrayList<String>());
		when(dao.getAggregationCompletedKeys()).thenReturn(new ArrayList<String>());
		
		final ConcurrentJPAAggregationRepository repo = new ConcurrentJPAAggregationRepository(dao);
		repo.doStart();
		repo.doStop();
	}
	
	@Test
	public void testDoStart_nonEmptyAggregation_assertNoException() throws Exception
	{
		AggregationDAO dao = mock(AggregationDAO.class);
		when(dao.getAggregationKeys()).thenReturn(Arrays.asList("12345"));
		when(dao.getAggregationCompletedKeys()).thenReturn(Arrays.asList("12345"));
		
		final ConcurrentJPAAggregationRepository repo = new ConcurrentJPAAggregationRepository(dao);
		repo.doStart();
		repo.doStop();
	}
	
	@Test
	public void testDoStart_emptyDAO_assertException() throws Exception
	{
		
		final ConcurrentJPAAggregationRepository repo = new ConcurrentJPAAggregationRepository();
		
		boolean exceptionOccured = false;
		try
		{
			repo.doStart();
		}
		catch(RuntimeException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
}
