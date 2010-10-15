package org.nhindirect.config.service;

import java.util.Collection;

import javax.jws.WebMethod;
import javax.jws.WebParam;


import org.nhindirect.config.store.Setting;

/**
 * Service class for methods related to a setting object.
 */
public interface SettingService 
{
    @WebMethod(operationName = "getAllSettings", action = "urn:GetAllSettings")
    public Collection<Setting> getAllSettings() throws ConfigurationServiceException;
    
    @WebMethod(operationName = "getSettingsByNames", action = "urn:GetSettingsByNames")
	public Collection<Setting> getSettingsByNames(@WebParam(name = "names") Collection<String> names) throws ConfigurationServiceException;    
    
    @WebMethod(operationName = "getSettingByName", action = "urn:GetSettingByName")
	public Setting getSettingByName(@WebParam(name = "name") String name)  throws ConfigurationServiceException;
    
    @WebMethod(operationName = "addSetting", action = "urn:AddSetting")
	public void addSetting(@WebParam(name = "name") String name, @WebParam(name = "value") String value) throws ConfigurationServiceException;
    
    @WebMethod(operationName = "updateSetting", action = "urn:UpdateSetting")
	public void updateSetting(@WebParam(name = "name") String name, @WebParam(name = "value") String value) throws ConfigurationServiceException;
    
    @WebMethod(operationName = "deleteSetting", action = "urn:DeleteSetting")
	public void deleteSetting(@WebParam(name = "names") Collection<String> names) throws ConfigurationServiceException;
}
