package org.nhindirect.monitor.distributedaggregatorroute;

import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestMultiRecipNonReliableMessageMonitorRoute extends org.nhindirect.monitor.route.TestMultiRecipNonReliableMessageMonitorRoute 
{
	
	
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() 
    {
    	return new ClassPathXmlApplicationContext("distributedAggregatorRoutes/monitor-route-to-mock.xml");
    }
}