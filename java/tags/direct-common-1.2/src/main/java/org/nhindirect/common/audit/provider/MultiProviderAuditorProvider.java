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

package org.nhindirect.common.audit.provider;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.nhindirect.common.audit.Auditor;
import org.nhindirect.common.audit.impl.MultiProviderAuditor;

import com.google.inject.Provider;

/**
 * Guice provider for creating {@link MultiProviderAuditor} instances.  The provider can take either existing {@link Auditor} instances or providers for creating
 * other instances.
 * @author Greg Meyer
 * @since 1.0
 */
public class MultiProviderAuditorProvider implements Provider<Auditor> 
{

	private Collection<Auditor> auditors;
	
	/**
	 * Creates a provider using existing {@link Auditor} instances.
	 * @param auditor An array of auditors to be used by {@link MultiProviderAuditor} instances.
	 */
	public MultiProviderAuditorProvider(Auditor[] auditors)
	{
		if (auditors == null || auditors.length == 0)
			throw new IllegalArgumentException("Auditor array cannot be null or empty."); 
		
		setAuditors(Arrays.asList(auditors));
	}
	
	/**
	 * Creates a provider using providers to create the internal auditors. 
	 * @param auditor An array of auditor providers used to create the internal auditors.
	 */	
	public MultiProviderAuditorProvider(Provider<Auditor>[] providers)
	{
		if (providers == null || providers.length == 0)
			throw new IllegalArgumentException("Prover list cannot be null or empty.");
		
		Collection<Auditor> auditors = new ArrayList<Auditor>();
		for (Provider<Auditor> provider : providers)
			auditors.add(provider.get());	
		
		setAuditors(auditors);
	}
	
	/*
	 * Sets the list of auditors that will be used to construct the MultiProviderAuditor
	 */
	private void setAuditors(Collection<Auditor> auditors)
	{
		this.auditors = auditors;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Auditor get()
	{
		return new MultiProviderAuditor(auditors);
	}
}
