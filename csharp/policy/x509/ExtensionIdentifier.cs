using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using Org.BouncyCastle.Utilities;

namespace Health.Direct.Policy.X509
{

    //Todo: Compare to technique in X5509FieldType...
    public class ExtensionIdentifier
    {

        static ExtensionIdentifier()
        {
            var extensionfield = Expression.Parameter(typeof(IExtensionField<>), "extensionField");

            TokenFieldMap = new Dictionary<string, ExtensionIdentifier>();

            foreach (var extFieldName in Values)
            {
                foreach (string fieldToken in extFieldName.GetFieldTokens())
                {
                    TokenFieldMap.Add(fieldToken, extFieldName);
                }
            }
        }

        /// <summary>
	    /// Key usage extension.  Defined by RFC5280 section 4.2.1.3
        /// </summary>
        public static readonly ExtensionIdentifier KeyUsage
            = new ExtensionIdentifier(new ExtensionStandard.KeyUsage(), typeof(KeyUsageExtensionField));

        /// <summary>
        /// Subject alternative name extension.  Defined by RFC5230 section 4.2.1.6
        /// </summary>
        public static readonly ExtensionIdentifier SubjectAltName 
            = new ExtensionIdentifier(new ExtensionStandard.SubjectAltName(), typeof(SubjectAltNameExtensionField));
        

        public static IEnumerable<ExtensionIdentifier> Values
        {
            get
            {
                yield return KeyUsage;
            }
        }


        readonly string m_id;
        readonly string m_rfcName;
        readonly string m_display;
        readonly IList<AttributeReferenceClass> m_subAttributes;
        readonly Type m_referenceClass;
        static readonly Dictionary<String, ExtensionIdentifier> TokenFieldMap;



        private ExtensionIdentifier(ExtensionStandard.Field field, Type referenceClass)
        {
            m_id = field.Id;
            m_rfcName = field.RfcName;
            m_display = field.Display;
            m_subAttributes = null;
            m_referenceClass = referenceClass;
        }

        private ExtensionIdentifier(ExtensionStandard.Field field, List<AttributeReferenceClass> subAttributes)
        {
            m_id = field.Id;
            m_rfcName = field.RfcName;
            m_display = field.Display;
            m_subAttributes = subAttributes;
            m_referenceClass = null;
        }


        /// <summary>
        /// Gets the object identifier (OID) of the extension.
        /// </summary>
        /// <returns>The object identifier (OID) of the extension.</returns>
        public string GetId()
        {
            return m_id;
        }

        /// <summary>
        /// Gets the name of the extension as defined by RFC5280.
        /// @return The name of the extension as defined by RFC5280.
        /// </summary>
        public String GetRfcName()
        {
            return m_rfcName;
        }

        /// <summary>
        /// Gets a human readable display name of the extension.
        /// <returns>Human readable display name of the extension.</returns>
        /// </summary>
        public String GetDisplay()
        {
            return m_display;
        }

        /// <summary>
        /// Some extensions may contain complex structure and multiple value may be extracted from the extension or may required
        /// additional qualifiers to identify a specific value.  This method
        /// get a list of attribute token names or qualifiers that can be access from the extension.  If the extension does not support sub attributes
        /// or qualifiers, then this method returns a single entry with the extension name.
        /// @return Gets a list of attribute token names or qualifiers that can be access from the extension.
        /// </summary> 
        public IList<String> GetFieldTokens()
        {
            if (m_subAttributes == null || !m_subAttributes.Any())
                return new List<string> { "X509.TBS.EXTENSION." + m_rfcName, "X509.TBS.EXTENSION." + m_rfcName + "+" };

            IList<String> names = new List<string>();
            foreach (var attributeReferenceClass in m_subAttributes)
            {
                names.Add("X509.TBS.EXTENSION." + m_rfcName + "." + attributeReferenceClass.GetAttribute());
            }
            return names;
        }


        /// <summary>
        /// Gets the class implementing the extension.
        /// <returns>
        /// The Type implementing the extension.
        /// </returns> 
        /// </summary>
        public dynamic GetReferenceClass(String tokenName, bool required)
        {
            dynamic retVal = null;

            if (m_referenceClass != null)
                return m_referenceClass;
            else
            {
                int idx = tokenName.LastIndexOf(".");
                if (idx >= 0)
                {
                    string name = tokenName.Substring(idx + 1);

                    foreach (var attrRef in m_subAttributes)
                    {
                        if (name.Equals(attrRef.GetAttribute()))
                        {
                            retVal = attrRef.GetReferenceClass(required);
                            break;
                        }
                    }
                }
            }

            return retVal;
        }

        /// <summary>
        /// Gets the extension associated with a specific <paramref name="token"/> string.
        /// <param name="token"/>Used to look up the ExtensionIdentifier.
        /// <returns>The ExtensionIdentifier associated with the token.  If the token does not represent a known extension, then null is returned.</returns>
        /// </summary>
        public static ExtensionIdentifier FromToken(String token)
        {
            ExtensionIdentifier extensionIdentifier;
            TokenFieldMap.TryGetValue(token, out extensionIdentifier);
            return extensionIdentifier;
        }
    }

    public class AttributeReferenceClass
    {
        private readonly String attribute;
        private Func<bool, dynamic> referenceClass;



        public AttributeReferenceClass(String attribute, Func<bool, dynamic> referenceClass)
        {
            this.attribute = attribute;
            this.referenceClass = referenceClass;
        }


        public String GetAttribute()
        {
            return attribute;
        }


        public dynamic GetReferenceClass(bool required)
        {
            return referenceClass(required);
        }
    }

}
