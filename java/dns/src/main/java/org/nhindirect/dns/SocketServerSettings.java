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
 * Tuning parameters for the DNS server socket connections.  Tuning parameters are important to maximize the performance
 * of the DNS server or to prevent the server machine from becoming over loaded.
 * 
 * @author Greg Meyer
 * @author Umesh Madan
 * @author Chris Lomonico
 * 
 * @since 1.0
 */
public class SocketServerSettings 
{

    private static final int DEFAULT_MAX_CONNECTION_BACKLOG = 64;
    private static final int DEFAULT_MAX_ACTIVE_REQUESTS = 64;
    private static final int DEFAULT_MAX_OUTSTANDING_ACCEPTS = 16;
    private static final int DEFAULT_READ_BUFFER_SIZE = 1024;	
    private static final int DEFAULT_SEND_TIMEOUT = 5000;
    private static final int DEFAULT_RECEIVE_TIMEOUT = 50000;
    private static final int DEFAULT_SOCKET_CLOSE_TIMEOUT = 5000;
    
	private int maxOutstandingAccepts;
	private int maxActiveRequests;
	private int maxConnectionBacklog;
	private int readBufferSize;
	private int sendTimeout;
	private int receiveTimeout;
	private int socketCloseTimeout;
	
	/**
	 * Creates a default set of socket parameters.
	 */
	public SocketServerSettings()
	{
		maxOutstandingAccepts = DEFAULT_MAX_OUTSTANDING_ACCEPTS;
		maxActiveRequests = DEFAULT_MAX_ACTIVE_REQUESTS;
		maxConnectionBacklog = DEFAULT_MAX_CONNECTION_BACKLOG;
		readBufferSize = DEFAULT_READ_BUFFER_SIZE;
		sendTimeout = DEFAULT_SEND_TIMEOUT;
		receiveTimeout = DEFAULT_RECEIVE_TIMEOUT;
		socketCloseTimeout = DEFAULT_SOCKET_CLOSE_TIMEOUT;
	}

	/**
	 * Gets the maximum number of requests that can be accepted by the server, but not yet
	 * committed to a processing thread.  Setting this value to high may result in DNS clients
	 * timing out due to outstanding requests waiting to long for a processing thread.  The
	 * default is 16 requests.
	 * @return The maximum number of requests that can be excepted by the server, but not yet
	 * committed to a processing thread.
	 */
	public int getMaxOutstandingAccepts() 
	{
		return maxOutstandingAccepts;
	}

	/**
	 * Sets the maximum number of requests that can be accepted by the server, but not yet
	 * committed to a processing thread.
	 * @param maxOutstandingAccepts The maximum number of requests that can be accepted by the server, but not yet
	 * committed to a processing thread.
	 */
	public void setMaxOutstandingAccepts(int maxOutstandingAccepts) 
	{
		this.maxOutstandingAccepts = maxOutstandingAccepts;
	}

	/**
	 * Gets the maximum number of concurrent requests that can be processed by the server at any give time.  Setting this
	 * value to high may result in overloading the system.  Setting this value to low can limit throughput.  The default
	 * 64.
	 * @return The maximum number of concurrent requests that can be processed by the server at any give time.
	 */
	public int getMaxActiveRequests() 
	{
		return maxActiveRequests;
	}

	/**
	 * Sets the maximum number of concurrent requests that can be processed by the server at any give time.
	 * @param maxActiveRequests The maximum number of concurrent requests that can be processed by the server at any give time.
	 */
	public void setMaxActiveRequests(int maxActiveRequests) 
	{
		this.maxActiveRequests = maxActiveRequests;
	}

	/**
	 * Gets the maximum number of connections that are in the IP socket accept backlog.  Socket backlog is only relevant
	 * for TCP session based connections.  Setting this value to high can overload the IP stack or result in DNS
	 * client timeouts.  The default value is 64.
	 *   
	 * @return The maximum number of connections that are in the IP socket accept backlog.
	 */
	public int getMaxConnectionBacklog() 
	{
		return maxConnectionBacklog;
	}

	/**
	 * Sets the maximum number of connections that are in the IP socket accept backlog.
	 * @param maxConnectionBacklog The maximum number of connections that are in the IP socket accept backlog.
	 */
	public void setMaxConnectionBacklog(int maxConnectionBacklog)
	{
		this.maxConnectionBacklog = maxConnectionBacklog;
	}

	/**
	 * Gets the maximum size request buffer size in bytes.  The default value is 1024 bytes.
	 * @return The maximum size request buffer size in bytes.
	 */
	public int getReadBufferSize() 
	{
		return readBufferSize;
	}

	/**
	 * Sets the maximum size request buffer size in bytes.
	 * @param readBufferSize The maximum size request buffer size in bytes.
	 */
	public void setReadBufferSize(int readBufferSize) 
	{
		this.readBufferSize = readBufferSize;
	}

	/**
	 * Gets the socket timeout in milliseconds for sending responses.  Setting this value to high can
	 * result in performance degradation if multiple clients abandon their sessions.  Setting this value
	 * to low can result in clients not receiving responses in high latency environments.  The default value is 
	 * 5000 milliseconds.
	 * @return  The socket timeout in milliseconds for sending responses
	 */
	public int getSendTimeout() 
	{
		return sendTimeout;
	}

    /**
     * Sets the socket timeout in milliseconds for sending responses.
     * @param sendTimeout Sets the socket timeout in milliseconds for sending responses.
     */
	public void setSendTimeout(int sendTimeout) 
	{
		this.sendTimeout = sendTimeout;
	}

	/**
	 * Gets the socket timeout in milliseconds for receiving or reading request.  Setting this value to high can
	 * result in performance degradation if multiple clients abandon their sessions.  Setting this value
	 * to low can result in the server not fully reading request data in high latency environments.  The default value is 
	 * 5000 milliseconds.
	 * @return  The socket timeout in milliseconds for receiving or reading requests.
	 */
	public int getReceiveTimeout() 
	{
		return receiveTimeout;
	}

	/**
	 * Sets the socket timeout in milliseconds for receiving or reading request.
	 * @param receiveTimeout The socket timeout in milliseconds for receiving or reading request.
	 */
	public void setReceiveTimeout(int receiveTimeout)
	{
		this.receiveTimeout = receiveTimeout;
	}

	/**
	 * Gets the timeout in milliseconds for closing a socket connection.  The default value is 5000 milliseconds.
	 * @return The timeout in milliseconds for closing a socket connection.
	 */
	public int getSocketCloseTimeout()
	{
		return socketCloseTimeout;
	}

	/**
	 * Sets the timeout in milliseconds for closing a socket connection.  The default value is 5000 milliseconds.
	 * @param socketCloseTimeout The timeout in milliseconds for closing a socket connection.
	 */
	public void setSocketCloseTimeout(int socketCloseTimeout)
	{
		this.socketCloseTimeout = socketCloseTimeout;
	}	
	
}
