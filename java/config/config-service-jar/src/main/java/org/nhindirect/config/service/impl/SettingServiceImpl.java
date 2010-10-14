package org.nhindirect.config.service.impl;

import java.util.Arrays;
import java.util.Collection;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.config.service.ConfigurationServiceException;
import org.nhindirect.config.service.SettingService;
import org.nhindirect.config.store.Setting;
import org.nhindirect.config.store.dao.SettingDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service class for methods related to an Anchor object.
 */
@WebService(endpointInterface = "org.nhindirect.config.service.SettingService")
public class SettingServiceImpl implements SettingService
{

    private static final Log log = LogFactory.getLog(SettingServiceImpl.class);

    private SettingDao dao;
	
    /**
     * Initialization method.
     */
    public void init() {
        log.info("SettingService initialized");
    }

    
	public void addSetting(String name, String value)
			throws ConfigurationServiceException {
		
		dao.add(name, value);
	}


	public void deleteSetting(Collection<String> names) throws ConfigurationServiceException {
		
		dao.delete(names);		
	}


	public Collection<Setting> getAllSettings()
			throws ConfigurationServiceException {
		
		return dao.getAll();
	}


	public Setting getSettingByName(String name)
			throws ConfigurationServiceException {
	
		Collection<Setting> settings = dao.getByNames(Arrays.asList(name));
		
		if (settings == null || settings.size() == 0)
			return null;
		
		return settings.iterator().next();
	}


	public Collection<Setting> getSettingsByNames(Collection<String> names)
			throws ConfigurationServiceException {

		return dao.getByNames(names);
	}


	public void updateSetting(String name, String value)
			throws ConfigurationServiceException {
		
		dao.update(name, value);	
	}

    /**
     * Set the value of the AnchorDao object.
     * 
     * @param dao
     *            the value of the AnchorDao object.
     */
    @Autowired
    public void setDao(SettingDao dao) {
        this.dao = dao;
    }

    /**
     * Return the value of the AnchorDao object.
     * 
     * @return the value of the AnchorDao object.
     */
    public SettingDao getDao() {
        return dao;
    }
}
