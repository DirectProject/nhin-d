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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xbill.DNS.Message;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Section;

/**
 * TCP socket server that handled DNS requests over TCP.
 * @author Greg Meyer
 * @since 1.0
 */
public class TCPServer extends DNSSocketServer 
{

	private static final Log LOGGER = LogFactory.getFactory().getInstance(TCPServer.class);
	
	private ServerSocket serverSocket;
	
	private volatile long missCount = 0;
	private volatile long errorCount = 0;
	private volatile long successCount = 0;	
	
	/**
	 * Creates a TCP socket server.  The server will not start accepting messages until the {@link #start()} method is called.
	 * @param settings  The server settings.  The settings contain specific IP and socket configuration parameters.
	 * @param responsder The DNS responder that will handle lookups.
	 * @throws DNSException
	 */
	public TCPServer(DNSServerSettings settings, DNSResponder responder) throws DNSException
	{
		super(settings, responder);		
		
		registerMBean(this.getClass());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void start() throws DNSException
	{
		LOGGER.info("DNS TCP Server Starting");
		super.start();
		
		if (LOGGER.isInfoEnabled())
		{
			StringBuilder builder = new StringBuilder();
			builder.append("DNS TCP Server Startup Complete\r\n\tBind Address: ").append(settings.getBindAddress());
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
		
		try
		{
			serverSocket.close();
		}
		catch (IOException e) {/* no-op */}
	
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createServerSocket() throws DNSException
	{
		
		try
		{
			serverSocket = new ServerSocket(settings.getPort(), settings.getMaxConnectionBacklog(), 
					Inet4Address.getByName(settings.getBindAddress()));

		}
		catch (Exception e)
		{
			throw new DNSException(null, "Failed to create TCP server socket: " + e.getMessage(), e);
		}
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Runnable getSocketAcceptTask()
	{
		return new AcceptTask();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Runnable getDNSRequestTask(Object s)
	{
		return new RequestTask((Socket)s);
	}
	
	/*
	 * Thread that accepts socket connections
	 */
	private class AcceptTask implements Runnable
	{
		public void run()
		{
			while(running)
			{

					try
					{
						Socket s = serverSocket.accept();
						
						s.setReceiveBufferSize(settings.getMaxRequestSize());
						s.setSoTimeout(settings.getReceiveTimeout());
						submitDNSRequest(s);
					}
					catch (IOException e)
					{
						if (running)
						{
							LOGGER.error("DNS TCP server socket dropped:" + e.getMessage());
							reconnect();						
						}
					}
			}
		}
	}
	
	/*
	 * In the case that the server loses it connections, the accept socket needs to be re-established.
	 */
	private void reconnect()
	{
		// socket may already be closed, but clean up to be thorough.
		try
		{
			serverSocket.close();
		} catch (IOException e) {/* no-op */}
		
		serverSocket = null;
		while (serverSocket == null && running)
		{	
			try
			{
				createServerSocket();
				LOGGER.error("DNS TCP server socket re-established");
			}
			catch (DNSException ex)
			{
				LOGGER.error("DNS TCP server socket failed to rebind.  Trying again in 5 seconds.");
				
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
	 * Task that handles incoming tasks
	 */
	private class RequestTask implements Runnable
	{
		private Socket requestSocket;
		
		public RequestTask(Socket s)
		{
			requestSocket = s;
		}
		
		public void run()
		{
			Message response = null;
			Message query = null;
			int inLength;
			DataInputStream dataIn;
			DataOutputStream dataOut;
			byte [] in;
			
			try
			{
				InputStream is = requestSocket.getInputStream();
				dataIn = new DataInputStream(is);
				inLength = dataIn.readUnsignedShort();
				in = new byte[inLength];
				dataIn.readFully(in);
				
				try
				{
					query = responder.toMessage(in);
					
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
						
					dataOut = new DataOutputStream(requestSocket.getOutputStream());
					byte[] writeBytes = response.toWire();
					dataOut.writeShort(writeBytes.length);
					dataOut.write(writeBytes);
				}
				else
					++errorCount;
			}
			catch (IOException e)
			{
				LOGGER.error("Wire/connection protocol error handing DNS request: " + e.getMessage(), e);
			}
			finally
			{
				try
				{
					requestSocket.close();
				}
				catch (IOException e) {/* no-op */}
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
