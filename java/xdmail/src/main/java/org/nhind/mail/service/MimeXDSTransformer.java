package org.nhind.mail.service;

import ihe.iti.xds_b._2007.DocumentRepositoryPortType;
import ihe.iti.xds_b._2007.DocumentRepositoryService;
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType.Document;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;

import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.AssociationType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExternalIdentifierType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.InternationalStringType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.LocalizedStringType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryPackageType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ValueListType;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

import org.apache.commons.lang.StringUtils;
import org.nhind.ccddb.CCDParser;

/*
 * TODO there is a major assumption within this class and underlying classes.  That is the assumption of single document. 
 * This means that a message body (not attachment) has one document, an attachment has one document or one XDM and an XDM 
 * has one document.
 * Obviously this needs to be enhanced per the appropriate specs.
 * Also, for XDR purposes there is an assumption that the doc is CDA if its body or attached. XDM does not make this assumption.
 * As we go to XDD , this will be easy to fix. 
 * Thanks , Vince Lewis
 */

/**
 * 
 * @author vlewis
 */
public class MimeXDSTransformer {

    private static final Logger LOGGER = Logger.getLogger(MimeXDSTransformer.class.getName());
    static final String MIMETYPE_TEXT_PLAIN = "text/plain";
    static final String MIMETYPE_TEXT_XML = "text/xml";
    static final String MIMETYPE_CDA = "text/cda+xml";
    static final String MIMETYPE_CCR = "application/ccr";
    static final String DEFAULT_LANGUAGE_CODE = "en-us"; // TODO default from
                                                         // Locale
    static final String RANDOM_OID_ROOT = "1.3.6.1.4.1.21367.3100.1.2.3";
    static final String NHINDIRECT_MESSAGE_ID_ASSIGNING_AUTHORITY_NAME = "NHINDirect";
    static final String NHINDIRECT_ADDR_OID = "1.3.6.1.4.1.21367.3100.1";
    static final String NHINDIRECT_MESSAGE_ID_ASSIGNING_AUTHORITY_ID = "1.3.6.1.4.1.21367.3100.1.1";
    static final String NHINDIRECT_MESSAGE_METADATA_CODESYSTEM_ID = "1.3.6.1.4.1.21367.3100.1.2";
    static final String CODE_UNSPECIFIED = "Unspecified";
    static final String CODE_CLINICALDATA = "Clinical Data";
    static final String CODE_CONTENT_TYPE = "Communication";
    static final String CODE_CONFIDENTIALITY = "N";
    static final String CODE_FORMAT_TEXT = "TEXT";
    static final String CODE_FORMAT_CDAR2 = "CDAR2/IHE 1.0";
    static final String CODE_FORMAT_CCRV1 = "CCR V1.0";
    static final String CODE_FORMAT_PDF = "PDF";
    protected String endpoint = null;
    protected String messageId = null;
    protected String relatesTo = null;
    protected String action = null;
    protected String to = null;
    private String thisHost = null;
    private String remoteHost = null;
    private String pid = null;
    private String from = null;
    private String suffix = null;
    private String replyEmail = null;

    public void forward(String endpoint, MimeMessage mimeMessage) throws Exception {
        ProvideAndRegisterDocumentSetRequestType prds = createRequest(mimeMessage);
        forwardRequest(endpoint, prds);
    }

    private void forwardRequest(String endpoint, ProvideAndRegisterDocumentSetRequestType prds) throws Exception {

        LOGGER.info(" SENDING TO ENDPOINT " + endpoint);
        relatesTo = messageId;
        action = "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-bResponse";
        messageId = UUID.randomUUID().toString();
        to = endpoint;

        setHeaderData();
        DocumentRepositoryService service = new DocumentRepositoryService();
        service.setHandlerResolver(new RepositoryHandlerResolver());
        DocumentRepositoryPortType port = service.getDocumentRepositoryPortSoap12(new MTOMFeature(true, 1));

        BindingProvider bp = (BindingProvider) port;
        SOAPBinding binding = (SOAPBinding) bp.getBinding();
        binding.setMTOMEnabled(true);

        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

        RegistryResponseType rrt = port.documentRepositoryProvideAndRegisterDocumentSetB(prds);
        String test = rrt.getStatus();
        if (StringUtils.contains(test, "Failure")) {
            throw new Exception("Failure Returned from XDR forward");
        }
    }

    /**
     * Transform a MimeMessage object into a
     * ProvideAndRegisterDocumentSetRequestType object.
     * 
     * @param mimeMessage
     *            The MimeMessage to be transformed.
     * @return a ProvideAndRegisterDocumentSetRequestType object.
     */
    private ProvideAndRegisterDocumentSetRequestType createRequest(MimeMessage mimeMessage) {
        ProvideAndRegisterDocumentSetRequestType prsr = null;

        try {
            byte[] xdsDocument = null;
            String xdsMimeType = null;
            String xdsFormatCode = null;

            Address[] froms = mimeMessage.getFrom();
            Address[] recips = mimeMessage.getAllRecipients();
            String msgContentType = mimeMessage.getContentType();

            String recip = recips[0].toString(); // TODO one recipient for now
            recip = "vlewis@lewistower.com";// TODO remove this
            String auth = froms[0].toString(); // TODO one from for now
            messageId = mimeMessage.getMessageID();
            Date sentDate = mimeMessage.getSentDate();
            // String organization = mimeMessage.
            String subject = mimeMessage.getSubject();

            LOGGER.info("messagecontenttype" + msgContentType);
            if (msgContentType.startsWith("multipart/mixed")) {
                LOGGER.info("in multipart/mixed");

                MimeMultipart mimeMultipart = (MimeMultipart) mimeMessage.getContent();

                // we grab any CDA document attachments and add them to the
                // submission using OHT
                for (int partIndex = 0; partIndex < mimeMultipart.getCount(); partIndex++) {

                    BodyPart bodyPart = mimeMultipart.getBodyPart(partIndex);
                    String contentType = bodyPart.getContentType();
                    LOGGER.info("in bodypart size = " + bodyPart.getSize());
                    if (bodyPart.getSize() > 0) {

                        String fname = bodyPart.getFileName();
                        if (fname == null || fname.equals("null")) {
                            continue;
                        }
                        LOGGER.info("file name " + fname);
                        if (StringUtils.contains(fname, ".zip")) {
                            try {
                                prsr = getMDMRequest(bodyPart);

                            } catch (Exception x) {

                            }
                        }

                        InputStream is = bodyPart.getInputStream();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buf = new byte[1024];
                        int i = 0;
                        while ((i = is.read(buf)) != -1) {
                            baos.write(buf, 0, i);
                        }
                        String contentString = new String(baos.toByteArray());

                        // LOGGER.info("BAOS" +contentString);
                        try {
                            // special handling for recognized content types
                            if (contentType.startsWith(MIMETYPE_TEXT_PLAIN)) {
                                if (contentString.trim().length() == 0) {
                                    continue; // skip 'empty' parts
                                }
                                xdsDocument = baos.toByteArray();
                                xdsMimeType = MIMETYPE_TEXT_PLAIN;
                                xdsFormatCode = CODE_FORMAT_TEXT;
                            } else if (contentType.startsWith(MIMETYPE_TEXT_XML)) {

                                // its a CDA R2
                                if (contentString.contains("urn:hl7-org:v3") && contentString.contains("POCD_HD000040")) {
                                    xdsDocument = baos.toByteArray();
                                    xdsMimeType = MIMETYPE_TEXT_XML;
                                    xdsFormatCode = CODE_FORMAT_CDAR2;
                                } // its some other XML (maybe a CCR or HL7
                                  // message) TODO support more XML types
                                else {
                                    xdsDocument = baos.toByteArray();
                                    xdsMimeType = MIMETYPE_TEXT_XML;
                                    xdsFormatCode = CODE_FORMAT_TEXT;
                                }

                                // otherwise make best effort passing MIME
                                // content type thru
                            } else {
                                xdsMimeType = contentType;
                                xdsFormatCode = CODE_FORMAT_TEXT;
                            }

                        } finally {
                            is.close();
                        }
                    }

                }

                // support for plain mail, no attachments
            } else if (msgContentType.startsWith(MIMETYPE_TEXT_PLAIN)) {
                xdsMimeType = MIMETYPE_TEXT_PLAIN;
                xdsFormatCode = CODE_FORMAT_TEXT;
                xdsDocument = ((String) mimeMessage.getContent()).getBytes();

                // form prsr
            } else {
                throw new MessagingException("message content type " + msgContentType + " is not supported");
            }
            prsr = getRequest(subject, sentDate, xdsFormatCode, xdsMimeType, xdsDocument, auth, recip);

        } catch (Exception x) {
        }

        return prsr;

    }

    ProvideAndRegisterDocumentSetRequestType getMDMRequest(BodyPart bodyPart) throws Exception {
        ProvideAndRegisterDocumentSetRequestType prsr = new ProvideAndRegisterDocumentSetRequestType();
        LOGGER.info("in getMDMRequest");
        XDMXDSTransformer xxt = new XDMXDSTransformer();
        DataHandler dh = bodyPart.getDataHandler();
        prsr = xxt.getXDMRequest(dh);
        return prsr;
    }

    ProvideAndRegisterDocumentSetRequestType getRequest(String subject, Date sentDate, String formatCode,
            String mimeType, byte[] doc, String auth, String recip) throws Exception {

        ProvideAndRegisterDocumentSetRequestType prsr = new ProvideAndRegisterDocumentSetRequestType();
        String sdoc = new String(doc);
        CCDParser cp = new CCDParser();
        cp.parseCCD(sdoc);
        String patientId = cp.getPatientId();
        String orgId = cp.getOrgId();
        String date = formatDate(sentDate);
        String subId = UUID.randomUUID().toString();
        String docId = UUID.randomUUID().toString();
        SimplePerson sp = null;

        SubmitObjectsRequest sor = getSubmitObjectsRequest(patientId, orgId, sp, subject, date, docId, subId,
                formatCode, mimeType, auth, recip);
        prsr.setSubmitObjectsRequest(sor);
        List<Document> docs = prsr.getDocument();
        Document pdoc = new Document();

        DataSource source = new ByteArrayDataSource(doc, "application/xml; charset=UTF-8");
        DataHandler dhnew = new DataHandler(source);
        pdoc.setValue(dhnew);
        pdoc.setId(docId);
        docs.add(pdoc);

        return prsr;

    }

    public SubmitObjectsRequest getSubmitObjectsRequest(String patientId, String orgId, SimplePerson person,
            String subject, String sentDate, String docId, String subId, String formatCode, String mimeType,
            String auth, String recip) {

        SubmitObjectsRequest req = new SubmitObjectsRequest();

        RegistryObjectListType rolt = new RegistryObjectListType();

        ExtrinsicObjectType eot = getExtrinsicObject(patientId, orgId, person, sentDate, docId, formatCode, mimeType,
                auth);
        QName qname = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "ExtrinsicObject");
        JAXBElement eotj = new JAXBElement(qname, ExtrinsicObjectType.class, eot);

        RegistryPackageType rpt = getSubmissionSet(patientId, orgId, subject, sentDate, subId, auth, recip);
        qname = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "RegistryPackage");
        JAXBElement rptj = new JAXBElement(qname, RegistryPackageType.class, rpt);

        ClassificationType clas = getClassification(rpt.getId());
        qname = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "Classification");
        JAXBElement clasj = new JAXBElement(qname, ClassificationType.class, clas);

        AssociationType1 ass = getAssociation(rpt.getId(), eot.getId());
        qname = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "Association");
        JAXBElement assj = new JAXBElement(qname, AssociationType1.class, ass);

        List elems = rolt.getIdentifiable();
        elems.add(eotj);
        elems.add(rptj);
        elems.add(clasj);
        elems.add(assj);

        req.setRegistryObjectList(rolt);

        return req;
    }

    protected ExtrinsicObjectType getExtrinsicObject(String patientId, String orgId, SimplePerson person,
            String sentDate, String docId, String formatCode, String mimeType, String auth) {

        ExtrinsicObjectType document = new ExtrinsicObjectType();

        String obType = "urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1";

        document.setId(docId);
        document.setObjectType(obType);
        document.setMimeType(mimeType);
        List<SlotType1> slots = document.getSlot();

        slots.add(makeSlot("creationTime", sentDate));
        // slots.add(makeSlot("serviceStartTime", formatDate(new Date())));
        // GregorianCalendar gd = new GregorianCalendar();
        // gd.add(gd.YEAR, 100);
        // slots.add(makeSlot("serviceStopTime", formatDate(gd.getTime())));
        slots.add(makeSlot("sourcePatientId", patientId + "^^^&" + orgId));
        if (person != null) {
            slots.add(makePatientSlot("sourcePatientInfo", person, patientId, orgId));
        }

        List<String> slotNames = new ArrayList<String>();
        if (auth != null) {
            slotNames.add("authorPerson");
        }
        slotNames.add("authorInstitution");
        slotNames.add("authorRole");
        List<String> slotValues = new ArrayList<String>();
        if (auth != null) {
            slotValues.add(auth);
        }
        slotValues.add(orgId);
        if (auth != null) {
            slotValues.add(auth + "'s Role");// see if we need this
        } else {
            slotValues.add("System");
        }

        List<String> snames = new ArrayList<String>();
        if (auth != null) {
            snames.add(null);
        }
        snames.add(null);
        snames.add(null);
        List<ClassificationType> classifs = document.getClassification();
        addClassifications(classifs, docId, "c101", "urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d", null, slotNames,
                slotValues, snames);
        slotNames = new ArrayList<String>();
        slotNames.add("codingScheme");
        slotValues = new ArrayList<String>();
        slotValues.add("Connect-a-thon classCodes");
        snames = new ArrayList<String>();
        snames.add("History and Physical");
        addClassifications(classifs, docId, "c102", "uuid:41a5887f-8865-4c09-adf7-e362475b143a",
                "History and Physical", slotNames, slotValues, snames);
        slotNames = new ArrayList<String>();
        slotNames.add("codingScheme");
        slotValues = new ArrayList<String>();
        slotValues.add("Connect-a-thon formatCodes");
        snames = new ArrayList<String>();
        snames.add(formatCode);
        addClassifications(classifs, docId, "c104", "urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d", formatCode,
                slotNames, slotValues, snames);
        slotNames = new ArrayList<String>();
        slotNames.add("codingScheme");
        slotValues = new ArrayList<String>();
        slotValues.add("Connect-a-thon healthcareFacilityTypeCodes");
        snames = new ArrayList<String>();
        snames.add("OF");
        addClassifications(classifs, docId, "c105", "urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1", "OF", slotNames,
                slotValues, snames);
        slotNames = new ArrayList<String>();
        slotNames.add("codingScheme");
        slotValues = new ArrayList<String>();
        slotValues.add("Connect-a-thon practiceSettingCodes");
        snames = new ArrayList<String>();
        snames.add("Multidisciplinary");
        addClassifications(classifs, docId, "c106", "urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead",
                "Multidisciplinary", slotNames, slotValues, snames);
        slotNames = new ArrayList<String>();
        slotNames.add("codingScheme");
        slotValues = new ArrayList<String>();
        slotValues.add("LOINC");
        snames = new ArrayList<String>();
        snames.add("34133-9");
        addClassifications(classifs, docId, "c107", "urn:uuid:f0306f51-975f-434e-a61c-c59651d33983", "34133-9",
                slotNames, slotValues, snames);

        List<ExternalIdentifierType> extIds = document.getExternalIdentifier();
        addExternalIds(extIds, docId, "urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427", "ei01",
                "XDSDocumentEntry.patientId", patientId + "^^^&" + orgId);
        addExternalIds(extIds, docId, "urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab", "ei02",
                "XDSDocumentEntry.uniqueId", docId);

        return document;
    }

    protected RegistryPackageType getSubmissionSet(String patientId, String orgId, String subject, String sentDate,
            String subId, String auth, String recip) {
        RegistryPackageType subset = new RegistryPackageType();

        String obType = "urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1";

        subset.setId(subId);
        subset.setObjectType(obType);

        List<SlotType1> slots = subset.getSlot();

        slots.add(makeSlot("submissionTime", sentDate));
        String intendedRecipient = "|" + recip + "^last^first^^^prefix^^^&amp;1.3.6.1.4.1.21367.3100.1&amp;ISO";
        slots.add(makeSlot("intendedRecipient", intendedRecipient));

        List<String> slotNames = new ArrayList<String>();
        if (auth != null) {
            slotNames.add("authorPerson");
        }
        slotNames.add("authorInstitution");
        slotNames.add("authorRole");
        List<String> slotValues = new ArrayList<String>();
        if (auth != null) {
            slotValues.add(auth);
        }
        slotValues.add(orgId);
        if (auth != null) {
            slotValues.add(auth + "'s Role");// see if we need this
        } else {
            slotValues.add("System");
        }

        List<String> snames = new ArrayList<String>();
        if (auth != null) {
            snames.add(null);
        }
        snames.add(null);
        snames.add(null);
        List<ClassificationType> classifs = subset.getClassification();
        addClassifications(classifs, subId, "c101", "urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d", null, slotNames,
                slotValues, snames);

        slotNames = new ArrayList<String>();
        slotNames.add("codingScheme");
        slotValues = new ArrayList<String>();
        slotValues.add("Connect-a-thon contentTypeCodes");
        snames = new ArrayList<String>();
        snames.add(subject);
        addClassifications(classifs, subId, "c102", "urn:uuid:aa543740-bdda-424e-8c96-df4873be8500", subject,
                slotNames, slotValues, snames);

        List<ExternalIdentifierType> extIds = subset.getExternalIdentifier();
        addExternalIds(extIds, subId, "urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8", "ei01",
                "XDSSubmissionSet.uniqueId", subId);
        addExternalIds(extIds, subId, "urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832", "ei02",
                "XDSSubmissionSet.sourceId", orgId);
        addExternalIds(extIds, subId, "urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446", "ei03",
                "XDSSubmissionSet.patientId", patientId + "^^^&" + orgId);

        return subset;
    }

    protected ClassificationType getClassification(String setId) {
        ClassificationType ct = new ClassificationType();
        ct.setClassificationNode("urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd");
        ct.setClassifiedObject(setId);

        return ct;
    }

    protected AssociationType1 getAssociation(String setId, String docId) {
        AssociationType1 at = new AssociationType1();

        at.setAssociationType("HasMember");
        at.setSourceObject(setId);
        at.setTargetObject(docId);
        List<SlotType1> slots = at.getSlot();
        slots.add(makeSlot("SubmissionSetStatus", "Original"));
        return at;
    }

    protected SlotType1 makePatientSlot(String name, SimplePerson patient, String patientId, String orgId) {

        SlotType1 slot = new SlotType1();
        try {
            slot.setName(name);
            ValueListType values = new ValueListType();
            slot.setValueList(values);
            List<String> vals = values.getValue();

            if (patient != null) {

                String value1 = "PID-3|" + patientId + "^^^&amp;" + orgId + "&amp;ISO";// <rim:Value>PID-3|pid1^^^domain</rim:Value>
                String value2 = "PID-5|" + patient.getLastName() + "^" + patient.getFirstName() + "^"
                        + patient.getMiddleName();// <rim:Value>PID-5|Doe^John^^^</rim:Value>
                String value3 = "PID-7|" + formatDateFromMDM(patient.getBirthDateTime());// <rim:Value>PID-7|19560527</rim:Value>
                String value4 = "PID-8|" + patient.getGenderCode(); // <rim:Value>PID-8|M</rim:Value>
                String value5 = "PID11|" + patient.getStreetAddress1() + "^" + patient.getCity() + "^^"
                        + patient.getState() + "^" + patient.getZipCode() + "^" + patient.getCountry();// <rim:Value>PID-11|100
                                                                                                       // Main
                                                                                                       // St^^Metropolis^Il^44130^USA</rim:Value>

                vals.add(value1);
                vals.add(value2);
                vals.add(value3);
                vals.add(value4);
                vals.add(value5);
            }
        } catch (Exception x) {
            x.printStackTrace();
        }

        return slot;
    }

    protected void addClassifications(List<ClassificationType> classifs, String docId, String id, String scheme,
            String rep, List<String> slotNames, List<String> slotValues, List<String> snames) {

        ClassificationType ct = new ClassificationType();
        classifs.add(ct);
        ct.setClassifiedObject(docId);
        ct.setClassificationScheme(scheme);
        ct.setId(id);
        ct.setNodeRepresentation(rep);
        List<SlotType1> slots = ct.getSlot();
        Iterator is = slotNames.iterator();
        int icount = 0;
        while (is.hasNext()) {
            String slotName = (String) is.next();
            SlotType1 slot = makeSlot(slotName, (String) slotValues.get(icount));
            slots.add(slot);

            String sname = (String) snames.get(icount);
            if (sname != null) {
                InternationalStringType name = new InternationalStringType();
                List<LocalizedStringType> names = name.getLocalizedString();
                LocalizedStringType lname = new LocalizedStringType();
                lname.setValue(sname);
                names.add(lname);
                ct.setName(name);
            }
            icount++;
        }

    }

    protected void addExternalIds(List<ExternalIdentifierType> extIds, String docId, String scheme, String id,
            String sname, String value) {

        ExternalIdentifierType ei = new ExternalIdentifierType();
        extIds.add(ei);
        ei.setRegistryObject(docId);
        ei.setIdentificationScheme(scheme);
        ei.setId(id);

        if (sname != null) {
            InternationalStringType name = new InternationalStringType();
            List<LocalizedStringType> names = name.getLocalizedString();
            LocalizedStringType lname = new LocalizedStringType();
            lname.setValue(sname);
            names.add(lname);
            ei.setName(name);
        }
        ei.setValue(value);

    }

    protected String formatDate(Date dateVal) {

        String formout = "yyyyMMddHHmmss";

        SimpleDateFormat dateOut = new SimpleDateFormat(formout);
        String ret = null;
        try {

            ret = dateOut.format(dateVal);
        } catch (Exception x) {
            x.printStackTrace();
        }
        return ret;
    }

    protected String formatDateFromMDM(String value) {
        String formout = "yyyyMMddHHmmss";
        String formin = "MM/dd/yyyy";
        String ret = value;

        if (StringUtils.contains(value, "+")) {
            value = value.substring(0, value.indexOf("+"));
        }

        SimpleDateFormat date = new SimpleDateFormat(formin);
        SimpleDateFormat dateOut = new SimpleDateFormat(formout);
        Date dateVal = null;
        try {
            dateVal = date.parse(value);
            ret = dateOut.format(dateVal);
        } catch (Exception x) {
            x.printStackTrace();
        }
        return ret;
    }

    protected static SlotType1 makeSlot(String name, String value) {
        SlotType1 slot = new SlotType1();
        slot.setName(name);
        ValueListType values = new ValueListType();
        slot.setValueList(values);
        List<String> vals = values.getValue();
        vals.add(value);
        return slot;
    }

    protected void setHeaderData() {
        Long threadId = new Long(Thread.currentThread().getId());
        LOGGER.info("THREAD ID " + threadId);

        ThreadData threadData = new ThreadData(threadId);
        threadData.setTo(this.to);
        threadData.setMessageId(this.messageId);
        threadData.setRelatesTo(this.relatesTo);
        threadData.setAction(this.action);
        threadData.setThisHost(this.thisHost);
        threadData.setRemoteHost(this.remoteHost);
        threadData.setPid(this.pid);
        threadData.setFrom(this.from);

        LOGGER.info(threadData.toString());
    }
}
