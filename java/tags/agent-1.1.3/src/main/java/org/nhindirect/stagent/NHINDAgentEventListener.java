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

package org.nhindirect.stagent;

/**
 * Callback interface for custom processing of a message in the {@link DefaultNHINDAgent}.  Implementations of this interface can be used to execute custom logic
 * during the processing stages of a message.  
 * @author Greg Meyer
 * @author Umesh Madan
 */
public interface NHINDAgentEventListener 
{
	/**
	 * Called when an unexpected error occurs in the agent.
	 * @param e The exception thrown by the agent.
	 */
    public void error(Exception e);
    
    /**
     * Called after the message has been validated but before it is decrypted. 
     * @param msg The incoming message.
     * @throws NHINDException
     */
    public void preProcessIncoming(IncomingMessage msg) throws NHINDException;
    
    /**
     * Called after the message is decrypted and the signature is validated.
     * @param msg The incoming message.
     * @throws NHINDException
     */
    public void postProcessIncoming(IncomingMessage msg) throws NHINDException;
    
    /**
     * Called in an exception occurs during the message processing stages. 
     * @param msg The incoming message.
     * @param The exception thrown by the agent.
     */
    public void errorIncoming(IncomingMessage msg, Exception e);
    
    /**
     * Called after the message has been validated but before it is encypted and signed. 
     * @param msg The outgoing message.
     * @throws NHINDException
     */    
    public void preProcessOutgoing(OutgoingMessage msg) throws NHINDException;
    
    /**
     * Called after the message has been encypted and signed. 
     * @param msg The outgoing message.
     * @throws NHINDException
     */      
    public void postProcessOutgoing(OutgoingMessage msg) throws NHINDException;
    
    /**
     * Called in an exception occurs during the message processing stages. 
     * @param msg The incoming message.
     * @param The exception thrown by the agent.
     */    
    public void errorOutgoing(OutgoingMessage msg, Exception e);
}
