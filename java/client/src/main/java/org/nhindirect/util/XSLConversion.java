package org.nhindirect.util;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * XSL conversion utilities.
 */
public class XSLConversion {

    /**
     * Hashtable of map files to templates.
     */
    private static Hashtable<String, Templates> conversions = new Hashtable<String, Templates>(10);

    /**
     * Default constructor.
     */
    public XSLConversion() {
    }

    /**
     * Perform the XSL conversion using the provided map file and message.
     * 
     * @param mapFile
     *            The map file.
     * @param message
     *            The message.
     * @return an XSL conversion.
     * @throws Exception
     */
    public String run(String mapFile, String message) throws Exception {
        String retXml = "";
        Transformer transformer = null;

        try {

            long start = System.currentTimeMillis();
            if (conversions.containsKey(mapFile)) {
                Templates temp = (Templates) conversions.get(mapFile);
                transformer = temp.newTransformer();
                Logger.getLogger(this.getClass().getPackage().getName()).log(Level.INFO, "from xsl cache");
            } else {
                synchronized (conversions) {
                    if (!conversions.containsKey(mapFile)) {
                        /*
                         * Use the static TransformerFactory.newInstance()
                         * method to instantiate a TransformerFactory. The
                         * javax.xml.transform.TransformerFactory system
                         * property setting determines the actual class to
                         * instantiate --
                         * org.apache.xalan.transformer.TransformerImpl.
                         */
                        TransformerFactory tFactory = TransformerFactory.newInstance();

                        /*
                         * Use the TransformerFactory to instantiate a Template
                         * that is thread safe for use in generating Transfomers
                         */
                        InputStream is = this.getClass().getResourceAsStream(mapFile);

                        Templates temp = tFactory.newTemplates(new StreamSource(is));
                        transformer = temp.newTransformer();
                        conversions.put(mapFile, temp);
                    }
                }
            }

            CharArrayWriter to = new CharArrayWriter();

            transformer.transform(new StreamSource(new CharArrayReader(message.toCharArray())), new StreamResult(to));
            retXml = to.toString();
            // Logger.getLogger(this.getClass().getPackage().getName()).log(Level.INFO,retXml);
            long elapse = System.currentTimeMillis() - start;
            Logger.getLogger(this.getClass().getPackage().getName()).log(Level.INFO,
                    "Started at " + new Timestamp(start).toString() + " Elapsed conversion time = " + elapse);

        } catch (Exception x) {
            x.printStackTrace();
            throw new Exception(x.getMessage());
        }
        
        return retXml;
    }

}
