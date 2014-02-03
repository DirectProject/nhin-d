package org.nhind.config.rest.impl.requests;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractGetRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Setting;

public class GetSettingRequest extends AbstractGetRequest<Setting>
{
	private final String settingName;
	
    public GetSettingRequest(HttpClient httpClient, String certServerUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, String settingName) 
    {
        super(httpClient, certServerUrl, jsonMapper, securityManager, false);
        
        if (settingName == null || settingName.isEmpty())
        	throw new IllegalArgumentException("Setting name cannot be null or empty");
        
        this.settingName = settingName;
    }
 
    @Override
    protected String getRequestUri() throws ServiceException
    {

    	return serviceUrl + "setting/" + uriEscape(settingName);
    }
}
