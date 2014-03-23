/* 
 Copyright (c) 2013, Direct Project
 All rights reserved.

 Authors:
    Joe Shook      jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading;
using Health.Direct.Common.Policies;
using Health.Direct.Policy.Extensions;
using Health.Direct.Policy.OpCode;
using Health.Direct.Policy.Operators;
using Health.Direct.Policy.X509;
using Health.Direct.Policy.X509.Standard;

namespace Health.Direct.Policy.Impl
{
    public class SimpleTextV1LexiconPolicyParser : XMLLexiconPolicyParser
    {
        static readonly IDictionary<string, TokenType> m_tokenMap;

        readonly ThreadLocal<int> m_buildLevel;

        static SimpleTextV1LexiconPolicyParser()
        {
            m_tokenMap = new Dictionary<string, TokenType>();

            // build the token list
            m_tokenMap.Add("(", TokenType.START_LEVEL);
            m_tokenMap.Add(")", TokenType.END_LEVEL);

            // build the operator tokens
            foreach (Code opCode in Code.Map)
            {
                m_tokenMap.Add(opCode.Token, opCode.OpCodeType == OpCodeType.Binary ? TokenType.OPERATOR_BINARY_EXPRESSION :  TokenType.OPERATOR_UNARY_EXPRESSION);
            }

            // add the X509Fields
            m_tokenMap.Add(X509FieldType.Signature.GetFieldToken(), TokenType.CERTIFICATE_REFERENCE_EXPRESSION);
            m_tokenMap.Add(X509FieldType.SignatureAlgorithm.GetFieldToken(), TokenType.CERTIFICATE_REFERENCE_EXPRESSION);

            // add the TBS Single fields
            foreach (TBSFieldStandard.ISingle field in TBSFieldStandard.Field.Map.FindAll(f => f is TBSFieldStandard.ISingle))
            {
                foreach (var rfcName in field.GetFieldTokens())
                {
                    m_tokenMap.Add(rfcName, TokenType.CERTIFICATE_REFERENCE_EXPRESSION);
                }
            }

            // add the TBS Complex fields
            foreach (TBSFieldStandard.IComplex field in TBSFieldStandard.Field.Map.FindAll(f => f is TBSFieldStandard.IComplex))
            {
                if (field.RfcName != TBSFieldName.Extenstions.RfcName)
                    foreach (var rfcName in field.GetFieldTokens())
                    {
                        m_tokenMap.Add(rfcName, TokenType.CERTIFICATE_REFERENCE_EXPRESSION);
                    }
            }


            
		    // add the extension fields
		    foreach (var identifier in ExtensionIdentifier.TokenFieldMap)
            {
                m_tokenMap.Add(identifier.Key, TokenType.CERTIFICATE_REFERENCE_EXPRESSION);
            }

        }

        //   /**
        //* Builds a certificate reference expression that is a <
        //* @param token The token used to build the field.
        //* @return An {@link TBSField} object that represents the token.  Returns null if the token does not represent an {@link TBSField}.
        //* @throws PolicyParseException
        //*/


        public SimpleTextV1LexiconPolicyParser()
        {
            m_buildLevel = new ThreadLocal<int>();
        }
        
        public override IPolicyExpression Parse(Stream stream) 
	    {
		    IList<TokenTypeAssociation> tokens = ParseToTokens(stream);
		
		    ResetLevel();
		
		    IPolicyExpression retExpression = BuildExpression(tokens.GetEnumerator());
			
		    if (GetLevel() != 0)
			    throw new PolicyGrammarException("Group not closed.");
		
		    if (retExpression.GetExpressionType() != PolicyExpressionType.OPERATION)
			    throw new PolicyGrammarException("Expression must evaluate to an operation");
		
		    return retExpression;
	    }

        protected IPolicyExpression BuildExpression(IEnumerator<TokenTypeAssociation> tokens) 
	    {
            return BuildExpression(tokens, false);
	    }


        /**
	     * Builds an aggregated {@link PolicyExpression} from a parsed list of tokens.
	     * @param tokens Parsed list of tokens used to build the {@link PolicyExpression}.
	     * @param level Used for keeping track of depth of operations.
	     * @return A {@link PolicyExpression} built from the parsed list of tokens.
	     * @throws PolicyParseException
	     */
	    protected IPolicyExpression BuildExpression(IEnumerator<TokenTypeAssociation> tokens, bool operandFrame)
	    {
	        if (!tokens.MoveNext())
	        {
	            return null;
	        }

		    IList<IPolicyExpression> builtOperandExpressions = new List<IPolicyExpression>();

	        do
	        {
			    TokenTypeAssociation assos = tokens.Current;
			    switch (assos.GetTokenType())
			    {
				    case TokenType.START_LEVEL:
				    {
					    IncrementLevel();
					    IPolicyExpression expression = BuildExpression(tokens);
					    if (operandFrame)
						    return expression;
					
					    builtOperandExpressions.Add(expression);
					    break;
				    }
				    case TokenType.END_LEVEL:
					    if (GetLevel() == 0)
						    throw new PolicyGrammarException("To many \")\" tokens.  Delete this token");
					
					    if (builtOperandExpressions.Count == 0)
						    throw new PolicyGrammarException("Group must contain at least one expression.");
					
					    DecrementLevel();
					    return builtOperandExpressions[0];
				    case TokenType.OPERATOR_BINARY_EXPRESSION:
                    case TokenType.OPERATOR_UNARY_EXPRESSION:
				    {
					    // regardless if this is a unary or binary expression, then next set of tokens should consist 
					    // of a parameter to this operator
					    IPolicyExpression subExpression = BuildExpression(tokens, true);

                        int tokenHashCode = 0;

				        if (subExpression != null)
				        {
                            //TODO Refactor
				            if (assos.GetTokenType() == TokenType.OPERATOR_UNARY_EXPRESSION)
				            {
                                tokenHashCode = (assos.GetToken() + "_" + GetOperandType(subExpression)).GetHashCode();
				            }
                            if (assos.GetTokenType() == TokenType.OPERATOR_BINARY_EXPRESSION)
                            {
                                string leftOperandType = GetOperandType(builtOperandExpressions.First());
                                string rightOperandType = GetOperandType(subExpression);
                                tokenHashCode = (assos.GetToken() + "_" + leftOperandType + "_" + rightOperandType).GetHashCode();
                            }

				        }
                        else //(subExpression == null)
				        {
                            throw new PolicyGrammarException("Missing parameter.  Operator must be followed by an expression.");
				        }

					    builtOperandExpressions.Add(subExpression);

				        

                        // get the operator for this token
                        OperatorBase operatorBase = PolicyOperator.FromToken(tokenHashCode);

					    // now add the parameters to the operator
                        if (builtOperandExpressions.Count == 1 && operatorBase is BinaryOperator)
						    throw new PolicyGrammarException("Missing parameter.  Binary operators require two parameters.");

                        IPolicyExpression operatorExpression = new OperationPolicyExpression(operatorBase, builtOperandExpressions);
					
					    if (operandFrame)
						    return operatorExpression;
					
					    builtOperandExpressions = new List<IPolicyExpression>();
					    builtOperandExpressions.Add(operatorExpression);
					
					    break;
				    }
				    case TokenType.LITERAL_EXPRESSION:
				    {
					    IPolicyExpression expression = new LiteralPolicyExpression<string>(new PolicyValue<string>(assos.GetToken()));
					    if (operandFrame)
						    return expression;  // exit this operand frame
					
					    builtOperandExpressions.Add(expression);
					    break;
				    }
				    case TokenType.CERTIFICATE_REFERENCE_EXPRESSION:
				    {
                        IPolicyExpression expression = BuildCertificateReferenceField(assos.GetToken());
					
					    if (operandFrame)
						    return expression;  // exit this operand frame

                        builtOperandExpressions.Add(expression);
					    break;
				    }
			    }
		    } while(tokens.MoveNext());
	
		    if (builtOperandExpressions.Count > 1)
			    throw new PolicyGrammarException("Erroneous expression.");
			
		    return builtOperandExpressions[0];
	    }

        private string GetOperandType(IPolicyExpression policyExpression)
        {
            try
            {
                IEnumerable<Type> genericInterfaces =
                    policyExpression.GetType().GetInterfaces().Where(i => i.GenericTypeArguments.Any()).ToArray();

                // IPolicyExpression is a IOperationPolicyExpression
                if (!genericInterfaces.Any())
                {
                    var policyOpertor = policyExpression as IOperationPolicyExpression;
                    if (policyOpertor == null)
                    {
                        return null;
                    }
                    var returnName = policyOpertor.GetPolicyOperator().ExecuteRef.Method.ReturnType.Name;
                    return returnName;
                }
                //if (genericInterfaces //Looing for ITBSField<IEnumerable<String>>
                //        .Any(i => i.GetGenericTypeDefinition() == typeof(ITBSField<>) &&  i.GetGenericArguments()
                //            .Any(a => a.GetInterfaces()
                //                .Any(ai => ai.IsGenericType && ai.GetGenericTypeDefinition() == typeof(IEnumerable<>)))))
                //{
                //    return "String";
                //}
                var rdnType = genericInterfaces.FirstOrDefault(i => i.GetGenericTypeDefinition() == typeof (IRdn<>));
                if(rdnType != null)
                {
                    return rdnType.GetGenericArguments().First().Name;
                }
                var args = genericInterfaces.First().GetGenericArguments();
                return args[0].Name;
            }
            catch (Exception ex)
            {
                throw new PolicyParseException("Error getting operand type for: " + policyExpression, ex);
            }
        }

        protected IPolicyExpression BuildCertificateReferenceField(String token) //throws PolicyParseException 
	    {
		    IPolicyExpression checkObj = BuildX509Field(token);
		    // check to see if its an X509Field
		    if (checkObj == null)
		    {
			    // check to see if TBSFieldName
			    checkObj = BuildTBSField(token);
			    if (checkObj == null)
			    {
				    // check for extension field
				    checkObj = BuildExtensionField(token);
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
        public X509Field<string> BuildX509Field(String token) //throws PolicyParseException 
	    {
		    X509Field<string> retVal = null;
		    X509FieldType fieldType = X509FieldType.FromToken(token);

		    if (fieldType != null)
		    {
			    try
			    {
                    retVal = fieldType.GetReferenceClass();
                    if (retVal == null)
					    throw new PolicyParseException("X509Field with token name " + token + " has not been implemented yet.");
			    }
			    catch (PolicyParseException)
			    {
				    throw;
			    }
			    
			    catch (Exception e)
			    {
				    throw new PolicyParseException("Error building X509Field", e);
			    }
		    }
		    return retVal;
	    }


        /// <summary>
        /// Builds a certificate reference expression that is a <see cref="ITBSField{T}"/>
        /// </summary>
        /// <param name="token"></param>
        /// <returns></returns>
        public IPolicyExpression BuildTBSField(String token) //throws PolicyParseException 
        {
            dynamic retVal = null;
            TBSFieldName fieldName = TBSFieldName.FromToken(token);

            if (fieldName != null)
            {
                try
                {
                    dynamic  fieldRefClass = fieldName.GetReferenceClass(token);
                    if (fieldRefClass == null)
                        throw new PolicyParseException("TBSField with token name " + token + " has not been implemented yet.");
                    

                    if (fieldRefClass.GetType() == typeof(IssuerAttributeField)
                            || fieldRefClass.GetType() == typeof(SubjectAttributeField))
                    {
                        bool required = token.EndsWith("+");
                        String rdnLookupToken = (required) ? token.Substring(0, token.Length - 1) : token;

                        RDNAttributeIdentifier identifier = RDNAttributeIdentifier.FromName(rdnLookupToken);
                        retVal = fieldRefClass.GetType() == typeof(IssuerAttributeField)
                            ? new IssuerAttributeField(required, identifier)  :
                            new SubjectAttributeField(required, identifier) ;
                    }
                    else
                    {
                        retVal = fieldRefClass as IPolicyExpression;
                    }
                }
                catch (PolicyParseException)
                {
                    throw;
                }
                catch (Exception e)
                {
                    throw new PolicyParseException("Error building TBSField: " + token, e);
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
        public IPolicyExpression BuildExtensionField(String token) //throws PolicyParseException 
	    {
            IPolicyExpression retVal = null;
		    ExtensionIdentifier fieldType = ExtensionIdentifier.FromToken(token);
            bool required = token.EndsWith("+");
		    if (fieldType != null)
		    {
			    try
			    {
				    Type retType = fieldType.GetReferenceClass(token, required);
                    if (retType == null)
			        {
			            throw new PolicyParseException("ExtensionField with token name " + token + " has not been implemented yet.");
			        }
                    var ctor = retType.Ctor<bool, IPolicyExpression>();
			        retVal = ctor(required);
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
	



        /// <summary>
        /// Parses an input stream of the SimpleTextV1 lexicon into a list of tokens.  Tokens are inserted into the list
        /// as they encountered in the stream.
        /// <param name="stream">Input stream</param>
        /// <returns>An ordered list of tokens parsed from the input stream.</returns>
        /// <exception cref="PolicyParseException"></exception>
        /// </summary>
        public IList<TokenTypeAssociation> ParseToTokens(Stream stream)
        {
            var tokens = new List<TokenTypeAssociation>();
            try
            {
                BinaryReader reader = new BinaryReader(stream);
                StringWriter writer = new StringWriter();
                bool holdMode = false;
                TokenType? holdType = null;
                while (reader.PeekChar() != -1)
                {
                    string inputChar = reader.ReadChar().ToString(CultureInfo.InvariantCulture);
                    writer.Write(inputChar);
                    String checkForTokenString = writer.ToString();

                    TokenType exactMatchToken;
                    bool tokenMatch = m_tokenMap.TryGetValue(checkForTokenString, out exactMatchToken);

                    if (tokenMatch)
                    {
                        // if the token is an operator, we need to keep looking forward to the next
                        // character because some operators are made up of the exact same characters.. we
                        // may have a partial string of an operator with more characters
                        if (reader.PeekChar() != -1 
                            &&
                            (exactMatchToken == TokenType.OPERATOR_BINARY_EXPRESSION
                            || exactMatchToken == TokenType.OPERATOR_UNARY_EXPRESSION
                            || exactMatchToken == TokenType.CERTIFICATE_REFERENCE_EXPRESSION))
                        {
                            holdType = exactMatchToken;
                            // go into hold mode so we can check the next character
                            holdMode = true;
                        }
                        else
                        {
                            // not an operator, so go ahead and mark it as a complete token
                            tokens.Add(new TokenTypeAssociation(checkForTokenString, exactMatchToken));
                            writer = new StringWriter();
                        }
                    }
                    else if (holdMode)
					{
						// we know that the checkForTokenString is comprised of an exact match at the beginning of the string
						// up to the last character in the string
						// break the string into the known token and the start of the next token
						String operatorToken = checkForTokenString.Substring(0, (checkForTokenString.Length - 1));
						String nextToken = checkForTokenString.Substring((checkForTokenString.Length - 1));
						
						// add the token to the token list
						tokens.Add(new TokenTypeAssociation(operatorToken, holdType));
						
						// reset the writer
						writer = new StringWriter();
						
						// check if the nextToken string matches a string

						tokenMatch = m_tokenMap.TryGetValue(nextToken, out exactMatchToken);
						if (tokenMatch)
						{		
							tokens.Add(new TokenTypeAssociation(nextToken, exactMatchToken));
						}
						else
							writer.Write(nextToken); // not a reserved token, so queue up the nextToken to continue on with the parsing
						
						holdMode = false;
						holdType = null;
					}
					else
					{
						// we didn't hit an exact match, but the new character we hit may be a reserved token
						// check to see if the checkForTokenString now contains a reserved token
					    foreach (var key in m_tokenMap.Keys)
					    {
					        int idx = checkForTokenString.IndexOf(key, StringComparison.Ordinal);
							if (idx >= 0)
							{
								// found one... need to break the string into a literal and the new token
								String firstToken = checkForTokenString.Substring(0, idx).Trim();
								String secondToken = checkForTokenString.Substring(idx).Trim(); 
								
								// if the first token is all white space, don't add it as a token
								if (!String.IsNullOrEmpty(firstToken))
									tokens.Add(new TokenTypeAssociation(firstToken, TokenType.LITERAL_EXPRESSION));

								// reset the writer
								writer = new StringWriter();
								
								// check if the second token (which we know is a reserved token)
								// is an operator... 
                                tokenMatch = m_tokenMap.TryGetValue(secondToken, out exactMatchToken);
								
								if (tokenMatch &&
                                    (exactMatchToken == TokenType.OPERATOR_BINARY_EXPRESSION
                                    || exactMatchToken == TokenType.OPERATOR_UNARY_EXPRESSION 
                                    || exactMatchToken == TokenType.CERTIFICATE_REFERENCE_EXPRESSION))
								{
									// go into hold mode
									holdMode = true;
									holdType = exactMatchToken;
									writer.Write(secondToken);
								}
								else
								{
									// the token is not an operator, so add it the token list
									tokens.Add(new TokenTypeAssociation(secondToken, exactMatchToken));
								}
								break;
							}
						}
					}
				}
                
                // now that we have completed traversing the expression lexicon, if there is anything left over in the writer
			    // add it as a token
			    String remainingString = writer.ToString().Trim();
			    if (!string.IsNullOrEmpty(remainingString))
			    {
                    TokenType exactMatchToken;
                    bool tokenMatch = m_tokenMap.TryGetValue(remainingString, out exactMatchToken);
                    TokenType addTokenType = (tokenMatch) ? exactMatchToken : TokenType.LITERAL_EXPRESSION;
				    tokens.Add(new TokenTypeAssociation(remainingString, addTokenType));
			    }
            }
            catch (IOException e)
            {
                throw new PolicyParseException("Error parsing: " + e.Message, e);
            }
            return tokens;
        }

        protected int ResetLevel()
        {
            m_buildLevel.Value = 0;
            return m_buildLevel.Value;
        }

        protected int GetLevel()
        {
            return m_buildLevel.Value;
        }

        protected int IncrementLevel()
        {
            m_buildLevel.Value++;
            return m_buildLevel.Value;
        }

        protected int DecrementLevel()
        {
            m_buildLevel.Value--;
            return m_buildLevel.Value;
        }
	

        /// <summary>
        /// Association of a token to a <see cref="TokenType"/>
        /// </summary>
        public class TokenTypeAssociation
        {
            private readonly String m_token;
            private readonly TokenType? m_type;

            /// <summary>
            /// Constructor
            /// </summary>
            /// <param name="token">The token</param>
            /// <param name="type">The token type</param>
            public TokenTypeAssociation(String token, TokenType? type)
            {
                this.m_token = token;
                this.m_type = type;
            }

            /// <summary>
            /// Gets the token
            /// </summary>
            /// <returns>The token</returns>
            public String GetToken()
            {
                return m_token;
            }

            /// <summary>
            /// Gets the token type
            /// @return The token type
            /// </summary>
            /// <returns>The token type</returns>
            public TokenType? GetTokenType()
            {
                return m_type;
            }

            /// <inheritdoc />
            public override String ToString()
            {
                StringBuilder builder = new StringBuilder("");
                builder.Append("Token Type: ").Append(m_type.ToString())
                       .Append("\r\nToken Value: ").Append(m_token);

                return builder.ToString();
            }
        }
    }

    public class PolicyGrammarException : Exception
    {
        public PolicyGrammarException()
        {
        }

        public PolicyGrammarException(String msg):base(msg){}

        public PolicyGrammarException(string msg, Exception ex) : base(msg, ex) { }
    }
}
