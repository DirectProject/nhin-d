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

/**
 * MBean interface definition for monitoring and managing a DNS socket server.
 * @author Greg Meyer
 *
 * @since 1.1.0
 */
public interface DNSSocketServerMBean 
{
	/**
	 * Gets the number of DNS requests received by the server.
	 * @return The number of DNS requests received by the server.
	 */
	public Long getResourceRequestCount();
	
	/**
	 * Gets the request load of the server.  Load is returned in number of requests per second averaged over the last 5 seconds.
	 * @return The request load of the server.
	 */
	public String getResourceRequestLoad();
		
	/**
	 * Gets the time in milliseconds that the server has been running since its last start.
	 * @return The time in milliseconds that the server has been running since its last start.
	 */
	public Long getUptime();
	
	/**
	 * Gets the number of requests that returned without error.  NXDOMAIN statuses qualify as successful requests.
	 * @return The number of requests that returned without error.
	 */
	public Long getSuccessfulRequestCount();
	
	/**
	 * Gets the number of requests that resulted in an error.
	 * @return The number of requests that resulted in an error.
	 */
	public Long getErrorRequestCount();	
	
	/**
	 * Gets the number of requests that returned no records without error.
	 * @return The number of request that returned no records without error.
	 */
	public Long getMissedRequestCount();
	
	/**
	 * Gets the number of requests that were rejected by the server due to being to busy.  A high number of rejected requests indicates that the server
	 * should be reconfigured to accept a higher load.
	 * @return The number of requests that were rejected by the server due to being to busy.
	 */
	public Long getRejectedRequestCount();
}
