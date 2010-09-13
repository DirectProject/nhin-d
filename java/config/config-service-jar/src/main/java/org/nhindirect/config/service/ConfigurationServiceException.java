package org.nhindirect.config.service;

public class ConfigurationServiceException extends Exception {

	private ConfigurationFault fault = new ConfigurationFault();
	
	public ConfigurationServiceException() {
	}

	public ConfigurationServiceException(String message) {
		super(message);
	}

	public ConfigurationServiceException(Throwable cause) {
		super(cause);
		if (cause instanceof Exception) {
			fault = ConfigurationFault.errorToFault((Exception)cause);
		}
	}

	public ConfigurationServiceException(String message, Throwable cause) {
		super(message, cause);
		if (cause instanceof Exception) {
			fault = ConfigurationFault.errorToFault((Exception)cause);
		}
	}
	
	public ConfigurationServiceException(ConfigurationFault aFault) {
		fault = aFault;
	}
}
