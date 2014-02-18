/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
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

package org.nhindirect.common.rest;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * Factory class for creating instances of HttpClients.  Clients created by this factory
 * are capable of being utilizes from multiple concurrent threads.
 * @author Greg Meyer
 * @since 1.1
 */
public class HttpClientFactory 
{
	public static final int DEFAULT_CON_TIMEOUT = 20000;
	public static final int DEFAULT_SO_TIMEOUT = 20000;
	
	protected static final ThreadSafeClientConnManager conMgr = new ThreadSafeClientConnManager();
	
	/**
	 * Creates an HttpClient with the default connection timeout and SO timeout.
	 * @return The HTTP client.
	 */
	public static HttpClient createHttpClient()
	{
		 final HttpClient client = new DefaultHttpClient(conMgr);
		 final HttpParams httpParams = client.getParams();
		 HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_CON_TIMEOUT);
		 HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_SO_TIMEOUT);
		 
		 return client;
	}
	
	/**
	 * Creates an HttpClient with a specific connection timeout and SO timeout.
	 * @return The HTTP client.
	 */
	public static HttpClient createHttpClient(int conTimeOut, int soTimeout)
	{
		 final HttpClient client = new DefaultHttpClient(conMgr);
		 final HttpParams httpParams = client.getParams();
		 HttpConnectionParams.setConnectionTimeout(httpParams, conTimeOut);
		 HttpConnectionParams.setSoTimeout(httpParams, soTimeout);
		 
		 return client;
	}
	
	/**
	 * Shuts down and clean up resource associated with all HTTP client created by this factory.
	 */
	///CLOVER:OFF
	public static void shutdownClients()
	{
		conMgr.shutdown();
	}
	///CLOVER:ON
}
