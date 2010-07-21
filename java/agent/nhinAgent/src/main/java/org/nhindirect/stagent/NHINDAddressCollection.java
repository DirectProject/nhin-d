package org.nhindirect.stagent;

import java.util.ArrayList;
import java.util.Collection;

import javax.mail.internet.InternetAddress;

import org.nhindirect.stagent.trust.TrustEnforcementStatus;

import java.security.cert.X509Certificate;

/**
 * A collection of NHINDAddresses.
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class NHINDAddressCollection extends ArrayList<NHINDAddress> 
{
	static final long serialVersionUID = -2750152014905400257L;	
	
    public static final TrustEnforcementStatus DefaultMinTrustStatus = TrustEnforcementStatus.Success_Offline;
    
    /**
     * Constructs an empty collection.
     */
    public NHINDAddressCollection()
    {
    }
    
    /**
     * Gets a collection of all certificates associated with all of the addresses in the collection.
     * @return A collection of all certificates associated with all of the addresses in the collection.
     */
    public Collection<X509Certificate> getCertificates()
    {
    	Collection<X509Certificate> certs = new ArrayList<X509Certificate>();
        
    	for (NHINDAddress add : this)
    		if (add.hasCertificate())
    			certs.add(add.getCertificate());
    	
    	return certs;
    }
    
    /**
     * Gets the first available certificate the certificate collection.  This is generally used to choose a certificate for validating a message signature.
     * @return The first available certificate the certificate collection.
     */
    public X509Certificate getFirstCertificate()
    {
    	for (NHINDAddress add : this)
    		if (add.hasCertificate())
    			return add.getCertificate();
    	
    	return null;
    }
            
    /**
     * Gets all addresses in the collection that are trusted.
     * @return All addresses in the collection that are trusted.
     */
    public Collection<NHINDAddress> getTrusted()
    {
        return this.getTrusted(NHINDAddressCollection.DefaultMinTrustStatus);
    }

    /**
     * Gets all addresses in the collection that meet the minimum trust status.
     * @param minTrustStatus The minimum trust status.
     * @return All addresses in the collection that are trusted.
     */
    public Collection<NHINDAddress> getTrusted(TrustEnforcementStatus minTrustStatus)
    {
    	Collection<NHINDAddress> adds = new ArrayList<NHINDAddress>();
    	
    	for (NHINDAddress add : this)
    		if (add.isTrusted(minTrustStatus))
    			adds.add(add);
    			
    	return adds;
    }
    
    /**
     * Gets all addresses in the collection that are not trusted.
     * @return All addresses in the collection that are not trusted.
     */
    public Collection<NHINDAddress> getUntrusted()
    {
        return this.getUntrusted(NHINDAddressCollection.DefaultMinTrustStatus);
    }

    /**
     * Gets all addresses in the collection that do not meet the minimum trust status.
     * @param minTrustStatus The minimum trust status.
     * @return All addresses in the collection that are not trusted.
     */    
    public Collection<NHINDAddress> getUntrusted(TrustEnforcementStatus minTrustStatus)
    {
    	Collection<NHINDAddress> adds = new ArrayList<NHINDAddress>();
    	
    	for (NHINDAddress add : this)
    		if (!add.isTrusted(minTrustStatus))
    			adds.add(add);
    			
    	return adds;    	
    }
    
    /**
     * Indicates if the collection has any addresses that are trusted.
     * @return True if the collection contains any addresses that are trusted.  False otherwise.
     */
    public boolean isTrusted()
    {
        return this.isTrusted(NHINDAddressCollection.DefaultMinTrustStatus);
    }

    /**
     * Indicates if the collection has any addresses that meet the minimum trust status.
     * @param minTrustStatus The minimum trust status.
     * @return True if the collection contains any addresses that eet the minimum trust status.  False otherwise.
     */
    public boolean isTrusted(TrustEnforcementStatus minTrustStatus)
    {
    	for (NHINDAddress add : this)
    		if (!add.isTrusted(minTrustStatus))
    			return false;
    	
    	return true;    	
    }
    
    /**
     * Removes all addresses from the collection that are note trusted.
     */
    public void removeUntrusted()
    {
        this.removeUntrusted(NHINDAddressCollection.DefaultMinTrustStatus);
    }
    
    /**
     * Removes all addresses from the collection that do not meet the minimum trust status.
     */    
    public void removeUntrusted(TrustEnforcementStatus minTrustStatus)
    {
        // Remove anybody who is not trusted
    	for (int i = this.size() - 1; i >=0; --i)
    		if (!this.get(i).isTrusted(minTrustStatus))
    			this.remove(i);
    }
    
    /**
     * Converts the collection an instance of a Collection<InternetAddress> object.
     * @return
     */
    public Collection<InternetAddress> toInternetAddressCollection()
    {
    	Collection<InternetAddress> retVal = new ArrayList<InternetAddress>();
    	
    	retVal.addAll(this);
    	
    	return retVal;
    	
    }
    
    /**
     * Generates an instance of an NHINDAddressCollection from a collection of NHINDAddress addresses.
     * @param source A collection of NHINDAddress addresses to seed this object with.
     * @return n instance of an NHINDAddressCollection object containing all of the source addresses.
     */
    static NHINDAddressCollection create(Collection<NHINDAddress> source)
    {
        NHINDAddressCollection addresses = new NHINDAddressCollection();
        addresses.addAll(source);
        
        return addresses;
    }   
}
