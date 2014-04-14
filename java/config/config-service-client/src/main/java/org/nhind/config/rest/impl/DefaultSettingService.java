package org.nhind.config.rest.impl;

import java.util.Collection;

import org.apache.http.client.HttpClient;
import org.nhind.config.rest.SettingService;
import org.nhind.config.rest.impl.requests.AddSettingRequest;
import org.nhind.config.rest.impl.requests.DeleteSettingRequest;
import org.nhind.config.rest.impl.requests.GetSettingRequest;
import org.nhind.config.rest.impl.requests.GetSettingsRequest;
import org.nhind.config.rest.impl.requests.UpdateSettingRequest;
import org.nhindirect.common.rest.AbstractSecuredService;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Setting;

public class DefaultSettingService extends AbstractSecuredService implements SettingService
{
    public DefaultSettingService(String serviceUrl, HttpClient httpClient, 
    		ServiceSecurityManager securityManager) 
    {	
        super(serviceUrl, httpClient, securityManager);
    }

	@Override
	public Collection<Setting> getSettings() throws ServiceException 
	{
		return callWithRetry(new GetSettingsRequest(httpClient, serviceURL, jsonMapper, securityManager));
	}

	@Override
	public Setting getSetting(String name) throws ServiceException 
	{
		final Collection<Setting> settings = callWithRetry(new GetSettingRequest(httpClient, serviceURL, jsonMapper, securityManager,
				name));
		
		return (settings.isEmpty()) ? null : settings.iterator().next();
	}

	@Override
	public void addSetting(String name, String value) throws ServiceException 
	{	
		callWithRetry(new AddSettingRequest(httpClient, serviceURL, jsonMapper, securityManager,
				name, value));	
	}

	@Override
	public void updateSetting(String name, String value) throws ServiceException 
	{
		callWithRetry(new UpdateSettingRequest(httpClient, serviceURL, jsonMapper, securityManager,
				name, value));	
	}

	@Override
	public void deleteSetting(String name) throws ServiceException 
	{
		callWithRetry(new DeleteSettingRequest(httpClient, serviceURL, jsonMapper, securityManager,
				name));	
	}
}
