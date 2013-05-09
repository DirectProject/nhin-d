package org.nhindirect.stagent.module;

import org.nhindirect.stagent.annotation.PublicPolicyResolver;
import org.nhindirect.stagent.policy.PolicyResolver;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;

public class PublicPolicyResolverModule extends AbstractModule
{
	private final Provider<PolicyResolver> resolverProvider;
	
	public static PublicPolicyResolverModule create(Provider<PolicyResolver> resolverProvider)
	{
		return new PublicPolicyResolverModule(resolverProvider);
	}
	
	private PublicPolicyResolverModule (Provider<PolicyResolver> resolverProvider)
	{
		this.resolverProvider = resolverProvider;
	}
	
	protected void configure()
	{
		bind(PolicyResolver.class).annotatedWith(PublicPolicyResolver.class).toProvider(resolverProvider);
	}
}
