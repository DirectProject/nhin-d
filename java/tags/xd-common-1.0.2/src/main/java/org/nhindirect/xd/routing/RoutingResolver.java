/* 
 * Copyright (c) 2010, NHIN Direct Project
 * All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in the 
 *    documentation and/or other materials provided with the distribution.  
 * 3. Neither the name of the the NHIN Direct Project (nhindirect.org)
 *    nor the names of its contributors may be used to endorse or promote products 
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY 
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.nhindirect.xd.routing;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Resolve an address for routing purposes.
 * 
 * @author beau
 */
public abstract class RoutingResolver
{
    /**
     * Resolve an address to a stored value.
     * 
     * @param address
     *            The address to resolve.
     * @return the stored value for the address, or the address itself if no
     *         value is stored.
     */
    public abstract String resolve(String address);

    /**
     * Determine whether or not the provided address resolves to an XD endpoint.
     * 
     * @param address
     *            The address to resolve.
     * @return true if the address maps to an XD endpoint, false otherwise.
     */
    public abstract boolean isXdEndpoint(String address);

    /**
     * Determine whether or not the provided address resolves to an SMTP endpoint.
     * 
     * @param address The address to resolve.
     * @return
     */
    public abstract boolean isSmtpEndpoint(String address);
    
    /**
     * Return a collection of SMTP endpoints from the provided collection of
     * addresses.
     * 
     * @param addresses
     *            The collection of address from which to extract SMTP
     *            endpoints.
     * @return the SMTP endpoints within the provided collection.
     */
    public Collection<String> getSmtpEndpoints(Collection<String> addresses)
    {
        Collection<String> smtpEndpoints = new ArrayList<String>();

        for (String address : addresses)
        {
            if (isSmtpEndpoint(address))
            {
                smtpEndpoints.add(address);
            }
        }

        return smtpEndpoints;
    }

    /**
     * Return a collection of XD endpoints from the provided collection of
     * addresses.
     * 
     * @param addresses
     *            The collection of addresses from which to extract XD
     *            endpoints.
     * @return the XD endpoints within the provided collection.
     */
    public Collection<String> getXdEndpoints(Collection<String> addresses)
    {
        Collection<String> xdEndpoints = new ArrayList<String>();

        for (String address : addresses)
        {
            if (isXdEndpoint(address))
            {
                xdEndpoints.add(address);
            }
        }

        return xdEndpoints;
    }

    /**
     * Determine if the collection of addresses contains SMTP endpoints.
     * 
     * @param addresses
     *            The collection of addresses to inspect.
     * @return true if the collection contains SMTP endpoints, false otherwise.
     */
    public boolean hasSmtpEndpoints(Collection<String> addresses)
    {
        return !getSmtpEndpoints(addresses).isEmpty();
    }

    /**
     * Determine if the collection of addresses contains XD endpoints.
     * 
     * @param addresses
     *            The collection of addresses to inspect.
     * @return true if the collection contains XD endpoints, false otherwise.
     */
    public boolean hasXdEndpoints(Collection<String> addresses)
    {
        return !getXdEndpoints(addresses).isEmpty();
    }
}
