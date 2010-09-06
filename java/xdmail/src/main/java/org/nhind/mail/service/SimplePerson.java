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

package org.nhind.mail.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Vincent Lewis
 */
public class SimplePerson implements Serializable {

    private String firstName;
    private String lastName;
    private String middleName;
    private String streetAddress1;
    private String streetAddress2;
    private String telephone;
    private String birthDateTime;
    private String languageCode;
    private String ethnicityCode;
    private String age;
    private String ageUnits;
    private String genderCode;
    private String zipCode;
    private String state;
    private String county;
    private String city;
    private String country;
    private String pcpOid;
    private String pcpName;
    private String ethnicityCodeSystem;
    private String ethnicityCodeName;
    private String genderCodeSystem;
    private String patientEuid;
    private String localId;
    private String localOrg;
    private String ssn;
    private Map otherOrgIds = new HashMap();;

    // portal related attribs
    private String npi;
    private String email;
    private String department;
    private String suffix;
    private String title;
    private String systemCode;
    private String systemName;
    private String phoneExt;

    private static final long serialVersionUID = -4738964463278522940L;

    public SimplePerson() {
    }

    public Map getOtherOrgIds() {
        return otherOrgIds;
    }

    public String getSSN() {
        return ssn;
    }

    public void setSSN(String ssn) {
        this.ssn = ssn;
    }

    public String getLocalId() {
        return localId;
    }

    public String getLocalOrg() {
        return localOrg;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public void setLocalOrg(String localOrg) {
        this.localOrg = localOrg;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getStreetAddress1() {
        return streetAddress1;
    }

    public void setStreetAddress1(String streetAddress1) {
        this.streetAddress1 = streetAddress1;
    }

    public String getStreetAddress2() {
        return streetAddress2;
    }

    public void setStreetAddress2(String streetAddress2) {
        this.streetAddress2 = streetAddress2;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getBirthDateTime() {
        return birthDateTime;
    }

    public void setBirthDateTime(String birthDateTime) {
        this.birthDateTime = birthDateTime;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getEthnicityCode() {
        return ethnicityCode;
    }

    public void setEthnicityCode(String ethnicityCode) {
        this.ethnicityCode = ethnicityCode;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAgeUnits() {
        return ageUnits;
    }

    public void setAgeUnits(String ageUnits) {
        this.ageUnits = ageUnits;
    }

    public String getGenderCode() {
        return genderCode;
    }

    public void setGenderCode(String genderCode) {
        this.genderCode = genderCode;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPcpOid() {
        return pcpOid;
    }

    public void setPcpOid(String pcpOid) {
        this.pcpOid = pcpOid;
    }

    public String getPcpName() {
        return pcpName;
    }

    public void setPcpName(String pcpName) {
        this.pcpName = pcpName;
    }

    public String getEthnicityCodeSystem() {
        return ethnicityCodeSystem;
    }

    public void setEthnicityCodeSystem(String ethnicityCodeSystem) {
        this.ethnicityCodeSystem = ethnicityCodeSystem;
    }

    public String getEthnicityCodeName() {
        return ethnicityCodeName;
    }

    public void setEthnicityCodeName(String ethnicityCodeName) {
        this.ethnicityCodeName = ethnicityCodeName;
    }

    public String getGenderCodeSystem() {
        return genderCodeSystem;
    }

    public void setGenderCodeSystem(String genderCodeSystem) {
        this.genderCodeSystem = genderCodeSystem;
    }

    public String getPatientEuid() {
        return patientEuid;
    }

    public void setPatientEuid(String patientEuid) {
        this.patientEuid = patientEuid;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNpi() {
        return npi;
    }

    public void setNpi(String npi) {
        this.npi = npi;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getPhoneExt() {
        return phoneExt;
    }

    public void setPhoneExt(String phoneExt) {
        this.phoneExt = phoneExt;
    }

}
