/* 
 Copyright (c) 2014, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

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

        /// <summary>
        /// Construct a <see cref="CertPolicyResolvers"/> container with supplied key value pairs.
        /// Name the resolver as the key and store the <see cref="IPolicyResolver"/> in value.
        /// </summary>
        /// <param name="resolvers"></param>
        public CertPolicyResolvers(IList<KeyValuePair<string, IPolicyResolver>> resolvers)
        {
            Resolvers = resolvers;
        }

        /// <inheritdoc />
        public IList<KeyValuePair<string, IPolicyResolver>> Resolvers { get; private set; }

        /// <inheritdoc />
        public IPolicyResolver TrustResolver
        {
            get
            {
                return Resolvers.FirstOrDefault(r => r.Key == TrustPolicyName).Value;
            }
        }

        /// <inheritdoc />
        public IPolicyResolver PrivateResolver
        {
            get
            {
                return Resolvers.FirstOrDefault(r => r.Key == PrivatePolicyName).Value;
            }
        }

        /// <inheritdoc />
        public IPolicyResolver PublicResolver
        {
            get
            {
                return Resolvers.FirstOrDefault(r => r.Key == PublicPolicyName).Value;
            }
        }

        
        
    }
}