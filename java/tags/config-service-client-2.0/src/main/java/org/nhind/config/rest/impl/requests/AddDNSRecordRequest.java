package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractPutRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.DNSRecord;

public class AddDNSRecordRequest extends AbstractPutRequest<DNSRecord, DNSRecord>
{
    public AddDNSRecordRequest(HttpClient httpClient, String serviceUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, DNSRecord record) 
    {
    	super(httpClient, serviceUrl, jsonMapper, securityManager, record);
    }

	@Override
	protected String getRequestUri() throws ServiceException 
	{
		return serviceUrl + "dns";
	}
}