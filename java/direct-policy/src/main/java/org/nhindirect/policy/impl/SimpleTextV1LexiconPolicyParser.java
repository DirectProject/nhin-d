/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
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

package org.nhindirect.policy.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.nhindirect.policy.LiteralPolicyExpressionFactory;
import org.nhindirect.policy.OperationPolicyExpressionFactory;
import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.policy.PolicyExpressionType;
import org.nhindirect.policy.PolicyGrammarException;
import org.nhindirect.policy.PolicyLexiconParser;
import org.nhindirect.policy.PolicyOperator;
import org.nhindirect.policy.PolicyLexicon;
import org.nhindirect.policy.PolicyOperatorParamsType;
import org.nhindirect.policy.PolicyParseException;
import org.nhindirect.policy.x509.ExtensionField;
import org.nhindirect.policy.x509.ExtensionIdentifier;
import org.nhindirect.policy.x509.IssuerAttributeField;
import org.nhindirect.policy.x509.RDNAttributeIdentifier;
import org.nhindirect.policy.x509.SubjectAttributeField;
import org.nhindirect.policy.x509.TBSField;
import org.nhindirect.policy.x509.TBSFieldName;
import org.nhindirect.policy.x509.X509Field;
import org.nhindirect.policy.x509.X509FieldType;

/**
 * Implementation of a {@link PolicyLexiconParser} that parses expressions using the {@link PolicyLexicon#SIMPLE_TEXT_V1} lexicon.  This parser
 * utilizes the XStream XML engine to serialize expressions.
 * <p>
 * The SimpleTextV1Lexicon strategy is to allow expressions to be written in a simple syntax similar to writing an "if" statement.  Expression are 
 * evaluated from left to right executing each operator in the order that it is encountered.  Although there is no precedence of operations
 * in the policy engine, this lexicon allows for expressions to be prioritized and grouped by placing them in parentheses.
 * <p>
 * Error reporting in the parser is limited, but detected grammar errors will throw either a {@link PolicyParseException} or a {@link PolicyGrammarException}.
 * Not all of the certificateReferenceExpression structures are supported yet, but will be available in subsequent releases.  Unsupported structures will
 * throw a {@link PolicyParseException} exception.
 * <p>
 * The following is an EBNF definition of the simple text v1 lexicon.
 * <p>
 * <pre>
 * letter = "A" | "B" | "C" | "D" | "E" | "F" | "G"
 *      | "H" | "I" | "J" | "K" | "L" | "M" | "N"
 *      | "O" | "P" | "Q" | "R" | "S" | "T" | "U"
 *      | "V" | "W" | "X" | "Y" | "Z" 
 *      | "a" | "b" | "c" | "d" | "e" | "f" | "g"
 *      | "h" | "i" | "j" | "k" | "l" | "m" | "n"
 *      | "o" | "p" | "q" | "r" | "s" | "t" | "u" 
 *      | "v" | "w" | "x" | "y" | "z" ;
 *      
 * digit = "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9" ;
 * 
 * symbol = "[" | "]" |  "." | "#" | "%" | "+" ;
 * 
 * unaryOperator = "^" | "{}" | "{}!" | "!" | "@@" ;
 * 
 * binaryOperator = "=" | "!=" |  ">" | "<" | "$" | "{?}" | "{?}!" | "{}$" 
 *      | "{}&" |  "||" | "&&"  | "&" | "|" ;
 *      
 * operator = unaryOperator | binaryOperator
 *      
 * literalExpression = { letter | digit | symbol | white space} ;
 * 
 * rdnAttributeName = "CN" | "C" | "O" | "OU" | "ST" | "L" | "E" | "DC"
 *      | "DNQUALIFIER" | "SERIALNUMBER" | "SN" | "TITLE" | "GIVENNAME"
 *      | "INITIALS" | "PSEUDONYM" | "GERNERAL_QUALIFIER" | "DN" ;
 *      
 * x509Expression = "X509.Algorithm" | "X509.Signature" ;
 * 
 * tbsExpression = "X509.TBS.Version" | "X509.TBS.SerialNumber" 
 *      | "X509.TBS.Signature" | "X509.TBS.Issuer.", rdnAttributeName
 *      | "X509.TBS.Validity.ValidFrom" | "X509.TBS.Validity.ValidTo"
 *      | "X509.TBS.Subject.", rdnAttributeName | X509.TBS.IssuerUniqueID
 *      | "X509.TBS.SubjectUniqueID" | "X509.TBS.SubjectPublicKeyInfo.Algorithm"
 *      | "X509.TBS.SubjectPublicKeyInfo.Size" ;
 *      
 * extensionExpression = "X509.TBS.EXTENSION.KeyUsage" | "X509.TBS.EXTENSION.SubjectAltName"
 *      | "X509.TBS.EXTENSION.SubjectDirectoryAttributes" 
 *      | "X509.TBS.EXTENSION.SubjectKeyIdentifier" | "X509.TBS.EXTENSION.IssuerAltName"
 *      | "X509.TBS.EXTENSION.AuthorityKeyIdentifier.KeyId" 
 *      | "X509.TBS.EXTENSION.AuthorityKeyIdentifier.CertIssuers"
 *      | "X509.TBS.EXTENSION.AuthorityKeyIdentifier.SerialNumber"
 *      | "X509.TBS.EXTENSION.CertificatePolicies.PolicyOIDs"
 *      | "X509.TBS.EXTENSION.CertificatePolicies.CPSUrls"
 *      | "X509.TBS.EXTENSION.PolicyMappings" | "X509.TBS.EXTENSION.BasicConstraints.CA"
 *      | "X509.TBS.EXTENSION.BasicConstraints.MaxPathLength"
 *      | "X509.TBS.EXTENSION.NameConstraints" | "X509.TBS.EXTENSION.PolicyConstraints"
 *      | "X509.TBS.EXTENSION.ExtKeyUsageSyntax" | "X509.TBS.EXTENSION.InhibitAnyPolicy"
 *      | "X509.TBS.EXTENSION.CRLDistributionPoints.FullName" 
 *      | "X509.TBS.EXTENSION.CRLDistributionPoints.RelativeToIssuer" 
 *      | "X509.TBS.EXTENSION.CRLDistributionPoints.Reasons"
 *      | "X509.TBS.EXTENSION.CRLDistributionPoints.CRLIssuer"
 *      | "X509.TBS.EXTENSION.FreshestCRL.FullName"
 *      | "X509.TBS.EXTENSION.FreshestCRL.RelativeToIssuer"
 *      | "X509.TBS.EXTENSION.FreshestCRL.Reasons" 
 *      | "X509.TBS.EXTENSION.FreshestCRL.CRLIssuer"
 *      | "X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.Url"
 *      | "X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.OCSPLocation"
 *      | "X509.TBS.EXTENSION.AuthorityInfoAccessSyntax.AccessMethod"
 *      | "X509.TBS.EXTENSION.SubjectInfoAccessSyntax.Url"
 *      | "X509.TBS.EXTENSION.SubjectInfoAccessSyntax.AccessMethod"
 *      | "X509.TBS.EXTENSION.SubjectInfoAccessSyntax.OCSPLocation" ;
 *      
 *  requiredExpression =  tbsExpression, "+" | extensionExpression, "+" ;
 *  
 *  certificateReferenceExpression = x509Expression | tbsExpression | 
 *      | extensionExpression | requiredExpression ;
 *      
 *  operatorExpression = [{(}] unaryOperator, [{white space}],  (literalExpression | certificateReferenceExpression | operatorExpression) , [{)}] |
 *      [{(}], (literalExpression | certificateReferenceExpression | operatorExpression) ,  [{white space}] , binaryOperator , [{white space}]
 *      (literalExpression | certificateReferenceExpression | operatorExpression) , [{)}] ;
 *      
 *  policyExpression = {operatorExpression}    
 * </pre>
 * <p>
 * <b>Examples</b>
 * <br>
 * <pre>
 * X509.Algorithm = 1.2.840.113549.1.1.11
 * (X509.TBS.EXTENSION.KeyUsage+ = 224) && (X509.Algorithm = 1.2.840.113549.1.1.11)
 * </pre>
 * @author Greg Meyer
 * @since 1.0
 * @see PolicyOperator
 * @see TBSFieldName
 * @see ExtensionIdentifier
 * @see X509FieldType
 * @see RDNAttributeIdentifier
 * @see PublicKeyAlgorithmIdentifier
 * @see SignatureAlgorithmIdentifier
 */
public class SimpleTextV1LexiconPolicyParser extends XMLLexiconPolicyParser
{
	
	protected static final Map<String, TokenType> tokenMap;
	protected static Map<String, PolicyOperator> operatorExpressionMap;
	protected ThreadLocal<Integer> buildLevel;
	
	
	static
	{
		tokenMap = new HashMap<String, TokenType>();
		
		// build the token list
		tokenMap.put("(", TokenType.START_LEVEL);
		tokenMap.put(")", TokenType.END_LEVEL);
		
		// build the operator tokens
		final PolicyOperator[] operators = (PolicyOperator[].class.cast(PolicyOperator.class.getEnumConstants()));
		for (PolicyOperator operator : operators)
			tokenMap.put(operator.getOperatorToken(), TokenType.OPERATOR_EXPRESSION);
		
		// add the X509Fields
		tokenMap.put(X509FieldType.SIGNATURE.getFieldToken(), TokenType.CERTIFICATE_REFERENCE_EXPRESSION);
		tokenMap.put(X509FieldType.SIGNATURE_ALGORITHM.getFieldToken(), TokenType.CERTIFICATE_REFERENCE_EXPRESSION);
		
		// add the TBS fields
		final TBSFieldName[] tbsFieldNames = (TBSFieldName[].class.cast(TBSFieldName.class.getEnumConstants()));
		for (TBSFieldName tbsFieldName : tbsFieldNames)
		{
			if (tbsFieldName != TBSFieldName.EXTENSIONS)
				for (String tokenName : tbsFieldName.getFieldTokens())
					tokenMap.put(tokenName, TokenType.CERTIFICATE_REFERENCE_EXPRESSION);
		}
		
		// add the extension fields
		final ExtensionIdentifier[] extensionFields = (ExtensionIdentifier[].class.cast(ExtensionIdentifier.class.getEnumConstants()));
		for (ExtensionIdentifier extensionField : extensionFields)
		{
			for (String tokenName : extensionField.getFieldTokens())
				tokenMap.put(tokenName, TokenType.CERTIFICATE_REFERENCE_EXPRESSION);
		}
	}
	
	/**
	 * Default constructor
	 */
	public SimpleTextV1LexiconPolicyParser()
	{
		buildLevel = new ThreadLocal<Integer>();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public PolicyExpression parse(InputStream stream) throws PolicyParseException 
	{
		final Vector<TokenTypeAssociation> tokens = parseToTokens(stream);
		
		resetLevel();
		
		final PolicyExpression retExpression = buildExpression(tokens.iterator());
			
		if (getLevel() != 0)
			throw new PolicyGrammarException("Group not closed.");
		
		if (retExpression.getExpressionType() != PolicyExpressionType.OPERATION)
			throw new PolicyGrammarException("Expression must evaluate to an operation");
		
		return retExpression;
	}

	protected PolicyExpression buildExpression(Iterator<TokenTypeAssociation> tokens) throws PolicyParseException 
	{
		return buildExpression(tokens, false);
	}
	
	/**
	 * Builds an aggregated {@link PolicyExpression} from a parsed list of tokens.
	 * @param tokens Parsed list of tokens used to build the {@link PolicyExpression}.
	 * @param level Used for keeping track of depth of operations.
	 * @return A {@link PolicyExpression} built from the parsed list of tokens.
	 * @throws PolicyParseException
	 */
	protected PolicyExpression buildExpression(Iterator<TokenTypeAssociation> tokens, boolean operandFrame) throws PolicyParseException 
	{
		if (!tokens.hasNext())
			return null;
		
		Vector<PolicyExpression> builtOperandExpressions = new Vector<PolicyExpression>();

		do
		{	
			final TokenTypeAssociation assos = tokens.next();
			switch (assos.getType())
			{
				case START_LEVEL:
				{
					incrementLevel();
					final PolicyExpression expression = buildExpression(tokens);
					if (operandFrame)
						return expression;
					
					builtOperandExpressions.add(expression);
					break;
				}
				case END_LEVEL:
					if (getLevel() == 0)
						throw new PolicyGrammarException("To many \")\" tokens.  Delete this token");
					
					if (builtOperandExpressions.size() == 0)
						throw new PolicyGrammarException("Group must contain at least one expression.");
					
					this.decrementLevel();
					return builtOperandExpressions.get(0);
				case OPERATOR_EXPRESSION:
				{
					// get the operator for this token
					final PolicyOperator operator = PolicyOperator.fromToken(assos.getToken());
					
					// regardless if this is a unary or binary expression, then next set of tokens should consist 
					// of a parameter to this operator
					final PolicyExpression subExpression = buildExpression(tokens, true);
					
					if (subExpression == null)
						throw new PolicyGrammarException("Missing parameter.  Operator must be followed by an expression.");
					
					builtOperandExpressions.add(subExpression);
					
					// now add the parameters to the operator
					if (builtOperandExpressions.size() == 1 && operator.getParamsType().equals(PolicyOperatorParamsType.BINARY))
						throw new PolicyGrammarException("Missing parameter.  Binary operators require two parameters.");
					
					final PolicyExpression operatorExpression = OperationPolicyExpressionFactory.getInstance(operator, builtOperandExpressions);
					
					if (operandFrame)
						return operatorExpression;
					
					builtOperandExpressions = new Vector<PolicyExpression>();
					builtOperandExpressions.add(operatorExpression);
					
					break;
				}
				case LITERAL_EXPRESSION:
				{
					final PolicyExpression expression = LiteralPolicyExpressionFactory.getInstance(assos.getToken());
					if (operandFrame)
						return expression;  // exit this operand frame
					
					builtOperandExpressions.add(expression);
					break;
				}
				case CERTIFICATE_REFERENCE_EXPRESSION:
				{
					final PolicyExpression expression = buildCertificateReferenceField(assos.getToken());
					
					if (operandFrame)
						return expression;  // exit this operand frame
					
					builtOperandExpressions.add(expression);
					break;
				}
			}
		} while(tokens.hasNext());
	
		if (builtOperandExpressions.size() > 1)
			throw new PolicyGrammarException("Erroneous expression.");
			
		return builtOperandExpressions.get(0);
	}
	
	protected PolicyExpression buildCertificateReferenceField(String token) throws PolicyParseException 
	{
		PolicyExpression checkObj = buildX509Field(token);
		// check to see if its an X509Field
		if (checkObj == null)
		{
			// check to see if TBSFieldName
			checkObj = buildTBSField(token);
			if (checkObj == null)
			{
				// check for extension field
				checkObj = buildExtensionField(token);
			}
		}
		
		return checkObj;
	}
	
	/**
	 * Builds a certificate reference expression that is an {@link X509Field}.
	 * @param token The token used to build the field.
	 * @return An {@link X509Field} object that represents the token.  Returns null if the token does not represent an {@link X509Field}.
	 * @throws PolicyParseException
	 */
	protected PolicyExpression buildX509Field(String token) throws PolicyParseException 
	{
		X509Field<?> retVal = null;
		final X509FieldType fieldType = X509FieldType.fromToken(token);

		if (fieldType != null)
		{
			try
			{
				Class<? extends X509Field<?>> fieldRefClass = fieldType.getReferenceClass();
				if (fieldRefClass == null)
					throw new PolicyParseException("X509Field with token name " + token + " has not been implemented yet.");
				
				retVal = fieldRefClass.newInstance();
			}
			catch (PolicyParseException ex)
			{
				throw ex;
			}
			///CLOVER:OFF
			catch (Exception e)
			{
				throw new PolicyParseException("Error building X509Field", e);
			}
			///CLOVER:ON
		}
		
		return retVal;
	}
	
	/**
	 * Builds a certificate reference expression that is an {@link TBSField}.
	 * @param token The token used to build the field.
	 * @return An {@link TBSField} object that represents the token.  Returns null if the token does not represent an {@link TBSField}.
	 * @throws PolicyParseException
	 */
	protected PolicyExpression buildTBSField(String token) throws PolicyParseException 
	{
		TBSField<?> retVal = null;
		final TBSFieldName fieldName = TBSFieldName.fromToken(token);

		if (fieldName != null)
		{
			try
			{
				final Class<? extends TBSField<?>> fieldRefClass = fieldName.getReferenceClass(token);
				if (fieldRefClass == null)
					throw new PolicyParseException("TBSField with token name " + token + " has not been implemented yet.");
				
				if (fieldRefClass.equals(IssuerAttributeField.class) || fieldRefClass.equals(SubjectAttributeField.class))
				{
					boolean required = token.endsWith("+");
					
					final String rdnLookupToken = (required) ? token.substring(0, token.length() - 1) : token;
					
					final RDNAttributeIdentifier identifier = RDNAttributeIdentifier.fromName(rdnLookupToken);
					retVal = fieldRefClass.equals(IssuerAttributeField.class) ? new IssuerAttributeField(required, identifier) :
						new SubjectAttributeField(required, identifier);
				}
				else
				{	
					retVal = fieldRefClass.newInstance();
				}
			}
			catch (PolicyParseException ex)
			{
				throw ex;
			}
			///CLOVER:OFF
			catch (Exception e)
			{
				throw new PolicyParseException("Error building TBSField", e);
			}
			///CLOVER:ON
		}
		
		return retVal;
	}
	
	/**
	 * Builds a certificate reference expression that is an {@link ExtensionField}.
	 * @param token The token used to build the field.
	 * @return An {@link ExtensionField} object that represents the token.  Returns null if the token does not represent an {@link ExtensionField}.
	 * @throws PolicyParseException
	 */
	protected PolicyExpression buildExtensionField(String token) throws PolicyParseException 
	{
		ExtensionField<?> retVal = null;
		final ExtensionIdentifier fieldType = ExtensionIdentifier.fromToken(token);

		if (fieldType != null)
		{
			try
			{
				boolean required = token.endsWith("+");
				
				final Class<? extends ExtensionField<?>> fieldRefClass = fieldType.getReferenceClass(token);
				
				if (fieldRefClass == null)
					throw new PolicyParseException("ExtensionField with token name " + token + " has not been implemented yet.");
				
				final Constructor<?> cons = fieldRefClass.getConstructor(Boolean.TYPE);
				retVal = (ExtensionField<?>)cons.newInstance(required);
			}
			catch (PolicyParseException ex)
			{
				throw ex;
			}
			///CLOVER:OFF
			catch (Exception e)
			{
				throw new PolicyParseException("Error building ExtensionField", e);
			}
			///CLOVER:ON
		}
		
		return retVal;
	}
	
	/**
	 * Parses an input stream of the SimpleTextV1 lexicon into a vector of tokens.  Tokens are inserted into the vector
	 * as they encountered in the stream.
	 * @param stream The input stream.
	 * @return An ordered vector of tokens parsed from the input stream.
	 * @throws PolicyParseException
	 */
	protected Vector<TokenTypeAssociation> parseToTokens(InputStream stream) throws PolicyParseException 
	{
		final Vector<TokenTypeAssociation> tokens = new Vector<TokenTypeAssociation>();
		
		try
		{
			final InputStreamReader isr = new InputStreamReader(stream);
			StringWriter writer = new StringWriter();
			boolean holdMode = false;
			TokenType holdType = null;
			
			for (int i; (i = isr.read()) > 0; ) 
			{
				writer.write(i);
				final String checkForTokenString = writer.toString();
				
				// check to see if we have an exact match to a token
				TokenType exactMatchToken = tokenMap.get(checkForTokenString);
				
				if (exactMatchToken != null)
				{
					// if the token is an operator, we need to keep looking forward to the next
					// character because some operators are made up of the exact same characters.. we
					// may have a partial string of an operator with more characters
					if (exactMatchToken == TokenType.OPERATOR_EXPRESSION || exactMatchToken == TokenType.CERTIFICATE_REFERENCE_EXPRESSION)
					{
						holdType = exactMatchToken;
						// go into hold mode so we can check the next character
						holdMode = true;
					}
					else
					{
						// not an operator, so go ahead and mark it as a complete token
						tokens.add(new TokenTypeAssociation(checkForTokenString, exactMatchToken));
						writer = new StringWriter();
					}
				}
				else
				{
					if (holdMode)
					{
						// we know that the checkForTokenString is comprised of an exact match at the beginning of the string
						// up to the last character in the string
						// break the string into the known token and the start of the next token
						final String operatorToken = checkForTokenString.substring(0, (checkForTokenString.length() - 1));
						final String nextToken = checkForTokenString.substring((checkForTokenString.length() - 1));
						
						// add the token to the token vector
						tokens.add(new TokenTypeAssociation(operatorToken, holdType));
						
						// reset the writer
						writer = new StringWriter();
						
						// check if the nextToken string matches a string

						exactMatchToken = tokenMap.get(nextToken);
						if (exactMatchToken != null)
						{							
								
							tokens.add(new TokenTypeAssociation(nextToken, exactMatchToken));
						}
						else
							writer.write(nextToken); // not a reserved token, so queue up the nextToken to continue on with the parsing
						
						holdMode = false;
						holdType = null;
					}
					else
					{
						// we didn't hit an exact match, but the new character we hit may be a reserved token
						// check to see if the checkForTokenString now contains a reserved token
						for (String key : tokenMap.keySet())
						{
							int idx = checkForTokenString.indexOf(key);
							if (idx >= 0)
							{
								// found one... need to break the string into a literal and the new token
								final String firstToken = checkForTokenString.substring(0, idx).trim();
								final String secondToken = checkForTokenString.substring(idx).trim(); 
								
								// if the first token is all white space, don't add it as a token
								if (!firstToken.isEmpty())
									tokens.add(new TokenTypeAssociation(firstToken, TokenType.LITERAL_EXPRESSION));

								// reset the writer
								writer = new StringWriter();
								
								// check if the second token (which we know is a reserved token)
								// is an operator... 
								exactMatchToken = tokenMap.get(secondToken);
								
								if (exactMatchToken == TokenType.OPERATOR_EXPRESSION || exactMatchToken == TokenType.CERTIFICATE_REFERENCE_EXPRESSION)
								{
									// go into hold mode
									holdMode = true;
									holdType = exactMatchToken;
									writer.write(secondToken);
								}
								else
								{
									// the token is not an operator, so add it the token vector
									tokens.add(new TokenTypeAssociation(secondToken, exactMatchToken));
								}
								break;
							}
						}
					}
				}
		    }
			
			// now that we have completed traversing the expression lexicon, if there is anything left over in the writer then
			// add it as a token
			final String remainingString = writer.toString().trim();
			if (!remainingString.isEmpty())
			{
				final TokenType exactMatchToken = tokenMap.get(remainingString);
				final TokenType addTokenType = (exactMatchToken != null) ? exactMatchToken : TokenType.LITERAL_EXPRESSION;
				tokens.add(new TokenTypeAssociation(remainingString, addTokenType));
			}
		}
		catch (IOException e)
		{
			throw new PolicyParseException("Error parsing: " + e.getMessage(), e);
		}
		
		return tokens;
	}
	
	
	
	/**
	 * Enumeration of token types.
	 * @author gm2552
	 * @since 1.0
	 */
	protected static enum TokenType
	{
		/**
		 * Start of a new depth level of an expression
		 */
		START_LEVEL,
		
		/**
		 * End of a depth level of an expression
		 */
		END_LEVEL,
		
		/**
		 * An operator expression
		 */
		OPERATOR_EXPRESSION,
		
		/**
		 * A literal expression
		 */
		LITERAL_EXPRESSION,
		
		/**
		 * A certificate reference expression
		 */
		CERTIFICATE_REFERENCE_EXPRESSION;
	}
	
	protected Integer resetLevel()
	{
		Integer level = buildLevel.get();
		
		level = (level == null) ? level = Integer.valueOf(0) : 0;
		buildLevel.set(level);
		
		return level;
	}
	
	protected Integer getLevel()
	{
		Integer level = buildLevel.get();
		if (level == null)
			level = resetLevel();
		
		return level;
	}
	
	protected Integer incrementLevel()
	{
		Integer level = getLevel();
		
		++level;
		buildLevel.set(level);
		
		return level;
	}
	
	protected Integer decrementLevel()
	{
		Integer level = getLevel();
		
		--level;
		buildLevel.set(level);
		
		return level;
	}
	
	/**
	 * Association of a token to a {@link TokenType}
	 * @author gm2552
	 * @since 1.0
	 */
	protected static class TokenTypeAssociation
	{
		protected final String token;
		protected final TokenType type;
		
		/**
		 * Constructor
		 * @param token The token
		 * @param type The token type
		 */
		public TokenTypeAssociation(String token, TokenType type)
		{
			this.token = token;
			this.type = type;
		}
		
		/**
		 * Gets the token
		 * @return The token
		 */
		public String getToken()
		{
			return token;
		}
		
		/**
		 * Gets the token type
		 * @return The token type
		 */
		public TokenType getType()
		{
			return type;
		}
		
		/**
		 * {@inheritDoc}
		 */
		public String toString()
		{
			final StringBuilder builder = new StringBuilder("");
			builder.append("Token Type: " ).append(type.toString())
			   .append("\r\nToken Value: ").append(token);
			
			return builder.toString();
		}
	}
}
