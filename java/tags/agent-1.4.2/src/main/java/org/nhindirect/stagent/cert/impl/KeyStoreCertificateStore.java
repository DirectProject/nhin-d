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

package org.nhindirect.stagent.cert.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.stagent.NHINDException;
import org.nhindirect.stagent.cert.CertificateStore;
import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.cert.impl.annotation.CertStoreKeyFile;
import org.nhindirect.stagent.cert.impl.annotation.CertStoreKeyFilePassword;
import org.nhindirect.stagent.cert.impl.annotation.CertStoreKeyFilePrivKeyPassword;

import com.google.inject.Inject;
import com.google.inject.internal.Nullable;

/**
 * Certificate storage using a Java keystore file.  If the keystore file does not exist, the service will automatically
 * generate a new file using the private keystore password.  After a KeyStoreCertificateService object is created, it
 * is initialized by calling {@link KeyStoreCertificateStore#loadKeyStore()}
 * @author Greg Meyer
 *
 */
public class KeyStoreCertificateStore extends CertificateStore 
{
	private Set<X509Certificate> certs = new HashSet<X509Certificate>();
	
	private static final Log LOGGER = LogFactory.getFactory().getInstance(KeyStoreCertificateStore.class);
	
	/*
	 * TODO: change the way the passwords and the keystore are held
	 */
	private File keyStoreFile;
	private String keyStorePassword;
	private String privateKeyPassword;
	private KeyStore ks;
	
	/**
	 * Constructs an uninitialized key store 
	 */
	public KeyStoreCertificateStore()
	{
	}

	/**
	 * Constructs a keystore using the provided file.
	 * @param keyStoreFile The file that contains the keystore.   
	 */
	public KeyStoreCertificateStore(File keyStoreFile)
	{
		this(keyStoreFile, null, null);
	}

	/**
	 * Constructs a keystore using the provided file and password that protects the keystore.
	 * @param keyStoreFile The file that contains the keystore.
	 * @param keyStorePassword The password that protects the keystores contents.    
	 */	
	public KeyStoreCertificateStore(File keyStoreFile, String keyStorePassword)
	{
		this(keyStoreFile, keyStorePassword, null);
	}

	/**
	 * Constructs a keystore using the provided file name, file password, and private key password.
	 * @param keyStoreFile The file name that contains the keystore.
	 * @param keyStorePassword The password that protects the keystores contents.
	 * @param privateKeyPassword The password used to retrive privates keys within the keystore.
	 */		
	@Inject
	public KeyStoreCertificateStore(@CertStoreKeyFile String keyStoreFileName, 
			@Nullable @CertStoreKeyFilePassword String keyStorePassword, @Nullable @CertStoreKeyFilePrivKeyPassword String privateKeyPassword)
	{
		this.keyStoreFile = new File(keyStoreFileName);
		this.keyStorePassword = keyStorePassword;
		this.privateKeyPassword = privateKeyPassword;
		
		if (keyStoreFile == null)
    		throw new IllegalArgumentException();

		bootstrapFromFile();
	}	
	
	/**
	 * Constructs a keystore using the provided file, file password, and private key password.
	 * @param keyStoreFile The file that contains the keystore.
	 * @param keyStorePassword The password that protects the keystores contents.
	 * @param privateKeyPassword The password used to retrive privates keys within the keystore.
	 */		
	public KeyStoreCertificateStore(File keyStoreFile, String keyStorePassword,  String privateKeyPassword)
	{
		this.keyStoreFile = keyStoreFile;
		this.keyStorePassword = keyStorePassword;
		this.privateKeyPassword = privateKeyPassword;
		
		if (keyStoreFile == null)
    		throw new IllegalArgumentException();

		bootstrapFromFile();
	}
	
	/**
	 * Sets the keystore file.
	 * @param Tl the keystore file.
	 */
	public void setKeyStoreFile(File fl)
	{
		if (ks != null)
			throw new IllegalStateException();			
		
		keyStoreFile = fl;
	}

	/**
	 * Sets the keystore file using fully qualified file path name.
	 * @param Tl the keystore file path name.
	 */
	public void setKeyStoreFile(String fileName)
	{
		if (ks != null)
			throw new IllegalStateException();		
		
		setKeyStoreFile(new File(fileName));
	}
	
	/**
	 * Sets the password used to open and protect the keystore.
	 * @param password The keystore password.
	 */
	public void setKeyStorePassword(String password)
	{
		if (ks != null)
			throw new IllegalStateException();
		
		this.keyStorePassword = password;
	}
	
	/**
	 * Sets the password used to retrieve private keys in the keystore.
	 * @param privateKeyPassword The private key password.
	 */
	public void setPrivateKeyPassword(String privateKeyPassword)
	{
		if (ks != null)
			throw new IllegalStateException();
		
		this.privateKeyPassword = privateKeyPassword;
	}
	
	/**
	 * Loads and initializes the service from an underlying keystore file.  If the specified file does not exist,
	 * then a new file is created using the keystore file and the keystore password if one has been provided. 
	 */
	public void loadKeyStore()
	{
		if (ks != null)
			throw new IllegalStateException();
		
		bootstrapFromFile();
	}
	
	private void bootstrapFromFile()
	{
		try
		{
			ks = KeyStore.getInstance(KeyStore.getDefaultType());
			 
			if (!keyStoreFile.exists())
			{
				// create a new keystore file
				ks.load(null, keyStorePassword == null ? null : keyStorePassword.toCharArray());
				
				FileOutputStream outStream = new FileOutputStream(keyStoreFile);
				ks.store(outStream, keyStorePassword == null ? null : keyStorePassword.toCharArray());

				IOUtils.closeQuietly(outStream);
			}
			else
			{
				// load from keystore file
				FileInputStream inStream = new FileInputStream(keyStoreFile);
				
				ks.load(inStream, keyStorePassword == null ? null : keyStorePassword.toCharArray());
				
				IOUtils.closeQuietly(inStream);	
				
				Enumeration<String> aliases = ks.aliases();
				
				while (aliases.hasMoreElements())
				{
					String alias = aliases.nextElement();
					
					Certificate cert = ks.getCertificate(alias);
					if (cert != null && cert instanceof X509Certificate)
					{
						X509Certificate addCert;
						
						// check if there is private key
						Key key = ks.getKey(alias, privateKeyPassword == null ? null : privateKeyPassword.toCharArray());
						if (key != null && key instanceof PrivateKey)
							addCert = X509CertificateEx.fromX509Certificate((X509Certificate)cert, (PrivateKey)key);
						else
							addCert = (X509Certificate)cert;
						
						certs.add(addCert);
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new NHINDException(e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
    public boolean contains(X509Certificate cert)
    {
    	return certs.contains(cert);
    }


	/**
	 * {@inheritDoc}
	 */
    public void add(X509Certificate cert)
    {
		String newAlias = cert.getIssuerX500Principal().getName() + ":" + cert.getIssuerX500Principal().getName();   									

		add(cert, newAlias);
    }
    
	/**
	 * {@inheritDoc}
	 */    
    public void remove(X509Certificate cert)
    {
    	if (certs.remove(cert))
    	{
    		// remove from the key store
    		try
    		{
    			String alias = ks.getCertificateAlias(cert);
    			if (alias != null)
    			{
    				ks.deleteEntry(alias);
    				// persist
    				FileOutputStream outStream = new FileOutputStream(keyStoreFile);
    				ks.store(outStream, keyStorePassword == null ? null : keyStorePassword.toCharArray());	
    				
    				IOUtils.closeQuietly(outStream);	
    			}
    		}
    		catch (Exception e)
    		{
    			LOGGER.warn("Error attempting to remove certificate: " + e.getMessage());
    		}
    	}
    }

	/**
	 * {@inheritDoc}
	 */
    public Collection<X509Certificate> getAllCertificates()
    {
    	// internal operations may iterate the returned collection for modifiable operations, so create a copy of the list
    	// instead of an unmodifiable collection backed by the set
    	return new ArrayList<X509Certificate>(certs);
    }
    
    /**
     * Adds a certificate into the keystore with a given alias name.
     * @param cert The certificate to add to the keystore.
     * @param alias The alias of the certificate.
     */
    public void add(X509Certificate cert, String alias)
    {
    	if (certs.contains(cert))
    	{
    		LOGGER.warn("Certificate already exists in store.  Use update() instead.");
    		return;
    	}
    	
		try
		{
			certs.add(cert);
			if (cert instanceof X509CertificateEx)
				ks.setKeyEntry(alias, ((X509CertificateEx)cert).getPrivateKey(),
						privateKeyPassword == null ? null : privateKeyPassword.toCharArray(), new Certificate[] {cert});
			else
				ks.setCertificateEntry(alias, cert);
			
			// persist
			FileOutputStream outStream = new FileOutputStream(keyStoreFile);
			ks.store(outStream, keyStorePassword == null ? null : keyStorePassword.toCharArray());	
			IOUtils.closeQuietly(outStream);
		}
		catch (Throwable e)
		{
			LOGGER.warn("Error adding certificate to store: " + e.getMessage());
		}     		
    }
    
    /**
     * Updates/replaces a certificate into the keystore with a given alias name.
     * @param cert The certificate to update in the keystore.
     * @param alias The alias of the certificate.
     */
    
    public void update(X509Certificate cert, String alias)
    {
        if (contains(cert))
        {
            remove(cert);
        }
        add(cert, alias);
    }
    
    /**
     * Gets a certificate in the keystore with a given alias name.
     * @param alias The alias of the certificate.  Returns null if a certificate with the alias name does not exist in the keystore.
     */    
    public X509Certificate getByAlias(String alias)
    {
    	X509Certificate retVal = null;
    	Certificate cert = null;
    	
    	try
    	{
    		cert = ks.getCertificate(alias);
			if (cert != null && cert instanceof X509Certificate)
			{
				
				// check if there is private key
				Key key = ks.getKey(alias, privateKeyPassword == null ? null : privateKeyPassword.toCharArray());
				if (key != null && key instanceof PrivateKey)
					retVal = X509CertificateEx.fromX509Certificate((X509Certificate)cert, (PrivateKey)key);
				else
					retVal = (X509Certificate)cert;				
			}    		    		
    	}
    	catch (Exception e)
    	{
    		throw new NHINDException(e);
    	}
    	
    	return retVal;
    }  
    
}
