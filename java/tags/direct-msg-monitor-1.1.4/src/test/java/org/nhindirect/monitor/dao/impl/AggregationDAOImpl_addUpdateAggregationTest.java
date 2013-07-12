package org.nhindirect.monitor.dao.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nhindirect.monitor.dao.AggregationDAO;
import org.nhindirect.monitor.dao.AggregationDAOException;
import org.nhindirect.monitor.dao.AggregationVersionException;
import org.nhindirect.monitor.dao.entity.Aggregation;
import org.nhindirect.monitor.dao.impl.AggregationDAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/test/resources/aggregationStore.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class AggregationDAOImpl_addUpdateAggregationTest 
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
	public void testAddUpdateAggregationTest_emptyRepository_assertAggregationAdded() throws Exception
	{
		final Aggregation insert = new Aggregation();
		insert.setExchangeBlob(new byte[] {0,3,2});
		insert.setId("12345");
		insert.setVersion(0);
		
		notifDao.addUpdateAggregation(insert);
		
		final Aggregation aggr = notifDao.getAggregation("12345");
		assertNotNull(aggr);
		assertEquals(insert, aggr);
	}
	
	@Test
	public void testAddUpdateAggregationTest_updateExisting_assertAggregationUpdated() throws Exception
	{
		final Aggregation insert = new Aggregation();
		insert.setExchangeBlob(new byte[] {0,3,2});
		insert.setId("12345");
		insert.setVersion(0);
		
		notifDao.addUpdateAggregation(insert);
		
		
		final Aggregation insert2 = new Aggregation();
		insert2.setExchangeBlob(new byte[] {0,3,2,4,1,32});
		insert2.setVersion(1);
		insert2.setId("12345");
		
		notifDao.addUpdateAggregation(insert2);
		
		final Aggregation aggr = notifDao.getAggregation("12345");
		assertNotNull(aggr);
		assertEquals(insert, aggr);
	}
	
	@Test
	public void testAddUpdateAggregationTest_emptyRepository_addNon0Version_assertException() throws Exception
	{
		final Aggregation insert = new Aggregation();
		insert.setExchangeBlob(new byte[] {0,3,2});
		insert.setId("12345");
		insert.setVersion(1);
		
		boolean exceptionOccured = false;
		
		try
		{
			notifDao.addUpdateAggregation(insert);
		}
		catch (AggregationDAOException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
		final Aggregation aggr = notifDao.getAggregation("12345");
		assertNull(aggr);
	}
	
	@Test
	public void testAddUpdateAggregationTest_updateIncorrectVersion_assertExceptionAndRollback() throws Exception
	{
		final Aggregation insert = new Aggregation();
		insert.setExchangeBlob(new byte[] {0,3,2});
		insert.setId("12345");
		insert.setVersion(0);
		
		notifDao.addUpdateAggregation(insert);
		
		final Aggregation insert2 = new Aggregation();
		insert2.setExchangeBlob(new byte[] {0,3,2,4,1,32});
		insert2.setVersion(3);
		insert2.setId("12345");
		
		boolean exceptionOccured = false;
		try
		{
			notifDao.addUpdateAggregation(insert2);
		}
		catch (AggregationDAOException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
		
		final Aggregation aggr = notifDao.getAggregation("12345");
		assertNotNull(aggr);
		assertEquals(insert, aggr);
	}
	
	@Test
	public void testAddUpdateAggregationTest_entityManagerException_assertNoAggregation() throws Exception
	{
		EntityManager mgr = mock(EntityManager.class);
		doThrow(new RuntimeException()).when(mgr).persist(any());
		
		
		final AggregationDAOImpl dao = new AggregationDAOImpl();
		dao.setEntityManager(mgr);
		
		boolean exceptionOccured = false;
		try
		{
			final Aggregation insert = new Aggregation();
			insert.setExchangeBlob(new byte[] {0,3,2});
			insert.setId("12345");
			insert.setVersion(0);
			
			dao.addUpdateAggregation(insert);
		}
		catch(AggregationDAOException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}	
	
	@Test
	public void testAddUpdateAggregationTest_optomisticLockException_assertAggregationVersionException() throws Exception
	{
		EntityManager mgr = mock(EntityManager.class);
		doThrow(new OptimisticLockException()).when(mgr).persist(any());
		
		
		final AggregationDAOImpl dao = new AggregationDAOImpl();
		dao.setEntityManager(mgr);
		
		boolean exceptionOccured = false;
		try
		{
			final Aggregation insert = new Aggregation();
			insert.setExchangeBlob(new byte[] {0,3,2});
			insert.setId("12345");
			insert.setVersion(0);
			
			dao.addUpdateAggregation(insert);
		}
		catch(AggregationVersionException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
	}
}
