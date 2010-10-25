/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen     jtheisen@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;

using Health.Direct.Common.Container;

namespace Health.Direct.Common.Diagnostics
{
    ///<summary>
    /// A static gateway to ease access to the LogFactory.
    ///</summary>
    public static class Log
    {
        private static ILogFactory s_logFactory;
        private static readonly object s_factorySync = new object();

        private static ILogFactory LogFactory
        {
            get
            {
                lock (s_factorySync)
                {
                    if (s_logFactory == null)
                    {
                        s_logFactory = IoC.Resolve<ILogFactory>();
                    }

                    return s_logFactory;
                }
            }
        }

        ///<summary>
        /// Get the logger for the generic parameter type <typeparamref name="T"/>.
        ///</summary>
        ///<typeparam name="T">The type to create a logger for</typeparam>
        ///<returns>The logger for type <typeparamref name="T"/></returns>
        public static ILogger For<T>()
        {
            return For(typeof (T));
        }

        ///<summary>
        /// Get the logger for the object's type
        ///</summary>
        ///<param name="obj">The object to create a logger for</param>
        ///<returns>The logger for the type <see cref="object.GetType"/></returns>
        ///<exception cref="ArgumentNullException">If the <paramref name="obj"/> was null</exception>
        public static ILogger For(object obj)
        {
            if (obj == null)
            {
                throw new ArgumentNullException("obj");
            }
            return For(obj.GetType());
        }

        ///<summary>
        /// Get the logger for the given <paramref name="type"/>
        ///</summary>
        ///<param name="type">The type to create a logger for</param>
        ///<returns>The logger for the type <paramref name="type"/></returns>
        public static ILogger For(Type type)
        {
            return LogFactory.GetLogger(type);
        }
    }
}