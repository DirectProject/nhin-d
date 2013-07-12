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

package org.nhindirect.common.tx.model;

/**
 * An enumeration of predefined message detail types
 * @author Greg Meyuer
 * @since 1.1
 */
public enum TxDetailType 
{
	/**
	 * Unknown detail type
	 */
	UNKNOWN("UNKNOWN"),	
	
	/**
	 * The message sender from the Sender header of a message
	 */
	SENDER("SENDER"),	
	
	/**
	 * The originator of the message from the From header of a message
	 */
	FROM("FROM"),	
	
	/**
	 * A comma delimited list of all recipients of a message
	 */
	RECIPIENTS("RECIPIENTS"),

	/**
	 * The final recipients of DNS and MDN messages
	 */
	FINAL_RECIPIENTS("FINAL_RECIPIENTS"),
	
	/**
	 * The message id of message
	 */
	MSG_ID("MSG_ID"),	
	
	/**
	 * For DNS and MDN message, this is the message id of the original message that the DSN or MDN message corresponds to
	 */
	PARENT_MSG_ID("PARENT_MESSAGE_ID"),
	
	/**
	 * The subject of the message
	 */
	SUBJECT("SUBJECT"),

	/**
	 * The DSN status field from a the delivery status part of a DSN message
	 */
	DSN_STATUS("DSN_STATUS"),
	
	/**
	 * The DSN action field from a the delivery status part of a DSN message
	 */
	DSN_ACTION("DSN_ACTION"),
	
	/**
	 * The message disposition of an MDN message
	 */
	DISPOSITION("DISPOSTION"),
	
	/**
	 * Disposition options 
	 */
	DISPOSITION_OPTIONS("DISPOSITION_OPTIONS"),

	/**
	 * The full message headers of a message
	 */
	MSG_FULL_HEADERS("MSG_FULL_HEADERS"); 
	
	protected final String type;
	
	/**
	 * Private constructor
	 * @param type The type
	 */
    private TxDetailType(String type) 
	{
        this.type = type;
    }
    
    /**
     * Gets the detail type as a string
     * @return
     */
    public String getType()
    {
    	return type;
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString()
    {
    	return getType();
    }
}
