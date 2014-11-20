/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Umesh Madan     umeshma@microsoft.com
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

package org.nhindirect.stagent.module;

import java.util.Collection;

import org.nhindirect.stagent.NHINDAgent;
import org.nhindirect.stagent.annotation.AgentDomains;
import org.nhindirect.stagent.utils.InjectionUtils;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provider;

public class AgentModule extends AbstractModule 
{
	private final Collection<String> domains;
	private final Module publicCertModule;
	private final Module privateCertModule;
	private final Module trustAnchorModule;
	private final Module publicPolicyResolverModule;
	private final Module privatePolicyResolverModule;
	private final Module trustModelModule;
	
	private final Provider<NHINDAgent> agentProvider;
	
	public static AgentModule create(Collection<String> domains, Module publicCertModule, Module privateCertModule, Module trustAnchorModule,
			 Provider<NHINDAgent> agentProvider, Module publicPolicyResolverModule, Module privatePolicyResolverModule, Module trustModelModule)
	{
		return new AgentModule(domains, publicCertModule, privateCertModule, trustAnchorModule, agentProvider, 
				publicPolicyResolverModule, privatePolicyResolverModule, trustModelModule);
		
	}
	
	public static AgentModule create(Collection<String> domains, Module publicCertModule, Module privateCertModule, Module trustAnchorModule)
	{
		return new AgentModule(domains, publicCertModule, privateCertModule, trustAnchorModule, null, null, null, null);
	}
	
	public static AgentModule create(Provider<NHINDAgent> agentProvider)
	{
		return new AgentModule(null, null, null, null, agentProvider, null, null, null);
	}
	
	private AgentModule(Collection<String> domains, Module publicCertModule, Module privateCertModule, Module trustAnchorModule,
			Provider<NHINDAgent> agentProvider, Module publicPolicyResolverModule, Module privatePolicyResolverModule, Module trustModelModule)
	{
		this.domains = domains;
		this.publicCertModule = publicCertModule;
		this.privateCertModule = privateCertModule;
		this.trustAnchorModule = trustAnchorModule;
		this.agentProvider = agentProvider;
		this.publicPolicyResolverModule = publicPolicyResolverModule;
		this.privatePolicyResolverModule = privatePolicyResolverModule;
		this.trustModelModule = trustModelModule;
	}
	
	protected void configure()
	{
		if (agentProvider != null)
			this.bind(NHINDAgent.class).toProvider(agentProvider);
		else
		{
			this.install(publicCertModule);
			this.install(privateCertModule);
			this.install(trustAnchorModule);
			this.bind(InjectionUtils.collectionOf(String.class)).annotatedWith(AgentDomains.class).toInstance(domains);
			if (publicPolicyResolverModule != null)
				this.install(publicPolicyResolverModule);
			if (privatePolicyResolverModule != null)
				this.install(privatePolicyResolverModule);
			if (trustModelModule != null)
				this.install(trustModelModule);
		}
	}
}
