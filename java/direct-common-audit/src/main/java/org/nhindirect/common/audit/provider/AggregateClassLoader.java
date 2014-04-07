/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
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

package org.nhindirect.common.audit.provider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;

/**
 * Class loader that aggregates a list class loaders and searches each on iteratively for classes resources.
 * @author Greg Meyer
 * @since 1.0
 */
public class AggregateClassLoader extends ClassLoader
{

	protected final Collection<ClassLoader> classLoaders;
	protected final Collection<ClassLoader> resourceLoaders;
	
	/**
	 * Constructor
	 * @param classLoaders Collection  of class loaders used for loading class objects.
	 * @param resourceLoaders Collection of class loaders used for loading resources.
	 */
	public AggregateClassLoader(Collection<ClassLoader> classLoaders, Collection<ClassLoader> resourceLoaders)
	{
		this.classLoaders = classLoaders;
		this.resourceLoaders = resourceLoaders;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void clearAssertionStatus() 
	{
		for(ClassLoader loader : classLoaders)
			loader.clearAssertionStatus();
		
		for(ClassLoader loader : resourceLoaders)
			loader.clearAssertionStatus();		
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
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


	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void setClassAssertionStatus(String arg0, boolean arg1) 
	{
		for(ClassLoader loader : classLoaders)
			loader.setClassAssertionStatus(arg0, arg1);
		
		for(ClassLoader loader : resourceLoaders)
			loader.setClassAssertionStatus(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void setDefaultAssertionStatus(boolean arg0) 
	{
		for(ClassLoader loader : classLoaders)
			loader.setDefaultAssertionStatus(arg0);
		
		for(ClassLoader loader : resourceLoaders)
			loader.setDefaultAssertionStatus(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void setPackageAssertionStatus(String arg0, boolean arg1) 
	{
		for(ClassLoader loader : classLoaders)
			loader.setPackageAssertionStatus(arg0, arg1);
		
		for(ClassLoader loader : resourceLoaders)
			loader.setPackageAssertionStatus(arg0, arg1);
	}

}
