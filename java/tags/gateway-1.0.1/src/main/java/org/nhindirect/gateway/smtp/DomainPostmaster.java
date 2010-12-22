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

package org.nhindirect.gateway.smtp;

import javax.mail.internet.InternetAddress;

/**
 * Indicates the postmaster account for a domain.
 * @author Greg Meyer
 * @author Umesh Madan
 */
public class DomainPostmaster
{
	private String domain;
	private InternetAddress postmaster;
	
	/**
	 * Constructs an empty postmaster.
	 */
	public DomainPostmaster()
	{
		domain = "";
		postmaster = new InternetAddress();
	}
	
	/**
	 * Constructs a postmaster for a domain with a postmaster address.
	 * @param domain The mail domain of the postmater.
	 * @param postmaster The postmaster's email address.
	 */
	public DomainPostmaster(String domain, InternetAddress postmaster)
	{
		this.domain = domain;
		this.postmaster = postmaster;
	}

	/**
	 * Gets the postmaster domain.
	 * @return The postmaster domain.
	 */
	public String getDomain() 
	{
		return domain;
	}

	/**
	 * Sets the postmaster's domain.
	 * @param domain The postmaster's domain.
	 */
	public void setDomain(String domain) 
	{
		this.domain = domain;
	}

	/**
	 * Get's the postmaster's email address.
	 * @return The postmaster's email address.
	 */
	public InternetAddress getPostmaster() 
	{
		return postmaster;
	}

	/**
	 * Sets the postmaster's email address.
	 * @param postmaster The postmaster's email address.
	 */
	public void setPostmasters(InternetAddress postmaster) 
	{
		this.postmaster = postmaster;
	}
	
	
}
