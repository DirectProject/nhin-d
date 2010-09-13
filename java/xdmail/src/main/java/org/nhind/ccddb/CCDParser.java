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
    
    public void parseCCD(String ccdXml) throws Exception {
   
        XSLConversion xsl = new XSLConversion();
        String dbXml = xsl.run(MAP_FILE, ccdXml);
        LOGGER.trace(dbXml);
        CCDDB pcd = (CCDDB) XMLUtils.unmarshal(dbXml, org.nhind.ccddb.ObjectFactory.class);
        PATIENT patient = pcd.getPATIENT();
        patientId = patient.getPATIENTID();
        orgId = patient.getFACILITYID();
    }

    public String getPatientId() {
        return patientId;
    }

    public String getOrgId(){
        return orgId;
    }
}
