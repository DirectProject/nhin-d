package org.nhind.config.rest.impl;

import java.util.Collection;

import org.apache.http.client.HttpClient;
import org.nhind.config.rest.DNSService;
import org.nhind.config.rest.impl.requests.AddDNSRecordRequest;
import org.nhind.config.rest.impl.requests.DeleteDNSRecordsByIdsRequest;
import org.nhind.config.rest.impl.requests.GetDNSRecordsRequest;
import org.nhind.config.rest.impl.requests.UpdateDNSRecordRequest;
import org.nhindirect.common.rest.AbstractSecuredService;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.DNSRecord;

public class DefaultDNSService extends AbstractSecuredService implements DNSService
{
    public DefaultDNSService(String serviceUrl, HttpClient httpClient, 
    		ServiceSecurityManager securityManager) 
    {	
        super(serviceUrl, httpClient, securityManager);
    }

	@Override
	public Collection<DNSRecord> getDNSRecord(int type, String name) throws ServiceException 
	{
		return callWithRetry(new GetDNSRecordsRequest(httpClient, serviceURL, jsonMapper, securityManager,
				type, name));		
	}

	@Override
	public void addDNSRecord(DNSRecord record) throws ServiceException 
	{
		callWithRetry(new AddDNSRecordRequest(httpClient, serviceURL, jsonMapper, securityManager, record));	
	}

	@Override
	public void updatedDNSRecord(DNSRecord record) throws ServiceException 
	{
		callWithRetry(new UpdateDNSRecordRequest(httpClient, serviceURL, jsonMapper, securityManager, record));	
	}

	@Override
	public void deleteDNSRecordsByIds(Collection<Long> ids) throws ServiceException 
	{
		callWithRetry(new DeleteDNSRecordsByIdsRequest(httpClient, serviceURL, jsonMapper, securityManager, ids));			
	}
}
