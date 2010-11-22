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

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Header;
import org.xbill.DNS.Message;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Section;

/**
 * Abstract DNSResponder for DNS requests.  It implements common methods for calling the DNS store and handling error conditions.  Protocol specific
 * (UDP, TCP, etc) messaging handling is implemented in concrete implementations.
 * @author Greg Meyer
 * @since 1.0
 */
public abstract class DNSResponder 
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(DNSResponder.class);
	
	protected DNSServerSettings settings;
	protected DNSStore store;
	
	/**
	 * Creates a DNS responder using the provided settings and DNS store.  The responder will not handle requests
	 * until {@link #start()} is called.
	 * @param settings The DNS server settings.
	 * @param store The DNS store that holds the DNS record information.
	 * @throws DNSException
	 */
	public DNSResponder(DNSServerSettings settings, DNSStore store) throws DNSException
	{
		this.settings = settings;
		this.store = store;
	}
	
	/**
	 * Starts the responder.  Concrete implementation bind their protocol specific handlers and start accepting DNS requests.
	 * @throws DNSException
	 */
	public abstract void start() throws DNSException;

	/**
	 * Stops the responder.  The responder will not londer accept DNS requests after stop has been called.
	 * @throws DNSException
	 */
	public abstract void stop() throws DNSException;
	
	/**
	 * Processes a DNS request and returns a DNS response.  The request is in raw DNS wire protocol format. 
	 * @param rawMessage The raw DNS wire protocol format of the request.
	 * @return A response to the DNS request.
	 * @throws DNSException
	 */
	public Message processRequest(byte[] rawMessage) throws DNSException
	{
		if (rawMessage == null || rawMessage.length == 0)
			throw new DNSException(DNSError.newError(Rcode.FORMERR), "Message cannot be null or empty.");
		
		Message msg;
		try
		{
			msg = new Message(rawMessage);
		}
		catch (IOException e)
		{
			throw new DNSException(DNSError.newError(Rcode.FORMERR), "IO Exception reading raw message.", e);
		}
		
		return processRequest(msg);
	}
	
	/**
	 * Processes a DNS request and returns a DNS response.
	 * @param request The DNS request message.
	 * @return A response to the DNS request.
	 */
	public Message processRequest(Message request)
	{
		if (request == null)
			throw new IllegalArgumentException("Missing request.  Request cannot be null.");
		
		Message response;
		try
		{
			response = store.get(request);
            if (response == null || response.getHeader() == null || response.getHeader().getRcode() != Rcode.NOERROR)
            {
            	response = processError(request, DNSError.newError(Rcode.NXDOMAIN));	
            }   			
		}
		catch (DNSException e)
		{

			LOGGER.error("Error processing DNS request: " + e.getMessage(), e);
			response = processError(request, e.getError());			
		}
		
		return response;
	}
	
	/**
	 * Processes a DNS error condition and creates an appropriate DNS response.
	 * @param request The original DNS request.
	 * @param error The error condition that occured.
	 * @return A response to the DNS request.
	 */
    protected Message processError(Message request, DNSError<?> error)
    {
    	Message errorResponse = null;
    	try
    	{
    		Header respHeader = new Header(request.toWire());
    		Message response = new Message();
    		response.setHeader(respHeader);
    		
    		for (int i = 0; i < 4; i++)
    			response.removeAllRecords(i);

    		response.addRecord(request.getQuestion(), Section.QUESTION);
    		
            response.getHeader().setFlag(Flags.QR);
        	if (request.getHeader().getFlag(Flags.RD))
        		response.getHeader().setFlag(Flags.RD);    		
    		respHeader.setRcode(Integer.parseInt(error.getError().toString()));    		    		
    		
    		return response;
    	}
    	catch (IOException e) {}
    	
    	return errorResponse;
    }
    
    /**
     * Converts a raw DNS wire protocol format message to a Message structure.
     * @param buffer The raw DNS wire protocol format.
     * @return A Message object converted from the buffer.
     * @throws DNSException
     */
    protected Message toMessage(byte[] buffer) throws DNSException
    {
    	if (buffer.length <= 0 || buffer.length > settings.getMaxRequestSize())
    		throw new DNSException(DNSError.newError(Rcode.REFUSED), "Invalid request size " + buffer.length);
    	
    	try
    	{
    		return new Message(buffer);
    	}
    	catch (IOException e)
    	{
    		throw new DNSException(DNSError.newError(Rcode.FORMERR), "Failed to deserialize raw byte message.");
    	}
    }
    
    /**
     * Converts a Message object to a raw DNS wire format byte array.
     * @param msg The message to convert.
     * @return A byte array representing the raw DNS wire format of the message.
     */
    protected byte[] toBytes(Message msg)
    {
    	return msg.toWire();
    }
}
