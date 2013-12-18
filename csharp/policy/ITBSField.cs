using System.Security.Cryptography.X509Certificates;
using Health.Direct.Policy.X509;

namespace Health.Direct.Policy
{
    /// <summary>
    /// Interface definition of a field in the to be signed (TBS) part of an X509 certificate.
    /// <typeparam name="T">The type of <see cref="IPolicyValue{T}"/> of the evaluated field of the <see cref="X509Certificate"/></typeparam>.
    /// </summary>
    public interface ITBSField<T> : IX509Field<T>
    {
        /// <summary>
        /// Get TBS field name.
        /// <returns><see cref="TBSFieldName"/> of the attribute extracted from the certificate.</returns>
        /// </summary>
        TBSFieldName GetFieldName();
    }

}