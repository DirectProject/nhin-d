/* 
 * Copyright (c) 2010, NHIN Direct Project
 * All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in the 
 *    documentation and/or other materials provided with the distribution.  
 * 3. Neither the name of the the NHIN Direct Project (nhindirect.org)
 *    nor the names of its contributors may be used to endorse or promote products 
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY 
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.nhindirect.xd.transform.util;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class contains common XML utility methods.
 * 
 * @author beau
 */
public class XmlUtils
{
    private static final Log LOGGER = LogFactory.getFactory().getInstance(XmlUtils.class);

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
    @SuppressWarnings("unchecked")
    public static String marshal(QName altName, Object jaxb, Class<?> factory)
    {
        String ret = null;

        try
        {
            javax.xml.bind.JAXBContext jc = javax.xml.bind.JAXBContext.newInstance(factory);
            Marshaller u = jc.createMarshaller();

            StringWriter sw = new StringWriter();
            u.marshal(new JAXBElement(altName, jaxb.getClass(), jaxb), sw);
            StringBuffer sb = sw.getBuffer();
            ret = new String(sb);
        }
        catch (Exception ex)
        {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("Failed to marshal message.", ex);
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
    public static Object unmarshal(String xml, Class<?> factory) throws JAXBException
    {
        javax.xml.bind.JAXBContext jaxbCtx = null;

        try
        {
            jaxbCtx = javax.xml.bind.JAXBContext.newInstance(factory);
        }
        catch (JAXBException e)
        {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to create JAXBContext object.", e);
            throw e;
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
    private static Object unmarshal(String xml, javax.xml.bind.JAXBContext jaxbCtx)
    {
        Object ret = null;

        try
        {
            byte currentXMLBytes[] = xml.getBytes();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(currentXMLBytes);

            javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            ret = unmarshaller.unmarshal(byteArrayInputStream);
        }
        catch (Exception ex)
        {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("Failed to unmarshal message.", ex);
        }

        return ret;
    }

}
