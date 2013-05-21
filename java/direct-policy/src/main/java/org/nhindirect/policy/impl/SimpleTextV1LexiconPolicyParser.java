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
import org.nhindirect.policy.PolicyLexiconParser;
import org.nhindirect.policy.PolicyOperator;
import org.nhindirect.policy.PolicyLexicon;
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
 * @author gm2552
 * @since 1.0
 */
public class SimpleTextV1LexiconPolicyParser extends XMLLexiconPolicyParser
{
	
	protected static Map<String, TokenType> tokenMap;
	protected static Map<String, PolicyOperator> operatorExpressionMap;
	
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
		
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public PolicyExpression parse(InputStream stream) throws PolicyParseException 
	{
		final Vector<TokenTypeAssociation> tokens = parseToTokens(stream);
		
		final PolicyExpression retExpression = buildExpression(tokens.iterator(), 0);
		
		return retExpression;
	}
	
	/**
	 * Builds an aggregated {@link PolicyExpression} from a parsed list of tokens.
	 * @param tokens Parsed list of tokens used to build the {@link PolicyExpression}.
	 * @param level Used for keeping track of depth of operations.
	 * @return A {@link PolicyExpression} built from the parsed list of tokens.
	 * @throws PolicyParseException
	 */
	protected PolicyExpression buildExpression(Iterator<TokenTypeAssociation> tokens, final int level) throws PolicyParseException 
	{
		Vector<PolicyExpression> builtOperandExpressions = new Vector<PolicyExpression>();
		
		do
		{
			TokenTypeAssociation assos = tokens.next();
			switch (assos.getType())
			{
				case START_LEVEL:
					builtOperandExpressions.add(buildExpression(tokens, (level + 1)));
					break;
				case END_LEVEL:
					return builtOperandExpressions.get(0);
				case OPERATOR_EXPRESSION:
				{
					// get the operator for this token
					final PolicyOperator operator = PolicyOperator.fromToken(assos.getToken());
					
					// regardless if this is a unary or binary expression, then next set of tokens should consist 
					// of a parameter to this operator
					builtOperandExpressions.add(buildExpression(tokens, level));
					
					// now add the parameters to the operator
					final PolicyExpression operatorExpression = OperationPolicyExpressionFactory.getInstance(operator, builtOperandExpressions);
					builtOperandExpressions = new Vector<PolicyExpression>();
					builtOperandExpressions.add(operatorExpression);
					
					break;
				}
				case LITERAL_EXPRESSION:
					builtOperandExpressions.add(LiteralPolicyExpressionFactory.getInstance(assos.getToken()));
					break;
				case CERTIFICATE_REFERENCE_EXPRESSION:
				{
					PolicyExpression checkObj = buildX509Field(assos.getToken());
					// check to see if its an X509Field
					if (checkObj == null)
					{
						// check to see if TBSFieldName
						checkObj = buildTBSField(assos.getToken());
						if (checkObj == null)
						{
							// check for extension field
							checkObj = buildExtensionField(assos.getToken());
						}
					}
					
					if (checkObj != null)
						builtOperandExpressions.add(checkObj);
					
					break;
				}
			}
		} while(tokens.hasNext());
		
		return builtOperandExpressions.get(0);
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
				retVal = fieldType.getReferenceClass().newInstance();
			}
			catch (Exception e)
			{
				throw new PolicyParseException("Error building X509Field", e);
			}
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
					final RDNAttributeIdentifier identifier = RDNAttributeIdentifier.fromName(token);
					retVal = fieldRefClass.equals(IssuerAttributeField.class) ? new IssuerAttributeField(false, identifier) :
						new SubjectAttributeField(false, identifier);
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
			catch (Exception e)
			{
				throw new PolicyParseException("Error building TBSField", e);
			}
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
				final Class<? extends ExtensionField<?>> fieldRefClass = fieldType.getReferenceClass(token);
				
				if (fieldRefClass == null)
					throw new PolicyParseException("ExtensionField with token name " + token + " has not been implemented yet.");
				
				final Constructor<?> cons = fieldRefClass.getConstructor(Boolean.TYPE);
				retVal = (ExtensionField<?>)cons.newInstance(Boolean.FALSE);
			}
			catch (PolicyParseException ex)
			{
				throw ex;
			}
			catch (Exception e)
			{
				throw new PolicyParseException("Error building ExtensionField", e);
			}
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
					if (exactMatchToken == TokenType.OPERATOR_EXPRESSION)
					{
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
						// break the string into the operator and the start of the next token
						final String operatorToken = checkForTokenString.substring(0, (checkForTokenString.length() - 1));
						final String nextToken = checkForTokenString.substring((checkForTokenString.length() - 1));
						
						// add the operator token to the token vector
						tokens.add(new TokenTypeAssociation(operatorToken, TokenType.OPERATOR_EXPRESSION));
						
						// reset the writer
						writer = new StringWriter();
						
						// check if the nextToken string matches a string

						exactMatchToken = tokenMap.get(nextToken);
						if (exactMatchToken != null)
						{
							// our grammar does not allow for two subsequent operators
							// so assume this token is a non operator
							tokens.add(new TokenTypeAssociation(nextToken, exactMatchToken));
						}
						else
							writer.write(nextToken); // not a reserved token, so queue up the nextToken to continue on with the parsing
						
						holdMode = false;
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
								
								if (exactMatchToken == TokenType.OPERATOR_EXPRESSION)
								{
									// go into hold mode
									holdMode = true;
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
		 * @param type The toke type
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
	}
}
