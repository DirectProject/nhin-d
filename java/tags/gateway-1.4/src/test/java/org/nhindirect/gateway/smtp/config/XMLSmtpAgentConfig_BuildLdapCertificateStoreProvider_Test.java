package org.nhindirect.gateway.smtp.config;


import junit.framework.TestCase;

import org.nhindirect.gateway.smtp.SmtpAgentException;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.ElementAdapter;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.stagent.cert.CertCacheFactory;
import org.nhindirect.stagent.cert.impl.provider.LdapCertificateStoreProvider;
import org.w3c.dom.Element;

/**
 * Generated test case.
 * @author junit_generate
 */
public class XMLSmtpAgentConfig_BuildLdapCertificateStoreProvider_Test extends TestCase {
    abstract class TestPlan extends BaseTestPlan {
        @Override
        protected void performInner() throws Exception 
        {
        	CertCacheFactory.getInstance().flushAll();
        	
            XMLSmtpAgentConfig impl = createXMLSmtpAgentConfig();
            try{
                LdapCertificateStoreProvider buildLdapCertificateStoreProvider = impl.buildLdapCertificateStoreProvider(createAnchorStoreNode(), createCacheStoreName());
                doAssertions(buildLdapCertificateStoreProvider);
            }
            catch(Exception e){
                doExceptionAssertions(e);
            }
            
        }

        protected XMLSmtpAgentConfig createXMLSmtpAgentConfig() {
            XMLSmtpAgentConfig config = new XMLSmtpAgentConfig(TestUtils.getTestConfigFile("ValidConfig.xml"), null);
            return config;
        }

        protected Element theCreateAnchorStoreNode;

        protected Element createAnchorStoreNode() throws Exception {
            theCreateAnchorStoreNode = new ElementAdapter(){
                @Override
                public String getAttribute(String name) {
                    if (name.equals("ldapURL")) {
                        return createLdapURL();
                    } 
                    else if (name.equals("ldapSearchBase")) {
                        return createLdapSearchBase();
                    } 
                    else if (name.equals("ldapSearchAttr")) {
                        return createLdapSearchAttr();
                    }
                    else if (name.equals("ldapCertAttr")) {
                        return createLdapCertAttr();
                    }
                    else if (name.equals("ldapCertFormat")) {
                        return createLdapCertFormat();
                    }
                    else if (name.equals("ldapUser")) {
                        return createLdapUser();
                    }
                    else if (name.equals("ldapPassword")) {
                        return createLdapPassword();
                    }
                    else if (name.equals("ldapConnTimeout")) {
                        return createLdapConnTimeout();
                    }
                    else if (name.equals("ldapCertPassphrase")) {
                        return createLdapCertPassphrase();
                    }
                    else {
                        return "";
                    }
                }
            };
            return theCreateAnchorStoreNode;
        }
        
        protected String createLdapURL(){
            return "";
        }
        
        protected String createLdapSearchBase(){
            return "";
        }
        
        protected String createLdapSearchAttr(){
            return "";
        }
        
        protected String createLdapCertAttr(){
            return "";
        }
        
        protected String createLdapCertFormat(){
            return "";
        }
        
        protected String createLdapUser(){
            return "";
        }
        
        protected String createLdapPassword(){
            return "";
        }
        
        protected String createLdapConnTimeout(){
            return "";
        }
        
        protected String createLdapCertPassphrase(){
            return "";
        }
        
        protected String createCacheStoreName(){
            return "LdapCacheStore";
        }

        protected void doAssertions(LdapCertificateStoreProvider buildLdapCertificateStoreProvider) throws Exception {
        }
        
        protected void doExceptionAssertions(Exception e) {            
        }
    }

    /**
     * 
     * @throws Exception
     */
    public void testAllRequiredParamsNotEmpty_AssertProviderNotNull() throws Exception {
        new TestPlan() {
            @Override
            protected String createLdapURL(){
                return "http://localhost";
            }
            @Override
            protected String createLdapSearchBase(){
                return "SearchBase";
            }
            @Override
            protected String createLdapSearchAttr(){
                return "SearchAttr";
            }
            @Override
            protected String createLdapCertAttr(){
                return "CertAttr";
            }
            @Override
            protected String createLdapCertFormat(){
                return "x509";
            }
            @Override
            protected void doAssertions(LdapCertificateStoreProvider buildLdapCertificateStoreProvider) throws Exception {
                assertNotNull(buildLdapCertificateStoreProvider);
            }
        }.perform();
    }
    
    /**
     * 
     * @throws Exception
     */
    public void testAnyRequiredParamEmpty_AssertSmtpAgentExceptionThrown() throws Exception {
        new TestPlan() {
            @Override
            protected String createLdapURL(){
                return "";
            }
            @Override
            protected String createLdapSearchBase(){
                return "SearchBase";
            }
            @Override
            protected String createLdapSearchAttr(){
                return "SearchAttr";
            }
            @Override
            protected String createLdapCertAttr(){
                return "CertAttr";
            }
            @Override
            protected String createLdapCertFormat(){
                return "x509";
            }            
            @Override
            protected void doExceptionAssertions(Exception e) {     
                assertTrue(e instanceof SmtpAgentException);
            }            
            @Override
            protected void doAssertions(LdapCertificateStoreProvider buildLdapCertificateStoreProvider) throws Exception {
                fail();
            }
        }.perform();
    }
    
    /**
     * 
     * @throws Exception
     */
    public void testIfCertFormatIsPKCS12AndPassphraseIsOk_AssertProviderNotNull() throws Exception {
        new TestPlan() {
            @Override
            protected String createLdapURL(){
                return "http://localhost";
            }
            @Override
            protected String createLdapSearchBase(){
                return "SearchBase";
            }
            @Override
            protected String createLdapSearchAttr(){
                return "SearchAttr";
            }
            @Override
            protected String createLdapCertAttr(){
                return "CertAttr";
            }
            @Override
            protected String createLdapCertFormat(){
                return "pkcs12";
            }  
            @Override
            protected String createLdapCertPassphrase(){
                return "passphrase";
            }                      
            @Override
            protected void doAssertions(LdapCertificateStoreProvider buildLdapCertificateStoreProvider) throws Exception {
               assertNotNull(buildLdapCertificateStoreProvider);
            }
        }.perform();
}
        
        /**
         * 
         * @throws Exception
         */
        public void testIfCertFormatIsPKCS12AndPassphraseIsEmpty_AssertSmtpExceptionThrown() throws Exception {
            new TestPlan() {
                @Override
                protected String createLdapURL(){
                    return "http://localhost";
                }
                @Override
                protected String createLdapSearchBase(){
                    return "SearchBase";
                }
                @Override
                protected String createLdapSearchAttr(){
                    return "SearchAttr";
                }
                @Override
                protected String createLdapCertAttr(){
                    return "CertAttr";
                }
                @Override
                protected String createLdapCertFormat(){
                    return "pkcs12";
                }            
                @Override
                protected void doExceptionAssertions(Exception e) {     
                    assertTrue(e instanceof SmtpAgentException);
                }            
                @Override
                protected void doAssertions(LdapCertificateStoreProvider buildLdapCertificateStoreProvider) throws Exception {
                    fail();
                }
            }.perform();
    }
    
    
}