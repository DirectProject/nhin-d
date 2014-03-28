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
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * The socket server is an IP protocol agnostic server that manages threading/concurrency and message dispatching to the 
 * concrete socket implementation.  It utilizes a "smart" thread pool for efficiently managing processing threads. 
 * @author Greg Meyer
 * @since 1.0
 */
public abstract class DNSSocketServer implements DNSSocketServerMBean
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(DNSSocketServer.class);	
	
	protected final DNSServerSettings settings;
	protected final DNSResponder responder;
	
	protected ExecutorService socketAcceptService;
	protected ThreadPoolExecutor dnsRequestService;
	
	protected final AtomicBoolean running;  
	
	private long serverStartTime = Long.MAX_VALUE;
	private volatile long rejectedCount = 0;
	private volatile long requestCount = 0;
	private TemporalCountBucket countBuckets[] = {new TemporalCountBucket(), new TemporalCountBucket(), 
			new TemporalCountBucket(), new TemporalCountBucket(), new TemporalCountBucket()};
	
	
	/**
	 * Creates a socket server.  The server will not start accepting messages until the {@link #start()} method is called.
	 * @param settings  The server settings.  The settings contain specific IP and socket configuration parameters.
	 * @param responsder The DNS responder that will handle lookups.
	 * @throws DNSException
	 */
	public DNSSocketServer(DNSServerSettings settings, DNSResponder responsder) throws DNSException
	{
		running = new AtomicBoolean(false);
		
		this.settings = settings;		
		this.responder = responsder;
		
		// create the server socket
		createServerSocket();
	}
	
	protected void registerMBean(Class<?> clazz)
	{
		final StringBuilder objectNameBuilder = new StringBuilder(clazz.getPackage().getName());
		objectNameBuilder.append(":type=").append(clazz.getSimpleName());
		objectNameBuilder.append(",name=").append(UUID.randomUUID());
		
		try
		{			
			final StandardMBean mbean = new StandardMBean(this, DNSSocketServerMBean.class);
		
			final MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
			mbeanServer.registerMBean(mbean, new ObjectName(objectNameBuilder.toString()));
		}
		catch (JMException e)
		{
			LOGGER.error("Unable to register the DNSSocketServer MBean", e);
		}
	}
	
	/**
	 * Starts the socket server and initializes the dispatch threads.  After this method has been called, the server will start accepting
	 * DNS requests.
	 * @throws DNSException
	 */
	public void start() throws DNSException
	{
		if (running.get() != true)
		{
			// create the accept thread
			running.set(true);
			
			dnsRequestService = new ThreadPoolExecutor(0, settings.getMaxActiveRequests(), 
					120L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
			
			
			socketAcceptService = Executors.newSingleThreadExecutor();
			socketAcceptService.execute(getSocketAcceptTask());
			
			serverStartTime = System.currentTimeMillis();
		}
		else
			LOGGER.info("Start requested, but socket server is already running.");
	
	}
	
	/**
	 * Shuts down the socket server and terminates the server from accepting additional requests.  The server attempts to gracefully
	 * shutdown the processing threads and gives currently running processing threads a chance to finish.
	 * @throws DNSException
	 */
	public void stop() throws DNSException
	{
		running.set(false);
		
		socketAcceptService.shutdown();
		
		dnsRequestService.shutdown();

	}
	
	protected void waitForGracefulStop()
	{
		try
		{
			socketAcceptService.awaitTermination(10, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) {/* no op */}
		
		try
		{
			dnsRequestService.awaitTermination(10, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) {/* no op */}
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
		
		updateCountMetrics();
		if (dnsRequestService.getActiveCount() < settings.getMaxActiveRequests())
			dnsRequestService.execute(getDNSRequestTask(s));
		else
		{		
			
			++rejectedCount;
			// just close the socket... we're too busy to handle anything
			try
			{
				if (s instanceof Socket)
					((Socket)s).close();
			}
			catch (IOException e) {}
		}
	}
	
	private void updateCountMetrics()
	{
		++requestCount;
		long curTime = System.currentTimeMillis();
		int bucketIndex = (int)((curTime / 1000) % 5);
		
		synchronized (countBuckets)
		{
			TemporalCountBucket bucket = countBuckets[bucketIndex];
			bucket.increment(curTime);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getUptime() 
	{
		if (running.get() == false || serverStartTime == Long.MAX_VALUE)
			return -1L;
		else
			return (System.currentTimeMillis() - serverStartTime);
	}		
	
	/**
	 * {@inheritDoc}
	 */	
	@Override
	public Long getRejectedRequestCount() 
	{
		return rejectedCount;
	}	
	
	/**
	 * {@inheritDoc}
	 */		
	@Override
	public Long getResourceRequestCount() 
	{
		return requestCount;
	}	
	
	/**
	 * {@inheritDoc}
	 */		
	@Override
	public String getResourceRequestLoad() 
	{
		// this is an approximation over the last 5 seconds converted to requests per second
		long curTime = System.currentTimeMillis();
		int numTransactions = 0;
		synchronized (countBuckets)
		{			
			for (TemporalCountBucket bucket : countBuckets)
			{
				numTransactions += bucket.getCount(curTime);
			}
		}
		
		// can get a little better accuracy if we take into consideration that the last full second of the 5 second time range has not yet passed
		double div = 4.0 + ((curTime % 1000) / 1000);
		
		int aveTransLoad = (int)((double)(numTransactions) / (div));
		
		return aveTransLoad + "/sec";
	}
	
	/*
	 * class used to hold request count within a 1 second time range
	 */
	private static class TemporalCountBucket
	{
		private volatile long count = 0;
		private volatile long firstAddedTime = 0;
		
		/*
		 * increment the access count... if the access time is outside
		 * of the 5 second time range, then reset the counter 
		 */
		public synchronized void increment(long accessTime)
		{			
			if ((accessTime - firstAddedTime) > 5000)
			{
				// round down to nearest 1000
				firstAddedTime = accessTime - (accessTime % 1000);
				count = 1;
			}
			else
				++count;
		}

		/*
		 * get the access count... if the access time is outside of the
		 * 5 second range, then return 0
		 */
		public synchronized long getCount(long accessTime)
		{
			if (firstAddedTime == 0)
				return 0;
			
			return ((accessTime - firstAddedTime) > 5000) ? 0 : count;
		}		
	}
}
