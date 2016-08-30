package org.nhindirect.monitor.aggregator.repository;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetailType;
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
public class ConcurrentJPAAggregationRepository_addTest extends CamelSpringTestSupport 
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
	public void testAdd_emptyRepository_addExchangeWithTxBody_assertExchangeAdded()
	{
		final Tx tx = TestUtils.makeMessage(TxMessageType.IMF, "12345", "", "me@test.com", "you@test.com", "", "", "");
		final Exchange exchange = new DefaultExchange(context);
		exchange.getIn().setBody(tx);
		
		final ConcurrentJPAAggregationRepository repo = new ConcurrentJPAAggregationRepository(notifDao);
		
		repo.add(context, "12345", exchange);
		
		final Exchange ex = repo.get(context, "12345");
		assertNotNull(ex);
		final Tx retrievedTx = (Tx)ex.getIn().getBody();
		assertEquals("12345", retrievedTx.getDetail(TxDetailType.MSG_ID).getDetailValue());
		final Integer version = (Integer)ex.getProperty(ConcurrentJPAAggregationRepository.AGGREGATION_ENTITY_VERSON);
		assertEquals(1, version.intValue());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testAdd_existingExchange_updateBody_assertExchangeAdded()
	{
		final Tx tx = TestUtils.makeMessage(TxMessageType.IMF, "12345", "", "me@test.com", "you@test.com", "", "", "");
		final Exchange exchange = new DefaultExchange(context);
		exchange.getIn().setBody(tx);
		
		final ConcurrentJPAAggregationRepository repo = new ConcurrentJPAAggregationRepository(notifDao);
		
		repo.add(context, "12345", exchange);

		// now update it
		
		Exchange retrievedEx = repo.get(context, "12345");

		final Tx tx1 = TestUtils.makeMessage(TxMessageType.IMF, "12345", "", "me@test.com", "you@test.com", "", "", "");
		final Tx tx2 = TestUtils.makeMessage(TxMessageType.IMF, "67890", "", "me@test2.com", "you@test2.com", "", "", "");
		
		final Collection<Tx> txs = Arrays.asList(tx1, tx2);
		retrievedEx.getIn().setBody(txs);
		repo.add(context, "12345", retrievedEx);
		
		retrievedEx = repo.get(context, "12345");

		final Collection<Tx> retrievedTxs = (Collection<Tx>)retrievedEx.getIn().getBody();
		assertEquals(2, retrievedTxs.size());
		
		assertEquals("12345", retrievedTxs.iterator().next().getDetail(TxDetailType.MSG_ID).getDetailValue());
		final Integer version = (Integer)retrievedEx.getProperty(ConcurrentJPAAggregationRepository.AGGREGATION_ENTITY_VERSON);
		assertEquals(3, version.intValue());
	}
	
	@Test
	public void testAdd_existingExchange_invalidVersion_assertExchangeAdded()
	{
		final Tx tx = TestUtils.makeMessage(TxMessageType.IMF, "12345", "", "me@test.com", "you@test.com", "", "", "");
		final Exchange exchange = new DefaultExchange(context);
		exchange.getIn().setBody(tx);
		
		final ConcurrentJPAAggregationRepository repo = new ConcurrentJPAAggregationRepository();
		repo.setAggreationDAO(notifDao);
		
		repo.add(context, "12345", exchange);

		// now try to update it
		
		Exchange retrievedEx = repo.get(context, "12345");
		
		final Tx tx1 = TestUtils.makeMessage(TxMessageType.IMF, "12345", "", "me@test.com", "you@test.com", "", "", "");
		final Tx tx2 = TestUtils.makeMessage(TxMessageType.IMF, "67890", "", "me@test2.com", "you@test2.com", "", "", "");
		
		final Collection<Tx> txs = Arrays.asList(tx1, tx2);
		retrievedEx.getIn().setBody(txs);
		retrievedEx.setProperty(ConcurrentJPAAggregationRepository.AGGREGATION_ENTITY_VERSON, 35);
		
		boolean exceptionOccured = false;
		
		try
		{
			repo.add(context, "12345", retrievedEx);
		}
		catch (RuntimeException e)
		{
			exceptionOccured = true;
		}

		assertTrue(exceptionOccured);
		
		
		// make sure id didn't change
		final Exchange ex = repo.get(context, "12345");
		assertNotNull(ex);
		final Tx retrievedTx = (Tx)ex.getIn().getBody();
		assertEquals("12345", retrievedTx.getDetail(TxDetailType.MSG_ID).getDetailValue());
		final Integer version = (Integer)ex.getProperty(ConcurrentJPAAggregationRepository.AGGREGATION_ENTITY_VERSON);
		assertEquals(1, version.intValue());

	}
	
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() 
    {
    	return new ClassPathXmlApplicationContext("distributedAggregatorRoutes/mock-route.xml");
    }
}
