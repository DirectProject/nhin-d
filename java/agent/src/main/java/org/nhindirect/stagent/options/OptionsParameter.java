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

/**
 * Tuning and configuration options for components of the security and trust agent.  Options can be set either programmatically or set as JVM options.
 * JVM settings can be overridden by setting options programmatically. 
 * @author Greg Meyer
 * @since 1.4
 */
public class OptionsParameter 
{
	/**
	 * String value that indicates the JCE provider that should be used for cryptography and certificate operations.
	 * <p><b>JVM Parameter:</b> -Dorg.nhindirect.stagent.cryptography.JCEProviderName
	 */
	public final static String JCE_PROVIDER = "JCE_PROVIDER";

	/**
	 * String value that indicates the directory where CRLs will be cached.  The directory may a full or relative path.
	 * <p><b>JVM Parameter:</b> -Dorg.nhindirect.stagent.cert.CRLCacheLocation
	 */
	public final static String CRL_CACHE_LOCATION = "CRL_CACHE_LOCATION";
	
	private final String paramName;
	private final String paramValue;
	
	/**
	 * Constructor
	 * @param name The name of the parameter
	 * @param value The String value of the parameter
	 */
	public OptionsParameter(String name, String value)
	{
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException("Parameter name cannot be null or empty");
		
		this.paramName = name;
		this.paramValue = value;
	}
	
	/**
	 * Gets the parameter name.
	 * @return The parameter name
	 */
	public String getParamName()
	{
		return paramName;
	}
	
	/**
	 * Gets the parameter value.
	 * @return The parameter value
	 */
	public String getParamValue()
	{
		return paramValue;
	}
}
