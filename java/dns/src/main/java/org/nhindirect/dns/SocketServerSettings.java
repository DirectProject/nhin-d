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
 * Tuning parameters for the DNS server socket connections.
 * 
 * @author Greg Meyer
 * @author Umesh Madan
 * @author Chris Lomonico
 */
public class SocketServerSettings 
{

    private static final int DEFAULT_MAX_CONNECTION_BACKLOG = 64;
    private static final int DEFAULT_MAX_ACTIVE_REQUESTS = 64;
    private static final int DEFAULT_MAX_OUTSTANDING_ACCEPTS = 16;
    private static final int DEFAULT_READ_BUFFER_SIZE = 1024;	
    private static final int DEFAULT_SEND_TIMEOUT = 5000;
    private static final int DEFAULT_RECEIVE_TIMEOUT = 10000;
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

	public int getMaxOutstandingAccepts() 
	{
		return maxOutstandingAccepts;
	}

	public void setMaxOutstandingAccepts(int maxOutstandingAccepts) 
	{
		this.maxOutstandingAccepts = maxOutstandingAccepts;
	}

	public int getMaxActiveRequests() 
	{
		return maxActiveRequests;
	}

	public void setMaxActiveRequests(int maxActiveRequests) 
	{
		this.maxActiveRequests = maxActiveRequests;
	}

	public int getMaxConnectionBacklog() 
	{
		return maxConnectionBacklog;
	}

	public void setMaxConnectionBacklog(int maxConnectionBacklog)
	{
		this.maxConnectionBacklog = maxConnectionBacklog;
	}

	public int getReadBufferSize() 
	{
		return readBufferSize;
	}

	public void setReadBufferSize(int readBufferSize) 
	{
		this.readBufferSize = readBufferSize;
	}

	public int getSendTimeout() 
	{
		return sendTimeout;
	}

	public void setSendTimeout(int sendTimeout) 
	{
		this.sendTimeout = sendTimeout;
	}

	public int getReceiveTimeout() 
	{
		return receiveTimeout;
	}

	public void setReceiveTimeout(int receiveTimeout)
	{
		this.receiveTimeout = receiveTimeout;
	}

	public int getSocketCloseTimeout()
	{
		return socketCloseTimeout;
	}

	public void setSocketCloseTimeout(int socketCloseTimeout)
	{
		this.socketCloseTimeout = socketCloseTimeout;
	}	
	
}
