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

package org.nhindirect.stagent.cryptography.activekeyops;

import java.security.Key;
import java.security.NoSuchProviderException;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientInformation;
import org.nhindirect.stagent.CryptoExtensions;

/**
 * A default implementation of the DirectRecipientInformation interface.  This implementation delegates directly to the 
 * BouncyCastle implementation.
 * @author gm2552
 * @since 2.1
 */
public class DefaultDirectRecipientInformation implements DirectRecipientInformation
{
	protected RecipientInformation recipInfo;
	protected final String encProvider;
	
	/**
	 * Constructor
	 * @param recipInfo The recip info
	 * @param encProvider The JCE provider used to decrypt this message.
	 */
	public DefaultDirectRecipientInformation(RecipientInformation recipInfo, String encProvider)
	{
		this.encProvider =  (StringUtils.isEmpty(encProvider)) ? CryptoExtensions.getJCESensitiveProviderName() : encProvider;
		this.recipInfo = recipInfo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] getDecryptedContent(Key key) throws CMSException, NoSuchProviderException 
	{
		return recipInfo.getContent(key, encProvider);
	}
	
	
}
