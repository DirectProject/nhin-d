using System.Collections.Generic;
using System.Linq;

namespace Health.Direct.Common.Policies
{
    /// <summary>
    /// Container of all certificate policies
    /// Three <see cref="IPolicyResolver"/>s can be hosted.  Each named according to <see cref="TrustPolicyName"/>, <see cref="PrivatePolicyName"/>, and <see cref="PublicPolicyName"/>
    /// </summary>
    public class CertPolicyResolvers : ICertPolicyResolvers
    {
        /// <summary>
        /// Name of Trust policy resolver
        /// </summary>
        public const string TrustPolicyName = "Trust";
        /// <summary>
        /// Name of Private policy resolver
        /// </summary>
        public const string PrivatePolicyName = "Private";
        /// <summary>
        /// Name of public policy resolver
        /// </summary>
        public const string PublicPolicyName = "Public";

        /// <summary>
        /// An empty CertPolicyResolvers container 
        /// </summary>
        public static readonly CertPolicyResolvers Default = new CertPolicyResolvers(); 

        /// <summary>
        /// Constructs an instance with a no resolvers
        /// </summary>
        public CertPolicyResolvers()
        {
            Resolvers = new List<KeyValuePair<string, IPolicyResolver>>();
        }

        public CertPolicyResolvers(IList<KeyValuePair<string, IPolicyResolver>> resolvers)
        {
            Resolvers = resolvers;
        }
        public IList<KeyValuePair<string, IPolicyResolver>> Resolvers { get; private set; }
        public IPolicyResolver TrustResolver
        {
            get
            {
                return Resolvers.FirstOrDefault(r => r.Key == TrustPolicyName).Value;
            }
        }
        public IPolicyResolver PrivateResolver
        {
            get
            {
                return Resolvers.FirstOrDefault(r => r.Key == PrivatePolicyName).Value;
            }
        }
        public IPolicyResolver PublicResolver
        {
            get
            {
                return Resolvers.FirstOrDefault(r => r.Key == PublicPolicyName).Value;
            }
        }

        
        
    }
}