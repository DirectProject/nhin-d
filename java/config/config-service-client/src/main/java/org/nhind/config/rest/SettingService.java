package org.nhind.config.rest;

import java.util.Collection;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Setting;

public interface SettingService 
{
	public Collection<Setting> getSettings() throws ServiceException;
	
	public Setting getSetting(String name) throws ServiceException;
	
	public void addSetting(String name, String value) throws ServiceException;
	
	public void updateSetting(String name, String value) throws ServiceException;
	
	public void deleteSetting(String name) throws ServiceException;
}
