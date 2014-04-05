package org.nhindirect.stagent.cert.impl;


import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.nhindirect.stagent.cert.CertStoreCachePolicy;
import org.nhindirect.stagent.cert.CertificateStore;
import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.cert.impl.LDAPCertificateStore;
import org.nhindirect.stagent.cert.impl.LdapCertUtilImpl;
import org.nhindirect.stagent.utils.BaseTestPlan;
import org.nhindirect.stagent.utils.TestUtils;


/**
 * Generated test case.
 * @author junit_generate
 */
public class LDAPCertificateStore_GetCertificates_Test extends TestCase {
	abstract class TestPlan extends BaseTestPlan {
		@Override
		protected void performInner() throws Exception {
			LDAPCertificateStore impl = createLDAPCertificateStore();
			Collection<X509Certificate> getCertificates = impl
					.getCertificates(createSubjectName());
			doAssertions(getCertificates);
		}

		protected LDAPCertificateStore createLDAPCertificateStore()
				throws Exception {
			return new LDAPCertificateStore(createLdapCertUtil(),
					createBootstrapStore(), (CertStoreCachePolicy) null) {
				@Override
				protected JCS getCache() {
					getCacheCalls++;
					return getCache_Internal();
				}
				@Override
				protected void addOrUpdateLocalStoreDelegate(
						Collection<X509Certificate> retVal) {
					addOrUpdateLocalStoreDelegateCalls++;
					addOrUpdateLocalStoreDelegate_Internal(retVal);
				}
				@Override 
				protected CertificateStore createDefaultLocalStore(){
					  createDefaultLocalStoreCalls++;
					  return createDefaultLocalStore_Internal();
				}
			};
		}

		protected JCS theGetCache;
		protected int getCacheCalls = 0;

		protected JCS getCache_Internal() {
			try {
				theGetCache = JCS.getInstance("");
			} catch (CacheException e) {
				e.printStackTrace();
				fail();
			}
			return theGetCache;
		}
		
		protected int addOrUpdateLocalStoreDelegateCalls = 0;

		protected void addOrUpdateLocalStoreDelegate_Internal(
				Collection<X509Certificate> retVal) {
		}
		
		protected CertificateStore theCreateDefaultLocalStore;
		protected int createDefaultLocalStoreCalls=0;
		protected CertificateStore createDefaultLocalStore_Internal(){
		  theCreateDefaultLocalStore=new KeyStoreCertificateStore();
		  return theCreateDefaultLocalStore;
		}

		protected String theCreateSubjectName;

		protected String createSubjectName() throws Exception {
			theCreateSubjectName = "createSubjectName";
			return theCreateSubjectName;
		}

		protected LdapCertUtilImpl theCreateLdapCertUtil;
		protected int ldapSearchCalls = 0;
		protected Collection<X509Certificate> theLdapSearch;

		protected Collection<X509Certificate> ldapSearch_Internal(
				String subjectName) {
			theLdapSearch = new ArrayList<X509Certificate>();
			return theLdapSearch;
		}

		protected LdapCertUtilImpl createLdapCertUtil() {
			theCreateLdapCertUtil = new LdapCertUtilImpl(
					(LdapEnvironment) null, (String) null, (String) null) {
				@Override
				public Collection<X509Certificate> ldapSearch(String subjectName) {
					ldapSearchCalls++;
					return ldapSearch_Internal(subjectName);
				}
			};
			return theCreateLdapCertUtil;
		}

		protected CertificateStore theCreateBootstrapStore;
		protected int getCertificatesCalls = 0;
		protected Collection<X509Certificate> theGetCertificates;

		protected Collection<X509Certificate> getCertificates_Internal(
				String subjectName) {
			theGetCertificates = new ArrayList<X509Certificate>();
			return theGetCertificates;
		}

		protected CertificateStore createBootstrapStore() {
			theCreateBootstrapStore = new KeyStoreCertificateStore() {
				@Override
				public Collection<X509Certificate> getCertificates(
						String subjectName) {
					getCertificatesCalls++;
					return getCertificates_Internal(subjectName);
				}
			};
			return theCreateBootstrapStore;
		}

		protected void doAssertions(Collection<X509Certificate> getCertificates)
				throws Exception {
		}
	}
	
	class CacheIsNull extends TestPlan {
		protected JCS getCache_Internal() {
			theGetCache = null;
			return theGetCache;
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testCacheIsNull_CertififcateIsRetrievedFromLdapServer() throws Exception {
		new CacheIsNull() {
			
			protected void doAssertions(Collection<X509Certificate> getCertificates)
				throws Exception {
				assertEquals(1, ldapSearchCalls);
			}
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testCacheIsNull_CorrectSubjectNameParamIsPassedToLdapSearchMethod() throws Exception {
		new CacheIsNull() {
			
			protected Collection<X509Certificate> ldapSearch_Internal(
					String subjectName) {
				assertEquals(theCreateSubjectName, subjectName);
				return super.ldapSearch_Internal(subjectName);
			}
			
			protected void doAssertions(Collection<X509Certificate> getCertificates)
				throws Exception {
				assertEquals(1, ldapSearchCalls);
			}
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testLdapSearchReturnsNonEmptyCollection_AddOrUpdateLocalStoreDelegateMethodIsCalled() throws Exception {
		new CacheIsNull() {
			
			protected Collection<X509Certificate> ldapSearch_Internal(
					String subjectName) {
				theLdapSearch = new ArrayList<X509Certificate>();
				try {
					X509CertificateEx internalCert = TestUtils.getInternalCert("user1");
					theLdapSearch.add(internalCert);
				} catch (Exception e) {
					e.printStackTrace();
					fail();
				}
				return theLdapSearch;
			}
			
			protected void addOrUpdateLocalStoreDelegate_Internal(
					Collection<X509Certificate> retVal) {
				assertEquals(theLdapSearch, retVal);
			}
			
			protected void doAssertions(Collection<X509Certificate> getCertificates)
				throws Exception {
				assertEquals(1, addOrUpdateLocalStoreDelegateCalls);
				assertEquals(theLdapSearch, getCertificates);
			}
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testLdapGetWildcardReturnsNonEmptyCollection_AddOrUpdateLocalStoreDelegateMethodNoCalled() throws Exception {
		new CacheIsNull() {
			
			protected Collection<X509Certificate> ldapSearch_Internal(
					String subjectName) {
				theLdapSearch = new ArrayList<X509Certificate>();
				try {
					X509CertificateEx internalCert = TestUtils.getInternalCert("user1");
					theLdapSearch.add(internalCert);
				} catch (Exception e) {
					e.printStackTrace();
					fail();
				}
				return theLdapSearch;
			}
			
			protected void addOrUpdateLocalStoreDelegate_Internal(
					Collection<X509Certificate> retVal) {
				assertEquals(theLdapSearch, retVal);
			}
			
			protected String createSubjectName() throws Exception {
				theCreateSubjectName = "*";
				return theCreateSubjectName;
			}
			
			protected void doAssertions(Collection<X509Certificate> getCertificates)
				throws Exception {
				assertEquals(0, addOrUpdateLocalStoreDelegateCalls);
				assertEquals(theLdapSearch, getCertificates);
			}
		}.perform();
	}	
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testLdapSearchReturnsEmptyCollection_GetsCertificatesFromBootstrapStore() throws Exception {
		new CacheIsNull() {
			
			protected Collection<X509Certificate> ldapSearch_Internal(
					String subjectName) {
				theLdapSearch = new ArrayList<X509Certificate>();
				return theLdapSearch;
			}
			
			protected Collection<X509Certificate> getCertificates_Internal(
					String subjectName) {
				assertEquals(theCreateSubjectName, subjectName);
				return super.getCertificates_Internal(subjectName);
			}
			
			protected void doAssertions(Collection<X509Certificate> getCertificates)
				throws Exception {
				assertEquals(0, addOrUpdateLocalStoreDelegateCalls);
				assertEquals(1, getCertificatesCalls);
				assertEquals(theGetCertificates, getCertificates);
			}
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testBootstrapStoreIsNull_AddOrUpdateLocalStoreDelegateIsNotCalled() throws Exception {
		new CacheIsNull() {
			
			protected CertificateStore createBootstrapStore() {
				theCreateBootstrapStore = null;
				return theCreateBootstrapStore;
			}
			
			protected CertificateStore createDefaultLocalStore_Internal(){
				  theCreateDefaultLocalStore = null;
				  return theCreateDefaultLocalStore;
			}
			
			protected Collection<X509Certificate> ldapSearch_Internal(
					String subjectName) {
				theLdapSearch = new ArrayList<X509Certificate>();
				try {
					X509CertificateEx internalCert = TestUtils.getInternalCert("user1");
					theLdapSearch.add(internalCert);
				} catch (Exception e) {
					e.printStackTrace();
					fail();
				}
				return theLdapSearch;
			}
			
			protected void doAssertions(Collection<X509Certificate> getCertificates)
				throws Exception {
				assertEquals(1, ldapSearchCalls);
				assertEquals(0, addOrUpdateLocalStoreDelegateCalls);
			}
			
		}.perform();
	}
	
	class CacheIsNotNull extends TestPlan {
		protected JCS getCache_Internal() {
			theGetCache = new JCSAdapter(null) {

				@Override 
				public Object get(Object name){
					  getCalls++;
					  return get_Internal(name);
				}
			};
			return theGetCache;
		}
		
		protected List<X509Certificate> theGet;
		protected int getCalls=0;
		protected Object get_Internal(Object name){
			theGet = new ArrayList<X509Certificate>();
			try {
				X509CertificateEx internalCert = TestUtils.getInternalCert("user1");
				theGet.add(internalCert);
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}		  
			return theGet;
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testCacheIsNotNull_AttemptsToGetCertificatesFromCache() throws Exception {
		new CacheIsNotNull() {
			
			protected void doAssertions(Collection<X509Certificate> getCertificates)
				throws Exception {
				assertEquals(1, getCalls);
				assertEquals(theGet, getCertificates);
			}
		}.perform();
	}
	
	class CertificateIsNotPresentInCache extends TestPlan {
		protected JCS getCache_Internal() {
			theGetCache = new JCSAdapter(null) {

				@Override 
				public Object get(Object name){
					  getCalls++;
					  return get_Internal(name);
				}

				@Override 
				public void putSafe(Object key,Object value) throws CacheException {
					  putSafeCalls++;
					  putSafe_Internal(key,value);
				}
			};
			return theGetCache;
		}
		
		protected Object theGet;
		protected int getCalls=0;
		protected Object get_Internal(Object name){
			theGet = new ArrayList<X509Certificate>();
			return theGet;
		}
		
		protected int putSafeCalls=0;
		protected void putSafe_Internal(Object key,Object value) throws CacheException {
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testCertificateIsNotInCache_CertififcateIsRetrievedFromLdapServer() throws Exception {
		new CertificateIsNotPresentInCache() {
			
			protected void doAssertions(Collection<X509Certificate> getCertificates)
			throws Exception {
				assertEquals(1, ldapSearchCalls);
				assertNotSame(theGet, getCertificates);
			}
			
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testCertificateIsRetrievedFromLdapServerAndAddedToCache() throws Exception {
		new CertificateIsNotPresentInCache() {
			
			protected Collection<X509Certificate> ldapSearch_Internal(
					String subjectName) {
				theLdapSearch = new ArrayList<X509Certificate>();
				try {
					X509CertificateEx internalCert = TestUtils.getInternalCert("user1");
					theLdapSearch.add(internalCert);
				} catch (Exception e) {
					e.printStackTrace();
					fail();
				}
				return theLdapSearch;
			}
			
			protected void addOrUpdateLocalStoreDelegate_Internal(
					Collection<X509Certificate> retVal) {
				assertEquals(theLdapSearch, retVal);
			}
			
			protected void doAssertions(Collection<X509Certificate> getCertificates)
				throws Exception {
				assertEquals(1, putSafeCalls);
				assertEquals(1, ldapSearchCalls);
				assertEquals(1, addOrUpdateLocalStoreDelegateCalls);
				assertEquals(theLdapSearch, getCertificates);
			}
			
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testCertificateIsRetrievedFromLdapServerAddOrUpdateLocalStoreDelegateMethodIsCalled() throws Exception {
		new CertificateIsNotPresentInCache() {
			
			protected void putSafe_Internal(Object key,Object value) throws CacheException {
				assertEquals(theCreateSubjectName, key);
			}
			
			protected Collection<X509Certificate> ldapSearch_Internal(
					String subjectName) {
				theLdapSearch = new ArrayList<X509Certificate>();
				try {
					X509CertificateEx internalCert = TestUtils.getInternalCert("user1");
					theLdapSearch.add(internalCert);
				} catch (Exception e) {
					e.printStackTrace();
					fail();
				}
				return theLdapSearch;
			}
			
			protected void doAssertions(Collection<X509Certificate> getCertificates)
				throws Exception {
				assertEquals(1, putSafeCalls);
				assertEquals(1, ldapSearchCalls);
				assertNotSame(theGet, getCertificates);
			}
			
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testLocalStoreDelegateIsNull_AddOrUpdateLocalStoreDelegateIsNotCalled() throws Exception {
		new CertificateIsNotPresentInCache() {
			
			protected CertificateStore createBootstrapStore() {
				theCreateBootstrapStore = null;
				return theCreateBootstrapStore;
			}
			
			protected CertificateStore createDefaultLocalStore_Internal(){
				  theCreateDefaultLocalStore = null;
				  return theCreateDefaultLocalStore;
			}
			
			protected void putSafe_Internal(Object key,Object value) throws CacheException {
				assertEquals(theCreateSubjectName, key);
			}
			
			protected Collection<X509Certificate> ldapSearch_Internal(
					String subjectName) {
				theLdapSearch = new ArrayList<X509Certificate>();
				try {
					X509CertificateEx internalCert = TestUtils.getInternalCert("user1");
					theLdapSearch.add(internalCert);
				} catch (Exception e) {
					e.printStackTrace();
					fail();
				}
				return theLdapSearch;
			}
			
			protected void doAssertions(Collection<X509Certificate> getCertificates)
				throws Exception {
				assertEquals(1, putSafeCalls);
				assertEquals(1, ldapSearchCalls);
				assertEquals(0, addOrUpdateLocalStoreDelegateCalls);
			}
			
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testLdapSearchReturnsNullCollection_GetsCertificatesFromBootstrapStore() throws Exception {
		new CertificateIsNotPresentInCache() {
			
			protected Collection<X509Certificate> ldapSearch_Internal(
					String subjectName) {
				theLdapSearch = null;
				return theLdapSearch;
			}
			
			protected Collection<X509Certificate> getCertificates_Internal(
					String subjectName) {
				assertEquals(theCreateSubjectName, subjectName);
				return super.getCertificates_Internal(subjectName);
			}
			
			protected void doAssertions(Collection<X509Certificate> getCertificates)
				throws Exception {
				assertEquals(0, putSafeCalls);
				assertEquals(1, getCertificatesCalls);
				assertEquals(theGetCertificates, getCertificates);
			}
			
		}.perform();
	}
}