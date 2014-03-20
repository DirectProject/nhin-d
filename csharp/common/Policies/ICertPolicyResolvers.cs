using System.Collections.Generic;

namespace Health.Direct.Common.Policies
{
    /// <summary>
    /// Contains certificate policy resolvers.
    /// </summary>
    public interface ICertPolicyResolvers
    {
        /// <summary>
        /// Retrieve certificate policy resolvers
        /// </summary>
        IList<KeyValuePair<string, IPolicyResolver>> Resolvers { get; }
        /// <summary>
        /// Get cert policy resolver for trust anchors
        /// </summary>
        IPolicyResolver TrustResolver { get; }
        /// <summary>
        /// Get cert policy resolver for private certificates
        /// </summary>
        IPolicyResolver PrivateResolver { get; }
        /// <summary>
        /// Get cert policy resolver for public certificates
        /// </summary>
        IPolicyResolver PublicResolver { get; }

    }
}