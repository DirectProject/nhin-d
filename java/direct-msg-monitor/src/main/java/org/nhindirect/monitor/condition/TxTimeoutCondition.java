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
 * Interface that defines a time condition.  A timeout condition determine how long correlation of messages will remain in the
 * aggregator.  For message monitoring, the timeout is based on a maximum amount of time that a correlation may stay in the aggregator
 * regardless of when the last message arrived.
 * This differs from traditional Camel timeouts which reset the time each time a new message is received.
 * @author Greg Meyer
 * @since 1.0
 */
public interface TxTimeoutCondition 
{
	/**
	 * Calculates the maximum time in milliseconds that the correlation of messages will stay in the aggregator before
	 * being considered timed out.
	 * @param txs Collection of messages correlated together
	 * @param exchangeStartTime The time that the correlation first entered the system.
	 * @return The remaining timeout in milliseconds.
	 */
	public long getTimeout(Collection<Tx> txs, long exchangeStartTime);
}
