package org.nhindirect.policy.impl;

import java.io.File;
import java.io.InputStream;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.nhindirect.policy.LiteralPolicyExpression;
import org.nhindirect.policy.OperationPolicyExpression;
import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.policy.PolicyExpressionType;
import org.nhindirect.policy.PolicyOperator;
import org.nhindirect.policy.x509.KeyUsageExtensionField;
import org.nhindirect.policy.x509.SubjectAttributeField;
import org.nhindirect.policy.x509.X509Field;

import junit.framework.TestCase;

public class SimpleTextV1LexiconPolicyParser_buildExpressionTest extends TestCase
{
	public void testParse_simpleExpression_validatePolicyExpression() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		InputStream stream = FileUtils.openInputStream(new File("./src/test/resources/policies/simpleLexiconSamp1.txt"));
		
		Vector<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.parseToTokens(stream);

		// now build expressions
		PolicyExpression expression = parser.buildExpression(tokens.iterator(), 0);
		
		// check that the expression is a logical and
		assertNotNull(expression);
		assertEquals(PolicyExpressionType.OPERATION, expression.getExpressionType());
		OperationPolicyExpression operationExpression = (OperationPolicyExpression)expression;
		assertEquals(PolicyOperator.LOGICAL_AND, operationExpression.getPolicyOperator());
		
		// now break down the operands which should each be a operator expressions
		
		// operator 1
		// should be an equals operator expressions
		expression = operationExpression.getOperands().get(0);
		assertEquals(PolicyExpressionType.OPERATION, expression.getExpressionType());
		OperationPolicyExpression param1operationExpression = (OperationPolicyExpression)expression;
		assertEquals(PolicyOperator.EQUALS, param1operationExpression.getPolicyOperator());
		
		// break down the sub operation parameters... should be two literals
		expression = param1operationExpression.getOperands().get(0);
		assertEquals(PolicyExpressionType.LITERAL, expression.getExpressionType());
		assertEquals("1" ,((LiteralPolicyExpression<?>)expression).getPolicyValue().getPolicyValue());
		
		expression = param1operationExpression.getOperands().get(1);
		assertEquals(PolicyExpressionType.LITERAL, expression.getExpressionType());
		assertEquals("2" ,((LiteralPolicyExpression<?>)expression).getPolicyValue().getPolicyValue());
		
		// operator 2
		// should be an equals operator expressions
		expression = operationExpression.getOperands().get(1);
		assertEquals(PolicyExpressionType.OPERATION, expression.getExpressionType());
		OperationPolicyExpression param2operationExpression = (OperationPolicyExpression)expression;
		assertEquals(PolicyOperator.NOT_EQUALS, param2operationExpression.getPolicyOperator());
		
		// break down the sub operation parameters... should be two literals
		expression = param2operationExpression.getOperands().get(0);
		assertEquals(PolicyExpressionType.LITERAL, expression.getExpressionType());
		assertEquals("2" ,((LiteralPolicyExpression<?>)expression).getPolicyValue().getPolicyValue());
		
		expression = param2operationExpression.getOperands().get(1);
		assertEquals(PolicyExpressionType.LITERAL, expression.getExpressionType());
		assertEquals("1" ,((LiteralPolicyExpression<?>)expression).getPolicyValue().getPolicyValue());		
	}
	
	public void testParse_x509FieldsType_validatePolicyExpression() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		InputStream stream = FileUtils.openInputStream(new File("./src/test/resources/policies/lexiconWithCertificateStruct.txt"));
		
		Vector<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.parseToTokens(stream);

		// now build expressions
		PolicyExpression expression = parser.buildExpression(tokens.iterator(), 0);
		
		// check that the expression is an equals
		assertNotNull(expression);
		assertEquals(PolicyExpressionType.OPERATION, expression.getExpressionType());
		OperationPolicyExpression operationExpression = (OperationPolicyExpression)expression;
		assertEquals(PolicyOperator.EQUALS, operationExpression.getPolicyOperator());
		
		// break down the sub operation parameters... should be a cert reference and a literal
		expression = operationExpression.getOperands().get(0);
		assertEquals(PolicyExpressionType.REFERENCE, expression.getExpressionType());
		assertTrue(expression instanceof X509Field);
		
		expression = operationExpression.getOperands().get(1);
		assertEquals(PolicyExpressionType.LITERAL, expression.getExpressionType());
		assertEquals("1.2.840.113549.1.1.11" ,((LiteralPolicyExpression<?>)expression).getPolicyValue().getPolicyValue());
	}
	
	public void testParse_tbsFieldName_rdnAttribute_validatePolicyExpression() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		InputStream stream = FileUtils.openInputStream(new File("./src/test/resources/policies/literalWithSpaces.txt"));
		
		Vector<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.parseToTokens(stream);

		// now build expressions
		PolicyExpression expression = parser.buildExpression(tokens.iterator(), 0);
		
		// check that the expression is an equals
		assertNotNull(expression);
		assertEquals(PolicyExpressionType.OPERATION, expression.getExpressionType());
		OperationPolicyExpression operationExpression = (OperationPolicyExpression)expression;
		assertEquals(PolicyOperator.EQUALS, operationExpression.getPolicyOperator());
		
		// break down the sub operation parameters... should be a cert reference and a literal
		expression = operationExpression.getOperands().get(0);
		assertEquals(PolicyExpressionType.REFERENCE, expression.getExpressionType());
		assertTrue(expression instanceof SubjectAttributeField);
		
		expression = operationExpression.getOperands().get(1);
		assertEquals(PolicyExpressionType.LITERAL, expression.getExpressionType());
		assertEquals("United States", ((LiteralPolicyExpression<?>)expression).getPolicyValue().getPolicyValue());
	}
	
	public void testParse_extensionName_keyUsage_validatePolicyExpression() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		InputStream stream = FileUtils.openInputStream(new File("./src/test/resources/policies/lexiconWithKeyUsage.txt"));
		
		Vector<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.parseToTokens(stream);

		// now build expressions
		PolicyExpression expression = parser.buildExpression(tokens.iterator(), 0);
		
		// check that the expression is an equals
		assertNotNull(expression);
		assertEquals(PolicyExpressionType.OPERATION, expression.getExpressionType());
		OperationPolicyExpression operationExpression = (OperationPolicyExpression)expression;
		assertEquals(PolicyOperator.EQUALS, operationExpression.getPolicyOperator());
		
		// break down the sub operation parameters... should be a cert reference and a literal
		expression = operationExpression.getOperands().get(0);
		assertEquals(PolicyExpressionType.REFERENCE, expression.getExpressionType());
		assertTrue(expression instanceof KeyUsageExtensionField);
		
		expression = operationExpression.getOperands().get(1);
		assertEquals(PolicyExpressionType.LITERAL, expression.getExpressionType());
		assertEquals("1", ((LiteralPolicyExpression<?>)expression).getPolicyValue().getPolicyValue());
	}	
}
