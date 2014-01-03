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
using System.Runtime.InteropServices;
using Health.Direct.Policy.X509.Standard;


namespace Health.Direct.Policy.X509
{
    public class TBSFieldStandard
    {
        public class Field: IField
        {
            
            public string RfcName { get; set; }
            public string Display { get; set; }

            public static readonly List<Field> Map;

            static Field()
            {
                Map = new List<Field>();
                Map.Add(new Version(new List<AttributeReferenceClass>() ));
                Map.Add(new SerialNumber(new SerialNumberAttributeField() ));
                Map.Add(new Signature(new List<AttributeReferenceClass>() ));
                Map.Add(new Issuer(RdnsToReferenceClass(rdn => new IssuerAttributeField(false, rdn) )));
                Map.Add(new Validity(new List<AttributeReferenceClass>
                {
                    new AttributeReferenceClass("ValidFrom", typeof(DateTime)),
                    new AttributeReferenceClass("ValidTo", typeof(DateTime))
                }));
                Map.Add(new Subject(RdnsToReferenceClass(rdn => new SubjectAttributeField(false, rdn) )));
                Map.Add(new Extensions(new List<AttributeReferenceClass>() ));

                //Todo: revisit this and the SubjectPublicKeyInfo in tBSFieldName
                Map.Add(new SubjectPublicKeyInfo(new List<TBSFieldStandard.AttributeReferenceClass>
                {
                    new AttributeReferenceClass("Algorithm", rdn => new SubjectAttributeField(false, rdn)),
                    new AttributeReferenceClass("Size",rdn => new SubjectAttributeField(false, rdn))
                }));
            }
        }

        public interface IField
        {
            string RfcName { get; set; }
            string Display { get; set; }
            
        }
        public interface ISingle: IField
        {
            ITBSField<Int64> ReferenceClass { get; set; }
            List<String> GetFieldTokens();
        }
        public interface IComplex: IField
        {
             IList<AttributeReferenceClass> SubAttributes { get; set; }
             List<String> GetFieldTokens();
        }

        

        public class Single : Field, ISingle
        {
            public ITBSField<Int64> ReferenceClass { get; set; }

            /// <summary>
            /// Single entry with the field name. 
            /// </summary>
            /// <returns>Single entry with the field name. </returns>
            public List<String> GetFieldTokens()
            {
                return new List<String> { "X509.TBS." + RfcName, "X509.TBS." + RfcName + "+" };
            }
        }

        public class Complex : Field, IComplex
        {

            public IList<AttributeReferenceClass> SubAttributes { get; set; }

            /// <summary>
            /// Some fields may contain complex structure and multiple value may be extracted from the field or may required
            /// additional qualifiers to identify a specific value.  This method
            /// get a list of attribute token names or qualifiers that can be access from the field.  
            /// </summary>
            /// <returns>A list of attribute token names or qualifiers that can be access from the field.</returns>
            public List<String> GetFieldTokens()
            {
                List<String> names = new List<String>();
                foreach (var attRefClass in SubAttributes)
                {
                    names.Add("X509.TBS." + RfcName + "." + attRefClass.GetAttribute());
                }

                return names;
            }
        }



        public class Version : Complex
        {
            public Version(List<AttributeReferenceClass> subAttributes) 
            {
                RfcName = "Version";
                Display = "Version";
                SubAttributes = subAttributes;}
            
        }

        public class SerialNumber : Single
        {
            public SerialNumber(ITBSField<Int64> referenceClass)
            {
                RfcName = "SerialNumber";
                Display = "Serial Number";
                ReferenceClass = referenceClass;
            }
        }

        public class Signature : Complex
        {
            public Signature(List<AttributeReferenceClass> subAttributes) 
            {
                RfcName = "Signature";
                Display = "Signature";
                SubAttributes = subAttributes;
            }

        }

        public class Issuer : Complex
        {
            public Issuer(List<AttributeReferenceClass> subAttributes)
            {
                RfcName = "Issuer";
                Display = "Issuer";
                SubAttributes = subAttributes;
            }
        }

        public class Validity : Complex
        {
            public Validity(List<AttributeReferenceClass> subattribues)
            {
                RfcName = "Validity";
                Display = "Validity";
                SubAttributes = subattribues;
            }
        }

        public class Subject : Complex
        {
            public Subject(List<AttributeReferenceClass> subAttributes)
            {
                RfcName = "Subject";
                Display = "Subject";;
                SubAttributes = subAttributes;
            }
        }

        public class Extensions : Complex
        {
            public Extensions(List<AttributeReferenceClass> subAttributes)
            {
                RfcName = "Extensions";
                Display = "Extensions";;
                SubAttributes = subAttributes;
            }
        }

        public class SubjectPublicKeyInfo : Complex
        {
            public SubjectPublicKeyInfo(List<AttributeReferenceClass> subAttributes)
            {
                RfcName = "SubjectPublicKeyInfo";
                Display = "Subject Public Key Info"; ;
                SubAttributes = subAttributes;
            }
        }
        

        public class AttributeReferenceClass
        {
            readonly String attribute;
            readonly Type referenceClass;
            private readonly Func<string, TBSField<List<string>>> referenceClassList;

            public AttributeReferenceClass(String attribute, Type referenceClass)
            {
                this.attribute = attribute;
                this.referenceClass = referenceClass;
            }

            public AttributeReferenceClass(String attribute)
            {
                this.attribute = attribute;
            }

            
            public AttributeReferenceClass(String attribute, Func<string, TBSField<List<string>>> referenceClass)
            {
                this.attribute = attribute;
                this.referenceClassList = referenceClass;
            }

            public String GetAttribute()
            {
                return attribute;
            }

            public Type GetReferenceClass()
            {
                return referenceClass;
            }

            public TBSField<List<string>> GetReferenceClass(string rdnName)
            {
                return referenceClassList(rdnName);
            }
            
        }

        public static List<AttributeReferenceClass> RdnsToReferenceClass(Func<string, TBSField<List<string>>> refClass)
        {
            var retVal = new List<AttributeReferenceClass>();

            foreach (var rdnAtrId in RDNAttributeIdentifier.Values)
            {
                retVal.Add(new AttributeReferenceClass(rdnAtrId.Name, refClass));
                retVal.Add(new AttributeReferenceClass(rdnAtrId.Name + "+", refClass));
            }
		   
            return retVal;
        }

    }
}
