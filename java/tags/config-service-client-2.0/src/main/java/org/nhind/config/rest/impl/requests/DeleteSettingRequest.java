package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractDeleteRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Setting;

public class DeleteSettingRequest extends AbstractDeleteRequest<Setting, Setting>
{
	private final String name;

    public DeleteSettingRequest(HttpClient httpClient, String certServerUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, String name) 
    {
        super(httpClient, certServerUrl, jsonMapper, securityManager, null);
        
        if (name == null || name.isEmpty())
        	throw new IllegalArgumentException("Name cannot be null or empty");
        
        this.name = name;
    }
 
    @Override
    protected String getRequestUri() throws ServiceException
    {

    	return serviceUrl + "setting/" + uriEscape(name);
    }
}