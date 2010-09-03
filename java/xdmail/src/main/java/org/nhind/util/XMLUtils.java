package org.nhind.util;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.logging.Logger;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

/**
 * Utility methods for XML related activities.
 * 
 * @author beau
 */
public class XMLUtils {

    /**
     * Class logger.
     */
    private static final Logger LOGGER = Logger.getLogger(XMLUtils.class.getPackage().getName());

    /**
     * Marshal an object into an XML string.
     * 
     * @param altName
     *            The altName.
     * @param jaxb
     *            The object to marshal.
     * @param factory
     *            The factory class.
     * @return a marshaled string from the object.
     */
    public static String marshal(QName altName, Object jaxb, Class factory) {
        String ret = null;

        try {
            javax.xml.bind.JAXBContext jc = javax.xml.bind.JAXBContext.newInstance(factory);
            Marshaller u = jc.createMarshaller();

            StringWriter sw = new StringWriter();
            u.marshal(new JAXBElement(altName, jaxb.getClass(), jaxb), sw);
            StringBuffer sb = sw.getBuffer();
            ret = new String(sb);
        } catch (Exception ex) {
            LOGGER.info("Failed to marshal message");
            ex.printStackTrace();
        }

        return ret;
    }

    /**
     * Unmarshal an string into an object.
     * 
     * @param xml
     *            The XML string.
     * @param factory
     *            The factory class.
     * @return an object representation of the string.
     */
    public static Object unmarshal(String xml, Class factory) {
        javax.xml.bind.JAXBContext jaxbCtx = null;

        try {
            jaxbCtx = javax.xml.bind.JAXBContext.newInstance(factory);
        } catch (JAXBException e) {
            LOGGER.info("Failed to create JAXBContext object");
            e.printStackTrace();
        }

        return unmarshal(xml, jaxbCtx);
    }

    /**
     * Unmarshal an string into an object.
     * 
     * @param xml
     *            The XML string.
     * @param jaxbCtx
     *            The JAXBContext object.
     * @return an object representation of the string.
     */
    private static Object unmarshal(String xml, javax.xml.bind.JAXBContext jaxbCtx) {
        Object ret = null;

        try {
            byte currentXMLBytes[] = xml.getBytes();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(currentXMLBytes);

            javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            ret = unmarshaller.unmarshal(byteArrayInputStream);
        } catch (Exception ex) {
            LOGGER.info("Failed to unmarshal message: " + xml.substring(0, 50));
            ex.printStackTrace();
        }

        return ret;
    }
}
