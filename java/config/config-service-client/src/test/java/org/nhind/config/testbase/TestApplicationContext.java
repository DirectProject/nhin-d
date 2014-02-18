package org.nhind.config.testbase;

import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class TestApplicationContext implements ApplicationContextAware
{
	  private static ApplicationContext CONTEXT;
	  
	  public void setApplicationContext(ApplicationContext context) throws BeansException 
	  {
	    CONTEXT = context;
	  }

	  public static ApplicationContext getApplicationContext()
	  {
		  return CONTEXT;
	  }
	  
	  @Test
	  public void testDummy()
	  {
		
	  }
}
