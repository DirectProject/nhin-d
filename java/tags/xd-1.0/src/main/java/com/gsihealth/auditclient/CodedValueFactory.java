/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gsihealth.auditclient;

import org.xml.xml.schema._7f0d86bd.healthcare_security_audit.CodedValueType;

/**
 *
 * @author vlewis
 */
public class CodedValueFactory {



/*| code                                          | codeSystem       | codeSystemName    | displayName                         | originalText                        | codedValueTypeID |*/

static final String [][] codes = {{"1","110107","DCM","DCM","Import","Import"},
{"2","110153","DCM","DCM","Source","Source"},
{"3","110152","DCM","DCM","Destination","Destination"},
{"4","2","RFC-3881","RFC-3881","PatientNumber","PatientNumber"},
{"5","ITI-41","IHETransactions","IHETransactions","ProvideandRegisterDocumentSet-b","ProvideandRegisterDocumentSet-b"},
{"6","urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd","IHEXDSMetadata","IHEXDSMetadata","submissionsetclassificationNode","submissionsetclassificationNode"},
{"7","ITI-42","IHETransactions","IHETransactions","RegisterDocumentSet-b","RegisterDocumentSet-b"},
{"8","ITI-43","IHETransactions","IHETransactions","RetrieveDocumentSet-b","RetrieveDocumentSet-b"},
{"9","ITI-18","IHETransactions","IHETransactions","RegitryStoredQuery","RegitryStoredQuery"},
{"10","110106","DCM","DCM","Export","Export"},
{"11","9","RFC-3881","RFC-3881","ReportNumber","ReportNumber"},
{"12","110112","DCM","DCM","Query","Query"},
{"13","ITI-9","IHETransactions","IHETransactions","PIXQuery","PIXQuery"},
{"14","ITI-21","IHETransactions","IHETransactions","PatientDemographicsQuery","PatientDemographicsQuery"},
{"15","110110","DCM","DCM","PatientRecord","PatientRecord"},
{"16","ITI-8","IHETransactions","IHETransactions","PatientIdentityFeed","PatientIdentityFeed"}};


    public static CodedValueType getCodedValueType(int index){

        CodedValueType cvt = new CodedValueType();

        String[] temp = codes[index-1];
        cvt.setCode(temp[1]);
        cvt.setCodeSystem(temp[2]);
        cvt.setCodeSystemName(temp[3]);
        cvt.setDisplayName(temp[4]);
        cvt.setOriginalText(temp[5]);

        System.out.println(cvt);


        return cvt;

    }
}
