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

package org.nhindirect.monitor.aggregator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.monitor.condition.TxCompletionCondition;
import org.nhindirect.monitor.condition.TxTimeoutCondition;

/**
 * Base aggregator for incoming transactions that combines messages together based on the correlation expression
 * and determines the completion of the aggregation based on an injected completion condition implementation.
 * <p>
 * The aggregator combines {@link Tx} messages into a collection of {@link Tx} messages.  A route may reuse the same
 * aggregator instance as a completion and/or timeout condition utilizing the instance as a Camel bean language expression and specifying
 * the {@link BasicTxAggregator#isAggregationComplete(Exchange)} and {@link BasicTxAggregator#getAggregationTime(Exchange)}
 * methods respectively.  
 * <p>
 * A completionCondition of type {@link TxCompletionCondition}
 * must be specified in the constructor if the  {@link BasicTxAggregator#isAggregationComplete(Exchange)} method is to be utilized.  
 * Failure to do so will result in an IllegalStateException when calling {@link BasicTxAggregator#isAggregationComplete(Exchange)}.
 * <p>
 * A timeoutCondition of type {@link TxTimeoutCondition}
 * must be specified in the constructor if the  {@link BasicTxAggregator#getAggregationTime(Exchange)} method is to be utilized.  
 * Failure to do so will result in an IllegalStateException when calling {@link BasicTxAggregator#getAggregationTime(Exchange)}. 
 * @author Greg Meyer
 * @Since 1.0
 *
 */
public class BasicTxAggregator implements AggregationStrategy
{	
	protected final TxCompletionCondition completionCondition;
	
	protected final TxTimeoutCondition timeoutCondition;
	
	/**
	 * Constructor
	 * @param completionCondition A completion condition that evaluates if the set of aggregated messages
	 * is considered complete and can be passed on to the next step in a Camel route.
	 */
	public BasicTxAggregator(TxCompletionCondition completionCondition, TxTimeoutCondition timeoutCondition)
	{	
		this.completionCondition = completionCondition;
		this.timeoutCondition = timeoutCondition;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) 
	{
		// if the old exchange is null, then this is the first message is the aggregation set
		// as determined by the correlator
        if (oldExchange == null) 
        {
        	// just replace the contents of the incoming exchange with a collection of Tx messages
        	final Collection<Tx> txs = new ArrayList<Tx>();
        	
        	txs.add(newExchange.getIn().getBody(Tx.class));
        	newExchange.getIn().setBody(txs);
        	
        	return newExchange; 
        }
        
        // the old exchange should contain the aggregated set of Tx messages as a collection
        // add the Tx message in the new exchange to the collection of the old exchange
        @SuppressWarnings("unchecked")
		final Collection<Tx> txs = oldExchange.getIn().getBody(Collection.class);
        txs.add(newExchange.getIn().getBody(Tx.class));
        
        oldExchange.getIn().setBody(txs);
        return oldExchange;
    }
	
	
	/**
	 * Determines if the set of messages contained in the exchange is complete and can be moved on to the next step in the route.
	 * @param theExchange The Camel message exchange envelope.  The envelopes incoming body MUST contains a collection of {@link Tx}
	 * object or be empty.
	 * @return true if the set of aggregated message is complete; false otherwise
	 */
	public boolean isAggregationComplete(Exchange theExchange)
	{
		if (completionCondition == null)
			throw new IllegalStateException("Completion condition cannot be null when utilizing the isAggregationComplete method");
		
		// first make sure there is an IMF message... that's our starting point, so don't 
		// do anything without it
        @SuppressWarnings("unchecked")
		final Collection<Tx> txs = theExchange.getIn().getBody(Collection.class);
		
        if (txs == null)
        	return false;
		
		return completionCondition.isComplete(txs);
	}
	

	public Long getAggregationTime(Exchange theExchange)
	{
		if (timeoutCondition == null)
			throw new IllegalStateException("Timeout condition cannot be null when utilizing the getAggregationTime method");
		
		// first make sure there is an IMF message... that's our starting point, so don't 
		// do anything without it
        @SuppressWarnings("unchecked")
		final Collection<Tx> txs = theExchange.getIn().getBody(Collection.class);
        
        if (txs == null)
        	return null;
		
        //final Long initialExhangeTime = theExchange.getProperty(TxConditionConstants.AGGREGATION_GROUP_START_TIMESTAMP, Long.class);
        final Date initialExhangeTime = theExchange.getProperty(Exchange.CREATED_TIMESTAMP, Date.class);
        
        if (initialExhangeTime == null)
        	return null;
		
		return timeoutCondition.getTimeout(txs, initialExhangeTime.getTime());
	}
}
