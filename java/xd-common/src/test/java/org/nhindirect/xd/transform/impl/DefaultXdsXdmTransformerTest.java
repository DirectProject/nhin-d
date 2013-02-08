package org.nhindirect.xd.transform.impl;

import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.nhindirect.xd.common.DirectDocument2;
import org.nhindirect.xd.common.DirectDocuments;
import org.nhindirect.xd.common.type.ClassCodeEnum;
import org.nhindirect.xd.common.type.FormatCodeEnum;
import org.nhindirect.xd.common.type.HealthcareFacilityTypeCodeEnum;
import org.nhindirect.xd.common.type.LoincEnum;
import org.nhindirect.xd.common.type.PracticeSettingCodeEnum;
import org.nhindirect.xd.transform.XdsXdmTransformer;
import org.nhindirect.xd.transform.pojo.SimplePerson;
import org.nhindirect.xd.transform.util.XmlUtils;

public class DefaultXdsXdmTransformerTest extends TestCase
{
    public void testTransform() throws Exception
    {
        // Create a collection of documents
        DirectDocuments documents = new DirectDocuments();
        
        documents.getSubmissionSet().setId("1");
        documents.getSubmissionSet().setName("2");
        documents.getSubmissionSet().setDescription("3");
        documents.getSubmissionSet().setSubmissionTime(new Date());
        documents.getSubmissionSet().setIntendedRecipient(Arrays.asList("5.1", "5.2"));
        documents.getSubmissionSet().setAuthorPerson("6");
        documents.getSubmissionSet().setAuthorInstitution(Arrays.asList("7.1", "7.2"));
        documents.getSubmissionSet().setAuthorRole("8");
        documents.getSubmissionSet().setAuthorSpecialty("9");
        documents.getSubmissionSet().setAuthorTelecommunication("10");
        documents.getSubmissionSet().setContentTypeCode("11");
        documents.getSubmissionSet().setContentTypeCode_localized("12");
        documents.getSubmissionSet().setUniqueId("13");
        documents.getSubmissionSet().setSourceId("14");
        documents.getSubmissionSet().setPatientId("xxx");
        
        DirectDocument2 doc1 = new DirectDocument2();
        doc1.setData(new String("data1").getBytes());
        
        DirectDocument2.Metadata metadata1 = doc1.getMetadata();
        metadata1.setMimeType("text/xml");
        metadata1.setId("1.2");
        metadata1.setDescription("1.3");
        metadata1.setCreationTime(new Date());
        metadata1.setLanguageCode("1.5");
        metadata1.setServiceStartTime(new Date());
        metadata1.setServiceStopTime(new Date());
        metadata1.setSourcePatient(new SimplePerson("1.Bob", "1.Smith"));
        metadata1.setAuthorPerson("1.10");
        metadata1.setAuthorInstitution(Arrays.asList("1.11.1", "1.11.2"));
        metadata1.setAuthorRole("1.12");
        metadata1.setAuthorSpecialty("1.13");
        metadata1.setClassCode(ClassCodeEnum.HISTORY_AND_PHYSICAL.getValue());
        metadata1.setClassCode_localized(ClassCodeEnum.HISTORY_AND_PHYSICAL.getValue());
        metadata1.setConfidentialityCode("1.16");
        metadata1.setConfidentialityCode_localized("1.17");
        metadata1.setFormatCode(FormatCodeEnum.HL7_CCD_DOCUMENT);
        metadata1.setHealthcareFacilityTypeCode(HealthcareFacilityTypeCodeEnum.OF.getValue());
        metadata1.setHealthcareFacilityTypeCode_localized(HealthcareFacilityTypeCodeEnum.OF.getValue());
        metadata1.setPracticeSettingCode(PracticeSettingCodeEnum.MULTIDISCIPLINARY.getValue());
        metadata1.setPracticeSettingCode_localized(PracticeSettingCodeEnum.MULTIDISCIPLINARY.getValue());
        metadata1.setLoinc(LoincEnum.LOINC_34133_9.getValue());
        metadata1.setLoinc_localized(LoincEnum.LOINC_34133_9.getValue());
        metadata1.setPatientId("xxx");
        metadata1.setUniqueId("1.27");
        
        DirectDocument2 doc2 = new DirectDocument2();
        doc2.setData(new String("data2").getBytes());
        
        DirectDocument2.Metadata metadata2 = doc2.getMetadata();
        metadata2.setMimeType("text/xml");
        metadata2.setId("2.2");
        metadata2.setDescription("2.3");
        metadata2.setCreationTime(new Date());
        metadata2.setLanguageCode("2.5");
        metadata2.setServiceStartTime(new Date());
        metadata2.setServiceStopTime(new Date());
        metadata2.setSourcePatient(new SimplePerson("2.Bob", "2.Smith"));
        metadata2.setAuthorPerson("2.10");
        metadata2.setAuthorInstitution(Arrays.asList("2.11.1", "2.11.2"));
        metadata2.setAuthorRole("2.12");
        metadata2.setAuthorSpecialty("2.13");
        metadata2.setClassCode(ClassCodeEnum.HISTORY_AND_PHYSICAL.getValue());
        metadata2.setClassCode_localized(ClassCodeEnum.HISTORY_AND_PHYSICAL.getValue());
        metadata2.setConfidentialityCode("2.16");
        metadata2.setConfidentialityCode_localized("2.17");
        metadata2.setFormatCode(FormatCodeEnum.IMMUNIZATION_REGISTRY_CONTENT_CRC);
        metadata2.setHealthcareFacilityTypeCode(HealthcareFacilityTypeCodeEnum.OF.getValue());
        metadata2.setHealthcareFacilityTypeCode_localized(HealthcareFacilityTypeCodeEnum.OF.getValue());
        metadata2.setPracticeSettingCode(PracticeSettingCodeEnum.MULTIDISCIPLINARY.getValue());
        metadata2.setPracticeSettingCode_localized(PracticeSettingCodeEnum.MULTIDISCIPLINARY.getValue());
        metadata2.setLoinc(LoincEnum.LOINC_34133_9.getValue());
        metadata2.setLoinc_localized(LoincEnum.LOINC_34133_9.getValue());
        metadata2.setPatientId("xxx");
        metadata2.setUniqueId("2.27");
        
        documents.getDocuments().add(doc1);
        documents.getDocuments().add(doc2);
        
        ProvideAndRegisterDocumentSetRequestType request = documents.toProvideAndRegisterDocumentSetRequestType();
        
        QName qname = new QName("urn:ihe:iti:xds-b:2007", "ProvideAndRegisterDocumentSet_bRequest");
        String body = XmlUtils.marshal(qname, request, ihe.iti.xds_b._2007.ObjectFactory.class);
        System.out.println(body);
        
        XdsXdmTransformer transformer = new DefaultXdsXdmTransformer();
        
        File f = transformer.transform(request);
        
        System.out.println(f.getAbsolutePath());
    }
}
