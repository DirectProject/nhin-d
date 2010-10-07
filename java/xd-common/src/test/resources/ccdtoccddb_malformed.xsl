<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ns1="http://xml.gsihealth.com/schema/ccddb" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:n="urn:hl7-org:v3" exclude-result-prefixes="n xs xsi xsl">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <xsl:template match="/">
        <CCDDB>
            <xsl:attribute name="xsi:noNamespaceSchemaLocation">
                <xsl:value-of select="'C:/Users/Public/GSIFolders/NBProjects/wsdl/ccddb.xsd'"/>
            </xsl:attribute>
            <xsl:variable name="var1_instance" select="."/>
            <FACILITY>
                <FACILITY_OID>1.2.3.4.5.6.7</FACILITY_OID>
                <MAIN_FACILITY_NAME>MY VERY OWN RHIO</MAIN_FACILITY_NAME>
            </FACILITY>

            <xsl:for-each select="$var1_instance/n:ClinicalDocument/n:recordTarget">
                <PATIENT>
                    <xsl:for-each_ select="n:patientRole/n:id">
                        <xsl:variable name="var16_id" select="."/>
                        <xsl:if test="$var16_id/@root">
                            <FACILITY_ID>
                                <xsl:value-of select="string(@root)"/>
                            </FACILITY_ID>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:for-each select="n:patientRole/n:id">
                        <xsl:variable name="var18_id" select="."/>
                        <xsl:if test="$var18_id/@extension">
                            <PATIENT_ID>
                                <xsl:value-of select="string(@extension)"/>
                            </PATIENT_ID>
                        </xsl:if>

                    <xsl:for-each select="n:patientRole/n:patient/n:name/n:given">
                        <FIRST_NAME>
                            <xsl:value-of select="string(.)"/>

                    </xsl:for-each>
                    <xsl:for-each select="n:patientRole/n:patient/n:name/n:family">
                        <LAST_NAME>
                            <xsl:value-of select="string(.)"/>
                        </LAST_NAME>
                    </xsl:for-each>
                    <xsl:for-each select="n:patientRole/n:addr/n:streetAddressLine">
                        <STREET_ADDRESS_1>
                            <xsl:value-of select="string(.)"/>
                        </STREET_ADDRESS_1>
                    </xsl:for-each>
                    <xsl:for-each select="n:patientRole/n:telecom">
                        <xsl:variable name="var26_telecom" select="."/>
                        <xsl:if test="$var26_telecom/@value">
                            <TELEPHONE>
                                <xsl:value-of select="string(@value)"/>
                            </TELEPHONE>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:for-each select="n:patientRole/n:patient/n:birthTime">
                        <xsl:variable name="var28_birthTime" select="."/>
                        <xsl:if test="$var28_birthTime/@value">
                            <BIRTH_DATE_TIME>
                                <xsl:value-of select="string(@value)"/>
                            </BIRTH_DATE_TIME>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:for-each select="n:patientRole/n:patient/n:languageCommunication/n:languageCode">
                        <xsl:variable name="var30_languageCode" select="."/>
                        <xsl:if test="$var30_languageCode/@code">
                            <LANGUAGE_CODE>
                                <xsl:value-of select="string(@code)"/>
                            </LANGUAGE_CODE>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:for-each select="n:patientRole/n:patient/n:ethnicGroupCode">
                        <xsl:variable name="var32_ethnicGroupCode" select="."/>
                        <xsl:if test="$var32_ethnicGroupCode/@code">
                            <ETHNICITY_CODE>
                                <xsl:value-of select="string(@code)"/>
                            </ETHNICITY_CODE
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:for-each select="n:patientRole/n:patient/n:administrativeGenderCode">
                        <xsl:variable name="var34_administrativeGenderCode" select="."/>
                        <xsl:if test="$var34_administrativeGenderCode/@code">
                            <GENDER_CODE>
                                <xsl:value-of select="string(@code)"/>
                            </GENDER_CODE>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:for-each select="n:patientRole/n:addr/n:postalCode">
                        <ZIP_CODE>
                            <xsl:value-of select="string(.)"/>
                        </ZIP_CODE>
                    </xsl:for-each>
                    <xsl:for-each select="n:patientRole/n:addr/n:state">
                        <STATE>
                            <xsl:value-of select="string(.)"/>
                        </STATE>
                    </xsl:for-each>
                    <xsl:for-each select="n:patientRole/n:addr/n:county">
                        <COUNTY>
                            <xsl:value-of select="string(.)"/>
                        </COUNTY>
                    </xsl:for-each>
                    <xsl:for-each select="n:patientRole/n:addr/n:city">
                        <CITY>
                            <xsl:value-of select="string(.)"/>
                        </CITY>
                    </xsl:for-each>
                    <xsl:for-each select="n:patientRole/n:providerOrganization/n:id">
                        <xsl:variable name="var44_id" select="."/>
                        <xsl:if test="$var44_id/@root">
                            <PCP_OID>
                                <xsl:value-of select="string(@root)"/>
                            </PCP_OID>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:for-each select="n:patientRole/n:providerOrganization/n:name">
                        <PCP_NAME>
                            <xsl:value-of select="string(.)"/>
                        </PCP_NAME>
                    </xsl:for-each>
                    <xsl:for-each select="n:patientRole/n:patient/n:ethnicGroupCode">
                        <xsl:variable name="var48_ethnicGroupCode" select="."/>
                        <xsl:if test="$var48_ethnicGroupCode/@codeSystem">
                            <ETHNICITY_CODE_SYSTEM>
                                <xsl:value-of select="string(@codeSystem)"/>
                            </ETHNICITY_CODE_SYSTEM>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:for-each select="n:patientRole/n:patient/n:ethnicGroupCode">
                        <xsl:variable name="var50_ethnicGroupCode" select="."/>
                        <xsl:if test="$var50_ethnicGroupCode/@displayName">
                            <ETHNICITY_CODE_NAME>
                                <xsl:value-of select="string(@displayName)"/>
                            </ETHNICITY_CODE_NAME>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:for-each select="n:patientRole/n:patient/n:administrativeGenderCode">
                        <xsl:variable name="var52_administrativeGenderCode" select="."/>
                        <xsl:if test="$var52_administrativeGenderCode/@codeSystem">
                            <GENDER_CODE_SYSTEM>
                                <xsl:value-of select="string(@codeSystem)"/>
                            </GENDER_CODE_SYSTEM>
                        </xsl:if>
                    </xsl:for-each>
                </PATIENT>
            </xsl:for-each>
            <xsl:for-each select="$var1_instance/n:ClinicalDocument/n:author">
                <xsl:variable name="var54_author" select="."/>
                <PROVIDER>
                    <xsl:for-each select="n:assignedAuthor/n:id">
                        <xsl:variable name="var56_id" select="."/>
                        <xsl:if test="$var56_id/@root">
                            <PROVIDER_OID>
                                <xsl:value-of select="string(@root)"/>
                            </PROVIDER_OID>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:if test="$var54_author/n:time/@value">
                        <EVENT_START_DATE_TIME>
                            <xsl:value-of select="string(n:time/@value)"/>
                        </EVENT_START_DATE_TIME>
                    </xsl:if>
                    <xsl:for-each select="n:assignedAuthor/n:representedOrganization/n:id">
                        <xsl:variable name="var58_id" select="."/>
                        <xsl:if test="$var58_id/@root">
                            <ORGANIZATION_OID>
                                <xsl:value-of select="string(@root)"/>
                            </ORGANIZATION_OID>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:for-each select="n:assignedAuthor/n:representedOrganization/n:name">
                        <ORGANIZATION_NAME>
                            <xsl:value-of select="string(.)"/>
                        </ORGANIZATION_NAME>
                    </xsl:for-each>
                    <xsl:for-each select="n:assignedAuthor/n:assignedPerson/n:name/n:given">
                        <PERSON_FIRST_NAME>
                            <xsl:value-of select="string(.)"/>
                        </PERSON_FIRST_NAME>
                    </xsl:for-each>
                    <xsl:for-each select="n:assignedAuthor/n:assignedPerson/n:name/n:prefix">
                        <PERSON_PREFIX>
                            <xsl:value-of select="string(.)"/>
                        </PERSON_PREFIX>
                    </xsl:for-each>
                    <xsl:for-each select="n:assignedAuthor/n:assignedPerson/n:name/n:family">
                        <PERSON_LAST_NAME>
                            <xsl:value-of select="string(.)"/>
                        </PERSON_LAST_NAME>
                    </xsl:for-each>
                    <xsl:for-each select="n:assignedAuthor/n:assignedAuthoringDevice/n:softwareName">
                        <SOFTWARE_NAME>
                            <xsl:value-of select="string(.)"/>
                        </SOFTWARE_NAME>
                    </xsl:for-each>
                    <xsl:for-each select="n:assignedAuthor/n:id">
                        <xsl:variable name="var70_id" select="."/>
                        <xsl:if test="$var70_id/@root">
                            <PROVIDER_TYPE_CODE>
                                <xsl:value-of select="concat('author', substring(string(@root), 0, 0))"/>
                            </PROVIDER_TYPE_CODE>
                        </xsl:if>
                    </xsl:for-each>
                </PROVIDER>
            </xsl:for-each>
            <xsl:for-each select="$var1_instance/n:ClinicalDocument/n:documentationOf">
                <PROVIDER>
                    <xsl:for-each select="n:serviceEvent/n:performer/n:assignedEntity/n:id">
                        <xsl:variable name="var74_id" select="."/>
                        <xsl:if test="$var74_id/@root">
                            <PROVIDER_OID>
                                <xsl:value-of select="string(@root)"/>
                            </PROVIDER_OID>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:for-each select="n:serviceEvent/n:effectiveTime/n:low">
                        <xsl:variable name="var76_low" select="."/>
                        <xsl:if test="$var76_low/@value">
                            <EVENT_START_DATE_TIME>
                                <xsl:value-of select="string(@value)"/>
                            </EVENT_START_DATE_TIME>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:for-each select="n:serviceEvent/n:effectiveTime/n:high">
                        <xsl:variable name="var78_high" select="."/>
                        <xsl:if test="$var78_high/@value">
                            <EVENT_END_DATE_TIME>
                                <xsl:value-of select="string(@value)"/>
                            </EVENT_END_DATE_TIME>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:for-each select="n:serviceEvent/n:performer/n:assignedEntity/n:representedOrganization/n:id">
                        <xsl:variable name="var80_id" select="."/>
                        <xsl:if test="$var80_id/@root">
                            <ORGANIZATION_OID>
                                <xsl:value-of select="string(@root)"/>
                            </ORGANIZATION_OID>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:for-each select="n:serviceEvent/n:performer/n:assignedEntity/n:representedOrganization/n:name">
                        <ORGANIZATION_NAME>
                            <xsl:value-of select="string(.)"/>
                        </ORGANIZATION_NAME>
                    </xsl:for-each>
                    <xsl:for-each select="n:serviceEvent/n:performer">
                        <PERFORMER_TYPE_CODE>
                            <xsl:value-of select="string(@typeCode)"/>
                        </PERFORMER_TYPE_CODE>
                    </xsl:for-each>
                    <xsl:for-each select="n:serviceEvent/n:performer/n:functionCode">
                        <xsl:variable name="var86_functionCode" select="."/>
                        <xsl:if test="$var86_functionCode/@code">
                            <FUNCTION_CODE>
                                <xsl:value-of select="string(@code)"/>
                            </FUNCTION_CODE>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:for-each select="n:serviceEvent/n:performer/n:functionCode">
                        <xsl:variable name="var88_functionCode" select="."/>
                        <xsl:if test="$var88_functionCode/@codeSystem">
                            <FUNCTION_CODE_SYSTEM>
                                <xsl:value-of select="string(@codeSystem)"/>
                            </FUNCTION_CODE_SYSTEM>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:for-each select="n:serviceEvent/n:performer/n:functionCode">
                        <xsl:variable name="var90_functionCode" select="."/>
                        <xsl:if test="$var90_functionCode/@displayName">
                            <FUNCTION_CODE_NAME>
                                <xsl:value-of select="string(@displayName)"/>
                            </FUNCTION_CODE_NAME>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:for-each select="n:serviceEvent/n:performer/n:assignedEntity/n:assignedPerson/n:name/n:given">
                        <PERSON_FIRST_NAME>
                            <xsl:value-of select="string(.)"/>
                        </PERSON_FIRST_NAME>
                    </xsl:for-each>
                    <xsl:for-each select="n:serviceEvent/n:performer/n:assignedEntity/n:assignedPerson/n:name/n:prefix">
                        <PERSON_PREFIX>
                            <xsl:value-of select="string(.)"/>
                        </PERSON_PREFIX>
                    </xsl:for-each>
                    <xsl:for-each select="n:serviceEvent/n:performer/n:assignedEntity/n:assignedPerson/n:name/n:family">
                        <PERSON_LAST_NAME>
                            <xsl:value-of select="string(.)"/>
                        </PERSON_LAST_NAME>
                    </xsl:for-each>
                    <xsl:for-each select="n:serviceEvent/n:performer/n:assignedEntity/n:id">
                        <xsl:variable name="var98_id" select="."/>
                        <xsl:if test="$var98_id/@root">
                            <PROVIDER_TYPE_CODE>
                                <xsl:value-of select="concat('documented', substring(string(@root), 0, 0))"/>
                            </PROVIDER_TYPE_CODE>
                        </xsl:if>
                    </xsl:for-each>
                </PROVIDER>
            </xsl:for-each>
            <xsl:for-each select="$var1_instance/n:ClinicalDocument/n:informant">
                <REPRESENTED_ORGANIZATION>
                    <xsl:for-each select="n:assignedEntity/n:representedOrganization/n:id">
                        <xsl:variable name="var102_id" select="."/>
                        <xsl:if test="$var102_id/@root">
                            <ORGANIZATION_OID>
                                <xsl:value-of select="string(@root)"/>
                            </ORGANIZATION_OID>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:for-each select="n:assignedEntity/n:representedOrganization/n:name">
                        <ORGANIZATION_NAME>
                            <xsl:value-of select="string(.)"/>
                        </ORGANIZATION_NAME>
                    </xsl:for-each>
                    <xsl:for-each select="n:assignedEntity/n:representedOrganization/n:id">
                        <xsl:variable name="var106_id" select="."/>
                        <xsl:if test="$var106_id/@root">
                            <REPRESENTATION_TYPE_CODE>
                                <xsl:value-of select="concat('informant', substring(string(@root), 0, 0))"/>
                            </REPRESENTATION_TYPE_CODE>
                        </xsl:if>
                    </xsl:for-each>
                </REPRESENTED_ORGANIZATION>
            </xsl:for-each>
            <xsl:for-each select="$var1_instance/n:ClinicalDocument/n:legalAuthenticator">
                <REPRESENTED_ORGANIZATION>
                    <xsl:for-each select="n:assignedEntity/n:representedOrganization/n:id">
                        <xsl:variable name="var110_id" select="."/>
                        <xsl:if test="$var110_id/@root">
                            <ORGANIZATION_OID>
                                <xsl:value-of select="string(@root)"/>
                            </ORGANIZATION_OID>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:for-each select="n:assignedEntity/n:representedOrganization/n:name">
                        <ORGANIZATION_NAME>
                            <xsl:value-of select="string(.)"/>
                        </ORGANIZATION_NAME>
                    </xsl:for-each>
                    <xsl:for-each select="n:assignedEntity/n:representedOrganization/n:id">
                        <xsl:variable name="var114_id" select="."/>
                        <xsl:if test="$var114_id/@root">
                            <REPRESENTATION_TYPE_CODE>
                                <xsl:value-of select="concat('legal', substring(string(@root), 0, 0))"/>
                            </REPRESENTATION_TYPE_CODE>
                        </xsl:if>
                    </xsl:for-each>
                </REPRESENTED_ORGANIZATION>
            </xsl:for-each>
            <xsl:for-each select="$var1_instance/n:ClinicalDocument/n:custodian">
                <REPRESENTED_ORGANIZATION>
                    <xsl:for-each select="n:assignedCustodian/n:representedCustodianOrganization/n:id">
                        <xsl:variable name="var118_id" select="."/>
                        <xsl:if test="$var118_id/@root">
                            <ORGANIZATION_OID>
                                <xsl:value-of select="string(@root)"/>
                            </ORGANIZATION_OID>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:for-each select="n:assignedCustodian/n:representedCustodianOrganization/n:name">
                        <ORGANIZATION_NAME>
                            <xsl:value-of select="string(.)"/>
                        </ORGANIZATION_NAME>
                    </xsl:for-each>
                    <xsl:for-each select="n:assignedCustodian/n:representedCustodianOrganization/n:id">
                        <xsl:variable name="var122_id" select="."/>
                        <xsl:if test="$var122_id/@root">
                            <REPRESENTATION_TYPE_CODE>
                                <xsl:value-of select="concat('custodian', substring(string(@root), 0, 0))"/>
                            </REPRESENTATION_TYPE_CODE>
                        </xsl:if>
                    </xsl:for-each>
                </REPRESENTED_ORGANIZATION>
            </xsl:for-each>
            <xsl:for-each select="$var1_instance/n:ClinicalDocument/n:component/n:structuredBody/n:component/n:section/n:entry/n:act/n:entryRelationship/n:observation">
                <xsl:variable name="var2_observation" select="."/>
                <xsl:for-each select="n:templateId">
                    <xsl:variable name="var4_templateId" select="."/>
                    <xsl:if test="$var4_templateId/@root='2.16.840.1.113883.10.20.1.28'">
                        <DIAGNOSIS>
                            <xsl:for-each select="$var2_observation/n:id">
                                <xsl:variable name="var6_id" select="."/>
                                <xsl:if test="$var6_id/@root">
                                    <DIAGNOSIS_ID>
                                        <xsl:value-of select="string(@root)"/>
                                    </DIAGNOSIS_ID>
                                </xsl:if>
                            </xsl:for-each>
                            <xsl:for-each select="$var2_observation/n:value">

                                <DIAGNOSIS_INJURY_CODE>
                                   <xsl:variable name="empty_string"/>
                                   <xsl:variable name="diagcode" select="string(@code)"/>
                                   <xsl:if test="normalize-space($diagcode) != $empty_string">
                                         <xsl:value-of select="$diagcode"/>
                                    </xsl:if>
                                      <xsl:if test="normalize-space($diagcode) = $empty_string">
                                         <xsl:value-of select="'UNKNOWN'"/>
                                    </xsl:if>
                                </DIAGNOSIS_INJURY_CODE>
                                <DIAGNOSIS_CODE_SYSTEM>
                                    <xsl:value-of select="string(@codeSystem)"/>
                                </DIAGNOSIS_CODE_SYSTEM>
                                <DIAGNOSIS_CODE_DESC>
                                    <xsl:value-of select="string(@displayName)"/>
                                </DIAGNOSIS_CODE_DESC>
                            </xsl:for-each>
                            <xsl:for-each select="$var2_observation/n:effectiveTime">
                                <xsl:variable name="var10_effectiveTime" select="."/>
                                <xsl:if test="$var10_effectiveTime/n:low/@value">
                                    <DIAGNOSIS_DATE_TIME>
                                        <xsl:value-of select="string(n:low/@value)"/>
                                    </DIAGNOSIS_DATE_TIME>
                                </xsl:if>
                            </xsl:for-each>
                            <xsl:for-each select="$var2_observation/n:statusCode">
                                <xsl:variable name="var12_statusCode" select="."/>
                                <xsl:if test="$var12_statusCode/@code">
                                    <DIAGNOSIS_STATUS>
                                        <xsl:value-of select="string(@code)"/>
                                    </DIAGNOSIS_STATUS>
                                </xsl:if>
                            </xsl:for-each>
                        </DIAGNOSIS>
                    </xsl:if>
                </xsl:for-each>
            </xsl:for-each>
            <!-- test results 2 blocks -->
            <xsl:for-each select="$var1_instance/n:ClinicalDocument/n:component/n:structuredBody/n:component/n:section/n:entry/n:organizer/n:component/n:observation">
                <xsl:variable name="var2_observation" select="."/>
                <xsl:for-each select="n:templateId">
                    <xsl:variable name="var4_templateId" select="."/>
                    <xsl:if test="$var4_templateId/@root='2.16.840.1.113883.10.20.1.31'">
                        <TEST_RESULT>
                            <xsl:for-each select="$var2_observation/n:id">
                                <xsl:variable name="var6_id" select="."/>
                                <xsl:if test="$var6_id/@root">
                                    <OBSERVATION_ID>
                                        <xsl:value-of select="string(@root)"/>
                                    </OBSERVATION_ID>
                                </xsl:if>
                            </xsl:for-each>
                            <xsl:for-each select="$var2_observation/n:code">

                                <OBSERVATION_CODE>
                                    <xsl:value-of select="string(@code)"/>
                                </OBSERVATION_CODE>
                                <OBSERVATION_CODE_TYPE>
                                    <xsl:value-of select="string(@codeSystem)"/>
                                </OBSERVATION_CODE_TYPE>
                                <OBSERVATION_NAME>
                                    <xsl:value-of select="string(@displayName)"/>
                                </OBSERVATION_NAME>
                            </xsl:for-each>

                            <xsl:for-each select="$var2_observation/n:value">

                                <RESULT>
                                    <xsl:value-of select="string(@value)"/>
                                </RESULT>
                                <RESULT_TYPE>
                                    <xsl:value-of select="string(@xsi:type)"/>
                                </RESULT_TYPE>
                                <RESULT_UNIT>
                                    <xsl:value-of select="string(@unit)"/>
                                </RESULT_UNIT>
                            </xsl:for-each>
                            <xsl:for-each select="$var2_observation/n:effectiveTime">
                                <xsl:variable name="var10_effectiveTime" select="."/>
                                <xsl:if test="$var10_effectiveTime/@value">
                                    <REPORT_DATE_TIME>
                                        <xsl:value-of select="string(@value)"/>
                                    </REPORT_DATE_TIME>
                                </xsl:if>
                            </xsl:for-each>
                            <xsl:for-each select="$var2_observation/n:statusCode">
                                <xsl:variable name="var12_statusCode" select="."/>
                                <xsl:if test="$var12_statusCode/@code">
                                    <TEST_STATUS>
                                        <xsl:value-of select="string(@code)"/>
                                    </TEST_STATUS>
                                </xsl:if>
                            </xsl:for-each>
                        </TEST_RESULT>
                    </xsl:if>
                </xsl:for-each>
            </xsl:for-each>
            <xsl:for-each select="$var1_instance/n:ClinicalDocument/n:component/n:structuredBody/n:component/n:section/n:entry/n:observation">
                <xsl:variable name="var2_observation" select="."/>
                <xsl:for-each select="n:templateId">
                    <xsl:variable name="var4_templateId" select="."/>
                    <xsl:if test="$var4_templateId/@root='2.16.840.1.113883.10.20.1.31'">
                        <TEST_RESULT>
                            <xsl:for-each select="$var2_observation/n:id">
                                <xsl:variable name="var6_id" select="."/>
                                <xsl:if test="$var6_id/@root">
                                    <OBSERVATION_ID>
                                        <xsl:value-of select="string(@root)"/>
                                    </OBSERVATION_ID>
                                </xsl:if>
                            </xsl:for-each>
                            <xsl:for-each select="$var2_observation/n:code">

                                <OBSERVATION_CODE>
                                    <xsl:value-of select="string(@code)"/>
                                </OBSERVATION_CODE>
                                <OBSERVATION_CODE_TYPE>
                                    <xsl:value-of select="string(@codeSystem)"/>
                                </OBSERVATION_CODE_TYPE>
                                <OBSERVATION_NAME>
                                    <xsl:value-of select="string(@displayName)"/>
                                </OBSERVATION_NAME>
                            </xsl:for-each>

                            <xsl:for-each select="$var2_observation/n:value">

                                <RESULT>
                                    <xsl:value-of select="string(@value)"/>
                                </RESULT>
                                <RESULT_TYPE>
                                    <xsl:value-of select="string(@xsi:type)"/>
                                </RESULT_TYPE>
                                <RESULT_UNIT>
                                    <xsl:value-of select="string(@unit)"/>
                                </RESULT_UNIT>
                            </xsl:for-each>
                            <xsl:for-each select="$var2_observation/n:effectiveTime">
                                <xsl:variable name="var10_effectiveTime" select="."/>
                                <xsl:if test="$var10_effectiveTime/@value">
                                    <REPORT_DATE_TIME>
                                        <xsl:value-of select="string(@value)"/>
                                    </REPORT_DATE_TIME>
                                </xsl:if>
                            </xsl:for-each>
                            <xsl:for-each select="$var2_observation/n:statusCode">
                                <xsl:variable name="var12_statusCode" select="."/>
                                <xsl:if test="$var12_statusCode/@code">
                                    <TEST_STATUS>
                                        <xsl:value-of select="string(@code)"/>
                                    </TEST_STATUS>
                                </xsl:if>
                            </xsl:for-each>
                        </TEST_RESULT>
                    </xsl:if>
                </xsl:for-each>
            </xsl:for-each>
            <!-- medication / substance_admin-->
                <xsl:for-each select="$var1_instance/n:ClinicalDocument/n:component/n:structuredBody/n:component/n:section">
                    <xsl:variable name="var_type" select="."/>
                  
                        <xsl:variable name="var_type_templateId" select="$var_type/n:templateId"/>
                        <xsl:if test="$var_type_templateId/@root='2.16.840.1.113883.10.20.1.8'">
                            <xsl:for-each select="$var1_instance/n:ClinicalDocument/n:component/n:structuredBody/n:component/n:section/n:entry/n:substanceAdministration">
                                <xsl:variable name="var42_medication" select="."/>
                                <xsl:for-each select="n:templateId">
                                    <xsl:variable name="var44_templateId" select="."/>
                                    <xsl:if test="$var44_templateId/@root='2.16.840.1.113883.10.20.1.24'">
                                        <SUBSTANCE_ADMIN>
                                            <xsl:for-each select="$var42_medication/n:id">
                                                <xsl:variable name="var46_id" select="."/>
                                                <xsl:if test="$var46_id/@root">
                                                    <SUBSTANCE_ADMIN_ID>
                                                        <xsl:value-of select="string(@root)"/>
                                                    </SUBSTANCE_ADMIN_ID>
                                                </xsl:if>
                                            </xsl:for-each>
                                            <xsl:for-each select="$var42_medication/n:statusCode">
                                                <xsl:variable name="var47_status" select="."/>
                                                <xsl:if test="$var47_status/@code">
                                                    <STATUS_CODE>
                                                        <xsl:value-of select="string(@code)"/>
                                                    </STATUS_CODE>
                                                </xsl:if>
                                            </xsl:for-each>
                                            <xsl:for-each select="$var42_medication/n:effectiveTime">
                                                <xsl:variable name="var48_time" select="."/>
                                                <xsl:if test="$var48_time/@xsi:type='PIVL_TS'">
                                                    <xsl:for-each select="$var48_time/n:period">
                                                        <xsl:variable name="var481_period" select="."/>
                                                        <xsl:if test="$var481_period/@value">
                                                            <DOSE_PERIOD_VALUE>
                                                                <xsl:value-of select="string(@value)"/>
                                                            </DOSE_PERIOD_VALUE>
                                                        </xsl:if>
                                                        <xsl:if test="$var481_period/@unit">
                                                            <DOSE_PERIOD_UNIT>
                                                                <xsl:value-of select="string(@unit)"/>
                                                            </DOSE_PERIOD_UNIT>
                                                        </xsl:if>
                                                    </xsl:for-each>
                                                </xsl:if>
                                                <xsl:if test="$var48_time/@xsi:type='IVL_TS'">
                                                    <xsl:for-each select="$var48_time/n:low">
                                                        <xsl:variable name="var482_low" select="."/>
                                                        <xsl:if test="$var482_low/@value">
                                                            <EFFECTIVE_DATE_TIME>
                                                                <xsl:value-of select="string(@value)"/>
                                                            </EFFECTIVE_DATE_TIME>
                                                        </xsl:if>
                                                    </xsl:for-each>
                                                </xsl:if>
                                            </xsl:for-each>

                                            <xsl:for-each select="$var42_medication/n:doseQuantity">
                                                <xsl:variable name="var49_quantity" select="."/>
                                                <xsl:if test="$var49_quantity/@value">
                                                    <DOSE_QUANTITY>
                                                        <xsl:value-of select="string(@value)"/>
                                                    </DOSE_QUANTITY>
                                                </xsl:if>
                                            </xsl:for-each>
                                            <xsl:for-each select="$var42_medication/n:doseUnit">
                                                <xsl:variable name="var410_unit" select="."/>
                                                <xsl:if test="$var410_unit/@value">
                                                    <DOSE_UNIT>
                                                        <xsl:value-of select="string(@value)"/>
                                                    </DOSE_UNIT>
                                                </xsl:if>
                                            </xsl:for-each>

                                            <xsl:for-each select="$var42_medication/n:routeCode">

                                                <ROUTE_CODE>
                                                    <xsl:value-of select="string(@code)"/>
                                                </ROUTE_CODE>
                                                <ROUTE_CODE_SYSTEM>
                                                    <xsl:value-of select="string(@codeSystem)"/>
                                                </ROUTE_CODE_SYSTEM>
                                                <ROUTE_CODE_SYSTEM_NAME>
                                                    <xsl:value-of select="string(@codeSystemName)"/>
                                                </ROUTE_CODE_SYSTEM_NAME>
                                                <ROUTE_CODE_DISPLAY_NAME>
                                                    <xsl:value-of select="string(@displayName)"/>
                                                </ROUTE_CODE_DISPLAY_NAME>
                                            </xsl:for-each>

                                            <xsl:for-each select="$var42_medication/n:consumable/n:manufacturedProduct/n:manufacturedMaterial/n:code">

                                                <MATERIAL_CODE>
                                                    <xsl:value-of select="string(@code)"/>
                                                </MATERIAL_CODE>
                                                <MATERIAL_CODE_SYSTEM>
                                                    <xsl:value-of select="string(@codeSystem)"/>
                                                </MATERIAL_CODE_SYSTEM>
                                                <MATERIAL_CODE_DISPLAY_NAME>
                                                    <xsl:value-of select="string(@displayName)"/>
                                                </MATERIAL_CODE_DISPLAY_NAME>
                                            </xsl:for-each>
                                        </SUBSTANCE_ADMIN>
                                    </xsl:if>
                                </xsl:for-each>
                            </xsl:for-each>
                        </xsl:if>
               
                </xsl:for-each>
            <!--immunization-->
                   <xsl:for-each select="$var1_instance/n:ClinicalDocument/n:component/n:structuredBody/n:component/n:section">
                       <xsl:variable name="var_type" select="."/>

                        <xsl:variable name="var_type_templateId" select="$var_type/n:templateId"/>
                        <xsl:if test="$var_type_templateId/@root='2.16.840.1.113883.10.20.1.6'">
                            <xsl:for-each select="$var1_instance/n:ClinicalDocument/n:component/n:structuredBody/n:component/n:section/n:entry/n:substanceAdministration">
                                <xsl:variable name="var42_medication" select="."/>
                                <xsl:for-each select="n:templateId">
                                    <xsl:variable name="var44_templateId" select="."/>
                                    <xsl:if test="$var44_templateId/@root='2.16.840.1.113883.10.20.1.24'">
                                        <IMMUNIZATION>
                                            <xsl:for-each select="$var42_medication/n:id">
                                                <xsl:variable name="var46_id" select="."/>
                                                <xsl:if test="$var46_id/@root">
                                                    <IMMUNIZATION_ID>
                                                        <xsl:value-of select="string(@root)"/>
                                                    </IMMUNIZATION_ID>
                                                </xsl:if>
                                            </xsl:for-each>
                                            <xsl:for-each select="$var42_medication/n:statusCode">
                                                <xsl:variable name="var47_status" select="."/>
                                                <xsl:if test="$var47_status/@code">
                                                    <STATUS_CODE>
                                                        <xsl:value-of select="string(@code)"/>
                                                    </STATUS_CODE>
                                                </xsl:if>
                                            </xsl:for-each>
                                            <xsl:for-each select="$var42_medication/n:effectiveTime">
                                                <xsl:variable name="var48_time" select="."/>
                                                <xsl:if test="$var48_time/@xsi:type='PIVL_TS'">
                                                    <xsl:for-each select="$var48_time/n:period">
                                                        <xsl:variable name="var481_period" select="."/>
                                                        <xsl:if test="$var481_period/@value">
                                                            <DOSE_PERIOD_VALUE>
                                                                <xsl:value-of select="string(@value)"/>
                                                            </DOSE_PERIOD_VALUE>
                                                        </xsl:if>
                                                        <xsl:if test="$var481_period/@unit">
                                                            <DOSE_PERIOD_UNIT>
                                                                <xsl:value-of select="string(@unit)"/>
                                                            </DOSE_PERIOD_UNIT>
                                                        </xsl:if>
                                                    </xsl:for-each>
                                                </xsl:if>
                                                <xsl:if test="$var48_time/@xsi:type='IVL_TS'">
                                                    <xsl:for-each select="$var48_time/n:low">
                                                        <xsl:variable name="var482_low" select="."/>
                                                        <xsl:if test="$var482_low/@value">
                                                            <EFFECTIVE_DATE_TIME>
                                                                <xsl:value-of select="string(@value)"/>
                                                            </EFFECTIVE_DATE_TIME>
                                                        </xsl:if>
                                                    </xsl:for-each>
                                                </xsl:if>
                                            </xsl:for-each>

                                            <xsl:for-each select="$var42_medication/n:doseQuantity">
                                                <xsl:variable name="var49_quantity" select="."/>
                                                <xsl:if test="$var49_quantity/@value">
                                                    <DOSE_QUANTITY>
                                                        <xsl:value-of select="string(@value)"/>
                                                    </DOSE_QUANTITY>
                                                </xsl:if>
                                            </xsl:for-each>
                                            <xsl:for-each select="$var42_medication/n:doseUnit">
                                                <xsl:variable name="var410_unit" select="."/>
                                                <xsl:if test="$var410_unit/@value">
                                                    <DOSE_UNIT>
                                                        <xsl:value-of select="string(@value)"/>
                                                    </DOSE_UNIT>
                                                </xsl:if>
                                            </xsl:for-each>

                                            <xsl:for-each select="$var42_medication/n:routeCode">

                                                <ROUTE_CODE>
                                                    <xsl:value-of select="string(@code)"/>
                                                </ROUTE_CODE>
                                                <ROUTE_CODE_SYSTEM>
                                                    <xsl:value-of select="string(@codeSystem)"/>
                                                </ROUTE_CODE_SYSTEM>
                                                <ROUTE_CODE_SYSTEM_NAME>
                                                    <xsl:value-of select="string(@codeSystemName)"/>
                                                </ROUTE_CODE_SYSTEM_NAME>
                                                <ROUTE_CODE_DISPLAY_NAME>
                                                    <xsl:value-of select="string(@displayName)"/>
                                                </ROUTE_CODE_DISPLAY_NAME>
                                            </xsl:for-each>

                                            <xsl:for-each select="$var42_medication/n:consumable/n:manufacturedProduct/n:manufacturedMaterial/n:code">

                                                <MATERIAL_CODE>
                                                    <xsl:value-of select="string(@code)"/>
                                                </MATERIAL_CODE>
                                                <MATERIAL_CODE_SYSTEM>
                                                    <xsl:value-of select="string(@codeSystem)"/>
                                                </MATERIAL_CODE_SYSTEM>
                                                <MATERIAL_CODE_DISPLAY_NAME>
                                                    <xsl:value-of select="string(@displayName)"/>
                                                </MATERIAL_CODE_DISPLAY_NAME>
                                            </xsl:for-each>
                                        </IMMUNIZATION>
                                    </xsl:if>
                                </xsl:for-each>
                            </xsl:for-each>
                        </xsl:if>
<!--procedure -->
                </xsl:for-each>
                   <xsl:for-each select="$var1_instance/n:ClinicalDocument/n:component/n:structuredBody/n:component/n:section">
                       <xsl:variable name="var_type" select="."/>

                        <xsl:variable name="var_type_templateId" select="$var_type/n:templateId"/>
                        <xsl:if test="$var_type_templateId/@root='2.16.840.1.113883.10.20.1.12'">
                            <xsl:for-each select="$var1_instance/n:ClinicalDocument/n:component/n:structuredBody/n:component/n:section/n:entry/n:procedure">
                                <xsl:variable name="var_procedure" select="."/>
                                <xsl:for-each select="n:templateId">
                                    <xsl:variable name="var_templateId" select="."/>
                                    <xsl:if test="$var_templateId/@root='2.16.840.1.113883.10.20.1.29'">
                                        <PROCEDURES>
                                            <xsl:for-each select="$var_procedure/n:id">
                                                <xsl:variable name="var_id" select="."/>
                                                <xsl:if test="$var_id/@root">
                                                    <PROCEDURE_ID>
                                                        <xsl:value-of select="string(@root)"/>
                                                    </PROCEDURE_ID>
                                                </xsl:if>
                                            </xsl:for-each>
                                            <xsl:for-each select="$var_procedure/n:statusCode">
                                                <xsl:variable name="var_status" select="."/>
                                                <xsl:if test="$var_status/@code">
                                                    <STATUS_CODE>
                                                        <xsl:value-of select="string(@code)"/>
                                                    </STATUS_CODE>
                                                </xsl:if>
                                            </xsl:for-each>
                                            <xsl:for-each select="$var_procedure/n:effectiveTime">
                                                <xsl:variable name="var_time" select="."/>
                                                          <xsl:if test="$var_time/@value">
                                                            <PROCEDURE_DATE_TIME>
                                                                <xsl:value-of select="string(@value)"/>
                                                            </PROCEDURE_DATE_TIME>
                                                        </xsl:if>                        
                                            </xsl:for-each>
                                            <xsl:for-each select="$var_procedure/n:code">
                                                <PROCEDURE_CODE>
                                                    <xsl:value-of select="string(@code)"/>
                                                </PROCEDURE_CODE>
                                                <PROCEDURE_CODE_SYSTEM>
                                                    <xsl:value-of select="string(@codeSystem)"/>
                                                </PROCEDURE_CODE_SYSTEM>
                                                <PROCEDURE_CODE_DISPLAY_NAME>
                                                    <xsl:value-of select="string(@displayName)"/>
                                                </PROCEDURE_CODE_DISPLAY_NAME>
                                            </xsl:for-each>
                                        </PROCEDURES>
                                    </xsl:if>
                                </xsl:for-each>
                            </xsl:for-each>
                        </xsl:if>

                </xsl:for-each>
        </CCDDB>
    </xsl:template>
</xsl:stylesheet>
