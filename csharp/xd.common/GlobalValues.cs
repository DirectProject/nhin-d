/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Vassil Peytchev     vassil@epic.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
namespace Health.Direct.Xd.Common
{
    public static class GlobalValues
    {
        //XML namespaces
        public const string HL7v3Namespace = "urn:hl7-org:v3";
        public const string IHEXDSbNamespace = "urn:ihe:iti:xds-b:2007";
        public const string ebXmlRS3Namespace = "urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0";
        public const string ebXmlLCMNamespace = "urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0";
        public const string ebXmlRIMNamespace = "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0";
        public const string ebXmlQueryNamespace = "urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0";
        public const string XSINamespace = "http://www.w3.org/2001/XMLSchema-instance";
        public const string Soap12Namespace = "http://www.w3.org/2003/05/soap-envelope";
        public const string WebServicesAddressingNamespace = "http://www.w3.org/2005/08/addressing";
        public const string WebServicesXmlMimeNamespace = "http://www.w3.org/2005/05/xmlmime";

        //Association Type
        public const string HasMember = "urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember";
        public const string RPLC = "urn:oasis:names:tc:ebxml-regrep:AssociationType:RPLC";
        public const string APND = "urn:oasis:names:tc:ebxml-regrep:AssociationType:APND";
        public const string XFRM = "urn:oasis:names:tc:ebxml-regrep:AssociationType:XFRM";
        public const string XFRM_RPLC = "urn:oasis:names:tc:ebxml-regrep:AssociationType:XFRM_RPLC";
        public const string signs = "urn:oasis:names:tc:ebxml-regrep:AssociationType:signs";
        
        public enum AssociationType
        {
            APND = 1, //The current document is an addendum to the parent document
            RPLC, // The current document is a replacement of the parent document
            XFRM, // The current document is a transformation of the parent document
            XFRM_RPLC // The current document is both transformation and a replacement of the parent document
        }

        //Registry Entry Status
        public const string Approved = "urn:oasis:names:tc:ebxml-regrep:StatusType:Approved";
        public const string Submitted = "urn:oasis:names:tc:ebxml-regrep:StatusType:Submitted";
        public const string Deprecated = "urn:oasis:names:tc:ebxml-regrep:StatusType:Deprecated";
        public const string Withdrawn = "urn:oasis:names:tc:ebxml-regrep:StatusType:Withdrawn";

        //IHE XDS Metadata UUIDs
        public const string APNDUUID = "urn:uuid:917dc511-f7da-4417-8664-de25b34d3def";
        public const string RPLCUUID = "urn:uuid:60fd13eb-b8f6-4f11-8f28-9ee000184339";
        public const string signsUUID = "urn:uuid:8ea93462-ad05-4cdc-8e54-a102584f6aff94";
        public const string XDSDocumentEntry_authorDescriptionUUID = "urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d";
        public const string XDSDocumentEntry_classCodeUUID = "urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a";
        public const string XDSDocumentEntry_confidentialityCodeUUID = "urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f";
        public const string XDSDocumentEntry_eventCodeListUUID = "urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4";
        public const string XDSDocumentEntry_formatCodeUUID = "urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d";
        public const string XDSDocumentEntry_healthCareFacilityTypeCodeUUID = "urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1";
        public const string XDSDocumentEntry_mimeTypeUUID = "urn:uuid:3dc97051-710256-41a8-a48b-8fce7af682d0";
        public const string XDSDocumentEntry_patientIdUUID = "urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427";
        public const string XDSDocumentEntry_practiceSettingCodeUUID = "urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead";
        public const string XDSDocumentEntry_typeCodeUUID = "urn:uuid:f0306f51-975f-434e-a61c-c59651d33983";
        public const string XDSDocumentEntry_uniqueIdUUID = "urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab";
        public const string XDSDocumentEntryStubUUID = "urn:uuid:10aa1a4b-715a-4120-bfd0-9760414112c8";
        public const string XDSDocumentEntryUUID = "urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1";
        public const string XDSFolder_codeListUUID = "urn:uuid:1ba97051-710256-41a8-a48b-8fce7af683c5";
        public const string XDSFolder_patientIdUUID = "urn:uuid:f64ffdf0-4b97-4e06-b79f-a52b38ec2f8a";
        public const string XDSFolder_uniqueIdUUID = "urn:uuid:75df8f67-9973-4fbe-a900-df66cefecc5a";
        public const string XDSFolderUUID = "urn:uuid:d9d542f3-6cc4-48b6-8870-ea235fbc94c2";
        public const string XDSSubmissionSetUUID = "urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd";
        public const string XDSSubmissionSet_authorDescriptionUUID = "urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d";
        public const string XDSSubmissionSet_contentTypeCodeUUID = "urn:uuid:aa543740-bdda-424e-8c96-df4873be8500";
        public const string XDSSubmissionSet_patientIdUUID = "urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446";
        public const string XDSSubmissionSet_sourceIdUUID = "urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832";
        public const string XDSSubmissionSet_uniqueIdUUID = "urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8";
        public const string XFRM_RPLCUUID = "urn:uuid:b76a27c7-af3c-4319-ba4c-b90c1dc45408";
        public const string XFRMUUID = "urn:uuid:ede379e6-1147-4374-a943-8fcdcf1cd620";


        public const string CONST_RESPONSE_STATUS_TYPE_FAILURE = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Failure";
        public const string CONST_RESPONSE_STATUS_TYPE_SUCCESS = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success";
        public const string CONST_RESPONSE_STATUS_TYPE_PARTIALSUCCESS = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:PartialSuccess";

        public const string CONST_AVAILABILITYSTATUS_APPROVED = "urn:oasis:names:tc:ebxml-regrep:StatusType:Approved";
        public const string CONST_AVAILABILITYSTATUS_DEPRECATED = "urn:oasis:names:tc:ebxml-regrep:StatusType:Deprecated";

        // iLink
        public const string CONST_ASSOCIATION_TYPE_APND = "urn:ihe:iti:2007:AssociationType:APND";
        public const string CONST_ASSOCIATION_TYPE_RPLC = "urn:ihe:iti:2007:AssociationType:RPLC";
        public const string CONST_ASSOCIATION_TYPE_XFRM = "urn:ihe:iti:2007:AssociationType:XFRM";
        public const string CONST_ASSOCIATION_TYPE_XFRM_RPLC = "urn:ihe:iti:2007:AssociationType:XFRM_RPLC";



        public const string CONST_ACTION_ProvideAndRegisterDocumentSet_bResponse = "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-bResponse";

        public const string CONST_ACTION_RegisterDocumentSetResponse = "urn:ihe:iti:2007:RegisterDocumentSet-bResponse";
        public const string CONST_ACTION_RegistryStoredQueryResponse = "urn:ihe:iti:2007:RegistryStoredQueryResponse";

        public const string CONST_SEVERITY_TYPE_ERROR = "urn:oasis:names:tc:ebxml-regrep:ErrorSeverityType:Error";

        #region "Common Constant between Registry and Repository"
        public const string CONST_ERROR_CODE_XDSAuthorizationException = "XDSAuthorizationException";
        public const string CONST_ERROR_CODE_XDSInvalidRequest = "XDSInvalidRequest";
        public const string CONST_ERROR_CODE_TimeOut = "TimeOut";
        #endregion

        #region "Constant for Repository"
        public const string CONST_ERROR_CODE_XDSMissingDocumentMetadata = "XDSMissingDocumentMetadata";
        public const string CONST_ERROR_CODE_XDSMissingDocumentAttachment = "XDSMissingDocumentAttachment";
        public const string CONST_ERROR_CODE_XDSMissingMetadata = "XDSMissingMetadata";
        public const string CONST_ERROR_CODE_XDSMissingDocument = "XDSMissingDocument";
        public const string CONST_ERROR_CODE_XDSRepositoryTooBusyException = "XDSRepositoryTooBusyException";
        public const string CONST_ERROR_CODE_XDSRepositoryMetadataError = "XDSRepositoryMetadataError";
        public const string CONST_ERROR_CODE_XDSRepositoryError = "XDSRepositoryError";
        public const string CONST_ERROR_CODE_XDSRepositoryDuplicateUniqueIdInMessage = "XDSRepositoryDuplicateUniqueIdInMessage";
        public const string CONST_ERROR_CODE_XDSUnknownRepositoryUniqueID = "XDSUnknownRepositoryUniqueID";
        public const string CONST_ERROR_CODE_XDSResultNotSinglePatient = "XDSResultNotSinglePatient";
        public const string CONST_ERROR_CODE_XDSRepositoryOutOfResources = "XDSRepositoryOutOfResources";
        public const string CONST_ERROR_CODE_XDSRegistryNotAvailable = "XDSRegistryNotAvailable";
        public const string CONST_ACTION_RETRIEVEDOCSETRESPONSE = "urn:ihe:iti:2007:RetrieveDocumentSetResponse";
        public const string CONST_ACTION_REGISTERDOCUMENTSETB = "urn:ihe:iti:2007:RegisterDocumentSet-b";
        #endregion


        #region "Constant for Registry"
        public const string CONST_REGISTRYERROR_CODE_XDSRegistryTooBusyException = "XDSRegistryTooBusyException";
        public const string CONST_REGISTRYERROR_CODE_UnsupportedCapabilityException = "UnsupportedCapabilityException";
        public const string CONST_REGISTRYERROR_CODE_XDSRegistryError = "XDSRegistryError";
        public const string CONST_REGISTRYERROR_CODE_XDSRegistryDuplicateUniqueIdInMessage = "XDSRegistryDuplicateUniqueIdInMessage";
        public const string CONST_REGISTRYERROR_CODE_XDSDuplicateUniqueIdInRegistry = "XDSDuplicateUniqueIdInRegistry";
        public const string CONST_REGISTRYERROR_CODE_XDSNonIdenticalHash = "XDSNonIdenticalHash";
        public const string CONST_REGISTRYERROR_CODE_XDSRegistryBusy = "XDSRegistryBusy";
        public const string CONST_REGISTRYERROR_CODE_XDSRegistryOutOfResources = "XDSRegistryOutOfResources";
        public const string CONST_REGISTRYERROR_CODE_XDSRegistryMetadataError = "XDSRegistryMetadataError";
        public const string CONST_REGISTRYERROR_CODE_XDSExtraMetadataNotSaved = "XDSExtraMetadataNotSaved";
        public const string CONST_REGISTRYERROR_CODE_XDSUnknownPatientId = "XDSUnknownPatientId";
        public const string CONST_REGISTRYERROR_CODE_XDSPatientIdDoesNotMatch = "XDSPatientIdDoesNotMatch";
        //Sekar-iLink
        public const string CONST_SLOT_LASTUPDATETIME = "lastUpdateTime";
        #endregion

    }
}