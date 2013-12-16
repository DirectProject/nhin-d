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
    public class TBSFieldName<T>
    {
        /// <summary>
        /// Certificate version
        /// </summary>
        public static readonly TBSFieldName<T> VERSION = new TBSFieldName<T>("Version", "Version",
                                                                       new List<AttributeReferenceClass<T>>());
        /// <summary>
        /// Certificate serial number
        /// </summary>
        public static readonly TBSFieldName<T> SERIAL_NUMBER = new TBSFieldName<T>("SerialNumber", "Serial Number", new SerialNumberAttributeField() as ITBSField<T>);
        
        /// <summary>
        /// Certificate signature algorithm
        /// </summary>
        public static readonly TBSFieldName<T> SIGNATURE = new TBSFieldName<T>("Signature", "Signature", new List<AttributeReferenceClass<T>>());


        //TODO: need delegates to insert the constructor params at run time. For both Issuer and Subject
        //Both hard coded for now.
        // So lots of temp code to get compiling...

        /// <summary>
        /// Distinguished name of certificate signer
        /// </summary>
        /// <param name="?"></param>
        public static readonly TBSFieldName<T> ISSUER = new TBSFieldName<T>("Issuer", "Issuer",
                                                                           rdnsToReferenceClass(new IssuerAttributeField(false, null) as ITBSField<T>));


        /// <summary>
        /// Distinguished name of certificate signer
        /// </summary>
        /// <param name="?"></param>
        public static readonly TBSFieldName<T> SUBJECT = new TBSFieldName<T>("Subject", "Subject",
                                                                            rdnsToReferenceClass(new SubjectAttributeField(false, null) as ITBSField<T>));


        /// <summary>
        /// Certificate extension fields
        /// </summary>
        public static readonly TBSFieldName<T> Extenstions = new TBSFieldName<T>("Extensions", "Extensions",
            new List<AttributeReferenceClass<T>>());



        static List<AttributeReferenceClass<T>> rdnsToReferenceClass(ITBSField<T> refClass)
	    {
		     List<AttributeReferenceClass<T>> retVal = new List<AttributeReferenceClass<T>>();

            foreach (var rdnAtrId in RDNAttributeIdentifier.Values)
            {
                retVal.Add(new AttributeReferenceClass<T>(rdnAtrId.GetName(), refClass));
            }
		   
		    return retVal;
	    }

        readonly String m_rfcName;
	    readonly String m_display;
	    readonly IList<AttributeReferenceClass<T>> m_subAttributes;
	    //Class<? extends TBSField<?>> referenceClass;
        ITBSField<T> m_referenceClass;
        //ITBSField<Object> m_referenceClass;
        //Type m_referenceClass;

	    static Dictionary<String, TBSFieldName<T>> tokenFieldMap; 

        private TBSFieldName(String rfcName, String display, IList<AttributeReferenceClass<T>> subAttributes) 
        {
            m_rfcName = rfcName;
	        m_display = display;
            m_subAttributes = subAttributes;
	        m_referenceClass = null;

            //add required flag option to each sub attribute
		    if (m_subAttributes.Count > 0)
		    {
                for (int i = 0; i < this.m_subAttributes.Count; i++ )
                {
                    var attRefClass = this.m_subAttributes[0];
                    m_subAttributes[0] = new AttributeReferenceClass<T>(attRefClass.GetAttribute() + "+",
                                                                        attRefClass.GetReferenceClass());
                }
		    }
        }

        private TBSFieldName(String rfcName, String display, ITBSField<T> referenceClass)
	    {
		    m_rfcName = rfcName;
		    m_display = display;
		    m_subAttributes = null;
		    m_referenceClass = referenceClass;		
	    }

    
        /// <summary>
        /// Gets the name of the field as defined by RFC5280.
        /// </summary>
        /// <returns>The name of the field as defined by RFC5280.</returns>
        public String GetRfcName()
        {
            return m_rfcName;
        }

        
        /// <summary>
        /// Gets a human readable display name of the field.
        /// </summary>
        /// <returns>A human readable display name of the field.</returns>
        public String GetDisplay()
        {
            return m_display;
        }



  
        /// <summary>
        /// Some fields may contain complex structure and multiple value may be extracted from the field or may required
        /// additional qualifiers to identify a specific value.  This method
        /// get a list of attribute token names or qualifiers that can be access from the field.  If the field does not support sub attributes
        /// or qualifiers, then this method returns a single entry with the field name. 
        /// </summary>
        /// <returns>A list of attribute token names or qualifiers that can be access from the field.</returns>
	    public List<String> GetFieldTokens()
	    {
		    if (m_subAttributes == null || !m_subAttributes.Any())
			    return new List<String>{"X509.TBS." + m_rfcName, "X509.TBS." + m_rfcName + "+"};
    		
		    List<String> names = new List<String>();
	        foreach (var attRefClass in m_subAttributes)
	        {
	            names.Add("X509.TBS." + m_rfcName + "." + attRefClass.GetAttribute());
	        }
    		
		    return names;
	    }
	
    
        
	//public Class<? extends TBSField<?>> getReferenceClass(String tokenName)
        
        /// <summary>
        /// Gets the class implementing the field name.
        /// </summary>
        /// <param name="tokenName">Field name</param>
        /// <returns>The class implementing the field name.</returns>
        public ITBSField<T> GetReferenceClass(String tokenName)
	{
		//Class<? extends TBSField<?>> retVal = null;
        ITBSField<T> retVal = null;

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
                    retVal = attRefClass.GetReferenceClass();
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
        public static TBSFieldName<T> FromToken(String token)
        {
            TBSFieldName<T> tbsFieldName;
            tokenFieldMap.TryGetValue(token, out tbsFieldName);
            return tbsFieldName;
        }





        //Todo: combine with ExtensionIdentifier.AttributeReferenceClass ???
        public class AttributeReferenceClass<T>
        {
            private readonly String attribute;
            //protected final Class<? extends TBSField<?>> referenceClass;
            private ITBSField<T> referenceClass;

            //public AttributeReferenceClass(String attribute, Class<? extends TBSField<?>> referenceClass)
            //{
            //    this.attribute = attribute;
            //    this.referenceClass = referenceClass;
            //}

            public AttributeReferenceClass(String attribute, ITBSField<T> referenceClass)
            {
                this.attribute = attribute;
                this.referenceClass = referenceClass;
            }


            public String GetAttribute()
            {
                return attribute;
            }

            //public Class<? extends TBSField<?>> getReferenceClass()
            //{
            //    return referenceClass;

            //}

            public ITBSField<T> GetReferenceClass()
            {
                return referenceClass;
            }
        }

    }
}
