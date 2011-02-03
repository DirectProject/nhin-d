package org.nhindirect.dns;

import org.xbill.DNS.Message;

public class MockDNSStore implements DNSStore 
{
	public MockDNSStore()
	{
		
	}

	@Override
	public Message get(Message dnsMsg) throws DNSException
	{
		//return new Message();
		return null;
	}
	
	
}
