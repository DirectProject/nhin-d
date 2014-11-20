/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
   in the documentation and/or other materials provided with the distribution.  
3. Neither the name of the The NHIN Direct Project (nhindirect.org) nor the names of its contributors may be used to endorse or promote 
   products derived from this software without specific prior written permission.
   
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.nhindirect.config.model;

import org.codehaus.enunciate.json.JsonRootType;
/**
 * Trust bundle to domain relationship.
 * @author Greg Meyer
 * @since 1.0
 */
///CLOVER:OFF
@JsonRootType
public class TrustBundleDomainReltn 
{
	private long id;
	
	private Domain domain;
	
	private TrustBundle trustBundle;
	
    private boolean incoming;
    
    private boolean outgoing;
	
    /**
     * Empty constructor
     */
	public TrustBundleDomainReltn()
	{
		
	}

	/**
	 * Gets the internal system id of the relationship.
	 * @return The internal system id of the relationship.
	 */
	public long getId() 
	{
		return id;
	}

	/**
	 * Sets the internal system id of the relationship.
	 * @param id The internal system id of the relationship.
	 */
	public void setId(long id) 
	{
		this.id = id;
	}

	/**
	 * Gets the domain associated to the trust bundle.
	 * @return The domain associated to the trust bundle.
	 */
	public Domain getDomain() 
	{
		return domain;
	}

	/**
	 * Sets the domain associated to the trust bundle.
	 * @param domain The domain associated to the trust bundle.
	 */
	public void setDomain(Domain domain)
	{
		this.domain = domain;
	}

	/**
	 * Gets the trust bundle associated to the domain.
	 * @return The trust bundle associated to the domain.
	 */
	public TrustBundle getTrustBundle() 
	{
		return trustBundle;
	}

	/**
	 * Sets the trust bundle associated to the domain.
	 * @param trustBundle The trust bundle associated to the domain.
	 */
	public void setTrustBundle(TrustBundle trustBundle) 
	{
		this.trustBundle = trustBundle;
	}

	/**
	 * Indicates if anchors in the bundle are valid for incoming messages.
	 * @return True if anchors in the bundle are valid for incoming messages.  False otherwise.
	 */
	public boolean isIncoming() 
	{
		return incoming;
	}

	/**
	 * Sets if anchors in the bundle are valid for incoming messages.
	 * @param incoming True if anchors in the bundle are valid for incoming messages.  False otherwise.
	 */
	public void setIncoming(boolean incoming) 
	{
		this.incoming = incoming;
	}

	/**
	 * Indicates if anchors in the bundle are valid for outgoing messages.
	 * @return True if anchors in the bundle are valid for outgoing messages.  False otherwise.
	 */
	public boolean isOutgoing() 
	{
		return outgoing;
	}

	/**
	 * Sets if anchors in the bundle are valid for outgoing messages.
	 * @param outgoing True if anchors in the bundle are valid for outgoing messages.  False otherwise.
	 */
	public void setOutgoing(boolean outgoing) 
	{
		this.outgoing = outgoing;
	}
	
	
}
///CLOVER:OFF