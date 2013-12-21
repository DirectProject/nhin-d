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

package org.nhindirect.gateway.smtp.dsn.module;

import org.nhindirect.gateway.smtp.dsn.DSNCreator;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;

/**
 * Guice module for creating instances of a DSNCreator object using a DSNCreator provider.
 * @author Greg Meyer
 * @since 2.0
 */
public class DSNCreatorProviderModule extends AbstractModule
{
	protected final Provider<DSNCreator> dsnCreator;
	
	/**
	 * Creates an instance of the module with a DSNCreator provider.
	 * @param dsnCreator Guice provider that will create instances of a specific DNSCreator type.
	 * @return Instance of the module.
	 */
	public static DSNCreatorProviderModule create(Provider<DSNCreator> dsnCreator)
	{
		return new DSNCreatorProviderModule(dsnCreator);
	}
	
	private DSNCreatorProviderModule(Provider<DSNCreator> dsnCreator)
	{
		this.dsnCreator = dsnCreator;
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void configure()
	{	
		bind(DSNCreator.class).toProvider(dsnCreator);
	}
}
