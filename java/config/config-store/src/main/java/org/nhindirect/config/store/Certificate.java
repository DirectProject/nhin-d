/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
   in the documentation and/or other materials provided with the distribution.  
3. Neither the name of the The NHIN Direct Project (nhindirect.org) nor the names of its contributors may be used to endorse or promote 
   products derived from this software without specific prior written permission.
   
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.nhindirect.config.store;

import java.io.ByteArrayInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Enumeration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Entity
@Table(name = "certificate")
/**
 * The JPA Certificate class
 */
public class Certificate {

	static
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}	
	
    private static final Log log = LogFactory.getLog(Certificate.class);

    public static final byte[] NULL_CERT = new byte[] {};

    private String owner;
    private String thumbprint;
    private long id = 0L;
    private byte[] data;
    private Calendar createTime;
    private Calendar validStartDate;
    private Calendar validEndDate;
    private EntityStatus status;
    private boolean privateKey;

    /**
     * Get the value of owner.
     * 
     * @return the value of owner.
     */
    @Column(name = "owner")
    public String getOwner() {
        return owner;
    }

    /**
     * Set the value of owner.
     * 
     * @param owner
     *            The value of owner.
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Get the value of data.
     * 
     * @return the value of data.
     */
    @Column(name = "certificateData",  length=4096)
    @Lob
    public byte[] getData() {
        return data;
    }

    /**
     * Set the value of data.
     * 
     * @param data
     *            The value of data.
     * @throws CertificateException
     */
    public void setData(byte[] data) throws CertificateException {
        this.data = data;
        if (data == NULL_CERT) {
            setThumbprint("");
        } else {
            loadCertFromData();
        }
    }

    /**
     * Indicates if the certificate has a private key
     * 
     * @return 
     */
    @Column(name = "privateKey")
    public boolean isPrivateKey() {
        return privateKey;
    }

    /**
     * Indicates if the certificate has a private key
     * 
     * @param data
     *            
     * @throws CertificateException
     */
    public void setPrivateKey(boolean b) throws CertificateException {
        this.privateKey = b;
    }    
    
    private void setThumbprint(String aThumbprint) {
        thumbprint = aThumbprint;

    }

    /**
     * Get the value of thumbprint.
     * 
     * @return the value of thumbprint.
     */
    @Column(name = "thumbprint")
    public String getThumbprint() {
        return thumbprint;
    }

    /**
     * Get the value of id.
     * 
     * @return the value of id.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() {
        return id;
    }

    /**
     * Set the value of id.
     * 
     * @param id
     *            The value of id.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the value of createTime.
     * 
     * @return the value of createTime.
     */
    @Column(name = "createTime")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getCreateTime() {
        return createTime;
    }

    /**
     * Set the value of createTime.
     * 
     * @param timestamp
     *            The value of createTime.
     */
    public void setCreateTime(Calendar timestamp) {
        createTime = timestamp;
    }

    /**
     * Get the value of status.
     * 
     * @return the value of status.
     */
    @Column(name = "status")
    @Enumerated
    public EntityStatus getStatus() {
        return status;
    }

    /**
     * Set the value of status.
     * 
     * @param status
     *            The value of status.
     */
    public void setStatus(EntityStatus status) {
        this.status = status;
    }

    /**
     * Get the value of validStartDate.
     * 
     * @return the value of validStartDate.
     */
    @Column(name = "validStartDate")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getValidStartDate() {
        return validStartDate;
    }

    /**
     * Set the value of validStartDate.
     * 
     * @param validStartDate
     *            The value of validStartDate.
     */
    public void setValidStartDate(Calendar validStartDate) {
        this.validStartDate = validStartDate;
    }

    /**
     * Get the value of validEndDate.
     * 
     * @return the value of validEndDate.
     */
    @Column(name = "validEndDate")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getValidEndDate() {
        return validEndDate;
    }

    /**
     * Set the value of validEndDate.
     * 
     * @param validEndDate
     *            The value of validEndDate.
     */
    public void setValidEndDate(Calendar validEndDate) {
        this.validEndDate = validEndDate;
    }

    /**
     * Validate the Certificate for the existance of data.
     * 
     * @throws CertificateException
     */
    public void validate() throws CertificateException {
        if (!hasData()) {
            throw new CertificateException("Invalid Certificate: no certificate data exists");
        }
    }

    private boolean hasData() {
        return ((data != null) && (!data.equals(NULL_CERT))) ? true : false;
    }

    /**
     * Clear the data of a Certificate.
     */
    public void clearData() {
        try {
            setData(NULL_CERT);
        } catch (CertificateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void loadCertFromData() throws CertificateException {
        X509Certificate cert = null;
        try {
            validate();
            CertContainer container = toCredential();
            cert = container.getCert();
            setThumbprint(Thumbprint.toThumbprint(cert).toString());
            setPrivateKey(container.getKey() != null);
        } catch (Exception e) {
            setData(NULL_CERT);
            throw new CertificateException("Data cannot be converted to a valid X.509 Certificate", e);
        }
    }
    
    public CertContainer toCredential() throws CertificateException 
    {
    	CertContainer certContainer = null;
        try 
        {
            validate();
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            
            // lets try this a as a PKCS12 data stream first
            try
            {
            	KeyStore localKeyStore = KeyStore.getInstance("PKCS12", "BC");
            	
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
    					certContainer = new CertContainer(cert, key);
    					
    				}
        		}
            }
            catch (Exception e)
            {
            	// must not be a PKCS12 stream, go on to next step
            }
   
            if (certContainer == null)            	
            {
            	//try X509 certificate factory next       
                bais.reset();
                bais = new ByteArrayInputStream(data);

            	X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(bais);
            	certContainer = new CertContainer(cert, null);
            }
            bais.close();
        } 
        catch (Exception e) 
        {
            throw new CertificateException("Data cannot be converted to a valid X.509 Certificate", e);
        }
        
        return certContainer;
    }
    
    public static class CertContainer
    {
		private final X509Certificate cert;
    	private final Key key;
    	
    	public CertContainer(X509Certificate cert, Key key)
    	{
    		this.cert = cert;
    		this.key = key;
    	}
    	
    	public X509Certificate getCert() 
    	{
			return cert;
		}

		public Key getKey() 
		{
			return key;
		}
	
    }
}
