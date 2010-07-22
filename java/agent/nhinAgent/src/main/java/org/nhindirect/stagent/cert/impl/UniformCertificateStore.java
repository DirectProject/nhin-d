package org.nhindirect.stagent.cert.impl;

import java.security.cert.X509Certificate;

import javax.mail.internet.InternetAddress;

import org.nhindirect.stagent.cert.ICertificateStore;

public class UniformCertificateStore implements ICertificateStore 
{
    X509Certificate m_cert;

    public UniformCertificateStore(X509Certificate cert)
    {
        this.setCertificate(cert);
    }

    public X509Certificate getCertificate()
    {
            return this.m_cert;
    }
        
    public void setCertificate(X509Certificate value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException();
        }

        this.m_cert = value;
    }

    public X509Certificate getCertificate(InternetAddress address)
    {
        if (address == null)
        {
            throw new IllegalArgumentException();
        }

        return this.m_cert;
    }
}
