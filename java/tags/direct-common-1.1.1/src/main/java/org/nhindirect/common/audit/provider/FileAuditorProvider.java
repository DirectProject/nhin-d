package org.nhindirect.common.audit.provider;

import java.io.File;

import org.nhindirect.common.audit.Auditor;
import org.nhindirect.common.audit.impl.FileAuditor;

import com.google.inject.Provider;

public class FileAuditorProvider implements Provider<Auditor> 
{
	private final File auditFile;
	
	/**
	 * Constructor with the logging file location.
	 * @param fileLoc The location of the logging file.
	 */
	public FileAuditorProvider(String fileLoc)
	{
		if (fileLoc == null || fileLoc.isEmpty())
			throw new IllegalArgumentException("File location cannot be null or empty");
		
		auditFile = new File(fileLoc);
	}
	
	
	/**
	 * Constructor with a file descriptor of the logging file.
	 * @param fileLoc File descriptor of the logging file.
	 */
	public FileAuditorProvider(File file)
	{	
		if (file == null)
			throw new IllegalArgumentException("File cannot be null");
		
		auditFile = file;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Auditor get()
	{
		return new FileAuditor(auditFile);
	}
}
