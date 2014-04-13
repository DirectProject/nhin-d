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
import org.nhindirect.trustbundle.core.CreateUnSignedPKCS7;


public class TestCreateUnSignedPKCS7 extends TestCase{

	private static String anchorDir = "";
	private static String metaDataFile = "" ;
	private static String destDir = "";
	private static String bundleName1 = "",bundleName2,bundleName3,bundleName4;
	private File metaFile;
	private boolean metaExists = false;
	private File createFile,createFile1,createFile2,createFile3,createFile4;
	String errorMsg="";
	String  projectPath = "";
	String srcDir = "src\\test\\resources\\testdatainput";
	String dstDir = "src\\test\\resources\\testdataoutput";
	String metaDataFileDir = "src\\test\\resources\\testdatainput\\TrustBundleMetaData.xml";
	String metaDataFileDir1 = "src\\test\\resources\\testdatainput\\Sample.txt";
	File outStr1;

	@Before
	public void setUp() throws Exception {

		//Get All the required Initial Inputs


		projectPath = new File(".").getAbsoluteFile().toString();
		projectPath = projectPath.substring(0, projectPath.length()-1);

		anchorDir = projectPath.concat(srcDir);

		destDir = projectPath.concat(dstDir);

		metaDataFile = projectPath.concat(metaDataFileDir);

		bundleName1 = "UnSignedNoMetaData.p7b";

		bundleName2 = "UnSignedMetaData.p7b";

		bundleName3 = "UnSignedNoMetaData.p7c";

		bundleName4 = "UnSignedMetaData.p7c";

		createFile = new File(destDir+"/"+bundleName1);

		createFile1 = new File(destDir+"/"+bundleName1);

		createFile2 = new File(destDir+"/"+bundleName2);

		createFile3 = new File(destDir+"/"+bundleName3);

		createFile4 = new File(destDir+"/"+bundleName4);

	}

	@After
	public void tearDown() throws Exception {
		//Free All Resource
		projectPath = null;


		anchorDir =  null;

		destDir =  null;

		metaDataFile =  null;


		createFile =  null;

		createFile1 =  null;

		createFile2 =  null;

		createFile3 =  null;

		createFile4 =  null;

	}

	@Test
	public void testGetParameters() {
		/*fail("Not yet implemented");*/

		//Case-1 Check the Blank Anchor directory
		CreateUnSignedPKCS7 unsigned = new CreateUnSignedPKCS7();

		 errorMsg = unsigned.getParameters("Select Trust Anchor Directory", metaDataFile, destDir, bundleName1);
		 assertEquals("Error: Kindly Provide Trust Anchor Directory",errorMsg);

		//Case-2 Check the Blank Anchor directory
		 errorMsg = unsigned.getParameters("You pressed cancel", metaDataFile, destDir, bundleName1);
		 assertEquals("Error: Kindly Provide Trust Anchor Directory",errorMsg);

		//Case-3 Check the Blank Destination directory
		 errorMsg = unsigned.getParameters(anchorDir, metaDataFile, "Select Trust Bundle Destination Directory", bundleName1);
		 assertEquals("Error: Kindly Provide Trust Bundle Destination Directory",errorMsg);

		//Case-4 Check the Blank Destination directory
		 errorMsg = unsigned.getParameters(anchorDir, metaDataFile, "You pressed cancel", bundleName1);
		 assertEquals("Error: Kindly Provide Trust Bundle Destination Directory",errorMsg);

		//Case-5 Check the MetaDataFile
		 errorMsg = unsigned.getParameters(anchorDir, metaDataFile, destDir, bundleName1);
		 assertEquals("Message: "+bundleName1+" created successfully!",errorMsg);

		 //Case-6 Check the MetaDataFile with different extension
		  metaDataFile = projectPath.concat(metaDataFileDir1);
		 errorMsg = unsigned.getParameters(anchorDir, metaDataFile, destDir, bundleName1);
		 assertEquals("Error: Kindly Provide A XML Meta data file",errorMsg);


		//Case-7 Check the Blank MetaDataFile
		 errorMsg = unsigned.getParameters(anchorDir, "Select Meta Data", destDir, bundleName1);
		 assertEquals("Error: Kindly Provide A XML Meta data file",errorMsg);

		//Case-8 Check the Blank MetaDataFile
		 errorMsg = unsigned.getParameters(anchorDir, "You pressed", destDir, bundleName1);
		 assertEquals("Error: Kindly Provide A XML Meta data file",errorMsg);


		//Case-9 Check the Blank Bundle Name
		 errorMsg = unsigned.getParameters(anchorDir, metaDataFile, destDir, "");
		 assertEquals("Error: Kindly Provide A Proper Trust Bundle Name with a .p7b or .p7c extension",errorMsg);

	   //Case-10 Check the Bundle Name with different extension
		 errorMsg = unsigned.getParameters(anchorDir, metaDataFile, destDir,"unsigned.txt");
		 assertEquals("Error: Kindly Provide A Proper Trust Bundle Name with a .p7b or .p7c extension",errorMsg);



	}


	@Test
	public void testCreate()
	{
		CreateUnSignedPKCS7 unsigned = new CreateUnSignedPKCS7();


		//Case-1 Check with no MetaData and bundle name with extension .p7b
		outStr1 = unsigned.create(anchorDir, createFile1,metaFile, metaExists);
		assertEquals(bundleName1,outStr1.getName());


		//Case-2 Check with MetaData and bundle name with extension .p7b
		outStr1 = unsigned.create(anchorDir, createFile2,new File(metaDataFile), true);
		assertEquals(bundleName2,outStr1.getName());

		//Case-3 Check with no MetaData and bundle name with extension .p7c
		outStr1 = unsigned.create(anchorDir, createFile3,metaFile, metaExists);
		assertEquals(bundleName3,outStr1.getName());


		//Case-4 Check with MetaData and bundle name with extension .p7c
		outStr1 = unsigned.create(anchorDir, createFile4,new File(metaDataFile), true);
		assertEquals(bundleName4,outStr1.getName());

		//Case-5 Check with wrong destination directory with NoMetaData
		outStr1 = unsigned.create(anchorDir, new File("C:\\JunitTesting\\wrong\\UnSignedNoMetaData.p7b"),metaFile, metaExists);
		assertEquals(null,outStr1);

		//Case-6 Check with wrong destination directory with MetaData
		outStr1 = unsigned.create(anchorDir, new File("C:\\wrong\\UnSignedNoMetaData.p7b"),new File(metaDataFile), metaExists);
		assertEquals(null,outStr1);

		//Case-7 Check with wrong anchor directory with NoMetaData
		outStr1 = unsigned.create("C:\\JunitTesting\\wrong",metaFile,metaFile, metaExists);
		assertEquals(null,outStr1);

		//Case-8 Check with wrong anchor directory with MetaData
		outStr1 = unsigned.create("C:\\JunitTesting\\wrong",createFile ,new File(metaDataFile), metaExists);
		assertEquals(null,outStr1);

		//Case-9 Check with Blank anchor directory with noMetaData
		outStr1 = unsigned.create("",createFile ,metaFile, metaExists);
		assertEquals(null,outStr1);

		//Case-10 Check with Blank anchor directory with MetaData
		outStr1 = unsigned.create("",createFile ,new File(metaDataFile), metaExists);
		assertEquals(null,outStr1);

		//Case-11 Check with Blank destination directory with noMetaData
		outStr1 = unsigned.create(anchorDir,new File("") ,metaFile, metaExists);
		assertEquals(null,outStr1);

		//Case-12 Check with Blank anchor directory with MetaData
		outStr1 = unsigned.create(anchorDir,new File("") ,new File(metaDataFile), metaExists);
		assertEquals(null,outStr1);

		//Case-13 Check with Blank MetaData File
		outStr1 = unsigned.create(anchorDir,createFile ,new File(""), metaExists);
		assertEquals(bundleName1,outStr1.getName());



	}

}
