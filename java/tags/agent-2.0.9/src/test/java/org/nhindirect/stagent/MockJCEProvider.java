package org.nhindirect.stagent;

import java.security.Provider;

public class MockJCEProvider extends Provider
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5604718565294241448L;

	public MockJCEProvider()
	{
		super("JunitMockProvider", 1, "A mock junit security provider");
	}
	
	
}
