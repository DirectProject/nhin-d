package org.nhind.xdr;

import junit.framework.TestCase;

public class DocumentRegistryTest extends TestCase {

    public DocumentRegistryTest(String testName) {
        super(testName);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testGetAuthorEmail() {
        String authorEmail = null;
        DocumentRegistry dr = new DocumentRegistry();
        
        dr.setAuthor("vincent.lewis@gsihealth.com^Allscripts^Provider^^^^^^&amp;1.3.6.1.4.1.21367.3100.1&amp;ISO");
        authorEmail = dr.getAuthorEmail();
        assertEquals("Email does not match expected value", "vincent.lewis@gsihealth.com", authorEmail);
        
        dr.setAuthor("nhin-d@nologs.org");
        authorEmail = dr.getAuthorEmail();
        assertEquals("Email does not match expected value", "nhin-d@nologs.org", authorEmail);
        
        dr.setAuthor("John Smith");
        authorEmail = dr.getAuthorEmail();
        assertEquals("Email does not match expected value", "postmaster@nhindirect.org", authorEmail);
        
        dr.setAuthor("");
        authorEmail = dr.getAuthorEmail();
        assertEquals("Email does not match expected value", "postmaster@nhindirect.org", authorEmail);
        
        dr.setAuthor(null);
        authorEmail = dr.getAuthorEmail();
        assertEquals("Email does not match expected value", "postmaster@nhindirect.org", authorEmail);
    }
    
}
