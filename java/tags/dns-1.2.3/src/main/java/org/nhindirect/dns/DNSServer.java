/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Umesh Madan     umeshma@microsoft.com
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/


package org.nhindirect.dns;

import java.lang.management.ManagementFactory;
import java.util.UUID;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;

/**
 * The DNS server creates the UDP and TCP responders and manages their life cycles.  DNS queries are delegated
 * the responders which use the {@link DNSStore} to lookup entries.
 * <p>
 * To run a server, an instance of a server is created followed by calling the {@link #start()} method.
 * @author Greg Meyer
 * @since 1.0
 */
public class DNSServer implements DNSServerMBean
{	
	private static final Log LOGGER = LogFactory.getFactory().getInstance(DNSServer.class);	
	
	private DNSResponder tcpResponder;
	private DNSResponder updResponder;
	private CompositeData settingsData;
	private final String dnsStoreImplName;
	
	/**
	 * Create a new DNSServer
	 * @param store The storage medium of the DNS records.
	 * @param settings DNS server specific settings such as UDP/TCP ports, IP bindings, and thread tuning parameters.
	 */
	@Inject
	public DNSServer(DNSStore store, DNSServerSettings settings)
	{		
		try
		{
			tcpResponder = new DNSResponderTCP(settings, store);
		}
		catch (DNSException e)
		{
			LOGGER.error("Failed to create TCP responder: " + e.getLocalizedMessage(), e);
		}
		
		try
		{
			updResponder = new DNSResponderUDP(settings, store);
		}
		catch (DNSException e)
		{
			LOGGER.error("Failed to create UDP responder: " + e.getLocalizedMessage(), e);
		}

		dnsStoreImplName = store.getClass().getName();
		
		registerMBean(settings);
	}
	
	/**
	 * Register the MBean
	 */
	private void registerMBean(DNSServerSettings settings)
	{
		String[] itemNames = {"Port", "Bind Address", "Max Request Size", "Max Outstanding Accepts", "Max Active Accepts", "Max Connection Backlog", 
				"Read Buffer Size", "Send Timeout", "Receive Timeout", "Socket Close Timeout"};
		
		String[] itemDesc = {"Port", "Bind Address", "Max Request Size", "Max Outstanding Accepts", "Max Active Accepts", "Max Connection Backlog", 
				"Read Buffer Size", "Send Timeout", "Receive Timeout", "Socket Close Timeout"};
		
		OpenType<?>[] types = {SimpleType.INTEGER, SimpleType.STRING, SimpleType.INTEGER, SimpleType.INTEGER, SimpleType.INTEGER, SimpleType.INTEGER, SimpleType.INTEGER,
				SimpleType.INTEGER, SimpleType.INTEGER, SimpleType.INTEGER};
		
		Object[] settingsValues = {settings.getPort(), settings.getBindAddress(), settings.getMaxRequestSize(), settings.getMaxOutstandingAccepts(), 
				settings.getMaxActiveRequests(), settings.getMaxConnectionBacklog(), settings.getReadBufferSize(), settings.getSendTimeout(), 
				settings.getReceiveTimeout(), settings.getSocketCloseTimeout()};
		
		try
		{
			CompositeType settingsType = new CompositeType(DNSServerSettings.class.getSimpleName(), "DNS server settings.", itemNames, itemDesc, types);
			settingsData = new CompositeDataSupport(settingsType, itemNames, settingsValues);
		}
		catch (OpenDataException e)
		{
			LOGGER.error("Failed to create settings composite type: " + e.getLocalizedMessage(), e);
			return;
		}
		 		
		
		Class<?> clazz = this.getClass();
		final StringBuilder objectNameBuilder = new StringBuilder(clazz.getPackage().getName());
		objectNameBuilder.append(":type=").append(clazz.getSimpleName());
		objectNameBuilder.append(",name=").append(UUID.randomUUID());
				
		try
		{			
			final StandardMBean mbean = new StandardMBean(this, DNSServerMBean.class);
		
			final MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
			mbeanServer.registerMBean(mbean, new ObjectName(objectNameBuilder.toString()));
		}
		catch (JMException e)
		{
			LOGGER.error("Unable to register the DNSServer MBean", e);
		}
	}	
	
	/**
	 * Starts the DNS server by initializing and launching the TCP and UDP listeners.
	 * @throws DNSException Thrown if the internal listeners could not be started.
	 */
	public void start() throws DNSException
	{
		tcpResponder.start();
		updResponder.start();
	}
	
	/**
	 * Stops the server and shuts down the TCP and UPD listeners.
	 * @throws DNSException Thrown if the internal listeners could not be stopped.
	 */
	public void stop() throws DNSException
	{
		tcpResponder.stop();
		updResponder.stop();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompositeData getServerSettings() 
	{

		return settingsData;
	}

	/**
	 * {@inheritDoc}
	 */	
	@Override
	public void startServer() 
	{
		LOGGER.info("Received request to start server.");
		try
		{
			start();
		}
		catch (DNSException e)
		{
			LOGGER.error("Failed to start server: " + e.getMessage(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */	
	@Override
	public void stopServer() 
	{
		LOGGER.info("Received request to stop server.");
		try
		{
			stop();
		}
		catch (DNSException e)
		{
			LOGGER.error("Failed to stop server: " + e.getMessage(), e);
		}		
	}
	
	/**
	 * {@inheritDoc}
	 */	
	@Override
	public String getDNSStoreImplName()
	{
		return dnsStoreImplName;
	}
}
