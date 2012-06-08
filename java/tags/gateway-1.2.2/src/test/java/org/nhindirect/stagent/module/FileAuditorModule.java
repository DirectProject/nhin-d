package org.nhindirect.stagent.module;

import org.nhindirect.common.audit.Auditor;
import org.nhindirect.common.audit.provider.FileAuditorProvider;

import com.google.inject.AbstractModule;

public class FileAuditorModule extends AbstractModule 
{
	private String fileLoc;
	
	public static FileAuditorModule create(String fileLoc)
	{
		return new FileAuditorModule(fileLoc);
	}
	
	private FileAuditorModule(String fileLoc)
	{
		this.fileLoc = fileLoc;
	}
	
	protected void configure()
	{
		bind(Auditor.class).toProvider(new FileAuditorProvider(fileLoc));
	}
}
