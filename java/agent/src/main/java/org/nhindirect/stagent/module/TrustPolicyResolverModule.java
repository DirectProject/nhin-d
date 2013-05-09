package org.nhindirect.stagent.module;

import org.nhindirect.stagent.policy.PolicyResolver;
import org.nhindirect.stagent.trust.annotation.TrustPolicyResolver;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;

public class TrustPolicyResolverModule extends AbstractModule
{
	private final Provider<PolicyResolver> resolverProvider;
	
	public static TrustPolicyResolverModule create(Provider<PolicyResolver> resolverProvider)
	{
		return new TrustPolicyResolverModule(resolverProvider);
	}
	
	private TrustPolicyResolverModule (Provider<PolicyResolver> resolverProvider)
	{
		this.resolverProvider = resolverProvider;
	}
	
	protected void configure()
	{
		bind(PolicyResolver.class).annotatedWith(TrustPolicyResolver.class).toProvider(resolverProvider);
	}
}
