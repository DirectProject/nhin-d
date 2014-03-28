package org.nhindirect.monitor.dao.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nhindirect.monitor.dao.AggregationDAO;
import org.nhindirect.monitor.dao.AggregationDAOException;
import org.nhindirect.monitor.dao.entity.Aggregation;
import org.nhindirect.monitor.dao.entity.AggregationCompleted;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/test/resources/aggregationStore.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class AggregationDAOImpl_removeAggregationTest 
{
	@Autowired
	private AggregationDAO notifDao;
	
	@Before
	public void setUp() throws Exception
	{
		notifDao.purgeAll();
		
		List<String> keys = notifDao.getAggregationKeys();
		assertEquals(0, keys.size());
		
		keys = notifDao.getAggregationCompletedKeys();
		assertEquals(0, keys.size());
	}
	
	@Test
	public void testRemoveAggregation_emptyRepository_assertExcpetion() throws Exception
	{
		final Aggregation remove = new Aggregation();
		remove.setExchangeBlob(new byte[] {0,3,2});
		remove.setId("12345");
		remove.setVersion(0);
		
		boolean exceptionOccured = false;
		try
		{
			notifDao.removeAggregation(remove, "12345");
		}
		catch (AggregationDAOException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	@Test
	public void testRemoveAggregation_removeAggregation_assertRemovedAndCompletedRepositry() throws Exception
	{
		final Aggregation insert = new Aggregation();
		insert.setExchangeBlob(new byte[] {0,3,2});
		insert.setId("12345");
		insert.setVersion(0);
		
		notifDao.addUpdateAggregation(insert);
		assertNotNull(notifDao.getAggregation("12345"));

		final Aggregation remove = new Aggregation();
		remove.setExchangeBlob(new byte[] {0,3,2});
		remove.setId("12345");
		remove.setVersion(1);
		
		notifDao.removeAggregation(remove, "12345");
		
		assertNull(notifDao.getAggregation("12345"));
		final AggregationCompleted completed = notifDao.getAggregationCompleted("12345", true);
		assertNotNull(completed);
		assertEquals("12345", completed.getId());
		assertEquals(3, completed.getVersion());
		assertTrue(Arrays.equals(remove.getExchangeBlob(), completed.getExchangeBlob()));
	}
	
	@Test
	public void testRemoveAggregation_removeAggregation_incorrectVersion_assertException() throws Exception
	{
		final Aggregation insert = new Aggregation();
		insert.setExchangeBlob(new byte[] {0,3,2});
		insert.setId("12345");
		insert.setVersion(0);
		
		notifDao.addUpdateAggregation(insert);
		assertNotNull(notifDao.getAggregation("12345"));

		final Aggregation remove = new Aggregation();
		remove.setExchangeBlob(new byte[] {0,3,2});
		remove.setId("12345");
		remove.setVersion(3);
		
		boolean exceptionOccured = false;
		try
		{
			notifDao.removeAggregation(remove, "12345");
		}
		catch (AggregationDAOException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
}
