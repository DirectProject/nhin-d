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

package org.nhindirect.xd.transform.pojo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing and containing attributes for a person.
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

    private Map<String, String> otherOrgIds = new HashMap<String, String>();

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

    /**
     * Default constructor.
     */
    public SimplePerson()
    {
        
    }

    public SimplePerson(String firstName, String lastName)
    {
        super();
        
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Get the value of otherOrgIds.
     * 
     * @return the value of otherOrgIds.
     */
    public Map<String, String> getOtherOrgIds() {
        return otherOrgIds;
    }

    /**
     * Set the value of otherOrgIds.
     * 
     * @param otherOrgIds
     *            The value of otherOrgIds.
     */
    public void setOtherOrgIds(Map<String, String> otherOrgIds) {
        this.otherOrgIds = otherOrgIds;
    }

    /**
     * Get the value of ssn.
     * 
     * @return the value of ssn.
     */
    public String getSSN() {
        return ssn;
    }

    /**
     * Set the value of ssn.
     * 
     * @param ssn
     *            The value of ssn.
     */
    public void setSSN(String ssn) {
        this.ssn = ssn;
    }

    /**
     * Get the value of localId.
     * 
     * @return the value of localId.
     */
    public String getLocalId() {
        return localId;
    }

    /**
     * Get the value of localOrg.
     * 
     * @return the value of localOrg.
     */
    public String getLocalOrg() {
        return localOrg;
    }

    /**
     * Set the value of localId.
     * 
     * @param localId
     *            The value of localId.
     */
    public void setLocalId(String localId) {
        this.localId = localId;
    }

    /**
     * Set the value of localOrg.
     * 
     * @param localOrg
     *            The value of localOrg.
     */
    public void setLocalOrg(String localOrg) {
        this.localOrg = localOrg;
    }

    /**
     * Get the value of firstName.
     * 
     * @return the value of firstName.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set the value of firstName.
     * 
     * @param firstName
     *            The value of firstName.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get the value of lastName.
     * 
     * @return the value of lastName.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set the value of lastName.
     * 
     * @param lastName
     *            The value of lastName.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Get the value of middleName.
     * 
     * @return the value of middleName.
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Set the value of middleName.
     * 
     * @param middleName
     *            The value of middleName.
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * Get the value of streetAddress1.
     * 
     * @return the value of streetAddress1.
     */
    public String getStreetAddress1() {
        return streetAddress1;
    }

    /**
     * Set the value of streetAddress1.
     * 
     * @param streetAddress1
     *            The value of streetAddress1.
     */
    public void setStreetAddress1(String streetAddress1) {
        this.streetAddress1 = streetAddress1;
    }

    /**
     * Get the value of streetAddress2.
     * 
     * @return the value of streetAddress2.
     */
    public String getStreetAddress2() {
        return streetAddress2;
    }

    /**
     * Set the value of streetAddress2.
     * 
     * @param streetAddress2
     *            The value of streetAddress2.
     */
    public void setStreetAddress2(String streetAddress2) {
        this.streetAddress2 = streetAddress2;
    }

    /**
     * Get the value of telephone.
     * 
     * @return the value of telephone.
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * Set the value of telephone.
     * 
     * @param telephone
     *            The value of telephone.
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * Get the value of birthDateTime.
     * 
     * @return the value of birthDateTime.
     */
    public String getBirthDateTime() {
        return birthDateTime;
    }

    /**
     * Set the value of birthDateTime.
     * 
     * @param birthDateTime
     *            The value of birthDateTime.
     */
    public void setBirthDateTime(String birthDateTime) {
        this.birthDateTime = birthDateTime;
    }

    /**
     * Get the value of languageCode.
     * 
     * @return the value of languageCode.
     */
    public String getLanguageCode() {
        return languageCode;
    }

    /**
     * Set the value of languageCode.
     * 
     * @param languageCode
     *            The value of languageCode.
     */
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    /**
     * Get the value of ethnicityCode.
     * 
     * @return the value of ethnicityCode.
     */
    public String getEthnicityCode() {
        return ethnicityCode;
    }

    /**
     * Set the value of ethnicityCode.
     * 
     * @param ethnicityCode
     *            The value of ethnicityCode.
     */
    public void setEthnicityCode(String ethnicityCode) {
        this.ethnicityCode = ethnicityCode;
    }

    /**
     * Get the value of age.
     * 
     * @return the value of age.
     */
    public String getAge() {
        return age;
    }

    /**
     * Set the value of age.
     * 
     * @param age
     *            The value of age.
     */
    public void setAge(String age) {
        this.age = age;
    }

    /**
     * Get the value of ageUnits.
     * 
     * @return the value of ageUnits.
     */
    public String getAgeUnits() {
        return ageUnits;
    }

    /**
     * Set the value of ageUnits.
     * 
     * @param ageUnits
     *            The value of ageUnits.
     */
    public void setAgeUnits(String ageUnits) {
        this.ageUnits = ageUnits;
    }

    /**
     * Get the value of genderCode.
     * 
     * @return the value of genderCode.
     */
    public String getGenderCode() {
        return genderCode;
    }

    /**
     * Set the value of genderCode.
     * 
     * @param genderCode
     *            The value of genderCode.
     */
    public void setGenderCode(String genderCode) {
        this.genderCode = genderCode;
    }

    /**
     * Get the value of zipCode.
     * 
     * @return the value of zipCode.
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Set the value of zipCode.
     * 
     * @param zipCode
     *            The value of zipCode.
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Get the value of state.
     * 
     * @return the value of state.
     */
    public String getState() {
        return state;
    }

    /**
     * Set the value of state.
     * 
     * @param state
     *            The value of state.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Get the value of county.
     * 
     * @return the value of county.
     */
    public String getCounty() {
        return county;
    }

    /**
     * Set the value of county.
     * 
     * @param county
     *            The value of county.
     */
    public void setCounty(String county) {
        this.county = county;
    }

    /**
     * Get the value of city.
     * 
     * @return the value of city.
     */
    public String getCity() {
        return city;
    }

    /**
     * Set the value of city.
     * 
     * @param city
     *            The value of city.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Get the value of country.
     * 
     * @return the value of country.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Set the value of country.
     * 
     * @param country
     *            The value of country.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Get the value of pcpOid.
     * 
     * @return the value of pcpOid.
     */
    public String getPcpOid() {
        return pcpOid;
    }

    /**
     * Set the value of pcpOid.
     * 
     * @param pcpOid
     *            The value of pcpOid.
     */
    public void setPcpOid(String pcpOid) {
        this.pcpOid = pcpOid;
    }

    /**
     * Get the value of pcpName.
     * 
     * @return the value of pcpName.
     */
    public String getPcpName() {
        return pcpName;
    }

    /**
     * Set the value of pcpName.
     * 
     * @param pcpName
     *            The value of pcpName.
     */
    public void setPcpName(String pcpName) {
        this.pcpName = pcpName;
    }

    /**
     * Get the value of ethnicityCodeSystem.
     * 
     * @return the value of ethnicityCodeSystem.
     */
    public String getEthnicityCodeSystem() {
        return ethnicityCodeSystem;
    }

    /**
     * Set the value of ethnicityCodeSystem.
     * 
     * @param ethnicityCodeSystem
     *            The value of ethnicityCodeSystem.
     */
    public void setEthnicityCodeSystem(String ethnicityCodeSystem) {
        this.ethnicityCodeSystem = ethnicityCodeSystem;
    }

    /**
     * Get the value of ethnicityCodeName.
     * 
     * @return the value of ethnicityCodeName.
     */
    public String getEthnicityCodeName() {
        return ethnicityCodeName;
    }

    /**
     * Set the value of ethnicityCodeName.
     * 
     * @param ethnicityCodeName
     *            The value of ethnicityCodeName.
     */
    public void setEthnicityCodeName(String ethnicityCodeName) {
        this.ethnicityCodeName = ethnicityCodeName;
    }

    /**
     * Get the value of genderCodeSystem.
     * 
     * @return the value of genderCodeSystem.
     */
    public String getGenderCodeSystem() {
        return genderCodeSystem;
    }

    /**
     * Set the value of genderCodeSystem.
     * 
     * @param genderCodeSystem
     *            The value of GenderCodeSystem.
     */
    public void setGenderCodeSystem(String genderCodeSystem) {
        this.genderCodeSystem = genderCodeSystem;
    }

    /**
     * Get the value of patientEuid.
     * 
     * @return the value of patientEuid.
     */
    public String getPatientEuid() {
        return patientEuid;
    }

    /**
     * Set the value of patientEuid.
     * 
     * @param patientEuid
     *            The value of patientEuid.
     */
    public void setPatientEuid(String patientEuid) {
        this.patientEuid = patientEuid;
    }

    /**
     * Get the value of department.
     * 
     * @return the value of department.
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Set the value of department.
     * 
     * @param department
     *            The value of department.
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * Get the value of email.
     * 
     * @return the value of email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the value of email.
     * 
     * @param email
     *            The value of email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the value of npi.
     * 
     * @return the value of npi.
     */
    public String getNpi() {
        return npi;
    }

    /**
     * Set the value of npi.
     * 
     * @param npi
     *            The value of npi.
     */
    public void setNpi(String npi) {
        this.npi = npi;
    }

    /**
     * Get the value of ssn.
     * 
     * @return the value of ssn.
     */
    public String getSsn() {
        return ssn;
    }

    /**
     * Set the value of ssn.
     * 
     * @param ssn
     *            The value of ssn.
     */
    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    /**
     * Get the value of suffix.
     * 
     * @return the value of suffix.
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * Set the value of suffix.
     * 
     * @param suffix
     *            The value of suffix.
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * Get the value of title.
     * 
     * @return the value of title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the value of title.
     * 
     * @param title
     *            The value of title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the value of systemCode.
     * 
     * @return the value of systemCode.
     */
    public String getSystemCode() {
        return systemCode;
    }

    /**
     * Set the value of sistemCode.
     * 
     * @param systemCode
     *            The value of systemCode.
     */
    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    /**
     * Get the value of systemName.
     * 
     * @return the value of systemName.
     */
    public String getSystemName() {
        return systemName;
    }

    /**
     * Set the value of systemName.
     * 
     * @param systemName
     *            The value of systemName.
     */
    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    /**
     * Get the value of phoneExt.
     * 
     * @return the value of phoneExt.
     */
    public String getPhoneExt() {
        return phoneExt;
    }

    /**
     * Set the value of phoneExt.
     * 
     * @param phoneExt
     *            The value of phoneExt.
     */
    public void setPhoneExt(String phoneExt) {
        this.phoneExt = phoneExt;
    }

}
