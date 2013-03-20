package org.nhindirect.monitor.aggregator.repository;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import org.nhindirect.monitor.util.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/test/resources/aggregationStore.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class ConcurrentJPAAggregationRepository_scanTest extends CamelSpringTestSupport 
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
	public void testScan_emptyRepository_assertEmptySet()
	{
		final ConcurrentJPAAggregationRepository repo = new ConcurrentJPAAggregationRepository(notifDao);
		
		final Set<String> ids = repo.scan(context);
		
		assertEquals(0, ids.size());
	}
	
	@Test
	public void testScan_singleEntryInRepository_assertSingleKey()
	{
		final Tx tx1 = TestUtils.makeMessage(TxMessageType.IMF, "12345", "", "me@test.com", "you@test.com", "", "", "");
		final Tx tx2 = TestUtils.makeMessage(TxMessageType.IMF, "67890", "", "me@test2.com", "you@test2.com", "", "", "");
		
		final Collection<Tx> txs = Arrays.asList(tx1, tx2);
		
		final Exchange exchange = new DefaultExchange(context);
		exchange.getIn().setBody(txs);
		
		
		final ConcurrentJPAAggregationRepository repo = new ConcurrentJPAAggregationRepository(notifDao);
		
		repo.add(context, "12345", exchange);
		
		repo.remove(context, "12345", exchange);
		
		final Set<String> ids = repo.scan(context);
		
		assertEquals(1, ids.size());
		assertEquals(exchange.getExchangeId(), ids.iterator().next());
	}
	
	@Test
	public void testScan_nullKeys_assertEmptySet() throws Exception
	{
		
		AggregationDAO dao = mock(AggregationDAO.class);
		when(dao.getAggregationCompletedKeys()).thenReturn(null);
		
		
		final ConcurrentJPAAggregationRepository repo = new ConcurrentJPAAggregationRepository(notifDao);
		
		final Set<String> ids = repo.scan(context);
		
		assertEquals(0, ids.size());
	}
	
	@Test
	public void testScan_daoException_assertException() throws Exception
	{
		AggregationDAO dao = mock(AggregationDAO.class);
		doThrow(new RuntimeException()).when(dao).getAggregationCompletedKeys();
		
		final ConcurrentJPAAggregationRepository repo = new ConcurrentJPAAggregationRepository(dao);
		
		boolean exceptionOccured = false;
		try
		{
			repo.scan(context);
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
