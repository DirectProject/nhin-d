using System.Security.Cryptography.X509Certificates;
using Health.Direct.Policy.X509;

namespace Health.Direct.Policy
{
    /// <summary>
    /// Interface definition of a certificate extension field.  Extensions are identified by a unique object identified (OID) and
    /// can be marked critical as defined by RFC5280.
    /// 
    /// <typeparam name="T">The type of <see cref="IPolicyValue{T}"/> of the evaluated field of the <see cref="X509Certificate"/> extension value</typeparam>.
    /// </summary>
    public interface IExtensionField<T> : ITBSField<T>
    {
        /// <summary>
        /// Gets the object identifier for the extension field.
        /// <returns>The object identifier for the extension field.</returns>
        /// </summary>
        ExtensionIdentifier GetExtentionIdentifier();

        /// <summary>
        /// Indicates if the extension is marked as critical.
        /// <returns>True if the extension is marked critical.  False otherwise.</returns>
        /// </summary>
        bool IsCritical();
    }
}