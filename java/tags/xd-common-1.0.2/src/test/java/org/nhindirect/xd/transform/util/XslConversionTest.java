package org.nhindirect.xd.transform.util;

import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.nhindirect.xd.transform.util.XslConversion;

/**
 * Test class for methods in the XSLConversion class.
 * 
 * @author beau
 */
public class XslConversionTest extends TestCase
{

    /**
     * Test constructor.
     * 
     * @param testName
     *            The test name
     */
    public XslConversionTest(String testName)
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

    /**
     * Test the run method.
     */
    public void testRun()
    {
        String input = null;
        XslConversion converter = new XslConversion();
        String output = null;

        try
        {
            input = getSampleCCD();
        }
        catch (Exception e)
        {
            fail("Test setup failed");
        }

        try
        {
            // Hit fresh, then hit cache
            output = converter.run("ccdtoccddb.xsl", input);
            assertTrue("Output is blank", !StringUtils.isBlank(output));

            output = converter.run("ccdtoccddb.xsl", input);
            assertTrue("Output is blank", !StringUtils.isBlank(output));
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }

        try
        {
            // Missing map
            output = converter.run("ccdtoccddb_missing.xsl", input);
            fail("Exception not thrown");
        }
        catch (Exception e)
        {
            assertTrue(true);
        }

        try
        {
            // Malformed map
            output = converter.run("ccdtoccddb_malformed.xsl", input);
            fail("Exception not thrown");
        }
        catch (Exception e)
        {
            assertTrue(true);
        }
    }

    /**
     * Return a sample CCD as a String.
     * 
     * @return a sample CCD as a String.
     * @throws Exception
     */
    private static String getSampleCCD() throws Exception
    {
        byte[] output = null;

        InputStream is = XslConversionTest.class.getClassLoader().getResourceAsStream("sampleccd.xml");
        output = new byte[is.available()];
        is.read(output);
        is.close();

        return new String(output);
    }
}
