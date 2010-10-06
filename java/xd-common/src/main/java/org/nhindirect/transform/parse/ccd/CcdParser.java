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

package org.nhindirect.transform.parse.ccd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.transform.parse.ccd.jaxb.CCDDB;
import org.nhindirect.transform.parse.ccd.jaxb.PATIENT;
import org.nhindirect.transform.util.XslConversion;
import org.nhindirect.transform.util.XmlUtils;

/**
 * Utilities for parsing a CCD file.
 * 
 * @author vlewis
 */
public class CcdParser
{
    private String patientId;
    private String orgId;

    private static final String MAP_FILE = "ccdtoccddb.xsl";

    private static final Log LOGGER = LogFactory.getFactory().getInstance(CcdParser.class);

    /**
     * Parse a CCD xml string.
     * 
     * @param ccdXml
     *            The String representation of a CCD request.
     * @throws Exception
     */
    public void parse(String ccdXml) throws Exception
    {
        XslConversion xsl = new XslConversion();
        String dbXml = xsl.run(MAP_FILE, ccdXml);
        LOGGER.trace(dbXml);
        CCDDB pcd = (CCDDB) XmlUtils.unmarshal(dbXml, org.nhindirect.transform.parse.ccd.jaxb.ObjectFactory.class);
        PATIENT patient = pcd.getPATIENT();
        patientId = patient.getPATIENTID();
        orgId = patient.getFACILITYID();
    }

    /**
     * Return the value of patientId.
     * 
     * @return the value of patientId.
     */
    public String getPatientId()
    {
        return patientId;
    }

    /**
     * Return the value of orgId.
     * 
     * @return the value of orgId.
     */
    public String getOrgId()
    {
        return orgId;
    }
}
