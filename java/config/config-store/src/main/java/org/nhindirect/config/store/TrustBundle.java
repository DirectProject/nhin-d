package org.nhindirect.config.store;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "trustbundle")
public class TrustBundle 
{
	private long id;
	private String bundleName;
	private String bundleURL;
    private byte[] signingCertificateData;
    private Collection<TrustBundleAnchor> trustBundleAnchors;
    private int refreshInterval;
    private Calendar lastRefreshAttempt;
    private BundleRefreshError lastRefreshError;
    private Calendar lastSuccessfulRefresh;    
    private Calendar createTime;  
    private String checkSum;
    
    public TrustBundle()
    {
    	refreshInterval = 0;
    }
    
    /**
     * Get the value of id.
     * 
     * @return the value of id.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() 
    {
        return id;
    }

    /**
     * Set the value of id.
     * 
     * @param id
     *            The value of id.
     */
    public void setId(long id) 
    {
        this.id = id;
    } 
    
    @Column(name = "bundleName", unique = true, nullable = false)
    public String getBundleName()
    {
    	return bundleName;
    }
    
    public void setBundleName(String bundleName)
    {
    	this.bundleName = bundleName;
    }
    
    @Column(name = "bundleURL", nullable = false)
    public String getBundleURL()
    {
    	return bundleURL;
    }
    
    public void setBundleURL(String bundleURL)
    {
    	this.bundleURL = bundleURL;
    }    
    
    @Column(name = "signingCertificateData", length=4096)
    @Lob
    public byte[] getSigningCertificateData() 
    {
        return signingCertificateData;
    }

    public void setSigningCertificateData(byte[] signingCertificateData) throws CertificateException 
    {
    	this.signingCertificateData = signingCertificateData;
    }    

    @Column(name = "refreshInterval")
    public int getRefreshInterval() 
    {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval) 
    {
    	this.refreshInterval = refreshInterval;
    } 
    
    /**
     * Get the value of createTime.
     * 
     * @return the value of createTime.
     */
    @Column(name = "createTime", nullable = false)    
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getCreateTime() 
    {
        return createTime;
    }

    /**
     * Set the value of createTime.
     * 
     * @param timestamp
     *            The value of createTime.
     */
    public void setCreateTime(Calendar timestamp) 
    {
        createTime = timestamp;
    }    

    @Column(name = "lastSuccessfulRefresh")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getLastSuccessfulRefresh() 
    {
        return lastSuccessfulRefresh;
    }

    public void setLastSuccessfulRefresh(Calendar lastSuccessfulRefresh) 
    {
        this.lastSuccessfulRefresh = lastSuccessfulRefresh;
    }  
    
    @Column(name = "lastRefreshAttempt")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getLastRefreshAttempt() 
    {
        return lastRefreshAttempt;
    }

    public void setLastRefreshAttempt(Calendar lastRefreshAttempt) 
    {
        this.lastRefreshAttempt = lastRefreshAttempt;
    }  
    
    @Column(name = "lastRefreshError")
    @Enumerated
    public BundleRefreshError getLastRefreshError() 
    {
        return lastRefreshError;
    }

    public void setLastRefreshError(BundleRefreshError lastRefreshError) 
    {
    	this.lastRefreshError = lastRefreshError;
    } 
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "trustBundle")
    public Collection<TrustBundleAnchor> getTrustBundleAnchors() 
    {
        if (trustBundleAnchors == null) 
        {
        	trustBundleAnchors = new ArrayList<TrustBundleAnchor>();
        }
        return trustBundleAnchors;
    }
    
    public void setTrustBundleAnchors(Collection<TrustBundleAnchor> trustBundleAnchors) 
    {
        this.trustBundleAnchors = trustBundleAnchors;
    }
    
    @Column(name = "getCheckSum", nullable = false)
    public String getCheckSum()
    {
    	return checkSum;
    }
    
    public void setCheckSum(String checkSum)
    {
    	this.checkSum = checkSum;
    }
    
    public X509Certificate toSigningCertificate() throws CertificateException 
    {
        X509Certificate cert = null;
        try 
        {
            validate();
            ByteArrayInputStream bais = new ByteArrayInputStream(signingCertificateData);
            cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(bais);
            bais.close();
        } 
        catch (Exception e) 
        {
            throw new CertificateException("Data cannot be converted to a valid X.509 Certificate", e);
        }
        
        return cert;
    } 
    
    public void validate() throws CertificateException 
    {
        if (!hasData()) 
        {
            throw new CertificateException("Invalid Certificate: no certificate data exists");
        }
    } 
    
    private boolean hasData()
    {
        return ((signingCertificateData != null) && (!signingCertificateData.equals(Certificate.NULL_CERT))) ? true : false;
    }
}
