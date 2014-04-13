package org.nhindirect.monitor.aggregator.repository;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.monitor.aggregator.repository.ConcurrentJPAAggregationRepository;
import org.nhindirect.monitor.dao.AggregationDAO;
import org.nhindirect.monitor.util.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/test/resources/aggregationStore.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class ConcurrentJPAAggregationRepository_getKeysTest extends CamelSpringTestSupport 
{
	@Autowired
	private AggregationDAO notifDao;
	
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		
		notifDao.purgeAll();
		
		List<String> keys = notifDao.getAggregationKeys();
		assertEquals(0, keys.size());
		
		keys = notifDao.getAggregationCompletedKeys();
		assertEquals(0, keys.size());
	}
	
	@Test
	public void testGetKeys_emptyRepository_assertEmptyList()
	{
		final ConcurrentJPAAggregationRepository repo = new ConcurrentJPAAggregationRepository(notifDao);
		
		final Set<String> keys = repo.getKeys();
		
		assertEquals(0, keys.size());
	}
	
	@Test
	public void testGetKeys_singleEntry_assertSingleKey()
	{
		final ConcurrentJPAAggregationRepository repo = new ConcurrentJPAAggregationRepository(notifDao);
	
		final Tx tx = TestUtils.makeMessage(TxMessageType.IMF, "12345", "", "me@test.com", "you@test.com", "", "", "");
		final Exchange exchange = new DefaultExchange(context);
		exchange.getIn().setBody(tx);
		
		repo.add(context, "12345", exchange);
		
		final Set<String> keys = repo.getKeys();
		
		assertEquals(1, keys.size());
		assertEquals("12345", keys.iterator().next());
	}
	
	@Test
	public void testGetKeys_multipleEntries_assertMultipleKeys()
	{
		final ConcurrentJPAAggregationRepository repo = new ConcurrentJPAAggregationRepository(notifDao);
	
		final Tx tx1 = TestUtils.makeMessage(TxMessageType.IMF, "12345", "", "me@test.com", "you@test.com", "", "", "");
		final Exchange exchange1 = new DefaultExchange(context);
		exchange1.getIn().setBody(tx1);
		
		repo.add(context, "12345", exchange1);
		
		final Tx tx2 = TestUtils.makeMessage(TxMessageType.IMF, "123456", "", "me@test.com", "you@test.com", "", "", "");
		final Exchange exchange2 = new DefaultExchange(context);
		exchange2.getIn().setBody(tx2);
		
		repo.add(context, "123456", exchange2);
		
		final Set<String> keys = repo.getKeys();
		
		assertEquals(2, keys.size());
		Iterator<String> iter = keys.iterator();
		
		assertEquals("12345", iter.next());
		assertEquals("123456", iter.next());
	}
	
	@Test
	public void testGetKeys_daoException_assertException() throws Exception
	{
		AggregationDAO dao = mock(AggregationDAO.class);
		doThrow(new RuntimeException()).when(dao).getAggregationKeys();
		
		final ConcurrentJPAAggregationRepository repo = new ConcurrentJPAAggregationRepository(dao);
		
		boolean exceptionOccured = false;
		try
		{
			repo.getKeys();
		}
		catch(RuntimeException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
	
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() 
    {
    	return new ClassPathXmlApplicationContext("distributedAggregatorRoutes/mock-route.xml");
    }
}
