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
using System.Linq;


namespace Health.Direct.Policy.X509
{
    public class TBSFieldName
    {
        /// <summary>
        /// Certificate version
        /// </summary>
        public static readonly TBSFieldName Version = new TBSFieldName(
            new TBSFieldStandard.Version(new List<TBSFieldStandard.AttributeReferenceClass>()));
                                                                       
        /// <summary>
        /// Certificate serial number
        /// </summary>
        public static readonly TBSFieldName SerialNumber = new TBSFieldName (
            new TBSFieldStandard.SerialNumber(new SerialNumberAttributeField()));
        
        /// <summary>
        /// Certificate signature algorithm
        /// </summary>
        public static readonly TBSFieldName Signature = new TBSFieldName(
            new TBSFieldStandard.Signature(new List<TBSFieldStandard.AttributeReferenceClass>()));


        //TODO: need delegates to insert the constructor params at run time. For both Issuer and Subject
        //Both hard coded for now.
        // So lots of temp code to get compiling...

        /// <summary>
        /// Distinguished name of certificate signer
        /// </summary>
        /// <param name="?"></param>
        public static readonly TBSFieldName Issuer = new TBSFieldName(
            new TBSFieldStandard.Issuer(TBSFieldStandard.RdnsToReferenceClass(rdn => new IssuerAttributeField(false, rdn))));

        /// <summary>
        /// Certificate valid to and valid from dates
        /// </summary>
        public static readonly TBSFieldName Validity = new TBSFieldName(
            new TBSFieldStandard.Validity(new List<TBSFieldStandard.AttributeReferenceClass>
            {
                new TBSFieldStandard.AttributeReferenceClass("ValidFrom", () => null),
                new TBSFieldStandard.AttributeReferenceClass("ValidTo", () => null)
            }));

        /// <summary>
        /// Distinguished name of certificate signer
        /// </summary>
        /// <param name="?"></param>
        public static readonly TBSFieldName Subject = new TBSFieldName(
            new TBSFieldStandard.Subject(TBSFieldStandard.RdnsToReferenceClass(rdn => new SubjectAttributeField(false, rdn))));


        /// <summary>
        /// Certificate extension fields
        /// </summary>
        public static readonly TBSFieldName Extenstions = new TBSFieldName(
            new TBSFieldStandard.Extensions(new List<TBSFieldStandard.AttributeReferenceClass>()) );


        public static readonly TBSFieldName SubjectPublicKeyInfo = new TBSFieldName(
            new TBSFieldStandard.SubjectPublicKeyInfo(
                new List<TBSFieldStandard.AttributeReferenceClass>
            {
                new TBSFieldStandard.AttributeReferenceClass("Algorithm", rdn => new SubjectAttributeField(false, rdn)),
                new TBSFieldStandard.AttributeReferenceClass("Size",rdn => new SubjectAttributeField(false, rdn))
            }));

        public static IEnumerable<TBSFieldName> Values
        {
            get
            {
                yield return Version;
                yield return SerialNumber;
                yield return Signature;
                yield return Issuer ;
                yield return Subject;
                yield return Extenstions;
            }
        }


        readonly TBSFieldStandard.IField tbsField;
        readonly ITBSField<string> m_referenceClass;
        readonly IList<TBSFieldStandard.AttributeReferenceClass> m_subAttributes;

	    //Class<? extends TBSField<?>> referenceClass;
        //ITBSField<Object> m_referenceClass;
        //Type m_referenceClass;

	    static readonly Dictionary<String, TBSFieldName> TokenFieldMap;

        private TBSFieldName(TBSFieldStandard.ISingle field)
        {
            tbsField = field;
            m_referenceClass = field.ReferenceClass;
        }

        private TBSFieldName(TBSFieldStandard.Complex field)
	    {
            tbsField = field;
            m_subAttributes = field.SubAttributes;
	    }

       

        static TBSFieldName()
        {
            Console.WriteLine("hello:: " );
            TokenFieldMap = new Dictionary<String, TBSFieldName>();

            foreach (var tbsFieldName in Values)
            {
                foreach (string fieldToken in tbsFieldName.GetFieldTokens())
                {
                    TokenFieldMap.Add(fieldToken, tbsFieldName); 
                }
            }
        } 
    
        /// <summary>
	    /// Some fields may contain complex structure and multiple value may be extracted from the field or may require
	    /// additional qualifiers to identify a specific value.  This method
	    /// get a list of attribute token names or qualifiers that can be access from the field.  If the field does not support sub attributes
	    /// or qualifiers, then this method returns a single entry with the field name.
        /// <returns>A list of attribute token names or qualifiers that can be access from the field.</returns> 
        /// </summary>
	    public IList<String> GetFieldTokens()
	    {
		    if (m_subAttributes == null || ! m_subAttributes.Any())
			    return new List<string>{"X509.TBS." + tbsField.RfcName, "X509.TBS." + tbsField.RfcName + "+"};
		
		    IList<String> names = new List<string>();
            foreach (var attributeReferenceClass in m_subAttributes)
            {
                names.Add("X509.TBS." + tbsField.RfcName + "." + attributeReferenceClass.GetAttribute());
            }
		    return names;
	    }


        /// <summary>
        /// Gets the name of the field as defined by RFC5280.
        /// </summary>
        /// <returns>The name of the field as defined by RFC5280.</returns>
        public String GetRfcName()
        {
            return tbsField.RfcName;
        }

        
        /// <summary>
        /// Gets a human readable display name of the field.
        /// </summary>
        /// <returns>A human readable display name of the field.</returns>
        public String GetDisplay()
        {
            return tbsField.Display;
        }



  
        
	
    
        
	//public Class<? extends TBSField<?>> getReferenceClass(String tokenName)
        
        /// <summary>
        /// Gets the class implementing the field name.
        /// </summary>
        /// <param name="tokenName">Field name</param>
        /// <returns>The class implementing the field name.</returns>
        public dynamic GetReferenceClass(String tokenName)
	    {
		    //Class<? extends TBSField<?>> retVal = null;
            dynamic retVal = null;

		    if (m_referenceClass != null)
		    {
		        return m_referenceClass;
		    }
            int idx = tokenName.LastIndexOf(".", StringComparison.CurrentCulture);
            if (idx >= 0)
            {
                String name = tokenName.Substring(idx + 1);

                foreach (var attRefClass in m_subAttributes)
                {
                    if (name.Equals(attRefClass.GetAttribute()))
                    {
                        retVal = attRefClass.GetReferenceClass(name) ;
                        break;
                    }		
                }
            }

                return retVal;
	    }



        /// <summary>
        /// Gets the field name associated with a specific token string.
        /// </summary>
        /// <param name="token">The token used to look up the TBSFieldName.</param>
        /// <returns>The TBSFieldName associated with the token.  If the token does not represent a known field, then null is returned.</returns>
        public static TBSFieldName FromToken(String token)
        {
            TBSFieldName tbsFieldName;
            TokenFieldMap.TryGetValue(token, out tbsFieldName);
            return tbsFieldName;
        }
    }
}
