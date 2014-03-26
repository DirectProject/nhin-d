package org.nhindirect.common.audit.provider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;

public class AggregateClassLoader extends ClassLoader
{

	protected final Collection<ClassLoader> classLoaders;
	protected final Collection<ClassLoader> resourceLoaders;
	
	public AggregateClassLoader(Collection<ClassLoader> classLoaders, Collection<ClassLoader> resourceLoaders)
	{
		this.classLoaders = classLoaders;
		this.resourceLoaders = resourceLoaders;
	}
	
	@Override
	public synchronized void clearAssertionStatus() 
	{
		for(ClassLoader loader : classLoaders)
			loader.clearAssertionStatus();
		
		for(ClassLoader loader : resourceLoaders)
			loader.clearAssertionStatus();		
	}


	@Override
	public URL getResource(String arg0) 
	{
		for(ClassLoader loader : resourceLoaders)
		{
			final URL url = loader.getResource(arg0);
			if (url != null)
				return url;
		}

		return null;
	}

	@Override
	public InputStream getResourceAsStream(String arg0) 
	{
		for(ClassLoader loader : resourceLoaders)
		{
			final InputStream stream = loader.getResourceAsStream(arg0);
			if (stream != null)
				return stream;
		}

		return null;
	}

	@Override
	public Enumeration<URL> getResources(String arg0) throws IOException 
	{ 
		Enumeration<URL> urls = null;
		for(ClassLoader loader : resourceLoaders)
		{
			try
			{
				urls= loader.getResources(arg0);

			}
			catch (IOException e)
			{
				
			}
		}

		return urls;
	}


	@Override
	public Class<?> loadClass(String arg0) throws ClassNotFoundException
	{
		Class<?> clazz = null;
		for(ClassLoader loader : classLoaders)
		{
			try
			{
				clazz = loader.loadClass(arg0);
				if (clazz != null)
					return clazz;
			}
			catch (ClassNotFoundException e)
			{
				
			}
		}

		throw new ClassNotFoundException("Class " + arg0 + " not found");
	}

	@Override
	public synchronized void setClassAssertionStatus(String arg0, boolean arg1) 
	{
		for(ClassLoader loader : classLoaders)
			loader.setClassAssertionStatus(arg0, arg1);
		
		for(ClassLoader loader : resourceLoaders)
			loader.setClassAssertionStatus(arg0, arg1);
	}

	@Override
	public synchronized void setDefaultAssertionStatus(boolean arg0) 
	{
		for(ClassLoader loader : classLoaders)
			loader.setDefaultAssertionStatus(arg0);
		
		for(ClassLoader loader : resourceLoaders)
			loader.setDefaultAssertionStatus(arg0);
	}

	@Override
	public synchronized void setPackageAssertionStatus(String arg0, boolean arg1) 
	{
		for(ClassLoader loader : classLoaders)
			loader.setPackageAssertionStatus(arg0, arg1);
		
		for(ClassLoader loader : resourceLoaders)
			loader.setPackageAssertionStatus(arg0, arg1);
	}

}
