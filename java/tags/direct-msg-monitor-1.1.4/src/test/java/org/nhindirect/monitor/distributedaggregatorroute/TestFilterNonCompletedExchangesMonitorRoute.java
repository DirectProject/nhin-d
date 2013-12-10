package org.nhindirect.monitor.distributedaggregatorroute;

import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class TestFilterNonCompletedExchangesMonitorRoute extends org.nhindirect.monitor.route.TestFilterNonCompletedExchangesMonitorRoute 
{

	
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() 
    {
    	return new ClassPathXmlApplicationContext("distributedAggregatorRoutes/monitor-route-to-mock-with-complete-filter.xml");
    }
}

