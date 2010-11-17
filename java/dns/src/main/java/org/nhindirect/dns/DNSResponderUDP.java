package org.nhindirect.dns;

public class DNSResponderUDP extends DNSResponder
{
	private final DNSSocketServer socketServer;
	
	public DNSResponderUDP(DNSServerSettings settings, DNSStore store) throws DNSException
	{
		super(settings, store);
		socketServer = new UDPServer(settings, this);
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
