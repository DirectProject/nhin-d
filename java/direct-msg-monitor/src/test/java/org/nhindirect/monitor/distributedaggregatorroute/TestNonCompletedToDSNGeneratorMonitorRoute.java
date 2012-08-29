package org.nhindirect.monitor.distributedaggregatorroute;

import org.nhindirect.monitor.dao.AggregationDAO;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class TestNonCompletedToDSNGeneratorMonitorRoute extends org.nhindirect.monitor.route.TestNonCompletedToDSNGeneratorMonitorRoute 
{	
	@Override
	public void postProcessTest() throws Exception
	{
		super.postProcessTest();
		
		final AggregationDAO dao = (AggregationDAO)context.getRegistry().lookup("aggregationDAO");
		dao.purgeAll();
		
		assertEquals(0,dao.getAggregationKeys().size());
		assertEquals(0,dao.getAggregationCompletedKeys().size());
	}
	
	
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() 
    {
    	return new ClassPathXmlApplicationContext("distributedAggregatorRoutes/monitor-route-to-error-message-generator.xml");
    }
}