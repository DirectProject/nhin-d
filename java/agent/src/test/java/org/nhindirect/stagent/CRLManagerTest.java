package org.nhindirect.stagent;

import junit.framework.TestCase;

import org.nhindirect.stagent.cert.impl.CRLManager;
import org.nhindirect.stagent.cert.impl.KeyStoreCertificateStore;

/**
 * Test class for the CRLManager class.
 * 
 * TODO: Convert this test class to the test suite setup used by the other Agent
 * tests.
 * 
 * @author beau
 */
public class CRLManagerTest extends TestCase 
{
    private static final String KEY_STORE_PASSWORD = "h3||0 wor|d";
    private static final String PRIVATE_KEY_PASSWORD = "pKpa$$wd";

    /**
     * Test the CRLManager class with normal and non-normal input.
     */
    public void testCrlManager()
    {
        String internalKeystoreFile = this.getClass().getClassLoader().getResource("crl/crlKeystore").getPath();
        KeyStoreCertificateStore service = new KeyStoreCertificateStore(internalKeystoreFile, KEY_STORE_PASSWORD, PRIVATE_KEY_PASSWORD);

        CRLManager crlManager = null;
        String localCrlFile = "file://" + this.getClass().getClassLoader().getResource("crl/local.crl").getPath();

        crlManager = new CRLManager();
        assertEquals("Output does not match expected", false, crlManager.isRevoked(null));

        crlManager = new CRLManager(localCrlFile);
        assertEquals("Output does not match expected", false, crlManager.isRevoked(null));
        
        crlManager = new CRLManager();
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("nd")));
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("revoked-local")));
        assertEquals("Output does not match expected", true, crlManager.isRevoked(service.getByAlias("revoked-remote")));
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("gm2552")));
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("multiple")));

        crlManager = new CRLManager(localCrlFile);
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("nd")));
        assertEquals("Output does not match expected", true, crlManager.isRevoked(service.getByAlias("revoked-local")));
        assertEquals("Output does not match expected", true, crlManager.isRevoked(service.getByAlias("revoked-remote")));
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("gm2552")));
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("multiple")));

        internalKeystoreFile = this.getClass().getClassLoader().getResource("keystores/internalKeystore").getPath();
        service = new KeyStoreCertificateStore(internalKeystoreFile, KEY_STORE_PASSWORD, PRIVATE_KEY_PASSWORD);

        crlManager = new CRLManager();
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("nd")));
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("revoked-local")));
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("revoked-remote")));
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("gm2552")));
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("multiple")));

        crlManager = new CRLManager(localCrlFile);
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("nd")));
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("revoked-local")));
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("revoked-remote")));
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("gm2552")));
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("multiple")));

        internalKeystoreFile = this.getClass().getClassLoader().getResource("crl/multiCrlKeystore").getPath();
        service = new KeyStoreCertificateStore(internalKeystoreFile, KEY_STORE_PASSWORD, PRIVATE_KEY_PASSWORD);

        crlManager = new CRLManager();
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("nd")));
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("revoked-local")));
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("revoked-remote")));
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("gm2552")));
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("multiple")));

        crlManager = new CRLManager(localCrlFile);
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("nd")));
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("revoked-local")));
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("revoked-remote")));
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("gm2552")));
        assertEquals("Output does not match expected", false, crlManager.isRevoked(service.getByAlias("multiple")));
    }

}
