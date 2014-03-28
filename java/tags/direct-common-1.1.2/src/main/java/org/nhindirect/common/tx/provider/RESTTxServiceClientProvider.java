/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer    gmeyer@cerner.com
 
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

package org.nhindirect.common.tx.provider;

import org.apache.http.client.HttpClient;
import org.nhindirect.common.rest.HttpClientFactory;
import org.nhindirect.common.tx.TxService;
import org.nhindirect.common.tx.impl.DefaultTxDetailParser;
import org.nhindirect.common.tx.impl.RESTTxServiceClient;

import com.google.inject.Provider;

///CLOVER:OFF
public class RESTTxServiceClientProvider implements Provider<TxService> 
{
	protected final String serviceURL;
	protected final HttpClient client;
	
	public RESTTxServiceClientProvider(String serviceURL)
	{
		this(serviceURL, HttpClientFactory.createHttpClient());
	}
	
	@SuppressWarnings("static-access")
	public RESTTxServiceClientProvider(String serviceURL, HttpClientFactory factory)
	{
		this(serviceURL, factory.createHttpClient());
	}
	
	public RESTTxServiceClientProvider(String serviceURL, HttpClient client)
	{
		this.serviceURL = serviceURL;
		this.client = client;
	}
	
	@Override
	public TxService get()
	{
		return new RESTTxServiceClient(serviceURL, client, new DefaultTxDetailParser());
	}
}
///CLOVER:ON
