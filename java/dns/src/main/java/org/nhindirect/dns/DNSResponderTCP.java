package org.nhindirect.dns;

public class DNSResponderTCP extends DNSResponder
{
	private final DNSSocketServer socketServer;
	
	public DNSResponderTCP(DNSServerSettings settings, DNSStore store) throws DNSException
	{
		super(settings, store);
		socketServer = new TCPServer(settings, this);
	}

	@Override
	public void start() throws DNSException 
	{
		socketServer.start();
	}

	@Override
	public void stop() throws DNSException 
	{
		socketServer.stop();
	}	
}
