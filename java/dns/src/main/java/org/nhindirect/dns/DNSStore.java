package org.nhindirect.dns;

import org.xbill.DNS.Message;

public interface DNSStore 
{
	public Message get(Message dnsMsg) throws DNSException;
}
