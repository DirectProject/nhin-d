package org.nhindirect.stagent.cert.impl;

import java.io.File;
import java.util.UUID;

import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.options.OptionsManager;
import org.nhindirect.stagent.options.OptionsParameter;

import junit.framework.TestCase;

public class CRLRevocationManager_initCRLCacheLocationTest extends TestCase
{
	static final char[] invalidFileName;
	
	static
	{
		invalidFileName = new char[Character.MAX_VALUE];
		
		for (char i = 1; i < Character.MAX_VALUE; ++i)
		{
			invalidFileName[i - 1] = i;
		}
	}
	
	@Override
	public void setUp()
	{
    	CryptoExtensions.registerJCEProviders();
		
		CRLRevocationManager.initCRLCacheLocation();
		CRLRevocationManager.getInstance().flush();		
		CRLRevocationManager.crlCacheLocation = null;
		OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(OptionsParameter.CRL_CACHE_LOCATION, ""));
	}
	
	@Override
	public void tearDown()
	{
		CRLRevocationManager.getInstance().flush();
		OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(OptionsParameter.CRL_CACHE_LOCATION, ""));
		CRLRevocationManager.initCRLCacheLocation();
	}
	
	public void testInitCRLCacheLocation_noOptionParameter()
	{	
		CRLRevocationManager.initCRLCacheLocation();
		assertTrue(CRLRevocationManager.crlCacheLocation.getAbsolutePath().endsWith("CrlCache"));
	}
	
	public void testInitCRLCacheLocation_customOptionParameter()
	{	
		String crlLocation = UUID.randomUUID().toString();
		OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(OptionsParameter.CRL_CACHE_LOCATION, "target/" + crlLocation));
		CRLRevocationManager.initCRLCacheLocation();
		assertTrue(CRLRevocationManager.crlCacheLocation.getAbsolutePath().endsWith(crlLocation));
	}
	
	public void testInitCRLCacheLocation_locExistsAndNotADirectory() throws Exception
	{	
		String crlLocation = UUID.randomUUID().toString();
		File createFile = new File("target/" + crlLocation);
		createFile.createNewFile();
		
		OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(OptionsParameter.CRL_CACHE_LOCATION, "target/" + crlLocation));
		CRLRevocationManager.initCRLCacheLocation();
		assertNull(CRLRevocationManager.crlCacheLocation);
	}
	
	public void testInitCRLCacheLocation_invalidLocationName() throws Exception
	{	
		
		OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(OptionsParameter.CRL_CACHE_LOCATION, "target/" + new String(invalidFileName)));
		CRLRevocationManager.initCRLCacheLocation();
		assertNull(CRLRevocationManager.crlCacheLocation);
	}
}
