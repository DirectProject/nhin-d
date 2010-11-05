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
using System.Collections.Generic;
using System.Configuration;

namespace Health.Direct.Common.Container
{
    ///<summary>
    /// This section can be used to move the configuration of the container to the configuration
    /// file. This section can be passed to the <see cref="IDependencyContainer.RegisterFromConfig"/> method
    /// to register all of the types with the container.
    ///</summary>
    /// <example>
    ///   &lt;configSections&gt;
    ///     &lt;section name="container" type="Health.Direct.Common.Container.SimpleContainerSection, Health.Direct.Common"/&gt;
    ///   &lt;/configSections&gt;
    ///   &lt;container&gt;
    ///     &lt;components&gt;
    ///       &lt;component service="Health.Direct.Common.Tests.Container.IFoo, Health.Direct.Common.Tests" 
    ///                  type="Health.Direct.Common.Tests.Container.Foo, Health.Direct.Common.Tests"
    ///                  scope="Singleton" /&gt;
    ///     &lt;/components&gt;
    ///   &lt;/container&gt;
    /// </example>
    public class SimpleContainerSection : ConfigurationSectionBase<SimpleContainerSection>
    {
        ///<summary>
        /// The list of components that are registered with the container.
        ///</summary>
        [ConfigurationProperty("components")]
        [ConfigurationCollection(typeof(List<SimpleComponentElement>), AddItemName = "component")]
        public SimpleComponentElementCollection Components
        {
            get
            {
                return (SimpleComponentElementCollection) this["components"];
            }
        }
    }

    ///<summary>
    /// A container of <see cref="SimpleComponentElement"/>
    ///</summary>
    public class SimpleComponentElementCollection : ConfigurationElementCollection
    {
        /// <summary>
        /// When overridden in a derived class, creates a new <see cref="SimpleComponentElement"/>.
        /// </summary>
        /// <returns>
        /// A new <see cref="SimpleComponentElement"/>.
        /// </returns>
        protected override ConfigurationElement CreateNewElement()
        {
            return new SimpleComponentElement();
        }

        /// <summary>
        /// Gets the element key for a specified configuration element when overridden in a derived class.
        /// </summary>
        /// <returns>
        /// An <see cref="T:System.Object"/> that acts as the key for the specified <see cref="SimpleComponentElement"/>.
        /// </returns>
        /// <param name="element">The <see cref="SimpleComponentElement"/> to return the key for. 
        ///                 </param>
        protected override object GetElementKey(ConfigurationElement element)
        {
            return ((SimpleComponentElement) element).Service;
        }

        ///<summary>
        /// Get a specifically indexed <see cref="SimpleComponentElement"/>
        ///</summary>
        ///<param name="index">The index of the component element to retrieve</param>
        public SimpleComponentElement this[int index]
        {
            get
            {
                return (SimpleComponentElement)BaseGet(index);
            }
        }
    }

    ///<summary>
    /// A component has a <see cref="Service"/> key as well as a <see cref="Type"/> that specifies the implementation.
    /// Additionally, a scope can be specified via the <see cref="Scope"/> property.
    ///</summary>
    public class SimpleComponentElement : ConfigurationElement
    {
        ///<summary>
        /// The interface that the component wants registered with the container.
        ///</summary>
        [ConfigurationProperty("service", IsRequired = true, IsKey = true)]
        public string Service
        {
            get
            {
                return (string) this["service"];
            }
        }

        ///<summary>
        /// The concrete implementation that implements the interface type specified in the <see cref="Service"/> type.
        ///</summary>
        [ConfigurationProperty("type", IsRequired = true)]
        public string Type
        {
            get
            {
                return (string) this["type"];
            }
        }

        ///<summary>
        /// The scope of the instance that is registered with the container. <see cref="InstanceScope"/> provides
        /// either <see cref="InstanceScope.Singleton"/> or <see cref="InstanceScope.Transient"/>.
        ///</summary>
        [ConfigurationProperty("scope", IsRequired = false, DefaultValue = InstanceScope.Transient)]
        public InstanceScope Scope
        {
            get
            {
                return (InstanceScope) this["scope"];
            }
        }

        ///<summary>
        /// A convience property that loads a the type specified in <see cref="Service"/>.
        ///</summary>
        public Type ServiceType
        {
            get
            {
                return LoadType(Service);
            }
        }

        ///<summary>
        /// Create an instance of the type <see cref="Type"/>.
        ///</summary>
        ///<returns>A new instance of the type specified by <see cref="Type"/></returns>
        public object CreateInstance()
        {
            return Activator.CreateInstance(LoadType(Type));
        }

        private static Type LoadType(string typeName)
        {
            return System.Type.GetType(typeName, true);
        }
    }

    ///<summary>
    /// The scope or lifetime of the object that is registered with the container.
    ///</summary>
    public enum InstanceScope
    {
        ///<summary>
        /// The registered service will return a new instance on each call to <see cref="IoC.Resolve{T}"/>
        ///</summary>
        Transient,
        ///<summary>
        /// The registered service will return the same instance on each call to <see cref="IoC.Resolve{T}"/>
        ///</summary>
        Singleton
    }
}