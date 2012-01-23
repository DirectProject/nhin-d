package org.nhindirect.gateway.smtp.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.apache.commons.fileupload.util.Streams;
import org.nhindirect.gateway.smtp.SmtpAgentException;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.ElementAdapter;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.stagent.cert.CertCacheFactory;
import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.cert.impl.LDAPCertificateStore;
import org.nhindirect.stagent.cert.impl.provider.LdapCertificateStoreProvider;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Generated test case.
 * 
 * @author junit_generate
 */
public class XMLSmtpAgentConfig_BuildPrivateCertStore_Test extends TestCase {
    abstract class TestPlan extends BaseTestPlan {
        @Override
        protected void performInner() throws Exception 
        {
        	CertCacheFactory.getInstance().flushAll();
        	
            XMLSmtpAgentConfig impl = createXMLSmtpAgentConfig();
            try {
                impl.buildPrivateCertStore(createPublicCertNode());
                doAssertions();
            } catch (Exception e) {
                doExceptionAssertions(e);
            }
        }

        protected XMLSmtpAgentConfig config;

        protected XMLSmtpAgentConfig createXMLSmtpAgentConfig() {
            config = new XMLSmtpAgentConfig(TestUtils.getTestConfigFile("ValidConfig.xml"), null) {
                @Override
                protected LdapCertificateStoreProvider buildLdapCertificateStoreProvider(Element anchorStoreNode, String cacheStoreName) {
                    return createLdapCertificateStoreProvider();
                }

            };
            return config;
        }

        protected LdapCertificateStoreProvider createLdapCertificateStoreProvider() {
            return new LdapCertificateStoreProvider(null, null, null) {
                @Override
                public CertificateResolver get() {
                    return createCertificateResolver();
                }
            };
        }

        protected CertificateResolver createCertificateResolver() {
            return new LDAPCertificateStore() {
                public Collection<X509Certificate> getCertificates(String string) {
                    try {
                        return createX509Certificates();
                    } catch (CertificateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
        }

        protected Collection<X509Certificate> createX509Certificates() throws CertificateException, IOException {
            InputStream certificate = this.getClass().getResourceAsStream("/x509Certificate.txt");
            Collection<X509Certificate> certs = new ArrayList<X509Certificate>();
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            String certificateString = Streams.asString(certificate).replaceAll("\r", "");
            InputStream is = new ByteArrayInputStream(certificateString.getBytes("ASCII"));
            X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
            certs.add(cert);
            return certs;
        }

        protected Node theCreatePublicCertNode;

        protected Node createPublicCertNode() throws Exception {
            theCreatePublicCertNode = new ElementAdapter() {
                @Override
                public short getNodeType() {
                    return Node.ELEMENT_NODE;
                }

                @Override
                public String getAttribute(String name) {
                    if (name.equals("type")) {
                        return createTrustAnchorType();
                    } else if (name.equals("file")) {
                        return "KeyStore";
                    } else if (name.equals("filePass")) {
                        return "h3||0 wor|d";
                    } else if (name.equals("privKeyPass")) {
                        return "pKpa$$wd";
                    } else {
                        return "";
                    }
                }
            };
            return theCreatePublicCertNode;
        }

        protected String createTrustAnchorType() {
            return "keystore";
        }

        protected void doAssertions() throws Exception {
        }

        protected void doExceptionAssertions(Exception e) throws Exception {
        }
    }

    /**
     * 
     * @throws Exception
     */
    public void testKeyStore() throws Exception {
        new TestPlan() {
            @Override
            protected String createTrustAnchorType() {
                return "keystore";
            }

            @Override
            protected void doAssertions() throws Exception {
                assertNotNull(config.privateCertModule);
            }
        }.perform();
    }

    /**
     * 
     * @throws Exception
     */
    public void testLdap() throws Exception {
        new TestPlan() {
            @Override
            protected String createTrustAnchorType() {
                return "LDAP";
            }

            @Override
            protected void doAssertions() throws Exception {
                assertNotNull(config.privateCertModule);
            }
        }.perform();
    }
    
    /**
     * 
     * @throws Exception
     */
    public void testSMTPExceptionThrown() throws Exception {
        new TestPlan() {
            @Override
            protected String createTrustAnchorType() {
                return "bad";
            }

            @Override
            protected void doAssertions() throws Exception {
                fail();
            }
            @Override
            protected void doExceptionAssertions(Exception e) throws Exception {
                assertTrue(e instanceof SmtpAgentException);
            }
        }.perform();
    }
    
   
}