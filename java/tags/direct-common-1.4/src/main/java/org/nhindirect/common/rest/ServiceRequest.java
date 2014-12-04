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

package org.nhindirect.common.rest;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.nhindirect.common.rest.exceptions.ServiceException;

/**
 * Defines a service request.
 * @author Greg Meyer
 * @since 1.1
 * @param <T> The return type of the service request
 * @param <E> Exception of the service request.
 */
public interface ServiceRequest <T, E extends Exception> extends Callable<T>
{
    /**
     * Executes the request, either returning data of type T or throwing an exception of type E or
     * RuntimeException. Requests that are parameterized should accept their parameters through a
     * constructor.
     * 
     * @return the result type of the request.
     * @throws IOException
     *             if errors are encountered in writing to a request object or reading from a
     *             response object.
     * @throws OAuthException
     *             if errors are encountered by the OAuth components. Note that this exception
     *             should only be propagated from the OAuth components themselves; authorization
     *             problems indicated by the HTTP response that are related to OAuth should be
     *             indicated via {@link OAuthAuthorizationException}.
     * @throws E
     *             any additional exceptions thrown by this method.
     */
    @Override
    public T call() throws E, IOException, ServiceException;
    
    /**
     * Cleans up resourced associated with the request.
     */
    public void destroy();
}
