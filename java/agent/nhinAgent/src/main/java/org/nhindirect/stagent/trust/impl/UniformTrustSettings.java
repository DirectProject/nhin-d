package org.nhindirect.stagent.trust.impl;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;

import javax.mail.internet.InternetAddress;

import org.nhindirect.stagent.trust.ITrustSettingsStore;

/**
 * {@link ITrustSettingsStore} implementation that applies the same trust anchors to all InternetAddresses.
 * @author Greg Meyer
 * @author Umesh Madan
 */
public class UniformTrustSettings implements ITrustSettingsStore
{
    private Collection<X509Certificate> m_anchors; 
    
    /**
     * Constructs a store that contains one anchor.
     * @param anchor The anchor in the store.
     */
    public UniformTrustSettings(X509Certificate anchor)
    {
    	Collection<X509Certificate> anchors = new ArrayList<X509Certificate>();
    	anchors.add(anchor);
        this.setAnchors(anchors);
    }
    
    /**
     * Constructs a store with a collection of certificate anchors.
     * @param anchor The anchor in the store.
     */
    public UniformTrustSettings(Collection<X509Certificate> anchors)
    {
        this.setAnchors(anchors);
    }
    
    /**
     * Gets the collection of anchors in the store.
     * @return The collection of anchors in the store.
     */
    public Collection<X509Certificate> getAnchors()
    {
        return this.m_anchors;
    }
       
    /**
     * Sets the collection of anchors in the store.
     * @param value The collection of anchors that will be in the store.
     */
    public void setAnchors(Collection<X509Certificate> value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException();
        }
        
        this.m_anchors = value;
    }
        
    /**
     * Because this implementation applies all anchors regardless of the the address, this effectively gets all anchors in the store.
     * {@inheritDoc}} 
     */
    public Collection<X509Certificate> getTrustAnchorsIncoming(InternetAddress address)
    {
    	return this.m_anchors;
    }

    /**
     * Because this implementation applies all anchors regardless of the the address, this effectively gets all anchors in the store.
     * {@inheritDoc}} 
     */    
    public Collection<X509Certificate> getTrustAnchorsOutgoing(InternetAddress address)
    {
    	return this.m_anchors;
    }  
}
