namespace Health.Direct.Policy
{
    /// <summary>
    /// Reference expression type object.  Reference expression values are set at runtime by injecting the referenced value into the expression using the 
    /// InjectReferenceValue(R value)" method.  Although reference expressions may reference complex objects, they functionally only allow the retrieval of
    /// an object attribute returned as a <see cref="IPolicyValue{T}"/>.
    /// 
    /// The policy engine supports either a generic structure or an X509 specific attributes.  X509 reference expressions are defined in the 
    /// org.nhindirect.policy.x509 package.
    /// 
    /// <typeparam name="R">The type of the reference object.</typeparam> 
    /// <typeparam name="T">The type of <see cref="IPolicyValue{T}"/></typeparam>.
    /// </summary>
    public interface IReferencePolicyExpression<R, T> : ILiteralPolicyExpression<T>
    {
        /// <summary>
        /// Gets the type of referenceable  expressions.
        /// <returns>The type of referenceable  expressions.</returns>
        /// </summary>
        PolicyExpressionReferenceType GetPolicyExpressionReferenceType();

        /// <summary>
        /// Injects the referenced value into the expressions.  The relevant accessible attribute of the reference object can be 
        /// retrieved by calling the <see cref="IPolicyValue{T}"/>.GetPolicyValue() method.
        /// <typeparam name="R">The type of the reference object.</typeparam> 
        /// <remarks>
        /// Throws PolicyProcessException Thrown if the reference value cannot be successfully processed or the relevant accessible attribute cannot be 
        /// retrieved from the value.  For example, the X509 reference expressions may require an X509 attribute to be present in a certificate.  If the required
        /// certificate attribute is not present, the this exception would be thrown.
        /// </remarks>
        /// </summary>
        void InjectReferenceValue(R value);
    }
}