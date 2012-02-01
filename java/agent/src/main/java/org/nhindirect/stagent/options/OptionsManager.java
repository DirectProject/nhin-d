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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages options tuning and configuration parameters of the agent.  Parameters are initialized from JVM parameters if they are set and can be overridden
 * programmatically.
 * <br>
 * The manager is implemented as a singleton and an instance can be obtained using the {@link #getInstance()} method.
 * <br>
 * Operations on this class are thread safe.
 * @author gm2552
 * @since 1.4
 */
public class OptionsManager 
{
	protected static final Map<String, String> JVM_PARAMS;
	
	protected final Map<String, OptionsParameter> options;
	
	protected static OptionsManager INSTANCE;
	
	static
	{
		JVM_PARAMS = new HashMap<String, String>();
		JVM_PARAMS.put(OptionsParameter.JCE_PROVIDER, "org.nhindirect.stagent.cryptography.JCEProviderName");
		JVM_PARAMS.put(OptionsParameter.CRL_CACHE_LOCATION, "org.nhindirect.stagent.cert.CRLCacheLocation");
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
		for (String param : JVM_PARAMS.keySet())
			initParam(param);
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
