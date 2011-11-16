/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Vincent Lewis     vincent.lewis@gsihealth.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
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
