package org.nhindirect.stagent.module;

import org.nhindirect.stagent.annotation.PrivatePolicyResolver;
import org.nhindirect.stagent.policy.PolicyResolver;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;

public class PrivatePolicyResolverModule extends AbstractModule
{
	private final Provider<PolicyResolver> resolverProvider;
	
	public static PrivatePolicyResolverModule create(Provider<PolicyResolver> resolverProvider)
	{
		return new PrivatePolicyResolverModule(resolverProvider);
	}
	
	private PrivatePolicyResolverModule (Provider<PolicyResolver> resolverProvider)
	{
		this.resolverProvider = resolverProvider;
	}
	
	protected void configure()
	{
		bind(PolicyResolver.class).annotatedWith(PrivatePolicyResolver.class).toProvider(resolverProvider);
	}
}
