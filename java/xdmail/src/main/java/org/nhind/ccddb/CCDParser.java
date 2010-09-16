/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nhind.ccddb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhind.mail.util.XMLUtils;
import org.nhind.mail.util.XSLConversion;

/**
 * Utilities for parsing a CCD file.
 * 
 * @author vlewis
 */
public class CCDParser {

    private String patientId;
    private String orgId;

    private static final String MAP_FILE = "ccdtoccddb.xsl";
    
    /**
     * Class logger.
     */
    private static final Log LOGGER = LogFactory.getFactory().getInstance(CCDParser.class);
    
    /**
     * Parse a CCD xml string.
     * 
     * @param ccdXml
     *            The String representation of a CCD request.
     * @throws Exception
     */
    public void parseCCD(String ccdXml) throws Exception {
        XSLConversion xsl = new XSLConversion();
        String dbXml = xsl.run(MAP_FILE, ccdXml);
        LOGGER.trace(dbXml);
        CCDDB pcd = (CCDDB) XMLUtils.unmarshal(dbXml, org.nhind.ccddb.ObjectFactory.class);
        PATIENT patient = pcd.getPATIENT();
        patientId = patient.getPATIENTID();
        orgId = patient.getFACILITYID();
    }

    /**
     * Return the value of patientId.
     * 
     * @return the value of patientId.
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * Return the value of orgId.
     * 
     * @return the value of orgId.
     */
    public String getOrgId(){
        return orgId;
    }
}
