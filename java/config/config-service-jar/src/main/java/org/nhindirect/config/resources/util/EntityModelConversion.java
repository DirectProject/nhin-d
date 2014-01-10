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

package org.nhindirect.config.resources.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;

import org.nhindirect.config.model.Address;
import org.nhindirect.config.model.Anchor;
import org.nhindirect.config.model.BundleRefreshError;
import org.nhindirect.config.model.CertPolicy;
import org.nhindirect.config.model.CertPolicyGroup;
import org.nhindirect.config.model.CertPolicyGroupDomainReltn;
import org.nhindirect.config.model.CertPolicyGroupUse;
import org.nhindirect.config.model.CertPolicyUse;
import org.nhindirect.config.model.Certificate;
import org.nhindirect.config.model.DNSRecord;
import org.nhindirect.config.model.Domain;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.model.Setting;
import org.nhindirect.config.model.TrustBundle;
import org.nhindirect.config.model.TrustBundleAnchor;
import org.nhindirect.config.model.exceptions.CertificateConversionException;
import org.nhindirect.config.store.CertificateException;
import org.nhindirect.policy.PolicyLexicon;

public class EntityModelConversion 
{
	
	public static org.nhindirect.config.store.Domain toEntityDomain(Domain domain)
	{
		final org.nhindirect.config.store.Domain retVal = new org.nhindirect.config.store.Domain();
		
		final Collection<org.nhindirect.config.store.Address> addresses = new ArrayList<org.nhindirect.config.store.Address>();
		if (domain.getAddresses() != null)
		{
			for (Address address : domain.getAddresses())
			{
				addresses.add(toEntityAddress(address));
			}
		}
		retVal.setAddresses(addresses);
		retVal.setCreateTime(domain.getCreateTime());
		retVal.setDomainName(domain.getDomainName());
		retVal.setId(domain.getId());
		
		if (domain.getPostmasterAddress() != null)
			retVal.setPostMasterEmail(domain.getPostmasterAddress().getEmailAddress());

		
		if (domain.getStatus() != null)
			retVal.setStatus(org.nhindirect.config.store.EntityStatus.valueOf(domain.getStatus().toString()));
		retVal.setUpdateTime(domain.getUpdateTime());
		
		return retVal;
	}
	
	public static Domain toModelDomain(org.nhindirect.config.store.Domain domain)
	{
		final Domain retVal = new Domain();
		
		final Collection<Address> addresses = new ArrayList<Address>();
		if (domain.getAddresses() != null)
		{
			for (org.nhindirect.config.store.Address address : domain.getAddresses())
			{
				addresses.add(toModelAddress(address));
			}
		}
		retVal.setAddresses(addresses);
		retVal.setCreateTime(domain.getCreateTime());
		retVal.setDomainName(domain.getDomainName());
		retVal.setId(domain.getId());

		// get the postmaster address
		if (domain.getPostMasterEmail() != null && !domain.getPostMasterEmail().isEmpty())
		{
			System.out.println("Postmaster email address: " + domain.getPostMasterEmail());
	        if ((domain.getAddresses().size() > 0) && (domain.getPostmasterAddressId() != null) && (domain.getPostmasterAddressId() > 0)) 
	        {
	            for (org.nhindirect.config.store.Address address : domain.getAddresses()) 
	            {
	                if (address.getId().equals(domain.getPostmasterAddressId())) 
	                {
	        			retVal.setPostmasterAddress(toModelAddress(address));
	        			break;
	                }
	            }
	        }			
		}
		
		if (domain.getStatus() != null)
			retVal.setStatus(EntityStatus.valueOf(domain.getStatus().toString()));
		retVal.setUpdateTime(domain.getUpdateTime());
		
		return retVal;
	}
	
	public static Address toModelAddress(org.nhindirect.config.store.Address address)
	{
    	if (address == null)
    		return null;
    	
    	final Address retVal = new Address();
    	retVal.setCreateTime(address.getCreateTime());
    	retVal.setDisplayName(address.getDisplayName());
    	retVal.setEmailAddress(address.getEmailAddress());
    	retVal.setEndpoint(address.getEndpoint());
    	retVal.setId(address.getId());
    	if (address.getStatus() != null)
    		retVal.setStatus(EntityStatus.valueOf(address.getStatus().toString()));
    	retVal.setType(address.getType());
    	retVal.setUpdateTime(address.getUpdateTime());
    	
    	if (address.getDomain() != null)
    		retVal.setDomainName(address.getDomain().getDomainName());
    	
    	return retVal;
	}
	
    public static org.nhindirect.config.store.Address toEntityAddress(Address address)
    {
    	if (address == null)
    		return null;
    	
    	final org.nhindirect.config.store.Address retVal = new org.nhindirect.config.store.Address();
    	retVal.setCreateTime(address.getCreateTime());
    	retVal.setDisplayName(address.getDisplayName());
    	retVal.setEmailAddress(address.getEmailAddress());
    	retVal.setEndpoint(address.getEndpoint());
    	retVal.setId(address.getId());
    	if (address.getStatus() != null)
    		retVal.setStatus(org.nhindirect.config.store.EntityStatus.valueOf(address.getStatus().toString()));
    	retVal.setType(address.getType());
    	retVal.setUpdateTime(address.getUpdateTime());
    	
    	return retVal;
    }
    
    public static Anchor toModelAnchor(org.nhindirect.config.store.Anchor anchor)
    {
    	if (anchor == null)
    		return null;
    	
    	final Anchor retVal = new Anchor();
    	
    	retVal.setCertificateData(anchor.getData());
    	retVal.setCertificateId(anchor.getCertificateId());
    	retVal.setCreateTime(anchor.getCreateTime());
    	retVal.setId(anchor.getId());
    	retVal.setIncoming(anchor.isIncoming());
    	retVal.setOutgoing(anchor.isOutgoing());
    	retVal.setOwner(anchor.getOwner());
    	retVal.setStatus(EntityStatus.valueOf(anchor.getStatus().toString()));
    	retVal.setThumbprint(anchor.getThumbprint());
    	retVal.setValidEndDate(anchor.getValidEndDate());
    	retVal.setValidStartDate(anchor.getValidStartDate());
    	
    	return retVal;
    }
    
    public static org.nhindirect.config.store.Anchor toEntityAnchor(Anchor anchor) throws CertificateException
    {
    	if (anchor == null)
    		return null;
    	
    	final org.nhindirect.config.store.Anchor retVal = new org.nhindirect.config.store.Anchor();
    	
    	retVal.setData(anchor.getCertificateData());
    	retVal.setCertificateId(anchor.getCertificateId());
    	retVal.setCreateTime(anchor.getCreateTime());
    	retVal.setId(anchor.getId());
    	retVal.setIncoming(anchor.isIncoming());
    	retVal.setOutgoing(anchor.isOutgoing());
    	retVal.setOwner(anchor.getOwner());
    	retVal.setStatus(org.nhindirect.config.store.EntityStatus.valueOf(anchor.getStatus().toString()));
    	retVal.setValidEndDate(anchor.getValidEndDate());
    	retVal.setValidStartDate(anchor.getValidStartDate());
    	
    	return retVal;
    }    
    
    public static Certificate toModelCertificate(org.nhindirect.config.store.Certificate cert)
    {
    	if (cert == null)
    		return null;
    	
    	final Certificate retVal = new Certificate();
    	
    	retVal.setOwner(cert.getOwner());
    	retVal.setCreateTime(cert.getCreateTime());
    	retVal.setData(cert.getData());
    	retVal.setId(cert.getId());
    	retVal.setPrivateKey(cert.isPrivateKey());
    	if (cert.getStatus() != null)
    		retVal.setStatus(EntityStatus.valueOf(cert.getStatus().toString()));
    	retVal.setThumbprint(cert.getThumbprint());
    	retVal.setValidEndDate(cert.getValidEndDate());
    	retVal.setValidStartDate(cert.getValidStartDate());

    	
    	return retVal;
    }   
    
    public static org.nhindirect.config.store.Certificate toEntityCertificate(Certificate cert) throws CertificateException
    {
    	if (cert == null)
    		return null;
    	
    	final org.nhindirect.config.store.Certificate retVal = new org.nhindirect.config.store.Certificate();
    	
    	retVal.setOwner(cert.getOwner());
    	retVal.setCreateTime(cert.getCreateTime());
    	retVal.setData(cert.getData());
    	retVal.setId(cert.getId());
    	if (cert.getStatus() != null)
    		retVal.setStatus(org.nhindirect.config.store.EntityStatus.valueOf(cert.getStatus().toString()));
    	
    	final Calendar endDate = Calendar.getInstance(Locale.getDefault());
    	endDate.setTime(retVal.toCredential().getCert().getNotAfter());
    	retVal.setValidEndDate(endDate);
    	
    	final Calendar startDate = Calendar.getInstance(Locale.getDefault());
    	startDate.setTime(retVal.toCredential().getCert().getNotBefore());	
    	retVal.setValidStartDate(startDate);

    	
    	return retVal;
    }    
    
    public static DNSRecord toModelDNSRecord(org.nhindirect.config.store.DNSRecord record)
    {
    	if (record == null)
    		return null;
    	
    	final DNSRecord retVal = new DNSRecord();
    	
    	retVal.setCreateTime(record.getCreateTime());
    	retVal.setData(record.getData());
    	retVal.setDclass(record.getDclass());
    	retVal.setId(record.getId());
    	retVal.setName(record.getName());
    	retVal.setTtl(record.getTtl());
    	retVal.setType(record.getType());
    	
    	return retVal;
    }   
    
    public static org.nhindirect.config.store.DNSRecord toEntityDNSRecord(DNSRecord record)
    {
    	if (record == null)
    		return null;
    	
    	final org.nhindirect.config.store.DNSRecord retVal = new org.nhindirect.config.store.DNSRecord();
    	
    	retVal.setCreateTime(record.getCreateTime());
    	retVal.setData(record.getData());
    	retVal.setDclass(record.getDclass());
    	retVal.setId(record.getId());
    	retVal.setName(record.getName());
    	retVal.setTtl(record.getTtl());
    	retVal.setType(record.getType());
    	
    	return retVal;
    }    
    
    public static Setting toModelSetting(org.nhindirect.config.store.Setting setting)
    {
    	if (setting == null)
    		return null;
    	
    	final Setting retVal = new Setting();
    	
    	retVal.setId(setting.getId());
    	retVal.setName(setting.getName());
    	if (setting.getStatus() != null)
    		retVal.setStatus(EntityStatus.valueOf(setting.getStatus().toString()));
    	retVal.setUpdateTime(setting.getUpdateTime());
    	retVal.setCreateTime(setting.getCreateTime());
    	retVal.setValue(setting.getValue());
    	
    	return retVal;
    }    
    
    public static TrustBundle toModelTrustBundle(org.nhindirect.config.store.TrustBundle bundle)
    {
    	if (bundle == null)
    		return null;
    	
    	final TrustBundle retVal = new TrustBundle();
    	
    	final Collection<TrustBundleAnchor> trustAnchors = new ArrayList<TrustBundleAnchor>();
    	
    	if (bundle.getTrustBundleAnchors() != null)
    	{
    		for (org.nhindirect.config.store.TrustBundleAnchor anchor : bundle.getTrustBundleAnchors())
    		{
    			final TrustBundleAnchor retAnchor = new TrustBundleAnchor();
    			retAnchor.setAnchorData(anchor.getData());
    			retAnchor.setThumbprint(anchor.getThumbprint());
    			retAnchor.setId(anchor.getId());
 
    	    	retAnchor.setValidEndDate(anchor.getValidEndDate());
    	    	retAnchor.setValidStartDate(anchor.getValidStartDate());
    	    	
    	    	trustAnchors.add(retAnchor);
    		}
    	}
    	
    	retVal.setBundleName(bundle.getBundleName());
    	retVal.setBundleURL(bundle.getBundleURL());
    	retVal.setCheckSum(bundle.getCheckSum());
    	retVal.setCreateTime(bundle.getCreateTime());
    	retVal.setId(bundle.getId());
    	retVal.setLastRefreshAttempt(bundle.getLastRefreshAttempt());
    	if (bundle.getLastRefreshAttempt() != null)
    		retVal.setLastRefreshError(BundleRefreshError.valueOf(bundle.getLastRefreshError().toString()));
    	
    	retVal.setLastSuccessfulRefresh(bundle.getLastSuccessfulRefresh());
    	retVal.setRefreshInterval(bundle.getRefreshInterval());
    	retVal.setSigningCertificateData(bundle.getSigningCertificateData());
    	retVal.setTrustBundleAnchors(trustAnchors);
    	return retVal;
    }  
    
    public static org.nhindirect.config.store.TrustBundle toEntityTrustBundle(TrustBundle bundle)
    {
    	if (bundle == null)
    		return null;
    	
    	final org.nhindirect.config.store.TrustBundle retVal = new org.nhindirect.config.store.TrustBundle();
    	
    	final Collection<org.nhindirect.config.store.TrustBundleAnchor> trustAnchors = new ArrayList<org.nhindirect.config.store.TrustBundleAnchor>();
    	
    	if (bundle.getTrustBundleAnchors() != null)
    	{
    		for (TrustBundleAnchor anchor : bundle.getTrustBundleAnchors())
    		{
    			final org.nhindirect.config.store.TrustBundleAnchor retAnchor = new org.nhindirect.config.store.TrustBundleAnchor();
    			try
    			{
    				retAnchor.setData(anchor.getAnchorData());
    			}
    			catch (CertificateException e) 
    			{
    				throw new CertificateConversionException(e);
				}
    			// the entity object sets all other attributes based on the cert data,
    			// no need to explicitly set it here
    	    	retAnchor.setTrustBundle(retVal);
    	    	
    	    	trustAnchors.add(retAnchor);
    		}
    	}
    	
    	retVal.setBundleName(bundle.getBundleName());
    	retVal.setBundleURL(bundle.getBundleURL());
    	
    	if (bundle.getCheckSum() == null)
    		retVal.setCheckSum("");
    	else
    		retVal.setCheckSum(bundle.getCheckSum());
    	
    	retVal.setCreateTime(bundle.getCreateTime());
    	retVal.setId(bundle.getId());
    	retVal.setLastRefreshAttempt(bundle.getLastRefreshAttempt());
    	if (bundle.getLastRefreshError() != null)
    		retVal.setLastRefreshError(org.nhindirect.config.store.BundleRefreshError.valueOf(bundle.getLastRefreshError().toString()));
    	
    	retVal.setLastSuccessfulRefresh(bundle.getLastSuccessfulRefresh());
    	retVal.setRefreshInterval(bundle.getRefreshInterval());
    	if (bundle.getSigningCertificateData() != null)
    	{

    			try 
    			{
					retVal.setSigningCertificateData(bundle.getSigningCertificateData());
				} 
    			catch (CertificateException e) 
    			{
    				throw new CertificateConversionException(e);
				}
    	}
    		retVal.setTrustBundleAnchors(trustAnchors);
    	return retVal;
    }   
    
    public static CertPolicy toModelCertPolicy(org.nhindirect.config.store.CertPolicy policy)
    {
    	if (policy == null)
    		return null;
    	
    	final CertPolicy retVal = new CertPolicy();
    	
    	retVal.setPolicyName(policy.getPolicyName());
    	retVal.setCreateTime(policy.getCreateTime());
    	if (policy.getLexicon() != null)
    		retVal.setLexicon(PolicyLexicon.valueOf(policy.getLexicon().toString()));
    	retVal.setPolicyData(policy.getPolicyData());
    	
    	return retVal;
    } 
    
    public static org.nhindirect.config.store.CertPolicy toEntityCertPolicy(CertPolicy policy)
    {
    	if (policy == null)
    		return null;
    	
    	final org.nhindirect.config.store.CertPolicy retVal = new org.nhindirect.config.store.CertPolicy();
    	
    	retVal.setPolicyName(policy.getPolicyName());
    	retVal.setCreateTime(policy.getCreateTime());
    	if (policy.getLexicon() != null)
    		retVal.setLexicon(PolicyLexicon.valueOf(policy.getLexicon().toString()));
    	retVal.setPolicyData(policy.getPolicyData());
    	
    	return retVal;
    }  
    
    public static CertPolicyGroup toModelCertPolicyGroup(org.nhindirect.config.store.CertPolicyGroup group)
    {
    	if (group == null)
    		return null;
    	
    	final CertPolicyGroup retVal = new CertPolicyGroup();
    	
    	final Collection<CertPolicyGroupUse> uses = new ArrayList<CertPolicyGroupUse>();
    	
    	if (group.getCertPolicyGroupReltn() != null)
    	{
    		for (org.nhindirect.config.store.CertPolicyGroupReltn reltn : group.getCertPolicyGroupReltn())
    		{
    			final CertPolicyGroupUse use = new CertPolicyGroupUse();
    			
    			use.setPolicy(toModelCertPolicy(reltn.getCertPolicy()));
    			if (reltn.getPolicyUse() != null)
    				use.setPolicyUse(CertPolicyUse.valueOf(reltn.getPolicyUse().toString()));
    			use.setIncoming(reltn.isIncoming());
    			use.setOutgoing(reltn.isOutgoing());

    			uses.add(use);
    		}
    	}
    	
    	retVal.setPolicyGroupName(group.getPolicyGroupName());
    	retVal.setCreateTime(group.getCreateTime());
    	retVal.setPolicies(uses);
    	   	
    	return retVal;
    }   
    
    public static org.nhindirect.config.store.CertPolicyGroup toEntityCertPolicyGroup(CertPolicyGroup group)
    {
    	if (group == null)
    		return null;
    	
    	final org.nhindirect.config.store.CertPolicyGroup retVal = new org.nhindirect.config.store.CertPolicyGroup();
    	
    	final Collection<org.nhindirect.config.store.CertPolicyGroupReltn> reltns = new ArrayList<org.nhindirect.config.store.CertPolicyGroupReltn>();
    	
    	if (group.getPolicies() != null)
    	{
    		for (CertPolicyGroupUse use : group.getPolicies())
    		{
    			org.nhindirect.config.store.CertPolicyGroupReltn reltn = new org.nhindirect.config.store.CertPolicyGroupReltn();

    			reltn.setCertPolicy(toEntityCertPolicy(use.getPolicy()));
    			reltn.setCertPolicyGroup(retVal);
    			reltn.setIncoming(use.isIncoming());
    			reltn.setOutgoing(use.isOutgoing());
    			
    			if (use.getPolicyUse() != null)
    				reltn.setPolicyUse(org.nhindirect.config.store.CertPolicyUse.valueOf(use.getPolicyUse().toString()));

    			reltns.add(reltn);
    		}
    	}
    	
    	retVal.setPolicyGroupName(group.getPolicyGroupName());
    	retVal.setCreateTime(group.getCreateTime());
    	
    	retVal.setCertPolicyGroupReltn(reltns);
    	   	
    	return retVal;
    }       
    
    public static CertPolicyGroupDomainReltn toModelCertPolicyGroupDomainReltn(org.nhindirect.config.store.CertPolicyGroupDomainReltn reltn)
    {
    	if (reltn == null)
    		return null;
    	
    	final CertPolicyGroupDomainReltn retVal = new CertPolicyGroupDomainReltn();
    	
    	retVal.setId(reltn.getId());
    	retVal.setPolicyGroup(toModelCertPolicyGroup(reltn.getCertPolicyGroup()));
    	retVal.setDomain(toModelDomain(reltn.getDomain()));

    	return retVal;
    }   
}
