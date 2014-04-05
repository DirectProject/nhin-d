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
import java.util.Map;

import javax.mail.internet.MimeMessage;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.UnsecuredServiceRequestBase;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.tx.TxDetailParser;
import org.nhindirect.common.tx.TxUtil;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.common.tx.model.TxMessageType;

public class SuppressNotificationRequest extends UnsecuredServiceRequestBase<Boolean, RuntimeException>
{
	private final Tx notificationMessage;
	private final TxDetailParser parser;
	
	protected final Log LOGGER = LogFactory.getFactory().getInstance(SuppressNotificationRequest.class);
	
	
	public static String getOriginalMessageId(Tx tx, TxDetailParser parser)
	{
		///CLOVER:OFF
        if (tx == null) 
        {
            throw new IllegalArgumentException("Invalid parameter received. Tx cannot be null.");
        }
        ///CLOVER:ON
        
        final TxMessageType type = tx.getMsgType();
        if (type != TxMessageType.DSN && type != TxMessageType.MDN)
        	return "";
        
        final TxDetail detail = tx.getDetail(TxDetailType.PARENT_MSG_ID);
        return (detail != null && !detail.getDetailValue().isEmpty()) ? detail.getDetailValue() : "";
        
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
	
    public SuppressNotificationRequest(HttpClient httpClient, String txServiceUrl, ObjectMapper jsonMapper, TxDetailParser parser, MimeMessage msg) 
    {
        this(httpClient, txServiceUrl, jsonMapper, parser, convertMimeMessageToTx(msg, parser));
    }
		
    public SuppressNotificationRequest(HttpClient httpClient, String txServiceUrl, ObjectMapper jsonMapper, TxDetailParser parser, Tx notificationMessage) 
    {
        super(httpClient, txServiceUrl, jsonMapper);
        
        if (notificationMessage == null) 
        {
            throw new IllegalArgumentException("Notification message id cannot be null");
        }
        
        ///CLOVER:OFF
        if (parser == null)
        {
        	throw new IllegalArgumentException("Parser cannot be null");
        }
       ///CLOVER:ON
        
        this.parser = parser;
        this.notificationMessage = notificationMessage;
    }
    
    /*
     * The request uses an overloaded post pattern to perform the operation.
     */
    private String getRequestUri() 
    {
    	String theURI = serviceUrl.endsWith("/") ? serviceUrl : serviceUrl + "/";
    	
    	theURI += "txs/suppressNotification/";

    	
    	return theURI;
    }  

    @Override
    public Boolean call() throws RuntimeException, IOException, ServiceException 
    {
    	final String originalMessageId = getOriginalMessageId(notificationMessage, parser);
    	if (originalMessageId == null || originalMessageId.isEmpty())
    		return false;
    	
    	return super.call();
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

        return jsonMapper.writeValueAsBytes(notificationMessage);
    }
    
    /**
     * {@inheritDoc}
     * Typically a post would not have to do this, but we are using an overloaded post pattern to perform the operation.  Generally
     * we would use a get placing the parameters in a query string, but because the query string could be very large, we have chosen
     * to use an overloaded post.
     */
    @Override
    protected Boolean parseResponse(HttpEntity response)
            throws IOException
    {
    	try
    	{
    		checkContentType(MediaType.APPLICATION_JSON, response);
    	}
    	///CLOVER:OFF
    	catch (ServiceException e)
    	{
    		throw new IOException("Returned media type is not " + MediaType.APPLICATION_JSON, e);
    	}
    	///CLOVER:ON
    	return jsonMapper.readValue(response.getContent(), Boolean.class);
    }    
    
    
    /**
     * {@inheritDoc}}
     */
    @Override
    public void destroy()
    {

    }
    	
}
