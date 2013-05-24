package org.nhindirect.monitor.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nhindirect.monitor.dao.AggregationDAO;
import org.nhindirect.monitor.dao.AggregationDAOException;
import org.nhindirect.monitor.dao.entity.Aggregation;
import org.nhindirect.monitor.dao.entity.AggregationCompleted;
import org.nhindirect.monitor.dao.impl.AggregationDAOImpl;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/test/resources/aggregationStoreWithDAODef.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class AggregationDAOImpl_getAggregationCompletedTest implements ApplicationContextAware
{
	private AggregationDAO notifDao;
	
	private ApplicationContext ctx;
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
	{
		ctx = applicationContext;
	}
	
	@Before
	public void setUp() throws Exception
	{
		notifDao = (AggregationDAO)ctx.getBean("aggregationDAO");
		
		notifDao.purgeAll();
		
		List<String> keys = notifDao.getAggregationKeys();
		assertEquals(0, keys.size());
		
		keys = notifDao.getAggregationCompletedKeys();
		assertEquals(0, keys.size());
		

	}
	
	@Test
	public void testGetAggregationCompleted_emptyRepository_assertNoAggregation() throws Exception
	{
		final AggregationCompleted aggr = notifDao.getAggregationCompleted("doenst matter", true);
		assertNull(aggr);
	}
	
	@Test
	public void testGetAggregationCompleted_nonEmptyRepository_keyDoesntExist_assertNoAggregation() throws Exception
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
		
		final AggregationCompleted aggr = notifDao.getAggregationCompleted("doenst matter", true);
		assertNull(aggr);
	}
	
	@Test
	public void testGetAggregationCompleted_nonEmptyRepository_keyExists_assertAggregationCompletedFound() throws Exception
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
		
		final AggregationCompleted aggr = notifDao.getAggregationCompleted("12345", true);
		assertNotNull(aggr);
		assertEquals("12345", aggr.getId());
		assertTrue(Arrays.equals(insert.getExchangeBlob(), aggr.getExchangeBlob()));
	}
	
	@Test
	public void testGetAggregationCompleted_nonEmptyRepository_exchangeLocked_assertAggregationCompletedNotFound() throws Exception
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
		
		final AggregationCompleted aggr = notifDao.getAggregationCompleted("12345", true);
		assertNotNull(aggr);
		assertEquals("12345", aggr.getId());
		assertTrue(Arrays.equals(insert.getExchangeBlob(), aggr.getExchangeBlob()));
		
		final AggregationCompleted lockedAggr = notifDao.getAggregationCompleted("12345", true);
		assertNull(lockedAggr);
	}
	
	
	@Test
	public void testGetAggregationCompleted_nonEmptyRepository_exchangeLockExpired_assertAggregationCompletedFound() throws Exception
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
		
		final AggregationCompleted aggr = notifDao.getAggregationCompleted("12345", true);
		assertNotNull(aggr);
		assertEquals("12345", aggr.getId());
		assertTrue(Arrays.equals(insert.getExchangeBlob(), aggr.getExchangeBlob()));
		
		Thread.sleep(4000);
		
		final AggregationCompleted lockedAggr = notifDao.getAggregationCompleted("12345", true);
		assertNotNull(lockedAggr);
		assertEquals("12345", lockedAggr.getId());
		assertTrue(Arrays.equals(insert.getExchangeBlob(), lockedAggr.getExchangeBlob()));
	}
	
	@Test
	public void testGetAggregationCompleted_optomisticLockException_assertAggregationCompletedNotFound() throws Exception
	{
		EntityManager mgr = mock(EntityManager.class);
		doThrow(new OptimisticLockException()).when(mgr).find((Class<?>)any(), any());
		
		
		final AggregationDAOImpl dao = new AggregationDAOImpl();
		dao.setEntityManager(mgr);
		
		final AggregationCompleted lockedAggr = dao.getAggregationCompleted("12345", true);
		assertNull(lockedAggr);
	}
	
	@Test
	public void testGetAggregationCompleted_entityManagerException_assertException() throws Exception
	{
		EntityManager mgr = mock(EntityManager.class);
		doThrow(new RuntimeException()).when(mgr).find((Class<?>)any(), any());
		
		
		final AggregationDAOImpl dao = new AggregationDAOImpl();
		dao.setEntityManager(mgr);
		
		boolean exceptionOccured = false;
		try
		{
			dao.getAggregationCompleted("12345", true);
		}
		catch(AggregationDAOException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}		
}
