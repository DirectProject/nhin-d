namespace Health.Direct.Policy.Impl
{
    /// <summary>
    /// Enumeration of token types
    /// </summary>
    public enum TokenType
    {
        /// <summary>
        /// Start of a new depth level of an expression
        /// </summary>
        START_LEVEL,
		
        /// <summary>
        /// End of a depth level of an expression
        /// </summary>
        END_LEVEL,
		
        /// <summary>
        /// An operator expression
        /// </summary>
        OPERATOR_EXPRESSION,
		
        /// <summary>
        /// A literal expression
        /// </summary>
        LITERAL_EXPRESSION,
		
        /// <summary>
        /// A certificate reference expression
        /// </summary>
        CERTIFICATE_REFERENCE_EXPRESSION
    }
}