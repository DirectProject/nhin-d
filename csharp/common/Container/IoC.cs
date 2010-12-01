/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;

using Health.Direct.Common.Diagnostics;

namespace Health.Direct.Common.Container
{
    ///<summary>
    /// This is the global dispatch class for plugging in an Inversion of Control
    /// container. This class is making use of the static gateway pattern.
    ///</summary>
    public static class IoC
    {
        private static IDependencyResolver m_resolver;

        ///<summary>
        /// Initialize the global inversion of control reference with the <paramref name="resolver"/>.
        /// <see cref="IoC"/> allows us to drop in a different inversion of control container
        /// that will vary between different companies' implementations of the gateway and agent.
        ///</summary>
        ///<param name="resolver">The container to use as the resolver</param>
        ///<returns>Returns a reference to the resolver so it can be used in method chaining.</returns>
        ///<exception cref="ArgumentNullException">Throw if <paramref name="resolver"/> was null</exception>
        public static T Initialize<T>(T resolver)
            where T : class, IDependencyResolver
        {
            if (resolver == null)
            {
                throw new ArgumentNullException("resolver");
            }

            m_resolver = resolver;

            return resolver;
        }

        ///<summary>
        /// Initialize the global inversion of control reference with the <paramref name="sectionName"/>.
        /// <see cref="IoC"/> allows us to drop in a different inversion of control container
        /// that will vary between different companies' implementations of the gateway and agent.
        ///</summary>
        ///<param name="sectionName">The container to use as the resolver</param>
        ///<returns>Returns a reference to the resolver so it can be used in method chaining.</returns>
        ///<exception cref="ArgumentNullException">Throw if <paramref name="sectionName"/> was null</exception>
        public static IDependencyResolver Initialize(string sectionName)
        {
            try
            {
                IocContainerSection section = IocContainerSection.Load(sectionName);
                IDependencyContainer container = section.CreateContainer();
                return container.RegisterFromConfig();
            }
            catch (Exception ex)
            {
                EventLogHelper.WriteError("While initializing container", ex.ToString());
                throw;
            }
        }

        ///<summary>
        /// Returns an instance of type <typeparamref name="T"/>.
        ///</summary>
        ///<typeparam name="T"></typeparam>
        ///<returns></returns>
        ///<exception cref="InvalidOperationException">Is throws if <see cref="Initialize{T}"/> was </exception>
        public static T Resolve<T>()
        {
            if (m_resolver == null)
            {
                m_resolver = Initialize("ioc");

                if (m_resolver == null)
                {
                    throw new InvalidOperationException("Resolve was called before Initialize");
                }
            }

            return m_resolver.Resolve<T>();
        }
    }
}