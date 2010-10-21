package org.nhindirect.xd.routing.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.util.AvailablePortFinder;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.nhind.config.Address;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.Domain;
import org.nhindirect.xd.routing.RoutingResolver;

/**
 * 
 * @author beau
 */
public class RoutingResolverImplTest extends TestCase
{
    private static Server server;
    private static int HTTPPort;
    private static String configServiceURL;

    private ConfigurationServiceProxy proxy;

    @SuppressWarnings("unused")
    private static final Log LOGGER = LogFactory.getFactory().getInstance(RoutingResolverImplTest.class);

    /**
     * Constructor.
     * 
     * @param testName
     *            The test name.
     */
    public RoutingResolverImplTest(String testName)
    {
        super(testName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    private void startService() throws Exception
    {
        /*
         * Setup the configuration service server
         */
        server = new Server();
        SocketConnector connector = new SocketConnector();

        HTTPPort = AvailablePortFinder.getNextAvailable(1024);
        connector.setPort(HTTPPort);

        WebAppContext context = new WebAppContext();

        context.setContextPath("/config");
        context.setServer(server);
        context.setWar("war/config-service.war");

        server.setSendServerVersion(false);
        server.addConnector(connector);
        server.addHandler(context);

        server.start();

        configServiceURL = "http://localhost:" + HTTPPort + "/config/ConfigurationService";

        proxy = new ConfigurationServiceProxy(configServiceURL);

        cleanConfig();
    }
    
    private void stopService() throws Exception
    {
        cleanConfig();

        server.stop();
        server = null;
    }
    
    private void cleanConfig() throws Exception
    {
        // clean addresses
        int addressCount = proxy.getAddressCount();
        Address[] addresses = proxy.listAddresss(null, addressCount);
        if (addresses != null)
            for (Address addr : addresses)
            {
                proxy.removeAddress(addr.getEmailAddress());
            }

        // clean domains
        int domainCount = proxy.getDomainCount();
        Domain[] doms = proxy.listDomains(null, domainCount);
        if (doms != null)
            for (Domain dom : doms)
            {
                proxy.removeDomain(dom.getDomainName());
            }
    }

    /**
     * Test the default resolver.
     */
    public void testDefaultResolver()
    {
        RoutingResolver resolver = new RoutingResolverImpl();

        List<String> smtpEndpoints = Arrays.asList("smtp@nologs.org", "smtp2@nologs.org");
        List<String> xdEndpoints = Arrays.asList("http://my.domain.com:8080/endpoint", "http://my.domain.com:8080/endpoint2");

        List<String> endpoints = new ArrayList<String>();
        endpoints.addAll(smtpEndpoints);
        endpoints.addAll(xdEndpoints);

        Collection<String> smtpResolved = resolver.getSmtpEndpoints(endpoints);
        Collection<String> xdResolved = resolver.getXdEndpoints(endpoints);

        assertEquals("List does not match expected size", 2, smtpResolved.size());
        assertTrue("List does not contain expected element", smtpResolved.contains(smtpEndpoints.get(0)));
        assertTrue("List does not contain expected element", smtpResolved.contains(smtpEndpoints.get(1)));

        assertEquals("List does not match expected size", 2, xdResolved.size());
        assertTrue("List does not contain expected element", xdResolved.contains(xdEndpoints.get(0)));
        assertTrue("List does not contain expected element", xdResolved.contains(xdEndpoints.get(1)));

        assertTrue("Output does not match expected", resolver.isSmtpEndpoint(smtpEndpoints.get(0)));
        assertTrue("Output does not match expected", resolver.isSmtpEndpoint(smtpEndpoints.get(1)));
        assertTrue("Output does not match expected", resolver.isXdEndpoint(xdEndpoints.get(0)));
        assertTrue("Output does not match expected", resolver.isXdEndpoint(xdEndpoints.get(1)));

        assertFalse("Output does not match expected", resolver.isSmtpEndpoint(xdEndpoints.get(0)));
        assertFalse("Output does not match expected", resolver.isSmtpEndpoint(xdEndpoints.get(1)));
        assertFalse("Output does not match expected", resolver.isXdEndpoint(smtpEndpoints.get(0)));
        assertFalse("Output does not match expected", resolver.isXdEndpoint(smtpEndpoints.get(1)));
    }

    /**
     * Test the resolver with a configuration service backing.
     * 
     * @throws Exception
     */
    public void testResolverWithConfigService() throws Exception
    {
        // TODO
        // incorporate test below
    }

    @SuppressWarnings("unused")
    private void todo() throws Exception
    {
        Address[] addrs = new Address[4];

        List<String> smtpEndpoints = Arrays.asList("smtp@nologs.org");
        List<String> xdEndpoints = Arrays.asList("xd@nologs.org");
        List<String> localEndpoints = Arrays.asList("local@nologs.org");
        List<String> emptyEndpoints = Arrays.asList("empty@nologs.org");

        List<String> endpoints = new ArrayList<String>();
        endpoints.addAll(smtpEndpoints);
        endpoints.addAll(xdEndpoints);
        endpoints.addAll(localEndpoints);
        endpoints.addAll(emptyEndpoints);

        // SMTP
        addrs[0] = new Address();
        addrs[0].setEmailAddress(smtpEndpoints.get(0));
        addrs[0].setDisplayName("displayName");
        addrs[0].setType("SMTP");

        // XD
        addrs[1] = new Address();
        addrs[1].setEmailAddress(xdEndpoints.get(0));
        addrs[1].setDisplayName("displayName");
        addrs[1].setType("XD");

        // LOCAL
        addrs[2] = new Address();
        addrs[2].setEmailAddress(localEndpoints.get(0));
        addrs[2].setDisplayName("displayName");
        addrs[2].setType("LOCAL");

        // EMPTY
        addrs[3] = new Address();
        addrs[3].setEmailAddress(emptyEndpoints.get(0));
        addrs[3].setDisplayName("displayName");

        proxy.addAddress(addrs);

        RoutingResolver resolver = new RoutingResolverImpl(configServiceURL);

        Collection<String> smtpResolved = resolver.getSmtpEndpoints(endpoints);
        Collection<String> xdResolved = resolver.getXdEndpoints(endpoints);

        assertEquals("List does not match expected size", 1, smtpResolved.size());
        assertEquals("List does not contain expected element", (new ArrayList<String>(smtpResolved)).get(0), smtpEndpoints.get(0));

        assertEquals("List does not match expected size", 1, xdResolved.size());
        assertEquals("List does not contain expected element", (new ArrayList<String>(xdResolved)).get(0), xdEndpoints.get(0));
    }

}
