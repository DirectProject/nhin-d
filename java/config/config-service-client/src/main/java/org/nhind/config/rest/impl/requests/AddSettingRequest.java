package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractPutRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Setting;

public class AddSettingRequest extends AbstractPutRequest<Setting, Setting>
{
	private final String name;
	private final String value;
	
    public AddSettingRequest(HttpClient httpClient, String serviceUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, 
            String name, String value) 
    {
    	super(httpClient, serviceUrl, jsonMapper, securityManager, null);
    	
    	if (name == null || name.isEmpty())
    		throw new IllegalArgumentException("Name cannot be null or empty");
    	
    	if (value == null || value.isEmpty())
    		throw new IllegalArgumentException("Value cannot be null or empty");
    	
    	this.name = name;
    	this.value = value;
    }

	@Override
	protected String getRequestUri() throws ServiceException 
	{
		return serviceUrl + "setting/" + uriEscape(name) + "/" + uriEscape(value);
	}
       
}
