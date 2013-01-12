package org.nhindirect.gateway.smtp.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

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

/**
 * Generated test case.
 * 
 * @author junit_generate
 */
public class XMLSmtpAgentConfig_BuildTrustAnchorResolver_Test extends TestCase {
    abstract class TestPlan extends BaseTestPlan {
        protected void performInner() throws Exception {
        	
        	CertCacheFactory.getInstance().flushAll();
        	
            XMLSmtpAgentConfig impl = createXMLSmtpAgentConfig();
            try{
                impl.buildTrustAnchorResolver(createAnchorStoreNode(), createAnchorHolder(), createAnchorHolder());
                doAssertions();
            } catch(Exception e){
                doExceptionAssertions(e);
            }            
        }

        protected XMLSmtpAgentConfig createXMLSmtpAgentConfig() {
            XMLSmtpAgentConfig config = new XMLSmtpAgentConfig(TestUtils.getTestConfigFile("ValidConfig.xml"), null) {

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
            return new LDAPCertificateStore(){
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
        
        protected Collection<X509Certificate> createX509Certificates() throws CertificateException, IOException{
            InputStream certificate = this.getClass().getResourceAsStream( "/x509Certificate.txt" );
            Collection<X509Certificate> certs = new ArrayList<X509Certificate>();
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            String certificateString = Streams.asString(certificate).replaceAll("\r", "");          
            InputStream is = new ByteArrayInputStream(certificateString.getBytes("ASCII"));
            X509Certificate cert = (X509Certificate) cf.generateCertificate(is);            
            certs.add(cert);
            return certs;
        }

        protected Element theCreateAnchorStoreNode;

        protected Element createAnchorStoreNode() throws Exception {
            theCreateAnchorStoreNode = new ElementAdapter() {   
                @Override
                public String getAttribute(String name) {
                    if (name.equals("storeType")) {
                        return createStoreType();
                    } else if (name.equals("type")) {
                        return createTrustAnchorType();
                    } 
                    else if (name.equals("file")) {
                        return "KeyStore";
                    }
                    else if (name.equals("filePass")) {
                        return "h3||0 wor|d";
                    }
                    else if (name.equals("privKeyPass")) {
                        return "pKpa$$wd";
                    }else {
                        return "";
                    }
                }
            };
            return theCreateAnchorStoreNode;
        }
        
        protected String createStoreType(){
            return "LDAP";
        }
        
        protected String createTrustAnchorType(){
            return "uniform";
        }

        protected Map<String, Collection<String>> theCreateIncomingAnchorHolder;

        @SuppressWarnings({ "unchecked", "serial" })
        protected Map<String, Collection<String>> createAnchorHolder() throws Exception {
            theCreateIncomingAnchorHolder = new HashMap(){
                @Override
                public Set<Entry<String, Collection<String>>>  entrySet() {  
                    Entry entry = new Map.Entry<String, Collection<String>>(){
                        public String getKey() {                            
                            return null;
                        }
                        public Collection<String> getValue() { 
                            Collection<String> coll = new ArrayList<String>();
                            coll.add("cacert");
                            return coll;
                        }
                        public Collection<String> setValue(Collection<String> value) {                           
                            return null;
                        }                       
                    };
                    HashSet hs = new HashSet<Entry<String, Collection<String>>>();
                    hs.add(entry);
                    return hs;
                }
                
            };
            return theCreateIncomingAnchorHolder;
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
    public void testLdapUniformTrustAnchor() throws Exception {
        new TestPlan() {
            @Override
            protected String createStoreType(){
                return "LDAP";
            }
            
            @Override
            protected String createTrustAnchorType(){
                return "uniform";
            }
            
            @Override
            protected void doAssertions() throws Exception {
            }
        }.perform();
    }
    
    /**
     * 
     * @throws Exception
     */
    public void testLdapMultiDomainTrustAnchor() throws Exception {
        new TestPlan() {
            @Override
            protected String createStoreType(){
                return "LDAP";
            }
            
            @Override
            protected String createTrustAnchorType(){
                return "multidomain";
            }
            
            @Override
            protected void doAssertions() throws Exception {
            }
        }.perform();
    }
    
    /**
     * 
     * @throws Exception
     */
    public void testKeyStoreUniformTrustAnchor() throws Exception {
        new TestPlan() {
            @Override
            protected String createStoreType(){
                return "keystore";
            }
            
            @Override
            protected String createTrustAnchorType(){
                return "uniform";
            }
            
            @Override
            protected void doAssertions() throws Exception {
            }
        }.perform();
    }
    
    /**
     * 
     * @throws Exception
     */
    public void testKeyStoreMultiDomainTrustAnchor() throws Exception {
        new TestPlan() {
            @Override
            protected String createStoreType(){
                return "keystore";
            }
            
            @Override
            protected String createTrustAnchorType(){
                return "multidomain";
            }
            
            @Override
            protected void doAssertions() throws Exception {
            }
        }.perform();
    }
    
    /**
     * 
     * @throws Exception
     */
    public void testSmtpAgentException() throws Exception {
        new TestPlan() {       
            
            @Override
            protected String createTrustAnchorType(){
                return "bad";
            }
            
            @Override
            protected void doExceptionAssertions(Exception e) throws Exception {
                assertTrue(e instanceof SmtpAgentException);
            }
            
            @Override
            protected void doAssertions() throws Exception {
                fail();
            }
        }.perform();
    }
}