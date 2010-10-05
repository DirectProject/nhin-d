package org.nhindirect;

import junit.framework.TestCase;

public class DirectDocumentTest extends TestCase
{

    public DirectDocumentTest(String testName)
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

    public void testDirectDocument()
    {
        DirectDocument document = new DirectDocument();
        DirectDocument.Metadata metadata = document.getMetadata();

        document.setData("document data");

        metadata.setDocument_author("author");
        metadata.setDocument_classCode("documentClassCode");
        metadata.setDocument_confidentialityCode("confidientialityCode");
        metadata.setDocument_creationTime("creationTime");
        metadata.setDocument_entryUUID("entryUUID");
        metadata.setDocument_formatCode("format_code");
        metadata.setDocument_healthcareFacilityTypeCode("healthcareFacitilyCode");
        metadata.setDocument_languageCode("languageCode");
        metadata.setDocument_mimeType("mimeType");
        metadata.setDocument_patientId("patientId");
        metadata.setDocument_practiceSettingCode("practiceSettingCode");
        metadata.setDocument_sourcePatientId("sourcePatientId");
        metadata.setDocument_sourcePatientInfo("sourcePatientInfo");
        metadata.setDocument_typeCode("typeCode");
        metadata.setDocument_uniqueId("uniqueId");
    }
}
