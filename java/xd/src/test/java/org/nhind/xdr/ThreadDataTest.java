package org.nhind.xdr;

import java.util.Map;

import junit.framework.TestCase;

/**
 * Test class for the ThreadData class.
 * 
 * @author beau
 */
public class ThreadDataTest extends TestCase {

    /**
     * Constructor
     * 
     * @param testName
     *            The test name
     */
    public ThreadDataTest(String testName) {
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
     * Test methods in the ThreadData class.
     */
    public void testThreadData() {
        Map<Long, Map<String, String>> map = ThreadData.getThreadMapView();
        
        // Compare against current size (which may affected by previous tests)
        int mapSize = map.size();

        ThreadData t1 = new ThreadData(new Long(99991));
        t1.setAction("action.1");
        t1.setFrom("from.1");
        t1.setMessageId("messageId.1");
        t1.setPid("pid.1");
        t1.setRelatesTo("relatesTo.1");
        t1.setRemoteHost("remoteHost.1");
        t1.setReplyAddress("replyAddress.1");
        t1.setThisHost("thisHost.1");
        t1.setTo("to.1");
       
        assertEquals("Map size does not match expected value.", mapSize + 1, map.size());
        assertEquals("Map does not contain specific key.", true, map.containsKey(new Long(99991)));
        assertEquals("Map value does not match expected.", "action.1", map.get(new Long(99991)).get(ThreadData.ACTION));        
        assertEquals("Map value does not match expected.", "from.1", map.get(new Long(99991)).get(ThreadData.FROM));    
        assertEquals("Map value does not match expected.", "messageId.1", map.get(new Long(99991)).get(ThreadData.MESSAGE));    
        assertEquals("Map value does not match expected.", "pid.1", map.get(new Long(99991)).get(ThreadData.PID));    
        assertEquals("Map value does not match expected.", "relatesTo.1", map.get(new Long(99991)).get(ThreadData.RELATESTO));    
        assertEquals("Map value does not match expected.", "remoteHost.1", map.get(new Long(99991)).get(ThreadData.REMOTEHOST));    
        assertEquals("Map value does not match expected.", "replyAddress.1", map.get(new Long(99991)).get(ThreadData.REPLY));    
        assertEquals("Map value does not match expected.", "thisHost.1", map.get(new Long(99991)).get(ThreadData.THISHOST));    
        assertEquals("Map value does not match expected.", "to.1", map.get(new Long(99991)).get(ThreadData.TO));    
        
        t1.setTo("to.1.1");

        assertEquals("Map value does not match expected.", "to.1.1", map.get(new Long(99991)).get(ThreadData.TO));    
        
        ThreadData t2 = new ThreadData(new Long(99992));
        t2.setAction("action.2");
        t2.setFrom("from.2");
        t2.setMessageId("messageId.2");
        t2.setPid("pid.2");
        t2.setRelatesTo("relatesTo.2");
        t2.setRemoteHost("remoteHost.2");
        t2.setReplyAddress("replyAddress.2");
        t2.setThisHost("thisHost.2");
        t2.setTo("to.2");
        
        assertEquals("Map size does not match expected value.", mapSize + 2, map.size());
        assertEquals("Map does not contain specific key.", true, map.containsKey(new Long(99992)));      
    }

}
