/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Chris Lomonico  chris.lomonico@surescripts.com
    Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.dns;

import org.xbill.DNS.Message;

import com.google.inject.ImplementedBy;

/**
 * A DNSStore encapsulates the physical medium that stores DNS records such as a files, databases, or web services.  A store processes DNS requests,
 * executes the lookup or operational request (such as zone transfers) logic, and returns an appropriate response.
 * @author Greg Meyer
 * @author Umesh Madan
 * 
 * @since 1.0
 */
@ImplementedBy(ConfigServiceDNSStore.class)
public interface DNSStore 
{
	/**
	 * Processes a lookup request for DNS records. 
	 * @param dnsMsg The DSN request message.
	 * @return The DNS response message.  Returns null for lookup requests if a matching record cannot be found.
	 * @throws DNSException Thrown is the request fails due to sever failure such as illegal request parameters or
	 * a failure accessing the physical record medium.
	 */
	public Message get(Message dnsMsg) throws DNSException;
}
