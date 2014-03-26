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

package org.nhindirect.monitor.condition;

import java.util.Collection;

import org.nhindirect.common.tx.model.Tx;

/**
 * Interface for an aggregation completion condition.  If the condition is true, the aggregated message is moved onto
 * the next step in a Camel route.
 * @author Greg Meyer
 * @since 1.0
 */
public interface TxCompletionCondition 
{	
	/**
	 * Determines if the set of {@link Tx} messages meets the set of conditions to be considered complete. 
	 * @param txs A collection of {@link Tx} objects that have been aggregated together by an Aggregator.  The collection
	 * of messages have a relation as determined by a correlator.
	 * @return true if the set of aggregated message is complete; false otherwise
	 */
	public boolean isComplete(Collection<Tx> txs);
	
	/**
	 * Gets the list of message recipients that are not considered complete.  Before a correlation of messages is considered complete,
	 * all recipients must meet the completion criteria.
	 * @param txs A collection of {@link Tx} objects that have been aggregated together by an Aggregator.  The collection
	 * of messages have a relation as determined by a correlator.
	 * @return List of message recipients that are incomplete.
	 */
	public Collection<String> getIncompleteRecipients(Collection<Tx> txs);
}
