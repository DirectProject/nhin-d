package org.nhindirect.policy.impl.machine;

import java.security.cert.X509Certificate;

import org.nhindirect.policy.PolicyRequiredException;
import org.nhindirect.policy.util.TestUtils;
import org.nhindirect.policy.x509.KeyUsageExtensionField;

import junit.framework.TestCase;

public class StackMachine_getCompilationReportTest extends TestCase
{
	public void testGetCompilationReportTest_noReport_assertEmptyCollection()
	{
		final StackMachineCompiler machine = new StackMachineCompiler();
		assertTrue(machine.getCompilationReport().isEmpty());
	}
	
	public void testGetCompilationReportTest_missingRequiredField_reportModeOff_assertEmptyCollection() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("umesh.der");
		
		final KeyUsageExtensionField keyExtendExp = new KeyUsageExtensionField(true);
		
		final StackMachineCompiler machine = new StackMachineCompiler();
		machine.setReportModeEnabled(false);
		assertFalse(machine.isReportModeEnabled());
		
		boolean exceptionOccured = false;
		
		try
		{
			machine.compile(cert, keyExtendExp);
		}
		catch (PolicyRequiredException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
		assertTrue(machine.getCompilationReport().isEmpty());
	}	
	
	public void testGetCompilationReportTest_missingRequiredField_reportModeOn_assertNonEmptyCollection() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("umesh.der");
		
		final KeyUsageExtensionField keyExtendExp = new KeyUsageExtensionField(true);
		
		final StackMachineCompiler machine = new StackMachineCompiler();
		machine.setReportModeEnabled(true);
		assertTrue(machine.isReportModeEnabled());
		

		machine.compile(cert, keyExtendExp);
		
		assertEquals(1, machine.getCompilationReport().size());
	}		
	
	public void testGetCompilationReportTest_mulipleCompilations_reportModeOn_assertCorrectReportSize() throws Exception
	{
		final X509Certificate cert1 = TestUtils.loadCertificate("umesh.der");
		final X509Certificate cert2 = TestUtils.loadCertificate("AlAnderson@hospitalA.direct.visionshareinc.com.der");
		
		KeyUsageExtensionField keyExtendExp = new KeyUsageExtensionField(true);
		
		final StackMachineCompiler machine = new StackMachineCompiler();
		machine.setReportModeEnabled(true);
		assertTrue(machine.isReportModeEnabled());
		
		machine.compile(cert1, keyExtendExp);
		
		assertEquals(1, machine.getCompilationReport().size());
		
		// do it again with a cert that has all required fields
		keyExtendExp = new KeyUsageExtensionField(true);
		machine.compile(cert2, keyExtendExp);
		
		assertTrue(machine.getCompilationReport().isEmpty());
		
		// do one more time with the first use case and make sure there is an entry in the report
		keyExtendExp = new KeyUsageExtensionField(true);
		machine.compile(cert1, keyExtendExp);
		
		assertEquals(1, machine.getCompilationReport().size());	
		
	}	
	
	public void testGetCompilationReportTest_switchModes_assertCorrectReportSize() throws Exception
	{
		final X509Certificate cert1 = TestUtils.loadCertificate("umesh.der");
		
		KeyUsageExtensionField keyExtendExp = new KeyUsageExtensionField(true);
		
		final StackMachineCompiler machine = new StackMachineCompiler();
		machine.setReportModeEnabled(true);
		assertTrue(machine.isReportModeEnabled());
		
		machine.compile(cert1, keyExtendExp);
		
		assertEquals(1, machine.getCompilationReport().size());
		
		// turn off report mode and make sure the report is empty
		keyExtendExp = new KeyUsageExtensionField(true);
		machine.setReportModeEnabled(false);
		assertFalse(machine.isReportModeEnabled());

		
		boolean exceptionOccured = false;
		
		try
		{
			machine.compile(cert1, keyExtendExp);
		}
		catch (PolicyRequiredException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
		assertTrue(machine.getCompilationReport().isEmpty());
		

		
	}		
}
