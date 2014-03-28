package org.nhindirect.common.audit.provider;

import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.nhindirect.common.audit.Auditor;
import org.nhindirect.common.audit.impl.RDBMSAuditor;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.inject.Provider;

public class RDBMSAuditorProvider implements Provider<Auditor> 
{
	private static final String DEFAULT_APPLICATION_CONTEXT_FILE = "auditStore.xml";
	
	private final EntityManager entityManager;
	
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
		
		
			final EntityManagerFactory factory = ctx.getBean(EntityManagerFactory.class);
		
			entityManager = factory.createEntityManager();
		}
		catch (Exception e)
		{
			throw new IllegalStateException("Entity manager could not be found in Spring configuration.", e);
		}
	}
	
	
	/**
	 * Constructor with the pre-configured entity manager.
	 * @param fileLoc The pre-configured entity manager.
	 */
	public RDBMSAuditorProvider(EntityManager entityManager)
	{
		if (entityManager == null)
			throw new IllegalArgumentException("Entity manager null");
		
		this.entityManager = entityManager;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Auditor get()
	{
		return new RDBMSAuditor(entityManager);
	}
}
