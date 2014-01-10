package org.nhindirect.config;

import java.io.File;

import org.nhindirect.config.providers.ConfigJSONProvider;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public abstract class BaseTestPlan extends SpringBaseTest
{
	private static final Integer CONNECTION_TIMEOUT = 10000; // 10 seconds
	private static final Integer READ_TIMEOUT = 1000000; // 1000 seconds for debugging purposes
	
	static protected String filePrefix;
	
    static
    {

		// check for Windows... it doens't like file://<drive>... turns it into FTP
		File file = new File("./src/test/resources/bundles/signedbundle.p7b");
		if (file.getAbsolutePath().contains(":/"))
			filePrefix = "file:///";
		else
			filePrefix = "file:///";
    }
    
	
	public void perform() throws Exception 
	{
		try 
		{
			setUp();
			setupMocks();
			Exception exception = null;
			try 
			{
				performInner();
			} 
			catch (Exception e) 
			{
				exception = e;
			}
			assertException(exception);
		} 
		finally 
		{
			tearDownMocks();
		}
	}

	protected abstract void performInner() throws Exception;

	protected void setupMocks() {
	}

	protected void tearDownMocks() {
	}

	protected void assertException(Exception exception) throws Exception {
		// default case should not throw an exception
		if (exception != null) {
			throw exception;
		}
	}
	
	public static WebResource getResource(String serviceURL)
	{
		final ClientConfig config = new DefaultClientConfig();

		config.getSingletons().add(new ConfigJSONProvider());

		// need to set timeouts so we don't block forever in the event of a bad URL or hung web server
		config.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, CONNECTION_TIMEOUT);
		config.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, READ_TIMEOUT);
		
		
		final Client client = Client.create(config);
		WebResource resource = client.resource(serviceURL);
		
		return resource;

	}
	
}
