package org.nhindirect.gateway.smtp.module;

import org.nhindirect.common.audit.Auditor;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;

public class AuditorModule extends AbstractModule 
{
	private final Provider<Auditor> provider;
	
	public static AuditorModule create(Provider<Auditor> provider)
	{
		return new AuditorModule(provider);
	}
	
	private AuditorModule(Provider<Auditor> provider)
	{
		this.provider = provider;
	}
	
	protected void configure()
	{
		bind(Auditor.class).toProvider(provider);
	}
}
