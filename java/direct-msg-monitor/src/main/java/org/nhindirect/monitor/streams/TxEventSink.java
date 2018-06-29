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

package org.nhindirect.monitor.streams;

import java.io.IOException;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.common.tx.model.Tx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Spring data streams Sink for TxEvents.  This class reads Tx messages from the event stream and submits them to the
 * camel route.
 * @author Greg Meyer
 * @Since 2.0
 */
@EnableBinding(TxInput.class)
public class TxEventSink
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(TxEventSink.class);
	
    @Autowired
	protected ObjectMapper objectMapper;
	
    @Autowired
	protected ProducerTemplate template;
    
	@StreamListener(target = TxInput.TX_INPUT)
	public void addTx(String txMarshalled) throws ClassNotFoundException, JsonParseException, JsonMappingException, IOException 
	{
    	///CLOVER:OFF
    	if (LOGGER.isTraceEnabled())
    		LOGGER.trace("Attempting to add Tx");
    	///CLOVER:ON	
		
		final Tx tx = objectMapper.readValue(txMarshalled, Tx.class);
		
    	if (template == null)
    		throw new IllegalStateException("Template producer cannot be null.  Please examine the txs resource configuration");
    	
    	try
    	{
    		template.sendBody(tx);
    	}
    	catch (Throwable t)
    	{
    		LOGGER.error("Failed to add Tx message", t);
    	}
    	
    	///CLOVER:OFF
    	if (LOGGER.isTraceEnabled())
    		LOGGER.trace("Tx added");		
	}
}
