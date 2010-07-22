package org.nhindirect.stagent.cert.impl;

import java.security.cert.X509Certificate;

import javax.mail.internet.InternetAddress;

import org.nhindirect.stagent.cert.ICertificateService;
import org.nhindirect.stagent.cert.X509CertificateEx;

public class UniformCertificateService extends UniformCertificateStore implements ICertificateService 
{
    public UniformCertificateService(X509Certificate cert)
    {
    	super(cert);
    }
    
    @Override
	public X509Certificate getCertificate()
	{
	        return super.getCertificate();
    }

    @Override
    public void setCertificate(X509Certificate value)
	{
        if (value == null || !(value instanceof X509CertificateEx))
        {
            throw new IllegalArgumentException("Not X509CertificateEx instance");
        }

        X509CertificateEx cert = (X509CertificateEx)value;         
        
        if (!cert.hasPrivateKey())
        {
            throw new IllegalArgumentException("No private key in cert");
        }

        super.setCertificate(value);
	}

	public X509CertificateEx getPrivateCertificate(InternetAddress address)
	{
	    if (address == null)
	    {
	        throw new IllegalArgumentException();
	    }
	    
	    return (X509CertificateEx)this.getCertificate();
	}
}
