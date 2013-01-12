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

package org.nhindirect.common.tx;

import java.io.InputStream;
import java.util.Map;

import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

import org.nhindirect.common.tx.model.TxDetail;

/**
 * Interface for a parser the breads a message into a set of Tx details
 * @author Greg Meyer
 * @since 1.1
 */
public interface TxDetailParser 
{
	/**
	 * Processed a collection InternetHeaders into a set attributes that will be stored as a set of transaction details.  Most details will
	 * take the same name as their corresponding Mime standard header name.
	 * <p>
	 * This method is particularly helpful when dealing with very large messages that are streamed and you do not wish to 
	 * load the entire message into memory.  Instead, only the headers are extracted from the message stream and processed.
	 * <br>  
	 * <b>NOTE:<\b> Using only message headers will not disable the ability to process some attributes such as MDN dispositions and DNS messages because
	 * they need to be parsed from the message body.  
	 * @param headers The Internet headers to be processed.
	 * @return A map of detail names to values.
	 */
	public Map<String, TxDetail> getMessageDetails(InternetHeaders headers);
	
	/**
	 * Processed a MimeMessage represented as an input stream to a set attributes that will be stored as a set of transaction details.  Most details will
	 * take the same name as their corresponding Mime standard header name.
	 * @param stream The message that will be processed.
	 * @return A map of detail names to values.
	 */
	public Map<String, TxDetail> getMessageDetails(InputStream stream);
	
	/**
	 * Processed a MimeMessage into set attributes that will be stored as a set of transaction details.  Most details will
	 * take the same name as their corresponding Mime standard header name.
	 * @param msg The message that will be processed.
	 * @return A map of detail names to values.
	 */
	public Map<String, TxDetail> getMessageDetails(MimeMessage msg);
}
