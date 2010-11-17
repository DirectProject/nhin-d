package org.nhindirect.dns;

import java.io.IOException;

import org.xbill.DNS.Header;
import org.xbill.DNS.Message;
import org.xbill.DNS.Rcode;

public abstract class DNSResponder 
{
	protected DNSServerSettings settings;
	protected DNSStore store;
	
	public DNSResponder(DNSServerSettings settings, DNSStore store) throws DNSException
	{
		this.settings = settings;
		this.store = store;
	}
	
	public abstract void start() throws DNSException;

	public abstract void stop() throws DNSException;
	
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
                throw new DNSException(DNSError.newError(Rcode.NXDOMAIN), "DNS store lookup error");
            }   			
		}
		catch (DNSException e)
		{
			response = processError(request, e.getError());			
		}
		
		return response;
	}
	
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

    		respHeader.setRcode(Integer.parseInt(error.getError().toString()));    		    		
    		
    		return response;
    	}
    	catch (IOException e) {}
    	
    	return errorResponse;
    }
    
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
    
    protected byte[] toBytes(Message msg)
    {
    	return msg.toWire();
    }
}
