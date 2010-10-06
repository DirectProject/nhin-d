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

import java.util.HashMap;
import java.util.Map;

import org.nhindirect.xd.transform.pojo.SimplePerson;

import junit.framework.TestCase;

/**
 * Test class for the methods in the SimplePerson class.
 * 
 * @author beau
 */
public class SimplePersonTest extends TestCase
{

    /**
     * Constructor
     * 
     * @param testName
     *            The test name
     */
    public SimplePersonTest(String testName)
    {
        super(testName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Test methods in the SimplePerson class.
     */
    @SuppressWarnings("unchecked")
    public void testSimplePerson()
    {
        SimplePerson person = new SimplePerson();

        String firstName = "A";
        String lastName = "B";
        String middleName = "C";
        String streetAddress1 = "D";
        String streetAddress2 = "E";
        String telephone = "F";
        String birthDateTime = "G";
        String languageCode = "H";
        String ethnicityCode = "I";
        String age = "J";
        String ageUnits = "K";
        String genderCode = "L";
        String zipCode = "M";
        String state = "N";
        String county = "O";
        String city = "P";
        String country = "Q";
        String pcpOid = "R";
        String pcpName = "S";
        String ethnicityCodeSystem = "T";
        String ethnicityCodeName = "U";
        String genderCodeSystem = "V";
        String patientEuid = "W";
        String localId = "X";
        String localOrg = "Y";
        String ssn = "Z";
        String npi = "AA";
        String email = "BB";
        String department = "CC";
        String suffix = "DD";
        String title = "EE";
        String systemCode = "FF";
        String systemName = "GG";
        String phoneExt = "HH";

        Map otherOrgIds = new HashMap();
        otherOrgIds.put("A", "B");

        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setMiddleName(middleName);
        person.setStreetAddress1(streetAddress1);
        person.setStreetAddress2(streetAddress2);
        person.setTelephone(telephone);
        person.setBirthDateTime(birthDateTime);
        person.setLanguageCode(languageCode);
        person.setEthnicityCode(ethnicityCode);
        person.setAge(age);
        person.setAgeUnits(ageUnits);
        person.setGenderCode(genderCode);
        person.setZipCode(zipCode);
        person.setState(state);
        person.setCounty(county);
        person.setCity(city);
        person.setCountry(country);
        person.setPcpOid(pcpOid);
        person.setPcpName(pcpName);
        person.setEthnicityCodeSystem(ethnicityCodeSystem);
        person.setEthnicityCodeName(ethnicityCodeName);
        person.setGenderCodeSystem(genderCodeSystem);
        person.setPatientEuid(patientEuid);
        person.setLocalId(localId);
        person.setLocalOrg(localOrg);
        person.setSsn(ssn);
        person.setNpi(npi);
        person.setEmail(email);
        person.setDepartment(department);
        person.setSuffix(suffix);
        person.setTitle(title);
        person.setSystemCode(systemCode);
        person.setSystemName(systemName);
        person.setPhoneExt(phoneExt);
        person.setOtherOrgIds(otherOrgIds);

        assertEquals("Actual value does not match expected", firstName, person.getFirstName());
        assertEquals("Actual value does not match expected", lastName, person.getLastName());
        assertEquals("Actual value does not match expected", middleName, person.getMiddleName());
        assertEquals("Actual value does not match expected", streetAddress1, person.getStreetAddress1());
        assertEquals("Actual value does not match expected", streetAddress2, person.getStreetAddress2());
        assertEquals("Actual value does not match expected", telephone, person.getTelephone());
        assertEquals("Actual value does not match expected", birthDateTime, person.getBirthDateTime());
        assertEquals("Actual value does not match expected", languageCode, person.getLanguageCode());
        assertEquals("Actual value does not match expected", ethnicityCode, person.getEthnicityCode());
        assertEquals("Actual value does not match expected", age, person.getAge());
        assertEquals("Actual value does not match expected", ageUnits, person.getAgeUnits());
        assertEquals("Actual value does not match expected", genderCode, person.getGenderCode());
        assertEquals("Actual value does not match expected", zipCode, person.getZipCode());
        assertEquals("Actual value does not match expected", state, person.getState());
        assertEquals("Actual value does not match expected", county, person.getCounty());
        assertEquals("Actual value does not match expected", city, person.getCity());
        assertEquals("Actual value does not match expected", country, person.getCountry());
        assertEquals("Actual value does not match expected", pcpOid, person.getPcpOid());
        assertEquals("Actual value does not match expected", pcpName, person.getPcpName());
        assertEquals("Actual value does not match expected", ethnicityCodeSystem, person.getEthnicityCodeSystem());
        assertEquals("Actual value does not match expected", ethnicityCodeName, person.getEthnicityCodeName());
        assertEquals("Actual value does not match expected", genderCodeSystem, person.getGenderCodeSystem());
        assertEquals("Actual value does not match expected", patientEuid, person.getPatientEuid());
        assertEquals("Actual value does not match expected", localId, person.getLocalId());
        assertEquals("Actual value does not match expected", localOrg, person.getLocalOrg());
        assertEquals("Actual value does not match expected", ssn, person.getSsn());
        assertEquals("Actual value does not match expected", npi, person.getNpi());
        assertEquals("Actual value does not match expected", email, person.getEmail());
        assertEquals("Actual value does not match expected", department, person.getDepartment());
        assertEquals("Actual value does not match expected", suffix, person.getSuffix());
        assertEquals("Actual value does not match expected", title, person.getTitle());
        assertEquals("Actual value does not match expected", systemCode, person.getSystemCode());
        assertEquals("Actual value does not match expected", systemName, person.getSystemName());
        assertEquals("Actual value does not match expected", phoneExt, person.getPhoneExt());
        assertEquals("Actual value does not match expected", otherOrgIds, person.getOtherOrgIds());

        person.setSsn(null);
        person.setSSN(ssn);

        assertEquals("Actual value does not match expected", ssn, person.getSSN());
    }
}
