/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/


package org.nhindirect.dns;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xbill.DNS.Message;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Section;

/**
 * UDP socket server that handled DNS requests over UDP.
 * @author Greg Meyer
 * @since 1.0
 */
public class UDPServer extends DNSSocketServer  
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(UDPServer.class);
	
	private static final int MAX_WIRE_SIZE = 512;
	
	
	private DatagramSocket serverSock;
	
	private volatile long missCount = 0;
	private volatile long errorCount = 0;
	private volatile long successCount = 0;	
	
	/**
	 * Creates a UDP server that listens to datagram packets.  The server will not start accepting messages until the {@link #start()} method is called.
	 * @param settings  The server settings.  The settings contain specific IP and socket configuration parameters.
	 * @param responsder The DNS responder that will handle lookups.
	 * @throws DNSException
	 */
	public UDPServer(DNSServerSettings settings, DNSResponder responder) throws DNSException
	{
		super(settings, responder);		
		
		registerMBean(this.getClass());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void start() throws DNSException
	{
		LOGGER.info("DNS UPD Server Starting");
		super.start();
		
		if (LOGGER.isInfoEnabled())
		{
			StringBuilder builder = new StringBuilder();
			builder.append("DNS UDP Server Startup Complete\r\n\tBind Address: ").append(settings.getBindAddress());
			builder.append("\r\n\tBind Port: ").append(settings.getPort());
			LOGGER.info(builder.toString());
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void stop() throws DNSException
	{
		super.stop();

		serverSock.close();
	
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createServerSocket() throws DNSException
	{
		
		try
		{
			serverSock = new DatagramSocket(settings.getPort(), Inet4Address.getByName(settings.getBindAddress()));
			serverSock.setReceiveBufferSize(settings.getMaxRequestSize());
			serverSock.setSoTimeout(settings.getReceiveTimeout());
		}
		catch (Exception e)
		{
			throw new DNSException(null, "Failed to create UDP server socket: " + e.getMessage(), e);
		}
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Runnable getSocketAcceptTask()
	{
		return new ReceiveTask();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Runnable getDNSRequestTask(Object packet)
	{
		return new RequestTask((DatagramPacket)packet);
	}
	
	/*
	 * Task that listens for datagram packets
	 */
	private class ReceiveTask implements Runnable
	{
		public void run()
		{
			
			while(running)
			{

					try
					{
						byte[] inBuffer = new byte[settings.getMaxRequestSize()];
						DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);

						serverSock.receive(inPacket);

						submitDNSRequest(inPacket);
					}
					catch (IOException e)
					{
						// udp has no state, so we can just call receive again
						// unless it was closed
						if (serverSock.isClosed() && running)
						{
							LOGGER.error("DNS UDP server socket dropped:" + e.getMessage());
							reconnect();
						}
					}
			}
		}
	}
	
	/*
	 * In the event that the server drops its connection, we need to open up a new
	 * datagram socket to listen for datagram packets.
	 */
	private void reconnect()
	{
		// socket may already be closed, but clean up to be thorough.
		serverSock.close();
		
		serverSock = null;
		while (serverSock == null && running)
		{	
			try
			{
				createServerSocket();
				LOGGER.error("DNS UDP server socket re-established");
			}
			catch (DNSException ex)
			{
				LOGGER.error("DNS UDP server socket failed to rebind.  Trying again in 5 seconds.");
				
				// the socket creation failed.... 
				// sleep 5 seconds and come back around and try again
				try
				{
					Thread.sleep(5000);
				}
				catch (InterruptedException iex) {/* no-op */}
			}
		}
	}
	
	/*
	 * Task that handles DNS requests.
	 */
	public class RequestTask implements Runnable
	{
		private DatagramPacket inPacket;
		
		public RequestTask(DatagramPacket inPacket)
		{
			this.inPacket = inPacket;
		}
		
		public void run()
		{
			Message query = null;
			Message response = null;
			DatagramPacket outPacket = null;
			
			try
			{
				
				try
				{
					query = responder.toMessage(inPacket.getData());
					
					response = responder.processRequest(query);
				}
				catch (DNSException e) 
				{
					if (query != null)
						response = responder.processError(query, e.getError());
				}

				if (response != null)
				{
					if (response.getRcode() == Rcode.NOERROR || response.getRcode() == Rcode.NXDOMAIN)
					{
						++successCount;
						if (response.getSectionArray(Section.ANSWER).length == 0)
							++missCount;	
					}
					else
						++errorCount;					
					
					byte[] writeBytes = response.toWire(MAX_WIRE_SIZE);
					outPacket = new DatagramPacket(writeBytes,
							writeBytes.length,
							inPacket.getAddress(),
							inPacket.getPort());
					
					serverSock.send(outPacket);
				}
				else
					++errorCount;				
			}
			catch (IOException e)
			{
				LOGGER.error("Wire/connection protocol error handing DNS request: " + e.getMessage(), e);
			}
		}
			
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getMissedRequestCount() 
	{
		return missCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getSuccessfulRequestCount() 
	{
		return successCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getErrorRequestCount()
	{
		return errorCount;
	}
}
