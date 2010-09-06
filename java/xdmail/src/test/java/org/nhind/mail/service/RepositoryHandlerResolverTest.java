package org.nhind.mail.service;

import java.util.List;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.PortInfo;

import junit.framework.TestCase;

/**
 * Test class for methods in RepositoryHandlerResolver.
 * 
 * @author beau
 */
public class RepositoryHandlerResolverTest extends TestCase {

    /**
     * Constructor
     * 
     * @param testName
     *            The test name
     */
    public RepositoryHandlerResolverTest(String testName) {
        super(testName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test the getHandlerChain method.
     */
    public void testGetHandlerChain() {
        PortInfo portInfo = null;
        List<Handler> output = null;
        RepositoryHandlerResolver handler = new RepositoryHandlerResolver();

        output = handler.getHandlerChain(portInfo);

        assertNotNull("List is null", output);
        assertTrue("List contains 0 elements", !output.isEmpty());
        assertEquals("List contains more than expected elements", 1, output.size());
        assertTrue("List does not contain expected element", output.get(0) instanceof RepositorySOAPHandler);
    }
}
