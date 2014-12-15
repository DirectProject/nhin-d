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


import java.util.Arrays;

import org.nhindirect.common.audit.Auditor;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.inject.Provider;

/**
 * Google Guice provider for the RDBMSAuditor.
 * @author Greg Meyer
 * @since 1.0
 */
public class RDBMSAuditorProvider implements Provider<Auditor> 
{
	private static final String DEFAULT_APPLICATION_CONTEXT_FILE = "auditStore.xml";
	
	private final String springConfigLocation;
	protected Auditor auditor = null;
	
	/**
	 * Default constructor.  Uses the default file auditStore.xml to pull the Sring configuration.
	 */
	public RDBMSAuditorProvider()
	{ 
		this((String)null);
	}
	
	/**
	 * Constructor with the spring file location for the entity manager.
	 * @param fileLoc The location of the spring file location for the entity manager.
	 */
	public RDBMSAuditorProvider(String springConfigLocation)
	{ 
		this.springConfigLocation = springConfigLocation;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized Auditor get()
	{
		if (auditor == null)
			auditor = createAuditor();
		
		return auditor;
	}
	
	protected Auditor createAuditor()
	{
		final String fileLoc = (springConfigLocation == null || springConfigLocation.isEmpty()) ? DEFAULT_APPLICATION_CONTEXT_FILE : springConfigLocation;
		
		final ClassLoader loader = new AggregateClassLoader(Arrays.asList(Thread.currentThread().getContextClassLoader()),
				Arrays.asList(ClassLoader.getSystemClassLoader(), 
				Thread.currentThread().getContextClassLoader(), RDBMSAuditorProvider.class.getClassLoader()));
		
		try
		{

			final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(fileLoc)
			{	
			    protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader)
			    {
			        super.initBeanDefinitionReader(reader);
			        
			        reader.setBeanClassLoader(loader);
			        setClassLoader(loader);
			    }

			};

			return (Auditor)ctx.getBean("auditor");
		}
		catch (Exception e)
		{
			throw new IllegalStateException("Auditor could not be found in Spring configuration.", e);
		}
	}
}
