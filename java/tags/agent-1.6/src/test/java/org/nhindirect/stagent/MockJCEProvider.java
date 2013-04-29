package org.nhindirect.stagent;

import java.security.Provider;

public class MockJCEProvider extends Provider
{
	public MockJCEProvider()
	{
		super("JunitMockProvider", 1, "A mock junit security provider");
	}
	
	
}
