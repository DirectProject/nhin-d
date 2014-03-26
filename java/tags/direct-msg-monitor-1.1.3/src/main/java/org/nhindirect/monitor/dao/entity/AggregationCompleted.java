/*
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

package org.nhindirect.monitor.dao.entity;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * DAO entity object for completed but not confirmed aggregations.
 * @author Greg Meyer
 * @since 1.1
 */
@Entity
@Table(name = "msgmonaggregationcomp")
public class AggregationCompleted 
{
	private String id;
	private byte[] blob;
	private int version;
	private Calendar recoveryLockedUntilDtTm;
	
	
	/**
	 * Default constructor
	 */
	public AggregationCompleted()
	{
		
	}
	
	/**
	 * Gets the exchange id.
	 * @return The exchange id of the aggregation.
	 */
    @Id
    @Column(name = "id", nullable = false)
    public String getId() 
    {
        return id;
    }
    
    /**
     * Set the value of id.
     * 
     * @param id
     *            The value of id.
     */
    public void setId(String id) 
    {
        this.id = id;
    }
    
    /**
     * Gets the serialized version of the exchange.
     * @return The serialized version of the exchange.
     */
    @Lob
    @Column(name = "exchangeBlob", length=65536)
    public byte[] getExchangeBlob()
    {
    	return blob;
    }
    
    /**
     * Sets the serialized version of the exchange.
     * @param The serialized version of the exchange.
     */
    public void setExchangeBlob(byte[] blob)
    {
    	this.blob = blob;
    }

    /**
     * Gets the version of the aggregation state.
     * @return The version of the aggregation state.
     */
    @Version
    @Column(name = "version")
    public int getVersion() 
    {
        return version;
    }
    
    /**
     * Sets the version of the aggregation state.
     * @param version The version of the aggregation state.
     */
    public void setVersion(int version)
    {
    	this.version = version;
    }
    
    /**
     * Gets the time that aggregation will become unlocked
     * @return The time that aggregation will become unlocked
     */
    @Column(name = "recoveryLockedUntilDtTm")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getRecoveryLockedUntilDtTm() 
    {
        return recoveryLockedUntilDtTm;
    }
    
    /**
     * Sets the time that aggregation will become unlocked
     * @param recoveryLockedUntilDtTm The time that aggregation will become unlocked
     */
    public void setRecoveryLockedUntilDtTm(Calendar recoveryLockedUntilDtTm) 
    {
        this.recoveryLockedUntilDtTm = recoveryLockedUntilDtTm;
    }
}
