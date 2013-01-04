package org.nhindirect.config.store;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "trustbundleanchor")
public class TrustBundleAnchor 
{
	private long id;
	private TrustBundle trustBundle;
    private byte[] anchorData;
    private String thumbprint;
    private Calendar validStartDate;
    private Calendar validEndDate;
    
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
    
    /**
     * Get the value of thumbprint.
     * 
     * @return the value of thumbprint.
     */
    @Column(name = "thumbprint", nullable = false)
    public String getThumbprint() 
    {
        return thumbprint;
    }

    /**
     * Set the value of thumbprint.
     * 
     * @param thumbprint
     *            The value of thumbprint.
     */
    public void setThumbprint(String thumbprint) 
    {
        this.thumbprint = thumbprint;
    }    
    
    /**
     * Get the value of the trust bundle.
     * 
     * @return the value of trust bundle.
     */
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "trustBundleId")
    public TrustBundle getTrustBundle() 
    {
        return trustBundle;
    }

    /**
     * Set the value of the trust bundle.
     * 
     * @param trustBundle
     *            The value of the trust bundle.
     */
    public void setTrustBundle(TrustBundle trustBundle) 
    {
        this.trustBundle = trustBundle;

    }
    
    /**
     * Get the value of anchorData.
     * 
     * @return the value of anchorData Data.
     */
    @Column(name = "anchorData", length=4096, nullable = false)
    @Lob
    public byte[] getData() 
    {
        return anchorData;
    }

    /**
     * Set the value of certificateData.
     * 
     * @param data
     *            The value of certificateData.
     * @throws CertificateException
     */
    public void setData(byte[] data) throws CertificateException 
    {
    	anchorData = data;
        if (data == Certificate.NULL_CERT) 
        {
            setThumbprint("");
        } 
        else 
        {
            loadCertFromData();
        }
    }    
    
    /**
     * Get the value of validStartDate.
     * 
     * @return the value of validStartDate.
     */
    @Column(name = "validStartDate", nullable = false)    
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getValidStartDate() 
    {
        return validStartDate;
    }

    /**
     * Set the value of validStartDate.
     * 
     * @param validStartDate
     *            The value of validStartDate.
     */
    public void setValidStartDate(Calendar validStartDate) 
    {
        this.validStartDate = validStartDate;
    }

    /**
     * Get the value of validEndDate.
     * 
     * @return the value of validEndDate.
     */
    @Column(name = "validEndDate", nullable = false)    
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getValidEndDate() 
    {
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
    
    private X509Certificate loadCertFromData() throws CertificateException 
    {
        X509Certificate cert = null;
        try {
            validate();
            final ByteArrayInputStream bais = new ByteArrayInputStream(anchorData);
            cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(bais);
            setThumbprint(Thumbprint.toThumbprint(cert).toString());
            
            final Calendar calEndTime = Calendar.getInstance(Locale.getDefault());
            calEndTime.setTime(cert.getNotAfter());
            final Calendar calStartTime = Calendar.getInstance(Locale.getDefault());
            calStartTime.setTime(cert.getNotBefore());
            
            this.setValidEndDate(calEndTime);
            this.setValidStartDate(calStartTime);
            bais.close();
        } catch (Exception e) {
            setData(Certificate.NULL_CERT);
            throw new CertificateException("Data cannot be converted to a valid X.509 Certificate", e);
        }
        
        return cert;
    }

    public X509Certificate toCertificate() throws CertificateException 
    {
        X509Certificate cert = null;
        try 
        {
            validate();
            final ByteArrayInputStream bais = new ByteArrayInputStream(anchorData);
            cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(bais);
            bais.close();
        } 
        catch (Exception e) 
        {
            throw new CertificateException("Data cannot be converted to a valid X.509 Certificate", e);
        }
        
        return cert;
    }   
    
    private boolean hasData()
    {
        return ((anchorData != null) && (!anchorData.equals(Certificate.NULL_CERT))) ? true : false;
    }
    
    /**
     * Validate the Anchor for the existance of data.
     * 
     * @throws CertificateException
     */
    public void validate() throws CertificateException 
    {
        if (!hasData()) 
        {
            throw new CertificateException("Invalid Certificate: no certificate data exists");
        }
    }    
}
