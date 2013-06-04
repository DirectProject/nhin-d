package org.nhindirect.monitor.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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
public class AggregationDAOImpl_getAggregationCompletedKeysTest 
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
	public void testGetAggregationKeys_emptyRepository_assertEmptyList() throws Exception
	{
		assertEquals(0, notifDao.getAggregationKeys().size());
	}
	
	@Test
	public void testGetAggregationCompletedKeys_nullQueryResult_assertEmptyList() throws Exception
	{
		Query query = mock(Query.class);
		when(query.getResultList()).thenReturn(null);
		
		EntityManager mgr = mock(EntityManager.class);
		when(mgr.createQuery((String)any())).thenReturn(query);
		
		final AggregationDAOImpl dao = new AggregationDAOImpl();
		dao.setEntityManager(mgr);
		
		assertEquals(0, dao.getAggregationCompletedKeys().size());
	}	
	
	@Test
	public void testGetAggregationCompletedKeys_emptyListResultSet_assertEmptyList() throws Exception
	{
		Query query = mock(Query.class);
		when(query.getResultList()).thenReturn(Collections.emptyList());
		
		EntityManager mgr = mock(EntityManager.class);
		when(mgr.createQuery((String)any())).thenReturn(query);
		
		final AggregationDAOImpl dao = new AggregationDAOImpl();
		dao.setEntityManager(mgr);
		
		assertEquals(0, dao.getAggregationCompletedKeys().size());
	}	
	
	@Test
	public void testGetAggregationCompletedKeys_singleEntryInRepository_assertKeysRetrieved() throws Exception
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
		
		List<String> keys = notifDao.getAggregationCompletedKeys();
		assertEquals(1, keys.size());
		assertEquals("12345", keys.iterator().next());
	}	
	
	@Test
	public void testGetAggregationCompletedKeys_entityManagerException_assertNoAggregation() throws Exception
	{
		EntityManager mgr = mock(EntityManager.class);
		doThrow(new RuntimeException()).when(mgr).createQuery((String)any());
		
		
		final AggregationDAOImpl dao = new AggregationDAOImpl();
		dao.setEntityManager(mgr);
		
		boolean exceptionOccured = false;
		try
		{
			dao.getAggregationCompletedKeys();
		}
		catch(AggregationDAOException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
}
