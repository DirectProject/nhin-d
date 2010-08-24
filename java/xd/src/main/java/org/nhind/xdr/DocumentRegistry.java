/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nhind.xdr;

import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.xml.bind.JAXBElement;
import javax.xml.ws.WebServiceContext;

import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryPackageType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Vince
 */
public class DocumentRegistry {

    @Resource
    protected SessionContext sessionContext;
    protected WebServiceContext mywscontext;

    String euid = null;
    String ssn = null;
    String orgId = null;
    String patientId = null;
    String sourceDocId = null;
    String sourceObjectId = null;
    List<String> forwards = null;
  
    public static final int PNR = 1;
    public static final int REG = 2;
    private boolean nhdirect = false;
    
    private String author = null;
    
    private static final Logger LOGGER = Logger.getLogger(DocumentRegistry.class.getPackage().getName());

    public String parseRegistry(ProvideAndRegisterDocumentSetRequestType prdst) throws Exception {
        SubmitObjectsRequest sor = prdst.getSubmitObjectsRequest();
        return parseRegistryData(sor, PNR);
    }

    protected String parseRegistryData(SubmitObjectsRequest sor, int ttype) throws Exception {
        String ret = null;

        try {
            if (sor != null) {
                RegistryObjectListType rol = sor.getRegistryObjectList();
                List extensible = rol.getIdentifiable();
                Iterator iext = extensible.iterator();
                while (iext.hasNext()) {
                    JAXBElement elem = (JAXBElement) iext.next();
                    String type = elem.getDeclaredType().getName();
                    Object value = elem.getValue();
                    if (type.equals("oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType")) {
                        ret = parseDocument((ExtrinsicObjectType) value);
                    } else if (type.equals("oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryPackageType")) {
                        // parseSubmissionSet((RegistryPackageType) value);
                        forwards = getForwards((RegistryPackageType) value);
                    }  else if (type.equals("oasis.names.tc.ebxml_regrep.xsd.rim._3.AssociationType1")) {
                    }
                    LOGGER.info(elem.getDeclaredType().getName() + elem.getValue().toString());
                }
                if (nhdirect && (forwards == null || forwards.isEmpty() || ((String) forwards.get(0)).equals("no endpoint"))) {
                    throw new Exception("NO ENDPOINT for IntendedRecipient");
                }
            }
        } catch (Exception x) {
            x.printStackTrace();
            throw (x);
        }
        return ret;
    }

    public List<String> getForwards() {
        return forwards;
    }

    /**
     * Extract an email address from an HL7 string using "^" delimiter.
     * 
     * @return the extracted email address
     */
    public String getAuthorEmail() {
        // Literal string
        if (!StringUtils.contains(author, "^")) {
            // If string is an email
            if (StringUtils.contains(author, "@")) {
                return author;
            } else {
                // TODO: What happens when an author email address is not provided?
                LOGGER.info("Author email not found");
                return "postmaster@nhindirect.org";
            }
        }
        
        String auserId = null;
        List<String> fields = split(author, "^");

        if (fields != null) {
            if (fields.get(0) != null) {
                auserId = fields.get(0);
            }
        }

        /*
         * String last = fields.get(1);
         * String first = fields.get(2);
         * String mid = fields.get(3);
         * String what = fields.get(4);
         * String prefix = fields.get(5);
         * String suffix = fields.get(6);
         * String what2 = fields.get(7);
         * String org = fields.get(8);
         * List<String> orgs = split(org, "&");
         * String aorgId = (String) orgs.get(1);
         * aorgId = aorgId.replace("ISO", "");
         */

        return auserId;
    }

    protected List<String> getForwards(RegistryPackageType rpt) throws Exception {
        List<String> forwards = new ArrayList<String>();

        for (SlotType1 slot : rpt.getSlot()) {
            String slotName = slot.getName();
            LOGGER.info(slotName);
            LOGGER.info(slot.getSlotType());
            LOGGER.info(slot.getValueList().getValue().get(0));

            if (slotName.equals("intendedRecipient")) {
                for (String prov : slot.getValueList().getValue()) {
                    StringTokenizer ids = new StringTokenizer(prov, "|");
                    String user = null;
                    String org = ids.nextToken();
                    if (ids.countTokens() > 1) {
                        user = ids.nextToken();
                    } else {
                        user = org;
                    }
                    List<String> fields = split(user, "^");

                    String userId = fields.get(0);
                    String last = fields.get(1);
                    String first = fields.get(2);
                    String mid = fields.get(3);
                    String what = fields.get(4);
                    String prefix = fields.get(5);
                    String suffix = fields.get(6);
                    String what2 = fields.get(7);
                    org = fields.get(8);
                    List<String> orgs = split(org, "&");
                    orgId = (String) orgs.get(1);
                 
                    orgId = orgId.replace("ISO", "");

                    String sendPoint = userId;
                    
                    /*
                     * TODO: define when and where to relay vs. email this.
                     * intentended recipient
                     * |john.smith@happyvalleyclinic.nhindirect.org^Smith^John^^^Dr^MD^^&amp;1.3.6.1.4.1.21367.3100.1
                     * casues a forward to
                     * "http://shinnytest.gsihealth.com:8080/DocumentRepository_Service/DocumentRepository?wsdl"
                     * otherwise the email is used for XDM
                     */
                    if(userId.equals("john.smith@happyvalleyclinic.nhindirect.org")){
                         sendPoint = "http://shinnytest.gsihealth.com:8080/DocumentRepository_Service/DocumentRepository?wsdl";
                    }

                    forwards.add(sendPoint);
                    
                }
            }
        }
        return forwards;
    }

    private String returnField(String in, String token, int field) {
        StringTokenizer list = new StringTokenizer(in, token);
        String ret = in;
        int count = 0;
        while (list.hasMoreElements()) {
            String temp = list.nextToken();
            if (++count == field) {
                ret = temp;
            }
        }
        return ret;
    }

    private String parseClassifications(List<ClassificationType> classes) throws Exception {
        String lauthor = null;

        try {
            for (ClassificationType clas : classes) {
                for (SlotType1 slot : clas.getSlot()) {
                    String sname = slot.getName();
                    if (sname.equals("authorPerson")
                            && slot.getValueList() != null
                            && slot.getValueList().getValue() != null) {
                        for (String value : slot.getValueList().getValue()) {
                            lauthor = value;
                        }
                    }
                }
            }
        } catch (Exception x) {
            x.printStackTrace();
            throw (x);
        }
        
        return lauthor;
    }

    private String parseDocument(ExtrinsicObjectType document) throws Exception {
        sourceObjectId = document.getId();

        String objectType = document.getObjectType();
        String mimeType = document.getMimeType();
        String firstName = null;
        String lastName = null;
        String streetAddress = null;
        String gender = null;
        String city = null;
        String state = null;
        String zip = null;
        String birth = null;
        String creationDate = null;
        String startDate = null;
        String stopDate = null;
        String lang = null;
        String reposId = null;

        boolean repos = false;
        
        for (SlotType1 slot : document.getSlot()) {
            if (slot.getName().equals("creationTime")) {
                creationDate = slot.getValueList().getValue().get(0);

            } else if (slot.getName().equals("languageCode")) {
                lang = slot.getValueList().getValue().get(0);

            } else if (slot.getName().equals("sourcePatientId")) {
                String idSeg = slot.getValueList().getValue().get(0);
                idSeg = idSeg.replaceAll("\\^\\^", "^null^");
                patientId = returnField(returnField(idSeg, "|", 2), "^", 1);
                patientId = patientId.replaceAll("'", "");
                String orgIdComp = returnField(returnField(idSeg, "|", 2), "^", 4);
                orgId = returnField(orgIdComp, "&", 2);
                if (slot.getValueList().getValue().size() > 1) {
                    idSeg = slot.getValueList().getValue().get(1);
                    idSeg = idSeg.replaceAll("\\^\\^", "^null^");
                    String test = returnField(returnField(idSeg, "|", 2), "^", 4);
                    test = returnField(test, "&", 2);
                   
                }

            } else if (slot.getName().equals("sourcePatientInfo")) {
                for (String pid : slot.getValueList().getValue()) {
                    LOGGER.info(pid);
                    if (pid.indexOf("PID-5") == 0) {
                        String name = returnField(pid, "|", 2);
                        firstName = returnField(name, "^", 2);
                        lastName = returnField(name, "^", 1);

                    } else if (pid.indexOf("PID-7") == 0) {
                        String temp = returnField(returnField(pid, "|", 2), "^", 1);
                        birth = formatDateForMDM(temp);
                    } else if (pid.indexOf("PID-8") == 0) {
                        gender = returnField(returnField(pid, "|", 2), "^", 1);

                    } else if (pid.indexOf("PID-11") == 0) {
                        String name = returnField(pid, "|", 2);
                        name = name.replaceAll("\\^\\^", "^null^");
                        streetAddress = returnField(name, "^", 1);
                        city = returnField(name, "^", 3);
                        state = returnField(name, "^", 4);
                        zip = returnField(name, "^", 5);
                    }
                }
            } else if (slot.getName().equals("serviceStartTime")) {
                List<String> list = slot.getValueList().getValue();
                if (list.size() > 0) {
                    startDate = list.get(0);
                }
            } else if (slot.getName().equals("serviceStopTime")) {
                List<String> list = slot.getValueList().getValue();
                if (list.size() > 0) {
                    stopDate = slot.getValueList().getValue().get(0);
                }
            } else if (slot.getName().equals("repositoryUniqueId")) {
                List<String> list = slot.getValueList().getValue();
                if (list.size() > 0) {
                    reposId = slot.getValueList().getValue().get(0);
                    repos = true;
                }
            }

        }
        List<ClassificationType> classes = document.getClassification();
        author = parseClassifications(classes);

        return mimeType;
    }

    // right now this is not used, just getForwards
    @SuppressWarnings("unused")
    private void parseSubmissionSet(RegistryPackageType set) throws Exception {

        try {

            String setId = set.getId();
            String objectType = set.getObjectType();

            String submissionDate = null;
            String setName = null;
            String setDesc = null;
            String reposId = null;
            String recipient = null;
            boolean repos = false;
            nhdirect = false;

            for (SlotType1 slot : set.getSlot()) {
                if (slot.getName().equals("submissionTime")) {
                    try {
                        submissionDate = slot.getValueList().getValue().get(0);
                    } catch (Exception x) {
                        submissionDate = null;
                    }

                } else if (slot.getName().equals("repositoryUniqueId")) {
                    reposId = slot.getValueList().getValue().get(0);
                    repos = true;
                } else if (slot.getName().equals("intendedRecipient")) {
                    recipient = slot.getValueList().getValue().get(0);
                    nhdirect = true;
                }
            }
            try {
                setName = set.getName().getLocalizedString().get(0).getValue();
            } catch (Exception x) {
                setName = "unknown";
            }

            try {
                setDesc = set.getDescription().getLocalizedString().get(0).getValue();
            } catch (Exception x) {
                setDesc = "";
            }


        } catch (Exception x) {
            x.printStackTrace();
         
            throw (x);
        }

    }

   private String formatDateForMDM(String value) {
        String form;
        String formOut = "MM/dd/yyyy";
        String ret = value;
        if (value.indexOf("/") >= 0) {
            return ret;
        }
        if (value.indexOf("+") >= 0) {
            value = value.substring(0, value.indexOf("+"));
        }
        int valen = value.length();
        if (value.indexOf(" ") >= 0) {
            form = "yyyyMMdd HH:mm:ss";

        } else if (value.indexOf("T") >= 0) {

            form = "yyyyMMdd'T'HH:mm:ss";
        } else {
            form = "yyyyMMddHHmmss";
        }

        form = form.substring(0, valen);


        SimpleDateFormat date = new SimpleDateFormat(form);
        SimpleDateFormat dateOut = new SimpleDateFormat(formOut);
        Date dateVal = null;
        try {
            dateVal = date.parse(value);
            ret = dateOut.format(dateVal);
        } catch (Exception x) {
            x.printStackTrace();
        }
        return ret;
    }

    /**
     * Split up a string using the given delimiter and return as a list of
     * tokens
     * 
     * @param input
     *            The string to split
     * @param delimiter
     *            The delimiter used for splitting
     * @return a list of split tokens
     */
    private List<String> split(String input, String delimiter) {
        String quotedDelimiter = Pattern.quote(delimiter);
        List<String> tokens = Arrays.asList(input.split(quotedDelimiter, -1));

        return tokens;
    }

    /**
     * Return the value of author.
     * 
     * @return the value of author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Set the value of author.
     * 
     * @param author
     *            The value of author
     */
    public void setAuthor(String author) {
        this.author = author;
    }
}
