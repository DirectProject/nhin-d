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


/**
 * Tuning parameters for the DNS server.
 * 
 * @author Greg Meyer
 * @author Umesh Madan
 * @author Chris Lomonico
 *
 * @since 1.0
 */
public class DNSServerSettings extends SocketServerSettings
{
	private static final int DEFAULT_PORT = 53;
	private static final String DEFAULT_BIND_ADDRESS = "0.0.0.0"; // bind to all adapters
	public  static final int DAFAULT_MAX_REQUEST_SIZE = 1024 * 16;
	
	private int port;
	private String bindAddress;
	private int maxRequestSize;
	
	/**
	 * Create default DNS server settings
	 */
	public DNSServerSettings()
	{
		super();
		port = DEFAULT_PORT;
		bindAddress = DEFAULT_BIND_ADDRESS;
		maxRequestSize = DAFAULT_MAX_REQUEST_SIZE;
	}

	/**
	 * Gets the IP port that the server will be listening on.  The default is 53.
	 * @return The IP port that the server will be listening on.
	 */
	public int getPort() 
	{
		return port;
	}

	/**
	 * Sets the IP port that the server will be listening on.
	 * @param port The IP port that the server will be listening on.
	 * 
	 */
	public void setPort(int port) 
	{
		this.port = port;
	}

	/**
	 * Gets the IP4 addresses that the server will be bound to.  The string is comma delimited list of IP addresses.  The default is 0.0.0.0 
	 * which means that the server will bind to add IP addresses available on the local machine.  
	 * @return The IP4 addresses that the server will be bound to.
	 */
	public String getBindAddress() 
	{
		return bindAddress;
	}

	/**
	 * Sets the IP4 addresses that the server will be bound to.
	 * @param bindAddress The IP4 addresses that the server will be bound to.
	 */
	public void setBindAddress(String bindAddress) 
	{
		this.bindAddress = bindAddress;
	}	
	
	/**
	 * Gets the maximum size in bytes of a request.  The default size is 16K.
	 * @return The maximum size in bytes of a request.
	 */
	public int getMaxRequestSize()
	{
		return maxRequestSize;
	}

	/**
	 * Sets the maximum size in bytes of a request.
	 * @param maxRequestSize The maximum size in bytes of a request.
	 */
	public void setMaxRequestSize(int maxRequestSize)
	{
		this.maxRequestSize = maxRequestSize;
	}
}
