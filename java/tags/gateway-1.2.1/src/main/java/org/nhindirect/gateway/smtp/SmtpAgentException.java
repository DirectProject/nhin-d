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

import org.nhindirect.stagent.NHINDException;

/**
 * Exceptions thrown by the {@link SmtpAgent}
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class SmtpAgentException extends NHINDException
{
	static final long serialVersionUID = 8534058110248864799L;
	
    /**
     * Constructs an exception with an SmtpAgentError.
     * @param error The smtp agent error
     */
    public SmtpAgentException(SmtpAgentError error)
    {
    	super(error);
    }
    
	/**
	 * Constructs an exception with a message and the smtp agent error.
	 * @param error The smtp agent error
	 * @param msg The exception message.
	 */    
    public SmtpAgentException(SmtpAgentError error, String message)
    {
    	super(error, message);
    }
       
	/**
	 * Constructs an exception with the smtp agent error and the exception that caused the error.
	 * @param error The smtp agent error.
	 * @param innerException The exception that caused the error.
	 */     
    public SmtpAgentException(SmtpAgentError error, Exception innerException)
    {
    	super(error, innerException);
    }
    
	/**
	 * Constructs an exception with the smtp agent error, a message, and the exception that caused the error.
	 * @param error The smtp agent error.
	 * @param msg The exception message.
	 * @param innerException The exception that caused the error.
	 */      
    public SmtpAgentException(SmtpAgentError error, String message, Exception innerException)
    {
    	super(error, message, innerException);
    }
}
