using System.Security.Cryptography.X509Certificates;
using Health.Direct.Policy.X509;

namespace Health.Direct.Policy
{
    /// <summary>
    /// Interface definition for an X509 certificate referenced policy expression.  Each object takes an X509Certificate and
    /// evaluates to a specific field or extension of the certificate.
    /// 
    /// An attribute may be flagged as required meaning that the field or extension must be present in the certificate
    /// to comply with the policy.
    /// <typeparam name="T">The type of <see cref="IPolicyValue{T}"/></typeparam>.
    /// </summary>
    public interface IX509Field<T> : IReferencePolicyExpression<X509Certificate2, T>
    {
        /// <summary>
        /// Get the <see cref="X509FieldType"/> of the certificate.
        /// <returns>
        /// The field type of the certificate.
        /// </returns> 
        /// </summary>
        X509FieldType X509FieldType { get; }

        /// <summary>
        /// Indicates if the field or extension must exist in the certificate to be compliant with the policy.
        /// <returns>
        /// True if the field or extension is required.  False otherwise</returns>
        /// </summary>
        bool IsRequired();

        /// <summary>
        /// Sets the required indicator.
        /// <param name="required">
        /// Set to true if the field or extension is required. false otherwise
        /// </param> 
        /// </summary>
        void SetRequired(bool required);
    }
}