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
using System.Text;
using Health.Direct.Policy.X509;

namespace Health.Direct.Policy.Impl
{
    public class SimpleTextV1LexiconPolicyParser
    {
        protected static readonly IDictionary<string, TokenType> tokenMap;

        static SimpleTextV1LexiconPolicyParser()
        {
            tokenMap = new Dictionary<string, TokenType>();

            // build the token list
            tokenMap.Add("(", TokenType.START_LEVEL);
            tokenMap.Add(")", TokenType.END_LEVEL);

            // build the operator tokens
            foreach (PolicyOpCode opCode in Enum.GetValues(typeof(PolicyOpCode)))
            {
                tokenMap.Add(opCode.ToString(), TokenType.OPERATOR_EXPRESSION);
            }

            // add the X509Fields
            tokenMap.Add(X509FieldType.SIGNATURE.GetFieldToken(), TokenType.CERTIFICATE_REFERENCE_EXPRESSION);
            tokenMap.Add(X509FieldType.SIGNATURE_ALGORITHM.GetFieldToken(), TokenType.CERTIFICATE_REFERENCE_EXPRESSION);

            // add the TBS fields

        }

     //   /**
     //* Builds a certificate reference expression that is a <
     //* @param token The token used to build the field.
     //* @return An {@link TBSField} object that represents the token.  Returns null if the token does not represent an {@link TBSField}.
     //* @throws PolicyParseException
     //*/


        public SimpleTextV1LexiconPolicyParser()
        {
            //buildLevel = new ThreadLocal<Integer>();
        }


        //TODO: in java this was an internal accessor.  We would need to subclass to unittest.
        /// <summary>
        /// Builds a certificate reference expression that is a <see cref="ITBSField{T}"/>
        /// </summary>
        /// <param name="token"></param>
        /// <returns></returns>
	public IPolicyExpression buildTBSField(String token) 
	{
        ITBSField<String> retVal = null;
		TBSFieldName<String> fieldName = TBSFieldName<String>.FromToken(token);

		if (fieldName != null)
		{
			try
			{
				//Class<? extends TBSField<?>> fieldRefClass = fieldName.GetReferenceClass(token);
                ITBSField<String> fieldRefClass = fieldName.GetReferenceClass(token);
				if (fieldRefClass == null)
					throw new PolicyParseException("TBSField with token name " + token + " has not been implemented yet.");
				
                ////Java
                //if (fieldRefClass.Equals(IssuerAttributeField.class) 
                //        || fieldRefClass.Equals(SubjectAttributeField.class))
                //{
                //    bool required = token.endsWith("+");
					
                //    String rdnLookupToken = (required) ? token.substring(0, token.length() - 1) : token;
					
                //    RDNAttributeIdentifier identifier = RDNAttributeIdentifier.fromName(rdnLookupToken);
                //    retVal = fieldRefClass.equals(IssuerAttributeField.class) ? new IssuerAttributeField(required, identifier) :
                //        new SubjectAttributeField(required, identifier);
                //}
                //else
                //{
                //    retVal = fieldRefClass.newInstance();
                //} 

				if (fieldRefClass.GetType() == typeof(IssuerAttributeField)
                        || fieldRefClass.GetType() == typeof(SubjectAttributeField))
				{
					bool required = token.EndsWith("+");
					String rdnLookupToken = (required) ? token.Substring(0, token.Length - 1) : token;
					
					RDNAttributeIdentifier identifier = RDNAttributeIdentifier.FromName(rdnLookupToken);
                    retVal = fieldRefClass.GetType() == typeof(IssuerAttributeField)
                        ? new IssuerAttributeField(required, identifier) as ITBSField<String> :
                        new SubjectAttributeField(required, identifier) as ITBSField<String>;
				}
				else
				{	
                    //maybe the expression cloning stuff???
                    //Or just send Types around... This is still a port of java and could change a lot.
					//retVal = fieldRefClass.newInstance();
                    retVal = Activator.CreateInstance(fieldRefClass.GetType()) as ITBSField<String>;
				}
			}
			catch (PolicyParseException ex)
			{
				throw;
			}
			catch (Exception e)
			{
				throw new PolicyParseException("Error building TBSField", e);
			}
			
		}
		
		return retVal;
	}
        //TODO: was projected in JAVA which is like intenal... Revisit

        public IList<TokenTypeAssociation> ParseToTokens(Stream stream)
        {
            var tokens = new List<TokenTypeAssociation>();
            try
            {
                BinaryReader reader = new BinaryReader(stream);
                StringWriter writer = new StringWriter();
                bool holdMode;
                TokenType holdType;
                while (reader.PeekChar() != -1)
                {
                    string checkForToken = reader.ReadChar().ToString(CultureInfo.InvariantCulture);
                    TokenType exactMatchToken;
                    tokenMap.TryGetValue("hello", out exactMatchToken);
                }
                
            }
            catch (IOException e)
            {
                throw new PolicyParseException("Error parsing: " + e.Message, e);
            }
            return tokens;
        }

        /// <summary>
        /// Association of a token to a <see cref="TokenType"/>
        /// </summary>
        public class TokenTypeAssociation
        {
            private readonly String token;
            private readonly TokenType type;

            /// <summary>
            /// Constructor
            /// </summary>
            /// <param name="token">The token</param>
            /// <param name="type">The token type</param>
            public TokenTypeAssociation(String token, TokenType type)
            {
                this.token = token;
                this.type = type;
            }

            /// <summary>
            /// Gets the token
            /// </summary>
            /// <returns>The token</returns>
            public String getToken()
            {
                return token;
            }

            /// <summary>
            /// Gets the token type
            /// @return The token type
            /// </summary>
            /// <returns>The token type</returns>
            public TokenType getType()
            {
                return type;
            }

            /// <inheritdoc />
            public String toString()
            {
                StringBuilder builder = new StringBuilder("");
                builder.Append("Token Type: ").Append(type.ToString())
                       .Append("\r\nToken Value: ").Append(token);

                return builder.ToString();
            }
        }
    }
}
