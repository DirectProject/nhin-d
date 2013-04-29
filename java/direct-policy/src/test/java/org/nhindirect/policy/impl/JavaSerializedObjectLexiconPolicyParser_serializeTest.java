package org.nhindirect.policy.impl;

import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Vector;

import junit.framework.TestCase;

import org.bouncycastle.asn1.x509.KeyUsage;
import org.nhindirect.policy.LiteralPolicyExpression;
import org.nhindirect.policy.LiteralPolicyExpressionFactory;
import org.nhindirect.policy.OperationPolicyExpression;
import org.nhindirect.policy.OperationPolicyExpressionFactory;
import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.policy.PolicyExpressionType;
import org.nhindirect.policy.PolicyOperator;
import org.nhindirect.policy.PolicyParseException;
import org.nhindirect.policy.PolicyValue;
import org.nhindirect.policy.PolicyValueFactory;
import org.nhindirect.policy.x509.ExtendedKeyUsageExtensionField;
import org.nhindirect.policy.x509.ExtendedKeyUsageIdentifier;
import org.nhindirect.policy.x509.KeyUsageExtensionField;

public class JavaSerializedObjectLexiconPolicyParser_serializeTest extends TestCase
{
	public void testSerialize_simpleExpression_validateExpression() throws Exception
	{
		final JavaSerializedObjectLexiconPolicyParser parser = new JavaSerializedObjectLexiconPolicyParser();
		
		// build the expression
		final PolicyValue<Boolean> op1 = PolicyValueFactory.getInstance(true);
		
		final LiteralPolicyExpression<Boolean> expr = LiteralPolicyExpressionFactory.getInstance(op1);
		final Vector<PolicyExpression> operands = new Vector<PolicyExpression>();
		operands.add(expr);
		
		final OperationPolicyExpression oper = OperationPolicyExpressionFactory.getInstance(PolicyOperator.LOGICAL_NOT, operands);
		
		// serialize
		final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		parser.serialize(oper, outStream);
		
		assertTrue(outStream.size() > 0);
		
		String serialzied = new String(outStream.toByteArray());
		System.out.println(serialzied);
		
		// deserialize
		final ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());

		final PolicyExpression deserExpression = parser.parse(inStream);
		
		assertNotNull(deserExpression);
		
		assertEquals(PolicyExpressionType.OPERATION, deserExpression.getExpressionType());
	}
	
	public void testSerialize_complexExpression_validateExpression() throws Exception
	{
		final JavaSerializedObjectLexiconPolicyParser parser = new JavaSerializedObjectLexiconPolicyParser();
		
		// build the expression
		final Integer keyUsage = KeyUsage.keyEncipherment;
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
		
		// serialize
		final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		parser.serialize(oper4, outStream);
		
		assertTrue(outStream.size() > 0);
		
		String serialzied = new String(outStream.toByteArray());
		System.out.println(serialzied);
		
		// deserialize
		final ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());

		final PolicyExpression deserExpression = parser.parse(inStream);
		
		assertNotNull(deserExpression);
		
		assertEquals(PolicyExpressionType.OPERATION, deserExpression.getExpressionType());
	}	
	
	public void testSerialize_nullExpression_assertExecption() throws Exception
	{
		boolean exceptionOccured = false;
		
		final JavaSerializedObjectLexiconPolicyParser parser = new JavaSerializedObjectLexiconPolicyParser();
		
		try
		{
			parser.serialize(null,  null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	public void testSerialize_nullStream_assertExecption() throws Exception
	{
		boolean exceptionOccured = false;
		
		final JavaSerializedObjectLexiconPolicyParser parser = new JavaSerializedObjectLexiconPolicyParser();
		
		try
		{
			parser.serialize(mock(PolicyExpression.class),  null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}
	
	public void testDeserialize_nullStream_assertExecption() throws Exception
	{
		boolean exceptionOccured = false;
		
		final JavaSerializedObjectLexiconPolicyParser parser = new JavaSerializedObjectLexiconPolicyParser();
		
		try
		{
			parser.deserialize(null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
	
	public void testDeserializeo_invalidObject_assertExecption() throws Exception
	{
		boolean exceptionOccured = false;
		
		final JavaSerializedObjectLexiconPolicyParser parser = new JavaSerializedObjectLexiconPolicyParser();
		
		try
		{
			parser.deserialize(new ByteArrayInputStream(new byte[] {0,1,2}));
		}
		catch (PolicyParseException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
}
