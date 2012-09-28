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

package org.nhindirect.common.tx.impl;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.UnsecuredServiceRequestBase;
import org.nhindirect.common.tx.TxDetailParser;
import org.nhindirect.common.tx.TxUtil;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetail;


public class TrackMessageRequest extends UnsecuredServiceRequestBase<Object, RuntimeException>
{
	private final Tx tx;
	
	protected static MimeMessage convertHeadersToMessage(InternetHeaders headers)
	{
        if (headers == null) 
        {
            throw new IllegalArgumentException("Invalid parameter received. Headers cannot be null.");
        }
        
		// convert into a MimeMessage with only the headers
		final MimeMessage msg = new MimeMessage((Session)null);
		
		try
		{
			@SuppressWarnings("unchecked")
			final Enumeration<String> henum = headers.getAllHeaderLines();
			while (henum.hasMoreElements())
				msg.addHeaderLine(henum.nextElement());
			
			return msg;
		}
		catch (MessagingException e)
		{
			return null;
		}
	}
	
	protected static Tx convertMimeMessageToTx(MimeMessage msg, TxDetailParser parser)
	{
        if (msg == null) 
        {
            throw new IllegalArgumentException("Invalid parameter received. Message cannot be null.");
        }
		
        final Map<String, TxDetail> details = parser.getMessageDetails(msg);
        return new Tx(TxUtil.getMessageType(msg), details);
	}
	
    public TrackMessageRequest(HttpClient httpClient, String txServiceUrl, ObjectMapper jsonMapper, TxDetailParser parser, MimeMessage msg) 
    {
        this(httpClient, txServiceUrl, jsonMapper, parser, convertMimeMessageToTx(msg, parser));
    }
    
    public TrackMessageRequest(HttpClient httpClient, String txServiceUrl, ObjectMapper jsonMapper, TxDetailParser parser, InternetHeaders headers) 
    {
        this(httpClient, txServiceUrl, jsonMapper, parser, convertHeadersToMessage(headers));
    }
		
    public TrackMessageRequest(HttpClient httpClient, String txServiceUrl, ObjectMapper jsonMapper, TxDetailParser parser, Tx tx) 
    {
        super(httpClient, txServiceUrl, jsonMapper);
        
        if (tx == null) 
        {
            throw new IllegalArgumentException("Invalid parameter received. Tx cannot be null.");
        }
        
        this.tx = tx;
    }
    
    /*
     * Get the URI
     */
    private String getRequestUri() 
    {
    	String theURI = serviceUrl.endsWith("/") ? serviceUrl : serviceUrl + "/";
    	
        return theURI + "txs";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected final HttpPost createRequest() throws IOException 
    {
    	HttpPost post = new HttpPost(getRequestUri());
        post.setHeader("Accept", MediaType.APPLICATION_JSON);
        return buildEntityRequest(post, makeContent(), MediaType.APPLICATION_JSON);
    }
    
    /*
     * make the content payload to be sent
     */
    private byte[] makeContent() throws IOException 
    {

        return jsonMapper.writeValueAsBytes(tx);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object parseResponse(HttpEntity response)
            throws IOException
    {
    	// doesn't matter
    	return null;
    }
    
    /**
     * {@inheritDoc}}
     */
    @Override
    public void destroy()
    {

    }
}
