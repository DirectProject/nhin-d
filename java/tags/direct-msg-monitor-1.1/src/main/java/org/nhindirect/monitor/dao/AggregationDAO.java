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

package org.nhindirect.monitor.dao;

import java.util.List;

import org.nhindirect.monitor.dao.entity.Aggregation;
import org.nhindirect.monitor.dao.entity.AggregationCompleted;

/**
 * DAO interface definition for storing camel exchanges for an aggregator repository.
 * @author Greg Meyer
 * @since 1.1
 */
public interface AggregationDAO 
{
	/**
	 * Gets an aggregation object by a correlation id.
	 * @param id The correlation id.
	 * @return The Aggregation exchange for the given correlation id.  Returns null if an aggregation for the id does not exist.
	 * @throws AggregationDAOException
	 */
	public Aggregation getAggregation(String id) throws AggregationDAOException;
	
	/**
	 * Adds a new aggregation if an entry for the aggregation's id does not already exist.  If the aggregation already exists,
	 * then the state is updated with the new aggregation.  Checking is done against the aggregations version number to ensure
	 * it was updated against the latest aggregation in the repository.  An AggregationVersionException is thrown if the version
	 * numbers do not match.
	 * @param aggr The aggregation that is either updated or added.
	 * @throws AggregationDAOException
	 */
	public void addUpdateAggregation(Aggregation aggr) throws AggregationDAOException;;
	
	/**
	 * Removes an aggregation object from the repository after its completion condition has been met.  The aggregation
	 * is moved to the completed repository until it has been successfully routed to its camel destination and confirmed.
	 * As with the {@link #addUpdateAggregation(Aggregation)} operation, the version number is validated before the 
	 * aggregation can be removed.  An AggregationVersionException is thrown if the version
	 * numbers do not match.
	 * @param aggr  The aggregation to remove and move to the completed repository.
	 * @param exchangeId  The id of the camel exchange.  This id becomes the key in the completed repository.
	 * @throws AggregationDAOException
	 */
	public void removeAggregation(Aggregation aggr, String exchangeId) throws AggregationDAOException;;
	
	/**
	 * Confirms that an exchange has been moved to its final camel destination and removes the exchange from the completed
	 * repository.  Version checking is not necessary because the content of a completed exchange should not change.
	 * @param id The exchange id of the exchange that is being confirmed.
	 * @throws AggregationDAOException
	 */
	public void confirmAggregation(String id) throws AggregationDAOException;;
	
	/**
	 * Gets a completed aggregation/exchange by exchange id.  This operation is used for recovery of completed exchanges
	 * that could not be delivery to their final camel destination.
	 * <p>
	 * This operation has an optional lock parameter that locks the completed
	 * exchange for a given amount of configurable time.  If the exchange is locked and the lock parameter is true, this method will
	 * return null.  This is necessary when multiple instances of the aggreation repository are running at the same time.  Camel automatically
	 * runs the recovery process on regulary interval.  Locking the completed exchange ensures multiple instances do not try to recover and 
	 * redeliver the same exchange at the same time.
	 * @param id The id of the exchange to recover.
	 * @param lock If true, the exchange is locked.
	 * @return Recovered exchange for redelivery.
	 * @throws AggregationDAOException
	 */
	public AggregationCompleted getAggregationCompleted(String id, boolean lock) throws AggregationDAOException;
	
	/**
	 * Gets a list of the keys of all aggregations/exchanges currently not completed.
	 * @return List of the keys of all aggregations/exchanges currently not completed.
	 * @throws AggregationDAOException
	 */
	public List<String> getAggregationKeys() throws AggregationDAOException;;
	
	/**
	 * Gets a list of the keys of all aggregations/exchanges that have been completed, but not confirmed.
	 * @return List of the keys of all aggregations/exchanges that have been completed, but not confirmed.
	 * @throws AggregationDAOException
	 */
	public List<String> getAggregationCompletedKeys() throws AggregationDAOException;;

	/**
	 * Purges all contents of the DAO store.  Generally only used for testing purposes.
	 * @throws AggregationDAOException
	 */
	public void purgeAll() throws AggregationDAOException;
}
