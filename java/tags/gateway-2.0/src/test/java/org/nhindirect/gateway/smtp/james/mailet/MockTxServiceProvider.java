package org.nhindirect.gateway.smtp.james.mailet;

import org.nhindirect.common.tx.TxService;

import com.google.inject.Provider;

public class MockTxServiceProvider implements Provider<TxService> 
{
	public MockTxServiceProvider()
	{
		
	}
	
	@Override
	public TxService get()
	{
		return new MockTxService();
	}
}
