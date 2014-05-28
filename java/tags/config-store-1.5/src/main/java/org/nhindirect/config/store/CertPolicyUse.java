package org.nhindirect.config.store;

public enum CertPolicyUse 
{
	/**
	 * Used when validating certificate path trust chaining
	 */
	TRUST,
	
	/**
	 * Used when accessing certificate via a private resolver.
	 */
	PRIVATE_RESOLVER,
	
	/**
	 * Used when accessing certificate via a public resolver.
	 */
	PUBLIC_RESOLVER,
	
	/**
	 * Used when performing basic policy validation outside the context of the security and trust agent.
	 */
	VALIDATION
}
