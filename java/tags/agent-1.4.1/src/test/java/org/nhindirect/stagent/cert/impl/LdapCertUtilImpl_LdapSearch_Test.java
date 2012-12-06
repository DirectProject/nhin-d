package org.nhindirect.stagent.cert.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.String;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import junit.framework.TestCase;

import org.apache.commons.codec.binary.Base64;
import org.nhindirect.stagent.cert.impl.LdapCertUtilImpl;
import org.nhindirect.stagent.cert.impl.LdapEnvironment;
import org.nhindirect.stagent.utils.BaseTestPlan;

/**
 * Generated test case.
 * @author junit_generate
 */
public class LdapCertUtilImpl_LdapSearch_Test extends TestCase {
	abstract class TestPlan extends BaseTestPlan {
		@Override
		protected void performInner() throws Exception {
			LdapCertUtilImpl impl = createLdapCertUtilImpl();
			Collection<X509Certificate> ldapSearch = impl
					.ldapSearch(createSubjectName());
			doAssertions(ldapSearch);
		}

		protected LdapCertUtilImpl createLdapCertUtilImpl() throws Exception {
			return new LdapCertUtilImpl(createLdapEnvironment(), createKeyStorePassword(),
					createCertificateFormat()) {
				@Override
				protected InitialDirContext getInitialDirContext(
						Hashtable<String, String> env) throws NamingException {
					getInitialDirContextCalls++;
					return getInitialDirContext_Internal(env);
				}

				@Override
				protected void closeDirContext(DirContext dirContext) {
					closeDirContextCalls++;
					closeDirContext_Internal(dirContext);
				}

				@Override
				protected void processPKCS12FileFormatAndAddToCertificates(
						ByteArrayInputStream inputStream,
						ArrayList<X509Certificate> certificates)
						throws KeyStoreException, NoSuchAlgorithmException,
						CertificateException, IOException,
						UnrecoverableKeyException {
					processPKCS12FileFormatAndAddToCertificatesCalls++;
					processPKCS12FileFormatAndAddToCertificates_Internal(
							inputStream, certificates);
				}
				
				
			};
		}

		protected InitialDirContext theGetInitialDirContext;
		protected int getInitialDirContextCalls = 0;

		protected InitialDirContext getInitialDirContext_Internal(
				Hashtable<String, String> env) throws NamingException {
			theGetInitialDirContext = new InitialDirContext() {

				@Override 
				public NamingEnumeration<SearchResult> search(String name,String filter,SearchControls cons) throws NamingException {
					  searchCalls++;
					  return search_Internal(name,filter,cons);
				}
			};
			return theGetInitialDirContext;
		}
		
		protected NamingEnumeration<SearchResult> theSearch;
		protected int searchCalls=0;
		protected NamingEnumeration<SearchResult> search_Internal(String name,String filter,SearchControls cons) throws NamingException {
		  theSearch=new NamingEnumeration<SearchResult>() {

			public void close() throws NamingException {
			}
			
			public boolean hasMore() throws NamingException {
				  hasMoreCalls++;
				  return hasMore_Internal();
			}

			public SearchResult next() throws NamingException {
				  nextCalls++;
				  return next_Internal();
			}
			public boolean hasMoreElements() {
				hasMoreElementsCalls++;
				return hasMoreElements_Internal();
			}

			
			public SearchResult nextElement(){
				nextElementCalls_SearchResult++;
				return nextElement_SearchResult_Internal();
			}
			  
		  };
		  return theSearch;
		}
		
		protected boolean theHasMore;
		protected int hasMoreCalls=0;
		protected boolean hasMore_Internal() throws NamingException {
			if(hasMoreCalls==1) {
				theHasMore=true;
			}
			else {
				theHasMore=false;
			}
		  return theHasMore;
		}
		
		protected SearchResult theNextElement_SearchResult;
		protected int nextElementCalls_SearchResult=0;
		
		@SuppressWarnings("serial")
		protected SearchResult nextElement_SearchResult_Internal(){
			theNextElement_SearchResult=new SearchResult("",null,new AttributesAdapter()) {

				@Override 
				public Attributes getAttributes(){
					  getAttributesCalls++;
					  return getAttributes_Internal();
				}			  
			  };
			return theNextElement_SearchResult;
		}
		
		protected SearchResult theNext;
		protected int nextCalls=0;
		
		@SuppressWarnings("serial")
		protected SearchResult next_Internal() throws NamingException {
		  theNext=new SearchResult("",null,new AttributesAdapter()) {

			@Override 
			public Attributes getAttributes(){
				  getAttributesCalls++;
				  return getAttributes_Internal();
			}			  
		  };
		  return theNext;
		}
		
		protected Attributes theGetAttributes;
		protected int getAttributesCalls=0;
		
		@SuppressWarnings("serial")
		protected Attributes getAttributes_Internal(){
		  theGetAttributes=new AttributesAdapter() {
			  
			@Override 
			public Attribute get(String attrID){
				getCallsByAttrId++;
				  return get_Internal(attrID);
			}
			  
		  };
		  return theGetAttributes;
		}
		
		protected Attribute theGetByAttrId;
		protected int getCallsByAttrId=0;
		
		@SuppressWarnings("serial")
		protected Attribute get_Internal(String attrID){
			theGetByAttrId=new AttributeAdapter() {
  
			@Override 
			public NamingEnumeration<?> getAll() throws NamingException {
				  getAllCalls++;
				  return getAll_Internal();
			}
			
		  };
		  return theGetByAttrId;
		}
		
		protected NamingEnumeration<?> theGetAll;
		protected int getAllCalls=0;
		protected NamingEnumeration<?> getAll_Internal() throws NamingException {
		  theGetAll=new NamingEnumeration<Object>() {
			public void close() throws NamingException {
			}
				
			public boolean hasMore() throws NamingException {
				  hasMoreCalls++;
				  return hasMore_Internal();
			}

			public SearchResult next() throws NamingException {
				  nextCalls++;
				  return next_Internal();
			}
			
			public boolean hasMoreElements(){
				  hasMoreElementsCalls++;
				  return hasMoreElements_Internal();
			}

			
			public Object nextElement(){
				  nextElementCalls++;
				  return nextElement_Internal();
			}

		  };
		  return theGetAll;
		}
		
		protected boolean theHasMoreElements;
		protected int hasMoreElementsCalls=0;
		protected boolean hasMoreElements_Internal(){
			if(hasMoreElementsCalls==1 || hasMoreElementsCalls==2) {
				theHasMoreElements=true;
			}
			else {
				theHasMoreElements=false;
			}
		  return theHasMoreElements;
		}
		
		protected String theNextElement;
		protected int nextElementCalls=0;
		protected Object nextElement_Internal(){
		  theNextElement =  "";
		  return theNextElement;
		}

		protected int closeDirContextCalls = 0;

		protected void closeDirContext_Internal(DirContext dirContext) {
		}

		protected String theCreateSubjectName;

		protected String createSubjectName() throws Exception {
			theCreateSubjectName = "createSubjectName";
			return theCreateSubjectName;
		}

		protected LdapEnvironment theCreateLdapEnvironment;
		protected int getEnvCalls = 0;
		protected Hashtable<String, String> theGetEnv;
		protected int getLdapSearchBaseCalls = 0;
		protected String theGetLdapSearchBase;
		protected int getLdapSearchFilterCalls = 0;
		protected String theGetLdapSearchFilter;

		protected Hashtable<String, String> getEnv_Internal() {
			theGetEnv = new Hashtable<String, String>();
			return theGetEnv;
		}

		protected String theGetReturningCertAttribute;
		protected int getReturningCertAttributeCalls=0;
		protected String getReturningCertAttribute_Internal(){
		  theGetReturningCertAttribute="getReturningCertAttribute";
		  return theGetReturningCertAttribute;
		}

		protected String getLdapSearchBase_Internal() {
			theGetLdapSearchBase = "getLdapSearchBase";
			return theGetLdapSearchBase;
		}

		protected String getLdapSearchFilter_Internal() {
			theGetLdapSearchFilter = "getLdapSearchFilter";
			return theGetLdapSearchFilter;
		}

		protected LdapEnvironment createLdapEnvironment() {
			theCreateLdapEnvironment = new LdapEnvironment(
					(Hashtable<String, String>) null, (String) null,
					(String) null, (String) null) {
				@Override
				public Hashtable<String, String> getEnv() {
					getEnvCalls++;
					return getEnv_Internal();
				}

				@Override 
				public String getReturningCertAttribute(){
					  getReturningCertAttributeCalls++;
					  return getReturningCertAttribute_Internal();
				}

				@Override
				public String getLdapSearchBase() {
					getLdapSearchBaseCalls++;
					return getLdapSearchBase_Internal();
				}

				@Override 
				public String getLdapSearchAttribute(){
					  getLdapSearchAttributeCalls++;
					  return getLdapSearchAttribute_Internal();
				}

			};
			return theCreateLdapEnvironment;
		}
		
		protected String theGetLdapSearchAttribute;
		protected int getLdapSearchAttributeCalls=0;
		protected String getLdapSearchAttribute_Internal(){
		  theGetLdapSearchAttribute="getLdapSearchAttribute";
		  return theGetLdapSearchAttribute;
		}

		protected String theCreateLdapSearchTarget;

		protected String createLdapSearchTarget() {
			theCreateLdapSearchTarget = "createLdapSearchTarget";
			return theCreateLdapSearchTarget;
		}
		
		protected String theCreateKeyStorePassword;
		protected int toCharArrayCalls = 0;
		protected char[] theToCharArray;

		protected char[] toCharArray_Internal() {
			theToCharArray = new char[] {};
			return theToCharArray;
		}

		protected String createKeyStorePassword() {
			theCreateKeyStorePassword = "1kingpuff";
			return theCreateKeyStorePassword;
		}

		protected String theCreateCertificateFormat;
		protected int equalsIgnoreCaseCalls = 0;
		protected boolean theEqualsIgnoreCase;

		protected boolean equalsIgnoreCase_Internal(String anotherString) {
			theEqualsIgnoreCase = false;
			return theEqualsIgnoreCase;
		}

		protected String createCertificateFormat() {
			theCreateCertificateFormat = "pkcs12";
			return theCreateCertificateFormat;
		}
		
		protected int processPKCS12FileFormatAndAddToCertificatesCalls = 0;

		protected void processPKCS12FileFormatAndAddToCertificates_Internal(
				ByteArrayInputStream inputStream,
				ArrayList<X509Certificate> certificates)
				throws KeyStoreException, NoSuchAlgorithmException,
				CertificateException, IOException, UnrecoverableKeyException {
		}

		protected void doAssertions(Collection<X509Certificate> ldapSearch)
				throws Exception {
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testNoValuesForCertAttribute_ReturnsEmptyListofCertificates() throws Exception {
		new TestPlan() {
			
			protected boolean hasMoreElements_Internal(){
				if(hasMoreElementsCalls==1) {
					theHasMoreElements=true;
				}
				else {
					theHasMoreElements=false;
				}
				return theHasMoreElements;
			}
			
			protected void doAssertions(Collection<X509Certificate> ldapSearch)
				throws Exception {
				assertTrue(ldapSearch.isEmpty());
				assertTrue(hasMoreElementsCalls>0);
			}
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testSearchResultIsNull_ReturnsEmptyListofCertificates() throws Exception {
		new TestPlan() {
			protected NamingEnumeration<SearchResult> search_Internal(String name,String filter,SearchControls cons) throws NamingException {
				  theSearch=null;
				  return theSearch;
			}
			
			protected void doAssertions(Collection<X509Certificate> ldapSearch)
				throws Exception {
				assertTrue(ldapSearch.isEmpty());
			}
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testCertificateFormatIsPKCS12_processPKCS12FileFormatAndAddToCertificatesIsCalled() throws Exception {
		new TestPlan() {
			protected String createCertificateFormat() {
				theCreateCertificateFormat = "pkcs12";
				return theCreateCertificateFormat;
			}
			
			protected void doAssertions(Collection<X509Certificate> ldapSearch)
				throws Exception {
				assertEquals(1, processPKCS12FileFormatAndAddToCertificatesCalls);
			}
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testValueRetrievedFromLDAPIsDecoded() throws Exception {
		new TestPlan() {
			
			protected String value = "encode this value";
			protected String createCertificateFormat() {
				theCreateCertificateFormat = "pkcs12";
				return theCreateCertificateFormat;
			}
			
			protected Object nextElement_Internal(){
				Base64 base64 = new Base64();
				theNextElement = new String(base64.encode(value.getBytes()));
				System.out.println("encoded val"+theNextElement);
				return theNextElement;
			}
			
			protected void processPKCS12FileFormatAndAddToCertificates_Internal(
					ByteArrayInputStream inputStream,
					ArrayList<X509Certificate> certificates)
					throws KeyStoreException, NoSuchAlgorithmException,
					CertificateException, IOException, UnrecoverableKeyException {
				int size = inputStream.available();
			    byte[] bytes  = new byte[size];
			    inputStream.read(bytes, 0, size);
			    String input = new String(bytes);
			    assertEquals(value, input);
			    assertNotSame(theNextElement, input);
			}
			
			protected void doAssertions(Collection<X509Certificate> ldapSearch)
				throws Exception {
				assertEquals(1, processPKCS12FileFormatAndAddToCertificatesCalls);
			}
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testCertificateFormatIsX509_CertificateIsAddedCorrectly() throws Exception {
		new TestPlan() {
			
			protected Object nextElement_Internal() {
				try {
					File fl = new File("testfile");
					int idx = fl.getAbsolutePath().lastIndexOf("testfile");
					String path = fl.getAbsolutePath().substring(0, idx);
					
					byte[] buffer = new byte[(int) new File(path + "src/test/resources/certs/bob.der").length()+100];
					BufferedInputStream f = new BufferedInputStream(new FileInputStream(path + "src/test/resources/certs/bob.der"));
					f.read(buffer);
					Base64 base64 = new Base64();
					theNextElement = new String(base64.encode(buffer));
						
				} catch (Exception e) {
					e.printStackTrace();
					fail();
				}	
			  return theNextElement;
			}
			
			protected String createCertificateFormat() {
				theCreateCertificateFormat = "X509";
				return theCreateCertificateFormat;
			}
			
			protected void doAssertions(Collection<X509Certificate> ldapSearch)
				throws Exception {
				assertEquals(1, nextElementCalls);
				assertNotNull(ldapSearch);
				assertEquals(1, ldapSearch.size());
				X509Certificate cert = ldapSearch.iterator().next();
				assertTrue(cert instanceof X509Certificate);
				assertEquals("EMAILADDRESS=bob@nhind.hsgincubator.com, CN=Bob Patel, OU=Incubator, O=HSG, L=Redmond, ST=WA, C=US", cert.getSubjectX500Principal().toString());
			}
			
		}.perform();
	}
}