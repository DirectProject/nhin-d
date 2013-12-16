using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;
using Health.Direct.Policy.Extensions;
using Health.Direct.Policy.X509;
using Org.BouncyCastle.Asn1;
using X509Certificate = Org.BouncyCastle.X509.X509Certificate;

namespace Health.Direct.Policy
{
    public abstract class ExtensionField<T> : TBSField<T>, IExtensionField<T>
    {
        public ExtensionField(bool required)
            : base(required)
        {
        }

        public override TBSFieldName<T> GetFieldName()
        {
            return TBSFieldName<T>.Extenstions;
        }

        public abstract ExtensionIdentifier<T> GetExtentionIdentifier();
        

        virtual public bool IsCritical()
        {
            if (Certificate == null)
			    throw new InvalidOperationException("Certificate value is null");

            List<string> criticalOIDs = Certificate.GetCriticalExtensionOIDs();

            return criticalOIDs.Contains(GetExtentionIdentifier().GetId());
        }

        /// <summary>
        /// Gets the specified certificate extension field from the certificate as a <see cref="DerObjectIdentifier"/>.  
        /// The extension field is determined by the concrete implementation of <see cref="GetExtentionIdentifier"/>
        /// <param name="cert">The certificate to extract the extension field from.</param>
        /// <returns>The extension field as DerObjectIdentifier.  If the extension does not exist in the certificate, then null is returned. </returns>
        /// <exception cref="PolicyProcessException">TODO:</exception>
	    /// </summary>
        protected DerObjectIdentifier GetExtensionValue(X509Certificate2 cert)
        {
    	    string oid = GetExtentionIdentifier().GetId();

            X509Extension x509Extension = cert.Extensions[oid];
            if (x509Extension != null)
            {
                byte[] bytes = x509Extension.RawData;
                if (bytes == null)
                {
                    return null;
                }

                return GetObject(bytes);
            }
            return null;
        }
    }
}
