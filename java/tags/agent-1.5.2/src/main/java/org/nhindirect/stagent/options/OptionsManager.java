/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.stagent.options;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Manages options tuning and configuration parameters of the agent.  Parameters are initialized from JVM parameters or from a properties file
 * if present and can be overridden programmatically.
 * <br>
 * The following is the order or precedence for applying options: 
 * <ol>
 * <li>Programmatic settings</li>
 * <li>JVM Settings</li>
 * <li>Properties based settings</li>
 * <li>Default settings</li>
 * <ol>
 * By default, the manager looks for a properties file named <i>agentSettings.properties</i> in the working directory, but can be overriden using the JVM parameter
 * org.nhindirect.stagent.PropertiesFile providing either the full path and file name or just file name that needs to be located in the working directory.
 * <br>Property and JVM setting names are defined in the {@link OptionsParameter} class.
 * <br>
 * The manager is implemented as a singleton and an instance can be obtained using the {@link #getInstance()} method.
 * <br>
 * Operations on this class are thread safe.
 * @author gm2552
 * @since 1.4
 */
public class OptionsManager 
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(OptionsManager.class);

	protected final static String OPTIONS_PROPERTIES_FILE_JVM_PARAM = "org.nhindirect.stagent.PropertiesFile";
	
	protected static final String DEFAULT_PROPERTIES_FILE = "agentSettings.properties";
	
	protected static final Map<String, String> JVM_PARAMS;
	
	protected final Map<String, OptionsParameter> options;
	
	protected static OptionsManager INSTANCE;
	
	static
	{
		JVM_PARAMS = new HashMap<String, String>();
		JVM_PARAMS.put(OptionsParameter.JCE_PROVIDER, "org.nhindirect.stagent.cryptography.JCEProviderName");
		JVM_PARAMS.put(OptionsParameter.CRL_CACHE_LOCATION, "org.nhindirect.stagent.cert.CRLCacheLocation");
		
		/*
		 * DNS resolver parameters
		 */
		JVM_PARAMS.put(OptionsParameter.DNS_CERT_RESOLVER_RETRIES, "org.nhindirect.stagent.cert.dnsresolver.ServerRetries");
		JVM_PARAMS.put(OptionsParameter.DNS_CERT_RESOLVER_TIMEOUT, "org.nhindirect.stagent.cert.dnsresolver.ServerTimeout");
		JVM_PARAMS.put(OptionsParameter.DNS_CERT_RESOLVER_USE_TCP, "org.nhindirect.stagent.cert.dnsresolver.ServerUseTCP");
		JVM_PARAMS.put(OptionsParameter.DNS_CERT_RESOLVER_MAX_CACHE_SIZE, "org.nhindirect.stagent.cert.dnsresolver.MaxCacheSize");
		JVM_PARAMS.put(OptionsParameter.DNS_CERT_RESOLVER_CACHE_TTL, "org.nhindirect.stagent.cert.dnsresolver.CacheTTL");
		
		/*
		 * LDAP resolver parameters
		 */
		JVM_PARAMS.put(OptionsParameter.LDAP_CERT_RESOLVER_MAX_CACHE_SIZE, "org.nhindirect.stagent.cert.ldapresolver.MaxCacheSize");
		JVM_PARAMS.put(OptionsParameter.LDAP_CERT_RESOLVER_CACHE_TTL, "org.nhindirect.stagent.cert.ldapresolver.CacheTTL");	
		
		/*
		 * Cryptography parameters
		 */
		JVM_PARAMS.put(OptionsParameter.CRYPTOGRAHPER_SMIME_ENCRYPTION_ALGORITHM, "org.nhindirect.stagent.cryptographer.smime.EncryptionAlgorithm");
		JVM_PARAMS.put(OptionsParameter.CRYPTOGRAHPER_SMIME_DIGEST_ALGORITHM, "org.nhindirect.stagent.cryptographer.smime.DigestAlgorithm");
	}
	
	
	/**
	 * Adds custom init parameters used for options.  Initialization parameters are added as a map of names to JVM parameters/properties.  Although not required, names
	 * should be name appropriately using the same convention as {@link OptionParameter option parameters}.  Names are the string used when calling
	 * {@link OptionsManager#getParameter(String)}.
	 * <br>
	 * If the OptionsManager has already been initialized with a previous call to {@link OptionsManager#getInstance()}, the manager immediately searched
	 * for JVM parameters in the map's values and loads them into the manager.
	 * @param initParams A map of option names to JVM parameters.
	 */
	public static synchronized void addInitParameters(Map<String, String> initParams)
	{
		JVM_PARAMS.putAll(initParams);
		
		// if an instance already exists,
		// then lookup and load the settings immediately from JVM options
		if (INSTANCE != null)
		{
			for (String param : initParams.keySet())
				INSTANCE.initParam(param);
		}
	}
	
	/**
	 * Gets the instance of the operations manager.
	 * @return Instance of the operations manager.
	 */
	public static synchronized OptionsManager getInstance()
	{
		if (INSTANCE == null)
			INSTANCE = new OptionsManager();
		
		return INSTANCE;
	}
	
	/**
	 * Constructor.  Initializes parameters from JVM setting if they are set.
	 */
	protected OptionsManager()
	{
		options = new HashMap<String, OptionsParameter>();
		
		initParams();
	}
	
	/**
	 * Gets a collection of all set option parameters.
	 * @return Collection of all set option parameters.
	 */
	public synchronized Collection<OptionsParameter> getParameters()
	{
		return Collections.unmodifiableCollection(options.values());
	}
	
	/**
	 * Gets a single option parameter by name.
	 * @param paramName The name of the parameter to retrieve.
	 * @return The OptionsParameter corresponding to the paremter name.  Returns null if a parameter with the name has not be set
	 * or loaded from JVM settings.
	 */
	public synchronized OptionsParameter getParameter(String paramName)
	{
		if (paramName == null || paramName.isEmpty())
			throw new IllegalArgumentException("Parameter name cannot be null or empty");
		
		return options.get(paramName);
	}
	
	/**
	 * Sets an options parameter.  If an existing setting with the same name already exist, then it will be replaced with 
	 * this setting.
	 * @param param The options paremeter to set.
	 */
	public synchronized void setOptionsParameter(OptionsParameter param)
	{
		if (param == null)
			throw new IllegalArgumentException("Parameter cannot be null");
		
		options.put(param.getParamName(), param);
	}
	
	/**
	 * Sets a collection of options parameters.  If an existing setting with the same name already exist, then it will be replaced with 
	 * the new setting.
	 * @param params
	 */
	public synchronized void setOptionsParameters(Collection<OptionsParameter> params)
	{
		if (params == null)
			throw new IllegalArgumentException("Parameters cannot be null");
		
		for (OptionsParameter param : params)
			setOptionsParameter(param);
	}
	
	/**
	 * Initializes the options parameters from JVM settings
	 */
	protected void initParams()
	{

		loadParamsFromPropertiesFile();

		
		for (String param : JVM_PARAMS.keySet())
			initParam(param);
	}
	
	/**
	 * Loads options from a properties file if it exists and applies any option not already
	 * set by a JVM option to the system properties
	 */
	protected void loadParamsFromPropertiesFile()
	{
		// get options from the properties file if it exists and set any parameter that has not been set
		// via JVM parameters

		// check if the file name parameter is set		
		String propFileName = System.getProperty(OPTIONS_PROPERTIES_FILE_JVM_PARAM, DEFAULT_PROPERTIES_FILE);
		if (propFileName.isEmpty())
			propFileName = DEFAULT_PROPERTIES_FILE;
		
		// now load the file into a properties object
		final File optionsFile = new File(propFileName);
		if (optionsFile.exists())
		{
			InputStream inStream = null;
			
			try
			{
				inStream = FileUtils.openInputStream(optionsFile);
				final Properties props = new Properties();
				props.load(inStream);
				
				// search through each JVM param and determine if 
				// 1. the parameter does not exist in the system properties
				// 2. the parameter exists in the properties file
				// if the these criteria are met, add the properties file based
				// setting to the system properties
				for (Object paramName : props.keySet())
				{
					final String paramValue = System.getProperty(paramName.toString(), "");

					if (paramValue.isEmpty())
					{
						final String propValue = props.getProperty(paramName.toString(), null);
						if (propValue != null)
						{
							System.setProperty(paramName.toString(), propValue);
							options.put(paramName.toString(), new OptionsParameter(paramName.toString(), propValue));	
						}
					}
				}
			}
			///CLOVER:OFF
			catch (Exception e)
			{
				LOGGER.warn("Exception occured loading options settings from properties file " + optionsFile, e);
			}
			///CLOVER:ON
			finally
			{
				IOUtils.closeQuietly(inStream);
			}
		}
	}
	
	/**
	 * Initializes a single parameter based on a parameter name.
	 * @param paramName The parameter name.
	 */
	protected void initParam(String paramName)
	{
		String jvmArg = (String)JVM_PARAMS.get(paramName);
		if (jvmArg != null && jvmArg.length() > 0)
		{
			String value = System.getProperty(jvmArg);

			if (value != null && value.length() > 0)
				options.put(paramName, new OptionsParameter(paramName, value));			
		}
	}
}
