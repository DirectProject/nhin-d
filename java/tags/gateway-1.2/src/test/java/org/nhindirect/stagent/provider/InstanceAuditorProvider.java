package org.nhindirect.stagent.provider;

import org.nhindirect.common.audit.Auditor;

import com.google.inject.Provider;

public class InstanceAuditorProvider implements Provider<Auditor> 
{
	private final Auditor instance;
	
	public InstanceAuditorProvider(Auditor instance)
	{
		this.instance = instance;
	}
	
	@Override
	public Auditor get()
	{
		return instance;
	}
}
