package org.nhindirect.monitor.processor;

import org.apache.commons.net.smtp.SMTPClient;

///CLOVER:OFF
public class SMTPClientFactory 
{
	public SMTPClient createInstance()
	{
		return new SMTPClient();
	}
}
///CLOVER:ON
