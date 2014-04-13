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

package org.nhindirect.nhindclient;

import java.net.ConnectException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import javax.mail.MessagingException;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.nhindclient.config.NHINDClientConfig;
import org.nhindirect.nhindclient.impl.NHINDClientImpl;
import org.nhindirect.xd.common.DirectDocument2;
import org.nhindirect.xd.common.DirectDocuments;
import org.nhindirect.xd.common.DirectMessage;
import org.nhindirect.xd.common.type.ClassCodeEnum;
import org.nhindirect.xd.common.type.FormatCodeEnum;
import org.nhindirect.xd.common.type.HealthcareFacilityTypeCodeEnum;
import org.nhindirect.xd.common.type.LoincEnum;
import org.nhindirect.xd.common.type.PracticeSettingCodeEnum;
import org.nhindirect.xd.transform.pojo.SimplePerson;

/**
 * 
 * @author vlewis
 */
public class NHINDClientTest extends TestCase
{
    @SuppressWarnings("unused")
    private static final Log LOGGER = LogFactory.getFactory().getInstance(NHINDClientTest.class);

    public NHINDClientTest(String testName)
    {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Quick integration test for the NHINDClient class.
     * 
     * @throws Exception
     */
    public void testClient() throws Exception
    {
        String sender = "lewistower1@gmail.com";
        // Collection<String> receivers = Arrays.asList("beau+receiver@nologs.org", "beau+receiver2@nologs.org", "http://ELS4055:8080/xd/services/DocumentRepository_Service");
        Collection<String> receivers = Arrays.asList("beau+receiver@nologs.org", "beau+receiver2@nologs.org");

        DirectMessage message = new DirectMessage(sender, receivers);

        message.setSubject("This is a test message (subject)");
        message.setBody("Please find the attached data.");
        message.setDirectDocuments(getTestDirectDocuments());

        NHINDClient client = new NHINDClientImpl(new NHINDClientConfig("gmail-smtp.l.google.com", "lewistower1@gmail.com", "hadron106"));
        
        try
        {
        	client.send(message);
        }
        catch (Throwable t)
        {

        	Throwable inner = t.getCause();
        	if (inner != null && inner instanceof MessagingException)
        	{
        		Throwable nextInner = inner.getCause();
        		if (nextInner != null && nextInner instanceof ConnectException)
        		{
        			// some companies may block the out bound server, so gracefully pass
        			// if this occurs
        			return;
        		}
        	}
        }
        
        message.getDirectDocuments().getSubmissionSet().setIntendedRecipient(Arrays.asList("|beau+document2@nologs.org^Smith^John^^^Dr^^^&amp;1.3.6.1.4.1.21367.3100.1&amp;ISO"));

        try
        {
            client.send(message);
        }
        catch (Throwable t)
        {

            Throwable inner = t.getCause();
            if (inner != null && inner instanceof MessagingException)
            {
                Throwable nextInner = inner.getCause();
                if (nextInner != null && nextInner instanceof ConnectException)
                {
                    // some companies may block the out bound server, so gracefully pass
                    // if this occurs
                    return;
                }
            }
        }
    }
    
    private DirectDocuments getTestDirectDocuments()
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
        metadata1.setMimeType("1.1");
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
        metadata1.setFormatCode(FormatCodeEnum.XDS_MEDICAL_SUMMARIES);
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
        metadata2.setMimeType("2.1");
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
        metadata2.setFormatCode(FormatCodeEnum.BASIC_PATIENT_PRIVACY_CONSENTS);
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
        
        return documents;
    }
}
