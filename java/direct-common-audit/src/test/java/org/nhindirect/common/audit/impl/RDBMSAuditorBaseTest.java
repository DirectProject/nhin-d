package org.nhindirect.common.audit.impl;

import static org.junit.Assert.assertEquals;


import java.io.File;

import org.junit.Before;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/test/resources/auditStore-test.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public abstract class RDBMSAuditorBaseTest 
{
	@Autowired
	protected RDBMSDao auditor;		
	
	private static final String derbyHomeLoc = "/target/data";	
	
	static
	{
		try
		{
			final File baseLocation = new File("dummy.txt");
			String fullDerbyHome = baseLocation.getAbsolutePath().substring(0, baseLocation.getAbsolutePath().lastIndexOf(File.separator)) + derbyHomeLoc;
			System.setProperty("derby.system.home", fullDerbyHome);

		}
		catch (Exception e)
		{
			
		}
	}	
	
	
	@Before
	public void setUp()
	{
		clearAuditEvent();
		
	}
	
	protected void clearAuditEvent()
	{
		auditor.clear();
		
		assertEquals((Integer)0, auditor.getEventCount());
	}
	
}
