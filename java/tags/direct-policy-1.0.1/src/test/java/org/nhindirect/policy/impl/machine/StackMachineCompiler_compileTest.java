package org.nhindirect.policy.impl.machine;

import java.security.cert.X509Certificate;
import java.util.Vector;

import org.bouncycastle.asn1.x509.KeyUsage;
import org.nhindirect.policy.LiteralPolicyExpression;
import org.nhindirect.policy.LiteralPolicyExpressionFactory;
import org.nhindirect.policy.Opcode;
import org.nhindirect.policy.OperationPolicyExpression;
import org.nhindirect.policy.OperationPolicyExpressionFactory;
import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.policy.PolicyOperator;
import org.nhindirect.policy.PolicyValue;
import org.nhindirect.policy.PolicyValueFactory;
import org.nhindirect.policy.impl.machine.StackMachine;
import org.nhindirect.policy.impl.machine.StackMachineCompiler;
import org.nhindirect.policy.impl.machine.StackMachineEntry;
import org.nhindirect.policy.util.TestUtils;
import org.nhindirect.policy.x509.ExtendedKeyUsageExtensionField;
import org.nhindirect.policy.x509.ExtendedKeyUsageIdentifier;
import org.nhindirect.policy.x509.KeyUsageExtensionField;

import junit.framework.TestCase;

public class StackMachineCompiler_compileTest extends TestCase
{
	public void testCompile_simpleUnaryLiteralOperation_assertEntriesAndEvaluation() throws Exception
	{
		// build the expression
		final PolicyValue<Boolean> op1 = PolicyValueFactory.getInstance(true);
		
		final LiteralPolicyExpression<Boolean> expr = LiteralPolicyExpressionFactory.getInstance(op1);
		final Vector<PolicyExpression> operands = new Vector<PolicyExpression>();
		operands.add(expr);
		
		final OperationPolicyExpression oper = OperationPolicyExpressionFactory.getInstance(PolicyOperator.LOGICAL_NOT, operands);
		
		final StackMachineCompiler compiler = new StackMachineCompiler();
		
		final Vector<Opcode> entries = compiler.compile(null, oper);
		
		assertEquals(2, entries.size());
		
		assertEquals(op1, ((StackMachineEntry)entries.get(0)).getValue());
		assertEquals(PolicyOperator.LOGICAL_NOT, ((StackMachineEntry)entries.get(1)).getOperator());
		
		// execute the compiled expression in the stack machine
		final StackMachine machine = new StackMachine();
		
		Boolean evalVal = machine.evaluate((Vector<Opcode>)entries);
		assertFalse(evalVal);
	}
	
	public void testCompile_simpleBinaryLiteralOperation_assertEntriesAndEvaluation() throws Exception
	{
		// build the expression
		final PolicyValue<Integer> op1 = PolicyValueFactory.getInstance(1);
		final PolicyValue<Integer> op2 = PolicyValueFactory.getInstance(1);
		
		final LiteralPolicyExpression<Integer> expr1 = LiteralPolicyExpressionFactory.getInstance(op1);
		final LiteralPolicyExpression<Integer> expr2 = LiteralPolicyExpressionFactory.getInstance(op2);
		final Vector<PolicyExpression> operands = new Vector<PolicyExpression>();
		operands.add(expr1);
		operands.add(expr2);
		
		final OperationPolicyExpression oper = OperationPolicyExpressionFactory.getInstance(PolicyOperator.EQUALS, operands);
		
		final StackMachineCompiler compiler = new StackMachineCompiler();
		
		final Vector<Opcode> entries = compiler.compile(null, oper);
		
		assertEquals(3, entries.size());
		
		assertEquals(op1, ((StackMachineEntry)entries.get(0)).getValue());
		assertEquals(op2, ((StackMachineEntry)entries.get(1)).getValue());
		assertEquals(PolicyOperator.EQUALS, ((StackMachineEntry)entries.get(2)).getOperator());
		
		// execute the compiled expression in the stack machine
		final StackMachine machine = new StackMachine();
		
		final Boolean evalVal = machine.evaluate(entries);
		assertTrue(evalVal);
	}	
	
	public void testCompile_simpleBinaryLiteralAndExpressionOperation_assertEntriesAndEvaluation() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("AlAnderson@hospitalA.direct.visionshareinc.com.der");
		
		// build the expression
		final Integer keyUsage = KeyUsage.digitalSignature | KeyUsage.keyEncipherment | KeyUsage.nonRepudiation;
		final PolicyValue<Integer> op1 = PolicyValueFactory.getInstance(keyUsage);

		final LiteralPolicyExpression<Integer> expr1 = LiteralPolicyExpressionFactory.getInstance(op1);
		final KeyUsageExtensionField expr2 = new KeyUsageExtensionField(true);
		
		final Vector<PolicyExpression> operands = new Vector<PolicyExpression>();
		operands.add(expr1);
		operands.add(expr2);
		
		final OperationPolicyExpression oper = OperationPolicyExpressionFactory.getInstance(PolicyOperator.EQUALS, operands);
		
		final StackMachineCompiler compiler = new StackMachineCompiler();
		
		final Vector<Opcode> entries = compiler.compile(cert, oper);
		
		assertEquals(3, entries.size());
		
		assertEquals(op1, ((StackMachineEntry)entries.get(0)).getValue());
		assertEquals(keyUsage, ((StackMachineEntry)entries.get(1)).getValue().getPolicyValue());
		assertEquals(PolicyOperator.EQUALS, ((StackMachineEntry)entries.get(2)).getOperator());
		
		// execute the compiled expression in the stack machine
		final StackMachine machine = new StackMachine();
		
		final Boolean evalVal = machine.evaluate(entries);
		assertTrue(evalVal);
	}	
	
	public void testCompile_multipleEmbeddedOperations_keyUsage_assertEntriesAndEvaluation() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("AlAnderson@hospitalA.direct.visionshareinc.com.der");
		
		// build the expression
		final Integer keyUsage = KeyUsage.nonRepudiation;
		final PolicyValue<Integer> op1 = PolicyValueFactory.getInstance(keyUsage);

		final LiteralPolicyExpression<Integer> expr1 = LiteralPolicyExpressionFactory.getInstance(op1);
		final KeyUsageExtensionField expr2 = new KeyUsageExtensionField(true);
		
		final Vector<PolicyExpression> operands1 = new Vector<PolicyExpression>();
		operands1.add(expr1);
		operands1.add(expr2);
		
		final OperationPolicyExpression oper1 = OperationPolicyExpressionFactory.getInstance(PolicyOperator.BITWISE_AND, operands1);
		
		
		// build outer expression embedding the first operation as a parameter
		final PolicyValue<Integer> op3 = PolicyValueFactory.getInstance(0);
		final LiteralPolicyExpression<Integer> expr3 = LiteralPolicyExpressionFactory.getInstance(op3);
		
		final Vector<PolicyExpression> operands2 = new Vector<PolicyExpression>();
		operands2.add(oper1);
		operands2.add(expr3);
		
		final OperationPolicyExpression oper2 = OperationPolicyExpressionFactory.getInstance(PolicyOperator.GREATER, operands2);
		
		
		final StackMachineCompiler compiler = new StackMachineCompiler();
		
		final Vector<Opcode> entries = compiler.compile(cert, oper2);
		
		assertEquals(5, entries.size());
		
		assertEquals(op1, ((StackMachineEntry)entries.get(0)).getValue());
		assertEquals(expr2.getPolicyValue().getPolicyValue(), ((StackMachineEntry)entries.get(1)).getValue().getPolicyValue());
		assertEquals(PolicyOperator.BITWISE_AND, ((StackMachineEntry)entries.get(2)).getOperator());
		assertEquals(op3, ((StackMachineEntry)entries.get(3)).getValue());
		assertEquals(PolicyOperator.GREATER, ((StackMachineEntry)entries.get(4)).getOperator());	
		
		// execute the compiled expression in the stack machine
		final StackMachine machine = new StackMachine();
		
		final Boolean evalVal = machine.evaluate(entries);
		assertTrue(evalVal);
	}		
	
	public void testCompile_multipleEmbeddedOperations_extendedKeyUsage_keyUsage_assertEntriesAndEvaluation() throws Exception
	{
		final X509Certificate cert = TestUtils.loadCertificate("mshost.der");
		
		// build the expression
		final Integer keyUsage = KeyUsage.nonRepudiation;
		final PolicyValue<Integer> op1 = PolicyValueFactory.getInstance(keyUsage);

		final LiteralPolicyExpression<Integer> expr1 = LiteralPolicyExpressionFactory.getInstance(op1);
		final KeyUsageExtensionField expr2 = new KeyUsageExtensionField(true);
		
		final Vector<PolicyExpression> operands1 = new Vector<PolicyExpression>();
		operands1.add(expr1);
		operands1.add(expr2);
		
		final OperationPolicyExpression oper1 = OperationPolicyExpressionFactory.getInstance(PolicyOperator.BITWISE_AND, operands1);
		
		
		// build outer expression embedding the first operation as a parameter
		final PolicyValue<Integer> op3 = PolicyValueFactory.getInstance(0);
		final LiteralPolicyExpression<Integer> expr3 = LiteralPolicyExpressionFactory.getInstance(op3);
		
		final Vector<PolicyExpression> operands2 = new Vector<PolicyExpression>();
		operands2.add(oper1);
		operands2.add(expr3);
		
		final OperationPolicyExpression oper2 = OperationPolicyExpressionFactory.getInstance(PolicyOperator.GREATER, operands2);
		
		
		final StackMachineCompiler compiler = new StackMachineCompiler();
		
		
		// build a separate expression for extended key usage
		final ExtendedKeyUsageExtensionField expr4 = new ExtendedKeyUsageExtensionField(true);
		
		final PolicyValue<String> op5 = PolicyValueFactory.getInstance(ExtendedKeyUsageIdentifier.ID_KP_EMAIL_PROTECTION.getId());
		final LiteralPolicyExpression<String> expr5 = LiteralPolicyExpressionFactory.getInstance(op5);

		
		final Vector<PolicyExpression> operands3 = new Vector<PolicyExpression>();
		operands3.add(expr4);
		operands3.add(expr5);
		
		final OperationPolicyExpression oper3 = OperationPolicyExpressionFactory.getInstance(PolicyOperator.CONTAINS, operands3);
		
		// build an and operator and make sure the cert has all policies met
		final Vector<PolicyExpression> operands4 = new Vector<PolicyExpression>();
		operands4.add(oper2);
		operands4.add(oper3);
	

		final OperationPolicyExpression oper4 = OperationPolicyExpressionFactory.getInstance(PolicyOperator.LOGICAL_AND, operands4);

		final Vector<Opcode> entries = compiler.compile(cert, oper4);
	
		assertEquals(9, entries.size());
		
		assertEquals(op1, ((StackMachineEntry)entries.get(0)).getValue());
		assertEquals(expr2.getPolicyValue().getPolicyValue(), ((StackMachineEntry)entries.get(1)).getValue().getPolicyValue());
		assertEquals(PolicyOperator.BITWISE_AND, ((StackMachineEntry)entries.get(2)).getOperator());
		assertEquals(op3, ((StackMachineEntry)entries.get(3)).getValue());
		assertEquals(PolicyOperator.GREATER, ((StackMachineEntry)entries.get(4)).getOperator());	
		assertEquals(expr4.getPolicyValue(), ((StackMachineEntry)entries.get(5)).getValue());	
		assertEquals(op5, ((StackMachineEntry)entries.get(6)).getValue());
		assertEquals(PolicyOperator.CONTAINS, ((StackMachineEntry)entries.get(7)).getOperator());			
		assertEquals(PolicyOperator.LOGICAL_AND, ((StackMachineEntry)entries.get(8)).getOperator());
		
		// execute the compiled expression in the stack machine
		final StackMachine machine = new StackMachine();
		
		final Boolean evalVal = machine.evaluate(entries);
		assertTrue(evalVal);
	}			
}
