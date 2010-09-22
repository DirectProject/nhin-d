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
 * Represents a Reporting-UA as specified by <a href="http://tools.ietf.org/html/rfc3798">RFC 3798</a>.
 * <p>
 * From RFC 3798, 3.2.2, The Reporting-UA field <br>
 * <i>
 * reporting-ua-field = "Reporting-UA" ":" ua-name ";" ua-product
 * 
 * For Internet Mail user agents, it is recommended that this field contain both: 
 * the DNS name of the particular instance of the MUA that generated the MDN and the
 * name of the product
 * </i>
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class ReportingUserAgent 
{
	private String name;
    private String product;
	
    /**
     * Initializes an instance with the specified user agent name and product name.
     * @param name The user agent name
     * @param product The user agent product
     */
    public ReportingUserAgent(String name, String product)
    {
        setName(name);
        setProduct(product);
    }
	
    /**
     * Gets the user agent's domain name.
     * @return The user agent's domain name
     */
    public String getName() 
    {
		return name;
	}

    /**
     * Sets the user agent's domain name.
     * @param name The user agent's domain name
     */
	public void setName(String name) 
	{
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException();
		
		this.name = name;
	}

	/**
	 * Gets the user agent's product
	 * @return The user agent's product
	 */
	public String getProduct() 
	{
		return product;
	}

	/**
	 * Sets the user agent's product.
	 * @param product The user agent's product
	 */
	public void setProduct(String product) 
	{
		if (product == null || product.isEmpty())
			throw new IllegalArgumentException();		
		
		this.product = product;
	}    
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return name + ";" + product;		
	}
}
