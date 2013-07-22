package org.nhindirect.policy.impl;

import java.io.File;
import java.io.InputStream;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.nhindirect.policy.LiteralPolicyExpression;
import org.nhindirect.policy.OperationPolicyExpression;
import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.policy.PolicyExpressionType;
import org.nhindirect.policy.PolicyGrammarException;
import org.nhindirect.policy.PolicyOperator;
import org.nhindirect.policy.x509.KeyUsageExtensionField;
import org.nhindirect.policy.x509.SubjectAttributeField;
import org.nhindirect.policy.x509.SubjectKeyIdentifierExtensionField;
import org.nhindirect.policy.x509.X509Field;

import junit.framework.TestCase;

public class SimpleTextV1LexiconPolicyParser_buildExpressionTest extends TestCase
{
	public void testBuildExpression_simpleExpression_validatePolicyExpression() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		final InputStream stream = FileUtils.openInputStream(new File("./src/test/resources/policies/simpleLexiconSamp1.txt"));
		
		final Vector<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.parseToTokens(stream);

		// now build expressions
		PolicyExpression expression = parser.buildExpression(tokens.iterator());
		
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
	
	public void testBuildExpression_x509FieldsType_validatePolicyExpression() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		final InputStream stream = FileUtils.openInputStream(new File("./src/test/resources/policies/lexiconWithCertificateStruct.txt"));
		
		final Vector<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.parseToTokens(stream);

		// now build expressions
		PolicyExpression expression = parser.buildExpression(tokens.iterator());
		
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
	
	public void testBuildExpression_tbsFieldName_rdnAttribute_validatePolicyExpression() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		final InputStream stream = FileUtils.openInputStream(new File("./src/test/resources/policies/literalWithSpaces.txt"));
		
		final Vector<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.parseToTokens(stream);

		// now build expressions
		PolicyExpression expression = parser.buildExpression(tokens.iterator());
		
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
	
	public void testBuildExpression_extensionName_keyUsage_validatePolicyExpression() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		final InputStream stream = FileUtils.openInputStream(new File("./src/test/resources/policies/lexiconWithKeyUsage.txt"));
		
		final Vector<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.parseToTokens(stream);

		// now build expressions
		PolicyExpression expression = parser.buildExpression(tokens.iterator());
		
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
	
	public void testBuildExpression_tinaryExpression_literalOperands_validatePolicyExpression() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		InputStream stream = IOUtils.toInputStream("2 = 1 != true");
		
		Vector<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.parseToTokens(stream);
		
		// now build expressions
		PolicyExpression expression = parser.buildExpression(tokens.iterator());
		
		// check that the expression is an equals
		assertNotNull(expression);
		assertEquals(PolicyExpressionType.OPERATION, expression.getExpressionType());
		OperationPolicyExpression operationExpression = (OperationPolicyExpression)expression;
		assertEquals(PolicyOperator.NOT_EQUALS, operationExpression.getPolicyOperator());
		
		// break down the sub operation parameters... should be an operation and a literal
		expression = operationExpression.getOperands().get(1);
		assertEquals(PolicyExpressionType.LITERAL, expression.getExpressionType());
		assertEquals("true", ((LiteralPolicyExpression<?>)expression).getPolicyValue().getPolicyValue());
		
		expression = operationExpression.getOperands().get(0);
		assertEquals(PolicyExpressionType.OPERATION, expression.getExpressionType());

		
		// break down the sub parameters again of this operation
		OperationPolicyExpression subOperation = (OperationPolicyExpression)expression;
		assertEquals(PolicyOperator.EQUALS, subOperation.getPolicyOperator());
		
		expression = subOperation.getOperands().get(0);
		assertEquals(PolicyExpressionType.LITERAL, expression.getExpressionType());
		assertEquals("2", ((LiteralPolicyExpression<?>)expression).getPolicyValue().getPolicyValue());
		
		expression = subOperation.getOperands().get(1);
		assertEquals(PolicyExpressionType.LITERAL, expression.getExpressionType());
		assertEquals("1", ((LiteralPolicyExpression<?>)expression).getPolicyValue().getPolicyValue());
	}
	
	public void testBuildExpression_tinaryExpression_operatorExpressionOperands_validatePolicyExpression() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		InputStream stream = IOUtils.toInputStream("false = !true && !false");
		
		Vector<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.parseToTokens(stream);
		
		// now build expressions
		PolicyExpression expression = parser.buildExpression(tokens.iterator());
		
		// check that the expression is an equals
		assertNotNull(expression);
		assertEquals(PolicyExpressionType.OPERATION, expression.getExpressionType());
		OperationPolicyExpression operationExpression = (OperationPolicyExpression)expression;
		assertEquals(PolicyOperator.LOGICAL_AND, operationExpression.getPolicyOperator());
		
		
		// break down the sub operation parameters... should be two operations
		OperationPolicyExpression subOperation = (OperationPolicyExpression)operationExpression.getOperands().get(0);
		assertEquals(PolicyExpressionType.OPERATION, subOperation.getExpressionType());
		assertEquals(PolicyOperator.EQUALS, subOperation.getPolicyOperator());
		
		expression = subOperation.getOperands().get(0);
		assertEquals(PolicyExpressionType.LITERAL, expression.getExpressionType());
		assertEquals("false", ((LiteralPolicyExpression<?>)expression).getPolicyValue().getPolicyValue());
		
		OperationPolicyExpression subSubOperation = (OperationPolicyExpression)subOperation.getOperands().get(1);
		assertEquals(PolicyExpressionType.OPERATION, subSubOperation.getExpressionType());
		assertEquals(PolicyOperator.LOGICAL_NOT, subSubOperation.getPolicyOperator());
		
		subOperation = (OperationPolicyExpression)operationExpression.getOperands().get(1);
		assertEquals(PolicyExpressionType.OPERATION, subOperation.getExpressionType());
		assertEquals(PolicyOperator.LOGICAL_NOT, subOperation.getPolicyOperator());
		
		expression = subOperation.getOperands().get(0);
		assertEquals(PolicyExpressionType.LITERAL, expression.getExpressionType());
		assertEquals("false", ((LiteralPolicyExpression<?>)expression).getPolicyValue().getPolicyValue());
	
	}
	
	public void  testBuildExpression_requiredCertField_validateTokens() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		final InputStream stream = IOUtils.toInputStream("X509.TBS.EXTENSION.SubjectKeyIdentifier+ = 1.3.2.3");
		
		Vector<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.parseToTokens(stream);
		
		// now build expressions
		PolicyExpression expression = parser.buildExpression(tokens.iterator());
		
		// check that the expression is an equals
		assertNotNull(expression);
		assertEquals(PolicyExpressionType.OPERATION, expression.getExpressionType());
		OperationPolicyExpression operationExpression = (OperationPolicyExpression)expression;
		assertEquals(PolicyOperator.EQUALS, operationExpression.getPolicyOperator());
		
		// break down the sub operation parameters... should be a literal and an operation
		expression = operationExpression.getOperands().get(0);
		assertEquals(PolicyExpressionType.REFERENCE, expression.getExpressionType());
		assertTrue(expression instanceof SubjectKeyIdentifierExtensionField);
		assertTrue(((SubjectKeyIdentifierExtensionField)expression).isRequired());

		expression = operationExpression.getOperands().get(1);
		assertEquals(PolicyExpressionType.LITERAL, expression.getExpressionType());
		
		stream.close();
	}	
	
	public void testBuildExpression_toManyClosingParenthesis_assertGrammarException() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		final InputStream stream = IOUtils.toInputStream("(2 = 1) != true)");
		
		final Vector<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.parseToTokens(stream);
		
		boolean exceptionOccured = false;
		
		try
		{
			parser.buildExpression(tokens.iterator());
		}
		catch (PolicyGrammarException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
		stream.close();
	}	
	
	public void testBuildExpression_binaryOperation_missingParameter_assertGrammarException() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		final InputStream stream = IOUtils.toInputStream("&& true");
		
		final Vector<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.parseToTokens(stream);
		
		boolean exceptionOccured = false;
		
		try
		{
			parser.buildExpression(tokens.iterator());
		}
		catch (PolicyGrammarException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
		stream.close();
	}	
	
	public void testBuildExpression_operation_missingSingleParameter_assertGrammarException() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		
		final InputStream stream = IOUtils.toInputStream("&&");
		
		final Vector<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.parseToTokens(stream);
		
		boolean exceptionOccured = false;
		
		try
		{
			parser.buildExpression(tokens.iterator());
		}
		catch (PolicyGrammarException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
		
		stream.close();
	}	
	
	public void testBuildExpression_emptyGroup_assertGrammarException() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		final InputStream stream = IOUtils.toInputStream("()");
		
		final Vector<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.parseToTokens(stream);
		
		boolean exceptionOccured = false;
		
		try
		{
			parser.buildExpression(tokens.iterator());
		}
		catch (PolicyGrammarException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
	
	public void testBuildExpression_erroniousExpressionAtEnd_assertGrammarException() throws Exception
	{
		final SimpleTextV1LexiconPolicyParser parser = new SimpleTextV1LexiconPolicyParser();
		final InputStream stream = IOUtils.toInputStream("((d = 1)) +");
		
		final Vector<SimpleTextV1LexiconPolicyParser.TokenTypeAssociation> tokens = parser.parseToTokens(stream);
		
		boolean exceptionOccured = false;
		
		try
		{
			parser.buildExpression(tokens.iterator());
		}
		catch (PolicyGrammarException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}

}
