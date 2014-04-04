/* 
Copyright (c) 2013, NHIN Direct Project
All rights reserved.

Authors:
   Amulya Misra        Drajer LLC/G3Soft
   Satyajeet Mahapatra Drajer LLC/G3Soft
 
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
package org.nhindirect.trustbundle.test.core;

import static org.junit.Assert.*;

import java.io.File;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nhindirect.trustbundle.core.CreateSignedPKCS7;

public class TestCreateSignedPKCS7 extends TestCase{

	private String anchorDir;
	private String metaDataFile;
	private String certificateDir;
	private String passkey;
	private String destDir;
	private String bundleName;
	String actual;

	File actualfile,expectedfile;

	File createFile,metaFile,p12certiFile;
	Boolean metaExists;

	String projectPath;


	@Before
	public void setUp() throws Exception {
		projectPath = new File(".").getAbsoluteFile().toString();
		projectPath = projectPath.substring(0, projectPath.length()-1);


		anchorDir = projectPath+"src\\test\\resources\\testdatainput";
		metaDataFile = projectPath+"src\\test\\resources\\testdatainput\\TrustBundleMetadata.xml";
		certificateDir=projectPath+"src\\test\\resources\\testdatainput\\server.p12";
		passkey="satya23";

		destDir=projectPath+"src\\test\\resources\\testdataoutput";
		bundleName="signednometadata.p7m";

		metaFile = new File(projectPath+"src\\test\\resources\\testdatainput\\TrustBundleMetadata.xml");
		p12certiFile = new File(projectPath+"src\\test\\resources\\testdatainput\\server.p12");
		metaExists=true;

	}

	@After
	public void tearDown() throws Exception {
				projectPath = null;
				anchorDir = null;
				metaDataFile = null;
				certificateDir=null;
				destDir=null;
				metaFile = null;
				p12certiFile = null;

	}

	@Test
	public void testGetParameters() {

		CreateSignedPKCS7 cspk=new CreateSignedPKCS7();
		//CASE 1 : WRONG XML FILE
		actual=cspk.getParameters(anchorDir, "test", certificateDir, passkey , destDir, bundleName);
		assertEquals("Error: Kindly Provide A XML Meta data file", actual);

		//CASE 2 : NOT SELECTING TRUST ANCHOR DIRECTORY
		actual=cspk.getParameters("Select Trust Anchor Directory", metaDataFile, certificateDir, passkey, destDir, bundleName);
		assertEquals("Error: Kindly Provide A Trust Anchor Directory", actual);

		//CASE 3 : CANCEL WHILE SELECT TRUST ANCHOR DIRECTORY
		actual=cspk.getParameters("You pressed cancel", metaDataFile, certificateDir, passkey, destDir, bundleName);
		assertEquals("Error: Kindly Provide A Trust Anchor Directory", actual);

		//CASE 4 : TRUST BUNDLE DESTINATION DIRECTORY NOT PASSED
		actual=cspk.getParameters(anchorDir, metaDataFile, certificateDir, passkey, "Select Trust Bundle Destination Directory", bundleName);
		assertEquals("Error: Kindly Provide A Trust Bundle Destination Directory", actual);

		//CASE 5 : CANCEL WHILE TRUST BUNDLE DESTINATION DIRECTORY NOT PASSED
		actual=cspk.getParameters(anchorDir, metaDataFile, certificateDir, passkey, "You pressed cancel", bundleName);
		assertEquals("Error: Kindly Provide A Trust Bundle Destination Directory", actual);

		//CASE 6 : PASSING WRONG PASSWORD
		actual=cspk.getParameters(anchorDir, metaDataFile, certificateDir, "wrongpass", destDir, bundleName);
		assertEquals("Error: Creation ofsignednometadata.p7mfile failed!", actual);

		//CASE 7 : BUNDLE NAME WRONG FORMAT
		actual=cspk.getParameters(anchorDir, metaDataFile, certificateDir, passkey, destDir, "signedBundle.ppp");
		assertEquals("Error: Kindly Provide A Proper Trust Bundle Name with extension .p7m", actual);

		//CASE 8 : ALL PARAMETERS ARE CORRECT
		actual=cspk.getParameters(anchorDir, metaDataFile, certificateDir, passkey, destDir, bundleName);
		assertEquals("Message: signednometadata.p7m created successfully!", actual);




		//fail("Not yet implemented"); // TODO
	}

	@Test
	public void testCreate() {
		CreateSignedPKCS7 cspk=new CreateSignedPKCS7();

		//CASE 1: ALL PARAMETER CORRECT
		createFile = new File(projectPath+"src\\test\\resources\\testdataoutput\\signedmetadata1.p7m");
		expectedfile = new File(projectPath+"src\\test\\resources\\testdataoutput\\signedmetadata1.p7m");
		actualfile=cspk.create(anchorDir, createFile, metaFile, metaExists, p12certiFile, passkey);
		assertEquals(expectedfile, actualfile);

		//CASE 2: ANCHOR DIRECTORY WRONG
		createFile = new File(projectPath+"src\\test\\resources\\testdataoutput\\signedmetadata2.p7m");
		actualfile=cspk.create("src\\test\\resources\\wrong directory", createFile, metaFile, metaExists, p12certiFile, passkey);
		assertEquals(null, actualfile);

		//CASE 3: WRONG META FILE WITH METAFILEEXISTS TRUE
		actualfile=cspk.create(anchorDir, createFile, new File(projectPath+"src\\test\\resources\\testdatainput\\wrong meta file.xml"), metaExists, p12certiFile, passkey);
		assertEquals(null,actualfile);

		//CASE 4: CORRECT META FILE WITH METAFILEEXISTS FALSE
		createFile = new File(projectPath+"src\\test\\resources\\testdataoutput\\signednometadata1.p7m");
		expectedfile = new File(projectPath+"src\\test\\resources\\testdataoutput\\signednometadata1.p7m");
		actualfile=cspk.create(anchorDir, createFile, metaFile, false, p12certiFile, passkey);
		assertEquals(expectedfile,actualfile);

		//CASE 5: WRONG META FILE WITH METAFILEEXISTS FALSE
		createFile = new File(projectPath+"src\\test\\resources\\testdataoutput\\signednometadata2.p7m");
		expectedfile = new File(projectPath+"src\\test\\resources\\testdataoutput\\signednometadata2.p7m");
		actualfile=cspk.create(anchorDir, createFile, new File(projectPath+"src\\test\\resources\\testdatainput\\wrong meta file.xml"), false, p12certiFile, passkey);
		assertEquals(expectedfile,actualfile);

		//CASE 6: WRONG p12certiFile
		actualfile=cspk.create(anchorDir, createFile, metaFile, true, new File(projectPath+"src\\test\\resources\\testdatainput\\wrongfile.p12"), passkey);
		assertEquals(null,actualfile);

		//CASE 7: PASS KEY NULL
		createFile = new File(projectPath+"src\\test\\resources\\testdataoutput\\signedmetadata2nopass.p7m");
		expectedfile = new File(projectPath+"src\\test\\resources\\testdataoutput\\signedmetadata2nopass.p7m");
		actualfile=cspk.create(anchorDir, createFile, metaFile, true, p12certiFile, null);
		assertEquals(expectedfile,actualfile);


	}

}
