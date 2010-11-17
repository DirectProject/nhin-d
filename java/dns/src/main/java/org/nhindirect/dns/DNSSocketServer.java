package org.nhindirect.dns;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public abstract class DNSSocketServer 
{
	protected final DNSServerSettings settings;
	protected final DNSResponder responder;
	
	protected ExecutorService socketAcceptService;
	protected ThreadPoolExecutor dnsRequestService;
	
	protected boolean running = false;  
	
	
	public DNSSocketServer(DNSServerSettings settings, DNSResponder responsder) throws DNSException
	{
		this.settings = settings;		
		this.responder = responsder;
		
		// create the server socket
		createServerSocket();
		

	}
	
	public void start() throws DNSException
	{
		// create the accept thread
		running = true;
		
		dnsRequestService = new ThreadPoolExecutor(0, settings.getMaxActiveRequests(), 
				120L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
		
		
		socketAcceptService = Executors.newSingleThreadExecutor();
		socketAcceptService.execute(getSocketAcceptTask());
		
	}
	
	public void stop() throws DNSException
	{
		running = false;
		
		socketAcceptService.shutdown();
		dnsRequestService.shutdown();
	}
	
	public abstract void createServerSocket() throws DNSException;		
	
	public abstract Runnable getSocketAcceptTask();
	
	public abstract Runnable getDNSRequestTask(Object s);
	
	protected void submitDNSRequest(Object s)
	{
		if (dnsRequestService.getActiveCount() < settings.getMaxActiveRequests())
			dnsRequestService.execute(getDNSRequestTask(s));
		else
		{			
			// just close the socket... we're too busy to handle anything
			try
			{
				if (s instanceof Socket)
					((Socket)s).close();
			}
			catch (IOException e) {}
		}
	}
}
