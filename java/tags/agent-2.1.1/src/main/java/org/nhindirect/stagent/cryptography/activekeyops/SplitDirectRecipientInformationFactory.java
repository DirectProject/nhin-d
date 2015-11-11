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

import java.io.InputStream;
import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.mail.smime.SMIMEEnveloped;
import org.nhindirect.stagent.CryptoExtensions;

/**
 * Factory class for creating instances of the concrete SplitDirectRecipientInformation class.
 * @author Greg Meyer
 * @since 2.1
 */
public class SplitDirectRecipientInformationFactory implements DirectRecipientInformationFactory
{
	protected final String keyEncProvider;
	protected final String encProvider;

	
	/**
	 * Constructor.  Defaults the JCE providers to the CryptoExtensions.getJCESensitiveProviderName() and
	 *  CryptoExtensions.getJCEProviderName() values.
	 */
	public SplitDirectRecipientInformationFactory()
	{
		this("", "");
	}
	
	/**
	 * Constructor
	 * @param keyEncProvider The name of the JCE provider used to decrypt the messgae content using the message symmetric key.  If this value
	 * is null of empty, the CryptoExtensions.getJCESensitiveProviderName() value is used.
	 * @param encProvider The name of the JCE provider used to decrypt the messgae content using the message symmetric key.  If this value
	 * is null of empty, the CryptoExtensions.getJCEProviderName() value is used.
	 */
	public SplitDirectRecipientInformationFactory(String keyEncProvider, String encProvider)
	{
		this.keyEncProvider = (StringUtils.isEmpty(keyEncProvider)) ? CryptoExtensions.getJCESensitiveProviderName() : keyEncProvider;
		this.encProvider = (StringUtils.isEmpty(encProvider)) ? CryptoExtensions.getJCEProviderName() : encProvider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DirectRecipientInformation createInstance(RecipientInformation recipient, SMIMEEnveloped env) 
	{
		try
		{
			if (recipient == null)
				return null;
			final Field infoField = recipient.getClass().getDeclaredField("_info");
			infoField.setAccessible(true);
			final Object info = infoField.get(recipient);
			
			if (!(info instanceof KeyTransRecipientInfo))
			{
				// fall back to the default if we don't know how to handle this info type
				return new DefaultDirectRecipientInformation(recipient, "");
			}
			
			final Field encAlgField = CMSEnvelopedData.class.getDeclaredField("encAlg");
			encAlgField.setAccessible(true);
			final Object encAlg = encAlgField.get(env);
			
			final Field dataField = RecipientInformation.class.getDeclaredField("_data");
			dataField.setAccessible(true);
			final Object data = dataField.get(recipient);
			
			return new SplitDirectRecipientInformation((KeyTransRecipientInfo)info, (AlgorithmIdentifier)encAlg, (InputStream)data, encProvider, keyEncProvider);
		}
		catch (Exception e)
		{
			throw new IllegalStateException("RecipientInformation configuration is not recognized: " + e.getMessage(), e);
		}
	}
	
	
}
