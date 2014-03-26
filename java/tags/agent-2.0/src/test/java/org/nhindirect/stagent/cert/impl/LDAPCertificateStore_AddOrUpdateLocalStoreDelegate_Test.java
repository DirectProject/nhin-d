package org.nhindirect.stagent.cert.impl;


import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import junit.framework.TestCase;
import org.nhindirect.stagent.cert.CertStoreCachePolicy;
import org.nhindirect.stagent.cert.CertificateStore;
import org.nhindirect.stagent.cert.impl.LDAPCertificateStore;
import org.nhindirect.stagent.utils.BaseTestPlan;

/**
 * Generated test case.
 * @author junit_generate
 */
public class LDAPCertificateStore_AddOrUpdateLocalStoreDelegate_Test extends
		TestCase {
	abstract class TestPlan extends BaseTestPlan {
		@Override
		protected void performInner() throws Exception {
			LDAPCertificateStore impl = createLDAPCertificateStore();
			impl.addOrUpdateLocalStoreDelegate(createRetVal());
			doAssertions();
		}

		protected LDAPCertificateStore createLDAPCertificateStore()
				throws Exception {
			return new LDAPCertificateStore((LdapCertUtilImpl) null,
					createBootstrapStore(), (CertStoreCachePolicy) null) {
			};
		}

		protected Collection<X509Certificate> theCreateRetVal;

		protected Collection<X509Certificate> createRetVal() throws Exception {
			theCreateRetVal = new ArrayList<X509Certificate>();
			return theCreateRetVal;
		}

		protected CertificateStore theCreateBootstrapStore;
		protected int containsCalls = 0;
		protected boolean theContains;
		protected int updateCalls = 0;
		protected int addCalls = 0;

		protected boolean contains_Internal(X509Certificate cert) {
			theContains = false;
			return theContains;
		}

		protected void update_Internal(X509Certificate cert) {
		}

		protected void add_Internal(X509Certificate cert) {
		}

		protected CertificateStore createBootstrapStore() {
			theCreateBootstrapStore = new KeyStoreCertificateStore() {
				@Override
				public boolean contains(X509Certificate cert) {
					containsCalls++;
					return contains_Internal(cert);
				}

				@Override
				public void update(X509Certificate cert) {
					updateCalls++;
					update_Internal(cert);
				}

				@Override
				public void add(X509Certificate cert) {
					addCalls++;
					add_Internal(cert);
				}
			};
			return theCreateBootstrapStore;
		}

		protected void doAssertions() throws Exception {
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testLocalStoreDelegateIsNull_LocalStoreIsNotUpdated() throws Exception {
		new TestPlan() {
			protected CertificateStore createBootstrapStore() {
				theCreateBootstrapStore = null;
				return theCreateBootstrapStore;
			}
			
			protected void doAssertions() throws Exception {
				assertEquals(0, addCalls);
				assertEquals(0, updateCalls);
			}
		}.perform();
	}
}