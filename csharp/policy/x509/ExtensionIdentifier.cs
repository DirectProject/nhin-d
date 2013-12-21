using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;

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

        /// <summary>
        /// Subject direct attributes extension.  Defined by RFC5280 section 4.2.1.8
        /// </summary>
        public static readonly ExtensionIdentifier SubjectDirectoryAttributes
            = new ExtensionIdentifier(new ExtensionStandard.SubjectDirectoryAttributes(),
                new List<AttributeReferenceClass>());

        /// <summary>
        /// Subject key identifier extension.  Defined by RFC5280 section 4.2.1.2
        /// </summary>
        public static readonly ExtensionIdentifier SubjectKeyIdentifier
            = new ExtensionIdentifier(new ExtensionStandard.SubjectKeyIdentifier(), typeof(SubjectKeyIdentifierExtensionField));

        /// <summary>
        /// Issuer alternative name extension.  Defined by RFC5280 section 4.2.1.7
        /// </summary>
        public static readonly ExtensionIdentifier IssuerAltName
            = new ExtensionIdentifier(new ExtensionStandard.IssuerAltName(),
                new List<AttributeReferenceClass>());

        /// <summary>
        /// Authority key identifier extension.  Defined by RFC5280 section 4.2.1.1 
        /// </summary>
        public static readonly ExtensionIdentifier AuthorityKeyIdentifier
            = new ExtensionIdentifier(new ExtensionStandard.AuthorityKeyIdentifier(),
                new List<AttributeReferenceClass>()
               {
                    new AttributeReferenceClass("KeyId", typeof(AuthorityKeyIdentifierKeyIdExtensionField)),
                    new AttributeReferenceClass("CertIssuers", null), 
				    new AttributeReferenceClass("SerialNumber", null)
               });

        /// <summary>
        /// Certificate policies extension.  Defined by RFC5280 section 4.2.1.4
        /// </summary>
        public static readonly ExtensionIdentifier CertificatePolicies
            = new ExtensionIdentifier(new ExtensionStandard.CertificatePolicies(),
                new List<AttributeReferenceClass>()
               {
                    new AttributeReferenceClass("PolicyOIDs", typeof(CertificatePolicyIndentifierExtensionField)),
                    new AttributeReferenceClass("CPSUrls", typeof(CertificatePolicyCpsUriExtensionField))
               });

        /// <summary>
        /// Policy mappings extension.  Defined by RFC5280 section 4.2.1.5
        /// </summary>
        public static readonly ExtensionIdentifier PolicyMappings
            = new ExtensionIdentifier(new ExtensionStandard.PolicyMappings(),
                new List<AttributeReferenceClass>());

        /// <summary>
        /// Authority key identifier extension.  Defined by RFC5280 section 4.2.1.1 
        /// </summary>
        public static readonly ExtensionIdentifier BasicConstraints
            = new ExtensionIdentifier(new ExtensionStandard.BasicConstraints(),
                new List<AttributeReferenceClass>()
               {
                    new AttributeReferenceClass("CA", typeof(BasicContraintsExtensionField)),
                    new AttributeReferenceClass("MaxPathLength", null)
               });

        /// <summary>
        /// Name constraints extension.  Defined by RFC5280 section 4.2.1.10
        /// </summary>
        public static readonly ExtensionIdentifier NameConstraints
            = new ExtensionIdentifier(new ExtensionStandard.NameConstraints(),
                new List<AttributeReferenceClass>());

        /// <summary>
        ///Policy constraints extension.  Defined by RFC5280 section 4.2.1.11
        /// </summary>
        public static readonly ExtensionIdentifier PolicyConstraints
            = new ExtensionIdentifier(new ExtensionStandard.PolicyConstraints(),
                new List<AttributeReferenceClass>());

        /// <summary>
        /// Subject key identifier extension.  Defined by RFC5280 section 4.2.1.2
        /// </summary>
        public static readonly ExtensionIdentifier ExtKeyUsageSyntax
            = new ExtensionIdentifier(new ExtensionStandard.ExtKeyUsageSyntax(), typeof(ExtendedKeyUsageExtensionField));

        /// <summary>
        /// Certificate revocation list distribution points extension.  Defined by RFC5280 section 4.2.1.13
        /// </summary>
        public static readonly ExtensionIdentifier CRLDistributionPoints
            = new ExtensionIdentifier(new ExtensionStandard.CRLDistributionPoints(),
                new List<AttributeReferenceClass>()
               {
                    new AttributeReferenceClass("FullName", typeof(CRLDistributionPointNameExtentionField)),
                    new AttributeReferenceClass("RelativeToIssuer", null), 
				    new AttributeReferenceClass("Reasons", null),
                    new AttributeReferenceClass("CRLIssuer", null)
               });

        /// <summary>
        /// Inhibit any policy extension.  Defined by RFC5280 section 4.2.1.14
        /// </summary>
        public static readonly ExtensionIdentifier InhibitAnyPolicy
            = new ExtensionIdentifier(new ExtensionStandard.InhibitAnyPolicy(), new List<AttributeReferenceClass>());

        /// <summary>
        /// Freshest certificate revocation list extension.  Defined by RFC5280 section 4.2.1.15
        /// </summary>
        public static readonly ExtensionIdentifier FreshestCRL
            = new ExtensionIdentifier(new ExtensionStandard.FreshestCRL(),
                new List<AttributeReferenceClass>()
               {
                    new AttributeReferenceClass("FullName", null),
                    new AttributeReferenceClass("RelativeToIssuer", null), 
				    new AttributeReferenceClass("Reasons", null),
                    new AttributeReferenceClass("CRLIssuer", null)
               });

        /// <summary>
        /// Authority information access extension.  Defined by RFC5280 section 4.2.2.1
        /// </summary>
        public static readonly ExtensionIdentifier AuthorityInfoAccessSyntax
            = new ExtensionIdentifier(new ExtensionStandard.AuthorityInfoAccessSyntax(),
                new List<AttributeReferenceClass>()
               {
                    new AttributeReferenceClass("Url", typeof(AuthorityInfoAccessExtentionField)),
                    new AttributeReferenceClass("AccessMethod", null), 
				    new AttributeReferenceClass("OCSPLocation", typeof(AuthorityInfoAccessOCSPLocExtentionField))
               });

        // <summary>
        /// Subject information access extension.  Defined by RFC5280 section 4.2.2.2
        /// </summary>
        public static readonly ExtensionIdentifier SubjectInfoAccessSyntax
            = new ExtensionIdentifier(new ExtensionStandard.SubjectInfoAccessSyntax(),
                new List<AttributeReferenceClass>()
               {
                    new AttributeReferenceClass("Url", null),
                    new AttributeReferenceClass("AccessMethod", null), 
				    new AttributeReferenceClass("OCSPLocation", null)
               });
        

        public static IEnumerable<ExtensionIdentifier> Values
        {
            get
            {
                yield return KeyUsage;
                yield return SubjectAltName;
                yield return SubjectDirectoryAttributes;
                yield return SubjectKeyIdentifier;
                yield return IssuerAltName;
                yield return AuthorityKeyIdentifier;
                yield return CertificatePolicies;
                yield return PolicyMappings;
                yield return BasicConstraints;
                yield return NameConstraints;
                yield return PolicyConstraints;
                yield return ExtKeyUsageSyntax;
                yield return CRLDistributionPoints;
                yield return InhibitAnyPolicy;
                yield return FreshestCRL;
                yield return AuthorityInfoAccessSyntax;
                yield return SubjectInfoAccessSyntax;
            }
        }


        readonly string m_id;
        readonly string m_rfcName;
        readonly string m_display;
        readonly List<AttributeReferenceClass> m_subAttributes;
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

            //add required flag option to each sub attribute
            if (this.m_subAttributes.Count > 0)
            {
                var addAttributes = new List<AttributeReferenceClass>();

                foreach (var attributeReferenceClass in m_subAttributes)
                {
                    addAttributes.Add(new AttributeReferenceClass(attributeReferenceClass.GetAttribute()
                        + "+", attributeReferenceClass.GetReferenceClass()));
                }
                m_subAttributes.AddRange(addAttributes);
            }
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
        public Type GetReferenceClass(String tokenName, bool required)
        {
            Type retVal = null;

            if (m_referenceClass != null)
            {
                return m_referenceClass;
            }
            var idx = tokenName.LastIndexOf(".", StringComparison.Ordinal);
            if (idx < 0) return null;
            var name = tokenName.Substring(idx + 1);

            foreach (var attrRef in m_subAttributes)
            {
                if (name.Equals(attrRef.GetAttribute()))
                {
                    retVal = attrRef.GetReferenceClass();
                    break;
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
        private readonly Type referenceClass;



        public AttributeReferenceClass(String attribute, Type referenceClass)
        {
            this.attribute = attribute;
            this.referenceClass = referenceClass;
        }


        public String GetAttribute()
        {
            return attribute;
        }


        public Type GetReferenceClass()
        {
            return referenceClass;
        }
    }

}
