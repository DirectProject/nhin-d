/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen
    Umesh Madan
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;

namespace Health.Direct.Common.Container
{

    public class Registration
    {
        public Type Type { get; set; }
        public Func<object> Component { get; set; }
    }

    ///<summary>
    /// SimpleDependencyResolver provides a simple (non-external) container
    /// so that dependencies can be isolated and other implementers of an
    /// agent or gateway can slide in their own container library.
    ///</summary>
    public class SimpleDependencyResolver : IDependencyContainer
    {
        //private readonly Dictionary<Type, Func<object>> m_types;
        private readonly List<Registration> m_registrations; 

        ///<summary>
        /// A simple <see cref="Dictionary{TKey,TValue}"/> based dependency
        /// resolver. All dependencies need to be initialized on startup and
        /// prior to calling to <see cref="IoC.Initialize{T}"/>.
        ///</summary>
        public SimpleDependencyResolver()
        {
            //m_types = new Dictionary<Type, Func<object>>();
            m_registrations = new List<Registration>();
        }
        
        /// <summary>
        /// Returns true if the given type is registered...
        /// </summary>
        /// <typeparam name="T">type to check</typeparam>
        /// <returns>true if registered</returns>
        public bool IsRegistered<T>()
        {
            //return m_types.ContainsKey(typeof(T));
            return m_registrations.Any(r => r.Type == typeof (T));
        }
        
        ///<summary>
        /// Given a specific type, return an instance of that type.
        ///</summary>
        ///<typeparam name="T">The type to attempt resolution</typeparam>
        ///<returns>An instance of type <typeparamref name="T"/></returns>
        public T Resolve<T>()
        {
            if (m_registrations.All(r => r.Type != typeof (T)))
            {
                throw new Exception("Could not resolve type in container - " + typeof(T).FullName);
            }

            //return (T) m_types[typeof (T)]();
            Registration registration = m_registrations.LastOrDefault(r => r.Type == typeof (T));
            if (registration == null) return default(T);
            return (T) registration.Component();
        }

        ///<summary>
        /// Given a specific type, return an instance of that type.
        ///</summary>
        ///<typeparam name="T">The type to attempt resolution</typeparam>
        ///<returns>An instance of type <typeparamref name="T"/></returns>
        public IList<T> ResolveAll<T>()
        {
            if (m_registrations.All(r => r.Type != typeof(T)))
            {
                throw new Exception("Could not resolve type in container - " + typeof(T).FullName);
            }

            //return (T) m_types[typeof (T)]();
            var registrations = m_registrations.Where(r => r.Type == typeof(T)).ToList();
            if (! registrations.Any()) return default(List<T>);
            return registrations.Select(r => (T)r.Component()).ToList();
        }

        /// <summary>
        /// Registers a specific instance <paramref name="obj"/> type <typeparamref name="T"/>
        /// with the container.
        /// </summary>
        /// <typeparam name="T">The type to register</typeparam>
        /// <param name="obj">The specific instance to register</param>
        /// <returns>An instance of self so the Register calls can be chained.</returns>
        public IDependencyContainer Register<T>(T obj)
        {
            return Register<T>(() => obj);
        }

        ///<summary>
        /// Registers a function, <paramref name="functor"/>, that returns an instance of type <typeparamref name="T"/>
        ///</summary>
        ///<param name="functor">The function that produces a new instance of <typeparamref name="T"/></param>
        ///<typeparam name="T">The type to register</typeparam>
        ///<returns>An instance of self so the Register calls can be chained.</returns>
        public IDependencyContainer Register<T>(Func<object> functor)
        {
            return Register(typeof (T), functor);
        }

        ///<summary>
        /// Registers a function of type <paramref name="serviceType"/> that is instatiated by <paramref name="functor"/>.
        ///</summary>
        ///<param name="serviceType">The type to register</param>
        ///<param name="functor">The function that produces a new instance of <paramref name="functor"/></param>
        ///<returns>A newly created container</returns>
        public IDependencyContainer Register(Type serviceType, Func<object> functor)
        {
            //m_types.Add(serviceType, functor);
            m_registrations.Add(new Registration{Type = serviceType, Component = functor});
            return this;
        }

        
        ///<summary>
        /// Register all of the components found in the <see cref="SimpleContainerSection"/>
        ///</summary>
        ///<returns>An instance of self so the Register calls can be chained.</returns>
        public IDependencyContainer RegisterFromConfig()
        {
            SimpleContainerSection section = SimpleContainerSection.Load("container");
            for (int i = 0; i<section.Components.Count; i++)
            {
                SimpleComponentElement component = section.Components[i];
                Register(component.ServiceType, GetCreator(component));
            }

            return this;
        }
        
        /// <summary>
        /// Register components
        /// </summary>
        /// <param name="containerSettings">A component settings object, typically deserialized using XmlSerializer</param>
        ///<returns>An instance of self so the Register calls can be chained.</returns>
        public IDependencyContainer Register(SimpleContainerSettings containerSettings)
        {
            if (containerSettings == null)
            {
                throw new ArgumentNullException("settings");
            }            
            
            if (containerSettings.HasComponents)
            {
                foreach(SimpleComponentSettings component in containerSettings.Components)
                {
                    component.Validate();
                    Register(component.ServiceType, GetCreator(component));
                }
            }

            return this;            
        }
        
        private static Func<object> GetCreator(SimpleComponentElement component)
        {
            if (component.Scope == InstanceScope.Transient)
            {
                return component.CreateInstance;
            }
            
            object instance = component.CreateInstance();
            return () => instance;
        }

        private static Func<object> GetCreator(SimpleComponentSettings component)
        {
            if (component.Scope == InstanceScope.Transient)
            {
                return component.CreateInstance;
            }

            object instance = component.CreateInstance();
            return () => instance;
        }
    }

    
}