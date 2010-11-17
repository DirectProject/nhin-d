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

public class TCPServer extends DNSSocketServer 
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(TCPServer.class);
	
	private ServerSocket serverSocket;
	
	public TCPServer(DNSServerSettings settings, DNSResponder responder) throws DNSException
	{
		super(settings, responder);			
	}
	

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
	
	public void stop() throws DNSException
	{
		super.stop();
		
		try
		{
			serverSocket.close();
		}
		catch (IOException e) {/* no-op */}
	
	}
	
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
	
	@Override
	public Runnable getSocketAcceptTask()
	{
		return new AcceptTask();
	}
	
	@Override
	public Runnable getDNSRequestTask(Object s)
	{
		return new RequestTask((Socket)s);
	}
	
	public class AcceptTask implements Runnable
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
	
	public class RequestTask implements Runnable
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

				dataOut = new DataOutputStream(requestSocket.getOutputStream());
				byte[] writeBytes = response.toWire();
				dataOut.writeShort(writeBytes.length);
				dataOut.write(writeBytes);
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

}
