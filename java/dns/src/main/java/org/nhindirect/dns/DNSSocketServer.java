/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Chris Lomonico  chris.lomonico@surescripts.com
    Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.dns;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * The socket server is an IP protocol agnostic server that manages threading/concurrency and message dispatching to the 
 * concrete socket implementation.  It utilizes a "smart" thread pool for efficiently managing processing threads. 
 * @author Greg Meyer
 * @since 1.0
 */
public abstract class DNSSocketServer 
{
	protected final DNSServerSettings settings;
	protected final DNSResponder responder;
	
	protected ExecutorService socketAcceptService;
	protected ThreadPoolExecutor dnsRequestService;
	
	protected boolean running = false;  
	
	/**
	 * Creates a socket server.  The server will not start accepting messages until the {@link #start()} method is called.
	 * @param settings  The server settings.  The settings contain specific IP and socket configuration parameters.
	 * @param responsder The DNS responder that will handle lookups.
	 * @throws DNSException
	 */
	public DNSSocketServer(DNSServerSettings settings, DNSResponder responsder) throws DNSException
	{
		this.settings = settings;		
		this.responder = responsder;
		
		// create the server socket
		createServerSocket();
	}
	
	/**
	 * Starts the socket server and initializes the dispatch threads.  After this method has been called, the server will start accepting
	 * DNS requests.
	 * @throws DNSException
	 */
	public void start() throws DNSException
	{
		// create the accept thread
		running = true;
		
		dnsRequestService = new ThreadPoolExecutor(0, settings.getMaxActiveRequests(), 
				120L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
		
		
		socketAcceptService = Executors.newSingleThreadExecutor();
		socketAcceptService.execute(getSocketAcceptTask());
		
	}
	
	/**
	 * Shuts down the socket server and terminates the server from accepting additional requests.  The server attempts to gracefully
	 * shutdown the processing threads and gives currently running processing threads a chance to finish.
	 * @throws DNSException
	 */
	public void stop() throws DNSException
	{
		running = false;
		
		socketAcceptService.shutdown();
		dnsRequestService.shutdown();
	}
	
	/**
	 * Creates and initializes the socket implementation that will accept incoming requests.
	 * @throws DNSException
	 */
	public abstract void createServerSocket() throws DNSException;		
	
	/**
	 * Gets the Runnable task that will be responsible for accepting connections.  This task
	 * is placed in a single thread, so it should loop until the running flag is set to false.
	 * @return The Runnable task that will be responsible for accepting connections
	 */
	public abstract Runnable getSocketAcceptTask();
	
	/**
	 * Gets the Runnable task that will process a DNS request.  Each accepted request will create a new instance
	 * of the Runnable task and run it in its own thread.
	 * @param s An arbitrary parameter passed to the Runnable task.  This parameter generally contain the DNS request information.  
	 * This may be the TCP socket from the accept() server socket call or a UDP datagram packet.
	 * @return The Runnable task that will process a DNS request
	 */
	public abstract Runnable getDNSRequestTask(Object s);
	
	/**
	 * Submits the DNS request to a runnable task.
	 * @param s An arbitrary parameter passed to the Runnable task.  This parameter generally contain the DNS request information.  
	 * This may be the TCP socket from the accept() server socket call or a UDP datagram packet.
	 */
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
