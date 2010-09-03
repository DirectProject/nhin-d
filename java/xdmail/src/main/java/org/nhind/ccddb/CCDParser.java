/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nhind.ccddb;

import java.io.ByteArrayInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.nhind.util.XSLConversion;

/**
 *
 * @author vlewis
 */
public class CCDParser {

    private String patientId;
    private String orgId;

    public void parseCCD(String ccdXml) throws Exception {
   
        String map = "/ccdtoccddb.xsl";

        XSLConversion xsl = new XSLConversion();
        String dbXml = xsl.run(map, ccdXml);
        Logger.getLogger(this.getClass().getPackage().getName()).log(Level.INFO, dbXml);
        CCDDB pcd = (CCDDB) unmarshalMessage(dbXml);
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

    private static Object unmarshalMessage(String xml) {

        Object ret = null;
        try {

            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(ObjectFactory.class);
            javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            byte currentXMLBytes[] = xml.getBytes();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(currentXMLBytes);
            ret = unmarshaller.unmarshal(byteArrayInputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }
}
