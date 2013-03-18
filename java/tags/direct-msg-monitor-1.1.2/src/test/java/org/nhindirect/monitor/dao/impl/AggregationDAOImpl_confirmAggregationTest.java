package org.nhindirect.monitor.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nhindirect.monitor.dao.AggregationDAO;
import org.nhindirect.monitor.dao.AggregationDAOException;
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
public class AggregationDAOImpl_confirmAggregationTest 
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
	public void testConfirmAggregationTest_aggregationRemoved() throws Exception
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
		
		assertNotNull(notifDao.getAggregationCompleted("12345", true));

		notifDao.confirmAggregation("12345");
		
		assertNull(notifDao.getAggregationCompleted("12345", true));
	}
	
	@Test
	public void testConfirmAggregationTest_aggregationNotFound_nothingRemoved() throws Exception
	{
		EntityManager mgr = mock(EntityManager.class);
		when(mgr.find((Class<?>)any(), any())).thenReturn(null);
		
		final AggregationDAOImpl dao = new AggregationDAOImpl();
		dao.setEntityManager(mgr);
		
		dao.confirmAggregation("12345");
		verify(mgr, times(1)).find((Class<?>)any(), any());
		verify(mgr, never()).remove(any());
	}
	
	@Test
	public void testConfirmAggregationTest_exceptionInRemove_assertException() throws Exception
	{
		EntityManager mgr = mock(EntityManager.class);
		doThrow(new RuntimeException()).when(mgr).find((Class<?>)any(), any());
		
		
		final AggregationDAOImpl dao = new AggregationDAOImpl();
		dao.setEntityManager(mgr);
		
		boolean exceptionOccured = false;
		try
		{
			dao.confirmAggregation("12345");
		}
		catch(AggregationDAOException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
}
