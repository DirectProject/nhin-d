using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;
using Org.BouncyCastle.Asn1;
using Org.BouncyCastle.Asn1.X509;

namespace Health.Direct.Policy.X509
{
    /// <summary>
    /// Subject alternative name extension field.
    /// <p/>
    /// The policy value of this extension is returned as a collection of strings containing a concatenation of
    /// the name type and the actual name.  For example, an alt name of rfc822 would look like the following.
    /// <br/>
    /// <pre>
    ///   rfc822:jshook@ssdev.direct.cert.clinicalinterop.com
    /// </pre>
    /// <br/>
    /// If the extension does not exist in the certificate, then the policy value returned by this class
    /// evaluates to an empty collection.
    /// </summary>
    public class SubjectAltNameExtensionField : ExtensionField<IList<String>>
    {
        /// <summary>
	    /// Create new instance
	    /// <param name="required">
	    /// Indicates if the field is required to be present in the certificate to be compliant with the policy.
	    /// </param>
	    /// </summary>
        public SubjectAltNameExtensionField(bool required)
            : base(required)
	    {
	    }

        /// <inheritdoc />
        public override void InjectReferenceValue(X509Certificate2 value)
        {
            Certificate = value;

            Asn1Object exValue = GetExtensionValue(value);

            if (exValue == null)
            {
                if (IsRequired())
                    throw new PolicyRequiredException("Extention " + ExtentionIdentifier.Display + " is marked as required by is not present.");
                else
                {
                    var emptyList = new List<string>();
                    PolicyValue = PolicyValueFactory<IList<string>>.GetInstance(emptyList);
                    return;
                }
            }

            var names = new List<string>();
		
		    var generalNames = GeneralNames.GetInstance(exValue);
	
            foreach (var name in generalNames.GetNames())
            {
                var type = StandardExt.FromTag<Standard.GeneralNameType>(name.TagNo);
                names.Add(type.Name() + ":" + name.Name);
		    }
            PolicyValue = PolicyValueFactory<IList<string>>.GetInstance(names);
        }

        /// <inheritdoc />
        public override ExtensionIdentifier ExtentionIdentifier
        {
            get { return ExtensionIdentifier.SubjectAltName; }
        }
    }
}
