package org.nhindirect.gateway.smtp.james.mailet;

import java.util.Iterator;
import java.util.Map;

import org.apache.mailet.MailetConfig;
import org.apache.mailet.MailetContext;

public class MockMailetConfig implements MailetConfig 
{
	private final Map<String, String> initParameters;
	private final String mailetName;
	private final MockMailetContext context;
	
	public MockMailetConfig(Map<String, String> initParameters, String mailetName)
	{
		this.initParameters = initParameters;
		this.mailetName = mailetName;
		this.context = new MockMailetContext();
	}
	
	public String getInitParameter(String param) 
	{
		return initParameters.get(param);
	}

	public Iterator<String> getInitParameterNames() 
	{
		return initParameters.keySet().iterator();
	}

	public MailetContext getMailetContext() 
	{
		return context;
	}

	public String getMailetName() 
	{
		return mailetName;
	}

}
