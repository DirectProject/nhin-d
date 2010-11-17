package org.nhindirect.dns;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xbill.DNS.Message;

public class UDPServer extends DNSSocketServer  
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(UDPServer.class);
	
	private DatagramSocket serverSock;
	
	public UDPServer(DNSServerSettings settings, DNSResponder responder) throws DNSException
	{
		super(settings, responder);			
	}
	

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
	
	public void stop() throws DNSException
	{
		super.stop();

		serverSock.close();
	
	}
	
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
	
	@Override
	public Runnable getSocketAcceptTask()
	{
		return new ReceiveTask();
	}
	
	@Override
	public Runnable getDNSRequestTask(Object packet)
	{
		return new RequestTask((DatagramPacket)packet);
	}
	
	public class ReceiveTask implements Runnable
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

				byte[] writeBytes = response.toWire();
				outPacket = new DatagramPacket(writeBytes,
						writeBytes.length,
						inPacket.getAddress(),
						inPacket.getPort());
				
				serverSock.send(outPacket);				
			}
			catch (IOException e)
			{
				LOGGER.error("Wire/connection protocol error handing DNS request: " + e.getMessage(), e);
			}
		}
			
	}
}
