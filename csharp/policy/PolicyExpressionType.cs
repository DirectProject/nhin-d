namespace Health.Direct.Policy
{
    /// <summary>
    /// Enumeration defining the types of expressions.
    /// </summary>
    public enum PolicyExpressionType
    {
        /// <summary>
        /// A literal expression.  Literals are simply primitive types or objects that have a static value. 
        /// In the policy engine, literals are represented by {@link PolicyValue} objects.</summary>
        LITERAL,

        /// <summary>
        /// References are objects whose values are evaluated at runtime similar to variables.  Reference may be simple structures or specific structure types 
        /// such as X509 certificates.</summary>
        REFERENCE,

        /// <summary>
        /// Operations are a combination of a {@link PolicyOperator} and one or more parameters.  Operator parameters are themselves expressions
        /// allowing parameters to be either literals, references, or the result of another operation.</summary>
        OPERATION
    }
}