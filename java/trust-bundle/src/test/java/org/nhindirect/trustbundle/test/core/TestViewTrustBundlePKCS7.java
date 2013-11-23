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
import org.nhindirect.trustbundle.core.ViewTrustBundlePKCS7;

/**
 * This is a Test class to Test the functionalities of the View Trust Bundle functionalities.
 * This class verifies the Validation and Viewing functionalities for Unsigned and Signed bundle.
 * @author 
 *
 */
public class TestViewTrustBundlePKCS7 extends TestCase{
	
	//private static final String String = null;
	private  File certiFileUnsignedNoMetaDataP7C;
	private  File certiFileUnsignedMetaDataP7C;
	private  File certiFileUnsignedNoMetaDataP7B;
	private  File certiFileUnsignedMetaDataP7B;
	
//	private String error ="";
	public ViewTrustBundlePKCS7 vpk =  new ViewTrustBundlePKCS7();
	boolean actual;
	String actualStr="";
	String projectPath = null;
	private File certiFileSignedNoMetaData;
	private File certiFileSignedMetaData;
	private File certiFileSignedcertnopwd;
	private File certiFileSignedcertpwd;

	@Before
	public void setUp() throws Exception {
		
		projectPath = new File(".").getAbsoluteFile().toString();
		projectPath = projectPath.substring(0, projectPath.length()-1);
		//System.out.println("path is:"+projectPath);
		
		certiFileUnsignedNoMetaDataP7C = new File(projectPath+"src\\test\\resources\\testdataoutput\\UnSignedNoMetaData.p7c");
		certiFileUnsignedMetaDataP7C = new File(projectPath+"src\\test\\resources\\testdataoutput\\UnSignedMetaData.p7c");
	
		certiFileUnsignedNoMetaDataP7B = new File(projectPath+"src\\test\\resources\\testdataoutput\\UnSignedNoMetaData.p7b");
		certiFileUnsignedMetaDataP7B = new File(projectPath+"src\\test\\resources\\testdataoutput\\UnSignedMetaData.p7b");
		
		certiFileSignedNoMetaData = new File(projectPath+"src\\test\\resources\\testdataoutput\\signednometadata1.p7m");
		certiFileSignedMetaData = new File(projectPath+"src\\test\\resources\\testdataoutput\\signedmetadata1.p7m");
		
		certiFileSignedcertnopwd = new File(projectPath+"src\\test\\resources\\testdataoutput\\signedmetadata2nopass.p7m");
		certiFileSignedcertpwd = new File(projectPath+"src\\test\\resources\\testdataoutput\\signedmetadata1.p7m");
	}

	@After
	public void tearDown() throws Exception {
		certiFileUnsignedNoMetaDataP7C = null;
		//System.out.println("'My File is"+certiFileUnsignedNoMetaDataP7C.getAbsolutePath());
		certiFileUnsignedMetaDataP7C = null;
		certiFileUnsignedNoMetaDataP7B = null;
		certiFileUnsignedMetaDataP7B = null;
		certiFileSignedNoMetaData = null;
		certiFileSignedMetaData = null;
		certiFileSignedcertnopwd = null;
		certiFileSignedcertpwd = null;
		
	}

	@Test
	public void testGetParameters() {
		

		//case 1 - Proper Directory not provided
		actualStr = vpk.getParameters("Select Trust Anchor Directory");
		//System.out.println(actualStr);
		assertEquals("Error: Kindly Provide Trust Anchor Directory",actualStr);
		
		//Case 2 - Trust Anchor Directory select operation cancelled	
		actualStr = vpk.getParameters("You pressed cancel");
		//System.out.println(actualStr);
		assertEquals("Error: Kindly Provide Trust Anchor Directory",actualStr);
		
		//Case-3 Improper file extension provided
		actualStr = vpk.getParameters("usginednometadata.p7d");
		//System.out.println(actualStr);
		assertEquals("Error:Please provide a valid file!",actualStr);
		
		//Proper File provided
		//System.out.println(certiFileUnsignedNoMetaDataP7C.getAbsolutePath());
		actualStr = vpk.getParameters(certiFileUnsignedNoMetaDataP7C.getAbsolutePath());
		//System.out.println(actualStr);
		assertTrue(actualStr.contains("Trust Anchor :"));
		assertTrue(actualStr.contains("Common Name :"));
		assertTrue(actualStr.contains("DN :"));
		assertTrue(actualStr.contains("Meta Data :"));
		assertTrue(actualStr.contains("Absent"));
		
			
		actualStr = vpk.getParameters(certiFileUnsignedNoMetaDataP7C.getAbsolutePath());
		assertTrue(actualStr.contains("Trust Anchor :"));
		assertTrue(actualStr.contains("Common Name :"));
		assertTrue(actualStr.contains("DN :"));
		assertTrue(actualStr.contains("Meta Data :"));
		assertTrue(actualStr.contains("Absent"));	
		
		//Proper file provided		
		actualStr = vpk.getParameters(certiFileUnsignedMetaDataP7C.getAbsolutePath());
		//System.out.println(actualStr);
		assertTrue(actualStr.contains("Trust Anchor :"));
		assertTrue(actualStr.contains("Common Name :"));
		assertTrue(actualStr.contains("DN :"));
		assertTrue(actualStr.contains("Meta Data :"));
		assertTrue(actualStr.contains("<TrustBundle "));
		

		
		actualStr = vpk.getParameters(certiFileUnsignedMetaDataP7C.getAbsolutePath());
		assertTrue(actualStr.contains("Trust Anchor :"));
		assertTrue(actualStr.contains("Common Name :"));
		assertTrue(actualStr.contains("DN :"));
		assertTrue(actualStr.contains("Meta Data :"));
		assertTrue(actualStr.contains("<TrustBundle "));
		
		actualStr = vpk.getParameters(certiFileUnsignedNoMetaDataP7B.getAbsolutePath());
		//System.out.println(actualStr);
		assertTrue(actualStr.contains("Trust Anchor :"));
		assertTrue(actualStr.contains("Common Name :"));
		assertTrue(actualStr.contains("DN :"));
		assertTrue(actualStr.contains("Meta Data :"));
		assertTrue(actualStr.contains("Absent"));
		

		
		actualStr = vpk.getParameters(certiFileUnsignedNoMetaDataP7B.getAbsolutePath());
		assertTrue(actualStr.contains("Trust Anchor :"));
		assertTrue(actualStr.contains("Common Name :"));
		assertTrue(actualStr.contains("DN :"));
		assertTrue(actualStr.contains("Meta Data :"));
		assertTrue(actualStr.contains("Absent"));
		
		
		actualStr = vpk.getParameters(certiFileUnsignedMetaDataP7B.getAbsolutePath());
		//System.out.println(actualStr);
		assertTrue(actualStr.contains("Trust Anchor :"));
		assertTrue(actualStr.contains("Common Name :"));
		assertTrue(actualStr.contains("DN :"));
		assertTrue(actualStr.contains("Meta Data :"));
		assertTrue(actualStr.contains("<TrustBundle"));
		
	}

	@Test
	public void testViewBundle() {
		//case2	
		
		actual = vpk.viewBundle(certiFileUnsignedNoMetaDataP7C);
		assertEquals(true, actual);
		
		
		actual = vpk.viewBundle(new File("54566"));
		assertEquals(false, actual);
		
		actual = vpk.viewBundle(certiFileUnsignedMetaDataP7C);
		assertEquals(true, actual);
		
		actual = vpk.viewBundle(certiFileUnsignedNoMetaDataP7B);
		assertEquals(true, actual);
		
		
		actual = vpk.viewBundle(certiFileUnsignedMetaDataP7B);
		assertEquals(true, actual);
	
				
		actual = vpk.viewBundle(certiFileSignedNoMetaData);
		assertEquals(true, actual);
		
		
		actual = vpk.viewBundle(certiFileSignedMetaData);
		assertEquals(true, actual);
		
		
		actual = vpk.viewBundle(certiFileSignedcertnopwd);
		assertEquals(true, actual);
		
		actual = vpk.viewBundle(certiFileSignedcertpwd);
		assertEquals(true, actual);
		
				
	}

}
