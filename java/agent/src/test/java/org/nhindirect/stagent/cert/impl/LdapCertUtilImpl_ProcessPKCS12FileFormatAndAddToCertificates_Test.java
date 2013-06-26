package org.nhindirect.stagent.cert.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.String;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.cert.impl.LdapCertUtilImpl;
import org.nhindirect.stagent.utils.BaseTestPlan;

/**
 * Generated test case.
 * @author junit_generate
 */
public class LdapCertUtilImpl_ProcessPKCS12FileFormatAndAddToCertificates_Test
		extends TestCase {
	abstract class TestPlan extends BaseTestPlan {
		@Override
		protected void performInner() throws Exception {
			LdapCertUtilImpl impl = createLdapCertUtilImpl();
			impl.processPKCS12FileFormatAndAddToCertificates(
					createInputStream(), createCertificates());
			doAssertions();
		}

		protected LdapCertUtilImpl createLdapCertUtilImpl() throws Exception {
			return new LdapCertUtilImpl((LdapEnvironment) null,
					createKeyStorePassword(), (String) null) {
			};
		}

		protected ByteArrayInputStream theCreateInputStream;

		protected ByteArrayInputStream createInputStream() throws Exception {
			File fl = new File("testfile");
			int idx = fl.getAbsolutePath().lastIndexOf("testfile");
			String path = fl.getAbsolutePath().substring(0, idx);
			
			byte[] buffer = new byte[(int) new File(path + "src/test/resources/certs/gm2552encrypted.p12").length()+100];
			BufferedInputStream f = new BufferedInputStream(new FileInputStream(path + "src/test/resources/certs/gm2552encrypted.p12"));
			f.read(buffer);
			theCreateInputStream = new ByteArrayInputStream(buffer);
			IOUtils.closeQuietly(f);
			return theCreateInputStream;
		}

		protected ArrayList<X509Certificate> theCreateCertificates;

		protected ArrayList<X509Certificate> createCertificates()
				throws Exception {
			theCreateCertificates = new ArrayList<X509Certificate>();
			return theCreateCertificates;
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

		protected void doAssertions() throws Exception {
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testPKCS12File() throws Exception {
		new TestPlan() {
			protected void doAssertions() throws Exception {
				assertNotNull(theCreateCertificates);
				assertEquals(1, theCreateCertificates.size());
				X509Certificate cert = theCreateCertificates.iterator().next();
				assertTrue(cert instanceof X509CertificateEx);
				X509CertificateEx privateCert = (X509CertificateEx) cert;
				assertNotNull(privateCert.getPrivateKey());
				System.out.println(privateCert.getIssuerDN());
				assertEquals("EMAILADDRESS=gm2552@securehealthemail.com, CN=Greg Meyer, OU=Medical Informatics, O=Cerner, ST=Missouri, C=US", privateCert.getSubjectDN().toString());
				System.out.println(privateCert.getIssuerX500Principal());
			}
		}.perform();
	}
}