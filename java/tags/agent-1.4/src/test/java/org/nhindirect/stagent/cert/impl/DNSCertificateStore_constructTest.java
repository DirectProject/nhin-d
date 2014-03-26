package org.nhindirect.stagent.cert.impl;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.nhindirect.stagent.cert.CertCacheFactory;
import org.nhindirect.stagent.cert.CertStoreCachePolicy;
import org.nhindirect.stagent.cert.CertificateStore;
import org.xbill.DNS.ResolverConfig;

import junit.framework.TestCase;

public class DNSCertificateStore_constructTest extends TestCase
{
	@Override
	public void setUp()
	{
		CertCacheFactory.getInstance().flushAll();
	}
	
	@Override
	public void tearDown()
	{
		CertCacheFactory.getInstance().flushAll();
	}
	
	public void testContructDNSCertificateStore_defaultConstructor()
	{
		DNSCertificateStore store = new DNSCertificateStore();
		
		assertEquals(ResolverConfig.getCurrentConfig().servers().length, store.servers.size());
		assertNotNull(store.localStoreDelegate);
		assertNotNull(store.cachePolicy);
	}
	
	public void testContructDNSCertificateStore_nullServers()
	{
		DNSCertificateStore store = new DNSCertificateStore(null);
		
		assertEquals(ResolverConfig.getCurrentConfig().servers().length, store.servers.size());
		assertNotNull(store.localStoreDelegate);
		assertNotNull(store.cachePolicy);
	}
	
	public void testContructDNSCertificateStore_emptyServers()
	{
		DNSCertificateStore store = new DNSCertificateStore(new ArrayList<String>());
		
		assertEquals(ResolverConfig.getCurrentConfig().servers().length, store.servers.size());
		assertNotNull(store.localStoreDelegate);
		assertNotNull(store.cachePolicy);
	}
	
	public void testContructDNSCertificateStore_providedServers()
	{
		Collection<String> servers = Arrays.asList("159.140.168.3");
		
		DNSCertificateStore store = new DNSCertificateStore(servers);
		
		assertEquals(1, store.servers.size());
		assertEquals("159.140.168.3", store.servers.iterator().next());
		assertNotNull(store.localStoreDelegate);
		assertNotNull(store.cachePolicy);
	}
	
	public void testContructDNSCertificateStore_fullConstructor_nullBootStrap_assertException()
	{
		boolean exceptionOccured = false;
		
		try
		{
			new DNSCertificateStore(null, null, null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	public void testContructDNSCertificateStore_fullConstructor_providedServers()
	{
		CertificateStore bootStrap = mock(CertificateStore.class);
		Collection<String> servers = Arrays.asList("159.140.168.3");
		
		DNSCertificateStore store = new DNSCertificateStore(servers, bootStrap, null);
		
		assertEquals(1, store.servers.size());
		assertEquals("159.140.168.3", store.servers.iterator().next());
		assertEquals(bootStrap, store.localStoreDelegate);
		assertNotNull(store.cachePolicy);
	}
	
	public void testContructDNSCertificateStore_fullConstructor_nullServers()
	{
		CertificateStore bootStrap = mock(CertificateStore.class);
		
		DNSCertificateStore store = new DNSCertificateStore(null, bootStrap, null);
		
		assertEquals(ResolverConfig.getCurrentConfig().servers().length, store.servers.size());
		assertNotNull(store.localStoreDelegate);
		assertNotNull(store.cachePolicy);
	}
	
	public void testContructDNSCertificateStore_fullConstructor_emptyServers()
	{
		CertificateStore bootStrap = mock(CertificateStore.class);
		
		DNSCertificateStore store = new DNSCertificateStore(new ArrayList<String>(), bootStrap, null);
		
		assertEquals(ResolverConfig.getCurrentConfig().servers().length, store.servers.size());
		assertNotNull(store.localStoreDelegate);
		assertNotNull(store.cachePolicy);
	}
	
	
	public void testContructDNSCertificateStore_fullConstructor_emptyServersAndProvidedCachePolicy()
	{
		CertificateStore bootStrap = mock(CertificateStore.class);
		CertStoreCachePolicy cachePolicy = mock(CertStoreCachePolicy.class);
		
		DNSCertificateStore store = new DNSCertificateStore(new ArrayList<String>(), bootStrap, cachePolicy);
		
		assertEquals(ResolverConfig.getCurrentConfig().servers().length, store.servers.size());
		assertNotNull(store.localStoreDelegate);
		assertEquals(cachePolicy, store.cachePolicy);
	}
}
