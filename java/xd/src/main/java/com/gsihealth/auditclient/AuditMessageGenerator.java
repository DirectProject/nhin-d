/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gsihealth.auditclient;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import org.xml.xml.schema._7f0d86bd.healthcare_security_audit.ActiveParticipantType;
import org.xml.xml.schema._7f0d86bd.healthcare_security_audit.AuditMessage;
import org.xml.xml.schema._7f0d86bd.healthcare_security_audit.CodedValueType;
import org.xml.xml.schema._7f0d86bd.healthcare_security_audit.EventIdentificationType;
import org.xml.xml.schema._7f0d86bd.healthcare_security_audit.ParticipantObjectIdentificationType;

/**
 *
 * @author vlewis
 */
public class AuditMessageGenerator {

    Short VAL1 = Short.parseShort("1");
    Short VAL2 = Short.parseShort("2");
    Short VAL20 = Short.parseShort("20");
    String auditMethod = "file";
    String auditFile = "C:\\testfiles\\audit.log";
    String auditHost=null;
    String auditPort = null;

    public AuditMessageGenerator( String auditFile){
        auditMethod = "file";
        this.auditFile=auditFile;
    }
    public AuditMessageGenerator( String auditHost, String auditPort){
         auditMethod = "syslog";
         this.auditHost= auditHost;
         this.auditPort= auditPort;
    }

    public void provideAndRegisterAudit(String sourceMessageId, String sourceName, String replyTo, String to, String thisName, String patientId, String subsetId, String pid) throws Exception {
        try {

            AuditMessage am = new AuditMessage();

            CodedValueType importcode = null;
            CodedValueType sourceCode = null;
            CodedValueType pnrCode = null;
            CodedValueType patientNumCode = null;
            CodedValueType destCode = null;
            CodedValueType subsetCode = null;
            CodedValueType rCode = null;

            CodedValueType tranType = null;
            importcode = CodedValueFactory.getCodedValueType(1);
            sourceCode = CodedValueFactory.getCodedValueType(2);
            destCode = CodedValueFactory.getCodedValueType(3);
            patientNumCode = CodedValueFactory.getCodedValueType(4);
            pnrCode = CodedValueFactory.getCodedValueType(5);
            subsetCode = CodedValueFactory.getCodedValueType(6);
            rCode = CodedValueFactory.getCodedValueType(7);

            tranType = pnrCode;
            

            EventIdentificationType eit = new EventIdentificationType();

            eit.setEventID(importcode);
            eit.getEventTypeCode().add(tranType);
            eit.setEventActionCode("C");

            am.setEventIdentification(eit);

            List apts = am.getActiveParticipant();
            ActiveParticipantType source = new ActiveParticipantType();

            source.setUserID(replyTo);
            source.setUserIsRequestor(Boolean.TRUE);
            source.getRoleIDCode().add(sourceCode);

            source.setNetworkAccessPointTypeCode(VAL2);
            source.setNetworkAccessPointID(sourceName);

            apts.add(source);

            ActiveParticipantType destination = new ActiveParticipantType();

            destination.setUserID(to);
            destination.setAlternativeUserID(pid);
            destination.setUserIsRequestor(Boolean.FALSE);
            destination.getRoleIDCode().add(destCode);
            destination.setNetworkAccessPointTypeCode(VAL2);
            destination.setNetworkAccessPointID(thisName);

            apts.add(destination);

            List poits = am.getParticipantObjectIdentification();

            ParticipantObjectIdentificationType patient = new ParticipantObjectIdentificationType();

            patient.setParticipantObjectTypeCode(VAL1);
            patient.setParticipantObjectTypeCodeRole(VAL1);
            patient.setParticipantObjectIDTypeCode(patientNumCode);
            patient.setParticipantObjectID(patientId);

            poits.add(patient);

            ParticipantObjectIdentificationType subset = new ParticipantObjectIdentificationType();

            subset.setParticipantObjectTypeCode(VAL2);
            subset.setParticipantObjectTypeCodeRole(VAL20);
            subset.setParticipantObjectIDTypeCode(subsetCode);
            subset.setParticipantObjectID(subsetId);

            poits.add(subset);

            QName messName = new QName("http://www.xml.org/xml/schema/7f0d86bd/healthcare-security-audit");
            String message = marshal(messName, am, org.xml.xml.schema._7f0d86bd.healthcare_security_audit.ObjectFactory.class);
            System.out.println(message);

            outputMessage(message);


        } catch (Exception x) {

            x.printStackTrace();
            throw x;
        }

    }

    public void provideAndRegisterAuditSource(String sourceMessageId, String sourceName, String replyTo, String to, String thisName, String patientId, String subsetId, String pid) throws Exception {
        try {

            AuditMessage am = new AuditMessage();

            CodedValueType exportcode = null;
            CodedValueType sourceCode = null;
            CodedValueType pnrCode = null;
            CodedValueType patientNumCode = null;
            CodedValueType destCode = null;
            CodedValueType subsetCode = null;


            CodedValueType tranType = null;


            exportcode = CodedValueFactory.getCodedValueType(10);
            sourceCode = CodedValueFactory.getCodedValueType(2);
            destCode = CodedValueFactory.getCodedValueType(3);
            patientNumCode = CodedValueFactory.getCodedValueType(4);
            pnrCode = CodedValueFactory.getCodedValueType(5);
            subsetCode = CodedValueFactory.getCodedValueType(6);

            tranType = pnrCode;

            EventIdentificationType eit = new EventIdentificationType();

            eit.setEventID(exportcode);
            eit.getEventTypeCode().add(tranType);
            eit.setEventActionCode("R");

            am.setEventIdentification(eit);

            List apts = am.getActiveParticipant();
            ActiveParticipantType source = new ActiveParticipantType();

            source.setUserID(replyTo);
            source.setUserIsRequestor(Boolean.TRUE);
            source.getRoleIDCode().add(sourceCode);
            source.setNetworkAccessPointTypeCode(VAL2);
            source.setNetworkAccessPointID(sourceName);

            apts.add(source);

            ActiveParticipantType destination = new ActiveParticipantType();

            destination.setUserID(to);
            destination.setAlternativeUserID(pid);
            destination.setUserIsRequestor(Boolean.FALSE);
            destination.getRoleIDCode().add(destCode);
            destination.setNetworkAccessPointTypeCode(VAL2);
            destination.setNetworkAccessPointID(thisName);

            apts.add(destination);


            List poits = am.getParticipantObjectIdentification();

            ParticipantObjectIdentificationType patient = new ParticipantObjectIdentificationType();

            patient.setParticipantObjectTypeCode(VAL1);
            patient.setParticipantObjectTypeCodeRole(VAL1);
            patient.setParticipantObjectIDTypeCode(patientNumCode);
            patient.setParticipantObjectID(patientId);

            poits.add(patient);

            ParticipantObjectIdentificationType subset = new ParticipantObjectIdentificationType();

            subset.setParticipantObjectTypeCode(VAL2);
            subset.setParticipantObjectTypeCodeRole(VAL20);
            subset.setParticipantObjectIDTypeCode(subsetCode);
            subset.setParticipantObjectID(subsetId);

            poits.add(subset);


            QName messName = new QName("http://www.xml.org/xml/schema/7f0d86bd/healthcare-security-audit");
            String message = marshal(messName, am, org.xml.xml.schema._7f0d86bd.healthcare_security_audit.ObjectFactory.class);
            System.out.println(message);

            outputMessage(message);

        } catch (Exception x) {

            x.printStackTrace();
            throw x;
        }

    }

    private  void outputMessage(String message) throws Exception {

       
        String newLine = "\r\n";
       
     
        if (auditMethod.equals("syslog")) {
            SyslogClient sc = new SyslogClient(auditHost,auditPort);
            sc.sendMessage(message);
        } else if (auditMethod.equals("file")) {

           
            if (auditFile == null) {
                throw new Exception("No Filename Foud for audit log");
            }
            FileOutputStream fos = new FileOutputStream(auditFile, true);
            fos.write(message.getBytes());
            fos.write(newLine.getBytes());
            fos.close();
        }

    }

    private static String marshal(QName altName, Object jaxb, Class<?> factory) {
        String ret = null;

        try {
            javax.xml.bind.JAXBContext jc = javax.xml.bind.JAXBContext.newInstance(factory);
            Marshaller u = jc.createMarshaller();

            StringWriter sw = new StringWriter();
            u.marshal(new JAXBElement(altName, jaxb.getClass(), jaxb), sw);
            StringBuffer sb = sw.getBuffer();
            ret = new String(sb);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Failed to marshal message.");
        }

        return ret;
    }

    public static void main(String[] args) throws Exception {

        AuditMessageGenerator amg = new AuditMessageGenerator("C:\\testfiles\\auditlog.txt");
        amg.provideAndRegisterAudit("1", "2", "3", "4", "5", "6", "7", "8");
    }
}
