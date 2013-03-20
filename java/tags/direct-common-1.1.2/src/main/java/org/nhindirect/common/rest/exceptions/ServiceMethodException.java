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

package org.nhindirect.common.rest.exceptions;

/**
 * Exceptions occurring due to an error in a service method.
 * @author Greg Meyer
 * @since 1.1
 */
///CLOVER:OFF
public class ServiceMethodException extends ServiceException
{
	static final long serialVersionUID = -9082791456753401631L;
	
	final int responseCode;
    /////CLOVER:OFF
	/**
	 * {@inheritDoc}
	 */
    /**
     * Constructor
     * @param responseCode The HTTP response code
     */
    public ServiceMethodException(int responseCode) 
    {
        this.responseCode = responseCode;
    }

    /**
     * Constructor 
     * @param responseCode The HTTP response code
     * @param t The parent exception.
     */
    public ServiceMethodException(int responseCode, Throwable t) 
    {
        super(t);
        this.responseCode = responseCode;
    }

    /**
     * Constructor 
     * @param responseCode The HTTP response code
     * @param msg Error message description
     */
    public ServiceMethodException(int responseCode, String msg) 
    {
        super(msg);
        this.responseCode = responseCode;
    }

    /**
     * Constructor
     * @param responseCode The HTTP response code
     * @param msg Error message description
     * @param t The parent exceptions
     */
    public ServiceMethodException(int responseCode, String msg, Throwable t) 
    {
        super(msg, t);
        this.responseCode = responseCode;
    }

    /**
     * @return the HTTP response code associated with this exception.
     */
    public int getResponseCode() 
    {
        return responseCode;
    }
}
///CLOVER:ON
