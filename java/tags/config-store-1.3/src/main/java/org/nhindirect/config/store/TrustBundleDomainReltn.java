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

package org.nhindirect.config.store;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * JPA entity object for a trust bundle to domain relationship
 * @author Greg Meyer
 * @since 1.2
 */
@Entity
@Table(name = "trustbundledomainreltn")
public class TrustBundleDomainReltn 
{
	private long id;
	
	private Domain domain;
	
	private TrustBundle trustBundle;
	
    private boolean incoming;
    
    private boolean outgoing;
	
	public TrustBundleDomainReltn()
	{
		
	}
	
    /**
     * Get the value of id.
     * 
     * @return the value of id.
     */
    @Column(name = "id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() 
    {
        return id;
    }
    
    /**
    * Set the value of id.
    * 
    * @param id
    *            The value of id.
    */
   public void setId(long id) 
   {
       this.id = id;
   }   
   
   /**
    * Gets the value of the trust bundle.
    * 
    * @return The value of the trust bundle.
    */
   @ManyToOne(optional = false, fetch = FetchType.EAGER)
   @JoinColumn(name = "trust_bundle_id")
   public TrustBundle getTrustBundle() 
   {
       return trustBundle;
   }
   
   /**
    * Sets the value of the trust bundle.
    * 
    * @param bundle The value of the trust bundle.
    */
   public void setTrustBundle(TrustBundle bundle)
   {
	   this.trustBundle = bundle;
   }
   
   
   /**
    * Gets the value of the domain.
    * 
    * @return The value of the domain.
    */
   @ManyToOne(optional = false, fetch = FetchType.EAGER)
   @JoinColumn(name = "domain_id")
   public Domain getDomain() 
   {
       return domain;
   }
   
   /**
    * Sets the value of the domain.
    * 
    * @param bundle The value of the domain.
    */
   public void setDomain(Domain domain)
   {
	   this.domain = domain;
   }  
   
   /**
    * Get the value of incoming.
    * 
    * @return the value of incoming.
    */
   @Column(name = "forIncoming")
   public boolean isIncoming() 
   {
       return incoming;
   }

   /**
    * Set the value of incoming.
    * 
    * @param incoming
    *            The value of incoming.
    */
   public void setIncoming(boolean incoming) 
   {
       this.incoming = incoming;
   } 
   
   /**
    * Get the value of outgoing.
    * 
    * @return the value of outgoing.
    */
   @Column(name = "forOutgoing")
   public boolean isOutgoing() {
       return outgoing;
   }

   /**
    * Set the value of outgoing.
    * 
    * @param outgoing
    *            The value of outgoing.
    */
   public void setOutgoing(boolean outgoing) {
       this.outgoing = outgoing;
   }
}
