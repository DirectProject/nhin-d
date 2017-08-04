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

package org.nhindirect.gateway.smtp.config.cert.impl;

import java.io.ByteArrayInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.common.crypto.KeyStoreProtectionManager;
import org.nhindirect.common.crypto.MutableKeyStoreProtectionManager;
import org.nhindirect.config.model.utils.CertUtils;
import org.nhindirect.config.model.utils.CertUtils.CertContainer;
import org.nhindirect.stagent.AgentError;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.NHINDException;
import org.nhindirect.stagent.cert.WrappedOnDemandX509CertificateEx;
import org.nhindirect.stagent.cert.X509CertificateEx;

/**
 * Utility methods for working with certificate stores and certificate data.
 * @author Greg Meyer
 *
 * @since 4.2
 */
public class CertStoreUtils
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(CertStoreUtils.class);
	
    public static X509Certificate certFromData(KeyStoreProtectionManager mgr,  byte[] data)
    {
    	X509Certificate retVal = null;
        try 
        {
        	// first check for wrapped data
        	final CertContainer container = CertUtils.toCertContainer(data);
        	if (container.getWrappedKeyData() != null)
        	{
        		// this is a wrapped key
        		// make sure we have a KeyStoreManager configured
        		if (mgr == null)
        		{
        			throw new NHINDException(AgentError.Unexpected,  
        					"Resolved certifiate has wrapped data, but resolver has not been configured to unwrap it.");
        		}
        		
        		// create a new wrapped certificate object
        		retVal = WrappedOnDemandX509CertificateEx.fromX509Certificate(mgr, container.getCert(), container.getWrappedKeyData());
        		return retVal;
        	}
        	
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            
            // lets try this a as a PKCS12 data stream first
            try
            {
            	KeyStore localKeyStore = KeyStore.getInstance("PKCS12", CryptoExtensions.getJCEProviderName());
            	
            	localKeyStore.load(bais, "".toCharArray());
            	Enumeration<String> aliases = localKeyStore.aliases();


        		// we are really expecting only one alias 
        		if (aliases.hasMoreElements())        			
        		{
        			String alias = aliases.nextElement();
        			X509Certificate cert = (X509Certificate)localKeyStore.getCertificate(alias);
        			
    				// check if there is private key
    				Key key = localKeyStore.getKey(alias, "".toCharArray());
    				if (key != null && key instanceof PrivateKey) 
    				{
    					retVal = X509CertificateEx.fromX509Certificate(cert, (PrivateKey)key);
    				}
    				else
    					retVal = cert;
    					
        		}
            }
            catch (Exception e)
            {
            	// must not be a PKCS12 stream, go on to next step
            }
   
            if (retVal == null)            	
            {
            	//try X509 certificate factory next       
                bais.reset();
                bais = new ByteArrayInputStream(data);

                retVal = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(bais);            	
            }
            bais.close();
            
            // if this is a certificate an no private key, and the keystore manager is configured,
            // look in the keystore manager to check if they private key is store in the token
            if (mgr != null && !(retVal instanceof X509CertificateEx))
            {
            	// make sure this a mutable manager
            	if (mgr instanceof MutableKeyStoreProtectionManager)
            	{
            		try
            		{
	            		final KeyStore ks = ((MutableKeyStoreProtectionManager)mgr).getKS();
	            		// check to see if this certificate exists in the key store
	            		final String alias = ks.getCertificateAlias(retVal);
	            		if (!StringUtils.isEmpty(alias))
	            		{
	            			// get the private key if it exits
	            			final PrivateKey pKey = (PrivateKey)ks.getKey(alias, "".toCharArray());
	            			if (pKey != null)
	            				retVal = X509CertificateEx.fromX509Certificate(retVal, pKey);
	            		}
            		}
            		catch (Exception e)
            		{
            			LOGGER.warn("Could not retrieve the private key from the PKCS11 token: " + e.getMessage(), e);
            		}
            	}
            }
            
        } 
        catch (Exception e) 
        {
            throw new NHINDException("Data cannot be converted to a valid X.509 Certificate", e);
        }
        
        return retVal;
    }
    
}
