package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractGetRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.DNSRecord;

public class GetDNSRecordsRequest extends AbstractGetRequest<DNSRecord>
{
	private final String name;
	private final int type;
	
    public GetDNSRecordsRequest(HttpClient httpClient, String certServerUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, int type, String name) 
    {
        super(httpClient, certServerUrl, jsonMapper, securityManager, true);
        
        this.name = name == null ? "" : name;
        this.type = type;
    }
 
    @Override
    protected String getRequestUri() throws ServiceException
    {
    	final StringBuilder builder = new StringBuilder("?type=").append(type);
    	builder.append("&name=").append(uriEscape(name));
    	  
    	return serviceUrl + "dns" + builder.toString();
    }
}