/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Umesh Madan     umeshma@microsoft.com
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

package org.nhindirect.stagent.mail.notifications;

/**
 * Represents an MDN Gateway as specified by <a href="http://tools.ietf.org/html/rfc3798">RFC 3798</a> 
 * <p>
 * From RFC 3798, 3.2.2, The MDN-Gateway field
 * <p>
 * <i>
 * mdn-gateway-field = "MDN-Gateway" ":" mta-name-type ";" mta-name<br>
 * ...<br>
 * For gateways into Internet Mail, the MTA-name-type will normally be
 * "smtp", and the mta-name will be the Internet domain name of the
 * gateway.
 * </i>
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class MdnGateway 
{
	/**
	 * The gateway type for gateways to Internet Mail.
	 */
    public static final String DefaultGatewayType = "smtp";
    
    private String domain;
	private String type;
	
	/**
	 * Initializes an instance with the specified <paramref name="domain"/> and the default type of "smtp"
	 * @param domain The domain name of this MDN Gateway
	 */
    public MdnGateway(String domain)    
	{
    	this(domain, DefaultGatewayType);
	}
    
    /**
     * Initializes an instance with the specified domain and type.
     * @param domain The domain name of this MDN Gateway
     * @param type The gateway type
     */
    public MdnGateway(String domain, String type)
    {
        this.domain = domain;
        this.type = type;
    }    
    
    /**
     * Gets the gateway domain.
     * @return The gateway domain.
     */
    public String getDomain() 
    {
		return domain;
	}

    /**
     * Sets the gateway domain.
     * @param domain The gateway domain.
     */
	public void setDomain(String domain) 
	{
		if (domain == null || domain.isEmpty())
			throw new IllegalArgumentException();
		
		this.domain = domain;
	}

	/**
	 * Gets the gateway type.
	 * @return The gateway type.
	 */
	public String getType() 
	{
		return type;
	}

	/**
	 * Sets the gateway type.
	 * @param type The gateway type.
	 */
	public void setType(String type) 
	{
		if (type == null || type.isEmpty())
			throw new IllegalArgumentException();	
		
		this.type = type;
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
    public String toString()
    {
		return type + "; " + domain;
    }

}
